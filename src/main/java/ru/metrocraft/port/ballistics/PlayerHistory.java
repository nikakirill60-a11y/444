package ru.metrocraft.port.ballistics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import ru.metrocraft.port.MetroPort;
import java.util.*;

/** Same 20 snapshots and ping/50 selection as the supplied PlayerData/EntityBullet. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class PlayerHistory {
    private static final Map<World,Map<UUID,Deque<LagSnapshot>>> HISTORY=new WeakHashMap<>();
    public static int indexForPing(int ping){return Math.max(0,Math.min(19,ping/50));}
    private static LagSnapshot capture(EntityPlayer p){
        return new LagSnapshot(p.posX,p.posY,p.posZ,p.renderYawOffset,p.rotationYawHead,p.rotationPitch,p.ticksExisted);
    }
    @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent event){
        if(event.player.world.isRemote||event.phase!=TickEvent.Phase.END||!event.player.isEntityAlive())return;
        Deque<LagSnapshot> history=HISTORY.computeIfAbsent(event.player.world,w->new HashMap<>())
                .computeIfAbsent(event.player.getUniqueID(),id->new ArrayDeque<>());
        history.addFirst(capture(event.player));while(history.size()>20)history.removeLast();
    }
    public static LagSnapshot.Hit trace(EntityPlayer target,int index,Vec3d start,Vec3d end){
        Map<UUID,Deque<LagSnapshot>> world=HISTORY.get(target.world);
        Deque<LagSnapshot> history=world==null?null:world.get(target.getUniqueID());
        LagSnapshot snapshot=null;
        if(history!=null&&!history.isEmpty()){
            if(index>=history.size())snapshot=history.peekFirst();
            else {int i=0;for(LagSnapshot entry:history){snapshot=entry;if(i++>=index)break;}}
        }
        if(snapshot==null)snapshot=capture(target);
        return snapshot.trace(start,end);
    }
    private static void clear(UUID id){for(Map<UUID,Deque<LagSnapshot>> world:HISTORY.values())world.remove(id);}
    @SubscribeEvent public static void clone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event){clear(event.getEntityPlayer().getUniqueID());}
    @SubscribeEvent public static void logout(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent event){clear(event.player.getUniqueID());}
    @SubscribeEvent public static void dimension(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event){clear(event.player.getUniqueID());}
    @SubscribeEvent public static void unload(WorldEvent.Unload event){HISTORY.remove(event.getWorld());}
}
