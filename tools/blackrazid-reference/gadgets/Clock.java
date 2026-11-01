package mods.provoda.guns.models.clock;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class Clock extends ModelBase
{
  //fields
    ModelRenderer Shape1;
    ModelRenderer Shape2;
    ModelRenderer Shape3;
    ModelRenderer Shape4;
    ModelRenderer Shape5;
    ModelRenderer Shape6;
    ModelRenderer Shape7;
    ModelRenderer Shape8;
    ModelRenderer Shape9;
    ModelRenderer Shape10;
    ModelRenderer Shape11;
    ModelRenderer Shape12;
    ModelRenderer Shape13;
    ModelRenderer Shape14;
  
  public Clock()
  {
    super.textureWidth = 256;
    super.textureHeight = 256;
    
      this.Shape1 = new ModelRenderer(this, 144, 131);
      this.Shape1.addBox(0F, 0F, 0F, 50, 50, 1);
      this.Shape1.setRotationPoint(-12.9F, -29F, 3.5F);
      this.Shape1.setTextureSize(256, 256);
      this.Shape1.mirror = true;
      this.setRotation(this.Shape1, 0F, 0F, 0F);
      this.Shape2 = new ModelRenderer(this, 1, 15);
      this.Shape2.addBox(-0.5F, -0.5F, -0.2F, 1, 1, 6);
      this.Shape2.setRotationPoint(12F, -3.8F, -2F);
      this.Shape2.setTextureSize(256, 256);
      this.Shape2.mirror = true;
      this.setRotation(this.Shape2, 0F, 0F, 0F);
      this.Shape3 = new ModelRenderer(this, 1, 25);
      this.Shape3.addBox(0F, -1F, 0F, 2, 3, 6);
      this.Shape3.setRotationPoint(36.5F, -5F, -2F);
      this.Shape3.setTextureSize(256, 256);
      this.Shape3.mirror = true;
      this.setRotation(this.Shape3, 0F, 0F, 1.570796F);
      this.Shape4 = new ModelRenderer(this, 1, 25);
      this.Shape4.addBox(0F, 0F, 0F, 2, 3, 6);
      this.Shape4.setRotationPoint(-10F, -5F, -2F);
      this.Shape4.setTextureSize(256, 256);
      this.Shape4.mirror = true;
      this.setRotation(this.Shape4, 0F, 0F, 1.570796F);
      this.Shape5 = new ModelRenderer(this, 1, 25);
      this.Shape5.addBox(0F, 0F, 0F, 2, 3, 6);
      this.Shape5.setRotationPoint(11.4F, 18F, -2F);
      this.Shape5.setTextureSize(256, 256);
      this.Shape5.mirror = true;
      this.setRotation(this.Shape5, 0F, 0F, 0F);
      this.Shape6 = new ModelRenderer(this, 35, 25);
      this.Shape6.addBox(0F, 0F, 0F, 2, 3, 6);
      this.Shape6.setRotationPoint(11F, -29F, -2F);
      this.Shape6.setTextureSize(256, 256);
      this.Shape6.mirror = true;
      this.setRotation(this.Shape6, 0F, 0F, 0F);
      this.Shape7 = new ModelRenderer(this, 202, 35);
      this.Shape7.addBox(0F, 0F, 0F, 14, 34, 6);
      this.Shape7.setRotationPoint(29F, -34F, -0.5F);
      this.Shape7.setTextureSize(256, 256);
      this.Shape7.mirror = true;
      this.setRotation(this.Shape7, 0F, 0F, 1.570796F);
      this.Shape8 = new ModelRenderer(this, 170, 35);
      this.Shape8.addBox(0F, 0F, 0F, 14, 34, 6);
      this.Shape8.setRotationPoint(29F, 12F, -1.5F);
      this.Shape8.setTextureSize(256, 256);
      this.Shape8.mirror = true;
      this.setRotation(this.Shape8, 0F, 0F, 1.570796F);
      this.Shape9 = new ModelRenderer(this, 104, 0);
      this.Shape9.addBox(0F, 0F, 0F, 1, 18, 4);
      this.Shape9.setRotationPoint(-11F, -3F, -1F);
      this.Shape9.setTextureSize(256, 256);
      this.Shape9.mirror = true;
      this.setRotation(this.Shape9, 0F, 0F, -0.4461433F);
      this.Shape10 = new ModelRenderer(this, 104, 0);
      this.Shape10.addBox(0F, 0F, 0F, 1, 18, 4);
      this.Shape10.setRotationPoint(27F, -21F, -1F);
      this.Shape10.setTextureSize(256, 256);
      this.Shape10.mirror = true;
      this.setRotation(this.Shape10, 0F, 0F, -0.4461433F);
      this.Shape11 = new ModelRenderer(this, 104, 0);
      this.Shape11.addBox(0F, 0F, 0F, 1, 18, 4);
      this.Shape11.setRotationPoint(35F, -3F, -1F);
      this.Shape11.setTextureSize(256, 256);
      this.Shape11.mirror = true;
      this.setRotation(this.Shape11, 0F, 0F, 0.426418F);
      this.Shape12 = new ModelRenderer(this, 104, 0);
      this.Shape12.addBox(0F, 0F, 0F, 1, 18, 4);
      this.Shape12.setRotationPoint(-4F, -21F, -1F);
      this.Shape12.setTextureSize(256, 256);
      this.Shape12.mirror = true;
      this.setRotation(this.Shape12, 0F, 0F, 0.426418F);
      this.Shape13 = new ModelRenderer(this, 4, 191);
      this.Shape13.addBox(0F, 0F, 0F, 34, 3, 60);
      this.Shape13.setRotationPoint(-5F, 23F, -2F);
      this.Shape13.setTextureSize(256, 256);
      this.Shape13.mirror = true;
      this.setRotation(this.Shape13, 0F, 0F, 0F);
      this.Shape14 = new ModelRenderer(this, 4, 191);
      this.Shape14.addBox(0F, 0F, 0F, 34, 3, 60);
      this.Shape14.setRotationPoint(-5F, -34F, 0F);
      this.Shape14.setTextureSize(256, 256);
      this.Shape14.mirror = true;
      this.setRotation(this.Shape14, 0F, 0F, 0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    this.Shape1.render(f5);
    this.Shape2.render(f5);
    this.Shape3.render(f5);
    this.Shape4.render(f5);
    this.Shape5.render(f5);
    this.Shape6.render(f5);
    this.Shape7.render(f5);
    this.Shape8.render(f5);
    this.Shape9.render(f5);
    this.Shape10.render(f5);
    this.Shape11.render(f5);
    this.Shape12.render(f5);
    this.Shape13.render(f5);
    this.Shape14.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity ent)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5, ent);
  }

}
