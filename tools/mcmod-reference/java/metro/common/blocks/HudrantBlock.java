/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  metro.common.blocks.tileentity.TileEntityHudrant
 *  net.minecraft.block.Block
 *  net.minecraft.block.ITileEntityProvider
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.MathHelper
 *  net.minecraft.world.World
 */
package metro.common.blocks;

import metro.common.Main;
import metro.common.blocks.tileentity.TileEntityHudrant;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class HudrantBlock
extends Block
implements ITileEntityProvider {
    public String model_name;
    public String texture_name;
    public float scale;

    public HudrantBlock(Material p_i45394_1_, String model_name, String texture_name, float scale) {
        super(Material.field_151576_e);
        this.model_name = model_name;
        this.texture_name = texture_name;
        this.scale = scale;
        this.func_149647_a(Main.dekorTab);
        this.func_149722_s();
        this.func_149752_b(6000000.0f);
        this.func_149672_a(field_149777_j);
        this.func_149658_d("iron_block");
    }

    public void func_149689_a(World world, int i1, int i2, int i3, EntityLivingBase en, ItemStack is) {
        int rotation = MathHelper.func_76128_c((double)((double)(en.field_70177_z * 4.0f / 360.0f) + 2.5)) & 3;
        world.func_72921_c(i1, i2, i3, rotation, 2);
    }

    public TileEntity func_149915_a(World world, int metadata) {
        return new TileEntityHudrant();
    }

    public int func_149645_b() {
        return -1;
    }

    public boolean func_149662_c() {
        return false;
    }

    public boolean func_149686_d() {
        return false;
    }
}
