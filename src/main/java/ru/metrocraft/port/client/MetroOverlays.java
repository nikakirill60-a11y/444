package ru.metrocraft.port.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.metromod.OverlayRules;
import java.nio.FloatBuffer;

/** Compatibility adaptation using original PNGs, not a claim to run the donor's GLSL 330 framebuffer pipeline. */
@Mod.EventBusSubscriber(modid=MetroPort.ID,value=Side.CLIENT)
public final class MetroOverlays {
    private static final ResourceLocation MASK=texture("mask_clean"),GOGGLES=texture("nvg_goggles"),NOISE=texture("nvg_noise");
    private static boolean masks=true,goggles=true,noise=true,tint=true;
    private static float maskOpacity=1F,noiseStrength=.035F;
    private MetroOverlays() {}
    private static ResourceLocation texture(String id){return new ResourceLocation(MetroPort.ID,"textures/metromod/ui/"+id+".png");}
    public static void configure(Configuration config,boolean enabledTint){
        tint=enabledTint;
        maskOpacity=ru.metrocraft.port.metromod.VisualOptions.bounded(config.getFloat("maskOpacity","client",1,0,1,"Mask opacity, client visual only."),1,1);
        noiseStrength=ru.metrocraft.port.metromod.VisualOptions.bounded(config.getFloat("nvgNoiseStrength","client",.035F,0,.2F,"NVG noise opacity, client visual only."),.035F,.2F);
        masks=config.getBoolean("maskOverlay","client",true,"Original clean mask PNG in first person; no invented damage stages.");
        goggles=config.getBoolean("nvgTextureOverlay","client",true,"Original goggles texture using compatibility blending, not the donor GLSL shader.");
        noise=config.getBoolean("nvgNoise","client",true,"Subtle animated original NVG noise texture; only while NVG is on.");
    }
    private static void quad(ResourceLocation resource,int width,int height,float u,float v){
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
        Tessellator tess=Tessellator.getInstance();BufferBuilder b=tess.getBuffer();
        b.begin(GL11.GL_QUADS,DefaultVertexFormats.POSITION_TEX);
        b.pos(0,height,0).tex(u,v+1).endVertex();b.pos(width,height,0).tex(u+1,v+1).endVertex();
        b.pos(width,0,0).tex(u+1,v).endVertex();b.pos(0,0,0).tex(u,v).endVertex();tess.draw();
    }
    @SubscribeEvent public static void overlay(RenderGameOverlayEvent.Pre event){
        if(event.getType()!=RenderGameOverlayEvent.ElementType.ALL)return;
        Minecraft mc=Minecraft.getMinecraft();
        if(mc.player==null || !OverlayRules.visible(mc.world!=null,mc.gameSettings.thirdPersonView==0,mc.player.isEntityAlive(),
                mc.player.isSpectator(),mc.gameSettings.hideGUI,mc.currentScreen!=null))return;
        boolean nvg=NvgEffects.visualActive();
        boolean mask=masks && OverlayRules.mask(mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD),mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
        if(!mask && !(nvg && (goggles||noise||tint)))return;
        int w=event.getResolution().getScaledWidth(),h=event.getResolution().getScaledHeight();
        // Snapshot actual GL state and restore through GlStateManager to keep its cache coherent.
        boolean oldBlend=GL11.glIsEnabled(GL11.GL_BLEND),oldAlpha=GL11.glIsEnabled(GL11.GL_ALPHA_TEST),
                oldDepth=GL11.glIsEnabled(GL11.GL_DEPTH_TEST),oldTexture=GL11.glIsEnabled(GL11.GL_TEXTURE_2D),
                depthMask=GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        int binding=GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D),src=GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB),
                dst=GL11.glGetInteger(GL14.GL_BLEND_DST_RGB),srcAlpha=GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA),dstAlpha=GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
        FloatBuffer color=GlStateBuffers.colorQuery();GL11.glGetFloat(GL11.GL_CURRENT_COLOR,color);
        GlStateManager.pushMatrix();
        try{
            GlStateManager.disableDepth();GlStateManager.depthMask(false);GlStateManager.disableAlpha();GlStateManager.enableTexture2D();GlStateManager.enableBlend();
            if(nvg){
                if(tint)net.minecraft.client.gui.Gui.drawRect(0,0,w,h,0x1430C060);
                GlStateManager.enableBlend();GlStateManager.color(1,1,1,1);
                if(goggles){
                    // Original google_tex is grayscale without alpha: multiply, don't paint a white rectangle.
                    GlStateManager.tryBlendFuncSeparate(GL11.GL_ZERO,GL11.GL_SRC_COLOR,GL11.GL_ZERO,GL11.GL_ONE);quad(GOGGLES,w,h,0,0);
                }
                if(noise){
                    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA,GL11.GL_ZERO,GL11.GL_ONE);
                    GlStateManager.color(.65F,1,.65F,noiseStrength);quad(NOISE,w,h,OverlayRules.noiseU(mc.player.ticksExisted),OverlayRules.noiseV(mc.player.ticksExisted));
                }
            }
            if(mask){
                GlStateManager.color(1,1,1,maskOpacity);GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA,GL11.GL_ZERO,GL11.GL_ONE);
                quad(MASK,w,h,0,0);
            }
        }finally{
            GlStateManager.bindTexture(binding);GlStateManager.color(color.get(0),color.get(1),color.get(2),color.get(3));
            GlStateManager.tryBlendFuncSeparate(src,dst,srcAlpha,dstAlpha);GlStateManager.depthMask(depthMask);
            if(oldBlend)GlStateManager.enableBlend();else GlStateManager.disableBlend();
            if(oldAlpha)GlStateManager.enableAlpha();else GlStateManager.disableAlpha();
            if(oldDepth)GlStateManager.enableDepth();else GlStateManager.disableDepth();
            if(oldTexture)GlStateManager.enableTexture2D();else GlStateManager.disableTexture2D();
            GlStateManager.popMatrix();
        }
    }
}
