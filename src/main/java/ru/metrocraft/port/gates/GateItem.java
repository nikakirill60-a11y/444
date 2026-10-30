package ru.metrocraft.port.gates;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import java.util.List;
import java.util.UUID;

/** Validate the whole structure BEFORE changing any cell or consuming an item. */
public final class GateItem extends ItemBlock {
    private final GateBlock gate;
    public GateItem(GateBlock gate) { super(gate); this.gate = gate; setMaxStackSize(1); }
    @Override public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos clicked, EnumHand hand,
                                               EnumFacing face, float hitX, float hitY, float hitZ) {
        BlockPos origin = world.getBlockState(clicked).getBlock().isReplaceable(world, clicked) ? clicked : clicked.offset(face);
        EnumFacing facing = player.getHorizontalFacing().getOpposite();
        ItemStack stack = player.getHeldItem(hand);
        List<BlockPos> footprint = Gates.footprint(origin, facing, gate.type);
        for (BlockPos pos : footprint) {
            if (!world.isBlockLoaded(pos) || pos.getY() < 0 || pos.getY() >= world.getHeight()
                    || !world.getWorldBorder().contains(pos) || !world.isAirBlock(pos)
                    || !player.canPlayerEdit(pos, face, stack)
                    || !world.isBlockModifiable(player, pos)) {
                if (!world.isRemote) player.sendStatusMessage(new TextComponentTranslation("metroport.gate.obstructed"), true);
                return EnumActionResult.FAIL;
            }
        }
        // No building a closed gate through another player/mob.
        if (!world.checkNoEntityCollision(GateTile.closedBounds(origin, facing))) return EnumActionResult.FAIL;
        if (world.isRemote) return EnumActionResult.SUCCESS;
        UUID owner = UUID.randomUUID();
        java.util.Map<BlockPos, IBlockState> changed = new java.util.LinkedHashMap<>();
        boolean installed = false;
        try {
            for (BlockPos pos : footprint) {
                changed.put(pos, world.getBlockState(pos));
                boolean root = pos.equals(origin);
                if (!world.setBlockState(pos, root ? gate.getDefaultState().withProperty(GateBlock.FACING, facing)
                        : Gates.PART.getDefaultState(), 2)) return EnumActionResult.FAIL;
                if (root) {
                    GateTile tile = (GateTile) world.getTileEntity(pos);
                    tile.configure(owner, gate.type, facing, gate.type.initial);
                } else {
                    ((GatePartTile) world.getTileEntity(pos)).configure(origin, owner);
                }
            }
            GateTile tile = (GateTile) world.getTileEntity(origin);
            if (tile.blockedAt(tile.progress)) return EnumActionResult.FAIL;
            tile.ready = true; tile.markDirty(); tile.sync();
            installed = true;
        } finally {
            // Roll back OUR cells only. Never clear or replace someone else's block.
            if (!installed) for (java.util.Map.Entry<BlockPos,IBlockState> entry : changed.entrySet()) {
                net.minecraft.tileentity.TileEntity te = world.getTileEntity(entry.getKey());
                if ((te instanceof GateTile && owner.equals(((GateTile) te).owner))
                        || (te instanceof GatePartTile && owner.equals(((GatePartTile) te).owner)))
                    world.setBlockState(entry.getKey(), entry.getValue(), 2);
            }
        }
        world.notifyNeighborsOfStateChange(origin, gate, false);
        if (!player.capabilities.isCreativeMode) stack.shrink(1);
        return EnumActionResult.SUCCESS;
    }
}
