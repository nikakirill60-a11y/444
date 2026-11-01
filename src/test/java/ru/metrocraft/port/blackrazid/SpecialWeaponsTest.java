package ru.metrocraft.port.blackrazid;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import org.junit.*;
import org.mockito.ArgumentCaptor;
import ru.metrocraft.port.blackrazid.weapons.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static ru.metrocraft.port.blackrazid.NativeWeaponsTest.*;

public class SpecialWeaponsTest {
    @BeforeClass public static void boot(){NativeWeaponsTest.boot();}
    private static void pressure(ItemStack s,int air){if(!s.hasTagCompound())s.setTagCompound(new NBTTagCompound());s.getTagCompound().setInteger("BlackrazidPressure",air);}
    private static ItemStack equipped(EntityPlayer p,String id,String mag,int count){
        ItemStack s=new ItemStack(item(id));hold(p,s);p.inventory.setInventorySlotContents(1,magazine(mag,count));assertTrue(gun(id).insertMagazine(s,p));return s;
    }
    @Test public void all22GunDefinitionsExistWithOwnNativeIds(){
        long count=BlackrazidWeapons.items().values().stream().filter(i->i instanceof BlackrazidGunItem).count();assertEquals(22,count);
        assertEquals(10,gun("saiga").entry.pellets);assertEquals(8,gun("duplet").entry.pellets);assertEquals(35,gun("ventil").entry.delay);
        assertEquals(20,gun("railgun").entry.delay);assertEquals(7,gun("abzac").entry.delay);assertEquals(5,gun("podonok").entry.delay);
    }
    @Test public void shotgunSpawnsTenPelletsButConsumesOneShellAndSharesShotId()throws Exception{
        EntityPlayer p=player(false);ItemStack s=equipped(p,"saiga","saiga_ammo",9);when(p.world.spawnEntity(any(Entity.class))).thenReturn(true);
        assertTrue(gun("saiga").fireNative(s,p));assertEquals(8,gun("saiga").ammo(s));ArgumentCaptor<Entity> captor=ArgumentCaptor.forClass(Entity.class);verify(p.world,times(10)).spawnEntity(captor.capture());
        java.util.Set<java.util.UUID> ids=new java.util.HashSet<>();for(Entity e:captor.getAllValues())ids.add(((BlackrazidBullet)e).shotId());assertEquals(1,ids.size());
    }
    @Test public void rejectedPelletsDoNotCreateFreePartiallySuccessfulShots()throws Exception{
        EntityPlayer p=player(false);ItemStack s=equipped(p,"abzac","abzac_ammo",5);when(p.world.spawnEntity(any(Entity.class))).thenReturn(false);
        assertFalse(gun("abzac").fireNative(s,p));assertEquals(5,gun("abzac").ammo(s));when(p.world.spawnEntity(any(Entity.class))).thenReturn(false,true);
        assertTrue(gun("abzac").fireNative(s,p));assertEquals(4,gun("abzac").ammo(s));assertFalse(gun("abzac").fireNative(s,p));
    }
    @Test public void revolverLoadsUpToSixOnlyAtCompletionAndCannotTopUp()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("revolver"));hold(p,s);ItemStack rounds=new ItemStack(item("bullet_magnum"),4);rounds.setTagCompound(new NBTTagCompound());rounds.getTagCompound().setString("Batch","retained");p.inventory.setInventorySlotContents(1,rounds);
        gun("revolver").startReload(s,p);ticks(gun("revolver"),s,p,29);assertEquals(4,rounds.getCount());assertEquals(0,gun("revolver").ammo(s));ticks(gun("revolver"),s,p,1);
        assertEquals(4,gun("revolver").ammo(s));assertTrue(rounds.isEmpty());assertEquals("retained",gun("revolver").loose(s).getTagCompound().getString("Batch"));
        p.inventory.setInventorySlotContents(1,new ItemStack(item("bullet_magnum"),64));gun("revolver").startReload(s,p);assertFalse(gun("revolver").reloading(s));
    }
    @Test public void looseReloadCancellationAndCaliberValidationPreserveInventory()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("ashot"));hold(p,s);p.inventory.setInventorySlotContents(1,new ItemStack(item("bullet_magnum"),10));gun("ashot").startReload(s,p);assertFalse(gun("ashot").reloading(s));
        ItemStack shells=new ItemStack(item("bullet12x70"),5);p.inventory.setInventorySlotContents(1,shells);gun("ashot").startReload(s,p);when(p.getHeldItemMainhand()).thenReturn(ItemStack.EMPTY);gun("ashot").onUpdate(s,p.world,p,0,false);assertEquals(5,shells.getCount());assertEquals(0,gun("ashot").ammo(s));
    }
    @Test public void dupletCanTopUpSingleRemainingBarrelWithoutOverfill()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("duplet"));hold(p,s);p.inventory.setInventorySlotContents(1,new ItemStack(item("bullet12x70"),1));gun("duplet").startReload(s,p);ticks(gun("duplet"),s,p,30);assertEquals(1,gun("duplet").ammo(s));
        p.inventory.setInventorySlotContents(1,new ItemStack(item("bullet12x70"),10));gun("duplet").startReload(s,p);ticks(gun("duplet"),s,p,30);assertEquals(2,gun("duplet").ammo(s));assertEquals(9,p.inventory.getStackInSlot(1).getCount());
    }
    @Test public void uboinikRequiresSixShellsNotWrongMagnumAndWaits110Ticks()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("uboinik"));hold(p,s);p.inventory.setInventorySlotContents(1,new ItemStack(item("bullet12x70"),5));p.inventory.setInventorySlotContents(2,new ItemStack(item("bullet_magnum"),64));gun("uboinik").startReload(s,p);assertFalse(gun("uboinik").reloading(s));
        p.inventory.setInventorySlotContents(1,new ItemStack(item("bullet12x70"),7));gun("uboinik").startReload(s,p);ticks(gun("uboinik"),s,p,109);assertEquals(0,gun("uboinik").ammo(s));ticks(gun("uboinik"),s,p,1);assertEquals(6,gun("uboinik").ammo(s));assertEquals(1,p.inventory.getStackInSlot(1).getCount());assertEquals(64,p.inventory.getStackInSlot(2).getCount());
    }
    @Test public void sniper35TickCooldownIsNotTruncatedToSeven()throws Exception{
        EntityPlayer p=player(false);ItemStack s=equipped(p,"preved","preved_ammo",5);when(p.world.spawnEntity(any(Entity.class))).thenReturn(true);assertTrue(gun("preved").fireNative(s,p));ticks(gun("preved"),s,p,34);assertFalse(gun("preved").fireNative(s,p));ticks(gun("preved"),s,p,1);assertTrue(gun("preved").fireNative(s,p));assertEquals(3,gun("preved").ammo(s));
    }
    @Test public void prevedSingleRoundFallbackDoesNotMaterializeAMagazine()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("preved"));hold(p,s);p.inventory.setInventorySlotContents(1,new ItemStack(item("bullet12_7x108"),3));gun("preved").startReload(s,p);ticks(gun("preved"),s,p,40);assertEquals(1,gun("preved").ammo(s));assertEquals(2,p.inventory.getStackInSlot(1).getCount());assertTrue(gun("preved").magazine(s).isEmpty());
    }
    @Test public void ventilEjectionKeepsOneChamberRoundWithoutCloningItOnReinsert()throws Exception{
        EntityPlayer p=player(false);ItemStack s=equipped(p,"ventil","ventil_ammo",5);assertEquals(5,gun("ventil").ammo(s));assertEquals(1,gun("ventil").loose(s).getCount());assertEquals(4,mag("ventil_ammo").rounds(gun("ventil").magazine(s)));
        assertTrue(gun("ventil").ejectMagazine(s,p));assertEquals(1,gun("ventil").ammo(s));assertEquals(4,mag("ventil_ammo").rounds(p.inventory.getStackInSlot(1)));assertTrue(gun("ventil").insertMagazine(s,p));assertEquals(5,gun("ventil").ammo(s));
        when(p.world.spawnEntity(any(Entity.class))).thenReturn(true);assertTrue(gun("ventil").fireNative(s,p));assertEquals(4,gun("ventil").ammo(s));assertEquals(1,gun("ventil").loose(s).getCount());
    }
    @Test public void velopalReturnsItsActualVyhlopMagazineNotSourceCopyPasteVentil()throws Exception{
        EntityPlayer p=player(false);ItemStack s=equipped(p,"velopal","vyhlop_ammo",5);assertTrue(gun("velopal").ejectMagazine(s,p));assertEquals(item("vyhlop_ammo"),p.inventory.getStackInSlot(1).getItem());assertEquals(4,mag("vyhlop_ammo").rounds(p.inventory.getStackInSlot(1)));assertEquals(1,gun("velopal").ammo(s));assertEquals(item("bullet12_7x55"),gun("velopal").loose(s).getItem());
    }
    @Test public void pressureOnlyArrivesAfterCompletedNativePumpCycle()throws Exception{
        for(String id:new String[]{"helsing","tihar","railgun","gatling","ognemet"}){
            EntityPlayer p=player(false);ItemStack s=new ItemStack(item(id));hold(p,s);BlackrazidGunItem g=gun(id);assertEquals(0,g.pressure(s));assertTrue(g.startPump(s,p));ticks(g,s,p,g.entry.pumpTicks-1);assertEquals(0,g.pressure(s));ticks(g,s,p,1);assertEquals(g.entry.airStep,g.pressure(s));
        }
    }
    @Test public void pumpSpamSwitchAndClientCannotGiveFreePressure()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("tihar"));hold(p,s);assertTrue(gun("tihar").startPump(s,p));assertFalse(gun("tihar").startPump(s,p));ticks(gun("tihar"),s,p,20);when(p.getHeldItemMainhand()).thenReturn(ItemStack.EMPTY);gun("tihar").onUpdate(s,p.world,p,0,false);assertEquals(0,gun("tihar").pressure(s));
        EntityPlayer client=player(true);ItemStack c=new ItemStack(item("tihar"));hold(client,c);assertFalse(gun("tihar").startPump(c,client));assertFalse(gun("ak").startPump(new ItemStack(item("ak")),p));
    }
    @Test public void pumpRespectsCapsAndIndependentPlayersAndNBT()throws Exception{
        EntityPlayer p=player(false),other=player(false);ItemStack s=new ItemStack(item("helsing")),b=new ItemStack(item("helsing"));hold(p,s);hold(other,b);
        for(int i=0;i<8;i++){assertTrue(gun("helsing").startPump(s,p));ticks(gun("helsing"),s,p,15);}assertEquals(16,gun("helsing").pressure(s));assertFalse(gun("helsing").startPump(s,p));assertEquals(0,gun("helsing").pressure(b));
        ItemStack saved=new ItemStack(s.writeToNBT(new NBTTagCompound()));assertEquals(16,gun("helsing").pressure(saved));pressure(s,Integer.MAX_VALUE);assertEquals(16,gun("helsing").pressure(s));pressure(s,-9);assertEquals(0,gun("helsing").pressure(s));
    }
    @Test public void pneumaticsCannotFireWithoutAirAndSpendOneOnSuccessfulShotOnly()throws Exception{
        EntityPlayer p=player(false);ItemStack s=equipped(p,"tihar","tihar_ammo",10);when(p.world.spawnEntity(any(Entity.class))).thenReturn(true);assertFalse(gun("tihar").fireNative(s,p));assertEquals(10,gun("tihar").ammo(s));
        ticks(gun("tihar"),s,p,7);pressure(s,4);assertTrue(gun("tihar").fireNative(s,p));assertEquals(9,gun("tihar").ammo(s));assertEquals(3,gun("tihar").pressure(s));
    }
    @Test public void gatlingNativeWindingBandsSelectDifferentFireDelays(){
        ItemStack s=new ItemStack(item("gatling"));for(int[] band:new int[][]{{0,5},{1,5},{133,5},{134,4},{266,4},{267,3},{400,3}}){pressure(s,band[0]);assertEquals(band[1],gun("gatling").shotDelay(s));}
    }
    @Test public void flamethrowerCostsOneFuelAndTwoPressureAndHasOwnFlameProjectile()throws Exception{
        EntityPlayer p=player(false);ItemStack s=equipped(p,"ognemet","ognemet_ammo",200);pressure(s,1);assertFalse(gun("ognemet").fireNative(s,p));assertEquals(200,gun("ognemet").ammo(s));ticks(gun("ognemet"),s,p,7);pressure(s,45);
        assertFalse(gun("ognemet").fireNative(s,p));assertEquals(45,gun("ognemet").pressure(s));when(p.world.spawnEntity(any(Entity.class))).thenReturn(true);assertTrue(gun("ognemet").fireNative(s,p));assertEquals(199,gun("ognemet").ammo(s));assertEquals(43,gun("ognemet").pressure(s));
        ArgumentCaptor<Entity> captor=ArgumentCaptor.forClass(Entity.class);verify(p.world,times(2)).spawnEntity(captor.capture());assertTrue(((BlackrazidBullet)captor.getValue()).isFlame());
    }
    @Test public void flameOnlyBurnsAfterAcceptedDamageAndNeverOnClientOrImmuneTarget()throws Exception{
        EntityPlayer p=player(false);BlackrazidBullet b=new BlackrazidBullet(p.world,p,4,1,0,java.util.UUID.randomUUID(),true);EntityLivingBase target=mock(EntityLivingBase.class);
        assertFalse(b.burn(p,target));verify(target,never()).setFire(anyInt());when(target.attackEntityFrom(any(DamageSource.class),eq(4F))).thenReturn(true);assertTrue(b.burn(p,target));verify(target).setFire(3);
        EntityLivingBase immune=mock(EntityLivingBase.class);java.lang.reflect.Field fireProof=Entity.class.getDeclaredField("isImmuneToFire");fireProof.setAccessible(true);fireProof.setBoolean(immune,true);assertFalse(b.burn(p,immune));verify(immune,never()).attackEntityFrom(any(DamageSource.class),anyFloat());
        EntityPlayer client=player(true);BlackrazidBullet c=new BlackrazidBullet(client.world,client,4,1,0,java.util.UUID.randomUUID(),true);assertFalse(c.burn(client,target));
    }
    @Test public void flameRespectsPvPAndCreativeBeforeDamageOrIgnition()throws Exception{
        EntityPlayer p=player(false),victim=player(false);BlackrazidBullet b=new BlackrazidBullet(p.world,p,4,1,0,java.util.UUID.randomUUID(),true);assertFalse(b.burn(p,victim));verify(victim,never()).setFire(anyInt());
        when(p.canAttackPlayer(victim)).thenReturn(true);victim.capabilities.isCreativeMode=true;assertFalse(b.burn(p,victim));verify(victim,never()).attackEntityFrom(any(DamageSource.class),anyFloat());
    }
    @Test public void sprintCancelsPumpWithoutAwardingPressure()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("tihar"));hold(p,s);
        assertTrue(gun("tihar").startPump(s,p));ticks(gun("tihar"),s,p,24);when(p.isSprinting()).thenReturn(true);
        ticks(gun("tihar"),s,p,1);assertEquals(0,gun("tihar").pressure(s));assertFalse(gun("tihar").reloading(s));
        when(p.isSprinting()).thenReturn(false);ticks(gun("tihar"),s,p,50);assertEquals(0,gun("tihar").pressure(s));
    }
    @Test public void gatlingWindingDoesNotPassivelyDrainWhileIdle()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("gatling"));hold(p,s);pressure(s,400);
        ticks(gun("gatling"),s,p,500);assertEquals(400,gun("gatling").pressure(s));
    }
    @Test public void prevedCyclesImmediatelyOnlyWithRemainingInstalledAmmo()throws Exception{
        String id="blackrazid_preved_rel";
        net.minecraft.util.SoundEvent sound=new net.minecraft.util.SoundEvent(new net.minecraft.util.ResourceLocation("metroport",id));
        net.minecraft.util.SoundEvent previous=ru.metrocraft.port.MetroPort.SOUNDS.put(id,sound);
        try{
            for(int rounds:new int[]{5,1}){
                EntityPlayer p=player(false);ItemStack s=equipped(p,"preved","preved_ammo",rounds);
                when(p.world.spawnEntity(any(Entity.class))).thenReturn(true);assertTrue(gun("preved").fireNative(s,p));
                int expected=rounds>1?1:0;
                verify(p.world,times(expected)).playSound(isNull(),anyDouble(),anyDouble(),anyDouble(),eq(sound),eq(net.minecraft.util.SoundCategory.PLAYERS),eq(1F),eq(1F));
                ticks(gun("preved"),s,p,35);
                verify(p.world,times(expected)).playSound(isNull(),anyDouble(),anyDouble(),anyDouble(),eq(sound),eq(net.minecraft.util.SoundCategory.PLAYERS),eq(1F),eq(1F));
            }
        }finally{if(previous==null)ru.metrocraft.port.MetroPort.SOUNDS.remove(id);else ru.metrocraft.port.MetroPort.SOUNDS.put(id,previous);}
    }
    @Test public void flameModeAndShotIdentitySurviveNativeNBTAndSpawnData()throws Exception{
        EntityPlayer p=player(false);java.util.UUID shot=java.util.UUID.randomUUID();BlackrazidBullet b=new BlackrazidBullet(p.world,p,4,1,0,shot,true),copy=new BlackrazidBullet(p.world);
        java.lang.reflect.Method write=BlackrazidBullet.class.getDeclaredMethod("writeEntityToNBT",NBTTagCompound.class),read=BlackrazidBullet.class.getDeclaredMethod("readEntityFromNBT",NBTTagCompound.class);write.setAccessible(true);read.setAccessible(true);NBTTagCompound tag=new NBTTagCompound();write.invoke(b,tag);read.invoke(copy,tag);assertTrue(copy.isFlame());assertEquals(shot,copy.shotId());
        io.netty.buffer.ByteBuf buf=Unpooled.buffer();b.writeSpawnData(buf);BlackrazidBullet client=new BlackrazidBullet(p.world);client.readSpawnData(buf);assertTrue(client.isFlame());assertEquals(1,buf.writerIndex());buf.release();
    }
}
