/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.RenderBlocks
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.IItemRenderer$ItemRenderType
 *  net.minecraftforge.client.IItemRenderer$ItemRendererHelper
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.GL11
 */
package com.flansmod.client.model;

import com.flansmod.client.FlansModClient;
import com.flansmod.client.FlansModResourceHandler;
import com.flansmod.client.TickHandlerClient;
import com.flansmod.client.model.EnumAnimationType;
import com.flansmod.client.model.GunAnimations;
import com.flansmod.client.model.ModelAttachment;
import com.flansmod.client.model.ModelGun;
import com.flansmod.client.model.ModelHand;
import com.flansmod.common.PlayerData;
import com.flansmod.common.PlayerHandler;
import com.flansmod.common.guns.AttachmentType;
import com.flansmod.common.guns.EnumFireMode;
import com.flansmod.common.guns.GunType;
import com.flansmod.common.guns.IScope;
import com.flansmod.common.guns.ItemBullet;
import com.flansmod.common.guns.ItemGun;
import com.flansmod.common.vector.Vector3f;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class RenderGun
implements IItemRenderer {
    public static boolean ShootSprite = true;
    private static TextureManager renderEngine;
    private static ModelHand model1;
    public static float smoothing;

    public RenderGun() {
        model1 = new ModelHand();
    }

    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
        switch (type) {
            case ENTITY: {
                if (!Minecraft.func_71410_x().field_71474_y.field_74347_j) {
                    return false;
                }
            }
            case EQUIPPED: 
            case EQUIPPED_FIRST_PERSON: {
                return item != null && item.func_77973_b() instanceof ItemGun && ((ItemGun)item.func_77973_b()).type.model != null;
            }
        }
        return false;
    }

    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
        return false;
    }

    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object ... data) {
        EntityLivingBase entity;
        if (!(item.func_77973_b() instanceof ItemGun)) {
            return;
        }
        RenderBlocks renderBlocks = (RenderBlocks)data[0];
        GunType gunType = ((ItemGun)item.func_77973_b()).type;
        if (gunType == null) {
            return;
        }
        ModelGun model = gunType.model;
        if (model == null) {
            return;
        }
        GunAnimations animations = type == IItemRenderer.ItemRenderType.ENTITY ? new GunAnimations() : FlansModClient.getGunAnimations((EntityLivingBase)data[1], false);
        this.renderGun(type, item, gunType, animations, false, data);
        if (gunType.oneHanded && type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON && (entity = (EntityLivingBase)data[1]) instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            PlayerData playerData = PlayerHandler.getPlayerData(player, Side.CLIENT);
            if (playerData.offHandGunSlot != 0) {
                ItemStack offHandItem;
                GunAnimations animations2 = FlansModClient.gunAnimationsLeft.get(data[1]);
                if (animations2 == null) {
                    animations2 = new GunAnimations();
                    FlansModClient.gunAnimationsLeft.put((EntityLivingBase)data[1], animations2);
                }
                if ((offHandItem = player.field_71071_by.func_70301_a(playerData.offHandGunSlot - 1)) == null || !(offHandItem.func_77973_b() instanceof ItemGun)) {
                    return;
                }
                GunType offHandGunType = ((ItemGun)offHandItem.func_77973_b()).type;
                if (!offHandGunType.oneHanded) {
                    return;
                }
                this.renderGun(type, offHandItem, offHandGunType, animations2, true, data);
            }
        }
    }

    public void renderOffHandGun(EntityPlayer player, ItemStack offHandItemStack) {
        GunAnimations animations = FlansModClient.gunAnimationsLeft.get(player);
        if (animations == null) {
            animations = new GunAnimations();
            FlansModClient.gunAnimationsLeft.put((EntityLivingBase)player, animations);
        }
        GunType offHandGunType = ((ItemGun)offHandItemStack.func_77973_b()).type;
        if (!offHandGunType.oneHanded) {
            return;
        }
        this.renderGun(IItemRenderer.ItemRenderType.EQUIPPED, offHandItemStack, offHandGunType, animations, true, player);
    }

    private void renderGun(IItemRenderer.ItemRenderType type, ItemStack item, GunType gunType, GunAnimations animations, boolean offHand, Object ... data) {
        float f = 0.0625f;
        ModelGun model = gunType.model;
        int flip = offHand ? -1 : 1;
        GL11.glPushMatrix();
        float reloadRotate = 0.0f;
        block0 : switch (type) {
            case ENTITY: {
                GL11.glScalef((float)model.entityScale, (float)model.entityScale, (float)model.entityScale);
                EntityItem entity = (EntityItem)data[1];
                GL11.glRotatef((float)((float)entity.field_70292_b + (entity.field_70292_b == 0 ? 0.0f : smoothing)), (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glTranslatef((float)(-0.2f + model.itemFrameOffset.x), (float)(0.2f + model.itemFrameOffset.y), (float)(0.1f + model.itemFrameOffset.z));
                break;
            }
            case EQUIPPED: {
                if (offHand) {
                    GL11.glRotatef((float)-70.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                    GL11.glRotatef((float)48.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)105.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glTranslatef((float)-0.1f, (float)-0.22f, (float)-0.15f);
                    GL11.glScalef((float)model.modelScale, (float)model.modelScale, (float)model.modelScale);
                } else {
                    GL11.glRotatef((float)35.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)-5.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glTranslatef((float)0.75f, (float)-0.22f, (float)-0.08f);
                    GL11.glScalef((float)1.0f, (float)1.0f, (float)-1.0f);
                }
                GL11.glScalef((float)model.modelScale, (float)model.modelScale, (float)model.modelScale);
                GL11.glTranslatef((float)model.thirdPersonOffset.x, (float)model.thirdPersonOffset.y, (float)model.thirdPersonOffset.z);
                break;
            }
            case EQUIPPED_FIRST_PERSON: {
                boolean silenced;
                AttachmentType scopeAttachment;
                IScope scope = gunType.getCurrentScope(item);
                if (FlansModClient.zoomProgress > 0.9f && scope.hasZoomOverlay()) {
                    GL11.glPopMatrix();
                    return;
                }
                float adsSwitch = FlansModClient.lastZoomProgress + (FlansModClient.zoomProgress - FlansModClient.lastZoomProgress) * smoothing;
                float runSwitch = FlansModClient.lastRunProgress + (FlansModClient.runProgress - FlansModClient.lastRunProgress) * smoothing;
                if (offHand) {
                    GL11.glTranslatef((float)0.0f, (float)0.03f, (float)-0.76f);
                    GL11.glRotatef((float)23.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)-4.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glTranslatef((float)0.15f, (float)0.2f, (float)-0.6f);
                    GL11.glScalef((float)1.0f, (float)1.0f, (float)1.0f);
                    break;
                }
                if (!animations.reloading) {
                    GL11.glTranslatef((float)(0.4f * runSwitch), (float)(0.14f * runSwitch), (float)(-0.2f * runSwitch));
                    GL11.glRotatef((float)(-20.0f * runSwitch), (float)1.0f, (float)0.0f, (float)0.0f);
                    GL11.glRotatef((float)(40.0f * runSwitch), (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glScalef((float)1.0f, (float)1.0f, (float)1.0f);
                }
                if ((scopeAttachment = gunType.getScope(item)) != null && scopeAttachment.FOVZoomLevel <= 2.0f && (double)scopeAttachment.zoomLevel <= 1.0) {
                    GL11.glTranslatef((float)0.0f, (float)(model.aimPosY * adsSwitch + model.colAimPos * adsSwitch), (float)0.0f);
                }
                if (scopeAttachment != null && (double)scopeAttachment.zoomLevel >= 3.0) {
                    GL11.glTranslatef((float)0.0f, (float)(model.aimPosY * adsSwitch + model.scopeAimPos * adsSwitch), (float)0.0f);
                }
                GL11.glRotatef((float)(25.0f - model.rotateAim * adsSwitch), (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)-5.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glTranslatef((float)(0.1f + model.aimPosX * adsSwitch), (float)(0.2f + model.aimPosY * adsSwitch), (float)(-0.6f - model.aimPosZ * adsSwitch));
                if (gunType.hasScopeOverlay && !animations.reloading) {
                    GL11.glTranslatef((float)(-0.3f * adsSwitch), (float)0.0f, (float)0.0f);
                }
                GL11.glRotatef((float)(4.5f * adsSwitch), (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glTranslatef((float)0.0f, (float)(-0.03f * adsSwitch), (float)0.0f);
                GL11.glScalef((float)model.modelScaleFirst, (float)model.modelScaleFirst, (float)model.modelScaleFirst);
                AttachmentType barrel = gunType.getBarrel(item);
                boolean bl = silenced = barrel != null && barrel.silencer;
                if (!silenced) {
                    Minecraft mc = Minecraft.func_71410_x();
                    byte Ammo = TickHandlerClient.Ammo;
                    if (TickHandlerClient.Ammo != 0 && Mouse.isButtonDown((int)0) && !animations.reloading && mc.field_71462_r == null && animations.pumping) {
                        GL11.glPushMatrix();
                        GL11.glScalef((float)model.scaleShoot, (float)model.scaleShoot, (float)model.scaleShoot);
                        GL11.glTranslatef((float)model.firstPersonOffsetShoot.x, (float)model.firstPersonOffsetShoot.y, (float)model.firstPersonOffsetShoot.z);
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)(model.shootPosInAim * adsSwitch));
                        if (barrel != null && !barrel.silencer) {
                            GL11.glTranslatef((float)0.3f, (float)0.0f, (float)0.0f);
                        }
                        if (ShootSprite) {
                            model.renderShootSprite(f);
                        } else if (!ShootSprite) {
                            model.renderShootSprite2(f);
                        }
                        GL11.glPopMatrix();
                    } else if (TickHandlerClient.Ammo != 0 && Mouse.isButtonDown((int)0) && !animations.reloading && mc.field_71462_r == null && animations.pumping) {
                        GL11.glPushMatrix();
                        GL11.glScalef((float)model.scaleShoot, (float)model.scaleShoot, (float)model.scaleShoot);
                        GL11.glTranslatef((float)model.firstPersonOffsetShoot.x, (float)model.firstPersonOffsetShoot.y, (float)model.firstPersonOffsetShoot.z);
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)(model.shootPosInAim * adsSwitch));
                        GL11.glPopMatrix();
                    }
                }
                if (animations.meleeAnimationProgress > 0 && animations.meleeAnimationProgress < gunType.meleePath.size()) {
                    Vector3f meleePos = gunType.meleePath.get(animations.meleeAnimationProgress);
                    Vector3f nextMeleePos = animations.meleeAnimationProgress + 1 < gunType.meleePath.size() ? gunType.meleePath.get(animations.meleeAnimationProgress + 1) : new Vector3f();
                    GL11.glTranslatef((float)(meleePos.x + (nextMeleePos.x - meleePos.x) * smoothing), (float)(meleePos.y + (nextMeleePos.y - meleePos.y) * smoothing), (float)(meleePos.z + (nextMeleePos.z - meleePos.z) * smoothing));
                    Vector3f meleeAngles = gunType.meleePathAngles.get(animations.meleeAnimationProgress);
                    Vector3f nextMeleeAngles = animations.meleeAnimationProgress + 1 < gunType.meleePathAngles.size() ? gunType.meleePathAngles.get(animations.meleeAnimationProgress + 1) : new Vector3f();
                    GL11.glRotatef((float)(meleeAngles.y + (nextMeleeAngles.y - meleeAngles.y) * smoothing), (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glRotatef((float)(meleeAngles.z + (nextMeleeAngles.z - meleeAngles.z) * smoothing), (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)(meleeAngles.x + (nextMeleeAngles.x - meleeAngles.x) * smoothing), (float)1.0f, (float)0.0f, (float)0.0f);
                }
                if (model.spinningCocking) {
                    GL11.glTranslatef((float)model.spinPoint.x, (float)model.spinPoint.y, (float)model.spinPoint.z);
                    float pumped = animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing;
                    GL11.glRotatef((float)(pumped * 180.0f + 180.0f), (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glTranslatef((float)(-model.spinPoint.x), (float)(-model.spinPoint.y), (float)(-model.spinPoint.z));
                }
                if (!animations.reloading) break;
                float effectiveReloadAnimationProgress = animations.lastReloadAnimationProgress + (animations.reloadAnimationProgress - animations.lastReloadAnimationProgress) * smoothing;
                reloadRotate = 1.0f;
                if (effectiveReloadAnimationProgress < model.tiltGunTime) {
                    reloadRotate = effectiveReloadAnimationProgress / model.tiltGunTime;
                }
                if (effectiveReloadAnimationProgress > model.tiltGunTime + model.unloadClipTime + model.loadClipTime) {
                    reloadRotate = 1.0f - (effectiveReloadAnimationProgress - (model.tiltGunTime + model.unloadClipTime + model.loadClipTime)) / model.untiltGunTime;
                }
                switch (model.animationType) {
                    case BOTTOM_CLIP: 
                    case PISTOL_CLIP: 
                    case SHOTGUN: 
                    case END_LOADED: {
                        GL11.glRotatef((float)(0.0f * reloadRotate), (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glRotatef((float)(0.0f * reloadRotate * (float)flip), (float)1.0f, (float)0.0f, (float)0.0f);
                        GL11.glTranslatef((float)(0.0f * reloadRotate), (float)(model.downReload * reloadRotate), (float)(0.0f * reloadRotate));
                        break block0;
                    }
                    case BACK_LOADED: {
                        GL11.glRotatef((float)(-75.0f * reloadRotate), (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glRotatef((float)(-30.0f * reloadRotate * (float)flip), (float)1.0f, (float)0.0f, (float)0.0f);
                        GL11.glTranslatef((float)(0.5f * reloadRotate), (float)0.0f, (float)0.0f);
                        break block0;
                    }
                    case BULLPUP: {
                        GL11.glRotatef((float)(70.0f * reloadRotate), (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glRotatef((float)(10.0f * reloadRotate * (float)flip), (float)1.0f, (float)0.0f, (float)0.0f);
                        GL11.glTranslatef((float)(0.5f * reloadRotate), (float)(-0.2f * reloadRotate), (float)0.0f);
                        break block0;
                    }
                    case RIFLE: {
                        GL11.glRotatef((float)(30.0f * reloadRotate), (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glRotatef((float)(-30.0f * reloadRotate * (float)flip), (float)1.0f, (float)0.0f, (float)0.0f);
                        GL11.glTranslatef((float)(0.5f * reloadRotate), (float)0.0f, (float)(-0.5f * reloadRotate));
                        break block0;
                    }
                    case RIFLE_CUSTOM: {
                        GL11.glRotatef((float)(75.0f * reloadRotate), (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glRotatef((float)(30.0f * reloadRotate), (float)0.0f, (float)1.0f, (float)0.0f);
                        GL11.glRotatef((float)(0.0f * reloadRotate), (float)1.0f, (float)0.0f, (float)0.0f);
                        GL11.glTranslatef((float)(0.4f * reloadRotate), (float)(0.0f * reloadRotate), (float)(-0.3f * reloadRotate));
                        break block0;
                    }
                    case RIFLE_TOP: 
                    case REVOLVER: {
                        GL11.glRotatef((float)(30.0f * reloadRotate), (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glRotatef((float)(10.0f * reloadRotate), (float)0.0f, (float)1.0f, (float)0.0f);
                        GL11.glRotatef((float)(-10.0f * reloadRotate * (float)flip), (float)1.0f, (float)0.0f, (float)0.0f);
                        GL11.glTranslatef((float)(0.1f * reloadRotate), (float)(-0.2f * reloadRotate), (float)(-0.1f * reloadRotate));
                        break block0;
                    }
                    case ALT_PISTOL_CLIP: {
                        GL11.glRotatef((float)(60.0f * reloadRotate * (float)flip), (float)0.0f, (float)1.0f, (float)0.0f);
                        GL11.glTranslatef((float)(0.15f * reloadRotate), (float)(0.25f * reloadRotate), (float)0.0f);
                        break block0;
                    }
                    case STRIKER: {
                        GL11.glRotatef((float)(-35.0f * reloadRotate * (float)flip), (float)1.0f, (float)0.0f, (float)0.0f);
                        GL11.glTranslatef((float)(0.2f * reloadRotate), (float)0.0f, (float)(-0.1f * reloadRotate));
                        break block0;
                    }
                    case GENERIC: {
                        GL11.glRotatef((float)(45.0f * reloadRotate), (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glTranslatef((float)(-0.2f * reloadRotate), (float)(-0.5f * reloadRotate), (float)0.0f);
                        break block0;
                    }
                    case CUSTOM: {
                        GL11.glRotatef((float)(model.rotateGunVertical * reloadRotate), (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glRotatef((float)(model.rotateGunHorizontal * reloadRotate), (float)0.0f, (float)1.0f, (float)0.0f);
                        GL11.glRotatef((float)(model.tiltGun * reloadRotate), (float)1.0f, (float)0.0f, (float)0.0f);
                        GL11.glTranslatef((float)(model.translateGun.x * reloadRotate), (float)(model.translateGun.y * reloadRotate), (float)(model.translateGun.z * reloadRotate));
                        break block0;
                    }
                }
                break;
            }
        }
        if (model.animationType == EnumAnimationType.BOTTOM_CLIP && type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)1.7f, (float)0.31f, (float)-0.02f);
            GL11.glRotatef((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glRotatef((float)-90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glRotatef((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glTranslatef((float)-1.0f, (float)-5.2f, (float)-0.98f);
            GL11.glScaled((double)0.7, (double)2.2, (double)0.7);
            GL11.glScaled((double)model.scaleHands, (double)model.scaleHands, (double)model.scaleHands);
            GL11.glTranslatef((float)model.leftHandX, (float)model.leftHandY, (float)model.leftHandZ);
            GL11.glTranslatef((float)0.0f, (float)(-(1.0f - Math.abs(animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing)) * model.handHandleDistance), (float)0.0f);
            Minecraft.func_71410_x().field_71446_o.func_110577_a(new ResourceLocation("textures/entity/steve.png"));
            model1.render_left();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslatef((float)0.0f, (float)-0.95f, (float)-2.26f);
            GL11.glRotatef((float)205.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glRotatef((float)-60.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glRotatef((float)80.5f, (float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glTranslatef((float)0.75f, (float)-2.65f, (float)0.15f);
            GL11.glScaled((double)0.85, (double)1.0, (double)0.85);
            GL11.glScaled((double)model.scaleHands, (double)(model.scaleHands * 2.65), (double)model.scaleHands);
            GL11.glTranslatef((float)model.rightHandX, (float)model.rightHandY, (float)model.rightHandZ);
            GL11.glTranslatef((float)((1.0f - Math.abs(animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing)) * model.handHandleDistance), (float)(-(1.0f - Math.abs(animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing)) * model.handHandleDistance), (float)((1.0f - Math.abs(animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing)) * model.handHandleDistance));
            Minecraft.func_71410_x().field_71446_o.func_110577_a(new ResourceLocation("textures/entity/steve.png"));
            model1.render_right();
            GL11.glPopMatrix();
        }
        this.renderGun(item, gunType, f, model, animations, reloadRotate);
        GL11.glPopMatrix();
    }

    public void renderGun(ItemStack item, GunType type, float f, ModelGun model, GunAnimations animations, float reloadRotate) {
        if (renderEngine == null) {
            renderEngine = Minecraft.func_71410_x().field_71446_o;
        }
        if (animations == null) {
            animations = GunAnimations.defaults;
        }
        AttachmentType scopeAttachment = type.getScope(item);
        AttachmentType barrelAttachment = type.getBarrel(item);
        AttachmentType stockAttachment = type.getStock(item);
        AttachmentType gripAttachment = type.getGrip(item);
        ItemStack[] bulletStacks = new ItemStack[type.numAmmoItemsInGun];
        boolean empty = true;
        for (int i = 0; i < type.numAmmoItemsInGun; ++i) {
            bulletStacks[i] = ((ItemGun)item.func_77973_b()).getBulletItemStack(item, i);
            if (bulletStacks[i] == null || !(bulletStacks[i].func_77973_b() instanceof ItemBullet) || bulletStacks[i].func_77960_j() >= bulletStacks[i].func_77958_k()) continue;
            empty = false;
        }
        renderEngine.func_110577_a(FlansModResourceHandler.getPaintjobTexture(type.getPaintjob(item.field_77990_d.func_74779_i("Paint"))));
        if (scopeAttachment != null) {
            GL11.glTranslatef((float)0.0f, (float)(-scopeAttachment.model.renderOffset / 16.0f), (float)0.0f);
        }
        GL11.glPushMatrix();
        GL11.glScalef((float)type.modelScale, (float)type.modelScale, (float)type.modelScale);
        GL11.glRotatef((float)(1.0f - Math.abs(animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing)), (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glTranslatef((float)(-(1.0f - Math.abs(animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing)) * model.pumpHandleDistance), (float)0.0f, (float)0.0f);
        model.renderGun(f);
        if (scopeAttachment == null && !model.scopeIsOnSlide && !model.scopeIsOnBreakAction) {
            model.renderDefaultScope(f);
        }
        if (barrelAttachment == null) {
            model.renderDefaultBarrel(f);
        }
        if (stockAttachment == null) {
            model.renderDefaultStock(f);
        }
        if (gripAttachment == null && !model.gripIsOnPump) {
            model.renderDefaultGrip(f);
        }
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(-(1.0f - Math.abs(animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing)) * model.pumpHandleDistance), (float)0.0f, (float)0.0f);
        model.renderSlide(f);
        if (scopeAttachment == null && model.scopeIsOnSlide) {
            model.renderDefaultScope(f);
        }
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)model.barrelBreakPoint.x, (float)model.barrelBreakPoint.y, (float)model.barrelBreakPoint.z);
        GL11.glRotatef((float)(reloadRotate * -model.breakAngle), (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glTranslatef((float)(-model.barrelBreakPoint.x), (float)(-model.barrelBreakPoint.y), (float)(-model.barrelBreakPoint.z));
        model.renderBreakAction(f);
        if (scopeAttachment == null && model.scopeIsOnBreakAction) {
            model.renderDefaultScope(f);
        }
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        model.renderPump(f);
        if (gripAttachment == null && model.gripIsOnPump) {
            model.renderDefaultGrip(f);
        }
        GL11.glPopMatrix();
        if (type.mode == EnumFireMode.MINIGUN) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)model.minigunBarrelOrigin.x, (float)model.minigunBarrelOrigin.y, (float)model.minigunBarrelOrigin.z);
            GL11.glRotatef((float)animations.minigunBarrelRotation, (float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glTranslatef((float)(-model.minigunBarrelOrigin.x), (float)(-model.minigunBarrelOrigin.y), (float)(-model.minigunBarrelOrigin.z));
            model.renderMinigunBarrel(f);
            GL11.glPopMatrix();
        }
        GL11.glPushMatrix();
        GL11.glTranslatef((float)model.revolverFlipPoint.x, (float)model.revolverFlipPoint.y, (float)model.revolverFlipPoint.z);
        GL11.glRotatef((float)(reloadRotate * model.revolverFlipAngle), (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glTranslatef((float)(-model.revolverFlipPoint.x), (float)(-model.revolverFlipPoint.y), (float)(-model.revolverFlipPoint.z));
        model.renderRevolverBarrel(f);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        boolean shouldRender = true;
        switch (model.animationType) {
            case END_LOADED: 
            case BACK_LOADED: {
                if (!empty) break;
                shouldRender = false;
                break;
            }
        }
        if (shouldRender && animations.reloading && Minecraft.func_71410_x().field_71474_y.field_74320_O == 0) {
            float effectiveReloadAnimationProgress = animations.lastReloadAnimationProgress + (animations.reloadAnimationProgress - animations.lastReloadAnimationProgress) * smoothing;
            float clipPosition = 0.0f;
            if (effectiveReloadAnimationProgress > model.tiltGunTime && effectiveReloadAnimationProgress < model.tiltGunTime + model.unloadClipTime) {
                clipPosition = (effectiveReloadAnimationProgress - model.tiltGunTime) / model.unloadClipTime;
            }
            if (effectiveReloadAnimationProgress >= model.tiltGunTime + model.unloadClipTime && effectiveReloadAnimationProgress < model.tiltGunTime + model.unloadClipTime + model.loadClipTime) {
                clipPosition = 1.0f - (effectiveReloadAnimationProgress - (model.tiltGunTime + model.unloadClipTime)) / model.loadClipTime;
            }
            float loadOnlyClipPosition = Math.max(0.0f, Math.min(1.0f, 1.0f - (effectiveReloadAnimationProgress - model.tiltGunTime) / (model.unloadClipTime + model.loadClipTime)));
            switch (model.animationType) {
                case BREAK_ACTION: {
                    GL11.glTranslatef((float)model.barrelBreakPoint.x, (float)model.barrelBreakPoint.y, (float)model.barrelBreakPoint.z);
                    GL11.glRotatef((float)(reloadRotate * -model.breakAngle), (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glTranslatef((float)(-model.barrelBreakPoint.x), (float)(-model.barrelBreakPoint.y), (float)(-model.barrelBreakPoint.z));
                    GL11.glTranslatef((float)(-1.0f * clipPosition), (float)0.0f, (float)0.0f);
                    break;
                }
                case REVOLVER: {
                    GL11.glTranslatef((float)model.revolverFlipPoint.x, (float)model.revolverFlipPoint.y, (float)model.revolverFlipPoint.z);
                    GL11.glRotatef((float)(reloadRotate * model.revolverFlipAngle), (float)1.0f, (float)0.0f, (float)0.0f);
                    GL11.glTranslatef((float)(-model.revolverFlipPoint.x), (float)(-model.revolverFlipPoint.y), (float)(-model.revolverFlipPoint.z));
                    GL11.glTranslatef((float)(-1.0f * clipPosition), (float)0.0f, (float)0.0f);
                    break;
                }
                case BOTTOM_CLIP: {
                    GL11.glRotatef((float)(-180.0f * clipPosition), (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)(60.0f * clipPosition), (float)1.0f, (float)0.0f, (float)0.0f);
                    GL11.glTranslatef((float)(0.5f * clipPosition), (float)0.0f, (float)0.0f);
                    break;
                }
                case PISTOL_CLIP: {
                    GL11.glRotatef((float)(-90.0f * clipPosition * clipPosition), (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glTranslatef((float)0.0f, (float)(-1.0f * clipPosition), (float)0.0f);
                    break;
                }
                case ALT_PISTOL_CLIP: {
                    GL11.glRotatef((float)(5.0f * clipPosition), (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glTranslatef((float)0.0f, (float)(-3.0f * clipPosition), (float)0.0f);
                    break;
                }
                case SIDE_CLIP: {
                    GL11.glRotatef((float)(180.0f * clipPosition), (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glRotatef((float)(60.0f * clipPosition), (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glTranslatef((float)(0.5f * clipPosition), (float)0.0f, (float)0.0f);
                    break;
                }
                case BULLPUP: {
                    GL11.glRotatef((float)(-150.0f * clipPosition), (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)(60.0f * clipPosition), (float)1.0f, (float)0.0f, (float)0.0f);
                    GL11.glTranslatef((float)(1.0f * clipPosition), (float)(-0.5f * clipPosition), (float)0.0f);
                    break;
                }
                case P90: {
                    GL11.glRotatef((float)(-15.0f * reloadRotate * reloadRotate), (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glTranslatef((float)0.0f, (float)(0.075f * reloadRotate), (float)0.0f);
                    GL11.glTranslatef((float)(-2.0f * clipPosition), (float)(-0.3f * clipPosition), (float)(0.5f * clipPosition));
                    break;
                }
                case RIFLE: {
                    float thing = clipPosition * model.numBulletsInReloadAnimation;
                    int bulletNum = MathHelper.func_76141_d((float)thing);
                    float bulletProgress = thing - (float)bulletNum;
                    GL11.glRotatef((float)(bulletProgress * 15.0f), (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glRotatef((float)(bulletProgress * 15.0f), (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glTranslatef((float)(bulletProgress * -1.0f), (float)0.0f, (float)(bulletProgress * 0.5f));
                    break;
                }
                case RIFLE_CUSTOM: {
                    GL11.glRotatef((float)(-90.0f * clipPosition * clipPosition), (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glTranslatef((float)0.0f, (float)(-1.0f * clipPosition), (float)0.0f);
                    break;
                }
                case RIFLE_TOP: {
                    float thing = clipPosition * model.numBulletsInReloadAnimation;
                    int bulletNum = MathHelper.func_76141_d((float)thing);
                    float bulletProgress = thing - (float)bulletNum;
                    GL11.glRotatef((float)(bulletProgress * 55.0f), (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glRotatef((float)(bulletProgress * 95.0f), (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glTranslatef((float)(bulletProgress * -0.1f), (float)(bulletProgress * 1.0f), (float)(bulletProgress * 0.5f));
                    break;
                }
                case SHOTGUN: 
                case STRIKER: {
                    float thing = clipPosition * model.numBulletsInReloadAnimation;
                    int bulletNum = MathHelper.func_76141_d((float)thing);
                    float bulletProgress = thing - (float)bulletNum;
                    GL11.glRotatef((float)(bulletProgress * -30.0f), (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glTranslatef((float)(bulletProgress * -0.5f), (float)(bulletProgress * -1.0f), (float)0.0f);
                    break;
                }
                case CUSTOM: {
                    GL11.glRotatef((float)(model.rotateClipVertical * clipPosition), (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)(model.rotateClipHorizontal * clipPosition), (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glRotatef((float)(model.tiltClip * clipPosition), (float)1.0f, (float)0.0f, (float)0.0f);
                    GL11.glTranslatef((float)(model.translateClip.x * clipPosition), (float)(model.translateClip.y * clipPosition), (float)(model.translateClip.z * clipPosition));
                    break;
                }
                case END_LOADED: {
                    float dYaw = loadOnlyClipPosition > 0.5f ? loadOnlyClipPosition * 2.0f - 1.0f : 0.0f;
                    GL11.glRotatef((float)(-45.0f * dYaw), (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glTranslatef((float)(-model.endLoadedAmmoDistance * dYaw), (float)(-0.5f * dYaw), (float)0.0f);
                    float xDisplacement = loadOnlyClipPosition < 0.5f ? loadOnlyClipPosition * 2.0f : 1.0f;
                    GL11.glTranslatef((float)(model.endLoadedAmmoDistance * xDisplacement), (float)0.0f, (float)0.0f);
                    break;
                }
                case BACK_LOADED: {
                    float dYaw = loadOnlyClipPosition > 0.5f ? loadOnlyClipPosition * 2.0f - 1.0f : 0.0f;
                    GL11.glTranslatef((float)(model.endLoadedAmmoDistance * dYaw), (float)(-0.5f * dYaw), (float)0.0f);
                    float xDisplacement = loadOnlyClipPosition < 0.5f ? loadOnlyClipPosition * 2.0f : 1.0f;
                    GL11.glTranslatef((float)(-model.endLoadedAmmoDistance * xDisplacement), (float)0.0f, (float)0.0f);
                }
            }
        }
        if (shouldRender) {
            model.renderAmmo(f);
        }
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        if (scopeAttachment != null) {
            GL11.glPushMatrix();
            GL11.glRotatef((float)(1.0f - Math.abs(animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing)), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glTranslatef((float)(-(1.0f - Math.abs(animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing)) * model.scopeHandleDistance), (float)0.0f, (float)0.0f);
            renderEngine.func_110577_a(FlansModResourceHandler.getTexture(scopeAttachment));
            if (model.scopeIsOnBreakAction) {
                GL11.glTranslatef((float)model.barrelBreakPoint.x, (float)model.barrelBreakPoint.y, (float)model.barrelBreakPoint.z);
                GL11.glRotatef((float)(reloadRotate * -model.breakAngle), (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glTranslatef((float)(-model.barrelBreakPoint.x), (float)(-model.barrelBreakPoint.y), (float)(-model.barrelBreakPoint.z));
            }
            if (scopeAttachment != null && scopeAttachment.FOVZoomLevel <= 2.0f && (double)scopeAttachment.zoomLevel <= 1.0) {
                GL11.glTranslatef((float)(model.colAttachPoint.x * type.modelScale), (float)(model.colAttachPoint.y * type.modelScale), (float)(model.colAttachPoint.z * type.modelScale));
            }
            if (scopeAttachment != null && (double)scopeAttachment.zoomLevel >= 3.0) {
                GL11.glTranslatef((float)(model.scopeAttachPoint.x * type.modelScale), (float)(model.scopeAttachPoint.y * type.modelScale), (float)(model.scopeAttachPoint.z * type.modelScale));
            }
            if (model.scopeIsOnSlide) {
                GL11.glScalef((float)0.1f, (float)0.1f, (float)0.1f);
            }
            GL11.glScalef((float)scopeAttachment.modelScale, (float)scopeAttachment.modelScale, (float)scopeAttachment.modelScale);
            ModelAttachment scopeModel = scopeAttachment.model;
            if (scopeModel != null) {
                scopeModel.renderAttachment(f);
            }
            renderEngine.func_110577_a(FlansModResourceHandler.getTexture(type));
            GL11.glPopMatrix();
        }
        if (gripAttachment != null) {
            GL11.glPushMatrix();
            GL11.glRotatef((float)(1.0f - Math.abs(animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing)), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glTranslatef((float)(-(1.0f - Math.abs(animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing)) * model.scopeHandleDistance), (float)0.0f, (float)0.0f);
            renderEngine.func_110577_a(FlansModResourceHandler.getTexture(gripAttachment));
            GL11.glTranslatef((float)(model.gripAttachPoint.x * type.modelScale), (float)(model.gripAttachPoint.y * type.modelScale), (float)(model.gripAttachPoint.z * type.modelScale));
            if (model.gripIsOnPump) {
                GL11.glTranslatef((float)(-(1.0f - Math.abs(animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing)) * model.pumpHandleDistance), (float)0.0f, (float)0.0f);
            }
            GL11.glScalef((float)gripAttachment.modelScale, (float)gripAttachment.modelScale, (float)gripAttachment.modelScale);
            ModelAttachment gripModel = gripAttachment.model;
            if (gripModel != null) {
                gripModel.renderAttachment(f);
            }
            renderEngine.func_110577_a(FlansModResourceHandler.getTexture(type));
            GL11.glPopMatrix();
        }
        if (barrelAttachment != null) {
            GL11.glPushMatrix();
            GL11.glRotatef((float)(1.0f - Math.abs(animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing)), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glTranslatef((float)(-(1.0f - Math.abs(animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing)) * model.scopeHandleDistance), (float)0.0f, (float)0.0f);
            renderEngine.func_110577_a(FlansModResourceHandler.getTexture(barrelAttachment));
            GL11.glTranslatef((float)(model.barrelAttachPoint.x * type.modelScale), (float)(model.barrelAttachPoint.y * type.modelScale), (float)(model.barrelAttachPoint.z * type.modelScale));
            GL11.glScalef((float)barrelAttachment.modelScale, (float)barrelAttachment.modelScale, (float)barrelAttachment.modelScale);
            GL11.glScalef((float)barrelAttachment.model.scaleAttachment, (float)barrelAttachment.model.scaleAttachment, (float)barrelAttachment.model.scaleAttachment);
            ModelAttachment barrelModel = barrelAttachment.model;
            if (barrelModel != null) {
                barrelModel.renderAttachment(f);
            }
            renderEngine.func_110577_a(FlansModResourceHandler.getTexture(type));
            GL11.glPopMatrix();
        }
        if (stockAttachment != null) {
            GL11.glPushMatrix();
            GL11.glRotatef((float)(1.0f - Math.abs(animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing)), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glTranslatef((float)(-(1.0f - Math.abs(animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing)) * model.pumpHandleDistance), (float)0.0f, (float)0.0f);
            renderEngine.func_110577_a(FlansModResourceHandler.getTexture(stockAttachment));
            GL11.glTranslatef((float)(model.stockAttachPoint.x * type.modelScale), (float)(model.stockAttachPoint.y * type.modelScale), (float)(model.stockAttachPoint.z * type.modelScale));
            GL11.glScalef((float)stockAttachment.modelScale, (float)stockAttachment.modelScale, (float)stockAttachment.modelScale);
            ModelAttachment stockModel = stockAttachment.model;
            if (stockModel != null) {
                stockModel.renderAttachment(f);
            }
            renderEngine.func_110577_a(FlansModResourceHandler.getTexture(type));
            GL11.glPopMatrix();
        }
    }
}

