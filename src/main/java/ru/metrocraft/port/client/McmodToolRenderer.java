package ru.metrocraft.port.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import ru.metrocraft.port.mcmod.McmodObjMesh;
import org.lwjgl.opengl.GL11;
import java.util.*;
import java.io.InputStream;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.mcmod.McmodMedkitItem;

/** Original tool meshes, native swing/active-hand timing, and the actual player's skin/sleeves. */
@Mod.EventBusSubscriber(modid=MetroPort.ID,value=Side.CLIENT)
public final class McmodToolRenderer extends TileEntityItemStackRenderer {
    public static final McmodToolRenderer INSTANCE=new McmodToolRenderer();
    private static ModelPlayer classic,slim;
    private static final Map<String,McmodObjMesh> MESHES=new HashMap<>();
    private static final java.nio.FloatBuffer MATRIX=org.lwjgl.BufferUtils.createFloatBuffer(16);
    public static boolean animated(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem().getRegistryName()!=null
                && ToolActionPose.animated(stack.getItem().getRegistryName().getPath());
    }
    @SubscribeEvent public static void hand(RenderSpecificHandEvent event) {
        ItemStack stack=event.getItemStack();
        AbstractClientPlayer player=Minecraft.getMinecraft().player;
        if(!animated(stack) || player==null || player.isSpectator())return;
        event.setCanceled(true);
        boolean left=(event.getHand()==EnumHand.MAIN_HAND?player.getPrimaryHand():player.getPrimaryHand().opposite())==EnumHandSide.LEFT;
        boolean medical=stack.getItem() instanceof McmodMedkitItem;
        float progress=medical && player.isHandActive() && player.getActiveHand()==event.getHand()
                ?ToolActionPose.unit((McmodMedkitItem.USE_TICKS-player.getItemInUseCount()+event.getPartialTicks())/McmodMedkitItem.USE_TICKS):0;
        float slash=medical?0:ToolActionPose.slash(event.getSwingProgress());
        float treatment=ToolActionPose.treatment(progress);
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal(); GlStateManager.disableCull();
        try {
            GlStateManager.translate((left?-1:1)*(medical?.36:.43),-.46-event.getEquipProgress()*.6,-.72);
            // Mirror the whole tool/action, but select the correct left/right Steve/Alex skin mesh below.
            if(left)GlStateManager.scale(-1,1,1);
            GlStateManager.translate(-.26*slash-.08*treatment,.08*treatment-.12*slash,-.04*treatment);
            GlStateManager.rotate((float)(-18-95*slash+8*treatment),0,0,1);
            GlStateManager.rotate((float)(-30*slash+(medical?22+12*treatment:0)),1,0,0);
            GlStateManager.rotate(medical?-12:-20,0,1,0);
            renderTool(stack,progress);
            if(!player.isInvisible()) {
                skinArm(player,left,false,new float[]{0,medical?-.13F:-.025F,.025F});
                if(medical && GunHandPose.usesSupport(left,player.getPrimaryHand()==EnumHandSide.LEFT,
                        player.getHeldItemMainhand().isEmpty(),player.getHeldItemOffhand().isEmpty()))
                    skinArm(player,!left,true,new float[]{-.25F,.025F,.08F});
            }
        } finally {
            GlStateManager.color(1,1,1,1); GlStateManager.enableCull(); GlStateManager.disableRescaleNormal(); GlStateManager.popMatrix();
        }
    }
    /** GUI uses the original flat icon; third-person/ground/frame views use these same original meshes. */
    @Override public void renderByItem(ItemStack stack,float partial) {
        if(!animated(stack))return;
        GlStateManager.pushMatrix(); GlStateManager.disableCull(); GlStateManager.enableRescaleNormal();
        try {
            GlStateManager.translate(.5,.5,.5);
            renderTool(stack,0);
        } finally {
            GlStateManager.color(1,1,1,1); GlStateManager.enableCull(); GlStateManager.disableRescaleNormal(); GlStateManager.popMatrix();
        }
    }
    private static void renderTool(ItemStack stack,float progress) {
        boolean medical=stack.getItem() instanceof McmodMedkitItem;
        String id=medical?"aptech":"machete";
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("metroport:textures/mcmod/tools/"+id+".png"));
        GlStateManager.color(1,1,1,1);
        McmodObjMesh mesh=MESHES.computeIfAbsent(id,key->{
            ResourceLocation path=new ResourceLocation("metroport:models/legacy/mcmod_tools/"+key+".obj");
            try(InputStream in=Minecraft.getMinecraft().getResourceManager().getResource(path).getInputStream()) {
                return McmodObjMesh.read(in);
            } catch(Exception ex) { throw new IllegalStateException("Cannot read tool mesh "+path,ex); }
        });
        group(mesh,"body",medical,0,0,0);
        if(medical) {
            int count=McmodMedkitItem.remaining(stack);
            float extract=ToolActionPose.extraction(progress);
            for(int i=1;i<=count;i++) {
                // Original OBJ has five separate syringe groups. Empty slots stay empty after use.
                float amount=i==count?extract:0;
                float centerX=(-124.34F+(i==2?57.3F:i>=3?112.15F+(i-3)*55.51F:0))*.001F;
                float injection=(float)Math.sin(Math.PI*ToolActionPose.unit((progress-.4F)/.28F));
                group(mesh,Integer.toString(i),true,amount*(-.23F-centerX),amount*.11F,amount*(-.02F+.035F*injection));
            }
        }
    }
    private static void group(McmodObjMesh mesh,String group,boolean medical,float x,float y,float z) {
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x,y,z);
            float scale=medical?.001F:.00065F;
            GlStateManager.scale(scale,scale,scale);
            GlStateManager.translate(0,medical?180:420,medical?-400:-450);
            List<float[]> vertices=mesh.groups.get(group);
            if(vertices!=null && !vertices.isEmpty()) {
                BufferBuilder b=Tessellator.getInstance().getBuffer();
                b.begin(GL11.GL_TRIANGLES,DefaultVertexFormats.POSITION_TEX_NORMAL);
                for(float[] v:vertices)b.pos(v[0],v[1],v[2]).tex(v[3],v[4]).normal(v[5],v[6],v[7]).endVertex();
                Tessellator.getInstance().draw();
            }
        } finally { GlStateManager.popMatrix(); }
    }
    private static void skinArm(AbstractClientPlayer player,boolean left,boolean support,float[] contact) {
        boolean alex="slim".equals(player.getSkinType());
        if(alex && slim==null)slim=new ModelPlayer(0,true);
        if(!alex && classic==null)classic=new ModelPlayer(0,false);
        ModelPlayer skin=alex?slim:classic;
        ModelRenderer arm=left?skin.bipedLeftArm:skin.bipedRightArm;
        ModelRenderer sleeve=left?skin.bipedLeftArmwear:skin.bipedRightArmwear;
        reset(arm); reset(sleeve);
        javax.vecmath.Matrix4f matrix=ToolActionPose.armMatrix(contact,left,alex,support);
        MATRIX.clear();
        for(int column=0;column<4;column++)for(int row=0;row<4;row++)MATRIX.put(matrix.getElement(row,column));
        MATRIX.flip();
        Minecraft.getMinecraft().getTextureManager().bindTexture(player.getLocationSkin());
        GlStateManager.pushMatrix();
        try {
            GlStateManager.multMatrix(MATRIX); arm.render(1/16F);
            if(player.isWearing(left?EnumPlayerModelParts.LEFT_SLEEVE:EnumPlayerModelParts.RIGHT_SLEEVE))sleeve.render(1/16F);
        } finally { GlStateManager.popMatrix(); }
    }
    private static void reset(ModelRenderer part) {
        part.setRotationPoint(0,0,0); part.rotateAngleX=part.rotateAngleY=part.rotateAngleZ=0;
        part.showModel=true; part.isHidden=false;
    }
}
