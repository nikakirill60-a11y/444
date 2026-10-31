package ru.metrocraft.port.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.exodus.ExodusContent;
import ru.metrocraft.port.exodus.ExodusMutant;

@Mod.EventBusSubscriber(modid = MetroPort.ID, value = Side.CLIENT)
public final class ExodusClient {
    @SubscribeEvent public static void models(ModelRegistryEvent event) {
        for (ExodusContent.Entry entry : ExodusContent.entries) if (entry.kind.equals("mutant")) {
            ModelLoader.registerItemVariants(Items.SPAWN_EGG, new ResourceLocation(MetroPort.ID, entry.id));
        }
    }
    public static void registerEntities() {
        add(ExodusMutant.Shrimp.class);
        add(ExodusMutant.Guard.class);
    }
    private static <T extends ExodusMutant> void add(Class<T> type) {
        RenderingRegistry.registerEntityRenderingHandler(type, manager -> new RenderLiving<T>(manager, new MutantModel(), .55F) {
            @Override protected ResourceLocation getEntityTexture(T entity) { return TextureMap.LOCATION_BLOCKS_TEXTURE; }
        });
    }
    /** Source RP geometry is static: no invented limb skeleton or animation. */
    private static final class MutantModel extends ModelBase {
        private final ItemStack display = new ItemStack(Items.SPAWN_EGG);
        @Override public void render(Entity entity, float swing, float amount, float age, float yaw, float pitch, float scale) {
            String id = ((ExodusMutant) entity).modelId();
            Minecraft mc = Minecraft.getMinecraft();
            IBakedModel model = mc.getRenderItem().getItemModelMesher().getModelManager()
                    .getModel(new ModelResourceLocation(MetroPort.ID + ":" + id, "inventory"));
            GlStateManager.pushMatrix();
            // RenderLiving uses Y-down model coordinates and places the feet at 24 model pixels.
            GlStateManager.translate(0, 24 * scale, 0);
            GlStateManager.scale(1, -1, -1);
            GlStateManager.translate(0, .5F, 0);
            // NONE: do not apply the RP's item GUI/HEAD transformations to a living creature.
            mc.getRenderItem().renderItem(display, model);
            GlStateManager.popMatrix();
        }
    }
}
