/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.GLAllocation
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.Tessellator
 *  org.lwjgl.opengl.ARBBufferObject
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GLContext
 */
package com.flansmod.client.tmt;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.ARBBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

@SideOnly(value=Side.CLIENT)
public class TmtTessellator
extends Tessellator {
    private static int nativeBufferSize = 0x200000;
    private static int trivertsInBuffer = nativeBufferSize / 48 * 6;
    public static boolean renderingWorldRenderer = false;
    public boolean defaultTexture = false;
    private int rawBufferSize = 0;
    public int textureID = 0;
    private static boolean convertQuadsToTriangles = false;
    private static boolean tryVBO = false;
    private static ByteBuffer byteBuffer = GLAllocation.func_74524_c((int)(nativeBufferSize * 4));
    private static IntBuffer intBuffer = byteBuffer.asIntBuffer();
    private static FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
    private static ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
    private int[] rawBuffer;
    private int vertexCount = 0;
    private double textureU;
    private double textureV;
    private double textureW;
    private int brightness;
    private int color;
    private boolean hasColor = false;
    private boolean hasTexture = false;
    private boolean hasBrightness = false;
    private boolean hasNormals = false;
    private int rawBufferIndex = 0;
    private int addedVertices = 0;
    private boolean isColorDisabled = false;
    public int field_78409_u;
    public double field_78408_v;
    public double field_78407_w;
    public double field_78417_x;
    private int normal;
    public static TmtTessellator instance = new TmtTessellator(0x200000);
    public boolean field_78415_z = false;
    private static boolean useVBO = false;
    private static IntBuffer vertexBuffers;
    private int vboIndex = 0;
    private static int vboCount;
    private int bufferSize;

    private TmtTessellator(int par1) {
    }

    public TmtTessellator() {
    }

    public int func_78381_a() {
        if (!this.field_78415_z) {
            throw new IllegalStateException("Not tesselating!");
        }
        this.field_78415_z = false;
        int offs = 0;
        while (offs < this.vertexCount) {
            int vtc = 0;
            vtc = this.field_78409_u == 7 && convertQuadsToTriangles ? Math.min(this.vertexCount - offs, trivertsInBuffer) : Math.min(this.vertexCount - offs, nativeBufferSize >> 5);
            intBuffer.clear();
            intBuffer.put(this.rawBuffer, offs * 10, vtc * 10);
            byteBuffer.position(0);
            byteBuffer.limit(vtc * 40);
            offs += vtc;
            if (useVBO) {
                this.vboIndex = (this.vboIndex + 1) % vboCount;
                ARBBufferObject.glBindBufferARB((int)34962, (int)vertexBuffers.get(this.vboIndex));
                ARBBufferObject.glBufferDataARB((int)34962, (ByteBuffer)byteBuffer, (int)35040);
            }
            if (this.hasTexture) {
                if (useVBO) {
                    GL11.glTexCoordPointer((int)4, (int)5126, (int)40, (long)12L);
                } else {
                    floatBuffer.position(3);
                    GL11.glTexCoordPointer((int)4, (int)40, (FloatBuffer)floatBuffer);
                }
                GL11.glEnableClientState((int)32888);
            }
            if (this.hasBrightness) {
                OpenGlHelper.func_77472_b((int)OpenGlHelper.field_77476_b);
                if (useVBO) {
                    GL11.glTexCoordPointer((int)2, (int)5122, (int)40, (long)36L);
                } else {
                    shortBuffer.position(18);
                    GL11.glTexCoordPointer((int)2, (int)40, (ShortBuffer)shortBuffer);
                }
                GL11.glEnableClientState((int)32888);
                OpenGlHelper.func_77472_b((int)OpenGlHelper.field_77478_a);
            }
            if (this.hasColor) {
                if (useVBO) {
                    GL11.glColorPointer((int)4, (int)5121, (int)40, (long)28L);
                } else {
                    byteBuffer.position(28);
                    GL11.glColorPointer((int)4, (boolean)true, (int)40, (ByteBuffer)byteBuffer);
                }
                GL11.glEnableClientState((int)32886);
            }
            if (this.hasNormals) {
                if (useVBO) {
                    GL11.glNormalPointer((int)5121, (int)40, (long)32L);
                } else {
                    byteBuffer.position(32);
                    GL11.glNormalPointer((int)40, (ByteBuffer)byteBuffer);
                }
                GL11.glEnableClientState((int)32885);
            }
            if (useVBO) {
                GL11.glVertexPointer((int)3, (int)5126, (int)40, (long)0L);
            } else {
                floatBuffer.position(0);
                GL11.glVertexPointer((int)3, (int)40, (FloatBuffer)floatBuffer);
            }
            GL11.glEnableClientState((int)32884);
            if (this.field_78409_u == 7 && convertQuadsToTriangles) {
                GL11.glDrawArrays((int)4, (int)0, (int)vtc);
            } else {
                GL11.glDrawArrays((int)this.field_78409_u, (int)0, (int)vtc);
            }
            GL11.glDisableClientState((int)32884);
            if (this.hasTexture) {
                GL11.glDisableClientState((int)32888);
            }
            if (this.hasBrightness) {
                OpenGlHelper.func_77472_b((int)OpenGlHelper.field_77476_b);
                GL11.glDisableClientState((int)32888);
                OpenGlHelper.func_77472_b((int)OpenGlHelper.field_77478_a);
            }
            if (this.hasColor) {
                GL11.glDisableClientState((int)32886);
            }
            if (!this.hasNormals) continue;
            GL11.glDisableClientState((int)32885);
        }
        if (this.rawBufferSize > 131072 && this.rawBufferIndex < this.rawBufferSize << 3) {
            this.rawBufferSize = 0;
            this.rawBuffer = null;
        }
        int var1 = this.rawBufferIndex * 4;
        this.reset();
        return var1;
    }

    private void reset() {
        this.vertexCount = 0;
        byteBuffer.clear();
        this.rawBufferIndex = 0;
        this.addedVertices = 0;
    }

    public void func_78382_b() {
        this.func_78371_b(7);
    }

    public void func_78371_b(int par1) {
        if (this.field_78415_z) {
            throw new IllegalStateException("Already tesselating!");
        }
        this.field_78415_z = true;
        this.reset();
        this.field_78409_u = par1;
        this.hasNormals = false;
        this.hasColor = false;
        this.hasTexture = false;
        this.hasBrightness = false;
        this.isColorDisabled = false;
    }

    public void func_78385_a(double par1, double par3) {
        this.hasTexture = true;
        this.textureU = par1;
        this.textureV = par3;
        this.textureW = 1.0;
    }

    public void setTextureUVW(double par1, double par3, double par4) {
        this.hasTexture = true;
        this.textureU = par1;
        this.textureV = par3;
        this.textureW = par4;
    }

    public void func_78380_c(int par1) {
        this.hasBrightness = true;
        this.brightness = par1;
    }

    public void func_78386_a(float par1, float par2, float par3) {
        this.func_78376_a((int)(par1 * 255.0f), (int)(par2 * 255.0f), (int)(par3 * 255.0f));
    }

    public void func_78369_a(float par1, float par2, float par3, float par4) {
        this.func_78370_a((int)(par1 * 255.0f), (int)(par2 * 255.0f), (int)(par3 * 255.0f), (int)(par4 * 255.0f));
    }

    public void func_78376_a(int par1, int par2, int par3) {
        this.func_78370_a(par1, par2, par3, 255);
    }

    public void func_78370_a(int par1, int par2, int par3, int par4) {
        if (!this.isColorDisabled) {
            if (par1 > 255) {
                par1 = 255;
            }
            if (par2 > 255) {
                par2 = 255;
            }
            if (par3 > 255) {
                par3 = 255;
            }
            if (par4 > 255) {
                par4 = 255;
            }
            if (par1 < 0) {
                par1 = 0;
            }
            if (par2 < 0) {
                par2 = 0;
            }
            if (par3 < 0) {
                par3 = 0;
            }
            if (par4 < 0) {
                par4 = 0;
            }
            this.hasColor = true;
            this.color = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? par4 << 24 | par3 << 16 | par2 << 8 | par1 : par1 << 24 | par2 << 16 | par3 << 8 | par4;
        }
    }

    public void func_78374_a(double par1, double par3, double par5, double par7, double par9) {
        this.func_78385_a(par7, par9);
        this.func_78377_a(par1, par3, par5);
    }

    public void addVertexWithUVW(double par1, double par3, double par5, double par7, double par9, double par10) {
        this.setTextureUVW(par7, par9, par10);
        this.func_78377_a(par1, par3, par5);
    }

    public void func_78377_a(double par1, double par3, double par5) {
        if (this.rawBufferIndex >= this.rawBufferSize - 40) {
            if (this.rawBufferSize == 0) {
                this.rawBufferSize = 65536;
                this.rawBuffer = new int[this.rawBufferSize];
            } else {
                this.rawBufferSize *= 2;
                this.rawBuffer = Arrays.copyOf(this.rawBuffer, this.rawBufferSize);
            }
        }
        ++this.addedVertices;
        if (this.field_78409_u == 7 && convertQuadsToTriangles && this.addedVertices % 4 == 0) {
            for (int var7 = 0; var7 < 2; ++var7) {
                int var8 = 10 * (3 - var7);
                if (this.hasTexture) {
                    this.rawBuffer[this.rawBufferIndex + 3] = this.rawBuffer[this.rawBufferIndex - var8 + 3];
                    this.rawBuffer[this.rawBufferIndex + 4] = this.rawBuffer[this.rawBufferIndex - var8 + 4];
                    this.rawBuffer[this.rawBufferIndex + 5] = this.rawBuffer[this.rawBufferIndex - var8 + 5];
                    this.rawBuffer[this.rawBufferIndex + 6] = this.rawBuffer[this.rawBufferIndex - var8 + 6];
                }
                if (this.hasBrightness) {
                    this.rawBuffer[this.rawBufferIndex + 9] = this.rawBuffer[this.rawBufferIndex - var8 + 9];
                }
                if (this.hasColor) {
                    this.rawBuffer[this.rawBufferIndex + 7] = this.rawBuffer[this.rawBufferIndex - var8 + 7];
                }
                this.rawBuffer[this.rawBufferIndex] = this.rawBuffer[this.rawBufferIndex - var8];
                this.rawBuffer[this.rawBufferIndex + 1] = this.rawBuffer[this.rawBufferIndex - var8 + 1];
                this.rawBuffer[this.rawBufferIndex + 2] = this.rawBuffer[this.rawBufferIndex - var8 + 2];
                ++this.vertexCount;
                this.rawBufferIndex += 10;
            }
        }
        if (this.hasTexture) {
            this.rawBuffer[this.rawBufferIndex + 3] = Float.floatToRawIntBits((float)this.textureU);
            this.rawBuffer[this.rawBufferIndex + 4] = Float.floatToRawIntBits((float)this.textureV);
            this.rawBuffer[this.rawBufferIndex + 5] = Float.floatToRawIntBits(0.0f);
            this.rawBuffer[this.rawBufferIndex + 6] = Float.floatToRawIntBits((float)this.textureW);
        }
        if (this.hasBrightness) {
            this.rawBuffer[this.rawBufferIndex + 9] = this.brightness;
        }
        if (this.hasColor) {
            this.rawBuffer[this.rawBufferIndex + 7] = this.color;
        }
        if (this.hasNormals) {
            this.rawBuffer[this.rawBufferIndex + 8] = this.normal;
        }
        this.rawBuffer[this.rawBufferIndex] = Float.floatToRawIntBits((float)(par1 + this.field_78408_v));
        this.rawBuffer[this.rawBufferIndex + 1] = Float.floatToRawIntBits((float)(par3 + this.field_78407_w));
        this.rawBuffer[this.rawBufferIndex + 2] = Float.floatToRawIntBits((float)(par5 + this.field_78417_x));
        this.rawBufferIndex += 10;
        ++this.vertexCount;
    }

    public void func_78378_d(int par1) {
        int j = par1 >> 16 & 0xFF;
        int k = par1 >> 8 & 0xFF;
        int l = par1 & 0xFF;
        this.func_78376_a(j, k, l);
    }

    public void func_78384_a(int par1, int par2) {
        int k = par1 >> 16 & 0xFF;
        int l = par1 >> 8 & 0xFF;
        int i1 = par1 & 0xFF;
        this.func_78370_a(k, l, i1, par2);
    }

    public void func_78383_c() {
        this.isColorDisabled = true;
    }

    public void func_78375_b(float par1, float par2, float par3) {
        this.hasNormals = true;
        byte b0 = (byte)(par1 * 127.0f);
        byte b1 = (byte)(par2 * 127.0f);
        byte b2 = (byte)(par3 * 127.0f);
        this.normal = b0 & 0xFF | (b1 & 0xFF) << 8 | (b2 & 0xFF) << 16;
    }

    public void func_78373_b(double par1, double par3, double par5) {
        this.field_78408_v = par1;
        this.field_78407_w = par3;
        this.field_78417_x = par5;
    }

    public void func_78372_c(float par1, float par2, float par3) {
        this.field_78408_v += (double)par1;
        this.field_78407_w += (double)par2;
        this.field_78417_x += (double)par3;
    }

    static {
        vboCount = 10;
        TmtTessellator.instance.defaultTexture = true;
        boolean bl = useVBO = tryVBO && GLContext.getCapabilities().GL_ARB_vertex_buffer_object;
        if (useVBO) {
            vertexBuffers = GLAllocation.func_74527_f((int)vboCount);
            ARBBufferObject.glGenBuffersARB((IntBuffer)vertexBuffers);
        }
    }
}

