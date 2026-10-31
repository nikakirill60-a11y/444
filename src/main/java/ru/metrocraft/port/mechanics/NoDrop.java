package ru.metrocraft.port.mechanics;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import ru.metrocraft.port.MetroPort;

/** Persistent return queue. A full inventory NEVER turns retained items into ground drops. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class NoDrop {
    private static final String QUEUE="MetroNoDropPending";
    public static boolean marked(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTagCompound() && stack.getTagCompound().getBoolean("NoDrop");
    }
    private static NBTTagCompound data(EntityPlayer player) {
        NBTTagCompound root=player.getEntityData();
        if(!root.hasKey(EntityPlayer.PERSISTED_NBT_TAG,10)) root.setTag(EntityPlayer.PERSISTED_NBT_TAG,new NBTTagCompound());
        return root.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }
    public static void retain(EntityPlayer player,ItemStack stack) {
        if(stack.isEmpty())return;
        NBTTagList queue=data(player).getTagList(QUEUE,10);
        queue.appendTag(stack.writeToNBT(new NBTTagCompound()));data(player).setTag(QUEUE,queue);
    }
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void drops(PlayerDropsEvent event) {
        EntityPlayer player=event.getEntityPlayer();
        if(player.world.isRemote || player.world.getGameRules().getBoolean("keepInventory"))return;
        java.util.Iterator<EntityItem> iterator=event.getDrops().iterator();
        while(iterator.hasNext()) {
            EntityItem item=iterator.next();
            if(marked(item.getItem())) {retain(player,item.getItem().copy());iterator.remove();}
        }
    }
    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void toss(ItemTossEvent event) {
        if(event.getPlayer().world.isRemote || !marked(event.getEntityItem().getItem()))return;
        event.setCanceled(true);
        ItemStack stack=event.getEntityItem().getItem().copy();
        event.getPlayer().inventory.addItemStackToInventory(stack);
        retain(event.getPlayer(),stack);
        event.getEntityItem().setDead();
        event.getPlayer().inventory.markDirty();
    }
    @SubscribeEvent public static void clone(PlayerEvent.Clone event) {
        NBTTagList queue=data(event.getOriginal()).getTagList(QUEUE,10);
        data(event.getEntityPlayer()).setTag(QUEUE,queue.copy());
        data(event.getOriginal()).removeTag(QUEUE);
    }
    @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player=event.player;
        if(event.phase!=TickEvent.Phase.END || player.world.isRemote || !player.isEntityAlive() || player.ticksExisted%20!=0)return;
        NBTTagList old=data(player).getTagList(QUEUE,10), remaining=new NBTTagList();
        for(int i=0;i<old.tagCount();i++) {
            ItemStack stack=new ItemStack(old.getCompoundTagAt(i));
            player.inventory.addItemStackToInventory(stack);
            if(!stack.isEmpty())remaining.appendTag(stack.writeToNBT(new NBTTagCompound()));
        }
        data(player).setTag(QUEUE,remaining);
    }
}
