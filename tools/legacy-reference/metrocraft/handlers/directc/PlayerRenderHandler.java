/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraftforge.client.event.RenderPlayerEvent$Specials$Post
 */
package metrocraft.handlers.directc;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import metrocraft.entities.clientplayer.ExtendedPlayer;
import metrocraft.renderitems.RenderBackpacks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class PlayerRenderHandler {
    @SubscribeEvent
    public void onPlayerRenderTick(RenderPlayerEvent.Specials.Post event) {
        if (event.entityPlayer == null) {
            return;
        }
        EntityPlayer player = (EntityPlayer)event.entityLiving;
        if (ExtendedPlayer.get((EntityPlayer)player).inventory.func_70301_a(0) != null) {
            new RenderBackpacks().render(player, event.renderer);
        }
    }
}

