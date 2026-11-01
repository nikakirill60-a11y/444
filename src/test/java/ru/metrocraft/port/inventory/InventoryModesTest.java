package ru.metrocraft.port.inventory;
import io.netty.buffer.*;
import org.junit.Test;
import static org.junit.Assert.*;
public class InventoryModesTest {
    @Test public void vanillaHasAllThreeRowsButMetroKeepsTheOriginalBackpackCapacity(){
        for(int rows=0;rows<=3;rows++){
            assertEquals(3,InventoryModes.rows(true,rows));assertEquals(rows,InventoryModes.rows(false,rows));
        }
        assertEquals(0,InventoryModes.rows(false,-10));assertEquals(3,InventoryModes.rows(false,100));
    }
    @Test public void preferenceAndOpenFlagHaveAnUnambiguousRoundTrip(){
        for(boolean mode:new boolean[]{false,true}){
            ByteBuf in=Unpooled.buffer(),out=Unpooled.buffer();
            try{
                new InventoryModePacket(mode).toBytes(in);assertEquals(mode?1:0,in.getUnsignedByte(0));
                InventoryModePacket copy=new InventoryModePacket();copy.fromBytes(in);copy.toBytes(out);
                assertEquals(mode?1:0,out.readUnsignedByte());assertFalse(in.isReadable());
            }finally{in.release();out.release();}
            for(boolean open:new boolean[]{false,true}){
                ByteBuf a=Unpooled.buffer(),b=Unpooled.buffer();
                try{
                    new InventoryModeSyncPacket(mode,open).toBytes(a);InventoryModeSyncPacket copy=new InventoryModeSyncPacket();copy.fromBytes(a);copy.toBytes(b);
                    assertEquals(mode,b.readBoolean());assertEquals(open,b.readBoolean());assertFalse(a.isReadable());assertFalse(b.isReadable());
                }finally{a.release();b.release();}
            }
        }
    }
}
