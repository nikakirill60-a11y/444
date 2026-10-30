package ru.metrocraft.port;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

/** This entire class is client-only: a dedicated server never loads Minecraft or LWJGL. */
@Mod.EventBusSubscriber(modid = MetroPort.ID, value = Side.CLIENT)
public final class ClientEvents {
    private static final KeyBinding RELOAD = new KeyBinding("key.metroport.reload", Keyboard.KEY_R, "key.categories.metroport");

    @SubscribeEvent
    public static void models(ModelRegistryEvent event) {
        for (Item item : MetroPort.ITEMS.values()) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
        ClientRegistry.registerKeyBinding(RELOAD);
    }

    @SubscribeEvent
    public static void key(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (RELOAD.isPressed() && mc.player != null && mc.currentScreen == null
                && mc.player.getHeldItemMainhand().getItem() instanceof GunItem) {
            MetroPort.NETWORK.sendToServer(new ReloadPacket());
        }
    }
}
