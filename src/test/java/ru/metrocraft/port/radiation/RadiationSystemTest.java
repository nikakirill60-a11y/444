package ru.metrocraft.port.radiation;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import org.junit.*;
import ru.metrocraft.port.*;
import static org.junit.Assert.*;
public class RadiationSystemTest {
    @BeforeClass public static void setup(){Bootstrap.register();}
    @Test public void mapZonesUseTheOriginalRaisedFiveBlockVolume(){
        BlockPos source=new BlockPos(-20,60,8);
        for(RadiationSystem.Zone zone:new RadiationSystem.Zone[]{RadiationSystem.Zone.RADIATION,RadiationSystem.Zone.AIR}){
            assertTrue(RadiationSystem.inside(zone,source,-19.5,65,8.5));
            assertTrue(RadiationSystem.inside(zone,source,-23,60.1,11));
            assertFalse(RadiationSystem.inside(zone,source,-19.5,60,8.5));
            assertFalse(RadiationSystem.inside(zone,source,-19.5,70,8.5));
            assertFalse(RadiationSystem.inside(zone,source,-24.5,65,8.5));
        }
    }
    @Test public void existingSourceKeepsItsTenBlockVolume(){
        BlockPos source=BlockPos.ORIGIN;
        assertTrue(RadiationSystem.inside(RadiationSystem.Zone.LEGACY,source,10.5,0,0));
        assertTrue(RadiationSystem.inside(RadiationSystem.Zone.LEGACY,source,-10,0,0));
        assertFalse(RadiationSystem.inside(RadiationSystem.Zone.LEGACY,source,11,0,0));
    }
    @Test public void safeAirWinsIndependentlyOfSourceIterationOrder(){
        assertEquals(3,RadiationSystem.combine(true,false));
        assertEquals(0,RadiationSystem.combine(true,true));
        assertEquals(0,RadiationSystem.combine(false,true));
        assertEquals(0,RadiationSystem.combine(false,false));
    }
    private ItemStack armor(boolean mask,String id){
        Definition d=new Definition();d.id=id;d.slot="head";d.gasMask=mask;d.durability=100;
        ItemStack stack=new ItemStack(new MetroArmor(d));stack.setTagCompound(new NBTTagCompound());return stack;
    }
    @Test public void filterProtectionRequiresARealMaskAndStrictlyPositiveRemainingTime(){
        assertFalse(RadiationSystem.filterProtects(ItemStack.EMPTY));
        ItemStack mask=armor(true,"test_rad_mask");
        for(int ticks:new int[]{-1,0,1,6000}){
            mask.getTagCompound().setInteger("MetroFilterTicks",ticks);
            assertEquals(ticks>0,RadiationSystem.filterProtects(mask));
        }
        ItemStack helmet=armor(false,"test_rad_helmet");helmet.getTagCompound().setInteger("MetroFilterTicks",6000);
        assertFalse(RadiationSystem.filterProtects(helmet));
    }
}
