package ru.metrocraft.port.client.menu;

import java.util.Random;

/** CPU-side donor scene state. No GL, network, player/weapon or GUI side effects.
 * Field names correspond to documented donor transforms, not invented positions.
 * This is only the mesh-motion subset, not a complete port of the donor GUI.
 */
public final class OriginalMenuState {
    public float pitch = -90, yaw, roll;
    public float sceneZ = 15, speed, gate, time;
    public float lightR, lightG, lightB;
    public float needle, multiplayerSwitch, siteSwitch, optionsSwitch, quitSwitch;
    public int selected;
    public final float[] tunnelZ = {0, -20.07f, -40.14f, -60.21f, -80.28f};
    public final int[] tunnelType = new int[5];
    public final float[][] webs = new float[5][3];
    public final float[][] roots = new float[5][3];
    public final float[][] particles = new float[10][3];
    private final Random random;
    private int needleFrames;
    private boolean needleDirection;

    public OriginalMenuState(Random random) {
        this.random = random;
        for (int i = 1; i < 5; i++) tunnelType[i] = random.nextInt(5);
        float[] particleZ = {-20, -26, -35, -42, -50, -58, -66, -74, -80, -90};
        for (int i = 0; i < 10; i++) {
            particles[i][0] = random.nextInt(7) - 3;
            particles[i][1] = particleZ[i];
        }
        float[] webZ = {-30, -45, -64};
        for (int i = 0; i < 3; i++) {
            webs[i][0] = random.nextInt(7) - 3;
            webs[i][1] = webZ[i];
            webs[i][2] = random.nextInt(3);
        }
        // Unassigned donor Vector3f entries (webs 3/4 and all roots) stay zero.
    }

    /** Donor updateScreen increments this timer separately from drawScreen. */
    public void tick() { time += 1; }

    /** Donor drawScreen uses seconds * 40, and freezes jumps greater than 10. */
    public static float frameDelta(double seconds) {
        if (!Double.isFinite(seconds) || seconds < 0) return 0;
        double delta = seconds * 40;
        return delta > 10 ? 0 : (float) delta;
    }

    public void look(float dx, float dy, float sensitivity) {
        if (!Float.isFinite(dx) || !Float.isFinite(dy) || !Float.isFinite(sensitivity)) return;
        float f = sensitivity * .6f;
        float scale = f * f * f * 8;
        yaw = clamp(yaw - dx * scale, -90, 90);
        pitch = clamp(pitch - dy * scale, -160, -20);
        selected = hotspot(yaw, pitch);
    }

    /** Exclusive boundaries and else-if priority from the donor update method. */
    public static int hotspot(float yaw, float pitch) {
        if (yaw > 15 && yaw < 32 && pitch < -56 && pitch > -68) return 1;
        if (yaw < 25 && yaw > -3 && pitch > -68 && pitch < -55) return 2;
        if (yaw > -24 && yaw < -2 && pitch > -68 && pitch < -49) return 3;
        if (yaw > -47 && yaw < -20 && pitch > -70 && pitch < -57) return 4;
        return 0;
    }

    /** Call once per rendered frame, only after all required meshes are ready. */
    public void advance(float delta, boolean running) {
        if (!Float.isFinite(delta) || delta < 0 || delta > 10) throw new IllegalArgumentException("delta");
        time += delta;
        if (running) {
            if (lightR < .6f && time > 60) {
                lightR += .0075f * delta;
                lightG += .0075f * delta;
                lightB += .012f * delta;
                if (lightR > .6f) { lightR = .6f; lightG = .6f; lightB = 1; }
            } else if (lightR >= .6f) {
                speed = Math.min(.15f, speed + .00025f * delta);
            }
            sceneZ -= speed * delta;
        }
        if (sceneZ > -31) gate = clamp(gate + (sceneZ < 3.5f && running ? .01f : -.01f) * delta, 0, 1);
        if (lightR < .1f) { pitch = -90; yaw = 0; }
        // Retain the donor's conditional increments, including fractional-frame
        // overshoot; clamping these switches would subtly change its animation.
        multiplayerSwitch = switchValue(multiplayerSwitch, selected == 1, 135, 6 * delta);
        siteSwitch = switchValue(siteSwitch, selected == 2, 90, 6 * delta);
        optionsSwitch = switchValue(optionsSwitch, selected == 3, 225, 9 * delta);
        quitSwitch = switchValue(quitSwitch, selected == 4, .02f, .001f * delta);
        if (needleFrames-- <= 0) {
            needleDirection = random.nextBoolean();
            needleFrames = 3 + random.nextInt(4);
        }
        float movement = (.25f + (random.nextInt(11) - 20) / 10f) * delta;
        needle = clamp(needle + (needleDirection ? movement : -movement), -45, 45);
    }

    /** Donor recycling happens after drawing the current frame. */
    public void recycle() {
        for (int i = 0; i < 5; i++) {
            if (tunnelZ[i] - sceneZ > 60) {
                tunnelZ[i] -= 100;
                tunnelType[i] = random.nextInt(5);
            }
        }
        recycle(webs, 3);
        recycle(roots, 2);
        for (float[] particle : particles) if (particle[1] - sceneZ > 60) {
            particle[1] -= 100;
            particle[0] = random.nextInt(7) - 3;
        }
    }
    private void recycle(float[][] objects, int types) {
        for (float[] object : objects) if (object[1] - sceneZ > 60) {
            object[1] -= 100;
            object[0] = random.nextInt(7) - 3;
            object[2] = random.nextInt(types);
        }
    }
    private static float switchValue(float value, boolean active, float max, float step) {
        return active ? (value < max ? value + step : value) : (value > 0 ? value - step : value);
    }
    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
