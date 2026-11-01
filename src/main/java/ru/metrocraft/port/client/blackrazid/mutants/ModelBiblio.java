// Generated from the user-supplied original model by import_blackrazid_mutants.py.
package ru.metrocraft.port.client.blackrazid.mutants;


import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelBiblio extends ModelBase {

   private MutantObjPart biblio = new MutantObjPart("metroport:models/blackrazid/mutants/bibliotecar/bibliotecar.obj");
   private MutantObjPart leftleg = new MutantObjPart("metroport:models/blackrazid/mutants/bibliotecar/leftleg.obj");
   private MutantObjPart rightleg = new MutantObjPart("metroport:models/blackrazid/mutants/bibliotecar/rightleg.obj");
   private MutantObjPart leftarm = new MutantObjPart("metroport:models/blackrazid/mutants/bibliotecar/leftarm.obj");
   private MutantObjPart leftarm1 = new MutantObjPart("metroport:models/blackrazid/mutants/bibliotecar/leftarm1.obj");
   private MutantObjPart rightarm = new MutantObjPart("metroport:models/blackrazid/mutants/bibliotecar/rightarm.obj");
   private MutantObjPart rightarm1 = new MutantObjPart("metroport:models/blackrazid/mutants/bibliotecar/rightarm1.obj");


   public void render(Entity entity, float par1, float par2, float par4, float par5, float par6, float par7) {
      float handCurRot2 = MathHelper.cos(par1 * 0.6662F + 3.1415927F) * 1.4F * par2;
      GL11.glPushMatrix();
      GL11.glShadeModel(7425);
      GL11.glTranslatef(0.0F, 1.55F + handCurRot2 * 30.0F / 900.0F, 0.0F);
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      this.biblio.render();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.04F, 2.05F, -0.088F);
      GL11.glRotatef(0.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 10.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 20.0F, 1.0F, 0.0F, 0.0F);
      this.leftarm.render();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.775F, -0.311F, 0.1F);
      GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 10.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
      GL11.glRotatef(handCurRot2 * 10.0F, 0.0F, 0.0F, 1.0F);
      this.leftarm1.render();
      GL11.glPopMatrix();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.04F, 2.05F, -0.088F);
      GL11.glRotatef(0.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 10.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(-handCurRot2 * 20.0F, 1.0F, 0.0F, 0.0F);
      this.rightarm.render();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.72F, -0.291F, 0.15F);
      GL11.glRotatef(10.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 10.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
      GL11.glRotatef(handCurRot2 * 10.0F, 0.0F, 0.0F, 1.0F);
      this.rightarm1.render();
      GL11.glPopMatrix();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 1.35F, -0.198F);
      GL11.glRotatef(handCurRot2 * 30.0F, 1.0F, 0.0F, 0.0F);
      this.rightleg.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.0F, 1.35F, -0.198F);
      GL11.glRotatef(-handCurRot2 * 30.0F, 1.0F, 0.0F, 0.0F);
      this.leftleg.render();
      GL11.glPopMatrix();
      GL11.glPopMatrix();
   }
}
