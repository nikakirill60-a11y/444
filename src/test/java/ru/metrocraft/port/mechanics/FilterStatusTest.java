package ru.metrocraft.port.mechanics;

import io.netty.buffer.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.*;
import ru.metrocraft.port.mcmod.WatchItem;
import ru.metrocraft.port.blackrazid.BlackrazidWatchItem;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FilterStatusTest {
    @BeforeClass public static void boot(){Bootstrap.register();}
    @Test public void dedicatedPacketIsTwelveBytesAndClampsUntrustedValues(){
        for(int value:new int[]{Integer.MIN_VALUE,-2,-1,0,1,1200,6000,6001,Integer.MAX_VALUE}){
            ByteBuf input=Unpooled.buffer(),output=Unpooled.buffer();
            try{
                input.writeInt(-1);input.writeInt(909);input.writeInt(value);
                FilterStatusPacket decoded=new FilterStatusPacket();decoded.fromBytes(input);decoded.toBytes(output);
                assertFalse(input.isReadable());assertEquals(12,output.readableBytes());assertEquals(-1,output.readInt());
                assertEquals(909,output.readInt());assertEquals(FilterRules.sanitize(value),output.readInt());assertFalse(output.isReadable());
            }finally{input.release();output.release();}
        }
    }
    @Test public void readingsAreSideEffectFreeAndOnlyActualMasksCount(){
        assertEquals(-1,FilterStatus.remaining(ItemStack.EMPTY));
        assertEquals(-1,FilterStatus.remaining(new ItemStack(Items.IRON_HELMET)));
        ItemStack mask=FilterItemTest.mask("status_mask","head",true);
        assertEquals(0,FilterStatus.remaining(mask));assertFalse(mask.hasTagCompound());
        mask.setTagCompound(new NBTTagCompound());mask.getTagCompound().setString("Keep","untouched");
        for(int value:new int[]{Integer.MIN_VALUE,0,1,6000,Integer.MAX_VALUE}){
            mask.getTagCompound().setInteger("MetroFilterTicks",value);assertEquals(Math.max(0,FilterRules.sanitize(value)),FilterStatus.remaining(mask));
            assertEquals(value,mask.getTagCompound().getInteger("MetroFilterTicks"));assertEquals("untouched",mask.getTagCompound().getString("Keep"));
        }
    }
    @Test public void longestWornFilterMatchesExistingHeadOrChestProtection()throws Exception{
        EntityPlayer p=FilterItemTest.player(false);
        ItemStack head=FilterItemTest.mask("status_head","head",true),chest=FilterItemTest.mask("himza","chest",false);
        head.setTagCompound(new NBTTagCompound());head.getTagCompound().setInteger("MetroFilterTicks",300);
        chest.setTagCompound(new NBTTagCompound());chest.getTagCompound().setInteger("MetroFilterTicks",2400);
        when(p.getItemStackFromSlot(EntityEquipmentSlot.HEAD)).thenReturn(head);
        when(p.getItemStackFromSlot(EntityEquipmentSlot.CHEST)).thenReturn(chest);assertEquals(2400,FilterStatus.remaining(p));
        when(p.getItemStackFromSlot(EntityEquipmentSlot.CHEST)).thenReturn(ItemStack.EMPTY);assertEquals(300,FilterStatus.remaining(p));
        when(p.getItemStackFromSlot(EntityEquipmentSlot.HEAD)).thenReturn(ItemStack.EMPTY);assertEquals(-1,FilterStatus.remaining(p));
    }
    @Test public void detectorPredicateKeepsBlackrazidTimeOnly(){
        assertTrue(ru.metrocraft.port.client.FilterWatchClient.detector(new ItemStack(new WatchItem())));
        assertFalse(ru.metrocraft.port.client.FilterWatchClient.detector(new ItemStack(new BlackrazidWatchItem())));
        assertFalse(ru.metrocraft.port.client.FilterWatchClient.detector(ItemStack.EMPTY));
    }
}
