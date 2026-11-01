package ru.metrocraft.port.client;

import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.blackrazid.weapons.*;
import ru.metrocraft.port.mcmod.GunActionPacket;

/** Original item icons, exact native socket indices and real server-owned attachment transactions. */
public final class BlackrazidModulesGui extends GuiContainer {
    private final EntityPlayer player;
    public BlackrazidModulesGui(EntityPlayer p){super(new NativeModuleContainer(p));player=p;xSize=320;ySize=218;}
    @Override public void initGui(){
        super.initGui();buttonList.clear();ItemStack gun=player.getHeldItemMainhand();
        for(int slot=0;slot<5;slot++){
            int column=0,y=guiTop+35+slot*30;
            for(int i=0;i<BlackrazidModules.entries().size();i++){
                ItemStack part=new ItemStack(BlackrazidWeapons.item(BlackrazidModules.entry(i).id));
                if(BlackrazidModules.accepts(gun,slot,part))addButton(new PartButton(slot*32+i+1,guiLeft+112+column++*28,y,part));
            }
            addButton(new GuiButton(slot*32,guiLeft+258,y,54,24,I18n.format("metroport.blackrazid.modules.remove")));
        }
    }
    @Override public void updateScreen(){
        super.updateScreen();if(!inventorySlots.canInteractWith(player)){mc.displayGuiScreen(null);return;}
        ItemStack gun=player.getHeldItemMainhand();
        for(GuiButton button:buttonList){
            int slot=button.id/32,choice=button.id%32;ItemStack installed=BlackrazidModules.get(gun,slot);
            button.enabled=choice==0?!installed.isEmpty():installed.isEmpty() && BlackrazidModules.count(player,BlackrazidWeapons.item(BlackrazidModules.entry(choice-1).id))>0;
        }
    }
    @Override protected void actionPerformed(GuiButton button){MetroPort.NETWORK.sendToServer(new GunActionPacket(3,inventorySlots.windowId,button.id/32,button.id%32));}
    @Override protected void drawGuiContainerBackgroundLayer(float partial,int x,int y){
        drawRect(guiLeft,guiTop,guiLeft+xSize,guiTop+ySize,0xf01b2020);drawRect(guiLeft,guiTop,guiLeft+xSize,guiTop+2,0xffb6a56b);
        for(int slot=0;slot<5;slot++)drawRect(guiLeft+7,guiTop+34+slot*30,guiLeft+106,guiTop+61+slot*30,0xff303630);
    }
    @Override protected void drawGuiContainerForegroundLayer(int x,int y){
        ItemStack gun=player.getHeldItemMainhand();fontRenderer.drawString(gun.getDisplayName(),9,9,0xe8dbb6);
        for(int slot=0;slot<5;slot++){
            int row=35+slot*30;fontRenderer.drawString(I18n.format("metroport.blackrazid.modules.slot",slot+1),10,row+8,0xbdbdb0);
            ItemStack installed=BlackrazidModules.get(gun,slot);
            if(!installed.isEmpty())mc.getRenderItem().renderItemAndEffectIntoGUI(installed,84,row+4);
        }
        fontRenderer.drawSplitString(I18n.format("metroport.blackrazid.modules.note"),9,190,302,0xbdbdb0);
    }
    @Override public void drawScreen(int x,int y,float partial){
        drawDefaultBackground();super.drawScreen(x,y,partial);
        for(GuiButton b:buttonList)if(b.isMouseOver() && b instanceof PartButton)renderToolTip(((PartButton)b).part,x,y);
        for(int slot=0;slot<5;slot++)if(isPointInRegion(82,37+slot*30,20,20,x,y)){
            ItemStack installed=BlackrazidModules.get(player.getHeldItemMainhand(),slot);if(!installed.isEmpty())renderToolTip(installed,x,y);
        }
    }
    private static final class PartButton extends GuiButton {
        final ItemStack part;
        PartButton(int id,int x,int y,ItemStack part){super(id,x,y,26,24,"");this.part=part;}
        @Override public void drawButton(net.minecraft.client.Minecraft mc,int mouseX,int mouseY,float partial){
            super.drawButton(mc,mouseX,mouseY,partial);if(visible)mc.getRenderItem().renderItemAndEffectIntoGUI(part,x+5,y+4);
        }
    }
}
