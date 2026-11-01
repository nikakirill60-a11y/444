package ru.metrocraft.port.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.inventory.WatchEquipment;
import ru.metrocraft.port.mcmod.WatchItem;
@Mod.EventBusSubscriber(modid=MetroPort.ID,value=Side.CLIENT)
public final class WatchClient implements LayerRenderer<AbstractClientPlayer> {
    private final RenderPlayer renderer;
    public WatchClient(RenderPlayer renderer){this.renderer=renderer;}
    public static void receive(int dimension,int id,ItemStack stack,int level){
        Minecraft.getMinecraft().addScheduledTask(()->{
            Minecraft mc=Minecraft.getMinecraft();if(mc.world==null || mc.world.provider.getDimension()!=dimension)return;
            Entity entity=mc.world.getEntityByID(id);
            if(entity instanceof EntityPlayer){WatchEquipment.receive((EntityPlayer)entity,stack);WatchEquipment.reading((EntityPlayer)entity,level);}
        });
    }
    @SubscribeEvent public static void hud(RenderGameOverlayEvent.Post event){
        Minecraft mc=Minecraft.getMinecraft();
        if(event.getType()!=RenderGameOverlayEvent.ElementType.ALL || mc.player==null || mc.gameSettings.hideGUI || mc.player.isSpectator())return;
        ItemStack watch=WatchEquipment.get(mc.player);
        if(watch.isEmpty() && WatchEquipment.accepts(mc.player.getHeldItemMainhand()))watch=mc.player.getHeldItemMainhand();
        if(watch.isEmpty() && WatchEquipment.accepts(mc.player.getHeldItemOffhand()))watch=mc.player.getHeldItemOffhand();
        if(watch.isEmpty())return;
        boolean timeOnly=watch.getItem() instanceof ru.metrocraft.port.blackrazid.BlackrazidWatchItem;
        int y=event.getResolution().getScaledHeight()-64;boolean danger=WatchEquipment.reading(mc.player)>0;
        mc.fontRenderer.drawStringWithShadow(I18n.format("metroport.watch.time",WatchItem.time(mc.world.getWorldTime())),8,y,0xDAE9CE);
        if(!timeOnly)mc.fontRenderer.drawStringWithShadow(I18n.format("metroport.watch.radiation",I18n.format(danger?"metroport.watch.danger":"metroport.watch.safe")),8,y+11,danger?0xFF6655:0x9AC87B);
    }
    @Override public void doRenderLayer(AbstractClientPlayer player,float swing,float amount,float partial,float age,float yaw,float pitch,float scale){
        if(WatchEquipment.get(player).isEmpty() || player.isInvisible() || player.isSpectator())return;
        GlStateManager.pushMatrix();
        try{
            if(player.isSneaking())GlStateManager.translate(0,.2,0);
            renderer.getMainModel().bipedLeftArm.postRender(1/16F);
            if(WatchEquipment.get(player).getItem() instanceof ru.metrocraft.port.blackrazid.BlackrazidWatchItem)
                BlackrazidWatchRenderer.onArm("slim".equals(player.getSkinType()),player.world.getWorldTime());
            else WatchRenderer.onArm("slim".equals(player.getSkinType()),player.world.getWorldTime(),WatchEquipment.reading(player));
        }finally{GlStateManager.popMatrix();}
    }
    @Override public boolean shouldCombineTextures(){return false;}
}
