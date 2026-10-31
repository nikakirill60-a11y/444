package ru.metrocraft.port.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.EnumHandSide;

/** First-person only. Vanilla Steve/Alex skin meshes, not a replacement skin or fake gun texture. */
public final class GunHands {
    private static ModelPlayer classic, slim;
    private static final java.nio.FloatBuffer MATRIX = org.lwjgl.BufferUtils.createFloatBuffer(16);
    private GunHands() {}
    public static void render(String id, TransformType context, float reload) {
        if(context != TransformType.FIRST_PERSON_LEFT_HAND && context != TransformType.FIRST_PERSON_RIGHT_HAND) return;
        Minecraft mc = Minecraft.getMinecraft();
        AbstractClientPlayer player = mc.player;
        if(player == null || player.isInvisible() || player.isSpectator()) return;
        boolean left = context == TransformType.FIRST_PERSON_LEFT_HAND;
        boolean alex = "slim".equals(player.getSkinType());
        if(alex && slim == null) slim = new ModelPlayer(0, true);
        if(!alex && classic == null) classic = new ModelPlayer(0, false);
        ModelPlayer skin = alex ? slim : classic;
        mc.getTextureManager().bindTexture(player.getLocationSkin());
        GlStateManager.color(1,1,1,1);
        try {
            arm(skin, player, alex, left, false, GunRenderPoses.handAnchor(id, false), reload);
            if(GunHandPose.usesSupport(left, player.getPrimaryHand() == EnumHandSide.LEFT,
                    player.getHeldItemMainhand().isEmpty(), player.getHeldItemOffhand().isEmpty())) {
                arm(skin, player, alex, !left, true, GunRenderPoses.handAnchor(id, true), reload);
            }
        } finally {
            mc.getTextureManager().bindTexture(new net.minecraft.util.ResourceLocation(LegacyItemRenderer.info(id).texture));
            GlStateManager.color(1,1,1,1);
        }
    }
    private static void arm(ModelPlayer skin, AbstractClientPlayer player, boolean alex, boolean left,
                            boolean support, float[] anchor, float reload) {
        ModelRenderer base = left ? skin.bipedLeftArm : skin.bipedRightArm;
        ModelRenderer sleeve = left ? skin.bipedLeftArmwear : skin.bipedRightArmwear;
        // Reset the vanilla shoulder offset: this renderer is wrist-anchored in gun space.
        reset(base); reset(sleeve);
        float[] wrist = GunHandPose.wrist(anchor, support, support ? !left : left, reload);
        javax.vecmath.Matrix4f matrix = GunHandPose.armMatrix(wrist, left, support, alex);
        MATRIX.clear();
        for(int column=0; column<4; column++) for(int row=0; row<4; row++)
            MATRIX.put(matrix.getElement(row, column));
        MATRIX.flip();
        GlStateManager.pushMatrix();
        try {
            GlStateManager.multMatrix(MATRIX);
            base.render(1/16F);
            if(player.isWearing(left ? EnumPlayerModelParts.LEFT_SLEEVE : EnumPlayerModelParts.RIGHT_SLEEVE))
                sleeve.render(1/16F);
        } finally { GlStateManager.popMatrix(); }
    }
    private static void reset(ModelRenderer arm) {
        arm.setRotationPoint(0,0,0);
        arm.rotateAngleX=arm.rotateAngleY=arm.rotateAngleZ=0;
        arm.showModel=true; arm.isHidden=false;
    }
}
