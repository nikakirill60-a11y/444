/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.MathHelper
 */
package com.flansmod.common.guns.raytracing;

import com.flansmod.common.FlansMod;
import com.flansmod.common.PlayerData;
import com.flansmod.common.PlayerHandler;
import com.flansmod.common.RotatedAxes;
import com.flansmod.common.guns.GunType;
import com.flansmod.common.guns.ItemGun;
import com.flansmod.common.guns.raytracing.BulletHit;
import com.flansmod.common.guns.raytracing.EnumHitboxType;
import com.flansmod.common.guns.raytracing.PlayerBulletHit;
import com.flansmod.common.guns.raytracing.PlayerHitbox;
import com.flansmod.common.vector.Vector3f;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class PlayerSnapshot {
    public EntityPlayer player;
    public Vector3f pos;
    public ArrayList<PlayerHitbox> hitboxes;
    public long time;

    public PlayerSnapshot(EntityPlayer p) {
        this.player = p;
        this.pos = new Vector3f(p.field_70165_t, p.field_70163_u, p.field_70161_v);
        if (FlansMod.proxy.isThePlayer(p)) {
            this.pos = new Vector3f(p.field_70165_t, p.field_70163_u - (double)1.6f, p.field_70161_v);
        }
        this.hitboxes = new ArrayList();
        RotatedAxes bodyAxes = new RotatedAxes(p.field_70761_aq, 0.0f, 0.0f);
        RotatedAxes headAxes = new RotatedAxes(p.field_70759_as - p.field_70761_aq, 0.0f, -p.field_70125_A);
        this.hitboxes.add(new PlayerHitbox(this.player, bodyAxes, new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(-0.25f, 0.0f, -0.15f), new Vector3f(0.5f, 1.4f, 0.3f), EnumHitboxType.BODY));
        this.hitboxes.add(new PlayerHitbox(this.player, bodyAxes.findLocalAxesGlobally(headAxes), new Vector3f(0.0f, 1.4f, 0.0f), new Vector3f(-0.25f, 0.0f, -0.25f), new Vector3f(0.5f, 0.5f, 0.5f), EnumHitboxType.HEAD));
        float yHead = (p.field_70759_as - p.field_70761_aq) / 57.295776f;
        float xHead = p.field_70125_A / 57.295776f;
        float zRight = 0.0f;
        float zLeft = 0.0f;
        float yRight = -0.1f + yHead - 1.5707964f;
        float yLeft = 0.1f + yHead + 0.4f - 1.5707964f;
        float xRight = -1.5707964f + xHead;
        float xLeft = -1.5707964f + xHead;
        RotatedAxes leftArmAxes = new RotatedAxes().rotateGlobalPitchInRads(xLeft -= MathHelper.func_76126_a((float)((float)p.field_70173_aa * 0.067f)) * 0.05f).rotateGlobalYawInRads((float)Math.PI + yLeft).rotateGlobalRollInRads(-(zLeft -= MathHelper.func_76134_b((float)((float)p.field_70173_aa * 0.09f)) * 0.05f + 0.05f));
        RotatedAxes rightArmAxes = new RotatedAxes().rotateGlobalPitchInRads(xRight += MathHelper.func_76126_a((float)((float)p.field_70173_aa * 0.067f)) * 0.05f).rotateGlobalYawInRads((float)Math.PI + yRight).rotateGlobalRollInRads(-(zRight += MathHelper.func_76134_b((float)((float)p.field_70173_aa * 0.09f)) * 0.05f + 0.05f));
        float originZRight = MathHelper.func_76126_a((float)(-p.field_70761_aq * (float)Math.PI / 180.0f)) * 5.0f / 16.0f;
        float originXRight = -MathHelper.func_76134_b((float)(-p.field_70761_aq * (float)Math.PI / 180.0f)) * 5.0f / 16.0f;
        float originZLeft = -MathHelper.func_76126_a((float)(-p.field_70761_aq * (float)Math.PI / 180.0f)) * 5.0f / 16.0f;
        float originXLeft = MathHelper.func_76134_b((float)(-p.field_70761_aq * (float)Math.PI / 180.0f)) * 5.0f / 16.0f;
        this.hitboxes.add(new PlayerHitbox(this.player, bodyAxes.findLocalAxesGlobally(leftArmAxes), new Vector3f(originXLeft, 1.3f, originZLeft), new Vector3f(-0.125f, -0.6f, -0.125f), new Vector3f(0.25f, 0.7f, 0.25f), EnumHitboxType.LEFTARM));
        this.hitboxes.add(new PlayerHitbox(this.player, bodyAxes.findLocalAxesGlobally(rightArmAxes), new Vector3f(originXRight, 1.3f, originZRight), new Vector3f(-0.125f, -0.6f, -0.125f), new Vector3f(0.25f, 0.7f, 0.25f), EnumHitboxType.RIGHTARM));
        ItemStack playerRightHandStack = this.player.func_71045_bC();
        if (playerRightHandStack != null && playerRightHandStack.func_77973_b() instanceof ItemGun) {
            GunType gunType = ((ItemGun)playerRightHandStack.func_77973_b()).type;
            if (gunType.shield) {
                this.hitboxes.add(new PlayerHitbox(this.player, bodyAxes.findLocalAxesGlobally(rightArmAxes), new Vector3f(originXRight, 1.3f, originZRight), new Vector3f(gunType.shieldOrigin.y, -1.05f + gunType.shieldOrigin.x, -0.0625f + gunType.shieldOrigin.z), new Vector3f(gunType.shieldDimensions.y, gunType.shieldDimensions.x, gunType.shieldDimensions.z), EnumHitboxType.RIGHTITEM));
            }
            PlayerData data = PlayerHandler.getPlayerData(this.player);
            if (gunType.oneHanded && data.offHandGunSlot != 0) {
                ItemStack leftHandStack = null;
                leftHandStack = this.player.field_70170_p.field_72995_K && !FlansMod.proxy.isThePlayer(this.player) ? data.offHandGunStack : this.player.field_71071_by.func_70301_a(data.offHandGunSlot - 1);
                if (leftHandStack != null && leftHandStack.func_77973_b() instanceof ItemGun) {
                    GunType leftGunType = ((ItemGun)leftHandStack.func_77973_b()).type;
                    if (leftGunType.shield) {
                        this.hitboxes.add(new PlayerHitbox(this.player, bodyAxes.findLocalAxesGlobally(leftArmAxes), new Vector3f(originXLeft, 1.3f, originZLeft), new Vector3f(leftGunType.shieldOrigin.y, -1.05f + leftGunType.shieldOrigin.x, -0.0625f + leftGunType.shieldOrigin.z), new Vector3f(leftGunType.shieldDimensions.y, leftGunType.shieldDimensions.x, leftGunType.shieldDimensions.z), EnumHitboxType.LEFTITEM));
                    }
                }
            }
        }
    }

    public ArrayList<BulletHit> raytrace(Vector3f origin, Vector3f motion) {
        Vector3f localOrigin = Vector3f.sub(origin, this.pos, null);
        ArrayList<BulletHit> hits = new ArrayList<BulletHit>();
        for (PlayerHitbox hitbox : this.hitboxes) {
            PlayerBulletHit hit = hitbox.raytrace(localOrigin, motion);
            if (hit == null || !(hit.intersectTime >= 0.0f) || !(hit.intersectTime <= 1.0f)) continue;
            hits.add(hit);
        }
        return hits;
    }

    @SideOnly(value=Side.CLIENT)
    public void renderSnapshot() {
        for (PlayerHitbox hitbox : this.hitboxes) {
            hitbox.renderHitbox(this.player.field_70170_p, this.pos);
        }
    }
}

