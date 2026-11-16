package ru.metrocraft.port.metromod.decor;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.util.WeakIdentityMap;
import java.util.*;

/** Physics is owned by the same controller/pose as rendering, not by delivery of
 * a separate ownership packet for every invisible proxy. Side-local weak index:
 * no client imports, no chunk loads, no scan through all proxy tile entities.
 */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class OriginalDecorCollisions {
    private static final WeakIdentityMap<World,Set<OriginalGateTile>> ROOTS=new WeakIdentityMap<>();
    private OriginalDecorCollisions() {}
    public static void track(OriginalGateTile tile) {
        if(tile.getWorld()==null)return;
        Set<OriginalGateTile> set=ROOTS.computeIfAbsent(tile.getWorld(),w->Collections.newSetFromMap(new WeakHashMap<OriginalGateTile,Boolean>()));
        synchronized(set){set.add(tile);}
    }
    public static void untrack(OriginalGateTile tile) {
        if(tile.getWorld()==null)return;Set<OriginalGateTile> set=ROOTS.get(tile.getWorld());if(set==null)return;
        synchronized(set){set.remove(tile);}
    }
    private static List<OriginalGateTile> roots(World world) {
        Set<OriginalGateTile> set=ROOTS.get(world);if(set==null)return Collections.emptyList();
        synchronized(set){return new ArrayList<>(set);}
    }
    private static boolean valid(World world,OriginalGateTile root) {
        return root.ready && !root.removing && root.owner!=null && !root.isInvalid()
                && world.isBlockLoaded(root.getPos()) && world.getTileEntity(root.getPos())==root
                && world.getBlockState(root.getPos()).getBlock() instanceof OriginalGateBlock;
    }
    @SubscribeEvent public static void gather(GetCollisionBoxesEvent event) {
        add(event.getWorld(),event.getAabb(),event.getCollisionBoxesList());
    }
    public static void add(World world,AxisAlignedBB query,List<AxisAlignedBB> out) {
        for(OriginalGateTile root:roots(world))if(valid(world,root) && root.getRenderBoundingBox().intersects(query))
            for(AxisAlignedBB box:root.collisionBoxes())if(box.intersects(query) && !out.contains(box))out.add(box);
    }
    /** Recover only an uninitialized proxy. NEVER adopt a foreign UUID. */
    public static OriginalGateTile pendingController(World world,BlockPos cell) {
        if(!world.isBlockLoaded(cell) || world.getBlockState(cell).getBlock()!=OriginalGates.PART)return null;
        TileEntity part=world.getTileEntity(cell);
        if(part instanceof OriginalGatePartTile && ((OriginalGatePartTile)part).owner!=null)return null;
        OriginalGateTile found=null;
        for(OriginalGateTile root:roots(world))if(valid(world,root) && root.occupied().contains(cell)) {
            if(found!=null)return null;found=root;
        }
        return found;
    }
    public static AxisAlignedBB localCollision(World world,BlockPos cell) {
        List<AxisAlignedBB> boxes=new ArrayList<>();AxisAlignedBB cube=new AxisAlignedBB(cell),result=null;
        add(world,cube,boxes);
        for(AxisAlignedBB box:boxes) {AxisAlignedBB clipped=box.intersect(cube);result=result==null?clipped:result.union(clipped);}
        return result==null?null:result.offset(-cell.getX(),-cell.getY(),-cell.getZ());
    }
}
