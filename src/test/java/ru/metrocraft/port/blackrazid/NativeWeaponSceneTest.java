package ru.metrocraft.port.blackrazid;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.*;
import static org.junit.Assert.*;
import static ru.metrocraft.port.blackrazid.NativeWeaponsTest.*;
import ru.metrocraft.port.blackrazid.weapons.*;

public class NativeWeaponSceneTest {
    @BeforeClass public static void boot(){NativeWeaponsTest.boot();}
    private static Set<String> names(List<NativeWeaponScene.Draw> parts){
        Set<String> names=new HashSet<>();assertFalse(parts.isEmpty());
        for(NativeWeaponScene.Draw part:parts){
            names.add(part.mesh);assertTrue(part.mesh,NativeWeaponVisuals.meshes().contains(part.mesh));
            assertEquals(16,part.matrix.length);for(float v:part.matrix)assertTrue(Float.isFinite(v));assertEquals(1,part.matrix[15],.00001F);
        }
        return names;
    }
    private static NativeWeaponScene.Draw part(List<NativeWeaponScene.Draw> parts,String mesh){for(NativeWeaponScene.Draw p:parts)if(p.mesh.equals(mesh))return p;throw new AssertionError(mesh);}
    @Test public void allSevenClosedNativeSceneAssembliesAreActuallyEnabled(){
        assertEquals(new HashSet<>(Arrays.asList("AK","AKSU","AK2012","Revolver","Rpk","Saiga","Ubludok")),NativeWeaponScene.guns());
        assertTrue(NativeWeaponVisuals.guns().containsAll(NativeWeaponScene.guns()));
    }
    private static void combinations(String source,String[][] options,int slot,NativeWeaponVisuals.Equipment e){
        if(slot<5){
            e.slots[slot]="";combinations(source,options,slot+1,e);
            for(String id:options[slot]){e.slots[slot]=id;combinations(source,options,slot+1,e);}e.slots[slot]="";return;
        }
        for(boolean magazine:new boolean[]{false,true})for(boolean big:new boolean[]{false,true})for(int action=0;action<=4;action++){
            e.magazine=magazine;e.big=big;e.action=action;e.phase=.75F;e.rounds=100;
            names(NativeWeaponScene.draw(source,e));
        }
    }
    @Test public void allSourceSocketCombinationsAndActionBranchesHaveBalancedFiniteMatrices()throws Exception{
        try(Reader r=new InputStreamReader(getClass().getResourceAsStream("/assets/metroport/blackrazid_module_fittings.json"),StandardCharsets.UTF_8)){
            Map<String,String[][]> fittings=new Gson().fromJson(r,new TypeToken<Map<String,String[][]>>(){}.getType());
            for(String source:NativeWeaponScene.guns())combinations(source,fittings.get(source),0,new NativeWeaponVisuals.Equipment());
        }
    }
    @Test public void actualBigMagazineIsUsedEvenWhenLegacyBooleanClaimsTheOpposite()throws Exception{
        EntityPlayer p=player(false);ItemStack gun=new ItemStack(item("ak"));hold(p,gun);p.inventory.setInventorySlotContents(1,magazine("rpk_ammo",40));assertTrue(gun("ak").insertMagazine(gun,p));
        gun.getTagCompound().setBoolean("BigAmmo",false);NBTTagCompound before=gun.getTagCompound().copy();
        NativeWeaponVisuals.Equipment e=NativeWeaponVisuals.equipment(gun,p);assertTrue(e.big);assertTrue(e.magazine);
        Set<String> n=names(NativeWeaponScene.draw("AK",e));assertTrue(n.contains("rpk/mag.obj"));assertFalse(n.contains("ak/mag.obj"));assertEquals(before,gun.getTagCompound());
        e.big=false;n=names(NativeWeaponScene.draw("AK",e));assertTrue(n.contains("ak/mag.obj"));assertFalse(n.contains("rpk/mag.obj"));
    }
    @Test public void ubludokNativeMagazineTranslationAndMatrixRepairDoNotMoveItsStock(){
        NativeWeaponVisuals.Equipment e=new NativeWeaponVisuals.Equipment();e.magazine=true;e.rounds=30;
        List<NativeWeaponScene.Draw> p=NativeWeaponScene.draw("Ubludok",e);
        assertEquals(.14F,part(p,"ubludok/mag.obj").matrix[12],.00001F);assertEquals(0,part(p,"ubludok/butt1.obj").matrix[12],.00001F);
        e.rounds=10;p=NativeWeaponScene.draw("Ubludok",e);assertEquals(.04F,part(p,"ubludok/mag.obj").matrix[12],.00001F);
        assertEquals(0,part(p,"ubludok/butt1.obj").matrix[12],.00001F);
        e.magazine=false;assertFalse(names(NativeWeaponScene.draw("Ubludok",e)).contains("ubludok/mag.obj"));
    }
    @Test public void sourceScopeAndSilencerOffsetsRemainOrderedRatherThanInventedSharedMounts(){
        NativeWeaponVisuals.Equipment e=new NativeWeaponVisuals.Equipment();e.slots[0]="OpticaSmall";e.slots[1]="Laser";e.slots[2]="Glushak";
        List<NativeWeaponScene.Draw> parts=NativeWeaponScene.draw("Revolver",e);
        NativeWeaponScene.Draw scope=part(parts,"rpk/optica.obj");assertEquals(.145F,scope.matrix[13],.00001F);assertEquals(.065F,scope.matrix[14],.00001F);
        NativeWeaponScene.Draw silencer=part(parts,"saiga/glushak.obj");assertEquals(.095F,silencer.matrix[13],.00001F);assertEquals(-.3F,silencer.matrix[14],.00001F);
        e.slots[3]="Butt";Set<String> n=names(NativeWeaponScene.draw("Revolver",e));assertTrue(n.contains("revolver/revolver_butt.obj"));assertFalse(n.contains("revolver/revolver.obj"));
    }
    @Test public void corruptRoundCountsCannotOverflowMatricesOrCreateUnboundedShellInstances(){
        for(String source:NativeWeaponScene.guns())for(int count:new int[]{Integer.MIN_VALUE,0,1,40,Integer.MAX_VALUE}){
            NativeWeaponVisuals.Equipment e=new NativeWeaponVisuals.Equipment();e.magazine=true;e.rounds=count;
            List<NativeWeaponScene.Draw> parts=NativeWeaponScene.draw(source,e);names(parts);assertTrue(parts.size()<80);
        }
    }
    @Test public void sceneResultsAreDetachedAndUnsupportedGunsFailExplicitly(){
        NativeWeaponVisuals.Equipment e=new NativeWeaponVisuals.Equipment();List<NativeWeaponScene.Draw> first=NativeWeaponScene.draw("AK",e);
        first.get(0).matrix[0]=999;assertEquals(1,NativeWeaponScene.draw("AK",e).get(0).matrix[0],0);
        try{first.clear();fail();}catch(UnsupportedOperationException expected){}
        try{NativeWeaponScene.draw("Tihar",e);fail();}catch(IllegalArgumentException expected){}
    }
}
