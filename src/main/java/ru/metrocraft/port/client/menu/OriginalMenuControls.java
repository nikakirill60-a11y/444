package ru.metrocraft.port.client.menu;

/** Original four keyboard targets and 1/10 per-frame camera easing. */
public final class OriginalMenuControls {
    private static final float[][] TARGET={{0,-90},{23.5f,-62},{8,-62},{-10,-59},{-28,-64}};
    private boolean targeting;
    public void cycle(OriginalMenuState state,int direction) {
        state.selected=direction>0 ? state.selected%4+1 : (state.selected<=1?4:state.selected-1);
        targeting=true;
    }
    public void mouse(OriginalMenuState state,float dx,float dy,float sensitivity) {
        if(dx!=0 || dy!=0) { targeting=false;state.look(dx,dy,sensitivity); }
    }
    public void advance(OriginalMenuState state,float delta) {
        if(!targeting)return;
        float[] target=TARGET[state.selected];
        if(Math.abs(state.yaw-target[0])<=.5f && Math.abs(state.pitch-target[1])<=.5f)targeting=false;
        else {
            state.yaw-=(state.yaw-target[0])/10*delta;
            state.pitch-=(state.pitch-target[1])/10*delta;
        }
    }
}
