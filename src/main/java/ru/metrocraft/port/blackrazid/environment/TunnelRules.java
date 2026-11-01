package ru.metrocraft.port.blackrazid.environment;
import com.google.gson.Gson;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.util.math.BlockPos;
/** Native first ring orientation is metadata zero; helpers occupy the shell, never the passage. */
public final class TunnelRules {
    private TunnelRules(){}
    public static final class Operation {public String type;public float[] values;}
    public static final class Shape {public int id;public String mesh;public boolean doubleSided;public int[][] offsets;public int[] angles;public Operation[][] transforms;public double[][] bounds;}
    private static final Shape[] SHAPES=load();
    private static Shape[] load(){try(Reader r=new InputStreamReader(TunnelRules.class.getResourceAsStream("/assets/metroport/blackrazid_tunnels.json"),StandardCharsets.UTF_8)){Shape[] data=new Gson().fromJson(r,Shape[].class);if(data.length!=19)throw new IllegalStateException("All 19 native tunnel profiles required");
        for(int i=0;i<data.length;i++)if(data[i].id!=i || data[i].angles.length!=4 || (i>0 && (data[i].transforms.length!=4 || data[i].bounds.length!=4)))throw new IllegalStateException("Invalid native tunnel data");return data;}catch(Exception e){throw new IllegalStateException("Cannot read native tunnels",e);}}
    public static int count(){return SHAPES.length;}
    public static Shape profile(int id){if(!valid(id))throw new IllegalArgumentException("Unknown tunnel shape");return SHAPES[id];}
    public static net.minecraft.util.math.AxisAlignedBB renderBounds(int shape,int direction){
        Shape data=profile(shape);if(data.bounds==null)return new net.minecraft.util.math.AxisAlignedBB(-9,-9,-9,10,10,10);
        double[] b=data.bounds[direction&3];return new net.minecraft.util.math.AxisAlignedBB(b[0],b[1],b[2],b[3],b[4],b[5]);
    }
    public static boolean valid(int id){return id>=0&&id<SHAPES.length;}
    public static List<BlockPos> footprint(int shape,int direction){
        if(!valid(shape))return Collections.emptyList();Set<BlockPos> cells=new LinkedHashSet<>();cells.add(BlockPos.ORIGIN);
        for(int[] p:SHAPES[shape].offsets)cells.add(ru.metrocraft.port.blackrazid.transport.RailRules.rotate(p[0],p[1],p[2],direction));return Collections.unmodifiableList(new ArrayList<>(cells));
    }
    public static int placementDirection(float yaw){return (net.minecraft.util.math.MathHelper.floor(yaw*4/360+.5)+3)&3;}
    public static int renderAngle(int shape,int direction){return profile(shape).angles[direction&3];}
}
