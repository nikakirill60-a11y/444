/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.gameevent.PlayerEvent$PlayerRespawnEvent
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingUpdateEvent
 */
package timaxa007.no_drop;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import timaxa007.no_drop.NoDropsIEEP;

public class EventsFML {
    @SubscribeEvent
    public void doPlayerEventPlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        NoDropsIEEP noDropsIEEP = NoDropsIEEP.get(event.player);
        if (noDropsIEEP == null) {
            return;
        }
        if (noDropsIEEP.save_drops.isEmpty()) {
            return;
        }
        for (ItemStack itemStack : noDropsIEEP.save_drops) {
            if (event.player.field_71071_by.func_70441_a(itemStack)) continue;
            event.player.func_71019_a(itemStack, false);
        }
        noDropsIEEP.save_drops.clear();
    }

    @SubscribeEvent
    public void JoinGame(LivingEvent.LivingUpdateEvent event) {
        event.entity.field_70155_l = 128.0;
    }
}

