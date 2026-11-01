package ru.metrocraft.port.blackrazid;
import java.util.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import ru.metrocraft.port.blackrazid.environment.*;
public class EnvironmentTest {
    @Rule public org.junit.rules.TemporaryFolder temporary=new org.junit.rules.TemporaryFolder();
    @BeforeClass public static void boot(){NativeWeaponsTest.boot();BlackrazidEnvironment.initialize();
        // Archive-only serialization tests: this tile is deliberately NOT registered by the mod.
        net.minecraftforge.fml.common.registry.GameRegistry.registerTileEntity(TunnelTile.class,new net.minecraft.util.ResourceLocation("metroport_test","archived_tunnel"));}
    private static ItemStack kit(int shape){return new ItemStack(BlackrazidEnvironment.KIT,64,shape);}
    @Test public void nativeRingHasSeventeenHelpersAndHollowCenter(){assertEquals(net.minecraft.block.material.Material.GROUND,BlackrazidEnvironment.TUNNEL.getDefaultState().getMaterial());assertEquals(net.minecraft.block.material.Material.ROCK,BlackrazidEnvironment.PROXY.getDefaultState().getMaterial());for(int d=0;d<4;d++){List<BlockPos> f=TunnelRules.footprint(0,d);assertEquals(18,f.size());assertEquals(18,new HashSet<>(f).size());for(int y=-5;y<=-1;y++)assertFalse(f.contains(new BlockPos(0,y,0)));}assertEquals(1,TunnelRules.footprint(1,0).size());assertTrue(TunnelRules.footprint(-1,0).isEmpty());assertTrue(TunnelRules.footprint(19,0).isEmpty());}
    @Test public void nativePlacementYawAndBothRendererAngleTablesAreRetained(){assertEquals(3,TunnelRules.placementDirection(0));assertEquals(0,TunnelRules.placementDirection(90));assertEquals(1,TunnelRules.placementDirection(180));assertEquals(2,TunnelRules.placementDirection(270));for(int d=0;d<4;d++){assertEquals(new int[]{90,180,270,0}[d],TunnelRules.renderAngle(0,d));assertEquals(new int[]{270,180,450,360}[d],TunnelRules.renderAngle(1,d));}}
    @Test public void allNineteenProfilesPlaceAndConsumeExactlyOneOfSixtyFourInEveryDirection()throws Exception{for(int shape=0;shape<TunnelRules.count();shape++)for(int dir=0;dir<4;dir++){RailsTest.MapWorld m=new RailsTest.MapWorld(false);BlockPos p=new BlockPos(20,80,20);ItemStack k=kit(shape);assertTrue(TunnelPlacement.place(m.world,p,shape,dir,m.player,k));assertEquals(63,k.getCount());assertEquals(TunnelRules.footprint(shape,dir).size(),m.tiles.size());for(BlockPos offset:TunnelRules.footprint(shape,dir))assertSame(TunnelPlacement.at(m.world,p),TunnelPlacement.primary(m.world,p.add(offset)));}}
    @Test public void farWallObstructionRejectsBeforeOverwritingAnyMapBlock()throws Exception{RailsTest.MapWorld m=new RailsTest.MapWorld(false);BlockPos p=new BlockPos(20,80,20),wall=p.add(0,-4,4);m.states.put(wall,Blocks.STONE.getDefaultState());ItemStack k=kit(0);assertFalse(TunnelPlacement.place(m.world,p,0,0,m.player,k));assertEquals(64,k.getCount());assertEquals(0,m.writes);assertSame(Blocks.STONE,m.world.getBlockState(wall).getBlock());}
    @Test public void protectedWallAndUnloadedWallNeverGetOverwritten()throws Exception{for(boolean protection:new boolean[]{true,false}){RailsTest.MapWorld m=new RailsTest.MapWorld(false);BlockPos p=new BlockPos(20,80,20),wall=p.add(0,-4,4);if(protection)when(m.world.isBlockModifiable(m.player,wall)).thenReturn(false);else when(m.world.isBlockLoaded(wall)).thenReturn(false);assertFalse(TunnelPlacement.place(m.world,p,0,0,m.player,kit(0)));assertEquals(0,m.writes);}}
    @Test public void ringCommitFailureRollsBackAndDoesNotConsume()throws Exception{RailsTest.MapWorld m=new RailsTest.MapWorld(false);m.failAt=8;BlockPos p=new BlockPos(20,80,20);ItemStack k=kit(0);assertFalse(TunnelPlacement.place(m.world,p,0,0,m.player,k));assertEquals(64,k.getCount());assertTrue(m.tiles.isEmpty());for(BlockPos offset:TunnelRules.footprint(0,0))assertSame(Blocks.AIR,m.world.getBlockState(p.add(offset)).getBlock());}
    @Test public void anyRingHelperBreakRemovesOnlyItsOwnStructure()throws Exception{RailsTest.MapWorld m=new RailsTest.MapWorld(false);BlockPos p=new BlockPos(20,80,20),other=p.add(4,0,0);assertTrue(TunnelPlacement.place(m.world,p,0,0,m.player,kit(0)));assertTrue(TunnelPlacement.place(m.world,other,1,0,m.player,kit(1)));m.world.setBlockToAir(p.add(0,-4,4));assertEquals(1,m.tiles.size());assertNotNull(TunnelPlacement.primary(m.world,other));}
    @Test public void clientAndSpectatorNeverPlaceOrConsume()throws Exception{RailsTest.MapWorld m=new RailsTest.MapWorld(true);ItemStack k=kit(0);assertFalse(TunnelPlacement.place(m.world,new BlockPos(0,80,0),0,0,m.player,k));assertEquals(64,k.getCount());assertEquals(0,m.writes);}
    @Test public void proxyCannotBreakAProtectedAnchor()throws Exception{RailsTest.MapWorld m=new RailsTest.MapWorld(false);BlockPos p=new BlockPos(20,80,20),wall=p.add(0,-4,4);assertTrue(TunnelPlacement.place(m.world,p,0,0,m.player,kit(0)));when(m.world.isBlockModifiable(m.player,p)).thenReturn(false);assertFalse(BlackrazidEnvironment.PROXY.removedByPlayer(m.world.getBlockState(wall),m.world,wall,m.player,true));assertEquals(18,m.tiles.size());}
    @Test public void missingHelperLoadValidationAndStaleOwnerCleanup()throws Exception{RailsTest.MapWorld m=new RailsTest.MapWorld(false);BlockPos p=new BlockPos(20,80,20),wall=p.add(0,-4,4);assertTrue(TunnelPlacement.place(m.world,p,0,0,m.player,kit(0)));m.tiles.remove(wall);m.states.put(wall,Blocks.AIR.getDefaultState());TunnelPlacement.validate(m.world,p);assertTrue(m.tiles.isEmpty());}
    @Test public void tunnelNbtRoundtripKeepsOwnershipShapeAndDirection(){TunnelTile t=new TunnelTile();t.setPos(new BlockPos(20,80,20));UUID id=UUID.randomUUID();t.configure(true,1,3,t.getPos(),id);TunnelTile copy=new TunnelTile();copy.readFromNBT(t.writeToNBT(new NBTTagCompound()));assertTrue(copy.primary());assertEquals(1,copy.shape());assertEquals(3,copy.direction());assertEquals(id,copy.identity());assertEquals(t.getPos(),copy.owner());assertNotNull(copy.getUpdatePacket());}
    @Test public void lightLevelsMatchOriginalFifteenTimesFlooredValues(){for(int i=0;i<4;i++){TransientLightBlock b=BlackrazidEnvironment.LIGHTS[i];assertEquals(new int[]{10,9,7,4}[i],b.getLightValue(b.getDefaultState()));assertTrue(b.isAir(b.getDefaultState(),null,BlockPos.ORIGIN));assertNull(b.getCollisionBoundingBox(b.getDefaultState(),null,BlockPos.ORIGIN));assertFalse(b.canCollideCheck(b.getDefaultState(),false));assertEquals(net.minecraft.block.state.BlockFaceShape.UNDEFINED,b.getBlockFaceShape(null,b.getDefaultState(),BlockPos.ORIGIN,net.minecraft.util.EnumFacing.UP));}}
    private static TransientLightTile light(RailsTest.MapWorld m,BlockPos p)throws Exception{m.world.setBlockState(p,BlackrazidEnvironment.LIGHTS[0].getDefaultState(),2);return (TransientLightTile)m.world.getTileEntity(p);}
    @Test public void temporaryLightExpiresExactlyOnFifthServerTick()throws Exception{RailsTest.MapWorld m=new RailsTest.MapWorld(false);BlockPos p=new BlockPos(1,80,1);TransientLightTile t=light(m,p);for(int i=0;i<4;i++){t.update();assertNotNull(m.tiles.get(p));}t.update();assertNull(m.tiles.get(p));assertSame(Blocks.AIR,m.world.getBlockState(p).getBlock());}
    @Test public void oldLightCannotEraseReplacementBlockOrReplacementTile()throws Exception{RailsTest.MapWorld m=new RailsTest.MapWorld(false);BlockPos p=new BlockPos(1,80,1);TransientLightTile t=light(m,p);m.world.setBlockState(p,Blocks.STONE.getDefaultState(),2);for(int i=0;i<8;i++)t.update();assertSame(Blocks.STONE,m.world.getBlockState(p).getBlock());}
    @Test public void lightClientDoesNotMutateAndNbtLifetimeIsBounded()throws Exception{RailsTest.MapWorld m=new RailsTest.MapWorld(true);TransientLightTile t=light(m,new BlockPos(1,80,1));t.update();assertEquals(5,t.remaining());NBTTagCompound tag=t.writeToNBT(new NBTTagCompound());tag.setInteger("Remaining",Integer.MAX_VALUE);TransientLightTile restored=new TransientLightTile();restored.readFromNBT(tag);assertEquals(5,restored.remaining());tag.setInteger("Remaining",-3);restored.readFromNBT(tag);assertEquals(1,restored.remaining());}
    @Test public void teleportSelectionUsesServerTeamsNotPatchesAndFailsClosedOnAmbiguity(){FactionTeleportBlock.Target a=new FactionTeleportBlock.Target("polis",0,new BlockPos(10,60,10)),b=new FactionTeleportBlock.Target("reich",0,new BlockPos(30,60,30));assertSame(a,FactionTeleportBlock.select(Arrays.asList(a,b),"polis",0));assertNull(FactionTeleportBlock.select(Arrays.asList(a,b),null,0));assertNull(FactionTeleportBlock.select(Arrays.asList(a,b),"polis",-1));assertNull(FactionTeleportBlock.select(Arrays.asList(a,a),"polis",0));}
    @Test public void randomUnaffiliatedTeamCanReachAllEightSourceBases(){List<FactionTeleportBlock.Target> targets=new ArrayList<>();for(int i=0;i<8;i++)targets.add(new FactionTeleportBlock.Target("base"+i,0,new BlockPos(i,60,0)));Set<Integer> found=new HashSet<>();Random r=new Random(71);for(int i=0;i<1000;i++)found.add(FactionTeleportBlock.select(targets,"free",0,"free",r).pos.getX());assertEquals(8,found.size());assertNull(FactionTeleportBlock.select(targets,"free",0,"",r));}
    @Test public void landingRequiresTwoAirCellsSolidFloorAndLoadedChunk()throws Exception{RailsTest.MapWorld m=new RailsTest.MapWorld(false);BlockPos p=new BlockPos(10,60,10);assertFalse(FactionTeleportBlock.safeLanding(m.world,p));m.states.put(p.down(),Blocks.STONE.getDefaultState());assertTrue(FactionTeleportBlock.safeLanding(m.world,p));m.states.put(p.up(),Blocks.STONE.getDefaultState());assertFalse(FactionTeleportBlock.safeLanding(m.world,p));m.states.remove(p.up());m.states.put(p,Blocks.LAVA.getDefaultState());assertFalse(FactionTeleportBlock.safeLanding(m.world,p));m.states.remove(p);when(m.world.isBlockLoaded(p)).thenReturn(false);assertFalse(FactionTeleportBlock.safeLanding(m.world,p));}
    private static net.minecraft.entity.player.EntityPlayerMP teleporter(net.minecraft.world.World world){net.minecraft.entity.player.EntityPlayerMP p=mock(net.minecraft.entity.player.EntityPlayerMP.class);p.world=world;p.dimension=0;when(p.isEntityAlive()).thenReturn(true);net.minecraft.scoreboard.Team team=mock(net.minecraft.scoreboard.Team.class);when(team.getName()).thenReturn("polis");when(p.getTeam()).thenReturn(team);return p;}
    @Test public void successfulTeleportIsServerOnlyCenteredAndRateLimited()throws Exception{RailsTest.MapWorld m=new RailsTest.MapWorld(false);BlockPos pos=new BlockPos(10,60,10);m.states.put(pos.down(),Blocks.STONE.getDefaultState());net.minecraft.entity.player.EntityPlayerMP p=teleporter(m.world);List<FactionTeleportBlock.Target> points=Collections.singletonList(new FactionTeleportBlock.Target("polis",0,pos));FactionTeleportBlock block=new FactionTeleportBlock();p.motionY=-5;p.fallDistance=20;assertTrue(block.teleport(m.world,p,points));verify(p).setPositionAndUpdate(10.5,60,10.5);assertEquals(0,p.motionY,0);assertEquals(0,p.fallDistance,0);assertFalse(block.teleport(m.world,p,points));when(m.world.getTotalWorldTime()).thenReturn(100L);assertTrue(block.teleport(m.world,p,points));}
    @Test public void spectatorsRidersWrongTeamsAndClientsCannotTeleport()throws Exception{RailsTest.MapWorld m=new RailsTest.MapWorld(false);BlockPos pos=new BlockPos(10,60,10);m.states.put(pos.down(),Blocks.STONE.getDefaultState());net.minecraft.entity.player.EntityPlayerMP p=teleporter(m.world);List<FactionTeleportBlock.Target> points=Collections.singletonList(new FactionTeleportBlock.Target("polis",0,pos));FactionTeleportBlock b=new FactionTeleportBlock();when(p.isSpectator()).thenReturn(true);assertFalse(b.teleport(m.world,p,points));when(p.isSpectator()).thenReturn(false);when(p.isRiding()).thenReturn(true);assertFalse(b.teleport(m.world,p,points));when(p.isRiding()).thenReturn(false);when(p.getTeam()).thenReturn(null);assertFalse(b.teleport(m.world,p,points));verify(p,never()).setPositionAndUpdate(anyDouble(),anyDouble(),anyDouble());RailsTest.MapWorld client=new RailsTest.MapWorld(true);assertFalse(b.teleport(client.world,teleporter(client.world),points));}
    @Test public void configuredDestinationNeverGeneratesNewTerrainOrReplacesCorruptChunk(){for(boolean exists:new boolean[]{false,true}){net.minecraft.world.WorldServer w=mock(net.minecraft.world.WorldServer.class);when(w.getWorldBorder()).thenReturn(new net.minecraft.world.border.WorldBorder());net.minecraft.world.gen.ChunkProviderServer provider=mock(net.minecraft.world.gen.ChunkProviderServer.class);when(w.getChunkProvider()).thenReturn(provider);when(provider.isChunkGeneratedAt(anyInt(),anyInt())).thenReturn(exists);List<FactionTeleportBlock.Target> points=Collections.singletonList(new FactionTeleportBlock.Target("polis",0,new BlockPos(100,60,100)));assertFalse(new FactionTeleportBlock().teleport(w,teleporter(w),points));verify(provider,never()).provideChunk(anyInt(),anyInt());if(exists)verify(provider).loadChunk(6,6);}}

