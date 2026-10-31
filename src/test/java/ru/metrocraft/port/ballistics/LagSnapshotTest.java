package ru.metrocraft.port.ballistics;
import net.minecraft.util.math.Vec3d;
import org.junit.Test;
import static org.junit.Assert.*;
public class LagSnapshotTest {
    @Test public void nativeHeadDamageIsDouble(){
        LagSnapshot.Hit hit=new LagSnapshot(0,0,0,0,0,0,0).trace(new Vec3d(0,1.65,-3),new Vec3d(0,1.65,3));
        assertNotNull(hit);assertEquals(LagSnapshot.Part.HEAD,hit.part);assertEquals(2,hit.damageMultiplier(),0);
    }
    @Test public void nativeBodyBoundsAndMisses(){
        LagSnapshot snapshot=new LagSnapshot(0,0,0,0,0,0,0);
        LagSnapshot.Hit hit=snapshot.trace(new Vec3d(0,.5,-3),new Vec3d(0,.5,3));
        assertNotNull(hit);assertEquals(LagSnapshot.Part.BODY,hit.part);assertEquals(1,hit.damageMultiplier(),0);
        assertNull(snapshot.trace(new Vec3d(3,.5,-3),new Vec3d(3,.5,3)));
    }
    @Test public void bodyIsAnOrientedBoxNotAnExpandedVanillaAabb(){
        Vec3d start=new Vec3d(-3,.5,.22),end=new Vec3d(3,.5,.22);
        assertNull(new LagSnapshot(0,0,0,0,0,0,0).trace(start,end));
        assertNotNull(new LagSnapshot(0,0,0,90,90,0,0).trace(start,end));
    }
    @Test public void historicalPositionDoesNotFollowCurrentMovement(){
        LagSnapshot before=new LagSnapshot(0,0,0,0,0,0,0),after=new LagSnapshot(20,0,0,0,0,0,1);
        Vec3d start=new Vec3d(0,.5,-3),end=new Vec3d(0,.5,3);
        assertNotNull(before.trace(start,end));assertNull(after.trace(start,end));
    }
    @Test public void pingSelectionMatchesNativeTwentySnapshotBuffer(){
        assertEquals(0,PlayerHistory.indexForPing(-100));assertEquals(0,PlayerHistory.indexForPing(49));
        assertEquals(4,PlayerHistory.indexForPing(200));assertEquals(19,PlayerHistory.indexForPing(5000));
    }
}
