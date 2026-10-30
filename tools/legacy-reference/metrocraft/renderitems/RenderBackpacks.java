/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.entity.RenderPlayer
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package metrocraft.renderitems;

import metrocraft.backpacksrender.BigRucksack;
import metrocraft.backpacksrender.MediumRucksack;
import metrocraft.backpacksrender.SmallRucksack;
import metrocraft.entities.clientplayer.ExtendedPlayer;
import metrocraft.main.MetroItems;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RenderBackpacks {
    public void render(EntityPlayer player, RenderPlayer renderModel) {
        ItemStack equipStack = ExtendedPlayer.get((EntityPlayer)player).inventory.func_70301_a(0);
        Item equip = equipStack.func_77973_b();
        if (equip == MetroItems.smallRucksack) {
            SmallRucksack.render(renderModel);
        } else if (equip == MetroItems.mediumRucksack) {
            MediumRucksack.render(renderModel);
        } else if (equip == MetroItems.bigRucksack) {
            BigRucksack.render(renderModel);
        }
    }
}

