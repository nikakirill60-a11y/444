package mods.provoda.guns.models.clock;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class HourArrowModel extends ModelBase {

   ModelRenderer Shape42;
   ModelRenderer Shape46;


   public HourArrowModel() {
      super.textureWidth = 128;
      super.textureHeight = 128;
      this.Shape42 = new ModelRenderer(this, 1, 62);
      this.Shape42.addBox(-4.0F, -1.0F, 1.6F, 15, 2, 1);
      this.Shape42.setRotationPoint(12.0F, -3.8F, -2.0F);
      this.Shape42.setTextureSize(128, 128);
      this.Shape42.mirror = true;
      this.setRotation(this.Shape42, 0.0F, 0.0F, -1.5707964F);
      this.Shape46 = new ModelRenderer(this, 1, 57);
      this.Shape46.addBox(-1.5F, -1.5F, 1.6F, 3, 3, 1);
      this.Shape46.setRotationPoint(12.0F, -3.8F, -2.0F);
      this.Shape46.setTextureSize(128, 128);
      this.Shape46.mirror = true;
      this.setRotation(this.Shape46, 0.0F, 0.0F, -1.5707964F);
   }

   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.render(var1, var2, var3, var4, var5, var6, var7);
      this.setRotationAngles(var2, var3, var4, var5, var6, var7, var1);
      this.Shape42.render(var7);
      this.Shape46.render(var7);
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
