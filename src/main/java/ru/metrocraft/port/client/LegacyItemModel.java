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
    private final String id;
    private final boolean gun;
    private final boolean guiIcon;
    public LegacyItemModel(String id, boolean gun, IBakedModel icon) { this(id, gun, icon, true); }
    public LegacyItemModel(String id, boolean gun, IBakedModel icon, boolean guiIcon) {
        this.id = id; this.gun = gun; this.icon = icon; this.guiIcon = guiIcon;
    }
    @Override public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) { return Collections.emptyList(); }
    @Override public boolean isAmbientOcclusion() { return false; }
    @Override public boolean isGui3d() { return true; }
    @Override public boolean isBuiltInRenderer() { return true; }
    @Override public TextureAtlasSprite getParticleTexture() { return icon.getParticleTexture(); }
    @Override public ItemCameraTransforms getItemCameraTransforms() { return icon.getItemCameraTransforms(); }
    @Override public ItemOverrideList getOverrides() { return ItemOverrideList.NONE; }
    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType type) {
        if (guiIcon && type == ItemCameraTransforms.TransformType.GUI) return icon.handlePerspective(type);
        if (gun) return Pair.of(this, GunRenderPoses.matrix(id, type));
        Pair<? extends IBakedModel, Matrix4f> transform = icon.handlePerspective(type);
        return Pair.of(this, transform.getRight());
    }
}
