package ru.metrocraft.port.gates;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Bootstrap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Executes the real server tick for all six leaves, without a renderer. */
public class GateMotionTest {
    @BeforeClass public static void boot(){Bootstrap.register();}
    private GateTile gate(GateType type,EnumFacing facing){
        World world=mock(World.class);GateTile tile=new GateTile();tile.setPos(new BlockPos(10,64,10));
        tile.configure(UUID.randomUUID(),type,facing);tile.ready=true;
        when(world.getBlockState(any(BlockPos.class))).thenReturn(net.minecraft.init.Blocks.STONE.getDefaultState());
        tile.setWorld(world);
        when(world.isBlockLoaded(any(BlockPos.class))).thenReturn(true);
        when(world.getEntitiesWithinAABBExcludingEntity(isNull(),any(AxisAlignedBB.class))).thenReturn(Collections.emptyList());
        for(BlockPos cell:Gates.footprint(tile.getPos(),facing,type))if(!cell.equals(tile.getPos())){
            GatePartTile part=new GatePartTile();part.configure(tile.getPos(),tile.owner);when(world.getTileEntity(cell)).thenReturn(part);
        }
        return tile;
    }
    @Test public void sixVariantsMoveOnlyWithLiveRedstoneInEveryFacing(){
        for(GateType type:GateType.values())for(EnumFacing facing:new EnumFacing[]{EnumFacing.SOUTH,EnumFacing.WEST,EnumFacing.NORTH,EnumFacing.EAST}){
            GateTile tile=gate(type,facing);tile.update();assertEquals(0,tile.progress,0);
            when(tile.getWorld().isBlockPowered(tile.getPos())).thenReturn(true);
            tile.update();assertEquals(.015F,tile.progress,.000001F);
            for(int i=0;i<70;i++)tile.update();assertEquals(1,tile.progress,0);
            when(tile.getWorld().isBlockPowered(tile.getPos())).thenReturn(false);
            tile.update();assertEquals(.985F,tile.progress,.000001F);
            for(int i=0;i<70;i++)tile.update();assertEquals(0,tile.progress,0);
        }
    }
    @Test public void powerOnAnyFootprintCellOpensEachVariant(){
        for(GateType type:GateType.values()){
            GateTile tile=gate(type,EnumFacing.NORTH);BlockPos cell=Gates.footprint(tile.getPos(),tile.facing,type).get(0);
            when(tile.getWorld().isBlockPowered(cell)).thenReturn(true);tile.update();assertTrue(tile.progress>0);
        }
    }
    @Test public void livingObstructionStopsBothDirectionsButNotNoClipOrDeadEntities(){
        for(GateType type:GateType.values()){
            GateTile tile=gate(type,EnumFacing.SOUTH);tile.progress=.5F;EntityLivingBase mob=mock(EntityLivingBase.class);when(mob.isEntityAlive()).thenReturn(true);
            when(tile.getWorld().getEntitiesWithinAABBExcludingEntity(isNull(),any(AxisAlignedBB.class))).thenReturn(Collections.<Entity>singletonList(mob));
            tile.update();assertEquals(.5F,tile.progress,0);when(tile.getWorld().isBlockPowered(tile.getPos())).thenReturn(true);
            tile.update();assertEquals(.5F,tile.progress,0);mob.noClip=true;tile.update();assertTrue(tile.progress>.5F);
            mob.noClip=false;when(mob.isEntityAlive()).thenReturn(false);float before=tile.progress;tile.update();assertTrue(tile.progress>before);
        }
    }
    @Test public void unloadedPartStopsAndReloadRechecksPowerBeforeMoving(){
        for(GateType type:GateType.values()){
            GateTile tile=gate(type,EnumFacing.EAST);BlockPos cell=Gates.footprint(tile.getPos(),tile.facing,type).get(0);
            when(tile.getWorld().isBlockPowered(tile.getPos())).thenReturn(true);when(tile.getWorld().isBlockLoaded(cell)).thenReturn(false);
            tile.update();assertEquals(0,tile.progress,0);when(tile.getWorld().isBlockLoaded(cell)).thenReturn(true);tile.update();assertTrue(tile.progress>0);
        }
    }
    @Test public void sameOwnerPartFromAnotherOriginIsNotAccepted(){
        GateTile tile=gate(GateType.MAIN,EnumFacing.SOUTH);BlockPos cell=Gates.footprint(tile.getPos(),tile.facing,tile.type).get(0);
        GatePartTile foreign=new GatePartTile();foreign.configure(tile.getPos().add(20,0,0),tile.owner);when(tile.getWorld().getTileEntity(cell)).thenReturn(foreign);
        tile.update();verify(tile.getWorld()).destroyBlock(tile.getPos(),true);assertEquals(0,tile.progress,0);
    }
    @Test public void invalidSavedProgressCannotPoisonAnimationOrCollision(){
        for(float value:new float[]{Float.NaN,Float.POSITIVE_INFINITY,Float.NEGATIVE_INFINITY}){
            NBTTagCompound tag=new NBTTagCompound();tag.setFloat("Progress",value);GateTile tile=new GateTile();tile.readFromNBT(tag);assertEquals(0,tile.progress,0);
        }
    }
}
