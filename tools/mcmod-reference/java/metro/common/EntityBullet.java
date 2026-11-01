/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.projectile.EntityThrowable
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.world.World
 */
package metro.common;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityBullet
extends EntityThrowable {
    private double bulletdamage;
    private int ticks;
    public EntityPlayer shootingEntity;

    public EntityBullet(World par1World) {
        super(par1World);
        this.func_70105_a(0.1f, 0.1f);
    }

    public EntityBullet(World par1World, EntityPlayer par2EntityLiving, double damage, float accuracy) {
        super(par1World, (EntityLivingBase)par2EntityLiving);
        this.bulletdamage = damage;
        this.shootingEntity = par2EntityLiving;
        this.func_70105_a(0.1f, 0.1f);
        this.func_70012_b(par2EntityLiving.field_70165_t, par2EntityLiving.field_70163_u + (double)par2EntityLiving.func_70047_e(), par2EntityLiving.field_70161_v, par2EntityLiving.field_70177_z, par2EntityLiving.field_70125_A);
        this.field_70165_t -= (double)(MathHelper.func_76134_b((float)(this.field_70177_z / 180.0f * (float)Math.PI)) * 0.16f);
        this.field_70163_u -= (double)0.1f;
        this.field_70161_v -= (double)(MathHelper.func_76126_a((float)(this.field_70177_z / 180.0f * (float)Math.PI)) * 0.16f);
        this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
        this.field_70129_M = 0.0f;
        this.field_70159_w = -MathHelper.func_76126_a((float)(this.field_70177_z / 180.0f * (float)Math.PI)) * MathHelper.func_76134_b((float)(this.field_70125_A / 180.0f * (float)Math.PI));
        this.field_70179_y = MathHelper.func_76134_b((float)(this.field_70177_z / 180.0f * (float)Math.PI)) * MathHelper.func_76134_b((float)(this.field_70125_A / 180.0f * (float)Math.PI));
        this.field_70181_x = -MathHelper.func_76126_a((float)(this.field_70125_A / 180.0f * (float)Math.PI));
        this.func_70186_c(this.field_70159_w, this.field_70181_x, this.field_70179_y, 20.0f, accuracy);
    }

    public EntityBullet(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }

    protected float func_70185_h() {
        return 0.001f;
    }

    public void func_70016_h(double par1, double par3, double par5) {
        this.field_70159_w = par1;
        this.field_70181_x = par3;
        this.field_70179_y = par5;
        if (this.field_70127_C == 0.0f && this.field_70126_B == 0.0f) {
            float f = MathHelper.func_76133_a((double)(par1 * par1 + par5 * par5));
            this.field_70126_B = this.field_70177_z = (float)(Math.atan2(par1, par5) * 180.0 / Math.PI);
            this.field_70127_C = this.field_70125_A = (float)(Math.atan2(par3, f) * 180.0 / Math.PI);
            this.field_70127_C = this.field_70125_A;
            this.field_70126_B = this.field_70177_z;
            this.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
        }
    }

    public void setArrowHeading(double par1, double par3, double par5, float par7, float par8) {
        float f = MathHelper.func_76133_a((double)(par1 * par1 + par3 * par3 + par5 * par5));
        par1 /= (double)f;
        par3 /= (double)f;
        par5 /= (double)f;
        par1 += this.field_70146_Z.nextGaussian() * (double)0.0075f * (double)par8;
        par3 += this.field_70146_Z.nextGaussian() * (double)0.0075f * (double)par8;
        par5 += this.field_70146_Z.nextGaussian() * (double)0.0075f * (double)par8;
        this.field_70159_w = par1 *= (double)par7;
        this.field_70181_x = par3 *= (double)par7;
        this.field_70179_y = par5 *= (double)par7;
        float f1 = MathHelper.func_76133_a((double)(par1 * par1 + par5 * par5));
        this.field_70126_B = this.field_70177_z = (float)(Math.atan2(par1, par5) * 180.0 / Math.PI);
        this.field_70127_C = this.field_70125_A = (float)(Math.atan2(par3, f1) * 180.0 / Math.PI);
    }

    protected void func_70184_a(MovingObjectPosition par1MovingObjectPosition) {
        if (par1MovingObjectPosition.field_72308_g != null) {
            double var2 = this.bulletdamage;
            if (par1MovingObjectPosition.field_72308_g.func_70097_a(DamageSource.func_76365_a((EntityPlayer)this.shootingEntity), (float)var2)) {
                this.func_70106_y();
            }
        }
        if (this.field_70170_p.func_147439_a(par1MovingObjectPosition.field_72311_b, par1MovingObjectPosition.field_72312_c, par1MovingObjectPosition.field_72309_d) == Blocks.field_150359_w || this.field_70170_p.func_147439_a(par1MovingObjectPosition.field_72311_b, par1MovingObjectPosition.field_72312_c, par1MovingObjectPosition.field_72309_d) == Blocks.field_150410_aZ || this.field_70170_p.func_147439_a(par1MovingObjectPosition.field_72311_b, par1MovingObjectPosition.field_72312_c, par1MovingObjectPosition.field_72309_d) == Blocks.field_150426_aN || this.field_70170_p.func_147439_a(par1MovingObjectPosition.field_72311_b, par1MovingObjectPosition.field_72312_c, par1MovingObjectPosition.field_72309_d) == Blocks.field_150399_cn || this.field_70170_p.func_147439_a(par1MovingObjectPosition.field_72311_b, par1MovingObjectPosition.field_72312_c, par1MovingObjectPosition.field_72309_d) == Blocks.field_150397_co) {
            this.func_70106_y();
        }
        if (this.field_70170_p.func_147439_a(par1MovingObjectPosition.field_72311_b, par1MovingObjectPosition.field_72312_c, par1MovingObjectPosition.field_72309_d).func_149688_o() == Material.field_151576_e) {
            this.func_70106_y();
        }
        if (!this.field_70170_p.field_72995_K && this.field_70170_p.func_147439_a(par1MovingObjectPosition.field_72311_b, par1MovingObjectPosition.field_72312_c, par1MovingObjectPosition.field_72309_d) != Blocks.field_150329_H && this.field_70170_p.func_147439_a(par1MovingObjectPosition.field_72311_b, par1MovingObjectPosition.field_72312_c, par1MovingObjectPosition.field_72309_d) != Blocks.field_150398_cm) {
            this.func_70106_y();
        }
    }
}
