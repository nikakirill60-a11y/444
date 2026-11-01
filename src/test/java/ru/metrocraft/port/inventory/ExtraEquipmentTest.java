package ru.metrocraft.port.inventory;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.Field;
import java.util.UUID;
import net.minecraft.entity.player.*;
import net.minecraft.init.Bootstrap;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.junit.*;
import ru.metrocraft.port.blackrazid.*;
import ru.metrocraft.port.blackrazid.survival.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ExtraEquipmentTest {
    private static SurvivalItem LAMP;
    private static SurvivalItem POWER;
    @BeforeClass public static void boot(){Bootstrap.register();LAMP=new SurvivalItem(SurvivalItem.Type.LAMP);POWER=new SurvivalItem(SurvivalItem.Type.POWER);}
    private static final class Player extends EntityPlayer {
        private Player(World w){super(w,new GameProfile(UUID.randomUUID(),"test"));}
        @Override public void stopActiveHand(){}
        @Override public boolean isEntityAlive(){return true;} @Override public boolean isSpectator(){return false;} @Override public boolean isCreative(){return false;}
    }
    private static Player player(int id,boolean remote)throws Exception{
        Field f=sun.misc.Unsafe.class.getDeclaredField("theUnsafe");f.setAccessible(true);
        Player p=(Player)((sun.misc.Unsafe)f.get(null)).allocateInstance(Player.class);p.world=mock(World.class);
        Field side=World.class.getField("isRemote");side.setAccessible(true);side.setBoolean(p.world,remote);
        p.setEntityId(id);p.inventory=new InventoryPlayer(p);Field caps=EntityPlayer.class.getField("capabilities");caps.setAccessible(true);caps.set(p,new PlayerCapabilities());return p;
    }
    private static BlackrazidBackpackItem bag(){BlackrazidContent.Entry e=new BlackrazidContent.Entry();e.storageSlots=5;return new BlackrazidBackpackItem(e);}
    private static int slot(MetroContainer c,boolean lamp){return lamp?44:45;}
    private static int total(Player p,MetroContainer c,Item item){
        int n=0;for(ItemStack s:p.inventory.mainInventory)if(s.getItem()==item)n+=s.getCount();
        for(ItemStack s:new ItemStack[]{p.inventory.getItemStack(),c.lamp.getStackInSlot(0),c.nativeBag.getStackInSlot(0)})if(s.getItem()==item)n+=s.getCount();return n;
    }
    @Test public void independentSlotsRejectChargersFlasksAndLegacyRowUnlockingBags(){
        assertTrue(ExtraEquipment.accepts(ExtraEquipment.Kind.LAMP,new ItemStack(LAMP)));
        assertFalse(ExtraEquipment.accepts(ExtraEquipment.Kind.LAMP,new ItemStack(POWER)));
        assertFalse(ExtraEquipment.accepts(ExtraEquipment.Kind.BLACKRAZID_BACKPACK,new ItemStack(new BackpackItem(3))));
        assertTrue(ExtraEquipment.accepts(ExtraEquipment.Kind.BLACKRAZID_BACKPACK,new ItemStack(bag())));
        assertFalse(ExtraEquipment.accepts(ExtraEquipment.Kind.LAMP,new ItemStack(bag())));
        assertTrue(WatchEquipment.accepts(new ItemStack(new BlackrazidWatchItem())));
        assertTrue(WatchEquipment.accepts(new ItemStack(new ru.metrocraft.port.mcmod.WatchItem())));
    }
    @Test public void clientPredictionAndServerExecutionNeverMultiplyEitherNewSlot()throws Exception{
        for(boolean lamp:new boolean[]{true,false}){
            Player server=player(lamp?4101:4102,false),client=player(lamp?4101:4102,true);assertEquals(server,client);
            MetroContainer s=new MetroContainer(server),c=new MetroContainer(client);server.openContainer=s;client.openContainer=c;
            assertEquals(46,s.inventorySlots.size());assertNotSame(s.lamp,c.lamp);assertNotSame(s.nativeBag,c.nativeBag);
            Item item=lamp?LAMP:bag();ItemStack stack=new ItemStack(item);stack.setStackDisplayName("Only original");stack.getTagCompound().setBoolean("NoDrop",true);
            server.inventory.setItemStack(stack);client.inventory.setItemStack(stack.copy());
            for(int i=0;i<40;i++){
                int count=s.inventorySlots.get(slot(s,lamp)).getStack().getCount();c.slotClick(slot(c,lamp),i%2,ClickType.PICKUP,client);
                assertEquals(count,s.inventorySlots.get(slot(s,lamp)).getStack().getCount());assertEquals(1,total(server,s,item));assertEquals(1,total(client,c,item));
                s.slotClick(slot(s,lamp),i%2,ClickType.PICKUP,server);assertEquals(1,total(server,s,item));
                c.putStackInSlot(slot(c,lamp),s.inventorySlots.get(slot(s,lamp)).getStack().copy());client.inventory.setItemStack(server.inventory.getItemStack().copy());
            }
            assertEquals("Only original",server.inventory.getItemStack().getDisplayName());
        }
    }
    @Test public void lateSnapshotsCannotReinsertAnItemPickedUpInTheOpenWindow()throws Exception{
        Player p=player(4103,true);MetroContainer c=new MetroContainer(p);p.openContainer=c;
        c.lamp.setInventorySlotContents(0,new ItemStack(LAMP));ItemStack old=c.lamp.getStackInSlot(0).copy();
        c.slotClick(44,0,ClickType.PICKUP,p);ExtraEquipment.receive(p,ExtraEquipment.Kind.LAMP,old);
        assertTrue(c.lamp.isEmpty());assertEquals(1,p.inventory.getItemStack().getCount());
    }
    @Test public void creativeRightClickWaitsForVanillaCountRestorationAndConsumesExactlyOnce()throws Exception{
        Player p=player(4104,false);ExtraEquipment.inventory(p,ExtraEquipment.Kind.LAMP);p.capabilities.isCreativeMode=true;
        ItemStack held=new ItemStack(LAMP);p.inventory.mainInventory.set(0,held);
        LAMP.onItemRightClick(p.world,p,EnumHand.MAIN_HAND);assertEquals(1,held.getCount());assertFalse(SurvivalSystem.hasLamp(p));
        held.setCount(1);ExtraEquipment.finishPendingEquip(p);assertTrue(held.isEmpty());assertTrue(SurvivalSystem.hasLamp(p));
        ExtraEquipment.finishPendingEquip(p);assertEquals(1,ExtraEquipment.get(p,ExtraEquipment.Kind.LAMP).getCount());
    }
    @Test public void delayedEquipCannotConsumeAnotherHandOrChangedHotbarSlot()throws Exception{
        Player p=player(4105,false);ExtraEquipment.inventory(p,ExtraEquipment.Kind.LAMP);ItemStack held=new ItemStack(LAMP);p.inventory.mainInventory.set(0,held);
        ExtraEquipment.equipAfterUse(p,ExtraEquipment.Kind.LAMP,EnumHand.MAIN_HAND,held);p.inventory.currentItem=1;ExtraEquipment.finishPendingEquip(p);
        assertEquals(1,held.getCount());assertFalse(SurvivalSystem.hasLamp(p));
    }
    @Test public void oldNativeBackpackMigratesWithContentsWithoutReplacingLegacyCapacitySlot()throws Exception{
        Player p=player(4106,false);ExtraEquipment.inventory(p,ExtraEquipment.Kind.BLACKRAZID_BACKPACK);
        ItemStack old=new ItemStack(bag());new BackpackStorage(old,null).setInventorySlotContents(4,new ItemStack(net.minecraft.init.Items.DIAMOND,3));
        Backpacks.inventory(p).setInventorySlotContents(0,old);ExtraEquipment.migrateLegacyBackpack(p);
        assertTrue(Backpacks.get(p).isEmpty());assertEquals(3,new BackpackStorage(ExtraEquipment.get(p,ExtraEquipment.Kind.BLACKRAZID_BACKPACK),null).getStackInSlot(4).getCount());
        ExtraEquipment.migrateLegacyBackpack(p);assertEquals(1,ExtraEquipment.get(p,ExtraEquipment.Kind.BLACKRAZID_BACKPACK).getCount());
        assertEquals(0,InventoryCapacity.rows(p));
        Backpacks.inventory(p).setInventorySlotContents(0,new ItemStack(new BackpackItem(3)));assertEquals(3,InventoryCapacity.rows(p));
        assertEquals(3,new BackpackStorage(ExtraEquipment.get(p,ExtraEquipment.Kind.BLACKRAZID_BACKPACK),null).getStackInSlot(4).getCount());
    }
    @Test public void removingSlotLampStopsChargingAndHandheldLampDoesNotBypassIt()throws Exception{
        Player p=player(4107,false);ExtraEquipment.inventory(p,ExtraEquipment.Kind.LAMP);p.inventory.mainInventory.set(0,new ItemStack(LAMP));
        assertFalse(SurvivalSystem.hasLamp(p));POWER.onUsingTick(new ItemStack(POWER),p,71985);assertEquals(0,SurvivalSystem.get(p).energy,0);
        ExtraEquipment.set(p,ExtraEquipment.Kind.LAMP,new ItemStack(LAMP));POWER.onUsingTick(new ItemStack(POWER),p,71985);assertEquals(20,SurvivalSystem.get(p).energy,0);
        ExtraEquipment.set(p,ExtraEquipment.Kind.LAMP,ItemStack.EMPTY);POWER.onUsingTick(new ItemStack(POWER),p,71970);assertEquals(20,SurvivalSystem.get(p).energy,0);
    }
}
