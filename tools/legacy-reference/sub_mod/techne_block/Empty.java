/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 */
package sub_mod.techne_block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;

public class Empty
extends Block {
    public Empty() {
        super(Material.field_151576_e);
        this.func_149647_a(CreativeTabs.field_78030_b);
        this.func_149663_c("Empty");
        this.func_149713_g(1);
        this.func_149658_d("sub_mod_techne_block:empty");
        this.func_149711_c(-1.0f);
        this.func_149752_b(1.0E7f);
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.field_149761_L = par1IconRegister.func_94245_a("provoda:empty");
    }

    public boolean func_149686_d() {
        return false;
    }

    public boolean func_149662_c() {
        return false;
    }
}

