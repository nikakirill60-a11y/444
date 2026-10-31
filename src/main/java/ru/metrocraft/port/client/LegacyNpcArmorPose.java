package ru.metrocraft.port.client;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/** HUG/AIMING/CRAWLING math recovered from the supplied ModelCustomArmour. */
public class LegacyNpcArmorPose extends ModelBiped {
    public LegacyNpcArmorPose(float size){super(size);}
    protected void npcPose(Entity entity,float swing,float amount,float age,float yaw,float pitch,float scale){
        if(!entity.getClass().getName().startsWith("noppes.npcs."))return;
        String name="";
        try{
            Object value=entity.getClass().getField("currentAnimation").get(entity);
            name=value instanceof Enum?((Enum<?>)value).name():String.valueOf(value);
        }catch(ReflectiveOperationException ignored){return;}
        switch(name){
            case "HUG":setRotationAnglesNPC(swing,amount,age,yaw,pitch,scale,entity);setRotationAnglesHUG(swing,amount,age,yaw,pitch,scale,entity);break;
            case "AIMING":setRotationAnglesNPC(swing,amount,age,yaw,pitch,scale,entity);setRotationAnglesAIMING(swing,amount,age,yaw,pitch,scale,entity);break;
            case "CRAWLING":setRotationAnglesNPC(swing,amount,age,yaw,pitch,scale,entity);setRotationAnglesCRAWLING(swing,amount,age,yaw,pitch,scale,entity);break;
            default:break;
        }
    }
    public void setRotationAnglesNPC(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        this.bipedHead.rotateAngleY = par4 / 57.295776f;
        this.bipedHead.rotateAngleX = par5 / 57.295776f;
        this.bipedHead.rotateAngleZ = 0.0f;
        this.bipedRightLeg.rotateAngleX = MathHelper.cos((float)(par1 * 0.6662f)) * 1.4f * par2;
        this.bipedLeftLeg.rotateAngleX = MathHelper.cos((float)(par1 * 0.6662f + (float)Math.PI)) * 1.4f * par2;
        this.bipedRightLeg.rotateAngleY = 0.0f;
        this.bipedLeftLeg.rotateAngleY = 0.0f;
        this.bipedLeftArm.rotateAngleX = 0.0f;
        this.bipedLeftArm.rotateAngleY = 0.0f;
        this.bipedLeftArm.rotateAngleZ = 0.0f;
        this.bipedLeftArm.rotationPointY = 2.0f;
        this.bipedRightArm.rotateAngleX = 0.0f;
        this.bipedRightArm.rotateAngleY = 0.0f;
        this.bipedRightArm.rotateAngleZ = 0.0f;
        this.bipedRightArm.rotationPointY = 2.0f;
        this.bipedBody.rotateAngleY = 0.0f;
        this.bipedBody.rotateAngleX = 0.0f;
        this.bipedBody.rotateAngleZ = 0.0f;
        this.bipedLeftLeg.rotateAngleZ = 0.0f;
        this.bipedLeftLeg.rotationPointZ = 0.0f;
        this.bipedRightLeg.rotateAngleZ = 0.0f;
        this.bipedRightLeg.rotationPointZ = 0.0f;
        this.bipedRightLeg.rotationPointY = 12.0f;
        this.bipedLeftLeg.rotationPointY = 12.0f;
    }

    public void setRotationAnglesHUG(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        float f6 = MathHelper.sin((float)(this.swingProgress * 3.141593f));
        float f7 = MathHelper.sin((float)((1.0f - (1.0f - this.swingProgress) * (1.0f - this.swingProgress)) * 3.141593f));
        this.bipedRightArm.rotateAngleZ = 0.0f;
        this.bipedLeftArm.rotateAngleZ = 0.0f;
        this.bipedRightArm.rotateAngleY = -(0.1f - f6 * 0.6f);
        this.bipedLeftArm.rotateAngleY = 0.1f;
        this.bipedRightArm.rotateAngleX = -1.570796f;
        this.bipedLeftArm.rotateAngleX = -1.570796f;
        this.bipedRightArm.rotateAngleX -= f6 * 1.2f - f7 * 0.4f;
        this.bipedRightArm.rotateAngleZ += MathHelper.cos((float)(par3 * 0.09f)) * 0.05f + 0.05f;
        this.bipedLeftArm.rotateAngleZ -= MathHelper.cos((float)(par3 * 0.09f)) * 0.05f + 0.05f;
        this.bipedRightArm.rotateAngleX += MathHelper.sin((float)(par3 * 0.067f)) * 0.05f;
        this.bipedLeftArm.rotateAngleX -= MathHelper.sin((float)(par3 * 0.067f)) * 0.05f;
        this.bipedLeftLeg.rotateAngleZ = 0.0f;
        this.bipedLeftLeg.rotationPointZ = 0.0f;
        this.bipedRightLeg.rotateAngleZ = 0.0f;
        this.bipedRightLeg.rotationPointZ = 0.0f;
        this.bipedRightLeg.rotationPointY = 12.0f;
        this.bipedLeftLeg.rotationPointY = 12.0f;
    }

