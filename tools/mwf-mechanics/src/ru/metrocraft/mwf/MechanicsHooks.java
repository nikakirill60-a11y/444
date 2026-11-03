package ru.metrocraft.mwf;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Logger;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/** Integrated into the supplied MWF binary. Server-authoritative, per ItemStack NBT. */
public final class MechanicsHooks {
    public static final String ENERGY = "metroEnergy", LEAK = "metroEnergyTime";
    private static final Logger LOG = Logger.getLogger("MetroMWFMechanics");
    private static final Map<Object, State> STATES = new HashMap<Object, State>();
    private static final java.util.concurrent.ConcurrentMap<Class<?>,Method[]> METHOD_LISTS = new java.util.concurrent.ConcurrentHashMap<Class<?>,Method[]>();
    private static final java.util.concurrent.ConcurrentMap<Class<?>,java.util.concurrent.ConcurrentMap<String,Field>> FIELDS = new java.util.concurrent.ConcurrentHashMap<Class<?>,java.util.concurrent.ConcurrentMap<String,Field>>();
    private static boolean installed;
    static final class State {
        long lastPump = Long.MIN_VALUE, busyUntil, nextFire;
        Shot shot;
    }
    static final class Shot {
        Object stack; long expires; float multiplier; int remaining, pellets;
        final Map<Integer,Integer> perTarget=new HashMap<Integer,Integer>();
        Shot(Object stack, long expires, float multiplier, int pellets) {
            this.stack=stack; this.expires=expires; this.multiplier=multiplier; this.pellets=pellets; this.remaining=pellets*4;
        }
    }
    public static void install() {
        if (installed) throw new IllegalStateException("Metro mechanics installed twice");
        try {
            Object network = field(Class.forName("com.modularwarfare.ModularWarfare"), "NETWORK");
            if (!Boolean.TRUE.equals(call(network, "registerPacket", PacketMetroPump.class)))
                throw new IllegalStateException("Cannot register Metro pump packet");
            Object bus=field(Class.forName("net.minecraftforge.common.MinecraftForge"), "EVENT_BUS");
            call(bus,"register",new MechanicsHooks());
            call(bus,"register",new AttachmentTransactions());
            Object handler=call(Class.forName("net.minecraftforge.fml.common.FMLCommonHandler"),"instance");
            if ("CLIENT".equals(String.valueOf(call(handler,"getSide"))))
                call(Class.forName("ru.metrocraft.mwf.MechanicsClient"),"install");
            installed=true;
            LOG.info("Metro mechanics 1.0: integrated pressure/energy, dynamic Duplet capacity and server shot authorization");
        } catch(Exception e) { throw new IllegalStateException("Metro mechanics initialization failed",e); }
    }
    private static State state(Object player) throws Exception {
        Object uuid=call(player,"getUniqueID|func_110124_au");
        State s=STATES.get(uuid); if(s==null) {s=new State(); STATES.put(uuid,s);} return s;
    }
    public static String id(Object stack) throws Exception {
        if(stack==null || Boolean.TRUE.equals(call(stack,"isEmpty|func_190926_b")))return "";
        String id=String.valueOf(call(call(stack,"getItem|func_77973_b"),"getRegistryName"));
        return id.startsWith("modularwarfare:")?id.substring("modularwarfare:".length()):"";
    }
    static boolean scoped(String id) { return MechanicsRules.energyGun(id)||"metro_duplet".equals(id); }
    static Object held(Object player) throws Exception { return call(player,"getHeldItemMainhand|func_184614_ca"); }
    static Object tag(Object stack) throws Exception { return call(stack,"getTagCompound|func_77978_p"); }
    static long now(Object player) throws Exception { return ((Number)call(field(player,"world","field_70170_p"),"getTotalWorldTime|func_82737_E")).longValue(); }
    static boolean remote(Object player) throws Exception { return Boolean.TRUE.equals(field(field(player,"world","field_70170_p"),"isRemote","field_72995_K")); }
    public static boolean attachment(Object stack,String module) throws Exception {
        Object n=tag(stack); if(n==null)return false;
        Object a=call(n,"getCompoundTag|func_74775_l","attachment_barrel");
        return ("modularwarfare:"+module).equals(String.valueOf(call(a,"getString|func_74779_i","id")))
            && ((Number)call(a,"getByte|func_74771_c","Count")).intValue()==1;
    }
    public static int maximum(Object stack) throws Exception { return MechanicsRules.maximum(id(stack),attachment(stack,"metro_mod_railgun_akk")); }
    public static int energy(Object stack) throws Exception {
        Object n=tag(stack); return n==null?0:MechanicsRules.clamp(((Number)call(n,"getInteger|func_74762_e",ENERGY)).intValue(),maximum(stack));
    }
    static boolean busy(Object player) throws Exception {
        Object uuid=call(player,"getUniqueID|func_110124_au");
        Class<?> h=Class.forName("com.modularwarfare.common.handler.ServerTickHandler");
        return ((Map<?,?>)field(h,"playerReloadCooldown")).containsKey(uuid)
            || ((Map<?,?>)field(h,"reloadEnhancedTask")).containsKey(uuid);
    }
    static boolean alive(Object player) throws Exception {
        return Boolean.TRUE.equals(call(player,"isEntityAlive|func_70089_S"))
            && !Boolean.TRUE.equals(call(player,"isSpectator|func_175149_v"));
    }
    public static Integer capacity(Object gunType,Object stack) {
        try {
            Integer base=(Integer)field(gunType,"internalAmmoStorage");
            if (!"metro_duplet".equals(String.valueOf(field(gunType,"internalName"))))return base;
            return Integer.valueOf(MechanicsRules.capacity("metro_duplet",attachment(stack,"metro_duplet_extra_barrels"),2));
        } catch(Exception e) { throw new IllegalStateException("Cannot resolve per-stack ammunition capacity",e); }
    }
    /** Gate the enhanced logical-shot packet before the native ammo consumption/sound path. */
    public static boolean beforeShot(Object player,Object packet) {
        try {
            Object stack=held(player); String actual=id(stack), claimed=String.valueOf(field(packet,"internalname"));
            if(!scoped(actual)&&!scoped(claimed))return true;
            if(remote(player)||!actual.equals(claimed)||!alive(player)||tag(stack)==null)return reject(player);
            long tick=now(player); State s=state(player);
            if(busy(player)||tick<s.nextFire||tick<s.busyUntil)return reject(player);
            // Native high-ping branch must not spend energy or authorize hits either.
            if(((Number)field(player,"ping","field_71138_i")).intValue()>2000)return reject(player);
            Class<?> ig=Class.forName("com.modularwarfare.common.guns.ItemGun");
            if(!Boolean.TRUE.equals(call(ig,"hasNextShot",stack)))return reject(player);
            Object gt=field(call(stack,"getItem|func_77973_b"),"type");
            int rpm=((Number)field(gt,"roundsPerMin")).intValue();
            float factor=1;
            if(MechanicsRules.energyGun(actual)) {
                int value=energy(stack);
                if(value<MechanicsRules.cost(actual))return reject(player);
                Object n=tag(stack);long since=1000;
                if("metro_railgun".equals(actual)&&Boolean.TRUE.equals(call(n,"hasKey|func_74764_b","metroRailLastShot"))) {
                    long last=((Number)call(n,"getLong|func_74763_f","metroRailLastShot")).longValue();
                    if(last<=tick)since=tick-last;
                }
                factor=MechanicsRules.damage(actual,value,since,attachment(stack,"metro_mod_railgun_kon"));
                call(n,"setInteger|func_74768_a",ENERGY,MechanicsRules.afterShot(actual,value,maximum(stack)));
                if("metro_railgun".equals(actual))call(n,"setLong|func_74772_a","metroRailLastShot",tick);
            }
            s.nextFire=tick+MechanicsRules.fireInterval(rpm);
            int pellets=Math.max(1,((Number)field(gt,"numBullets")).intValue());
            // Reserve one logical shot, not one pressure charge per pellet/penetration hit.
            s.shot=new Shot(stack,tick+5,factor,pellets);
            return true;
        } catch(Exception e) { LOG.warning("Shot refused: "+e); return reject(player); }
    }
    /** Hit packets can never authorize themselves, spend pressure again or outlive their shot. */
    public static boolean beforeHit(Object player,Object packet) {
        try {
            Object stack=held(player); String actual=id(stack), claimed=String.valueOf(field(packet,"internalname"));
            if(!scoped(actual)&&!scoped(claimed))return true;
            if(!actual.equals(claimed))return false;
            Shot shot=state(player).shot;
            if(shot==null||shot.stack!=stack||now(player)>shot.expires||shot.remaining<=0)return false;
            int target=((Number)field(packet,"entityId")).intValue();
            if(target>=0) {
                Integer previous=shot.perTarget.get(target);int count=previous==null?0:previous.intValue();
                if(count>=shot.pellets)return false;
                shot.perTarget.put(target,count+1);
            }
            --shot.remaining; return true;
        } catch(Exception e) { LOG.warning("Hit refused: "+e); return false; }
    }
    /** The Metro pack is Enhanced. Never let a legacy/basic packet bypass its logical-shot gate. */
    public static boolean allowLegacy(Object player,Object stack) {
        try {return !scoped(id(held(player)))&&!scoped(id(stack))?true:reject(player);}
        catch(Exception e){LOG.warning("Legacy shot refused: "+e);return false;}
    }
    public static float scaleDamage(float base,Object player) {
        try {
            if(!scoped(id(held(player))))return base;
            Shot shot=state(player).shot;
            return shot!=null&&shot.stack==held(player)&&now(player)<=shot.expires?base*shot.multiplier:0;
        } catch(Exception e) {return 0;}
    }
    public static boolean clientCanFire(boolean nativeAllowed,Object player) {
        if(!nativeAllowed)return false;
        try {
            Object stack=held(player); String id=id(stack);
            if(!MechanicsRules.energyGun(id))return true;
            return energy(stack)>=MechanicsRules.cost(id)
                && !Boolean.TRUE.equals(call(Class.forName("ru.metrocraft.mwf.MechanicsClient"),"blocksFire"));
        } catch(Exception e) {LOG.warning("Client fire refused: "+e);return false;}
    }
    public static void afterConsume(Object stack) {
        try {
            String id=id(stack); if(!MechanicsRules.energyGun(id)||tag(stack)==null)return;
            Object handler=call(Class.forName("net.minecraftforge.fml.common.FMLCommonHandler"),"instance");
            if("CLIENT".equals(String.valueOf(call(handler,"getEffectiveSide"))))
                call(tag(stack),"setInteger|func_74768_a",ENERGY,MechanicsRules.afterShot(id,energy(stack),maximum(stack)));
        } catch(Exception e) { throw new IllegalStateException("Client energy prediction failed",e); }
    }
    public static boolean pump(Object player,int slot) {
        try {
            if(remote(player)||!alive(player)||slot<0||slot>8)return false;
            Object inv=field(player,"inventory","field_71071_by");
            if(((Number)field(inv,"currentItem","field_70461_c")).intValue()!=slot)return false;
            Object stack=held(player);String id=id(stack);Object n=tag(stack);
            if(!MechanicsRules.energyGun(id)||n==null||busy(player))return false;
            long tick=now(player);State s=state(player);
            if(tick<s.nextFire||tick<s.busyUntil||(s.lastPump!=Long.MIN_VALUE&&tick-s.lastPump<10))return false;
            int value=energy(stack), maximum=maximum(stack);if(value>=maximum)return false;
            call(n,"setInteger|func_74768_a",ENERGY,MechanicsRules.pump(value,maximum));
            call(n,"setLong|func_74772_a",LEAK,tick);
            s.lastPump=tick;s.busyUntil=tick+10;
            sync(player); playPump(player,id);
            return true;
        } catch(Exception e) {LOG.warning("Pump refused: "+e);return false;}
    }
    static void playPump(Object player,String id) {
        try {
            Object pos=call(player,"getPosition|func_180425_c");
            Class<?> packet=Class.forName("com.modularwarfare.common.network.PacketPlaySound");
            Object p=null;
            for(Constructor<?> c:packet.getConstructors()) if(c.getParameterTypes().length==4) {
                p=c.newInstance(pos,"metro_"+("metro_railgun".equals(id)?"charge":"pump"),.65f,1f);break;
            }
            Object network=field(Class.forName("com.modularwarfare.ModularWarfare"),"NETWORK");
            call(network,"sendToAllAround",p,((Number)field(player,"posX","field_70165_t")).doubleValue(),
                ((Number)field(player,"posY","field_70163_u")).doubleValue(),((Number)field(player,"posZ","field_70161_v")).doubleValue(),
                16f,((Number)field(player,"dimension","field_71093_bK")).intValue());
        } catch(Exception e) {LOG.fine("Pump audio unavailable: "+e);}
    }
    static boolean reject(Object player) {try{sync(player);}catch(Exception e){LOG.fine("Resync: "+e);}return false;}
    static void sync(Object player) throws Exception {
        Object inv=field(player,"inventory","field_71071_by"); call(inv,"markDirty|func_70296_d");
        Object container=field(player,"inventoryContainer","field_71069_bz");
        int slot=((List<?>)field(container,"inventorySlots","field_75151_b")).size()-10+((Number)field(inv,"currentItem","field_70461_c")).intValue();
        call(player,"sendSlotContents|func_71111_a",container,slot,held(player));
    }
    @SubscribeEvent public void tick(TickEvent.PlayerTickEvent event) {
        if(event.phase!=TickEvent.Phase.END)return;
        Object player=event.player;
        try {
            if(remote(player))return;
            long tick=now(player); Object uuid=call(player,"getUniqueID|func_110124_au");State s=STATES.get(uuid);
            if(s!=null&&s.shot!=null&&tick>s.shot.expires)s.shot=null;
            // Idle leakage has one-second resolution; do not scan 41 inventory slots every tick.
            if(tick%20!=0)return;
            Object inv=field(player,"inventory","field_71071_by");
            for(int i=0;i<41;i++) {
                Object stack=call(inv,"getStackInSlot|func_70301_a",i);String id=id(stack);
                if(!MechanicsRules.energyGun(id)||tag(stack)==null)continue;
                Object n=tag(stack); int energy=energy(stack), maximum=maximum(stack);
                int raw=((Number)call(n,"getInteger|func_74762_e",ENERGY)).intValue();
                boolean changed=raw!=energy;
                if(!"metro_railgun".equals(id)&&energy>100) {
                    long last=((Number)call(n,"getLong|func_74763_f",LEAK)).longValue();
                    if(!Boolean.TRUE.equals(call(n,"hasKey|func_74764_b",LEAK))||last>tick)last=tick;
                    boolean valve=attachment(stack,"metro_mod_tihar_valve")||attachment(stack,"metro_mod_helsing_valve");
                    int next=valve?energy:MechanicsRules.leak(energy,tick-last);
                    if(next!=energy){changed=true;energy=next;last+=((tick-last)/20)*20;}
                    call(n,"setLong|func_74772_a",LEAK,last);
                }
                if(changed) {call(n,"setInteger|func_74768_a",ENERGY,MechanicsRules.clamp(energy,maximum));call(inv,"markDirty|func_70296_d");}
            }
        } catch(Exception e) {LOG.warning("Energy tick failed: "+e);}
    }
    @SubscribeEvent public void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        try{STATES.remove(call(event.player,"getUniqueID|func_110124_au"));}catch(Exception e){LOG.fine(e.toString());}
    }
    /** MCP + SRG lookup keeps one binary usable in Forge production and deobfuscated tests. */
    public static Object field(Object target,String... names) throws Exception {
        Class<?> type=target instanceof Class?(Class<?>)target:target.getClass();
        java.util.concurrent.ConcurrentMap<String,Field> cache=FIELDS.get(type);
        if(cache==null){cache=new java.util.concurrent.ConcurrentHashMap<String,Field>();java.util.concurrent.ConcurrentMap<String,Field> prior=FIELDS.putIfAbsent(type,cache);if(prior!=null)cache=prior;}
        String key=Arrays.toString(names);Field saved=cache.get(key);
        if(saved!=null)return saved.get(target instanceof Class?null:target);
        for(Class<?> c=type;c!=null;c=c.getSuperclass())for(String name:names)try {
            Field f=c.getDeclaredField(name);f.setAccessible(true);cache.putIfAbsent(key,f);return f.get(target instanceof Class?null:target);
        }catch(NoSuchFieldException ignored){}
        throw new NoSuchFieldException(type.getName()+Arrays.toString(names));
    }
    static Class<?> boxed(Class<?> c) {
        if(c==int.class)return Integer.class;if(c==boolean.class)return Boolean.class;if(c==float.class)return Float.class;
        if(c==long.class)return Long.class;if(c==double.class)return Double.class;if(c==byte.class)return Byte.class;return c;
    }
    public static Object call(Object target,String names,Object... args) throws Exception {
        if(target==null)throw new NullPointerException(names);
        Class<?> type=target instanceof Class?(Class<?>)target:target.getClass();
        Method[] methods=METHOD_LISTS.get(type);
        if(methods==null){methods=type.getMethods();Method[] prior=METHOD_LISTS.putIfAbsent(type,methods);if(prior!=null)methods=prior;}
        for(String name:names.split("\\|"))for(Method m:methods)if(m.getName().equals(name)&&m.getParameterTypes().length==args.length) {
            boolean match=true;Class<?>[] types=m.getParameterTypes();
            for(int i=0;i<args.length;i++)if(args[i]!=null&&!boxed(types[i]).isInstance(args[i]))match=false;
            if(match)try{return m.invoke(target instanceof Class?null:target,args);}catch(InvocationTargetException e){
                if(e.getCause() instanceof Exception)throw(Exception)e.getCause();throw e;
            }
        }
        throw new NoSuchMethodException(type.getName()+"."+names);
    }
}
