package ru.metrocraft.port.metromod.decor;

import com.google.gson.Gson;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;

/** Affine adapter from original block-grid cells to current unit-scale models.
 * Does NOT inspect meshes or turn render animation into collision geometry. */
public final class DonorGermoCollision {
    public double[] offset,scale;
    private static final Map<String,DonorGermoCollision> ALL=load();
    private final Map<EnumFacing,DonorGermoMotion> closed=new EnumMap<>(EnumFacing.class);
    private static Map<String,DonorGermoCollision> load() {
        try(InputStream in=DonorGermoCollision.class.getResourceAsStream("/assets/metroport/metromod_germo_collision.json")) {
            if(in==null)throw new IOException("Missing donor Germo grid metadata");
            java.lang.reflect.Type type=new com.google.gson.reflect.TypeToken<LinkedHashMap<String,DonorGermoCollision>>(){}.getType();
            Map<String,DonorGermoCollision> out=new Gson().fromJson(new InputStreamReader(in,StandardCharsets.UTF_8),type);
            if(out.size()!=17)throw new IOException("Incomplete donor Germo grid metadata");
            for(DonorGermoCollision p:out.values()) {
                if(p.offset.length!=3 || p.scale.length!=3)throw new IOException("Invalid Germo transform");
                for(int i=0;i<3;i++)if(!Double.isFinite(p.offset[i]) || Math.abs(p.offset[i])>32 || !Double.isFinite(p.scale[i]) || p.scale[i]<.1 || p.scale[i]>10)throw new IOException("Invalid Germo transform");
            }
            return Collections.unmodifiableMap(out);
        } catch(IOException e) {throw new IllegalStateException("Cannot load Germo grid metadata",e);}
    }
    public static DonorGermoCollision get(String id) {return ALL.get(id);}
    public synchronized DonorGermoMotion seed(String id,EnumFacing facing,float progress) {
        if(progress>0)return DonorGermoMotion.seed(id,-facing.getHorizontalAngle(),progress);
        DonorGermoMotion cached=closed.get(facing);
        if(cached==null){cached=DonorGermoMotion.seed(id,-facing.getHorizontalAngle(),0);closed.put(facing,cached);}
        return cached.copy();
    }
    /** Inverse cardinal rotation of cube corners BEFORE removing the catalogue
     * offset/scale. Donor callbacks already rotated and rounded grid origins. */
    public List<AxisAlignedBB> boxes(OriginalGateProfile profile,DonorGermoMotion motion,BlockPos origin,EnumFacing facing) {
        EnumFacing right=facing.rotateYCCW();List<AxisAlignedBB> out=new ArrayList<>();
        for(DonorGermoMotion.Cell cell:motion.cells()) {
            double[] b=DonorGermoCoordinates.localBox(cell,offset,scale,profile.offset,profile.ground,
                    right.getXOffset(),right.getZOffset(),facing.getXOffset(),facing.getZOffset());
            out.add(OriginalGateProfile.worldBox(origin,facing,b));
        }
        return out;
    }
    public static List<BlockPos> occupied(BlockPos origin,List<AxisAlignedBB> boxes) {
        LinkedHashSet<BlockPos> cells=new LinkedHashSet<>();cells.add(origin.toImmutable());
        for(AxisAlignedBB b:boxes)for(int x=MathHelper.floor(b.minX+1e-7);x<Math.ceil(b.maxX-1e-7);x++)
            for(int y=MathHelper.floor(b.minY+1e-7);y<Math.ceil(b.maxY-1e-7);y++)
                for(int z=MathHelper.floor(b.minZ+1e-7);z<Math.ceil(b.maxZ-1e-7);z++) {
                    cells.add(new BlockPos(x,y,z));if(cells.size()>4096)throw new IllegalStateException("Germo footprint budget");
                }
        return new ArrayList<>(cells);
    }
    public static NBTTagCompound save(DonorGermoMotion motion) {
        NBTTagCompound tag=new NBTTagCompound();tag.setInteger("Version",1);
        tag.setFloat("X",motion.state.animation.x);tag.setFloat("Y",motion.state.animation.y);tag.setFloat("Z",motion.state.animation.z);
        tag.setFloat("PrevX",motion.state.previous.x);tag.setFloat("PrevY",motion.state.previous.y);tag.setFloat("PrevZ",motion.state.previous.z);
        byte[] flags=new byte[motion.state.segments.length];for(int i=0;i<flags.length;i++)flags[i]=(byte)(motion.state.segments[i]?1:0);
        tag.setByteArray("Segments",flags);tag.setIntArray("Cells",motion.packedCells());return tag;
    }
    public static DonorGermoMotion restore(String id,EnumFacing facing,NBTTagCompound tag) {
        if(tag.getInteger("Version")!=1)return null;
        String[] keys={"X","Y","Z","PrevX","PrevY","PrevZ"};float[] data=new float[6];
        for(int i=0;i<6;i++){if(!tag.hasKey(keys[i],5))return null;data[i]=tag.getFloat(keys[i]);}
        if(!tag.hasKey("Segments",7) || !tag.hasKey("Cells",11))return null;
        return DonorGermoMotion.restore(id,-facing.getHorizontalAngle(),data,tag.getByteArray("Segments"),tag.getIntArray("Cells"));
    }
}
