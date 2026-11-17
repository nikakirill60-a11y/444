package ru.metrocraft.port.client.menu;

import java.io.IOException;
import java.util.Random;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

/** Original scene main-menu runtime, not an in-world renderer or launcher UI.
 * Visual equivalence still needs comparison in a real 1.12.2 client.
 */
public final class OriginalMenuScreen extends GuiScreen {
    private final OriginalMenuState state=new OriginalMenuState(new Random());
    private final OriginalMenuControls controls=new OriginalMenuControls();
    private final OriginalMenuLighting lights=new OriginalMenuLighting();
    private final OriginalMenuOverlay overlay=new OriginalMenuOverlay(state);
    private OriginalMenuMeshes meshes;
    private OriginalMenuScene scene;
    private OriginalMenuAudio audio;
    private OriginalMenuEffects effects;
    private long previousFrame;
    private boolean grabbed,closed,releasedByUser;
    private int previousSelection;
    private int activationDelay=10;

    @Override public void initGui() {
        closed=false;releasedByUser=false;previousFrame=0;activationDelay=10;
        OriginalMenuClient.opened(this);
    }
    private void load() throws IOException {
        if(meshes!=null)return;
        try(MenuGlScope scope=new MenuGlScope()) {
            effects=new OriginalMenuEffects();
            meshes=new OriginalMenuMeshes() {
                @Override public void cameraLights(){lights.positions(state);}
                @Override public void dashboard(){overlay.draw();}
                @Override public void particles(){effects.drops(state);}
                @Override public void glows(){effects.glows(state);}
            };
            meshes.preload();scene=new OriginalMenuScene(meshes,state);
        } catch(IOException|RuntimeException e) { releaseResources();throw e; }
        audio=new OriginalMenuAudio(mc.getSoundHandler());previousFrame=0;
    }
    private void grab(boolean value) {
        if(grabbed==value)return;
        grabbed=value;Mouse.setGrabbed(value);Mouse.getDX();Mouse.getDY();
    }
    @Override public void drawScreen(int mouseX,int mouseY,float partialTicks) {
        if(closed)return;
        try {
            load();
            long now=System.nanoTime();
            float delta=previousFrame==0?0:OriginalMenuState.frameDelta((now-previousFrame)/1e9);
            previousFrame=now;
            if(!Display.isActive()) { grab(false);delta=0; }
            else {
                grab(!releasedByUser);
                if(!releasedByUser && state.lightR>.1f)controls.mouse(state,Mouse.getDX(),Mouse.getDY(),mc.gameSettings.mouseSensitivity);
                else {Mouse.getDX();Mouse.getDY();}
            }
            controls.advance(state,delta);state.advance(delta,true);
            try(MenuGlScope scope=new MenuGlScope()) {
                GlStateManager.clearColor(0,0,0,1);GlStateManager.depthMask(true);
                GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
                GlStateManager.viewport(0,0,mc.displayWidth,mc.displayHeight);
                GL11.glMatrixMode(GL11.GL_PROJECTION);GL11.glLoadIdentity();
                GLU.gluPerspective(70,(float)mc.displayWidth/Math.max(1,mc.displayHeight),.1f,100);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);GL11.glLoadIdentity();
                lights.setup();scene.draw();
            }
            state.recycle();
            if(audio!=null && previousSelection!=state.selected && state.selected!=0)audio.selected();
            if(previousSelection!=state.selected)overlay.notice="";
            previousSelection=state.selected;
        } catch(IOException|RuntimeException failure) {
            releaseResources();OriginalMenuClient.vanilla(failure);
        }
    }
    @Override public void updateScreen() {
        state.tick();if(activationDelay>0)activationDelay--;
        if(audio!=null && Display.isActive()) {
            mc.getMusicTicker().stopMusic();audio.tick(state.speed);
        } else if(audio!=null)audio.close();
    }
    @Override protected void mouseClicked(int x,int y,int button) throws IOException {
        if(!Display.isActive())return;
        if(releasedByUser) {releasedByUser=false;grab(true);activationDelay=2;return;}
        if(button==0)activate();
    }
    @Override public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel=Mouse.getEventDWheel();
        if(wheel!=0 && state.lightR>.1f)controls.cycle(state,wheel<0?1:-1);
    }
    @Override protected void keyTyped(char typedChar,int keyCode) throws IOException {
        if(keyCode==Keyboard.KEY_F8) { OriginalMenuClient.vanilla(null);return; }
        if(keyCode==Keyboard.KEY_ESCAPE) { releasedByUser=true;grab(false);return; }
        if(state.lightR<=.1f)return;
        if(keyCode==Keyboard.KEY_W || keyCode==Keyboard.KEY_A || keyCode==Keyboard.KEY_UP || keyCode==Keyboard.KEY_LEFT)
            controls.cycle(state,-1);
        else if(keyCode==Keyboard.KEY_S || keyCode==Keyboard.KEY_D || keyCode==Keyboard.KEY_DOWN || keyCode==Keyboard.KEY_RIGHT || keyCode==Keyboard.KEY_TAB)
            controls.cycle(state,1);
        else if(keyCode==Keyboard.KEY_RETURN || keyCode==Keyboard.KEY_NUMPADENTER || keyCode==Keyboard.KEY_SPACE)activate();
    }
    private void activate() {
        if(activationDelay>0 || state.lightR<=.1f || meshes==null)return;
        activationDelay=10;
        switch(state.selected) {
            case 1:
                // The sole network action: explicit user activation, loopback only.
                mc.displayGuiScreen(new GuiConnecting(this,mc,new ServerData("MinETRO",OriginalMenuClient.SERVER,false)));
                break;
            case 2:
                overlay.notice=I18n.format("metroport.menu.site_unset");
                if(audio!=null)audio.selected();
                break;
            case 3:
                // Use the target version's actual settings, not unsupported donor settings.
                mc.displayGuiScreen(new GuiOptions(this,mc.gameSettings));break;
            case 4: mc.shutdown();releaseResources();break;
            default: break;
        }
    }
    void releaseResources() {
        grab(false);
        if(audio!=null){audio.close();audio=null;}
        if(meshes!=null){meshes.close();meshes=null;}
        scene=null;effects=null;previousFrame=0;
    }
    @Override public void onGuiClosed() {
        closed=true;releaseResources();OriginalMenuClient.closed(this);
    }
    @Override public boolean doesGuiPauseGame(){return false;}
}
