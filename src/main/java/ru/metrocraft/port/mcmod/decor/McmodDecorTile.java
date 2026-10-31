package ru.metrocraft.port.mcmod.decor;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

/** Entry and rotation live in the saved block state; no redundant, desynchronizable tile payload. */
public final class McmodDecorTile extends TileEntity {
    @Override public AxisAlignedBB getRenderBoundingBox() {
        if(!(getBlockType() instanceof McmodDecorBlock))return new AxisAlignedBB(getPos());
        McmodDecorBlock block=(McmodDecorBlock)getBlockType();
        return DecorPose.bounds(block.entry,getBlockMetadata()).offset(getPos());
    }
}
