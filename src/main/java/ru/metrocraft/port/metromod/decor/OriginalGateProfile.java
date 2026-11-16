package ru.metrocraft.port.metromod.decor;

import com.google.gson.Gson;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Shared geometry/animation contract: dedicated server never loads a renderer or an OBJ. */
public final class OriginalGateProfile {
    public String id, source, name_ru;
    public float step;
    public double ground;
    public Part[] parts;
    public static final class Part {
        public String mesh;
        public Map<String,String> materials;
        public double[] move;
        public double[][] boxes;
    }
    private static final Map<String,OriginalGateProfile> ALL = load();
    public static Collection<OriginalGateProfile> all() { return ALL.values(); }
    public static OriginalGateProfile get(String id) { return ALL.get(id); }
    private static Map<String,OriginalGateProfile> load() {
        try (InputStream in=OriginalGateProfile.class.getResourceAsStream("/assets/metroport/metromod_gates.json")) {
            if(in==null)throw new IOException("Missing original gate profiles");
            OriginalGateProfile[] profiles=new Gson().fromJson(new InputStreamReader(in,StandardCharsets.UTF_8),OriginalGateProfile[].class);
            Map<String,OriginalGateProfile> result=new LinkedHashMap<>();
            for(OriginalGateProfile p:profiles) {
                if(!p.id.matches("metromod_germo_[124569]") || result.put(p.id,p)!=null || p.parts.length==0
                        || !Double.isFinite(p.ground) || !(p.step>0 && p.step<=.02F))throw new IOException("Invalid gate profile");
                for(Part part:p.parts) {
                    if(part.move.length!=3 || part.boxes.length==0)throw new IOException("Invalid gate part");
                    for(double v:part.move)if(!Double.isFinite(v))throw new IOException("Invalid movement");
                    for(double[] b:part.boxes) {
                        if(b.length!=6)throw new IOException("Invalid collision box");
                        for(int i=0;i<3;i++)if(!Double.isFinite(b[i]) || !Double.isFinite(b[i+3]) || b[i]>=b[i+3]
                                || Math.abs(b[i])>32 || Math.abs(b[i+3])>32)throw new IOException("Invalid collision bounds");
                    }
                }
            }
            return Collections.unmodifiableMap(result);
        } catch(IOException e) { throw new IllegalStateException("Cannot load original METRO_MOD gates",e); }
    }
    public static float clamp(float value) { return Float.isFinite(value)?MathHelper.clamp(value,0,1):0; }
    public static AxisAlignedBB worldBox(BlockPos pos,EnumFacing facing,double[] b) {
        EnumFacing right=facing.rotateYCCW();
        double x0=pos.getX()+.5+right.getXOffset()*b[0]+facing.getXOffset()*b[2];
        double z0=pos.getZ()+.5+right.getZOffset()*b[0]+facing.getZOffset()*b[2];
        double x1=pos.getX()+.5+right.getXOffset()*b[3]+facing.getXOffset()*b[5];
        double z1=pos.getZ()+.5+right.getZOffset()*b[3]+facing.getZOffset()*b[5];
        return new AxisAlignedBB(x0,pos.getY()+b[1],z0,x1,pos.getY()+b[4],z1);
    }
    public List<AxisAlignedBB> boxes(BlockPos pos,EnumFacing facing,float progress,boolean movingOnly) {
        List<AxisAlignedBB> out=new ArrayList<>();
        double t=clamp(progress);
        for(Part part:parts) {
            if(movingOnly && part.move[0]==0 && part.move[1]==0 && part.move[2]==0)continue;
            for(double[] b:part.boxes)out.add(worldBox(pos,facing,new double[]{b[0]+part.move[0]*t,b[1]+ground+part.move[1]*t,
                    b[2]+part.move[2]*t,b[3]+part.move[0]*t,b[4]+ground+part.move[1]*t,b[5]+part.move[2]*t}));
        }
        return out;
    }
    public AxisAlignedBB bounds(BlockPos pos,EnumFacing facing) {
        AxisAlignedBB result=null;
        for(float progress:new float[]{0,1})for(AxisAlignedBB box:boxes(pos,facing,progress,false))result=result==null?box:result.union(box);
        return result;
    }
    /** Includes the complete movement pocket and controller, never edits outside this set. */
    public List<BlockPos> footprint(BlockPos pos,EnumFacing facing) {
        AxisAlignedBB box=bounds(pos,facing);
        List<BlockPos> result=new ArrayList<>(); result.add(pos.toImmutable());
        for(int x=MathHelper.floor(box.minX);x<Math.ceil(box.maxX);x++)
            for(int y=MathHelper.floor(box.minY);y<Math.ceil(box.maxY);y++)
                for(int z=MathHelper.floor(box.minZ);z<Math.ceil(box.maxZ);z++) {
                    BlockPos cell=new BlockPos(x,y,z);if(!cell.equals(pos))result.add(cell);
                }
        if(result.size()>4096)throw new IllegalStateException("Gate footprint too large");
        return Collections.unmodifiableList(result);
    }
}
