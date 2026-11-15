package ru.metrocraft.port.editor;

import io.netty.buffer.*;
import net.minecraft.util.math.BlockPos;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class EditorPreviewPacketTest {
    private final BlockPos origin=new BlockPos(-10,64,20);
    private EditorPreviewPacket one(){return new EditorPreviewPacket(-1,42,2,new EditSession.Box(origin,2,1,1),Arrays.asList(origin,origin.add(1,0,0)));}
    private byte[] bytes(EditorPreviewPacket p){ByteBuf b=Unpooled.buffer();try{p.toBytes(b);byte[] result=new byte[b.readableBytes()];b.readBytes(result);return result;}finally{b.release();}}
    private EditorPreviewPacket decode(byte[] bytes){ByteBuf b=Unpooled.wrappedBuffer(bytes);try{EditorPreviewPacket p=new EditorPreviewPacket();p.fromBytes(b);return p;}finally{b.release();}}
    @Test public void roundTripRetainsGeometryAndKind(){EditorPreviewPacket p=decode(bytes(one()));assertTrue(p.valid());assertEquals(2,p.kind());assertEquals(2,p.total());assertEquals(origin,p.box().origin);assertEquals(2,p.cells().size());}
    @Test public void clearPacketHasNoGeometry(){EditorPreviewPacket p=decode(bytes(EditorPreviewPacket.clear(0,1)));assertTrue(p.valid());assertEquals(0,p.kind());assertNull(p.box());assertTrue(p.cells().isEmpty());}
    @Test public void selectionOutlineContainsNoCells(){EditorPreviewPacket p=decode(bytes(new EditorPreviewPacket(0,1,1,new EditSession.Box(origin,1,1,1),Collections.emptyList())));assertTrue(p.valid());assertEquals(1,p.kind());assertEquals(0,p.total());}
    @Test public void maximumPayloadIsBoundedAndTruncationIsExplicit(){List<BlockPos> cells=new ArrayList<>();for(int y=0;y<16;y++)for(int z=0;z<16;z++)for(int x=0;x<16;x++)cells.add(origin.add(x,y,z));byte[] b=bytes(new EditorPreviewPacket(0,1,2,new EditSession.Box(origin,16,16,16),cells));assertTrue(b.length<1024);EditorPreviewPacket p=decode(b);assertTrue(p.valid());assertEquals(4096,p.total());assertEquals(256,p.cells().size());}
    @Test public void everyTruncatedPayloadIsRejectedWithoutThrowing(){byte[] b=bytes(one());for(int i=0;i<b.length;i++)assertFalse("length="+i,decode(Arrays.copyOf(b,i)).valid());}
    @Test public void oversizedCellCountIsRejected(){byte[] b=bytes(one());b[26]=1;b[27]=1;assertFalse(decode(b).valid());}
    @Test public void invalidDimensionsAndKindAreRejected(){byte[] b=bytes(one());b[21]=0;assertFalse(decode(b).valid());b=bytes(one());b[8]=99;assertFalse(decode(b).valid());}
    @Test public void outOfBoundsCellIsRejected(){byte[] b=bytes(one());b[28]=32;assertFalse(decode(b).valid());}
    @Test public void duplicateCellsAreRejected(){byte[] b=bytes(one());b[31]=b[28];b[32]=b[29];b[33]=b[30];assertFalse(decode(b).valid());}
    @Test public void trailingBytesAreRejected(){byte[] b=bytes(one());assertFalse(decode(Arrays.copyOf(b,b.length+1)).valid());byte[] c=bytes(EditorPreviewPacket.clear(0,1));assertFalse(decode(Arrays.copyOf(c,c.length+1)).valid());}
    @Test public void cellListAndPositionsAreImmutableSnapshots(){BlockPos.MutableBlockPos mutable=new BlockPos.MutableBlockPos(origin.getX(),origin.getY(),origin.getZ());List<BlockPos> cells=new ArrayList<>();cells.add(mutable);EditorPreviewPacket p=new EditorPreviewPacket(0,1,2,new EditSession.Box(origin,1,1,1),cells);mutable.setPos(0,0,0);cells.clear();assertEquals(origin,p.cells().get(0));try{p.cells().clear();fail("immutable");}catch(UnsupportedOperationException expected){}}
    @Test(expected=IllegalArgumentException.class) public void constructingOutsideWorldOutlineIsRefused(){new EditorPreviewPacket(0,1,1,new EditSession.Box(new BlockPos(0,256,0),1,1,1),Collections.emptyList());}
}
