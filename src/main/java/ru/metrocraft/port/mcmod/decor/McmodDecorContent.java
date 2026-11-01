package ru.metrocraft.port.mcmod.decor;

import com.google.gson.Gson;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ru.metrocraft.port.MetroPort;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

// Archived feature: intentionally not registered in the reduced build.
public final class McmodDecorContent {
    public static final class Entry {
        public String id,sourceId,nameRu,nameEn,kind;
        public float scale;
        public double[] bounds;
    }
    public static Entry[] entries;
    public static final Map<String,McmodDecorBlock> BLOCKS=new LinkedHashMap<>();
    public static void load() {
        try(Reader in=new InputStreamReader(McmodDecorContent.class.getResourceAsStream("/assets/metroport/mcmod_decor.json"),StandardCharsets.UTF_8)) {
            entries=new Gson().fromJson(in,Entry[].class);
        } catch(Exception ex) {throw new IllegalStateException("Cannot read MCMOD decoration catalogue",ex);}
    }
    @SubscribeEvent public static void blocks(RegistryEvent.Register<Block> event) {
        if(entries==null)load();
        GameRegistry.registerTileEntity(McmodDecorTile.class,new ResourceLocation(MetroPort.ID,"mcmod_decor"));
        for(Entry entry:entries) {
            McmodDecorBlock block=new McmodDecorBlock(entry);
            block.setRegistryName(MetroPort.ID,entry.id).setTranslationKey("metroport."+entry.id).setCreativeTab(MetroPort.DECOR);
            BLOCKS.put(entry.id,block);event.getRegistry().register(block);
        }
    }
    @SubscribeEvent public static void items(RegistryEvent.Register<Item> event) {
        for(McmodDecorBlock block:BLOCKS.values()) {
            Item item=new ItemBlock(block).setRegistryName(block.getRegistryName()).setTranslationKey("metroport."+block.entry.id).setCreativeTab(MetroPort.DECOR);
            event.getRegistry().register(item);MetroPort.ITEMS.put(block.entry.id,item);
        }
    }
}
