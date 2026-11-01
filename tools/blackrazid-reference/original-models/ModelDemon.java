package mods.provoda.mobs.models.demon;

import mods.provoda.GLLoader.GLModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelDemon extends ModelBase {

   private GLModel demon = new GLModel("/mods/provoda/mobs/models/demon/demon.obj");
   private GLModel demonleftarm = new GLModel("/mods/provoda/mobs/models/demon/leftarm.obj");
   private GLModel demonrightarm = new GLModel("/mods/provoda/mobs/models/demon/rightarm.obj");
   private GLModel demonlegs = new GLModel("/mods/provoda/mobs/models/demon/legs.obj");
   private GLModel demonleftkrilo = new GLModel("/mods/provoda/mobs/models/demon/leftkrilo.obj");
   private GLModel demonrightkrilo = new GLModel("/mods/provoda/mobs/models/demon/rightkrilo.obj");
   private GLModel demonrightkrilo1 = new GLModel("/mods/provoda/mobs/models/demon/rightkrilo1.obj");
   private GLModel demonleftkrilo1 = new GLModel("/mods/provoda/mobs/models/demon/leftkrilo1.obj");


   public void render(Entity entity, float par1, float par2, float par4, float par5, float par6, float par7) {
      float handCurRot2 = MathHelper.cos(par4 / 2.0F);
      EntityLiving entity1 = (EntityLiving)entity;
      GL11.glPushMatrix();
      GL11.glShadeModel(7425);
      GL11.glTranslatef(0.0F, 1.5F + handCurRot2 * 30.0F / 900.0F, 2.0F);
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
      this.demon.render();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.33F, 1.689F, 0.693F);
      GL11.glRotatef(handCurRot2 * 40.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 20.0F, 0.0F, 0.0F, 1.0F);
      this.demonleftarm.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.33F, 1.689F, 0.693F);
      GL11.glRotatef(-handCurRot2 * 40.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 20.0F, 0.0F, 0.0F, 1.0F);
      this.demonrightarm.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.0F, 1.247F, 0.092F);
      GL11.glRotatef(handCurRot2 * 5.0F, 0.0F, 0.0F, 1.0F);
      GL11.glRotatef(handCurRot2 * 30.0F, 1.0F, 0.0F, 0.0F);
      this.demonlegs.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.061F, 1.808F, 0.615F);
      GL11.glRotatef(handCurRot2 * 20.0F, 0.0F, 0.0F, 1.0F);
      this.demonleftkrilo.render();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.503F, 1.374F, 0.195F);
      GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
      GL11.glRotatef(handCurRot2 * 40.0F, 0.0F, 0.0F, 1.0F);
      this.demonleftkrilo1.render();
      GL11.glPopMatrix();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.09F, 1.808F, 0.615F);
      GL11.glRotatef(-handCurRot2 * 20.0F, 0.0F, 0.0F, 1.0F);
      this.demonrightkrilo.render();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.41F, 1.374F, 0.195F);
      GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
      GL11.glRotatef(-handCurRot2 * 40.0F, 0.0F, 0.0F, 1.0F);
      this.demonrightkrilo1.render();
      GL11.glPopMatrix();
      GL11.glPopMatrix();
      GL11.glPopMatrix();
   }
}
