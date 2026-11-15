package ru.metrocraft.port.editor;

/** Shared lifetime and geometry bounds, without Minecraft client or GL dependencies. */
public final class PreviewRules {
    public static final int MAX_CELLS=256;
    public static final long LIFETIME_NANOS=10000000000L;
    private PreviewRules(){}
    public static long deadline(long now){return now+LIFETIME_NANOS;}
    // Subtraction is safe across System.nanoTime's signed wrap for this bounded interval.
    public static boolean alive(long now,long deadline){return now-deadline<0;}
    public static boolean show(boolean sameOwnerWorld,boolean creative,boolean alive,boolean spectator,boolean hidden,boolean screen,boolean fresh){
        return sameOwnerWorld && creative && alive && !spectator && !hidden && !screen && fresh;
    }
    public static boolean bounds(int x,int y,int z,int w,int h,int d){
        try{Blueprint.volume(w,h,d);}catch(IllegalArgumentException bad){return false;}
        return x>-30000000 && z>-30000000 && (long)x+w<=30000000 && (long)z+d<=30000000 && y>=0 && (long)y+h<=256;
    }
}
