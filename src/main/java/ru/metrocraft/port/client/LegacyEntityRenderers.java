package ru.metrocraft.port.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import ru.metrocraft.port.entity.*;
import ru.metrocraft.port.client.mobs.*;

public final class LegacyEntityRenderers {
    public static void register() {
        RenderingRegistry.registerEntityRenderingHandler(MetroBullet.class,manager -> new net.minecraft.client.renderer.entity.Render<MetroBullet>(manager){
            @Override protected ResourceLocation getEntityTexture(MetroBullet entity){return null;}
        });
        add(KrisEntity.class,new Kris(),"kris"); add(MurzikEntity.class,new Murzik(),"murzik");
        add(TuzicEntity.class,new Tuzic(),"tuzic"); add(JeryEntity.class,new Jery(),"jery");
        add(Trup1Entity.class,new Trup1(),"trup1"); add(Trup2Entity.class,new Trup2(),"trup2");
        add(Trup3Entity.class,new Trup3(),"trup3"); add(Trup4Entity.class,new Trup4(),"trup4");
        add(KompEntity.class,new Komp(),"komp"); add(GribEntity.class,new ModelGrib(),"grib");
    }
    private static <T extends EntityLiving> void add(Class<T> entity, ModelBase model, String name) {
        ResourceLocation texture = new ResourceLocation("metroport:textures/entity/" + name + ".png");
        RenderingRegistry.registerEntityRenderingHandler(entity, manager -> new RenderLiving<T>(manager,model,0.5F) {
            @Override protected ResourceLocation getEntityTexture(T entity) { return texture; }
        });
    }
}
