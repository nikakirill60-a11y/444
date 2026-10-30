/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.MinecraftServer
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingUpdateEvent
 */
package metrocraft.handler.directs;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.HashMap;
import java.util.Map;
import metrocraft.entities.clientplayer.ExtendedPlayer;
import metrocraft.main.MetroItems;
import metrocraft.server.PacketDispatcher;
import metrocraft.server.clientreg.SyncEquipBackMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.living.LivingEvent;

public class PlayerUpdateEventHandler {
    private static HashMap<Integer, Integer> equip0Map = new HashMap();

    @SubscribeEvent
    public void onPlayerUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if (event.entity.field_70170_p.field_72995_K || !(event.entityLiving instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer)event.entityLiving;
        int prevEquip = equip0Map.containsKey(player.func_145782_y()) ? equip0Map.get(player.func_145782_y()) : -1;
        int currentEquip = 0;
        if (prevEquip == -1) {
            for (Map.Entry<Integer, Integer> entry0 : equip0Map.entrySet()) {
                Entity target = event.entity.field_70170_p.func_73045_a(entry0.getKey().intValue());
                if (target == null || target.field_71093_bK != event.entity.field_71093_bK) continue;
                PacketDispatcher.sendTo(new SyncEquipBackMessage(entry0.getKey(), entry0.getValue()), (EntityPlayerMP)player);
            }
        }
        if (MinecraftServer.func_71276_C().func_71259_af() % 20 == 0) {
            ItemStack itemStack = ExtendedPlayer.get((EntityPlayer)player).inventory.func_70301_a(0);
            if (itemStack != null) {
                Item equip = itemStack.func_77973_b();
                if (equip == MetroItems.smallRucksack) {
                    currentEquip = 1;
                } else if (equip == MetroItems.mediumRucksack) {
                    currentEquip = 2;
                } else if (equip == MetroItems.bigRucksack) {
                    currentEquip = 3;
                }
            }
            if (prevEquip != currentEquip) {
                equip0Map.put(player.func_145782_y(), currentEquip);
                if (prevEquip + currentEquip >= 0) {
                    PacketDispatcher.sendToDimension(new SyncEquipBackMessage(player.func_145782_y(), currentEquip), player.field_71093_bK);
                }
            }
            if (MinecraftServer.func_71276_C().func_71259_af() % 12000 == 0) {
                equip0Map.clear();
            }
        }
    }
}

