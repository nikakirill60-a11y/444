/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.simpleimpl.MessageContext
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraftforge.common.MinecraftForge
 */
package metrocraft.directs;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import metrocraft.handler.directs.EntityPlayerHandler;
import metrocraft.handler.directs.PlayerUpdateEventHandler;
import metrocraft.main.MetroItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxyP {
    public void preInit() {
    }

    public void Init() {
        MetroItems.registerItems();
        MinecraftForge.EVENT_BUS.register((Object)new EntityPlayerHandler());
        MinecraftForge.EVENT_BUS.register((Object)new PlayerUpdateEventHandler());
    }

    public void postInit() {
    }

    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().field_147369_b;
    }
}

