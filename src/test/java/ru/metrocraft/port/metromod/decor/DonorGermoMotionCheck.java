package ru.metrocraft.port.metromod.decor;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Pure Java transaction/snapshot checks, independent of Forge resolution. */
public final class DonorGermoMotionCheck {
    private static int assertions;
    private static void check(boolean ok,String message) {assertions++;if(!ok)throw new AssertionError(message);}
    private static byte[] flags(DonorGermoMotion m) {
        byte[] out=new byte[m.state.segments.length];for(int i=0;i<out.length;i++)out[i]=(byte)(m.state.segments[i]?1:0);return out;
    }
    private static float[] vectors(DonorGermoMotion m) {
        return new float[]{m.state.animation.x,m.state.animation.y,m.state.animation.z,m.state.previous.x,m.state.previous.y,m.state.previous.z};
    }
    private static DonorGermoMotion restore(String id,float yaw,DonorGermoMotion m) {
        return DonorGermoMotion.restore(id,yaw,vectors(m),flags(m),m.packedCells());
    }
    private static void same(DonorGermoMotion a,DonorGermoMotion b) {
        check(Arrays.equals(a.packedCells(),b.packedCells()),"Cells differ");
        check(Arrays.equals(vectors(a),vectors(b)),"Vectors differ");
        check(Arrays.equals(flags(a),flags(b)),"Segments differ");
    }
    private static void coordinates() {
        double[] offset={3.99,-.75,.2},scale={1.08,.87,1.12},placement={.1,0,-.2};double ground=.75;
        // Both signs of both cardinal axes: inverse projection followed by
        // default donor T*S recovers EXACT integer cube extrema.
        for(int[] axes:new int[][]{{1,0,0,1},{0,1,-1,0},{-1,0,0,-1},{0,-1,1,0}})
            for(int x=-12;x<=12;x++)for(int z=-12;z<=12;z++) {
                DonorGermoMotion.Cell c=new DonorGermoMotion.Cell(x,2,z);
                double[] b=DonorGermoCoordinates.localBox(c,offset,scale,placement,ground,axes[0],axes[1],axes[2],axes[3]);
                double[] recovered={Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY};
                for(int mask=0;mask<4;mask++) {
                    double lx=(b[(mask&1)!=0?3:0]-placement[0])*scale[0]+offset[0];
                    double lz=(b[(mask&2)!=0?5:2]-placement[2])*scale[2]+offset[2];
                    double sx=lx*axes[0]+lz*axes[2],sz=lx*axes[1]+lz*axes[3];
                    recovered[0]=Math.min(recovered[0],sx);recovered[1]=Math.min(recovered[1],sz);
                    recovered[2]=Math.max(recovered[2],sx);recovered[3]=Math.max(recovered[3],sz);
                }
                check(Math.abs(recovered[0]-x)<1e-10 && Math.abs(recovered[2]-x-1)<1e-10,"X transform drift");
                check(Math.abs(recovered[1]-z)<1e-10 && Math.abs(recovered[3]-z-1)<1e-10,"Z transform drift");
                check(Math.abs((b[1]-ground-placement[1])*scale[1]+offset[1]-2)<1e-10,"Y transform drift");
            }
    }
    public static void main(String[] args) throws Exception {
        coordinates();
        Set<String> ids=new LinkedHashSet<>();for(String row:Files.readAllLines(Paths.get(args[0]),StandardCharsets.UTF_8))if(!row.startsWith("#"))ids.add(row.split("\t")[0]);
        for(String id:ids)for(float yaw:new float[]{0,-90,-180,-270}) {
            DonorGermoMotion closed=DonorGermoMotion.seed(id,yaw,0);
            System.out.println(id+" yaw="+yaw+" closed cells="+closed.cells().size()+" progress="+closed.progress());
            check(!closed.cells().isEmpty(),"Empty closed seed: "+id);
            check(closed.progress()==0,"Nonzero closed progress: "+id);
            DonorGermoMotion m=closed.copy();
            for(int tick=0;tick<1200;tick++) {
                boolean open=tick<400 || tick>=600 && tick<850;
                DonorGermoMotion copy=m.copy();DonorGermoMotion candidate=m.next(open);
                same(m,copy); // rejected candidate leaves the original unchanged
                if(tick%37==0) {
                    DonorGermoMotion loaded=restore(id,yaw,m);check(loaded!=null,"Snapshot rejected");
                    same(m,loaded);same(candidate,loaded.next(open));
                }
                m=candidate;
            }
            check(m.progress()==0,"Did not close after reversals: "+id);
            float[] invalid=vectors(m);invalid[0]=Float.NaN;
            check(DonorGermoMotion.restore(id,yaw,invalid,flags(m),m.packedCells())==null,"Accepted NaN");
            check(DonorGermoMotion.restore(id,yaw,vectors(m),new byte[0],m.packedCells())==null,"Accepted wrong segment count");
            check(DonorGermoMotion.restore(id,yaw,vectors(m),flags(m),new int[]{0,0})==null,"Accepted partial cell");
            check(DonorGermoMotion.restore(id,yaw,vectors(m),flags(m),new int[]{49,0,0})==null,"Accepted outside cell");
        }
        System.out.println("PASS: 17 profiles / 4 yaws; motion, reversals, rejected-tick isolation, snapshots; "+assertions+" assertions; NOT gameplay validation");
    }
}
