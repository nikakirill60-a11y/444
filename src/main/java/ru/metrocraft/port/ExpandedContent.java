package ru.metrocraft.port;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = MetroPort.ID)
public final class ExpandedContent {
    public static final Map<String, Block> BLOCKS = new LinkedHashMap<>();
    @SubscribeEvent
    public static void blocks(RegistryEvent.Register<Block> event) {
        for (int i = 0; i < 58; i++) {
            Block block = new Block(Material.WOOD).setHardness(2).setTranslationKey("metroport.block" + i)
                    .setCreativeTab(MetroPort.THINGS).setRegistryName(MetroPort.ID, "block" + i);
            BLOCKS.put("block" + i, block);
            event.getRegistry().register(block);
        }
        Block radiation = new RadiationBlock().setRegistryName(MetroPort.ID, "radiation")
                .setTranslationKey("metroport.radiation").setCreativeTab(MetroPort.THINGS);
        BLOCKS.put("radiation", radiation);
        event.getRegistry().register(radiation);
    }
    @SubscribeEvent
    public static void items(RegistryEvent.Register<Item> event) {
        for (Map.Entry<String, Block> entry : BLOCKS.entrySet()) {
            add(event, entry.getKey(), new ItemBlock(entry.getValue()));
        }
        add(event, "filter", new FilterItem());
        add(event, "testpickaxe", new MetroPickaxe());
    }
    private static void add(RegistryEvent.Register<Item> event, String id, Item item) {
        item.setRegistryName(MetroPort.ID, id).setTranslationKey("metroport." + id).setCreativeTab(MetroPort.THINGS);
        MetroPort.ITEMS.put(id, item);
        event.getRegistry().register(item);
    }
    private static final class MetroPickaxe extends ItemPickaxe {
        private MetroPickaxe() { super(ToolMaterial.DIAMOND); }
    }
}
