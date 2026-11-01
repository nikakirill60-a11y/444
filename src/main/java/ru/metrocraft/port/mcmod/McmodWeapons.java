package ru.metrocraft.port.mcmod;
import com.google.gson.Gson;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.metrocraft.port.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class McmodWeapons {
    private McmodWeapons(){}
    public static Definition[] definitions(){
        try(Reader in=new InputStreamReader(McmodWeapons.class.getResourceAsStream("/assets/metroport/mcmod_guns.json"),StandardCharsets.UTF_8)){
            return new Gson().fromJson(in,Definition[].class);
        }catch(Exception e){throw new IllegalStateException("Cannot load MCMOD AK-74",e);}
    }
    @SubscribeEvent public static void items(RegistryEvent.Register<Item> event){
        for(Definition d:definitions()){
            Item item=d.kind.equals("gun")?new McmodAkItem(d):new ContentItem(d);
            item.setRegistryName(MetroPort.ID,d.id).setTranslationKey("metroport."+d.id).setCreativeTab(MetroPort.WEAPONS);
            event.getRegistry().register(item);MetroPort.ITEMS.put(d.id,item);
        }
    }
    @SubscribeEvent public static void sounds(RegistryEvent.Register<SoundEvent> event){
        for(String id:new String[]{"mcmod_ak_boom","mcmod_ak74_reload"}){
            ResourceLocation key=new ResourceLocation(MetroPort.ID,id);SoundEvent sound=new SoundEvent(key).setRegistryName(key);
            event.getRegistry().register(sound);MetroPort.SOUNDS.put(id,sound);
        }
    }
}
