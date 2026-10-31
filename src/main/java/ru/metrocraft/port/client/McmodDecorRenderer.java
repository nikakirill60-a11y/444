package ru.metrocraft.port.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.metrocraft.port.mcmod.McmodObjMesh;
import ru.metrocraft.port.mcmod.decor.*;
import java.util.*;
import java.io.InputStream;

/** Shared tessellated display-list cache, matching the original renderer's immutable OBJ meshes. */
public final class McmodDecorRenderer extends TileEntitySpecialRenderer<McmodDecorTile> {
    private static final Map<String,Integer> LISTS=new HashMap<>();
    public static void mesh(McmodDecorContent.Entry entry) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("metroport:textures/mcmod/decor/"+entry.sourceId+".png"));
        int list=LISTS.computeIfAbsent(entry.sourceId,key->{
            ResourceLocation path=new ResourceLocation("metroport:models/mcmod/decor/"+key+".obj");
            McmodObjMesh obj;
            try(InputStream in=Minecraft.getMinecraft().getResourceManager().getResource(path).getInputStream()) {obj=McmodObjMesh.read(in);}
            catch(Exception ex) {throw new IllegalStateException("Cannot read decoration "+path,ex);}
            int id=GLAllocation.generateDisplayLists(1);GlStateManager.glNewList(id,GL11.GL_COMPILE);
            BufferBuilder b=Tessellator.getInstance().getBuffer();b.begin(GL11.GL_TRIANGLES,DefaultVertexFormats.POSITION_TEX_NORMAL);
            for(List<float[]> group:obj.groups.values())for(float[] v:group)b.pos(v[0],v[1],v[2]).tex(v[3],v[4]).normal(v[5],v[6],v[7]).endVertex();
            Tessellator.getInstance().draw();GlStateManager.glEndList();return id;
        });
        GlStateManager.callList(list);
    }
    @Override public void render(McmodDecorTile tile,double x,double y,double z,float partial,int destroyStage,float alpha) {
        if(!(tile.getBlockType() instanceof McmodDecorBlock))return;
        McmodDecorContent.Entry entry=((McmodDecorBlock)tile.getBlockType()).entry;
        GlStateManager.pushMatrix();GlStateManager.disableCull();GlStateManager.enableRescaleNormal();
        try {
            GlStateManager.translate(x+.5,y,z+.5);GlStateManager.rotate(DecorPose.angle(tile.getBlockMetadata()),0,1,0);
            GlStateManager.scale(entry.scale,entry.scale,entry.scale);GlStateManager.color(1,1,1,alpha);mesh(entry);
        } finally {GlStateManager.color(1,1,1,1);GlStateManager.enableCull();GlStateManager.disableRescaleNormal();GlStateManager.popMatrix();}
    }
}
