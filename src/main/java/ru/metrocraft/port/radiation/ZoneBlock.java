package ru.metrocraft.port.radiation;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import java.util.Random;
public class ZoneBlock extends Block {
    public final RadiationSystem.Zone zone;
    public ZoneBlock(RadiationSystem.Zone zone){super(Material.BARRIER);this.zone=zone;setBlockUnbreakable();setResistance(6000000);}
    @Override public boolean isOpaqueCube(IBlockState state){return false;}
    @Override public boolean isFullCube(IBlockState state){return false;}
    @Override public EnumBlockRenderType getRenderType(IBlockState state){return EnumBlockRenderType.INVISIBLE;}
    @Override public AxisAlignedBB getCollisionBoundingBox(IBlockState state,IBlockAccess world,BlockPos pos){return NULL_AABB;}
    @Override public boolean hasTileEntity(IBlockState state){return true;}
    @Override public TileEntity createTileEntity(World world,IBlockState state){return new ZoneTile();}
    @Override public void onBlockAdded(World world,BlockPos pos,IBlockState state){RadiationSystem.register(world,pos,zone);world.scheduleUpdate(pos,this,20);}
    @Override public void updateTick(World world,BlockPos pos,IBlockState state,Random random){
        // The original radiation ID retains its scheduled ticks: also upgrades old chunks without a tile.
        if(!world.isRemote){world.getTileEntity(pos);RadiationSystem.register(world,pos,zone);world.scheduleUpdate(pos,this,20);}
    }
    @Override public void breakBlock(World world,BlockPos pos,IBlockState state){RadiationSystem.remove(world,pos);super.breakBlock(world,pos,state);}
    @Override @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    public void randomDisplayTick(IBlockState state,World world,BlockPos pos,Random random){
        net.minecraft.entity.player.EntityPlayer p=net.minecraft.client.Minecraft.getMinecraft().player;
        if(p!=null && p.capabilities.isCreativeMode && (p.getHeldItemMainhand().getItem()==net.minecraft.item.Item.getItemFromBlock(this) || p.getHeldItemOffhand().getItem()==net.minecraft.item.Item.getItemFromBlock(this)))
            world.spawnParticle(EnumParticleTypes.BARRIER,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,0,0,0);
    }
}
