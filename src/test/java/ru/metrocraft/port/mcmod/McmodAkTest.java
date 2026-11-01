package ru.metrocraft.port.mcmod;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.*;
import ru.metrocraft.port.Definition;
import java.io.*;
import java.util.*;
import static org.junit.Assert.*;

public class McmodAkTest {
    @BeforeClass public static void setup(){Bootstrap.register();}
    @Test public void onlyOneMCMODGunAndItsOwnAmmoAreDeclared(){
        Definition[] d=McmodWeapons.definitions();assertEquals(2,d.length);
        assertEquals("mcmod_ak74",d[0].id);assertEquals("gun",d[0].kind);assertTrue(d[0].automatic);
        assertEquals(30,d[0].capacity);assertEquals(60,d[0].reloadTicks);assertEquals(2,d[0].delay);
        assertEquals(13,d[0].damage,0);assertEquals(1600,d[0].durability);
        assertEquals("mcmod_ammo5x45",d[1].id);assertEquals(d[1].id,d[0].ammo);assertEquals("ammo",d[1].kind);
    }
    @Test public void actualNativeOBJReaderFindsTheOriginalBaseAndAnimationGroups()throws Exception{
        try(InputStream in=getClass().getResourceAsStream("/assets/metroport/models/mcmod/guns/ak74.obj")){
            McmodObjMesh mesh=McmodObjMesh.read(in);assertEquals(25,mesh.groups.size());
            for(String group:new String[]{"body","mag","zatv","fire1","fire2"}){
                assertFalse(mesh.groups.get(group).isEmpty());
                for(float[] v:mesh.groups.get(group))for(float f:v)assertTrue(Float.isFinite(f));
            }
        }
    }
    @Test public void originalRawGripAndForeendLandExactlyOnExistingSkinHandAnchors(){
        assertArrayEquals(new float[]{-.1F,.13F,-.175F},McmodAkPose.point(0,-500,550),1e-5F);
        assertArrayEquals(new float[]{.5F,.3F,-.175F},McmodAkPose.point(0,-160,-650),1e-5F);
        assertTrue(McmodAkPose.point(0,-100,-1326)[0]>McmodAkPose.point(0,-100,1367)[0]);
    }
    @Test public void zeroConditionRetainsTheGunAndRepairPreservesCustomNBT(){
        McmodAkItem item=new McmodAkItem(McmodWeapons.definitions()[0]);ItemStack stack=new ItemStack(item);
        stack.setStackDisplayName("Original AK");stack.getTagCompound().setBoolean("NoDrop",true);
        stack.getTagCompound().setInteger("MetroAmmo",17);assertTrue(item.canFire(stack));
        assertFalse(stack.attemptDamageItem(5000,new Random(1),null));
        assertFalse(stack.isEmpty());assertEquals(1,stack.getCount());assertEquals(1600,stack.getItemDamage());
        assertFalse(item.canFire(stack));assertEquals(0,stack.getTagCompound().getInteger("condition"));
        stack.setItemDamage(10);assertTrue(item.canFire(stack));assertEquals(1590,stack.getTagCompound().getInteger("condition"));
        assertTrue(stack.getTagCompound().getBoolean("NoDrop"));assertEquals(17,stack.getTagCompound().getInteger("MetroAmmo"));
        assertEquals("Original AK",stack.getDisplayName());
    }
}
