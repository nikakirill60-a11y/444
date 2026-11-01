package ru.metrocraft.port.blackrazid;

import com.google.gson.Gson;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.metrocraft.port.MetroPort;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class BlackrazidContent {
    public static final class Entry {
        public String id,sourceId,model,mode,texture,icon,nameRu,nameEn;
        public int condition,sourceArmorPoints,storageSlots;
        public float defence;
        public boolean gasMask;
    }
    public static Entry[] entries;
    public static void load() {
        try(Reader r=new InputStreamReader(BlackrazidContent.class.getResourceAsStream("/assets/metroport/blackrazid_equipment.json"),StandardCharsets.UTF_8)) {
            entries=new Gson().fromJson(r,Entry[].class);
        }catch(Exception e){throw new IllegalStateException("Cannot load BlackRazid equipment",e);}
    }
    @SubscribeEvent public static void items(RegistryEvent.Register<Item> event) {
        if(entries==null)load();
        for(Entry e:entries){
            Item item="backpack".equals(e.mode)?new BlackrazidBackpackItem(e):new BlackrazidArmorItem(e);
            item.setRegistryName(MetroPort.ID,e.id).setTranslationKey(MetroPort.ID+"."+e.id).setCreativeTab(MetroPort.ARMOR);
            event.getRegistry().register(item);
            if(MetroPort.ITEMS.put(e.id,item)!=null)throw new IllegalStateException("Duplicate BlackRazid id: "+e.id);
        }
    }
}
