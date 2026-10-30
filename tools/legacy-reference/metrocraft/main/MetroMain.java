/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.Mod
 *  cpw.mods.fml.common.Mod$EventHandler
 *  cpw.mods.fml.common.Mod$Instance
 *  cpw.mods.fml.common.SidedProxy
 *  cpw.mods.fml.common.event.FMLInitializationEvent
 *  cpw.mods.fml.common.event.FMLPostInitializationEvent
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.network.IGuiHandler
 *  cpw.mods.fml.common.network.NetworkRegistry
 */
package metrocraft.main;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import metrocraft.directs.CommonProxyP;
import metrocraft.handlers.GuiHandler;
import metrocraft.server.PacketDispatcher;

@Mod(modid="metrocraft", name="MetroCraft", version="0.1")
public class MetroMain {
    @Mod.Instance(value="metrocraft")
    public static MetroMain mod;
    @SidedProxy(clientSide="metrocraft.directc.ClientProxyZ", serverSide="metrocraft.directs.CommonProxyP")
    public static CommonProxyP proxy;
    private static int modGuiIndex;
    public static final int GUI_CUSTOM_INV;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit();
        NetworkRegistry.INSTANCE.registerGuiHandler((Object)this, (IGuiHandler)new GuiHandler());
        PacketDispatcher.registerPackets();
    }

    @Mod.EventHandler
    public void Init(FMLInitializationEvent event) {
        proxy.Init();
        System.out.println("Metro Mod has been successfully initialized");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }

    static {
        modGuiIndex = 0;
        GUI_CUSTOM_INV = modGuiIndex++;
    }
}

