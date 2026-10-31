package ru.metrocraft.port.exodus;

import com.google.gson.Gson;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.util.ResourceLocation;
import net.minecraft.init.Bootstrap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.*;

/** Native MC JSON deserialization and block-state/collision contracts, no OpenGL or game world. */
public class ExodusResourcesTest {
    private static ExodusContent.Entry[] entries;
    private static InputStreamReader resource(String path) {
        InputStream stream = ExodusResourcesTest.class.getResourceAsStream("/assets/metroport/" + path);
        assertNotNull(path, stream);
        return new InputStreamReader(stream, StandardCharsets.UTF_8);
    }
    @BeforeClass public static void setup() throws Exception {
        Bootstrap.register();
        try (InputStreamReader reader = resource("exodus.json")) {
            entries = new Gson().fromJson(reader, ExodusContent.Entry[].class);
        }
    }
    @Test public void minecraftAcceptsAllSourceModelElementsAndDisplayTransforms() throws Exception {
        int total = 0;
        for (ExodusContent.Entry entry : entries) {
            try (InputStreamReader reader = resource("models/" + entry.model + ".json")) {
                ModelBlock model = ModelBlock.deserialize(reader);
                assertEquals(entry.id, entry.elements, model.getElements().size());
                assertNotNull(model.getAllTransforms());
                total += model.getElements().size();
            }
        }
        assertEquals(1014, total);
    }
    @Test public void everyPlacedVariantResolvesToARealModelAfterNativeBlockPrefixing() throws Exception {
        int count = 0;
        for (ExodusContent.Entry entry : entries) if (entry.kind.equals("decor")) {
            try (InputStreamReader reader = resource("blockstates/" + entry.id + ".json")) {
                ModelBlockDefinition definition = ModelBlockDefinition.parseFromReader(reader);
                for (String face : new String[]{"north", "east", "south", "west"}) {
                    for (Variant variant : definition.getVariant("facing=" + face).getVariantList()) {
                        ResourceLocation model = variant.getModelLocation();
                        assertEquals("metroport", model.getNamespace());
                        assertEquals(entry.model, model.getPath());
                        try (InputStreamReader modelReader = resource("models/" + model.getPath() + ".json")) {
                            assertEquals(entry.elements, ModelBlock.deserialize(modelReader).getElements().size());
                        }
                        count++;
                    }
                }
            }
        }
        assertEquals(88, count);
    }
    @Test public void furnitureFacingMetadataRoundTripsWithoutChangingDefaultModel() {
        int count = 0;
        for (ExodusContent.Entry entry : entries) if (entry.kind.equals("decor")) {
            ExodusDecorBlock block = new ExodusDecorBlock(entry);
            assertEquals(EnumFacing.NORTH, block.getDefaultState().getValue(ExodusDecorBlock.FACING));
            for (int meta=0; meta<4; meta++) {
                IBlockState state = block.getStateFromMeta(meta);
                assertEquals(meta, block.getMetaFromState(state));
                assertFalse(block.isFullCube(state));
                assertFalse(block.isOpaqueCube(state));
            }
            count++;
        }
        assertEquals(22, count);
    }
    @Test public void furnitureCollisionStaysInsideItsOwnCellInEveryDirection() {
        for (ExodusContent.Entry entry : entries) if (entry.kind.equals("decor")) {
            ExodusDecorBlock block = new ExodusDecorBlock(entry);
            for (int meta=0; meta<4; meta++) {
                IBlockState state = block.getStateFromMeta(meta);
                AxisAlignedBB selected = block.getBoundingBox(state, null, BlockPos.ORIGIN);
                assertTrue(selected.minX < selected.maxX && selected.minY < selected.maxY && selected.minZ < selected.maxZ);
                AxisAlignedBB collision = block.getCollisionBoundingBox(state, null, BlockPos.ORIGIN);
                if (collision != null) {
                    assertTrue(collision.minX >= 0 && collision.minY >= 0 && collision.minZ >= 0);
                    assertTrue(collision.maxX <= 1 && collision.maxY <= 1 && collision.maxZ <= 1);
                }
            }
        }
    }
}
