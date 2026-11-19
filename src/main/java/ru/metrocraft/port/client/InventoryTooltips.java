package ru.metrocraft.port.client;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.inventory.*;

@Mod.EventBusSubscriber(modid=MetroPort.ID,value=Side.CLIENT)
public final class InventoryTooltips {
    @SubscribeEvent public static void tooltip(ItemTooltipEvent e){
        ItemStack stack=e.getItemStack();if(stack.isEmpty())return;
        ItemRarity rarity=ItemRarity.of(stack);
        if(!e.getToolTip().isEmpty() && !ItemMetrics.currency(stack))e.getToolTip().set(0,rarity.format+net.minecraft.util.text.TextFormatting.getTextWithoutFormattingCodes(e.getToolTip().get(0)));
        e.getToolTip().add(rarity.format+I18n.format(rarity.key()));
        e.getToolTip().add("\u00a77"+I18n.format("metroport.inventory.weight",ItemMetrics.kg(ItemMetrics.grams(stack))));
        e.getToolTip().add("\u00a77"+I18n.format("metroport.inventory.size",ItemMetrics.width(stack),ItemMetrics.height(stack)));
        int bonus=ItemMetrics.capacityBonus(stack);
        if(bonus>0)e.getToolTip().add("\u00a76"+I18n.format("metroport.inventory.capacity",ItemMetrics.kg(bonus)));
    }
}
