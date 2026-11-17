"""Original menu audio preparation is not a completed or enabled 3D menu."""
import hashlib
import io
import json
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch
import zipfile

from Crypto.Cipher import AES
from Crypto.Util.Padding import pad
import soundfile as sf
from tools import import_metromod_menu_audio as audio

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / 'src/main/resources/assets/metroport'


def fixture(bad_event=False, bad_audio=False):
    output = io.BytesIO()
    with sf.SoundFile(output, mode='w', samplerate=22050, channels=1,
                      format='OGG', subtype='VORBIS') as sound:
        sound.write([0.0] * 220)
    plain = b'not Ogg' if bad_audio else output.getvalue()
    key = b'0123456789abcdef'
    payload = key + AES.new(key, AES.MODE_ECB).encrypt(pad(plain, 16))
    definitions = {'menu.' + n: {'category': c, 'sounds': ['menu/' + s for s in samples]}
                   for n, (c, samples) in audio.EVENTS.items()}
    if bad_event:
        definitions['menu.menu_railnoise']['sounds'].pop()
    output = io.BytesIO()
    with zipfile.ZipFile(output, 'w') as z:
        z.writestr('assets/metro2033server/sounds.json', json.dumps(definitions))
        for _, samples in audio.EVENTS.values():
            for s in samples:
                z.writestr('assets/metro2033server/sounds/menu/' + s + '.m2033sound', payload)
        z.writestr('unrelated.class', b'not executed or copied')
    return output.getvalue()


class MenuAudioTest(unittest.TestCase):
    def test_published_audio_hashes_and_event_closure(self):
        report = json.loads((ROOT / 'reports/METRO_MOD/menu_audio_verified.json').read_text())
        events = json.loads((RES / 'sounds.json').read_text())
        self.assertEqual(6, len(report['events']))
        self.assertEqual(21, len(report['files']))
        self.assertFalse(report['menu_implemented'])
        self.assertFalse(report['in_game_playback_verified'])
        self.assertEqual(audio.CONTENT_SHA, report['source_sha256'])
        samples = set()
        for name, value in report['events'].items():
            self.assertEqual(value, events[name])
            for sample in value['sounds']:
                samples.add('sounds/' + sample.removeprefix('metroport:') + '.ogg')
        self.assertEqual(samples, {r['target_path'] for r in report['files']})
        for row in report['files']:
            data = (RES / row['target_path']).read_bytes()
            self.assertEqual(row['sha256'], hashlib.sha256(data).hexdigest())
            self.assertEqual(row['bytes'], len(data))
            self.assertEqual(row['frames'], audio.validate(data, '.ogg')['frames'])

    def test_source_hash_is_required(self):
        with self.assertRaisesRegex(ValueError, 'SHA256'):
            audio.prepare(b'other archive')

    def test_synthetic_source_roundtrip_and_idempotence(self):
        data = fixture()
        with tempfile.TemporaryDirectory() as tmp, patch.object(audio, 'CONTENT_SHA', audio.sha(data)):
            root = Path(tmp); source = root / 'Content.jar'; source.write_bytes(data)
            res = root / 'assets'; res.mkdir()
            (res / 'sounds.json').write_text('{"existing":{"sounds":["metroport:existing"]}}')
            report = root / 'report.json'
            audio.install(source, res, report)
            before = {str(p): p.read_bytes() for p in root.rglob('*') if p.is_file()}
            audio.install(source, res, report)
            self.assertEqual(before, {str(p): p.read_bytes() for p in root.rglob('*') if p.is_file()})
            self.assertEqual({'sounds': ['metroport:existing']}, json.loads((res / 'sounds.json').read_text())['existing'])
            self.assertEqual(21, len(list(res.rglob('*.ogg'))))
            self.assertFalse(list(res.rglob('*.class')))

    def test_changed_event_or_invalid_vorbis_refused(self):
        for kwargs in [{'bad_event': True}, {'bad_audio': True}]:
            data = fixture(**kwargs)
            with patch.object(audio, 'CONTENT_SHA', audio.sha(data)), self.assertRaises(ValueError):
                audio.prepare(data)

    def test_conflict_is_detected_before_writing_assets(self):
        data = fixture()
        with tempfile.TemporaryDirectory() as tmp, patch.object(audio, 'CONTENT_SHA', audio.sha(data)):
            root = Path(tmp); source = root / 'Content.jar'; source.write_bytes(data)
            res = root / 'assets'; res.mkdir()
            original = '{"metromod.menu.menu_railnoise":{"sounds":["unrelated"]}}'
            (res / 'sounds.json').write_text(original)
            with self.assertRaisesRegex(ValueError, 'Conflicting sound event'):
                audio.install(source, res, root / 'report.json')
            self.assertEqual(original, (res / 'sounds.json').read_text())
            self.assertFalse((res / 'sounds').exists())
            self.assertFalse((root / 'report.json').exists())
