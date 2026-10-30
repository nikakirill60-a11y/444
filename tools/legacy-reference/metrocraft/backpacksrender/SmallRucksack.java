/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.entity.RenderPlayer
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  org.lwjgl.opengl.GL11
 */
package metrocraft.backpacksrender;

import metrocraft.main.MetroResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class SmallRucksack
extends Item {
    public SmallRucksack() {
        this.func_77637_a(CreativeTabs.field_78037_j);
        this.func_77625_d(1);
        this.func_77655_b("smallRucksack");
    }

    public void func_94581_a(IIconRegister par1IconRegister) {
        this.field_77791_bV = par1IconRegister.func_94245_a("metrocraft:smallrucksackicon");
    }

    public static void render(RenderPlayer renderModel) {
        GL11.glPushMatrix();
        Minecraft.func_71410_x().field_71446_o.func_110577_a(MetroResources.smallRucksackTexture);
        renderModel.field_77109_a.field_78115_e.func_78794_c(0.0625f);
        GL11.glRotatef((float)180.0f, (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glScalef((float)0.9f, (float)0.9f, (float)0.9f);
        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.025f);
        MetroResources.smallRucksackModel.renderAll();
        GL11.glPopMatrix();
    }
}

