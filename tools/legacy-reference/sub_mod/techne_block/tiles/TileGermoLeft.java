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

public class TileGermoLeft
extends TileEntity {
    public int timer = 0;
    public float runProgress = 0.0f;
    public float lastrunProgress = 0.0f;

    public void func_145845_h() {
        this.lastrunProgress = this.runProgress;
        if (this.field_145850_b.func_72864_z(this.field_145851_c, this.field_145848_d, this.field_145849_e)) {
            if (this.timer < 160) {
                ++this.timer;
            }
            this.runProgress = 1.0f - (1.0f - this.runProgress) * 0.985f;
        } else {
            if (this.timer > 0) {
                --this.timer;
            }
            this.runProgress *= 0.985f;
        }
    }

    @SideOnly(value=Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        return bb;
    }
}

