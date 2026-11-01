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
        add(KikimoraEntity.class,"blackrazid_kikimora",85,0x4c4033,0x8a7357);
        add(StrazhEntity.class,"blackrazid_strazh",86,0x4c4033,0x8a7357);
        add(NosachEntity.class,"blackrazid_nosach",87,0x4c4033,0x8a7357);
        add(DarkEntity.class,"blackrazid_dark",88,0x4c4033,0x8a7357);
        add(BearEntity.class,"blackrazid_bear",89,0x4c4033,0x8a7357);
        add(MobusEntity.class,"blackrazid_mobus",90,0x4c4033,0x8a7357);
        add(AquaFemaleEntity.class,"blackrazid_aqua_female",91,0x4c4033,0x8a7357);
        add(AquaMaleEntity.class,"blackrazid_aqua_male",92,0x4c4033,0x8a7357);
        add(MutantSpawner.KikimoraNest.class,"blackrazid_spawner_kikimora",93,0x202020,0x8a7357);
        add(MutantSpawner.StrazhNest.class,"blackrazid_spawner_strazh",94,0x202020,0x8a7357);
        add(MutantSpawner.NosachNest.class,"blackrazid_spawner_nosach",95,0x202020,0x8a7357);
        add(MutantSpawner.DarkNest.class,"blackrazid_spawner_dark",96,0x202020,0x8a7357);
        add(MutantSpawner.BearNest.class,"blackrazid_spawner_bear",97,0x202020,0x8a7357);
        add(MutantSpawner.MobusNest.class,"blackrazid_spawner_mobus",98,0x202020,0x8a7357);
        add(MutantSpawner.AquaFemaleNest.class,"blackrazid_spawner_aqua_female",99,0x202020,0x8a7357);
        add(MutantSpawner.AquaMaleNest.class,"blackrazid_spawner_aqua_male",100,0x202020,0x8a7357);
    }
    private static void add(Class<? extends Entity> type,String name,int id,int base,int spots){
        EntityRegistry.registerModEntity(new ResourceLocation(MetroPort.ID,name),type,name,id,MetroPort.INSTANCE,64,3,true,base,spots);
    }
    @SubscribeEvent public static void sounds(RegistryEvent.Register<SoundEvent> e){
        for(String name:new String[]{"rat","spider","biblio","kikimora","strazh","nosach","dark","bear","aquaf","aquam"})for(String type:new String[]{"alive","hit","death"}){
            // Deliberately silent where the supplied archive contains no recording.
            if((name.equals("bear") && type.equals("hit")) || (name.equals("dark") && !type.equals("alive")))continue;
            String id="blackrazid_"+name+"_"+type;ResourceLocation key=new ResourceLocation(MetroPort.ID,id);
            SoundEvent sound=new SoundEvent(key).setRegistryName(key);e.getRegistry().register(sound);MetroPort.SOUNDS.put(id,sound);
        }
    }
}
