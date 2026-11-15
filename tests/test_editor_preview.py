"""Static side-boundary/render safeguards; dynamic packet/transaction cases are in JUnit."""
from pathlib import Path
import re
import unittest
ROOT=Path(__file__).resolve().parents[1]
J=ROOT/'src/main/java/ru/metrocraft/port'
class EditorPreviewTest(unittest.TestCase):
    def test_packet_id_16_is_client_bound_only_and_unique(self):
        text=(J/'MetroPort.java').read_text()
        registrations=re.findall(r'registerMessage\((.*?)\);',text,re.S)
        rows=[r for r in registrations if 'EditorPreviewPacket' in r]
        self.assertEqual(1,len(rows));self.assertIn(',16,Side.CLIENT',rows[0]);self.assertNotIn('Side.SERVER',rows[0])
    def test_preview_never_mutates_world_on_client(self):
        text=(J/'client/EditorPreviewRenderer.java').read_text()
        for forbidden in ['setBlockState','setTileEntity','sendToServer','loadShader','gammaSetting','renderBlock','disableDepth() ;']:
            self.assertNotIn(forbidden,text)
        self.assertIn('GlStateManager.enableDepth()',text)
    def test_renderer_restores_state_and_uses_safe_lwjgl_query(self):
        text=(J/'client/EditorPreviewRenderer.java').read_text()
        for token in ['GlStateBuffers.colorQuery()','finally{','GlStateManager.glLineWidth(line)','GlStateManager.matrixMode(matrix)','GlStateManager.depthMask(mask)','GlStateManager.popMatrix()']:
            self.assertIn(token,text)
        self.assertNotIn('createFloatBuffer(4)',text)
    def test_receiver_rejects_stale_owner_and_world(self):
        text=(J/'client/EditorPreviewRenderer.java').read_text()
        for token in ['mc.world!=expectedWorld','mc.player!=expectedOwner','mc.player.dimension!=dimension','mc.player.getEntityId()!=id','PreviewRules.alive','PreviewRules.show']:
            self.assertIn(token,text)
    def test_wire_data_contains_no_block_ids_or_te_nbt(self):
        text=(J/'editor/EditorPreviewPacket.java').read_text()
        for forbidden in ['NBTTagCompound','writeItemStack','fromJson','Class.forName','writeUTF','setBlockState']:
            self.assertNotIn(forbidden,text)
        self.assertIn('count>PreviewRules.MAX_CELLS',text);self.assertIn('buf.readableBytes()!=count*3',text)
    def test_original_limits_and_file_format_unchanged(self):
        self.assertIn('LIMIT=4096, SIDE=32',(J/'editor/Blueprint.java').read_text())
        self.assertIn('MAX_FILES=32',(J/'editor/BlueprintStore.java').read_text())
        text=(J/'editor/MetroEditCommand.java').read_text()
        for token in ['canUseCommand(2','capabilities.isCreativeMode','BreakEvent','PlaceEvent','World changed during edit','PreviewRules.alive']:
            self.assertIn(token,text)
    def test_mirror_and_preview_have_explicit_subcommands(self):
        text=(J/'editor/MetroEditCommand.java').read_text()
        for token in ['case "mirror"','case "preview"','case "confirm"','case "cancel"','case "outline"','Mirror.FRONT_BACK:Mirror.LEFT_RIGHT']:
            self.assertIn(token,text)
