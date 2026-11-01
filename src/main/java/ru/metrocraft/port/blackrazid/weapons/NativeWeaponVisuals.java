package ru.metrocraft.port.blackrazid.weapons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.item.ItemStack;

/** Read-only source-owned visual rules. No renderer, Minecraft singleton, or mutations of gun/module NBT. */
public final class NativeWeaponVisuals {
    private NativeWeaponVisuals(){}
    public static final String SHOT_TIME="BlackrazidVisualShotTime";
    public static final class Mesh { public Map<String,String> materials; public float[] bounds; public String sha256; }
    public static final class Module { public String id,sourceId,mesh,source; public float[] center; public float scale; }
    private static final Map<String,Mesh> MESHES=read("blackrazid_weapon_meshes.json",new TypeToken<Map<String,Mesh>>(){}.getType());
    private static final Module[] MODULES=read("blackrazid_weapon_module_models.json",Module[].class);
    private static <T>T read(String file,java.lang.reflect.Type type){
        try(Reader r=new InputStreamReader(Objects.requireNonNull(NativeWeaponVisuals.class.getResourceAsStream("/assets/metroport/"+file)),StandardCharsets.UTF_8)){return new Gson().fromJson(r,type);}
        catch(IOException e){throw new IllegalStateException("Native visual resources unavailable",e);}
    }
    public static Set<String> meshes(){return Collections.unmodifiableSet(MESHES.keySet());}
    public static Mesh mesh(String id){Mesh m=MESHES.get(id);if(m==null)throw new IllegalArgumentException("Unknown native mesh "+id);return m;}
    public static List<Module> modules(){return Collections.unmodifiableList(Arrays.asList(MODULES));}
    public static Module module(String id){for(Module m:MODULES)if(m.id.equals(id))return m;return null;}
    public static boolean supported(ItemStack stack){return !stack.isEmpty() && (stack.getItem() instanceof BlackrazidGunItem && ((BlackrazidGunItem)stack.getItem()).entry.sourceId.equals("Gatling")
        || stack.getItem() instanceof BlackrazidModules.ModuleItem && module(((BlackrazidModules.ModuleItem)stack.getItem()).entry.id)!=null);}
    public static final class Part {
        public final String mesh;public final float[] translation;public final float angle,x,y,z;
        private Part(String name,float tx,float ty,float tz,float angle,float x,float y,float z){mesh="gatling/"+name+".obj";translation=new float[]{tx,ty,tz};this.angle=angle;this.x=x;this.y=y;this.z=z;}
    }
    private static void part(List<Part> list,String name){list.add(new Part(name,0,0,0,0,0,0,1));}
    /** All native source equipment branches, each part once. No duplicate three-barrel bundle with GatlingBarrel. */
    public static List<Part> gatling(boolean five,boolean stock,int drive,boolean magazine,float rotation,float pump){
        if(drive<0 || drive>2)throw new IllegalArgumentException("Native drive must be crank, gyro or motor");
        rotation=Float.isFinite(rotation)?rotation%360:0;pump=Float.isFinite(pump)?Math.max(0,Math.min(1,pump)):0;
        List<Part> parts=new ArrayList<>();part(parts,"gatling");part(parts,"zvezda");
        parts.add(new Part(five?"stvol5":"stvol",0,0,0,rotation,0,0,1));
        if(magazine)part(parts,"mag");part(parts,drive==2?"base5":"base3");
        if(drive==1){part(parts,"giro");parts.add(new Part("giro_wheel",.002F,-.002F,-.32F,rotation,0,1,0));}
        if(stock)part(parts,"butt");
        if(drive!=2){parts.add(new Part("prujmeh",0,0,0,30*pump,0,0,1));parts.add(new Part("pruj",0,-.093F,-.441F,-40*pump,1,0,0));}
        return Collections.unmodifiableList(parts);
    }
    /** Bounded world-tick animation. Render frequency/GUI previews cannot advance or share a rotor's state. */
    public static float spin(double now,long shot){
        double elapsed=now-shot;if(!Double.isFinite(now) || elapsed<0 || shot<0)return 0;
        return (float)(((shot%8)+Math.min(8,elapsed))*45%360);
    }
    public static float actionProgress(int ticks,int duration,float partial){
        if(ticks<=0 || duration<=0)return 0;
        float p=Float.isFinite(partial)?Math.max(0,Math.min(1,partial)):0;
        return Math.max(0,Math.min(1,1-(ticks-p)/duration));
    }
    public static float excursion(float progress){return Float.isFinite(progress)?(float)Math.sin(Math.PI*Math.max(0,Math.min(1,progress))):0;}
}
