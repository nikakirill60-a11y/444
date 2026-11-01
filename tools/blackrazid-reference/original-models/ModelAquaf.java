package mods.provoda.mobs.models.aqua_female;

import mods.provoda.GLLoader.GLModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelAquaf extends ModelBase {

   private GLModel strazh = new GLModel("/mods/provoda/mobs/models/aqua_female/aqua.obj");
   private GLModel hvost = new GLModel("/mods/provoda/mobs/models/aqua_female/hvost.obj");
   private GLModel leftarm = new GLModel("/mods/provoda/mobs/models/aqua_female/leftleg.obj");
   private GLModel rightarm = new GLModel("/mods/provoda/mobs/models/aqua_female/rightleg.obj");


   public void render(Entity entity, float par1, float par2, float par4, float par5, float par6, float par7) {
      float handCurRot2 = MathHelper.cos(par1 * 0.6662F + 3.1415927F) * 1.4F * par2;
      EntityLiving entity1 = (EntityLiving)entity;
      GL11.glPushMatrix();
      GL11.glShadeModel(7425);
      GL11.glTranslatef(0.0F, 2.2F, -0.5F);
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      this.strazh.render();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.0F, 1.659F, -0.119F);
      GL11.glRotatef(0.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 10.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 20.0F, 1.0F, 0.0F, 0.0F);
      this.leftarm.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 1.659F, -0.119F);
      GL11.glRotatef(0.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(handCurRot2 * 10.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(-handCurRot2 * 20.0F, 1.0F, 0.0F, 0.0F);
      this.rightarm.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 0.0F, -1.041F);
      GL11.glRotatef(handCurRot2 * 30.0F, 0.0F, 1.0F, 0.0F);
      this.hvost.render();
      GL11.glPopMatrix();
      GL11.glPopMatrix();
   }
}
