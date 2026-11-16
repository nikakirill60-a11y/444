package ru.metrocraft.port.structures;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import java.util.*;

/** A deterministic, vanilla-only four-station ring. Coordinates use rail-bed Y=0.
 * This is a new build, not a copy of the original server map. No world access.
 */
public final class MetroNetworkPlan {
    public enum Kind {
        AIR, BRICK, CRACKED, FLOOR, PLATFORM, TRIM, GLASS, BAR, LIGHT, POWER,
        BLUE, GREEN, RED, YELLOW, CRAFT, SLAB,
        STAIR_E, STAIR_S, STAIR_W, STAIR_N, LADDER,
        RAIL_EW, RAIL_NS, RAIL_SE, RAIL_SW, RAIL_NW, RAIL_NE, BOOST_EW, BOOST_NS,
        SIGN_S, SIGN_W, SIGN_N, SIGN_E;
        public boolean rail() {return name().startsWith("RAIL_") || name().startsWith("BOOST_");}
        public boolean sign() {return name().startsWith("SIGN_");}
        public IBlockState state() {
            switch(this) {
                case AIR:return Blocks.AIR.getDefaultState();
                case BRICK:return Blocks.STONEBRICK.getDefaultState();
                case CRACKED:return Blocks.STONEBRICK.getStateFromMeta(2);
                case FLOOR:return Blocks.STONE.getStateFromMeta(6);
                case PLATFORM:return Blocks.STONE_SLAB.getStateFromMeta(0).withProperty(BlockSlab.HALF,BlockSlab.EnumBlockHalf.TOP);
                case TRIM:return Blocks.QUARTZ_BLOCK.getDefaultState();
                case GLASS:return Blocks.GLASS.getDefaultState();
                case BAR:return Blocks.IRON_BARS.getDefaultState();
                case LIGHT:return Blocks.SEA_LANTERN.getDefaultState();
                case POWER:return Blocks.REDSTONE_BLOCK.getDefaultState();
                case BLUE:return Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(11);
                case GREEN:return Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(13);
                case RED:return Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(14);
                case YELLOW:return Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(4);
                case CRAFT:return Blocks.CRAFTING_TABLE.getDefaultState();
                case SLAB:return Blocks.STONE_SLAB.getDefaultState();
                case LADDER:return Blocks.LADDER.getDefaultState().withProperty(BlockLadder.FACING,EnumFacing.SOUTH);
                case STAIR_E:case STAIR_S:case STAIR_W:case STAIR_N:
                    return Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING,new EnumFacing[]{EnumFacing.EAST,EnumFacing.SOUTH,EnumFacing.WEST,EnumFacing.NORTH}[ordinal()-STAIR_E.ordinal()]);
                case BOOST_EW:case BOOST_NS:
                    return Blocks.GOLDEN_RAIL.getDefaultState().withProperty(BlockRailPowered.SHAPE,this==BOOST_EW?BlockRailBase.EnumRailDirection.EAST_WEST:BlockRailBase.EnumRailDirection.NORTH_SOUTH).withProperty(BlockRailPowered.POWERED,true);
                case SIGN_S:case SIGN_W:case SIGN_N:case SIGN_E:
                    return Blocks.WALL_SIGN.getDefaultState().withProperty(BlockWallSign.FACING,new EnumFacing[]{EnumFacing.SOUTH,EnumFacing.WEST,EnumFacing.NORTH,EnumFacing.EAST}[ordinal()-SIGN_S.ordinal()]);
                default:
                    BlockRailBase.EnumRailDirection[] shapes={BlockRailBase.EnumRailDirection.EAST_WEST,BlockRailBase.EnumRailDirection.NORTH_SOUTH,BlockRailBase.EnumRailDirection.SOUTH_EAST,BlockRailBase.EnumRailDirection.SOUTH_WEST,BlockRailBase.EnumRailDirection.NORTH_WEST,BlockRailBase.EnumRailDirection.NORTH_EAST};
                    return Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE,shapes[ordinal()-RAIL_EW.ordinal()]);
            }
        }
    }
    public static final class Station {
        public final String name,room;public final int x,z,rotation;public final Kind color;
        Station(String name,String room,int x,int z,int rotation,Kind color) {this.name=name;this.room=room;this.x=x;this.z=z;this.rotation=rotation;this.color=color;}
        public BlockPos at(int x,int y,int z) {
            switch(rotation) {
                case 1:return new BlockPos(this.x-z,y,this.z+x);
                case 2:return new BlockPos(this.x-x,y,this.z-z);
                case 3:return new BlockPos(this.x+z,y,this.z-x);
                default:return new BlockPos(this.x+x,y,this.z+z);
            }
        }
    }
    public static final List<Station> STATIONS=Collections.unmodifiableList(Arrays.asList(
            new Station("СЕВЕРНАЯ","Электрощитовая",0,-64,0,Kind.BLUE),
            new Station("ВОСТОЧНАЯ","Мастерская",64,0,1,Kind.GREEN),
            new Station("ЮЖНАЯ","Пост охраны",0,64,2,Kind.RED),
            new Station("ЗАПАДНАЯ","Склад",-64,0,3,Kind.YELLOW)));
    private final LinkedHashMap<BlockPos,Kind> cells=new LinkedHashMap<>();
    private final LinkedHashMap<BlockPos,String[]> signs=new LinkedHashMap<>();
    public final int surface;
    private MetroNetworkPlan(int surface) {this.surface=surface;}
    public Map<BlockPos,Kind> cells() {return Collections.unmodifiableMap(cells);}
    public Map<BlockPos,String[]> signs() {return Collections.unmodifiableMap(signs);}
    public BlockPos min() {return new BlockPos(-87,0,-88);}
    public BlockPos max() {return new BlockPos(87,Math.max(9,surface+4),87);}
    public BlockPos entrance() {return STATIONS.get(0).at(16,surface+1,-16);}
    public static MetroNetworkPlan create(int surface) {
        if(surface<12 || surface>64)throw new IllegalArgumentException("Entrance height must be 12..64 above rail bed");
        MetroNetworkPlan p=new MetroNetworkPlan(surface);p.ring();
        for(Station station:STATIONS)p.station(station);
        p.tracks(60);p.tracks(68);p.entranceBuilding();return p;
    }
    private void put(BlockPos p,Kind kind) {cells.put(p,kind);if(!kind.sign())signs.remove(p);}
    private void put(int x,int y,int z,Kind kind) {put(new BlockPos(x,y,z),kind);}
    private void put(Station s,int x,int y,int z,Kind kind) {
        if(kind==Kind.STAIR_E)kind=Kind.values()[Kind.STAIR_E.ordinal()+s.rotation];
        if(kind==Kind.SIGN_S)kind=Kind.values()[Kind.SIGN_S.ordinal()+s.rotation];
        put(s.at(x,y,z),kind);
    }
    private void sign(Station s,int x,int y,int z,String... lines) {
        put(s,x,y,z,Kind.SIGN_S);String[] text={"","","",""};System.arraycopy(lines,0,text,0,Math.min(4,lines.length));signs.put(s.at(x,y,z),text);
    }
    private void ring() {
        for(int x=-71;x<=71;x++)for(int z=-71;z<=71;z++) {
            int r=Math.max(Math.abs(x),Math.abs(z));if(r<57)continue;
            for(int y=0;y<=7;y++) {
                Kind k=y==0?Kind.FLOOR:y==7 || r==57 || r==71 || (y==6 && (r==58 || r==70))?Kind.BRICK:Kind.AIR;
                if(y==1 && r==64)k=Kind.PLATFORM;
                if(y==5 && (r==57 || r==71) && (Math.abs(x+z)%8==0))k=Kind.TRIM;
                if(y==6 && r==64 && (x%16==0 || z%16==0))k=Kind.LIGHT;
                put(x,y,z,k);
            }
        }
    }
    private void station(Station s) {
        for(int x=-24;x<=24;x++)for(int z=-11;z<=11;z++)for(int y=0;y<=9;y++) {
            boolean end=Math.abs(x)==24;
            boolean portal=end && Math.abs(z)<=6 && y<=6;
            Kind k=y==0?Kind.FLOOR:y==9 || Math.abs(z)==11 || (end && !portal)?Kind.BRICK:Kind.AIR;
            if(y==3 && Math.abs(z)==11)k=s.color;
            if(y==1 && Math.abs(z)<=9 && (Math.abs(z)<=2 || Math.abs(z)>=6))k=Kind.PLATFORM;
            if(y==8 && (Math.abs(z)==10 || x%8==0))k=Kind.TRIM;
            if(y==7 && x%8==0 && (z==0 || Math.abs(z)==7))k=Kind.LIGHT;
            if(Math.abs(z)==9 && x%8==0 && y>=2 && y<=7)k=y==5?s.color:Kind.TRIM;
            put(s,x,y,z,k);
        }
        // Benches on side platforms, leaving a two-block clear walking lane.
        for(int bx:new int[]{-16,-8,0,8})for(int x=bx-1;x<=bx+1;x++)for(int z:new int[]{-8,8})put(s,x,2,z,Kind.SLAB);
        // Pedestrian bridge: three stairs up from island and both side platforms.
        for(int z=-9;z<=9;z++)for(int x=17;x<=19;x++)put(s,x,4,z,Kind.BRICK);
        for(int z:new int[]{-8,-7,-6,-1,0,1,6,7,8})for(int i=0;i<3;i++) {
            int x=14+i;for(int y=1;y<2+i;y++)put(s,x,y,z,Kind.BRICK);put(s,x,2+i,z,Kind.STAIR_E);
        }
        for(int z=-9;z<=9;z++)if(Math.abs(z)>1 && (Math.abs(z)<6 || Math.abs(z)>8)) {
            put(s,17,5,z,Kind.BAR);put(s,19,5,z,Kind.BAR);
        }
        // Station name and route signage, wall directly behind each sign.
        sign(s,4,4,-10,"MinETRO",s.name,"Кольцевая линия","Переход: восток");
        sign(s,-12,4,-10,"Следующая:",STATIONS.get((s.rotation+1)%4).name,"Два пути","Выход: Северная");
        serviceRoom(s);
    }
    private void serviceRoom(Station s) {
        for(int x=8;x<=22;x++)for(int z=-23;z<=-11;z++)for(int y=0;y<=8;y++) {
            boolean wall=x==8 || x==22 || z==-23 || z==-11;
            Kind k=y<=1?Kind.BRICK:y==8 || wall?Kind.BRICK:Kind.AIR;
            if(z==-11 && x>=11 && x<=13 && y>=2 && y<=4)k=Kind.AIR;
            if(y==3 && wall && !(z==-11 && x>=11 && x<=13))k=s.color;
            if(y==7 && x%4==0 && z==-17)k=Kind.LIGHT;
            put(s,x,y,z,k);
        }
        sign(s,19,3,-10,"Служебное",s.room,"Переход над", "путями →");
        put(s,10,2,-21,Kind.CRAFT);
        for(int x=18;x<=20;x++) {put(s,x,2,-21,Kind.BRICK);put(s,x,4,-21,Kind.SLAB);}
    }
    private void tracks(int r) {
        for(int a=-r;a<=r;a++) {
            rail(a,-r,a==-r?Kind.RAIL_SE:a==r?Kind.RAIL_SW:Kind.RAIL_EW);
            rail(a,r,a==-r?Kind.RAIL_NE:a==r?Kind.RAIL_NW:Kind.RAIL_EW);
            if(Math.abs(a)<r) {rail(-r,a,Kind.RAIL_NS);rail(r,a,Kind.RAIL_NS);}
        }
    }
    private void rail(int x,int z,Kind k) {
        if((k==Kind.RAIL_EW || k==Kind.RAIL_NS) && (Math.abs(x+z)%8==0)) {
            put(x,0,z,Kind.POWER);k=k==Kind.RAIL_EW?Kind.BOOST_EW:Kind.BOOST_NS;
        }
        put(x,1,z,k);
    }
    private void entranceBuilding() {
        Station n=STATIONS.get(0);
        // Ladder shaft, with a solid backing and two clear interior columns.
        for(int y=8;y<=surface+3;y++)for(int x=14;x<=18;x++)for(int z=-22;z<=-18;z++)
            put(n,x,y,z,x==14 || x==18 || z==-22 || z==-18?Kind.BRICK:Kind.AIR);
        for(int y=2;y<=surface+2;y++) {
            put(n,16,y,-22,Kind.BRICK);put(n,16,y,-21,Kind.LADDER);
        }
        // Surface vestibule surrounds the shaft; no unsupported/sign-only entry.
        for(int x=12;x<=20;x++)for(int z=-24;z<=-16;z++)for(int y=surface;y<=surface+4;y++) {
            boolean shaft=x==16 && z==-21;
            if(y==surface && shaft)continue;
            Kind k=y==surface || y==surface+4 || x==12 || x==20 || z==-24 || z==-16?Kind.BRICK:Kind.AIR;
            if((x==12 || x==20) && z>=-22 && z<=-18 && y==surface+2)k=Kind.GLASS;
            if(z==-16 && x>=15 && x<=17 && y>surface && y<surface+3)k=Kind.AIR;
            if(y==surface+3 && (x==14 || x==18) && z==-18)k=Kind.LIGHT;
            put(n,x,y,z,k);
        }
        // Restore the upper ladder after carving the vestibule interior.
        for(int y=surface;y<=surface+2;y++) {put(n,16,y,-22,Kind.BRICK);put(n,16,y,-21,Kind.LADDER);}
        sign(n,16,surface+3,-15,"MinETRO","МЕТРО","Северная","Спуск по лестнице");
    }
}
