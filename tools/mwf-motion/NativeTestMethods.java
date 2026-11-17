import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Test-only: execute native MWF ammo methods against isolated API facades. Never shipped. */
public final class NativeTestMethods {
    public static void main(String[] args)throws Exception{
        ClassNode nativeGun=new ClassNode(Opcodes.ASM5);
        try(ZipFile z=new ZipFile(args[0])){new ClassReader(z.getInputStream(z.getEntry("com/modularwarfare/common/guns/ItemGun.class"))).accept(nativeGun,0);}
        Path p=Paths.get(args[1],"com/modularwarfare/common/guns/ItemGun.class");ClassNode test=new ClassNode(Opcodes.ASM5);new ClassReader(Files.readAllBytes(p)).accept(test,0);
        Set<String> methods=new HashSet<String>(Arrays.asList("getAmmoCount","hasNextShot","hasAmmoLoaded","consumeShot"));
        test.methods.removeIf(m->methods.contains(m.name));int n=0;
        for(MethodNode m:nativeGun.methods)if(methods.contains(m.name)){test.methods.add(m);n++;}
        if(n!=4)throw new IllegalStateException("Expected four native ammo methods");ClassWriter out=new ClassWriter(0);test.accept(out);Files.write(p,out.toByteArray());
    }
}
