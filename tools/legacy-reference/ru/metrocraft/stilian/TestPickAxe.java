/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item$ToolMaterial
 *  net.minecraft.item.ItemPickaxe
 */
package ru.metrocraft.stilian;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;

public class TestPickAxe
extends ItemPickaxe {
    protected TestPickAxe() {
        super(Item.ToolMaterial.EMERALD);
        this.func_77637_a(CreativeTabs.field_78040_i);
        this.func_111206_d("stilmetro:Pix");
    }
}

