package ru.metrocraft.port.client;

import org.junit.Test;
import javax.vecmath.Vector4f;
import static org.junit.Assert.*;

public class ToolActionPoseTest {
    @Test public void animationsStartAndReturnAtRestAndHaveAVisibleMiddle() {
        for(float p:new float[]{0,1}) {
            assertEquals(0,ToolActionPose.slash(p),1e-6);
            assertEquals(0,ToolActionPose.treatment(p),1e-6);
            assertEquals(0,ToolActionPose.extraction(p),1e-6);
        }
        assertEquals(1,ToolActionPose.slash(.5F),1e-6);
        assertTrue(ToolActionPose.treatment(.5F)>.9);
        assertEquals(1,ToolActionPose.extraction(.5F),1e-6);
    }
    @Test public void progressIsClampedAndFinite() {
        for(float p:new float[]{-100,0,.25F,.5F,1,100,Float.NaN,Float.POSITIVE_INFINITY}) {
            assertTrue(Float.isFinite(ToolActionPose.slash(p)));
            assertTrue(ToolActionPose.extraction(p)>=0 && ToolActionPose.extraction(p)<=1);
        }
        assertTrue(ToolActionPose.animated("mcmod_machete"));assertTrue(ToolActionPose.animated("mcmod_aptech"));
        assertFalse(ToolActionPose.animated("medkit"));assertFalse(ToolActionPose.animated(null));
    }
    @Test public void actualRenderMatrixPinsSteveAndAlexPalmsIncludingOppositeSupportHand() {
        float[] contact={-.25F,.025F,.08F};
        for(boolean left:new boolean[]{false,true})for(boolean slim:new boolean[]{false,true})for(boolean support:new boolean[]{false,true}) {
            float center=(left?1:-1)*(slim?.5F:1);
            Vector4f palm=new Vector4f(center/16F,9/16F,0,1);
            ToolActionPose.armMatrix(contact,left,slim,support).transform(palm);
            assertEquals(contact[0],palm.x,1e-6);assertEquals(contact[1],palm.y,1e-6);assertEquals(contact[2],palm.z,1e-6);
        }
    }
}
