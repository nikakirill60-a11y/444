package ru.metrocraft.port.client;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.mcmod.*;
/** Compact 1.12 GUI for the original four implemented module categories and repair. */
public final class GunModsGui extends GuiContainer {
    private final EntityPlayer player;
    public GunModsGui(EntityPlayer player){super(new GunWorkbenchContainer(player));this.player=player;xSize=300;ySize=170;}
    @Override public void initGui(){
        super.initGui();buttonList.clear();
        for(int page=1;page<=4;page++){
            int y=guiTop+42+(page-1)*24;
            if(page==2){addButton(new ModuleButton("pric2",21,guiLeft+110,y,65,20,I18n.format("metroport.mcmod.mods.optic")));addButton(new ModuleButton("pric",22,guiLeft+177,y,65,20,I18n.format("metroport.mcmod.mods.collimator")));}
            else addButton(new ModuleButton(page==1?"stv":page==3?"mag":"laser",page*10+1,guiLeft+110,y,132,20,I18n.format("metroport.mcmod.mods.install")));
            addButton(new ModuleButton("pric0",page*10,guiLeft+244,y,48,20,I18n.format("metroport.mcmod.mods.remove")));
        }
        if(MetroPort.SOUNDS.containsKey("mcmod_open"))player.playSound(MetroPort.SOUNDS.get("mcmod_open"),.6F,1);
        addButton(new ModuleButton("ok",51,guiLeft+110,guiTop+140,182,20,I18n.format("metroport.mcmod.mods.repair")));
    }
    @Override public void updateScreen(){
        super.updateScreen();ItemStack stack=player.getHeldItemMainhand();
        if(!(stack.getItem() instanceof McmodAkItem)){mc.displayGuiScreen(null);return;}
        McmodAkItem item=(McmodAkItem)stack.getItem();
        for(GuiButton button:buttonList){int page=button.id/10,value=button.id%10;
            int current=page<=4?McmodAkItem.module(stack,GunUpgrades.KEYS[page]):0;
            int[] cost=GunUpgrades.cost(page,value);
            button.enabled=page==5?stack.getItemDamage()>0:GunUpgrades.supported(item.getDefinition().id,page,value)&&(value==0?current!=0:current==0);
            button.enabled&=GunUpgrades.count(player,"mcmod_res_metall")>=cost[0]&&GunUpgrades.count(player,"mcmod_res_xim")>=cost[1];
        }
    }
    @Override protected void actionPerformed(GuiButton button){
        MetroPort.NETWORK.sendToServer(new GunActionPacket(1,inventorySlots.windowId,button.id/10,button.id%10));
    }
    @Override protected void drawGuiContainerBackgroundLayer(float partial,int x,int y){
        drawRect(guiLeft,guiTop,guiLeft+xSize,guiTop+ySize,0xf01b2020);
        net.minecraft.client.renderer.GlStateManager.color(1,1,1,.7F);net.minecraft.client.renderer.GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(new net.minecraft.util.ResourceLocation("metroport:textures/mcmod/mods_gui/bar2.png"));
        Gui.drawScaledCustomSizeModalRect(guiLeft,guiTop,0,0,1,1,xSize,ySize,1,1);
        net.minecraft.client.renderer.GlStateManager.color(1,1,1,1);net.minecraft.client.renderer.GlStateManager.disableBlend();
        drawRect(guiLeft,guiTop,guiLeft+xSize,guiTop+2,0xffb6a56b);
    }
    @Override protected void drawGuiContainerForegroundLayer(int x,int y){
        ItemStack gun=player.getHeldItemMainhand();fontRenderer.drawString(gun.getDisplayName(),9,8,0xe8dbb6);
        if(gun.getItem() instanceof McmodAkItem){McmodAkItem item=(McmodAkItem)gun.getItem();fontRenderer.drawString(I18n.format("metroport.mcmod.gun.condition",item.getMaxDamage()-gun.getItemDamage(),item.getMaxDamage()),9,20,0xd0d0d0);}
        fontRenderer.drawString(I18n.format("metroport.mcmod.mods.resources",GunUpgrades.count(player,"mcmod_res_metall"),GunUpgrades.count(player,"mcmod_res_xim")),9,31,0xb8b8a8);
        for(int page=1;page<=4;page++)fontRenderer.drawString(I18n.format("metroport.mcmod.mods.page"+page),9,48+(page-1)*24,0xe0e0e0);
        fontRenderer.drawString(I18n.format("metroport.mcmod.mods.page5"),9,146,0xe0e0e0);
    }
    @Override public void drawScreen(int x,int y,float partial){
        drawDefaultBackground();super.drawScreen(x,y,partial);
        for(GuiButton b:buttonList)if(b.isMouseOver()){
            int page=b.id/10,value=b.id%10;int[] price=GunUpgrades.cost(page,value);
            drawHoveringText(I18n.format(value==0?"metroport.mcmod.mods.refund":"metroport.mcmod.mods.price",value==0?8:price[0],value==0?4:price[1]),x,y);
        }
    }
    private static final class ModuleButton extends GuiButton {
        private final String icon,label;
        ModuleButton(String icon,int id,int x,int y,int width,int height,String label){super(id,x,y,width,height,"");this.icon=icon;this.label=label;}
        @Override public void drawButton(net.minecraft.client.Minecraft mc,int x,int y,float partial){
            super.drawButton(mc,x,y,partial);if(!visible)return;
            net.minecraft.client.renderer.GlStateManager.color(1,1,1,enabled?1:.35F);net.minecraft.client.renderer.GlStateManager.enableBlend();
            mc.getTextureManager().bindTexture(new net.minecraft.util.ResourceLocation("metroport:textures/mcmod/mods_gui/"+icon+".png"));
            Gui.drawScaledCustomSizeModalRect(this.x+2,this.y+2,0,0,1,1,16,16,1,1);
            net.minecraft.client.renderer.GlStateManager.color(1,1,1,1);net.minecraft.client.renderer.GlStateManager.disableBlend();
            net.minecraft.client.renderer.GlStateManager.pushMatrix();
            try{net.minecraft.client.renderer.GlStateManager.translate(this.x+21,this.y+7,0);net.minecraft.client.renderer.GlStateManager.scale(.75F,.75F,1);mc.fontRenderer.drawStringWithShadow(label,0,0,enabled?0xffffff:0x888888);}
            finally{net.minecraft.client.renderer.GlStateManager.popMatrix();}
        }
    }

}
