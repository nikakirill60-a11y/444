/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.entity.monster.EntityZombie
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.event.entity.EntityEvent$EntityConstructing
 *  net.minecraftforge.event.entity.EntityJoinWorldEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingUpdateEvent
 *  net.minecraftforge.event.entity.player.EntityItemPickupEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$Clone
 */
package metrocraft.handler.directs;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import metrocraft.entities.clientplayer.ExtendedPlayer;
import metrocraft.main.MetroItems;
import metrocraft.server.PacketDispatcher;
import metrocraft.server.clientreg.SyncPlayerPropsMessage;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class EntityPlayerHandler {
    /*
     * Enabled aggressive block sorting
     */
    @SubscribeEvent
    public void onPlayerUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if (event.entity.field_70170_p.field_72995_K) return;
        if (!(event.entityLiving instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer)event.entityLiving;
        if (player.field_71075_bZ.field_75098_d) return;
        ItemStack slot1 = ExtendedPlayer.get((EntityPlayer)player).inventory.func_70301_a(0);
        if (slot1 != null) {
            Item equip = slot1.func_77973_b();
            if (equip == MetroItems.smallRucksack) {
                int i = 18;
                while (i < 36) {
                    player.func_71019_a(player.field_71071_by.func_70301_a(i), true);
                    player.field_71071_by.func_70299_a(i, null);
                    ++i;
                }
                return;
            }
            if (equip != MetroItems.mediumRucksack) {
                if (equip == MetroItems.bigRucksack) return;
                return;
            }
            int i = 27;
            while (i < 36) {
                player.func_71019_a(player.field_71071_by.func_70301_a(i), true);
                player.field_71071_by.func_70299_a(i, null);
                ++i;
            }
            return;
        }
        int i = 9;
        while (i < 36) {
            player.func_71019_a(player.field_71071_by.func_70301_a(i), true);
            player.field_71071_by.func_70299_a(i, null);
            ++i;
        }
    }

    @SubscribeEvent
    public void onPickupItem(EntityItemPickupEvent event) {
        block10: {
            EntityPlayer player;
            block11: {
                Item equip;
                block13: {
                    block12: {
                        if (event.entity.field_70170_p.field_72995_K || !(event.entityLiving instanceof EntityPlayer)) {
                            return;
                        }
                        player = (EntityPlayer)event.entityLiving;
                        if (player.field_71075_bZ.field_75098_d) break block10;
                        ItemStack slot1 = ExtendedPlayer.get((EntityPlayer)player).inventory.func_70301_a(0);
                        if (slot1 == null) break block11;
                        equip = slot1.func_77973_b();
                        if (equip != MetroItems.smallRucksack) break block12;
                        for (int i = 0; i < 18; ++i) {
                            if (player.field_71071_by.func_70301_a(i) == null) {
                                event.setCanceled(false);
                                return;
                            }
                            if (player.field_71071_by.func_70301_a(i) == null) continue;
                            if (player.field_71071_by.func_70301_a(i).func_77973_b() == event.item.func_92059_d().func_77973_b()) {
                                if (player.field_71071_by.func_70301_a((int)i).field_77994_a + event.item.func_92059_d().field_77994_a > player.field_71071_by.func_70301_a(i).func_77976_d()) continue;
                                event.setCanceled(false);
                                continue;
                            }
                            event.setCanceled(true);
                        }
                        break block10;
                    }
                    if (equip != MetroItems.mediumRucksack) break block13;
                    for (int i = 0; i < 27; ++i) {
                        if (player.field_71071_by.func_70301_a(i) == null) {
                            event.setCanceled(false);
                            return;
                        }
                        if (player.field_71071_by.func_70301_a(i) == null) continue;
                        if (player.field_71071_by.func_70301_a(i).func_77973_b() == event.item.func_92059_d().func_77973_b()) {
                            if (player.field_71071_by.func_70301_a((int)i).field_77994_a + event.item.func_92059_d().field_77994_a > player.field_71071_by.func_70301_a(i).func_77976_d()) continue;
                            event.setCanceled(false);
                            continue;
                        }
                        event.setCanceled(true);
                    }
                    break block10;
                }
                if (equip != MetroItems.bigRucksack) break block10;
                event.setCanceled(false);
                break block10;
            }
            for (int i = 0; i < 9; ++i) {
                if (player.field_71071_by.func_70301_a(i) == null) {
                    event.setCanceled(false);
                    return;
                }
                if (player.field_71071_by.func_70301_a(i) == null) continue;
                if (player.field_71071_by.func_70301_a(i).func_77973_b() == event.item.func_92059_d().func_77973_b()) {
                    if (player.field_71071_by.func_70301_a((int)i).field_77994_a + event.item.func_92059_d().field_77994_a > player.field_71071_by.func_70301_a(i).func_77976_d()) continue;
                    event.setCanceled(false);
                    continue;
                }
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer && ExtendedPlayer.get((EntityPlayer)event.entity) == null) {
            ExtendedPlayer.register((EntityPlayer)event.entity);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayer && !event.entity.field_70170_p.field_72995_K) {
            PacketDispatcher.sendTo(new SyncPlayerPropsMessage((EntityPlayer)event.entity), (EntityPlayerMP)event.entity);
        }
    }

    @SubscribeEvent
    public void onClonePlayer(PlayerEvent.Clone event) {
        ExtendedPlayer.get(event.entityPlayer).copy(ExtendedPlayer.get(event.original));
    }

    @SubscribeEvent
    public void kill(LivingDeathEvent event) {
        if (event.source.func_76346_g() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.source.func_76346_g();
            if (event.entity instanceof EntityPlayer) {
                ++ExtendedPlayer.get((EntityPlayer)player).playersKilled;
                PacketDispatcher.sendTo(new SyncPlayerPropsMessage(player), (EntityPlayerMP)player);
            } else if (event.entity instanceof EntityZombie) {
                ++ExtendedPlayer.get((EntityPlayer)player).zombiesKilled;
                PacketDispatcher.sendTo(new SyncPlayerPropsMessage(player), (EntityPlayerMP)player);
            }
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.entity;
            for (int i = 0; i < ExtendedPlayer.get((EntityPlayer)player).inventory.inventory.length; ++i) {
                ItemStack stack = ExtendedPlayer.get((EntityPlayer)player).inventory.inventory[i];
                player.func_71019_a(stack, true);
                ExtendedPlayer.get((EntityPlayer)player).inventory.func_70299_a(i, null);
            }
        }
    }
}

