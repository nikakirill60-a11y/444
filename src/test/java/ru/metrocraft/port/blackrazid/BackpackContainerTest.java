package ru.metrocraft.port.blackrazid;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.world.World;
import org.junit.*;
import ru.metrocraft.port.inventory.*;
import java.lang.reflect.Field;
import java.util.UUID;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BackpackContainerTest {
    @BeforeClass public static void bootstrap(){Bootstrap.register();BlackrazidContent.load();}
    private static final class Player extends EntityPlayer {
        private Player(World w){super(w,new GameProfile(UUID.randomUUID(),"test"));}
        @Override public boolean isSpectator(){return false;} @Override public boolean isCreative(){return false;} @Override public boolean isEntityAlive(){return true;}
    }
    private static Player player(int id,boolean remote)throws Exception{
        Field f=sun.misc.Unsafe.class.getDeclaredField("theUnsafe");f.setAccessible(true);
        Player p=(Player)((sun.misc.Unsafe)f.get(null)).allocateInstance(Player.class);p.world=mock(World.class);
        Field side=World.class.getField("isRemote");side.setAccessible(true);side.setBoolean(p.world,remote);p.setEntityId(id);p.inventory=new InventoryPlayer(p);
        Field caps=EntityPlayer.class.getField("capabilities");caps.setAccessible(true);caps.set(p,new PlayerCapabilities());return p;
    }
    private static int heldSlot(BackpackContainer c){return c.storage.getSizeInventory()+27;}
    @Test public void bagCannotBePickedThrownClonedOrSwappedWhileOpen()throws Exception{
        Player p=player(3001,false);ItemStack bag=new ItemStack(BackpackStorageTest.item(5));p.inventory.setInventorySlotContents(0,bag);
        BackpackContainer c=new BackpackContainer(p,0);p.openContainer=c;
        for(ClickType click:new ClickType[]{ClickType.PICKUP,ClickType.THROW,ClickType.CLONE,ClickType.QUICK_MOVE}){
            assertTrue(c.slotClick(heldSlot(c),0,click,p).isEmpty());assertSame(bag,p.getHeldItemMainhand());assertTrue(p.inventory.getItemStack().isEmpty());
        }
        c.slotClick(0,0,ClickType.SWAP,p);assertSame(bag,p.getHeldItemMainhand());assertTrue(c.storage.isEmpty());
    }
    @Test public void replacedOrChangedHeldBagInvalidatesOldWindow()throws Exception{
        Player p=player(3002,false);p.inventory.setInventorySlotContents(0,new ItemStack(BackpackStorageTest.item(5)));BackpackContainer c=new BackpackContainer(p,0);
        assertTrue(c.canInteractWith(p));p.inventory.setInventorySlotContents(0,new ItemStack(BackpackStorageTest.item(5)));assertFalse(c.canInteractWith(p));
        assertTrue(c.slotClick(0,0,ClickType.PICKUP,p).isEmpty());
    }
    @Test public void equippedStoragePersistsOnTheActualLiveEquipmentStack()throws Exception{
        Player p=player(3003,false);InventoryBasic slot=Backpacks.inventory(p);slot.setInventorySlotContents(0,new ItemStack(BackpackStorageTest.item(10)));
        BackpackContainer c=new BackpackContainer(p,2);p.openContainer=c;c.storage.setInventorySlotContents(9,new ItemStack(Items.DIAMOND,4));
        assertSame(slot.getStackInSlot(0),c.bag);assertTrue(c.canInteractWith(p));
        assertEquals(4,new BackpackStorage(Backpacks.get(p),null).getStackInSlot(9).getCount());
        assertEquals(4,new BackpackContainer(p,2).storage.getStackInSlot(9).getCount());assertEquals(0,Backpacks.rows(c.bag));
    }
    @Test public void offhandBagCannotBeSwappedIntoItsOwnContents()throws Exception{
        Player p=player(3004,false);ItemStack bag=new ItemStack(BackpackStorageTest.item(5));p.inventory.setInventorySlotContents(40,bag);
        BackpackContainer c=new BackpackContainer(p,1);c.slotClick(0,40,ClickType.SWAP,p);assertSame(bag,p.getHeldItemOffhand());assertTrue(c.storage.isEmpty());
    }
    @Test public void clientAndServerWithEqualEntityIdsNeverShareEquippedBag()throws Exception{
        Player server=player(3005,false),client=player(3005,true);assertEquals(server,client);
        InventoryBasic a=Backpacks.inventory(server),b=Backpacks.inventory(client);assertNotSame(a,b);
        a.setInventorySlotContents(0,new ItemStack(BackpackStorageTest.item(5)));assertTrue(b.isEmpty());
        assertSame(a,new MetroContainer(server).bag);assertSame(b,new MetroContainer(client).bag);
    }
    @Test public void lateEquipmentPacketCannotOverwriteOpenStorageBag()throws Exception{
        Player p=player(3006,true);InventoryBasic slot=Backpacks.inventory(p);slot.setInventorySlotContents(0,new ItemStack(BackpackStorageTest.item(5)));
        BackpackContainer c=new BackpackContainer(p,2);p.openContainer=c;Backpacks.receive(p,ItemStack.EMPTY);assertSame(c.bag,slot.getStackInSlot(0));
    }
}
