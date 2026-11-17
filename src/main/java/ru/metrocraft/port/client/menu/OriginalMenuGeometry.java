package ru.metrocraft.port.client.menu;

/** Generated mesh-only transform adapter; see tools/generate_metromod_menu_scene.py.
 * Not a complete GUI or proof of visual parity. No Minecraft/GL dependency.
 * Source SHA256: a6a5c835ffd7277aa57996eff780aed11df1d61bab9e1644296689276f9d0cad
 */
public final class OriginalMenuGeometry {
    public interface Sink {
        void push();
        void pop();
        void translate(float x, float y, float z);
        void rotate(float angle, float x, float y, float z);
        void scale(float x, float y, float z);
        void color(float r, float g, float b);
        void mesh(String sourceReference);
        default void cameraLights() {}
        default void dashboard() {}
        default void particles() {}
        default void glows() {}
    }
    private final Sink out;
    private final OriginalMenuState s;
    public OriginalMenuGeometry(Sink out, OriginalMenuState state) {
        this.out = out;
        this.s = state;
    }
    public void roots(float f, float f2, int n) {
        out.push(); try {
        switch (n) {
            case 0: {
                out.translate(f, 10.0f, f2);
                out.rotate(90.0f, 0.0f, 1.0f, 0.0f);
                out.mesh("plant/root1.m2033model");
                break;
            }
            case 1: {
                out.translate(f, 7.0f, f2);
                out.scale(10.0f, 10.0f, 10.0f);
                out.mesh("plant/root2.m2033model");
            }
        }
        } finally { out.pop(); }
    }

    public void webs(float f, float f2, int n) {
        out.push(); try {
        out.translate(f, 8.0f, f2);
        switch (n) {
            case 0: {
                out.mesh("decor_metro/web1.m2033model");
                break;
            }
            case 1: {
                out.mesh("decor_metro/web2.m2033model");
                break;
            }
            case 2: {
                out.mesh("decor_metro/web3.m2033model");
            }
        }
        } finally { out.pop(); }
    }

    public void tunnel(int n) {
        switch (n) {
            case 0: {
                out.mesh("tunnels/tunnel1.m2033model");
                break;
            }
            case 1: {
                out.mesh("tunnels/tunnel2.m2033model");
                break;
            }
            case 2: {
                out.mesh("tunnels/tunnel3.m2033model");
                break;
            }
            case 3: {
                out.mesh("tunnels/tunnel4.m2033model");
                break;
            }
            case 4: {
                out.scale(1.0f, 1.0f, 1.014f);
                out.mesh("tunnels/tunnel5.m2033model");
            }
        }
        out.push(); try {
        out.translate(3.2f, 1.15f, -11.2f);
        for (int i = 0; i < 8; ++i) {
            out.translate(0.0f, 0.0f, 2.6f);
            out.push(); try {
            out.rotate(90.0f, 0.0f, 1.0f, 0.0f);
            out.mesh("decor_metro/trub.m2033model");
            } finally { out.pop(); }
        }
        } finally { out.pop(); }
    }

