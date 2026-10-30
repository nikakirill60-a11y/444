/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.gui.inventory.GuiInventory
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  org.lwjgl.opengl.GL11
 */
package metrocraft.gui;

import metrocraft.containers.ContainerCustomPlayer;
import metrocraft.entities.clientplayer.InventoryCustomPlayer;
import metrocraft.main.MetroItems;
import metrocraft.main.MetroResources;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class GuiCustomPlayerInventory
extends GuiContainer {
    private float xSize_lo;
    private float ySize_lo;
    private final InventoryCustomPlayer inventory;

    public GuiCustomPlayerInventory(EntityPlayer player, InventoryPlayer inventoryPlayer, InventoryCustomPlayer inventoryCustom) {
        super((Container)new ContainerCustomPlayer(player, inventoryPlayer, inventoryCustom));
        this.inventory = inventoryCustom;
    }

    public void func_73863_a(int mouseX, int mouseY, float f) {
        super.func_73863_a(mouseX, mouseY, f);
        this.xSize_lo = mouseX;
        this.ySize_lo = mouseY;
    }

    protected void func_146976_a(float f, int mouseX, int mouseY) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.field_146297_k.func_110434_K().func_110577_a(MetroResources.inventoryTexture);
        this.func_73729_b(this.field_147003_i, this.field_147009_r, 0, 0, this.field_146999_f, this.field_147000_g);
        GuiInventory.func_147046_a((int)(this.field_147003_i + 88), (int)(this.field_147009_r + 75), (int)30, (float)((float)(this.field_147003_i + 51) - this.xSize_lo), (float)((float)(this.field_147009_r + 25) - this.ySize_lo), (EntityLivingBase)this.field_146297_k.field_71439_g);
        this.field_146297_k.func_110434_K().func_110577_a(MetroResources.slotsTexture);
        int x = 0;
        ItemStack equip = this.inventory.func_70301_a(0);
        if (equip != null) {
            Item item = equip.func_77973_b();
            if (item == MetroItems.smallRucksack) {
                x = 1;
            } else if (item == MetroItems.mediumRucksack) {
                x = 2;
            } else if (item == MetroItems.bigRucksack) {
                x = 3;
            }
        }
        this.func_73729_b(this.field_147003_i + 7, this.field_147009_r + 83, 0, 0, 162, 18 * x);
    }
}

