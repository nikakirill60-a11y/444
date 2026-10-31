package ru.metrocraft.port;

import com.google.gson.Gson;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Mod(modid = MetroPort.ID, name = "MetroCraft Port", version = "1.3.0", acceptedMinecraftVersions = "[1.12.2]")
@Mod.EventBusSubscriber(modid = MetroPort.ID)
public final class MetroPort {
    public static final String ID = "metroport";
    @Mod.Instance(ID)
    public static MetroPort INSTANCE;
    @net.minecraftforge.fml.common.SidedProxy(clientSide="ru.metrocraft.port.client.ClientProxy", serverSide="ru.metrocraft.port.CommonProxy")
    public static CommonProxy PROXY;
    public static final Map<String, Item> ITEMS = new LinkedHashMap<>();
    public static final Map<String, SoundEvent> SOUNDS = new LinkedHashMap<>();
    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(ID);
    public static Definition[] definitions;

    public static final CreativeTabs WEAPONS = tab("weapons", "ak74", Items.BOW);
    public static final CreativeTabs ARMOR = tab("armor", "armor", Items.IRON_CHESTPLATE);
    public static final CreativeTabs THINGS = tab("things", "medkit", Items.PAPER);

    private static CreativeTabs tab(String name, String icon, Item fallback) {
        return new CreativeTabs(ID + "." + name) {
            @Override
            public ItemStack createIcon() {
                Item item = ITEMS.get(icon);
                return new ItemStack(item == null ? fallback : item);
            }
            @Override
            @net.minecraftforge.fml.relauncher.SideOnly(Side.CLIENT)
            public void displayAllRelevantItems(net.minecraft.util.NonNullList<ItemStack> contents) {
                super.displayAllRelevantItems(contents);
                if (!name.equals("things")) return;
                for (ResourceLocation entity : net.minecraft.entity.EntityList.ENTITY_EGGS.keySet()) {
                    if (!entity.getNamespace().equals(ID)) continue;
                    ItemStack egg = new ItemStack(Items.SPAWN_EGG);
                    net.minecraft.item.ItemMonsterPlacer.applyEntityIdToItemStack(egg, entity);
                    contents.add(egg);
                }
            }
        };
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        try (InputStream stream = MetroPort.class.getResourceAsStream("/assets/metroport/content.json")) {
            if (stream == null) throw new IllegalStateException("Missing content.json");
            definitions = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Definition[].class);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot load MetroCraft content", e);
        }
        NETWORK.registerMessage(ReloadPacket.Handler.class, ReloadPacket.class, 0, Side.SERVER);
        NETWORK.registerMessage(ru.metrocraft.port.inventory.BackpackSyncPacket.Handler.class,
                ru.metrocraft.port.inventory.BackpackSyncPacket.class,1,Side.CLIENT);
        NETWORK.registerMessage(ru.metrocraft.port.inventory.OpenInventoryPacket.Handler.class,
                ru.metrocraft.port.inventory.OpenInventoryPacket.class,2,Side.SERVER);
        NETWORK.registerMessage(ShotFeedbackPacket.Handler.class,ShotFeedbackPacket.class,3,Side.CLIENT);
        NetworkRegistry.INSTANCE.registerGuiHandler(this,new ru.metrocraft.port.inventory.InventoryGuiHandler());
        ru.metrocraft.port.entity.LegacyEntities.register();
        ru.metrocraft.port.gates.Gates.initialize();
        PROXY.preInit();
    }

    @Mod.EventHandler
    public void serverStarting(net.minecraftforge.fml.common.event.FMLServerStartingEvent event) {
        event.registerServerCommand(new ru.metrocraft.port.mechanics.GiveNoDrop());
    }
    @Mod.EventHandler
    public void init(net.minecraftforge.fml.common.event.FMLInitializationEvent event) { PROXY.init(); }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (Definition d : definitions) {
            Item item;
            switch (d.kind) {
                case "gun": item = new GunItem(d); break;
                case "armor": item = new MetroArmor(d); break;
                case "tool": item = new ToolItem(d); break;
                case "attachment": item = new AttachmentItem(d); break;
                default: item = new ContentItem(d);
            }
            item.setRegistryName(ID, d.id);
            item.setTranslationKey(ID + "." + d.id);
            item.setCreativeTab(d.kind.equals("gun") ? WEAPONS : d.kind.equals("armor") ? ARMOR : THINGS);
            ITEMS.put(d.id, item);
            event.getRegistry().register(item);
        }
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        net.minecraft.util.ResourceLocation gateSound = new net.minecraft.util.ResourceLocation(ID, "germo");
        SoundEvent gateEvent = new SoundEvent(gateSound).setRegistryName(gateSound);
        SOUNDS.put("germo", gateEvent); event.getRegistry().register(gateEvent);
        for (Definition d : definitions) {
            for (String id : new String[]{d.shootSound, d.reloadSound}) {
                if (id == null || SOUNDS.containsKey(id)) continue;
                ResourceLocation location = new ResourceLocation(ID, id);
                SoundEvent sound = new SoundEvent(location).setRegistryName(location);
                SOUNDS.put(id, sound);
                event.getRegistry().register(sound);
            }
        }
    }
}
