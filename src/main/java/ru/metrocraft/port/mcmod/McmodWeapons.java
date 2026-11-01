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
        java.util.List<Definition> entries=new java.util.ArrayList<>();
        for(String catalog:new String[]{"mcmod_guns.json","mcmod_firearms.json"})
            try(Reader in=new InputStreamReader(McmodWeapons.class.getResourceAsStream("/assets/metroport/"+catalog),StandardCharsets.UTF_8)){
                java.util.Collections.addAll(entries,new Gson().fromJson(in,Definition[].class));
            }catch(Exception e){throw new IllegalStateException("Cannot load MCMOD firearms: "+catalog,e);}
        return entries.toArray(new Definition[0]);
    }
    @SubscribeEvent public static void items(RegistryEvent.Register<Item> event){
        for(Definition d:definitions()){
            Item item=d.kind.equals("gun")?new McmodAkItem(d):new ContentItem(d);
            item.setRegistryName(MetroPort.ID,d.id).setTranslationKey("metroport."+d.id).setCreativeTab(MetroPort.WEAPONS);
            event.getRegistry().register(item);MetroPort.ITEMS.put(d.id,item);
        }
    }
    @SubscribeEvent public static void sounds(RegistryEvent.Register<SoundEvent> event){
        java.util.Set<String> ids=new java.util.LinkedHashSet<>();
        ids.add("mcmod_medkit_use_heavy");
        for(Definition d:definitions())if(d.kind.equals("gun")){ids.add(d.shootSound);ids.add(d.reloadSound);}
        java.util.Collections.addAll(ids,"mcmod_craft","mcmod_open","mcmod_loot_metal","mcmod_tikhar_pump_1");
        for(String id:ids){
            ResourceLocation key=new ResourceLocation(MetroPort.ID,id);SoundEvent sound=new SoundEvent(key).setRegistryName(key);
            event.getRegistry().register(sound);MetroPort.SOUNDS.put(id,sound);
        }
    }
}
