package ru.metrocraft.port.client.menu;

import java.util.*;

/** Standalone CPU regression checks: no Minecraft, Forge or OpenGL context. */
public final class OriginalMenuSceneCheck {
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
    private static void near(double expected, double actual) {
        check(Math.abs(expected - actual) < .0001, expected + " != " + actual);
    }
    private static final class Recorder implements OriginalMenuGeometry.Sink {
        final Deque<double[]> stack = new ArrayDeque<>();
        final Map<String, List<double[]>> draws = new LinkedHashMap<>();
        double[] matrix = identity();
        String failOn;
        static double[] identity() {
            double[] m = new double[16];
            for (int i = 0; i < 4; i++) m[i*4+i] = 1;
            return m;
        }
        void multiply(double[] b) {
            double[] c = new double[16];
            for (int row = 0; row < 4; row++) for (int col = 0; col < 4; col++)
                for (int i = 0; i < 4; i++) c[row*4+col] += matrix[row*4+i] * b[i*4+col];
            matrix = c;
        }
        @Override public void push() { stack.push(matrix.clone()); }
        @Override public void pop() { matrix = stack.pop(); }
        @Override public void translate(float x, float y, float z) {
            double[] m = identity(); m[3]=x; m[7]=y; m[11]=z; multiply(m);
        }
        @Override public void scale(float x, float y, float z) {
            double[] m = identity(); m[0]=x; m[5]=y; m[10]=z; multiply(m);
        }
        @Override public void rotate(float angle, float x, float y, float z) {
            double a = Math.toRadians(angle), c = Math.cos(a), s = Math.sin(a), t = 1-c;
            double[] m = {t*x*x+c,t*x*y-s*z,t*x*z+s*y,0,
                          t*x*y+s*z,t*y*y+c,t*y*z-s*x,0,
                          t*x*z-s*y,t*y*z+s*x,t*z*z+c,0, 0,0,0,1};
            multiply(m);
        }
        @Override public void color(float r, float g, float b) { }
        @Override public void mesh(String source) {
            if (source.equals(failOn)) throw new IllegalStateException("injected mesh failure");
            draws.computeIfAbsent(source, key -> new ArrayList<>()).add(matrix.clone());
        }
        int count(String mesh) { List<double[]> list=draws.get(mesh); return list == null ? 0 : list.size(); }
        void balanced() { check(stack.isEmpty(), "Leaked matrix stack"); }
    }

