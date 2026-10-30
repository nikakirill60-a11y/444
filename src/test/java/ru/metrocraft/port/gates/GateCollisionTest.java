package ru.metrocraft.port.gates;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

/** Exercises the actual Java shape/ray code without starting a game or a world. */
public class GateCollisionTest {
    private GateTile tile(GateType type, float progress, EnumFacing facing) {
        GateTile tile = new GateTile(); tile.setPos(new BlockPos(10,64,10));
        tile.type = type; tile.progress = progress; tile.facing = facing; return tile;
    }
    private List<AxisAlignedBB> cell(GateTile tile, int width, int y, int depth) {
        BlockPos p = tile.getPos().offset(tile.facing.rotateYCCW(),width).up(y).offset(tile.facing,depth);
        List<AxisAlignedBB> result = new ArrayList<>(); tile.addCollision(p,new AxisAlignedBB(p),result); return result;
    }
    @Test public void liftGateFreesThePassageButRetainsTheHeader() {
        for (EnumFacing facing : new EnumFacing[]{EnumFacing.SOUTH,EnumFacing.WEST,EnumFacing.NORTH,EnumFacing.EAST}) {
            GateTile closed = tile(GateType.MAIN,0,facing), open = tile(GateType.MAIN,1,facing);
            assertFalse(cell(closed,0,1,0).isEmpty()); assertTrue(cell(open,0,1,0).isEmpty());
            assertFalse(cell(open,0,3,0).isEmpty()); assertFalse(cell(open,0,5,0).isEmpty());
            assertTrue(cell(closed,0,5,0).isEmpty());
        }
    }
    @Test public void swingGateDoesNotKeepAHugeAabbAcrossTheOpenPassage() {
        for (GateType type : new GateType[]{GateType.LEFT,GateType.LEFT45,GateType.HINGED})
            for (EnumFacing facing : new EnumFacing[]{EnumFacing.SOUTH,EnumFacing.WEST,EnumFacing.NORTH,EnumFacing.EAST}) {
                assertFalse(cell(tile(type,0,facing),0,1,0).isEmpty());
                assertTrue(cell(tile(type,1,facing),0,1,0).isEmpty());
            }
    }
    @Test public void rayTraceHitsTheClosedLeafNotInvisibleClearance() {
        GateTile closed = tile(GateType.MAIN,0,EnumFacing.SOUTH), open = tile(GateType.MAIN,1,EnumFacing.SOUTH);
        BlockPos cell = closed.getPos().up(); Vec3d start = new Vec3d(10.5,65.5,7), end = new Vec3d(10.5,65.5,14);
        assertNotNull(closed.rayTrace(cell,start,end)); assertNull(open.rayTrace(cell,start,end));
    }
    @Test public void collisionIsClippedToTheQueriedCell() {
        GateTile tile = tile(GateType.LEFT,.5F,EnumFacing.EAST);
        for (int w=-3;w<=3;w++) for(int d=-1;d<=5;d++) for(int y=0;y<4;y++) {
            BlockPos pos = tile.getPos().offset(tile.facing.rotateYCCW(),w).up(y).offset(tile.facing,d);
            AxisAlignedBB bounds = new AxisAlignedBB(pos);
            for (AxisAlignedBB shape : cell(tile,w,y,d)) {
                assertTrue(shape.minX>=bounds.minX && shape.maxX<=bounds.maxX);
                assertTrue(shape.minY>=bounds.minY && shape.maxY<=bounds.maxY);
                assertTrue(shape.minZ>=bounds.minZ && shape.maxZ<=bounds.maxZ);
            }
        }
    }
}
