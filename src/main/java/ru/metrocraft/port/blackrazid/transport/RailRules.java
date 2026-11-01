package ru.metrocraft.port.blackrazid.transport;

import com.google.gson.Gson;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.util.math.*;

/** Source footprint and tangent mathematics, independent of client rendering and general player properties. */
public final class RailRules {
    private RailRules(){}
    public static final class Shape {public int id;public String nameRu,nameEn;public int[][] offsets;}
    private static final Shape[] SHAPES=load();
    private static Shape[] load(){
        try(Reader r=new InputStreamReader(RailRules.class.getResourceAsStream("/assets/metroport/blackrazid_rails.json"),StandardCharsets.UTF_8)){
            Shape[] shapes=new Gson().fromJson(r,Shape[].class);if(shapes.length!=12)throw new IllegalStateException("12 native rails required");return shapes;
        }catch(Exception e){throw new IllegalStateException("Cannot load native rail footprints",e);}
    }
    public static boolean valid(int id){return id>=0&&id<SHAPES.length;}
    public static boolean isSwitch(int id){return id==5||id==6||id==9||id==10||id==11;}
    public static BlockPos rotate(int x,int y,int z,int direction){
        switch(direction&3){case 1:return new BlockPos(-z,y,x);case 2:return new BlockPos(-x,y,-z);case 3:return new BlockPos(z,y,-x);default:return new BlockPos(x,y,z);}
    }
    public static Vec3d rotate(double x,double z,int direction){
        switch(direction&3){case 1:return new Vec3d(-z,0,x);case 2:return new Vec3d(-x,0,-z);case 3:return new Vec3d(z,0,-x);default:return new Vec3d(x,0,z);}
    }
    public static List<BlockPos> footprint(int id,int direction){
        if(!valid(id))return Collections.emptyList();Set<BlockPos> result=new LinkedHashSet<>();result.add(BlockPos.ORIGIN);
        for(int[] p:SHAPES[id].offsets)result.add(rotate(p[0],p[1],p[2],direction));return Collections.unmodifiableList(new ArrayList<>(result));
    }
    public static final class Frame {
        public final Vec3d position,tangent;
        private Frame(Vec3d position,Vec3d tangent){this.position=position;this.tangent=tangent;}
    }
    /** Branch is latched by the entering vehicle; changing power must not redirect a car midway through a turnout. */
    public static Frame frame(int id,int direction,boolean branch,BlockPos primary,Vec3d position,Vec3d heading){
        if(!valid(id)||!finite(position)||!finite(heading))return null;
        double y=primary.getY()+.375;Vec3d straight=rotate(0,1,direction);
        if(id==0||((id==5||id==6||id==9||id==10)&&!branch)){
            Vec3d origin=new Vec3d(primary.getX()+.5,y,primary.getZ()+.5),delta=position.subtract(origin);
            return new Frame(origin.add(straight.scale(delta.dotProduct(straight))),straight);
        }
        if(id==3||id==4||((id==5||id==6)&&branch)){
            double sign=id==3||id==5?-1:1;Vec3d center=rotate(sign*15.5,-.5,direction).addVector(primary.getX()+.5,y,primary.getZ()+.5);
            return circle(position,center,15.5,heading);
        }
        if(id==7||id==8||id==9||id==10||id==11){
            double dx=position.x-primary.getX(),dz=position.z-primary.getZ();int half=dx*dx+dz*dz<62?0:1;
            double sign=id==7||id==9||(id==11&&branch)?-1:1;
            Vec3d center=rotate(sign*12.95,-.15,direction+half*2).add(rotate(sign*5*half,15*half,direction)).addVector(primary.getX()+.5,y,primary.getZ()+.5);
            return circle(position,center,12.9,heading);
        }
        // Crossing and dead end keep the incoming axis; the latter is stopped by its source collision.
        Vec3d axis=Math.abs(heading.x)>Math.abs(heading.z)?new Vec3d(1,0,0):new Vec3d(0,0,1);
        return new Frame(new Vec3d(position.x,y,position.z),axis);
    }
    private static Frame circle(Vec3d p,Vec3d center,double radius,Vec3d heading){
        double dx=center.x-p.x,dz=center.z-p.z,length=Math.hypot(dx,dz);
        if(length<1e-8)return new Frame(new Vec3d(p.x,center.y,p.z),heading.lengthSquared()>1e-12?new Vec3d(heading.x,0,heading.z).normalize():new Vec3d(0,0,1));
        return new Frame(new Vec3d(center.x-dx/length*radius,center.y,center.z-dz/length*radius),new Vec3d(-dz/length,0,dx/length));
    }
    private static boolean finite(Vec3d v){return v!=null&&Double.isFinite(v.x)&&Double.isFinite(v.y)&&Double.isFinite(v.z);}
}
