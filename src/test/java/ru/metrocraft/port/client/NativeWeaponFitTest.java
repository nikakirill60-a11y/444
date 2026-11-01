package ru.metrocraft.port.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.vecmath.*;
import org.junit.Test;
import ru.metrocraft.port.blackrazid.weapons.NativeWeaponVisuals;
import static org.junit.Assert.*;

/** Exercise the actual body/skin/camera matrices together, not just palm pinning. */
public class NativeWeaponFitTest {
    private static final class Pose { float[] translation,rotation; float scale; }
    private static final class Fit { float[] grip,trigger,support,aimSight; Pose firstPerson; }
    private Map<String,Fit> fits() throws IOException {
        try(Reader reader=new InputStreamReader(getClass().getResourceAsStream("/assets/metroport/geometry/blackrazid_weapon_poses.json"),StandardCharsets.UTF_8)){
            return new Gson().fromJson(reader,new TypeToken<Map<String,Fit>>(){}.getType());
        }
    }
    private Matrix4f camera(Fit fit,boolean left,boolean aim){
        float[] translation=aim?GunRenderPoses.aimTranslation(fit.grip,fit.aimSight,fit.firstPerson.scale,left,fit.firstPerson.translation[2]):fit.firstPerson.translation;
        Matrix4f matrix=GunRenderPoses.compose(fit.grip,translation,aim?new float[]{0,90,0}:fit.firstPerson.rotation,fit.firstPerson.scale,true,left);
        if(left){Matrix4f flip=new Matrix4f();flip.setIdentity();flip.m00=-1;matrix.mul(flip,matrix);matrix.mul(flip);}
        Matrix4f parent=new Matrix4f();parent.setIdentity();parent.setTranslation(new Vector3f(left?-.56F:.56F,-.52F,-.72F));parent.mul(matrix);return parent;
    }
    @Test public void allNativeMuzzlesIncludingGatlingSeparateBarrelsPointAwayInEitherHand() throws Exception {
        Map<String,Fit> fits=fits();assertEquals(12,fits.size());
        for(String source:NativeWeaponVisuals.guns())for(boolean left:new boolean[]{false,true})for(boolean aim:new boolean[]{false,true}){
            NativeWeaponVisuals.Gun gun=NativeWeaponVisuals.gun(source);
            Vector4f muzzle=new Vector4f(0,0,1,0);
            gun.modelMatrix().transform(muzzle);camera(fits.get(gun.id),left,aim).transform(muzzle);
            assertTrue(source+" muzzle points at player",muzzle.z<-.9F);
        }
    }
    @Test public void nativeSightsProjectToCrosshairForBothHandsNotTheLegacyFixedHeight() throws Exception {
        for(Fit fit:fits().values())for(boolean left:new boolean[]{false,true}){
            Vector4f sight=new Vector4f(fit.aimSight[0],fit.aimSight[1],fit.aimSight[2],1);
            camera(fit,left,true).transform(sight);
            assertEquals(0,sight.x,2e-6F);assertEquals(0,sight.y,2e-6F);assertTrue(sight.z<-.7F);
        }
    }
    @Test public void modelToPalmRatioIsIndependentOfSharedCameraScaleAndNoLongerToySized() throws Exception {
        for(String source:NativeWeaponVisuals.guns()){
            NativeWeaponVisuals.Gun gun=NativeWeaponVisuals.gun(source);
            assertTrue(source,gun.nativeScale/GunHandPose.armScale(gun.id)>=2.2F);
            if(source.equals("AK") || source.equals("Rpk") || source.equals("Saiga")){
                float[] bounds=NativeWeaponVisuals.mesh(gun.body).bounds;
                float receiverWidth=(bounds[3]-bounds[0])*gun.nativeScale;
                float palmWidth=4/16F*GunHandPose.armScale(gun.id);
                assertTrue(source+" receiver must not disappear inside a fist",receiverWidth/palmWidth>.45F);
            }
        }
    }
    @Test public void nativePalmsArePinnedForBothSkinWidthsWithSmallerArmsAndOutboardElbows() throws Exception {
        for(Map.Entry<String,Fit> entry:fits().entrySet())for(boolean left:new boolean[]{false,true})for(boolean slim:new boolean[]{false,true})
            for(boolean support:new boolean[]{false,true})for(float reload:new float[]{0,.5F,1}){
                Fit fit=entry.getValue();float[] contact=GunHandPose.wrist(entry.getKey(),support?fit.support:fit.trigger,support,left,reload);
                float center=(left?1:-1)*(slim?.5F:1);
                Vector4f palm=new Vector4f(center/16F,9/16F,0,1),elbow=new Vector4f(center/16F,-2/16F,0,1);
                Matrix4f matrix=GunHandPose.armMatrix(entry.getKey(),contact,left,support,slim);matrix.transform(palm);matrix.transform(elbow);
                assertEquals(contact[0],palm.x,1e-6F);assertEquals(contact[1],palm.y,1e-6F);assertEquals(contact[2],palm.z,1e-6F);
                assertEquals(.6F*.6F*.6F,matrix.determinant(),1e-6F);
                assertTrue(elbow.x<contact[0]);assertTrue(elbow.y<contact[1]);assertTrue(left?elbow.z<contact[2]:elbow.z>contact[2]);
            }
    }
    @Test public void nativeWristsDoNotPutBothHandsOnTheSameSideAndDoNotChangeLegacyOffsets(){
        float[] anchor={.3F,.1F,0};
        for(boolean left:new boolean[]{false,true})for(boolean support:new boolean[]{false,true}){
            float[] wrist=GunHandPose.wrist("blackrazid_ak",anchor,support,left,0);
            assertEquals(left?-.035F:.035F,wrist[2],0);
            for(String id:new String[]{"revolver","ak74","mcmod_ak74"})
                assertArrayEquals(GunHandPose.wrist(anchor,support,left,0),GunHandPose.wrist(id,anchor,support,left,0),0);
        }
    }
    @Test public void idleGripIsVisibleAboveHotbarAndNativeStockDoesNotReachNearClipPlane() throws Exception {
        Map<String,Fit> fits=fits();
        for(String source:NativeWeaponVisuals.guns())for(boolean left:new boolean[]{false,true}){
            NativeWeaponVisuals.Gun gun=NativeWeaponVisuals.gun(source);Fit fit=fits.get(gun.id);Matrix4f view=camera(fit,left,false);
            Vector4f palm=new Vector4f(fit.trigger[0],fit.trigger[1],fit.trigger[2],1);view.transform(palm);
            // Vertical NDC with Minecraft's 70-degree item FOV. Hotbar starts below NDC -.8.
            double ndcY=palm.y/-palm.z/Math.tan(Math.toRadians(35));assertTrue(source,ndcY>-.7 && ndcY<-.2);
            Matrix4f body=gun.modelMatrix();view.mul(body);float[] b=NativeWeaponVisuals.mesh(gun.body).bounds;
            for(int i=0;i<8;i++){
                Vector4f corner=new Vector4f(b[(i&1)==0?0:3],b[(i&2)==0?1:4],b[(i&4)==0?2:5],1);view.transform(corner);
                assertTrue(source+" crosses camera near plane",corner.z<-.3F);
            }
        }
    }
}
