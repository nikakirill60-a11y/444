// Generated from the user-supplied original model by import_blackrazid_mutants.py.
package ru.metrocraft.port.client.blackrazid.mutants;


import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ModelRat extends ModelBase {

   private MutantObjPart strazh = new MutantObjPart("metroport:models/blackrazid/mutants/rat/rat.obj");


   public void render(Entity entity, float par1, float par2, float par4, float par5, float par6, float par7) {
      GL11.glPushMatrix();
      GL11.glShadeModel(7425);
      GL11.glTranslatef(0.0F, 1.5F, 0.0F);
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      this.strazh.render();
      GL11.glPopMatrix();
   }
}
