package ru.metrocraft.mwf;

/** Pure fixed-tick rules. Visual interpolation must never authorize a server shot. */
public final class RotorRules {
    public static final String GUN="metro_gatling";
    public static final class Rotor {
        public double speed,angle;
        public void tick(boolean trigger,boolean motor) {
            double before=speed;
            speed=Math.max(0,Math.min(1,speed+(trigger?(motor?.1:.05):(-1.0/30))));
            angle=(angle+(before+speed)*.5*60)%360; // 1200 degrees/sec at full speed
        }
        public boolean ready(){return speed>=.25-1e-8;}
        public int rpm(boolean fiveBarrels){return (int)Math.round((fiveBarrels?600:400)*(.25+.75*speed));}
        public int interval(boolean fiveBarrels){return (int)Math.ceil(1200.0/rpm(fiveBarrels)-1e-8);}
    }
    private RotorRules(){}
}
