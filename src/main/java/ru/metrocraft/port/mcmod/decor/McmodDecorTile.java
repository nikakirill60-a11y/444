package ru.metrocraft.port.mcmod.decor;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

/** Entry and rotation live in the saved block state; no redundant, desynchronizable tile payload. */
public final class McmodDecorTile extends TileEntity {
    public int rotation() {
        return world!=null && world.getBlockState(pos).getBlock() instanceof McmodDecorBlock
                ?((McmodDecorBlock)world.getBlockState(pos).getBlock()).getMetaFromState(world.getBlockState(pos)):0;
    }
    @Override public AxisAlignedBB getRenderBoundingBox() {
        if(!(getBlockType() instanceof McmodDecorBlock))return new AxisAlignedBB(getPos());
        McmodDecorBlock block=(McmodDecorBlock)getBlockType();
        return DecorPose.bounds(block.entry,rotation()).offset(getPos());
    }
}
