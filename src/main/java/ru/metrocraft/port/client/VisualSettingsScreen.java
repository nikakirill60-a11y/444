package ru.metrocraft.port.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;
import ru.metrocraft.port.metromod.VisualOptions;
import java.io.*;

/** Local visual configuration only; Cancel/Escape never applies the draft or mutates server equipment. */
public final class VisualSettingsScreen extends GuiScreen {
    private final GuiScreen parent;
    private final Configuration config;
    private VisualOptions draft=new VisualOptions();
    public VisualSettingsScreen(GuiScreen parent){
        this.parent=parent;config=new Configuration(new File(new File(Minecraft.getMinecraft().gameDir,"config"),"metroport-visuals.cfg"));config.load();
        draft.mask=config.getBoolean("maskOverlay","client",true,"");draft.goggles=config.getBoolean("nvgTextureOverlay","client",true,"");
        draft.noise=config.getBoolean("nvgNoise","client",true,"");draft.tint=config.getBoolean("nvgTint","client",true,"");
        draft.maskOpacity=config.getFloat("maskOpacity","client",1,0,1,"");draft.noiseStrength=config.getFloat("nvgNoiseStrength","client",.035F,0,.2F,"");draft.normalize();
    }
    private String label(int id){
        switch(id){
            case 0:return I18n.format("metroport.visual.mask")+": "+on(draft.mask);
            case 1:return I18n.format("metroport.visual.goggles")+": "+on(draft.goggles);
            case 2:return I18n.format("metroport.visual.noise")+": "+on(draft.noise);
            case 3:return I18n.format("metroport.visual.tint")+": "+on(draft.tint);
            case 4:return I18n.format("metroport.visual.opacity")+": "+Math.round(draft.maskOpacity*100)+"%";
            default:return I18n.format("metroport.visual.strength")+": "+String.format(java.util.Locale.ROOT,"%.1f%%",draft.noiseStrength*100);
        }
    }
    private String on(boolean enabled){return I18n.format(enabled?"options.on":"options.off");}
    @Override public void initGui(){
        buttonList.clear();int top=Math.max(30,height/2-85);
        for(int i=0;i<6;i++)buttonList.add(new GuiButton(i,width/2-145,top+i*23,290,20,label(i)));
        buttonList.add(new GuiButton(6,width/2-145,top+143,90,20,I18n.format("metroport.visual.defaults")));
        buttonList.add(new GuiButton(7,width/2-45,top+143,90,20,I18n.format("gui.done")));
        buttonList.add(new GuiButton(8,width/2+55,top+143,90,20,I18n.format("gui.cancel")));
    }
    @Override protected void actionPerformed(GuiButton button)throws IOException{
        switch(button.id){
            case 0:draft.mask=!draft.mask;break;case 1:draft.goggles=!draft.goggles;break;
            case 2:draft.noise=!draft.noise;break;case 3:draft.tint=!draft.tint;break;
            case 4:draft.maskOpacity=draft.maskOpacity>=.999F?0:Math.min(1,Math.round(draft.maskOpacity*10+1)/10F);break;
            case 5:draft.noiseStrength=draft.noiseStrength>=.199F?0:Math.min(.2F,(Math.round(draft.noiseStrength*200)+1)/200F);break;
            case 6:draft=new VisualOptions();break;
            case 7:
                draft.normalize();config.get("client","maskOverlay",true).set(draft.mask);config.get("client","nvgTextureOverlay",true).set(draft.goggles);
                config.get("client","nvgNoise",true).set(draft.noise);config.get("client","nvgTint",true).set(draft.tint);
                config.get("client","maskOpacity",1D).set(draft.maskOpacity);config.get("client","nvgNoiseStrength",.035D).set(draft.noiseStrength);
                config.save();MetroOverlays.configure(config,draft.tint);mc.displayGuiScreen(parent);return;
            case 8:mc.displayGuiScreen(parent);return;
        }
        initGui();
    }
    @Override protected void keyTyped(char c,int key)throws IOException{if(key==1)mc.displayGuiScreen(parent);else super.keyTyped(c,key);}
    @Override public void drawScreen(int x,int y,float partial){
        drawDefaultBackground();drawCenteredString(fontRenderer,I18n.format("metroport.visual.title"),width/2,12,0xFFFFFF);
        super.drawScreen(x,y,partial);
    }
}
