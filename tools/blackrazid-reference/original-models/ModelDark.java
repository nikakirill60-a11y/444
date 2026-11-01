package mods.provoda.mobs.models.dark;

import mods.provoda.GLLoader.GLModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelDark extends ModelBase {

   private GLModel dark = new GLModel("/mods/provoda/mobs/models/dark/dark1.obj");
   private GLModel leftleg = new GLModel("/mods/provoda/mobs/models/dark/leftleg.obj");
   private GLModel rightleg = new GLModel("/mods/provoda/mobs/models/dark/rightleg.obj");
   private GLModel leftarm = new GLModel("/mods/provoda/mobs/models/dark/leftarm.obj");
   private GLModel leftarm1 = new GLModel("/mods/provoda/mobs/models/dark/leftarm1.obj");
   private GLModel rightarm = new GLModel("/mods/provoda/mobs/models/dark/rightarm.obj");
   private GLModel rightarm1 = new GLModel("/mods/provoda/mobs/models/dark/rightarm1.obj");


   public void render(Entity entity, float par1, float par2, float par4, float par5, float par6, float par7) {
      float handCurRot2 = MathHelper.cos(par1 * 0.6662F + 3.1415927F) * 1.4F * par2;
      EntityLiving entity1 = (EntityLiving)entity;
      GL11.glPushMatrix();
      GL11.glShadeModel(7425);
      GL11.glTranslatef(0.0F, 1.55F + handCurRot2 * 30.0F / 900.0F, 0.0F);
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      GL11.glDisable(2884);
      this.dark.render();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.01F, 2.03F, -0.0F);
      GL11.glRotatef(handCurRot2 * 10.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 20.0F, 1.0F, 0.0F, 0.0F);
      this.leftarm.render();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.55F, -0.53F, 0.1F);
      GL11.glRotatef(handCurRot2 * 10.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 10.0F, 0.0F, 0.0F, 1.0F);
      this.leftarm1.render();
      GL11.glPopMatrix();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.01F, 2.03F, -0.0F);
      GL11.glRotatef(handCurRot2 * 10.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(-handCurRot2 * 20.0F, 1.0F, 0.0F, 0.0F);
      this.rightarm.render();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.5F, -0.53F, 0.1F);
      GL11.glRotatef(-handCurRot2 * 10.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 10.0F, 0.0F, 0.0F, 1.0F);
      this.rightarm1.render();
      GL11.glPopMatrix();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 1.353F, 0.038F);
      GL11.glRotatef(handCurRot2 * 30.0F, 1.0F, 0.0F, 0.0F);
      this.rightleg.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.0F, 1.353F, 0.038F);
      GL11.glRotatef(-handCurRot2 * 30.0F, 1.0F, 0.0F, 0.0F);
      this.leftleg.render();
      GL11.glPopMatrix();
      GL11.glEnable(2884);
      GL11.glPopMatrix();
   }
}
