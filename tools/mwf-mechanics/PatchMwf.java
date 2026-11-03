import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.zip.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;
import jdk.internal.org.objectweb.asm.tree.analysis.*;

/** Build-time only. Uses JDK 17 ASM; never shipped inside the game or as a coremod. */
public final class PatchMwf implements Opcodes {
    static final String HOOK="ru/metrocraft/mwf/MechanicsHooks";
    static final String GUN="com/modularwarfare/common/guns/GunType";
    static final String BASE="f91a5419cbc9e3cbd18f831e37c17aa2833d577142d324fed1ca14846ce95183";
    static final Map<String,Integer> edits=new TreeMap<>();
    static void count(String key) { edits.put(key,edits.getOrDefault(key,0)+1); }
    static MethodInsnNode hook(String name,String desc) {return new MethodInsnNode(INVOKESTATIC,HOOK,name,desc,false);}
    static int stackArgument(MethodNode method) {
        int local=(method.access&ACC_STATIC)!=0?0:1;
        for(Type type:Type.getArgumentTypes(method.desc)) {
            if(type.getDescriptor().equals("Lnet/minecraft/item/ItemStack;"))return local;
            local+=type.getSize();
        }
        return -1;
    }
    static InsnList stackForCapacity(ClassNode cls,MethodNode method) {
        InsnList out=new InsnList();int local=stackArgument(method);
        if(local>=0) {out.add(new VarInsnNode(ALOAD,local));return out;}
        if(cls.name.equals("com/modularwarfare/common/handler/ServerTickHandler")&&method.name.equals("onPlayerTick")) {
            out.add(new VarInsnNode(ALOAD,0)); // native method is static
            out.add(new FieldInsnNode(GETFIELD,"net/minecraftforge/fml/common/gameevent/TickEvent$PlayerTickEvent","player","Lnet/minecraft/entity/player/EntityPlayer;"));
            out.add(new MethodInsnNode(INVOKEVIRTUAL,"net/minecraft/entity/player/EntityPlayer","func_184614_ca","()Lnet/minecraft/item/ItemStack;",false));
            return out;
        }
        throw new IllegalStateException("No unambiguous ItemStack at "+cls.name+"."+method.name+method.desc);
    }
    static void gate(ClassNode cls,MethodNode method,String hook) {
        FieldNode player=null,outer=null;
        for(FieldNode f:cls.fields) {
            if(f.name.equals("val$entityPlayer"))player=f;
            if(f.name.equals("this$0"))outer=f;
        }
        if(player==null||outer==null)throw new IllegalStateException("Unexpected packet runnable fields");
        InsnList inject=new InsnList();LabelNode pass=new LabelNode();
        inject.add(new VarInsnNode(ALOAD,0));inject.add(new FieldInsnNode(GETFIELD,cls.name,player.name,player.desc));
        inject.add(new VarInsnNode(ALOAD,0));inject.add(new FieldInsnNode(GETFIELD,cls.name,outer.name,outer.desc));
        inject.add(hook(hook,"(Ljava/lang/Object;Ljava/lang/Object;)Z"));
        inject.add(new JumpInsnNode(IFNE,pass));inject.add(new InsnNode(RETURN));inject.add(pass);
        inject.add(new FrameNode(F_SAME,0,null,0,null));
        method.instructions.insert(inject);count(hook);
    }
    static byte[] patch(byte[] bytes) {
        ClassNode cls=new ClassNode(ASM8);new ClassReader(bytes).accept(cls,0);boolean changed=false;
        for(MethodNode method:cls.methods) {
            boolean shot=cls.name.equals("com/modularwarfare/common/network/PacketExpShot$1")&&method.name.equals("run");
            boolean hit=cls.name.equals("com/modularwarfare/common/network/PacketExpGunFire$1")&&method.name.equals("run");
            if(shot||hit) {gate(cls,method,shot?"beforeShot":"beforeHit");changed=true;}
            for(AbstractInsnNode ins:method.instructions.toArray()) {
                if(ins instanceof FieldInsnNode) {
                    FieldInsnNode f=(FieldInsnNode)ins;
                    if(f.getOpcode()==GETFIELD&&f.owner.equals(GUN)&&f.name.equals("internalAmmoStorage")) {
                        // BASIC animations have no stack parameter and are not used by the Metro pack.
                        if(cls.name.equals("com/modularwarfare/client/fpp/basic/animations/AnimStateMachine"))continue;
                        InsnList replace=stackForCapacity(cls,method);
                        replace.add(hook("capacity","(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Integer;"));
                        method.instructions.insertBefore(ins,replace);method.instructions.remove(ins);count("capacity");changed=true;
                    } else if(hit&&f.getOpcode()==GETFIELD&&f.owner.equals(GUN)
                        &&(f.name.equals("gunDamage")||f.name.equals("gunDamageHeadshotBonus"))) {
                        InsnList inject=new InsnList();inject.add(new VarInsnNode(ALOAD,0));
                        inject.add(new FieldInsnNode(GETFIELD,cls.name,"val$entityPlayer","Lnet/minecraft/entity/player/EntityPlayerMP;"));
                        inject.add(hook("scaleDamage","(FLjava/lang/Object;)F"));
                        method.instructions.insert(ins,inject);count("damage");changed=true;
                    }
                }
                if(cls.name.equals("com/modularwarfare/ModularWarfare")&&ins instanceof MethodInsnNode) {
                    MethodInsnNode m=(MethodInsnNode)ins;
                    if(m.owner.equals("com/modularwarfare/common/network/NetworkHandler")&&m.name.equals("initialise")&&m.desc.equals("()V")) {
                        method.instructions.insert(ins,hook("install","()V"));count("install");changed=true;
                    }
                }
                if(cls.name.equals("com/modularwarfare/common/guns/manager/ShotManager")&&method.name.equals("checkCanFireClient")&&ins.getOpcode()==IRETURN) {
                    InsnList inject=new InsnList();inject.add(new VarInsnNode(ALOAD,0));inject.add(hook("clientCanFire","(ZLjava/lang/Object;)Z"));
                    method.instructions.insertBefore(ins,inject);count("clientCanFire");changed=true;
                }
                if(cls.name.equals("com/modularwarfare/common/guns/ItemGun")&&method.name.equals("consumeShot")&&ins.getOpcode()==RETURN) {
                    InsnList inject=new InsnList();inject.add(new VarInsnNode(ALOAD,0));inject.add(hook("afterConsume","(Ljava/lang/Object;)V"));
                    method.instructions.insertBefore(ins,inject);count("afterConsume");changed=true;
                }
            }
        }
        if(cls.name.equals("com/modularwarfare/ModularWarfare")) {
            for(FieldNode f:cls.fields)if(f.name.equals("MOD_VERSION"))f.value="2024.2.4.6f-metro1";
            for(AnnotationNode a:cls.visibleAnnotations)if(a.desc.equals("Lnet/minecraftforge/fml/common/Mod;")) {
                for(int i=0;i<a.values.size();i+=2)if(a.values.get(i).equals("version"))a.values.set(i+1,"2024.2.4.6f-metro1");
                a.values.add("acceptableRemoteVersions");a.values.add("[2024.2.4.6f-metro1]");
            }
            changed=true;
        }
        if(!changed)return bytes;
        // Preserve original stack frames, supplying an explicit same-frame only for each new entry gate.
        ClassWriter out=new ClassWriter(ClassWriter.COMPUTE_MAXS);cls.accept(out);
        byte[] result=out.toByteArray();ClassNode verified=new ClassNode(ASM8);new ClassReader(result).accept(verified,0);
        for(MethodNode m:verified.methods)if((m.access&(ACC_ABSTRACT|ACC_NATIVE))==0)try {
            new Analyzer<BasicValue>(new BasicVerifier()).analyze(verified.name,m);
        }catch(AnalyzerException e){throw new IllegalStateException("Invalid bytecode stack: "+verified.name+"."+m.name,e);}
        count("classes");return result;
    }
    static String sha(byte[] bytes)throws Exception {
        StringBuilder s=new StringBuilder();for(byte b:MessageDigest.getInstance("SHA-256").digest(bytes))s.append(String.format("%02x",b&255));return s.toString();
    }
    public static void main(String[] args)throws Exception {
        Path source=Paths.get(args[0]),destination=Paths.get(args[1]),helpers=Paths.get(args[2]);
        if(source.toAbsolutePath().equals(destination.toAbsolutePath()))throw new IllegalArgumentException("Never overwrite the original JAR");
        if(!BASE.equals(sha(Files.readAllBytes(source))))throw new IllegalArgumentException("Not the selected closed MWF JAR");
        TreeMap<String,byte[]> files=new TreeMap<>();
        try(ZipFile zip=new ZipFile(source.toFile())) {
            Enumeration<? extends ZipEntry> entries=zip.entries();
            while(entries.hasMoreElements()) {
                ZipEntry e=entries.nextElement();if(e.isDirectory())continue;
                String n=e.getName();if(n.matches("(?i)META-INF/[^/]+\\.(SF|RSA|DSA)"))continue;
                byte[] data=zip.getInputStream(e).readAllBytes();
                if(n.startsWith("com/modularwarfare/")&&n.endsWith(".class"))data=patch(data);
                if(files.put(n,data)!=null)throw new IllegalStateException("Duplicate original entry "+n);
            }
        }
        if(edits.getOrDefault("capacity",0)!=15||edits.getOrDefault("beforeShot",0)!=1||edits.getOrDefault("beforeHit",0)!=1
            ||edits.getOrDefault("damage",0)!=3||edits.getOrDefault("install",0)!=1||edits.getOrDefault("clientCanFire",0)!=5
            ||edits.getOrDefault("afterConsume",0)!=1)throw new IllegalStateException("Unexpected hook counts: "+edits);
        try(var paths=Files.walk(helpers.resolve("ru/metrocraft/mwf"))) {
            for(Path p:paths.filter(p->p.toString().endsWith(".class")).toList()) {
                String name=helpers.relativize(p).toString().replace(File.separatorChar,'/');
                if(name.contains("Test"))continue;
                if(files.put(name,Files.readAllBytes(p))!=null)throw new IllegalStateException("Helper already present: "+name);
            }
        }
        files.put("metro-mechanics-build.txt",("Metro mechanics 1.0\nBase SHA-256: "+BASE+"\nHooks: "+edits+"\nNot Minecraft runtime validated.\n").getBytes(java.nio.charset.StandardCharsets.UTF_8));
        Files.createDirectories(destination.getParent());
        try(ZipOutputStream zip=new ZipOutputStream(Files.newOutputStream(destination))) {
            for(var entry:files.entrySet()) {ZipEntry e=new ZipEntry(entry.getKey());e.setTime(0);zip.putNextEntry(e);zip.write(entry.getValue());zip.closeEntry();}
        }
        System.out.println("PATCH_OK "+edits+" SHA256="+sha(Files.readAllBytes(destination)));
    }
}
