/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.DamageSource
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package blocks;

import com.flansmod.common.FlansMod;
import com.flansmod.common.teams.ArmourType;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class Radiation
extends Block {
    int i = 0;
    public ArmourType type;

    public Radiation(int var1) {
        super(Material.field_151578_c);
        this.func_149722_s();
        this.func_149647_a(FlansMod.tabFlanParts);
        this.func_149658_d("sub_mod_techne_block:empty");
        float var2 = 0.0f;
        float var3 = 0.0f;
        this.func_149676_a(0.0f + var3, 0.0f + var2, 0.0f + var3, 1.0f + var3, 1.0f + var2, 1.0f + var3);
    }

    public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
        return null;
    }

    public boolean func_149662_c() {
        return false;
    }

    public boolean func_149686_d() {
        return false;
    }

    public boolean func_149655_b(IBlockAccess var1, int var2, int var3, int var4) {
        return this.field_149764_J != Material.field_151587_i;
    }

    public void func_149689_a(World world, int x, int y, int z, EntityLivingBase eLB, ItemStack itemStack) {
        world.func_147464_a(x, y, z, (Block)this, 0);
    }

    public void func_149674_a(World world, int x, int y, int z, Random rand) {
        world.func_147464_a(x, y, z, (Block)this, 0);
        int radius = 10;
        ++this.i;
        AxisAlignedBB box = AxisAlignedBB.func_72330_a((double)(x - radius), (double)(y - radius), (double)(z - radius), (double)(x + radius), (double)(y + radius), (double)(z + radius));
        List entityLivings = world.func_72872_a(EntityLivingBase.class, box);
        for (EntityLivingBase entity : entityLivings) {
            if (!(entity instanceof EntityPlayer)) continue;
            EntityPlayer var6 = (EntityPlayer)entity;
            if (var6.field_71075_bZ.field_75098_d) continue;
            if (var6.field_71071_by.func_70440_f(3) != null) {
                if (var6.field_71071_by.func_70440_f(3).func_77978_p() != null) {
                    if (var6.field_71071_by.func_70440_f(3).func_77978_p().func_74767_n("isGasMask")) {
                        if (var6.field_71071_by.func_70440_f(3).func_77978_p().func_74762_e("Time_Left") > 0) continue;
                        var6.func_70097_a(DamageSource.field_76377_j, 3.0f);
                        continue;
                    }
                    var6.func_70097_a(DamageSource.field_76377_j, 3.0f);
                    continue;
                }
                var6.func_70097_a(DamageSource.field_76377_j, 3.0f);
                continue;
            }
            if (var6.field_71071_by.func_70440_f(2) != null) {
                if (var6.field_71071_by.func_70440_f(2).func_77978_p() != null) {
                    if (var6.field_71071_by.func_70440_f(2).func_77978_p().func_74767_n("isGasMask")) {
                        if (var6.field_71071_by.func_70440_f(2).func_77978_p().func_74762_e("Time_Left") > 0) continue;
                        var6.func_70097_a(DamageSource.field_76377_j, 3.0f);
                        continue;
                    }
                    var6.func_70097_a(DamageSource.field_76377_j, 3.0f);
                    continue;
                }
                var6.func_70097_a(DamageSource.field_76377_j, 3.0f);
                continue;
            }
            var6.func_70097_a(DamageSource.field_76377_j, 3.0f);
        }
    }
}

