/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.Mod
 *  cpw.mods.fml.common.Mod$EventHandler
 *  cpw.mods.fml.common.Mod$Instance
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.event.FMLServerStartingEvent
 *  net.minecraft.command.ICommand
 *  net.minecraftforge.common.MinecraftForge
 */
package timaxa007.no_drop;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.command.ICommand;
import net.minecraftforge.common.MinecraftForge;
import timaxa007.no_drop.EventsFML;
import timaxa007.no_drop.EventsForge;
import timaxa007.no_drop.NoDropCommand;

@Mod(modid="nodrops", name="No Drops", version="0.2")
public final class NoDropsMod {
    public static final String MODID = "nodrops";
    public static final String NAME = "No Drops";
    public static final String VERSION = "0.2";
    @Mod.Instance(value="nodrops")
    public static NoDropsMod instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register((Object)new EventsFML());
        MinecraftForge.EVENT_BUS.register((Object)new EventsForge());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand((ICommand)new NoDropCommand());
    }
}

