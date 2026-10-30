package ru.metrocraft.port.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import ru.metrocraft.port.MetroPort;

public final class LegacyEntities {
    public static void register() {
        add(KrisEntity.class,"kris",0); add(MurzikEntity.class,"murzik",1);
        add(TuzicEntity.class,"tuzic",2); add(JeryEntity.class,"jery",3);
        add(Trup1Entity.class,"trup1",4); add(Trup2Entity.class,"trup2",5);
        add(Trup3Entity.class,"trup3",6); add(Trup4Entity.class,"trup4",7);
        add(KompEntity.class,"komp",8); add(GribEntity.class,"grib",9);
    }
    private static void add(Class<? extends Entity> type, String name, int id) {
        EntityRegistry.registerModEntity(new ResourceLocation(MetroPort.ID, name), type, name, id,
                MetroPort.INSTANCE, 64, 1, true, 5597999, 139);
    }
}
