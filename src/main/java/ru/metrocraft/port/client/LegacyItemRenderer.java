package ru.metrocraft.port.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import ru.metrocraft.port.ContentItem;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class LegacyItemRenderer extends TileEntityItemStackRenderer {
    public static final LegacyItemRenderer INSTANCE = new LegacyItemRenderer();
    public static final class Info { public String texture, kind; public float scale; }
    private static Map<String, Info> catalogue;
    public static Info info(String id) {
        if (catalogue == null) {
            try (InputStreamReader reader = new InputStreamReader(Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("metroport:geometry/catalogue.json")).getInputStream(), StandardCharsets.UTF_8)) {
                catalogue = new Gson().fromJson(reader, new TypeToken<Map<String, Info>>(){}.getType());
            } catch (Exception e) { throw new IllegalStateException(e); }
        }
        return catalogue.get(id);
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        String id = stack.getItem().getRegistryName().getPath();
        Info model = info(id);
        if (model == null) return;
        boolean gun=stack.getItem() instanceof ru.metrocraft.port.GunItem;
        net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType context=LegacyItemModel.context();
        boolean first=context==net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND
                || context==net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;
        if(gun && first && GunClient.aim>.9F && !GunClient.scope(stack).equalsIgnoreCase("None"))return;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0.5, 0.5);
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(model.texture));
        GlStateManager.color(1, 1, 1, 1);
        LegacyGeometry geometry=LegacyGeometry.get(id);
        if(gun){
            double now=Minecraft.getMinecraft().world==null?0:Minecraft.getMinecraft().world.getTotalWorldTime()+partialTicks;
            float kick=0,reload=0;
            if(stack.hasTagCompound()){
                double since=now-stack.getTagCompound().getLong("MetroLastShot");
                if(stack.getTagCompound().hasKey("MetroLastShot") && since>=0 && since<4)kick=(float)(1-since/4);
                int duration=stack.getTagCompound().getInteger("MetroReloadDuration");
                if(duration>0 && stack.getTagCompound().getInteger("MetroReloadTicks")>0){
                    float p=(float)((now-stack.getTagCompound().getLong("MetroReloadStart"))/duration);
                    reload=Math.max(0,Math.min(1,Math.min(p/.25F,(1-p)/.25F)));
                }
            }
            GlStateManager.translate(-kick*geometry.rig.gunSlideDistance*model.scale,geometry.rig.downReload*model.scale*reload,0);
            for(String group:geometry.groups.keySet()){
                if(group.startsWith("shootSprite"))continue;
                if(group.equals("defaultScopeModel")&&installed(stack,"scope")!=null)continue;
                if(group.equals("defaultBarrelModel")&&installed(stack,"barrel")!=null)continue;
                geometry.renderGroup(group,model.scale/16F);
            }
            mounted(stack,geometry,model);
            if(first) GunHands.render(id, context, reload);
        }else geometry.renderAll(model.scale/16F);
        GlStateManager.popMatrix();
    }
    private static String installed(ItemStack gun,String slot){
        if(!gun.hasTagCompound())return null;
        String id=gun.getTagCompound().getCompoundTag(ru.metrocraft.port.AttachmentItem.TAG).getString(slot);
        net.minecraft.item.Item item=ru.metrocraft.port.MetroPort.ITEMS.get(id);
        if(!(item instanceof ru.metrocraft.port.AttachmentItem))return null;
        ru.metrocraft.port.Definition d=((ru.metrocraft.port.AttachmentItem)item).getDefinition();
        return slot.equals(d.attachmentSlot)&&java.util.Arrays.asList(((ru.metrocraft.port.GunItem)gun.getItem()).getDefinition().allowedAttachments).contains(id)?id:null;
    }
    private static void mounted(ItemStack gun,LegacyGeometry geometry,Info model){
        for(String slot:new String[]{"scope","barrel","grip","stock"}){
            String id=installed(gun,slot);if(id==null)continue;
            ru.metrocraft.port.Definition d=((ru.metrocraft.port.AttachmentItem)ru.metrocraft.port.MetroPort.ITEMS.get(id)).getDefinition();
            float[] point=slot.equals("scope")?(d.zoom>=3?geometry.rig.scopeAttachPoint:geometry.rig.colAttachPoint):
                    slot.equals("barrel")?geometry.rig.barrelAttachPoint:slot.equals("grip")?geometry.rig.gripAttachPoint:geometry.rig.stockAttachPoint;
            Info attachment=info(id);if(point==null||attachment==null)continue;
            GlStateManager.pushMatrix();
            try{
                GlStateManager.translate(point[0]*model.scale,point[1]*model.scale,point[2]*model.scale);
                Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(attachment.texture));
                LegacyGeometry part=LegacyGeometry.get(id);
                part.renderAll(attachment.scale*part.rig.scaleAttachment/16F);
            }finally{GlStateManager.popMatrix();}
        }
    }
}
