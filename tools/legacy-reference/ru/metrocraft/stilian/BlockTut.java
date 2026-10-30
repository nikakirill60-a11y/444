/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 */
package ru.metrocraft.stilian;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockTut
extends Block {
    protected BlockTut(Material p_i45394_1_, String name, String texture) {
        super(p_i45394_1_);
        this.func_149663_c(name);
        this.func_149715_a(0.0f);
        this.func_149713_g(10);
        this.func_149711_c(2.0f);
        this.func_149752_b(10.0f);
        this.setHarvestLevel("axe", 3);
        this.func_149672_a(field_149766_f);
        this.func_149658_d("stilmetro:" + texture);
        GameRegistry.registerBlock((Block)this, (String)name);
    }
}

