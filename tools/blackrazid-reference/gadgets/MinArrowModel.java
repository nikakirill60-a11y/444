package mods.provoda.guns.models.clock;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class MinArrowModel extends ModelBase {

   ModelRenderer Shape41;
   ModelRenderer Shape47;


   public MinArrowModel() {
      super.textureWidth = 128;
      super.textureHeight = 128;
      this.Shape41 = new ModelRenderer(this, 1, 50);
      this.Shape41.addBox(-4.0F, -1.0F, 3.0F, 21, 2, 1);
      this.Shape41.setRotationPoint(12.0F, -3.8F, -2.0F);
      this.Shape41.setTextureSize(128, 128);
      this.Shape41.mirror = true;
      this.setRotation(this.Shape41, 0.0F, 0.0F, -1.5707964F);
      this.Shape47 = new ModelRenderer(this, 1, 45);
      this.Shape47.addBox(-1.5F, -1.5F, 3.0F, 3, 3, 1);
      this.Shape47.setRotationPoint(12.0F, -3.8F, -2.0F);
      this.Shape47.setTextureSize(128, 128);
      this.Shape47.mirror = true;
      this.setRotation(this.Shape47, 0.0F, 0.0F, -1.5707964F);
   }

   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.render(var1, var2, var3, var4, var5, var6, var7);
      this.setRotationAngles(var2, var3, var4, var5, var6, var7, var1);
      this.Shape41.render(var7);
      this.Shape47.render(var7);
   }

   private void setRotation(ModelRenderer var1, float var2, float var3, float var4) {
      var1.rotateAngleX = var2;
      var1.rotateAngleY = var3;
      var1.rotateAngleZ = var4;
   }

   public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.setRotationAngles(var1, var2, var3, var4, var5, var6, var7);
   }
}
