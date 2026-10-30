/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.client.model.TexturedQuad
 *  net.minecraft.client.renderer.GLAllocation
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package com.flansmod.client.tmt;

import com.flansmod.client.tmt.Bone;
import com.flansmod.client.tmt.Coord2D;
import com.flansmod.client.tmt.ModelPool;
import com.flansmod.client.tmt.ModelPoolEntry;
import com.flansmod.client.tmt.PositionTextureVertex;
import com.flansmod.client.tmt.PositionTransformVertex;
import com.flansmod.client.tmt.Shape2D;
import com.flansmod.client.tmt.Shape3D;
import com.flansmod.client.tmt.TextureGroup;
import com.flansmod.client.tmt.TexturedPolygon;
import com.flansmod.client.tmt.TmtTessellator;
import com.flansmod.client.tmt.TransformGroup;
import com.flansmod.client.tmt.TransformGroupBone;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ModelRendererTurbo
extends ModelRenderer {
    private PositionTextureVertex[] vertices = new PositionTextureVertex[0];
    private TexturedPolygon[] faces = new TexturedPolygon[0];
    private int textureOffsetX;
    private int textureOffsetY;
    private boolean compiled = false;
    private int displayList = 0;
    private int[] displayListArray;
    private Map<String, TransformGroup> transformGroup = new HashMap<String, TransformGroup>();
    private Map<String, TextureGroup> textureGroup;
    private TransformGroup currentGroup;
    private TextureGroup currentTextureGroup;
    public boolean field_78809_i = false;
    public boolean flip = false;
    public boolean field_78806_j = true;
    public boolean field_1402_i = false;
    public boolean forcedRecompile = false;
    public boolean useLegacyCompiler;
    public List field_78804_l;
    public List field_78805_m;
    public final String field_78802_n;
    private String defaultTexture;
    public static final int MR_FRONT = 0;
    public static final int MR_BACK = 1;
    public static final int MR_LEFT = 2;
    public static final int MR_RIGHT = 3;
    public static final int MR_TOP = 4;
    public static final int MR_BOTTOM = 5;
    private static final float pi = (float)Math.PI;

    public ModelRendererTurbo(ModelBase modelbase, String s) {
        super(modelbase, s);
        this.transformGroup.put("0", new TransformGroupBone(new Bone(0.0f, 0.0f, 0.0f, 0.0f), 1.0));
        this.textureGroup = new HashMap<String, TextureGroup>();
        this.textureGroup.put("0", new TextureGroup());
        this.currentTextureGroup = this.textureGroup.get("0");
        this.field_78802_n = s;
        this.defaultTexture = "";
        this.useLegacyCompiler = false;
    }

    public ModelRendererTurbo(ModelBase modelbase) {
        this(modelbase, null);
    }

    public ModelRendererTurbo(ModelBase modelbase, int textureX, int textureY) {
        this(modelbase, textureX, textureY, 64, 32);
    }

    public ModelRendererTurbo(ModelBase modelbase, int textureX, int textureY, int textureU, int textureV) {
        this(modelbase);
        this.textureOffsetX = textureX;
        this.textureOffsetY = textureY;
        this.field_78801_a = textureU;
        this.field_78799_b = textureV;
    }

    public void addPolygon(PositionTextureVertex[] verts) {
        this.copyTo(verts, new TexturedPolygon[]{new TexturedPolygon(verts)});
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addPolygon(PositionTextureVertex[] verts, int[][] uv) {
        try {
            for (int i = 0; i < verts.length; ++i) {
                verts[i] = verts[i].setTexturePosition((float)uv[i][0] / this.field_78801_a, (float)uv[i][1] / this.field_78799_b);
            }
        }
        finally {
            this.addPolygon(verts);
        }
    }

    public void addPolygon(PositionTextureVertex[] verts, int u1, int v1, int u2, int v2) {
        this.copyTo(verts, new TexturedPolygon[]{this.addPolygonReturn(verts, u1, v1, u2, v2)});
    }

    private TexturedPolygon addPolygonReturn(PositionTextureVertex[] verts, int u1, int v1, int u2, int v2, float q1, float q2, float q3, float q4) {
        if (verts.length < 3) {
            return null;
        }
        float uOffs = 1.0f / (this.field_78801_a * 10.0f);
        float vOffs = 1.0f / (this.field_78799_b * 10.0f);
        if (verts.length < 4) {
            float xMin = -1.0f;
            float yMin = -1.0f;
            float xMax = 0.0f;
            float yMax = 0.0f;
            for (PositionTextureVertex vert : verts) {
                float xPos = vert.field_78241_b;
                float yPos = vert.field_78242_c;
                xMax = Math.max(xMax, xPos);
                xMin = xMin < -1.0f ? xPos : Math.min(xMin, xPos);
                yMax = Math.max(yMax, yPos);
                yMin = yMin < -1.0f ? yPos : Math.min(yMin, yPos);
            }
            float uMin = (float)u1 / this.field_78801_a + uOffs;
            float vMin = (float)v1 / this.field_78799_b + vOffs;
            float uSize = (float)(u2 - u1) / this.field_78801_a - uOffs * 2.0f;
            float vSize = (float)(v2 - v1) / this.field_78799_b - vOffs * 2.0f;
            float xSize = xMax - xMin;
            float ySize = yMax - yMin;
            for (int i = 0; i < verts.length; ++i) {
                float xPos = verts[i].field_78241_b;
                float yPos = verts[i].field_78242_c;
                xPos = (xPos - xMin) / xSize;
                yPos = (yPos - yMin) / ySize;
                verts[i] = verts[i].setTexturePosition(uMin + xPos * uSize, vMin + yPos * vSize);
            }
        } else {
            verts[0] = verts[0].setTexturePosition(((float)u2 / this.field_78801_a - uOffs) * q1, ((float)v1 / this.field_78799_b + vOffs) * q1, q1);
            verts[1] = verts[1].setTexturePosition(((float)u1 / this.field_78801_a + uOffs) * q2, ((float)v1 / this.field_78799_b + vOffs) * q2, q2);
            verts[2] = verts[2].setTexturePosition(((float)u1 / this.field_78801_a + uOffs) * q3, ((float)v2 / this.field_78799_b - vOffs) * q3, q3);
            verts[3] = verts[3].setTexturePosition(((float)u2 / this.field_78801_a - uOffs) * q4, ((float)v2 / this.field_78799_b - vOffs) * q4, q4);
        }
        return new TexturedPolygon(verts);
    }

    private TexturedPolygon addPolygonReturn(PositionTextureVertex[] verts, int u1, int v1, int u2, int v2) {
        if (verts.length < 3) {
            return null;
        }
        float uOffs = 1.0f / (this.field_78801_a * 10.0f);
        float vOffs = 1.0f / (this.field_78799_b * 10.0f);
        if (verts.length < 4) {
            float xMin = -1.0f;
            float yMin = -1.0f;
            float xMax = 0.0f;
            float yMax = 0.0f;
            for (PositionTextureVertex vert : verts) {
                float xPos = vert.field_78241_b;
                float yPos = vert.field_78242_c;
                xMax = Math.max(xMax, xPos);
                xMin = xMin < -1.0f ? xPos : Math.min(xMin, xPos);
                yMax = Math.max(yMax, yPos);
                yMin = yMin < -1.0f ? yPos : Math.min(yMin, yPos);
            }
            float uMin = (float)u1 / this.field_78801_a + uOffs;
            float vMin = (float)v1 / this.field_78799_b + vOffs;
            float uSize = (float)(u2 - u1) / this.field_78801_a - uOffs * 2.0f;
            float vSize = (float)(v2 - v1) / this.field_78799_b - vOffs * 2.0f;
            float xSize = xMax - xMin;
            float ySize = yMax - yMin;
            for (int i = 0; i < verts.length; ++i) {
                float xPos = verts[i].field_78241_b;
                float yPos = verts[i].field_78242_c;
                xPos = (xPos - xMin) / xSize;
                yPos = (yPos - yMin) / ySize;
                verts[i] = verts[i].setTexturePosition(uMin + xPos * uSize, vMin + yPos * vSize);
            }
        } else {
            verts[0] = verts[0].setTexturePosition((float)u2 / this.field_78801_a - uOffs, (float)v1 / this.field_78799_b + vOffs);
            verts[1] = verts[1].setTexturePosition((float)u1 / this.field_78801_a + uOffs, (float)v1 / this.field_78799_b + vOffs);
            verts[2] = verts[2].setTexturePosition((float)u1 / this.field_78801_a + uOffs, (float)v2 / this.field_78799_b - vOffs);
            verts[3] = verts[3].setTexturePosition((float)u2 / this.field_78801_a - uOffs, (float)v2 / this.field_78799_b - vOffs);
        }
        return new TexturedPolygon(verts);
    }

    public void addRectShape(float[] v, float[] v1, float[] v2, float[] v3, float[] v4, float[] v5, float[] v6, float[] v7, int w, int h, int d) {
        float[] var1 = new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
        this.addRectShape(v, v1, v2, v3, v4, v5, v6, v7, w, h, d, var1);
    }

    public void addRectShape(float[] v, float[] v1, float[] v2, float[] v3, float[] v4, float[] v5, float[] v6, float[] v7, int w, int h, int d, float[] qParam) {
        PositionTextureVertex[] verts = new PositionTextureVertex[8];
        TexturedPolygon[] poly = new TexturedPolygon[6];
        PositionTextureVertex positionTexturevertex = new PositionTextureVertex(v[0], v[1], v[2], 0.0f, 0.0f);
        PositionTextureVertex positionTexturevertex1 = new PositionTextureVertex(v1[0], v1[1], v1[2], 0.0f, 8.0f);
        PositionTextureVertex positionTexturevertex2 = new PositionTextureVertex(v2[0], v2[1], v2[2], 8.0f, 8.0f);
        PositionTextureVertex positionTexturevertex3 = new PositionTextureVertex(v3[0], v3[1], v3[2], 8.0f, 0.0f);
        PositionTextureVertex positionTexturevertex4 = new PositionTextureVertex(v4[0], v4[1], v4[2], 0.0f, 0.0f);
        PositionTextureVertex positionTexturevertex5 = new PositionTextureVertex(v5[0], v5[1], v5[2], 0.0f, 8.0f);
        PositionTextureVertex positionTexturevertex6 = new PositionTextureVertex(v6[0], v6[1], v6[2], 8.0f, 8.0f);
        PositionTextureVertex positionTexturevertex7 = new PositionTextureVertex(v7[0], v7[1], v7[2], 8.0f, 0.0f);
        verts[0] = positionTexturevertex;
        verts[1] = positionTexturevertex1;
        verts[2] = positionTexturevertex2;
        verts[3] = positionTexturevertex3;
        verts[4] = positionTexturevertex4;
        verts[5] = positionTexturevertex5;
        verts[6] = positionTexturevertex6;
        verts[7] = positionTexturevertex7;
        poly[0] = this.addPolygonReturn(new PositionTextureVertex[]{positionTexturevertex5, positionTexturevertex1, positionTexturevertex2, positionTexturevertex6}, this.textureOffsetX + d + w, this.textureOffsetY + d, this.textureOffsetX + d + w + d, this.textureOffsetY + d + h, 1.0f, qParam[7], qParam[10] * qParam[7], qParam[10]);
        poly[1] = this.addPolygonReturn(new PositionTextureVertex[]{positionTexturevertex, positionTexturevertex4, positionTexturevertex7, positionTexturevertex3}, this.textureOffsetX, this.textureOffsetY + d, this.textureOffsetX + d, this.textureOffsetY + d + h, qParam[9] * qParam[6], qParam[9], 1.0f, qParam[6]);
        poly[2] = this.addPolygonReturn(new PositionTextureVertex[]{positionTexturevertex5, positionTexturevertex4, positionTexturevertex, positionTexturevertex1}, this.textureOffsetX + d, this.textureOffsetY, this.textureOffsetX + d + w, this.textureOffsetY + d, 1.0f, qParam[8], qParam[1] * qParam[8], qParam[1]);
        poly[3] = this.addPolygonReturn(new PositionTextureVertex[]{positionTexturevertex2, positionTexturevertex3, positionTexturevertex7, positionTexturevertex6}, this.textureOffsetX + d + w, this.textureOffsetY, this.textureOffsetX + d + w + w, this.textureOffsetY + d, qParam[3], qParam[3] * qParam[11], qParam[11], 1.0f);
        poly[4] = this.addPolygonReturn(new PositionTextureVertex[]{positionTexturevertex1, positionTexturevertex, positionTexturevertex3, positionTexturevertex2}, this.textureOffsetX + d, this.textureOffsetY + d, this.textureOffsetX + d + w, this.textureOffsetY + d + h, qParam[0], qParam[0] * qParam[4], qParam[4], 1.0f);
        poly[5] = this.addPolygonReturn(new PositionTextureVertex[]{positionTexturevertex4, positionTexturevertex5, positionTexturevertex6, positionTexturevertex7}, this.textureOffsetX + d + w + d, this.textureOffsetY + d, this.textureOffsetX + d + w + d + w, this.textureOffsetY + d + h, qParam[2] * qParam[5], qParam[2], 1.0f, qParam[5]);
        if (this.field_78809_i ^ this.flip) {
            for (TexturedPolygon aPoly : poly) {
                aPoly.flipFace();
            }
        }
        this.copyTo(verts, poly);
    }

    public ModelRendererTurbo addBox(float x, float y, float z, int w, int h, int d) {
        this.func_78790_a(x, y, z, w, h, d, 0.0f);
        return this;
    }

    public void func_78790_a(float x, float y, float z, int w, int h, int d, float expansion) {
        this.addBox(x, y, z, w, h, d, expansion, 1.0f);
    }

    public void addBox(float x, float y, float z, int w, int h, int d, float expansion, float scale) {
        float scaleX = (float)w * scale;
        float scaleY = (float)h * scale;
        float scaleZ = (float)d * scale;
        float x1 = x + scaleX;
        float y1 = y + scaleY;
        float z1 = z + scaleZ;
        float expX = expansion + scaleX - (float)w;
        float expY = expansion + scaleY - (float)h;
        float expZ = expansion + scaleZ - (float)d;
        x -= expX;
        y -= expY;
        z -= expZ;
        x1 += expansion;
        y1 += expansion;
        z1 += expansion;
        if (this.field_78809_i) {
            float xTemp = x1;
            x1 = x;
            x = xTemp;
        }
        float[] v = new float[]{x, y, z};
        float[] v1 = new float[]{x1, y, z};
        float[] v2 = new float[]{x1, y1, z};
        float[] v3 = new float[]{x, y1, z};
        float[] v4 = new float[]{x, y, z1};
        float[] v5 = new float[]{x1, y, z1};
        float[] v6 = new float[]{x1, y1, z1};
        float[] v7 = new float[]{x, y1, z1};
        this.addRectShape(v, v1, v2, v3, v4, v5, v6, v7, w, h, d);
    }

    public void addTrapezoid(float x, float y, float z, int w, int h, int d, float scale, float bottomScale, int dir) {
        int m;
        float f4 = x + (float)w;
        float f5 = y + (float)h;
        float f6 = z + (float)d;
        x -= scale;
        y -= scale;
        z -= scale;
        f4 += scale;
        f5 += scale;
        f6 += scale;
        int n = m = this.field_78809_i ? -1 : 1;
        if (this.field_78809_i) {
            float f7 = f4;
            f4 = x;
            x = f7;
        }
        float[] v = new float[]{x, y, z};
        float[] v1 = new float[]{f4, y, z};
        float[] v2 = new float[]{f4, f5, z};
        float[] v3 = new float[]{x, f5, z};
        float[] v4 = new float[]{x, y, f6};
        float[] v5 = new float[]{f4, y, f6};
        float[] v6 = new float[]{f4, f5, f6};
        float[] v7 = new float[]{x, f5, f6};
        switch (dir) {
            case 3: {
                v[1] = v[1] - bottomScale;
                v[2] = v[2] - bottomScale;
                v3[1] = v3[1] + bottomScale;
                v3[2] = v3[2] - bottomScale;
                v4[1] = v4[1] - bottomScale;
                v4[2] = v4[2] + bottomScale;
                v7[1] = v7[1] + bottomScale;
                v7[2] = v7[2] + bottomScale;
                break;
            }
            case 2: {
                v1[1] = v1[1] - bottomScale;
                v1[2] = v1[2] - bottomScale;
                v2[1] = v2[1] + bottomScale;
                v2[2] = v2[2] - bottomScale;
                v5[1] = v5[1] - bottomScale;
                v5[2] = v5[2] + bottomScale;
                v6[1] = v6[1] + bottomScale;
                v6[2] = v6[2] + bottomScale;
                break;
            }
            case 0: {
                v[0] = v[0] - (float)m * bottomScale;
                v[1] = v[1] - bottomScale;
                v1[0] = v1[0] + (float)m * bottomScale;
                v1[1] = v1[1] - bottomScale;
                v2[0] = v2[0] + (float)m * bottomScale;
                v2[1] = v2[1] + bottomScale;
                v3[0] = v3[0] - (float)m * bottomScale;
                v3[1] = v3[1] + bottomScale;
                break;
            }
            case 1: {
                v4[0] = v4[0] - (float)m * bottomScale;
                v4[1] = v4[1] - bottomScale;
                v5[0] = v5[0] + (float)m * bottomScale;
                v5[1] = v5[1] - bottomScale;
                v6[0] = v6[0] + (float)m * bottomScale;
                v6[1] = v6[1] + bottomScale;
                v7[0] = v7[0] - (float)m * bottomScale;
                v7[1] = v7[1] + bottomScale;
                break;
            }
            case 4: {
                v[0] = v[0] - (float)m * bottomScale;
                v[2] = v[2] - bottomScale;
                v1[0] = v1[0] + (float)m * bottomScale;
                v1[2] = v1[2] - bottomScale;
                v4[0] = v4[0] - (float)m * bottomScale;
                v4[2] = v4[2] + bottomScale;
                v5[0] = v5[0] + (float)m * bottomScale;
                v5[2] = v5[2] + bottomScale;
                break;
            }
            case 5: {
                v2[0] = v2[0] + (float)m * bottomScale;
                v2[2] = v2[2] - bottomScale;
                v3[0] = v3[0] - (float)m * bottomScale;
                v3[2] = v3[2] - bottomScale;
                v6[0] = v6[0] + (float)m * bottomScale;
                v6[2] = v6[2] + bottomScale;
                v7[0] = v7[0] - (float)m * bottomScale;
                v7[2] = v7[2] + bottomScale;
            }
        }
        float[] qValues = new float[]{Math.abs((v[0] - v1[0]) / (v3[0] - v2[0])), Math.abs((v[0] - v1[0]) / (v4[0] - v5[0])), Math.abs((v4[0] - v5[0]) / (v7[0] - v6[0])), Math.abs((v3[0] - v2[0]) / (v7[0] - v6[0])), Math.abs((v[1] - v3[1]) / (v1[1] - v2[1])), Math.abs((v4[1] - v7[1]) / (v5[1] - v6[1])), Math.abs((v[1] - v3[1]) / (v4[1] - v7[1])), Math.abs((v1[1] - v2[1]) / (v5[1] - v6[1])), Math.abs((v[2] - v4[2]) / (v1[2] - v5[2])), Math.abs((v[2] - v4[2]) / (v3[2] - v7[2])), Math.abs((v1[2] - v5[2]) / (v2[2] - v6[2])), Math.abs((v3[2] - v7[2]) / (v2[2] - v6[2]))};
        this.addRectShape(v, v1, v2, v3, v4, v5, v6, v7, w, h, d);
    }

    public void addFlexBox(float x, float y, float z, int w, int h, int d, float scale, float bScale1, float bScale2, float bScale3, float bScale4, int dir) {
        int m;
        float f4 = x + (float)w;
        float f5 = y + (float)h;
        float f6 = z + (float)d;
        x -= scale;
        y -= scale;
        z -= scale;
        f4 += scale;
        f5 += scale;
        f6 += scale;
        int n = m = this.field_78809_i ? -1 : 1;
        if (this.field_78809_i) {
            float f7 = f4;
            f4 = x;
            x = f7;
        }
        float[] v = new float[]{x, y, z};
        float[] v1 = new float[]{f4, y, z};
        float[] v2 = new float[]{f4, f5, z};
        float[] v3 = new float[]{x, f5, z};
        float[] v4 = new float[]{x, y, f6};
        float[] v5 = new float[]{f4, y, f6};
        float[] v6 = new float[]{f4, f5, f6};
        float[] v7 = new float[]{x, f5, f6};
        switch (dir) {
            case 3: {
                v[1] = v[1] - bScale1;
                v[2] = v[2] - bScale3;
                v3[1] = v3[1] + bScale2;
                v3[2] = v3[2] - bScale3;
                v4[1] = v4[1] - bScale1;
                v4[2] = v4[2] + bScale4;
                v7[1] = v7[1] + bScale2;
                v7[2] = v7[2] + bScale4;
                break;
            }
            case 2: {
                v1[1] = v1[1] - bScale1;
                v1[2] = v1[2] - bScale3;
                v2[1] = v2[1] + bScale2;
                v2[2] = v2[2] - bScale3;
                v5[1] = v5[1] - bScale1;
                v5[2] = v5[2] + bScale4;
                v6[1] = v6[1] + bScale2;
                v6[2] = v6[2] + bScale4;
                break;
            }
            case 0: {
                v[0] = v[0] - (float)m * bScale4;
                v[1] = v[1] - bScale1;
                v1[0] = v1[0] + (float)m * bScale3;
                v1[1] = v1[1] - bScale1;
                v2[0] = v2[0] + (float)m * bScale3;
                v2[1] = v2[1] + bScale2;
                v3[0] = v3[0] - (float)m * bScale4;
                v3[1] = v3[1] + bScale2;
                break;
            }
            case 1: {
                v4[0] = v4[0] - (float)m * bScale4;
                v4[1] = v4[1] - bScale1;
                v5[0] = v5[0] + (float)m * bScale3;
                v5[1] = v5[1] - bScale1;
                v6[0] = v6[0] + (float)m * bScale3;
                v6[1] = v6[1] + bScale2;
                v7[0] = v7[0] - (float)m * bScale4;
                v7[1] = v7[1] + bScale2;
                break;
            }
            case 4: {
                v[0] = v[0] - (float)m * bScale1;
                v[2] = v[2] - bScale3;
                v1[0] = v1[0] + (float)m * bScale2;
                v1[2] = v1[2] - bScale3;
                v4[0] = v4[0] - (float)m * bScale1;
                v4[2] = v4[2] + bScale4;
                v5[0] = v5[0] + (float)m * bScale2;
                v5[2] = v5[2] + bScale4;
                break;
            }
            case 5: {
                v2[0] = v2[0] + (float)m * bScale2;
                v2[2] = v2[2] - bScale3;
                v3[0] = v3[0] - (float)m * bScale1;
                v3[2] = v3[2] - bScale3;
                v6[0] = v6[0] + (float)m * bScale2;
                v6[2] = v6[2] + bScale4;
                v7[0] = v7[0] - (float)m * bScale1;
                v7[2] = v7[2] + bScale4;
            }
        }
        float[] qValues = new float[]{Math.abs((v[0] - v1[0]) / (v3[0] - v2[0])), Math.abs((v[0] - v1[0]) / (v4[0] - v5[0])), Math.abs((v4[0] - v5[0]) / (v7[0] - v6[0])), Math.abs((v3[0] - v2[0]) / (v7[0] - v6[0])), Math.abs((v[1] - v3[1]) / (v1[1] - v2[1])), Math.abs((v4[1] - v7[1]) / (v5[1] - v6[1])), Math.abs((v[1] - v3[1]) / (v4[1] - v7[1])), Math.abs((v1[1] - v2[1]) / (v5[1] - v6[1])), Math.abs((v[2] - v4[2]) / (v1[2] - v5[2])), Math.abs((v[2] - v4[2]) / (v3[2] - v7[2])), Math.abs((v1[2] - v5[2]) / (v2[2] - v6[2])), Math.abs((v3[2] - v7[2]) / (v2[2] - v6[2]))};
        this.addRectShape(v, v1, v2, v3, v4, v5, v6, v7, w, h, d);
    }

    public void addFlexTrapezoid(float x, float y, float z, int w, int h, int d, float scale, float bScale1, float bScale2, float bScale3, float bScale4, float fScale1, float fScale2, int dir) {
        int m;
        float f4 = x + (float)w;
        float f5 = y + (float)h;
        float f6 = z + (float)d;
        x -= scale;
        y -= scale;
        z -= scale;
        f4 += scale;
        f5 += scale;
        f6 += scale;
        int n = m = this.field_78809_i ? -1 : 1;
        if (this.field_78809_i) {
            float f7 = f4;
            f4 = x;
            x = f7;
        }
        float[] v = new float[]{x, y, z};
        float[] v1 = new float[]{f4, y, z};
        float[] v2 = new float[]{f4, f5, z};
        float[] v3 = new float[]{x, f5, z};
        float[] v4 = new float[]{x, y, f6};
        float[] v5 = new float[]{f4, y, f6};
        float[] v6 = new float[]{f4, f5, f6};
        float[] v7 = new float[]{x, f5, f6};
        switch (dir) {
            case 3: {
                v[2] = v[2] - fScale1;
                v1[2] = v1[2] - fScale1;
                v4[2] = v4[2] + fScale2;
                v5[2] = v5[2] + fScale2;
                v[1] = v[1] - bScale1;
                v[2] = v[2] - bScale3;
                v3[1] = v3[1] + bScale2;
                v3[2] = v3[2] - bScale3;
                v4[1] = v4[1] - bScale1;
                v4[2] = v4[2] + bScale4;
                v7[1] = v7[1] + bScale2;
                v7[2] = v7[2] + bScale4;
                break;
            }
            case 2: {
                v[2] = v[2] - fScale1;
                v1[2] = v1[2] - fScale1;
                v4[2] = v4[2] + fScale2;
                v5[2] = v5[2] + fScale2;
                v1[1] = v1[1] - bScale1;
                v1[2] = v1[2] - bScale3;
                v2[1] = v2[1] + bScale2;
                v2[2] = v2[2] - bScale3;
                v5[1] = v5[1] - bScale1;
                v5[2] = v5[2] + bScale4;
                v6[1] = v6[1] + bScale2;
                v6[2] = v6[2] + bScale4;
                break;
            }
            case 0: {
                v1[1] = v1[1] - fScale1;
                v5[1] = v5[1] - fScale1;
                v2[1] = v2[1] + fScale2;
                v6[1] = v6[1] + fScale2;
                v[0] = v[0] - (float)m * bScale4;
                v[1] = v[1] - bScale1;
                v1[0] = v1[0] + (float)m * bScale3;
                v1[1] = v1[1] - bScale1;
                v2[0] = v2[0] + (float)m * bScale3;
                v2[1] = v2[1] + bScale2;
                v3[0] = v3[0] - (float)m * bScale4;
                v3[1] = v3[1] + bScale2;
                break;
            }
            case 1: {
                v1[1] = v1[1] - fScale1;
                v5[1] = v5[1] - fScale1;
                v2[1] = v2[1] + fScale2;
                v6[1] = v6[1] + fScale2;
                v4[0] = v4[0] - (float)m * bScale4;
                v4[1] = v4[1] - bScale1;
                v5[0] = v5[0] + (float)m * bScale3;
                v5[1] = v5[1] - bScale1;
                v6[0] = v6[0] + (float)m * bScale3;
                v6[1] = v6[1] + bScale2;
                v7[0] = v7[0] - (float)m * bScale4;
                v7[1] = v7[1] + bScale2;
                break;
            }
            case 4: {
                v1[2] = v1[2] - fScale1;
                v2[2] = v2[2] - fScale1;
                v5[2] = v5[2] + fScale2;
                v6[2] = v6[2] + fScale2;
                v[0] = v[0] - (float)m * bScale1;
                v[2] = v[2] - bScale3;
                v1[0] = v1[0] + (float)m * bScale2;
                v1[2] = v1[2] - bScale3;
                v4[0] = v4[0] - (float)m * bScale1;
                v4[2] = v4[2] + bScale4;
                v5[0] = v5[0] + (float)m * bScale2;
                v5[2] = v5[2] + bScale4;
                break;
            }
            case 5: {
                v1[2] = v1[2] - fScale1;
                v2[2] = v2[2] - fScale1;
                v5[2] = v5[2] + fScale2;
                v6[2] = v6[2] + fScale2;
                v2[0] = v2[0] + (float)m * bScale2;
                v2[2] = v2[2] - bScale3;
                v3[0] = v3[0] - (float)m * bScale1;
                v3[2] = v3[2] - bScale3;
                v6[0] = v6[0] + (float)m * bScale2;
                v6[2] = v6[2] + bScale4;
                v7[0] = v7[0] - (float)m * bScale1;
                v7[2] = v7[2] + bScale4;
            }
        }
        float[] qValues = new float[]{Math.abs((v[0] - v1[0]) / (v3[0] - v2[0])), Math.abs((v[0] - v1[0]) / (v4[0] - v5[0])), Math.abs((v4[0] - v5[0]) / (v7[0] - v6[0])), Math.abs((v3[0] - v2[0]) / (v7[0] - v6[0])), Math.abs((v[1] - v3[1]) / (v1[1] - v2[1])), Math.abs((v4[1] - v7[1]) / (v5[1] - v6[1])), Math.abs((v[1] - v3[1]) / (v4[1] - v7[1])), Math.abs((v1[1] - v2[1]) / (v5[1] - v6[1])), Math.abs((v[2] - v4[2]) / (v1[2] - v5[2])), Math.abs((v[2] - v4[2]) / (v3[2] - v7[2])), Math.abs((v1[2] - v5[2]) / (v2[2] - v6[2])), Math.abs((v3[2] - v7[2]) / (v2[2] - v6[2]))};
        this.addRectShape(v, v1, v2, v3, v4, v5, v6, v7, w, h, d);
    }

    public void addBox(float x, float y, float z, float w, float h, float d) {
        int rw = MathHelper.func_76123_f((float)w);
        int rh = MathHelper.func_76123_f((float)h);
        int rd = MathHelper.func_76123_f((float)d);
        this.addShapeBox(x, y, z, rw, rh, rd, 0.0f, 0.0f, 0.0f, 0.0f, w -= (float)rw, 0.0f, 0.0f, w, 0.0f, d -= (float)rd, 0.0f, 0.0f, d, 0.0f, h -= (float)rh, 0.0f, w, h, 0.0f, w, h, d, 0.0f, h, d);
    }

    public void addShapeBox(float x, float y, float z, int w, int h, int d, float scale, float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float x5, float y5, float z5, float x6, float y6, float z6, float x7, float y7, float z7) {
        int m;
        float f4 = x + (float)w;
        float f5 = y + (float)h;
        float f6 = z + (float)d;
        x -= scale;
        y -= scale;
        z -= scale;
        f4 += scale;
        f5 += scale;
        f6 += scale;
        int n = m = this.field_78809_i ? -1 : 1;
        if (this.field_78809_i) {
            float f7 = f4;
            f4 = x;
            x = f7;
        }
        float[] v = new float[]{x - x0, y - y0, z - z0};
        float[] v1 = new float[]{f4 + x1, y - y1, z - z1};
        float[] v2 = new float[]{f4 + x5, f5 + y5, z - z5};
        float[] v3 = new float[]{x - x4, f5 + y4, z - z4};
        float[] v4 = new float[]{x - x3, y - y3, f6 + z3};
        float[] v5 = new float[]{f4 + x2, y - y2, f6 + z2};
        float[] v6 = new float[]{f4 + x6, f5 + y6, f6 + z6};
        float[] v7 = new float[]{x - x7, f5 + y7, f6 + z7};
        float[] qValues = new float[]{Math.abs((v[0] - v1[0]) / (v3[0] - v2[0])), Math.abs((v[0] - v1[0]) / (v4[0] - v5[0])), Math.abs((v4[0] - v5[0]) / (v7[0] - v6[0])), Math.abs((v3[0] - v2[0]) / (v7[0] - v6[0])), Math.abs((v[1] - v3[1]) / (v1[1] - v2[1])), Math.abs((v4[1] - v7[1]) / (v5[1] - v6[1])), Math.abs((v[1] - v3[1]) / (v4[1] - v7[1])), Math.abs((v1[1] - v2[1]) / (v5[1] - v6[1])), Math.abs((v[2] - v4[2]) / (v1[2] - v5[2])), Math.abs((v[2] - v4[2]) / (v3[2] - v7[2])), Math.abs((v1[2] - v5[2]) / (v2[2] - v6[2])), Math.abs((v3[2] - v7[2]) / (v2[2] - v6[2]))};
        this.addRectShape(v, v1, v2, v3, v4, v5, v6, v7, w, h, d);
    }

    public void addShape3D(float x, float y, float z, Coord2D[] coordinates, float depth, int shapeTextureWidth, int shapeTextureHeight, int sideTextureWidth, int sideTextureHeight, int direction) {
        this.addShape3D(x, y, z, coordinates, depth, shapeTextureWidth, shapeTextureHeight, sideTextureWidth, sideTextureHeight, direction, null);
    }

    public void addShape3D(float x, float y, float z, Coord2D[] coordinates, float depth, int shapeTextureWidth, int shapeTextureHeight, int sideTextureWidth, int sideTextureHeight, int direction, float[] faceLengths) {
        this.addShape3D(x, y, z, new Shape2D(coordinates), depth, shapeTextureWidth, shapeTextureHeight, sideTextureWidth, sideTextureHeight, direction, faceLengths);
    }

    public void addShape3D(float x, float y, float z, ArrayList<Coord2D> coordinates, float depth, int shapeTextureWidth, int shapeTextureHeight, int sideTextureWidth, int sideTextureHeight, int direction) {
        this.addShape3D(x, y, z, coordinates, depth, shapeTextureWidth, shapeTextureHeight, sideTextureWidth, sideTextureHeight, direction, null);
    }

    public void addShape3D(float x, float y, float z, ArrayList<Coord2D> coordinates, float depth, int shapeTextureWidth, int shapeTextureHeight, int sideTextureWidth, int sideTextureHeight, int direction, float[] faceLengths) {
        this.addShape3D(x, y, z, new Shape2D(coordinates), depth, shapeTextureWidth, shapeTextureHeight, sideTextureWidth, sideTextureHeight, direction, faceLengths);
    }

    public void addShape3D(float x, float y, float z, Shape2D shape, float depth, int shapeTextureWidth, int shapeTextureHeight, int sideTextureWidth, int sideTextureHeight, int direction) {
        this.addShape3D(x, y, z, shape, depth, shapeTextureWidth, shapeTextureHeight, sideTextureWidth, sideTextureHeight, direction, null);
    }

    public void addShape3D(float x, float y, float z, Shape2D shape, float depth, int shapeTextureWidth, int shapeTextureHeight, int sideTextureWidth, int sideTextureHeight, int direction, float[] faceLengths) {
        float rotX = 0.0f;
        float rotY = 0.0f;
        float rotZ = 0.0f;
        switch (direction) {
            case 2: {
                rotY = 1.5707964f;
                break;
            }
            case 3: {
                rotY = -1.5707964f;
                break;
            }
            case 4: {
                rotX = 1.5707964f;
                break;
            }
            case 5: {
                rotX = -1.5707964f;
                break;
            }
            case 0: {
                rotY = (float)Math.PI;
                break;
            }
        }
        this.addShape3D(x, y, z, shape, depth, shapeTextureWidth, shapeTextureHeight, sideTextureWidth, sideTextureHeight, rotX, rotY, rotZ, faceLengths);
    }

    public void addShape3D(float x, float y, float z, Shape2D shape, float depth, int shapeTextureWidth, int shapeTextureHeight, int sideTextureWidth, int sideTextureHeight, float rotX, float rotY, float rotZ) {
        this.addShape3D(x, y, z, shape, depth, shapeTextureWidth, shapeTextureHeight, sideTextureWidth, sideTextureHeight, rotX, rotY, rotZ, null);
    }

    public void addShape3D(float x, float y, float z, Shape2D shape, float depth, int shapeTextureWidth, int shapeTextureHeight, int sideTextureWidth, int sideTextureHeight, float rotX, float rotY, float rotZ, float[] faceLengths) {
        Shape3D shape3D = shape.extrude(x, y, z, rotX, rotY, rotZ, depth, this.textureOffsetX, this.textureOffsetY, this.field_78801_a, this.field_78799_b, shapeTextureWidth, shapeTextureHeight, sideTextureWidth, sideTextureHeight, faceLengths);
        if (this.flip) {
            for (int idx = 0; idx < shape3D.faces.length; ++idx) {
                shape3D.faces[idx].flipFace();
            }
        }
        this.copyTo((PositionTextureVertex[])shape3D.vertices, shape3D.faces);
    }

    public void addPixel(float x, float y, float z, float width, float height, float length) {
        this.addPixel(x, y, z, new float[]{width, height, length}, this.textureOffsetX, this.textureOffsetY);
    }

    public void addPixel(float x, float y, float z, float[] scale, int w, int h) {
        PositionTextureVertex[] verts = new PositionTextureVertex[8];
        TexturedPolygon[] poly = new TexturedPolygon[6];
        float x1 = x + scale[0];
        float y1 = y + scale[1];
        float z1 = z + scale[2];
        float[] f = new float[]{x, y, z};
        float[] f1 = new float[]{x1, y, z};
        float[] f2 = new float[]{x1, y1, z};
        float[] f3 = new float[]{x, y1, z};
        float[] f4 = new float[]{x, y, z1};
        float[] f5 = new float[]{x1, y, z1};
        float[] f6 = new float[]{x1, y1, z1};
        float[] f7 = new float[]{x, y1, z1};
        PositionTextureVertex positionTexturevertex = new PositionTextureVertex(f[0], f[1], f[2], 0.0f, 0.0f);
        PositionTextureVertex positionTexturevertex1 = new PositionTextureVertex(f1[0], f1[1], f1[2], 0.0f, 8.0f);
        PositionTextureVertex positionTexturevertex2 = new PositionTextureVertex(f2[0], f2[1], f2[2], 8.0f, 8.0f);
        PositionTextureVertex positionTexturevertex3 = new PositionTextureVertex(f3[0], f3[1], f3[2], 8.0f, 0.0f);
        PositionTextureVertex positionTexturevertex4 = new PositionTextureVertex(f4[0], f4[1], f4[2], 0.0f, 0.0f);
        PositionTextureVertex positionTexturevertex5 = new PositionTextureVertex(f5[0], f5[1], f5[2], 0.0f, 8.0f);
        PositionTextureVertex positionTexturevertex6 = new PositionTextureVertex(f6[0], f6[1], f6[2], 8.0f, 8.0f);
        PositionTextureVertex positionTexturevertex7 = new PositionTextureVertex(f7[0], f7[1], f7[2], 8.0f, 0.0f);
        verts[0] = positionTexturevertex;
        verts[1] = positionTexturevertex1;
        verts[2] = positionTexturevertex2;
        verts[3] = positionTexturevertex3;
        verts[4] = positionTexturevertex4;
        verts[5] = positionTexturevertex5;
        verts[6] = positionTexturevertex6;
        verts[7] = positionTexturevertex7;
        poly[0] = this.addPolygonReturn(new PositionTextureVertex[]{positionTexturevertex5, positionTexturevertex1, positionTexturevertex2, positionTexturevertex6}, w, h, w + 1, h + 1);
        poly[1] = this.addPolygonReturn(new PositionTextureVertex[]{positionTexturevertex, positionTexturevertex4, positionTexturevertex7, positionTexturevertex3}, w, h, w + 1, h + 1);
        poly[2] = this.addPolygonReturn(new PositionTextureVertex[]{positionTexturevertex5, positionTexturevertex4, positionTexturevertex, positionTexturevertex1}, w, h, w + 1, h + 1);
        poly[3] = this.addPolygonReturn(new PositionTextureVertex[]{positionTexturevertex2, positionTexturevertex3, positionTexturevertex7, positionTexturevertex6}, w, h, w + 1, h + 1);
        poly[4] = this.addPolygonReturn(new PositionTextureVertex[]{positionTexturevertex1, positionTexturevertex, positionTexturevertex3, positionTexturevertex2}, w, h, w + 1, h + 1);
        poly[5] = this.addPolygonReturn(new PositionTextureVertex[]{positionTexturevertex4, positionTexturevertex5, positionTexturevertex6, positionTexturevertex7}, w, h, w + 1, h + 1);
        this.copyTo(verts, poly);
    }

    public void addSprite(float x, float y, float z, int w, int h, float expansion) {
        this.addSprite(x, y, z, w, h, 1, false, false, false, false, false, expansion);
    }

    public void addSprite(float x, float y, float z, int w, int h, boolean rotX, boolean rotY, boolean rotZ, boolean mirrorX, boolean mirrorY, float expansion) {
        this.addSprite(x, y, z, w, h, 1, rotX, rotY, rotZ, mirrorX, mirrorY, expansion);
    }

    public void addSprite(float x, float y, float z, int w, int h, int d, boolean rotX, boolean rotY, boolean rotZ, boolean mirrorX, boolean mirrorY, float expansion) {
        this.addSprite(x, y, z, w, h, d, 1.0f, rotX, rotY, rotZ, mirrorX, mirrorY, expansion);
    }

    public void addSprite(float x, float y, float z, int w, int h, int d, float pixelScale, boolean rotX, boolean rotY, boolean rotZ, boolean mirrorX, boolean mirrorY, float expansion) {
        Object[] mask = new String[h];
        char[] str = new char[w];
        Arrays.fill(str, '1');
        Arrays.fill(mask, new String(str));
        this.addSprite(x, y, z, (String[])mask, d, pixelScale, rotX, rotY, rotZ, mirrorX, mirrorY, expansion);
    }

    public void addSprite(float x, float y, float z, String[] mask, int d, float pixelScale, boolean rotX, boolean rotY, boolean rotZ, boolean mirrorX, boolean mirrorY, float expansion) {
        int w = mask[0].length();
        int h = mask.length;
        float x1 = x - expansion;
        float y1 = y - expansion;
        float z1 = z - expansion;
        int wDir = 0;
        int hDir = 0;
        int dDir = 0;
        float wScale = 1.0f + expansion / ((float)w * pixelScale);
        float hScale = 1.0f + expansion / ((float)h * pixelScale);
        if (!rotX) {
            if (!rotY) {
                if (!rotZ) {
                    wDir = 0;
                    hDir = 1;
                    dDir = 2;
                } else {
                    wDir = 1;
                    hDir = 0;
                    dDir = 2;
                }
            } else if (!rotZ) {
                wDir = 2;
                hDir = 1;
                dDir = 0;
            } else {
                wDir = 2;
                hDir = 0;
                dDir = 1;
            }
        } else if (!rotY) {
            if (!rotZ) {
                wDir = 0;
                hDir = 2;
                dDir = 1;
            } else {
                wDir = 1;
                hDir = 2;
                dDir = 0;
            }
        } else if (!rotZ) {
            wDir = 2;
            hDir = 0;
            dDir = 1;
        } else {
            wDir = 2;
            hDir = 1;
            dDir = 0;
        }
        int texStartX = this.textureOffsetX + (mirrorX ? w - 1 : 0);
        int texStartY = this.textureOffsetY + (mirrorY ? h - 1 : 0);
        int texDirX = mirrorX ? -1 : 1;
        int texDirY = mirrorY ? -1 : 1;
        float wVoxSize = this.getPixelSize(wScale, hScale, (float)d * pixelScale + expansion * 2.0f, 0, 1, wDir, 1, 1);
        float hVoxSize = this.getPixelSize(wScale, hScale, (float)d * pixelScale + expansion * 2.0f, 0, 1, hDir, 1, 1);
        float dVoxSize = this.getPixelSize(wScale, hScale, (float)d * pixelScale + expansion * 2.0f, 0, 1, dDir, 1, 1);
        for (int i = 0; i < w; ++i) {
            for (int j = 0; j < h; ++j) {
                if (mask[j].charAt(i) != '1') continue;
                this.addPixel(x1 + this.getPixelSize(wScale, hScale, 0.0f, wDir, hDir, 0, i, j), y1 + this.getPixelSize(wScale, hScale, 0.0f, wDir, hDir, 1, i, j), z1 + this.getPixelSize(wScale, hScale, 0.0f, wDir, hDir, 2, i, j), new float[]{wVoxSize, hVoxSize, dVoxSize}, texStartX + texDirX * i, texStartY + texDirY * j);
            }
        }
    }

    private float getPixelSize(float wScale, float hScale, float dScale, int wDir, int hDir, int checkDir, int texPosX, int texPosY) {
        return wDir == checkDir ? wScale * (float)texPosX : (hDir == checkDir ? hScale * (float)texPosY : dScale);
    }

    public void addSphere(float x, float y, float z, float r, int segs, int rings, int textureW, int textureH) {
        PositionTextureVertex[] verts;
        if (segs < 3) {
            segs = 3;
        }
        PositionTextureVertex[] tempVerts = new PositionTextureVertex[segs * (++rings - 1) + 2];
        TexturedPolygon[] poly = new TexturedPolygon[segs * rings];
        tempVerts[0] = new PositionTextureVertex(x, y - r, z, 0.0f, 0.0f);
        tempVerts[tempVerts.length - 1] = new PositionTextureVertex(x, y + r, z, 0.0f, 0.0f);
        float uOffs = 1.0f / (this.field_78801_a * 10.0f);
        float vOffs = 1.0f / (this.field_78799_b * 10.0f);
        float texW = (float)textureW / this.field_78801_a - 2.0f * uOffs;
        float texH = (float)textureH / this.field_78799_b - 2.0f * vOffs;
        float segW = texW / (float)segs;
        float segH = texH / (float)rings;
        float startU = (float)this.textureOffsetX / this.field_78801_a;
        float startV = (float)this.textureOffsetY / this.field_78799_b;
        int currentFace = 0;
        for (int j = 1; j < rings; ++j) {
            for (int i = 0; i < segs; ++i) {
                float yWidth = MathHelper.func_76134_b((float)(-1.5707964f + (float)Math.PI / (float)rings * (float)j));
                float yHeight = MathHelper.func_76126_a((float)(-1.5707964f + (float)Math.PI / (float)rings * (float)j));
                float xSize = MathHelper.func_76126_a((float)((float)Math.PI / (float)segs * (float)i * 2.0f + (float)Math.PI)) * yWidth;
                float zSize = -MathHelper.func_76134_b((float)((float)Math.PI / (float)segs * (float)i * 2.0f + (float)Math.PI)) * yWidth;
                int curVert = 1 + i + segs * (j - 1);
                tempVerts[curVert] = new PositionTextureVertex(x + xSize * r, y + yHeight * r, z + zSize * r, 0.0f, 0.0f);
                if (i <= 0) continue;
                PositionTextureVertex[] verts2 = j == 1 ? new PositionTextureVertex[]{tempVerts[curVert].setTexturePosition(startU + segW * (float)i, startV + segH * (float)j), tempVerts[curVert - 1].setTexturePosition(startU + segW * (float)(i - 1), startV + segH * (float)j), tempVerts[0].setTexturePosition(startU + segW * (float)(i - 1), startV), tempVerts[0].setTexturePosition(startU + segW + segW * (float)i, startV)} : new PositionTextureVertex[]{tempVerts[curVert].setTexturePosition(startU + segW * (float)i, startV + segH * (float)j), tempVerts[curVert - 1].setTexturePosition(startU + segW * (float)(i - 1), startV + segH * (float)j), tempVerts[curVert - 1 - segs].setTexturePosition(startU + segW * (float)(i - 1), startV + segH * (float)(j - 1)), tempVerts[curVert - segs].setTexturePosition(startU + segW * (float)i, startV + segH * (float)(j - 1))};
                poly[currentFace] = new TexturedPolygon(verts2);
                ++currentFace;
            }
            verts = j == 1 ? new PositionTextureVertex[]{tempVerts[1].setTexturePosition(startU + segW * (float)segs, startV + segH * (float)j), tempVerts[segs].setTexturePosition(startU + segW * (float)(segs - 1), startV + segH * (float)j), tempVerts[0].setTexturePosition(startU + segW * (float)(segs - 1), startV), tempVerts[0].setTexturePosition(startU + segW * (float)segs, startV)} : new PositionTextureVertex[]{tempVerts[1 + segs * (j - 1)].setTexturePosition(startU + texW, startV + segH * (float)j), tempVerts[segs * (j - 1) + segs].setTexturePosition(startU + texW - segW, startV + segH * (float)j), tempVerts[segs * (j - 1)].setTexturePosition(startU + texW - segW, startV + segH * (float)(j - 1)), tempVerts[1 + segs * (j - 1) - segs].setTexturePosition(startU + texW, startV + segH * (float)(j - 1))};
            poly[currentFace] = new TexturedPolygon(verts);
            ++currentFace;
        }
        for (int i = 0; i < segs; ++i) {
            verts = new PositionTextureVertex[3];
            int curVert = tempVerts.length - (segs + 1);
            verts[0] = tempVerts[tempVerts.length - 1].setTexturePosition(startU + segW * ((float)i + 0.5f), startV + texH);
            verts[1] = tempVerts[curVert + i].setTexturePosition(startU + segW * (float)i, startV + texH - segH);
            verts[2] = tempVerts[curVert + (i + 1) % segs].setTexturePosition(startU + segW * (float)(i + 1), startV + texH - segH);
            poly[currentFace] = new TexturedPolygon(verts);
            ++currentFace;
        }
        this.copyTo(tempVerts, poly);
    }

    public void addCone(float x, float y, float z, float radius, float length, int segments) {
        this.addCone(x, y, z, radius, length, segments, 1.0f);
    }

    public void addCone(float x, float y, float z, float radius, float length, int segments, float baseScale) {
        this.addCone(x, y, z, radius, length, segments, baseScale, 4);
    }

    public void addCone(float x, float y, float z, float radius, float length, int segments, float baseScale, int baseDirection) {
        this.addCone(x, y, z, radius, length, segments, baseScale, baseDirection, (int)Math.floor(radius * 2.0f), (int)Math.floor(radius * 2.0f));
    }

    public void addCone(float x, float y, float z, float radius, float length, int segments, float baseScale, int baseDirection, int textureCircleDiameterW, int textureCircleDiameterH) {
        this.addCylinder(x, y, z, radius, length, segments, baseScale, 0.0f, baseDirection, textureCircleDiameterW, textureCircleDiameterH, 1);
    }

    public void addCylinder(float x, float y, float z, float radius, float length, int segments) {
        this.addCylinder(x, y, z, radius, length, segments, 1.0f, 1.0f);
    }

    public void addCylinder(float x, float y, float z, float radius, float length, int segments, float baseScale, float topScale) {
        this.addCylinder(x, y, z, radius, length, segments, baseScale, topScale, 4);
    }

    public void addCylinder(float x, float y, float z, float radius, float length, int segments, float baseScale, float topScale, int baseDirection) {
        this.addCylinder(x, y, z, radius, length, segments, baseScale, topScale, baseDirection, (int)Math.floor(radius * 2.0f), (int)Math.floor(radius * 2.0f), (int)Math.floor(length));
    }

    public void addCylinder(float x, float y, float z, float radius, float length, int segments, float baseScale, float topScale, int baseDirection, int textureCircleDiameterW, int textureCircleDiameterH, int textureH) {
        boolean coneTop;
        boolean dirTop = baseDirection == 4 || baseDirection == 5;
        boolean dirSide = baseDirection == 3 || baseDirection == 2;
        boolean dirFront = baseDirection == 0 || baseDirection == 1;
        boolean dirMirror = baseDirection == 2 || baseDirection == 5 || baseDirection == 1;
        boolean coneBase = baseScale == 0.0f;
        boolean bl = coneTop = topScale == 0.0f;
        if (coneBase && coneTop) {
            baseScale = 1.0f;
            coneBase = false;
        }
        PositionTextureVertex[] tempVerts = new PositionTextureVertex[segments * (coneBase || coneTop ? 1 : 2) + 2];
        TexturedPolygon[] poly = new TexturedPolygon[segments * (coneBase || coneTop ? 2 : 3)];
        float xLength = dirSide ? length : 0.0f;
        float yLength = dirTop ? length : 0.0f;
        float zLength = dirFront ? length : 0.0f;
        float xStart = dirMirror ? x + xLength : x;
        float yStart = dirMirror ? y + yLength : y;
        float zStart = dirMirror ? z + zLength : z;
        float xEnd = !dirMirror ? x + xLength : x;
        float yEnd = !dirMirror ? y + yLength : y;
        float zEnd = !dirMirror ? z + zLength : z;
        tempVerts[0] = new PositionTextureVertex(xStart, yStart, zStart, 0.0f, 0.0f);
        tempVerts[tempVerts.length - 1] = new PositionTextureVertex(xEnd, yEnd, zEnd, 0.0f, 0.0f);
        float xCur = xStart;
        float yCur = yStart;
        float zCur = zStart;
        float sCur = coneBase ? topScale : baseScale;
        for (int repeat = 0; repeat < (coneBase || coneTop ? 1 : 2); ++repeat) {
            for (int index = 0; index < segments; ++index) {
                float xSize = (float)(this.field_78809_i ^ dirMirror ? -1 : 1) * MathHelper.func_76126_a((float)((float)Math.PI / (float)segments * (float)index * 2.0f + (float)Math.PI)) * radius * sCur;
                float zSize = -MathHelper.func_76134_b((float)((float)Math.PI / (float)segments * (float)index * 2.0f + (float)Math.PI)) * radius * sCur;
                float xPlace = xCur + (!dirSide ? xSize : 0.0f);
                float yPlace = yCur + (!dirTop ? zSize : 0.0f);
                float zPlace = zCur + (dirSide ? xSize : (dirTop ? zSize : 0.0f));
                tempVerts[1 + index + repeat * segments] = new PositionTextureVertex(xPlace, yPlace, zPlace, 0.0f, 0.0f);
            }
            xCur = xEnd;
            yCur = yEnd;
            zCur = zEnd;
            sCur = topScale;
        }
        float uScale = 1.0f / this.field_78801_a;
        float vScale = 1.0f / this.field_78799_b;
        float uOffset = uScale / 20.0f;
        float vOffset = vScale / 20.0f;
        float uCircle = (float)textureCircleDiameterW * uScale;
        float vCircle = (float)textureCircleDiameterH * vScale;
        float uWidth = (uCircle * 2.0f - uOffset * 2.0f) / (float)segments;
        float vHeight = (float)textureH * vScale - uOffset * 2.0f;
        float uStart = (float)this.textureOffsetX * uScale;
        float vStart = (float)this.textureOffsetY * vScale;
        for (int index = 0; index < segments; ++index) {
            int index2 = (index + 1) % segments;
            float uSize = MathHelper.func_76126_a((float)((float)Math.PI / (float)segments * (float)index * 2.0f + (!dirTop ? 0.0f : (float)Math.PI))) * (0.5f * uCircle - 2.0f * uOffset);
            float vSize = MathHelper.func_76134_b((float)((float)Math.PI / (float)segments * (float)index * 2.0f + (!dirTop ? 0.0f : (float)Math.PI))) * (0.5f * vCircle - 2.0f * vOffset);
            float uSize1 = MathHelper.func_76126_a((float)((float)Math.PI / (float)segments * (float)index2 * 2.0f + (!dirTop ? 0.0f : (float)Math.PI))) * (0.5f * uCircle - 2.0f * uOffset);
            float vSize1 = MathHelper.func_76134_b((float)((float)Math.PI / (float)segments * (float)index2 * 2.0f + (!dirTop ? 0.0f : (float)Math.PI))) * (0.5f * vCircle - 2.0f * vOffset);
            PositionTextureVertex[] vert = new PositionTextureVertex[]{tempVerts[0].setTexturePosition(uStart + 0.5f * uCircle, vStart + 0.5f * vCircle), tempVerts[1 + index2].setTexturePosition(uStart + 0.5f * uCircle + uSize1, vStart + 0.5f * vCircle + vSize1), tempVerts[1 + index].setTexturePosition(uStart + 0.5f * uCircle + uSize, vStart + 0.5f * vCircle + vSize)};
            poly[index] = new TexturedPolygon(vert);
            if (this.field_78809_i ^ this.flip) {
                poly[index].flipFace();
            }
            if (!coneBase && !coneTop) {
                vert = new PositionTextureVertex[]{tempVerts[1 + index].setTexturePosition(uStart + uOffset + uWidth * (float)index, vStart + vOffset + vCircle), tempVerts[1 + index2].setTexturePosition(uStart + uOffset + uWidth * (float)(index + 1), vStart + vOffset + vCircle), tempVerts[1 + segments + index2].setTexturePosition(uStart + uOffset + uWidth * (float)(index + 1), vStart + vOffset + vCircle + vHeight), tempVerts[1 + segments + index].setTexturePosition(uStart + uOffset + uWidth * (float)index, vStart + vOffset + vCircle + vHeight)};
                poly[index + segments] = new TexturedPolygon(vert);
                if (this.field_78809_i ^ this.flip) {
                    poly[index + segments].flipFace();
                }
            }
            vert = new PositionTextureVertex[]{tempVerts[tempVerts.length - 1].setTexturePosition(uStart + 1.5f * uCircle, vStart + 0.5f * vCircle), tempVerts[tempVerts.length - 2 - index].setTexturePosition(uStart + 1.5f * uCircle + uSize1, vStart + 0.5f * vCircle + vSize1), tempVerts[tempVerts.length - (1 + segments) + (segments - index) % segments].setTexturePosition(uStart + 1.5f * uCircle + uSize, vStart + 0.5f * vCircle + vSize)};
            poly[poly.length - segments + index] = new TexturedPolygon(vert);
            if (!(this.field_78809_i ^ this.flip)) continue;
            poly[poly.length - segments + index].flipFace();
        }
        this.copyTo(tempVerts, poly);
    }

    public void addObj(String file) {
        this.addModel(file, ModelPool.OBJ);
    }

    public void addModel(String file, Class modelFormat) {
        ModelPoolEntry entry = ModelPool.addFile(file, modelFormat, this.transformGroup, this.textureGroup);
        if (entry == null) {
            return;
        }
        PositionTextureVertex[] verts = Arrays.copyOf(entry.vertices, entry.vertices.length);
        TexturedPolygon[] poly = Arrays.copyOf(entry.faces, entry.faces.length);
        if (this.flip) {
            for (TexturedPolygon face : this.faces) {
                face.flipFace();
            }
        }
        this.copyTo(verts, poly, false);
    }

    public ModelRendererTurbo setTextureOffset(int x, int y) {
        this.textureOffsetX = x;
        this.textureOffsetY = y;
        return this;
    }

    public void setPosition(float x, float y, float z) {
        this.field_78800_c = x;
        this.field_78797_d = y;
        this.field_78798_e = z;
    }

    public void doMirror(boolean x, boolean y, boolean z) {
        for (TexturedPolygon face : this.faces) {
            PositionTextureVertex[] verts = face.vertexPositions;
            for (int j = 0; j < verts.length; ++j) {
                verts[j].field_78243_a.field_72450_a = verts[j].field_78243_a.field_72450_a * (double)(x ? -1 : 1);
                verts[j].field_78243_a.field_72448_b = verts[j].field_78243_a.field_72448_b * (double)(y ? -1 : 1);
                verts[j].field_78243_a.field_72449_c = verts[j].field_78243_a.field_72449_c * (double)(z ? -1 : 1);
            }
            if (!(x ^ y ^ z)) continue;
            face.flipFace();
        }
    }

    public void setMirrored(boolean isMirrored) {
        this.field_78809_i = isMirrored;
    }

    public void setFlipped(boolean isFlipped) {
        this.flip = isFlipped;
    }

    public void clear() {
        this.vertices = new PositionTextureVertex[0];
        this.faces = new TexturedPolygon[0];
        this.transformGroup.clear();
        this.transformGroup.put("0", new TransformGroupBone(new Bone(0.0f, 0.0f, 0.0f, 0.0f), 1.0));
        this.currentGroup = this.transformGroup.get("0");
    }

    public void copyTo(PositionTextureVertex[] verts, TexturedPolygon[] poly) {
        this.copyTo(verts, poly, true);
    }

    public void copyTo(PositionTextureVertex[] verts, TexturedPolygon[] poly, boolean copyGroup) {
        int idx;
        this.vertices = Arrays.copyOf(this.vertices, this.vertices.length + verts.length);
        this.faces = Arrays.copyOf(this.faces, this.faces.length + poly.length);
        for (idx = 0; idx < verts.length; ++idx) {
            this.vertices[this.vertices.length - verts.length + idx] = verts[idx];
            if (!copyGroup || !(verts[idx] instanceof PositionTransformVertex)) continue;
            ((PositionTransformVertex)verts[idx]).addGroup(this.currentGroup);
        }
        for (idx = 0; idx < poly.length; ++idx) {
            this.faces[this.faces.length - poly.length + idx] = poly[idx];
            if (!copyGroup) continue;
            this.currentTextureGroup.addPoly(poly[idx]);
        }
    }

    public void copyTo(PositionTextureVertex[] verts, TexturedQuad[] quad) {
        TexturedPolygon[] poly = new TexturedPolygon[quad.length];
        for (int idx = 0; idx < quad.length; ++idx) {
            poly[idx] = new TexturedPolygon((PositionTextureVertex[])quad[idx].field_78239_a);
        }
        this.copyTo(verts, poly);
    }

    public void setGroup(String groupName) {
        this.setGroup(groupName, new Bone(0.0f, 0.0f, 0.0f, 0.0f), 1.0);
    }

    public void setGroup(String groupName, Bone bone, double weight) {
        if (!this.transformGroup.containsKey(groupName)) {
            this.transformGroup.put(groupName, new TransformGroupBone(bone, weight));
        }
        this.currentGroup = this.transformGroup.get(groupName);
    }

    public TransformGroup getGroup() {
        return this.currentGroup;
    }

    public TransformGroup getGroup(String groupName) {
        if (!this.transformGroup.containsKey(groupName)) {
            return null;
        }
        return this.transformGroup.get(groupName);
    }

    public void setTextureGroup(String groupName) {
        if (!this.textureGroup.containsKey(groupName)) {
            this.textureGroup.put(groupName, new TextureGroup());
        }
        this.currentTextureGroup = this.textureGroup.get(groupName);
    }

    public TextureGroup getTextureGroup() {
        return this.currentTextureGroup;
    }

    public TextureGroup getTextureGroup(String groupName) {
        if (!this.textureGroup.containsKey(groupName)) {
            return null;
        }
        return this.textureGroup.get(groupName);
    }

    public void setGroupTexture(String s) {
        this.currentTextureGroup.texture = s;
    }

    public void setDefaultTexture(String s) {
        this.defaultTexture = s;
    }

    public void func_78785_a(float worldScale) {
        this.render(worldScale, false);
    }

    public void render(float worldScale, boolean oldRotateOrder) {
        if (this.field_1402_i) {
            return;
        }
        if (!this.field_78806_j) {
            return;
        }
        if (!this.compiled || this.forcedRecompile) {
            this.compileDisplayList(worldScale);
        }
        if (this.field_78795_f != 0.0f || this.field_78796_g != 0.0f || this.field_78808_h != 0.0f) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)(this.field_78800_c * worldScale), (float)(this.field_78797_d * worldScale), (float)(this.field_78798_e * worldScale));
            if (!oldRotateOrder && this.field_78796_g != 0.0f) {
                GL11.glRotatef((float)(this.field_78796_g * 57.29578f), (float)0.0f, (float)1.0f, (float)0.0f);
            }
            if (this.field_78808_h != 0.0f) {
                GL11.glRotatef((float)((float)(oldRotateOrder ? -1 : 1) * this.field_78808_h * 57.29578f), (float)0.0f, (float)0.0f, (float)1.0f);
            }
            if (oldRotateOrder && this.field_78796_g != 0.0f) {
                GL11.glRotatef((float)(-this.field_78796_g * 57.29578f), (float)0.0f, (float)1.0f, (float)0.0f);
            }
            if (this.field_78795_f != 0.0f) {
                GL11.glRotatef((float)(this.field_78795_f * 57.29578f), (float)1.0f, (float)0.0f, (float)0.0f);
            }
            this.callDisplayList();
            if (this.field_78805_m != null) {
                for (Object childModel : this.field_78805_m) {
                    ((ModelRenderer)childModel).func_78785_a(worldScale);
                }
            }
            GL11.glPopMatrix();
        } else if (this.field_78800_c != 0.0f || this.field_78797_d != 0.0f || this.field_78798_e != 0.0f) {
            GL11.glTranslatef((float)(this.field_78800_c * worldScale), (float)(this.field_78797_d * worldScale), (float)(this.field_78798_e * worldScale));
            this.callDisplayList();
            if (this.field_78805_m != null) {
                for (Object childModel : this.field_78805_m) {
                    ((ModelRenderer)childModel).func_78785_a(worldScale);
                }
            }
            GL11.glTranslatef((float)(-this.field_78800_c * worldScale), (float)(-this.field_78797_d * worldScale), (float)(-this.field_78798_e * worldScale));
        } else {
            this.callDisplayList();
            if (this.field_78805_m != null) {
                for (Object childModel : this.field_78805_m) {
                    ((ModelRenderer)childModel).func_78785_a(worldScale);
                }
            }
        }
    }

    public void func_78791_b(float f) {
        if (this.field_1402_i) {
            return;
        }
        if (!this.field_78806_j) {
            return;
        }
        if (!this.compiled) {
            this.compileDisplayList(f);
        }
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(this.field_78800_c * f), (float)(this.field_78797_d * f), (float)(this.field_78798_e * f));
        if (this.field_78796_g != 0.0f) {
            GL11.glRotatef((float)(this.field_78796_g * 57.29578f), (float)0.0f, (float)1.0f, (float)0.0f);
        }
        if (this.field_78795_f != 0.0f) {
            GL11.glRotatef((float)(this.field_78795_f * 57.29578f), (float)1.0f, (float)0.0f, (float)0.0f);
        }
        if (this.field_78808_h != 0.0f) {
            GL11.glRotatef((float)(this.field_78808_h * 57.29578f), (float)0.0f, (float)0.0f, (float)1.0f);
        }
        this.callDisplayList();
        GL11.glPopMatrix();
    }

    public void func_78794_c(float f) {
        if (this.field_1402_i) {
            return;
        }
        if (!this.field_78806_j) {
            return;
        }
        if (!this.compiled || this.forcedRecompile) {
            this.compileDisplayList(f);
        }
        if (this.field_78795_f != 0.0f || this.field_78796_g != 0.0f || this.field_78808_h != 0.0f) {
            GL11.glTranslatef((float)(this.field_78800_c * f), (float)(this.field_78797_d * f), (float)(this.field_78798_e * f));
            if (this.field_78808_h != 0.0f) {
                GL11.glRotatef((float)(this.field_78808_h * 57.29578f), (float)0.0f, (float)0.0f, (float)1.0f);
            }
            if (this.field_78796_g != 0.0f) {
                GL11.glRotatef((float)(this.field_78796_g * 57.29578f), (float)0.0f, (float)1.0f, (float)0.0f);
            }
            if (this.field_78795_f != 0.0f) {
                GL11.glRotatef((float)(this.field_78795_f * 57.29578f), (float)1.0f, (float)0.0f, (float)0.0f);
            }
        } else if (this.field_78800_c != 0.0f || this.field_78797_d != 0.0f || this.field_78798_e != 0.0f) {
            GL11.glTranslatef((float)(this.field_78800_c * f), (float)(this.field_78797_d * f), (float)(this.field_78798_e * f));
        }
    }

    private void callDisplayList() {
        if (this.useLegacyCompiler) {
            GL11.glCallList((int)this.displayList);
        } else {
            TextureManager renderEngine = RenderManager.field_78727_a.field_78724_e;
            Collection<TextureGroup> textures = this.textureGroup.values();
            Iterator<TextureGroup> itr = textures.iterator();
            int i = 0;
            while (itr.hasNext()) {
                TextureGroup curTexGroup = itr.next();
                curTexGroup.loadTexture();
                GL11.glCallList((int)this.displayListArray[i]);
                try {
                    if (!this.defaultTexture.equals("")) {
                        renderEngine.func_110577_a(new ResourceLocation("", this.defaultTexture));
                    }
                }
                catch (Throwable t) {
                    // empty catch block
                }
                ++i;
            }
        }
    }

    private void compileDisplayList(float worldScale) {
        if (this.useLegacyCompiler) {
            this.compileLegacyDisplayList(worldScale);
        } else {
            Collection<TextureGroup> textures = this.textureGroup.values();
            Iterator<TextureGroup> itr = textures.iterator();
            this.displayListArray = new int[this.textureGroup.size()];
            int i = 0;
            while (itr.hasNext()) {
                this.displayListArray[i] = GLAllocation.func_74526_a((int)1);
                GL11.glNewList((int)this.displayListArray[i], (int)4864);
                TmtTessellator tessellator = TmtTessellator.instance;
                TextureGroup usedGroup = itr.next();
                for (int j = 0; j < usedGroup.poly.size(); ++j) {
                    usedGroup.poly.get(j).draw(tessellator, worldScale);
                }
                GL11.glEndList();
                ++i;
            }
        }
        this.compiled = true;
    }

    private void compileLegacyDisplayList(float worldScale) {
        this.displayList = GLAllocation.func_74526_a((int)1);
        GL11.glNewList((int)this.displayList, (int)4864);
        TmtTessellator tessellator = TmtTessellator.instance;
        for (TexturedPolygon face : this.faces) {
            face.draw(tessellator, worldScale);
        }
        GL11.glEndList();
    }
}

