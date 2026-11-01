package ru.metrocraft.port.radiation;
import net.minecraft.tileentity.TileEntity;
public final class ZoneTile extends TileEntity {
    @Override public void onLoad(){if(world.getBlockState(pos).getBlock() instanceof ZoneBlock)RadiationSystem.register(world,pos,((ZoneBlock)world.getBlockState(pos).getBlock()).zone);}
    @Override public void invalidate(){if(world!=null)RadiationSystem.remove(world,pos);super.invalidate();}
    @Override public void onChunkUnload(){if(world!=null)RadiationSystem.remove(world,pos);super.onChunkUnload();}
}