    @Test public void ceilingPlacementChecksAdventureEditPermissionOnTheActualFace()throws Exception{RailsTest.MapWorld m=new RailsTest.MapWorld(false);BlockPos ceiling=new BlockPos(20,80,20),anchor=ceiling.down();m.states.put(ceiling,Blocks.STONE.getDefaultState());ItemStack k=kit(1);when(m.player.getHeldItem(net.minecraft.util.EnumHand.MAIN_HAND)).thenReturn(k);when(m.player.canPlayerEdit(eq(anchor),eq(net.minecraft.util.EnumFacing.UP),eq(k))).thenReturn(false);assertEquals(net.minecraft.util.EnumActionResult.SUCCESS,BlackrazidEnvironment.KIT.onItemUse(m.player,m.world,ceiling,net.minecraft.util.EnumHand.MAIN_HAND,net.minecraft.util.EnumFacing.DOWN,.5F,.5F,.5F));assertEquals(63,k.getCount());verify(m.player).canPlayerEdit(anchor,net.minecraft.util.EnumFacing.DOWN,k);assertNotNull(TunnelPlacement.primary(m.world,anchor));}

    @Test public void generatedConfigDisablesDestinationsUntilServerExplicitlyEnablesOne()throws Exception{
        // Standalone JUnit does not run LaunchWrapper's FMLInjectionData.build(). Supply only its game-directory fixture.
        java.lang.reflect.Field home=net.minecraftforge.fml.relauncher.FMLInjectionData.class.getDeclaredField("minecraftHome");home.setAccessible(true);Object previous=home.get(null);home.set(null,temporary.getRoot());
        try{
            java.io.File file=temporary.newFile("teleports.cfg");FactionTeleportBlock.load(file);RailsTest.MapWorld m=new RailsTest.MapWorld(false);BlockPos pos=new BlockPos(10,60,10);m.states.put(pos.down(),Blocks.STONE.getDefaultState());net.minecraft.entity.player.EntityPlayerMP p=teleporter(m.world);FactionTeleportBlock b=new FactionTeleportBlock();
            try{
                b.onBlockActivated(m.world,BlockPos.ORIGIN,b.getDefaultState(),p,net.minecraft.util.EnumHand.MAIN_HAND,net.minecraft.util.EnumFacing.UP,0,0,0);verify(p,never()).setPositionAndUpdate(anyDouble(),anyDouble(),anyDouble());
                net.minecraftforge.common.config.Configuration cfg=new net.minecraftforge.common.config.Configuration(file);cfg.load();cfg.get("destination.Polis","enabled",false).set(true);cfg.get("destination.Polis","x",0).set(10);cfg.get("destination.Polis","y",1).set(60);cfg.get("destination.Polis","z",0).set(10);cfg.save();FactionTeleportBlock.load(file);
                b.onBlockActivated(m.world,BlockPos.ORIGIN,b.getDefaultState(),p,net.minecraft.util.EnumHand.MAIN_HAND,net.minecraft.util.EnumFacing.UP,0,0,0);verify(p).setPositionAndUpdate(10.5,60,10.5);
            }finally{FactionTeleportBlock.load(temporary.newFile("disabled.cfg"));}
        }finally{home.set(null,previous);}
    }

