package ru.metrocraft.port.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import ru.metrocraft.port.inventory.*;

/** Persistent information on the card itself, not a hover-only replacement. */
public final class ItemCards {
    private static void line(String text,int x,int y,int width,float scale,int color){
        if(text.isEmpty()||width<2)return;
        net.minecraft.client.gui.FontRenderer font=Minecraft.getMinecraft().fontRenderer;
        String fit=font.trimStringToWidth(text,Math.max(1,(int)(width/scale)));
        GlStateManager.pushMatrix();GlStateManager.translate(x,y,200);GlStateManager.scale(scale,scale,1);
        font.drawString(fit,0,0,color);GlStateManager.popMatrix();
    }
    public static void draw(ItemStack stack,EntityPlayer player,int x,int y,int w,int h,boolean icon){
        if(stack.isEmpty())return;ItemCardData data=ItemCardData.of(stack,player);
        int rgb=ItemRarity.of(stack).color;boolean compact=w<36||h<32;
        Gui.drawRect(x,y,x+w,y+h,0xff000000|rgb);Gui.drawRect(x+1,y+1,x+w-1,y+h-1,0xff242522);
        float title=compact?.28f:.6f,detail=compact?.22f:.44f,bottom=compact?.28f:.55f;
        line(data.name,x+2,y+2,w-4,title,0xd2cec5);
        line(data.id,x+2,y+(compact?5:9),w-4,detail,0x8b8b85);
        if(icon){
            int top=compact?8:15,bottomSpace=compact?7:17;
            float size=Math.max(6,Math.min(42,Math.min(w-5,h-top-bottomSpace)));
            GlStateManager.pushMatrix();GlStateManager.translate(x+(w-size)/2,y+top,150);GlStateManager.scale(size/16,size/16,1);
            RenderHelper.enableGUIStandardItemLighting();Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack,0,0);
            RenderHelper.disableStandardItemLighting();GlStateManager.popMatrix();GlStateManager.color(1,1,1,1);
        }
        String weight=data.weight+" "+I18n.format("metroport.inventory.kg");
        line(weight,x+2,y+h-(compact?4:7),compact?w-4:Math.max(w/2,w-80),bottom,0xa9aaa1);
        if(!compact){
            int statsWidth=Math.max(12,w/2-3);
            line(data.primary,x+w-statsWidth-2,y+h-15,statsWidth,.55f,0xd0d1c9);
            line(data.secondary,x+w-statsWidth-2,y+h-7,statsWidth,.44f,0xa9aaa1);
        }
        if(data.count>1)line("×"+data.count,x+w-15,y+(compact?8:17),14,compact?.3f:.55f,0xffffff);
    }
    private ItemCards(){}
}
