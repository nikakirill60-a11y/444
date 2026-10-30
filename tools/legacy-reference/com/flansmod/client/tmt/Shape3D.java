/*
 * Decompiled with CFR 0.152.
 */
package com.flansmod.client.tmt;

import com.flansmod.client.tmt.PositionTransformVertex;
import com.flansmod.client.tmt.TexturedPolygon;

public class Shape3D {
    public PositionTransformVertex[] vertices;
    public TexturedPolygon[] faces;

    public Shape3D(PositionTransformVertex[] verts, TexturedPolygon[] poly) {
        this.vertices = verts;
        this.faces = poly;
    }
}

