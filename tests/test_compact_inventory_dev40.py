"""Guard against repeating the dev39 oversized/scrolled inventory regression."""
from pathlib import Path
import json,unittest
R=Path(__file__).resolve().parents[1]
J=R/'src/main/java/ru/metrocraft/port'
class CompactInventory40(unittest.TestCase):
    def test_screen_is_fixed_and_uses_compact_layout(self):
        gui=(J/'client/MetroInventoryGui.java').read_text()
        self.assertIn('xSize=CompactInventoryLayout.WIDTH;ySize=CompactInventoryLayout.HEIGHT',gui)
        self.assertIn('CELL=CompactInventoryLayout.CELL',gui)
        self.assertIn('CompactInventoryLayout.cellAt(x,y)',gui)
        for retired in ['GridViewport','scrollDrag','GL_SCISSOR_TEST','handleMouseInput','width-8','height-8']:
            self.assertNotIn(retired,gui)
        self.assertIn('for(int y=0;y<8;y++)for(int x=0;x<8;x++)',gui)
        self.assertIn('new GridClickPacket',gui);self.assertIn('KEY_R',gui)
    def test_small_cards_do_not_draw_microtext_or_change_size(self):
        cards=(J/'client/ItemCards.java').read_text()
        self.assertIn('if(layout.name)',cards)
        self.assertIn('if(layout.id)',cards)
        self.assertNotIn('.22f',cards);self.assertNotIn('.28f',cards)
        gui=(J/'client/MetroInventoryGui.java').read_text()
        self.assertIn('drawGuiContainerForegroundLayer(int x,int y){}',gui)
    def test_other_user_changes_are_preserved(self):
        b=json.loads((R/'src/main/resources/assets/metroport/inventory_balance.json').read_text())['items']
        self.assertEqual('exclusive',b['metroport:mcmod_zapal_vest']['rarity'])
        self.assertEqual('exclusive',b['metroport:mcmod_zapal_helmet']['rarity'])
        self.assertEqual((4,4),tuple(b['metroport:mcmod_zapal_vest'][k] for k in ['width','height']))
        for id in ['rem','armorrem','remkomplekt']:self.assertEqual((2,2),tuple(b['metroport:'+id][k] for k in ['width','height']))
        self.assertEqual((3,3),tuple(b['metroport:kanistra'][k] for k in ['width','height']))
        self.assertEqual('minetro',json.loads((R/'src/main/resources/mcmod.info').read_text())[0]['modid'])
