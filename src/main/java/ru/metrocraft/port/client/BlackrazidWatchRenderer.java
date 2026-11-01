package ru.metrocraft.port.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import ru.metrocraft.port.client.blackrazid.gadgets.*;
import ru.metrocraft.port.mcmod.WatchItem;
import org.lwjgl.opengl.GL11;

/** Original Clock + hour/minute/second models. Intentionally no O2 model or dosimeter. */
public final class BlackrazidWatchRenderer extends TileEntityItemStackRenderer {
    public static final BlackrazidWatchRenderer INSTANCE=new BlackrazidWatchRenderer();
    private static final Clock BODY=new Clock();
    private static final HourArrowModel HOUR=new HourArrowModel();
    private static final MinArrowModel MINUTE=new MinArrowModel();
    private static final SekArrowModel SECOND=new SekArrowModel();
    private static void arrow(net.minecraft.client.model.ModelBase model,float degrees){
        GlStateManager.pushMatrix();
        try{GlStateManager.translate(12*.003,-3.8*.003,-2*.003);GlStateManager.rotate(degrees,0,0,1);GlStateManager.translate(-12*.003,3.8*.003,2*.003);model.render(null,0,0,0,0,0,.003F);}
        finally{GlStateManager.popMatrix();}
    }
    private static void draw(long time){
        int texture=GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);boolean cull=GL11.glIsEnabled(GL11.GL_CULL_FACE);
        java.nio.FloatBuffer color=GlStateBuffers.colorQuery();GL11.glGetFloat(GL11.GL_CURRENT_COLOR,color);
        GlStateManager.pushMatrix();
        try{
            GlStateManager.disableCull();GlStateManager.color(1,1,1,1);
            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("metroport:textures/blackrazid/gadgets/clock.png"));
            GlStateManager.translate(-12*.003,3.8*.003,-4*.003);BODY.render(null,0,0,0,0,0,.003F);
            double minutes=WatchItem.minutes(time);double seconds=Math.floorMod(time,24000L)*3.6%60;
            arrow(HOUR,(float)((minutes%720)*.5));arrow(MINUTE,(float)((minutes%60)*6));arrow(SECOND,(float)(seconds*6));
        }finally{
            GlStateManager.popMatrix();GlStateManager.bindTexture(texture);GlStateManager.color(color.get(0),color.get(1),color.get(2),color.get(3));
            if(cull)GlStateManager.enableCull();else GlStateManager.disableCull();
        }
    }
    public static void onArm(boolean slim,long time){
        GlStateManager.pushMatrix();try{GlStateManager.translate((slim?.5:1)/16,.40,-.14);draw(time);}finally{GlStateManager.popMatrix();}
    }
    @Override public void renderByItem(ItemStack stack,float partial){
        GlStateManager.pushMatrix();try{GlStateManager.translate(.5,.5,.5);GlStateManager.rotate(180,0,1,0);GlStateManager.scale(2,2,2);draw(Minecraft.getMinecraft().world==null?0:Minecraft.getMinecraft().world.getWorldTime());}finally{GlStateManager.popMatrix();}
    }
}
