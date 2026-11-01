package ru.metrocraft.port.inventory;
import net.minecraft.entity.player.*;
import net.minecraft.init.Bootstrap;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.junit.*;
import ru.metrocraft.port.mcmod.WatchItem;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Executes Minecraft's real slotClick code, not a hand-written imitation of inventory clicks. */
public class EquipmentTransactionTest {
    @BeforeClass public static void bootstrap(){Bootstrap.register();}
    private EntityPlayer player;
    private Container container;
    private EquipmentInventory equipment;
    private final AtomicReference<ItemStack> saved=new AtomicReference<>(ItemStack.EMPTY);
    private final List<ItemStack> drops=new ArrayList<>();
    private WatchItem watch;
    @Before public void setup()throws Exception {
        watch=new WatchItem();player=mock(EntityPlayer.class);player.world=mock(World.class);
        java.lang.reflect.Field capabilities=EntityPlayer.class.getField("capabilities");capabilities.setAccessible(true);capabilities.set(player,new PlayerCapabilities());
        player.inventory=new InventoryPlayer(player);
        when(player.dropItem(any(ItemStack.class),anyBoolean())).thenAnswer(inv->{drops.add(((ItemStack)inv.getArgument(0)).copy());return null;});
        equipment=new EquipmentInventory("test",ItemStack.EMPTY,s->s.getItem()==watch,s->saved.set(s.copy()));
        container=new Container(){
            {addSlotToContainer(new Slot(equipment,0,0,0){@Override public boolean isItemValid(ItemStack s){return equipment.isItemValidForSlot(0,s);}@Override public int getSlotStackLimit(){return 1;}});
                for(int i=0;i<36;i++)addSlotToContainer(new Slot(player.inventory,i,0,0));}
            @Override public boolean canInteractWith(EntityPlayer p){return true;}
            @Override public ItemStack transferStackInSlot(EntityPlayer p,int i){return ItemStack.EMPTY;}
            @Override public boolean canMergeSlot(ItemStack stack,Slot slot){return slot.inventory!=equipment;}
        };
        player.openContainer=container;
    }
    private int count(){int n=equipment.getStackInSlot(0).getCount()+player.inventory.getItemStack().getCount();for(ItemStack s:player.inventory.mainInventory)n+=s.getCount();for(ItemStack s:drops)n+=s.getCount();return n;}
    private void click(int slot,int button,ClickType type){container.slotClick(slot,button,type,player);assertEquals(1,count());assertEquals(equipment.getStackInSlot(0).getCount(),saved.get().getCount());}
    private void one(){ItemStack item=new ItemStack(watch);item.setStackDisplayName("Preserved clock");player.inventory.setItemStack(item);}
    @Test public void repeatedLeftAndRightPickupNeverDuplicatesTheClock(){one();for(int i=0;i<100;i++){click(0,i%2,ClickType.PICKUP);click(0,i%2,ClickType.PICKUP);}}
    @Test public void hotbarNumberSwapsConserveTheSameSingleClock(){one();click(0,0,ClickType.PICKUP);for(int i=0;i<100;i++){click(0,i%9,ClickType.SWAP);click(0,i%9,ClickType.SWAP);}}
    @Test public void shiftClickAndDoubleClickDoNotMakeAnotherClock(){one();click(0,0,ClickType.PICKUP);for(int i=0;i<50;i++){click(0,0,ClickType.QUICK_MOVE);click(0,0,ClickType.PICKUP);click(1,0,ClickType.PICKUP_ALL);click(0,0,ClickType.PICKUP);}}
    @Test public void throwRemovesTheClockFromTheSavedSlot(){one();click(0,0,ClickType.PICKUP);click(0,0,ClickType.THROW);assertTrue(equipment.getStackInSlot(0).isEmpty());assertTrue(saved.get().isEmpty());assertEquals(1,drops.size());}
    @Test public void markDirtyDoesNotReplaceTheStackBeingMutatedByVanilla(){ItemStack stack=new ItemStack(watch);equipment.setInventorySlotContents(0,stack);equipment.markDirty();assertSame(stack,equipment.getStackInSlot(0));stack.shrink(1);equipment.markDirty();assertTrue(saved.get().isEmpty());}
    @Test public void removeStackFromSlotAlsoPersistsTheRemoval(){equipment.setInventorySlotContents(0,new ItemStack(watch));ItemStack removed=equipment.removeStackFromSlot(0);assertEquals(1,removed.getCount());assertTrue(saved.get().isEmpty());}
}
