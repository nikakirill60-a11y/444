package ru.metrocraft.port.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.*;
import java.nio.FloatBuffer;
import java.util.*;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.editor.*;

/** Depth-tested wireframes only: no ghost blocks in the world, no mesh/UV editor and no through-wall rendering. */
@Mod.EventBusSubscriber(modid=MetroPort.ID,value=Side.CLIENT)
public final class EditorPreviewRenderer {
    private static WorldClient world;
    private static EntityPlayer owner;
    private static EditSession.Box box;
    private static List<BlockPos> cells=Collections.emptyList();
    private static int kind;
    private static long deadline;
    private EditorPreviewRenderer(){}
    private static void clear(){world=null;owner=null;box=null;cells=Collections.emptyList();kind=0;}
    public static void receive(int dimension,int id,int nextKind,EditSession.Box bounds,List<BlockPos> nextCells){
        Minecraft mc=Minecraft.getMinecraft();WorldClient expectedWorld=mc.world;EntityPlayer expectedOwner=mc.player;
        mc.addScheduledTask(()->{
            if(expectedWorld==null || expectedOwner==null || mc.world!=expectedWorld || mc.player!=expectedOwner
                    || mc.player.dimension!=dimension || mc.player.getEntityId()!=id)return;
            clear();
            if(nextKind==0)return;
            world=expectedWorld;owner=expectedOwner;box=bounds;cells=nextCells;kind=nextKind;deadline=PreviewRules.deadline(System.nanoTime());
        });
    }
    @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event){
        if(event.phase!=TickEvent.Phase.END)return;Minecraft mc=Minecraft.getMinecraft();
        if(world!=mc.world || owner!=mc.player || !PreviewRules.alive(System.nanoTime(),deadline)
                || mc.player==null || !mc.player.capabilities.isCreativeMode || !mc.player.isEntityAlive() || mc.player.isSpectator())clear();
    }
    @SubscribeEvent public static void render(RenderWorldLastEvent event){
        Minecraft mc=Minecraft.getMinecraft();
        if(box==null || mc.player==null || !PreviewRules.show(world==mc.world && owner==mc.player,
                mc.player.capabilities.isCreativeMode,mc.player.isEntityAlive(),mc.player.isSpectator(),mc.gameSettings.hideGUI,
                mc.currentScreen!=null,PreviewRules.alive(System.nanoTime(),deadline)))return;
        Entity view=mc.getRenderViewEntity();if(view==null)return;
        double partial=event.getPartialTicks(),x=view.lastTickPosX+(view.posX-view.lastTickPosX)*partial,
                y=view.lastTickPosY+(view.posY-view.lastTickPosY)*partial,z=view.lastTickPosZ+(view.posZ-view.lastTickPosZ)*partial;
        boolean blend=GL11.glIsEnabled(GL11.GL_BLEND),texture=GL11.glIsEnabled(GL11.GL_TEXTURE_2D),
                light=GL11.glIsEnabled(GL11.GL_LIGHTING),depth=GL11.glIsEnabled(GL11.GL_DEPTH_TEST),mask=GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        int matrix=GL11.glGetInteger(GL11.GL_MATRIX_MODE),src=GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB),dst=GL11.glGetInteger(GL14.GL_BLEND_DST_RGB),
                srcAlpha=GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA),dstAlpha=GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
        float line=GL11.glGetFloat(GL11.GL_LINE_WIDTH);
        FloatBuffer color=GlStateBuffers.colorQuery();GL11.glGetFloat(GL11.GL_CURRENT_COLOR,color);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);GlStateManager.pushMatrix();
        try{
            GlStateManager.translate(-x,-y,-z);GlStateManager.disableTexture2D();GlStateManager.disableLighting();
            GlStateManager.enableDepth();GlStateManager.depthMask(false);GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA,GL11.GL_ONE,GL11.GL_ZERO);GlStateManager.glLineWidth(2F);
            BlockPos o=box.origin;AxisAlignedBB outline=new AxisAlignedBB(o.getX(),o.getY(),o.getZ(),o.getX()+box.width,o.getY()+box.height,o.getZ()+box.depth);
            RenderGlobal.drawSelectionBoundingBox(outline.grow(.003),.2F,kind==1?.7F:1F,kind==1?1F:.25F,.9F);
            GlStateManager.glLineWidth(1F);
            for(BlockPos pos:cells)RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(pos).grow(.002),.35F,1F,.35F,.55F);
        }finally{
            GlStateManager.glLineWidth(line);GlStateManager.color(color.get(0),color.get(1),color.get(2),color.get(3));
            GlStateManager.tryBlendFuncSeparate(src,dst,srcAlpha,dstAlpha);GlStateManager.depthMask(mask);
            if(blend)GlStateManager.enableBlend();else GlStateManager.disableBlend();
            if(texture)GlStateManager.enableTexture2D();else GlStateManager.disableTexture2D();
            if(light)GlStateManager.enableLighting();else GlStateManager.disableLighting();
            if(depth)GlStateManager.enableDepth();else GlStateManager.disableDepth();
            GlStateManager.popMatrix();GlStateManager.matrixMode(matrix);
        }
    }
}
