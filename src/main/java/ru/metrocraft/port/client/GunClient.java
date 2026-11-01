package ru.metrocraft.port.client;

import net.minecraft.client.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import ru.metrocraft.port.*;

@Mod.EventBusSubscriber(modid=MetroPort.ID,value=Side.CLIENT)
public final class GunClient {
    public static float aim;
    private static float recoverPitch,recoverYaw;
    public static void shot(String id,float pitch,float yaw){
        Minecraft mc=Minecraft.getMinecraft();
        if(mc.player==null || mc.world==null || !Float.isFinite(pitch) || !Float.isFinite(yaw))return;
        ItemStack stack=mc.player.getHeldItemMainhand();
        if(!(stack.getItem() instanceof GunItem) || !((GunItem)stack.getItem()).getDefinition().id.equals(id))return;
        float up=Math.min(20,Math.max(0,pitch))*.5F, side=Math.max(-10,Math.min(10,yaw));
        mc.player.rotationPitch-=up;mc.player.rotationYaw+=side;recoverPitch+=up;recoverYaw+=side;
    }
    @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event){
        if(event.phase!=TickEvent.Phase.END)return;
        Minecraft mc=Minecraft.getMinecraft();
        if(mc.player==null || mc.world==null){aim=0;recoverPitch=0;recoverYaw=0;return;}
        ItemStack stack=mc.player.getHeldItemMainhand();
        Definition d=stack.getItem() instanceof GunItem?((GunItem)stack.getItem()).getDefinition():null;
        if(d!=null&&d.mcmodAir&&ClientEvents.pumping()&&mc.player.ticksExisted%8==0)MetroPort.NETWORK.sendToServer(new ru.metrocraft.port.mcmod.GunActionPacket(2,0,0,0));
        if(stack.getItem() instanceof ru.metrocraft.port.blackrazid.weapons.BlackrazidGunItem
                && ((ru.metrocraft.port.blackrazid.weapons.BlackrazidGunItem)stack.getItem()).entry.airCapacity>0
                && ClientEvents.pumping() && mc.player.ticksExisted%8==0)
            MetroPort.NETWORK.sendToServer(new ru.metrocraft.port.mcmod.GunActionPacket(2,0,0,0));
        aim=Math.max(0,Math.min(1,aim+(d!=null&&ClientEvents.aiming()?.2F:-.2F)));
        float dp=Math.min(recoverPitch,d==null?.5F:Math.max(.05F,d.decreaseRecoil*.1F));
        float dy=Math.copySign(Math.min(Math.abs(recoverYaw),d==null?.1F:Math.max(.05F,d.decreaseYaw*.1F)),recoverYaw);
        mc.player.rotationPitch=Math.max(-90,Math.min(90,mc.player.rotationPitch+dp));mc.player.rotationYaw-=dy;
        recoverPitch-=dp;recoverYaw-=dy;
    }
    public static String scope(ItemStack gun){
        if(!(gun.getItem() instanceof GunItem))return "None";
        if(gun.getItem() instanceof ru.metrocraft.port.mcmod.McmodAkItem)return ru.metrocraft.port.mcmod.McmodAkItem.module(gun,"modScope")==1?"svd":"None";
        String id=gun.hasTagCompound()?gun.getTagCompound().getCompoundTag(AttachmentItem.TAG).getString("scope"):"";
        net.minecraft.item.Item item=MetroPort.ITEMS.get(id);
        if(item instanceof AttachmentItem && java.util.Arrays.asList(((GunItem)gun.getItem()).getDefinition().allowedAttachments).contains(id))
            return ((AttachmentItem)item).getDefinition().zoomOverlay;
        return ((GunItem)gun.getItem()).getDefinition().scope;
    }
    @SubscribeEvent public static void crosshair(RenderGameOverlayEvent.Pre event){
        Minecraft mc=Minecraft.getMinecraft();
        if(event.getType()==RenderGameOverlayEvent.ElementType.CROSSHAIRS && mc.player!=null && aim>.9F
                && !scope(mc.player.getHeldItemMainhand()).equalsIgnoreCase("None"))event.setCanceled(true);
    }
    @SubscribeEvent public static void overlay(RenderGameOverlayEvent.Post event){
        Minecraft mc=Minecraft.getMinecraft();if(event.getType()!=RenderGameOverlayEvent.ElementType.ALL || mc.player==null)return;
        for(ItemStack armor:mc.player.getArmorInventoryList())if(armor.getItem() instanceof MetroArmor
                && ((MetroArmor)armor.getItem()).getDefinition().armorOverlay.equals("pnv")){
            int width=event.getResolution().getScaledWidth(),height=event.getResolution().getScaledHeight();
            GlStateManager.enableBlend();GlStateManager.color(1,1,1,1);
            mc.getTextureManager().bindTexture(new ResourceLocation("metroport:textures/scopes/pnv.png"));
            Gui.drawModalRectWithCustomSizedTexture(0,0,0,0,width,height,width,height);
            GlStateManager.disableBlend();break;
        }
        if(aim<.9F)return;
        String scope=scope(mc.player.getHeldItemMainhand());if(!scope.equals("svd")&&!scope.equals("aim1"))return;
        int w=event.getResolution().getScaledWidth(),h=event.getResolution().getScaledHeight(),left=(w-h)/2;
        Gui.drawRect(0,0,Math.max(0,left),h,0xff000000);Gui.drawRect(Math.min(w,left+h),0,w,h,0xff000000);
        GlStateManager.enableBlend();GlStateManager.color(1,1,1,1);
        mc.getTextureManager().bindTexture(new ResourceLocation("metroport:textures/scopes/"+scope+".png"));
        Gui.drawModalRectWithCustomSizedTexture(left,0,0,0,h,h,h,h);
        GlStateManager.disableBlend();
    }
    @SubscribeEvent public static void tooltip(net.minecraftforge.event.entity.player.ItemTooltipEvent event){
        if(ru.metrocraft.port.mechanics.NoDrop.marked(event.getItemStack()))event.getToolTip().add("§a"+net.minecraft.client.resources.I18n.format("metroport.nodrop.name"));
    }
}
