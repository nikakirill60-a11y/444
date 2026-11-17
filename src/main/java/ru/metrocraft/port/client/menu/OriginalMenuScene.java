package ru.metrocraft.port.client.menu;

/** Mesh scene composition recovered from donor render order.
 * Caller owns projection (70 degrees, .1..100), lights, GL state and lifecycle.
 * Runtime callbacks supply lighting, lettering, particles and light flares.
 * Donor player-body rendering is not supplied yet.
 */
public final class OriginalMenuScene {
    private final OriginalMenuGeometry.Sink out;
    private final OriginalMenuState state;
    private final OriginalMenuGeometry geometry;
    public OriginalMenuScene(OriginalMenuGeometry.Sink out, OriginalMenuState state) {
        this.out = out;
        this.state = state;
        this.geometry = new OriginalMenuGeometry(out, state);
    }
    public void draw() {
        out.push();
        try {
            out.rotate(state.pitch - 90, 1, 0, 0);
            out.rotate(state.yaw, 0, 1, 0);
            out.rotate(state.roll, 0, 0, 1);
            out.cameraLights();
            out.rotate(180, 0, 0, 1);
            out.translate(0, -2.6f, .2f);
            geometry.regina();
            out.push();
            try {
                out.translate(0, 0, state.sceneZ);
                out.push();
                try {
                    out.translate(.27f, 0, 34.4f);
                    out.rotate(180, 0, 1, 0);
                    for (int i = 0; i < 5; i++) {
                        out.push();
                        try {
                            out.translate(0, 0, state.tunnelZ[i]);
                            geometry.tunnel(state.tunnelType[i]);
                        } finally { out.pop(); }
                    }
                } finally { out.pop(); }
                if (state.sceneZ > -31) geometry.intro();
                for (float[] web : state.webs) geometry.webs(web[0], -web[1], Math.round(web[2]));
                for (float[] root : state.roots) geometry.roots(root[0], -root[1], Math.round(root[2]));
                out.particles();
            } finally { out.pop(); }
            out.glows();
        } finally { out.pop(); }
    }
}
