package ru.metrocraft.port;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import ru.metrocraft.port.metromod.decor.RemovedDecorMappings;

/** Genuine new registry namespace; old saved stacks keep their full NBT through Forge remaps. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class NamespaceMigration {
    public static boolean legacy(String domain){return domain.equals("metroport")||domain.equals("metrocraftport");}
    public static boolean ours(String domain){return legacy(domain)||domain.equals(MetroPort.ID);}
    public static ResourceLocation current(ResourceLocation id){return legacy(id.getNamespace())?new ResourceLocation(MetroPort.ID,id.getPath()):id;}
    /** Balance keys and old data exports deliberately stay stable, unlike displayed registry IDs. */
    public static ResourceLocation balanceId(ResourceLocation id){return ours(id.getNamespace())?new ResourceLocation(MetroPort.LEGACY_ID,id.getPath()):id;}
    private static <T extends IForgeRegistryEntry<T>> void migrate(RegistryEvent.MissingMappings<T> event){
        for(RegistryEvent.MissingMappings.Mapping<T> mapping:event.getAllMappings()){
            if(!legacy(mapping.key.getNamespace())||RemovedDecorMappings.retired(mapping.key))continue;
            ResourceLocation target=current(mapping.key);
            if(mapping.registry.containsKey(target))mapping.remap(mapping.registry.getValue(target));
        }
    }
    @SubscribeEvent public static void items(RegistryEvent.MissingMappings<Item> event){migrate(event);}
    @SubscribeEvent public static void blocks(RegistryEvent.MissingMappings<Block> event){migrate(event);}
    @SubscribeEvent public static void entities(RegistryEvent.MissingMappings<EntityEntry> event){migrate(event);}
    @SubscribeEvent public static void sounds(RegistryEvent.MissingMappings<SoundEvent> event){migrate(event);}
    private NamespaceMigration(){}
}
