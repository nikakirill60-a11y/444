package ru.metrocraft.port.client;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

/** Scratch buffers for the LWJGL 2 state-query API used by Minecraft 1.12.2. */
public final class GlStateBuffers {
    private GlStateBuffers(){}

    /**
     * glGetFloat(pname, buffer) checks for 16 REMAINING floats, even for
     * GL_CURRENT_COLOR, which returns only RGBA. Four floats crash before the
     * driver is called. Each query owns its direct buffer; no shared position.
     * Read RGBA with absolute get(0..3): OpenGL does not advance its position.
     */
    public static FloatBuffer colorQuery(){return BufferUtils.createFloatBuffer(16);}
}
