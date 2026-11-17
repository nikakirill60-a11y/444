package ru.metrocraft.port.client.menu;

import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public class OriginalMenuControlsTest {
    @Test public void cycleFromNoneAndWrapInBothDirections() {
        OriginalMenuState state=new OriginalMenuState(new Random(1));OriginalMenuControls c=new OriginalMenuControls();
        c.cycle(state,-1);assertEquals(4,state.selected);
        c.cycle(state,1);assertEquals(1,state.selected);
        for(int i=0;i<4;i++)c.cycle(state,1);assertEquals(1,state.selected);
        c.cycle(state,-1);assertEquals(4,state.selected);
    }
    @Test public void keyboardEasesToOriginalHotspotWithoutChangingMovement() {
        OriginalMenuState state=new OriginalMenuState(new Random(1));OriginalMenuControls c=new OriginalMenuControls();c.cycle(state,1);
        c.advance(state,1);assertEquals(2.35,state.yaw,.0001);assertEquals(-87.2,state.pitch,.0001);
        for(int i=0;i<100;i++)c.advance(state,1);
        assertEquals(1,OriginalMenuState.hotspot(state.yaw,state.pitch));assertEquals(15,state.sceneZ,0);
    }
    @Test public void mouseMovementCancelsKeyboardTargeting() {
        OriginalMenuState state=new OriginalMenuState(new Random(1));OriginalMenuControls c=new OriginalMenuControls();c.cycle(state,1);
        c.mouse(state,10,10,1);float yaw=state.yaw,pitch=state.pitch;c.advance(state,1);
        assertEquals(yaw,state.yaw,0);assertEquals(pitch,state.pitch,0);
    }
}
