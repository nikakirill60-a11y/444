package ru.metrocraft.port.patches;

import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class FactionPatchesTest {
    private static FactionPatchItem satan,legion;
    @BeforeClass public static void setup() {
        Bootstrap.register();
        satan=new FactionPatchItem("satanists");satan.setRegistryName("metroport_tests","patch_satanists");
        legion=new FactionPatchItem("legion");legion.setRegistryName("metroport_tests","patch_legion");
        ForgeRegistries.ITEMS.register(satan);ForgeRegistries.ITEMS.register(legion);
    }
    private ItemStack armor() {
        ItemStack armor=new ItemStack(Items.IRON_CHESTPLATE);armor.setItemDamage(7);
        NBTTagCompound tag=new NBTTagCompound();tag.setBoolean("NoDrop",true);tag.setInteger("condition",123);armor.setTagCompound(tag);
        return armor;
    }
    @Test public void onlyChestArmorAcceptsPatches() {
        assertTrue(FactionPatches.accepts(armor()));assertFalse(FactionPatches.accepts(new ItemStack(Items.IRON_HELMET)));
        assertFalse(FactionPatches.accepts(new ItemStack(Items.IRON_LEGGINGS)));assertFalse(FactionPatches.accepts(new ItemStack(Items.STICK)));
        assertFalse(FactionPatches.accepts(ItemStack.EMPTY));
    }
    @Test public void attachCopiesOnlyOnePatchAndPreservesBothArmorAndPatchTags() {
        ItemStack armor=armor(),patch=new ItemStack(satan,12);patch.setStackDisplayName("Named patch");
        NBTTagCompound tag=patch.getTagCompound();tag.setBoolean("NoDrop",true);
        assertTrue(FactionPatches.attach(armor,patch).isEmpty());assertEquals(12,patch.getCount());
        ItemStack stored=FactionPatches.get(armor);assertEquals(satan,stored.getItem());assertEquals(1,stored.getCount());
        assertEquals("Named patch",stored.getDisplayName());assertTrue(stored.getTagCompound().getBoolean("NoDrop"));
        assertEquals(7,armor.getItemDamage());assertEquals(123,armor.getTagCompound().getInteger("condition"));assertTrue(armor.getTagCompound().getBoolean("NoDrop"));
        stored.setCount(0);assertEquals(1,FactionPatches.get(armor).getCount());
    }
    @Test public void replacementAndRemovalReturnExactlyOneOldPatch() {
        ItemStack armor=armor();FactionPatches.attach(armor,new ItemStack(satan));
        assertEquals(satan,FactionPatches.attach(armor,new ItemStack(legion)).getItem());
        assertEquals(legion,FactionPatches.detach(armor).getItem());assertTrue(FactionPatches.detach(armor).isEmpty());
        assertTrue(armor.getTagCompound().getBoolean("NoDrop"));
    }
    @Test public void armorSaveReloadAndCopyCarryThePatchButRemainIndependent() {
        ItemStack armor=armor();FactionPatches.attach(armor,new ItemStack(satan));
        ItemStack loaded=new ItemStack(armor.writeToNBT(new NBTTagCompound()));
        assertEquals(satan,FactionPatches.get(loaded).getItem());FactionPatches.detach(loaded);
        assertEquals(satan,FactionPatches.get(armor).getItem());
        NBTTagCompound bogus=new ItemStack(Items.DIAMOND).writeToNBT(new NBTTagCompound());
        loaded.getTagCompound().setTag("MetroFactionPatch",bogus);assertTrue(FactionPatches.get(loaded).isEmpty());
    }
}
