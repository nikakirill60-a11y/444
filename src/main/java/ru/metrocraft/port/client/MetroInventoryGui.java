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
    public MetroInventoryGui(EntityPlayer player) { super(new MetroContainer(player)); this.player=player; xSize=176; ySize=166; }
    @Override public void drawScreen(int x,int y,float tick){drawDefaultBackground();super.drawScreen(x,y,tick);renderHoveredToolTip(x,y);}
    @Override protected void drawGuiContainerForegroundLayer(int x,int y) { }
    @Override protected void drawGuiContainerBackgroundLayer(float partial,int mouseX,int mouseY) {
        GlStateManager.color(1,1,1,1);
        mc.getTextureManager().bindTexture(new ResourceLocation("metroport:textures/gui/inventory.png"));
        drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);
        GuiInventory.drawEntityOnScreen(guiLeft+88,guiTop+75,30,guiLeft+88-mouseX,guiTop+25-mouseY,player);
        int rows=Backpacks.rows(((MetroContainer)inventorySlots).bag.getStackInSlot(0));
        mc.getTextureManager().bindTexture(new ResourceLocation("metroport:textures/gui/slots.png"));
        GlStateManager.color(1,1,1,1);
        drawTexturedModalRect(guiLeft+7,guiTop+83,0,0,162,rows*18);
        for(int row=rows;row<3;row++) drawRect(guiLeft+7,guiTop+83+row*18,guiLeft+169,guiTop+101+row*18,0xAA303030);
    }
}
