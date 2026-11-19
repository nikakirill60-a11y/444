package ru.metrocraft.port.client;

import java.util.*;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import ru.metrocraft.port.MetroPort;

/** Some player/weapon mods replace RenderPlayer after init. Install on the actual renderer once. */
@Mod.EventBusSubscriber(modid=MetroPort.ID,value=Side.CLIENT)
public final class EquipmentLayers {
    private static final Set<RenderPlayer> INSTALLED=Collections.newSetFromMap(new WeakHashMap<RenderPlayer,Boolean>());
    public static void install(RenderPlayer renderer){
        if(!INSTALLED.add(renderer))return;
        renderer.addLayer(new NvgLayer(renderer));renderer.addLayer(new WatchClient(renderer));
    }
    @SubscribeEvent public static void render(RenderPlayerEvent.Pre event){install(event.getRenderer());}
    private EquipmentLayers(){}
}
