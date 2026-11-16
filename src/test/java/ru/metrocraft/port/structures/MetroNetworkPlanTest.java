package ru.metrocraft.port.structures;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.*;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import org.junit.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import static org.junit.Assert.*;

public class MetroNetworkPlanTest {
    private static MetroNetworkPlan plan;
    @BeforeClass public static void boot() {Bootstrap.register();plan=MetroNetworkPlan.create(24);}
    @Test public void deterministicBoundedVanillaOnlyPlan() {
        assertEquals(plan.cells(),MetroNetworkPlan.create(24).cells());
        assertTrue(plan.cells().size()>60000);assertTrue(plan.cells().size()<150000);
        for(Map.Entry<BlockPos,MetroNetworkPlan.Kind> e:plan.cells().entrySet()) {
            BlockPos p=e.getKey();assertTrue(p.getX()>=plan.min().getX() && p.getX()<=plan.max().getX());assertTrue(p.getZ()>=plan.min().getZ() && p.getZ()<=plan.max().getZ());assertTrue(p.getY()>=0 && p.getY()<=plan.max().getY());
            assertEquals("minecraft",e.getValue().state().getBlock().getRegistryName().getNamespace());
        }
        assertFalse(plan.cells().containsKey(new BlockPos(0,0,0))); // no clearing the square inside the ring
    }
    @Test public void bothRailCircuitsHaveCorrectCornersSupportsAndPoweredSegments() {
        for(int r:new int[]{60,68}) {
            int count=0,powered=0;
            for(Map.Entry<BlockPos,MetroNetworkPlan.Kind> e:plan.cells().entrySet())if(e.getValue().rail() && Math.max(Math.abs(e.getKey().getX()),Math.abs(e.getKey().getZ()))==r) {
                count++;BlockPos pos=e.getKey();assertEquals(1,pos.getY());assertNotEquals(MetroNetworkPlan.Kind.AIR,plan.cells().get(pos.down()));
                if(e.getValue().name().startsWith("BOOST")) {powered++;assertEquals(MetroNetworkPlan.Kind.POWER,plan.cells().get(pos.down()));}
                IBlockState rail=e.getValue().state();BlockRailBase.EnumRailDirection shape=((BlockRailBase)rail.getBlock()).getRailDirection(null,pos,rail,null);
                for(EnumFacing direction:connections(shape)) {
                    BlockPos next=pos.offset(direction);MetroNetworkPlan.Kind k=plan.cells().get(next);assertNotNull(k);assertTrue(k.rail());
                    BlockRailBase.EnumRailDirection nextShape=((BlockRailBase)k.state().getBlock()).getRailDirection(null,next,k.state(),null);
                    assertTrue(Arrays.asList(connections(nextShape)).contains(direction.getOpposite()));
                }
            }
            assertEquals(8*r,count);assertTrue(powered>40);
        }
    }
    private EnumFacing[] connections(BlockRailBase.EnumRailDirection s) {
        switch(s) {
            case EAST_WEST:return new EnumFacing[]{EnumFacing.EAST,EnumFacing.WEST};
            case NORTH_SOUTH:return new EnumFacing[]{EnumFacing.NORTH,EnumFacing.SOUTH};
            case SOUTH_EAST:return new EnumFacing[]{EnumFacing.SOUTH,EnumFacing.EAST};
            case SOUTH_WEST:return new EnumFacing[]{EnumFacing.SOUTH,EnumFacing.WEST};
            case NORTH_EAST:return new EnumFacing[]{EnumFacing.NORTH,EnumFacing.EAST};
            case NORTH_WEST:return new EnumFacing[]{EnumFacing.NORTH,EnumFacing.WEST};
            default:throw new AssertionError(s);
        }
    }
    @Test public void maintenanceWalkwayConnectsAllFourIslandsWithoutWalkingOnRails() {
        Set<BlockPos> seen=new HashSet<>();Deque<BlockPos> queue=new ArrayDeque<>();BlockPos start=new BlockPos(0,1,-64);queue.add(start);seen.add(start);
        while(!queue.isEmpty()) {
            BlockPos p=queue.removeFirst();
            for(EnumFacing f:EnumFacing.Plane.HORIZONTAL) {
                BlockPos next=p.offset(f);
                if(seen.contains(next) || plan.cells().get(next)!=MetroNetworkPlan.Kind.PLATFORM || plan.cells().get(next.up())!=MetroNetworkPlan.Kind.AIR || plan.cells().get(next.up(2))!=MetroNetworkPlan.Kind.AIR)continue;
                seen.add(next);queue.add(next);
            }
        }
        for(MetroNetworkPlan.Station s:MetroNetworkPlan.STATIONS)assertTrue(s.name,seen.contains(s.at(0,1,0)));
    }
    @Test public void eachStationHasLitPlatformsCrossingBridgeAndAccessibleServiceDoor() {
        for(MetroNetworkPlan.Station s:MetroNetworkPlan.STATIONS) {
            assertEquals(MetroNetworkPlan.Kind.LIGHT,plan.cells().get(s.at(0,7,0)));
            for(int z=-8;z<=8;z++)assertEquals(MetroNetworkPlan.Kind.BRICK,plan.cells().get(s.at(18,4,z)));
            for(int z:new int[]{-7,0,7})for(int i=0;i<3;i++)assertTrue(plan.cells().get(s.at(14+i,2+i,z)).name().startsWith("STAIR"));
            for(int y=2;y<=4;y++)assertEquals(MetroNetworkPlan.Kind.AIR,plan.cells().get(s.at(12,y,-11)));
            assertEquals(MetroNetworkPlan.Kind.BRICK,plan.cells().get(s.at(12,1,-11)));
            assertEquals(s.name,plan.signs().get(s.at(4,4,-10))[1]);
        }
    }
    @Test public void entranceLadderIsContinuousAndBackedFromRoomToSurface() {
        MetroNetworkPlan.Station n=MetroNetworkPlan.STATIONS.get(0);
        for(int y=2;y<=26;y++) {assertEquals(MetroNetworkPlan.Kind.LADDER,plan.cells().get(n.at(16,y,-21)));assertEquals(MetroNetworkPlan.Kind.BRICK,plan.cells().get(n.at(16,y,-22)));}
        assertEquals(MetroNetworkPlan.Kind.AIR,plan.cells().get(plan.entrance()));assertEquals(MetroNetworkPlan.Kind.AIR,plan.cells().get(plan.entrance().up()));
        assertEquals(MetroNetworkPlan.Kind.BRICK,plan.cells().get(plan.entrance().down()));
        assertEquals(MetroNetworkPlan.Kind.BRICK,plan.cells().get(n.at(15,24,-21))); // step off ladder, not a 3x3 fall shaft
        assertEquals(MetroNetworkPlan.Kind.AIR,plan.cells().get(n.at(15,25,-21)));
    }
    @Test public void allWallSignsHaveBackingAndFourLines() {
        for(Map.Entry<BlockPos,String[]> e:plan.signs().entrySet()) {
            assertEquals(4,e.getValue().length);IBlockState sign=plan.cells().get(e.getKey()).state();EnumFacing facing=sign.getValue(BlockWallSign.FACING);
            MetroNetworkPlan.Kind support=plan.cells().get(e.getKey().offset(facing.getOpposite()));assertNotNull(support);assertTrue(support.state().isFullCube());
        }
    }
    @Test public void rejectUnboundedEntranceShaft() {
        for(int height:new int[]{-1,0,11,65,1000000})try {MetroNetworkPlan.create(height);fail();}catch(IllegalArgumentException expected) {}
    }
    @Test public void exportClassic112SchematicAndReadItBack() throws Exception {
        BlockPos min=plan.min(),max=plan.max();int w=max.getX()-min.getX()+1,h=max.getY()+1,l=max.getZ()-min.getZ()+1;
        byte[] blocks=new byte[w*h*l],data=new byte[blocks.length];
        for(Map.Entry<BlockPos,MetroNetworkPlan.Kind> e:plan.cells().entrySet()) {
            BlockPos p=e.getKey();int i=(p.getY()*l+p.getZ()-min.getZ())*w+p.getX()-min.getX();IBlockState state=e.getValue().state();int id=Block.getIdFromBlock(state.getBlock());assertTrue(id<256);blocks[i]=(byte)id;data[i]=(byte)state.getBlock().getMetaFromState(state);
        }
        NBTTagCompound root=new NBTTagCompound();root.setShort("Width",(short)w);root.setShort("Height",(short)h);root.setShort("Length",(short)l);root.setString("Materials","Alpha");root.setByteArray("Blocks",blocks);root.setByteArray("Data",data);
        root.setInteger("WEOriginX",0);root.setInteger("WEOriginY",0);root.setInteger("WEOriginZ",0);root.setInteger("WEOffsetX",min.getX());root.setInteger("WEOffsetY",0);root.setInteger("WEOffsetZ",min.getZ());
        root.setTag("Entities",new NBTTagList());NBTTagList tiles=new NBTTagList();
        for(Map.Entry<BlockPos,String[]> e:plan.signs().entrySet()) {
            NBTTagCompound tag=new NBTTagCompound();tag.setString("id","minecraft:sign");tag.setInteger("x",e.getKey().getX()-min.getX());tag.setInteger("y",e.getKey().getY());tag.setInteger("z",e.getKey().getZ()-min.getZ());
            for(int line=0;line<4;line++)tag.setString("Text"+(line+1),ITextComponent.Serializer.componentToJson(new TextComponentString(e.getValue()[line])));tiles.appendTag(tag);
        }
        root.setTag("TileEntities",tiles);Path path=Paths.get("build/structures/MinETRO_ring_4stations_1.12.2.schematic");Files.createDirectories(path.getParent());
        try(OutputStream out=Files.newOutputStream(path)) {CompressedStreamTools.writeCompressed(root,out);}
        NBTTagCompound read;try(InputStream in=Files.newInputStream(path)) {read=CompressedStreamTools.readCompressed(in);}
        assertArrayEquals(blocks,read.getByteArray("Blocks"));assertArrayEquals(data,read.getByteArray("Data"));assertEquals(plan.signs().size(),read.getTagList("TileEntities",10).tagCount());
        Files.write(Paths.get("build/structures/network-counts.txt"),Arrays.asList("cells="+plan.cells().size(),"schematic="+w+"x"+h+"x"+l,"rails=1024","stations=4","surface=24"),java.nio.charset.StandardCharsets.UTF_8);
    }
}
