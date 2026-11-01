/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  metro.common.blocks.tileentity.TileEntityRad
 *  net.minecraft.block.Block
 *  net.minecraft.block.ITileEntityProvider
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.Minecraft
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.world.World
 */
package metro.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import metro.client.events.LeftClickingEvent;
import metro.common.Main;
import metro.common.blocks.tileentity.TileEntityRad;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class AirBlock
extends Block
implements ITileEntityProvider {
    public String model_name;
    public String texture_name;
    public float scale;

    public AirBlock(Material p_i45394_1_, String model_name, String texture_name, float scale) {
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

    @SideOnly(value=Side.CLIENT)
    public void func_149734_b(World w, int x, int y, int z, Random p_149734_5_) {
        double posx = Minecraft.func_71410_x().field_71439_g.field_70165_t;
        double posy = Minecraft.func_71410_x().field_71439_g.field_70163_u;
        double posz = Minecraft.func_71410_x().field_71439_g.field_70161_v;
        int lodx = Math.abs((int)((double)x + 0.5 - posx));
        int lody = Math.abs((int)((double)(y + 5) - posy));
        int lodz = Math.abs((int)((double)z + 0.5 - posz));
        int radius = 5;
        if (lodx < radius && lody < radius && lodz < radius) {
            LeftClickingEvent.isRad = false;
        }
    }

    public TileEntity func_149915_a(World world, int metadata) {
        return new TileEntityRad();
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

    public AxisAlignedBB func_149668_a(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_) {
        return null;
    }
}
