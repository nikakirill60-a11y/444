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
    @Override public void inventoryMode(boolean vanilla,boolean open) {
        net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(()->InventoryComparison.receive(vanilla,open));
    }
    @Override public void init() { ClientEvents.initialize(); }
}
