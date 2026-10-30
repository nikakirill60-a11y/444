/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 */
package metrocraft.entities.clientplayer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryCustomPlayer
extends InventoryPlayer
implements IInventory {
    private final String name = "Inventory";
    private final String tagName = "CustomInvTag";
    public static final int INV_SIZE = 1;
    public ItemStack[] inventory = new ItemStack[1];

    public InventoryCustomPlayer(EntityPlayer player) {
        super(player);
        this.field_70458_d = player;
    }

    public void copy(InventoryCustomPlayer inv) {
        for (int i = 0; i < inv.func_70302_i_(); ++i) {
            ItemStack stack = inv.func_70301_a(i);
            this.inventory[i] = stack == null ? null : stack.func_77946_l();
        }
        this.func_70296_d();
    }

    public int func_70302_i_() {
        return this.inventory.length;
    }

    public ItemStack func_70301_a(int slot) {
        return this.inventory[slot];
    }

    public ItemStack func_70298_a(int slot, int amount) {
        ItemStack stack = this.func_70301_a(slot);
        if (stack != null) {
            if (stack.field_77994_a > amount) {
                stack = stack.func_77979_a(amount);
                this.func_70296_d();
            } else {
                this.func_70299_a(slot, null);
            }
        }
        return stack;
    }

    public ItemStack func_70304_b(int slot) {
        ItemStack stack = this.func_70301_a(slot);
        this.func_70299_a(slot, null);
        return stack;
    }

    public void func_70299_a(int slot, ItemStack stack) {
        this.inventory[slot] = stack;
        if (stack != null && stack.field_77994_a > this.func_70297_j_()) {
            stack.field_77994_a = this.func_70297_j_();
        }
        this.func_70296_d();
    }

    public String func_145825_b() {
        return "Inventory";
    }

    public boolean func_145818_k_() {
        return "Inventory".length() > 0;
    }

    public int func_70297_j_() {
        return 1;
    }

    public void func_70296_d() {
        for (int i = 0; i < this.func_70302_i_(); ++i) {
            if (this.func_70301_a(i) == null || this.func_70301_a((int)i).field_77994_a != 0) continue;
            this.inventory[i] = null;
        }
    }

    public boolean func_70300_a(EntityPlayer player) {
        return true;
    }

    public void func_70295_k_() {
    }

    public void func_70305_f() {
    }

    public boolean func_94041_b(int slot, ItemStack stack) {
        return false;
    }

    public void writeToNBT(NBTTagCompound compound) {
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < this.func_70302_i_(); ++i) {
            if (this.func_70301_a(i) == null) continue;
            NBTTagCompound item = new NBTTagCompound();
            item.func_74774_a("Slot", (byte)i);
            this.func_70301_a(i).func_77955_b(item);
            items.func_74742_a((NBTBase)item);
        }
        compound.func_74782_a("CustomInvTag", (NBTBase)items);
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagList items = compound.func_150295_c("CustomInvTag", (int)compound.func_74732_a());
        for (int i = 0; i < items.func_74745_c(); ++i) {
            NBTTagCompound item = items.func_150305_b(i);
            byte slot = item.func_74771_c("Slot");
            if (slot < 0 || slot >= this.func_70302_i_()) continue;
            this.inventory[slot] = ItemStack.func_77949_a((NBTTagCompound)item);
        }
    }
}

