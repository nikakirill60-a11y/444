// Original BlackRazid model; package/API adaptation only.
package ru.metrocraft.port.client.blackrazid.gadgets;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class SekArrowModel extends ModelBase {

   ModelRenderer Shape55;
   ModelRenderer Shape56;


   public SekArrowModel() {
      super.textureWidth = 128;
      super.textureHeight = 128;
      this.Shape55 = new ModelRenderer(this, 17, 21);
      this.Shape55.addBox(-2.5F, -0.5F, 0.3F, 23, 1, 1);
      this.Shape55.setRotationPoint(12.0F, -3.8F, -2.0F);
      this.Shape55.setTextureSize(128, 128);
      this.Shape55.mirror = true;
      this.setRotation(this.Shape55, 0.0F, 0.0F, -1.5707964F);
      this.Shape56 = new ModelRenderer(this, 17, 17);
      this.Shape56.addBox(-1.0F, -1.0F, 0.3F, 2, 2, 1);
      this.Shape56.setRotationPoint(12.0F, -3.8F, -2.0F);
      this.Shape56.setTextureSize(128, 128);
      this.Shape56.mirror = true;
      this.setRotation(this.Shape56, 0.0F, 0.0F, -1.5707964F);
   }

   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.render(var1, var2, var3, var4, var5, var6, var7);
      this.setRotationAngles(var2, var3, var4, var5, var6, var7, var1);
      this.Shape55.render(var7);
      this.Shape56.render(var7);
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
