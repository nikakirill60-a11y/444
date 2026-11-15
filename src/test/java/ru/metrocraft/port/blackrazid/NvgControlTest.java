package ru.metrocraft.port.blackrazid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import org.junit.*;
import ru.metrocraft.port.*;
import ru.metrocraft.port.inventory.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NvgControlTest {
    private static MetroArmor nvg;
    @BeforeClass public static void boot(){
        SurvivalRulesTest.boot();Definition d=new Definition();d.id="control_test_pnv";d.slot="head";d.nightVision=true;d.durability=100;
        nvg=new NvgItem(d);nvg.setRegistryName("metroport","control_test_pnv");
        net.minecraftforge.fml.common.registry.ForgeRegistries.ITEMS.register(nvg);
    }
    private EntityPlayer equipped(boolean remote)throws Exception{
        EntityPlayer p=SurvivalRulesTest.player(remote);
        NvgEquipment.set(p,new ItemStack(nvg));return p;
    }
    @Test public void existingSavesRemainEnabledUntilExplicitlySwitchedOff()throws Exception{
        EntityPlayer p=equipped(false);assertTrue(NvgControl.requested(p));assertTrue(NvgControl.active(p));assertTrue(NvgControl.toggle(p));assertFalse(NvgControl.active(p));
        when(p.world.getTotalWorldTime()).thenReturn(5L);assertTrue(NvgControl.toggle(p));assertTrue(NvgControl.active(p));
    }
    @Test public void requestFloodIsRateLimitedAndClockRollbackDoesNotLockDevice()throws Exception{
        EntityPlayer p=equipped(false);when(p.world.getTotalWorldTime()).thenReturn(100L);assertTrue(NvgControl.toggle(p));assertFalse(NvgControl.toggle(p));
        when(p.world.getTotalWorldTime()).thenReturn(104L);assertFalse(NvgControl.toggle(p));when(p.world.getTotalWorldTime()).thenReturn(50L);assertTrue(NvgControl.toggle(p));
    }
    @Test public void deadSpectatorMissingDeviceAndClientCannotToggle()throws Exception{
        EntityPlayer p=equipped(false);when(p.isEntityAlive()).thenReturn(false);assertFalse(NvgControl.toggle(p));assertFalse(NvgControl.active(p));
        when(p.isEntityAlive()).thenReturn(true);when(p.isSpectator()).thenReturn(true);assertFalse(NvgControl.toggle(p));assertFalse(NvgControl.active(p));
        assertFalse(NvgControl.toggle(SurvivalRulesTest.player(false)));assertFalse(NvgControl.toggle(equipped(true)));
    }
    @Test public void toggleLeavesEquipmentAndLampBatteryUntouched()throws Exception{
        EntityPlayer p=equipped(false);ItemStack before=NvgEquipment.get(p);NvgControl.toggle(p);
        assertTrue(ItemStack.areItemStacksEqual(before,NvgEquipment.get(p)));assertEquals(0,ru.metrocraft.port.blackrazid.survival.SurvivalSystem.get(p).energy,0);
    }
    @Test public void switchedOffDeviceOnlyRemovesItsOwnNightVision()throws Exception{
        EntityPlayer p=equipped(false);p.getEntityData().setBoolean("MetroNvgVisionOwned",true);
        when(p.getActivePotionEffect(MobEffects.NIGHT_VISION)).thenReturn(new PotionEffect(MobEffects.NIGHT_VISION,210,0,false,false));
        NvgControl.toggle(p);NvgEquipment.tick(new net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent(net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END,p));
        verify(p).removePotionEffect(MobEffects.NIGHT_VISION);
        EntityPlayer other=equipped(false);other.getEntityData().setBoolean("MetroNvgVisionOwned",true);
        when(other.getActivePotionEffect(MobEffects.NIGHT_VISION)).thenReturn(new PotionEffect(MobEffects.NIGHT_VISION,6000,0,false,false));
        NvgControl.toggle(other);NvgEquipment.tick(new net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent(net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END,other));
        verify(other,never()).removePotionEffect(MobEffects.NIGHT_VISION);
    }
    @Test public void clonePreservesSwitchButNotOldRateLimit()throws Exception{
        EntityPlayer old=equipped(false),next=equipped(false);NvgControl.toggle(old);
        NvgControl.clone(new net.minecraftforge.event.entity.player.PlayerEvent.Clone(next,old,true));assertFalse(NvgControl.requested(next));assertTrue(NvgControl.toggle(next));
    }
    @Test public void statePacketRoundTripHasOnlyOwnerTelemetry(){
        io.netty.buffer.ByteBuf b=io.netty.buffer.Unpooled.buffer();NvgStatePacket packet=new NvgStatePacket(-1,42,true);packet.toBytes(b);assertEquals(9,b.readableBytes());
        NvgStatePacket copy=new NvgStatePacket();copy.fromBytes(b);assertEquals(0,b.readableBytes());copy.toBytes(b);assertEquals(-1,b.readInt());assertEquals(42,b.readInt());assertTrue(b.readBoolean());b.release();
    }
}
