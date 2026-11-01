package ru.metrocraft.port.blackrazid;

import java.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.*;
import static org.junit.Assert.*;
import static ru.metrocraft.port.blackrazid.NativeWeaponsTest.*;
import ru.metrocraft.port.blackrazid.weapons.*;

/** Pure plans and actual stack/inventory state, without a client or OpenGL context. */
public class NativeSpecialWeaponVisualsTest {
    @BeforeClass public static void boot(){NativeWeaponsTest.boot();}
    private static Set<String> names(List<NativeWeaponVisuals.Part> parts){
        Set<String> out=new HashSet<>();for(NativeWeaponVisuals.Part p:parts){out.add(p.mesh);assertTrue(NativeWeaponVisuals.meshes().contains(p.mesh));for(float v:p.translation)assertTrue(Float.isFinite(v));assertTrue(Float.isFinite(p.angle));}return out;
    }
    private static NativeWeaponVisuals.Part part(List<NativeWeaponVisuals.Part> parts,String name){for(NativeWeaponVisuals.Part p:parts)if(p.mesh.equals(name))return p;throw new AssertionError(name);}
    @Test public void independentProfilesHaveRealBodiesAndFiniteHandSpaceTransforms(){
        assertEquals(new HashSet<>(Arrays.asList("Gatling","Abzac","Vyhlop","Velopal","Ognemet")),NativeWeaponVisuals.guns());
        for(String source:NativeWeaponVisuals.guns()){
            NativeWeaponVisuals.Gun profile=NativeWeaponVisuals.gun(source);assertEquals(source,profile.sourceId);assertTrue(NativeWeaponVisuals.meshes().contains(profile.body));assertTrue(profile.nativeScale>0);assertEquals(3,profile.nativeTranslation.length);
            for(float v:profile.nativeTranslation)assertTrue(Float.isFinite(v));
        }
        assertNull(NativeWeaponVisuals.gun("Revolver"));
    }
    @Test public void abzacEquipmentMatrixSelectsActualMagazineAndOnlyNativeBranches(){
        for(boolean first:new boolean[]{false,true})for(int bits=0;bits<32;bits++){
            NativeWeaponVisuals.Equipment e=new NativeWeaponVisuals.Equipment();e.magazine=(bits&1)!=0;e.big=(bits&2)!=0;e.automatic=(bits&4)!=0;e.silenced=(bits&8)!=0;e.laser=(bits&16)!=0;e.rounds=75;
            List<NativeWeaponVisuals.Part> parts=NativeWeaponVisuals.special("Abzac",e,first);Set<String> n=names(parts);
            assertTrue(n.contains("abzac/abzac.obj"));assertTrue(n.contains("abzac/zatvor.obj"));assertEquals(e.magazine && e.big,n.contains("abzac/magbig.obj"));assertEquals(e.magazine && !e.big,n.contains("abzac/mag.obj"));
            assertEquals(e.automatic,n.contains("abzac/autofire.obj"));assertEquals(e.silenced,n.contains("abzac/compens.obj"));assertEquals(e.laser,n.contains("rpk/laser.obj"));
            if(e.laser)assertArrayEquals(first?new float[]{.01F,.04F,.03F}:new float[]{0,0,0},part(parts,"rpk/laser.obj").translation,0);
            assertArrayEquals(first?new float[]{0,.074F,.093F}:new float[]{0,0,0},part(parts,"abzac/zatvor.obj").translation,0);
        }
    }
    @Test public void exposedAbzacShellsAreBoundedAndNotInventedDuringTransfer(){
        NativeWeaponVisuals.Equipment e=new NativeWeaponVisuals.Equipment();e.magazine=true;
        for(int amount:new int[]{Integer.MIN_VALUE,-1,0,1,4,5,100,Integer.MAX_VALUE}){
            e.rounds=amount;List<NativeWeaponVisuals.Part> parts=NativeWeaponVisuals.special("Abzac",e,true);int shells=0;
            for(NativeWeaponVisuals.Part p:parts)if(p.mesh.equals("abzac/bullet.obj"))shells++;
            assertEquals(Math.min(5,Math.max(0,amount)),shells);
        }
        for(int action=1;action<=4;action++){e.action=action;assertFalse(names(NativeWeaponVisuals.special("Abzac",e,true)).contains("abzac/bullet.obj"));}
        e.action=0;assertFalse(names(NativeWeaponVisuals.special("Abzac",e,false)).contains("abzac/bullet.obj"));
        e.magazine=false;assertFalse(names(NativeWeaponVisuals.special("Abzac",e,true)).contains("abzac/bullet.obj"));
    }
    @Test public void vyhlopOpticReplacesNativeSightAndKeepsTheTwoExactScopeOffsets(){
        for(boolean first:new boolean[]{false,true})for(boolean optic:new boolean[]{false,true}){
            NativeWeaponVisuals.Equipment e=new NativeWeaponVisuals.Equipment();e.optic=optic;e.laser=true;e.magazine=true;
            List<NativeWeaponVisuals.Part> p=NativeWeaponVisuals.special("Vyhlop",e,first);Set<String> n=names(p);
            assertEquals(!optic,n.contains("vyhlop/pricel.obj"));assertEquals(optic,n.contains("railgun/optica.obj"));assertEquals(optic,n.contains("railgun/test.obj"));assertTrue(n.contains("vyhlop/mag.obj"));
            assertArrayEquals(new float[]{.01F,-.005F,-.25F},part(p,"rpk/laser.obj").translation,0);
            if(optic){assertArrayEquals(new float[]{.0025F,-.078F,.1F},part(p,"railgun/optica.obj").translation,0);assertArrayEquals(new float[]{.0025F,-.078F,.34F},part(p,"railgun/test.obj").translation,0);}
            e.magazine=false;assertFalse(names(NativeWeaponVisuals.special("Vyhlop",e,first)).contains("vyhlop/mag.obj"));
        }
    }
    @Test public void velopalDoesNotAcquireImaginaryOpticsOrEmitterFromAcceptedModules(){
        NativeWeaponVisuals.Equipment e=new NativeWeaponVisuals.Equipment();e.optic=e.laser=e.magazine=e.automatic=e.silenced=true;
        for(boolean first:new boolean[]{false,true})assertEquals(Collections.singleton("vyhlop/velo.obj"),names(NativeWeaponVisuals.special("Velopal",e,first)));
    }
    @Test public void ognemetGaugeAndBoltAreFirstPersonSourceBranchesWithClampedPressure(){
        NativeWeaponVisuals.Equipment e=new NativeWeaponVisuals.Equipment();e.magazine=true;
        for(int pressure:new int[]{Integer.MIN_VALUE,0,50,180,Integer.MAX_VALUE}){
            e.pressure=pressure;List<NativeWeaponVisuals.Part> p=NativeWeaponVisuals.special("Ognemet",e,true);
            NativeWeaponVisuals.Part gauge=part(p,"ognemet/strelka.obj");assertArrayEquals(new float[]{.037F,.1F,-.002F},gauge.translation,0);assertEquals(100-Math.min(180,Math.max(0,pressure))*1.4F,gauge.angle,0);
            assertEquals(-50,part(p,"ognemet/zatvor.obj").angle,0);assertTrue(names(p).contains("ognemet/mag.obj"));
        }
        List<NativeWeaponVisuals.Part> other=NativeWeaponVisuals.special("Ognemet",e,false);assertEquals(new HashSet<>(Arrays.asList("ognemet/ognemet1.obj","ognemet/zatvor.obj")),names(other));assertEquals(0,part(other,"ognemet/zatvor.obj").angle,0);
        e.magazine=false;assertFalse(names(NativeWeaponVisuals.special("Ognemet",e,true)).contains("ognemet/mag.obj"));
    }
    @Test public void bigMagazineSnapshotUsesLoadedItemNotUntrustedLegacyDisplayBooleans()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("abzac"));hold(p,s);p.inventory.setInventorySlotContents(1,magazine("abzac_ammo_big",40));assertTrue(gun("abzac").insertMagazine(s,p));
        s.getTagCompound().setBoolean("BigAmmo",false);NBTTagCompound before=s.getTagCompound().copy();
        NativeWeaponVisuals.Equipment e=NativeWeaponVisuals.equipment(s,p);assertTrue(e.magazine);assertTrue(e.big);assertEquals(40,e.rounds);assertEquals(before,s.getTagCompound());
        ItemStack detached=gun("abzac").visualMagazine(s,p);mag("abzac_ammo_big").rounds(detached,0);assertEquals(40,gun("abzac").ammo(s));
    }
    @Test public void incomingMagazinePreviewIsDetachedReadOnlyAndDoesNotPeekForUnrelatedStacks()throws Exception{
        EntityPlayer p=player(false);ItemStack s=new ItemStack(item("abzac"));hold(p,s);ItemStack candidate=magazine("abzac_ammo_big",23);p.inventory.setInventorySlotContents(1,candidate);
        assertTrue(gun("abzac").visualMagazine(s,p).isEmpty());assertFalse(s.hasTagCompound());
        NBTTagCompound tag=new NBTTagCompound();tag.setInteger("BlackrazidAction",1);tag.setInteger("BlackrazidActionTicks",5);s.setTagCompound(tag);NBTTagCompound before=tag.copy(),ammoBefore=candidate.writeToNBT(new NBTTagCompound());
        for(int i=0;i<100;i++){assertTrue(NativeWeaponVisuals.equipment(s,p).big);ItemStack copy=gun("abzac").visualMagazine(s,p);mag("abzac_ammo_big").rounds(copy,0);assertNotSame(candidate,copy);}
        assertEquals(before,s.getTagCompound());assertEquals(ammoBefore,candidate.writeToNBT(new NBTTagCompound()));assertSame(candidate,p.inventory.getStackInSlot(1));
        assertTrue(gun("abzac").visualMagazine(s.copy(),p).isEmpty());assertTrue(gun("abzac").visualMagazine(s,null).isEmpty());
        tag.setInteger("BlackrazidAction",2);assertTrue(gun("abzac").visualMagazine(s,p).isEmpty());
        assertTrue(gun("ak").visualMagazine(s,p).isEmpty());
    }
    @Test public void unsupportedAssemblyFailsExplicitlyAndPlansCannotModifySharedGeometry(){
        NativeWeaponVisuals.Equipment e=new NativeWeaponVisuals.Equipment();List<NativeWeaponVisuals.Part> p=NativeWeaponVisuals.special("Velopal",e,true);
        try{p.clear();fail();}catch(UnsupportedOperationException expected){}
        p.get(0).translation[0]=999;assertEquals(0,NativeWeaponVisuals.special("Velopal",e,true).get(0).translation[0],0);
        try{NativeWeaponVisuals.special("AK",e,true);fail();}catch(IllegalArgumentException expected){}
    }
}
