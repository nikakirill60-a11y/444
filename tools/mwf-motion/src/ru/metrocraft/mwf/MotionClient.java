package ru.metrocraft.mwf;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import mchhui.hegltf.DataNode;
import org.joml.Matrix4f;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import static ru.metrocraft.mwf.MechanicsHooks.*;

/** Physical-client only. No shared GunType mutation and no cumulative node/animation edits. */
public final class MotionClient {
    private static final Logger LOG=Logger.getLogger("MetroMWFMotion");
    private static final ThreadLocal<Context> CONTEXT=new ThreadLocal<Context>();
    private static final Map<Integer,Sample> REMOTE=new HashMap<Integer,Sample>();
    private static final RotorRules.Rotor LOCAL=new RotorRules.Rotor();
    private static Object world;private static int lastSlot=-1;private static boolean pressed,failed;
    private static long lastSent=Long.MIN_VALUE,tickNanos;
    private static Map<String,Profile> profiles;
    public static final class Profile {String gun,attachment;float[] matrix;boolean rotor;}
    static final class Context {Object stack,entity;String gun;Context(Object s,Object e,String g){stack=s;entity=e;gun=g;}}
    static final class Sample {float speed,angle;long at;Sample(int s,int a){speed=s/1000f;angle=a/1000f;at=System.nanoTime();}}
    private static Object mc()throws Exception{return call(Class.forName("net.minecraft.client.Minecraft"),"getMinecraft|func_71410_x");}
    public static void install()throws Exception {
        try(Reader r=new InputStreamReader(MotionClient.class.getResourceAsStream("/assets/modularwarfare/metro_motion.json"),"UTF-8")){
            Object gson=Class.forName("com.google.gson.Gson").getConstructor().newInstance();
            Map<?,?> raw=(Map<?,?>)call(gson,"fromJson",r,Map.class);profiles=new HashMap<String,Profile>();
            for(Map.Entry<?,?> entry:raw.entrySet()){
                Map<?,?> v=(Map<?,?>)entry.getValue();Profile p=new Profile();p.gun=(String)v.get("gun");p.attachment=(String)v.get("attachment");p.rotor=Boolean.TRUE.equals(v.get("rotor"));
                if(!p.rotor){List<?> list=(List<?>)v.get("matrix");if(list.size()!=16)throw new IllegalStateException("Invalid stock pose matrix");p.matrix=new float[16];for(int i=0;i<16;i++)p.matrix[i]=((Number)list.get(i)).floatValue();}
                profiles.put((String)entry.getKey(),p);
            }
            if(profiles.size()!=5)throw new IllegalStateException("Missing motion profiles");
        }
        call(field(Class.forName("net.minecraftforge.common.MinecraftForge"),"EVENT_BUS"),"register",new MotionClient());
    }
    public static void sample(int entity,int speed,int angle){
        long now=System.nanoTime();
        for(Iterator<Sample> it=REMOTE.values().iterator();it.hasNext();)if(now-it.next().at>2_000_000_000L)it.remove();
        if(REMOTE.size()>=512&&!REMOTE.containsKey(entity))REMOTE.clear();
        REMOTE.put(entity,new Sample(speed,angle));
    }
    private static void send(Object p,int slot,boolean down)throws Exception {
        call(field(Class.forName("com.modularwarfare.ModularWarfare"),"NETWORK"),"sendToServer",new PacketMetroRotor(slot,down));
    }
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event){
        if(event.phase!=TickEvent.Phase.END)return;
        try {
            Object mc=mc(),w=field(mc,"world","field_71441_e"),p=field(mc,"player","field_71439_g");
            if(world!=w){world=w;lastSlot=-1;lastSent=Long.MIN_VALUE;pressed=false;failed=false;LOCAL.speed=0;LOCAL.angle=0;REMOTE.clear();}
            if(p==null||w==null||failed)return;
            int slot=MotionHooks.slot(p);Object stack=held(p);boolean gun=MotionHooks.gatling(stack);
            if(lastSlot!=slot||!gun){LOCAL.speed=0;LOCAL.angle=0;pressed=false;lastSent=Long.MIN_VALUE;lastSlot=slot;}
            if(!gun)return;
            Object settings=field(mc,"gameSettings","field_71474_y");
            Object key=field(settings,"keyBindAttack","field_74312_F");
            boolean down=Boolean.TRUE.equals(call(key,"isKeyDown|func_151470_d"))
                &&field(mc,"currentScreen","field_71462_r")==null
                &&Boolean.TRUE.equals(field(mc,"inGameHasFocus","field_71415_G"))&&alive(p)
                &&!Boolean.TRUE.equals(call(p,"isSprinting|func_70051_ag"))
                &&Boolean.TRUE.equals(call(Class.forName("com.modularwarfare.common.guns.ItemGun"),"hasNextShot",stack));
            Object machine=call(Class.forName("com.modularwarfare.client.ClientRenderHooks"),"getEnhancedAnimMachine",p);
            down&=!Boolean.TRUE.equals(field(machine,"reloading"));
            Object controller=call(Class.forName("com.modularwarfare.client.fpp.enhanced.renderers.RenderGunEnhanced"),"getClientController");
            if(controller!=null)down&=!Boolean.TRUE.equals(call(controller,"isDrawing"));
            long tick=now(p);
            if(down!=pressed||lastSent==Long.MIN_VALUE||tick-lastSent>=4){send(p,slot,down);lastSent=tick;}
            pressed=down;LOCAL.tick(down,MotionHooks.has(stack,"grip","metro_mod_gatling_motor"));tickNanos=System.nanoTime();
            for(Iterator<Sample> it=REMOTE.values().iterator();it.hasNext();)if(System.nanoTime()-it.next().at>2_000_000_000L)it.remove();
        }catch(Exception e){fail(e);}catch(LinkageError e){fail(e);}
    }
    private static void fail(Throwable e){if(!failed)LOG.log(Level.WARNING,"Metro rotor disabled until world change",e);failed=true;pressed=false;LOCAL.speed=0;}
    public static boolean ready(){return !failed&&pressed&&LOCAL.speed>=.30-1e-8;}
    public static int rpm()throws Exception {
        Object p=field(mc(),"player","field_71439_g");
        // Match server fixed-tick intervals so client prediction never knowingly outruns its gate.
        return Math.max(1,1200/LOCAL.interval(MotionHooks.has(held(p),"barrel","metro_mod_gatling_barrel")));
    }
    public static void enter(Object stack,Object entity){
        CONTEXT.remove();
        try {
            if(entity==null)entity=field(mc(),"player","field_71439_g");
            String id=id(stack);
            if(RotorRules.GUN.equals(id)||"metro_podonok".equals(id)||"metro_revolver".equals(id))CONTEXT.set(new Context(stack,entity,id));
        }catch(Exception e){fail(e);}
    }
    public static void leave(){CONTEXT.remove();}
    /** Called on a fresh LOCAL matrix before parent multiplication, every pose calculation. */
    public static void node(DataNode node,Matrix4f matrix){
        if(profiles==null||node.name==null||!node.name.startsWith("metroMotion"))return;
        Context context=CONTEXT.get();if(context==null)return;
        Profile profile=profiles.get(node.name);if(profile==null||!profile.gun.equals(context.gun))return;
        try {
            if(profile.rotor){
                float angle=0;Object local=field(mc(),"player","field_71439_g");
                if(context.entity==local){
                    double elapsed=Math.max(0,Math.min(.05,(System.nanoTime()-tickNanos)/1e9));
                    angle=(float)(LOCAL.angle+elapsed*1200*LOCAL.speed);
                }else if(context.entity!=null){
                    int id=((Number)call(context.entity,"getEntityId|func_145782_y")).intValue();Sample s=REMOTE.get(id);
                    if(s!=null)angle=s.angle+(float)Math.max(0,Math.min(.25,(System.nanoTime()-s.at)/1e9))*1200*s.speed;
                }
                matrix.rotateX((float)Math.toRadians(angle));
            }else if(MotionHooks.has(context.stack,"stock",profile.attachment)){
                matrix.mul(new Matrix4f().set(profile.matrix));
            }
        }catch(Exception e){fail(e);}
    }
}
