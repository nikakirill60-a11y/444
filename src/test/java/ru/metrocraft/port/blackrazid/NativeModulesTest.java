package ru.metrocraft.port.blackrazid;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.*;
import org.mockito.ArgumentCaptor;
import ru.metrocraft.port.blackrazid.weapons.*;
import static ru.metrocraft.port.blackrazid.NativeWeaponsTest.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NativeModulesTest {
    @BeforeClass public static void boot(){NativeWeaponsTest.boot();}
    private static int choice(String source){for(int i=0;i<BlackrazidModules.entries().size();i++)if(BlackrazidModules.entry(i).sourceId.equals(source))return i+1;throw new AssertionError(source);}
    private static ItemStack part(String source){return new ItemStack(BlackrazidWeapons.item(BlackrazidModules.entry(choice(source)-1).id));}
    private static void open(EntityPlayer p){p.openContainer=new NativeModuleContainer(p);assertTrue(p.openContainer.canInteractWith(p));}
    private static void close(EntityPlayer p){p.openContainer=p.inventoryContainer;}
    private static void install(EntityPlayer p,ItemStack s,int slot,String source){
        p.inventory.setInventorySlotContents(8,part(source));open(p);assertTrue(BlackrazidModules.change(p,s,slot,choice(source)));close(p);
    }
    private static ItemStack held(EntityPlayer p,String id){ItemStack s=new ItemStack(item(id));hold(p,s);return s;}
    private static void air(ItemStack s,int value){if(!s.hasTagCompound())s.setTagCompound(new NBTTagCompound());s.getTagCompound().setInteger("BlackrazidPressure",value);}
    @Test public void all28SingleStackNativePartsHaveSeparateStableEntries(){
        assertEquals(28,BlackrazidModules.entries().size());
        assertEquals(28,BlackrazidWeapons.items().values().stream().filter(i->i instanceof BlackrazidModules.ModuleItem).count());
        for(BlackrazidModules.Entry e:BlackrazidModules.entries()){assertTrue(e.id.startsWith("blackrazid_mod_"));assertEquals(1,BlackrazidWeapons.item(e.id).getItemStackLimit());}
    }
    @Test public void readsNeverCreateNBTAndIndependentGunsDoNotShareModules(){
        ItemStack a=new ItemStack(item("ak")),b=new ItemStack(item("ak"));
        for(int i=-1;i<7;i++)assertTrue(BlackrazidModules.get(a,i).isEmpty());assertFalse(a.hasTagCompound());assertEquals(1.2F,BlackrazidModules.zoom(b),0);
    }
    @Test public void nativeSocketIndicesAndWeaponSpecificSpecializationRejectUnrelatedParts(){
        assertTrue(BlackrazidModules.accepts(new ItemStack(item("abzac")),0,part("AutoFire")));
        assertFalse(BlackrazidModules.accepts(new ItemStack(item("ak")),0,part("AutoFire")));
        assertFalse(BlackrazidModules.accepts(new ItemStack(item("aksu")),2,part("Glushak"))); // Native AKSU GUI has no socket 2.
        assertTrue(BlackrazidModules.accepts(new ItemStack(item("duplet")),0,part("Laser")));
        assertFalse(BlackrazidModules.accepts(new ItemStack(item("duplet")),1,part("Laser")));
        assertTrue(BlackrazidModules.accepts(new ItemStack(item("gatling")),2,part("GatlingMotor")));
        assertFalse(BlackrazidModules.accepts(new ItemStack(item("gatling")),2,part("RailgunAkk")));
        assertFalse(BlackrazidModules.hasSockets(new ItemStack(item("ognemet"))));assertFalse(BlackrazidModules.hasSockets(new ItemStack(item("velopal"))));
    }
    @Test public void installRemovesExactlyOriginalItemAndRemovalReturnsAllCustomNBT()throws Exception{
        EntityPlayer p=player(false);ItemStack s=held(p,"revolver"),part=part("Butt");part.setTagCompound(new NBTTagCompound());part.getTagCompound().setString("Serial","native-17");p.inventory.setInventorySlotContents(2,part);
        open(p);assertTrue(BlackrazidModules.change(p,s,3,choice("Butt")));assertTrue(p.inventory.getStackInSlot(2).isEmpty());assertEquals("native-17",BlackrazidModules.get(s,3).getTagCompound().getString("Serial"));
        ItemStack detached=BlackrazidModules.get(s,3);detached.getTagCompound().setString("Serial","edited");assertEquals("native-17",BlackrazidModules.get(s,3).getTagCompound().getString("Serial"));
        assertFalse(BlackrazidModules.change(p,s,3,choice("Butt")));assertTrue(BlackrazidModules.change(p,s,3,0));assertTrue(BlackrazidModules.get(s,3).isEmpty());
        ItemStack returned=p.inventory.getStackInSlot(1);assertEquals("native-17",returned.getTagCompound().getString("Serial"));assertFalse(BlackrazidModules.change(p,s,3,0));
    }
    @Test public void survivesRealNBTSerializationAndDoesNotCrossGunsOrPlayers()throws Exception{
        EntityPlayer p=player(false),q=player(false);ItemStack s=held(p,"ak"),other=held(q,"ak");install(p,s,2,"Glushak");
        ItemStack loaded=new ItemStack(s.writeToNBT(new NBTTagCompound()));assertTrue(BlackrazidModules.has(loaded,2,"Glushak"));assertFalse(BlackrazidModules.has(other,2,"Glushak"));
        assertEquals("blackrazid_vsv_shootclose",gun("ak").effects(loaded).sound);assertEquals("blackrazid_ak_shoot",gun("ak").effects(other).sound);
    }
    @Test public void clientSpectatorDeadWrongHeldCursorAndForeignContainerCannotInstall()throws Exception{
        EntityPlayer p=player(false);ItemStack s=held(p,"revolver");p.inventory.setInventorySlotContents(1,part("Butt"));assertFalse(BlackrazidModules.change(p,s,3,choice("Butt")));
        open(p);p.inventory.setItemStack(part("Laser"));assertFalse(BlackrazidModules.change(p,s,3,choice("Butt")));p.inventory.setItemStack(ItemStack.EMPTY);
        when(p.isSpectator()).thenReturn(true);assertFalse(BlackrazidModules.change(p,s,3,choice("Butt")));when(p.isSpectator()).thenReturn(false);
        when(p.isEntityAlive()).thenReturn(false);assertFalse(BlackrazidModules.change(p,s,3,choice("Butt")));when(p.isEntityAlive()).thenReturn(true);
        when(p.getHeldItemMainhand()).thenReturn(s.copy());assertFalse(BlackrazidModules.change(p,s,3,choice("Butt")));
        EntityPlayer client=player(true);ItemStack c=held(client,"revolver");client.inventory.setInventorySlotContents(1,part("Butt"));open(client);assertFalse(BlackrazidModules.change(client,c,3,choice("Butt")));
        assertEquals(1,p.inventory.getStackInSlot(1).getCount());assertFalse(s.hasTagCompound());
    }
    @Test public void sameItemNBTRefreshOnlyRelaxesIdentityOnClientNotServer()throws Exception{
        for(boolean client:new boolean[]{true,false}){
            EntityPlayer p=player(client);ItemStack s=held(p,"ak");NativeModuleContainer container=new NativeModuleContainer(p);
            when(p.getHeldItemMainhand()).thenReturn(s.copy());assertEquals(client,container.canInteractWith(p));
            p.inventory.currentItem=1;assertFalse(container.canInteractWith(p));
        }
    }
    @Test public void fullAccessibleInventoryPreventsRemovalWithoutLossOrDrop()throws Exception{
        EntityPlayer p=player(false);ItemStack s=held(p,"revolver");install(p,s,3,"Butt");open(p);
        for(int i=1;i<36;i++)p.inventory.setInventorySlotContents(i,new ItemStack(net.minecraft.init.Items.STICK));
        assertFalse(BlackrazidModules.change(p,s,3,0));assertTrue(BlackrazidModules.has(s,3,"Butt"));verify(p,never()).dropItem(any(ItemStack.class),anyBoolean());
    }
    @Test public void noMissingInaccessibleWrongSocketStackedOrOutOfBoundsInstall()throws Exception{
        EntityPlayer p=player(false);ItemStack s=held(p,"revolver");open(p);assertFalse(BlackrazidModules.change(p,s,3,choice("Butt")));
        p.inventory.setInventorySlotContents(35,part("Butt"));assertFalse(BlackrazidModules.change(p,s,3,choice("Butt")));
        p.inventory.setInventorySlotContents(1,part("Butt"));assertFalse(BlackrazidModules.change(p,s,2,choice("Butt")));
        p.inventory.getStackInSlot(1).setCount(2);assertFalse(BlackrazidModules.change(p,s,3,choice("Butt")));
        for(int n:new int[]{-1,5,255})assertFalse(BlackrazidModules.change(p,s,n,choice("Butt")));
        for(int n:new int[]{-1,29,255})assertFalse(BlackrazidModules.change(p,s,3,n));assertFalse(s.hasTagCompound());
    }
    @Test public void tamperedNBTWrongItemCountSocketAndNonGunProduceNoEffects(){
        ItemStack s=new ItemStack(item("revolver"));NBTTagCompound root=new NBTTagCompound(),mods=new NBTTagCompound();
        mods.setTag("3",new ItemStack(net.minecraft.init.Items.DIAMOND).writeToNBT(new NBTTagCompound()));root.setTag("BlackrazidModules",mods);s.setTagCompound(root);assertTrue(BlackrazidModules.get(s,3).isEmpty());
        ItemStack butt=part("Butt");butt.setCount(2);mods.setTag("3",butt.writeToNBT(new NBTTagCompound()));assertTrue(BlackrazidModules.get(s,3).isEmpty());
        mods.setTag("2",part("Butt").writeToNBT(new NBTTagCompound()));assertTrue(BlackrazidModules.get(s,2).isEmpty());assertEquals(.6F,gun("revolver").effects(s).recoil,0);
        ItemStack fake=new ItemStack(net.minecraft.init.Items.STICK);fake.setTagCompound(root);assertTrue(BlackrazidModules.get(fake,3).isEmpty());
    }
    @Test public void nativeStocksReduceRecoilAndDupletStocksDoNotStackBonuses()throws Exception{
        for(String[] row:new String[][]{{"revolver","Butt"},{"ubludok","UbludokButt"},{"podonok","PodonokButt"}}){
            EntityPlayer p=player(false);ItemStack s=held(p,row[0]);install(p,s,3,row[1]);assertEquals(.4F,gun(row[0]).effects(s).recoil,0);
        }
        EntityPlayer p=player(false);ItemStack d=held(p,"duplet");install(p,d,2,"DupletButt");install(p,d,3,"DupletPriklad");assertEquals(1,gun("duplet").effects(d).recoil,0);
    }
    @Test public void everyNativeScopeChangesZoomWithoutChangingDamageOrInventingLaserSpreadBonus()throws Exception{
        EntityPlayer p=player(false);ItemStack s=held(p,"ak");
        String[] scopes={"Kolimator","OpticaSmall","Optica","TiharOptica"};float[] zoom={1.5F,2,4,4};
        for(int i=0;i<scopes.length;i++){install(p,s,0,scopes[i]);assertEquals(zoom[i],BlackrazidModules.zoom(s),0);open(p);assertTrue(BlackrazidModules.change(p,s,0,0));close(p);}
        NativeModuleEffects base=gun("ak").effects(s);install(p,s,1,"Laser");assertTrue(BlackrazidModules.laser(s));assertEquals(base.accuracy,gun("ak").effects(s).accuracy);assertEquals(base.damage,gun("ak").effects(s).damage,0);
        ItemStack d=held(p,"duplet");install(p,d,0,"Laser");assertTrue(BlackrazidModules.laser(d));
    }
    @Test public void shotgunSuppressorsUseOriginalSoundAndVolumesAndBarrelsUseActualSpread()throws Exception{
        for(String id:new String[]{"ashot","saiga","uboinik"}){
            EntityPlayer p=player(false);ItemStack s=held(p,id);install(p,s,2,"Glushak");assertEquals("blackrazid_saiga_closeshoot",gun(id).effects(s).sound);
            assertEquals(id.equals("saiga")?.1F:id.equals("uboinik")?.3F:1,gun(id).effects(s).volume,0);
        }
        EntityPlayer p=player(false);ItemStack s=held(p,"uboinik");install(p,s,2,"UboinikBarrel");assertEquals(70,gun("uboinik").effects(s).accuracy);
        ItemStack d=held(p,"duplet");install(p,d,1,"DupletBarrel");assertEquals(70,gun("duplet").effects(d).accuracy);assertEquals(4,gun("duplet").effects(d).speed,0);
    }
    @Test public void barrelAddsOneDamageAndPrevedBarrelChangesActualProjectileSpeed()throws Exception{
        for(String id:new String[]{"revolver","podonok","preved"}){
            EntityPlayer p=player(false);ItemStack s=held(p,id);float damage=gun(id).entry.damage;install(p,s,2,"Barrel");
            assertEquals(damage+1,gun(id).effects(s).damage,0);assertEquals(id.equals("preved")?10:4,gun(id).effects(s).speed,0);
        }
    }
    @Test public void abzacGasAndRailgunCapacitorSelectNativeCooldowns()throws Exception{
        for(String[] row:new String[][]{{"abzac","AutoFire","0","4"},{"podonok","PodonokGas","4","3"},{"railgun","RailgunKon","2","8"}}){
            EntityPlayer p=player(false);ItemStack s=held(p,row[0]);install(p,s,Integer.parseInt(row[2]),row[1]);assertEquals(Integer.parseInt(row[3]),gun(row[0]).shotDelay(s));
        }
        EntityPlayer p=player(false);ItemStack a=held(p,"abzac");install(p,a,2,"AbzacGlushak");assertEquals(2,gun("abzac").effects(a).recoil,0);
    }
    @Test public void actualFireUsesNewCooldownNotOnlyDisplayedModuleStats()throws Exception{
        EntityPlayer p=player(false);ItemStack s=held(p,"abzac");p.inventory.setInventorySlotContents(1,magazine("abzac_ammo",6));assertTrue(gun("abzac").insertMagazine(s,p));install(p,s,0,"AutoFire");when(p.world.spawnEntity(any(Entity.class))).thenReturn(true);
        assertTrue(gun("abzac").fireNative(s,p));ticks(gun("abzac"),s,p,3);assertFalse(gun("abzac").fireNative(s,p));ticks(gun("abzac"),s,p,1);assertTrue(gun("abzac").fireNative(s,p));assertEquals(4,gun("abzac").ammo(s));
    }
    @Test public void quadBarrelReloadsFourShoots16PelletsForTwoShellsAndCannotDiscardLoadedAmmo()throws Exception{
        EntityPlayer p=player(false);ItemStack s=held(p,"duplet");install(p,s,1,"QuadBarrel");assertEquals(4,gun("duplet").capacity(s));p.inventory.setInventorySlotContents(1,new ItemStack(item("bullet12x70"),10));gun("duplet").startReload(s,p);ticks(gun("duplet"),s,p,30);assertEquals(4,gun("duplet").ammo(s));assertEquals(6,p.inventory.getStackInSlot(1).getCount());
        open(p);assertFalse(BlackrazidModules.change(p,s,1,0));assertEquals(4,gun("duplet").ammo(s));close(p);
        when(p.world.spawnEntity(any(Entity.class))).thenReturn(true);assertTrue(gun("duplet").fireNative(s,p));verify(p.world,times(16)).spawnEntity(any(Entity.class));assertEquals(2,gun("duplet").ammo(s));ticks(gun("duplet"),s,p,3);
        open(p);assertTrue(BlackrazidModules.change(p,s,1,0));assertEquals(2,gun("duplet").capacity(s));assertEquals(2,gun("duplet").ammo(s));
    }
    @Test public void quadRejectsSingleShellAndFailedSpawnsConsumeNothing()throws Exception{
        EntityPlayer p=player(false);ItemStack s=held(p,"duplet");install(p,s,1,"QuadBarrel");p.inventory.setInventorySlotContents(1,new ItemStack(item("bullet12x70"),1));gun("duplet").startReload(s,p);ticks(gun("duplet"),s,p,30);when(p.world.spawnEntity(any(Entity.class))).thenReturn(true);
        assertFalse(gun("duplet").fireNative(s,p));assertEquals(1,gun("duplet").ammo(s));ticks(gun("duplet"),s,p,7);
        p.inventory.setInventorySlotContents(1,new ItemStack(item("bullet12x70"),3));gun("duplet").startReload(s,p);ticks(gun("duplet"),s,p,30);when(p.world.spawnEntity(any(Entity.class))).thenReturn(false);assertFalse(gun("duplet").fireNative(s,p));assertEquals(4,gun("duplet").ammo(s));
    }
    @Test public void gatlingBarrelRecoilCombinationsAndMotorHaveActualPressureEffects()throws Exception{
        EntityPlayer p=player(false);ItemStack s=held(p,"gatling");install(p,s,0,"GatlingBarrel");
        for(int[] band:new int[][]{{1,4},{134,3},{267,2},{400,2}}){air(s,band[0]);assertEquals(band[1],gun("gatling").shotDelay(s));}
        assertEquals(.6F,gun("gatling").effects(s).recoil,0);install(p,s,1,"GatlingButt");assertEquals(.5F,gun("gatling").effects(s).recoil,0);
        install(p,s,2,"GatlingGiro");assertEquals(.3F,gun("gatling").effects(s).recoil,0);open(p);assertTrue(BlackrazidModules.change(p,s,1,0));close(p);assertEquals(.4F,gun("gatling").effects(s).recoil,0);
        open(p);assertTrue(BlackrazidModules.change(p,s,2,0));close(p);install(p,s,2,"GatlingMotor");air(s,0);ticks(gun("gatling"),s,p,1);assertEquals(400,gun("gatling").pressure(s));
        p.inventory.setInventorySlotContents(1,magazine("gatling_ammo",5));assertTrue(gun("gatling").insertMagazine(s,p));when(p.world.spawnEntity(any(Entity.class))).thenReturn(true);assertTrue(gun("gatling").fireNative(s,p));assertEquals(400,gun("gatling").pressure(s));assertEquals(4,gun("gatling").ammo(s));
    }
    @Test public void railgunBatteryRaisesCapacityAndCompletedPumpStepOnly()throws Exception{
        EntityPlayer p=player(false);ItemStack s=held(p,"railgun");install(p,s,2,"RailgunAkk");assertEquals(60,gun("railgun").pressureCapacity(s));assertTrue(gun("railgun").startPump(s,p));ticks(gun("railgun"),s,p,gun("railgun").entry.pumpTicks-1);assertEquals(0,gun("railgun").pressure(s));ticks(gun("railgun"),s,p,1);assertEquals(10,gun("railgun").pressure(s));
        for(int i=0;i<5;i++){assertTrue(gun("railgun").startPump(s,p));ticks(gun("railgun"),s,p,gun("railgun").entry.pumpTicks);}assertEquals(60,gun("railgun").pressure(s));assertFalse(gun("railgun").startPump(s,p));
    }
    @Test public void busyGunCannotOpenOrChangeModulesOrFireInsideModuleScreen()throws Exception{
        EntityPlayer p=player(false);ItemStack s=held(p,"revolver");open(p);assertFalse(gun("revolver").fireNative(s,p));close(p);
        p.inventory.setInventorySlotContents(1,new ItemStack(item("bullet_magnum"),6));gun("revolver").startReload(s,p);assertFalse(new NativeModuleContainer(p).canInteractWith(p));ticks(gun("revolver"),s,p,30);
        when(p.world.spawnEntity(any(Entity.class))).thenReturn(true);assertTrue(gun("revolver").fireNative(s,p));assertFalse(new NativeModuleContainer(p).canInteractWith(p));
    }
    @Test public void creativeStillConsumesAndReturnsExactlyOneActualPart()throws Exception{
        EntityPlayer p=player(false);p.capabilities.isCreativeMode=true;ItemStack s=held(p,"ak");install(p,s,2,"Glushak");
        assertTrue(p.inventory.getStackInSlot(8).isEmpty());open(p);assertTrue(BlackrazidModules.change(p,s,2,0));
        assertEquals(1,p.inventory.getStackInSlot(1).getCount());assertFalse(BlackrazidModules.change(p,s,2,0));
    }
    @Test public void actualPrevedBulletGetsBarrelSpeedAndDamageAndUsesOneRound()throws Exception{
        EntityPlayer p=player(false);ItemStack s=held(p,"preved");install(p,s,2,"Barrel");p.inventory.setInventorySlotContents(1,magazine("preved_ammo",5));assertTrue(gun("preved").insertMagazine(s,p));
        when(p.world.spawnEntity(any(Entity.class))).thenReturn(true);assertTrue(gun("preved").fireNative(s,p));
        ArgumentCaptor<Entity> capture=ArgumentCaptor.forClass(Entity.class);verify(p.world).spawnEntity(capture.capture());Entity bullet=capture.getValue();
        assertEquals(10,Math.sqrt(bullet.motionX*bullet.motionX+bullet.motionY*bullet.motionY+bullet.motionZ*bullet.motionZ),1e-5);
        java.lang.reflect.Field damage=BlackrazidBullet.class.getDeclaredField("damage");damage.setAccessible(true);assertEquals(gun("preved").entry.damage+1,damage.getFloat(bullet),0);assertEquals(4,gun("preved").ammo(s));
    }

}
