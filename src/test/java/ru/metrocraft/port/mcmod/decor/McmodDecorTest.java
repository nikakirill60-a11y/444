package ru.metrocraft.port.mcmod.decor;

import net.minecraft.init.Bootstrap;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.client.renderer.block.model.*;
import org.junit.*;
import ru.metrocraft.port.mcmod.McmodObjMesh;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import static org.junit.Assert.*;

public class McmodDecorTest {
    @BeforeClass public static void setup() {Bootstrap.register();McmodDecorContent.load();}
    @Test public void twentyOriginalMeshesParseWithTheActualRendererReader()throws Exception {
        assertEquals(20,McmodDecorContent.entries.length);Set<String> ids=new HashSet<>();
        for(McmodDecorContent.Entry e:McmodDecorContent.entries) {
            assertTrue(ids.add(e.id));
            try(InputStream in=getClass().getResourceAsStream("/assets/metroport/models/mcmod/decor/"+e.sourceId+".obj")) {
                McmodObjMesh mesh=McmodObjMesh.read(in);int count=0;
                for(List<float[]> group:mesh.groups.values())for(float[] v:group) {
                    count++;assertEquals(8,v.length);for(float f:v)assertTrue(Float.isFinite(f));
                    for(int axis=0;axis<3;axis++)assertTrue(v[axis]>=e.bounds[axis]-1e-4 && v[axis]<=e.bounds[axis+3]+1e-4);
                }
                assertTrue(e.id,count>30);
            }
        }
    }
    @Test public void allTwentyOriginalTexturesDecode()throws Exception {
        for(McmodDecorContent.Entry e:McmodDecorContent.entries)
            try(InputStream in=getClass().getResourceAsStream("/assets/metroport/textures/mcmod/decor/"+e.sourceId+".png")) {
                assertNotNull(e.id,in);java.awt.image.BufferedImage image=ImageIO.read(in);
                assertNotNull(image);assertTrue(image.getWidth()>=256);assertTrue(image.getHeight()>=256);
            }
    }
    @Test public void recoveredRotationsAndNativeFacingMetadataAgree() {
        assertArrayEquals(new int[]{270,180,90,0},new int[]{DecorPose.angle(0),DecorPose.angle(1),DecorPose.angle(2),DecorPose.angle(3)});
        for(McmodDecorContent.Entry e:McmodDecorContent.entries) {
            McmodDecorBlock block=new McmodDecorBlock(e);
            assertEquals(net.minecraft.block.material.Material.ROCK,block.getMaterial(block.getDefaultState()));
            assertEquals(net.minecraft.block.SoundType.METAL,block.getSoundType(block.getDefaultState(),null,BlockPos.ORIGIN,null));
            assertEquals(-1,block.getBlockHardness(block.getDefaultState(),null,BlockPos.ORIGIN),0);
            for(int meta=0;meta<4;meta++) {
                assertEquals(meta,block.getMetaFromState(block.getStateFromMeta(meta)));
                assertEquals((meta+1)&3,block.getMetaFromState(block.withRotation(block.getStateFromMeta(meta),Rotation.CLOCKWISE_90)));
            }
        }
    }
    @Test public void allRotatedCollisionsStayInTheirCellAndItemModelsFit() {
        for(McmodDecorContent.Entry e:McmodDecorContent.entries)for(int meta=0;meta<4;meta++) {
            AxisAlignedBB b=DecorPose.bounds(e,meta),c=DecorPose.collision(e,meta);
            assertTrue(b.maxX>b.minX && b.maxY>b.minY && b.maxZ>b.minZ);assertNotNull(e.id,c);
            assertTrue(c.minX>=0 && c.minY>=0 && c.minZ>=0);assertTrue(c.maxX<=1 && c.maxY<=1 && c.maxZ<=1);
            double largest=Math.max(e.bounds[3]-e.bounds[0],Math.max(e.bounds[4]-e.bounds[1],e.bounds[5]-e.bounds[2]));
            assertEquals(.72,largest*DecorPose.itemScale(e),1e-5);
        }
    }
    @Test public void nativeBlockstateAndItemParsersResolveAllActualModelPaths()throws Exception {
        for(McmodDecorContent.Entry e:McmodDecorContent.entries) {
            try(Reader in=new InputStreamReader(getClass().getResourceAsStream("/assets/metroport/blockstates/"+e.id+".json"),"UTF-8")) {
                ModelBlockDefinition def=ModelBlockDefinition.parseFromReader(in);
                for(String facing:new String[]{"north","east","south","west"}) {
                    ResourceLocation model=def.getVariant("facing="+facing).getVariantList().get(0).getModelLocation();
                    assertNotNull(getClass().getResource("/assets/metroport/models/"+model.getPath()+".json"));
                }
            }
            try(Reader in=new InputStreamReader(getClass().getResourceAsStream("/assets/metroport/models/item/"+e.id+".json"),"UTF-8")) {
                ModelBlock model=ModelBlock.deserialize(in);assertNotNull(model.getParentLocation());
            }
        }
    }
}
