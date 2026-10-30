/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 */
package metrocraft.containers.regslots;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class SlotArmor
extends Slot {
    final int armorType;
    final EntityPlayer player;

    public SlotArmor(EntityPlayer player, IInventory inventory, int slotIndex, int x, int y, int armorType) {
        super(inventory, slotIndex, x, y);
        this.player = player;
        this.armorType = armorType;
    }

    public int func_75219_a() {
        return 1;
    }

    public boolean func_75214_a(ItemStack itemstack) {
        Item item = itemstack == null ? null : itemstack.func_77973_b();
        return item != null && item.isValidArmor(itemstack, this.armorType, (Entity)this.player);
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon func_75212_b() {
        return ItemArmor.func_94602_b((int)this.armorType);
    }
}

