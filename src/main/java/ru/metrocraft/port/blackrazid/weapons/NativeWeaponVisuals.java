package ru.metrocraft.port.blackrazid.weapons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;

/** Read-only source-owned visual rules. No renderer, Minecraft singleton, or mutations of gun/module NBT. */
public final class NativeWeaponVisuals {
    private NativeWeaponVisuals(){}
    public static final String SHOT_TIME="BlackrazidVisualShotTime";
    public static final class Mesh { public Map<String,String> materials; public float[] bounds; public String sha256; }
    public static final class Module { public String id,sourceId,mesh,source; public float[] center; public float scale; }
    private static final Map<String,Mesh> MESHES=read("blackrazid_weapon_meshes.json",new TypeToken<Map<String,Mesh>>(){}.getType());
    public static final class Gun { public String id,sourceId,body; public float[] nativeTranslation; public float nativeScale,nativeYaw;
        /** Shared by the GL renderer and geometry/contact regression tests. */
        public javax.vecmath.Matrix4f modelMatrix(){
            javax.vecmath.Matrix4f result=new javax.vecmath.Matrix4f(),step=new javax.vecmath.Matrix4f();
            result.setIdentity();result.setTranslation(new javax.vecmath.Vector3f(nativeTranslation));
            step.rotY((float)Math.toRadians(nativeYaw));result.mul(step);
            step.setIdentity();step.setScale(nativeScale);result.mul(step);return result;
        }
    }
    private static final Map<String,Gun> GUNS=read("blackrazid_weapon_models.json",new TypeToken<Map<String,Gun>>(){}.getType());
    public static Set<String> guns(){return Collections.unmodifiableSet(GUNS.keySet());}
    public static Gun gun(String sourceId){return GUNS.get(sourceId);}
    private static final Module[] MODULES=read("blackrazid_weapon_module_models.json",Module[].class);
    private static <T>T read(String file,java.lang.reflect.Type type){
        try(Reader r=new InputStreamReader(Objects.requireNonNull(NativeWeaponVisuals.class.getResourceAsStream("/assets/metroport/"+file)),StandardCharsets.UTF_8)){return new Gson().fromJson(r,type);}
        catch(IOException e){throw new IllegalStateException("Native visual resources unavailable",e);}
    }
    public static Set<String> meshes(){return Collections.unmodifiableSet(MESHES.keySet());}
    public static Mesh mesh(String id){Mesh m=MESHES.get(id);if(m==null)throw new IllegalArgumentException("Unknown native mesh "+id);return m;}
    public static List<Module> modules(){return Collections.unmodifiableList(Arrays.asList(MODULES));}
    public static Module module(String id){for(Module m:MODULES)if(m.id.equals(id))return m;return null;}
    public static boolean supported(ItemStack stack){return !stack.isEmpty() && (stack.getItem() instanceof BlackrazidGunItem && GUNS.containsKey(((BlackrazidGunItem)stack.getItem()).entry.sourceId)
        || stack.getItem() instanceof BlackrazidModules.ModuleItem && module(((BlackrazidModules.ModuleItem)stack.getItem()).entry.id)!=null);}
    public static final class Part {
        public final String mesh,role;public final float[] translation;public final float angle,x,y,z;
        private Part(String name,float tx,float ty,float tz,float angle,float x,float y,float z){this(name,"body",tx,ty,tz,angle,x,y,z);}
        private Part(String name,String role,float tx,float ty,float tz,float angle,float x,float y,float z){mesh=name.contains("/")?name:"gatling/"+name+".obj";this.role=role;translation=new float[]{tx,ty,tz};this.angle=angle;this.x=x;this.y=y;this.z=z;}
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
    /** Visual-only equipment snapshot. Values come from validated items/slots, never legacy display booleans. */
    public static final class Equipment { public boolean magazine,big,laser,automatic,silenced,optic,aim;public int rounds,pressure,action;public float phase;public final String[] slots={"","","","",""}; }
    public static Equipment equipment(ItemStack stack,EntityPlayer viewer){
        Equipment e=new Equipment();
        if(stack.isEmpty() || !(stack.getItem() instanceof BlackrazidGunItem))return e;
        BlackrazidGunItem gun=(BlackrazidGunItem)stack.getItem();
        ItemStack magazine=gun.visualMagazine(stack,viewer);e.magazine=!magazine.isEmpty();
        e.big=e.magazine && magazine.getItem() instanceof MagazineItem && gun.entry.magazines.length>1 && gun.entry.magazines[0].equals(((MagazineItem)magazine.getItem()).entry.id);
        for(int i=0;i<e.slots.length;i++){
            ItemStack module=BlackrazidModules.get(stack,i);if(!module.isEmpty())e.slots[i]=((BlackrazidModules.ModuleItem)module.getItem()).entry.sourceId;
        }
        e.rounds=gun.ammo(stack);e.pressure=gun.pressure(stack);e.action=gun.visualAction(stack);
        e.laser=BlackrazidModules.has(stack,1,"Laser");e.automatic=BlackrazidModules.has(stack,0,"AutoFire");
        e.silenced=BlackrazidModules.has(stack,2,"AbzacGlushak");e.optic=BlackrazidModules.has(stack,0,"Optica");return e;
    }
    private static void add(List<Part> out,String mesh,String role,float tx,float ty,float tz,float angle,float x,float y,float z){out.add(new Part(mesh,role,tx,ty,tz,angle,x,y,z));}
    /** Static assembly branches/pivots from the four native renderers; motion is applied separately in tick space. */
    public static List<Part> special(String source,Equipment e,boolean first){
        List<Part> out=new ArrayList<>();
        switch(source){
            case "Abzac":
                add(out,"abzac/abzac.obj","body",0,0,0,0,0,0,1);
                add(out,"abzac/zatvor.obj","bolt",0,first?.074F:0,first?.093F:0,0,1,0,0);
                if(e.magazine)add(out,e.big?"abzac/magbig.obj":"abzac/mag.obj","magazine",0,0,0,0,0,0,1);
                if(e.silenced)add(out,"abzac/compens.obj","body",0,0,0,0,0,0,1);
                if(e.automatic)add(out,"abzac/autofire.obj","body",0,0,0,0,0,0,1);
                if(e.laser)add(out,"rpk/laser.obj","body",first?.01F:0,first?.04F:0,first?.03F:0,0,0,0,1);
                if(first && e.magazine && e.action==0){
                    float[][] shells={{0,.065F,180},{.04F,.065F,-30},{.075F,.045F,-64},{.092F,.01F,-110},{.082F,-.035F,-100}};
                    for(int i=0;i<Math.min(5,Math.max(0,e.rounds));i++)add(out,"abzac/bullet.obj","shell",shells[i][0],shells[i][1],0,shells[i][2],0,0,1);
                }
                break;
            case "Vyhlop":
                add(out,"vyhlop/vyhlop1.obj","body",0,0,0,0,0,0,1);add(out,"vyhlop/zatvor.obj","bolt",0,0,0,0,1,0,0);
                if(e.magazine)add(out,"vyhlop/mag.obj","magazine",0,0,0,0,0,0,1);
                if(e.optic){add(out,"railgun/optica.obj","body",.0025F,-.078F,.1F,0,0,0,1);add(out,"railgun/test.obj","body",.0025F,-.078F,.34F,0,0,0,1);}
                else add(out,"vyhlop/pricel.obj","body",0,0,0,0,0,0,1);
                if(e.laser)add(out,"rpk/laser.obj","body",.01F,-.005F,-.25F,0,0,0,1);
                break;
            case "Velopal":add(out,"vyhlop/velo.obj","body",0,0,0,0,0,0,1);break;
            case "Ognemet":
                add(out,"ognemet/ognemet1.obj","body",0,0,0,0,0,0,1);
                add(out,"ognemet/zatvor.obj","bolt",0,0,0,first?-50:0,1,0,0);
                if(first){
                    add(out,"ognemet/strelka.obj","body",.037F,.1F,-.002F,100-Math.max(0,Math.min(180,e.pressure))*1.4F,0,0,1);
                    if(e.magazine)add(out,"ognemet/mag.obj","magazine",0,0,0,0,0,0,1);
                }
                break;
            default:throw new IllegalArgumentException("No native special assembly "+source);
        }
        return Collections.unmodifiableList(out);
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
