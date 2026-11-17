package ru.metrocraft.port.client.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

/** Dashboard-space lettering using the donor's positions, scale and Z/Z/Y axes.
 * Font rasterization is provided by 1.12.2, not claimed identical to donor font.
 */
final class OriginalMenuOverlay {
    static final String[] LABELS={"", "metroport.menu.enter", "metroport.menu.site", "metroport.menu.options", "metroport.menu.quit"};
    String notice="";
    private final OriginalMenuState state;
    OriginalMenuOverlay(OriginalMenuState state) { this.state=state; }
    void draw() {
        GlStateManager.disableLighting();GlStateManager.disableFog();
        GlStateManager.color(1,1,1,1);
        try {
            int light=Math.max(0,Math.min(255,Math.round(state.lightB*255)));
            int white=0xff000000|light<<16|light<<8|light;
            int amber=0xff000000|light<<16|Math.round(light/1.3f)<<8;
            text(.55f,.01f,-.16f,180,0,-45,.0025f,.0025f,.0025f,I18n.format("metroport.menu.server"),white);
            // No automatic status probe or invented online count.
            text(.44f,-.025f,-.065f,180,0,-45,.0016f,.0016f,.0016f,OriginalMenuClient.SERVER,white);
            text(-.55f,-.08f,-.24f,180,-5,45,.0018f,.0018f,.0018f,"Metro 2033 Client",0xff000000);
            text(-.55f,-.1f,-.24f,180,-5,45,.0014f,.0014f,.0014f,"MinETRO / Forge 1.12.2",0xff000000);
            text(-.01f,-.09f,-.05f,180,0,0,.0030769233f,.004f,.004f,I18n.format("metroport.menu.welcome"),amber);
            String caption=notice.isEmpty()?I18n.format(LABELS[state.selected]):notice;
            text(-.01f,-.135f,-.05f,180,0,0,.0034f,.0034f,.0034f,caption,amber);
            text(.295f,-.278f,.01f,180,3,0,.0023529413f,.0022f,.004f,I18n.format(LABELS[1]),0xff000000);
            text(.085f,-.265f,0,180,-3,0,.0022222223f,.0022f,.004f,I18n.format(LABELS[2]),0xff000000);
            text(-.129f,-.294f,0,180,1,0,.0032520327f,.003f,.004f,I18n.format(LABELS[3]),0xff000000);
            text(-.329f,-.266f,0,180,-1,0,.0030769233f,.003f,.004f,I18n.format(LABELS[4]),0xff000000);
        } finally { GlStateManager.enableFog();GlStateManager.enableLighting();GlStateManager.color(1,1,1,1); }
    }
    private static void text(float x,float y,float z,float rz,float rz2,float ry,
                             float sx,float sy,float sz,String text,int color) {
        FontRenderer font=Minecraft.getMinecraft().fontRenderer;
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x,y,z);GlStateManager.scale(sx,sy,sz);
            GlStateManager.rotate(rz,0,0,1);GlStateManager.rotate(rz2,0,0,1);GlStateManager.rotate(ry,0,1,0);
            font.drawString(text,-font.getStringWidth(text)/2,0,color);
        } finally { GlStateManager.popMatrix(); }
    }
}
