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
    private static final KeyBinding AIM = new KeyBinding("key.metroport.aim", Keyboard.KEY_LMENU, "key.categories.metroport");
    private static final KeyBinding RELOAD = new KeyBinding("key.metroport.reload", Keyboard.KEY_R, "key.categories.metroport");

    @SubscribeEvent
    public static void models(ModelRegistryEvent event) {
        for (Item item : MetroPort.ITEMS.values()) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }

    public static void initialize() {
        ClientRegistry.registerKeyBinding(RELOAD);
        ClientRegistry.registerKeyBinding(AIM);
        for (net.minecraft.client.renderer.entity.RenderPlayer playerRenderer : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()) {
            playerRenderer.addLayer(new ru.metrocraft.port.client.BackpackClient(playerRenderer));
        }
    }

    @SubscribeEvent
    public static void bake(net.minecraftforge.client.event.ModelBakeEvent event) {
        for (Definition d : MetroPort.definitions) {
            if (!d.kind.equals("gun") && !d.kind.equals("attachment")) continue;
            Item item = MetroPort.ITEMS.get(d.id);
            ModelResourceLocation location = new ModelResourceLocation(item.getRegistryName(), "inventory");
            net.minecraft.client.renderer.block.model.IBakedModel icon = event.getModelRegistry().getObject(location);
            if (icon != null) {
                item.setTileEntityItemStackRenderer(ru.metrocraft.port.client.LegacyItemRenderer.INSTANCE);
                event.getModelRegistry().putObject(location, new ru.metrocraft.port.client.LegacyItemModel(icon));
            }
        }
    }

    @SubscribeEvent
    public static void inventory(net.minecraftforge.client.event.GuiOpenEvent event) {
        Minecraft mc=Minecraft.getMinecraft();
        if(event.getGui() instanceof net.minecraft.client.gui.inventory.GuiInventory && mc.player!=null && !mc.player.capabilities.isCreativeMode) {
            event.setCanceled(true);
            MetroPort.NETWORK.sendToServer(new ru.metrocraft.port.inventory.OpenInventoryPacket());
        }
    }

    @SubscribeEvent
    public static void fov(net.minecraftforge.client.event.FOVUpdateEvent event) {
        net.minecraft.item.ItemStack held=event.getEntity().getHeldItemMainhand();
        if(AIM.isKeyDown() && held.getItem() instanceof GunItem) {
            float zoom=Math.max(1.2F,Math.min(8,AttachmentItem.multiplier(held,"zoom")));
            event.setNewfov(event.getNewfov()/zoom);
        }
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
