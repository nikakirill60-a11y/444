package ru.metrocraft.port.blackrazid;

import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import ru.metrocraft.port.blackrazid.weapons.*;
import java.util.*;

public class NativeWeaponsTest {
    @BeforeClass public static void boot(){
        Bootstrap.register();
        for(Map.Entry<String,Item> e:BlackrazidWeapons.items().entrySet()){
            ResourceLocation name=e.getValue().getRegistryName();
            if(!Item.REGISTRY.containsKey(name))net.minecraftforge.fml.common.registry.ForgeRegistries.ITEMS.register(e.getValue());
        }
    }
    static Item item(String id){return BlackrazidWeapons.item("blackrazid_"+id);}
    static MagazineItem mag(String id){return (MagazineItem)item(id);}
    static BlackrazidGunItem gun(String id){return (BlackrazidGunItem)item(id);}
    static ItemStack magazine(String id,int rounds){ItemStack s=new ItemStack(item(id));mag(id).rounds(s,rounds);return s;}
    static EntityPlayer player(boolean remote)throws Exception{
        EntityPlayer p=SurvivalRulesTest.player(remote);
        java.lang.reflect.Field inventory=EntityPlayer.class.getField("inventory");inventory.setAccessible(true);inventory.set(p,new InventoryPlayer(p));
        java.lang.reflect.Field random=World.class.getField("rand");random.setAccessible(true);random.set(p.world,new Random(1));
        java.lang.reflect.Field provider=World.class.getField("provider");provider.setAccessible(true);provider.set(p.world,mock(net.minecraft.world.WorldProvider.class));
        when(p.getCooldownTracker()).thenReturn(new CooldownTracker());
        when(p.getUniqueID()).thenReturn(UUID.randomUUID());when(p.getLookVec()).thenReturn(new Vec3d(0,0,1));
        return p;
    }
    static void hold(EntityPlayer p,ItemStack stack){p.inventory.currentItem=0;p.inventory.setInventorySlotContents(0,stack);when(p.getHeldItemMainhand()).thenReturn(stack);}
    static void ticks(BlackrazidGunItem item,ItemStack stack,EntityPlayer p,int count){for(int i=0;i<count;i++)item.onUpdate(stack,p.world,p,0,true);}
    @Test public void allNativeItemsHaveSeparateIdsAndMagazineCapacities(){
        assertEquals(35,BlackrazidWeapons.items().size());assertEquals(30,mag("ak_ammo").entry.capacity);assertEquals(40,mag("rpk_ammo").entry.capacity);assertEquals(100,mag("rpk_ammo_big").entry.capacity);
        assertEquals(150,mag("gatling_ammo").entry.capacity);assertEquals(8,mag("helsing_ammo").entry.capacity);assertEquals(15,mag("tihar_ammo").entry.capacity);
    }
    @Test public void freshMagazinesEmptyExceptNativeFuelOffset(){
        for(Item item:BlackrazidWeapons.items().values())if(item instanceof MagazineItem){MagazineItem m=(MagazineItem)item;ItemStack s=new ItemStack(m);assertEquals(m.entry.defaultRounds,m.rounds(s));assertFalse(s.hasTagCompound());}
        assertEquals(200,mag("ognemet_ammo").rounds(new ItemStack(item("ognemet_ammo"))));assertEquals(5,mag("ognemet_ammo").entry.sourceCapacityArgument);
    }
    @Test public void readingDoesNotCreateTagsOrShareRoundsBetweenStacks(){
        ItemStack a=new ItemStack(item("ak_ammo")),b=new ItemStack(item("ak_ammo"));assertEquals(0,mag("ak_ammo").rounds(a));assertFalse(a.hasTagCompound());mag("ak_ammo").rounds(a,17);assertEquals(17,mag("ak_ammo").rounds(a));assertEquals(0,mag("ak_ammo").rounds(b));
    }
    @Test public void malformedAndOverflowNBTClampedBeforeFuelOffset(){
        for(String id:new String[]{"ak_ammo","ognemet_ammo"}){
            MagazineItem m=mag(id);ItemStack s=magazine(id,0);s.getTagCompound().setInteger("Ammo",Integer.MAX_VALUE);assertEquals(m.entry.capacity,m.rounds(s));
            s.getTagCompound().setInteger("Ammo",Integer.MIN_VALUE);assertEquals(0,m.rounds(s));
        }
        ItemStack s=magazine("ognemet_ammo",33);assertEquals(-167,s.getTagCompound().getInteger("Ammo"));assertEquals(33,mag("ognemet_ammo").rounds(s));
    }
    @Test public void oneClickMovesExactlyOneMatchingRoundServerSide()throws Exception{
        EntityPlayer p=player(false);ItemStack s=magazine("ak_ammo",5);hold(p,s);p.inventory.setInventorySlotContents(1,new ItemStack(item("bullet5x45"),20));
        assertTrue(mag("ak_ammo").fillOne(s,p));assertEquals(6,mag("ak_ammo").rounds(s));assertEquals(19,p.inventory.getStackInSlot(1).getCount());
        assertFalse(mag("ak_ammo").fillOne(s,p));assertEquals(19,p.inventory.getStackInSlot(1).getCount());
    }
    @Test public void fullOrWrongCaliberMagazineConsumesNothing()throws Exception{
        EntityPlayer p=player(false);ItemStack s=magazine("ak_ammo",30);hold(p,s);p.inventory.setInventorySlotContents(1,new ItemStack(item("bullet5x45"),20));assertFalse(mag("ak_ammo").fillOne(s,p));
        mag("ak_ammo").rounds(s,0);p.inventory.setInventorySlotContents(1,new ItemStack(item("bullet12x70"),20));assertFalse(mag("ak_ammo").fillOne(s,p));assertEquals(20,p.inventory.getStackInSlot(1).getCount());
    }
    @Test public void clientSpectatorAndUnheldCopiesCannotFill()throws Exception{
        for(boolean remote:new boolean[]{true,false}){
            EntityPlayer p=player(remote);ItemStack s=magazine("ak_ammo",3);hold(p,s);p.inventory.setInventorySlotContents(1,new ItemStack(item("bullet5x45"),9));
            when(p.isSpectator()).thenReturn(!remote);assertFalse(mag("ak_ammo").fillOne(s,p));assertEquals(3,mag("ak_ammo").rounds(s));assertEquals(9,p.inventory.getStackInSlot(1).getCount());
        }
        EntityPlayer p=player(false);ItemStack s=magazine("ak_ammo",3);hold(p,s);p.inventory.setInventorySlotContents(1,new ItemStack(item("bullet5x45"),9));assertFalse(mag("ak_ammo").fillOne(s.copy(),p));
    }
    @Test public void lockedInventoryAndArmorNotUsedForRounds()throws Exception{
        EntityPlayer p=player(false);ItemStack s=magazine("ak_ammo",0);hold(p,s);
        p.inventory.setInventorySlotContents(9,new ItemStack(item("bullet5x45")));p.inventory.setInventorySlotContents(39,new ItemStack(item("bullet5x45")));
        assertFalse(mag("ak_ammo").fillOne(s,p));assertEquals(0,mag("ak_ammo").rounds(s));
        p.inventory.setInventorySlotContents(40,new ItemStack(item("bullet5x45")));assertTrue(mag("ak_ammo").fillOne(s,p));assertEquals(1,mag("ak_ammo").rounds(s));assertTrue(p.inventory.getStackInSlot(40).isEmpty());
    }
    @Test public void fuelCannotBeRefilledUsingConstructorRoundDependency()throws Exception{
        EntityPlayer p=player(false);ItemStack s=magazine("ognemet_ammo",23);hold(p,s);p.inventory.setInventorySlotContents(1,new ItemStack(item("bullet12_7x55"),10));assertFalse(mag("ognemet_ammo").fillOne(s,p));assertEquals(23,mag("ognemet_ammo").rounds(s));assertEquals(10,p.inventory.getStackInSlot(1).getCount());
    }
    @Test public void overstackedMagazineCannotDuplicateItsAmmunition()throws Exception{
        EntityPlayer p=player(false);ItemStack gun=new ItemStack(item("ak"));hold(p,gun);ItemStack s=magazine("ak_ammo",20);s.setCount(2);p.inventory.setInventorySlotContents(1,s);
        assertFalse(gun("ak").insertMagazine(gun,p));assertFalse(mag("ak_ammo").fillOne(s,p));assertEquals(2,s.getCount());assertEquals(0,gun("ak").ammo(gun));
    }
    @Test public void wrongMagazineAndLooseRoundsCannotLoadAGun()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("ak"));hold(p,s);p.inventory.setInventorySlotContents(1,magazine("saiga_ammo",10));p.inventory.setInventorySlotContents(2,new ItemStack(item("bullet5x45"),64));
        assertFalse(gun("ak").insertMagazine(s,p));assertFalse(gun("ak").accepts(p.inventory.getStackInSlot(1)));assertEquals(64,p.inventory.getStackInSlot(2).getCount());
    }
    @Test public void loadingWaitsForNativeDurationAndMovesFullExactStack()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("ak"));hold(p,s);ItemStack m=magazine("ak_ammo",17);m.getTagCompound().setString("Custom","preserve me");p.inventory.setInventorySlotContents(1,m);
        gun("ak").startReload(s,p);assertTrue(gun("ak").reloading(s));assertEquals(0,gun("ak").ammo(s));ticks(gun("ak"),s,p,39);assertSame(m,p.inventory.getStackInSlot(1));ticks(gun("ak"),s,p,1);
        assertEquals(17,gun("ak").ammo(s));assertEquals(30,gun("ak").capacity(s));assertTrue(p.inventory.getStackInSlot(1).isEmpty());assertEquals("preserve me",gun("ak").magazine(s).getTagCompound().getString("Custom"));
    }
    @Test public void switchCancelsLoadingWithoutReservationOrLoss()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("ak"));hold(p,s);ItemStack m=magazine("ak_ammo",20);p.inventory.setInventorySlotContents(1,m);gun("ak").startReload(s,p);ticks(gun("ak"),s,p,10);
        when(p.getHeldItemMainhand()).thenReturn(ItemStack.EMPTY);gun("ak").onUpdate(s,p.world,p,0,false);assertFalse(gun("ak").reloading(s));assertEquals(0,gun("ak").ammo(s));assertSame(m,p.inventory.getStackInSlot(1));
    }
    @Test public void replacedOrMovedAmmoRecheckedAtCompletion()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("ak"));hold(p,s);p.inventory.setInventorySlotContents(1,magazine("ak_ammo",20));gun("ak").startReload(s,p);p.inventory.setInventorySlotContents(1,new ItemStack(Items.STICK));ticks(gun("ak"),s,p,40);
        assertEquals(0,gun("ak").ammo(s));assertEquals(Items.STICK,p.inventory.getStackInSlot(1).getItem());assertFalse(gun("ak").reloading(s));
    }
    @Test public void filledPreferredNativeMagazineIsChosenBeforeSmallAndEmpty()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("ak"));hold(p,s);p.inventory.setInventorySlotContents(1,magazine("ak_ammo",30));p.inventory.setInventorySlotContents(2,magazine("rpk_ammo",12));
        assertTrue(gun("ak").insertMagazine(s,p));assertEquals(40,gun("ak").capacity(s));assertEquals(12,gun("ak").ammo(s));assertEquals(30,mag("ak_ammo").rounds(p.inventory.getStackInSlot(1)));
    }
    @Test public void ejectionIsTimedAndPreservesAllRoundsAndTags()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("ak"));hold(p,s);ItemStack m=magazine("ak_ammo",11);m.getTagCompound().setString("Owner","tag");p.inventory.setInventorySlotContents(1,m);gun("ak").insertMagazine(s,p);
        gun("ak").startReload(s,p);ticks(gun("ak"),s,p,21);assertEquals(11,gun("ak").ammo(s));ticks(gun("ak"),s,p,1);assertEquals(0,gun("ak").ammo(s));assertEquals(11,mag("ak_ammo").rounds(p.inventory.getStackInSlot(1)));assertEquals("tag",p.inventory.getStackInSlot(1).getTagCompound().getString("Owner"));
    }
    @Test public void fullInventoryRetainsMagazineWithoutAnyTossOrNoDropCallback()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("ak"));hold(p,s);ItemStack retained=magazine("ak_ammo",14);retained.getTagCompound().setBoolean("NoDrop",true);p.inventory.setInventorySlotContents(1,retained);gun("ak").insertMagazine(s,p);
        for(int i=1;i<9;i++)p.inventory.setInventorySlotContents(i,new ItemStack(Items.STICK));assertFalse(gun("ak").ejectMagazine(s,p));assertEquals(14,gun("ak").ammo(s));assertTrue(p.inventory.getStackInSlot(9).isEmpty());
        assertFalse(gun("ak").ejectMagazine(s,p));verify(p,never()).dropItem(any(ItemStack.class),anyBoolean());
        assertEquals(14,gun("ak").ammo(s));p.inventory.setInventorySlotContents(1,ItemStack.EMPTY);assertTrue(gun("ak").ejectMagazine(s,p));assertEquals(14,mag("ak_ammo").rounds(p.inventory.getStackInSlot(1)));assertFalse(gun("ak").ejectMagazine(s,p));
    }
    @Test public void playerTimersAndGunCopiesAreIndependent()throws Exception{
        EntityPlayer a=player(false),b=player(false);ItemStack x=new ItemStack(item("ak")),y=new ItemStack(item("ak"));hold(a,x);hold(b,y);a.inventory.setInventorySlotContents(1,magazine("ak_ammo",10));b.inventory.setInventorySlotContents(1,magazine("ak_ammo",20));gun("ak").startReload(x,a);ticks(gun("ak"),x,a,40);assertEquals(10,gun("ak").ammo(x));assertEquals(0,gun("ak").ammo(y));assertFalse(gun("ak").reloading(y));
        ItemStack saved=new ItemStack(x.writeToNBT(new NBTTagCompound()));assertEquals(10,gun("ak").ammo(saved));assertEquals(30,gun("ak").capacity(saved));
    }
    @Test public void clientDeadAndSpectatorCannotInsertOrFire()throws Exception{
        for(int mode=0;mode<3;mode++){
            EntityPlayer p=player(mode==0);when(p.isSpectator()).thenReturn(mode==1);when(p.isEntityAlive()).thenReturn(mode!=2);ItemStack s=new ItemStack(item("ak"));hold(p,s);p.inventory.setInventorySlotContents(1,magazine("ak_ammo",5));
            assertFalse(gun("ak").insertMagazine(s,p));assertFalse(gun("ak").fireNative(s,p));assertEquals(5,mag("ak_ammo").rounds(p.inventory.getStackInSlot(1)));verify(p.world,never()).spawnEntity(any(Entity.class));
        }
    }
    @Test public void shotConsumesOneOnlyAfterSuccessfulSpawnAndCooldownIsPerStack()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("ak"));hold(p,s);p.inventory.setInventorySlotContents(1,magazine("ak_ammo",5));gun("ak").insertMagazine(s,p);
        assertFalse(gun("ak").fireNative(s,p));assertEquals(5,gun("ak").ammo(s));when(p.world.spawnEntity(any(Entity.class))).thenReturn(true);
        assertTrue(gun("ak").fireNative(s,p));assertEquals(4,gun("ak").ammo(s));assertFalse(gun("ak").fireNative(s,p));ticks(gun("ak"),s,p,2);assertFalse(gun("ak").fireNative(s,p));ticks(gun("ak"),s,p,1);assertTrue(gun("ak").fireNative(s,p));assertEquals(3,gun("ak").ammo(s));
    }
    @Test public void sprintAndReloadCannotFireOrSpendAmmo()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("ak"));hold(p,s);p.inventory.setInventorySlotContents(1,magazine("ak_ammo",5));gun("ak").insertMagazine(s,p);when(p.world.spawnEntity(any(Entity.class))).thenReturn(true);when(p.isSprinting()).thenReturn(true);assertFalse(gun("ak").fireNative(s,p));when(p.isSprinting()).thenReturn(false);gun("ak").startReload(s,p);assertFalse(gun("ak").fireNative(s,p));assertEquals(5,gun("ak").ammo(s));
    }
    @Test public void spreadingPreservesSpeedAndZeroAccuracyPreservesDirection(){
        Random random=new Random(3);for(int i=0;i<300;i++)assertEquals(10,BlackrazidBullet.direction(new Vec3d(.5,.25,1),10,15,random).length(),.000001);
        Vec3d exact=BlackrazidBullet.direction(new Vec3d(0,0,1),4,0,random);assertEquals(0,exact.x,0);assertEquals(0,exact.y,0);assertEquals(4,exact.z,0);
    }
    @Test public void legacyLooseRoundOrMCMODNBTDoesNotGrantNativeAmmo(){
        ItemStack s=new ItemStack(item("ak"));s.setTagCompound(new NBTTagCompound());s.getTagCompound().setInteger("MetroAmmo",10000);s.getTagCompound().setInteger("Bullet_Left",10000);s.getTagCompound().setInteger("modBarrel",3);assertEquals(0,gun("ak").ammo(s));assertEquals(0,gun("ak").capacity(s));
    }
    @Test public void pendingReloadCannotFollowAGunToAnotherPlayer()throws Exception{
        EntityPlayer a=player(false),b=player(false);ItemStack s=new ItemStack(item("ak"));hold(a,s);a.inventory.setInventorySlotContents(1,magazine("ak_ammo",10));gun("ak").startReload(s,a);
        hold(b,s);b.inventory.setInventorySlotContents(1,magazine("ak_ammo",20));ticks(gun("ak"),s,b,40);assertEquals(0,gun("ak").ammo(s));assertFalse(gun("ak").reloading(s));assertEquals(20,mag("ak_ammo").rounds(b.inventory.getStackInSlot(1)));
    }
    @Test public void intermediateUnloadedChunkStopsBulletBeforeRayTrace()throws Exception{
        World world=player(false).world;when(world.isBlockLoaded(any(BlockPos.class))).thenReturn(true);
        Vec3d from=new Vec3d(15,64,15),to=new Vec3d(21,64,21);assertTrue(BlackrazidBullet.loadedPath(world,from,to));
        when(world.isBlockLoaded(new BlockPos(16,64,0))).thenReturn(false);assertFalse(BlackrazidBullet.loadedPath(world,from,to));
        assertFalse(BlackrazidBullet.loadedPath(world,from,new Vec3d(Double.NaN,64,21)));assertFalse(BlackrazidBullet.loadedPath(world,from,new Vec3d(1000,64,21)));
        verify(world,never()).rayTraceBlocks(any(Vec3d.class),any(Vec3d.class),anyBoolean(),anyBoolean(),anyBoolean());
    }
    @Test public void deathAndLogoutCancelPendingActionWithoutSpendingResources()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("ak"));hold(p,s);ItemStack m=magazine("ak_ammo",15);p.inventory.setInventorySlotContents(1,m);
        gun("ak").startReload(s,p);BlackrazidWeapons.death(new net.minecraftforge.event.entity.living.LivingDeathEvent(p,DamageSource.GENERIC));assertFalse(gun("ak").reloading(s));assertSame(m,p.inventory.getStackInSlot(1));
        gun("ak").startReload(s,p);BlackrazidWeapons.logout(new net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent(p));assertFalse(gun("ak").reloading(s));assertSame(m,p.inventory.getStackInSlot(1));
        gun("ak").insertMagazine(s,p);gun("ak").startReload(s,p);BlackrazidWeapons.dimension(new net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent(p,0,1));assertFalse(gun("ak").reloading(s));assertEquals(15,gun("ak").ammo(s));
    }
}
