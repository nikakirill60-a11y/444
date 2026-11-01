// Generated from the user-supplied original model by import_blackrazid_mutants.py.
package ru.metrocraft.port.client.blackrazid.mutants;


import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelDemon extends ModelBase {

   private MutantObjPart demon = new MutantObjPart("metroport:models/blackrazid/mutants/demon/demon.obj","metroport:textures/blackrazid/mutants/demon.png","metroport:textures/blackrazid/mutants/demon_part02.png");
   private MutantObjPart demonleftarm = new MutantObjPart("metroport:models/blackrazid/mutants/demon/leftarm.obj","metroport:textures/blackrazid/mutants/demon.png","metroport:textures/blackrazid/mutants/demon_part02.png");
   private MutantObjPart demonrightarm = new MutantObjPart("metroport:models/blackrazid/mutants/demon/rightarm.obj","metroport:textures/blackrazid/mutants/demon.png","metroport:textures/blackrazid/mutants/demon_part02.png");
   private MutantObjPart demonlegs = new MutantObjPart("metroport:models/blackrazid/mutants/demon/legs.obj","metroport:textures/blackrazid/mutants/demon.png","metroport:textures/blackrazid/mutants/demon_part02.png");
   private MutantObjPart demonleftkrilo = new MutantObjPart("metroport:models/blackrazid/mutants/demon/leftkrilo.obj","metroport:textures/blackrazid/mutants/demon.png","metroport:textures/blackrazid/mutants/demon_part02.png");
   private MutantObjPart demonrightkrilo = new MutantObjPart("metroport:models/blackrazid/mutants/demon/rightkrilo.obj","metroport:textures/blackrazid/mutants/demon.png","metroport:textures/blackrazid/mutants/demon_part02.png");
   private MutantObjPart demonrightkrilo1 = new MutantObjPart("metroport:models/blackrazid/mutants/demon/rightkrilo1.obj","metroport:textures/blackrazid/mutants/demon.png","metroport:textures/blackrazid/mutants/demon_part02.png");
   private MutantObjPart demonleftkrilo1 = new MutantObjPart("metroport:models/blackrazid/mutants/demon/leftkrilo1.obj","metroport:textures/blackrazid/mutants/demon.png","metroport:textures/blackrazid/mutants/demon_part02.png");


   public void render(Entity entity, float par1, float par2, float par4, float par5, float par6, float par7) {
      float handCurRot2 = MathHelper.cos(par4 / 2.0F);
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
