package ru.metrocraft.port.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.inventory.*;

@Mod.EventBusSubscriber(modid=MetroPort.ID,value=Side.CLIENT)
public final class InventoryComparison {
    private static final int MODE=19440,EQUIPMENT=19441;
    public static void receive(boolean vanilla,boolean open){
        Minecraft mc=Minecraft.getMinecraft();
        if(mc.player==null)return;
        mc.player.getEntityData().setBoolean(InventoryModes.KEY,vanilla);
        if(open && vanilla)mc.displayGuiScreen(new GuiInventory(mc.player));
    }
    @SubscribeEvent public static void buttons(GuiScreenEvent.InitGuiEvent.Post event){
        if(!(event.getGui() instanceof GuiInventory) && !(event.getGui() instanceof MetroInventoryGui)
                && !(event.getGui() instanceof GuiContainerCreative))return;
        GuiContainer gui=(GuiContainer)event.getGui();
        int x=gui.getGuiLeft(),y=Math.max(0,gui.getGuiTop()-23);
        event.getButtonList().add(new GuiButton(MODE,x,y,106,20,I18n.format("metroport.inventory.mode",InventoryModes.vanilla(Minecraft.getMinecraft().player)?"Minecraft":"Metro")));
        if(!(gui instanceof MetroInventoryGui))event.getButtonList().add(new GuiButton(EQUIPMENT,x+108,y,68,20,I18n.format("metroport.inventory.equipment")));
    }
    @SubscribeEvent public static void click(GuiScreenEvent.ActionPerformedEvent.Pre event){
        int id=event.getButton().id;
        if(id!=MODE && id!=EQUIPMENT)return;
        if(!(event.getGui() instanceof GuiInventory) && !(event.getGui() instanceof MetroInventoryGui)
                && !(event.getGui() instanceof GuiContainerCreative))return;
        event.setCanceled(true);
        if(id==MODE)MetroPort.NETWORK.sendToServer(new InventoryModePacket(!InventoryModes.vanilla(Minecraft.getMinecraft().player)));
        else MetroPort.NETWORK.sendToServer(new OpenInventoryPacket());
    }
}
