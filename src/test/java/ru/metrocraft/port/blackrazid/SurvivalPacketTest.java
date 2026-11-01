package ru.metrocraft.port.blackrazid;
import io.netty.buffer.*;
import org.junit.Test;
import static org.junit.Assert.*;
import ru.metrocraft.port.blackrazid.survival.*;
public class SurvivalPacketTest {
    @Test public void ownerSnapshotRoundtripAndFixedWireSize(){
        SurvivalPacket a=new SurvivalPacket();a.dimension=-2;a.entity=543;a.water=23.5F;a.energy=64;a.lamp=true;
        ByteBuf b=Unpooled.buffer();try{a.toBytes(b);assertEquals(17,b.readableBytes());SurvivalPacket c=new SurvivalPacket();c.fromBytes(b);assertEquals(-2,c.dimension);assertEquals(543,c.entity);assertEquals(23.5F,c.water,0);assertEquals(64,c.energy,0);assertTrue(c.lamp);assertEquals(0,b.readableBytes());}finally{b.release();}
    }
    @Test public void clientSnapshotBoundsAreSanitized(){
        ByteBuf b=Unpooled.buffer();try{b.writeInt(0);b.writeInt(1);b.writeFloat(Float.NaN);b.writeFloat(300);b.writeBoolean(false);SurvivalPacket s=new SurvivalPacket();s.fromBytes(b);assertEquals(100,s.water,0);assertEquals(100,s.energy,0);}finally{b.release();}
    }
    @Test public void togglePacketCarriesNoMutablePlayerProperties(){ByteBuf b=Unpooled.buffer();try{new LampPacket().toBytes(b);assertEquals(0,b.readableBytes());new LampPacket().fromBytes(b);}finally{b.release();}}
}