    public static void main(String[] args) {
        OriginalMenuState s = new OriginalMenuState(new Random(12));
        near(15,s.sceneZ); near(-90,s.pitch); near(0,s.speed);
        for (int i=1;i<5;i++) near(-20.07*i,s.tunnelZ[i]);
        for (int type:s.tunnelType) check(type>=0 && type<5,"tunnel type");
        check(OriginalMenuState.hotspot(23.5f,-62)==1,"multiplayer center");
        check(OriginalMenuState.hotspot(8,-62)==2,"site center");
        check(OriginalMenuState.hotspot(-10,-59)==3,"options center");
        check(OriginalMenuState.hotspot(-28,-64)==4,"quit center");
        check(OriginalMenuState.hotspot(20,-60)==1,"overlap priority 1 before 2");
        check(OriginalMenuState.hotspot(-2.5f,-60)==2,"overlap priority 2 before 3");
        check(OriginalMenuState.hotspot(-22,-60)==3,"overlap priority 3 before 4");
        check(OriginalMenuState.hotspot(32,-60)==0,"exclusive right boundary");
        check(OriginalMenuState.hotspot(23,-68)==0,"exclusive lower boundary");
        check(OriginalMenuState.hotspot(Float.NaN,-60)==0,"NaN hotspot");
        near(1,OriginalMenuState.frameDelta(.025)); near(10,OriginalMenuState.frameDelta(.25));
        near(0,OriginalMenuState.frameDelta(.251)); near(0,OriginalMenuState.frameDelta(-1));
        near(0,OriginalMenuState.frameDelta(Double.NaN));
        s.look(100000,100000,1); near(-90,s.yaw); near(-160,s.pitch);
        s.look(-100000,-100000,1); near(90,s.yaw); near(-20,s.pitch);
        s.look(Float.NaN,1,1); near(90,s.yaw);
        s = new OriginalMenuState(new Random(12));
        for (int i=0;i<60;i++) s.advance(1,true);
        near(0,s.lightR); near(15,s.sceneZ);
        for (int i=0;i<1000;i++) s.advance(1,true);
        near(.15,s.speed); near(.6,s.lightR); near(1,s.lightB);
        check(s.sceneZ<3.5,"scene did not move"); near(1,s.gate);
        float stopped=s.sceneZ; s.advance(1,false); near(stopped,s.sceneZ);
        s.selected=1;s.multiplayerSwitch=0;s.advance(1,false);near(6,s.multiplayerSwitch);
        s.selected=2;s.advance(1,false);near(0,s.multiplayerSwitch);near(6,s.siteSwitch);
        s.selected=3;s.advance(1,false);near(9,s.optionsSwitch);
        s.selected=4;s.advance(1,false);near(.001,s.quitSwitch);
        s.sceneZ=-100;s.tunnelZ[0]=0;s.recycle();near(-100,s.tunnelZ[0]);
        boolean rejected=false;try{s.advance(Float.NaN,true);}catch(IllegalArgumentException expected){rejected=true;}
        check(rejected,"invalid delta accepted");

        s = new OriginalMenuState(new Random(12));
        s.gate=1;
        Recorder r = new Recorder();
        OriginalMenuGeometry geometry = new OriginalMenuGeometry(r,s);
        geometry.regina(); r.balanced();
        check(r.count("drezina/regina.m2033model")==1,"Regina body");
        check(r.count("drezina/regina_well.m2033model")==4,"four wheels");
        double[] wheel=r.draws.get("drezina/regina_well.m2033model").get(0);
        near(.77,wheel[7]);near(1.725,wheel[11]);
        check(r.count("menu/part9_1.m2033model")==2,"dashboard fans");
        r = new Recorder(); geometry=new OriginalMenuGeometry(r,s);geometry.intro();r.balanced();
        check(r.count("tunnels/rails.m2033model")==32,"intro rails");
        check(r.count("tunnels/tunnel_betonnew_1.m2033model")==7,"intro tunnel repeats");
        double[] left=r.draws.get("menu/germo_left.m2033model").get(0);
        double[] right=r.draws.get("menu/germo_right.m2033model").get(0);
        near(3.03,left[3]);near(-2.47,right[3]);near(.28,left[7]);near(14.7,left[11]);
        for(int type=0;type<5;type++) {r=new Recorder();geometry=new OriginalMenuGeometry(r,s);geometry.tunnel(type);r.balanced();
            check(r.count("tunnels/tunnel"+(type+1)+".m2033model")==1,"tunnel variation");
            check(r.count("decor_metro/trub.m2033model")==8,"tunnel pipes");
            if(type==4)near(1.014,r.draws.get("tunnels/tunnel5.m2033model").get(0)[10]);
        }
        // Every generated mesh method must unwind even if loading a mesh fails.
        String[] failures={"drezina/regina_well.m2033model","menu/germo_left.m2033model","decor_metro/trub.m2033model"};
        for(String failure:failures){
            r=new Recorder();r.failOn=failure;
            boolean thrown=false;
            try {new OriginalMenuScene(r,s).draw();}catch(IllegalStateException expected){thrown=true;}
            check(thrown,"failure was not reached: "+failure);r.balanced();
        }
        r=new Recorder();new OriginalMenuScene(r,s).draw();r.balanced();
        near(15,s.sceneZ); // Drawing itself must never advance simulation.
        check(r.count("drezina/regina.m2033model")==1,"scene render order");
        System.out.println("Original menu CPU scene checks passed (no GL/game verification)");
    }
}
