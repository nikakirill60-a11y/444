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
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0.5, 0.5);
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(model.texture));
        GlStateManager.color(1, 1, 1, 1);
        LegacyGeometry.get(id).renderAll(model.scale / 16F);
        GlStateManager.popMatrix();
    }
}
