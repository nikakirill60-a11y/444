package ru.metrocraft.port.editor;
import net.minecraft.init.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.Rotation;
import org.junit.*;
import static org.junit.Assert.*;

public class EditorBlocksTest {
    @BeforeClass public static void boot(){Bootstrap.register();}
    @Test public void ordinaryBlocksAndAirRoundTrip(){for(IBlockState s:new IBlockState[]{Blocks.AIR.getDefaultState(),Blocks.STONE.getDefaultState(),Blocks.PLANKS.getStateFromMeta(2),Blocks.WOOL.getStateFromMeta(14)})assertEquals(s,EditorBlocks.decode(EditorBlocks.encode(s)));}
    @Test public void chestsHoppersAndMachinesAreNotClipboardMaterial(){assertFalse(EditorBlocks.allowed(Blocks.CHEST.getDefaultState()));assertFalse(EditorBlocks.allowed(Blocks.HOPPER.getDefaultState()));assertFalse(EditorBlocks.allowed(Blocks.FURNACE.getDefaultState()));}
    @Test public void commandBlocksAndStructureBlocksAreRefused(){assertFalse(EditorBlocks.allowed(Blocks.COMMAND_BLOCK.getDefaultState()));assertFalse(EditorBlocks.allowed(Blocks.STRUCTURE_BLOCK.getDefaultState()));}
    @Test public void physicsAndMultiPartAndLiquidBlocksAreRefused(){for(IBlockState s:new IBlockState[]{Blocks.SAND.getDefaultState(),Blocks.TNT.getDefaultState(),Blocks.WATER.getDefaultState(),Blocks.OAK_DOOR.getDefaultState(),Blocks.BED.getDefaultState()})assertFalse(EditorBlocks.allowed(s));}
    @Test(expected=IllegalArgumentException.class) public void missingBlockNeverTurnsIntoAir(){EditorBlocks.decode(new Blueprint.Spec("missingmod:unknown",0));}
    @Test(expected=IllegalArgumentException.class) public void invalidMetadataNeverSilentlyChangesBlock(){EditorBlocks.decode(new Blueprint.Spec("minecraft:stone",15));}
    @Test public void logOrientationRotatesUsingNativeBlockLogic(){IBlockState s=Blocks.LOG.getStateFromMeta(4);assertEquals(s.withRotation(Rotation.CLOCKWISE_90),EditorBlocks.decode(EditorBlocks.encode(s.withRotation(Rotation.CLOCKWISE_90))));}
    @Test public void permissionLevelIsOperatorOnly(){assertEquals(2,new MetroEditCommand().getRequiredPermissionLevel());assertEquals("metroedit",new MetroEditCommand().getName());}
}
