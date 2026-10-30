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

@Mod(modid = MetroPort.ID, name = "MetroCraft Port", version = "1.0.0", acceptedMinecraftVersions = "[1.12.2]")
@Mod.EventBusSubscriber(modid = MetroPort.ID)
public final class MetroPort {
    public static final String ID = "metroport";
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
            public ItemStack getTabIconItem() {
                Item item = ITEMS.get(icon);
                return new ItemStack(item == null ? fallback : item);
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
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (Definition d : definitions) {
            Item item;
            switch (d.kind) {
                case "gun": item = new GunItem(d); break;
                case "armor": item = new MetroArmor(d); break;
                case "tool": item = new ToolItem(d); break;
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
