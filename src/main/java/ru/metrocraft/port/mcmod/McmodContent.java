package ru.metrocraft.port.mcmod;

import com.google.gson.Gson;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.metrocraft.port.MetroPort;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class McmodContent {
    public static McmodArmorEntry[] armor;
    public static void load() {
        try (Reader reader = new InputStreamReader(McmodContent.class.getResourceAsStream("/assets/metroport/mcmod_armor.json"), StandardCharsets.UTF_8)) {
            armor = new Gson().fromJson(reader, McmodArmorEntry[].class);
            McmodOrdinaryContent.load();
        } catch (Exception e) { throw new IllegalStateException("Cannot load MCMOD armor catalog", e); }
    }
    @SubscribeEvent public static void items(RegistryEvent.Register<Item> event) {
        if (armor == null) load();
        for (McmodArmorEntry entry : armor) {
            McmodArmorItem item = new McmodArmorItem(entry);
            item.setRegistryName(MetroPort.ID,entry.id).setTranslationKey(MetroPort.ID+"."+entry.id).setCreativeTab(MetroPort.ARMOR);
            event.getRegistry().register(item);
            if (MetroPort.ITEMS.put(entry.id, item) != null) throw new IllegalStateException("Duplicate MCMOD id: " + entry.id);
        }
        for(McmodOrdinaryContent.Entry entry:McmodOrdinaryContent.entries) {
            Item item=McmodOrdinaryContent.create(entry);
            item.setRegistryName(MetroPort.ID,entry.id).setTranslationKey(MetroPort.ID+"."+entry.id).setCreativeTab(MetroPort.THINGS);
            event.getRegistry().register(item);
            if(MetroPort.ITEMS.put(entry.id,item)!=null)throw new IllegalStateException("Duplicate MCMOD id: "+entry.id);
        }
    }
}
