package ru.metrocraft.port.client;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Literal original boxes, UVs, hierarchy and armed/rest shoulder offsets; no legacy executable code. */
public final class BlackrazidModel extends ModelBiped {
    public static final class Box { public float[] values;public boolean mirror; }
    public static final class Part {
        public String name;public int[] uv,size;public float[] point,rotation;public Box[] boxes;public String[] parents;
    }
    public static final class Data { public int[] textureSize;public float inflation;public Part[] parts;public Map<String,float[]> armed,rest; }
    private static final Map<String,BlackrazidModel> CACHE=new HashMap<>();
    private final Map<String,ModelRenderer> nodes=new HashMap<>();
    private final Data data;private final String mode;
    BlackrazidModel(Data d,String mode){
        super(d.inflation,0,d.textureSize[0],d.textureSize[1]);this.data=d;this.mode=mode;
        // Keep the original super(inflation) base cubes: their atlas is part of the source outfit.
        for(Part p:d.parts){
            ModelRenderer box=new ModelRenderer(this,p.uv[0],p.uv[1]);box.setTextureSize(p.size[0],p.size[1]);
            for(Box b:p.boxes){float[] v=b.values;box.mirror=b.mirror;box.addBox(v[0],v[1],v[2],(int)v[3],(int)v[4],(int)v[5],v.length>6?v[6]:0);}
            box.setRotationPoint(p.point[0],p.point[1],p.point[2]);
            box.rotateAngleX=p.rotation[0];box.rotateAngleY=p.rotation[1];box.rotateAngleZ=p.rotation[2];nodes.put(p.name,box);
        }
        for(Part p:d.parts)for(String parent:p.parents)joint(parent).addChild(nodes.get(p.name));
    }
    public static BlackrazidModel get(String id,String mode){
        return CACHE.computeIfAbsent(id,key->{
            try(Reader r=new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(
                    new net.minecraft.util.ResourceLocation("metroport:geometry/blackrazid/"+id+".json")).getInputStream(),StandardCharsets.UTF_8)){
                return new BlackrazidModel(new Gson().fromJson(r,Data.class),mode);
            }catch(Exception e){throw new IllegalStateException("Cannot load BlackRazid model "+id,e);}
        });
    }
    private ModelRenderer joint(String id){
        switch(id){case "head":return bipedHead;case "headwear":return bipedHeadwear;case "body":return bipedBody;
            case "rightarm":return bipedRightArm;case "leftarm":return bipedLeftArm;case "rightleg":return bipedRightLeg;case "leftleg":return bipedLeftLeg;
            default:ModelRenderer node=nodes.get(id);if(node==null)throw new IllegalArgumentException("Unknown joint "+id);return node;}
    }
    @Override public void setRotationAngles(float swing,float amount,float age,float yaw,float pitch,float scale,Entity entity){
        super.setRotationAngles(swing,amount,age,yaw,pitch,scale,entity);
        boolean armed=entity instanceof EntityLivingBase && (((EntityLivingBase)entity).getHeldItemMainhand().getItem() instanceof ru.metrocraft.port.GunItem
                ||((EntityLivingBase)entity).getHeldItemOffhand().getItem() instanceof ru.metrocraft.port.GunItem);
        for(Part p:data.parts){float[] v=(armed?data.armed:data.rest).get(p.name);if(v==null)v=p.point;nodes.get(p.name).setRotationPoint(v[0],v[1],v[2]);}
    }
    @Override public void render(Entity entity,float swing,float amount,float age,float yaw,float pitch,float scale){
        boolean helmet="helmet".equals(mode);
        boolean hideHead=!helmet && entity instanceof EntityLivingBase && !((EntityLivingBase)entity).getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty();
        bipedHead.showModel=bipedHeadwear.showModel=!hideHead;
        bipedBody.showModel=bipedRightArm.showModel=bipedLeftArm.showModel=bipedRightLeg.showModel=bipedLeftLeg.showModel=!helmet;
        GlStateManager.enableBlend();GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ZERO);
        try{super.render(entity,swing,amount,age,yaw,pitch,scale);}finally{GlStateManager.disableBlend();}
    }
    public double patchSurface(){
        double x=.257;
        for(Part p:data.parts)if(java.util.Arrays.asList(p.parents).contains("leftarm")){
            javax.vecmath.Matrix4f m=new javax.vecmath.Matrix4f(),step=new javax.vecmath.Matrix4f();m.setIdentity();
            m.setTranslation(new javax.vecmath.Vector3f(p.point));step.rotZ(p.rotation[2]);m.mul(step);step.rotY(p.rotation[1]);m.mul(step);step.rotX(p.rotation[0]);m.mul(step);
            for(Box box:p.boxes){float[] v=box.values;float grow=v.length>6?v[6]:0;
                for(int bits=0;bits<8;bits++){
                    javax.vecmath.Vector4f point=new javax.vecmath.Vector4f(v[0]+((bits&1)==0?-grow:v[3]+grow),v[1]+((bits&2)==0?-grow:v[4]+grow),v[2]+((bits&4)==0?-grow:v[5]+grow),1);m.transform(point);
                    if(point.y>=-.8 && point.y<=3.84 && Math.abs(point.z)<=2.4)x=Math.max(x,point.x/16+.008);
                }
            }
        }return x;
    }
    /** The player layer has already applied bipedBody.postRender, including sneaking/body rotation. */
    public void renderBackpack(){
        bipedBody.setRotationPoint(0,0,0);bipedBody.rotateAngleX=bipedBody.rotateAngleY=bipedBody.rotateAngleZ=0;bipedBody.showModel=true;
        GlStateManager.enableBlend();GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        try{bipedBody.render(1/16F);}finally{GlStateManager.disableBlend();}
    }
}
