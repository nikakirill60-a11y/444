"""Regression for the attached dev.12 client crash, plus both identical TESR bugs."""
import re
import unittest
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
CLIENT = ROOT / 'src/main/java/ru/metrocraft/port/client'
RENDERERS = ('weapons/NativeWeaponRenderer.java', 'transport/WideRailRenderer.java',
             'environment/TunnelRenderer.java')

class GlQueryBufferTests(unittest.TestCase):
    def test_each_affected_renderer_uses_the_safe_query_buffer(self):
        for name in RENDERERS:
            with self.subTest(renderer=name):
                text = (CLIENT / 'blackrazid' / name).read_text()
                self.assertIn('FloatBuffer color=GlStateBuffers.colorQuery()', text)
                self.assertIn('GL11.glGetFloat(GL11.GL_CURRENT_COLOR,color)', text)
                self.assertNotIn('createFloatBuffer(4)', text)
                self.assertIn('GlStateManager.color(color.get(0),color.get(1),color.get(2),color.get(3))', text)
                self.assertNotIn('color.flip()', text)

    def test_lwjgl_query_helper_returns_an_independent_sixteen_float_direct_buffer(self):
        text = (CLIENT / 'GlStateBuffers.java').read_text()
        self.assertIn('return BufferUtils.createFloatBuffer(16)', text)
        self.assertNotRegex(text, r'static\s+(?:final\s+)?FloatBuffer\s+\w+\s*=')
        self.assertNotIn('glGetFloat(', re.sub(r'/\*.*?\*/', '', text, flags=re.S))

    def test_no_buffer_based_float_state_query_bypasses_the_lwjgl_guard(self):
        queries = []
        for path in CLIENT.rglob('*.java'):
            text = path.read_text()
            # Scalar overloads (e.g. NativeLaser point size) are not affected.
            for match in re.finditer(r'GL11\.glGetFloat\(\s*GL11\.\w+\s*,\s*(\w+)\s*\)', text):
                name = match[1]
                self.assertIn('FloatBuffer '+name+'=GlStateBuffers.colorQuery()', text, str(path))
                queries.append(path.relative_to(CLIENT).as_posix())
        self.assertEqual(sorted(['blackrazid/'+name for name in RENDERERS]+['BlackrazidGadgetRenderer.java','BlackrazidWatchRenderer.java']), sorted(queries))
