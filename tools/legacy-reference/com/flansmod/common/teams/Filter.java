/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package com.flansmod.common.teams;

import com.flansmod.common.FlansMod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class Filter
extends Item {
    public Filter() {
        this.func_77637_a(FlansMod.tabFlanParts);
        this.func_77655_b("Filter");
        this.func_77625_d(64);
    }

    public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
        if (var3.field_71071_by.func_70440_f(3) != null && var3.field_71071_by.func_70440_f(3).func_77978_p() != null && var3.field_71071_by.func_70440_f(3).func_77978_p().func_74767_n("isGasMask")) {
            var3.field_71071_by.func_70440_f(3).func_77978_p().func_74768_a("Time_Left", 300);
            var3.field_71071_by.func_146026_a((Item)FlansMod.filter);
        } else if (var3.field_71071_by.func_70440_f(2) != null && var3.field_71071_by.func_70440_f(2).func_77978_p() != null && var3.field_71071_by.func_70440_f(2).func_77978_p().func_74767_n("isGasMask")) {
            var3.field_71071_by.func_70440_f(2).func_77978_p().func_74768_a("Time_Left", 300);
            var3.field_71071_by.func_146026_a((Item)FlansMod.filter);
        }
        return var1;
    }

    @SideOnly(value=Side.CLIENT)
    public void func_94581_a(IIconRegister icon) {
        this.field_77791_bV = icon.func_94245_a("FlansMod:filter");
    }

    public boolean onEntitySwing(EntityLiving entityLiving, ItemStack stack) {
        return true;
    }
}

