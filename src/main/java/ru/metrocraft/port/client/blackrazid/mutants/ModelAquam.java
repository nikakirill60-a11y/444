// Generated from the user-supplied original model by import_blackrazid_mutants.py.
package ru.metrocraft.port.client.blackrazid.mutants;


import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelAquam extends ModelBase {

   private MutantObjPart strazh = new MutantObjPart("metroport:models/blackrazid/mutants/aqua_male/aqua.obj");
   private MutantObjPart leftleg = new MutantObjPart("metroport:models/blackrazid/mutants/aqua_male/leftleg.obj");
   private MutantObjPart rightleg = new MutantObjPart("metroport:models/blackrazid/mutants/aqua_male/rightleg.obj");
   private MutantObjPart leftarm = new MutantObjPart("metroport:models/blackrazid/mutants/aqua_male/leftarm.obj");
   private MutantObjPart rightarm = new MutantObjPart("metroport:models/blackrazid/mutants/aqua_male/rightarm.obj");
   private MutantObjPart leftarm1 = new MutantObjPart("metroport:models/blackrazid/mutants/aqua_male/leftleg1.obj");
   private MutantObjPart rightarm1 = new MutantObjPart("metroport:models/blackrazid/mutants/aqua_male/rightleg1.obj");
   private MutantObjPart hvost = new MutantObjPart("metroport:models/blackrazid/mutants/aqua_male/hvost.obj");


   public void render(Entity entity, float par1, float par2, float par4, float par5, float par6, float par7) {
      float handCurRot2 = MathHelper.cos(par1 * 0.6662F + 3.1415927F) * 1.4F * par2;
      GL11.glPushMatrix();
      GL11.glShadeModel(7425);
      GL11.glTranslatef(0.0F, 1.5F, -1.0F);
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      this.strazh.render();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.44F, 2.5F, -0.398F);
      GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(0.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 20.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 10.0F, 1.0F, 0.0F, 0.0F);
      this.leftarm.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.44F, 2.5F, -0.398F);
      GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(0.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 20.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(-handCurRot2 * 10.0F, 1.0F, 0.0F, 0.0F);
      this.rightarm.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.238F, 1.977F, -0.52F);
      GL11.glRotatef(0.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 30.0F, 1.0F, 0.0F, 0.0F);
      this.leftarm1.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.238F, 1.977F, -0.52F);
      GL11.glRotatef(0.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(-handCurRot2 * 30.0F, 1.0F, 0.0F, 0.0F);
      this.rightarm1.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 2.007F, -0.805F);
      GL11.glRotatef(handCurRot2 * 30.0F, 1.0F, 0.0F, 0.0F);
      this.rightleg.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.0F, 2.007F, -0.805F);
      GL11.glRotatef(-handCurRot2 * 30.0F, 1.0F, 0.0F, 0.0F);
      this.leftleg.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 2.007F, -1.14F);
      GL11.glRotatef(handCurRot2 * 20.0F, 0.0F, 1.0F, 0.0F);
      this.hvost.render();
      GL11.glPopMatrix();
      GL11.glPopMatrix();
   }
}
