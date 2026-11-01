package ru.metrocraft.port.client;

import ru.metrocraft.port.CommonProxy;
import ru.metrocraft.port.ClientEvents;

public final class ClientProxy extends CommonProxy {
    @Override public void preInit() { LegacyEntityRenderers.register();
        ExodusClient.registerEntities();
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(
                ru.metrocraft.port.gates.GateTile.class, new ru.metrocraft.port.client.gates.GateRenderer()); }
    @Override public void shotFeedback(String gun,float pitch,float yaw) {
        net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(()->GunClient.shot(gun,pitch,yaw));
    }
    @Override public void init() { ClientEvents.initialize();
        ((net.minecraft.client.resources.IReloadableResourceManager)net.minecraft.client.Minecraft.getMinecraft().getResourceManager()).registerReloadListener(manager->McmodAkRenderer.reload()); }
}
