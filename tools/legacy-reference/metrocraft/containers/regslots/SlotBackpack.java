/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 */
package metrocraft.containers.regslots;

import metrocraft.main.MetroItems;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotBackpack
extends Slot {
    public SlotBackpack(IInventory inventory, int slotIndex, int x, int y) {
        super(inventory, slotIndex, x, y);
    }

    public boolean func_75214_a(ItemStack stack) {
        if (stack.func_77973_b() == MetroItems.smallRucksack) {
            return true;
        }
        if (stack.func_77973_b() == MetroItems.mediumRucksack) {
            return true;
        }
        return stack.func_77973_b() == MetroItems.bigRucksack;
    }
}

