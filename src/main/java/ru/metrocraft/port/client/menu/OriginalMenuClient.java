package ru.metrocraft.port.client.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;

/** Registered exclusively from ClientProxy. Never loaded by dedicated servers. */
public final class OriginalMenuClient {
    public static final String SERVER="localhost:25565";
    private static boolean bypass;
    private static OriginalMenuScreen active;
    private static boolean initialized;
    public static void initialize() {
        if(initialized)return;initialized=true;
        MinecraftForge.EVENT_BUS.register(new OriginalMenuClient());
        ((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(manager->{
            if(active!=null)active.releaseResources();
        });
    }
    @SubscribeEvent public void open(GuiOpenEvent event) {
        Minecraft mc=Minecraft.getMinecraft();
        if(!bypass && !Boolean.getBoolean("metroport.disableOriginalMenu") && mc.world==null &&
           event.getGui()!=null && event.getGui().getClass()==GuiMainMenu.class)event.setGui(new OriginalMenuScreen());
    }
    static void opened(OriginalMenuScreen screen) { active=screen; }
    static void closed(OriginalMenuScreen screen) { if(active==screen)active=null; }
    static void vanilla(Throwable failure) {
        bypass=true;
        if(failure!=null)LogManager.getLogger("MinETROMenu").error("Original menu failed; restoring vanilla menu for this session",failure);
        Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
    }
}
