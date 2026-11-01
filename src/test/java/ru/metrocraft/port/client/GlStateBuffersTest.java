package ru.metrocraft.port.client;

import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import org.junit.Test;
import org.lwjgl.BufferChecks;
import org.lwjgl.BufferUtils;
import static org.junit.Assert.*;

/** Exercises the real LWJGL 2 precondition from the crash, without an OpenGL context. */
public class GlStateBuffersTest {
    @Test public void oldFourFloatColorQueryReproducesReportedLwjglFailure(){
        try{
            BufferChecks.checkBuffer(BufferUtils.createFloatBuffer(4),16);
            fail("LWJGL must reject a four-float glGetFloat destination");
        }catch(IllegalArgumentException expected){
            assertTrue(expected.getMessage().contains("16"));
            assertTrue(expected.getMessage().contains("4"));
        }
    }
    @Test public void newColorQueryPassesActualLwjglSixteenFloatCheck(){
        FloatBuffer color=GlStateBuffers.colorQuery();
        assertTrue(color.isDirect());assertEquals(ByteOrder.nativeOrder(),color.order());
        assertEquals(0,color.position());assertEquals(16,color.remaining());
        BufferChecks.checkBuffer(color,16);
    }
    @Test public void rgbaRestoreDoesNotRequireFlippingOrAdvanceBufferPosition(){
        FloatBuffer color=GlStateBuffers.colorQuery();
        // Model native absolute writes: glGetFloat does not increment position.
        float[] rgba={.125F,.25F,.5F,.75F};
        for(int i=0;i<4;i++)color.put(i,rgba[i]);
        for(int i=0;i<4;i++)assertEquals(rgba[i],color.get(i),0);
        assertEquals(0,color.position());assertEquals(16,color.remaining());
        BufferChecks.checkBuffer(color,16);
    }
    @Test public void nestedRenderersDoNotShareColorOrBufferPosition(){
        FloatBuffer outer=GlStateBuffers.colorQuery(),inner=GlStateBuffers.colorQuery();
        assertNotSame(outer,inner);outer.put(0,.75F);inner.put(0,.25F);inner.position(4);
        assertEquals(.75F,outer.get(0),0);assertEquals(16,outer.remaining());
        BufferChecks.checkBuffer(outer,16);
        assertEquals(12,inner.remaining());assertEquals(16,GlStateBuffers.colorQuery().remaining());
    }
}
