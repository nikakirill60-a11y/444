/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.ITileEntityProvider
 *  net.minecraft.block.material.Material
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.MathHelper
 *  net.minecraft.world.World
 */
package sub_mod.techne_block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import sub_mod.techne_block.ModBlockTechne;
import sub_mod.techne_block.tiles.TileGermoLeft;

public class GermoLeft
extends Block
implements ITileEntityProvider {
    public GermoLeft() {
        super(Material.field_151594_q);
        this.func_149647_a(CreativeTabs.field_78030_b);
        this.func_149711_c(0.25f);
        this.func_149672_a(field_149777_j);
        this.func_149658_d("iron_block");
        this.func_149663_c("GermoLeft");
        this.func_149676_a(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }

    public TileEntity func_149915_a(World world, int metadata) {
        return new TileGermoLeft();
    }

    public int func_149645_b() {
        return -1;
    }

    public boolean func_149662_c() {
        return false;
    }

    public void func_149689_a(World w, int par2, int par3, int par4, EntityLivingBase living, ItemStack stack) {
        w.func_147464_a(par2, par3, par4, (Block)this, 0);
        int l = MathHelper.func_76128_c((double)((double)(living.field_70177_z * 4.0f / 360.0f) + 0.5)) & 3;
        int i1 = w.func_72805_g(par2, par3, par4) >> 2;
        ++l;
        if ((l %= 4) == 0) {
            w.func_72921_c(par2, par3, par4, 0 | i1 << 2, 2);
            w.func_147449_b(par2, par3, par4 + 3, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3, par4 + 1, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3, par4 + 4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3, par4 + 2, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 1, par4 + 3, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 1, par4 + 1, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 1, par4 + 4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 1, par4 + 2, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 2, par4 + 3, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 2, par4 + 1, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 2, par4 + 4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 2, par4 + 2, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 2, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 - 1, par4 + 3, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 - 1, par4 + 1, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 - 1, par4 + 4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 - 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 - 1, par4 + 2, ModBlockTechne.Empty);
        }
        if (l == 1) {
            w.func_72921_c(par2, par3, par4, 1 | i1 << 2, 2);
            w.func_147449_b(par2 - 3, par3, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 - 4, par3, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 - 1, par3, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 - 2, par3, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 - 4, par3 + 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 - 1, par3 + 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 - 3, par3 + 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 - 2, par3 + 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 - 3, par3 + 2, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 - 1, par3 + 2, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 - 4, par3 + 2, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 - 2, par3 + 2, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 2, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 - 3, par3 - 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 - 1, par3 - 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 - 4, par3 - 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 - 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 - 2, par3 - 1, par4, ModBlockTechne.Empty);
        }
        if (l == 2) {
            w.func_72921_c(par2, par3, par4, 2 | i1 << 2, 2);
            w.func_147449_b(par2, par3, par4 - 1, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3, par4 - 3, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3, par4 - 2, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3, par4 - 4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 1, par4 - 1, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 1, par4 - 3, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 1, par4 - 2, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 1, par4 - 4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 2, par4 - 1, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 2, par4 - 3, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 2, par4 - 2, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 2, par4 - 4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 2, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 - 1, par4 - 1, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 - 1, par4 - 3, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 - 1, par4 - 4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 - 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 - 1, par4 - 2, ModBlockTechne.Empty);
        }
        if (l == 3) {
            w.func_72921_c(par2, par3, par4, 3 | i1 << 2, 2);
            w.func_147449_b(par2 + 1, par3, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 + 3, par3, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 + 2, par3, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 + 4, par3, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 + 1, par3 + 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 + 3, par3 + 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 + 2, par3 + 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 + 4, par3 + 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 + 1, par3 + 2, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 + 3, par3 + 2, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 + 2, par3 + 2, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 + 4, par3 + 2, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 + 2, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 + 1, par3 - 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 + 3, par3 - 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 + 4, par3 - 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2, par3 - 1, par4, ModBlockTechne.Empty);
            w.func_147449_b(par2 + 2, par3 - 1, par4, ModBlockTechne.Empty);
        }
    }

    public void func_149664_b(World w, int par2, int par3, int par4, int par5) {
        if (par5 == 2) {
            w.func_147468_f(par2, par3, par4 - 1);
            w.func_147468_f(par2, par3, par4 - 3);
            w.func_147468_f(par2, par3, par4 - 2);
            w.func_147468_f(par2, par3, par4 - 4);
            w.func_147468_f(par2, par3 + 1, par4 - 1);
            w.func_147468_f(par2, par3 + 1, par4 - 3);
            w.func_147468_f(par2, par3 + 1, par4 - 2);
            w.func_147468_f(par2, par3 + 1, par4 - 4);
            w.func_147468_f(par2, par3 + 1, par4);
            w.func_147468_f(par2, par3 + 2, par4 - 1);
            w.func_147468_f(par2, par3 + 2, par4 - 3);
            w.func_147468_f(par2, par3 + 2, par4 - 2);
            w.func_147468_f(par2, par3 + 2, par4 - 4);
            w.func_147468_f(par2, par3 + 2, par4);
            w.func_147468_f(par2, par3 - 1, par4 - 1);
            w.func_147468_f(par2, par3 - 1, par4 - 3);
            w.func_147468_f(par2, par3 - 1, par4 - 4);
            w.func_147468_f(par2, par3 - 1, par4);
            w.func_147468_f(par2, par3 - 1, par4 - 2);
        }
        if (par5 == 3) {
            w.func_147468_f(par2 + 1, par3, par4);
            w.func_147468_f(par2 + 3, par3, par4);
            w.func_147468_f(par2 + 2, par3, par4);
            w.func_147468_f(par2 + 4, par3, par4);
            w.func_147468_f(par2 + 1, par3 + 1, par4);
            w.func_147468_f(par2 + 3, par3 + 1, par4);
            w.func_147468_f(par2 + 2, par3 + 1, par4);
            w.func_147468_f(par2 + 4, par3 + 1, par4);
            w.func_147468_f(par2, par3 + 1, par4);
            w.func_147468_f(par2 + 1, par3 + 2, par4);
            w.func_147468_f(par2 + 3, par3 + 2, par4);
            w.func_147468_f(par2 + 2, par3 + 2, par4);
            w.func_147468_f(par2 + 4, par3 + 2, par4);
            w.func_147468_f(par2, par3 + 2, par4);
            w.func_147468_f(par2 + 1, par3 - 1, par4);
            w.func_147468_f(par2 + 3, par3 - 1, par4);
            w.func_147468_f(par2 + 4, par3 - 1, par4);
            w.func_147468_f(par2, par3 - 1, par4);
            w.func_147468_f(par2 + 2, par3 - 1, par4);
        }
        if (par5 == 0) {
            w.func_147468_f(par2, par3, par4 + 3);
            w.func_147468_f(par2, par3, par4 + 1);
            w.func_147468_f(par2, par3, par4 + 4);
            w.func_147468_f(par2, par3, par4 + 2);
            w.func_147468_f(par2, par3 + 1, par4 + 3);
            w.func_147468_f(par2, par3 + 1, par4 + 1);
            w.func_147468_f(par2, par3 + 1, par4 + 4);
            w.func_147468_f(par2, par3 + 1, par4 + 2);
            w.func_147468_f(par2, par3 + 1, par4);
            w.func_147468_f(par2, par3 + 2, par4 + 3);
            w.func_147468_f(par2, par3 + 2, par4 + 1);
            w.func_147468_f(par2, par3 + 2, par4 + 4);
            w.func_147468_f(par2, par3 + 2, par4 + 2);
            w.func_147468_f(par2, par3 + 2, par4);
            w.func_147468_f(par2, par3 - 1, par4 + 3);
            w.func_147468_f(par2, par3 - 1, par4 + 1);
            w.func_147468_f(par2, par3 - 1, par4 + 4);
            w.func_147468_f(par2, par3 - 1, par4);
            w.func_147468_f(par2, par3 - 1, par4 + 2);
        }
        if (par5 == 1) {
            w.func_147468_f(par2 - 4, par3, par4);
            w.func_147468_f(par2 - 1, par3, par4);
            w.func_147468_f(par2 - 3, par3, par4);
            w.func_147468_f(par2 - 2, par3, par4);
            w.func_147468_f(par2 - 3, par3 + 1, par4);
            w.func_147468_f(par2 - 1, par3 + 1, par4);
            w.func_147468_f(par2 - 4, par3 + 1, par4);
            w.func_147468_f(par2 - 2, par3 + 1, par4);
            w.func_147468_f(par2, par3 + 1, par4);
            w.func_147468_f(par2 - 3, par3 + 2, par4);
            w.func_147468_f(par2 - 1, par3 + 2, par4);
            w.func_147468_f(par2 - 4, par3 + 2, par4);
            w.func_147468_f(par2 - 2, par3 + 2, par4);
            w.func_147468_f(par2, par3 + 2, par4);
            w.func_147468_f(par2 - 3, par3 - 1, par4);
            w.func_147468_f(par2 - 1, par3 - 1, par4);
            w.func_147468_f(par2 - 4, par3 - 1, par4);
            w.func_147468_f(par2, par3 - 1, par4);
            w.func_147468_f(par2 - 2, par3 - 1, par4);
        }
    }

    public void func_149674_a(World w, int par2, int par3, int par4, Random var5) {
        w.func_147464_a(par2, par3, par4, (Block)this, 0);
    }

    public int func_149738_a(World var1) {
        return 1;
    }

    public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
        var1.func_147464_a(var2, var3, var4, (Block)this, 4);
    }

    public boolean func_149686_d() {
        return false;
    }
}

