package ru.metrocraft.port.gates;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ru.metrocraft.port.MetroPort;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = MetroPort.ID)
public final class Gates {
    public static final Map<GateType, GateBlock> BLOCKS = new EnumMap<>(GateType.class);
    public static final GatePartBlock PART = new GatePartBlock();
    public static void initialize() {
        GameRegistry.registerTileEntity(GateTile.class, new ResourceLocation(MetroPort.ID, "gate"));
        GameRegistry.registerTileEntity(GatePartTile.class, new ResourceLocation(MetroPort.ID, "gate_part"));
    }
    @SubscribeEvent public static void blocks(RegistryEvent.Register<Block> event) {
        for (GateType type : GateType.values()) {
            GateBlock block = new GateBlock(type); BLOCKS.put(type, block); event.getRegistry().register(block);
        }
        event.getRegistry().register(PART);
    }
    @SubscribeEvent public static void items(RegistryEvent.Register<Item> event) {
        for (GateBlock block : BLOCKS.values()) {
            Item item = new GateItem(block).setRegistryName(block.getRegistryName());
            event.getRegistry().register(item); MetroPort.ITEMS.put(block.type.id, item);
        }
    }
    @SubscribeEvent public static void cleanup(net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (event.phase != net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END || world.isRemote
                || world.getTotalWorldTime() % 100 != 0) return;
        // Parts in unloaded chunks are cleaned when both chunks become available.
        for (TileEntity tile : new ArrayList<>(world.loadedTileEntityList)) if (tile instanceof GatePartTile) {
            GatePartTile part = (GatePartTile) tile;
            if (part.owner != null && world.isBlockLoaded(part.origin) && part.controller() == null
                    && world.getTileEntity(part.getPos()) == part) world.setBlockToAir(part.getPos());
        }
    }
    public static BlockPos offset(BlockPos origin, EnumFacing facing, int width, int y, int depth) {
        return origin.offset(facing.rotateYCCW(), width).up(y).offset(facing, depth);
    }
    /** Reserve the complete movement pocket. Nothing outside this set is ever edited. */
    public static List<BlockPos> footprint(BlockPos origin, EnumFacing facing, GateType type) {
        List<BlockPos> result = new ArrayList<>();
        int minW = type.swing ? -3 : -2, maxW = type.swing ? 3 : 2;
        int minD = type.swing ? -1 : 0, maxD = type.swing ? 5 : 0, height = type.swing ? 4 : 8;
        for (int w = minW; w <= maxW; w++) for (int d = minD; d <= maxD; d++) for (int y = 0; y < height; y++)
            result.add(offset(origin, facing, w, y, d));
        return result;
    }
    public static GateTile controller(World world, BlockPos pos) {
        if (!world.isBlockLoaded(pos)) return null;
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof GateTile) return (GateTile) tile;
        if (tile instanceof GatePartTile) return ((GatePartTile) tile).controller();
        return null;
    }
}
