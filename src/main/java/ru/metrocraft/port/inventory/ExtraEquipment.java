package ru.metrocraft.port.inventory;

import java.util.EnumMap;
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
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.blackrazid.BlackrazidBackpackItem;
import ru.metrocraft.port.blackrazid.survival.SurvivalItem;
import ru.metrocraft.port.mechanics.NoDrop;

/** Two independent server-owned slots. Save never replaces a live Container stack. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class ExtraEquipment {
    public enum Kind { LAMP,BLACKRAZID_BACKPACK }
    private static final ru.metrocraft.port.util.WeakIdentityMap<EntityPlayer,EnumMap<Kind,EquipmentInventory>> LIVE=new ru.metrocraft.port.util.WeakIdentityMap<>();
    private static final class PendingEquip {
        final Kind kind;final net.minecraft.util.EnumHand hand;final ItemStack stack;
        PendingEquip(Kind kind,net.minecraft.util.EnumHand hand,ItemStack stack){this.kind=kind;this.hand=hand;this.stack=stack;}
    }
    private static final ru.metrocraft.port.util.WeakIdentityMap<EntityPlayer,PendingEquip> PENDING=new ru.metrocraft.port.util.WeakIdentityMap<>();
    public static void equipAfterUse(EntityPlayer p,Kind kind,net.minecraft.util.EnumHand hand,ItemStack stack){
        if(!p.world.isRemote)PENDING.computeIfAbsent(p,k->new PendingEquip(kind,hand,stack));
    }
    public static void finishPendingEquip(EntityPlayer p){
        PendingEquip pending=PENDING.remove(p);
        if(pending==null || p.world.isRemote || !p.isEntityAlive() || p.isSpectator() || p.getHeldItem(pending.hand)!=pending.stack
                || !accepts(pending.kind,pending.stack) || !get(p,pending.kind).isEmpty())return;
        // After vanilla creative-mode count restoration, not inside onItemRightClick.
        ItemStack moved=pending.stack.splitStack(1);set(p,pending.kind,moved);p.inventory.markDirty();
    }
    private ExtraEquipment(){}
    private static String key(Kind kind){return "MetroExtraEquipment_"+kind.name();}
    public static boolean accepts(Kind kind,ItemStack stack){
        if(stack.isEmpty())return false;
        return kind==Kind.LAMP?stack.getItem() instanceof SurvivalItem && ((SurvivalItem)stack.getItem()).type==SurvivalItem.Type.LAMP:stack.getItem() instanceof BlackrazidBackpackItem;
    }
    public static ItemStack get(EntityPlayer player,Kind kind){
        EnumMap<Kind,EquipmentInventory> map=LIVE.get(player);EquipmentInventory live=map==null?null:map.get(kind);
        ItemStack stack=live==null?new ItemStack(player.getEntityData().getCompoundTag(key(kind)).copy()):live.getStackInSlot(0).copy();
        if(!accepts(kind,stack))return ItemStack.EMPTY;stack.setCount(1);return stack;
    }
    private static void save(EntityPlayer p,Kind kind,ItemStack stack){p.getEntityData().setTag(key(kind),stack.writeToNBT(new NBTTagCompound()));}
    public static void receive(EntityPlayer p,Kind kind,ItemStack stack){
        if(p.world.isRemote && (p.openContainer instanceof MetroContainer || p.openContainer instanceof ru.metrocraft.port.blackrazid.BackpackContainer))return;
        if(!stack.isEmpty() && !accepts(kind,stack))return;
        save(p,kind,stack);EnumMap<Kind,EquipmentInventory> map=LIVE.get(p);if(map!=null && map.containsKey(kind))map.get(kind).load(stack);
    }
    private static void changed(EntityPlayer p,Kind kind,ItemStack stack){
        save(p,kind,stack);
        if(!p.world.isRemote && p instanceof EntityPlayerMP){
            ExtraEquipmentSyncPacket packet=new ExtraEquipmentSyncPacket(p,kind,stack);
            MetroPort.NETWORK.sendTo(packet,(EntityPlayerMP)p);MetroPort.NETWORK.sendToAllTracking(packet,p);
        }
    }
    public static void set(EntityPlayer p,Kind kind,ItemStack stack){
        if(!stack.isEmpty() && (stack.getCount()!=1 || !accepts(kind,stack)))throw new IllegalArgumentException("Wrong extra equipment type/count");
        EnumMap<Kind,EquipmentInventory> map=LIVE.get(p);if(map!=null && map.containsKey(kind))map.get(kind).load(stack);
        changed(p,kind,stack);
    }
    public static InventoryBasic inventory(EntityPlayer player,Kind kind){
        EnumMap<Kind,EquipmentInventory> map=LIVE.computeIfAbsent(player,p->new EnumMap<>(Kind.class));
        return map.computeIfAbsent(kind,k->{
            java.lang.ref.WeakReference<EntityPlayer> owner=new java.lang.ref.WeakReference<>(player);
            return new EquipmentInventory("metroport.extra."+kind.name(),get(player,kind),stack->accepts(kind,stack),stack->{EntityPlayer p=owner.get();if(p!=null)changed(p,kind,stack);});
        });
    }
    /** Upgrade the old shared backpack slot without losing or cloning the bag's stored contents. */
    public static void migrateLegacyBackpack(EntityPlayer player){
        if(player.world.isRemote || player.openContainer instanceof ru.metrocraft.port.blackrazid.BackpackContainer)return;
        ItemStack old=Backpacks.get(player);if(!(old.getItem() instanceof BlackrazidBackpackItem))return;
        Backpacks.set(player,ItemStack.EMPTY); // Remove original BEFORE delivering its single copy.
        if(get(player,Kind.BLACKRAZID_BACKPACK).isEmpty())set(player,Kind.BLACKRAZID_BACKPACK,old);
        else {player.inventory.addItemStackToInventory(old);if(!old.isEmpty())player.dropItem(old,false);}
    }
    private static void sync(EntityPlayer p){for(Kind kind:Kind.values())set(p,kind,get(p,kind));}
    @SubscribeEvent public static void login(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent e){migrateLegacyBackpack(e.player);sync(e.player);}
    @SubscribeEvent public static void respawn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent e){sync(e.player);}
    @SubscribeEvent public static void dimension(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent e){sync(e.player);}
    @SubscribeEvent public static void clone(PlayerEvent.Clone e){for(Kind kind:Kind.values())receive(e.getEntityPlayer(),kind,get(e.getOriginal(),kind));}
    @SubscribeEvent public static void track(PlayerEvent.StartTracking e){
        if(e.getTarget() instanceof EntityPlayer && e.getEntityPlayer() instanceof EntityPlayerMP)
            for(Kind kind:Kind.values())MetroPort.NETWORK.sendTo(new ExtraEquipmentSyncPacket((EntityPlayer)e.getTarget(),kind,get((EntityPlayer)e.getTarget(),kind)),(EntityPlayerMP)e.getEntityPlayer());
    }
    @SubscribeEvent public static void death(LivingDropsEvent e){
        if(!(e.getEntityLiving() instanceof EntityPlayer) || e.getEntityLiving().world.isRemote)return;
        EntityPlayer p=(EntityPlayer)e.getEntityLiving();if(p.world.getGameRules().getBoolean("keepInventory"))return;
        for(Kind kind:Kind.values()){
            ItemStack stack=get(p,kind);if(stack.isEmpty() || NoDrop.marked(stack))continue;
            set(p,kind,ItemStack.EMPTY);e.getDrops().add(new EntityItem(p.world,p.posX,p.posY,p.posZ,stack));
        }
    }
    @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent e){
        EntityPlayer p=e.player;if(e.phase!=TickEvent.Phase.END || p.world.isRemote)return;
        finishPendingEquip(p);if(p.ticksExisted%20!=0)return;
        if(p.openContainer instanceof MetroContainer || p.openContainer instanceof ru.metrocraft.port.blackrazid.BackpackContainer)return;
        migrateLegacyBackpack(p);sync(p);
    }
}
