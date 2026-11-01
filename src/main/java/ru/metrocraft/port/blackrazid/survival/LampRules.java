package ru.metrocraft.port.blackrazid.survival;
/** Client-only light sampling policy; never changes world blocks or the global/NVG brightness. */
public final class LampRules {
    private LampRules(){}
    public static int brightness(double distance,double forwardCosine){
        if(!Double.isFinite(distance) || !Double.isFinite(forwardCosine) || distance<0 || distance>8)return 0;
        if(distance<1.5)return 15;
        return forwardCosine>=.85?Math.max(0,15-(int)Math.ceil(distance)):0;
    }
}
