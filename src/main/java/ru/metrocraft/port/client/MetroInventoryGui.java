package ru.metrocraft.port.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import ru.metrocraft.port.inventory.Backpacks;
import ru.metrocraft.port.inventory.MetroContainer;

public final class MetroInventoryGui extends GuiContainer {
    private final EntityPlayer player;
    public MetroInventoryGui(EntityPlayer player) { super(new MetroContainer(player)); this.player=player; xSize=220; ySize=166; }
    private net.minecraft.client.gui.GuiButton bagButton;
    @Override public void initGui(){super.initGui();bagButton=new net.minecraft.client.gui.GuiButton(711,guiLeft+175,guiTop+78,40,18,net.minecraft.client.resources.I18n.format("metroport.blackrazid.open"));buttonList.add(bagButton);}
    @Override public void updateScreen(){super.updateScreen();if(bagButton!=null)bagButton.visible=!ru.metrocraft.port.inventory.ExtraEquipment.get(player,ru.metrocraft.port.inventory.ExtraEquipment.Kind.BLACKRAZID_BACKPACK).isEmpty();}
    @Override protected void actionPerformed(net.minecraft.client.gui.GuiButton button) throws java.io.IOException {
        if(button==bagButton)ru.metrocraft.port.MetroPort.NETWORK.sendToServer(new ru.metrocraft.port.blackrazid.OpenBackpackPacket());else super.actionPerformed(button);
    }
    @Override public void drawScreen(int x,int y,float tick){
        drawDefaultBackground();super.drawScreen(x,y,tick);renderHoveredToolTip(x,y);
        if(ru.metrocraft.port.inventory.EquipmentRules.sealed(player.getItemStackFromSlot(net.minecraft.inventory.EntityEquipmentSlot.CHEST))
                && x>=guiLeft+43 && x<guiLeft+62 && y>=guiTop+6 && y<guiTop+25)
            drawHoveringText(java.util.Collections.singletonList(net.minecraft.client.resources.I18n.format("metroport.armor.builtin_head")),x,y);
    }
    @Override protected void drawGuiContainerForegroundLayer(int x,int y) { }
    private void extraSlot(int x,int y,String label){
        drawRect(guiLeft+x-1,guiTop+y-1,guiLeft+x+17,guiTop+y+17,0xFF353535);
        drawRect(guiLeft+x,guiTop+y,guiLeft+x+16,guiTop+y+16,0xFF777777);
        fontRenderer.drawString(net.minecraft.client.resources.I18n.format(label),guiLeft+176,guiTop+y-12,0x404040);
    }
    @Override protected void drawGuiContainerBackgroundLayer(float partial,int mouseX,int mouseY) {
        GlStateManager.color(1,1,1,1);
        mc.getTextureManager().bindTexture(new ResourceLocation("metroport:textures/gui/inventory.png"));
        drawTexturedModalRect(guiLeft,guiTop,0,0,176,ySize);
        drawRect(guiLeft+174,guiTop,guiLeft+220,guiTop+ySize,0xFFC6C6C6);
        extraSlot(184,18,"metroport.blackrazid.lamp_slot");extraSlot(184,54,"metroport.blackrazid.bag_slot");
        drawRect(guiLeft+139,guiTop+17,guiLeft+157,guiTop+35,0xFF353535);
        drawRect(guiLeft+140,guiTop+18,guiLeft+156,guiTop+34,0xFF777777);
        fontRenderer.drawString(net.minecraft.client.resources.I18n.format("metroport.nvg.slot"),guiLeft+135,guiTop+6,0x404040);
        drawRect(guiLeft+139,guiTop+53,guiLeft+157,guiTop+71,0xFF353535);
        drawRect(guiLeft+140,guiTop+54,guiLeft+156,guiTop+70,0xFF777777);
        fontRenderer.drawString(net.minecraft.client.resources.I18n.format("metroport.watch.slot"),guiLeft+135,guiTop+42,0x404040);
        if(ru.metrocraft.port.inventory.EquipmentRules.sealed(player.getItemStackFromSlot(net.minecraft.inventory.EntityEquipmentSlot.CHEST))) {
            drawRect(guiLeft+43,guiTop+6,guiLeft+62,guiTop+25,0x88552222);
            fontRenderer.drawString("×",guiLeft+48,guiTop+11,0xFF5555);
        }
        GuiInventory.drawEntityOnScreen(guiLeft+88,guiTop+75,30,guiLeft+88-mouseX,guiTop+25-mouseY,player);
        int rows=ru.metrocraft.port.inventory.InventoryCapacity.rows(player);
        mc.getTextureManager().bindTexture(new ResourceLocation("metroport:textures/gui/slots.png"));
        GlStateManager.color(1,1,1,1);
        drawTexturedModalRect(guiLeft+7,guiTop+83,0,0,162,rows*18);
        for(int row=rows;row<3;row++) drawRect(guiLeft+7,guiTop+83+row*18,guiLeft+169,guiTop+101+row*18,0xAA303030);
    }
}
