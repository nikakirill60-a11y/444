package ru.metrocraft.port.mcmod.decor;

import net.minecraft.util.math.AxisAlignedBB;

/** Shared original GL rotation + model scale, used by CPU tests and both actual render paths. */
public final class DecorPose {
    private DecorPose() {}
    public static int angle(int metadata) {return 270-90*(metadata&3);}
    public static AxisAlignedBB bounds(McmodDecorContent.Entry e,int meta) {
        double minX=Double.POSITIVE_INFINITY,minZ=minX,maxX=Double.NEGATIVE_INFINITY,maxZ=maxX;
        double t=Math.toRadians(angle(meta)),c=Math.cos(t),s=Math.sin(t);
        for(double x:new double[]{e.bounds[0],e.bounds[3]})for(double z:new double[]{e.bounds[2],e.bounds[5]}) {
            double px=.5+e.scale*(c*x+s*z),pz=.5+e.scale*(-s*x+c*z);
            minX=Math.min(minX,px);maxX=Math.max(maxX,px);minZ=Math.min(minZ,pz);maxZ=Math.max(maxZ,pz);
        }
        return new AxisAlignedBB(minX,e.bounds[1]*e.scale,minZ,maxX,e.bounds[4]*e.scale,maxZ);
    }
    public static AxisAlignedBB collision(McmodDecorContent.Entry e,int meta) {
        AxisAlignedBB b=bounds(e,meta);
        double x0=Math.max(0,b.minX),y0=Math.max(0,b.minY),z0=Math.max(0,b.minZ);
        double x1=Math.min(1,b.maxX),y1=Math.min(1,b.maxY),z1=Math.min(1,b.maxZ);
        return x1>x0&&y1>y0&&z1>z0?new AxisAlignedBB(x0,y0,z0,x1,y1,z1):null;
    }
    public static float itemScale(McmodDecorContent.Entry e) {
        return (float)(.72/Math.max(e.bounds[3]-e.bounds[0],Math.max(e.bounds[4]-e.bounds[1],e.bounds[5]-e.bounds[2])));
    }
}
