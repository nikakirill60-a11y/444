package ru.metrocraft.port.inventory;

import net.minecraft.entity.player.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.metrocraft.port.MetroPort;

/** A comparison switch over ONE real inventory. No copying, swapping or second item store. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class InventoryModes {
    public static final String KEY="MetroVanillaInventory";
    private InventoryModes() {}
    public static boolean vanilla(EntityPlayer player){return player.getEntityData().getBoolean(KEY);}
    public static int rows(boolean vanilla,int backpackRows){return vanilla?3:Math.max(0,Math.min(3,backpackRows));}
    public static int rows(EntityPlayer player){return rows(vanilla(player)||player.capabilities.isCreativeMode,Backpacks.rows(Backpacks.get(player)));}
    public static boolean emptyForSwitch(EntityPlayer player){
        return player.inventory.getItemStack().isEmpty() && player.inventoryContainer instanceof net.minecraft.inventory.ContainerPlayer
                && ((net.minecraft.inventory.ContainerPlayer)player.inventoryContainer).craftMatrix.isEmpty();
    }
    public static void sync(EntityPlayerMP player,boolean open){
        MetroPort.NETWORK.sendTo(new InventoryModeSyncPacket(vanilla(player),open),player);
    }
    public static void switchTo(EntityPlayerMP player,int mode){
        if(mode!=0 && mode!=1)return;
        if(!player.isEntityAlive() || player.isSpectator()
                || (player.openContainer!=player.inventoryContainer && !(player.openContainer instanceof MetroContainer)))return;
        if(!emptyForSwitch(player)){player.sendStatusMessage(new TextComponentTranslation("metroport.inventory.clear_cursor"),true);return;}
        long now=player.world.getTotalWorldTime();
        if(player.getEntityData().hasKey("MetroModeSwitchTick") && now-player.getEntityData().getLong("MetroModeSwitchTick")<5)return;
        player.getEntityData().setLong("MetroModeSwitchTick",now);
        player.getEntityData().setBoolean(KEY,mode==1);
        player.closeScreen();
        sync(player,mode==1);
        if(mode==0)player.openGui(MetroPort.INSTANCE,0,player.world,0,0,0);
        player.sendContainerToPlayer(player.inventoryContainer);
    }
    @SubscribeEvent public static void clone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event){
        event.getEntityPlayer().getEntityData().setBoolean(KEY,vanilla(event.getOriginal()));
    }
    @SubscribeEvent public static void login(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event){
        if(event.player instanceof EntityPlayerMP)sync((EntityPlayerMP)event.player,false);
    }
    @SubscribeEvent public static void respawn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event){
        if(event.player instanceof EntityPlayerMP)sync((EntityPlayerMP)event.player,false);
    }
    @SubscribeEvent public static void dimension(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event){
        if(event.player instanceof EntityPlayerMP)sync((EntityPlayerMP)event.player,false);
    }
}
