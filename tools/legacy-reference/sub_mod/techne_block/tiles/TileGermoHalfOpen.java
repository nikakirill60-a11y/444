/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.AxisAlignedBB
 */
package sub_mod.techne_block.tiles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileGermoHalfOpen
extends TileEntity {
    public int timer = 0;
    public float animLocX;
    public float animLocX1;
    public float animLocX2;
    public float animLocX3;
    public boolean g0;
    public boolean g1;
    public boolean g2;
    public boolean g3;
    public short rotation = (short)180;

    public void func_145845_h() {
        if (this.field_145850_b.func_72864_z(this.field_145851_c, this.field_145848_d, this.field_145849_e)) {
            if (this.timer < 90) {
                ++this.timer;
            }
        } else if (this.timer > 0) {
            --this.timer;
        }
        if (this != null && this.field_145850_b != null) {
            int animMaxX = this.field_145850_b.func_72805_g(this.field_145851_c, this.field_145848_d, this.field_145849_e);
            if (animMaxX == 0) {
                this.rotation = (short)90;
            }
            if (animMaxX == 1) {
                this.rotation = 0;
            }
            if (animMaxX == 2) {
                this.rotation = (short)270;
            }
        }
        float animMaxX1 = 83.0f;
        float animspeed = 0.1f;
        if (this.animLocX > animMaxX1) {
            this.animLocX = animMaxX1;
        }
        if (this.animLocX < 0.0f) {
            this.animLocX = 0.0f;
        }
        if (this.animLocX1 > animMaxX1) {
            this.animLocX1 = animMaxX1;
        }
        if (this.animLocX1 < 0.0f) {
            this.animLocX1 = 0.0f;
        }
        if (this.animLocX2 > animMaxX1) {
            this.animLocX2 = animMaxX1;
        }
        if (this.animLocX2 < 0.0f) {
            this.animLocX2 = 0.0f;
        }
        if (this.animLocX3 > animMaxX1) {
            this.animLocX3 = animMaxX1;
        }
        if (this.animLocX3 < 0.0f) {
            this.animLocX3 = 0.0f;
        }
        if (this.field_145850_b.func_72864_z(this.field_145851_c, this.field_145848_d, this.field_145849_e)) {
            if (this.rotation == 90) {
                this.g0 = true;
            }
            if (this.rotation == 0) {
                this.g1 = true;
            }
            if (this.rotation == 180) {
                this.g2 = true;
            }
            if (this.rotation == 270) {
                this.g3 = true;
            }
        } else {
            if (this.rotation == 90) {
                this.g0 = false;
            }
            if (this.rotation == 0) {
                this.g1 = false;
            }
            if (this.rotation == 180) {
                this.g2 = false;
            }
            if (this.rotation == 270) {
                this.g3 = false;
            }
        }
        if (this.g0) {
            if (this.animLocX < animMaxX1) {
                this.animLocX += 0.32f / (animspeed * 3.0f);
            }
        } else if (this.animLocX <= animMaxX1 && this.animLocX > 0.0f) {
            this.animLocX -= 0.32f / (animspeed * 3.0f);
        }
        if (this.g1) {
            if (this.animLocX1 < animMaxX1) {
                this.animLocX1 += 0.32f / (animspeed * 3.0f);
            }
        } else if (this.animLocX1 <= animMaxX1 && this.animLocX1 > 0.0f) {
            this.animLocX1 -= 0.32f / (animspeed * 3.0f);
        }
        if (this.g2) {
            if (this.animLocX2 < animMaxX1) {
                this.animLocX2 += 0.32f / (animspeed * 3.0f);
            }
        } else if (this.animLocX2 <= animMaxX1 && this.animLocX2 > 0.0f) {
            this.animLocX2 -= 0.32f / (animspeed * 3.0f);
        }
        if (this.g3) {
            if (this.animLocX3 < animMaxX1) {
                this.animLocX3 += 0.32f / (animspeed * 3.0f);
            }
        } else if (this.animLocX3 <= animMaxX1 && this.animLocX3 > 0.0f) {
            this.animLocX3 -= 0.32f / (animspeed * 3.0f);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        return bb;
    }
}

