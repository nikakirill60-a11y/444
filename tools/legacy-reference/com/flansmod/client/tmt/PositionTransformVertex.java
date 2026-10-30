/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Vec3
 */
package com.flansmod.client.tmt;

import com.flansmod.client.tmt.PositionTextureVertex;
import com.flansmod.client.tmt.TransformGroup;
import java.util.ArrayList;
import net.minecraft.util.Vec3;

public class PositionTransformVertex
extends PositionTextureVertex {
    public Vec3 neutralVector;
    public ArrayList<TransformGroup> transformGroups = new ArrayList();

    public PositionTransformVertex(float x, float y, float z, float u, float v) {
        this(Vec3.func_72443_a((double)x, (double)y, (double)z), u, v);
    }

    public PositionTransformVertex(PositionTextureVertex vertex, float u, float v) {
        super(vertex, u, v);
        this.neutralVector = vertex instanceof PositionTransformVertex ? ((PositionTransformVertex)vertex).neutralVector : Vec3.func_72443_a((double)vertex.field_78243_a.field_72450_a, (double)vertex.field_78243_a.field_72448_b, (double)vertex.field_78243_a.field_72449_c);
    }

    public PositionTransformVertex(PositionTextureVertex vertex) {
        this(vertex, vertex.field_78241_b, vertex.field_78242_c);
    }

    public PositionTransformVertex(Vec3 vector, float u, float v) {
        super(vector, u, v);
        this.neutralVector = Vec3.func_72443_a((double)vector.field_72450_a, (double)vector.field_72448_b, (double)vector.field_72449_c);
    }

    public void setTransformation() {
        if (this.transformGroups.size() == 0) {
            this.field_78243_a.field_72450_a = this.neutralVector.field_72450_a;
            this.field_78243_a.field_72448_b = this.neutralVector.field_72448_b;
            this.field_78243_a.field_72449_c = this.neutralVector.field_72449_c;
            return;
        }
        double weight = 0.0;
        for (TransformGroup transformGroup : this.transformGroups) {
            weight += transformGroup.getWeight();
        }
        this.field_78243_a.field_72450_a = 0.0;
        this.field_78243_a.field_72448_b = 0.0;
        this.field_78243_a.field_72449_c = 0.0;
        for (TransformGroup group : this.transformGroups) {
            double cWeight = group.getWeight() / weight;
            Vec3 vector = group.doTransformation(this);
            this.field_78243_a.field_72450_a += cWeight * vector.field_72450_a;
            this.field_78243_a.field_72448_b += cWeight * vector.field_72448_b;
            this.field_78243_a.field_72449_c += cWeight * vector.field_72449_c;
        }
    }

    public void addGroup(TransformGroup group) {
        this.transformGroups.add(group);
    }

    public void removeGroup(TransformGroup group) {
        this.transformGroups.remove(group);
    }
}

