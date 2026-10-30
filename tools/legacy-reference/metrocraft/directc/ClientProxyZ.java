/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.simpleimpl.MessageContext
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraftforge.common.MinecraftForge
 */
package metrocraft.directc;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import metrocraft.directs.CommonProxyP;
import metrocraft.handlers.directc.EntityPlayerHandler;
import metrocraft.handlers.directc.PlayerRenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxyZ
extends CommonProxyP {
    @Override
    public void preInit() {
        super.preInit();
    }

    @Override
    public void Init() {
        super.Init();
        MinecraftForge.EVENT_BUS.register((Object)new PlayerRenderHandler());
        MinecraftForge.EVENT_BUS.register((Object)new EntityPlayerHandler());
    }

    @Override
    public void postInit() {
        super.postInit();
    }

    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.side.isClient() ? Minecraft.func_71410_x().field_71439_g : super.getPlayerEntity(ctx);
    }
}

