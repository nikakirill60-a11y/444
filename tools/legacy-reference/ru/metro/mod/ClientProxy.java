/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.registry.RenderingRegistry
 *  net.minecraft.client.renderer.entity.Render
 */
package ru.metro.mod;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.renderer.entity.Render;
import ru.metro.mod.CommonProxy;
import ru.metro.mod.GribEntity;
import ru.metro.mod.JeryEntity;
import ru.metro.mod.KompEntity;
import ru.metro.mod.KrisEntity;
import ru.metro.mod.MurzikEntity;
import ru.metro.mod.RenderGribEntity;
import ru.metro.mod.RenderJeryEntity;
import ru.metro.mod.RenderKompEntity;
import ru.metro.mod.RenderKrisEntity;
import ru.metro.mod.RenderMurzikEntity;
import ru.metro.mod.RenderTrup1Entity;
import ru.metro.mod.RenderTrup2Entity;
import ru.metro.mod.RenderTrup3Entity;
import ru.metro.mod.RenderTrup4Entity;
import ru.metro.mod.RenderTuzicEntity;
import ru.metro.mod.Trup1Entity;
import ru.metro.mod.Trup2Entity;
import ru.metro.mod.Trup3Entity;
import ru.metro.mod.Trup4Entity;
import ru.metro.mod.TuzicEntity;
import ru.metro.models.Jery;
import ru.metro.models.Komp;
import ru.metro.models.Kris;
import ru.metro.models.ModelGrib;
import ru.metro.models.Murzik;
import ru.metro.models.Trup1;
import ru.metro.models.Trup2;
import ru.metro.models.Trup3;
import ru.metro.models.Trup4;
import ru.metro.models.Tuzic;

public class ClientProxy
extends CommonProxy {
    @Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(KrisEntity.class, (Render)new RenderKrisEntity(new Kris(), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(MurzikEntity.class, (Render)new RenderMurzikEntity(new Murzik(), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(TuzicEntity.class, (Render)new RenderTuzicEntity(new Tuzic(), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(JeryEntity.class, (Render)new RenderJeryEntity(new Jery(), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(Trup1Entity.class, (Render)new RenderTrup1Entity(new Trup1(), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(Trup2Entity.class, (Render)new RenderTrup2Entity(new Trup2(), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(Trup3Entity.class, (Render)new RenderTrup3Entity(new Trup3(), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(Trup4Entity.class, (Render)new RenderTrup4Entity(new Trup4(), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(KompEntity.class, (Render)new RenderKompEntity(new Komp(), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(GribEntity.class, (Render)new RenderGribEntity(new ModelGrib(), 0.5f));
    }
}

