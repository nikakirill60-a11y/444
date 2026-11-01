/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ava
 *  cpw.mods.fml.client.registry.KeyBindingRegistry$KeyHandler
 *  cpw.mods.fml.common.TickType
 *  net.minecraft.client.Minecraft
 */
package mods.provoda.until;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.TickType;
import java.util.EnumSet;
import mods.provoda.ModBase;
import mods.provoda.until.PacketHandler;
import net.minecraft.client.Minecraft;

public class ModifiKeyBind
extends KeyBindingRegistry.KeyHandler {
    Minecraft mc = Minecraft.x();
    private EnumSet tickTypes = EnumSet.of(TickType.CLIENT);
    private boolean ModifikeyDown = false;
    private boolean ModifikeyUp = true;

    public ModifiKeyBind(ava[] keyBindings, boolean[] repeatings) {
        super(keyBindings, repeatings);
    }

    public String getLabel() {
        return "Metro2033Modifi";
    }

    public void keyDown(EnumSet types, ava kb, boolean tickEnd, boolean isRepeat) {
        if (!this.ModifikeyDown) {
            if (this.mc.g.cd() != null && (this.mc.g.cd().b() == ModBase.Rpk || this.mc.g.cd().b() == ModBase.Saiga || this.mc.g.cd().b() == ModBase.Abzac || this.mc.g.cd().b() == ModBase.Uboinik || this.mc.g.cd().b() == ModBase.Ventil || this.mc.g.cd().b() == ModBase.Vsv || this.mc.g.cd().b() == ModBase.Vyhlop || this.mc.g.cd().b() == ModBase.Ubludok || this.mc.g.cd().b() == ModBase.AK2012 || this.mc.g.cd().b() == ModBase.AK || this.mc.g.cd().b() == ModBase.Ashot || this.mc.g.cd().b() == ModBase.AKSU || this.mc.g.cd().b() == ModBase.Duplet || this.mc.g.cd().b() == ModBase.Gatling || this.mc.g.cd().b() == ModBase.Helsing || this.mc.g.cd().b() == ModBase.Tihar || this.mc.g.cd().b() == ModBase.Podonok || this.mc.g.cd().b() == ModBase.Preved || this.mc.g.cd().b() == ModBase.Railgun || this.mc.g.cd().b() == ModBase.Revolver || this.mc.g.cd().b() == ModBase.AKK) && this.mc.s == null) {
                PacketHandler.sendOpenGuiPacketModifi();
            }
            this.ModifikeyDown = true;
            this.ModifikeyUp = false;
        }
    }

    public void keyUp(EnumSet types, ava kb, boolean tickEnd) {
        if (!this.ModifikeyUp) {
            this.ModifikeyDown = false;
            this.ModifikeyUp = true;
        }
    }

    public EnumSet ticks() {
        return this.tickTypes;
    }
}
