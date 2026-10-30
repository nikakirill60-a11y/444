package ru.metrocraft.port.client;

import org.junit.Test;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;
import static org.junit.Assert.*;

public class GunRenderPosesTest {
    private final float[] grip = {-.25F, .06F, -.175F};
    private Matrix4f hand(boolean left, boolean first) {
        Matrix4f m = GunRenderPoses.compose(grip,
                first ? new float[]{-.2F,.08F,-.25F} : new float[]{0,.05F,.1F},
                first ? new float[]{0,100,0} : new float[]{0,0,90}, first ? .8F : .65F, first, left);
        if (left) {
            // Apply the real ForgeHooksClient left-hand conjugation to the RAW matrix.
            Matrix4f f = new Matrix4f(); f.setIdentity(); f.m00 = -1;
            m.mul(f,m); m.mul(f);
        }
        return m;
    }
    @Test public void bothFirstPersonMuzzlesFaceForward() {
        for (boolean left : new boolean[]{false,true}) {
            Matrix4f m = hand(left,true); Vector4f muzzle = new Vector4f(1,0,0,0); m.transform(muzzle);
            assertTrue("muzzle must point away from camera", muzzle.z < -.75F);
            assertEquals(.8F, (float)Math.sqrt(muzzle.x*muzzle.x+muzzle.y*muzzle.y+muzzle.z*muzzle.z), 1e-5F);
            assertTrue("no inside-out mesh", m.determinant() > 0);
            assertTrue(left ? muzzle.x > 0 : muzzle.x < 0);
        }
    }
    @Test public void bothGripsMapToTheHandAnchor() {
        for (boolean left : new boolean[]{false,true}) {
            Vector4f point = new Vector4f(grip[0],grip[1],grip[2],1); hand(left,true).transform(point);
            assertEquals(left ? .2F : -.2F, point.x, 1e-5F);
            assertEquals(.08F, point.y, 1e-5F); assertEquals(-.25F, point.z, 1e-5F);
        }
    }
    @Test public void thirdPersonRespectsVanillaArmParent() {
        for (boolean left : new boolean[]{false,true}) {
            Matrix4f parent = new Matrix4f(), y = new Matrix4f();
            parent.rotX((float)Math.toRadians(-90)); y.rotY((float)Math.PI); parent.mul(y); parent.mul(hand(left,false));
            Vector4f muzzle = new Vector4f(1,0,0,0); parent.transform(muzzle);
            assertEquals(0, muzzle.x, 1e-5F); assertEquals(0, muzzle.y, 1e-5F); assertEquals(-.65F, muzzle.z, 1e-5F);
        }
    }
}
