package ru.metrocraft.port.entity;
import net.minecraft.util.math.Vec3d;
import org.junit.Test;
import static org.junit.Assert.*;
public class MetroBulletTest {
    @Test public void nativeAirDragAndGravity(){
        Vec3d next=MetroBullet.nextMotion(new Vec3d(40,0,0),.99F,.25F);
        assertEquals(39.6,next.x,1e-5);assertEquals(-.005,next.y,1e-7);assertEquals(0,next.z,0);
    }
    @Test public void nativeWaterDrag(){
        Vec3d next=MetroBullet.nextMotion(new Vec3d(10,2,4),.8F,.25F);
        assertEquals(8,next.x,1e-5);assertEquals(1.595,next.y,1e-5);assertEquals(3.2,next.z,1e-5);
    }
    @Test public void repeatedTicksActuallyCurveRatherThanHitscan(){
        Vec3d velocity=new Vec3d(40,0,0),pos=Vec3d.ZERO;
        for(int i=0;i<20;i++){pos=pos.add(velocity);velocity=MetroBullet.nextMotion(velocity,.99F,.25F);}
        assertTrue(pos.x>700&&pos.x<800);assertTrue(pos.y<-.8);assertTrue(velocity.x<40);
    }
}
