package ru.metrocraft.port.client.gates;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import ru.metrocraft.port.client.LegacyObjModel;
import ru.metrocraft.port.gates.*;

public final class GateRenderer extends TileEntitySpecialRenderer<GateTile> {
    private final ModelGermo main = new ModelGermo();
    private final ModelGermo1 right = new ModelGermo1();
    private final ModelGermoLeft left = new ModelGermoLeft();
    private final ModelGermoRamka frame = new ModelGermoRamka();
    @Override public void render(GateTile tile, double x, double y, double z, float partial, int destroyStage, float alpha) {
        float p = tile.previous + (tile.progress - tile.previous) * partial;
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x + .5, y + (tile.type == GateType.HINGED ? 2 : 1.5), z + .5);
            GlStateManager.rotate(90 - tile.facing.getHorizontalAngle(), 0, 1, 0);
            GlStateManager.rotate(180, 0, 0, 1);
            GlStateManager.color(1, 1, 1, alpha);
            GlStateManager.enableRescaleNormal(); GlStateManager.disableCull();
            if (tile.type == GateType.HINGED) {
                bindTexture(new ResourceLocation("metroport:textures/gates/germa.png"));
                GlStateManager.translate(.426667, 0, -1.638717);
                LegacyObjModel.get("germaramka").render();
                GlStateManager.rotate(90 * p, 0, 1, 0);
                LegacyObjModel.get("germa").render();
            } else {
                bindTexture(new ResourceLocation("metroport:textures/gates/model_techne.png")); frame.render();
                if (tile.type.swing) {
                    GlStateManager.translate(0, 0, -2.5);
                    GlStateManager.rotate(90 * p, 0, 1, 0);
                    GlStateManager.translate(0, 0, 2.5);
                    bindTexture(new ResourceLocation("metroport:textures/gates/modelgermoleft.png")); left.render();
                } else {
                    GlStateManager.translate(0, -4 * p, 0);
                    if (tile.type == GateType.RIGHT) {
                        bindTexture(new ResourceLocation("metroport:textures/gates/modelgermo1.png")); right.render();
                    } else main.render();
                }
            }
        } finally {
            GlStateManager.enableCull(); GlStateManager.disableRescaleNormal(); GlStateManager.color(1, 1, 1, 1);
            GlStateManager.popMatrix();
        }
    }
    @Override public boolean isGlobalRenderer(GateTile tile) { return true; }
}
