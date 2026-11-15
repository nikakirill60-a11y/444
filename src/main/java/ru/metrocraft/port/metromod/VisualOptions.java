package ru.metrocraft.port.metromod;

/** Pure settings draft, separate from renderer state and server-owned equipment flags. */
public final class VisualOptions {
    public boolean mask=true,goggles=true,noise=true,tint=true;
    public float maskOpacity=1F,noiseStrength=.035F;
    public static float bounded(float value,float fallback,float max){return Float.isNaN(value)||Float.isInfinite(value)?fallback:Math.max(0,Math.min(max,value));}
    public void normalize(){maskOpacity=bounded(maskOpacity,1F,1F);noiseStrength=bounded(noiseStrength,.035F,.2F);}
    public VisualOptions copy(){VisualOptions o=new VisualOptions();o.mask=mask;o.goggles=goggles;o.noise=noise;o.tint=tint;o.maskOpacity=maskOpacity;o.noiseStrength=noiseStrength;return o;}
}
