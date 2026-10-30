package ru.metrocraft.port.client;

import ru.metrocraft.port.CommonProxy;
import ru.metrocraft.port.ClientEvents;

public final class ClientProxy extends CommonProxy {
    @Override public void preInit() { LegacyEntityRenderers.register();
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(
                ru.metrocraft.port.gates.GateTile.class, new ru.metrocraft.port.client.gates.GateRenderer()); }
    @Override public void init() { ClientEvents.initialize(); }
}
