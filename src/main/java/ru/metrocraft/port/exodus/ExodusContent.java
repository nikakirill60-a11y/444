package ru.metrocraft.port.exodus;

import com.google.gson.Gson;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import ru.metrocraft.port.MetroPort;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/** Additive registrations: never substitute Minecraft ores, pumpkins or storage blocks. */
@Mod.EventBusSubscriber(modid = MetroPort.ID)
public final class ExodusContent {
    public static final class Entry {
        public String id, kind, source, model, ru, en;
        public double[] bounds;
        public int elements;
    }
    public static Entry[] entries;
    public static final Map<String, Block> BLOCKS = new LinkedHashMap<>();
    public static void load() {
        try (InputStream stream = ExodusContent.class.getResourceAsStream("/assets/metroport/exodus.json")) {
            if (stream == null) throw new IllegalStateException("Missing Exodus catalogue");
            entries = java.util.Arrays.stream(new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Entry[].class)).filter(e -> ru.metrocraft.port.ContentPolicy.exodus(e.id,e.kind)).toArray(Entry[]::new);
        } catch (Exception e) { throw new IllegalStateException("Cannot load Exodus content", e); }
    }
    public static void registerEntities() {
        EntityRegistry.registerModEntity(new ResourceLocation(MetroPort.ID, "exodus_shrimp"), ExodusMutant.Shrimp.class,
                "exodus_shrimp", 11, MetroPort.INSTANCE, 64, 3, true, 0x475f52, 0x8e8070);
        EntityRegistry.registerModEntity(new ResourceLocation(MetroPort.ID, "exodus_guard"), ExodusMutant.Guard.class,
                "exodus_guard", 12, MetroPort.INSTANCE, 64, 3, true, 0x655f4c, 0xa0a086);
        // No natural-spawn rules: existing worlds must not unexpectedly fill with new mobs.
    }
    @SubscribeEvent public static void blocks(RegistryEvent.Register<Block> event) {
        for (Entry entry : entries) if (entry.kind.equals("decor")) {
            Block block = new ExodusDecorBlock(entry).setRegistryName(MetroPort.ID, entry.id)
                    .setTranslationKey("metroport." + entry.id).setCreativeTab(MetroPort.DECOR);
            BLOCKS.put(entry.id, block);
            event.getRegistry().register(block);
        }
    }
    @SubscribeEvent public static void items(RegistryEvent.Register<Item> event) {
        for (Entry entry : entries) {
            Item item;
            if (entry.kind.equals("decor")) {
                item = new ItemBlock(BLOCKS.get(entry.id)).setCreativeTab(MetroPort.DECOR);
            } else if (entry.kind.equals("mask")) {
                item = new ExodusMask(entry.id).setCreativeTab(MetroPort.ARMOR);
            } else continue; // Mutants use the normal, NBT-tagged spawn eggs, not duplicate item IDs.
            item.setRegistryName(MetroPort.ID, entry.id).setTranslationKey("metroport." + entry.id);
            MetroPort.ITEMS.put(entry.id, item);
            event.getRegistry().register(item);
        }
    }
}
