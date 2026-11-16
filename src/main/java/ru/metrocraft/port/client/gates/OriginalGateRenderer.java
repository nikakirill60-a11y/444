package ru.metrocraft.port.client.gates;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.metrocraft.port.mcmod.McmodObjMesh;
import ru.metrocraft.port.metromod.decor.*;
import java.io.InputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

/** Original multi-material meshes. Each submesh has its own material, never one guessed atlas. */
public final class OriginalGateRenderer extends TileEntitySpecialRenderer<OriginalGateTile> {
    private static final Map<String,Map<String,Integer>> LISTS=new HashMap<>();
    // All draws run on the render thread; avoid a direct/native allocation for
    // every part of every visible lamp/door on every frame.
    private static final java.nio.FloatBuffer MATRIX=org.lwjgl.BufferUtils.createFloatBuffer(16);
    public static void reload() {
        for(Map<String,Integer> mesh:LISTS.values())for(int list:mesh.values())GLAllocation.deleteDisplayLists(list);
        LISTS.clear();
    }
    private static Map<String,Integer> mesh(String name) {
        return LISTS.computeIfAbsent(name,key->{
            McmodObjMesh obj;
            try(InputStream in=new GZIPInputStream(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(key)).getInputStream())) {
                obj=McmodObjMesh.readMaterials(in);
            } catch(Exception e) {throw new IllegalStateException("Cannot load original METRO_MOD gate "+key,e);}
            Map<String,Integer> result=new LinkedHashMap<>();
            for(Map.Entry<String,List<float[]>> group:obj.groups.entrySet()) {
                int list=GLAllocation.generateDisplayLists(1);GlStateManager.glNewList(list,GL11.GL_COMPILE);
                BufferBuilder b=Tessellator.getInstance().getBuffer();b.begin(GL11.GL_TRIANGLES,DefaultVertexFormats.POSITION_TEX_NORMAL);
                for(float[] v:group.getValue())b.pos(v[0],v[1],v[2]).tex(v[3],v[4]).normal(v[5],v[6],v[7]).endVertex();
                Tessellator.getInstance().draw();GlStateManager.glEndList();result.put(group.getKey(),list);
            }
            return result;
        });
    }
    public static void draw(OriginalGateProfile profile,float progress) {
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(profile.offset[0],profile.ground+profile.offset[1],profile.offset[2]);
            for(OriginalGateProfile.Part part:profile.parts) {
                GlStateManager.pushMatrix();
                try {
                    double[] matrix=part.matrix(progress);
                    MATRIX.clear();
                    for(int col=0;col<4;col++)for(int row=0;row<4;row++)MATRIX.put((float)matrix[row*4+col]);
                    MATRIX.flip();GlStateManager.multMatrix(MATRIX);
                    for(Map.Entry<String,Integer> group:mesh(part.mesh).entrySet()) {
                        String texture=part.materials.get(group.getKey());
                        if(texture==null)throw new IllegalStateException("Missing original gate material "+group.getKey());
                        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(texture));
                        GlStateManager.callList(group.getValue());
                    }
                } finally {GlStateManager.popMatrix();}
            }
        } finally {GlStateManager.popMatrix();}
    }
    @Override public void render(OriginalGateTile tile,double x,double y,double z,float partial,int destroyStage,float alpha) {
        if(!tile.ready || tile.owner==null)return;
        GlStateManager.pushMatrix();GlStateManager.disableCull();GlStateManager.enableRescaleNormal();
        try {
            GlStateManager.translate(x+.5,y,z+.5);GlStateManager.rotate(-tile.facing.getHorizontalAngle(),0,1,0);
            GlStateManager.color(1,1,1,alpha);
            draw(tile.profile,tile.previous+(tile.progress-tile.previous)*partial);
        } finally {GlStateManager.color(1,1,1,1);GlStateManager.disableRescaleNormal();GlStateManager.enableCull();GlStateManager.popMatrix();}
    }
    @Override public boolean isGlobalRenderer(OriginalGateTile tile) { return true; }
}
