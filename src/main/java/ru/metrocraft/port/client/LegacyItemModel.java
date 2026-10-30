package ru.metrocraft.port.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.tuple.Pair;
import javax.vecmath.Matrix4f;
import java.util.Collections;
import java.util.List;

/** Keep crisp original GUI icons; render the actual Turbo geometry in hands/on the ground. */
public final class LegacyItemModel implements IBakedModel {
    private final IBakedModel icon;
    public LegacyItemModel(IBakedModel icon) { this.icon = icon; }
    @Override public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) { return Collections.emptyList(); }
    @Override public boolean isAmbientOcclusion() { return false; }
    @Override public boolean isGui3d() { return true; }
    @Override public boolean isBuiltInRenderer() { return true; }
    @Override public TextureAtlasSprite getParticleTexture() { return icon.getParticleTexture(); }
    @Override public ItemCameraTransforms getItemCameraTransforms() { return icon.getItemCameraTransforms(); }
    @Override public ItemOverrideList getOverrides() { return ItemOverrideList.NONE; }
    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType type) {
        Pair<? extends IBakedModel, Matrix4f> transform = icon.handlePerspective(type);
        if (type == ItemCameraTransforms.TransformType.GUI) return transform;
        return Pair.of(this, transform.getRight());
    }
}
