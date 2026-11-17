package ru.metrocraft.port.client.menu;

import java.io.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.metrocraft.port.mcmod.McmodObjMesh;

/** Original decoded drop/glow textures and byte-identical donor quad geometry. */
final class OriginalMenuEffects {
    private static final ResourceLocation DROPS=new ResourceLocation("metroport","textures/metromod/menu_effects/tunnel_kapli.png");
    private static final ResourceLocation GLOW=new ResourceLocation("metroport","textures/metromod/menu_effects/fx_glowlamp_small.png");
    private final McmodObjMesh quad;
    OriginalMenuEffects() throws IOException {
        try(InputStream in=Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("metroport","models/metromod/menu_effects/quad.obj")).getInputStream()) {
            quad=McmodObjMesh.read(in);
        }
    }
    void drops(OriginalMenuState state) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(DROPS);
        GlStateManager.disableBlend();GlStateManager.color(1,1,1,1);
        try {
            for(float[] point:state.particles) {
                GlStateManager.pushMatrix();
                try {
                    GlStateManager.translate(point[0],.5f,-point[1]);GlStateManager.scale(1,2.5f,1);
                    GlStateManager.rotate(state.yaw,0,1,0);
                    float offset=state.time*-.007f;
                    BufferBuilder b=Tessellator.getInstance().getBuffer();b.begin(GL11.GL_QUADS,DefaultVertexFormats.POSITION_TEX);
                    b.pos(0,3,1).tex(0,-.4f+offset).endVertex();b.pos(1,3,1).tex(1,-.4f+offset).endVertex();
                    b.pos(1,0,1).tex(1,offset).endVertex();b.pos(0,0,1).tex(0,offset).endVertex();Tessellator.getInstance().draw();
                } finally {GlStateManager.popMatrix();}
            }
        } finally {GlStateManager.enableBlend();}
    }
    void glows(OriginalMenuState s) {
        GlStateManager.disableLighting();GlStateManager.alphaFunc(GL11.GL_GREATER,0);GlStateManager.depthMask(false);
        Minecraft.getMinecraft().getTextureManager().bindTexture(GLOW);
        try {
            glow(s,-.452f,2.370f,.45f,3,s.lightB,s.lightG/3.3f,s.lightR/5);
            if(s.selected>0) {
                float[][] p={{0,0,0},{.32f,.34f,.65f},{.033f,.352f,.65f},{-.065f,.325f,.65f},{-.305f,.335f,.645f}};
                float[] at=p[s.selected];glow(s,at[0],at[1]+1.9f,at[2]-.2f,5,s.lightB,s.lightG/2.5f,s.lightR/5);
            }
        } finally {GlStateManager.enableLighting();GlStateManager.alphaFunc(GL11.GL_GREATER,.1f);GlStateManager.depthMask(true);GlStateManager.color(1,1,1,1);}
    }
    private void glow(OriginalMenuState s,float x,float y,float z,float size,float r,float g,float blue) {
        GlStateManager.pushMatrix();
        try {
            GlStateManager.color(r,g,blue,.9f);GlStateManager.translate(x,y,z);
            GlStateManager.rotate(90+s.pitch,1,0,0);GlStateManager.rotate(s.yaw,0,1,0);GlStateManager.scale(size,size,1);
            BufferBuilder b=Tessellator.getInstance().getBuffer();b.begin(GL11.GL_TRIANGLES,DefaultVertexFormats.POSITION_TEX);
            for(List<float[]> group:quad.groups.values())for(float[] v:group)b.pos(v[0],v[1],v[2]).tex(v[3],v[4]).endVertex();
            Tessellator.getInstance().draw();
        } finally {GlStateManager.popMatrix();}
    }
}
