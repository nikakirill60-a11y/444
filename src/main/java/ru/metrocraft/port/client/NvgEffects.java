package ru.metrocraft.port.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.inventory.NvgEquipment;
import ru.metrocraft.port.inventory.NvgTogglePacket;
import java.io.File;

/** Server-owned NVG display state. Original texture overlays are drawn separately behind the HUD. */
@Mod.EventBusSubscriber(modid=MetroPort.ID,value=Side.CLIENT)
public final class NvgEffects {
    private static final KeyBinding TOGGLE=new KeyBinding("key.metroport.nvg_toggle",Keyboard.KEY_N,"key.categories.metroport");
    private static WorldClient world;
    private static EntityPlayer owner;
    private static boolean active,ready,tint=true;
    private static int age;
    private NvgEffects() {}
    public static void initialize(){
        ClientRegistry.registerKeyBinding(TOGGLE);
        Minecraft mc=Minecraft.getMinecraft();
        Configuration config=new Configuration(new File(new File(mc.gameDir,"config"),"metroport-visuals.cfg"));
        config.load();tint=config.getBoolean("nvgTint","client",true,"Subtle NVG tint only. Does not replace shaders, textures or hand poses.");
        MetroOverlays.configure(config,tint);
        if(config.hasChanged())config.save();
    }
    public static void receive(int dimension,int id,boolean on){
        Minecraft mc=Minecraft.getMinecraft();WorldClient expectedWorld=mc.world;EntityPlayer expectedOwner=mc.player;
        mc.addScheduledTask(()->{
            if(expectedWorld==null || expectedOwner==null || expectedWorld!=mc.world || expectedOwner!=mc.player
                    || mc.player.dimension!=dimension || mc.player.getEntityId()!=id)return;
            world=expectedWorld;owner=expectedOwner;active=on;ready=true;age=0;
        });
    }
    @SubscribeEvent public static void key(InputEvent.KeyInputEvent event){
        Minecraft mc=Minecraft.getMinecraft();
        while(TOGGLE.isPressed())if(mc.player!=null && mc.currentScreen==null)MetroPort.NETWORK.sendToServer(new NvgTogglePacket());
    }
    @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event){
        if(event.phase!=TickEvent.Phase.END)return;Minecraft mc=Minecraft.getMinecraft();
        if(world!=mc.world || owner!=mc.player){world=mc.world;owner=mc.player;active=false;ready=false;age=0;}
        if(!mc.isGamePaused() && age<61)age++;
    }
    public static boolean visualActive(){
        Minecraft mc=Minecraft.getMinecraft();
        return active && ready && age<=60 && world==mc.world && owner==mc.player && mc.player!=null
                && mc.player.isEntityAlive() && !mc.player.isSpectator() && !NvgEquipment.get(mc.player).isEmpty();
    }
    @SubscribeEvent public static void overlay(RenderGameOverlayEvent.Post event){
        Minecraft mc=Minecraft.getMinecraft();
        if(event.getType()!=RenderGameOverlayEvent.ElementType.ALL || mc.player==null || !mc.player.isEntityAlive()
                || mc.player.isSpectator() || mc.gameSettings.hideGUI || !ready || age>60
                || world!=mc.world || owner!=mc.player || NvgEquipment.get(mc.player).isEmpty())return;
        String state=I18n.format(active?"metroport.blackrazid.on":"metroport.blackrazid.off");
        mc.fontRenderer.drawStringWithShadow(I18n.format("metroport.nvg.state",state),8,event.getResolution().getScaledHeight()-106,0x90DA90);
    }
}
