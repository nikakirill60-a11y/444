package ru.metrocraft.port.blackrazid.survival;

import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import ru.metrocraft.port.MetroPort;

@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class SurvivalSystem {
    public static final String KEY="MetroBlackrazidSurvival";
    private SurvivalSystem(){}
    public static NBTTagCompound encode(SurvivalState state){
        state.sanitize();NBTTagCompound tag=new NBTTagCompound();tag.setFloat("Water",state.water);tag.setFloat("Energy",state.energy);
        tag.setInteger("StarveTicks",state.starveTicks);tag.setBoolean("Lamp",state.lamp);return tag;
    }
    public static SurvivalState decode(NBTTagCompound tag){
        SurvivalState s=new SurvivalState();if(tag.hasKey("Water",99))s.water=tag.getFloat("Water");
        if(tag.hasKey("Energy",99))s.energy=tag.getFloat("Energy");s.starveTicks=tag.getInteger("StarveTicks");s.lamp=tag.getBoolean("Lamp");s.sanitize();return s;
    }
    public static SurvivalState get(EntityPlayer p){return decode(p.getEntityData().getCompoundTag(KEY));}
    public static void save(EntityPlayer p,SurvivalState s){p.getEntityData().setTag(KEY,encode(s));}
    public static boolean hasLamp(EntityPlayer p){return isLamp(p.getHeldItemMainhand()) || isLamp(p.getHeldItemOffhand());}
    private static boolean isLamp(ItemStack s){return !s.isEmpty() && s.getItem() instanceof SurvivalItem && ((SurvivalItem)s.getItem()).type==SurvivalItem.Type.LAMP;}
    public static void sync(EntityPlayer p){if(p instanceof EntityPlayerMP && !p.world.isRemote)MetroPort.NETWORK.sendTo(new SurvivalPacket(p,get(p)),(EntityPlayerMP)p);}
    @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent e){
        EntityPlayer p=e.player;if(e.phase!=TickEvent.Phase.END || p.world.isRemote || !p.isEntityAlive())return;
        SurvivalState s=get(p);boolean before=s.lamp;
        if(s.tick(p.capabilities.isCreativeMode || p.isSpectator(),hasLamp(p)))p.attackEntityFrom(DamageSource.STARVE,1);
        save(p,s);if(p.ticksExisted%20==0 || before!=s.lamp)sync(p);
    }
    @SubscribeEvent public static void clone(net.minecraftforge.event.entity.player.PlayerEvent.Clone e){
        SurvivalState s=get(e.getOriginal());if(e.isWasDeath()){s.water=100;s.starveTicks=0;}s.lamp=false;save(e.getEntityPlayer(),s);
    }
    @SubscribeEvent public static void login(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent e){
        if(e.player.world.isRemote)return;SurvivalState s=get(e.player);s.lamp=false;save(e.player,s);sync(e.player);
    }
    @SubscribeEvent public static void respawn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent e){sync(e.player);}
    @SubscribeEvent public static void dimension(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent e){sync(e.player);}
}
