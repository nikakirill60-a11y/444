/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.model.AdvancedModelLoader
 *  net.minecraftforge.client.model.IModelCustom
 */
package metrocraft.main;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class MetroResources {
    public static final ResourceLocation inventoryTexture = new ResourceLocation("metrocraft", "textures/gui/inventory.png");
    public static final ResourceLocation slotsTexture = new ResourceLocation("metrocraft", "textures/gui/slots.png");
    public static final ResourceLocation smallRucksackTexture = new ResourceLocation("metrocraft", "textures/models/items/smallRucksack.png");
    public static final ResourceLocation mediumRucksackTexture = new ResourceLocation("metrocraft", "textures/models/items/mediumRucksack.png");
    public static final ResourceLocation bigRucksackTexture = new ResourceLocation("metrocraft", "textures/models/items/bigRucksack.png");
    public static final IModelCustom smallRucksackModel = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metrocraft", "models/items/smallRucksack.obj"));
    public static final IModelCustom mediumRucksackModel = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metrocraft", "models/items/mediumRucksack.obj"));
    public static final IModelCustom bigRucksackModel = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metrocraft", "models/items/bigRucksack.obj"));
}

