package ru.metrocraft.port.blackrazid.survival;

/** Only the selected thirst/lamp rules, NOT the old general player-properties class. */
public final class SurvivalState {
    public static final float THIRST_PER_TICK=.004F, ENERGY_PER_TICK=.015F;
    public static final int STARVE_INTERVAL=65;
    public float water=100,energy=0;
    public int starveTicks=0;
    public boolean lamp=false;
    public static float percent(float value,float fallback){return Float.isFinite(value)?Math.max(0,Math.min(100,value)):fallback;}
    public void sanitize(){water=percent(water,100);energy=percent(energy,0);starveTicks=Math.max(0,Math.min(STARVE_INTERVAL-1,starveTicks));}
    /** Returns one damage pulse; paused modes must not accumulate catch-up damage. */
    public boolean tick(boolean paused,boolean hasLamp){
        sanitize();
        if(!hasLamp || energy<=0)lamp=false;
        if(paused)return false;
        if(lamp){energy=Math.max(0,energy-ENERGY_PER_TICK);if(energy<=0)lamp=false;}
        if(water>0){water=Math.max(0,water-THIRST_PER_TICK);starveTicks=0;return false;}
        if(++starveTicks>=STARVE_INTERVAL){starveTicks=0;return true;}
        return false;
    }
    public boolean drink(){sanitize();if(water>=100)return false;water=Math.min(100,water+30);starveTicks=0;return true;}
    public boolean charge(){sanitize();if(energy>=100)return false;energy=Math.min(100,energy+20);return true;}
    public boolean toggle(boolean hasLamp){sanitize();lamp=hasLamp && energy>0 && !lamp;return lamp;}
}
