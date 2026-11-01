// Generated from the user-supplied original model by import_blackrazid_mutants.py.
package ru.metrocraft.port.client.blackrazid.mutants;


import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelSpider extends ModelBase {

   private MutantObjPart modelObj = new MutantObjPart("metroport:models/blackrazid/mutants/spider/spider.obj");
   private MutantObjPart leftleg = new MutantObjPart("metroport:models/blackrazid/mutants/spider/leftleg.obj");
   private MutantObjPart rightleg = new MutantObjPart("metroport:models/blackrazid/mutants/spider/rightleg.obj");
   private MutantObjPart leftarm = new MutantObjPart("metroport:models/blackrazid/mutants/spider/leftarm.obj");
   private MutantObjPart rightarm = new MutantObjPart("metroport:models/blackrazid/mutants/spider/rightarm.obj");


   public void render(Entity entity, float par1, float par2, float par4, float par5, float par6, float par7) {
      float handCurRot2 = MathHelper.cos(par1 * 0.6662F + 3.1415927F) * 1.4F * par2;
      GL11.glPushMatrix();
      GL11.glShadeModel(7425);
      GL11.glTranslatef(0.0F, 1.63F + handCurRot2 * 30.0F / 900.0F, 0.0F);
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      this.modelObj.render();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.106F, 0.322F, 0.241F);
      GL11.glRotatef(handCurRot2 * 30.0F, 0.0F, 1.0F, 0.0F);
      this.leftarm.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.106F, 0.322F, 0.241F);
      GL11.glRotatef(handCurRot2 * 30.0F, 0.0F, 1.0F, 0.0F);
      this.rightarm.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.11F, 0.38F, 0.0F);
      GL11.glRotatef(-handCurRot2 * 30.0F, 0.0F, 1.0F, 0.0F);
      this.rightleg.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.1F, 0.38F, 0.03F);
      GL11.glRotatef(-handCurRot2 * 30.0F, 0.0F, 1.0F, 0.0F);
      this.leftleg.render();
      GL11.glPopMatrix();
      GL11.glPopMatrix();
   }
}
