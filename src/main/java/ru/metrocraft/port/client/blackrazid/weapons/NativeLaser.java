package ru.metrocraft.port.client.blackrazid.weapons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.blackrazid.weapons.BlackrazidModules;

/** Explicit visual adaptation: a bounded, depth-tested aiming dot; not the still-missing native laser mesh. */
@Mod.EventBusSubscriber(modid=MetroPort.ID,value=Side.CLIENT)
public final class NativeLaser {
    private NativeLaser(){}
    @SubscribeEvent public static void render(RenderWorldLastEvent event){
        Minecraft mc=Minecraft.getMinecraft();Entity view=mc.getRenderViewEntity();
        if(mc.player==null || view==null || mc.currentScreen!=null || mc.gameSettings.hideGUI || !mc.player.isEntityAlive()
                || mc.player.isSpectator() || mc.player.isSprinting() || !BlackrazidModules.laser(mc.player.getHeldItemMainhand()))return;
        RayTraceResult hit=mc.player.rayTrace(32,event.getPartialTicks());
        if(hit==null || hit.typeOfHit!=RayTraceResult.Type.BLOCK || hit.sideHit==null)return;
        double x=view.lastTickPosX+(view.posX-view.lastTickPosX)*event.getPartialTicks();
        double y=view.lastTickPosY+(view.posY-view.lastTickPosY)*event.getPartialTicks();
        double z=view.lastTickPosZ+(view.posZ-view.lastTickPosZ)*event.getPartialTicks();
        Vec3d dot=hit.hitVec.add(new Vec3d(hit.sideHit.getDirectionVec()).scale(.003));
        float size=GL11.glGetFloat(GL11.GL_POINT_SIZE);
        boolean lighting=GL11.glIsEnabled(GL11.GL_LIGHTING),texture=GL11.glIsEnabled(GL11.GL_TEXTURE_2D),writeDepth=GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        GlStateManager.pushMatrix();
        try{
            GlStateManager.translate(-x,-y,-z);GlStateManager.disableTexture2D();GlStateManager.disableLighting();GlStateManager.depthMask(false);
            GL11.glPointSize(4);GlStateManager.color(1,.05F,.02F,1);
            Tessellator t=Tessellator.getInstance();t.getBuffer().begin(GL11.GL_POINTS,net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION);
            t.getBuffer().pos(dot.x,dot.y,dot.z).endVertex();t.draw();
        }finally{
            GL11.glPointSize(size);GlStateManager.color(1,1,1,1);GlStateManager.depthMask(writeDepth);
            if(texture)GlStateManager.enableTexture2D();else GlStateManager.disableTexture2D();
            if(lighting)GlStateManager.enableLighting();else GlStateManager.disableLighting();GlStateManager.popMatrix();
        }
    }
}
