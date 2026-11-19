package ru.metrocraft.port.inventory;

import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.*;
import static org.junit.Assert.*;

public class ItemCardsTest {
    @BeforeClass public static void boot(){Bootstrap.register();}
    public static class Type {public Integer internalAmmoStorage=10;public String[] acceptedBullets={"unknown"};}
    public static class Gun extends com.modularwarfare.common.guns.ItemGun {public Type type=new Type();}
    @Test public void snapshotShowsRealIdWeightAndAmmoWithoutWritingOrChangingNbt(){
        Gun gun=new Gun();gun.setRegistryName("modularwarfare","card_test");ItemStack stack=new ItemStack(gun);
        NBTTagCompound tag=new NBTTagCompound();tag.setInteger("ammocount",9);tag.setBoolean("NoDrop",true);stack.setTagCompound(tag.copy());
        ItemCardData data=ItemCardData.of(stack,null);
        assertEquals("modularwarfare:card_test",data.id);assertEquals("9/10/0",data.primary);assertEquals("4.00",data.weight);
        assertEquals(tag,stack.getTagCompound());
        ItemStack empty=new ItemStack(gun);ItemCardData.of(empty,null);assertFalse(empty.hasTagCompound());
    }
    @Test public void everyNormalItemHasWeightAndDimensionsAndDamageableItemsShowRealCondition(){
        ItemCardData apple=ItemCardData.of(new ItemStack(Items.APPLE,2),null);assertEquals("minecraft:apple",apple.id);assertEquals(2,apple.count);assertEquals("1×1",apple.secondary);
        ItemStack armor=new ItemStack(Items.IRON_CHESTPLATE);armor.setItemDamage(12);
        assertEquals((armor.getMaxDamage()-12)+"/"+armor.getMaxDamage(),ItemCardData.of(armor,null).primary);
    }
    @Test public void allSixtyFourCellsFitWithoutScrollingOrWindowGrowth(){
        assertEquals(312,CompactInventoryLayout.WIDTH);assertEquals(230,CompactInventoryLayout.HEIGHT);assertEquals(16,CompactInventoryLayout.CELL);
        for(int y=0;y<128;y++)for(int x=0;x<128;x++)assertEquals(x/16+y/16*8,CompactInventoryLayout.cellAt(x,y));
        assertEquals(63,CompactInventoryLayout.cellAt(127,127));assertEquals(-1,CompactInventoryLayout.cellAt(128,0));assertEquals(-1,CompactInventoryLayout.cellAt(0,128));assertEquals(-1,CompactInventoryLayout.cellAt(-1,0));
        assertTrue(CompactInventoryLayout.GRID_X+128<CompactInventoryLayout.WIDTH);
        assertTrue(CompactInventoryLayout.GRID_Y+128<184); // Quick-access row remains below the entire grid.
    }
    @Test public void labelsNeverForceLargerRectanglesOrOverlapIcons(){
        for(int width=16;width<=128;width+=16)for(int height=16;height<=128;height+=16){
            CompactInventoryLayout.Card card=new CompactInventoryLayout.Card(width,height);
            assertTrue(card.iconSize<=width-4);assertTrue(card.header+card.iconSize+card.footer<=height);
        }
        CompactInventoryLayout.Card tiny=new CompactInventoryLayout.Card(16,16);assertFalse(tiny.name);assertFalse(tiny.id);assertEquals(12,tiny.iconSize);
        assertTrue(new CompactInventoryLayout.Card(32,32).name);assertFalse(new CompactInventoryLayout.Card(32,32).id);
        assertTrue(new CompactInventoryLayout.Card(64,64).id);
    }
    @Test public void requestedSizesAndExclusiveZapalSurviveNewNamespace(){
        for(String id:new String[]{"rem","armorrem","remkomplekt"}){
            ItemStack stack=new ItemStack(new Item().setRegistryName("minetro",id));assertEquals(2,ItemMetrics.width(stack));assertEquals(2,ItemMetrics.height(stack));
        }
        ItemStack can=new ItemStack(new Item().setRegistryName("minetro","kanistra"));assertEquals(3,ItemMetrics.width(can));assertEquals(3,ItemMetrics.height(can));assertEquals(6000,ItemMetrics.grams(can));
        for(String id:new String[]{"mcmod_zapal_helmet","mcmod_zapal_vest"})assertEquals(ItemRarity.EXCLUSIVE,ItemRarity.of(new ItemStack(new Item().setRegistryName("minetro",id))));
    }
}