    @Test public void everyNewKindHasFourTransformsAndBoundedRenderData(){
        assertEquals(19,TunnelRules.count());assertFalse(TunnelRules.valid(-1));assertFalse(TunnelRules.valid(19));
        for(int shape=1;shape<19;shape++)for(int dir=0;dir<4;dir++){
            TunnelRules.Shape profile=TunnelRules.profile(shape);assertNotNull(profile.mesh);assertTrue(profile.transforms[dir].length>=3);
            AxisAlignedBB b=TunnelRules.renderBounds(shape,dir);assertTrue(b.minX<b.maxX);assertTrue(b.minY<b.maxY);assertTrue(b.minZ<b.maxZ);
            assertEquals(1,TunnelRules.footprint(shape,dir).size());
        }
    }
    @Test public void originalCurveDoubleAndCrossAnglesAreNotForcedToStraightProfile(){
        for(int d=0;d<4;d++){assertEquals(new int[]{90,360,270,180}[d],TunnelRules.renderAngle(2,d));assertEquals(new int[]{180,90,360,270}[d],TunnelRules.renderAngle(15,d));assertEquals(360,TunnelRules.renderAngle(9,d));}
    }
    @Test public void newHighMetadataAndOwnerIdentitySurviveNbtAndPickBlock()throws Exception{
        for(int shape=2;shape<19;shape++){
            RailsTest.MapWorld m=new RailsTest.MapWorld(false);BlockPos p=new BlockPos(20,80,20);ItemStack k=kit(shape);assertTrue(TunnelPlacement.place(m.world,p,shape,3,m.player,k));TunnelTile t=TunnelPlacement.at(m.world,p),loaded=new TunnelTile();loaded.readFromNBT(t.writeToNBT(new NBTTagCompound()));
            assertEquals(shape,loaded.shape());assertEquals(3,loaded.direction());assertEquals(t.identity(),loaded.identity());assertEquals(p,loaded.owner());
            assertEquals(shape,BlackrazidEnvironment.TUNNEL.getPickBlock(m.world.getBlockState(p),null,m.world,p,m.player).getMetadata());
            assertEquals("item.metroport.blackrazid_tunnels_"+shape,BlackrazidEnvironment.KIT.getTranslationKey(new ItemStack(BlackrazidEnvironment.KIT,1,shape)));
        }
    }
    @Test public void allNewProfilesRefuseOccupiedProtectedAndUnloadedAnchorsBeforeMutation()throws Exception{
        for(int shape=2;shape<19;shape++)for(int mode=0;mode<3;mode++){
            RailsTest.MapWorld m=new RailsTest.MapWorld(false);BlockPos p=new BlockPos(20,80,20);ItemStack k=kit(shape);
            if(mode==0)m.states.put(p,Blocks.STONE.getDefaultState());if(mode==1)when(m.world.isBlockModifiable(m.player,p)).thenReturn(false);if(mode==2)when(m.world.isBlockLoaded(p)).thenReturn(false);
            assertFalse(TunnelPlacement.place(m.world,p,shape,0,m.player,k));assertEquals(64,k.getCount());assertEquals(0,m.writes);
        }
    }
    @Test public void newProfilesAreServerOwnedAndFailedPlacementNeverConsumesKit()throws Exception{
        for(int shape=2;shape<19;shape++){
            RailsTest.MapWorld client=new RailsTest.MapWorld(true);ItemStack k=kit(shape);assertFalse(TunnelPlacement.place(client.world,new BlockPos(20,80,20),shape,0,client.player,k));assertEquals(0,client.writes);assertEquals(64,k.getCount());
            RailsTest.MapWorld server=new RailsTest.MapWorld(false);server.failAt=1;assertFalse(TunnelPlacement.place(server.world,new BlockPos(20,80,20),shape,0,server.player,k));assertEquals(64,k.getCount());assertTrue(server.tiles.isEmpty());
        }
    }
    @Test public void transformedCurveBoundsExtendBeyondOldNineBlockCube(){
        for(int shape:new int[]{2,5,12,15})for(int dir=0;dir<4;dir++){
            TunnelTile t=new TunnelTile(true);BlockPos p=new BlockPos(100,80,100);t.setPos(p);t.configure(true,shape,dir,p,UUID.randomUUID());AxisAlignedBB actual=t.getRenderBoundingBox(),nativeBounds=TunnelRules.renderBounds(shape,dir).offset(p);
            assertTrue(actual.minX<=nativeBounds.minX);assertTrue(actual.maxX>=nativeBounds.maxX);assertTrue(actual.minY<=nativeBounds.minY);assertTrue(actual.maxY>=nativeBounds.maxY);assertTrue(actual.minZ<=nativeBounds.minZ);assertTrue(actual.maxZ>=nativeBounds.maxZ);
            assertTrue(nativeBounds.minX<91 || nativeBounds.maxX>110 || nativeBounds.minZ<91 || nativeBounds.maxZ>110);
        }
    }
    @Test public void allNativeTunnelMeshesParseWithCompleteMaterialMappingWithoutOpenGL()throws Exception{
        com.google.gson.JsonObject maps;try(java.io.Reader r=new java.io.InputStreamReader(getClass().getResourceAsStream("/assets/metroport/blackrazid_tunnel_mesh_materials.json"),java.nio.charset.StandardCharsets.UTF_8)){maps=new com.google.gson.JsonParser().parse(r).getAsJsonObject();}
        for(int shape=1;shape<19;shape++){
            String name=TunnelRules.profile(shape).mesh;try(java.io.InputStream in=getClass().getResourceAsStream("/assets/metroport/models/blackrazid/tunnels/"+name)){
                ru.metrocraft.port.mcmod.McmodObjMesh mesh=ru.metrocraft.port.mcmod.McmodObjMesh.readMaterials(in);assertFalse(mesh.groups.isEmpty());com.google.gson.JsonObject materials=maps.getAsJsonObject(name);
                for(Map.Entry<String,List<float[]>> group:mesh.groups.entrySet()){assertTrue(materials.has(group.getKey()));assertFalse(group.getValue().isEmpty());assertEquals(0,group.getValue().size()%3);for(float[] vertex:group.getValue())for(float coordinate:vertex)assertTrue(Float.isFinite(coordinate));}
            }
        }
    }

}
