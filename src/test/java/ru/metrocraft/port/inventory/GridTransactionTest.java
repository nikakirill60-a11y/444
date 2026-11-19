package ru.metrocraft.port.inventory;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.Field;
import java.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.world.World;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Real vanilla slotClick, real ItemStacks. Layout never owns or duplicates an item. */
public class GridTransactionTest {
    @BeforeClass public static void bootstrap(){Bootstrap.register();}
    private static final class Player extends EntityPlayer {
        Player(World w){super(w,new GameProfile(UUID.randomUUID(),"grid"));}
        @Override public boolean isEntityAlive(){return true;}
        @Override public boolean isSpectator(){return false;}
        @Override public boolean isCreative(){return false;}
    }
    private Player p;private MetroContainer c;
    @Before public void setup()throws Exception{
        Field f=sun.misc.Unsafe.class.getDeclaredField("theUnsafe");f.setAccessible(true);
        p=(Player)((sun.misc.Unsafe)f.get(null)).allocateInstance(Player.class);p.world=mock(World.class);p.inventory=new InventoryPlayer(p);
        Field caps=EntityPlayer.class.getField("capabilities");caps.setAccessible(true);caps.set(p,new PlayerCapabilities());
        c=new MetroContainer(p);p.openContainer=c;
    }
    private ItemStack item(String id){return new ItemStack(new Item().setRegistryName(id));}
    private int count(){int n=p.inventory.getItemStack().getCount();for(ItemStack s:p.inventory.mainInventory)n+=s.getCount();return n;}
    @Test public void threeByTwoCanBePlacedRotatedAndPickedFromAnyCoveredCell(){
        ItemStack gun=item("modularwarfare:metro_revolver");gun.setStackDisplayName("Original");gun.getTagCompound().setBoolean("NoDrop",true);p.inventory.setItemStack(gun);
        assertTrue(c.gridAction(6,0,true,0));assertTrue(p.inventory.getItemStack().isEmpty());assertEquals(1,count());
        PackingGrid grid=GridInventory.snapshot(p);assertTrue(PackingGrid.rotated(grid.position(0)));assertEquals(2,grid.width(0));assertEquals(3,grid.height(0));
        assertTrue(c.gridAction(7+2*8,0,false,0));assertTrue(p.inventory.getStackInSlot(9).isEmpty());
        assertEquals("Original",p.inventory.getItemStack().getDisplayName());assertTrue(p.inventory.getItemStack().getTagCompound().getBoolean("NoDrop"));assertEquals(1,count());
    }
    @Test public void collisionAndOffGridPlacementCannotConsumeOrSwapAnyItem(){
        p.inventory.setItemStack(item("metroport:blackrazid_sparta"));assertTrue(c.gridAction(9,0,false,0));
        ItemStack second=item("metroport:heavy");p.inventory.setItemStack(second);
        assertFalse(c.gridAction(0,0,false,0));assertSame(second,p.inventory.getItemStack());assertEquals(2,count());
        assertFalse(c.gridAction(7,0,false,0));assertSame(second,p.inventory.getItemStack());
    }
    @Test public void rightClickSplitsAndMergesAStackWithoutChangingItsFootprint(){
        p.inventory.setItemStack(new ItemStack(Items.STICK,12));assertTrue(c.gridAction(0,0,false,0));
        assertTrue(c.gridAction(0,1,false,0));assertEquals(6,p.inventory.getItemStack().getCount());assertEquals(12,count());
        assertTrue(c.gridAction(0,1,false,0));assertEquals(5,p.inventory.getItemStack().getCount());assertEquals(7,p.inventory.getStackInSlot(9).getCount());assertEquals(12,count());
    }
    @Test public void hiddenBackingSlotsCannotBeUsedWithVanillaPackets(){
        p.inventory.mainInventory.set(9,new ItemStack(Items.STICK,4));c.slotClick(6,0,ClickType.PICKUP,p);
        assertTrue(p.inventory.getItemStack().isEmpty());assertEquals(4,p.inventory.getStackInSlot(9).getCount());
        p.inventory.setItemStack(new ItemStack(Items.APPLE));c.slotClick(7,0,ClickType.PICKUP,p);assertTrue(p.inventory.getStackInSlot(10).isEmpty());assertEquals(5,count());
    }
    @Test public void oldWorldOverflowIsTakeOnlyAndNeverDeleted(){
        Item armor=new Item().setRegistryName("metroport:blackrazid_sparta");for(int i=9;i<14;i++)p.inventory.mainInventory.set(i,new ItemStack(armor));
        PackingGrid grid=GridInventory.snapshot(p);assertEquals(-1,grid.position(4));assertEquals(5,count());
        assertFalse(GridInventory.roomFor(p,14,new ItemStack(Items.STICK)));
        assertTrue(c.gridAction(0,0,false,1));assertEquals(1,p.inventory.getItemStack().getCount());assertTrue(p.inventory.getStackInSlot(13).isEmpty());assertEquals(5,count());
        assertFalse(c.gridAction(0,0,false,1));assertEquals(5,count());
    }
    @Test public void savedCoordinatesSurviveClosingAndOpeningTheContainer(){
        p.inventory.setItemStack(new ItemStack(Items.STICK,3));assertTrue(c.gridAction(63,0,false,0));
        int[] before=GridInventory.snapshot(p).positions();c=new MetroContainer(p);p.openContainer=c;
        assertArrayEquals(before,GridInventory.snapshot(p).positions());assertEquals(63,GridInventory.snapshot(p).position(0));assertEquals(3,count());
    }
    @Test public void malformedActionsArePureNoOps(){
        p.inventory.setItemStack(new ItemStack(Items.STICK,5));
        for(int cell:new int[]{-1,64,Integer.MAX_VALUE})assertFalse(c.gridAction(cell,0,false,0));
        assertFalse(c.gridAction(0,2,false,0));assertFalse(c.gridAction(0,0,false,9));assertEquals(5,count());
    }
    @Test public void repeatedPickupPlaceAndStackMergesConserveEveryItem(){
        p.inventory.setItemStack(new ItemStack(Items.STICK,48));
        for(int i=0;i<100;i++){
            assertTrue(c.gridAction(i%64,0,false,0));assertEquals(48,count());
            assertTrue(c.gridAction(i%64,0,false,0));assertEquals(48,count());
        }
    }
}
