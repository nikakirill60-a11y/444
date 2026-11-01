package ru.metrocraft.port.blackrazid;

import java.util.*;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import ru.metrocraft.port.blackrazid.transport.*;

public class RailsTest {
    @BeforeClass public static void boot(){NativeWeaponsTest.boot();BlackrazidRails.initialize();}
    /** Models vanilla's important ordering: state is already changed when breakBlock sees the old tile. */
    private static final class MapWorld {
        final EntityPlayer player;final World world;final Map<BlockPos,IBlockState> states=new HashMap<>();final Map<BlockPos,TileEntity> tiles=new HashMap<>();
        int writes,failAt=-1;
        MapWorld(boolean client)throws Exception{
            player=NativeWeaponsTest.player(client);world=player.world;
            when(world.getWorldBorder()).thenReturn(new net.minecraft.world.border.WorldBorder());when(world.isBlockLoaded(any(BlockPos.class))).thenReturn(true);
            when(world.isBlockModifiable(eq(player),any(BlockPos.class))).thenReturn(true);when(player.canPlayerEdit(any(BlockPos.class),any(EnumFacing.class),any(ItemStack.class))).thenReturn(true);
            when(world.getBlockState(any(BlockPos.class))).thenAnswer(c->states.getOrDefault(c.getArgument(0),Blocks.AIR.getDefaultState()));
            when(world.getTileEntity(any(BlockPos.class))).thenAnswer(c->tiles.get(c.getArgument(0)));
            when(world.setBlockState(any(BlockPos.class),any(IBlockState.class),anyInt())).thenAnswer(c->{
                BlockPos p=((BlockPos)c.getArgument(0)).toImmutable();IBlockState next=c.getArgument(1),old=states.getOrDefault(p,Blocks.AIR.getDefaultState());
                if(++writes==failAt)return false;states.put(p,next);
                if(old.getBlock() instanceof WideRailBlock&&old.getBlock()!=next.getBlock())old.getBlock().breakBlock(world,p,old);
                tiles.remove(p);
                if(next.getBlock().hasTileEntity(next)){TileEntity t=next.getBlock().createTileEntity(world,next);t.setWorld(world);t.setPos(p);tiles.put(p,t);}
                return true;
            });
            doAnswer(c->{tiles.remove(c.getArgument(0));return null;}).when(world).removeTileEntity(any(BlockPos.class));
            when(world.setBlockToAir(any(BlockPos.class))).thenAnswer(c->world.setBlockState(c.getArgument(0),Blocks.AIR.getDefaultState(),3));
        }
        void support(BlockPos origin,int shape,int dir){for(BlockPos offset:RailRules.footprint(shape,dir))if(offset.getY()==0)states.put(origin.add(offset).down(),Blocks.STONE.getDefaultState());}
        ItemStack kit(int shape){return new ItemStack(BlackrazidRails.KIT,1,shape);}
        boolean place(BlockPos p,int shape,int dir,ItemStack stack){support(p,shape,dir);return RailPlacement.place(world,p,shape,dir,player,stack);}
    }
    @Test public void allNativeFootprintsRotateWithoutDuplicatesAndContainPrimary(){
        for(int shape=0;shape<12;shape++)for(int dir=0;dir<4;dir++){
            List<BlockPos> cells=RailRules.footprint(shape,dir);assertFalse(cells.isEmpty());assertEquals(BlockPos.ORIGIN,cells.get(0));assertEquals(cells.size(),new HashSet<>(cells).size());assertEquals(RailRules.footprint(shape,0).size(),cells.size());
        }
        assertEquals(3,RailRules.footprint(0,0).size());assertEquals(new BlockPos(-2,4,1),RailRules.rotate(1,4,2,1));assertFalse(RailRules.valid(12));assertFalse(RailRules.valid(-1));
    }
    @Test public void realPlacementCreatesAndLinksAllSourceCellsInAllDirections()throws Exception{
        for(int shape=0;shape<12;shape++)for(int dir=0;dir<4;dir++){
            MapWorld map=new MapWorld(false);BlockPos p=new BlockPos(100,60,100);ItemStack kit=map.kit(shape);assertTrue(map.place(p,shape,dir,kit));assertTrue(kit.isEmpty());
            RailTile main=RailPlacement.primary(map.world,p);assertNotNull(main);assertEquals(shape,main.shape());assertEquals(dir,main.direction());
            for(BlockPos offset:RailRules.footprint(shape,dir)){RailTile tile=RailPlacement.at(map.world,p.add(offset));assertSame(main,RailPlacement.primary(map.world,p.add(offset)));if(!offset.equals(BlockPos.ORIGIN))assertTrue(main.owns(tile));}
        }
    }
    @Test public void creativePlacementDoesNotConsumeKit()throws Exception{MapWorld m=new MapWorld(false);m.player.capabilities.isCreativeMode=true;ItemStack kit=m.kit(0);assertTrue(m.place(new BlockPos(0,60,0),0,0,kit));assertEquals(1,kit.getCount());}
    @Test public void clientPlacementNeverWritesOrConsumes()throws Exception{MapWorld m=new MapWorld(true);ItemStack kit=m.kit(0);assertFalse(m.place(new BlockPos(0,60,0),0,0,kit));assertEquals(1,kit.getCount());assertEquals(0,m.writes);}
    @Test public void spectatorAndInvalidMetadataNeverPlace()throws Exception{MapWorld m=new MapWorld(false);when(m.player.isSpectator()).thenReturn(true);assertFalse(m.place(new BlockPos(0,60,0),0,0,m.kit(0)));assertFalse(m.place(new BlockPos(0,60,0),50,0,m.kit(0)));assertEquals(0,m.writes);}
    @Test public void obstacleInFarFootprintRejectsBeforeFirstWrite()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0);m.support(p,3,0);BlockPos far=p.add(RailRules.footprint(3,0).get(40));m.states.put(far,Blocks.STONE.getDefaultState());ItemStack kit=m.kit(3);assertFalse(RailPlacement.place(m.world,p,3,0,m.player,kit));assertEquals(0,m.writes);assertEquals(1,kit.getCount());}
    @Test public void unsupportedRemoteCellRejectsEntirePlacement()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0);m.support(p,4,0);m.states.remove(p.add(RailRules.footprint(4,0).get(40)).down());assertFalse(RailPlacement.place(m.world,p,4,0,m.player,m.kit(4)));assertEquals(0,m.writes);}
    @Test public void protectedRemoteCellIsNotBypassedByMultiBlockPlacement()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0),cell=p.add(RailRules.footprint(3,0).get(40));when(m.world.isBlockModifiable(m.player,cell)).thenReturn(false);assertFalse(m.place(p,3,0,m.kit(3)));assertEquals(0,m.writes);}
    @Test public void unloadedCellDoesNotLoadChunkOrPartiallyPlace()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0),cell=p.add(-1,0,0);when(m.world.isBlockLoaded(cell)).thenReturn(false);assertFalse(m.place(p,0,0,m.kit(0)));assertEquals(0,m.writes);}
    @Test public void existingTileIsNeverOverwrittenEvenInReplaceableState()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0);m.tiles.put(p,mock(TileEntity.class));assertFalse(m.place(p,0,0,m.kit(0)));assertEquals(0,m.writes);}
    @Test public void failedCommitRollsBackAllWrittenCellsWithoutConsuming()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0);ItemStack kit=m.kit(1);m.failAt=5;assertFalse(m.place(p,1,0,kit));assertEquals(1,kit.getCount());assertTrue(m.tiles.isEmpty());for(BlockPos d:RailRules.footprint(1,0))assertSame(Blocks.AIR,m.world.getBlockState(p.add(d)).getBlock());}
    @Test public void proxyBreakRemovesEntireMatchingStructure()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0);assertTrue(m.place(p,3,0,m.kit(3)));m.world.setBlockToAir(p.add(RailRules.footprint(3,0).get(40)));assertTrue(m.tiles.isEmpty());}
    @Test public void primaryBreakWorksAfterVanillaAlreadyChangedItsState()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0);assertTrue(m.place(p,0,0,m.kit(0)));m.world.setBlockToAir(p);assertTrue(m.tiles.isEmpty());}
    @Test public void replacingOrUnrelatedBlockIsNotDeletedByOldOwnerCleanup()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0),other=p.add(1,0,0);assertTrue(m.place(p,0,0,m.kit(0)));m.states.put(other,Blocks.STONE.getDefaultState());m.tiles.remove(other);RailPlacement.remove(m.world,p);assertSame(Blocks.STONE,m.world.getBlockState(other).getBlock());}
    @Test public void samePositionDifferentIdentityCannotBeHijackedByStaleProxy()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0);assertTrue(m.place(p,0,0,m.kit(0)));RailTile main=RailPlacement.at(m.world,p),child=RailPlacement.at(m.world,p.add(1,0,0));main.configure(true,0,0,p,UUID.randomUUID());assertFalse(main.owns(child));assertNull(RailPlacement.primary(m.world,child.getPos()));}
    @Test public void worldOriginIsValidOwnerNotAnUnsetSentinel()throws Exception{MapWorld m=new MapWorld(false);assertTrue(m.place(BlockPos.ORIGIN,0,0,m.kit(0)));assertNotNull(RailPlacement.primary(m.world,new BlockPos(1,0,0)));}
    @Test public void redstoneChangesServerTileAndSynchronizesIt()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0);assertTrue(m.place(p,5,0,m.kit(5)));RailTile tile=RailPlacement.at(m.world,p);assertFalse(tile.active());when(m.world.isBlockPowered(p)).thenReturn(true);tile.updatePower();assertTrue(tile.active());assertTrue(tile.getUpdateTag().getBoolean("Active"));when(m.world.isBlockPowered(p)).thenReturn(false);tile.updatePower();assertFalse(tile.active());}
    @Test public void tileRoundTripKeepsShapeDirectionPowerAndOwnership(){RailTile root=new RailTile(true);root.setPos(new BlockPos(0,60,0));UUID id=UUID.randomUUID();root.configure(true,11,3,root.getPos(),id);NBTTagCompound tag=root.writeToNBT(new NBTTagCompound());RailTile copy=new RailTile();copy.readFromNBT(tag);assertEquals(11,copy.shape());assertEquals(3,copy.direction());assertEquals(id,copy.identity());assertEquals(root.getPos(),copy.owner());assertNotNull(copy.getUpdatePacket());}
    @Test public void malformedTileNbtCannotIndexOutsideNativeShapes(){RailTile t=new RailTile();NBTTagCompound tag=new NBTTagCompound();tag.setInteger("Shape",Integer.MAX_VALUE);tag.setInteger("Direction",-1);tag.setString("RailIdentity","not uuid");t.readFromNBT(tag);assertEquals(0,t.shape());assertEquals(3,t.direction());assertNotNull(t.identity());}
    @Test public void lossOfSupportRemovesWholeFootprint()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0);assertTrue(m.place(p,0,0,m.kit(0)));m.states.remove(p.add(1,-1,0));RailPlacement.neighbor(m.world,p.add(1,0,0));assertTrue(m.tiles.isEmpty());}
    @Test public void unloadedSupportDoesNotDestroyAValidRail()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0),floor=p.add(1,-1,0);assertTrue(m.place(p,0,0,m.kit(0)));m.states.remove(floor);when(m.world.isBlockLoaded(floor)).thenReturn(false);RailPlacement.neighbor(m.world,p);assertEquals(3,m.tiles.size());}
    @Test public void deadEndKeepsSourceStopHeightAndStraightStaysLow()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0);assertTrue(m.place(p,2,0,m.kit(2)));assertEquals(1.375,BlackrazidRails.RAIL.getBoundingBox(m.world.getBlockState(p),m.world,p).maxY,0);BlockPos helper=p.add(1,0,0);assertEquals(.05,BlackrazidRails.PROXY.getBoundingBox(m.world.getBlockState(helper),m.world,helper).maxY,0);}
    @Test public void sourceStraightFramesSnapPerpendicularCoordinate(){for(int d=0;d<4;d++){BlockPos p=new BlockPos(20,60,20);RailRules.Frame f=RailRules.frame(0,d,false,p,new Vec3d(20.8,61,20.9),new Vec3d(0,0,1));assertEquals(60.375,f.position.y,0);assertEquals(1,f.tangent.length(),.00001);if(d%2==0)assertEquals(20.5,f.position.x,0);else assertEquals(20.5,f.position.z,0);}}
    @Test public void turnoutBranchIsExplicitAndNotReadMidCurve(){BlockPos p=BlockPos.ORIGIN;Vec3d at=new Vec3d(-3,.375,5),heading=new Vec3d(0,0,1);RailRules.Frame line=RailRules.frame(5,0,false,p,at,heading),turn=RailRules.frame(5,0,true,p,at,heading);assertEquals(.5,line.position.x,0);assertNotEquals(line.position.x,turn.position.x,.01);assertEquals(1,turn.tangent.length(),.00001);}
    @Test public void allCurveFramesAreFiniteIncludingAtCircleCenter(){for(int s:new int[]{3,4,5,6,7,8,9,10,11})for(int d=0;d<4;d++){RailRules.Frame f=RailRules.frame(s,d,true,BlockPos.ORIGIN,new Vec3d(-15,0,0),new Vec3d(0,0,1));assertTrue(Double.isFinite(f.position.x));assertTrue(Double.isFinite(f.tangent.x));assertEquals(1,f.tangent.length(),.00001);}assertNull(RailRules.frame(100,0,false,BlockPos.ORIGIN,Vec3d.ZERO,Vec3d.ZERO));}
    @Test public void materialAwareParserKeepsEverySourceTriangleAndThreeAtlases()throws Exception{for(String name:new String[]{"rails","rails90","railsx"})try(java.io.InputStream a=getClass().getResourceAsStream("/assets/metroport/models/blackrazid/rails/"+name+".obj");java.io.InputStream b=getClass().getResourceAsStream("/assets/metroport/models/blackrazid/rails/"+name+".obj")){ru.metrocraft.port.mcmod.McmodObjMesh original=ru.metrocraft.port.mcmod.McmodObjMesh.read(a),materials=ru.metrocraft.port.mcmod.McmodObjMesh.readMaterials(b);assertEquals(original.groups.values().stream().mapToInt(List::size).sum(),materials.groups.values().stream().mapToInt(List::size).sum());assertTrue(materials.groups.size()>=3);for(List<float[]> part:materials.groups.values()){assertFalse(part.isEmpty());assertEquals(0,part.size()%3);}}}
    @Test public void breakingRemoteProxyCannotBypassProtectedPrimary()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0),proxy=p.add(1,0,0);assertTrue(m.place(p,0,0,m.kit(0)));when(m.world.isBlockModifiable(m.player,p)).thenReturn(false);assertFalse(BlackrazidRails.PROXY.removedByPlayer(m.world.getBlockState(proxy),m.world,proxy,m.player,true));assertEquals(3,m.tiles.size());}
    @Test public void centerFallbackIgnoresVerticalHeadingAndStillHasUnitTangent(){RailRules.Frame f=RailRules.frame(3,0,true,BlockPos.ORIGIN,new Vec3d(-15,0,0),new Vec3d(0,1,0));assertNotNull(f);assertEquals(1,f.tangent.length(),.00001);}

    @Test public void cleanupSkipsUnloadedCellsWithoutReadingTheirBlockState()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0),unloaded=p.add(1,0,0);assertTrue(m.place(p,0,0,m.kit(0)));clearInvocations(m.world);when(m.world.isBlockLoaded(unloaded)).thenReturn(false);RailPlacement.remove(m.world,p);verify(m.world,never()).getBlockState(unloaded);assertEquals(1,m.tiles.size());}

    @Test public void orphanProxyDefersUnloadedOwnerThenCleansAfterOwnerIsKnownMissing()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0),proxy=p.add(1,0,0);assertTrue(m.place(p,0,0,m.kit(0)));m.tiles.remove(p);m.states.put(p,Blocks.AIR.getDefaultState());clearInvocations(m.world);when(m.world.isBlockLoaded(p)).thenReturn(false);BlackrazidRails.PROXY.updateTick(m.world,proxy,m.world.getBlockState(proxy),new Random(1));assertNotNull(m.tiles.get(proxy));verify(m.world).scheduleUpdate(proxy,BlackrazidRails.PROXY,20);verify(m.world,never()).getBlockState(p);when(m.world.isBlockLoaded(p)).thenReturn(true);BlackrazidRails.PROXY.updateTick(m.world,proxy,m.world.getBlockState(proxy),new Random(1));assertNull(m.tiles.get(proxy));}
    @Test public void primaryLoadValidationRemovesStructureWithMissingHelper()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0),missing=p.add(1,0,0);assertTrue(m.place(p,0,0,m.kit(0)));m.tiles.remove(missing);m.states.put(missing,Blocks.AIR.getDefaultState());BlackrazidRails.RAIL.updateTick(m.world,p,m.world.getBlockState(p),new Random(1));assertTrue(m.tiles.isEmpty());}
    @Test public void rollbackNeverDeletesAnExternallyReplacedRailIdentity()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0);UUID replacement=UUID.randomUUID();ItemStack kit=m.kit(1);m.failAt=3;doAnswer(c->{if(m.writes==2&&!replacement.equals(RailPlacement.at(m.world,p).identity()))RailPlacement.at(m.world,p).configure(true,0,0,p,replacement);return null;}).when(m.world).notifyBlockUpdate(any(BlockPos.class),any(IBlockState.class),any(IBlockState.class),anyInt());assertFalse(m.place(p,1,0,kit));assertEquals(replacement,RailPlacement.at(m.world,p).identity());assertSame(BlackrazidRails.RAIL,m.world.getBlockState(p).getBlock());assertEquals(1,kit.getCount());}

    @Test public void buildHeightNotNetherTerrainCeilingAndBorderAreRespected()throws Exception{MapWorld m=new MapWorld(false);when(m.world.getHeight()).thenReturn(256);when(m.world.getActualHeight()).thenReturn(128);assertTrue(m.place(new BlockPos(0,200,0),0,0,m.kit(0)));for(BlockPos denied:Arrays.asList(new BlockPos(0,-1,0),new BlockPos(0,256,0),new BlockPos(30000000,60,0))){MapWorld n=new MapWorld(false);assertFalse(n.place(denied,0,0,n.kit(0)));assertEquals(0,n.writes);}}

    @Test public void redstoneNeverQueriesPowerAcrossAnUnloadedNeighbor()throws Exception{MapWorld m=new MapWorld(false);BlockPos p=new BlockPos(0,60,0),neighbor=p.up();assertTrue(m.place(p,5,0,m.kit(5)));RailTile root=RailPlacement.at(m.world,p);clearInvocations(m.world);when(m.world.isBlockLoaded(neighbor)).thenReturn(false);root.updatePower();verify(m.world,never()).isBlockPowered(any(BlockPos.class));verify(m.world).scheduleUpdate(p,BlackrazidRails.RAIL,20);when(m.world.isBlockLoaded(neighbor)).thenReturn(true);when(m.world.isBlockPowered(p)).thenReturn(true);root.updatePower();assertTrue(root.active());}

}
