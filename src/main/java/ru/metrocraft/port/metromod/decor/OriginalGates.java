package ru.metrocraft.port.metromod.decor;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ru.metrocraft.port.MetroPort;
import java.util.*;

/** Separate registry from the six pre-existing gates: no replacement of saved-world IDs. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class OriginalGates {
    public static final Map<String,OriginalGateBlock> BLOCKS=new LinkedHashMap<>();
    public static final OriginalGatePartBlock PART=new OriginalGatePartBlock();
    private OriginalGates() {}
    public static void initialize() {
        GameRegistry.registerTileEntity(OriginalGateTile.class,new ResourceLocation(MetroPort.ID,"metromod_gate"));
        GameRegistry.registerTileEntity(OriginalGatePartTile.class,new ResourceLocation(MetroPort.ID,"metromod_gate_part"));
    }
    @SubscribeEvent public static void blocks(RegistryEvent.Register<Block> event) {
        for(OriginalGateProfile profile:OriginalGateProfile.all()) {
            if(!DecorAvailability.published(profile))continue;
            OriginalGateBlock block=new OriginalGateBlock(profile);BLOCKS.put(profile.id,block);event.getRegistry().register(block);
        }
        event.getRegistry().register(PART);
    }
    @SubscribeEvent public static void items(RegistryEvent.Register<Item> event) {
        for(OriginalGateBlock block:BLOCKS.values()) {
            Item item=new OriginalGateItem(block).setRegistryName(block.getRegistryName());
            event.getRegistry().register(item);MetroPort.ITEMS.put(block.profile.id,item);
        }
    }
    public static OriginalGateTile controller(World world,BlockPos pos) {
        if(!world.isBlockLoaded(pos))return null;
        TileEntity tile=world.getTileEntity(pos);
        if(tile instanceof OriginalGateTile)return (OriginalGateTile)tile;
        if(tile instanceof OriginalGatePartTile) {
            OriginalGateTile root=((OriginalGatePartTile)tile).controller();
            if(root!=null)return root;
        }
        return OriginalDecorCollisions.pendingController(world,pos);
    }
    @SubscribeEvent public static void cleanup(net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent event) {
        World world=event.world;
        if(event.phase!=net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END || world.isRemote || world.getTotalWorldTime()%100!=0)return;
        for(TileEntity tile:new ArrayList<>(world.loadedTileEntityList))if(tile instanceof OriginalGatePartTile) {
            OriginalGatePartTile part=(OriginalGatePartTile)tile;
            if(world.isBlockLoaded(part.origin) && world.getTileEntity(part.getPos())==part) {
                OriginalGateTile root=part.controller();
                if(root==null)world.setBlockToAir(part.getPos());else root.releaseUnusedPart(part.getPos());
            }
        }
    }
}
