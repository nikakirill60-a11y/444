/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.Player
 *  mg
 *  mods.provoda.until.ExtendetPlayer
 *  net.minecraftforge.event.ForgeSubscribe
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingUpdateEvent
 *  sq
 */
package mods.provoda.until;

import cpw.mods.fml.common.network.Player;
import mods.provoda.until.ExtendetPlayer;
import mods.provoda.until.PacketHandler;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingEvent;

public class Thirst {
    int hitTimer;

    @ForgeSubscribe
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent var1) {
        if (var1.entityLiving instanceof sq) {
            sq var2 = (sq)var1.entityLiving;
            ExtendetPlayer var3 = ExtendetPlayer.get((sq)var2);
            Player player1 = (Player)var2;
            if (!var2.q.I) {
                if (var3.thirsted()) {
                    PacketHandler.sendGetWater(player1, var3.thirst());
                } else if (var3.ThirstTimerSet()) {
                    var2.a(mg.f, 1);
                }
            }
        }
    }
}
