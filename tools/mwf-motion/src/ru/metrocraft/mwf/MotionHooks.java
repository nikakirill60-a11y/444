package ru.metrocraft.mwf;

import java.util.*;
import java.util.logging.Logger;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import static ru.metrocraft.mwf.MechanicsHooks.*;

/** Additional metro5 rules. Original metro4 mechanics remain in their original classes. */
public final class MotionHooks {
    private static final Logger LOG=Logger.getLogger("MetroMWFMotion");
    private static final Map<Object,State> STATES=new HashMap<Object,State>();
    static final class State {
        final RotorRules.Rotor rotor=new RotorRules.Rotor();
        Object stack,world;int slot;long refreshed=Long.MIN_VALUE,nextShot,ticketUntil;boolean pressed;int remaining;
        final Set<Integer> targets=new HashSet<Integer>();
    }
    static boolean gatling(Object stack)throws Exception{return RotorRules.GUN.equals(id(stack));}
    static Object uuid(Object p)throws Exception{return call(p,"getUniqueID|func_110124_au");}
    static int slot(Object p)throws Exception{return ((Number)field(field(p,"inventory","field_71071_by"),"currentItem","field_70461_c")).intValue();}
    static boolean has(Object stack,String slot,String id)throws Exception {
        Object n=tag(stack);if(n==null)return false;
        Object a=call(n,"getCompoundTag|func_74775_l","attachment_"+slot);
        return ("modularwarfare:"+id).equals(String.valueOf(call(a,"getString|func_74779_i","id")))&&((Number)call(a,"getByte|func_74771_c","Count")).intValue()==1;
    }
    static boolean usable(Object p)throws Exception {
        return alive(p)&&!busy(p)&&!Boolean.TRUE.equals(call(p,"isSprinting|func_70051_ag"))
            &&Boolean.TRUE.equals(call(Class.forName("com.modularwarfare.common.guns.ItemGun"),"hasNextShot",held(p)));
    }
    static State state(Object p)throws Exception {
        Object key=uuid(p),stack=held(p),world=field(p,"world","field_70170_p");int slot=slot(p);State s=STATES.get(key);
        if(s==null||s.stack!=stack||s.world!=world||s.slot!=slot){s=new State();s.stack=stack;s.world=world;s.slot=slot;STATES.put(key,s);}
        return s;
    }
    public static void install()throws Exception {
        Object network=field(Class.forName("com.modularwarfare.ModularWarfare"),"NETWORK");
        if(!Boolean.TRUE.equals(call(network,"registerPacket",PacketMetroRotor.class)))throw new IllegalStateException("Cannot register rotor trigger");
        call(field(Class.forName("net.minecraftforge.common.MinecraftForge"),"EVENT_BUS"),"register",new MotionHooks());
        Object fml=call(Class.forName("net.minecraftforge.fml.common.FMLCommonHandler"),"instance");
        if("CLIENT".equals(String.valueOf(call(fml,"getSide"))))call(Class.forName("ru.metrocraft.mwf.MotionClient"),"install");
    }
    public static void trigger(Object p,int slot,boolean pressed){
        try {
            if(remote(p)||slot<0||slot>8||slot(p)!=slot||!gatling(held(p)))return;
            State s=state(p);s.pressed=pressed&&usable(p);s.refreshed=now(p);
        }catch(Exception e){LOG.warning("Rotor trigger refused: "+e);}
    }
    @SubscribeEvent public void tick(TickEvent.PlayerTickEvent event){
        if(event.phase!=TickEvent.Phase.END)return;
        try {
            Object p=event.player;if(remote(p))return;
            if(!gatling(held(p))){STATES.remove(uuid(p));return;}
            State s=state(p);long tick=now(p);
            boolean active=s.pressed&&tick>=s.refreshed&&tick-s.refreshed<=10&&usable(p);
            if(!active)s.pressed=false;
            s.rotor.tick(active,has(s.stack,"grip","metro_mod_gatling_motor"));
            // Releasing the trigger stops NEW shots, not hits from a shot already
            // authorized in the preceding five ticks (e.g. a penetration packet).
            if(tick%4==0){
                int entity=((Number)call(p,"getEntityId|func_145782_y")).intValue();
                call(field(Class.forName("com.modularwarfare.ModularWarfare"),"NETWORK"),"sendToAllTracking",
                    new PacketMetroRotor(entity,(int)Math.round(s.rotor.speed*1000),(int)(s.rotor.angle*1000)),p);
            }
        }catch(Exception e){LOG.warning("Rotor tick failed: "+e);}
    }
    @SubscribeEvent public void logout(PlayerEvent.PlayerLoggedOutEvent e){try{STATES.remove(uuid(e.player));}catch(Exception ignored){}}
    public static Object reloadTask(Object p){
        Object original=MechanicsHooks.reloadTask(p);if(original!=null)return original;
        try{return gatling(held(p))?((Map<?,?>)field(Class.forName("com.modularwarfare.common.handler.ServerTickHandler"),"reloadEnhancedTask")).get(uuid(p)):null;}
        catch(Exception e){throw new IllegalStateException("Cannot capture rotor reload",e);}
    }
    public static boolean beforeShot(Object p,Object packet){
        try {
            String actual=id(held(p)),claimed=String.valueOf(field(packet,"internalname"));
            if(!RotorRules.GUN.equals(actual)&&!RotorRules.GUN.equals(claimed))return MechanicsHooks.beforeShot(p,packet);
            if(remote(p)||!actual.equals(claimed)||!usable(p))return reject(p);
            State s=state(p);long tick=now(p);
            if(!s.pressed||tick<s.refreshed||tick-s.refreshed>10||!s.rotor.ready()||tick<s.nextShot)return reject(p);
            s.nextShot=tick+s.rotor.interval(has(s.stack,"barrel","metro_mod_gatling_barrel"));
            s.ticketUntil=tick+5;s.remaining=4;s.targets.clear();return true;
        }catch(Exception e){LOG.warning("Rotor shot refused: "+e);return reject(p);}
    }
    public static boolean beforeHit(Object p,Object packet){
        try {
            String actual=id(held(p)),claimed=String.valueOf(field(packet,"internalname"));
            if(!RotorRules.GUN.equals(actual)&&!RotorRules.GUN.equals(claimed))return MechanicsHooks.beforeHit(p,packet);
            if(!actual.equals(claimed))return false;State s=state(p);long tick=now(p);
            if(!alive(p)||s.remaining<=0||tick>s.ticketUntil)return false;
            int target=((Number)field(packet,"entityId")).intValue();
            if(target>=0&&!s.targets.add(target))return false;
            --s.remaining;return true;
        }catch(Exception e){LOG.warning("Rotor hit refused: "+e);return false;}
    }
    public static boolean allowLegacy(Object p,Object stack){
        try{if(gatling(stack)||gatling(held(p)))return reject(p);}catch(Exception e){return false;}
        return MechanicsHooks.allowLegacy(p,stack);
    }
    public static boolean clientCanFire(boolean allowed,Object p){
        if(!MechanicsHooks.clientCanFire(allowed,p))return false;
        try {return !gatling(held(p))||Boolean.TRUE.equals(call(Class.forName("ru.metrocraft.mwf.MotionClient"),"ready"));}
        catch(Exception e){LOG.warning("Rotor prediction refused: "+e);return false;}
    }
    /** Only the Enhanced client cooldown read is intercepted, NOT packet ABI/anti-cheat RPM fields. */
    public static int clientRpm(int nativeRpm,Object p){
        try {return gatling(held(p))?((Number)call(Class.forName("ru.metrocraft.mwf.MotionClient"),"rpm")).intValue():nativeRpm;}
        catch(Exception e){return nativeRpm;}
    }
}
