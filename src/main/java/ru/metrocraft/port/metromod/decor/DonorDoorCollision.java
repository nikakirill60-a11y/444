package ru.metrocraft.port.metromod.decor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Port of the donor's model-mode Door bounds contract, not render-mesh voxels.
 * Provenance and the reviewed behavior overrides: DONOR_DOOR_COLLISION.json.
 * Map-authored transforms/custom bounds are not contained in the donor client.
 * MinETRO retains its existing unit-scale placement/ground/facing adapter.
 */
public final class DonorDoorCollision {
    private double[][] models;
    private double[] grow;
    private static final Map<String,DonorDoorCollision> ALL=load();
    public static DonorDoorCollision get(String id) { return ALL.get(id); }
    private static Map<String,DonorDoorCollision> load() {
        try(InputStream in=DonorDoorCollision.class.getResourceAsStream("/assets/metroport/metromod_door_collision.json")) {
            if(in==null)throw new IOException("Missing donor Door collision data");
            Map<String,DonorDoorCollision> data=new Gson().fromJson(new InputStreamReader(in,StandardCharsets.UTF_8),
                    new TypeToken<LinkedHashMap<String,DonorDoorCollision>>(){}.getType());
            if(data==null || data.size()!=27)throw new IOException("Incomplete donor Door collision data");
            for(Map.Entry<String,DonorDoorCollision> entry:data.entrySet()) {
                DonorDoorCollision value=entry.getValue();
                if(!entry.getKey().startsWith("metromod_door_") || value==null || value.models==null || value.models.length<1 || value.models.length>2
                        || value.grow==null || value.grow.length!=3)throw new IOException("Invalid donor Door collision rule");
                for(double[] box:value.models)if(!valid(box))throw new IOException("Invalid donor model extrema");
                for(double g:value.grow)if(g!=0 && g!=1)throw new IOException("Invalid donor bounds expansion");
            }
            return Collections.unmodifiableMap(data);
        } catch(IOException e) {throw new IllegalStateException("Cannot load donor Door collisions",e);}
    }
    public static boolean valid(double[] b) {
        if(b==null || b.length!=6)return false;
        for(int i=0;i<3;i++)if(!Double.isFinite(b[i]) || !Double.isFinite(b[i+3]) || b[i]>b[i+3]
                || Math.abs(b[i])>32 || Math.abs(b[i+3])>32)return false;
        return true;
    }
    /** Donor uses float extrema and adds .005 to BOTH ends of spans below .01.
     * It does not snap/voxelize and does not clamp the final span to exactly .01.
     */
    static double[] providerBox(double[] source) {
        double[] b=source.clone();
        for(int i=0;i<3;i++)if(b[i+3]-b[i]<(double).01F) {b[i]-=(double)(.01F/2);b[i+3]+=(double)(.01F/2);}
        return b;
    }
    public AxisAlignedBB bounds(OriginalGateProfile p,BlockPos pos,EnumFacing facing,double[] custom) {
        double[] b;
        if(custom!=null) {
            if(!valid(custom))throw new IllegalArgumentException("Invalid custom Door bounds");
            // A stored AABB overrides the behavior/provider; no automatic grow.
            b=custom.clone();
        } else {
            b=providerBox(models[0]);
            for(int n=1;n<models.length;n++) {
                double[] other=providerBox(models[n]);
                for(int i=0;i<3;i++){b[i]=Math.min(b[i],other[i]);b[i+3]=Math.max(b[i+3],other[i+3]);}
            }
            for(int i=0;i<3;i++){b[i]-=grow[i];b[i+3]+=grow[i];}
        }
        for(int i=0;i<3;i++){double shift=p.offset[i]+(i==1?p.ground:0);b[i]+=shift;b[i+3]+=shift;}
        return OriginalGateProfile.worldBox(pos,facing,b);
    }
}
