// Generated from the user-supplied original model by import_blackrazid_mutants.py.
package ru.metrocraft.port.client.blackrazid.mutants;


import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelStrazh extends ModelBase {

   private MutantObjPart strazh = new MutantObjPart("metroport:models/blackrazid/mutants/strazh/strazh1.obj");
   private MutantObjPart leftleg = new MutantObjPart("metroport:models/blackrazid/mutants/strazh/leftleg.obj");
   private MutantObjPart rightleg = new MutantObjPart("metroport:models/blackrazid/mutants/strazh/rightleg.obj");
   private MutantObjPart leftarm = new MutantObjPart("metroport:models/blackrazid/mutants/strazh/leftarm.obj");
   private MutantObjPart rightarm = new MutantObjPart("metroport:models/blackrazid/mutants/strazh/rightarm.obj");


   public void render(Entity entity, float par1, float par2, float par4, float par5, float par6, float par7) {
      float handCurRot2 = MathHelper.cos(par1 * 0.6662F + 3.1415927F) * 1.4F * par2;
      GL11.glPushMatrix();
      GL11.glShadeModel(7425);
      GL11.glTranslatef(0.0F, 1.5F, 0.75F);
      GL11.glRotatef(195.0F, 1.0F, 0.0F, 0.0F);
      this.strazh.render();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.05F, 1.771F, 0.863F);
      GL11.glRotatef(0.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 10.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 20.0F, 1.0F, 0.0F, 0.0F);
      this.leftarm.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.05F, 1.771F, 0.863F);
      GL11.glRotatef(0.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 10.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 20.0F, 1.0F, 0.0F, 0.0F);
      this.rightarm.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.02F, 1.35F, 0.2F);
      GL11.glRotatef(-handCurRot2 * 30.0F, 1.0F, 0.0F, 0.0F);
      this.rightleg.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.02F, 1.35F, 0.2F);
      GL11.glRotatef(-handCurRot2 * 30.0F, 1.0F, 0.0F, 0.0F);
      this.leftleg.render();
      GL11.glPopMatrix();
      GL11.glPopMatrix();
   }
}
