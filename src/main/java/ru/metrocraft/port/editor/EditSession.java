package ru.metrocraft.port.editor;

import java.util.*;
import net.minecraft.util.math.BlockPos;

/** Transaction logic separated from Minecraft authorization and storage. One undo, bounded per-player state. */
public final class EditSession {
    public interface Access {
        Blueprint.Spec read(BlockPos pos); // Must reject unloaded, out-of-range, unsafe or protected source cells.
        void authorize(BlockPos pos,Blueprint.Spec before,Blueprint.Spec after);
        void write(BlockPos pos,Blueprint.Spec state);
    }
    private BlockPos first,second;
    private Blueprint clipboard;
    private LinkedHashMap<BlockPos,Blueprint.Spec> undoBefore,undoAfter;
    public void select(boolean one,BlockPos pos){if(one)first=pos.toImmutable();else second=pos.toImmutable();}
    public Blueprint clipboard(){if(clipboard==null)throw new IllegalStateException("Clipboard is empty");return clipboard;}
    public void clipboard(Blueprint value){clipboard=Objects.requireNonNull(value);}
    public void copy(Access access){
        if(first==null || second==null)throw new IllegalStateException("Select pos1 and pos2 first");
        // Long arithmetic before narrowing prevents coordinate overflow.
        long w=Math.abs((long)first.getX()-second.getX())+1,h=Math.abs((long)first.getY()-second.getY())+1,d=Math.abs((long)first.getZ()-second.getZ())+1;
        if(w>Blueprint.SIDE || h>Blueprint.SIDE || d>Blueprint.SIDE)throw new IllegalArgumentException("Selection too large");
        Blueprint.Spec[] data=new Blueprint.Spec[Blueprint.volume((int)w,(int)h,(int)d)];
        BlockPos min=new BlockPos(Math.min(first.getX(),second.getX()),Math.min(first.getY(),second.getY()),Math.min(first.getZ(),second.getZ()));
        int i=0;for(int y=0;y<h;y++)for(int z=0;z<d;z++)for(int x=0;x<w;x++)data[i++]=access.read(min.add(x,y,z));
        clipboard=new Blueprint((int)w,(int)h,(int)d,data);
    }
    public int paste(Access access,BlockPos origin){
        Blueprint b=clipboard();LinkedHashMap<BlockPos,Blueprint.Spec> next=new LinkedHashMap<>();
        for(int y=0;y<b.height;y++)for(int z=0;z<b.depth;z++)for(int x=0;x<b.width;x++){
            Blueprint.Spec state=b.get(x,y,z);if(!state.air())next.put(origin.add(x,y,z),state);
        }
        LinkedHashMap<BlockPos,Blueprint.Spec> before=apply(access,next,null);
        if(!before.isEmpty()){undoBefore=before;undoAfter=next;}
        return before.size();
    }
    public int undo(Access access){
        if(undoBefore==null)throw new IllegalStateException("Nothing to undo");
        int count=apply(access,undoBefore,undoAfter).size();undoBefore=null;undoAfter=null;return count;
    }
    private LinkedHashMap<BlockPos,Blueprint.Spec> apply(Access access,LinkedHashMap<BlockPos,Blueprint.Spec> next,Map<BlockPos,Blueprint.Spec> expected){
        LinkedHashMap<BlockPos,Blueprint.Spec> before=new LinkedHashMap<>();
        for(Map.Entry<BlockPos,Blueprint.Spec> e:next.entrySet()){
            Blueprint.Spec old=access.read(e.getKey());
            if(expected!=null && !old.equals(expected.get(e.getKey())))throw new IllegalStateException("World changed; undo refused");
            access.authorize(e.getKey(),old,e.getValue());
            if(!old.equals(e.getValue()))before.put(e.getKey(),old);
        }
        // Event handlers may have changed blocks during preflight: don't overwrite those changes.
        for(Map.Entry<BlockPos,Blueprint.Spec> e:before.entrySet())
            if(!access.read(e.getKey()).equals(e.getValue()))throw new IllegalStateException("World changed during validation");
        List<BlockPos> attempted=new ArrayList<>();
        try{
            for(BlockPos pos:before.keySet()){attempted.add(pos);access.write(pos,next.get(pos));}
        }catch(RuntimeException failure){
            boolean rolledBack=true;
            Collections.reverse(attempted);
            for(BlockPos pos:attempted)try{access.write(pos,before.get(pos));}catch(RuntimeException bad){rolledBack=false;failure.addSuppressed(bad);}
            if(!rolledBack)throw new IllegalStateException("Rollback incomplete; inspect the selected area",failure);
            throw failure;
        }
        return before;
    }
}
