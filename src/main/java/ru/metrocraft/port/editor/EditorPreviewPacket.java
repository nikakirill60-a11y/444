package ru.metrocraft.port.editor;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import java.util.*;

/** Owner-only, S2C only. Coordinates for bounded outlines, never block mutation requests or block/NBT payloads. */
public final class EditorPreviewPacket implements IMessage {
    private int dimension,entity,kind,total;
    private EditSession.Box box;
    private List<BlockPos> cells=Collections.emptyList();
    private boolean valid;
    public EditorPreviewPacket(){}
    public EditorPreviewPacket(int dimension,int entity,int kind,EditSession.Box box,List<BlockPos> changed){
        this.dimension=dimension;this.entity=entity;this.kind=kind;this.box=box;total=changed.size();
        List<BlockPos> snapshot=new ArrayList<>();
        for(BlockPos pos:changed.subList(0,Math.min(changed.size(),PreviewRules.MAX_CELLS)))snapshot.add(pos.toImmutable());
        cells=Collections.unmodifiableList(snapshot);valid=validate();
        if(!valid)throw new IllegalArgumentException("Invalid outline packet");
    }
    public static EditorPreviewPacket clear(int dimension,int entity){return new EditorPreviewPacket(dimension,entity,0,null,Collections.emptyList());}
    public boolean valid(){return valid;}
    public int kind(){return kind;}
    public int total(){return total;}
    public EditSession.Box box(){return box;}
    public List<BlockPos> cells(){return cells;}
    private boolean validate(){
        if(kind<0 || kind>2 || cells.size()>PreviewRules.MAX_CELLS || total<cells.size() || total>Blueprint.LIMIT)return false;
        if(kind==0)return box==null && total==0 && cells.isEmpty();
        if(box==null || !PreviewRules.bounds(box.origin.getX(),box.origin.getY(),box.origin.getZ(),box.width,box.height,box.depth))return false;
        if(total>Blueprint.volume(box.width,box.height,box.depth) || (kind==1 && (total!=0 || !cells.isEmpty())))return false;
        Set<BlockPos> seen=new HashSet<>();
        for(BlockPos pos:cells){
            int x=pos.getX()-box.origin.getX(),y=pos.getY()-box.origin.getY(),z=pos.getZ()-box.origin.getZ();
            if(x<0 || y<0 || z<0 || x>=box.width || y>=box.height || z>=box.depth || !seen.add(pos))return false;
        }
        return cells.size()==Math.min(total,PreviewRules.MAX_CELLS);
    }
    @Override public void toBytes(ByteBuf buf){
        if(!valid)throw new IllegalStateException("Invalid outline packet");
        buf.writeInt(dimension);buf.writeInt(entity);buf.writeByte(kind);if(kind==0)return;
        buf.writeInt(box.origin.getX());buf.writeInt(box.origin.getY());buf.writeInt(box.origin.getZ());
        buf.writeByte(box.width);buf.writeByte(box.height);buf.writeByte(box.depth);buf.writeShort(total);buf.writeShort(cells.size());
        for(BlockPos pos:cells){buf.writeByte(pos.getX()-box.origin.getX());buf.writeByte(pos.getY()-box.origin.getY());buf.writeByte(pos.getZ()-box.origin.getZ());}
    }
    @Override public void fromBytes(ByteBuf buf){
        valid=false;box=null;cells=Collections.emptyList();
        try{
            dimension=buf.readInt();entity=buf.readInt();kind=buf.readUnsignedByte();
            if(kind==0){total=0;valid=!buf.isReadable();return;}
            if(kind!=1 && kind!=2)return;
            int x=buf.readInt(),y=buf.readInt(),z=buf.readInt(),w=buf.readUnsignedByte(),h=buf.readUnsignedByte(),d=buf.readUnsignedByte();
            if(!PreviewRules.bounds(x,y,z,w,h,d))return;
            box=new EditSession.Box(new BlockPos(x,y,z),w,h,d);total=buf.readUnsignedShort();int count=buf.readUnsignedShort();
            if(count>PreviewRules.MAX_CELLS || buf.readableBytes()!=count*3)return;
            List<BlockPos> decoded=new ArrayList<>(count);
            for(int i=0;i<count;i++)decoded.add(box.origin.add(buf.readUnsignedByte(),buf.readUnsignedByte(),buf.readUnsignedByte()));
            cells=Collections.unmodifiableList(decoded);valid=validate();
        }catch(IndexOutOfBoundsException|IllegalArgumentException bad){valid=false;}
    }
    public static final class Handler implements IMessageHandler<EditorPreviewPacket,IMessage> {
        @Override public IMessage onMessage(EditorPreviewPacket message,MessageContext context){
            if(message.valid)ru.metrocraft.port.client.EditorPreviewRenderer.receive(message.dimension,message.entity,message.kind,message.box,message.cells);
            return null;
        }
    }
}
