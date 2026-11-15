package ru.metrocraft.port.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import ru.metrocraft.port.MetroPort;

/** Switches the existing dedicated-slot NVG, without moving/copying equipment or inventing battery rules. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class NvgControl {
    private static final String ENABLED="MetroNvgEnabled", LAST="MetroNvgToggleTick";
    private NvgControl() {}
    public static boolean requested(EntityPlayer player) {
        NBTTagCompound data=player.getEntityData();
        return !data.hasKey(ENABLED,99) || data.getBoolean(ENABLED);
    }
    public static boolean active(EntityPlayer player) {
        return player.isEntityAlive() && !player.isSpectator() && requested(player) && !NvgEquipment.get(player).isEmpty();
    }
    public static boolean toggle(EntityPlayer player) {
        if(player.world.isRemote || !player.isEntityAlive() || player.isSpectator() || NvgEquipment.get(player).isEmpty())return false;
        NBTTagCompound data=player.getEntityData();long now=player.world.getTotalWorldTime();
        if(data.hasKey(LAST,99) && now>=data.getLong(LAST) && now-data.getLong(LAST)<5)return false;
        data.setLong(LAST,now);data.setBoolean(ENABLED,!requested(player));sync(player);return true;
    }
    public static void sync(EntityPlayer player) {
        if(!player.world.isRemote && player instanceof EntityPlayerMP)
            MetroPort.NETWORK.sendTo(new NvgStatePacket(player.dimension,player.getEntityId(),active(player)),(EntityPlayerMP)player);
    }
    @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player=event.player;
        if(event.phase==TickEvent.Phase.END && !player.world.isRemote && (player.ticksExisted==1 || player.ticksExisted%20==0))sync(player);
    }
    @SubscribeEvent public static void clone(PlayerEvent.Clone event) {
        event.getEntityPlayer().getEntityData().setBoolean(ENABLED,requested(event.getOriginal()));
        event.getEntityPlayer().getEntityData().removeTag(LAST);
    }
}
