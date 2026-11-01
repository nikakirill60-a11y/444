package ru.metrocraft.port.blackrazid.environment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
/** Intended source lifetime is five server ticks. Fixes the original always-true ID != A || ID != B bug. */
public final class TransientLightTile extends TileEntity implements ITickable {
    private int remaining=5;
    public int remaining(){return remaining;}
    @Override public void update(){
        if(world==null||world.isRemote||!world.isBlockLoaded(pos)||world.getTileEntity(pos)!=this)return;
        if(!(world.getBlockState(pos).getBlock() instanceof TransientLightBlock))return;
        if(--remaining<=0)world.setBlockToAir(pos);else markDirty();
    }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound n){super.writeToNBT(n);n.setInteger("Remaining",remaining);return n;}
    @Override public void readFromNBT(NBTTagCompound n){super.readFromNBT(n);remaining=n.hasKey("Remaining",3)?Math.max(1,Math.min(5,n.getInteger("Remaining"))):5;}
}
