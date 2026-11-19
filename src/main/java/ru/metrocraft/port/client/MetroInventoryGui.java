package ru.metrocraft.port.client;

import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import ru.metrocraft.port.inventory.*;

/** Texture-independent Metro panel. 312x230 fits Minecraft's minimum 320x240 scaled viewport. */
public final class MetroInventoryGui extends GuiContainer {
    private final EntityPlayer player;
    private net.minecraft.client.gui.GuiButton recovery,overflow;
    private boolean cursorRotated;
    private static final int GRID_X=170,GRID_Y=40,CELL=16;
    private static final int ORANGE=0xffb58249,TEXT=0xc7bfb1;
    public MetroInventoryGui(EntityPlayer player){super(new MetroContainer(player));this.player=player;xSize=312;ySize=230;}
    @Override public void initGui(){
        super.initGui();recovery=new net.minecraft.client.gui.GuiButton(711,guiLeft+170,guiTop+204,132,18,I18n.format("metroport.inventory.recover"));buttonList.add(recovery);
        overflow=new net.minecraft.client.gui.GuiButton(712,guiLeft+170,guiTop+204,132,18,"");buttonList.add(overflow);
    }
    @Override public void updateScreen(){
        super.updateScreen();
        int count=overflowCount();boolean legacy=ru.metrocraft.port.blackrazid.BackpackContainer.valid(player,2);
        if(overflow!=null){overflow.visible=count>0;overflow.displayString=I18n.format("metroport.inventory.overflow",count);}
        if(recovery!=null)recovery.visible=legacy && count==0;
        if(player.inventory.getItemStack().isEmpty())cursorRotated=false;
    }
    @Override protected void actionPerformed(net.minecraft.client.gui.GuiButton button)throws java.io.IOException{
        if(button==overflow)sendGrid(0,0,1);
        else if(button==recovery)ru.metrocraft.port.MetroPort.NETWORK.sendToServer(new ru.metrocraft.port.blackrazid.OpenBackpackPacket());else super.actionPerformed(button);
    }
    @Override public void drawScreen(int x,int y,float tick){
        drawDefaultBackground();super.drawScreen(x,y,tick);renderHoveredToolTip(x,y);
        int cell=gridCell(x,y);
        if(cell>=0 && player.inventory.getItemStack().isEmpty()){
            int record=container().grid().recordAt(cell);
            if(record>=0)renderToolTip(player.inventory.getStackInSlot(9+record),x,y);
        }
        if(CarryWeight.carried(player)>CarryWeight.capacity(player) && isPointInRegion(8,205,148,20,x,y))
            drawHoveringText(java.util.Collections.singletonList(I18n.format("metroport.inventory.overloaded")),x,y);
        for(Slot slot:inventorySlots.inventorySlots)if(slot.isEnabled() && !slot.getHasStack() && isPointInRegion(slot.xPos,slot.yPos,16,16,x,y)){
            String key=label(slot.slotNumber);if(key!=null)drawHoveringText(java.util.Collections.singletonList(I18n.format(key)),x,y);
        }
    }
    private MetroContainer container(){return (MetroContainer)inventorySlots;}
    private int overflowCount(){
        PackingGrid grid=container().grid();int count=0;
        for(int i=0;i<27;i++)if(!player.inventory.getStackInSlot(i+9).isEmpty() && grid.position(i)<0)count++;
        return count;
    }
    private int gridCell(int mouseX,int mouseY){
        int x=mouseX-guiLeft-GRID_X,y=mouseY-guiTop-GRID_Y;
        return x<0||y<0||x>=CELL*8||y>=CELL*8?-1:x/CELL+(y/CELL)*8;
    }
    private void sendGrid(int cell,int button,int action){
        ru.metrocraft.port.MetroPort.NETWORK.sendToServer(new GridClickPacket(inventorySlots.windowId,container().gridRevision,cell,button,cursorRotated,action));
    }
    @Override protected void mouseClicked(int x,int y,int button)throws java.io.IOException{
        int cell=gridCell(x,y);
        if(cell>=0){if(button==0||button==1)sendGrid(cell,button,0);return;}
        super.mouseClicked(x,y,button);
    }
    @Override protected void mouseReleased(int x,int y,int button){if(gridCell(x,y)<0)super.mouseReleased(x,y,button);}
    @Override protected void mouseClickMove(int x,int y,int button,long elapsed){if(gridCell(x,y)<0)super.mouseClickMove(x,y,button,elapsed);}
    @Override protected void keyTyped(char typed,int code)throws java.io.IOException{
        if(code==org.lwjgl.input.Keyboard.KEY_R && !player.inventory.getItemStack().isEmpty()){cursorRotated=!cursorRotated;return;}
        super.keyTyped(typed,code);
    }
    private static String label(int slot){
        switch(slot){
            case 0:return "metroport.inventory.legacy_bag";
            case 1:return "metroport.inventory.helmet";case 2:return "metroport.inventory.armor";
            case 3:return "metroport.inventory.legs";case 4:return "metroport.inventory.feet";
            case 5:return "metroport.inventory.offhand";case 33:return "metroport.inventory.weapon1";
            case 34:return "metroport.inventory.weapon2";case 35:return "metroport.inventory.melee";
            case 42:return "metroport.nvg.slot";case 43:return "metroport.watch.slot";
            case 44:return "metroport.blackrazid.lamp_slot";case 45:return "metroport.blackrazid.bag_slot";
            default:return null;
        }
    }
    @Override protected void drawGuiContainerForegroundLayer(int x,int y){}
    private void panel(int x,int y,int w,int h,int border,int fill){
        drawRect(guiLeft+x,guiTop+y,guiLeft+x+w,guiTop+y+h,border);
        drawRect(guiLeft+x+1,guiTop+y+1,guiLeft+x+w-1,guiTop+y+h-1,fill);
    }
    private void text(String text,int x,int y,int color){fontRenderer.drawString(text,guiLeft+x,guiTop+y,color);}
    private void small(String text,int x,int y,int color,float scale){
        GlStateManager.pushMatrix();GlStateManager.translate(guiLeft+x,guiTop+y,0);GlStateManager.scale(scale,scale,1);
        fontRenderer.drawString(text,0,0,color);GlStateManager.popMatrix();
    }
    private void weaponCard(int id,int top){
        Slot slot=inventorySlots.getSlot(id);ItemStack stack=slot.getStack();int color=stack.isEmpty()?ORANGE:0xff000000|ItemRarity.of(stack).color;
        panel(8,top,148,32,color,0xff242321);
        small(stack.isEmpty()?I18n.format(label(id)):fontRenderer.trimStringToWidth(stack.getDisplayName(),157),36,top+4,TEXT,.75f);
        if(!stack.isEmpty()){
            small(ItemMetrics.kg(ItemMetrics.grams(stack))+" "+I18n.format("metroport.inventory.kg"),36,top+20,0xa8a096,.7f);
            if(stack.isItemStackDamageable())small(Math.max(0,100-stack.getItemDamage()*100/Math.max(1,stack.getMaxDamage()))+"%",124,top+20,0xa8a096,.7f);
        }
    }
    @Override protected void drawGuiContainerBackgroundLayer(float partial,int mouseX,int mouseY){
        GlStateManager.color(1,1,1,1);
        panel(0,0,xSize,ySize,ORANGE,0xf5292724);
        panel(5,5,155,20,0xff39342d,0xff1a1918);panel(165,5,142,20,0xff39342d,0xff1a1918);
        text(I18n.format("metroport.inventory.equipment"),10,11,TEXT);text(I18n.format("metroport.inventory.storage"),170,11,TEXT);
        drawRect(guiLeft+162,guiTop+29,guiLeft+163,guiTop+222,ORANGE);
        small(player.getName(),170,32,TEXT,.8f);

        GuiInventory.drawEntityOnScreen(guiLeft+77,guiTop+121,31,guiLeft+77-mouseX,guiTop+60-mouseY,player);
        weaponCard(33,132);weaponCard(34,169);
        for(Slot slot:inventorySlots.inventorySlots){
            if(!slot.isEnabled() || slot.slotNumber==33||slot.slotNumber==34)continue;
            if(slot.slotNumber==0&&!slot.getHasStack())continue;
            int color=slot.getHasStack()?0xff000000|ItemRarity.of(slot.getStack()).color:0xff665b4b;
            panel(slot.xPos-2,slot.yPos-2,20,20,color,0xff1e1e1b);
            if(!slot.getHasStack() && label(slot.slotNumber)!=null){
                String name=I18n.format(label(slot.slotNumber));
                small(fontRenderer.trimStringToWidth(name,29),slot.xPos+1,slot.yPos+6,0x958065,.48f);
            }
        }
        drawGrid(mouseX,mouseY);
        long weight=CarryWeight.carried(player),capacity=CarryWeight.capacity(player);boolean over=weight>capacity;
        text(ItemMetrics.kg(weight)+" / "+ItemMetrics.kg(capacity)+" "+I18n.format("metroport.inventory.kg"),9,208,over?0xed6b56:TEXT);
        panel(8,221,148,3,0xff151514,0xff151514);
        int fill=(int)Math.min(146,146.0*weight/Math.max(1,capacity));
        drawRect(guiLeft+9,guiTop+222,guiLeft+9+fill,guiTop+223,over?0xffdf5546:ORANGE);
        if((recovery==null || !recovery.visible) && (overflow==null || !overflow.visible)){
            int x=170;for(ItemRarity rarity:ItemRarity.values()){
                drawRect(guiLeft+x,guiTop+211,guiLeft+x+18,guiTop+214,0xff000000|rarity.color);x+=23;
            }
        }
    }
    private void drawGrid(int mouseX,int mouseY){
        for(int y=0;y<8;y++)for(int x=0;x<8;x++)panel(GRID_X+x*CELL,GRID_Y+y*CELL,CELL,CELL,0xff514b42,0xff1c1d1b);
        PackingGrid grid=container().grid();
        for(int i=0;i<27;i++){
            int position=grid.position(i);ItemStack stack=player.inventory.getStackInSlot(i+9);
            if(position<0||stack.isEmpty())continue;
            int cell=PackingGrid.cell(position),x=GRID_X+cell%8*CELL,y=GRID_Y+cell/8*CELL;
            int w=grid.width(i)*CELL,h=grid.height(i)*CELL,rgb=ItemRarity.of(stack).color;
            panel(x,y,w,h,0xff000000|rgb,0xff252622);
            drawRect(guiLeft+x+1,guiTop+y+1,guiLeft+x+w-1,guiTop+y+h-1,0x22000000|rgb);
            float scale=Math.min(2.4f,Math.max(.7f,(Math.min(w,h)-4)/16f));
            GlStateManager.pushMatrix();
            GlStateManager.translate(guiLeft+x+(w-16*scale)/2,guiTop+y+(h-16*scale)/2,0);
            GlStateManager.scale(scale,scale,1);
            net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(stack,0,0);
            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();GlStateManager.popMatrix();
            if(stack.getCount()>1)small(Integer.toString(stack.getCount()),x+w-11,y+h-7,0xffffff,.65f);
            if(stack.isItemStackDamageable()){
                int remaining=Math.max(0,w-2-(w-2)*stack.getItemDamage()/Math.max(1,stack.getMaxDamage()));
                drawRect(guiLeft+x+1,guiTop+y+h-2,guiLeft+x+1+remaining,guiTop+y+h-1,0xff75a568);
            }
        }
        ItemStack cursor=player.inventory.getItemStack();int cell=gridCell(mouseX,mouseY);
        if(!cursor.isEmpty() && cell>=0){
            int record=grid.recordAt(cell),anchor=record>=0?PackingGrid.cell(grid.position(record)):cell;
            int w=cursorRotated?ItemMetrics.height(cursor):ItemMetrics.width(cursor),h=cursorRotated?ItemMetrics.width(cursor):ItemMetrics.height(cursor);
            boolean fits=grid.canPlace(ItemMetrics.width(cursor),ItemMetrics.height(cursor),PackingGrid.encode(anchor,cursorRotated),record);
            int x=GRID_X+anchor%8*CELL,y=GRID_Y+anchor/8*CELL;
            drawRect(guiLeft+x,guiTop+y,guiLeft+Math.min(GRID_X+128,x+w*CELL),guiTop+Math.min(GRID_Y+128,y+h*CELL),fits?0x6675b96a:0x66df5546);
        }
        small(I18n.format("metroport.inventory.rotate"),170,172,TEXT,.65f);
    }

}
