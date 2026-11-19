package ru.metrocraft.port.metromod.decor;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.metrocraft.port.MetroPort;

/** Removed objects become air, not invisible colliders or missing-model cubes in old worlds. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class RemovedDecorMappings {
    public static boolean retired(ResourceLocation key){
        if(!ru.metrocraft.port.NamespaceMigration.ours(key.getNamespace()))return false;
        String id=key.getPath();
        return id.equals("germo") || id.startsWith("germo_") || id.equals("gate_part")
                || id.startsWith("metromod_germo_") || id.startsWith("metromod_door_") || id.startsWith("metromod_light_");
    }
    @SubscribeEvent public static void blocks(RegistryEvent.MissingMappings<Block> event){
        for(RegistryEvent.MissingMappings.Mapping<Block> mapping:event.getAllMappings())
            if(retired(mapping.key))mapping.remap(Blocks.AIR);
    }
    @SubscribeEvent public static void items(RegistryEvent.MissingMappings<Item> event){
        for(RegistryEvent.MissingMappings.Mapping<Item> mapping:event.getAllMappings())
            if(retired(mapping.key))mapping.ignore();
    }
    private RemovedDecorMappings(){}
}
