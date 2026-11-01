package ru.metrocraft.port.client;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import ru.metrocraft.port.blackrazid.BackpackContainer;
public final class BlackrazidBackpackGui extends GuiContainer {
    private final BackpackContainer bag;
    public BlackrazidBackpackGui(EntityPlayer player,int source){super(new BackpackContainer(player,source));bag=(BackpackContainer)inventorySlots;xSize=176;ySize=168;}
    @Override public void drawScreen(int x,int y,float partial){drawDefaultBackground();super.drawScreen(x,y,partial);renderHoveredToolTip(x,y);}
    @Override protected void drawGuiContainerForegroundLayer(int x,int y){fontRenderer.drawString(bag.bag.getDisplayName(),8,6,0xeeeeee);}
    @Override protected void drawGuiContainerBackgroundLayer(float partial,int x,int y){
        drawRect(guiLeft,guiTop,guiLeft+xSize,guiTop+ySize,0xff343837);
        for(net.minecraft.inventory.Slot s:inventorySlots.inventorySlots){
            int color=s.inventory==bag.storage?0xffb3b19d:0xff777b75;
            drawRect(guiLeft+s.xPos-1,guiTop+s.yPos-1,guiLeft+s.xPos+17,guiTop+s.yPos+17,0xff151817);
            drawRect(guiLeft+s.xPos,guiTop+s.yPos,guiLeft+s.xPos+16,guiTop+s.yPos+16,color);
        }
    }
}
