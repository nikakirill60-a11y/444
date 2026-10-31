package ru.metrocraft.port.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.metrocraft.port.mcmod.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Original six OBJ groups follow vanilla biped joints, using recovered MCMOD transforms. */
public final class McmodArmorModel extends ModelBiped {
    private static final Map<String, McmodArmorModel> MODELS = new HashMap<>();
    private static final Map<String, McmodObjMesh> MESHES = new HashMap<>();
    private static final java.nio.FloatBuffer MATRIX=org.lwjgl.BufferUtils.createFloatBuffer(16);
    private final McmodArmorEntry entry;
    private McmodArmorModel(McmodArmorEntry entry) { super(0); this.entry=entry; }
    public static McmodArmorModel get(McmodArmorEntry entry) { return MODELS.computeIfAbsent(entry.id, key -> new McmodArmorModel(entry)); }
    private McmodObjMesh mesh() {
        return MESHES.computeIfAbsent(entry.model, key -> {
            ResourceLocation file = new ResourceLocation("metroport:models/mcmod/"+key+"/model.obj");
            try (Reader reader = new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(file).getInputStream(), StandardCharsets.UTF_8)) {
                return McmodObjMesh.read(reader);
            } catch (Exception e) { throw new IllegalStateException("Cannot load MCMOD armor " + file, e); }
        });
    }
    @Override public void render(Entity entity, float limbSwing, float amount, float age, float yaw, float pitch, float scale) {
        setRotationAngles(limbSwing,amount,age,yaw,pitch,scale,entity);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ZERO);
        try {
            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("metroport:textures/mcmod/armor/"+entry.model+".png"));
            if (isSneak) GlStateManager.translate(0,.2,0);
            boolean separateHead = entity instanceof EntityLivingBase && !((EntityLivingBase)entity).getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty();
            if (entry.helmet() || (entry.whole() && !separateHead)) {
                GlStateManager.pushMatrix();
                if (isChild) { GlStateManager.scale(.75F,.75F,.75F); GlStateManager.translate(0,16*scale,0); }
                part(bipedHead,"head",scale);
                GlStateManager.popMatrix();
            }
            if (!entry.helmet()) {
                GlStateManager.pushMatrix();
                if (isChild) { GlStateManager.scale(.5F,.5F,.5F); GlStateManager.translate(0,24*scale,0); }
                part(bipedBody,"body",scale);
                part(bipedRightArm,"rightarm",scale);
                part(bipedLeftArm,"leftarm",scale);
                part(bipedRightLeg,"rightleg",scale);
                part(bipedLeftLeg,"leftleg",scale);
                GlStateManager.popMatrix();
            }
        } finally { GlStateManager.disableBlend(); GlStateManager.popMatrix(); }
    }
    private void part(ModelRenderer joint, String group, float scale) {
        List<float[]> vertices = mesh().groups.get(group);
        if (vertices==null || vertices.isEmpty()) return;
        GlStateManager.pushMatrix();
        javax.vecmath.Matrix4f matrix=McmodArmorPose.posed(group,scale,
                joint.rotationPointX,joint.rotationPointY,joint.rotationPointZ,
                joint.rotateAngleX,joint.rotateAngleY,joint.rotateAngleZ,joint.offsetX,joint.offsetY,joint.offsetZ);
        MATRIX.clear();
        for (int column=0;column<4;column++) for (int row=0;row<4;row++) MATRIX.put(matrix.getElement(row,column));
        MATRIX.flip();
        GlStateManager.multMatrix(MATRIX);
        BufferBuilder buffer=Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_TRIANGLES,DefaultVertexFormats.POSITION_TEX_NORMAL);
        for (float[] v : vertices) buffer.pos(v[0],v[1],v[2]).tex(v[3],v[4]).normal(v[5],v[6],v[7]).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.popMatrix();
    }
}
