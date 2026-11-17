import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;

/** Incremental patch of the pinned metro4 binary, never a substitute upstream. Build-only. */
public final class PatchMotion implements Opcodes {
    static final String BASE="dc78d8251b98d82006489432d09fb98486df888b779aaf675a62987bc5160823";
    static final String H="ru/metrocraft/mwf/MotionHooks", C="ru/metrocraft/mwf/MotionClient";
    static final Map<String,Integer> edits=new TreeMap<String,Integer>();
    static void count(String k){edits.put(k,edits.getOrDefault(k,0)+1);}
    static MethodInsnNode hook(String owner,String name,String desc){return new MethodInsnNode(INVOKESTATIC,owner,name,desc);}
    static int arg(MethodNode m,String type){int n=(m.access&ACC_STATIC)!=0?0:1;for(Type t:Type.getArgumentTypes(m.desc)){if(t.getDescriptor().equals(type))return n;n+=t.getSize();}return -1;}
    static byte[] patch(byte[] original)throws Exception {
        ClassNode c=new ClassNode(ASM5);new ClassReader(original).accept(c,0);boolean changed=false;
        for(MethodNode m:c.methods){
            boolean render=c.name.endsWith("/RenderGunEnhanced")&&(m.name.equals("renderItem")||(m.name.equals("drawThirdGun")&&m.desc.endsWith(";Z)V")));
            if(render){
                int stack=arg(m,"Lnet/minecraft/item/ItemStack;"),entity=arg(m,"Lnet/minecraft/entity/EntityLivingBase;");
                if(stack<0)throw new IllegalStateException("Missing render stack");
                InsnList enter=new InsnList();enter.add(new VarInsnNode(ALOAD,stack));enter.add(entity<0?new InsnNode(ACONST_NULL):new VarInsnNode(ALOAD,entity));
                enter.add(hook(C,"enter","(Ljava/lang/Object;Ljava/lang/Object;)V"));m.instructions.insert(enter);
                for(AbstractInsnNode i:m.instructions.toArray())if(i.getOpcode()==RETURN)m.instructions.insertBefore(i,hook(C,"leave","()V"));
                count("renderContext");changed=true;
            }
            int rpmReads=0;
            for(AbstractInsnNode i:m.instructions.toArray()){
                if(i instanceof MethodInsnNode){
                    MethodInsnNode call=(MethodInsnNode)i;
                    if(c.name.startsWith("com/modularwarfare/")&&call.owner.equals("ru/metrocraft/mwf/MechanicsHooks")){
                        if(Arrays.asList("beforeShot","beforeHit","allowLegacy","clientCanFire","reloadTask").contains(call.name)){
                            call.owner=H;count(call.name);changed=true;
                        }else if(call.name.equals("install")){
                            m.instructions.insert(i,hook(H,"install","()V"));count("install");changed=true;
                        }
                    }
                }
                if(c.name.endsWith("/ShotManager")&&m.name.equals("fireClient")&&i instanceof FieldInsnNode){
                    FieldInsnNode f=(FieldInsnNode)i;
                    if(f.getOpcode()==GETFIELD&&f.name.equals("roundsPerMin")&&f.owner.equals("com/modularwarfare/common/guns/GunType")){
                        ++rpmReads;
                        if(rpmReads==1){InsnList t=new InsnList();t.add(new VarInsnNode(ALOAD,0));t.add(hook(H,"clientRpm","(ILjava/lang/Object;)I"));m.instructions.insert(i,t);count("cooldownRpm");changed=true;}
                    }
                }
                if(c.name.equals("mchhui/hegltf/GltfRenderModel")&&m.name.equals("calculateNodeAndChildren")&&i instanceof VarInsnNode&&i.getOpcode()==ALOAD&&((VarInsnNode)i).var==2&&i.getNext().getOpcode()==IFNULL){
                    InsnList t=new InsnList();t.add(new VarInsnNode(ALOAD,1));t.add(new VarInsnNode(ALOAD,4));
                    t.add(hook(C,"node","(Lmchhui/hegltf/DataNode;Lorg/joml/Matrix4f;)V"));m.instructions.insertBefore(i,t);count("localNodePose");changed=true;
                }
            }
        }
        if(c.name.equals("com/modularwarfare/ModularWarfare")){
            for(FieldNode f:c.fields)if(f.name.equals("MOD_VERSION"))f.value="2024.2.4.6f-metro5";
            for(AnnotationNode a:c.visibleAnnotations)if(a.desc.equals("Lnet/minecraftforge/fml/common/Mod;"))for(int i=0;i<a.values.size();i+=2){
                if(a.values.get(i).equals("version"))a.values.set(i+1,"2024.2.4.6f-metro5");
                if(a.values.get(i).equals("acceptableRemoteVersions"))a.values.set(i+1,"[2024.2.4.6f-metro5]");
            }
            changed=true;
        }
        if(!changed)return original;
        ClassWriter out=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(out);byte[] bytes=out.toByteArray();
        ClassNode verify=new ClassNode(ASM5);new ClassReader(bytes).accept(verify,0);
        for(MethodNode m:verify.methods)if((m.access&(ACC_NATIVE|ACC_ABSTRACT))==0)new Analyzer<BasicValue>(new BasicVerifier()).analyze(c.name,m);
        count("classes");return bytes;
    }
    static String sha(byte[] b)throws Exception{StringBuilder s=new StringBuilder();for(byte x:MessageDigest.getInstance("SHA-256").digest(b))s.append(String.format("%02x",x&255));return s.toString();}
    public static void main(String[] args)throws Exception {
        Path input=Paths.get(args[0]);if(!sha(Files.readAllBytes(input)).equals(BASE))throw new IllegalStateException("Expected exact metro4 JAR");
        Map<String,byte[]> files=new TreeMap<String,byte[]>();
        try(ZipFile z=new ZipFile(input.toFile())){Enumeration<? extends ZipEntry> es=z.entries();while(es.hasMoreElements()){
            ZipEntry e=es.nextElement();if(e.isDirectory())continue;byte[] b=z.getInputStream(e).readAllBytes();
            files.put(e.getName(),e.getName().endsWith(".class")?patch(b):b);
        }}
        Map<String,Integer> required=new TreeMap<String,Integer>();
        required.put("beforeShot",1);required.put("beforeHit",1);required.put("allowLegacy",1);required.put("clientCanFire",5);required.put("reloadTask",1);
        required.put("install",1);required.put("cooldownRpm",1);required.put("renderContext",2);required.put("localNodePose",1);
        for(String key:required.keySet())if(!required.get(key).equals(edits.get(key)))throw new IllegalStateException("Wrong patch count "+key+": "+edits);
        Path classes=Paths.get(args[1]);try(java.util.stream.Stream<Path> stream=Files.walk(classes)){
            for(Iterator<Path> it=stream.filter(p->p.toString().endsWith(".class")).iterator();it.hasNext();){Path p=it.next();String name=classes.relativize(p).toString().replace('\\','/');if(!name.startsWith("ru/metrocraft/mwf/"))throw new IllegalStateException("Compile facade leaked: "+name);files.put(name,Files.readAllBytes(p));}
        }
        files.put("assets/modularwarfare/metro_motion.json",Files.readAllBytes(Paths.get(args[2])));
        try(ZipOutputStream z=new ZipOutputStream(Files.newOutputStream(Paths.get(args[3])))){z.setLevel(9);for(String name:files.keySet()){
            ZipEntry e=new ZipEntry(name);e.setTime(0);z.putNextEntry(e);z.write(files.get(name));z.closeEntry();
        }}
        System.out.println("Verified hooks: "+edits);System.out.println("SHA-256: "+sha(Files.readAllBytes(Paths.get(args[3]))));
    }
}
