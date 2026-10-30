/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.Vec3
 */
package com.flansmod.client.tmt;

import com.flansmod.client.tmt.ModelPoolEntry;
import com.flansmod.client.tmt.PositionTextureVertex;
import com.flansmod.client.tmt.PositionTransformVertex;
import com.flansmod.client.tmt.TexturedPolygon;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ModelPoolObjEntry
extends ModelPoolEntry {
    public ModelPoolObjEntry() {
        this.fileExtensions = new String[]{"obj"};
    }

    @Override
    public void getModel(File file) {
        try {
            int i;
            String s;
            BufferedReader in = new BufferedReader(new FileReader(file));
            ArrayList<PositionTransformVertex> verts = new ArrayList<PositionTransformVertex>();
            ArrayList<float[]> uvs = new ArrayList<float[]>();
            ArrayList<float[]> normals = new ArrayList<float[]>();
            ArrayList<TexturedPolygon> face = new ArrayList<TexturedPolygon>();
            while ((s = in.readLine()) != null) {
                int ind;
                Object v;
                if (s.contains("#")) {
                    s = s.substring(0, s.indexOf("#"));
                }
                if ((s = s.trim()).equals("")) continue;
                if (s.startsWith("g ")) {
                    this.setTextureGroup(s.substring(s.indexOf(" ") + 1).trim());
                    continue;
                }
                if (s.startsWith("v ")) {
                    s = s.substring(s.indexOf(" ") + 1).trim();
                    v = new float[3];
                    for (int i2 = 0; i2 < 3; ++i2) {
                        ind = s.indexOf(" ");
                        v[i2] = ind > -1 ? Float.parseFloat(s.substring(0, ind)) : Float.parseFloat(s.substring(0));
                        s = s.substring(s.indexOf(" ") + 1).trim();
                    }
                    float flt = v[2];
                    v[2] = -v[1];
                    v[1] = flt;
                    verts.add(new PositionTransformVertex(v[0], v[1], v[2], 0.0f, 0.0f));
                    continue;
                }
                if (s.startsWith("vt ")) {
                    s = s.substring(s.indexOf(" ") + 1).trim();
                    v = new float[2];
                    for (int i3 = 0; i3 < 2; ++i3) {
                        ind = s.indexOf(" ");
                        v[i3] = ind > -1 ? Float.parseFloat(s.substring(0, ind)) : Float.parseFloat(s.substring(0));
                        s = s.substring(s.indexOf(" ") + 1).trim();
                    }
                    uvs.add(new float[]{v[0], 1.0f - v[1]});
                    continue;
                }
                if (s.startsWith("vn ")) {
                    s = s.substring(s.indexOf(" ") + 1).trim();
                    v = new float[3];
                    for (int i4 = 0; i4 < 3; ++i4) {
                        ind = s.indexOf(" ");
                        v[i4] = ind > -1 ? Float.parseFloat(s.substring(0, ind)) : Float.parseFloat(s.substring(0));
                        s = s.substring(s.indexOf(" ") + 1).trim();
                    }
                    float flt = v[2];
                    v[2] = v[1];
                    v[1] = flt;
                    normals.add(new float[]{v[0], v[1], v[2]});
                    continue;
                }
                if (!s.startsWith("f ")) continue;
                s = s.substring(s.indexOf(" ") + 1).trim();
                v = new ArrayList();
                int finalPhase = 0;
                float[] normal = new float[]{0.0f, 0.0f, 0.0f};
                ArrayList<Vec3> iNormal = new ArrayList<Vec3>();
                do {
                    float[] curNormals;
                    float[] curUV;
                    int vInt;
                    int ind2 = s.indexOf(" ");
                    String s1 = s;
                    if (ind2 > -1) {
                        s1 = s.substring(0, ind2);
                    }
                    if (s1.contains("/")) {
                        String[] f = s1.split("/");
                        vInt = Integer.parseInt(f[0]) - 1;
                        if (f[1].equals("")) {
                            f[1] = f[0];
                        }
                        int vtInt = Integer.parseInt(f[1]) - 1;
                        curUV = uvs.size() > vtInt ? (float[])uvs.get(vtInt) : new float[]{0.0f, 0.0f};
                        int vnInt = 0;
                        if (f.length == 3) {
                            if (f[2].equals("")) {
                                f[2] = f[0];
                            }
                            vnInt = Integer.parseInt(f[2]) - 1;
                        } else {
                            vnInt = Integer.parseInt(f[0]) - 1;
                        }
                        curNormals = normals.size() > vnInt ? (float[])normals.get(vnInt) : new float[]{0.0f, 0.0f, 0.0f};
                    } else {
                        vInt = Integer.parseInt(s1) - 1;
                        curUV = uvs.size() > vInt ? (float[])uvs.get(vInt) : new float[]{0.0f, 0.0f};
                        curNormals = normals.size() > vInt ? (float[])normals.get(vInt) : new float[]{0.0f, 0.0f, 0.0f};
                    }
                    iNormal.add(Vec3.func_72443_a((double)curNormals[0], (double)curNormals[1], (double)curNormals[2]));
                    normal[0] = normal[0] + curNormals[0];
                    normal[1] = normal[1] + curNormals[1];
                    normal[2] = normal[2] + curNormals[2];
                    if (vInt < verts.size()) {
                        ((ArrayList)v).add(((PositionTransformVertex)((Object)verts.get(vInt))).setTexturePosition(curUV[0], curUV[1]));
                        if (verts.get(vInt) instanceof PositionTransformVertex) {
                            ((PositionTransformVertex)((Object)verts.get(vInt))).addGroup(this.group);
                        }
                    }
                    if (ind2 > -1) {
                        s = s.substring(s.indexOf(" ") + 1).trim();
                        continue;
                    }
                    ++finalPhase;
                } while (finalPhase < 1);
                float d = MathHelper.func_76133_a((double)(normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2]));
                normal[0] = normal[0] / d;
                normal[1] = normal[1] / d;
                normal[2] = normal[2] / d;
                PositionTextureVertex[] vToArr = new PositionTextureVertex[((ArrayList)v).size()];
                for (int i5 = 0; i5 < ((ArrayList)v).size(); ++i5) {
                    vToArr[i5] = (PositionTextureVertex)((Object)((ArrayList)v).get(i5));
                }
                TexturedPolygon poly = new TexturedPolygon(vToArr);
                poly.setNormals(normal[0], normal[1], normal[2]);
                poly.setNormals(iNormal);
                face.add(poly);
                this.texture.addPoly(poly);
            }
            this.vertices = new PositionTransformVertex[verts.size()];
            for (i = 0; i < verts.size(); ++i) {
                this.vertices[i] = (PositionTransformVertex)((Object)verts.get(i));
            }
            this.faces = new TexturedPolygon[face.size()];
            for (i = 0; i < face.size(); ++i) {
                this.faces[i] = (TexturedPolygon)face.get(i);
            }
            in.close();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

