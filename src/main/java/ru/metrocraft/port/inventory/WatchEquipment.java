package ru.metrocraft.port.inventory;

import net.minecraft.entity.player.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import ru.metrocraft.port.*;
import ru.metrocraft.port.mechanics.NoDrop;

/** A real extra slot, not a virtual helmet: saved, tracked, dropped/retained, and server-owned. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class WatchEquipment {
    private static final String KEY="MetroEquippedWatch",READING="MetroWatchRadiation";
    private static final java.util.Map<EntityPlayer,EquipmentInventory> INVENTORIES=new java.util.WeakHashMap<>();
    private WatchEquipment() {}
    public static boolean accepts(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ru.metrocraft.port.mcmod.WatchItem;
    }
    public static ItemStack get(EntityPlayer player) {
        EquipmentInventory live=INVENTORIES.get(player);
        ItemStack stack=live==null?new ItemStack(player.getEntityData().getCompoundTag(KEY).copy()):live.getStackInSlot(0).copy();
        if(!accepts(stack))return ItemStack.EMPTY;stack.setCount(1);return stack;
    }
    private static void save(EntityPlayer player,ItemStack stack){
        player.getEntityData().setTag(KEY,stack.writeToNBT(new NBTTagCompound()));
    }
    public static void receive(EntityPlayer player,ItemStack stack) {
        // The native window protocol owns the local slot during an open transaction.
        // Late equipment/reading packets may update render state, but must not put an
        // old item back into an open inventory after the user has picked it up.
        if(player.world.isRemote && player.openContainer instanceof MetroContainer)return;
        save(player,stack);
        EquipmentInventory inventory=INVENTORIES.get(player);if(inventory!=null)inventory.load(stack);
    }
    private static void changed(EntityPlayer player,ItemStack stack){
        save(player,stack);
        if(!player.world.isRemote && player instanceof EntityPlayerMP){
            WatchSyncPacket packet=new WatchSyncPacket(player,stack);
            MetroPort.NETWORK.sendTo(packet,(EntityPlayerMP)player);MetroPort.NETWORK.sendToAllTracking(packet,player);
        }
    }
    public static void set(EntityPlayer player,ItemStack stack) {
        if(!stack.isEmpty() && (!accepts(stack) || stack.getCount()!=1))throw new IllegalArgumentException("Equipment slot requires exactly one valid item");
        EquipmentInventory inventory=INVENTORIES.get(player);if(inventory!=null)inventory.load(stack);
        changed(player,stack);
    }
    public static InventoryBasic inventory(EntityPlayer player) {
        EquipmentInventory inventory=INVENTORIES.get(player);
        if(inventory==null){
            java.lang.ref.WeakReference<EntityPlayer> owner=new java.lang.ref.WeakReference<>(player);
            inventory=new EquipmentInventory("metroport.watch",get(player),WatchEquipment::accepts,stack->{EntityPlayer p=owner.get();if(p!=null)changed(p,stack);});
            INVENTORIES.put(player,inventory);
        }
        return inventory;
    }

    @SubscribeEvent public static void login(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {set(event.player,get(event.player));}
    @SubscribeEvent public static void respawn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event) {set(event.player,get(event.player));}
    @SubscribeEvent public static void dimension(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {set(event.player,get(event.player));}
    @SubscribeEvent public static void track(PlayerEvent.StartTracking event) {
        if(event.getTarget() instanceof EntityPlayer && event.getEntityPlayer() instanceof EntityPlayerMP)
            MetroPort.NETWORK.sendTo(new WatchSyncPacket((EntityPlayer)event.getTarget(),get((EntityPlayer)event.getTarget())),(EntityPlayerMP)event.getEntityPlayer());
    }
    @SubscribeEvent public static void clone(PlayerEvent.Clone event) {
        // LivingDrops cleared ordinary devices first. keepInventory and NoDrop devices survive.
        receive(event.getEntityPlayer(),get(event.getOriginal()));
    }
    @SubscribeEvent public static void death(LivingDropsEvent event) {
        if(!(event.getEntityLiving() instanceof EntityPlayer) || event.getEntityLiving().world.isRemote)return;
        EntityPlayer player=(EntityPlayer)event.getEntityLiving();ItemStack stack=get(player);
        if(stack.isEmpty() || player.world.getGameRules().getBoolean("keepInventory") || NoDrop.marked(stack))return;
        event.getDrops().add(new EntityItem(player.world,player.posX,player.posY,player.posZ,stack));set(player,ItemStack.EMPTY);
    }
    public static int reading(EntityPlayer player){return Math.max(0,Math.min(3,player.getEntityData().getInteger(READING)));}
    public static void reading(EntityPlayer player,int level){player.getEntityData().setInteger(READING,Math.max(0,Math.min(3,level)));}
    @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player=event.player;
        if(event.phase!=TickEvent.Phase.END || player.world.isRemote || !player.isEntityAlive())return;
        if(player.ticksExisted==1 || player.ticksExisted==20)set(player,get(player));
        if(player.ticksExisted%10!=0)return;
        int level=ru.metrocraft.port.radiation.RadiationSystem.level(player);
        if(level!=reading(player)){
            reading(player,level);
            if(player instanceof EntityPlayerMP){
                WatchSyncPacket packet=new WatchSyncPacket(player,get(player));
                MetroPort.NETWORK.sendTo(packet,(EntityPlayerMP)player);MetroPort.NETWORK.sendToAllTracking(packet,player);
            }
        }
    }
}
