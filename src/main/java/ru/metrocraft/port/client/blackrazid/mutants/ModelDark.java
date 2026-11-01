// Generated from the user-supplied original model by import_blackrazid_mutants.py.
package ru.metrocraft.port.client.blackrazid.mutants;


import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelDark extends ModelBase {

   private MutantObjPart dark = new MutantObjPart("metroport:models/blackrazid/mutants/dark/dark1.obj");
   private MutantObjPart leftleg = new MutantObjPart("metroport:models/blackrazid/mutants/dark/leftleg.obj");
   private MutantObjPart rightleg = new MutantObjPart("metroport:models/blackrazid/mutants/dark/rightleg.obj");
   private MutantObjPart leftarm = new MutantObjPart("metroport:models/blackrazid/mutants/dark/leftarm.obj");
   private MutantObjPart leftarm1 = new MutantObjPart("metroport:models/blackrazid/mutants/dark/leftarm1.obj");
   private MutantObjPart rightarm = new MutantObjPart("metroport:models/blackrazid/mutants/dark/rightarm.obj");
   private MutantObjPart rightarm1 = new MutantObjPart("metroport:models/blackrazid/mutants/dark/rightarm1.obj");


   public void render(Entity entity, float par1, float par2, float par4, float par5, float par6, float par7) {
      float handCurRot2 = MathHelper.cos(par1 * 0.6662F + 3.1415927F) * 1.4F * par2;
      GL11.glPushMatrix();
      GL11.glShadeModel(7425);
      GL11.glTranslatef(0.0F, 1.55F + handCurRot2 * 30.0F / 900.0F, 0.0F);
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      net.minecraft.client.renderer.GlStateManager.disableCull();
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
      net.minecraft.client.renderer.GlStateManager.enableCull();
      GL11.glPopMatrix();
   }
}
