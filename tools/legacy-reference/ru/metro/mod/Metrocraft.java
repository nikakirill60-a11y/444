/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.Mod
 *  cpw.mods.fml.common.Mod$EventHandler
 *  cpw.mods.fml.common.Mod$Instance
 *  cpw.mods.fml.common.SidedProxy
 *  cpw.mods.fml.common.event.FMLInitializationEvent
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.registry.EntityRegistry
 *  cpw.mods.fml.common.registry.LanguageRegistry
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityList$EntityEggInfo
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemFood
 *  net.minecraft.potion.Potion
 */
package ru.metro.mod;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.potion.Potion;
import ru.metro.mod.CommonProxy;
import ru.metro.mod.GribEntity;
import ru.metro.mod.JeryEntity;
import ru.metro.mod.KompEntity;
import ru.metro.mod.KrisEntity;
import ru.metro.mod.MurzikEntity;
import ru.metro.mod.Trup1Entity;
import ru.metro.mod.Trup2Entity;
import ru.metro.mod.Trup3Entity;
import ru.metro.mod.Trup4Entity;
import ru.metro.mod.TuzicEntity;

@Mod(modid="Metrocraft", name="Metro-Craft", version="0.7")
public class Metrocraft {
    @Mod.Instance(value="Metrocraft")
    public static Metrocraft instance;
    public static Item MetroFood;
    @SidedProxy(clientSide="ru.metro.mod.ClientProxy", serverSide="ru.metro.mod.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void modLoad(FMLInitializationEvent event) {
    }

    @Mod.EventHandler
    public void preLoad(FMLPreInitializationEvent event) {
        Metrocraft.registerEntity(KrisEntity.class, "KrisEntity", 5597999, 139);
        proxy.registerRenderers();
        Metrocraft.registerEntity(MurzikEntity.class, "MurzikEntity", 5597999, 139);
        proxy.registerRenderers();
        Metrocraft.registerEntity(TuzicEntity.class, "TuzicEntity", 5597999, 139);
        proxy.registerRenderers();
        Metrocraft.registerEntity(JeryEntity.class, "JeryEntity", 5597999, 139);
        proxy.registerRenderers();
        Metrocraft.registerEntity(Trup1Entity.class, "Trup1Entity", 5597999, 139);
        proxy.registerRenderers();
        Metrocraft.registerEntity(Trup2Entity.class, "Trup2Entity", 5597999, 139);
        proxy.registerRenderers();
        Metrocraft.registerEntity(Trup3Entity.class, "Trup3Entity", 5597999, 139);
        proxy.registerRenderers();
        Metrocraft.registerEntity(Trup4Entity.class, "Trup4Entity", 5597999, 139);
        proxy.registerRenderers();
        Metrocraft.registerEntity(KompEntity.class, "Komp", 5597999, 139);
        proxy.registerRenderers();
        Metrocraft.registerEntity(GribEntity.class, "Grib", 5597999, 139);
        proxy.registerRenderers();
        MetroFood = new ItemFood(3001, 2.0f, true).func_77844_a(Potion.field_82731_v.field_76415_H, 30, 0, 0.0f).func_77655_b("glist").func_77637_a(CreativeTabs.field_78039_h);
        LanguageRegistry.addName((Object)MetroFood, (String)"glist");
    }

    public static void registerEntity(Class entityClass, String name, int primaryColor, int secondaryColor) {
        int entityID = EntityRegistry.findGlobalUniqueEntityId();
        long seed = name.hashCode();
        EntityRegistry.registerGlobalEntityID((Class)entityClass, (String)name, (int)entityID);
        EntityRegistry.registerModEntity((Class)entityClass, (String)name, (int)entityID, (Object)instance, (int)64, (int)1, (boolean)true);
        EntityList.field_75627_a.put(entityID, new EntityList.EntityEggInfo(entityID, primaryColor, secondaryColor));
    }
}

