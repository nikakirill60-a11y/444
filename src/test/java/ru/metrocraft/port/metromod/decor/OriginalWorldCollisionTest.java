package ru.metrocraft.port.metromod.decor;

import net.minecraft.entity.*;
import net.minecraft.init.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Real World.getCollisionBoxes + real Entity.move, separate server/client worlds.
 * Only block storage/chunk availability is mocked. Proxy ownership can arrive late.
 * This is an engine integration test, NOT an OpenGL or full Minecraft-client run.
 */
public class OriginalWorldCollisionTest {
    private static int busId;
    private static net.minecraftforge.fml.common.eventhandler.ListenerList listeners;
    private static final net.minecraftforge.fml.common.eventhandler.IEventListener collisionListener=event->{
        if(event instanceof net.minecraftforge.event.world.GetCollisionBoxesEvent)
            OriginalDecorCollisions.gather((net.minecraftforge.event.world.GetCollisionBoxesEvent)event);
    };
    @BeforeClass public static void boot() throws Exception {
        Bootstrap.register();OriginalGates.initialize();
        // Plain JUnit does not run Forge's EventSubscriptionTransformer, which adds
        // the event's default constructor and ListenerList override. Register the
        // typed listener explicitly; World still posts the real Forge event.
        java.lang.reflect.Field id=net.minecraftforge.fml.common.eventhandler.EventBus.class.getDeclaredField("busID");id.setAccessible(true);
        busId=id.getInt(MinecraftForge.EVENT_BUS);
        listeners=new net.minecraftforge.event.world.GetCollisionBoxesEvent(null,null,null,new ArrayList<>()).getListenerList();
        listeners.register(busId,net.minecraftforge.fml.common.eventhandler.EventPriority.NORMAL,collisionListener);
    }
    @AfterClass public static void cleanup() {if(listeners!=null)listeners.unregister(busId,collisionListener);}
    private static class Walker extends Entity {
        Walker(World world) {super(null);this.world=world;setSize(.6F,1.8F);stepHeight=0;}
        @Override protected void entityInit() {}
        @Override protected void readEntityFromNBT(NBTTagCompound tag) {}
        @Override protected void writeEntityToNBT(NBTTagCompound tag) {}
        @Override protected boolean canTriggerWalking() {return false;}
    }
    private static class Scene {
        final OriginalGateTile root;
        final OriginalDecorTestWorld grid;
        Scene(OriginalGateProfile profile,EnumFacing facing,boolean client,boolean pending) throws Exception {
            root=new OriginalGateTile();root.setPos(new BlockPos(0,64,0));root.configure(UUID.randomUUID(),profile,facing);root.ready=true;
            root.setWorld(mock(World.class));grid=new OriginalDecorTestWorld(root);
            java.lang.reflect.Field remote=World.class.getField("isRemote");remote.setAccessible(true);remote.setBoolean(grid.world,client);
            java.lang.reflect.Field profiler=World.class.getField("profiler");profiler.setAccessible(true);profiler.set(grid.world,new Profiler());
            doCallRealMethod().when(grid.world).getCollisionBoxes(nullable(Entity.class),any(AxisAlignedBB.class));
            when(grid.world.getEntitiesWithinAABBExcludingEntity(nullable(Entity.class),any(AxisAlignedBB.class))).thenReturn(Collections.emptyList());
            root.onLoad();
            if(pending)for(TileEntity tile:grid.tiles.values())if(tile instanceof OriginalGatePartTile) {
                ((OriginalGatePartTile)tile).owner=null;((OriginalGatePartTile)tile).origin=BlockPos.ORIGIN;
            }
        }
        AxisAlignedBB leaf() {
            if(root.profile.donorDoor!=null)return root.doorBounds();
            return root.profile.boxes(root.getPos(),root.facing,0,true).stream().max(Comparator.comparingDouble(b->(b.maxX-b.minX)*(b.maxY-b.minY)+(b.maxZ-b.minZ)*(b.maxY-b.minY))).get();
        }
        Walker walk(boolean reverse) {
            AxisAlignedBB leaf=leaf();boolean alongX=leaf.maxX-leaf.minX<leaf.maxZ-leaf.minZ;
            double distance=(alongX?leaf.maxX-leaf.minX:leaf.maxZ-leaf.minZ)+2;
            double sign=reverse?-1:1;
            double x=(leaf.minX+leaf.maxX)/2,z=(leaf.minZ+leaf.maxZ)/2;
            Walker walker=new Walker(grid.world);walker.setPosition(x-(alongX?sign*distance/2:0),Math.min(leaf.maxY-.05,Math.max(leaf.minY+.05,65.02)),z-(alongX?0:sign*distance/2));
            walker.move(MoverType.SELF,alongX?sign*distance:0,0,alongX?0:sign*distance);
            return walker;
        }
    }
    private OriginalGateProfile gate() {return OriginalGateProfile.get("metromod_germo_1");}
    @Test public void delayedProxyOwnershipReproducesLegacyWalkthroughAndControllerFixBlocksIt() throws Exception {
        Scene scene=new Scene(gate(),EnumFacing.SOUTH,true,true);
        OriginalDecorCollisions.untrack(scene.root);
        assertFalse("legacy: solid block IDs but no resolved geometry",scene.walk(false).collidedHorizontally);
        OriginalDecorCollisions.track(scene.root);
        assertTrue("physics must not depend on each proxy packet",scene.walk(false).collidedHorizontally);
    }
    @Test public void all27DoorsStopRealEntityMoveBothSidesEveryFacingOnServerAndFreshClient() throws Exception {
        int count=0;
        for(OriginalGateProfile p:OriginalGateProfile.all())if(p.is("Door")) {
            count++;
            for(EnumFacing facing:EnumFacing.Plane.HORIZONTAL)for(boolean client:new boolean[]{false,true}) {
                Scene scene=new Scene(p,facing,client,client);
                for(boolean reverse:new boolean[]{false,true})assertTrue(p.id+" facing="+facing+" client="+client+" reverse="+reverse,scene.walk(reverse).collidedHorizontally);
                // Packet/save state: activation removes physics even before the
                // first animation frame; progress alone never opens collision.
                NBTTagCompound state=scene.root.writeToNBT(new NBTTagCompound());
                state.setBoolean("Manual",true);state.setFloat("Progress",0);scene.root.readFromNBT(state);
                for(boolean reverse:new boolean[]{false,true})assertFalse(p.id+" activated "+facing+" client="+client,scene.walk(reverse).collidedHorizontally);
                state.setBoolean("Manual",false);state.setFloat("Progress",1);scene.root.readFromNBT(state);
                assertTrue(p.id+" inactive but animX still open",scene.walk(false).collidedHorizontally);
            }
        }
        assertEquals(27,count);
    }
    @Test public void worldQueryWorksEvenWithEntireProxyBlockUpdateDelayed() throws Exception {
        Scene scene=new Scene(gate(),EnumFacing.WEST,true,true);
        for(BlockPos p:new ArrayList<>(scene.grid.states.keySet()))if(!p.equals(scene.root.getPos())) {scene.grid.states.remove(p);scene.grid.tiles.remove(p);}
        assertTrue(scene.walk(false).collidedHorizontally);
    }
    @Test public void openedApertureReallyAllowsEntityMoveAndStonePlacement() throws Exception {
        Scene scene=new Scene(gate(),EnumFacing.SOUTH,false,false);scene.root.progress=1;assertTrue(scene.root.fitParts(1));
        assertFalse(scene.walk(false).collidedHorizontally);
        BlockPos aperture=scene.root.getPos().up();assertTrue(scene.grid.world.isAirBlock(aperture));
        scene.grid.states.put(aperture,Blocks.STONE.getDefaultState());
        assertFalse(scene.grid.world.getCollisionBoxes(null,new AxisAlignedBB(aperture).shrink(.01)).isEmpty());
    }
    @Test public void serverAndClientWithSamePositionAndUuidDoNotShareProgress() throws Exception {
        Scene server=new Scene(gate(),EnumFacing.SOUTH,false,false),client=new Scene(gate(),EnumFacing.SOUTH,true,true);
        client.root.owner=server.root.owner;client.root.progress=1;
        assertTrue(server.walk(false).collidedHorizontally);assertFalse(client.walk(false).collidedHorizontally);
    }
    @Test public void loadedPendingProxyCanResolveOnlyItsActualController() throws Exception {
        Scene scene=new Scene(gate(),EnumFacing.NORTH,true,true);
        BlockPos cell=scene.root.occupied().get(1);assertSame(scene.root,OriginalGates.controller(scene.grid.world,cell));
        OriginalGatePartTile part=(OriginalGatePartTile)scene.grid.tiles.get(cell);part.owner=UUID.randomUUID();
        assertNull(OriginalGates.controller(scene.grid.world,cell));
    }
    @Test public void proxyPacketRoundTripRestoresOwnerAndOrigin() throws Exception {
        Scene server=new Scene(gate(),EnumFacing.SOUTH,false,false),client=new Scene(gate(),EnumFacing.SOUTH,true,true);
        client.root.owner=server.root.owner;
        BlockPos cell=server.root.occupied().get(1);OriginalGatePartTile source=(OriginalGatePartTile)server.grid.tiles.get(cell),target=(OriginalGatePartTile)client.grid.tiles.get(cell);
        PacketBuffer buffer=new PacketBuffer(io.netty.buffer.Unpooled.buffer());
        try {
            source.getUpdatePacket().writePacketData(buffer);SPacketUpdateTileEntity wire=new SPacketUpdateTileEntity();wire.readPacketData(buffer);
            target.onDataPacket(null,wire);assertEquals(server.root.owner,target.owner);assertEquals(server.root.getPos(),target.origin);assertSame(client.root,target.controller());
        } finally {buffer.release();}
    }
    @Test public void configureAndFirstServerTickExplicitlyScheduleOwnershipResync() throws Exception {
        Scene scene=new Scene(gate(),EnumFacing.SOUTH,false,false);BlockPos cell=scene.root.occupied().get(1);
        OriginalGatePartTile part=(OriginalGatePartTile)scene.grid.tiles.get(cell);clearInvocations(scene.grid.world);
        part.configure(scene.root.getPos(),scene.root.owner);
        verify(scene.grid.world).notifyBlockUpdate(eq(cell),any(),any(),eq(2));
        clearInvocations(scene.grid.world);scene.root.update();verify(scene.grid.world).notifyBlockUpdate(eq(cell),any(),any(),eq(2));
    }
    @Test public void snapshotPlacementDefersPacketsUntilCommitTick() throws Exception {
        Scene scene=new Scene(gate(),EnumFacing.SOUTH,false,false);scene.grid.world.captureBlockSnapshots=true;clearInvocations(scene.grid.world);
        scene.root.syncStructure();verify(scene.grid.world,never()).notifyBlockUpdate(any(),any(),any(),anyInt());
        scene.grid.world.captureBlockSnapshots=false;scene.root.update();verify(scene.grid.world,atLeastOnce()).notifyBlockUpdate(any(),any(),any(),eq(2));
    }
    @Test public void chunkUnloadAndInvalidationRemoveControllerPhysics() throws Exception {
        Scene scene=new Scene(gate(),EnumFacing.SOUTH,true,true);scene.root.onChunkUnload();assertFalse(scene.walk(false).collidedHorizontally);
        scene.root.onLoad();assertTrue(scene.walk(false).collidedHorizontally);scene.root.invalidate();assertFalse(scene.walk(false).collidedHorizontally);
    }
    @Test public void boundsAndPathPassabilityUseGeometryInsteadOfAlwaysPassableOrFullCube() throws Exception {
        Scene scene=new Scene(gate(),EnumFacing.SOUTH,true,false);BlockPos panel=scene.root.getPos().up();
        assertNotNull(OriginalGates.PART.getCollisionBoundingBox(OriginalGates.PART.getDefaultState(),scene.grid.world,panel));
        assertFalse(OriginalGates.PART.isPassable(scene.grid.world,panel));
        scene.root.progress=1;
        assertNull(OriginalGates.PART.getCollisionBoundingBox(OriginalGates.PART.getDefaultState(),scene.grid.world,panel));assertTrue(OriginalGates.PART.isReplaceable(scene.grid.world,panel));
        OriginalGates.PART.breakBlock(scene.grid.world,panel,OriginalGates.PART.getDefaultState());verify(scene.grid.world,never()).destroyBlock(scene.root.getPos(),true);
    }
}
