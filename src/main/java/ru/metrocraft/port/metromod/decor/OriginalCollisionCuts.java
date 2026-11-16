package ru.metrocraft.port.metromod.decor;

import java.util.*;
import net.minecraft.util.math.*;

/** World-grid cells removed by a player, relative to the controller. These are
 * independent of the moving donor cells; replay must never recreate a cut. */
public final class OriginalCollisionCuts {
    private final Set<BlockPos> cells=new LinkedHashSet<>();
    public boolean add(BlockPos relative) {
        if(Math.abs((long)relative.getX())>64 || Math.abs((long)relative.getY())>64 || Math.abs((long)relative.getZ())>64 || cells.size()>=4096)return false;
        return cells.add(relative.toImmutable());
    }
    public boolean contains(BlockPos relative) {return cells.contains(relative);}
    public boolean remove(BlockPos relative) {return cells.remove(relative);}
    public void clear() {cells.clear();}
    public int[] save() {
        int[] data=new int[cells.size()*3];int i=0;
        for(BlockPos p:cells){data[i++]=p.getX();data[i++]=p.getY();data[i++]=p.getZ();}return data;
    }
    public void load(int[] data) {
        clear();if(data.length%3!=0 || data.length>12288)return;
        for(int i=0;i<data.length;i+=3)if(!add(new BlockPos(data[i],data[i+1],data[i+2]))) {clear();return;}
    }
    private static void box(List<AxisAlignedBB> out,double x,double y,double z,double X,double Y,double Z) {
        if(X-x>1e-9 && Y-y>1e-9 && Z-z>1e-9)out.add(new AxisAlignedBB(x,y,z,X,Y,Z));
    }
    public List<AxisAlignedBB> apply(List<AxisAlignedBB> source,BlockPos origin) {
        if(cells.isEmpty())return source;
        List<AxisAlignedBB> result=new ArrayList<>(source);
        for(BlockPos relative:cells) {
            AxisAlignedBB cut=new AxisAlignedBB(origin.add(relative));List<AxisAlignedBB> next=new ArrayList<>();
            for(AxisAlignedBB b:result) {
                if(!b.intersects(cut)){next.add(b);continue;}
                AxisAlignedBB i=b.intersect(cut);
                box(next,b.minX,b.minY,b.minZ,i.minX,b.maxY,b.maxZ);
                box(next,i.maxX,b.minY,b.minZ,b.maxX,b.maxY,b.maxZ);
                box(next,i.minX,b.minY,b.minZ,i.maxX,i.minY,b.maxZ);
                box(next,i.minX,i.maxY,b.minZ,i.maxX,b.maxY,b.maxZ);
                box(next,i.minX,i.minY,b.minZ,i.maxX,i.maxY,i.minZ);
                box(next,i.minX,i.minY,i.maxZ,i.maxX,i.maxY,b.maxZ);
            }
            result=next;
        }
        return result;
    }
}
