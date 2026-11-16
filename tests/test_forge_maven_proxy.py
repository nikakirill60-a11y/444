import subprocess
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch
from tools.forge_maven_proxy import Cache, safe_path, ORIGIN, ORIGINS


class ForgeMavenProxyTest(unittest.TestCase):
    def setUp(self):
        output=patch('builtins.print');output.start();self.addCleanup(output.stop)

    def test_only_relative_safe_maven_paths(self):
        self.assertEqual('net/minecraftforge/forge/1.0/forge-1.0.pom.sha1',safe_path('/net/minecraftforge/forge/1.0/forge-1.0.pom.sha1'))
        for path in ['/../secret','/%2e%2e/secret','/a//b','/a/%252e%252e/b','https://evil.test/x','//evil.test/a','/a?q=1','/a#b','/a\\b']:
            with self.subTest(path=path),self.assertRaises(ValueError):safe_path(path)

    def test_success_is_atomic_cached_and_uses_only_official_origin(self):
        with tempfile.TemporaryDirectory() as directory:
            cache=Cache(directory)
            def download(args,**kwargs):
                self.assertEqual(ORIGIN+'a/item.pom',args[-1])
                Path(args[args.index('--output')+1]).write_bytes(b'<project/>')
                return subprocess.CompletedProcess(args,0,'200','')
            with patch('tools.forge_maven_proxy.subprocess.run',side_effect=download) as run:
                status,path=cache.get('a/item.pom');self.assertEqual(200,status);self.assertEqual(b'<project/>',path.read_bytes())
                self.assertEqual((status,path),cache.get('a/item.pom'));self.assertEqual(1,run.call_count)
                self.assertFalse(list(Path(directory).rglob('*.partial')))

    def test_negative_cache_only_for_404_not_transient_errors(self):
        with tempfile.TemporaryDirectory() as directory:
            cache=Cache(directory)
            with patch('tools.forge_maven_proxy.subprocess.run',return_value=subprocess.CompletedProcess([],0,'404','')) as run:
                self.assertEqual((404,None),cache.get('missing.pom'));self.assertEqual((404,None),cache.get('missing.pom'))
                self.assertEqual(len(ORIGINS),run.call_count)
            with patch('tools.forge_maven_proxy.subprocess.run',return_value=subprocess.CompletedProcess([],28,'000','timeout')) as run:
                self.assertEqual((502,None),cache.get('retry.pom'));self.assertEqual((502,None),cache.get('retry.pom'))
                self.assertEqual(2*len(ORIGINS),run.call_count)
            self.assertFalse((Path(directory)/'retry.pom').exists())

    def test_truncated_failed_download_is_never_reused(self):
        with tempfile.TemporaryDirectory() as directory:
            cache=Cache(directory)
            def fail(args,**kwargs):
                Path(args[args.index('--output')+1]).write_bytes(b'partial')
                return subprocess.CompletedProcess(args,18,'200','truncated')
            with patch('tools.forge_maven_proxy.subprocess.run',side_effect=fail):
                self.assertEqual((502,None),cache.get('a.jar'))
            self.assertFalse(list(Path(directory).rglob('*.partial')))
            self.assertFalse((Path(directory)/'a.jar').exists())

    def test_official_legacy_endpoint_can_recover_a_cdn_404(self):
        with tempfile.TemporaryDirectory() as directory:
            cache=Cache(directory)
            def download(args,**kwargs):
                if args[-1]==ORIGIN+'a.jar':return subprocess.CompletedProcess(args,0,'404','')
                self.assertEqual(ORIGINS[1]+'a.jar',args[-1])
                Path(args[args.index('--output')+1]).write_bytes(b'official artifact')
                return subprocess.CompletedProcess(args,0,'200','')
            with patch('tools.forge_maven_proxy.subprocess.run',side_effect=download):
                status,path=cache.get('a.jar');self.assertEqual(200,status)
                self.assertEqual(b'official artifact',path.read_bytes())

    def test_client_pom_workaround_reuses_forge_selected_mcp_without_pinning(self):
        script=(Path(__file__).resolve().parents[1]/'tools/forge_ci.init.gradle').read_text()
        self.assertIn("details.requested.group == 'net.minecraft'",script)
        self.assertIn("details.requested.name == 'client'",script)
        self.assertIn("details.requested.version == ext.get('MC_VERSION')",script)
        self.assertIn("details.useVersion(ext.get('MCP_VERSION').toString())",script)
        self.assertNotIn('20200226',script)
        self.assertNotIn('transitive = false',script)
