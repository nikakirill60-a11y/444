package ru.metrocraft.port.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.metrocraft.port.mcmod.*;
import java.io.InputStream;
import java.util.*;

/** Original base meshes and atlases; actual Steve/Alex hands share the established AK camera rig. */
public final class McmodAkRenderer extends TileEntityItemStackRenderer {
    public static final McmodAkRenderer INSTANCE=new McmodAkRenderer();
    private static final Map<String,Integer> LISTS=new HashMap<>();
    private static McmodObjMesh mesh;
    private static void group(String name){
        if(mesh==null)try(InputStream in=Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("metroport:models/mcmod/guns/ak74.obj")).getInputStream()){
            mesh=McmodObjMesh.read(in);
        }catch(Exception e){throw new IllegalStateException("Cannot read original AK-74 mesh",e);}
        int list=LISTS.computeIfAbsent(name,key->{
            List<float[]> vertices=mesh.groups.get(key);if(vertices==null)throw new IllegalStateException("AK group missing: "+key);
            int id=GLAllocation.generateDisplayLists(1);GlStateManager.glNewList(id,GL11.GL_COMPILE);
            BufferBuilder b=Tessellator.getInstance().getBuffer();b.begin(GL11.GL_TRIANGLES,DefaultVertexFormats.POSITION_TEX_NORMAL);
            for(float[] v:vertices)b.pos(v[0],v[1],v[2]).tex(v[3],v[4]).normal(v[5],v[6],v[7]).endVertex();
            Tessellator.getInstance().draw();GlStateManager.glEndList();return id;
        });
        GlStateManager.pushMatrix();
        try{GlStateManager.translate(McmodAkPose.X,McmodAkPose.Y,McmodAkPose.Z);GlStateManager.rotate(-90,0,1,0);
            GlStateManager.scale(McmodAkPose.SCALE,McmodAkPose.SCALE,McmodAkPose.SCALE);GlStateManager.callList(list);
        }finally{GlStateManager.popMatrix();}
    }
    private static void texture(String suffix){Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("metroport:textures/mcmod/guns/ak74"+suffix+".png"));}
    @Override public void renderByItem(ItemStack stack,float partial){
        Minecraft mc=Minecraft.getMinecraft();TransformType type=LegacyItemModel.context();
        boolean first=type==TransformType.FIRST_PERSON_LEFT_HAND || type==TransformType.FIRST_PERSON_RIGHT_HAND;
        double now=mc.world==null?0:mc.world.getTotalWorldTime()+partial;float kick=0,reload=0;
        if(stack.hasTagCompound()){
            double since=now-stack.getTagCompound().getLong("MetroLastShot");
            if(stack.getTagCompound().hasKey("MetroLastShot") && since>=0 && since<4)kick=(float)(1-since/4);
            int duration=stack.getTagCompound().getInteger("MetroReloadDuration");
            if(duration>0 && stack.getTagCompound().getInteger("MetroReloadTicks")>0){
                float p=(float)((now-stack.getTagCompound().getLong("MetroReloadStart"))/duration);
                reload=Math.max(0,Math.min(1,Math.min(p/.25F,(1-p)/.25F)));
            }
        }
        GlStateManager.pushMatrix();GlStateManager.disableCull();GlStateManager.enableRescaleNormal();
        try{
            GlStateManager.translate(.5,.5,.5);GlStateManager.translate(-.035*kick,0,0);GlStateManager.color(1,1,1,1);
            texture(first?"":"_low");group("body");
            GlStateManager.pushMatrix();try{GlStateManager.translate(0,-.14*reload,0);GlStateManager.rotate(-12*reload,0,0,1);group("mag");}finally{GlStateManager.popMatrix();}
            if(first){
                GlStateManager.pushMatrix();try{GlStateManager.translate(-.045*kick,0,0);group("zatv");}finally{GlStateManager.popMatrix();}
                GunHands.render("ak74",type,reload,McmodAkPose.handAnchor(false),McmodAkPose.handAnchor(true));
                if(kick>.5F){
                    texture("_flash");GlStateManager.enableBlend();GlStateManager.blendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);GlStateManager.disableLighting();
                    try{group("fire1");}finally{GlStateManager.enableLighting();GlStateManager.disableBlend();}
                }
            }
        }finally{GlStateManager.color(1,1,1,1);GlStateManager.enableCull();GlStateManager.disableRescaleNormal();GlStateManager.popMatrix();}
    }
}
