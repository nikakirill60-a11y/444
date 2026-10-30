/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.item.Item
 */
package metrocraft.main;

import cpw.mods.fml.common.registry.GameRegistry;
import metrocraft.backpacksrender.BigRucksack;
import metrocraft.backpacksrender.MediumRucksack;
import metrocraft.backpacksrender.SmallRucksack;
import net.minecraft.item.Item;

public class MetroItems {
    public static Item smallRucksack;
    public static Item mediumRucksack;
    public static Item bigRucksack;

    public static void registerItems() {
        smallRucksack = new SmallRucksack();
        mediumRucksack = new MediumRucksack();
        bigRucksack = new BigRucksack();
        GameRegistry.registerItem((Item)smallRucksack, (String)smallRucksack.func_77658_a());
        GameRegistry.registerItem((Item)mediumRucksack, (String)mediumRucksack.func_77658_a());
        GameRegistry.registerItem((Item)bigRucksack, (String)bigRucksack.func_77658_a());
    }
}

