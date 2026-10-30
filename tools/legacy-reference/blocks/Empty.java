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
package blocks;

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

