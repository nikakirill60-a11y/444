/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ayl
 *  lt
 *  org.lwjgl.opengl.GL11
 *  tj
 */
package mods.provoda.gui.modifi;

import mods.provoda.ModBase;
import mods.provoda.gui.modifi.ModifiContainer;
import org.lwjgl.opengl.GL11;

public class ModifiGui
extends ayl {
    private float xSize_lo;
    private float ySize_lo;
    private String texture;
    private final lt inventory;

    public ModifiGui(ModifiContainer var1) {
        super((tj)var1);
        this.inventory = var1.inventory;
    }

    public void a(int var1, int var2, float var3) {
        super.a(var1, var2, var3);
        this.xSize_lo = var1;
        this.ySize_lo = var2;
    }

    protected void b(int var1, int var2) {
    }

    protected void a(float var1, int var2, int var3) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.texture = "/mods/provoda/gui/null_gui.png";
        if (this.g.g.bK.h() != null) {
            if (this.g.g.bK.h().b() == ModBase.Rpk) {
                this.texture = "/mods/provoda/gui/rpk_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.Saiga) {
                this.texture = "/mods/provoda/gui/saiga_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.Abzac) {
                this.texture = "/mods/provoda/gui/abzac_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.Uboinik) {
                this.texture = "/mods/provoda/gui/uboinik_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.Ventil) {
                this.texture = "/mods/provoda/gui/ventil_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.Vsv) {
                this.texture = "/mods/provoda/gui/vsv_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.Vyhlop) {
                this.texture = "/mods/provoda/gui/vyhlop_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.Ubludok) {
                this.texture = "/mods/provoda/gui/ubludok_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.AK2012) {
                this.texture = "/mods/provoda/gui/aknew_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.AK) {
                this.texture = "/mods/provoda/gui/ak_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.Ashot) {
                this.texture = "/mods/provoda/gui/ashot_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.AKSU) {
                this.texture = "/mods/provoda/gui/aksu_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.Duplet) {
                this.texture = "/mods/provoda/gui/duplet_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.Gatling) {
                this.texture = "/mods/provoda/gui/gatling_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.Helsing) {
                this.texture = "/mods/provoda/gui/helsing_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.Tihar) {
                this.texture = "/mods/provoda/gui/tihar_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.Podonok) {
                this.texture = "/mods/provoda/gui/podonok_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.Preved) {
                this.texture = "/mods/provoda/gui/preved_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.Railgun) {
                this.texture = "/mods/provoda/gui/railgun_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.Revolver) {
                this.texture = "/mods/provoda/gui/revolver_gui.png";
            } else if (this.g.g.bK.h().b() == ModBase.AKK) {
                this.texture = "/mods/provoda/gui/akk_gui.png";
            }
        }
        this.g.p.b(this.texture);
        int var4 = (this.h - this.b) / 2;
        int var5 = (this.i - this.c) / 2 - 4;
        this.b(var4, var5 - 37, 0, 0, this.b, 221);
    }
}
