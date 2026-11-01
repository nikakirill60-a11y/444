package ru.metrocraft.port.blackrazid.mutants;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import ru.metrocraft.port.MetroPort;

@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class BlackrazidMutants {
    private BlackrazidMutants(){}
    public static void register(){
        add(RatEntity.class,"blackrazid_rat",80,0x4c4033,0x8a7357);
        add(SpiderEntity.class,"blackrazid_spider",81,0x4c2824,0xba814d);
        add(BiblioEntity.class,"blackrazid_biblio",82,0x555044,0xada288);
        add(MutantSpawner.SpiderNest.class,"blackrazid_spawner_spider",83,0x202020,0xba814d);
        add(MutantSpawner.LibrarianNest.class,"blackrazid_spawner_biblio",84,0x202020,0xada288);
    }
    private static void add(Class<? extends Entity> type,String name,int id,int base,int spots){
        EntityRegistry.registerModEntity(new ResourceLocation(MetroPort.ID,name),type,name,id,MetroPort.INSTANCE,64,3,true,base,spots);
    }
    @SubscribeEvent public static void sounds(RegistryEvent.Register<SoundEvent> e){
        for(String name:new String[]{"rat","spider","biblio"})for(String type:new String[]{"alive","hit","death"}){
            String id="blackrazid_"+name+"_"+type;ResourceLocation key=new ResourceLocation(MetroPort.ID,id);
            SoundEvent sound=new SoundEvent(key).setRegistryName(key);e.getRegistry().register(sound);MetroPort.SOUNDS.put(id,sound);
        }
    }
}
