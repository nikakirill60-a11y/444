package ru.metrocraft.port.client.blackrazid.weapons;

import java.io.*;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.zip.GZIPInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import ru.metrocraft.port.client.GlStateBuffers;
import org.lwjgl.opengl.GL11;
import org.lwjgl.BufferUtils;
import ru.metrocraft.port.blackrazid.weapons.*;
import ru.metrocraft.port.client.*;
import ru.metrocraft.port.mcmod.McmodObjMesh;

/** Original meshes/materials, stack-owned equipment selection; tick-based visual adaptation, no legacy GL stack pops. */
public final class NativeWeaponRenderer extends TileEntityItemStackRenderer {
    public static final NativeWeaponRenderer INSTANCE=new NativeWeaponRenderer();
    private static final Map<String,Map<String,Integer>> LISTS=new HashMap<>();
    public static void reload(){for(Map<String,Integer> materials:LISTS.values())for(int list:materials.values())GLAllocation.deleteDisplayLists(list);LISTS.clear();}
    private static Map<String,Integer> mesh(String id){
        Map<String,Integer> cached=LISTS.get(id);if(cached!=null)return cached;
        Map<String,Integer> lists=new LinkedHashMap<>();
        try(InputStream raw=Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("metroport:models/blackrazid/weapons/"+id+".gz")).getInputStream();InputStream in=new GZIPInputStream(raw)){
            McmodObjMesh geometry=McmodObjMesh.readMaterials(in);
            if(!NativeWeaponVisuals.mesh(id).materials.keySet().containsAll(geometry.groups.keySet()))throw new IOException("Unmapped original material in "+id);
            for(Map.Entry<String,List<float[]>> entry:geometry.groups.entrySet()){
                int list=GLAllocation.generateDisplayLists(1);lists.put(entry.getKey(),list);GlStateManager.glNewList(list,GL11.GL_COMPILE);
                try{
                    BufferBuilder b=Tessellator.getInstance().getBuffer();b.begin(GL11.GL_TRIANGLES,DefaultVertexFormats.POSITION_TEX_NORMAL);
                    for(float[] v:entry.getValue())b.pos(v[0],v[1],v[2]).tex(v[3],v[4]).normal(v[5],v[6],v[7]).endVertex();
                    Tessellator.getInstance().draw();
                }finally{GlStateManager.glEndList();}
            }
        }catch(Exception e){for(int list:lists.values())GLAllocation.deleteDisplayLists(list);throw new IllegalStateException("Cannot load original native weapon mesh "+id,e);}
        LISTS.put(id,lists);return lists; // Drop expanded source/vertex arrays after GPU compilation.
    }
    private static void draw(String id){
        for(Map.Entry<String,Integer> entry:mesh(id).entrySet()){
            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(NativeWeaponVisuals.mesh(id).materials.get(entry.getKey())));GlStateManager.callList(entry.getValue());
        }
    }
    @Override public void renderByItem(ItemStack stack,float partial){
        if(!NativeWeaponVisuals.supported(stack))return;
        int texture=GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D),shade=GL11.glGetInteger(GL11.GL_SHADE_MODEL);
        boolean normalize=GL11.glIsEnabled(GL11.GL_NORMALIZE),cull=GL11.glIsEnabled(GL11.GL_CULL_FACE),blend=GL11.glIsEnabled(GL11.GL_BLEND);
        int srcRGB=GL11.glGetInteger(GL14_SRC_RGB),dstRGB=GL11.glGetInteger(GL14_DST_RGB),srcA=GL11.glGetInteger(GL14_SRC_ALPHA),dstA=GL11.glGetInteger(GL14_DST_ALPHA);
        FloatBuffer color=GlStateBuffers.colorQuery();GL11.glGetFloat(GL11.GL_CURRENT_COLOR,color);
        GlStateManager.pushMatrix();
        try{
            GlStateManager.translate(.5,.5,.5);GlStateManager.color(1,1,1,1);GlStateManager.enableNormalize();GlStateManager.disableCull();GlStateManager.shadeModel(GL11.GL_SMOOTH);
            GlStateManager.enableBlend();GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA,GL11.GL_ONE,GL11.GL_ZERO);
            if(stack.getItem() instanceof BlackrazidModules.ModuleItem){
                NativeWeaponVisuals.Module module=NativeWeaponVisuals.module(((BlackrazidModules.ModuleItem)stack.getItem()).entry.id);
                GlStateManager.rotate(90,0,1,0);GlStateManager.scale(module.scale,module.scale,module.scale);GlStateManager.translate(-module.center[0],-module.center[1],-module.center[2]);draw(module.mesh);
            }else{
                BlackrazidGunItem gun=(BlackrazidGunItem)stack.getItem();
                if(gun.entry.sourceId.equals("Gatling"))gatling(stack,gun,partial);else special(stack,gun,partial);
            }
        }finally{
            GlStateManager.color(color.get(0),color.get(1),color.get(2),color.get(3));GlStateManager.bindTexture(texture);GlStateManager.shadeModel(shade);
            GlStateManager.tryBlendFuncSeparate(srcRGB,dstRGB,srcA,dstA);if(blend)GlStateManager.enableBlend();else GlStateManager.disableBlend();
            if(cull)GlStateManager.enableCull();else GlStateManager.disableCull();if(normalize)GlStateManager.enableNormalize();else GlStateManager.disableNormalize();GlStateManager.popMatrix();
        }
    }
    // OpenGL 1.4 state names, kept numeric for compatibility with the existing 1.12 GL context.
    private static final int GL14_SRC_RGB=0x80C9,GL14_DST_RGB=0x80C8,GL14_SRC_ALPHA=0x80CB,GL14_DST_ALPHA=0x80CA;
    private static void special(ItemStack stack,BlackrazidGunItem gun,float partial){
        Minecraft mc=Minecraft.getMinecraft();TransformType type=LegacyItemModel.context();
        boolean first=type==TransformType.FIRST_PERSON_LEFT_HAND || type==TransformType.FIRST_PERSON_RIGHT_HAND;
        NativeWeaponVisuals.Gun profile=NativeWeaponVisuals.gun(gun.entry.sourceId);
        NativeWeaponVisuals.Equipment equipment=NativeWeaponVisuals.equipment(stack,mc.player);
        int action=gun.visualAction(stack),duration=action==4?gun.entry.pumpTicks:action==2?gun.entry.unloadingTicks:action==3?gun.entry.looseLoadingTicks:gun.entry.loadingTicks;
        float excursion=NativeWeaponVisuals.excursion(NativeWeaponVisuals.actionProgress(gun.visualTicks(stack),duration,partial));
        float reload=action>=1 && action<=3?excursion:0,pump=action==4?excursion:0;
        equipment.phase=NativeWeaponVisuals.actionProgress(gun.visualTicks(stack),duration,partial);equipment.aim=GunClient.aim>.5F;
        double now=mc.world==null?0:mc.world.getTotalWorldTime()+partial;
        boolean hasShot=stack.hasTagCompound() && stack.getTagCompound().hasKey(NativeWeaponVisuals.SHOT_TIME,99);
        double since=hasShot?now-stack.getTagCompound().getLong(NativeWeaponVisuals.SHOT_TIME):-1;
        float kick=since>=0 && since<4?(float)(1-since/4):0;
        GlStateManager.translate(-.025F*kick,-.06F*reload,0);
        GlStateManager.pushMatrix();
        try{
            GlStateManager.translate(profile.nativeTranslation[0],profile.nativeTranslation[1],profile.nativeTranslation[2]);
            GlStateManager.rotate(90,0,1,0);GlStateManager.scale(profile.nativeScale,profile.nativeScale,profile.nativeScale);
            if(NativeWeaponScene.guns().contains(gun.entry.sourceId)){
                FloatBuffer matrix=BufferUtils.createFloatBuffer(16);
                for(NativeWeaponScene.Draw part:NativeWeaponScene.draw(gun.entry.sourceId,equipment)){
                    GlStateManager.pushMatrix();try{
                        matrix.clear();matrix.put(part.matrix);matrix.flip();GlStateManager.multMatrix(matrix);
                        if(part.role.equals("magazine")){GlStateManager.translate(0,-.35F*reload,0);GlStateManager.rotate(-15*reload,0,0,1);}
                        if(part.role.equals("bolt")){GlStateManager.translate(0,0,-.025F*kick);GlStateManager.rotate(-20*Math.max(reload,pump),1,0,0);}
                        draw(part.mesh);
                    }finally{GlStateManager.popMatrix();}
                }
            }else for(NativeWeaponVisuals.Part part:NativeWeaponVisuals.special(gun.entry.sourceId,equipment,first)){
                GlStateManager.pushMatrix();try{
                    GlStateManager.translate(part.translation[0],part.translation[1],part.translation[2]);GlStateManager.rotate(part.angle,part.x,part.y,part.z);
                    if(part.role.equals("magazine")){GlStateManager.translate(0,-.35F*reload,0);GlStateManager.rotate(-15*reload,0,0,1);}
                    if(part.role.equals("bolt")){GlStateManager.translate(0,0,-.025F*kick);GlStateManager.rotate(-20*Math.max(reload,pump),1,0,0);}
                    draw(part.mesh);
                }finally{GlStateManager.popMatrix();}
            }
        }finally{GlStateManager.popMatrix();}
        if(first)GunHands.render(profile.id,type,Math.max(reload,pump));
    }
    private static void gatling(ItemStack stack,BlackrazidGunItem gun,float partial){
        Minecraft mc=Minecraft.getMinecraft();double now=mc.world==null?0:mc.world.getTotalWorldTime()+partial;
        boolean hasShot=stack.hasTagCompound() && stack.getTagCompound().hasKey(NativeWeaponVisuals.SHOT_TIME,99);
        long last=hasShot?stack.getTagCompound().getLong(NativeWeaponVisuals.SHOT_TIME):0;double since=now-last;
        float rotation=hasShot?NativeWeaponVisuals.spin(now,last):0,kick=hasShot && since>=0 && since<4?(float)(1-since/4):0;
        int action=gun.visualAction(stack),ticks=gun.visualTicks(stack),duration=action==4?gun.entry.pumpTicks:action==2?gun.entry.unloadingTicks:gun.entry.loadingTicks;
        float excursion=NativeWeaponVisuals.excursion(NativeWeaponVisuals.actionProgress(ticks,duration,partial));
        float reload=action==1 || action==2?excursion:0,pump=action==4?excursion:0;
        boolean five=BlackrazidModules.has(stack,0,"GatlingBarrel"),stock=BlackrazidModules.has(stack,1,"GatlingButt");
        int drive=BlackrazidModules.has(stack,2,"GatlingMotor")?2:BlackrazidModules.has(stack,2,"GatlingGiro")?1:0;
        boolean magazine=!gun.magazine(stack).isEmpty() || action==1;
        GlStateManager.translate(-.035F*kick,-.08F*reload,0);
        GlStateManager.pushMatrix();
        try{
            // Native Z-forward metres -> this gun's own X-forward palm/contact space.
            GlStateManager.translate(.12,.164,-.126);GlStateManager.rotate(90,0,1,0);GlStateManager.scale(.7,.7,.7);
            for(NativeWeaponVisuals.Part part:NativeWeaponVisuals.gatling(five,stock,drive,magazine,rotation,pump)){
                GlStateManager.pushMatrix();try{
                    GlStateManager.translate(part.translation[0],part.translation[1],part.translation[2]);GlStateManager.rotate(part.angle,part.x,part.y,part.z);
                    if(part.mesh.equals("gatling/mag.obj")){GlStateManager.translate(0,-.4F*reload,0);GlStateManager.rotate(-20*reload,0,0,1);}
                    draw(part.mesh);
                }finally{GlStateManager.popMatrix();}
            }
            if(drive!=2){GlStateManager.pushMatrix();try{
                GlStateManager.scale(.9,1,1);GlStateManager.translate(-.003,.03,0);GlStateManager.rotate(-340+gun.pressure(stack)/1.65F,0,0,1);draw("gatling/air.obj");
            }finally{GlStateManager.popMatrix();}}
        }finally{GlStateManager.popMatrix();}
        TransformType type=LegacyItemModel.context();
        if(type==TransformType.FIRST_PERSON_LEFT_HAND || type==TransformType.FIRST_PERSON_RIGHT_HAND)GunHands.render("blackrazid_gatling",type,Math.max(reload,pump));
    }
}
