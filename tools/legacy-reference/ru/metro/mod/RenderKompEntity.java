/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.renderer.entity.RenderLiving
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 */
package ru.metro.mod;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import ru.metro.models.Komp;

public class RenderKompEntity
extends RenderLiving {
    private static final ResourceLocation textureLocation = new ResourceLocation("Metrocraft:textures/tehnica/pc.png");

    public RenderKompEntity(Komp model, float shadowSize) {
        super((ModelBase)model, shadowSize);
    }

    protected ResourceLocation func_110775_a(Entity par1Entity) {
        return textureLocation;
    }
}

