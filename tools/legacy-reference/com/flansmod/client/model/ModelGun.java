/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.item.ItemStack
 */
package com.flansmod.client.model;

import com.flansmod.client.model.EnumAnimationType;
import com.flansmod.client.model.EnumHandsAnimation;
import com.flansmod.client.model.EnumMeleeAnimation;
import com.flansmod.client.tmt.ModelRendererTurbo;
import com.flansmod.common.guns.AttachmentType;
import com.flansmod.common.guns.GunType;
import com.flansmod.common.vector.Vector3f;
import net.minecraft.client.model.ModelBase;
import net.minecraft.item.ItemStack;

public class ModelGun
extends ModelBase {
    public ModelRendererTurbo[] gunModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] backpackModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] defaultBarrelModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] defaultScopeModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] defaultStockModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] defaultGripModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] ammoModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] revolverBarrelModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] breakActionModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] slideModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] pumpModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] lHandModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] rHandModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] minigunBarrelModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] leverActionModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] shootSprite = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] shootSprite2 = new ModelRendererTurbo[0];
    public Vector3f minigunBarrelOrigin = new Vector3f();
    public Vector3f barrelAttachPoint = new Vector3f();
    public Vector3f scopeAttachPoint = new Vector3f();
    public Vector3f stockAttachPoint = new Vector3f();
    public Vector3f gripAttachPoint = new Vector3f();
    public Vector3f colAttachPoint = new Vector3f();
    public float shootPosInAim = 0.0f;
    public float scaleShoot = 1.0f;
    public float translateHandsOnShoot = 0.0f;
    public float scopeAimPos;
    public float colAimPos;
    public float rotateX = 0.0f;
    public float rotateY = 0.0f;
    public float rotateZ = 0.0f;
    public float aimPosZ = 0.405f;
    public float aimPosY = 0.2f;
    public float aimPosX = 0.2f;
    public float pistolHandY = 1.0f;
    public float pistolHandX = 1.0f;
    public float pistolHandZ = 1.0f;
    public float leftHandX = 1.0f;
    public float leftHandY = 1.0f;
    public float leftHandZ = 1.0f;
    public float rightHandX = 1.0f;
    public float rightHandY = 1.0f;
    public float rightHandZ = 1.0f;
    public float entityScale = 1.0f;
    public float rotateAim = 5.0f;
    public double scaleHandPistol = 1.0;
    public float rotateGun = 2.0f;
    public float downReload = 1.0f;
    public double scaleHands = 1.0;
    public float modelScaleFirst = 1.0f;
    public float modelScale = 1.0f;
    public float gunSlideDistance = 0.25f;
    public EnumAnimationType animationType = EnumAnimationType.NONE;
    public EnumHandsAnimation handsAnimationType = EnumHandsAnimation.NONE;
    public EnumMeleeAnimation meleeAnimation = EnumMeleeAnimation.DEFAULT;
    public float tiltGunTime = 0.25f;
    public float unloadClipTime = 0.25f;
    public float loadClipTime = 0.25f;
    public float untiltGunTime = 0.25f;
    public boolean scopeIsOnSlide = false;
    public boolean scopeIsOnBreakAction = false;
    public float numBulletsInReloadAnimation = 1.0f;
    public int handDelay = 0;
    public int handDelayAfterReload = 0;
    public int handTime = 1;
    public int pumpDelay = 0;
    public int pumpDelayAfterReload = 0;
    public int pumpTime = 1;
    public float handHandleDistance = 0.25f;
    public float pumpHandleDistance = 0.25f;
    public float scopeHandleDistance = 0.25f;
    public float endLoadedAmmoDistance = 1.0f;
    public boolean gripIsOnPump = false;
    public Vector3f barrelBreakPoint = new Vector3f();
    public float revolverFlipAngle = 15.0f;
    public Vector3f revolverFlipPoint = new Vector3f();
    public float breakAngle = 45.0f;
    public boolean spinningCocking = false;
    public Vector3f spinPoint = new Vector3f();
    public float tiltHands = 0.1f;
    public float rotateHandRVertical = 0.0f;
    public float rotateHandRHorizontal = 0.0f;
    public float tiltHandR = 0.1f;
    public float lHandPosOnReloadX = 0.0f;
    public float lHandPosOnReloadZ = 0.0f;
    public float rotateHandsHorizontal = 0.0f;
    public float rotateHandsVertical = 0.0f;
    public boolean LeftReload = true;
    public Vector3f translateHands = new Vector3f(0.0f, 0.0f, 0.0f);
    public Vector3f translateWeapon = new Vector3f(0.0f, 0.0f, 0.0f);
    public float rotateGunVertical = 0.0f;
    public float rotateGunHorizontal = 0.0f;
    public float tiltGun = 0.0f;
    public Vector3f translateGun = new Vector3f(0.0f, 0.0f, 0.0f);
    public float rotateClipVertical = 0.0f;
    public float rotateClipHorizontal = 0.0f;
    public float tiltClip = 0.0f;
    public Vector3f translateClip = new Vector3f(0.0f, 0.0f, 0.0f);
    public Vector3f thirdPersonOffset = new Vector3f();
    public Vector3f firstPersonOffsetShoot = new Vector3f();
    public Vector3f itemFrameOffset = new Vector3f();

    public void renderGun(float f) {
        this.render(this.gunModel, f);
    }

    public void renderSlide(float f) {
        this.render(this.slideModel, f);
    }

    public void renderPump(float f) {
        this.render(this.pumpModel, f);
    }

    public void renderDefaultScope(float f) {
        this.render(this.defaultScopeModel, f);
    }

    public void renderDefaultBarrel(float f) {
        this.render(this.defaultBarrelModel, f);
    }

    public void renderDefaultStock(float f) {
        this.render(this.defaultStockModel, f);
    }

    public void renderDefaultGrip(float f) {
        this.render(this.defaultGripModel, f);
    }

    public void renderAmmo(float f) {
        this.render(this.ammoModel, f);
    }

    public void renderMinigunBarrel(float f) {
        this.render(this.minigunBarrelModel, f);
    }

    public void renderRevolverBarrel(float f) {
        this.render(this.revolverBarrelModel, f);
    }

    public void renderBreakAction(float f) {
        this.render(this.breakActionModel, f);
    }

    public void renderLHandModel(float f) {
        this.render(this.lHandModel, f);
    }

    public void renderRHandModel(float f) {
        this.render(this.rHandModel, f);
    }

    public void renderShootSprite(float f) {
        this.render(this.shootSprite, f);
    }

    public void renderShootSprite2(float f) {
        this.render(this.shootSprite2, f);
    }

    private void render(ModelRendererTurbo[] models, float f) {
        for (ModelRendererTurbo model : models) {
            if (model == null) continue;
            model.func_78785_a(f);
        }
    }

    public void flipAll() {
        this.flip(this.gunModel);
        this.flip(this.defaultBarrelModel);
        this.flip(this.defaultScopeModel);
        this.flip(this.defaultStockModel);
        this.flip(this.defaultGripModel);
        this.flip(this.ammoModel);
        this.flip(this.slideModel);
        this.flip(this.pumpModel);
        this.flip(this.lHandModel);
        this.flip(this.rHandModel);
        this.flip(this.shootSprite);
        this.flip(this.shootSprite2);
        this.flip(this.minigunBarrelModel);
        this.flip(this.revolverBarrelModel);
        this.flip(this.breakActionModel);
    }

    protected void flip(ModelRendererTurbo[] model) {
        for (ModelRendererTurbo part : model) {
            part.doMirror(false, true, true);
            part.func_78793_a(part.field_78800_c, -part.field_78797_d, -part.field_78798_e);
        }
    }

    public void translateAll(float x, float y, float z) {
        this.translate(this.gunModel, x, y, z);
        this.translate(this.defaultBarrelModel, x, y, z);
        this.translate(this.defaultScopeModel, x, y, z);
        this.translate(this.defaultStockModel, x, y, z);
        this.translate(this.defaultGripModel, x, y, z);
        this.translate(this.ammoModel, x, y, z);
        this.translate(this.slideModel, x, y, z);
        this.translate(this.pumpModel, x, y, z);
        this.translate(this.minigunBarrelModel, x, y, z);
        this.translate(this.revolverBarrelModel, x, y, z);
        this.translate(this.breakActionModel, x, y, z);
        this.translate(this.rHandModel, x, y, z);
        this.translate(this.lHandModel, x, y, z);
        this.translate(this.shootSprite, x, y, z);
        this.translate(this.shootSprite2, x, y, z);
    }

    public void translateAimScope(ItemStack item, GunType type) {
        AttachmentType scopeAttachment = type.getScope(item);
        if (scopeAttachment != null) {
            this.scopeAimPos = 0.1f;
        }
    }

    public void translateShoot(float x, float y, float z) {
        this.translate(this.shootSprite, x, y, z);
        this.translate(this.shootSprite2, x, y, z);
    }

    public void translateWeapon(float x, float y, float z) {
        this.translate(this.gunModel, x, y, z);
        this.translate(this.defaultBarrelModel, x, y, z);
        this.translate(this.defaultScopeModel, x, y, z);
        this.translate(this.defaultStockModel, x, y, z);
        this.translate(this.defaultGripModel, x, y, z);
        this.translate(this.ammoModel, x, y, z);
        this.translate(this.slideModel, x, y, z);
        this.translate(this.pumpModel, x, y, z);
        this.translate(this.minigunBarrelModel, x, y, z);
        this.translate(this.revolverBarrelModel, x, y, z);
        this.translate(this.breakActionModel, x, y, z);
        this.translate(this.shootSprite, x, y, z);
        this.translate(this.shootSprite2, x, y, z);
    }

    public void translateHands(float x, float y, float z) {
        this.translate(this.rHandModel, x, y, z);
        this.translate(this.lHandModel, x, y, z);
    }

    protected void translate(ModelRendererTurbo[] model, float x, float y, float z) {
        for (ModelRendererTurbo mod : model) {
            mod.field_78800_c += x;
            mod.field_78797_d += y;
            mod.field_78798_e += z;
        }
    }
}

