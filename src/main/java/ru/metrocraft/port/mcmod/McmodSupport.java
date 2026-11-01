package ru.metrocraft.port.mcmod;
import com.google.gson.Gson;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.metrocraft.port.*;
import ru.metrocraft.port.radiation.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class McmodSupport {
    public static final Map<String,Block> BLOCKS=new LinkedHashMap<>();
    public static Definition[] definitions(){
        try(Reader reader=new InputStreamReader(McmodSupport.class.getResourceAsStream("/assets/metroport/mcmod_support.json"),StandardCharsets.UTF_8)){
            return new Gson().fromJson(reader,Definition[].class);
        }catch(Exception e){throw new IllegalStateException("Cannot load MCMOD support items",e);}
    }
    @SubscribeEvent public static void blocks(RegistryEvent.Register<Block> event){
        net.minecraftforge.fml.common.registry.GameRegistry.registerTileEntity(ZoneTile.class,new ResourceLocation(MetroPort.ID,"radiation_zone"));
        for(Definition d:definitions()){
            Block block;
            switch(d.kind){
                case "collision":block=new CollisionBlock();break;
                case "radiation":block=new ZoneBlock(RadiationSystem.Zone.RADIATION);break;
                case "air":block=new ZoneBlock(RadiationSystem.Zone.AIR);break;
                case "respawn":block=new RespawnBlock();break;
                default:continue;
            }
            block.setRegistryName(MetroPort.ID,d.id).setTranslationKey("metroport."+d.id).setCreativeTab(MetroPort.DECOR);
            event.getRegistry().register(block);BLOCKS.put(d.id,block);
        }
    }
    @SubscribeEvent public static void items(RegistryEvent.Register<Item> event){
        for(Definition d:definitions()){
            if("ammo".equals(d.kind))continue;
            Item item=d.kind.equals("ammo")?new ContentItem(d):d.kind.equals("watch")?new WatchItem():new ItemBlock(BLOCKS.get(d.id));
            item.setRegistryName(MetroPort.ID,d.id).setTranslationKey("metroport."+d.id).setCreativeTab(d.kind.equals("ammo")?MetroPort.WEAPONS:d.kind.equals("watch")?MetroPort.THINGS:MetroPort.THINGS);
            event.getRegistry().register(item);MetroPort.ITEMS.put(d.id,item);
        }
    }
    private static final class CollisionBlock extends BlockBarrier {
        CollisionBlock(){super();}
        @Override @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
        public EnumBlockRenderType getRenderType(IBlockState state){
            EntityPlayer player=net.minecraft.client.Minecraft.getMinecraft().player;
            return player!=null && player.capabilities.isCreativeMode?EnumBlockRenderType.MODEL:EnumBlockRenderType.INVISIBLE;
        }
        @Override @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
        public void randomDisplayTick(IBlockState state,World world,BlockPos pos,java.util.Random random){
            EntityPlayer player=net.minecraft.client.Minecraft.getMinecraft().player;
            if(player!=null && player.capabilities.isCreativeMode && (player.getHeldItemMainhand().getItem()==Item.getItemFromBlock(this) || player.getHeldItemOffhand().getItem()==Item.getItemFromBlock(this)))
                world.spawnParticle(EnumParticleTypes.BARRIER,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,0,0,0);
        }
    }
    private static final class RespawnBlock extends Block {
        private static final AxisAlignedBB BOUNDS=new AxisAlignedBB(0,0,0,1,.1,.5);
        RespawnBlock(){super(Material.IRON);setBlockUnbreakable();setResistance(6000000);}
        @Override public boolean isOpaqueCube(IBlockState state){return false;}
        @Override public boolean isFullCube(IBlockState state){return false;}
        @Override public AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess world,BlockPos pos){return BOUNDS;}
        @Override public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing face,float x,float y,float z){
            if(!world.isRemote && !player.isSpectator()){
                BlockPos spawn=player.getPosition();
                player.setSpawnPoint(spawn,true);
                // Forge keeps per-dimension spawn positions; also select that dimension for
                // providers (e.g. Nether) that would otherwise force an Overworld respawn.
                if(spawn.equals(player.getBedLocation(player.dimension)) && player.isSpawnForced(player.dimension)) {
                    player.setSpawnDimension(player.dimension);
                    player.sendStatusMessage(new TextComponentTranslation("metroport.respawn.saved"),true);
                }
            }
            return true;
        }
    }
}
