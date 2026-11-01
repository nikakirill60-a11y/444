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
        render(id,context,reload,GunRenderPoses.handAnchor(id,false),GunRenderPoses.handAnchor(id,true));
    }
    public static void render(String id,TransformType context,float reload,float[] trigger,float[] supportAnchor) {
        if(context != TransformType.FIRST_PERSON_LEFT_HAND && context != TransformType.FIRST_PERSON_RIGHT_HAND) return;
        Minecraft mc = Minecraft.getMinecraft();
        AbstractClientPlayer player = mc.player;
        if(player == null || player.isInvisible() || player.isSpectator()) return;
        int previousTexture=org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL11.GL_TEXTURE_BINDING_2D);
        boolean left = context == TransformType.FIRST_PERSON_LEFT_HAND;
        boolean alex = "slim".equals(player.getSkinType());
        if(alex && slim == null) slim = new ModelPlayer(0, true);
        if(!alex && classic == null) classic = new ModelPlayer(0, false);
        ModelPlayer skin = alex ? slim : classic;
        mc.getTextureManager().bindTexture(player.getLocationSkin());
        GlStateManager.color(1,1,1,1);
        try {
            arm(id,skin, player, alex, left, false, trigger, reload);
            if(GunHandPose.usesSupport(left, player.getPrimaryHand() == EnumHandSide.LEFT,
                    player.getHeldItemMainhand().isEmpty(), player.getHeldItemOffhand().isEmpty())) {
                arm(id,skin, player, alex, !left, true, supportAnchor, reload);
            }
        } finally {
            GlStateManager.bindTexture(previousTexture);
            GlStateManager.color(1,1,1,1);
        }
    }
    private static void arm(String id,ModelPlayer skin, AbstractClientPlayer player, boolean alex, boolean left,
                            boolean support, float[] anchor, float reload) {
        ModelRenderer base = left ? skin.bipedLeftArm : skin.bipedRightArm;
        ModelRenderer sleeve = left ? skin.bipedLeftArmwear : skin.bipedRightArmwear;
        // Reset the vanilla shoulder offset: this renderer is wrist-anchored in gun space.
        reset(base); reset(sleeve);
        float[] wrist = GunHandPose.wrist(anchor, support, support && "revolver".equals(id) ? !left : left, reload);
        javax.vecmath.Matrix4f matrix = GunHandPose.armMatrix(id,wrist, left, support, alex);
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
            if(left && !ru.metrocraft.port.inventory.WatchEquipment.get(player).isEmpty()) {
                WatchRenderer.onArm(alex,player.world.getWorldTime(),ru.metrocraft.port.inventory.WatchEquipment.reading(player));
                mcSkin(player);
            }
        } finally { GlStateManager.popMatrix(); }
    }
    private static void mcSkin(AbstractClientPlayer player){Minecraft.getMinecraft().getTextureManager().bindTexture(player.getLocationSkin());}
    private static void reset(ModelRenderer arm) {
        arm.setRotationPoint(0,0,0);
        arm.rotateAngleX=arm.rotateAngleY=arm.rotateAngleZ=0;
        arm.showModel=true; arm.isHidden=false;
    }
}