    public void setRotationAnglesAIMING(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        float f6 = 0.0f;
        float f7 = 0.0f;
        this.bipedRightArm.rotateAngleZ = 0.0f;
        this.bipedLeftArm.rotateAngleZ = 0.0f;
        this.bipedRightArm.rotateAngleY = -(0.1f - f6 * 0.6f) + this.bipedHead.rotateAngleY;
        this.bipedLeftArm.rotateAngleY = 0.1f - f6 * 0.6f + this.bipedHead.rotateAngleY + 0.4f;
        this.bipedRightArm.rotateAngleX = -1.5707964f + this.bipedHead.rotateAngleX;
        this.bipedLeftArm.rotateAngleX = -1.5707964f + this.bipedHead.rotateAngleX;
        this.bipedRightArm.rotateAngleX -= f6 * 1.2f - f7 * 0.4f;
        this.bipedLeftArm.rotateAngleX -= f6 * 1.2f - f7 * 0.4f;
        this.bipedRightArm.rotateAngleZ += MathHelper.cos((float)(par3 * 0.09f)) * 0.05f + 0.05f;
        this.bipedLeftArm.rotateAngleZ -= MathHelper.cos((float)(par3 * 0.09f)) * 0.05f + 0.05f;
        this.bipedRightArm.rotateAngleX += MathHelper.sin((float)(par3 * 0.067f)) * 0.05f;
        this.bipedLeftArm.rotateAngleX -= MathHelper.sin((float)(par3 * 0.067f)) * 0.05f;
        this.bipedLeftLeg.rotateAngleZ = 0.0f;
        this.bipedLeftLeg.rotationPointZ = 0.0f;
        this.bipedRightLeg.rotateAngleZ = 0.0f;
        this.bipedRightLeg.rotationPointZ = 0.0f;
        this.bipedRightLeg.rotationPointY = 12.0f;
        this.bipedLeftLeg.rotationPointY = 12.0f;
    }

    public void setRotationAnglesCRAWLING(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        this.bipedHead.rotateAngleZ = -par4 / 57.295776f;
        this.bipedHead.rotateAngleY = 0.0f;
        this.bipedHead.rotateAngleX = -0.95993114f;
        if ((double)par2 > 0.25) {
            par2 = 0.25f;
        }
        float movement = MathHelper.cos((float)(par1 * 0.8f + (float)Math.PI)) * par2;
        this.bipedLeftArm.rotateAngleX = (float)Math.PI - movement * 0.25f;
        this.bipedLeftArm.rotateAngleY = movement * -0.46f;
        this.bipedLeftArm.rotateAngleZ = movement * -0.2f;
        this.bipedLeftArm.rotationPointY = 2.0f - movement * 9.0f;
        this.bipedRightArm.rotateAngleX = (float)Math.PI + movement * 0.25f;
        this.bipedRightArm.rotateAngleY = movement * -0.4f;
        this.bipedRightArm.rotateAngleZ = movement * -0.2f;
        this.bipedRightArm.rotationPointY = 2.0f + movement * 9.0f;
        this.bipedBody.rotateAngleY = movement * 0.1f;
        this.bipedBody.rotateAngleX = 0.0f;
        this.bipedBody.rotateAngleZ = movement * 0.1f;
        this.bipedLeftLeg.rotateAngleX = movement * 0.1f;
        this.bipedLeftLeg.rotateAngleY = movement * 0.1f;
        this.bipedLeftLeg.rotateAngleZ = -0.122173056f - movement * 0.25f;
        this.bipedLeftLeg.rotationPointY = 10.4f + movement * 9.0f;
        this.bipedLeftLeg.rotationPointZ = movement * 0.6f - 0.01f;
        this.bipedRightLeg.rotateAngleX = movement * -0.1f;
        this.bipedRightLeg.rotateAngleY = movement * 0.1f;
        this.bipedRightLeg.rotateAngleZ = 0.122173056f - movement * 0.25f;
        this.bipedRightLeg.rotationPointY = 10.4f - movement * 9.0f;
        this.bipedRightLeg.rotationPointZ = movement * -0.6f - 0.01f;
    }

}
