package ru.metrocraft.port.blackrazid.environment;
import com.google.gson.Gson;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.util.math.BlockPos;
/** Native first ring orientation is metadata zero; helpers occupy the shell, never the passage. */
public final class TunnelRules {
    private TunnelRules(){}
    private static final class Shape {int id;int[][] offsets;}
    private static final Shape[] SHAPES=load();
    private static Shape[] load(){try(Reader r=new InputStreamReader(TunnelRules.class.getResourceAsStream("/assets/metroport/blackrazid_tunnels.json"),StandardCharsets.UTF_8)){Shape[] data=new Gson().fromJson(r,Shape[].class);if(data.length!=2)throw new IllegalStateException("Two recovered tunnel profiles required");return data;}catch(Exception e){throw new IllegalStateException("Cannot read native tunnels",e);}}
    public static boolean valid(int id){return id>=0&&id<SHAPES.length;}
    public static List<BlockPos> footprint(int shape,int direction){
        if(!valid(shape))return Collections.emptyList();Set<BlockPos> cells=new LinkedHashSet<>();cells.add(BlockPos.ORIGIN);
        for(int[] p:SHAPES[shape].offsets)cells.add(ru.metrocraft.port.blackrazid.transport.RailRules.rotate(p[0],p[1],p[2],direction));return Collections.unmodifiableList(new ArrayList<>(cells));
    }
    public static int placementDirection(float yaw){return (net.minecraft.util.math.MathHelper.floor(yaw*4/360+.5)+3)&3;}
    public static int renderAngle(int shape,int direction){return shape==0?new int[]{90,180,270,0}[direction&3]:new int[]{90,0,270,180}[direction&3]+180;}
}
