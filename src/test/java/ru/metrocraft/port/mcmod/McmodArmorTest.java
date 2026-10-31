package ru.metrocraft.port.mcmod;

import net.minecraft.init.Bootstrap;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import org.junit.BeforeClass;
import org.junit.Test;
import javax.imageio.ImageIO;
import javax.vecmath.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.Assert.*;

/** Actual shared renderer geometry/math and native ItemArmor contracts, no GL context or game-world claims. */
public class McmodArmorTest {
    private static final Map<String,McmodArmorItem> items=new HashMap<>();
    private static final Set<String> models=new HashSet<>();
    private static InputStream resource(String path) {
        InputStream in=McmodArmorTest.class.getResourceAsStream("/assets/metroport/"+path);
        assertNotNull(path,in); return in;
    }
    @BeforeClass public static void setup() {
        Bootstrap.register(); McmodContent.load();
        for(McmodArmorEntry entry:McmodContent.armor) {
            items.put(entry.id,new McmodArmorItem(entry)); models.add(entry.model);
        }
    }
    @Test public void originalMeshesContainOnlyArticulatedPartsAndFiniteTriangles() throws Exception {
        assertEquals(6,models.size()); int total=0;
        Set<String> allowed=new HashSet<>(Arrays.asList("head","body","rightarm","leftarm","rightleg","leftleg"));
        for(String model:models) try(Reader reader=new InputStreamReader(resource("models/mcmod/"+model+"/model.obj"),StandardCharsets.UTF_8)) {
            McmodObjMesh mesh=McmodObjMesh.read(reader);
            assertTrue(model,mesh.groups.containsKey("body")); assertTrue(model,mesh.groups.containsKey("head"));
            assertTrue(allowed.containsAll(mesh.groups.keySet()));
            for(List<float[]> vertices:mesh.groups.values()) {
                assertEquals(0,vertices.size()%3); assertFalse(vertices.isEmpty());
                for(float[] v:vertices) { assertEquals(8,v.length); for(float value:v)assertTrue(Float.isFinite(value)); }
                total+=vertices.size()/3;
            }
        }
        assertTrue(total>1000);
    }
    @Test public void readerSupportsBlenderObjectsNegativeIndicesAndExplicitNormals() throws Exception {
        String obj="v 0 0 0\nv 1 0 0\nv 1 1 0\nv 0 1 0\nvt 0 .25\nvn 0 0 -1\n"+
                "o head\nf -4/-1/-1 -3/-1/-1 -2/-1/-1 -1/-1/-1 # quad\ng body\nf 1 2 3\n";
        McmodObjMesh mesh=McmodObjMesh.read(new StringReader(obj));
        assertEquals(6,mesh.groups.get("head").size()); assertEquals(3,mesh.groups.get("body").size());
        assertEquals(.75F,mesh.groups.get("head").get(0)[4],.00001F);
        assertEquals(-1F,mesh.groups.get("head").get(0)[7],.00001F);
        assertEquals(1F,mesh.groups.get("body").get(0)[7],.00001F);
    }
    @Test public void originalIconsAndAtlasesDecode() throws Exception {
        for(String model:models) for(String kind:new String[]{"armor","icons"})
            try(InputStream in=resource("textures/mcmod/"+kind+"/"+model+".png")) {
                java.awt.image.BufferedImage image=ImageIO.read(in);
                assertNotNull(image); assertTrue(image.getWidth()>0); assertTrue(image.getHeight()>0);
            }
    }
    @Test public void wholeOutfitsAreExactlyFiveSingleChestItems() {
        assertEquals(7,items.size()); int whole=0;
        for(McmodArmorItem item:items.values()) if(item.entry.whole()) {
            assertEquals(EntityEquipmentSlot.CHEST,item.armorType);
            assertEquals(1,item.getItemStackLimit());
            assertEquals(item.entry.condition,item.getMaxDamage());
            assertEquals(item.entry.sourceProtect/100D,item.getProperties(null,new ItemStack(item),DamageSource.GENERIC,10,0).AbsorbRatio,.00001);
            whole++;
        }
        assertEquals(5,whole);
    }
    @Test public void splitBkzHasSeparateSlotsWithoutDoublingProtectionOrFlanBonuses() {
        McmodArmorItem helmet=items.get("mcmod_bkz_helmet"), vest=items.get("mcmod_bkz_vest");
        assertEquals(EntityEquipmentSlot.HEAD,helmet.armorType); assertEquals(EntityEquipmentSlot.CHEST,vest.armorType);
        assertEquals(.6,helmet.entry.defence+vest.entry.defence,.00001);
        for(McmodArmorItem item:items.values()) {
            assertEquals(1,item.getDefinition().moveSpeedModifier,0);
            assertEquals(0,item.getDefinition().knockbackModifier,0);
            assertEquals(item.entry.radiation,item.isGasMask());
        }
    }
    @Test public void conditionMirrorsDurabilityWithoutDestroyingUnrelatedNbt() {
        ItemStack stack=new ItemStack(items.get("mcmod_officer_ozk"));
        NBTTagCompound tag=new NBTTagCompound(); tag.setBoolean("NoDrop",true); tag.setInteger("MetroFilterTicks",1234); stack.setTagCompound(tag);
        McmodArmorItem.syncCondition(stack); assertEquals(1500,tag.getInteger("condition"));
        stack.setItemDamage(17); McmodArmorItem.syncCondition(stack); assertEquals(1483,tag.getInteger("condition"));
        assertEquals(17,stack.getItemDamage()); assertEquals(1234,tag.getInteger("MetroFilterTicks")); assertTrue(tag.getBoolean("NoDrop"));
        stack.setItemDamage(9999); McmodArmorItem.syncCondition(stack); assertEquals(0,tag.getInteger("condition"));
    }
    @Test public void localTransformsMatchRecoveredScaleTranslateAndRotationOrder() {
        for(String group:Arrays.asList("head","body","rightarm","leftarm","rightleg","leftleg")) {
            Point3f p=new Point3f(.2F,1.6F,.3F); McmodArmorPose.local(group).transform(p);
            float tx=group.equals("rightarm")?.32F:group.equals("leftarm")?-.31F:group.equals("rightleg")?.125F:group.equals("leftleg")?-.12F:0;
            float ty=group.equals("head")?-1.5F:group.equals("body")?-1.45F:group.endsWith("arm")?-1.34F:-.7F;
            float sy=group.endsWith("arm")?1.1F:1.05F;
            assertEquals(1.05F*(tx-.3F),p.x,.00001);
            assertEquals(-sy*(1.6F+ty),p.y,.00001);
            assertEquals(-1.05F*.2F,p.z,.00001);
        }
    }
    @Test public void hiddenVanillaLegsStillUseTheirJointPivot() {
        Point3f point=new Point3f(0,.7F,0);
        McmodArmorPose.posed("rightleg",1/16F,-1.9F,12,0,0,0,0,0,0,0).transform(point);
        assertEquals(-1.9F/16+.125F*1.05F,point.x,.00001); assertEquals(.75F,point.y,.00001);
    }
    @Test public void articulatedPartsRotateAroundTheirOwnJoints() {
        float angle=(float)Math.PI/2;
        Point3f local=new Point3f(.1F,1.6F,.2F); McmodArmorPose.local("head").transform(local);
        Point3f posed=new Point3f(.1F,1.6F,.2F);
        McmodArmorPose.posed("head",1/16F,0,1,0,0,angle,0,0,0,0).transform(posed);
        assertEquals(local.z,posed.x,.00001); assertEquals(local.y+1/16F,posed.y,.00001); assertEquals(-local.x,posed.z,.00001);
    }
}
