/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumChatFormatting
 *  net.minecraftforge.event.entity.EntityEvent$EntityConstructing
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingUpdateEvent
 *  net.minecraftforge.event.entity.player.ItemTooltipEvent
 *  net.minecraftforge.event.entity.player.PlayerDropsEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$Clone
 */
package timaxa007.no_drop;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import timaxa007.no_drop.NoDropsIEEP;

public class EventsForge {
    @SubscribeEvent
    public void doEntityConstructing(EntityEvent.EntityConstructing event) {
        if (!(event.entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer)event.entity;
        if (NoDropsIEEP.get(player) == null) {
            NoDropsIEEP.reg(player);
        }
    }

    @SubscribeEvent
    public void doPlayerDropsEvent(PlayerDropsEvent event) {
        if (event.drops.isEmpty()) {
            return;
        }
        NoDropsIEEP noDropsIEEP = NoDropsIEEP.get(event.entityPlayer);
        if (noDropsIEEP == null) {
            return;
        }
        for (int i = 0; i < event.drops.size(); ++i) {
            EntityItem ei = (EntityItem)event.drops.get(i);
            ItemStack itemStack = ei.func_92059_d();
            if (!itemStack.func_77942_o() || !itemStack.func_77978_p().func_74764_b("NoDrop")) continue;
            noDropsIEEP.save_drops.add(itemStack);
            event.drops.remove(i--);
        }
    }

    @SubscribeEvent
    public void JoinGame(LivingEvent.LivingUpdateEvent event) {
        event.entity.field_70155_l = 128.0;
    }

    @SubscribeEvent
    public void doPlayerEventClone(PlayerEvent.Clone event) {
        NoDropsIEEP noDropsIEEP_old = NoDropsIEEP.get(event.original);
        if (noDropsIEEP_old == null) {
            return;
        }
        if (noDropsIEEP_old.save_drops.isEmpty()) {
            return;
        }
        NoDropsIEEP noDropsIEEP_new = NoDropsIEEP.get(event.entityPlayer);
        if (noDropsIEEP_new == null) {
            return;
        }
        noDropsIEEP_new.save_drops.addAll(noDropsIEEP_old.save_drops);
        noDropsIEEP_old.save_drops.clear();
    }

    @SubscribeEvent
    public void doItemTooltipEvent(ItemTooltipEvent event) {
        if (event.itemStack == null) {
            return;
        }
        if (!event.itemStack.func_77942_o()) {
            return;
        }
        if (event.itemStack.func_77978_p().func_74764_b("NoDrop")) {
            event.toolTip.add(EnumChatFormatting.GREEN + "\u041d\u0435\u0432\u044b\u043f\u0430\u0434\u0430\u044e\u0449\u0438\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442");
        }
    }
}

