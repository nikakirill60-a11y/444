package ru.metrocraft.port.metromod.decor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Shared geometry/animation contract: dedicated server never loads a renderer or an OBJ. */
public final class OriginalGateProfile {
    public String id, source, name_ru;
    public String kind="Germo";
    public boolean is(String value) { return value.equals(kind); }
    public float step;
    public double ground;
    private double legacyGround;
    public double[] offset={0,0,0};
    public Part[] parts;
    public DonorDoorCollision donorDoor;
    public DonorGermoCollision donorGermo;
    public static final class Part {
        public String mesh;
        public Map<String,String> materials;
        public double[] move;
        public double[][] boxes;
        public double[][] frames;
        public boolean moving;
        public double[] matrix(float progress) {
            if(frames==null)return new double[]{1,0,0,move[0]*progress,0,1,0,move[1]*progress,0,0,1,move[2]*progress,0,0,0,1};
            double frame=clamp(progress)*(frames.length-1);int a=(int)frame,b=Math.min(a+1,frames.length-1);
            double[] result=new double[16];for(int i=0;i<16;i++)result[i]=frames[a][i]+(frames[b][i]-frames[a][i])*(frame-a);
            return result;
        }
        public boolean moves() { return frames==null?move[0]!=0 || move[1]!=0 || move[2]!=0:moving; }
    }
    private static final Map<String,OriginalGateProfile> ALL = load();
    public static Collection<OriginalGateProfile> all() { return ALL.values(); }
    public static OriginalGateProfile get(String id) { return ALL.get(id); }
    private static Map<String,OriginalGateProfile> load() {
        try (InputStream in=OriginalGateProfile.class.getResourceAsStream("/assets/metroport/metromod_gates.json")) {
            if(in==null)throw new IOException("Missing original gate profiles");
            List<OriginalGateProfile> profiles=new ArrayList<>(Arrays.asList(new Gson().fromJson(new InputStreamReader(in,StandardCharsets.UTF_8),OriginalGateProfile[].class)));
            try(InputStream extra=OriginalGateProfile.class.getResourceAsStream("/assets/metroport/metromod_interactive.json")) {
                if(extra==null)throw new IOException("Missing interactive profiles");
                profiles.addAll(Arrays.asList(new Gson().fromJson(new InputStreamReader(extra,StandardCharsets.UTF_8),OriginalGateProfile[].class)));
            }
            JsonObject layout;
            try(InputStream fix=OriginalGateProfile.class.getResourceAsStream("/assets/metroport/metromod_layout.json")) {
                if(fix==null)throw new IOException("Missing collision layout");
                layout=new Gson().fromJson(new InputStreamReader(fix,StandardCharsets.UTF_8),JsonObject.class);
            }
            Map<String,Double> groundFix;
            try(InputStream fix=OriginalGateProfile.class.getResourceAsStream("/assets/metroport/metromod_ground.json")) {
                if(fix==null)throw new IOException("Missing grounded door layout");
                groundFix=new Gson().fromJson(new InputStreamReader(fix,StandardCharsets.UTF_8),new com.google.gson.reflect.TypeToken<Map<String,Double>>(){}.getType());
                if(groundFix.size()!=44)throw new IOException("Incomplete grounded door layout");
            }
            Map<String,OriginalGateProfile> result=new LinkedHashMap<>();
            for(OriginalGateProfile p:profiles) {
                if(!p.id.matches("metromod_(germo|door|lever|light)_[a-z0-9_]+") || result.put(p.id,p)!=null || p.parts.length==0
                        || !Arrays.asList("Germo","Door","Lever","Light").contains(p.kind) || !Double.isFinite(p.ground) || !(p.step>0 && p.step<=.02F))throw new IOException("Invalid gate profile");
                if(layout.has(p.id)) {
                    JsonObject fix=layout.getAsJsonObject(p.id);
                    if(fix.has("offset"))p.offset=new Gson().fromJson(fix.get("offset"),double[].class);
                    if(p.offset.length!=3)throw new IOException("Invalid layout offset");
                    for(double v:p.offset)if(!Double.isFinite(v) || Math.abs(v)>8)throw new IOException("Invalid layout offset");
                    if(fix.has("solid_parts"))for(Map.Entry<String,JsonElement> entry:fix.getAsJsonObject("solid_parts").entrySet())
                        p.parts[Integer.parseInt(entry.getKey())].boxes=new Gson().fromJson(entry.getValue(),double[][].class);
                }
                p.legacyGround=p.ground;
                if(p.is("Door") || p.is("Germo")) {
                    Double fixed=groundFix.get(p.id);
                    if(fixed==null || !Double.isFinite(fixed) || Math.abs(fixed)>32)throw new IOException("Invalid grounded door layout");
                    p.ground=fixed;
                }
                p.donorDoor=DonorDoorCollision.get(p.id);
                p.donorGermo=DonorGermoCollision.get(p.id);
                if(p.is("Germo") != (p.donorGermo!=null))throw new IOException("Missing/unexpected donor Germo rule");
                if(p.is("Door") != (p.donorDoor!=null))throw new IOException("Missing/unexpected donor Door collision rule");
                for(Part part:p.parts) {
                    if(part.move.length!=3 || part.boxes.length==0)throw new IOException("Invalid gate part");
                    if(part.frames!=null) {
                        if(part.frames.length!=65)throw new IOException("Invalid frame count");
                        for(double[] frame:part.frames) {
                            if(frame.length!=16)throw new IOException("Invalid pose matrix");
                            for(double v:frame)if(!Double.isFinite(v) || Math.abs(v)>128)throw new IOException("Invalid pose");
                            if(frame[12]!=0 || frame[13]!=0 || frame[14]!=0 || frame[15]!=1)throw new IOException("Non-affine pose");
                        }
                    }
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
            if(movingOnly && !part.moves())continue;
            double[] m=part.matrix((float)t);
            for(double[] b:part.boxes) {
                double[] box={Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,
                        Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY};
                for(int mask=0;mask<8;mask++)for(int axis=0;axis<3;axis++) {
                    double value=m[axis*4+3]+offset[axis]+(axis==1?ground:0);
                    for(int k=0;k<3;k++)value+=m[axis*4+k]*b[k+((mask&(1<<k))!=0?3:0)];
                    box[axis]=Math.min(box[axis],value);box[axis+3]=Math.max(box[axis+3],value);
                }
                // The authored wall-facing body plane, not the quarter-voxel
                // padding, touches the support. Collision must not invade it.
                if(is("Lever")) {box[2]=Math.max(box[2],-.498);box[5]=Math.max(box[5],box[2]+1e-6);}
                out.add(worldBox(pos,facing,box));
            }
        }
        return out;
    }
    private final Map<EnumFacing,AxisAlignedBB> localBounds=new EnumMap<>(EnumFacing.class);
    public synchronized AxisAlignedBB bounds(BlockPos pos,EnumFacing facing) {
        AxisAlignedBB cached=localBounds.get(facing);
        if(cached!=null)return cached.offset(pos);
        AxisAlignedBB result=null;
        // Every pose extremum, not just endpoints: rotating leaves may swing outside both.
        int count=parts[0].frames==null?1:64;
        for(int i=0;i<=count;i++)for(AxisAlignedBB box:boxes(BlockPos.ORIGIN,facing,(float)i/count,false))result=result==null?box:result.union(box);
        if(donorDoor!=null)result=result.union(donorDoor.bounds(this,BlockPos.ORIGIN,facing,null));
        if(donorGermo!=null)for(float value:new float[]{0,1})for(AxisAlignedBB b:initialPhysics(BlockPos.ORIGIN,facing,value))result=result.union(b);
        localBounds.put(facing,result);return result.offset(pos);
    }
    public List<AxisAlignedBB> initialPhysics(BlockPos pos,EnumFacing facing,float progress) {
        if(donorGermo!=null)return floorBoxes(donorGermo.boxes(this,donorGermo.seed(id,facing,clamp(progress)),pos,facing),pos);
        return donorDoor==null?boxes(pos,facing,progress,false):floorBoxes(Collections.singletonList(donorDoor.bounds(this,pos,facing,null)),pos);
    }
    /** Grounding must never reserve the supporting floor due to donor padding. */
    public static List<AxisAlignedBB> floorBoxes(List<AxisAlignedBB> boxes,BlockPos origin) {
        List<AxisAlignedBB> result=new ArrayList<>();
        for(AxisAlignedBB b:boxes)if(b.maxY>origin.getY()+1e-9)result.add(new AxisAlignedBB(b.minX,Math.max(origin.getY(),b.minY),b.minZ,b.maxX,b.maxY,b.maxZ));
        return result;
    }
    /** Only cells touched NOW are blocks. Empty aperture/swing pocket stays air. */
    public List<BlockPos> occupied(BlockPos pos,EnumFacing facing,float progress) {
        LinkedHashSet<BlockPos> cells=new LinkedHashSet<>();cells.add(pos.toImmutable());
        for(AxisAlignedBB box:initialPhysics(pos,facing,progress)) {
            for(int x=MathHelper.floor(box.minX+1e-7);x<Math.ceil(box.maxX-1e-7);x++)
                for(int y=MathHelper.floor(box.minY+1e-7);y<Math.ceil(box.maxY-1e-7);y++)
                    for(int z=MathHelper.floor(box.minZ+1e-7);z<Math.ceil(box.maxZ-1e-7);z++)cells.add(new BlockPos(x,y,z));
        }
        if(cells.size()>4096)throw new IllegalStateException("Decor footprint too large");
        return new ArrayList<>(cells);
    }
    /** Includes the complete movement pocket and controller, never edits outside this set. */
    public List<BlockPos> footprint(BlockPos pos,EnumFacing facing) {
        AxisAlignedBB box=bounds(pos,facing);
        if(is("Door") || is("Germo"))box=box.union(box.offset(0,legacyGround-ground,0));
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
