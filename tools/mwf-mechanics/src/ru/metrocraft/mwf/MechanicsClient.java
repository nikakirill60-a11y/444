package ru.metrocraft.mwf;

import java.lang.reflect.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import static ru.metrocraft.mwf.MechanicsHooks.*;

/** Loaded reflectively on the physical client only; no client references in common class signatures. */
public final class MechanicsClient {
    private static Object key;
    private static long lastSent=Long.MIN_VALUE,busyUntil;
    private static Object lastWorld;
    private static Object minecraft() throws Exception {return call(Class.forName("net.minecraft.client.Minecraft"),"getMinecraft|func_71410_x");}
    public static void install() throws Exception {
        Class<?> binding=Class.forName("net.minecraft.client.settings.KeyBinding");
        key=binding.getConstructor(String.class,int.class,String.class).newInstance("key.metro.pump",25,"key.categories.metro_mechanics"); // LWJGL KEY_P
        call(Class.forName("net.minecraftforge.fml.client.registry.ClientRegistry"),"registerKeyBinding",key);
        Object bus=field(Class.forName("net.minecraftforge.common.MinecraftForge"),"EVENT_BUS");call(bus,"register",new MechanicsClient());
        Object proxy=field(Class.forName("com.modularwarfare.ModularWarfare"),"PROXY");
        call(proxy,"registerSound","metro_pump");call(proxy,"registerSound","metro_charge");
    }
    public static boolean blocksFire() throws Exception {
        Object mc=minecraft(),player=field(mc,"player","field_71439_g");
        if(player==null)return false;
        return (key!=null&&field(mc,"currentScreen","field_71462_r")==null&&Boolean.TRUE.equals(call(key,"isKeyDown|func_151470_d")))
            || now(player)<busyUntil;
    }
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event) {
        if(event.phase!=TickEvent.Phase.END)return;
        try {
            Object mc=minecraft(),world=field(mc,"world","field_71441_e"),player=field(mc,"player","field_71439_g");
            if(world!=lastWorld){lastWorld=world;lastSent=Long.MIN_VALUE;busyUntil=0;}
            if(player==null||world==null||field(mc,"currentScreen","field_71462_r")!=null||key==null)return;
            if(!Boolean.TRUE.equals(call(key,"isKeyDown|func_151470_d")))return;
            Object stack=held(player);String id=id(stack);
            if(!MechanicsRules.energyGun(id)||tag(stack)==null||energy(stack)>=maximum(stack))return;
            long tick=now(player);if(lastSent!=Long.MIN_VALUE&&tick-lastSent<10)return;
            // Do not predict a pressure increase: wait for authoritative inventory/NBT sync.
            int slot=((Number)field(field(player,"inventory","field_71071_by"),"currentItem","field_70461_c")).intValue();
            Object network=field(Class.forName("com.modularwarfare.ModularWarfare"),"NETWORK");
            call(network,"sendToServer",new PacketMetroPump(slot));lastSent=tick;busyUntil=tick+10;
        } catch(Exception e) {throw new IllegalStateException("Metro pump client tick failed",e);}
    }
    @SubscribeEvent public void overlay(RenderGameOverlayEvent.Post event) {
        if(event.getType()!=RenderGameOverlayEvent.ElementType.ALL)return;
        try {
            Object mc=minecraft(),player=field(mc,"player","field_71439_g");if(player==null)return;
            Object stack=held(player);String id=id(stack);if(!MechanicsRules.energyGun(id))return;
            Object resolution=call(event,"getResolution");
            int width=((Number)call(resolution,"getScaledWidth|func_78326_a")).intValue();
            int height=((Number)call(resolution,"getScaledHeight|func_78328_b")).intValue();
            int value=energy(stack),max=maximum(stack),color=value<MechanicsRules.cost(id)?0xff7777:0xffdd77;
            String text=("metro_railgun".equals(id)?"Заряд ":"Давление ")+value+"/"+max;
            Object font=field(mc,"fontRenderer","field_71466_p");
            call(font,"drawStringWithShadow|func_175063_a",text,(float)(width/2+95),(float)(height-52),color);
            String help=String.valueOf(call(Class.forName("net.minecraft.client.settings.GameSettings"),"getKeyDisplayString|func_74298_c",((Number)call(key,"getKeyCode|func_151463_i")).intValue()));
            call(font,"drawStringWithShadow|func_175063_a","["+help+"] "+("metro_railgun".equals(id)?"зарядить":"накачать"),(float)(width/2+95),(float)(height-41),0xaaaaaa);
        } catch(Exception e) {throw new IllegalStateException("Metro energy HUD failed",e);}
    }
}
