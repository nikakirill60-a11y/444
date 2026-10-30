/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package metrocraft.containers;

import metrocraft.containers.regslots.SlotArmor;
import metrocraft.containers.regslots.SlotBackpack;
import metrocraft.entities.clientplayer.ExtendedPlayer;
import metrocraft.entities.clientplayer.InventoryCustomPlayer;
import metrocraft.main.MetroItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ContainerCustomPlayer
extends Container {
    private final EntityPlayer player;
    private static final int ARMOR_START = 1;
    private static final int ARMOR_END = 2;
    private static final int INV_START = 4;
    private static final int INV_END = 4;
    private static final int HOTBAR_START = 5;
    private static final int HOTBAR_END = 13;

    public ContainerCustomPlayer(EntityPlayer player, InventoryPlayer inventoryPlayer, InventoryCustomPlayer inventoryCustom) {
        this.player = player;
        this.func_75146_a(new SlotBackpack(inventoryCustom, 0, 24, 18));
        for (int i = 0; i < 2; ++i) {
            this.func_75146_a(new SlotArmor(player, (IInventory)inventoryPlayer, inventoryPlayer.func_70302_i_() - 1 - i, 44, 7 + i * 18, i));
        }
        for (int j = 0; j < 11; ++j) {
            this.func_75146_a(new Slot((IInventory)inventoryPlayer, j + 9, 8 + j * 18, 84));
        }
        this.field_75152_c = 0;
        while (this.field_75152_c < 9) {
            this.func_75146_a(new Slot((IInventory)inventoryPlayer, this.field_75152_c + 18, 8 + this.field_75152_c * 18, 102));
            ++this.field_75152_c;
        }
        this.field_75152_c = 0;
        while (this.field_75152_c < 9) {
            this.func_75146_a(new Slot((IInventory)inventoryPlayer, this.field_75152_c + 27, 8 + this.field_75152_c * 18, 120));
            ++this.field_75152_c;
        }
        this.field_75152_c = 0;
        while (this.field_75152_c < 9) {
            this.func_75146_a(new Slot((IInventory)inventoryPlayer, this.field_75152_c, 8 + this.field_75152_c * 18, 142));
            ++this.field_75152_c;
        }
    }

    public boolean func_75145_c(EntityPlayer player) {
        return true;
    }

    public ItemStack func_82846_b(EntityPlayer player, int par2) {
        return null;
    }

    public void func_75134_a(EntityPlayer player) {
        block5: {
            block3: {
                Item equip;
                block4: {
                    super.func_75134_a(player);
                    Slot slot = (Slot)this.field_75151_b.get(0);
                    if (slot == null || !slot.func_75216_d()) break block3;
                    equip = slot.func_75211_c().func_77973_b();
                    if (equip != MetroItems.smallRucksack) break block4;
                    for (int i = 18; i < 36; ++i) {
                        ItemStack itemstack = player.field_71071_by.func_70304_b(i);
                        if (itemstack == null) continue;
                        player.func_71019_a(itemstack, false);
                    }
                    break block5;
                }
                if (equip != MetroItems.mediumRucksack) break block5;
                for (int i = 27; i < 36; ++i) {
                    ItemStack itemstack = player.field_71071_by.func_70304_b(i);
                    if (itemstack == null) continue;
                    player.func_71019_a(itemstack, false);
                }
                break block5;
            }
            for (int i = 9; i < 36; ++i) {
                ItemStack itemstack = player.field_71071_by.func_70304_b(i);
                if (itemstack == null) continue;
                player.func_71019_a(itemstack, false);
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    public ItemStack func_75144_a(int num, int var1, int typeClick, EntityPlayer player) {
        ItemStack equipStack = ExtendedPlayer.get((EntityPlayer)player).inventory.func_70301_a(0);
        if (equipStack == null) {
            if (num >= 32) return super.func_75144_a(num, var1, typeClick, player);
            if (num <= 4) return super.func_75144_a(num, var1, typeClick, player);
            return null;
        }
        Item equip = equipStack.func_77973_b();
        if (equip == MetroItems.smallRucksack) {
            if (num >= 32) return super.func_75144_a(num, var1, typeClick, player);
            if (num <= 13) return super.func_75144_a(num, var1, typeClick, player);
            return null;
        }
        if (equip == MetroItems.mediumRucksack) {
            if (num >= 32) return super.func_75144_a(num, var1, typeClick, player);
            if (num <= 22) return super.func_75144_a(num, var1, typeClick, player);
            return null;
        }
        if (equip == MetroItems.bigRucksack) return super.func_75144_a(num, var1, typeClick, player);
        return super.func_75144_a(num, var1, typeClick, player);
    }
}

