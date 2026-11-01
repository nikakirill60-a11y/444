/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.Minecraft
 */
package metro.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import metro.common.Main;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;

public class CollisionBlock
extends Block {
    public String model_name;
    public String texture_name;
    public float scale;
    public static String texture = "metro:collision";

    public CollisionBlock(Material p_i45394_1_) {
        super(Material.field_151576_e);
        this.func_149647_a(Main.dekorTab);
        this.func_149722_s();
        this.func_149752_b(6000000.0f);
        this.func_149672_a(field_149777_j);
        this.func_149658_d(texture);
    }

    @SideOnly(value=Side.CLIENT)
    public int func_149645_b() {
        if (Minecraft.func_71410_x().field_71439_g.field_71075_bZ.field_75098_d) {
            return 0;
        }
        return -1;
    }

    public boolean func_149662_c() {
        return false;
    }

    public boolean func_149686_d() {
        return true;
    }
}
