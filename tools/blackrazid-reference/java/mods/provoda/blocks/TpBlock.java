/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aa
 *  aab
 *  ab
 *  aif
 *  apa
 *  ly
 *  mods.provoda.until.ExtendetPlayer
 *  net.minecraft.server.MinecraftServer
 *  sq
 *  t
 */
package mods.provoda.blocks;

import mods.provoda.ModBase;
import mods.provoda.until.ExtendetPlayer;
import net.minecraft.server.MinecraftServer;

public class TpBlock
extends apa
implements ab {
    public TpBlock(int var1) {
        super(var1, aif.e);
    }

    public void a(ly var1) {
        this.cQ = var1.a("provoda:tpblock");
    }

    public boolean a(aab var1, int var2, int var3, int var4, sq var5, int var6, float var7, float var8, float var9) {
        if (!var1.I) {
            ExtendetPlayer ep = ExtendetPlayer.get((sq)var5);
            MinecraftServer minecraftserver = MinecraftServer.D();
            aa icommandmanager = minecraftserver.E();
            if (ep.getFaction() == 0) {
                icommandmanager.a((ab)this, "tp " + var5.bS + " " + ModBase.Polisx + " " + ModBase.Polisy + " " + ModBase.Polisz);
            } else if (ep.getFaction() == 1) {
                icommandmanager.a((ab)this, "tp " + var5.bS + " " + ModBase.Krx + " " + ModBase.Kry + " " + ModBase.Krz);
            } else if (ep.getFaction() == 2) {
                icommandmanager.a((ab)this, "tp " + var5.bS + " " + ModBase.Nazix + " " + ModBase.Naziy + " " + ModBase.Naziz);
            } else if (ep.getFaction() == 3) {
                icommandmanager.a((ab)this, "tp " + var5.bS + " " + ModBase.ArbatKonfederationx + " " + ModBase.ArbatKonfederationy + " " + ModBase.ArbatKonfederationz);
            } else if (ep.getFaction() == 4) {
                icommandmanager.a((ab)this, "tp " + var5.bS + " " + ModBase.Ganzax + " " + ModBase.Ganzay + " " + ModBase.Ganzaz);
            } else if (ep.getFaction() == 5) {
                icommandmanager.a((ab)this, "tp " + var5.bS + " " + ModBase.Konfederation1905yearx + " " + ModBase.Konfederation1905yeary + " " + ModBase.Konfederation1905yearz);
            } else if (ep.getFaction() == 6) {
                icommandmanager.a((ab)this, "tp " + var5.bS + " " + ModBase.VDNHx + " " + ModBase.VDNHy + " " + ModBase.VDNHz);
            } else if (ep.getFaction() == 7) {
                icommandmanager.a((ab)this, "tp " + var5.bS + " " + ModBase.Prestx + " " + ModBase.Presty + " " + ModBase.Prestz);
            } else if (ep.getFaction() == 15) {
                int i = var1.s.nextInt(7);
                if (i == 0) {
                    icommandmanager.a((ab)this, "tp " + var5.bS + " " + ModBase.Polisx + " " + ModBase.Polisy + " " + ModBase.Polisz);
                } else if (i == 1) {
                    icommandmanager.a((ab)this, "tp " + var5.bS + " " + ModBase.Krx + " " + ModBase.Kry + " " + ModBase.Krz);
                } else if (i == 2) {
                    icommandmanager.a((ab)this, "tp " + var5.bS + " " + ModBase.Nazix + " " + ModBase.Naziy + " " + ModBase.Naziz);
                } else if (i == 3) {
                    icommandmanager.a((ab)this, "tp " + var5.bS + " " + ModBase.ArbatKonfederationx + " " + ModBase.ArbatKonfederationy + " " + ModBase.ArbatKonfederationz);
                } else if (i == 4) {
                    icommandmanager.a((ab)this, "tp " + var5.bS + " " + ModBase.Ganzax + " " + ModBase.Ganzay + " " + ModBase.Ganzaz);
                } else if (i == 5) {
                    icommandmanager.a((ab)this, "tp " + var5.bS + " " + ModBase.Konfederation1905yearx + " " + ModBase.Konfederation1905yeary + " " + ModBase.Konfederation1905yearz);
                } else if (i == 6) {
                    icommandmanager.a((ab)this, "tp " + var5.bS + " " + ModBase.VDNHx + " " + ModBase.VDNHy + " " + ModBase.VDNHz);
                } else if (i == 7) {
                    icommandmanager.a((ab)this, "tp " + var5.bS + " " + ModBase.Prestx + " " + ModBase.Presty + " " + ModBase.Prestz);
                }
            }
        }
        return true;
    }

    public String c_() {
        return "Faction";
    }

    public void a(String s) {
    }

    public boolean a(int i, String s) {
        return i <= 2;
    }

    public String a(String s, Object ... var2) {
        return s;
    }

    public t b() {
        return null;
    }
}
