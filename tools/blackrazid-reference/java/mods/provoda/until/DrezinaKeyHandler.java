/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ava
 *  bdv
 *  cpw.mods.fml.client.FMLClientHandler
 *  cpw.mods.fml.client.registry.KeyBindingRegistry$KeyHandler
 *  cpw.mods.fml.common.TickType
 */
package mods.provoda.until;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.TickType;
import java.util.EnumSet;
import mods.provoda.entity.EntityDrezinaBase;
import mods.provoda.until.PacketHandler;

public class DrezinaKeyHandler
extends KeyBindingRegistry.KeyHandler {
    public static ava ReverseUp = new ava("\u0420\u0430\u0437\u0433\u043e\u043d \u0434\u0440\u0435\u0437\u0438\u043d\u044b", 72);
    public static ava ReverseDown = new ava("\u0417\u0430\u0434\u043d\u0438\u0439 \u0445\u043e\u0434 \u0434\u0440\u0435\u0437\u0438\u043d\u044b", 80);
    public static ava ReverseZero = new ava("\u0422\u043e\u0440\u043c\u043e\u0437 \u0434\u0440\u0435\u0437\u0438\u043d\u044b", 76);

    public DrezinaKeyHandler() {
        super(new ava[]{ReverseUp, ReverseDown, ReverseZero}, new boolean[]{true, true, false});
    }

    public String getLabel() {
        return "Drezina Key's";
    }

    public EnumSet ticks() {
        return EnumSet.of(TickType.CLIENT);
    }

    public void keyDown(EnumSet types, ava kb, boolean tickEnd, boolean isRepeat) {
        if (FMLClientHandler.instance().getClient().e != null && FMLClientHandler.instance().getClient().g != null) {
            bdv player = FMLClientHandler.instance().getClient().g;
            if (player.o instanceof EntityDrezinaBase) {
                EntityDrezinaBase how = (EntityDrezinaBase)player.o;
                boolean sendpacket = false;
                if (kb.equals(ReverseUp) && !how.zeroReverse) {
                    if (how.reverse < 127 && !how.zeroReverse) {
                        how.reverse = (byte)(how.reverse + 1);
                        PacketHandler.SpeedUpPress();
                    }
                } else if (kb.equals(ReverseDown) && !how.zeroReverse && how.reverse > -127) {
                    how.reverse = (byte)(how.reverse - 1);
                    PacketHandler.SpeedDownPress();
                }
                if (ReverseZero.c()) {
                    how.zeroReverse = true;
                    PacketHandler.SpeedStopPress();
                }
            }
        }
    }

    public void keyUp(EnumSet types, ava kb, boolean tickEnd) {
    }
}
