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
    private static final KeyBinding INVENTORY = new KeyBinding("key.metroport.inventory", Keyboard.KEY_I, "key.categories.metroport");

    private static Boolean editorCreative;
    @SubscribeEvent public static void editorMode(net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent event){
        if(event.phase!=net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END)return;
        Minecraft mc=Minecraft.getMinecraft();
        if(mc.player==null){editorCreative=null;return;}
        boolean creative=mc.player.capabilities.isCreativeMode;
        if(editorCreative!=null && editorCreative!=creative)mc.renderGlobal.loadRenderers();
        editorCreative=creative;
    }

    @SubscribeEvent
    public static void models(ModelRegistryEvent event) {
        for (Item item : MetroPort.ITEMS.values()) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }

    public static void initialize() {
        ClientRegistry.registerKeyBinding(INVENTORY);
        for (net.minecraft.client.renderer.entity.RenderPlayer playerRenderer : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()) {
            playerRenderer.addLayer(new ru.metrocraft.port.client.BackpackClient(playerRenderer));
            playerRenderer.addLayer(new ru.metrocraft.port.client.FactionPatchLayer(playerRenderer));
            playerRenderer.addLayer(new ru.metrocraft.port.client.NvgLayer(playerRenderer));
            playerRenderer.addLayer(new ru.metrocraft.port.client.WatchClient(playerRenderer));
        }
    }

    // Retired model hook
    public static void bake(net.minecraftforge.client.event.ModelBakeEvent event) {
        for (Definition d : MetroPort.definitions) {
            if (!d.kind.equals("gun") && !d.kind.equals("attachment")) continue;
            Item item = MetroPort.ITEMS.get(d.id);
            ModelResourceLocation location = new ModelResourceLocation(item.getRegistryName(), "inventory");
            net.minecraft.client.renderer.block.model.IBakedModel icon = event.getModelRegistry().getObject(location);
            if (icon != null) {
                item.setTileEntityItemStackRenderer(ru.metrocraft.port.client.LegacyItemRenderer.INSTANCE);
                event.getModelRegistry().putObject(location, new ru.metrocraft.port.client.LegacyItemModel(d.id, d.kind.equals("gun"), icon));
            }
        }
    }

    // Retired model hook
    public static void nativeWeaponModels(net.minecraftforge.client.event.ModelBakeEvent event){
        for(Item item:MetroPort.ITEMS.values()){
            net.minecraft.item.ItemStack stack=new net.minecraft.item.ItemStack(item);
            if(!ru.metrocraft.port.blackrazid.weapons.NativeWeaponVisuals.supported(stack))continue;
            ModelResourceLocation location=new ModelResourceLocation(item.getRegistryName(),"inventory");
            net.minecraft.client.renderer.block.model.IBakedModel icon=event.getModelRegistry().getObject(location);
            if(icon!=null){item.setTileEntityItemStackRenderer(ru.metrocraft.port.client.blackrazid.weapons.NativeWeaponRenderer.INSTANCE);
                boolean gun=item instanceof ru.metrocraft.port.blackrazid.weapons.BlackrazidGunItem;
                event.getModelRegistry().putObject(location,new ru.metrocraft.port.client.LegacyItemModel(item.getRegistryName().getPath(),gun,icon));
            }
        }
    }

    // Retired model hook
    public static void mcmodAkModel(net.minecraftforge.client.event.ModelBakeEvent event) {
        for(Definition d:ru.metrocraft.port.mcmod.McmodWeapons.definitions()){
            if(!d.kind.equals("gun"))continue;
            Item item=MetroPort.ITEMS.get(d.id);if(item==null)continue;
            ModelResourceLocation location=new ModelResourceLocation(item.getRegistryName(),"inventory");
            net.minecraft.client.renderer.block.model.IBakedModel icon=event.getModelRegistry().getObject(location);
            if(icon!=null){
                item.setTileEntityItemStackRenderer(ru.metrocraft.port.client.McmodAkRenderer.INSTANCE);
                event.getModelRegistry().putObject(location,new ru.metrocraft.port.client.LegacyItemModel(ru.metrocraft.port.client.McmodAkRenderer.profile(d.id),true,icon));
            }
        }
    }

    @SubscribeEvent public static void survivalModels(net.minecraftforge.client.event.ModelBakeEvent event){
        for(String id:new String[]{"blackrazid_lamp","blackrazid_power","blackrazid_flyashka","blackrazid_watch"}){
            Item item=MetroPort.ITEMS.get(id);if(item==null)continue;
            ModelResourceLocation key=new ModelResourceLocation(item.getRegistryName(),"inventory");
            net.minecraft.client.renderer.block.model.IBakedModel icon=event.getModelRegistry().getObject(key);
            if(icon!=null){item.setTileEntityItemStackRenderer(id.equals("blackrazid_watch")?ru.metrocraft.port.client.BlackrazidWatchRenderer.INSTANCE:ru.metrocraft.port.client.BlackrazidGadgetRenderer.INSTANCE);
                event.getModelRegistry().putObject(key,new ru.metrocraft.port.client.LegacyItemModel(id,false,icon));}
        }
    }
    @SubscribeEvent public static void watchModel(net.minecraftforge.client.event.ModelBakeEvent event) {
        Item item=MetroPort.ITEMS.get("mcmod_watch");if(item==null)return;
        ModelResourceLocation key=new ModelResourceLocation(item.getRegistryName(),"inventory");
        net.minecraft.client.renderer.block.model.IBakedModel icon=event.getModelRegistry().getObject(key);
        if(icon!=null){item.setTileEntityItemStackRenderer(ru.metrocraft.port.client.WatchRenderer.INSTANCE);event.getModelRegistry().putObject(key,new ru.metrocraft.port.client.LegacyItemModel("mcmod_watch",false,icon,false));}
    }

    @SubscribeEvent
    public static void toolModels(net.minecraftforge.client.event.ModelBakeEvent event) {
        for (String id : new String[]{"mcmod_machete", "mcmod_aptech"}) {
            Item item=MetroPort.ITEMS.get(id);
            if(item==null)continue;
            ModelResourceLocation location=new ModelResourceLocation(item.getRegistryName(),"inventory");
            net.minecraft.client.renderer.block.model.IBakedModel icon=event.getModelRegistry().getObject(location);
            if(icon!=null) {
                item.setTileEntityItemStackRenderer(ru.metrocraft.port.client.McmodToolRenderer.INSTANCE);
                event.getModelRegistry().putObject(location,new ru.metrocraft.port.client.LegacyItemModel(id,false,icon));
            }
        }
    }

    // Retired model hook
    public static void mcmodDecorModels(net.minecraftforge.client.event.ModelBakeEvent event) {
        for(ru.metrocraft.port.mcmod.decor.McmodDecorBlock block:ru.metrocraft.port.mcmod.decor.McmodDecorContent.BLOCKS.values()) {
            Item item=Item.getItemFromBlock(block);ModelResourceLocation location=new ModelResourceLocation(item.getRegistryName(),"inventory");
            net.minecraft.client.renderer.block.model.IBakedModel icon=event.getModelRegistry().getObject(location);
            if(icon!=null) {
                item.setTileEntityItemStackRenderer(ru.metrocraft.port.client.McmodDecorItemRenderer.INSTANCE);
                event.getModelRegistry().putObject(location,new ru.metrocraft.port.client.LegacyItemModel(block.entry.id,false,icon,false));
            }
        }
    }

    @SubscribeEvent
    public static void gateModels(net.minecraftforge.client.event.ModelBakeEvent event) {
        for (ru.metrocraft.port.gates.GateBlock block : ru.metrocraft.port.gates.Gates.BLOCKS.values()) {
            Item item = Item.getItemFromBlock(block);
            ModelResourceLocation location = new ModelResourceLocation(item.getRegistryName(), "inventory");
            net.minecraft.client.renderer.block.model.IBakedModel icon = event.getModelRegistry().getObject(location);
            if (icon != null) {
                item.setTileEntityItemStackRenderer(ru.metrocraft.port.client.gates.GateItemRenderer.INSTANCE);
                event.getModelRegistry().putObject(location,
                        new ru.metrocraft.port.client.LegacyItemModel(block.type.id, false, icon, false));
            }
        }
    }

    @SubscribeEvent
    public static void patchTooltip(net.minecraftforge.event.entity.player.ItemTooltipEvent event) {
        net.minecraft.item.ItemStack patch=ru.metrocraft.port.patches.FactionPatches.get(event.getItemStack());
        if(!patch.isEmpty())event.getToolTip().add(net.minecraft.client.resources.I18n.format("metroport.patch.equipped",patch.getDisplayName()));
    }

    @SubscribeEvent
    public static void inventory(net.minecraftforge.client.event.GuiOpenEvent event) {
        Minecraft mc=Minecraft.getMinecraft();
        if(event.getGui() instanceof net.minecraft.client.gui.inventory.GuiInventory && mc.player!=null && !mc.player.capabilities.isCreativeMode) {
            event.setCanceled(true);
            MetroPort.NETWORK.sendToServer(new ru.metrocraft.port.inventory.OpenInventoryPacket());
        }
    }

    // Do not register R/M/V/Alt handlers or change external firearms' FOV.
    public static boolean pumping(){return false;}
    public static boolean aiming(){return false;}
    @SubscribeEvent public static void key(InputEvent.KeyInputEvent event){
        Minecraft mc=Minecraft.getMinecraft();
        if(INVENTORY.isPressed() && mc.player!=null && mc.currentScreen==null)
            MetroPort.NETWORK.sendToServer(new ru.metrocraft.port.inventory.OpenInventoryPacket());
    }
}
