package ru.metrocraft.port.blackrazid;

import net.minecraft.init.*;
import net.minecraft.item.*;
import org.junit.*;
import static org.junit.Assert.*;

public class BackpackStorageTest {
    @BeforeClass public static void bootstrap(){Bootstrap.register();BlackrazidContent.load();}
    static BlackrazidBackpackItem item(int capacity){
        for(BlackrazidContent.Entry e:BlackrazidContent.entries)if(e.storageSlots==capacity)return new BlackrazidBackpackItem(e);
        throw new AssertionError("No bag "+capacity);
    }
    @Test public void capacitiesAreExactlyFiveTenFifteen(){for(int n:new int[]{5,10,15})assertEquals(n,new BackpackStorage(new ItemStack(item(n)),null).getSizeInventory());}
    @Test public void realVanillaStacksAndTagsSurviveClosingAndReopening(){
        ItemStack bag=new ItemStack(item(15));bag.setStackDisplayName("My backpack");
        BackpackStorage first=new BackpackStorage(bag,null);ItemStack cargo=new ItemStack(Items.STICK,37);cargo.setStackDisplayName("My cargo");
        first.setInventorySlotContents(14,cargo);BackpackStorage reopened=new BackpackStorage(bag,null);
        assertEquals(37,reopened.getStackInSlot(14).getCount());assertEquals("My cargo",reopened.getStackInSlot(14).getDisplayName());assertEquals("My backpack",bag.getDisplayName());
    }
    @Test public void identicalBagsDoNotShareContents(){
        ItemStack a=new ItemStack(item(5)),b=new ItemStack(a.getItem());
        BackpackStorage one=new BackpackStorage(a,null),two=new BackpackStorage(b,null);
        one.setInventorySlotContents(0,new ItemStack(Items.DIAMOND,3));assertTrue(two.isEmpty());assertFalse(b.hasTagCompound());
    }
    @Test public void takingLastItemSavesEmptyInventoryWithoutResurrection(){
        ItemStack bag=new ItemStack(item(5));BackpackStorage storage=new BackpackStorage(bag,null);
        storage.setInventorySlotContents(0,new ItemStack(Items.STICK,5));assertEquals(5,storage.removeStackFromSlot(0).getCount());
        assertTrue(new BackpackStorage(bag,null).isEmpty());assertEquals(0,bag.getTagCompound().getTagList("ItemInventory",10).tagCount());
    }
    @Test public void decrementUsesLiveContentsAndPersistsOnce(){
        ItemStack bag=new ItemStack(item(5));BackpackStorage storage=new BackpackStorage(bag,null);
        storage.setInventorySlotContents(4,new ItemStack(Items.STICK,5));assertEquals(2,storage.decrStackSize(4,2).getCount());
        assertEquals(3,new BackpackStorage(bag,null).getStackInSlot(4).getCount());
    }
    @Test public void nestedBagsRejectedIncludingOldMetroBags(){
        BackpackStorage storage=new BackpackStorage(new ItemStack(item(5)),null);
        assertFalse(storage.isItemValidForSlot(0,new ItemStack(item(10))));
        assertFalse(storage.isItemValidForSlot(0,new ItemStack(new ru.metrocraft.port.inventory.BackpackItem(3))));
        assertTrue(storage.isItemValidForSlot(0,new ItemStack(Items.STICK)));
    }
    @Test public void loadingDoesNotWriteOrFireChangeCallbacks(){
        ItemStack bag=new ItemStack(item(5));final int[] changes={0};BackpackStorage storage=new BackpackStorage(bag,()->changes[0]++);
        assertFalse(bag.hasTagCompound());assertEquals(0,changes[0]);storage.setInventorySlotContents(0,new ItemStack(Items.STICK));assertEquals(1,changes[0]);
        net.minecraft.nbt.NBTTagCompound before=bag.getTagCompound().copy();new BackpackStorage(bag,()->changes[0]++);
        assertEquals(1,changes[0]);assertEquals(before,bag.getTagCompound());
    }
}
