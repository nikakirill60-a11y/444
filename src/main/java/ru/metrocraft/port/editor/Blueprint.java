package ru.metrocraft.port.editor;

import java.util.*;
import net.minecraft.util.math.BlockPos;

/** Bounded, versioned ordinary-block clipboard. No entities, inventories, TE NBT or executable donor data. */
public final class Blueprint {
    public static final int LIMIT=4096, SIDE=32;
    public final int width,height,depth;
    private final Spec[] cells;
    public static final class Spec {
        public final String block;
        public final int meta;
        public Spec(String block,int meta){
            if(block==null || !block.matches("[a-z0-9_.-]+:[a-z0-9_/.-]+") || block.length()>160 || meta<0 || meta>15)
                throw new IllegalArgumentException("Invalid block identifier/metadata");
            this.block=block;this.meta=meta;
        }
        public boolean air(){return "minecraft:air".equals(block);}
        @Override public boolean equals(Object other){return other instanceof Spec && meta==((Spec)other).meta && block.equals(((Spec)other).block);}
        @Override public int hashCode(){return Objects.hash(block,meta);}
    }
    public Blueprint(int width,int height,int depth,Spec[] data){
        int volume=volume(width,height,depth);
        if(data==null || data.length!=volume)throw new IllegalArgumentException("Invalid cell count");
        cells=data.clone();for(Spec s:cells)if(s==null)throw new IllegalArgumentException("Null cell");
        this.width=width;this.height=height;this.depth=depth;
    }
    public static int volume(int w,int h,int d){
        if(w<1 || h<1 || d<1 || w>SIDE || h>SIDE || d>SIDE || (long)w*h*d>LIMIT)
            throw new IllegalArgumentException("Maximum 4096 blocks, 32 per side");
        return w*h*d;
    }
    public int size(){return cells.length;}
    public Spec get(int x,int y,int z){return cells[(y*depth+z)*width+x];}
    public static BlockPos rotated(int x,int y,int z,int w,int d,int turns){
        switch(Math.floorMod(turns,4)){
            case 1:return new BlockPos(d-1-z,y,x);
            case 2:return new BlockPos(w-1-x,y,d-1-z);
            case 3:return new BlockPos(z,y,w-1-x);
            default:return new BlockPos(x,y,z);
        }
    }
    public Blueprint rotate(int turns,java.util.function.BiFunction<Spec,Integer,Spec> rotator){
        int t=Math.floorMod(turns,4),w=t%2==0?width:depth,d=t%2==0?depth:width;
        Spec[] result=new Spec[size()];
        for(int y=0;y<height;y++)for(int z=0;z<depth;z++)for(int x=0;x<width;x++){
            BlockPos p=rotated(x,y,z,width,depth,t);result[(p.getY()*d+p.getZ())*w+p.getX()]=rotator.apply(get(x,y,z),t);
        }
        return new Blueprint(w,height,d,result);
    }
    /** Flip across the clipboard's X or Z axis; also transform native block orientation. */
    public Blueprint mirror(boolean xAxis,java.util.function.BiFunction<Spec,Boolean,Spec> mirrorer){
        Spec[] result=new Spec[size()];
        for(int y=0;y<height;y++)for(int z=0;z<depth;z++)for(int x=0;x<width;x++){
            int nx=xAxis?width-1-x:x,nz=xAxis?z:depth-1-z;
            result[(y*depth+nz)*width+nx]=mirrorer.apply(get(x,y,z),xAxis);
        }
        return new Blueprint(width,height,depth,result);
    }

}
