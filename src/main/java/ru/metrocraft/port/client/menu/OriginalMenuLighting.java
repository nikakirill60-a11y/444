package ru.metrocraft.port.client.menu;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GlStateManager;

/** Fixed-function menu lights/fog from donor setup + per-frame light method.
 * Shader-based world lighting is not installed or modified by this adapter.
 */
final class OriginalMenuLighting {
    private final FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
    private FloatBuffer values(float a,float b,float c,float d) {
        buffer.clear();buffer.put(a).put(b).put(c).put(d).flip();return buffer;
    }
    private void light(int id,int property,float a,float b,float c,float d) {
        GL11.glLight(GL11.GL_LIGHT0+id,property,values(a,b,c,d));
    }
    void setup() {
        GlStateManager.enableTexture2D(); GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA,GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableDepth();GlStateManager.depthMask(true);GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.enableAlpha();GlStateManager.alphaFunc(GL11.GL_GREATER,.1f);
        GlStateManager.enableCull();GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);GlStateManager.enableColorMaterial();
        GlStateManager.colorMaterial(GL11.GL_FRONT,GL11.GL_SPECULAR);
        GlStateManager.enableLighting();GlStateManager.color(1,1,1,1);
        GL11.glMaterial(GL11.GL_FRONT,GL11.GL_DIFFUSE,values(.9f,.9f,.9f,1));
        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT,values(.01f,.01f,.01f,0));
        for(int i=0;i<6;i++) {
            GlStateManager.enableLight(i);
            light(i,GL11.GL_AMBIENT,0,0,0,1);
            light(i,GL11.GL_SPECULAR,0,0,0,1);
            GL11.glLightf(GL11.GL_LIGHT0+i,GL11.GL_SPOT_CUTOFF,180);
            GL11.glLightf(GL11.GL_LIGHT0+i,GL11.GL_SPOT_EXPONENT,0);
            GL11.glLightf(GL11.GL_LIGHT0+i,GL11.GL_CONSTANT_ATTENUATION,i==0?1:0);
            GL11.glLightf(GL11.GL_LIGHT0+i,GL11.GL_LINEAR_ATTENUATION,i==3?5:0);
            GL11.glLightf(GL11.GL_LIGHT0+i,GL11.GL_QUADRATIC_ATTENUATION,
                i==0?0:i==1?10:i==2?30:i==3?20:i==4?30:50);
        }
        light(0,GL11.GL_SPECULAR,.6f,.6f,1,1);
        GL11.glLightf(GL11.GL_LIGHT0,GL11.GL_SPOT_CUTOFF,80);
        GL11.glLightf(GL11.GL_LIGHT0,GL11.GL_SPOT_EXPONENT,2.5f);
        GlStateManager.enableFog();GL11.glFogi(GL11.GL_FOG_MODE,GL11.GL_EXP);
        GL11.glFogf(GL11.GL_FOG_DENSITY,.1f);GL11.glFog(GL11.GL_FOG_COLOR,values(0,0,0,0));
        GL11.glHint(GL11.GL_FOG_HINT,GL11.GL_FASTEST);
    }
    /** Must be invoked AFTER camera rotations and BEFORE Z180/world translation. */
    void positions(OriginalMenuState s) {
        light(0,GL11.GL_POSITION,0,-1,5,1);light(0,GL11.GL_SPOT_DIRECTION,0,7,7,0);
        light(4,GL11.GL_POSITION,0,.24f,.3f,1);
        light(1,GL11.GL_POSITION,-.47f,.05f,.65f,1);
        light(2,GL11.GL_POSITION,.5f,.01f,.59f,1);
        float[][] selection={{100,.34f,.65f},{.32f,.34f,.65f},{.033f,.352f,.65f},{-.065f,.325f,.65f},{-.305f,.335f,.645f}};
        float[] point=selection[s.selected>=0 && s.selected<=4?s.selected:0];
        light(3,GL11.GL_POSITION,-point[0],point[1],point[2],1);
        if(s.selected==0)GlStateManager.disableLight(3);
        light(5,GL11.GL_POSITION,.455f,.215f,.65f,1);
        light(0,GL11.GL_DIFFUSE,s.lightR*3,s.lightG*3,s.lightB*3,1);
        light(4,GL11.GL_DIFFUSE,s.lightR,s.lightG,s.lightB,1);
        light(1,GL11.GL_DIFFUSE,s.lightB,s.lightB,s.lightR,1);
        light(2,GL11.GL_DIFFUSE,s.lightB,s.lightB,s.lightR,1);
        light(3,GL11.GL_DIFFUSE,s.lightB,s.lightR/1.5f,s.lightR/1.5f,1);
        light(5,GL11.GL_DIFFUSE,s.lightR,s.lightG/5,s.lightB/5,1);
    }
}
