package ru.metrocraft.port.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import ru.metrocraft.port.inventory.*;

/** Compact cards: draw only information that fits the original item rectangle. */
public final class ItemCards {
    private static void line(String text,int x,int y,int width,float scale,int color){
        if(text.isEmpty()||width<2)return;
        net.minecraft.client.gui.FontRenderer font=Minecraft.getMinecraft().fontRenderer;
        String fit=font.trimStringToWidth(text,Math.max(1,(int)(width/scale)));
        GlStateManager.pushMatrix();GlStateManager.translate(x,y,0);GlStateManager.scale(scale,scale,1);
        font.drawString(fit,0,0,color);GlStateManager.popMatrix();
    }
    public static void draw(ItemStack stack,EntityPlayer player,int x,int y,int w,int h,boolean icon){
        if(stack.isEmpty())return;
        CompactInventoryLayout.Card layout=new CompactInventoryLayout.Card(w,h);
        int rgb=ItemRarity.of(stack).color;
        Gui.drawRect(x,y,x+w,y+h,0xff000000|rgb);Gui.drawRect(x+1,y+1,x+w-1,y+h-1,0xff242522);
        if(icon){
            int size=layout.iconSize;
            int top=layout.header+(h-layout.header-layout.footer-size)/2;
            GlStateManager.pushMatrix();GlStateManager.translate(x+(w-size)/2f,y+top,0);GlStateManager.scale(size/16f,size/16f,1);
            RenderHelper.enableGUIStandardItemLighting();Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack,0,0);
            RenderHelper.disableStandardItemLighting();GlStateManager.popMatrix();GlStateManager.color(1,1,1,1);
        }
        if(layout.name){
            ItemCardData data=ItemCardData.of(stack,player);
            line(data.name,x+2,y+2,w-4,.5f,0xd2cec5);
            if(layout.id)line(data.id,x+2,y+8,w-4,.5f,0x8b8b85);
            line(data.weight+" "+I18n.format("metroport.inventory.kg"),x+2,y+h-7,layout.stats?w/2-2:w-4,.5f,0xa9aaa1);
            if(layout.stats){
                line(data.primary,x+w/2,y+h-14,w/2-2,.5f,0xd0d1c9);
                line(data.secondary,x+w/2,y+h-7,w/2-2,.5f,0xa9aaa1);
            }
        }
        if(stack.getCount()>1)line(Integer.toString(stack.getCount()),x+Math.max(1,w-12),y+h-(layout.name?14:6),Math.min(11,w-2),.55f,0xffffff);
        if(stack.isItemStackDamageable()){
            int remaining=Math.max(0,w-2-(w-2)*stack.getItemDamage()/Math.max(1,stack.getMaxDamage()));
            Gui.drawRect(x+1,y+h-2,x+1+remaining,y+h-1,0xff75a568);
        }
    }
    private ItemCards(){}
}
