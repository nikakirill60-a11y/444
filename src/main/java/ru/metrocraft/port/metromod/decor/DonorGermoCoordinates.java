package ru.metrocraft.port.metromod.decor;

/** Pure affine coordinate adapter, separately compilable without Minecraft.
 * The input is an integer cube AFTER donor rotation/rounding, never a mesh. */
public final class DonorGermoCoordinates {
    private DonorGermoCoordinates() {}
    public static double[] localBox(DonorGermoMotion.Cell cell,double[] offset,double[] scale,
                                   double[] placementOffset,double ground,int rightX,int rightZ,int frontX,int frontZ) {
        double[] b={Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY};
        for(int mask=0;mask<8;mask++) {
            double x=cell.x+((mask&1)!=0?1:0),y=cell.y+((mask&2)!=0?1:0),z=cell.z+((mask&4)!=0?1:0);
            double[] v={x*rightX+z*rightZ,y,x*frontX+z*frontZ};
            for(int i=0;i<3;i++) {
                double raw=(v[i]-offset[i])/scale[i]+placementOffset[i]+(i==1?ground:0);
                b[i]=Math.min(b[i],raw);b[i+3]=Math.max(b[i+3],raw);
            }
        }
        return b;
    }
}
