/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.Vec3
 */
package com.flansmod.client.tmt;

import com.flansmod.client.tmt.Angle3D;
import com.flansmod.client.tmt.Bone;
import com.flansmod.client.tmt.PositionTransformVertex;
import com.flansmod.client.tmt.TransformGroup;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class TransformGroupBone
extends TransformGroup {
    protected Angle3D baseAngles;
    protected Vec3 baseVector;
    protected Bone attachedBone;
    protected double weight;

    public TransformGroupBone(Bone bone, double wght) {
        this.baseVector = bone.getPosition();
        this.baseAngles = bone.getAbsoluteAngle();
        this.attachedBone = bone;
        this.weight = wght;
    }

    public Angle3D getBaseAngles() {
        return this.baseAngles.copy();
    }

    public Angle3D getTransformAngle() {
        Angle3D returnAngle = this.attachedBone.getAbsoluteAngle().copy();
        returnAngle.angleX -= this.baseAngles.angleX;
        returnAngle.angleY -= this.baseAngles.angleY;
        returnAngle.angleZ -= this.baseAngles.angleZ;
        return returnAngle;
    }

    public Vec3 getBaseVector() {
        return Vec3.func_72443_a((double)this.baseVector.field_72450_a, (double)this.baseVector.field_72448_b, (double)this.baseVector.field_72449_c);
    }

    public Vec3 getTransformVector() {
        return this.baseVector.func_72444_a(this.attachedBone.getPosition());
    }

    public Vec3 getCurrentVector() {
        return this.attachedBone.getPosition();
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    public void attachBone(Bone bone) {
        this.baseVector = bone.getPosition();
        this.baseAngles = bone.getAbsoluteAngle();
        this.attachedBone = bone;
    }

    @Override
    public Vec3 doTransformation(PositionTransformVertex vertex) {
        Vec3 vector = Vec3.func_72443_a((double)vertex.neutralVector.field_72450_a, (double)vertex.neutralVector.field_72448_b, (double)vertex.neutralVector.field_72449_c);
        vector = this.getBaseVector().func_72444_a(vector);
        Angle3D angle = this.getTransformAngle();
        this.setVectorRotations(vector, angle.angleX, angle.angleY, angle.angleZ);
        return vector;
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

