package mods.provoda.mobs.models.kikimora;

import mods.provoda.GLLoader.GLModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelKikimora extends ModelBase {

   private GLModel kikimora = new GLModel("/mods/provoda/mobs/models/kikimora/kikimora1.obj");
   private GLModel leftArm = new GLModel("/mods/provoda/mobs/models/kikimora/leftArm.obj");
   private GLModel rightArm = new GLModel("/mods/provoda/mobs/models/kikimora/rightArm.obj");
   private GLModel leftLeg = new GLModel("/mods/provoda/mobs/models/kikimora/leftLeg.obj");
   private GLModel rightLeg = new GLModel("/mods/provoda/mobs/models/kikimora/rightLeg.obj");
   private GLModel hvost = new GLModel("/mods/provoda/mobs/models/kikimora/hvost.obj");
   private boolean hand1;
   private boolean hand2;
   private boolean hand3;
   private boolean hand4;
   private float handCurRot1;
   private float handMaxRot1;
   private float handCurRot2;
   private float handMaxRot2;


   public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {
      this.handCurRot2 = MathHelper.cos(par1 * 0.6662F + 3.1415927F) * 1.4F * par2;
      GL11.glPushMatrix();
      GL11.glShadeModel(7425);
      GL11.glTranslatef(0.0F, 1.6F + this.handCurRot2 * 30.0F / 900.0F, 0.0F);
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      Minecraft.getMinecraft().renderEngine.bindTexture("/mods/provoda/textures/mobs/kikimora.png");
      this.kikimora.render();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 0.77F, 0.37F);
      GL11.glRotatef(this.handCurRot2 * 30.0F, 1.0F, 0.0F, 0.0F);
      this.leftArm.render();
      this.rightArm.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 0.7F, -0.165F);
      GL11.glRotatef(-this.handCurRot2 * 30.0F, 1.0F, 0.0F, 0.0F);
      this.leftLeg.render();
      this.rightLeg.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 0.0F, -0.299F);
      GL11.glRotatef(-this.handCurRot2 * 30.0F / 5.0F, 0.0F, 1.0F, 0.0F);
      this.hvost.render();
      GL11.glPopMatrix();
      GL11.glPopMatrix();
   }

   public void render(Entity entity, float par1, float par2, float par4, float par5, float par6, float par7) {
      this.setRotationAngles(par1, par2, par4, par5, par6, par7, entity);
      this.handMaxRot1 = 40.0F;
      EntityLiving entity1 = (EntityLiving)entity;
      GL11.glPushMatrix();
      GL11.glShadeModel(7425);
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      if(entity.worldObj.isRemote && (entity1.prevPosX != entity1.posX || entity1.prevPosZ != entity1.posZ)) {
         float animspeed;
         if(this.hand1) {
            Minecraft.getMinecraft();
            animspeed = (float)60F / 100.0F;
            this.handCurRot1 += 7.0F / (animspeed * 3.0F);
         }

         if(!this.hand1) {
            Minecraft.getMinecraft();
            animspeed = (float)60F / 100.0F;
            this.handCurRot1 -= 7.0F / (animspeed * 3.0F);
         }
      }

      if(this.handCurRot1 > this.handMaxRot1) {
         this.handCurRot1 = 40.0F;
         this.hand1 = false;
      }

      if(-40.0F > this.handCurRot1) {
         this.handCurRot1 = -40.0F;
         this.hand1 = true;
      }

      GL11.glPopMatrix();
   }
}
