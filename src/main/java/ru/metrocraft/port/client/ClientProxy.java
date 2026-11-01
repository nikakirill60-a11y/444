package ru.metrocraft.port.client;

import ru.metrocraft.port.CommonProxy;
import ru.metrocraft.port.ClientEvents;

public final class ClientProxy extends CommonProxy {
    @Override public void preInit() { LegacyEntityRenderers.register();
        net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(ru.metrocraft.port.blackrazid.grenades.GrenadeEntity.class,manager->new net.minecraft.client.renderer.entity.RenderSnowball<>(manager,ru.metrocraft.port.MetroPort.ITEMS.get("blackrazid_grenade"),net.minecraft.client.Minecraft.getMinecraft().getRenderItem()));
        ExodusClient.registerEntities();
        ru.metrocraft.port.client.blackrazid.mutants.MutantRenderers.register();
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(
                ru.metrocraft.port.gates.GateTile.class, new ru.metrocraft.port.client.gates.GateRenderer()); }
    @Override public void shotFeedback(String gun,float pitch,float yaw) {
        net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(()->GunClient.shot(gun,pitch,yaw));
    }
    @Override public void survival(int dimension,int entity,float water,float energy,boolean lamp){BlackrazidSurvivalClient.receive(dimension,entity,water,energy,lamp);}
    @Override public void init() { ClientEvents.initialize();BlackrazidSurvivalClient.initialize();
        ((net.minecraft.client.resources.IReloadableResourceManager)net.minecraft.client.Minecraft.getMinecraft().getResourceManager()).registerReloadListener(manager->{BlackrazidGadgetRenderer.reload();ru.metrocraft.port.client.blackrazid.mutants.MutantObjPart.reload();}); }
}
