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

    @Test public void newUnderhandProfilesKeepPalmsPinnedForEveryGunSkinSideAndReloadPhase(){
        for(String id:new String[]{"ak74","aks74u","duplet","rpk","saiga","tihar","vsv","vintovka","pkm","podonok","preved","svd","vssk","ybludok","yboinik"})
            for(boolean left:new boolean[]{false,true})for(boolean slim:new boolean[]{false,true})
                for(boolean support:new boolean[]{false,true})for(float reload:new float[]{0,.5F,1}){
                    float[] contact=GunHandPose.wrist(new float[]{.4F,.19F,-.175F},support,left,reload);
                    Matrix4f matrix=GunHandPose.armMatrix(id,contact,left,support,slim);
                    float center=(left?1:-1)*(slim?.5F:1);
                    Vector4f palm=new Vector4f(center/16,9/16F,0,1),shoulder=new Vector4f(center/16,-2/16F,0,1);
                    matrix.transform(palm);matrix.transform(shoulder);
                    assertEquals(contact[0],palm.x,1e-6F);assertEquals(contact[1],palm.y,1e-6F);assertEquals(contact[2],palm.z,1e-6F);
                    assertTrue(shoulder.x<contact[0]);assertTrue(shoulder.y<contact[1]);assertTrue(matrix.determinant()>0);
                }
    }
    @Test public void newRifleArmsAreSmallerButTheApprovedRevolverIsUnchanged(){
        float[] contact={.2F,.1F,-.175F};
        for(boolean left:new boolean[]{false,true})for(boolean slim:new boolean[]{false,true})for(boolean support:new boolean[]{false,true}){
            Matrix4f original=GunHandPose.armMatrix(contact,left,support,slim);
            Matrix4f revolver=GunHandPose.armMatrix("revolver",contact,left,support,slim);
            assertTrue(original.epsilonEquals(revolver,0));
            assertEquals(.85F*.85F*.85F,GunHandPose.armMatrix("ak74",contact,left,support,slim).determinant(),1e-6F);
        }
    }

    @Test public void revolverStillUsesTheFrozenVersion190Matrices(){
        float[] contact={.12F,.19F,-.175F};
        for(boolean left:new boolean[]{false,true})for(boolean slim:new boolean[]{false,true})for(boolean support:new boolean[]{false,true}){
            Matrix4f expected=new Matrix4f(),step=new Matrix4f();expected.setIdentity();expected.setTranslation(new javax.vecmath.Vector3f(contact));
            step.rotZ((float)Math.toRadians(support?-55:-65));expected.mul(step);
            step.rotX((float)Math.toRadians(-(left?-1:1)*(support?30:18)));expected.mul(step);
            step.setIdentity();step.setTranslation(new javax.vecmath.Vector3f(-(left?1:-1)*(slim?.5F:1)/16,-9/16F,0));expected.mul(step);
            assertTrue(expected.epsilonEquals(GunHandPose.armMatrix("revolver",contact,left,support,slim),1e-7F));
        }
    }

    @Test public void rifleElbowsFanOutToOppositeSidesInsteadOfCrossingTheStock(){
        for(boolean slim:new boolean[]{false,true})for(boolean support:new boolean[]{false,true})for(boolean left:new boolean[]{false,true}){
            float[] palm={.3F,.2F,-.175F};float center=(left?1:-1)*(slim?.5F:1);
            Vector4f elbow=new Vector4f(center/16F,-2/16F,0,1);
            GunHandPose.armMatrix("ak74",palm,left,support,slim).transform(elbow);
            Vector4f watchFace=new Vector4f(0,0,-1,0);GunHandPose.armMatrix("ak74",palm,left,support,slim).transform(watchFace);
            assertTrue("outer wrist/watch face must face upwards",watchFace.y>0);
            assertTrue(elbow.x<palm[0]-.2F);assertTrue(elbow.y<palm[1]-.2F);
            assertTrue(left?elbow.z<palm[2]-.25F:elbow.z>palm[2]+.25F);
        }
    }

}
