package ru.metrocraft.port.client;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import ru.metrocraft.port.inventory.*;

/** A skin over the SAME vanilla transaction container: no cloned items or new protocol. */
public final class MetroChestGui extends GuiContainer {
    private final int storageSlots,rows;
    public MetroChestGui(GuiChest original){
        super(original.inventorySlots);
        storageSlots=inventorySlots.inventorySlots.size()-36;rows=storageSlots/9;
        xSize=230;ySize=132+rows*18;
        for(int i=0;i<storageSlots;i++){
            Slot slot=inventorySlots.getSlot(i);slot.xPos=34+i%9*18;slot.yPos=36+i/9*18;
        }
        for(int i=0;i<36;i++){
            Slot slot=inventorySlots.getSlot(storageSlots+i);slot.xPos=34+i%9*18;
            slot.yPos=rows*18+(i<27?49+i/9*18:107);
        }
    }
    public static boolean supports(GuiChest gui){
        int count=gui.inventorySlots.inventorySlots.size()-36;
        return count>=9 && count<=54 && count%9==0;
    }
    private void panel(int x,int y,int w,int h,int edge,int fill){
        drawRect(guiLeft+x,guiTop+y,guiLeft+x+w,guiTop+y+h,edge);
        drawRect(guiLeft+x+1,guiTop+y+1,guiLeft+x+w-1,guiTop+y+h-1,fill);
    }
    @Override protected void drawGuiContainerBackgroundLayer(float tick,int mouseX,int mouseY){
        panel(0,0,xSize,ySize,0xffb37c44,0xf5292724);
        panel(5,5,xSize-10,20,0xff39342d,0xff1a1918);
        fontRenderer.drawString(fontRenderer.trimStringToWidth(inventorySlots.getSlot(0).inventory.getDisplayName().getUnformattedText(),205),guiLeft+12,guiTop+11,0xd7c8ad);
        fontRenderer.drawString(I18n.format("container.inventory"),guiLeft+34,guiTop+rows*18+36,0xd7c8ad);
        for(Slot slot:inventorySlots.inventorySlots){
            int edge=slot.getHasStack()?0xff000000|ItemRarity.of(slot.getStack()).color:0xff665b4b;
            panel(slot.xPos-1,slot.yPos-1,18,18,edge,0xff1e1e1b);
        }
        if(mc.player!=null){
            long grams=CarryWeight.carried(mc.player),capacity=CarryWeight.capacity(mc.player);
            String weight=ItemMetrics.kg(grams)+" / "+ItemMetrics.kg(capacity)+" "+I18n.format("metroport.inventory.kg");
            fontRenderer.drawString(weight,guiLeft+34,guiTop+ySize-11,grams>capacity?0xed6b56:0xd7c8ad);
        }
    }
    @Override protected void drawGuiContainerForegroundLayer(int mouseX,int mouseY){}
    @Override public void drawScreen(int mouseX,int mouseY,float tick){
        drawDefaultBackground();super.drawScreen(mouseX,mouseY,tick);renderHoveredToolTip(mouseX,mouseY);
    }
}
