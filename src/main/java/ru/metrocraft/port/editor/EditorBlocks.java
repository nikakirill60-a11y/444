package ru.metrocraft.port.editor;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import ru.metrocraft.port.ExpandedContent;
import java.util.*;

/** Explicit inert-block allowlist: no machines, containers, gates, physics blocks or missing-block substitution. */
public final class EditorBlocks {
    private EditorBlocks(){}
    public static boolean allowed(IBlockState s){
        Block b=s.getBlock();
        if(b.hasTileEntity(s))return false;
        if(b==Blocks.AIR)return true;
        if(ExpandedContent.BLOCKS.containsValue(b))return b.getClass()==Block.class && s.isFullCube();
        return Arrays.asList(Blocks.STONE,Blocks.COBBLESTONE,Blocks.STONEBRICK,Blocks.BRICK_BLOCK,
            Blocks.PLANKS,Blocks.LOG,Blocks.LOG2,Blocks.SANDSTONE,Blocks.RED_SANDSTONE,
            Blocks.END_STONE,Blocks.END_BRICKS,Blocks.WOOL,Blocks.STAINED_HARDENED_CLAY,
            Blocks.HARDENED_CLAY,Blocks.CONCRETE,Blocks.QUARTZ_BLOCK,Blocks.PRISMARINE,
            Blocks.OBSIDIAN,Blocks.NETHERRACK,Blocks.NETHER_BRICK,Blocks.DIRT,Blocks.GRASS,
            Blocks.GLASS,Blocks.STAINED_GLASS,Blocks.PACKED_ICE,Blocks.COAL_BLOCK,
            Blocks.IRON_BLOCK,Blocks.GOLD_BLOCK,Blocks.DIAMOND_BLOCK,Blocks.LAPIS_BLOCK,
            Blocks.EMERALD_BLOCK,Blocks.BONE_BLOCK,Blocks.BOOKSHELF,Blocks.HAY_BLOCK).contains(b);
    }
    public static Blueprint.Spec encode(IBlockState state){
        if(!allowed(state) || state.getBlock().getRegistryName()==null)throw new IllegalArgumentException("Only supported ordinary building blocks; no tile entities/gates/containers");
        return new Blueprint.Spec(state.getBlock().getRegistryName().toString(),state.getBlock().getMetaFromState(state));
    }
    public static IBlockState decode(Blueprint.Spec spec){
        ResourceLocation id=new ResourceLocation(spec.block);
        if(!Block.REGISTRY.containsKey(id))throw new IllegalArgumentException("Unknown block: "+spec.block);
        Block b=Block.REGISTRY.getObject(id);IBlockState state=b.getStateFromMeta(spec.meta);
        if(!allowed(state) || b.getMetaFromState(state)!=spec.meta)throw new IllegalArgumentException("Unsupported block state: "+spec.block);
        return state;
    }
}
