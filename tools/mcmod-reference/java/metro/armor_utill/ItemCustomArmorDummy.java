/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package metro.armor_utill;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import metro.armor_utill.ArmorCustomModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class ItemCustomArmorDummy
extends Item {
    public final int armorType;
    public final int armor;
    public final int protect;
    public final int maxcondition;

    public ItemCustomArmorDummy(int armorType, int armor, int protect, int maxcondition) {
        this.armorType = armorType;
        this.armor = armor;
        this.protect = protect;
        this.maxcondition = maxcondition;
    }

    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
        return this.armorType == armorType;
    }

    @SideOnly(value=Side.CLIENT)
    public ArmorCustomModel getModel(EntityLivingBase entityPlayer, ItemStack stack, int slot) {
        return null;
    }
}
