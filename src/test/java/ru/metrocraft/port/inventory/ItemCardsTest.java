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
    @Test public void viewportMapsScrolledPixelsToOriginalServerCellsAndClamps(){
        GridViewport v=new GridViewport(3,3,40);assertEquals(0,v.at(0,0));assertEquals(18,v.at(119,119));assertEquals(-1,v.at(120,0));
        v.scroll(100,100);assertEquals(5,v.column);assertEquals(5,v.row);assertEquals(45,v.at(0,0));assertEquals(63,v.at(119,119));
        assertEquals(0,v.x(45));assertEquals(80,v.y(63));v.scroll(-100,-100);assertEquals(0,v.at(0,0));
        GridViewport full=new GridViewport(8,8,40);full.scroll(20,20);assertEquals(63,full.at(319,319));assertEquals(0,full.column);
    }
    @Test public void requestedSizesAndExclusiveZapalSurviveNewNamespace(){
        for(String id:new String[]{"rem","armorrem","remkomplekt"}){
            ItemStack stack=new ItemStack(new Item().setRegistryName("minetro",id));assertEquals(2,ItemMetrics.width(stack));assertEquals(2,ItemMetrics.height(stack));
        }
        ItemStack can=new ItemStack(new Item().setRegistryName("minetro","kanistra"));assertEquals(3,ItemMetrics.width(can));assertEquals(3,ItemMetrics.height(can));assertEquals(6000,ItemMetrics.grams(can));
        for(String id:new String[]{"mcmod_zapal_helmet","mcmod_zapal_vest"})assertEquals(ItemRarity.EXCLUSIVE,ItemRarity.of(new ItemStack(new Item().setRegistryName("minetro",id))));
    }
}
