package ru.metrocraft.port.inventory;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.*;
import net.minecraft.entity.Entity;
import net.minecraft.init.Bootstrap;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.junit.*;
import ru.metrocraft.port.Definition;
import ru.metrocraft.port.mcmod.WatchItem;
import ru.metrocraft.port.util.WeakIdentityMap;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Real Entity.equals/hashCode, real MetroContainer, independent client prediction and server execution.
 * Mockito player mocks use identity equality and could not reproduce this integrated-server bug.
 */
public class EquipmentSideIsolationTest {
    private static WatchItem watch;
    private static NvgItem nvg;
    @BeforeClass public static void bootstrap(){
        Bootstrap.register();watch=new WatchItem();Definition d=new Definition();d.id="isolation_test_pnv";d.slot="head";d.nightVision=true;d.durability=100;
        nvg=new NvgItem(d);
    }
    private static final class Player extends EntityPlayer {
        private Player(World world){super(world,new GameProfile(UUID.randomUUID(),"test"));}
        @Override public boolean isSpectator(){return false;}
        @Override public boolean isCreative(){return false;}
        @Override public boolean isEntityAlive(){return true;}
    }
    private static Player player(int id,boolean remote)throws Exception{
        // Skip Entity's world/game bootstrap, NOT its equals/hashCode or inventory methods.
        Field f=sun.misc.Unsafe.class.getDeclaredField("theUnsafe");f.setAccessible(true);
        Player player=(Player)((sun.misc.Unsafe)f.get(null)).allocateInstance(Player.class);
        player.world=mock(World.class);Field side=World.class.getField("isRemote");side.setAccessible(true);side.setBoolean(player.world,remote);
        player.setEntityId(id);player.inventory=new InventoryPlayer(player);
        Field caps=EntityPlayer.class.getField("capabilities");caps.setAccessible(true);caps.set(player,new PlayerCapabilities());
        return player;
    }
    private static int slot(MetroContainer c,boolean clock){return c.inventorySlots.size()-(clock?1:2);}
    private static InventoryBasic equipment(MetroContainer c,boolean clock){return clock?c.watch:c.nvg;}
    private static int count(Player p,MetroContainer c,Item item){
        int count=0;for(ItemStack stack:p.inventory.mainInventory)if(stack.getItem()==item)count+=stack.getCount();
        for(ItemStack stack:new ItemStack[]{p.inventory.getItemStack(),c.watch.getStackInSlot(0),c.nvg.getStackInSlot(0)})if(stack.getItem()==item)count+=stack.getCount();
        return count;
    }
    @Test public void originalWeakHashMapReallyAliasesClientAndServerEntities()throws Exception{
        Player server=player(812,false),client=player(812,true);assertNotSame(server,client);assertEquals(server,client);assertEquals(server.hashCode(),client.hashCode());
        Map<EntityPlayer,String> broken=new WeakHashMap<>();broken.put(server,"server inventory");assertEquals("server inventory",broken.get(client));
        WeakIdentityMap<EntityPlayer,String> fixed=new WeakIdentityMap<>();fixed.computeIfAbsent(server,k->"server inventory");assertNull(fixed.get(client));
    }
    @Test public void legacySharedSlotPredictionThenExecutionReproducesTwoServerItems()throws Exception{
        Player server=player(850,false),client=player(850,true);
        EquipmentInventory shared=new EquipmentInventory("legacy shared",ItemStack.EMPTY,s->s.getItem()==watch,s->{});
        Map<EntityPlayer,EquipmentInventory> broken=new WeakHashMap<>();broken.put(server,shared);
        assertSame(shared,broken.get(client));
        Container s=single(shared),c=single(broken.get(client));
        server.inventory.setItemStack(new ItemStack(watch));client.inventory.setItemStack(new ItemStack(watch));
        c.slotClick(0,0,ClickType.PICKUP,client);s.slotClick(0,0,ClickType.PICKUP,server);
        assertEquals("old alias bug: equipped copy AND unchanged server cursor",2,shared.getStackInSlot(0).getCount()+server.inventory.getItemStack().getCount());
    }
    private static Container single(InventoryBasic inventory){return new Container(){
        {addSlotToContainer(new Slot(inventory,0,0,0));}
        @Override public boolean canInteractWith(EntityPlayer player){return true;}
    };}
    @Test public void realWatchClientPredictionAndServerClickNeverShareOrMultiplyStacks()throws Exception{pairedClicks(true);}
    @Test public void realNvgClientPredictionAndServerClickNeverShareOrMultiplyStacks()throws Exception{pairedClicks(false);}
    private static void pairedClicks(boolean clock)throws Exception{
        Player server=player(clock?901:902,false),client=player(clock?901:902,true);
        MetroContainer s=new MetroContainer(server),c=new MetroContainer(client);server.openContainer=s;client.openContainer=c;
        assertNotSame(equipment(s,clock),equipment(c,clock));Item item=clock?watch:nvg;
        ItemStack initial=new ItemStack(item);initial.setStackDisplayName("Only original device");initial.getTagCompound().setBoolean("NoDrop",true);
        server.inventory.setItemStack(initial);client.inventory.setItemStack(initial.copy());
        for(int i=0;i<100;i++){
            int before=equipment(s,clock).getStackInSlot(0).getCount();
            c.slotClick(slot(c,clock),i%2,ClickType.PICKUP,client);
            assertEquals("prediction must not change the SERVER slot",before,equipment(s,clock).getStackInSlot(0).getCount());
            assertEquals(1,count(server,s,item));assertEquals(1,count(client,c,item));
            s.slotClick(slot(s,clock),i%2,ClickType.PICKUP,server);assertEquals(1,count(server,s,item));
            // The vanilla window ack replaces only the client's slot and cursor.
            c.putStackInSlot(slot(c,clock),equipment(s,clock).getStackInSlot(0).copy());client.inventory.setItemStack(server.inventory.getItemStack().copy());
            assertEquals(1,count(client,c,item));
        }
        ItemStack result=server.inventory.getItemStack();assertEquals("Only original device",result.getDisplayName());assertTrue(result.getTagCompound().getBoolean("NoDrop"));
    }
    @Test public void sameIdRespawnObjectDoesNotReuseOldInventoryOrOldSaveCallback()throws Exception{
        for(boolean clock:new boolean[]{true,false}){
            Player old=player(clock?1001:1002,false),replacement=player(clock?1001:1002,false);
            MetroContainer before=new MetroContainer(old),after=new MetroContainer(replacement);
            assertNotSame(equipment(before,clock),equipment(after,clock));
            equipment(before,clock).setInventorySlotContents(0,new ItemStack(clock?watch:nvg));
            assertTrue(equipment(after,clock).getStackInSlot(0).isEmpty());
            assertEquals(1,old.getEntityData().getCompoundTag(clock?"MetroEquippedWatch":"MetroEquippedNvg").getByte("Count"));
            assertFalse(replacement.getEntityData().hasKey(clock?"MetroEquippedWatch":"MetroEquippedNvg"));
        }
    }
    @Test public void reopeningBothContainersKeepsSeparateLiveDeviceStores()throws Exception{
        Player s=player(1200,false),c=player(1200,true);
        InventoryBasic server=WatchEquipment.inventory(s),client=WatchEquipment.inventory(c);
        server.setInventorySlotContents(0,new ItemStack(watch));
        for(int i=0;i<20;i++){assertSame(server,new MetroContainer(s).watch);assertSame(client,new MetroContainer(c).watch);assertTrue(client.isEmpty());}
    }
    @Test public void creativeRightClickConsumesAfterVanillaRestoresHeldCount()throws Exception{
        Player p=player(1250,false);NvgEquipment.inventory(p);p.capabilities.isCreativeMode=true;ItemStack held=new ItemStack(nvg);p.inventory.mainInventory.set(0,held);
        nvg.onItemRightClick(p.world,p,net.minecraft.util.EnumHand.MAIN_HAND);assertEquals(1,held.getCount());assertTrue(NvgEquipment.get(p).isEmpty());
        held.setCount(1); // Vanilla PlayerInteractionManager's creative restoration, after item use.
        NvgEquipment.finishPendingEquip(p);assertTrue(held.isEmpty());assertEquals(1,NvgEquipment.get(p).getCount());
        NvgEquipment.finishPendingEquip(p);assertEquals(1,NvgEquipment.get(p).getCount());
    }
    @Test public void delayedRightClickCannotEquipAChangedHandOrCopyAnAlreadyEquippedDevice()throws Exception{
        Player p=player(1260,false);NvgEquipment.inventory(p);ItemStack held=new ItemStack(nvg);p.inventory.mainInventory.set(0,held);
        NvgEquipment.equipAfterUse(p,net.minecraft.util.EnumHand.MAIN_HAND,held);p.inventory.currentItem=1;NvgEquipment.finishPendingEquip(p);
        assertEquals(1,held.getCount());assertTrue(NvgEquipment.get(p).isEmpty());
        p.inventory.currentItem=0;NvgEquipment.set(p,new ItemStack(nvg));NvgEquipment.equipAfterUse(p,net.minecraft.util.EnumHand.MAIN_HAND,held);NvgEquipment.finishPendingEquip(p);assertEquals(1,held.getCount());
    }
    @Test public void concurrentFactoryIsAtomicAndEqualIdsRemainIndependent()throws Exception{
        Player s=player(1300,false),c=player(1300,true);WeakIdentityMap<Player,Object> map=new WeakIdentityMap<>();AtomicInteger calls=new AtomicInteger();
        ExecutorService pool=Executors.newFixedThreadPool(4);
        try{
            List<Future<Object>> futures=new ArrayList<>();for(int i=0;i<24;i++)futures.add(pool.submit(()->map.computeIfAbsent(s,k->{calls.incrementAndGet();return new Object();})));
            Object value=futures.get(0).get();for(Future<Object> f:futures)assertSame(value,f.get());assertEquals(1,calls.get());assertNull(map.get(c));
        }finally{pool.shutdownNow();}
    }
}
