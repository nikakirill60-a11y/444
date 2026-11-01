package ru.metrocraft.port.blackrazid;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static ru.metrocraft.port.blackrazid.NativeWeaponsTest.*;
import ru.metrocraft.port.blackrazid.weapons.*;
import ru.metrocraft.port.mcmod.McmodObjMesh;

public class NativeWeaponVisualsTest {
    @BeforeClass public static void boot(){NativeWeaponsTest.boot();}
    @Test public void all22NativeMeshesParseThroughTheRealRuntimeObjReaderWithoutOpenGL()throws Exception{
        assertEquals(22,NativeWeaponVisuals.meshes().size());
        for(String key:NativeWeaponVisuals.meshes()){
            try(InputStream raw=getClass().getResourceAsStream("/assets/metroport/models/blackrazid/weapons/"+key+".gz");InputStream in=new GZIPInputStream(raw)){
                McmodObjMesh mesh=McmodObjMesh.readMaterials(in);assertFalse(key,mesh.groups.isEmpty());assertTrue(NativeWeaponVisuals.mesh(key).materials.keySet().containsAll(mesh.groups.keySet()));
                for(List<float[]> triangles:mesh.groups.values()){assertEquals(0,triangles.size()%3);for(float[] v:triangles)for(float value:v)assertTrue(Float.isFinite(value));}
            }
        }
    }
    @Test public void sevenRegisteredSourceModulesAreSupportedButOtherItemsAndOtherGunsAreNot(){
        assertEquals(7,NativeWeaponVisuals.modules().size());Set<String> names=new HashSet<>();
        for(NativeWeaponVisuals.Module m:NativeWeaponVisuals.modules()){
            ItemStack stack=new ItemStack(BlackrazidWeapons.item(m.id));assertTrue(stack.getItem() instanceof BlackrazidModules.ModuleItem);assertTrue(NativeWeaponVisuals.supported(stack));assertFalse(stack.hasTagCompound());assertTrue(names.add(m.sourceId));assertTrue(m.scale>0);
        }
        assertTrue(NativeWeaponVisuals.supported(new ItemStack(item("gatling"))));assertFalse(NativeWeaponVisuals.supported(new ItemStack(item("revolver"))));assertFalse(NativeWeaponVisuals.supported(new ItemStack(item("ak"))));assertFalse(NativeWeaponVisuals.supported(ItemStack.EMPTY));assertNull(NativeWeaponVisuals.module("nonexistent"));
    }
    private static Set<String> names(List<NativeWeaponVisuals.Part> parts){Set<String> result=new HashSet<>();for(NativeWeaponVisuals.Part p:parts)assertTrue("Duplicate native part "+p.mesh,result.add(p.mesh));return result;}
    @Test public void all24NativeEquipmentConfigurationsHaveExactlyOneBarrelAndDriveAndNoPhantomParts(){
        for(boolean five:new boolean[]{false,true})for(boolean stock:new boolean[]{false,true})for(int drive=0;drive<3;drive++)for(boolean mag:new boolean[]{false,true}){
            Set<String> parts=names(NativeWeaponVisuals.gatling(five,stock,drive,mag,120,.5F));assertTrue(parts.contains("gatling/gatling.obj"));assertTrue(parts.contains("gatling/zvezda.obj"));
            assertEquals(five,parts.contains("gatling/stvol5.obj"));assertEquals(!five,parts.contains("gatling/stvol.obj"));assertEquals(stock,parts.contains("gatling/butt.obj"));assertEquals(mag,parts.contains("gatling/mag.obj"));
            assertEquals(drive==2,parts.contains("gatling/base5.obj"));assertEquals(drive!=2,parts.contains("gatling/base3.obj"));assertEquals(drive==1,parts.contains("gatling/giro.obj"));assertEquals(drive==1,parts.contains("gatling/giro_wheel.obj"));
            assertEquals(drive!=2,parts.contains("gatling/pruj.obj"));assertEquals(drive!=2,parts.contains("gatling/prujmeh.obj"));assertTrue(NativeWeaponVisuals.meshes().containsAll(parts));
        }
    }
    @Test public void localGyroAndSpringPivotsRemainTheOriginalNativeOffsets(){
        for(NativeWeaponVisuals.Part p:NativeWeaponVisuals.gatling(false,false,1,false,130,1)){
            if(p.mesh.endsWith("giro_wheel.obj")){assertArrayEquals(new float[]{.002F,-.002F,-.32F},p.translation,0);assertEquals(130,p.angle,0);assertEquals(1,p.y,0);}
            if(p.mesh.endsWith("pruj.obj")){assertArrayEquals(new float[]{0,-.093F,-.441F},p.translation,0);assertEquals(-40,p.angle,0);assertEquals(1,p.x,0);}
        }
    }
    @Test public void spinIsTickBasedBurstContinuousAndStopsWithoutAFrameCounter(){
        assertEquals(NativeWeaponVisuals.spin(102.5,100),NativeWeaponVisuals.spin(102.5,102),0);
        assertEquals(NativeWeaponVisuals.spin(108,100),NativeWeaponVisuals.spin(100000,100),0);
        for(int n=0;n<1000;n++)assertEquals(NativeWeaponVisuals.spin(102.5,100),NativeWeaponVisuals.spin(102.5,100),0);
        assertEquals(0,NativeWeaponVisuals.spin(99,100),0);assertEquals(0,NativeWeaponVisuals.spin(Double.NaN,100),0);assertEquals(0,NativeWeaponVisuals.spin(Double.POSITIVE_INFINITY,100),0);
        assertEquals(0,NativeWeaponVisuals.spin(100,-1),0);assertEquals(0,NativeWeaponVisuals.spin(100,Long.MAX_VALUE),0);
    }
    @Test public void animationInputsAreClampedAndCannotInjectNaNsOrInvalidDrives(){
        for(float partial:new float[]{-100,0,.5F,1,100,Float.NaN,Float.POSITIVE_INFINITY}){
            float p=NativeWeaponVisuals.actionProgress(3,10,partial);assertTrue(p>=0 && p<=1);assertTrue(Float.isFinite(p));
            for(NativeWeaponVisuals.Part part:NativeWeaponVisuals.gatling(true,true,1,true,Float.NaN,partial))assertTrue(Float.isFinite(part.angle));
        }
        assertEquals(0,NativeWeaponVisuals.actionProgress(0,10,1),0);assertEquals(0,NativeWeaponVisuals.actionProgress(3,0,1),0);
        assertEquals(1,NativeWeaponVisuals.excursion(.5F),.00001F);assertEquals(0,NativeWeaponVisuals.excursion(Float.NaN),0);
        for(int drive:new int[]{-1,3,Integer.MAX_VALUE})try{NativeWeaponVisuals.gatling(false,false,drive,false,0,0);fail();}catch(IllegalArgumentException expected){}
    }
    @Test public void renderingStateReadsNeverCreateOrModifyStackNBT(){
        ItemStack fresh=new ItemStack(item("gatling"));assertEquals(0,gun("gatling").visualAction(fresh));assertEquals(0,gun("gatling").visualTicks(fresh));assertFalse(fresh.hasTagCompound());
        NBTTagCompound tag=new NBTTagCompound();tag.setInteger("BlackrazidAction",4);tag.setInteger("BlackrazidActionTicks",5);fresh.setTagCompound(tag);NBTTagCompound before=tag.copy();
        for(int i=0;i<100;i++){assertEquals(4,gun("gatling").visualAction(fresh));assertEquals(5,gun("gatling").visualTicks(fresh));NativeWeaponVisuals.supported(fresh);}
        assertEquals(before,fresh.getTagCompound());assertEquals(0,gun("ak").visualAction(fresh));assertEquals(0,gun("ak").visualTicks(fresh));
    }
    @Test public void visualShotTimestampIsOnlyServerSuccessfulShotNotFailedSpawnOrCooldownAndSurvivesNBT()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("ak"));hold(p,s);p.inventory.setInventorySlotContents(1,magazine("ak_ammo",5));assertTrue(gun("ak").insertMagazine(s,p));
        when(p.world.getTotalWorldTime()).thenReturn(123L);assertFalse(gun("ak").fireNative(s,p));assertFalse(s.getTagCompound().hasKey(NativeWeaponVisuals.SHOT_TIME));
        when(p.world.spawnEntity(any(Entity.class))).thenReturn(true);assertTrue(gun("ak").fireNative(s,p));assertEquals(123,s.getTagCompound().getLong(NativeWeaponVisuals.SHOT_TIME));
        when(p.world.getTotalWorldTime()).thenReturn(999L);assertFalse(gun("ak").fireNative(s,p));assertEquals(123,s.getTagCompound().getLong(NativeWeaponVisuals.SHOT_TIME));
        ItemStack loaded=new ItemStack(s.writeToNBT(new NBTTagCompound()));assertEquals(123,loaded.getTagCompound().getLong(NativeWeaponVisuals.SHOT_TIME));assertFalse(new ItemStack(item("ak")).hasTagCompound());
        EntityPlayer client=player(true);ItemStack c=new ItemStack(item("ak"));hold(client,c);assertFalse(gun("ak").fireNative(c,client));assertFalse(c.hasTagCompound());
    }
}
