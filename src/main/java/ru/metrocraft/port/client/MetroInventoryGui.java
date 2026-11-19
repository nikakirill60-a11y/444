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
    private GridViewport viewport=new GridViewport(3,3,CELL);
    private int scrollDrag;
    private static final int GRID_X=170,GRID_Y=40,CELL=40;
    private static final int ORANGE=0xffb58249,TEXT=0xc7bfb1;
    public MetroInventoryGui(EntityPlayer player){super(new MetroContainer(player));this.player=player;xSize=312;ySize=230;}
    @Override public void initGui(){
        xSize=Math.min(520,Math.max(312,width-8));ySize=Math.min(440,Math.max(230,height-8));
        int oldX=viewport.column,oldY=viewport.row;
        viewport=new GridViewport((xSize-180)/CELL,(ySize-100)/CELL,CELL);viewport.scroll(oldX,oldY);
        for(int i=36;i<42;i++)inventorySlots.getSlot(i).yPos=ySize-46;
        super.initGui();recovery=new net.minecraft.client.gui.GuiButton(711,guiLeft+170,guiTop+ySize-26,132,18,I18n.format("metroport.inventory.recover"));buttonList.add(recovery);
        overflow=new net.minecraft.client.gui.GuiButton(712,guiLeft+170,guiTop+ySize-26,132,18,"");buttonList.add(overflow);
    }
    @Override public void updateScreen(){
        super.updateScreen();
        int count=overflowCount();boolean legacy=ru.metrocraft.port.blackrazid.BackpackContainer.valid(player,2);
        if(overflow!=null){overflow.visible=count>0;overflow.displayString=I18n.format("metroport.inventory.overflow",count);}
        if(recovery!=null)recovery.visible=legacy && count==0;
    }
    @Override protected void actionPerformed(net.minecraft.client.gui.GuiButton button)throws java.io.IOException{
        if(button==overflow){cursorRotated=false;sendGrid(0,0,1);}
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
        return viewport.at(mouseX-guiLeft-GRID_X,mouseY-guiTop-GRID_Y);
    }
    @Override public void handleMouseInput()throws java.io.IOException{
        super.handleMouseInput();int wheel=org.lwjgl.input.Mouse.getEventDWheel();
        int x=org.lwjgl.input.Mouse.getEventX()*width/mc.displayWidth;
        int y=height-org.lwjgl.input.Mouse.getEventY()*height/mc.displayHeight-1;
        if(wheel!=0 && gridCell(x,y)>=0){int direction=wheel>0?-1:1;viewport.scroll(isShiftKeyDown()?direction:0,isShiftKeyDown()?0:direction);}
    }
    private boolean scrollbar(int x,int y){
        int dx=x-guiLeft-GRID_X,dy=y-guiTop-GRID_Y;
        if(dx>=0&&dx<viewport.columns*CELL&&dy>=viewport.rows*CELL+1&&dy<=viewport.rows*CELL+7){scrollDrag=1;dragScroll(x,y);return true;}
        if(dy>=0&&dy<viewport.rows*CELL&&dx>=viewport.columns*CELL+1&&dx<=viewport.columns*CELL+7){scrollDrag=2;dragScroll(x,y);return true;}
        return false;
    }
    private void dragScroll(int x,int y){
        if(scrollDrag==1)viewport.scroll((int)Math.round((x-guiLeft-GRID_X)/(double)(viewport.columns*CELL)*(8-viewport.columns))-viewport.column,0);
        if(scrollDrag==2)viewport.scroll(0,(int)Math.round((y-guiTop-GRID_Y)/(double)(viewport.rows*CELL)*(8-viewport.rows))-viewport.row);
    }
    private void sendGrid(int cell,int button,int action){
        ru.metrocraft.port.MetroPort.NETWORK.sendToServer(new GridClickPacket(inventorySlots.windowId,container().gridRevision,cell,button,cursorRotated,action));
    }
    @Override protected void mouseClicked(int x,int y,int button)throws java.io.IOException{
        if(button==0 && scrollbar(x,y))return;
        int cell=gridCell(x,y);
        if(cell>=0){
            if(button==0||button==1){
                if(player.inventory.getItemStack().isEmpty()){
                    PackingGrid grid=container().grid();int record=grid.recordAt(cell);
                    cursorRotated=record>=0 && PackingGrid.rotated(grid.position(record));
                }
                sendGrid(cell,button,0);
            }
            return;
        }
        if(player.inventory.getItemStack().isEmpty())cursorRotated=false;
        super.mouseClicked(x,y,button);
    }
    @Override protected void mouseReleased(int x,int y,int button){if(scrollDrag!=0){scrollDrag=0;return;}if(gridCell(x,y)<0)super.mouseReleased(x,y,button);}
    @Override protected void mouseClickMove(int x,int y,int button,long elapsed){if(scrollDrag!=0){dragScroll(x,y);return;}if(gridCell(x,y)<0)super.mouseClickMove(x,y,button,elapsed);}
    @Override protected void keyTyped(char typed,int code)throws java.io.IOException{
        if(code==org.lwjgl.input.Keyboard.KEY_R && !player.inventory.getItemStack().isEmpty()){cursorRotated=!cursorRotated;return;}
        super.keyTyped(typed,code);
    }
    private static String label(int slot){
        switch(slot){
            case 0:return "metroport.inventory.legacy_bag";
            case 1:return "metroport.inventory.helmet";case 2:return "metroport.inventory.armor";
            case 5:return "metroport.inventory.offhand";case 33:return "metroport.inventory.weapon1";
            case 34:return "metroport.inventory.weapon2";case 35:return "metroport.inventory.melee";
            case 42:return "metroport.nvg.slot";case 43:return "metroport.watch.slot";
            case 44:return "metroport.blackrazid.lamp_slot";case 45:return "metroport.blackrazid.bag_slot";
            default:return null;
        }
    }
    @Override protected void drawGuiContainerForegroundLayer(int x,int y){
        for(Slot slot:inventorySlots.inventorySlots){
            if(!slot.isEnabled()||!slot.getHasStack())continue;
            if(slot.slotNumber==33||slot.slotNumber==34)ItemCards.draw(slot.getStack(),player,8,slot.slotNumber==33?132:169,148,32,true);
            else ItemCards.draw(slot.getStack(),player,slot.xPos-2,slot.yPos-2,24,28,true);
        }
    }
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
        panel(5,5,155,20,0xff39342d,0xff1a1918);panel(165,5,xSize-170,20,0xff39342d,0xff1a1918);
        text(I18n.format("metroport.inventory.equipment"),10,11,TEXT);text(I18n.format("metroport.inventory.storage"),170,11,TEXT);
        drawRect(guiLeft+162,guiTop+29,guiLeft+163,guiTop+ySize-8,ORANGE);
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
                drawRect(guiLeft+x,guiTop+ySize-19,guiLeft+x+15,guiTop+ySize-16,0xff000000|rarity.color);x+=18;
            }
        }
    }
    private void drawGrid(int mouseX,int mouseY){
        for(int y=0;y<viewport.rows;y++)for(int x=0;x<viewport.columns;x++)panel(GRID_X+x*CELL,GRID_Y+y*CELL,CELL,CELL,0xff514b42,0xff1c1d1b);
        PackingGrid grid=container().grid();
        int factor=new net.minecraft.client.gui.ScaledResolution(mc).getScaleFactor();
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
        org.lwjgl.opengl.GL11.glScissor((guiLeft+GRID_X)*factor,mc.displayHeight-(guiTop+GRID_Y+viewport.rows*CELL)*factor,viewport.columns*CELL*factor,viewport.rows*CELL*factor);
        try{
            for(int i=0;i<27;i++){
                int position=grid.position(i);ItemStack stack=player.inventory.getStackInSlot(i+9);
                if(position<0||stack.isEmpty())continue;
                int cell=PackingGrid.cell(position),x=GRID_X+viewport.x(cell),y=GRID_Y+viewport.y(cell);
                ItemCards.draw(stack,player,guiLeft+x,guiTop+y,grid.width(i)*CELL,grid.height(i)*CELL,true);
            }
            ItemStack cursor=player.inventory.getItemStack();int cell=gridCell(mouseX,mouseY);
            if(!cursor.isEmpty() && cell>=0){
                int record=grid.recordAt(cell),anchor=record>=0?PackingGrid.cell(grid.position(record)):cell;
                int w=cursorRotated?ItemMetrics.height(cursor):ItemMetrics.width(cursor),h=cursorRotated?ItemMetrics.width(cursor):ItemMetrics.height(cursor);
                boolean fits=grid.canPlace(ItemMetrics.width(cursor),ItemMetrics.height(cursor),PackingGrid.encode(anchor,cursorRotated),record);
                int x=GRID_X+viewport.x(anchor),y=GRID_Y+viewport.y(anchor);
                drawRect(guiLeft+x,guiTop+y,guiLeft+x+w*CELL,guiTop+y+h*CELL,fits?0x3375b96a:0x33df5546);
            }
        }finally{org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);}
        int w=viewport.columns*CELL,h=viewport.rows*CELL;
        panel(GRID_X,GRID_Y+h+2,w,4,0xff39342d,0xff1a1918);
        drawRect(guiLeft+GRID_X+viewport.column*w/8,guiTop+GRID_Y+h+2,guiLeft+GRID_X+(viewport.column+viewport.columns)*w/8,guiTop+GRID_Y+h+6,ORANGE);
        panel(GRID_X+w+2,GRID_Y,4,h,0xff39342d,0xff1a1918);
        drawRect(guiLeft+GRID_X+w+2,guiTop+GRID_Y+viewport.row*h/8,guiLeft+GRID_X+w+6,guiTop+GRID_Y+(viewport.row+viewport.rows)*h/8,ORANGE);
        small(I18n.format("metroport.inventory.scroll"),170,GRID_Y+h+9,TEXT,.48f);
    }
}
