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
    private static javax.vecmath.Matrix4f effective(String id,boolean left){
        javax.vecmath.Matrix4f m=ToolActionPose.thirdPerson(id,left);
        if(left){javax.vecmath.Matrix4f f=new javax.vecmath.Matrix4f();f.setIdentity();f.m00=-1;m.mul(f,m);m.mul(f);}return m;
    }
    @Test public void thirdPersonMeshHandlesAreAtThePalmForBothHandsNotTheIconHandle(){
        for(boolean left:new boolean[]{false,true})for(String id:new String[]{"mcmod_machete","mcmod_aptech"}){
            Vector4f grip=id.equals("mcmod_aptech")?new Vector4f(0,-.13F,.025F,1):new Vector4f(0,0,0,1);
            effective(id,left).transform(grip);assertEquals(0,grip.x,1e-6F);assertEquals(.05F,grip.y,1e-6F);assertEquals(.1F,grip.z,1e-6F);
        }
    }
    @Test public void thirdPersonKitIsMoreThanTwiceTheOldGeneratedIconScale(){
        Vector4f edge=new Vector4f(1,0,0,0);effective("mcmod_aptech",false).transform(edge);assertEquals(1.25F,edge.length(),1e-6F);assertTrue(edge.length()>.55F*2);
    }

}
