/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.FMLClientHandler
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.client.entity.EntityClientPlayerMP
 *  net.minecraft.client.gui.inventory.GuiInventory
 *  net.minecraftforge.client.event.GuiOpenEvent
 */
package metrocraft.handlers.directc;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import metrocraft.main.MetroMain;
import metrocraft.server.PacketDispatcher;
import metrocraft.server.serverreg.OpenGuiMessage;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiOpenEvent;

public class EntityPlayerHandler {
    @SubscribeEvent
    public void onOpenGui(GuiOpenEvent event) {
        if (event.gui instanceof GuiInventory) {
            EntityClientPlayerMP player = FMLClientHandler.instance().getClient().field_71439_g;
            if (!player.field_71075_bZ.field_75098_d) {
                event.setCanceled(true);
                PacketDispatcher.sendToServer(new OpenGuiMessage(MetroMain.GUI_CUSTOM_INV));
            } else {
                event.setCanceled(false);
            }
        }
    }
}

