package ru.metrocraft.port.mechanics;

import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import ru.metrocraft.port.MetroPort;
import java.util.*;

/** Pellets from one shell accumulate before damage; vanilla i-frames are not bypassed. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class ProjectileDamage {
    private static final Map<World,Map<String,Hit>> PENDING=new WeakHashMap<>();
    private static final class Hit{Entity victim,projectile;EntityPlayer owner;float amount;}
    public static void queue(World world,UUID shot,Entity victim,EntityPlayer owner,Entity projectile,float amount){
        Map<String,Hit> hits=PENDING.computeIfAbsent(world,w->new LinkedHashMap<>());
        String key=shot+":"+victim.getUniqueID();Hit hit=hits.get(key);
        if(hit==null){hit=new Hit();hit.victim=victim;hit.owner=owner;hit.projectile=projectile;hits.put(key,hit);}
        hit.amount+=amount;
    }
    @SubscribeEvent public static void tick(TickEvent.WorldTickEvent event){
        if(event.world.isRemote || event.phase!=TickEvent.Phase.END)return;
        Map<String,Hit> hits=PENDING.remove(event.world);if(hits==null)return;
        for(Hit hit:hits.values())if(hit.victim.isEntityAlive() && hit.owner.isEntityAlive()){
            if(hit.victim instanceof EntityPlayer && !hit.owner.canAttackPlayer((EntityPlayer)hit.victim))continue;
            if(hit.victim.attackEntityFrom(new EntityDamageSourceIndirect("metrobullet",hit.projectile,hit.owner).setProjectile(),hit.amount)
                    && hit.victim instanceof EntityLivingBase){
                EntityLivingBase living=(EntityLivingBase)hit.victim;living.hurtResistantTime=living.maxHurtResistantTime/2;
            }
        }
    }
    @SubscribeEvent public static void unload(WorldEvent.Unload event){PENDING.remove(event.getWorld());}
}
