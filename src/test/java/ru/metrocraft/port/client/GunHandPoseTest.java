package ru.metrocraft.port.client;

import org.junit.Test;
import static org.junit.Assert.*;

public class GunHandPoseTest {
    @Test public void supportOnlyWhenTheOtherLogicalHandIsEmpty() {
        for(boolean primaryLeft : new boolean[]{false,true}) {
            assertTrue(GunHandPose.usesSupport(primaryLeft, primaryLeft, false, true));
            assertFalse(GunHandPose.usesSupport(primaryLeft, primaryLeft, false, false));
            assertTrue(GunHandPose.usesSupport(!primaryLeft, primaryLeft, true, false));
            assertFalse(GunHandPose.usesSupport(!primaryLeft, primaryLeft, false, false));
        }
    }
    @Test public void rightAndLeftWristsStraddleTheSameWeaponWithoutMovingContactsForward() {
        float[] anchor = {.2F,.1F,-.175F};
        for(boolean support : new boolean[]{false,true}) {
            float[] right = GunHandPose.wrist(anchor,support,false,0);
            float[] left = GunHandPose.wrist(anchor,support,true,0);
            assertEquals(anchor[0],right[0],0);
            assertEquals(right[0],left[0],0);
            assertEquals(right[1],left[1],0);
            assertEquals(2*anchor[2],right[2]+left[2],1e-6F);
            assertTrue(support ? right[2]<anchor[2] : right[2]>anchor[2]);
        }
    }
    @Test public void reloadMovesOnlySupportDownAndBackAndLeavesInputImmutable() {
        float[] anchor = {.2F,.1F,-.175F};
        assertArrayEquals(GunHandPose.wrist(anchor,false,false,0), GunHandPose.wrist(anchor,false,false,1),0);
        float[] rest = GunHandPose.wrist(anchor,true,false,0);
        float[] loading = GunHandPose.wrist(anchor,true,false,1);
        assertEquals(rest[0]-.12F,loading[0],1e-6F);
        assertEquals(rest[1]-.22F,loading[1],1e-6F);
        assertEquals(rest[2],loading[2],0);
        assertArrayEquals(new float[]{.2F,.1F,-.175F},anchor,0);
    }
    @Test public void reloadProgressIsFiniteAndClamped() {
        float[] a = {0,0,0};
        assertArrayEquals(GunHandPose.wrist(a,true,false,0),GunHandPose.wrist(a,true,false,Float.NaN),0);
        assertArrayEquals(GunHandPose.wrist(a,true,false,0),GunHandPose.wrist(a,true,false,-1),0);
        assertArrayEquals(GunHandPose.wrist(a,true,false,1),GunHandPose.wrist(a,true,false,2),0);
    }
}
