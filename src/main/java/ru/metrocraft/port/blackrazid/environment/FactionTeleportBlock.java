package ru.metrocraft.port.blackrazid.environment;
import java.io.File;
import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.*;
import net.minecraftforge.common.config.Configuration;
/** Selected faction-teleport mechanic only. Server scoreboard teams replace the excluded general faction system. */
public final class FactionTeleportBlock extends Block {
    public static final String[] SOURCE_FACTIONS={"Polis","Kr","Nazi","ArbatKonfederation","Ganza","Konfederation1905year","VDNH","Prest"};
    public static final class Target {
        public final String team;public final int dimension;public final BlockPos pos;
        public Target(String team,int dimension,BlockPos pos){this.team=team;this.dimension=dimension;this.pos=pos.toImmutable();}
    }
    private static volatile String randomTeam="";
    private static volatile List<Target> configured=Collections.emptyList();
    private static final class Cooldown {World world;long until;Cooldown(World w,long time){world=w;until=time;}}
    private final Map<EntityPlayer,Cooldown> cooldowns=new WeakHashMap<>();
    public FactionTeleportBlock(){super(Material.ROCK);setBlockUnbreakable();setResistance(6000000);}
    public static void load(File file){
        Configuration c=new Configuration(file);c.load();randomTeam=c.getString("randomTeam","access","","Optional server team for the native unaffiliated random-destination branch; blank disables it.").trim();List<Target> points=new ArrayList<>();
        String[] teams={"polis","red_line","reich","arbat","hansa","confederation1905","vdnh","prest"};
        for(int i=0;i<SOURCE_FACTIONS.length;i++){
            String key="destination."+SOURCE_FACTIONS[i];boolean enabled=c.getBoolean("enabled",key,false,"Set a safe existing map destination, then explicitly enable it.");
            String team=c.getString("team",key,teams[i],"Exact server scoreboard team name; cosmetic patches never grant access.").trim();
            int dimension=c.getInt("dimension",key,0,Integer.MIN_VALUE,Integer.MAX_VALUE,"Same-dimension teleport only.");
            int x=c.getInt("x",key,0,-29999983,29999983,"Feet block X"),y=c.getInt("y",key,1,1,254,"Feet block Y"),z=c.getInt("z",key,0,-29999983,29999983,"Feet block Z");
            if(enabled&&!team.isEmpty())points.add(new Target(team,dimension,new BlockPos(x,y,z)));
        }
        configured=Collections.unmodifiableList(points);if(c.hasChanged())c.save();
    }
    public static Target select(List<Target> targets,String team,int dimension){
        if(team==null)return null;Target chosen=null;
        for(Target t:targets)if(t.dimension==dimension&&t.team.equals(team)){if(chosen!=null)return null;chosen=t;}
        return chosen; // Duplicate team destinations fail closed, rather than silently choosing an arbitrary base.
    }
    public static Target select(List<Target> targets,String team,int dimension,String randomTeam,Random random){
        if(team==null||randomTeam.isEmpty()||!team.equals(randomTeam))return select(targets,team,dimension);
        List<Target> eligible=new ArrayList<>();for(Target t:targets)if(t.dimension==dimension)eligible.add(t);
        return eligible.isEmpty()?null:eligible.get(random.nextInt(eligible.size())); // All eight source bases can be reached, not nextInt(7).
    }
    public static boolean safeLanding(World world,BlockPos p){
        int height=world.getHeight();if(height<=0)height=256;
        if(p.getY()<1||p.getY()+1>=height||!world.getWorldBorder().contains(p)||!world.isBlockLoaded(p)||!world.isBlockLoaded(p.up())||!world.isBlockLoaded(p.down()))return false;
        // Exact two-cell air column: no lava, water, fire, door, fence or suffocation landing.
        return world.getBlockState(p).getMaterial()==Material.AIR&&world.getBlockState(p.up()).getMaterial()==Material.AIR&&world.getBlockState(p.down()).isSideSolid(world,p.down(),EnumFacing.UP);
    }
    public boolean teleport(World world,EntityPlayerMP player,List<Target> targets){
        if(world.isRemote||player.world!=world||!player.isEntityAlive()||player.isSpectator()||player.isRiding()||player.isBeingRidden())return false;
        Cooldown last=cooldowns.get(player);long now=world.getTotalWorldTime();if(last!=null&&last.world==world&&now<last.until)return false;
        Target t=select(targets,player.getTeam()==null?null:player.getTeam().getName(),player.dimension,randomTeam,world.rand);if(t==null)return false;
        int height=world.getHeight();if(height<=0)height=256;
        if(t.pos.getY()<1||t.pos.getY()+1>=height||!world.getWorldBorder().contains(t.pos))return false;
        cooldowns.put(player,new Cooldown(world,now+100)); // Also throttle repeated reads of an unavailable/corrupt map chunk.
        if(!world.isBlockLoaded(t.pos)){
            if(!(world instanceof WorldServer))return false;net.minecraft.world.gen.ChunkProviderServer provider=((WorldServer)world).getChunkProvider();
            // A configured point may load an EXISTING map chunk. Never generate an uncharted destination.
            if(!provider.isChunkGeneratedAt(t.pos.getX()>>4,t.pos.getZ()>>4))return false;if(provider.loadChunk(t.pos.getX()>>4,t.pos.getZ()>>4)==null)return false;
        }
        if(!safeLanding(world,t.pos))return false;
        player.motionX=player.motionY=player.motionZ=0;player.fallDistance=0;
        player.setPositionAndUpdate(t.pos.getX()+.5,t.pos.getY(),t.pos.getZ()+.5);return true;
    }
    @Override public boolean onBlockActivated(World w,BlockPos p,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing side,float x,float y,float z){
        if(!w.isRemote&&player instanceof EntityPlayerMP&&!teleport(w,(EntityPlayerMP)player,configured))player.sendStatusMessage(new TextComponentTranslation("metroport.blackrazid.teleport.unavailable"),true);
        return true;
    }
}
