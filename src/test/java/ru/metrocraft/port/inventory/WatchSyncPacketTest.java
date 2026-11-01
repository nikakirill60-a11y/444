package ru.metrocraft.port.inventory;
import io.netty.buffer.*;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import org.junit.*;
import static org.junit.Assert.*;
public class WatchSyncPacketTest {
    @BeforeClass public static void setup(){Bootstrap.register();}
    @Test public void wireFormatPreservesDimensionEntityAndNBTAndClampsUntrustedReadings(){
        for(int level:new int[]{0,3,255}){
            ByteBuf input=Unpooled.buffer(),output=Unpooled.buffer();
            try{
                ItemStack stack=new ItemStack(Items.CLOCK);stack.setStackDisplayName("Watch packet NBT");stack.getTagCompound().setBoolean("NoDrop",true);
                input.writeInt(-1);input.writeInt(987);input.writeByte(level);ByteBufUtils.writeItemStack(input,stack);
                WatchSyncPacket copy=new WatchSyncPacket();copy.fromBytes(input);copy.toBytes(output);
                assertFalse(input.isReadable());assertEquals(-1,output.readInt());assertEquals(987,output.readInt());assertEquals(Math.min(3,level),output.readUnsignedByte());
                ItemStack restored=ByteBufUtils.readItemStack(output);assertEquals("Watch packet NBT",restored.getDisplayName());
                assertTrue(restored.getTagCompound().getBoolean("NoDrop"));assertFalse(output.isReadable());
            }finally{input.release();output.release();}
        }
    }
}
