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
import ru.metro.models.Trup3;

public class RenderTrup3Entity
extends RenderLiving {
    private static final ResourceLocation textureLocation = new ResourceLocation("Metrocraft:textures/entity/trup3.png");

    public RenderTrup3Entity(Trup3 model, float shadowSize) {
        super((ModelBase)model, shadowSize);
    }

    protected ResourceLocation func_110775_a(Entity par1Entity) {
        return textureLocation;
    }
}

