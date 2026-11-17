package ru.metrocraft.port.client.menu;

import java.nio.FloatBuffer;
import net.minecraft.client.renderer.GlStateManager;
import ru.metrocraft.port.client.GlStateBuffers;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import net.minecraft.client.renderer.OpenGlHelper;

/** GUI-only compatibility GL scope. Restores driver AND GlStateManager caches.
 * pushAttrib alone is insufficient: Minecraft caches enable flags/bindings.
 * No global hooks into the world/weapon renderer are used.
 */
final class MenuGlScope implements AutoCloseable {
    private final int activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
    private final int program = OpenGlHelper.shadersSupported ? GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM) : 0;
    private final int mode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
    private final int texture = defaultTexture();
    private final int alphaFunc = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
    private final float alphaRef = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
    private final int depthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
    private final boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
    private final int cullFace = GL11.glGetInteger(GL11.GL_CULL_FACE_MODE);
    private final int shade = GL11.glGetInteger(GL11.GL_SHADE_MODEL);
    private final int materialFace = GL11.glGetInteger(GL11.GL_COLOR_MATERIAL_FACE);
    private final int materialMode = GL11.glGetInteger(GL11.GL_COLOR_MATERIAL_PARAMETER);
    private final int src = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB), dst = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
    private final int srcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA), dstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
    private final int[] caps = {GL11.GL_TEXTURE_2D, GL11.GL_BLEND, GL11.GL_ALPHA_TEST, GL11.GL_DEPTH_TEST,
        GL11.GL_CULL_FACE, GL11.GL_LIGHTING, GL11.GL_FOG, GL11.GL_COLOR_MATERIAL, GL11.GL_NORMALIZE,
        GL12.GL_RESCALE_NORMAL, GL11.GL_LIGHT0, GL11.GL_LIGHT1, GL11.GL_LIGHT2, GL11.GL_LIGHT3, GL11.GL_LIGHT4, GL11.GL_LIGHT5};
    private final boolean[] enabled = new boolean[caps.length];
    private final FloatBuffer color=GlStateBuffers.colorQuery();
    private final FloatBuffer clearColor=GlStateBuffers.colorQuery();
    private static int defaultTexture() {
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        return GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
    }
    MenuGlScope() {
        for (int i=0;i<caps.length;i++) enabled[i]=GL11.glIsEnabled(caps[i]);
        GL11.glGetFloat(GL11.GL_CURRENT_COLOR, color);
        GL11.glGetFloat(GL11.GL_COLOR_CLEAR_VALUE, clearColor);
        if(OpenGlHelper.shadersSupported)OpenGlHelper.glUseProgram(0);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glMatrixMode(GL11.GL_PROJECTION); GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW); GL11.glPushMatrix();
    }
    @Override public void close() {
        GL11.glMatrixMode(GL11.GL_MODELVIEW); GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION); GL11.glPopMatrix();
        GL11.glMatrixMode(mode);
        GL11.glPopAttrib();
        for (int i=0;i<caps.length;i++) set(caps[i], enabled[i]);
        GlStateManager.bindTexture(texture);
        GlStateManager.alphaFunc(alphaFunc,alphaRef);
        GlStateManager.depthFunc(depthFunc); GlStateManager.depthMask(depthMask);
        GlStateManager.cullFace(cullFace == GL11.GL_FRONT ? GlStateManager.CullFace.FRONT :
            cullFace == GL11.GL_FRONT_AND_BACK ? GlStateManager.CullFace.FRONT_AND_BACK : GlStateManager.CullFace.BACK);
        GlStateManager.shadeModel(shade); GlStateManager.colorMaterial(materialFace,materialMode);
        GlStateManager.tryBlendFuncSeparate(src,dst,srcAlpha,dstAlpha);
        GlStateManager.color(color.get(0),color.get(1),color.get(2),color.get(3));
        GlStateManager.clearColor(clearColor.get(0),clearColor.get(1),clearColor.get(2),clearColor.get(3));
        if(OpenGlHelper.shadersSupported)OpenGlHelper.glUseProgram(program);
        GlStateManager.setActiveTexture(activeTexture);
    }
    private static void set(int cap, boolean on) {
        switch(cap) {
            case GL11.GL_TEXTURE_2D: if(on)GlStateManager.enableTexture2D();else GlStateManager.disableTexture2D();break;
            case GL11.GL_BLEND: if(on)GlStateManager.enableBlend();else GlStateManager.disableBlend();break;
            case GL11.GL_ALPHA_TEST: if(on)GlStateManager.enableAlpha();else GlStateManager.disableAlpha();break;
            case GL11.GL_DEPTH_TEST: if(on)GlStateManager.enableDepth();else GlStateManager.disableDepth();break;
            case GL11.GL_CULL_FACE: if(on)GlStateManager.enableCull();else GlStateManager.disableCull();break;
            case GL11.GL_LIGHTING: if(on)GlStateManager.enableLighting();else GlStateManager.disableLighting();break;
            case GL11.GL_FOG: if(on)GlStateManager.enableFog();else GlStateManager.disableFog();break;
            case GL11.GL_COLOR_MATERIAL: if(on)GlStateManager.enableColorMaterial();else GlStateManager.disableColorMaterial();break;
            case GL11.GL_NORMALIZE: if(on)GlStateManager.enableNormalize();else GlStateManager.disableNormalize();break;
            case GL12.GL_RESCALE_NORMAL: if(on)GlStateManager.enableRescaleNormal();else GlStateManager.disableRescaleNormal();break;
            default: if(on)GlStateManager.enableLight(cap-GL11.GL_LIGHT0);else GlStateManager.disableLight(cap-GL11.GL_LIGHT0);
        }
    }
}
