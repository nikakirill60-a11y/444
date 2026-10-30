/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.Vec3
 */
package com.flansmod.client.tmt;

import com.flansmod.client.tmt.Coord2D;
import com.flansmod.client.tmt.PositionTextureVertex;
import com.flansmod.client.tmt.PositionTransformVertex;
import com.flansmod.client.tmt.Shape3D;
import com.flansmod.client.tmt.TexturedPolygon;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class Shape2D {
    public ArrayList<Coord2D> coords;

    public Shape2D() {
        this.coords = new ArrayList();
    }

    public Shape2D(Coord2D[] coordArray) {
        this.coords = new ArrayList();
        Collections.addAll(this.coords, coordArray);
    }

    public Shape2D(ArrayList<Coord2D> coordList) {
        this.coords = coordList;
    }

    public Coord2D[] getCoordArray() {
        return (Coord2D[])this.coords.toArray();
    }

    public Shape3D extrude(float x, float y, float z, float rotX, float rotY, float rotZ, float depth, int u, int v, float textureWidth, float textureHeight, int shapeTextureWidth, int shapeTextureHeight, int sideTextureWidth, int sideTextureHeight, float[] faceLengths) {
        PositionTransformVertex[] verts = new PositionTransformVertex[this.coords.size() * 2];
        PositionTextureVertex[] vertsTop = new PositionTransformVertex[this.coords.size()];
        PositionTextureVertex[] vertsBottom = new PositionTransformVertex[this.coords.size()];
        TexturedPolygon[] poly = new TexturedPolygon[this.coords.size() + 2];
        Vec3 extrudeVector = Vec3.func_72443_a((double)0.0, (double)0.0, (double)depth);
        this.setVectorRotations(extrudeVector, rotX, rotY, rotZ);
        if (faceLengths != null && faceLengths.length < this.coords.size()) {
            faceLengths = null;
        }
        float totalLength = 0.0f;
        for (int idx = 0; idx < this.coords.size(); ++idx) {
            Coord2D curCoord = this.coords.get(idx);
            Coord2D nextCoord = this.coords.get((idx + 1) % this.coords.size());
            float texU1 = (float)(curCoord.uCoord + u) / textureWidth;
            float texU2 = (float)(shapeTextureWidth * 2 - curCoord.uCoord + u) / textureWidth;
            float texV = (float)(curCoord.vCoord + v) / textureHeight;
            Vec3 vecCoord = Vec3.func_72443_a((double)curCoord.xCoord, (double)curCoord.yCoord, (double)0.0);
            this.setVectorRotations(vecCoord, rotX, rotY, rotZ);
            verts[idx] = new PositionTransformVertex(x + (float)vecCoord.field_72450_a, y + (float)vecCoord.field_72448_b, z + (float)vecCoord.field_72449_c, texU1, texV);
            verts[idx + this.coords.size()] = new PositionTransformVertex(x + (float)vecCoord.field_72450_a - (float)extrudeVector.field_72450_a, y + (float)vecCoord.field_72448_b - (float)extrudeVector.field_72448_b, z + (float)vecCoord.field_72449_c - (float)extrudeVector.field_72449_c, texU2, texV);
            vertsTop[idx] = new PositionTransformVertex(verts[idx]);
            vertsBottom[this.coords.size() - idx - 1] = new PositionTransformVertex(verts[idx + this.coords.size()]);
            if (faceLengths != null) {
                totalLength += faceLengths[idx];
                continue;
            }
            totalLength = (float)((double)totalLength + Math.sqrt(Math.pow(curCoord.xCoord - nextCoord.xCoord, 2.0) + Math.pow(curCoord.yCoord - nextCoord.yCoord, 2.0)));
        }
        poly[this.coords.size()] = new TexturedPolygon(vertsTop);
        poly[this.coords.size() + 1] = new TexturedPolygon(vertsBottom);
        float currentLengthPosition = totalLength;
        for (int idx = 0; idx < this.coords.size(); ++idx) {
            Coord2D curCoord = this.coords.get(idx);
            Coord2D nextCoord = this.coords.get((idx + 1) % this.coords.size());
            float currentLength = (float)Math.sqrt(Math.pow(curCoord.xCoord - nextCoord.xCoord, 2.0) + Math.pow(curCoord.yCoord - nextCoord.yCoord, 2.0));
            if (faceLengths != null) {
                currentLength = faceLengths[faceLengths.length - idx - 1];
            }
            float ratioPosition = currentLengthPosition / totalLength;
            float ratioLength = (currentLengthPosition - currentLength) / totalLength;
            float texU1 = (ratioLength * (float)sideTextureWidth + (float)u) / textureWidth;
            float texU2 = (ratioPosition * (float)sideTextureWidth + (float)u) / textureWidth;
            float texV1 = ((float)v + (float)shapeTextureHeight) / textureHeight;
            float texV2 = ((float)v + (float)shapeTextureHeight + (float)sideTextureHeight) / textureHeight;
            PositionTextureVertex[] polySide = new PositionTransformVertex[]{new PositionTransformVertex(verts[idx], texU2, texV1), new PositionTransformVertex(verts[this.coords.size() + idx], texU2, texV2), new PositionTransformVertex(verts[this.coords.size() + (idx + 1) % this.coords.size()], texU1, texV2), new PositionTransformVertex(verts[(idx + 1) % this.coords.size()], texU1, texV1)};
            poly[idx] = new TexturedPolygon(polySide);
            currentLengthPosition -= currentLength;
        }
        return new Shape3D(verts, poly);
    }

    protected void setVectorRotations(Vec3 vector, float xRot, float yRot, float zRot) {
        float x = xRot;
        float y = yRot;
        float z = zRot;
        float xC = MathHelper.func_76134_b((float)x);
        float xS = MathHelper.func_76126_a((float)x);
        float yC = MathHelper.func_76134_b((float)y);
        float yS = MathHelper.func_76126_a((float)y);
        float zC = MathHelper.func_76134_b((float)z);
        float zS = MathHelper.func_76126_a((float)z);
        double xVec = vector.field_72450_a;
        double yVec = vector.field_72448_b;
        double zVec = vector.field_72449_c;
        double xy = (double)xC * yVec - (double)xS * zVec;
        double xz = (double)xC * zVec + (double)xS * yVec;
        double yz = (double)yC * xz - (double)yS * xVec;
        double yx = (double)yC * xVec + (double)yS * xz;
        double zx = (double)zC * yx - (double)zS * xy;
        double zy = (double)zC * xy + (double)zS * yx;
        xVec = zx;
        yVec = zy;
        zVec = yz;
        vector.field_72450_a = xVec;
        vector.field_72448_b = yVec;
        vector.field_72449_c = zVec;
    }
}