    public void intro() {
        int n;
        out.push(); try {
        out.translate(0.33f, 4.18f, 14.0f);
        out.push(); try {
        out.push(); try {
        out.translate(0.0f, 2.5f, -35.0f);
        out.scale(5.0f, 5.0f, 5.0f);
        out.mesh("plant/root2.m2033model");
        } finally { out.pop(); }
        out.push(); try {
        out.translate(-3.2f, -3.2f, -49.0f);
        for (n = 0; n < 16; ++n) {
            out.translate(0.0f, 0.0f, 2.6f);
            out.push(); try {
            out.rotate(-90.0f, 0.0f, 1.0f, 0.0f);
            out.mesh("decor_metro/trub.m2033model");
            } finally { out.pop(); }
        }
        } finally { out.pop(); }
        out.push(); try {
        out.translate(-3.2f, -3.2f, -5.15f);
        out.rotate(-90.0f, 0.0f, 1.0f, 0.0f);
        out.mesh("decor_metro/trub90.m2033model");
        } finally { out.pop(); }
        out.push(); try {
        out.translate(-3.2f, -3.2f, 7.0f);
        out.rotate(90.0f, 0.0f, 1.0f, 0.0f);
        out.rotate(180.0f, 0.0f, 1.0f, 0.0f);
        out.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        out.mesh("decor_metro/trub90.m2033model");
        } finally { out.pop(); }
        out.push(); try {
        out.translate(-3.2f, -3.2f, 9.0f);
        out.rotate(90.0f, 0.0f, 1.0f, 0.0f);
        out.rotate(180.0f, 0.0f, 1.0f, 0.0f);
        out.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        out.mesh("decor_metro/trub.m2033model");
        } finally { out.pop(); }
        out.push(); try {
        out.translate(-3.0f, 4.0f, 1.0f);
        out.rotate(-90.0f, 0.0f, 1.0f, 0.0f);
        out.mesh("plant/root1.m2033model");
        } finally { out.pop(); }
        } finally { out.pop(); }
        out.push(); try {
        out.rotate(90.0f, 0.0f, 1.0f, 0.0f);
        out.mesh("menu/tun.m2033model");
        } finally { out.pop(); }
        out.push(); try {
        out.translate(-0.05f, -3.9f, 0.7f);
        out.rotate(180.0f, 0.0f, 1.0f, 0.0f);
        out.push(); try {
        out.translate((-s.gate * 2.75f), 0.0f, 0.0f);
        out.mesh("menu/germo_left.m2033model");
        } finally { out.pop(); }
        out.translate((s.gate * 2.75f), 0.0f, 0.0f);
        out.mesh("menu/germo_right.m2033model");
        } finally { out.pop(); }
        out.translate(-0.9f, -4.17f, 11.55f);
        for (n = 0; n < 32; ++n) {
            out.translate(0.0f, 0.0f, -1.82f);
            out.mesh("tunnels/rails.m2033model");
        }
        } finally { out.pop(); }
        out.push(); try {
        out.rotate(180.0f, 0.0f, 1.0f, 0.0f);
        out.translate(-0.35f, -0.01f, -5.96f);
        for (n = 0; n < 7; ++n) {
            out.translate(0.01f, -0.0f, 5.32f);
            out.mesh("tunnels/tunnel_betonnew_1.m2033model");
        }
        } finally { out.pop(); }
    }

