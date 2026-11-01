/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  mp
 */
package mods.provoda.until;

public class LinkResolver {
    public static void solveLink(mp entA, mp entB, float posAX, float posAY, float posAZ, float posBX, float posBY, float posBZ, float d) {
        float rigidity = 0.5f;
        float damping = 0.8f;
        float limit = 0.04f;
        float targetMotionX = (float)(entA.x - entB.x);
        float targetMotionZ = (float)(entA.z - entB.z);
        float targetX = (posAX + posBX) / 2.0f;
        float targetZ = (posAZ + posBZ) / 2.0f;
        float vecX = targetX - posAX;
        float vecZ = targetZ - posAZ;
        float hypot = (float)Math.sqrt(vecX * vecX + vecZ * vecZ);
        float unitX = vecX / hypot;
        float unitZ = vecZ / hypot;
        float stretch = hypot - d;
        float damperX = LinkResolver.limitForce(targetMotionX * damping, limit);
        float damperZ = LinkResolver.limitForce(targetMotionZ * damping, limit);
        float forceX = LinkResolver.limitForce(rigidity * stretch * unitX - damperX, limit);
        float forceZ = LinkResolver.limitForce(rigidity * stretch * unitZ - damperZ, limit);
        if (forceX == forceX && forceZ == forceZ) {
            entA.g((double)forceX, 0.0, (double)forceZ);
            entB.g((double)(-forceX), 0.0, (double)(-forceZ));
        }
    }

    private static float limitForce(float force, float limit) {
        return Math.min(Math.abs(force), limit) * Math.signum(force);
    }
}
