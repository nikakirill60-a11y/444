package ru.metrocraft.port.radiation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import ru.metrocraft.port.*;
import java.util.*;

/** Loaded sources only, one authoritative reading/damage decision per player; safe air wins. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class RadiationSystem {
    public enum Zone { LEGACY, RADIATION, AIR }
    private static final Map<World,Map<BlockPos,Zone>> SOURCES=new WeakHashMap<>();
    private static final DamageSource DAMAGE=new DamageSource("metroRadiation").setDamageBypassesArmor();
    private RadiationSystem(){}
    public static void register(World world,BlockPos pos,Zone zone){if(!world.isRemote)SOURCES.computeIfAbsent(world,w->new HashMap<>()).put(pos.toImmutable(),zone);}
    public static void remove(World world,BlockPos pos){if(world.isRemote)return;Map<BlockPos,Zone> sources=SOURCES.get(world);if(sources!=null)sources.remove(pos);}
    public static boolean inside(Zone zone,BlockPos pos,double x,double y,double z){
        if(zone==Zone.LEGACY)return x>=pos.getX()-10 && x<pos.getX()+11 && y>=pos.getY()-10 && y<pos.getY()+11 && z>=pos.getZ()-10 && z<pos.getZ()+11;
        return Math.abs(x-pos.getX()-.5)<5 && Math.abs(y-pos.getY()-5)<5 && Math.abs(z-pos.getZ()-.5)<5;
    }
    public static int combine(boolean radiation,boolean safeAir){return radiation && !safeAir?3:0;}
    public static int level(EntityPlayer player){
        if(player.world.isRemote)return 0;
        Map<BlockPos,Zone> sources=SOURCES.get(player.world);if(sources==null)return 0;
        boolean radiation=false,air=false;
        for(Map.Entry<BlockPos,Zone> entry:sources.entrySet()){
            if(entry.getValue()==Zone.LEGACY ? new net.minecraft.util.math.AxisAlignedBB(entry.getKey()).grow(10).intersects(player.getEntityBoundingBox()) : inside(entry.getValue(),entry.getKey(),player.posX,player.posY,player.posZ)){
                if(entry.getValue()==Zone.AIR)air=true;else radiation=true;
            }
        }
        return combine(radiation,air);
    }
    public static boolean filterProtects(ItemStack stack){return !stack.isEmpty() && stack.getItem() instanceof MetroArmor && ((MetroArmor)stack.getItem()).isGasMask()
            && stack.hasTagCompound() && stack.getTagCompound().getInteger("MetroFilterTicks")>0;}
    public static boolean protectedBy(EntityPlayer player){return filterProtects(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD)) || filterProtects(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));}
    @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent event){
        EntityPlayer player=event.player;
        if(event.phase!=TickEvent.Phase.END || player.world.isRemote || player.ticksExisted%20!=0 || !player.isEntityAlive() || player.capabilities.isCreativeMode || player.isSpectator())return;
        int level=level(player);if(level>0 && !protectedBy(player))player.attackEntityFrom(DAMAGE,level);
    }
    @SubscribeEvent public static void unload(WorldEvent.Unload event){if(!event.getWorld().isRemote)SOURCES.remove(event.getWorld());}
}
