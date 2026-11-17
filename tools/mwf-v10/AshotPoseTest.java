package ru.metrocraft.mwf;

import java.lang.reflect.Field;
import java.util.*;
import org.joml.Matrix4f;
import mchhui.hegltf.DataNode;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;

/** Real metro6 client hook and actual output matrices; only Minecraft API is a test facade. */
public final class AshotPoseTest {
    static int checks;
    static void check(boolean value,String message){checks++;if(!value)throw new AssertionError(message);}
    static void same(Matrix4f a,Matrix4f b,String message){float[] x=a.get(new float[16]),y=b.get(new float[16]);for(int i=0;i<16;i++)check(Math.abs(x[i]-y[i])<1e-5,message+" index "+i);}
    static void attach(ItemStack stack,String slot,String id){stack.tag.setTag("attachment_"+slot,new ItemStack(new Item("modularwarfare:"+id,1)).writeToNBT(new NBTTagCompound()));}
    public static void main(String[] args)throws Exception {
        Map<String,MotionClient.Profile> profiles=new HashMap<String,MotionClient.Profile>();
        for(int side=0;side<2;side++){
            MotionClient.Profile p=new MotionClient.Profile();p.gun="metro_ashot";p.attachment="metro_mod_ashot_butt";p.matrix=new float[16];
            for(int i=0;i<16;i++)p.matrix[i]=Float.parseFloat(args[side*16+i]);
            profiles.put("metroMotionAshot"+(side==0?"Left":"Right"),p);
        }
        Field field=MotionClient.class.getDeclaredField("profiles");field.setAccessible(true);field.set(null,profiles);
        Object entity=new Object();ItemStack gun=new ItemStack(new Item("modularwarfare:metro_ashot",1));gun.tag=new NBTTagCompound();
        for(Map.Entry<String,MotionClient.Profile> entry:profiles.entrySet()){
            DataNode node=new DataNode();node.name=entry.getKey();
            Matrix4f rest=new Matrix4f().translation(.12f,-.04f,.08f).rotateY(.23f);
            MotionClient.enter(gun,entity);Matrix4f a=new Matrix4f(rest);MotionClient.node(node,a);same(a,rest,"no-stock pose untouched");
            attach(gun,"barrel","metro_mod_ashot_butt");a=new Matrix4f(rest);MotionClient.node(node,a);same(a,rest,"wrong slot ignored");gun.tag.removeTag("attachment_barrel");
            attach(gun,"stock","metro_mod_butt");a=new Matrix4f(rest);MotionClient.node(node,a);same(a,rest,"revolver stock cannot activate Ashot");
            attach(gun,"stock","metro_mod_ashot_butt");a=new Matrix4f(rest);MotionClient.node(node,a);
            Matrix4f expected=new Matrix4f(rest).mul(new Matrix4f().set(entry.getValue().matrix));same(a,expected,"correct stock applies output matrix");
            check(!Arrays.equals(a.get(new float[16]),rest.get(new float[16])),"stock visibly changes pose");
            a=new Matrix4f(rest);MotionClient.node(node,a);same(a,expected,"no accumulated offsets");
            gun.tag.removeTag("attachment_stock");a=new Matrix4f(rest);MotionClient.node(node,a);same(a,rest,"stock removal restores baseline");
            MotionClient.leave();attach(gun,"stock","metro_mod_ashot_butt");a=new Matrix4f(rest);MotionClient.node(node,a);same(a,rest,"no leaking context");gun.tag.removeTag("attachment_stock");
        }
        // Native ItemGun methods are transplanted from the delivered JAR by the builder.
        ItemStack velo=MechanicsTest.gun("metro_velopal",0);
        ((MechanicsTest.Type)((com.modularwarfare.common.guns.ItemGun)velo.item).type).internalAmmoStorage=6;
        velo.tag.setInteger("ammocount",6);
        check(MechanicsHooks.capacity(((com.modularwarfare.common.guns.ItemGun)velo.item).type,velo)==6,"Velopal native capacity six");
        for(int i=6;i>0;i--){
            check(com.modularwarfare.common.guns.ItemGun.getAmmoCount(velo)==i,"native round count before shot");
            check(com.modularwarfare.common.guns.ItemGun.hasNextShot(velo),"six rounds each authorize a shot");
            com.modularwarfare.common.guns.ItemGun.consumeShot(velo);
        }
        check(com.modularwarfare.common.guns.ItemGun.getAmmoCount(velo)==0,"six shots leave zero");
        check(!com.modularwarfare.common.guns.ItemGun.hasNextShot(velo),"seventh shot refused");
        System.out.println("ASHOT_POSE_TESTS_OK "+checks+" assertions; NOT a live Minecraft test");
    }
}
