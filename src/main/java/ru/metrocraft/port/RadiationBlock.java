package ru.metrocraft.port;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import java.util.Random;

/** An invisible map-maker radiation source, matching the legacy 10-block bounding volume. */
public final class RadiationBlock extends Block {
    private static final DamageSource RADIATION = new DamageSource("metroRadiation").setDamageBypassesArmor();
    public RadiationBlock() { super(Material.AIR); setBlockUnbreakable(); setResistance(6000000); }
    @Override public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state) { return false; }
    @Override public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.INVISIBLE; }
    @Override public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) { return NULL_AABB; }
    @Override public void onBlockAdded(World world, BlockPos pos, IBlockState state) { world.scheduleUpdate(pos, this, 20); }
    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (world.isRemote) return;
        for (EntityPlayer player : world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos).grow(10))) {
            if (player.capabilities.isCreativeMode || player.isSpectator()) continue;
            if (!protectedBy(player, EntityEquipmentSlot.HEAD) && !protectedBy(player, EntityEquipmentSlot.CHEST)) {
                player.attackEntityFrom(RADIATION, 3);
            }
        }
        world.scheduleUpdate(pos, this, 20);
    }
    private boolean protectedBy(EntityPlayer player, EntityEquipmentSlot slot) {
        ItemStack stack = player.getItemStackFromSlot(slot);
        return stack.getItem() instanceof MetroArmor && ((MetroArmor)stack.getItem()).isGasMask()
                && stack.hasTagCompound() && stack.getTagCompound().getInteger("MetroFilterTicks") > 0;
    }
}
