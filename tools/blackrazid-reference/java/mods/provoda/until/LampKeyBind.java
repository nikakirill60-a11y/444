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
import mods.provoda.until.PacketHandler;
import net.minecraft.client.Minecraft;

public class LampKeyBind
extends KeyBindingRegistry.KeyHandler {
    private EnumSet tickTypes = EnumSet.of(TickType.CLIENT);
    private boolean keyDown = false;
    public static boolean keyPressed = false;
    public boolean keyhasPressed;
    private boolean keyUp = true;

    public LampKeyBind(ava[] keyBindings, boolean[] repeatings) {
        super(keyBindings, repeatings);
    }

    public String getLabel() {
        return "Metro2033Lamp";
    }

    public void keyDown(EnumSet var1, ava var2, boolean var3, boolean var4) {
        if (Minecraft.x().s == null) {
            this.keyhasPressed = true;
        }
    }

    public void keyUp(EnumSet var1, ava var2, boolean var3) {
        if (this.keyhasPressed) {
            if (keyPressed) {
                keyPressed = false;
                PacketHandler.LampKeyUnpressed();
                this.keyhasPressed = false;
            } else {
                PacketHandler.LampKeyPressed();
                keyPressed = true;
                this.keyhasPressed = false;
            }
        }
    }

    public EnumSet ticks() {
        return this.tickTypes;
    }
}