    public void regina() {
        out.push(); try {
        out.translate(-0.0f, 0.14f, 0.0f);
        out.rotate(0.0f, 0.0f, 1.0f, 0.0f);
        out.mesh("drezina/regina.m2033model");
        out.translate(0.0f, 0.63f, 1.725f);
        out.push(); try {
        out.rotate((-s.sceneZ * 50.0f), 1.0f, 0.0f, 0.0f);
        out.mesh("drezina/regina_well.m2033model");
        } finally { out.pop(); }
        out.push(); try {
        out.translate(0.0f, 0.0f, -3.46f);
        out.rotate((-s.sceneZ * 50.0f), 1.0f, 0.0f, 0.0f);
        out.mesh("drezina/regina_well.m2033model");
        } finally { out.pop(); }
        out.rotate(180.0f, 0.0f, 1.0f, 0.0f);
        out.push(); try {
        out.rotate((s.sceneZ * 50.0f), 1.0f, 0.0f, 0.0f);
        out.mesh("drezina/regina_well.m2033model");
        } finally { out.pop(); }
        out.push(); try {
        out.translate(0.0f, 0.0f, 3.46f);
        out.rotate((s.sceneZ * 50.0f), 1.0f, 0.0f, 0.0f);
        out.mesh("drezina/regina_well.m2033model");
        } finally { out.pop(); }
        out.push(); try {
        out.rotate(180.0f, 0.0f, 1.0f, 0.0f);
        out.translate(0.0f, 3.0f, 0.0f);
        out.rotate(30.0f, 1.0f, 0.0f, 0.0f);
        out.translate(0.0f, -1.7f, -0.3f);
        out.push(); try {
        out.translate(-0.57f, -0.17f, -0.22f);
        out.rotate(-60.0f, 0.0f, 1.0f, 0.0f);
        out.color(1.0f, 1.0f, 1.0f);
        out.mesh("menu/part2.m2033model");
        } finally { out.pop(); }
        out.dashboard();
        out.push(); try {
        out.translate(0.52f, -0.36f, -0.1f);
        out.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        out.mesh("menu/part1.m2033model");
        } finally { out.pop(); }
        out.push(); try {
        out.translate(-0.56f, -0.31f, 0.0f);
        out.rotate(-45.0f, 0.0f, 1.0f, 0.0f);
        out.mesh("menu/part3.m2033model");
        out.push(); try {
        out.translate(0.002f, 0.202f, -0.004f);
        out.rotate(s.needle, 0.0f, 0.0f, 1.0f);
        out.mesh("menu/part3_1.m2033model");
        } finally { out.pop(); }
        } finally { out.pop(); }
        out.push(); try {
        out.translate(0.0f, -0.12f, 0.0f);
        out.mesh("menu/part4.m2033model");
        } finally { out.pop(); }
        out.push(); try {
        out.translate(0.3f, -0.25f, 0.0f);
        out.mesh("menu/part5.m2033model");
        out.translate(-0.027f, 0.013f, 0.0f);
        out.rotate(s.multiplayerSwitch, 0.0f, 0.0f, 1.0f);
        out.mesh("menu/part5_1.m2033model");
        } finally { out.pop(); }
        out.push(); try {
        out.translate(0.08f, -0.25f, 0.0f);
        out.mesh("menu/part6.m2033model");
        out.translate(0.017f, 0.025f, 0.0f);
        out.rotate((s.siteSwitch - 30.0f), 0.0f, 1.0f, 0.0f);
        out.mesh("menu/part6_1.m2033model");
        } finally { out.pop(); }
        out.push(); try {
        out.translate(-0.14f, -0.25f, 0.0f);
        out.mesh("menu/part7.m2033model");
        out.translate(0.008f, 0.014f, 0.0f);
        out.rotate(s.optionsSwitch, 0.0f, 0.0f, 1.0f);
        out.mesh("menu/part7_1.m2033model");
        } finally { out.pop(); }
        out.push(); try {
        out.translate(-0.34f, -0.25f, 0.0f);
        out.mesh("menu/part8.m2033model");
        out.push(); try {
        out.translate(0.0f, -0.0f, (s.quitSwitch - 0.01f));
        out.mesh("menu/part8_1.m2033model");
        } finally { out.pop(); }
        out.push(); try {
        out.translate(0.0f, -0.0f, (-s.quitSwitch + 0.01f));
        out.mesh("menu/part8_2.m2033model");
        } finally { out.pop(); }
        } finally { out.pop(); }
        out.translate(0.0f, -0.4f, 0.0f);
        out.color(0.4f, 0.4f, 0.4f);
        out.mesh("menu/part9.m2033model");
        out.push(); try {
        out.translate(0.4f, -0.0f, 0.13f);
        out.rotate((-s.time), 0.0f, 1.0f, 0.0f);
        out.mesh("menu/part9_1.m2033model");
        } finally { out.pop(); }
        out.push(); try {
        out.translate(-0.4f, -0.0f, 0.15f);
        out.rotate((-s.time + 90.0f), 0.0f, 1.0f, 0.0f);
        out.mesh("menu/part9_1.m2033model");
        } finally { out.pop(); }
        } finally { out.pop(); }
        } finally { out.pop(); }
    }
}
