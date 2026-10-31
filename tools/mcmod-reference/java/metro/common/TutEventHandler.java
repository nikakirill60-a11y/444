/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.network.simpleimpl.IMessage
 *  metro.common.network.Network
 *  metro.common.network.packets.DeathMessage
 *  metro.common.network.packets.HitMessage
 *  metro.common.network.packets.PlayerDataMessageClient
 *  metro.common.player.ExtendedPlayer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.event.entity.EntityEvent$EntityConstructing
 *  net.minecraftforge.event.entity.EntityJoinWorldEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.player.PlayerDropsEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$Clone
 */
package metro.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import java.util.ArrayList;
import metro.common.ItemCustomArmor;
import metro.common.network.Network;
import metro.common.network.packets.DeathMessage;
import metro.common.network.packets.HitMessage;
import metro.common.network.packets.PlayerDataMessageClient;
import metro.common.player.ExtendedPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class TutEventHandler {
    public final ArrayList<ItemStack> save_drops = new ArrayList();
    boolean newPlayer;

    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer && ExtendedPlayer.get((EntityPlayer)((EntityPlayer)event.entity)) == null) {
            ExtendedPlayer.register((EntityPlayer)((EntityPlayer)event.entity));
            ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer)((EntityPlayer)event.entity));
            this.newPlayer = true;
        }
    }

    @SubscribeEvent
    public void doEntityConstructing(EntityEvent.EntityConstructing event) {
        if (!(event.entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer)event.entity;
    }

    @SubscribeEvent
    public void onEntityLoad(EntityJoinWorldEvent event) {
        ExtendedPlayer props;
        if (event.entity instanceof EntityPlayer && (props = ExtendedPlayer.get((EntityPlayer)((EntityPlayer)event.entity))).isServerSide()) {
            Network.INSTANCE.sendTo((IMessage)new PlayerDataMessageClient(props.getC(), props.getGasTick(), props.getHealth(), props.getX(), props.getY(), props.getZ(), props.getNg2020()), (EntityPlayerMP)event.entity);
        }
    }

    @SubscribeEvent
    public void onEntityL(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer)event.entity;
        }
    }

    @SubscribeEvent
    public void onClonePlayer(PlayerEvent.Clone e) {
        if (e.wasDeath) {
            NBTTagCompound compound = new NBTTagCompound();
            ExtendedPlayer.get((EntityPlayer)e.original).saveNBTData(compound);
            ExtendedPlayer.get((EntityPlayer)e.entityPlayer).loadNBTData(compound);
        }
    }

    @SubscribeEvent
    public void onPlayerDamage(LivingHurtEvent e) {
        Entity from = e.source.func_76364_f();
        if (e.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)e.entityLiving;
            ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer)((EntityPlayer)e.entity));
            float health = player.func_110143_aJ();
            this.setKiller(player, from);
            if (player.field_71071_by.func_70440_f(2) != null && player.field_71071_by.func_70440_f(2).func_77973_b() instanceof ItemCustomArmor) {
                Slot slots = (Slot)player.field_71069_bz.field_75151_b.get(38);
                ItemCustomArmor armor = (ItemCustomArmor)player.field_71071_by.func_70440_f(2).func_77973_b();
                ItemStack item = player.field_71071_by.func_70440_f(2);
                float protection = armor.protect;
                if (item.field_77990_d != null) {
                    if (item.field_77990_d.func_74762_e("condition") > 0) {
                        float end_hp = e.ammount * ((100.0f - protection) / 100.0f) * 5.0f;
                        item.field_77990_d.func_74768_a("condition", item.field_77990_d.func_74762_e("condition") - 1);
                        e.ammount = end_hp;
                    } else {
                        e.ammount *= 5.0f;
                    }
                }
            } else {
                e.ammount *= 5.0f;
            }
        }
    }

    private void setKiller(EntityPlayer player, Entity killer) {
        ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer)player);
        if (killer != null) {
            Network.INSTANCE.sendTo((IMessage)new DeathMessage(0, killer.func_70005_c_(), 0, 0), (EntityPlayerMP)player);
        } else {
            Network.INSTANCE.sendTo((IMessage)new DeathMessage(0, "", 0, 0), (EntityPlayerMP)player);
        }
    }

    @SubscribeEvent
    public void onPlayeLivingDeathEvent(LivingDeathEvent e) {
        Entity from = e.source.func_76364_f();
        if (e.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)e.entityLiving;
            e.setCanceled(true);
            if (player.func_110143_aJ() < 1.0f) {
                System.out.println("Custom Death");
            }
        }
    }

    @SubscribeEvent
    public void doPlayerDropsEvent(PlayerDropsEvent event) {
        if (event.drops.isEmpty()) {
            return;
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase)event.entity;
            entity.func_110148_a(SharedMonsterAttributes.field_111266_c).func_111128_a(2.0);
        }
    }

    @SubscribeEvent
    public void onHitmark(LivingHurtEvent e) {
        Entity from = e.source.func_76364_f();
        if (from instanceof EntityPlayer) {
            Network.INSTANCE.sendTo((IMessage)new HitMessage(from.func_70005_c_()), (EntityPlayerMP)from);
        }
    }
}
