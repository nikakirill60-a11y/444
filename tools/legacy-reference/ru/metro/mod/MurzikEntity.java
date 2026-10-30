/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.monster.EntityMob
 *  net.minecraft.world.World
 */
package ru.metro.mod;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;

public class MurzikEntity
extends EntityMob {
    public MurzikEntity(World par1World) {
        super(par1World);
        this.func_70105_a(2.0f, 1.0f);
    }

    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(25.0);
        this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(25.0);
        this.func_110148_a(SharedMonsterAttributes.field_111266_c).func_111128_a(3.0);
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(100.0);
        this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(2.0);
    }
}

