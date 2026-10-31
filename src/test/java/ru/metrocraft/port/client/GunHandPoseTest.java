package ru.metrocraft.port.client;

import org.junit.Test;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;
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
    @Test public void actualArmMatrixPinsThePalmCentreToItsContactForBothSkinWidths() {
        float[] anchor = {.23F,.32F,-.208F};
        for(boolean left : new boolean[]{false,true}) for(boolean slim : new boolean[]{false,true})
            for(boolean support : new boolean[]{false,true}) for(float reload : new float[]{0,.5F,1}) {
                float[] contact = GunHandPose.wrist(anchor, support, left, reload);
                Matrix4f matrix = GunHandPose.armMatrix(contact, left, support, slim);
                float center = (left ? 1 : -1)*(slim ? .5F : 1);
                Vector4f palm = new Vector4f(center/16F,9/16F,0,1);
                matrix.transform(palm);
                assertEquals(contact[0],palm.x,1e-6F);
                assertEquals(contact[1],palm.y,1e-6F);
                assertEquals(contact[2],palm.z,1e-6F);
                assertTrue(matrix.determinant()>0);
            }
    }
    @Test public void cameraGripDoesNotPushHandsDownAnymoreAndArmExtendsBackAndDown() {
        float[] anchor = {.23F,.32F,-.208F};
        for(boolean left : new boolean[]{false,true}) for(boolean support : new boolean[]{false,true}) {
            float[] contact = GunHandPose.wrist(anchor,support,left,0);
            assertEquals(anchor[1],contact[1],0);
            Matrix4f matrix = GunHandPose.armMatrix(contact,left,support,false);
            float center = left ? 1 : -1;
            Vector4f shoulder = new Vector4f(center/16F,-2/16F,0,1);
            matrix.transform(shoulder);
            assertTrue(shoulder.x<contact[0]);
            assertTrue(shoulder.y<contact[1]);
        }
    }

}
