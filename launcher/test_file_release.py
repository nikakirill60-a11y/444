import copy
import hashlib
import io
import json
import tempfile
import unittest
from pathlib import Path
from file_release import CHUNK, Storage, prepare, upload, validate, mode_for, safe_path


class FakeStorage:
    def __init__(self):
        self.objects, self.writes, self.activations = {}, [], []
        self.active = None

    def rpc(self, name, payload):
        if name == 'file_distribution_protocol':
            return 2
        self.activations.append(payload)

    def request(self, method, path):
        assert method == 'GET'
        return io.BytesIO(json.dumps([] if self.active is None else [{'version': self.active}]).encode())

    def exists(self, sha, size):
        return sha in self.objects and len(self.objects[sha]) == size

    def upload(self, sha, data):
        assert hashlib.sha256(data).hexdigest() == sha
        self.objects[sha] = data
        self.writes.append(sha)


class FileReleaseTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.source, self.out = self.root / 'game', self.root / 'prepared'
        self.source.mkdir()
        self.put('mods/metrocraft-port-test.jar', b'minetro')
        self.put('mods/mwf.jar', b'mwf')
        self.put('config/defaults.cfg', b'config')
        self.storage = FakeStorage()

    def put(self, name, data):
        p = self.source / name
        p.parent.mkdir(parents=True, exist_ok=True)
        p.write_bytes(data)

    def test_prepare_excludes_worlds_and_personal_files(self):
        for path in ['saves/map/level.dat', 'screenshots/a.png', 'options.txt', 'ModularWarfare/packv4.json', 'ModularWarfare/mod_config.json']:
            self.put(path, b'private')
        m = prepare(self.source, self.out, 'v1')
        self.assertEqual({'mods/metrocraft-port-test.jar', 'mods/mwf.jar', 'config/defaults.cfg'}, {f['path'] for f in m['files']})
        self.assertEqual('seed', m['files'][0]['mode'])
        self.assertNotIn(hashlib.sha256(b'private').hexdigest(), [p.name for p in (self.out / 'chunks').iterdir()])

    def test_dry_run_writes_nothing(self):
        prepare(self.source, self.out, 'v1')
        r = upload(self.out, self.storage)
        self.assertGreater(r['pendingBytes'], 0)
        self.assertEqual([], self.storage.writes)
        self.assertEqual([], self.storage.activations)

    def test_upload_does_not_activate_without_explicit_owner_acceptance(self):
        prepare(self.source, self.out, 'v1')
        r = upload(self.out, self.storage, apply=True)
        self.assertGreater(r['uploadedBytes'], 0)
        self.assertFalse(r['activated'])
        self.assertEqual([], self.storage.activations)
        for apply, approval in [(False, 'accepted'), (True, None), (True, ' ')]:
            with self.assertRaises(ValueError):
                upload(self.out, self.storage, apply=apply, activate=True, approval=approval)

    def test_second_upload_and_changed_file_only(self):
        prepare(self.source, self.out, 'v1')
        first = upload(self.out, self.storage, apply=True)
        self.assertGreater(first['uploadedBytes'], 0)
        self.assertEqual(0, upload(self.out, self.storage, apply=True)['uploadedBytes'])
        self.put('mods/mwf.jar', b'new mwf')
        prepare(self.source, self.out, 'v2')
        self.assertEqual(len(b'new mwf'), upload(self.out, self.storage, apply=True)['uploadedBytes'])

    def test_large_files_split_and_deduplicate(self):
        self.put('ModularWarfare/pack.zip', b'x' * CHUNK + b'x' * CHUNK + b'end')
        m = prepare(self.source, self.out, 'v1')
        f = next(f for f in m['files'] if f['path'].endswith('pack.zip'))
        self.assertEqual(3, len(f['chunks']))
        self.assertEqual(f['chunks'][0], f['chunks'][1])
        self.assertEqual(CHUNK + 3 + 7 + 3 + 6, upload(self.out, self.storage, apply=True)['uploadedBytes'])

    def test_corrupt_local_chunk_stops_activation(self):
        prepare(self.source, self.out, 'v1')
        chunk = next((self.out / 'chunks').iterdir())
        chunk.write_bytes(b'bad')
        with self.assertRaises(ValueError):
            upload(self.out, self.storage, apply=True, activate=True, approval='owner test')
        self.assertEqual([], self.storage.activations)

    def test_activation_carries_expected_previous_version(self):
        self.storage.active = 'v0'
        prepare(self.source, self.out, 'v1')
        upload(self.out, self.storage, apply=True, activate=True, approval='owner explicitly accepted v1')
        self.assertEqual('v0', self.storage.activations[0]['expected_version'])
        self.assertEqual('v1', self.storage.activations[0]['new_manifest']['version'])

    def test_safe_paths_and_credential_destinations(self):
        for path in ['../evil', 'mods/CON.jar', 'mods/x:ads', 'mods/a\\b', 'mods/foo.', 'mods//x']:
            with self.assertRaises(ValueError):
                safe_path(path)
        self.assertIsNone(mode_for('saves/world/level.dat'))
        for url in ['http://example.com', 'https://user:pass@x.supabase.co', 'https://x.supabase.co.evil.test', 'https://x.supabase.co/?x=1']:
            with self.assertRaises(ValueError):
                Storage(url, 'dummy-not-a-key')

    def test_collisions_protected_files_and_bad_chunks(self):
        m = prepare(self.source, self.out, 'v1')
        for path, mode in [('saves/map.dat', 'managed'), ('config/a.cfg', 'managed'), ('ModularWarfare/packv4.json', 'managed')]:
            bad = copy.deepcopy(m); bad['files'][0].update(path=path, mode=mode)
            with self.assertRaises(ValueError):
                validate(bad)
        bad = copy.deepcopy(m); bad['files'].append(copy.deepcopy(bad['files'][0]))
        with self.assertRaises(ValueError):
            validate(bad)
        bad = copy.deepcopy(m); bad['files'][0]['chunks'][0]['bytes'] += 1
        with self.assertRaises(ValueError):
            validate(bad)

    def test_large_manifest_over_old_file_and_json_limits(self):
        m = prepare(self.source, self.out, 'large-6000')
        template = m['files'][1]
        m['files'].extend({**template, 'path': 'resourcepacks/assets/t_' + str(i) + '.png', 'mode': 'managed'} for i in range(6000))
        self.assertGreater(len(json.dumps(m).encode()), 512 * 1024)
        self.assertEqual(6003, len(validate(m)['files']))

    def test_symlinks_and_nested_output_refused(self):
        with self.assertRaises(ValueError):
            prepare(self.source, self.source / 'prepared', 'v1')
        target = self.root / 'outside.txt'; target.write_bytes(b'private')
        try:
            (self.source / 'mods' / 'linked.jar').symlink_to(target)
        except OSError:
            self.skipTest('Host does not permit creating symlinks')
        with self.assertRaises(ValueError):
            prepare(self.source, self.out, 'v1')

if __name__ == '__main__':
    unittest.main()
