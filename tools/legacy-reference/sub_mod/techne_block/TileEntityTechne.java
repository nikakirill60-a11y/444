/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.AxisAlignedBB
 */
package sub_mod.techne_block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityTechne
extends TileEntity {
    public int timer = 0;
    public boolean sound = true;
    public boolean sound1 = false;
    public boolean sound2 = false;
    public boolean sound3 = false;
    public float runProgress = 0.0f;
    public float lastrunProgress = 0.0f;

    public void func_145845_h() {
        this.lastrunProgress = this.runProgress;
        if (this.field_145850_b.func_72864_z(this.field_145851_c, this.field_145848_d, this.field_145849_e)) {
            if (this.timer < 301) {
                ++this.timer;
            }
            if (this.timer == 160) {
                this.sound2 = true;
            }
            if (this.timer > 160) {
                this.sound2 = false;
            }
            this.runProgress = 1.0f - (1.0f - this.runProgress) * 0.995f;
            if ((double)this.runProgress > 0.5) {
                this.runProgress = 1.0f - (1.0f - this.runProgress) * 0.995f;
                if ((double)this.runProgress > 0.8) {
                    this.runProgress = 1.0f - (1.0f - this.runProgress) * 0.985f;
                    if ((double)this.runProgress > 0.94) {
                        this.runProgress = 1.0f - (1.0f - this.runProgress) * 0.945f;
                    }
                }
            }
        } else {
            if (this.timer > 0) {
                --this.timer;
            }
            if (this.timer == 140) {
                this.sound3 = true;
            }
            if (this.timer < 140) {
                this.sound3 = false;
            }
            this.runProgress *= 0.995f;
            if ((double)this.runProgress < 0.5) {
                this.runProgress *= 0.995f;
                if ((double)this.runProgress < 0.2) {
                    this.runProgress *= 0.985f;
                    if ((double)this.runProgress < 0.05) {
                        this.runProgress *= 0.94f;
                    }
                }
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        return bb;
    }
}

