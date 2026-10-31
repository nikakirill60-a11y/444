package ru.metrocraft.port.mcmod;

import net.minecraft.init.Bootstrap;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.BeforeClass;
import org.junit.Test;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;
import static org.junit.Assert.*;

public class McmodItemsTest {
    @BeforeClass public static void setup() { Bootstrap.register(); McmodContent.load(); }
    private ItemStack kit() { return new ItemStack(new McmodMedkitItem()); }
    @Test public void newAndDamagedKitsHaveFiveOrRemainingSyringes() {
        ItemStack kit=kit();assertEquals(5,McmodMedkitItem.remaining(kit));assertEquals(100,McmodMedkitItem.timer(kit));
        kit.setItemDamage(3);assertEquals(2,McmodMedkitItem.remaining(kit));
        assertFalse(kit.getItem().isRepairable());assertFalse(kit.getItem().getIsRepairable(kit,kit));
    }
    @Test public void medkitRejectsFullHealthDeadPlayersSpectatorsAndCooldown() {
        ItemStack kit=kit();assertFalse(McmodMedkitItem.canUse(kit,20,20,false));
        assertFalse(McmodMedkitItem.canUse(kit,0,20,false));assertFalse(McmodMedkitItem.canUse(kit,10,20,true));
        assertTrue(McmodMedkitItem.canUse(kit,10,20,false));McmodMedkitItem.spend(kit,false);
        assertFalse(McmodMedkitItem.canUse(kit,10,20,false));
    }
    @Test public void cooldownIsExactly100TicksAndOnlyAffectsItsOwnStack() {
        ItemStack one=kit(),two=kit();McmodMedkitItem.spend(one,false);
        for(int i=0;i<99;i++)McmodMedkitItem.tick(one);
        assertFalse(McmodMedkitItem.canUse(one,1,20,false));assertTrue(McmodMedkitItem.canUse(two,1,20,false));
        McmodMedkitItem.tick(one);assertTrue(McmodMedkitItem.canUse(one,1,20,false));
    }
    @Test public void fiveUsesRetainAnEmptyKitAndNeverReset() {
        ItemStack kit=kit();
        for(int i=0;i<5;i++) { assertTrue(McmodMedkitItem.canUse(kit,1,20,false));McmodMedkitItem.spend(kit,false);for(int t=0;t<100;t++)McmodMedkitItem.tick(kit); }
        assertEquals(0,McmodMedkitItem.remaining(kit));assertEquals(5,kit.getItemDamage());
        assertFalse(kit.isEmpty());assertFalse(McmodMedkitItem.canUse(kit,1,20,false));
        McmodMedkitItem.tick(kit);assertEquals(0,McmodMedkitItem.remaining(kit));
    }
    @Test public void creativeUseKeepsSyringesAndOtherNbtSurvives() {
        ItemStack kit=kit();NBTTagCompound tag=new NBTTagCompound();tag.setBoolean("NoDrop",true);tag.setString("Owner","test");kit.setTagCompound(tag);
        McmodMedkitItem.spend(kit,true);assertEquals(5,McmodMedkitItem.remaining(kit));
        assertEquals(0,McmodMedkitItem.timer(kit));assertTrue(tag.getBoolean("NoDrop"));assertEquals("test",tag.getString("Owner"));
    }
    @Test public void ordinaryFactoriesRespectKindsStackSizesAndMacheteSourceOverride() {
        assertEquals(9,McmodOrdinaryContent.entries.length);Set<String> ids=new HashSet<>();
        for(McmodOrdinaryContent.Entry entry:McmodOrdinaryContent.entries) {
            assertTrue(ids.add(entry.id));Item item=McmodOrdinaryContent.create(entry);assertEquals(entry.stackSize,item.getItemStackLimit());
            if(entry.kind.equals("filter"))assertTrue(item instanceof ru.metrocraft.port.FilterItem);
            if(entry.kind.equals("medkit"))assertTrue(item instanceof McmodMedkitItem);
            if(entry.kind.equals("machete")) {
                assertTrue(item instanceof ItemAxe);assertEquals(1561,item.getMaxDamage());
                ItemStack tool=new ItemStack(item);assertTrue(item.hitEntity(tool,null,null));assertEquals(0,tool.getItemDamage());
            }
        }
    }
    @Test public void allNineOriginalOrdinaryIconsDecode() throws Exception {
        for(McmodOrdinaryContent.Entry entry:McmodOrdinaryContent.entries)
            try(InputStream in=getClass().getResourceAsStream("/assets/metroport/textures/mcmod/items/"+entry.sourceId+".png")) {
                assertNotNull(entry.id,in);BufferedImage image=ImageIO.read(in);assertNotNull(image);
                assertTrue(image.getWidth()>0);assertTrue(image.getHeight()>0);
            }
    }
    @Test public void allActualEquipmentIconsDecodeAndPairImagesDiffer() throws Exception {
        Map<String,Long> hashes=new HashMap<>();int generated=0;
        for(McmodArmorEntry entry:McmodContent.armor) {
            try(InputStream in=getClass().getResourceAsStream("/assets/metroport/textures/"+entry.icon+".png")) {
                assertNotNull(entry.id,in);BufferedImage image=ImageIO.read(in);assertNotNull(image);
                long hash=0;int visible=0,transparent=0;
                for(int y=0;y<image.getHeight();y++)for(int x=0;x<image.getWidth();x++) {
                    int pixel=image.getRGB(x,y);hash=31*hash+pixel;if((pixel>>>24)>0)visible++;else transparent++;
                }
                assertTrue(visible>0);hashes.put(entry.id,hash);
                if(!entry.whole()) { generated++;assertEquals(128,image.getWidth());assertEquals(128,image.getHeight());assertTrue(transparent>0); }
            }
        }
        assertEquals(53,generated);
        for(McmodArmorEntry e:McmodContent.armor)if(e.helmet() && hashes.containsKey("mcmod_"+e.model+"_vest"))
            assertNotEquals(hashes.get(e.id),hashes.get("mcmod_"+e.model+"_vest"));
    }
}
