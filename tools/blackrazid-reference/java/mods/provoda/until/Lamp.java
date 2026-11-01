/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.Player
 *  mods.provoda.until.ExtendetPlayer
 *  net.minecraftforge.event.ForgeSubscribe
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingUpdateEvent
 *  sq
 */
package mods.provoda.until;

import cpw.mods.fml.common.network.Player;
import mods.provoda.ModBase;
import mods.provoda.until.ExtendetPlayer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingEvent;

public class Lamp {
    int hitTimer;
    int keyTimer;

    @ForgeSubscribe
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent var1) {
        if (var1.entityLiving instanceof sq) {
            sq var2 = (sq)var1.entityLiving;
            ExtendetPlayer var3 = ExtendetPlayer.get((sq)var2);
            Player player1 = (Player)var2;
            if (var2.q.I || !var3.getLampKeyP() || (var2.bK.h() == null || var2.bK.h().b() != ModBase.Lamp) && (var2.bL.a(14).c() == null || var2.bL.a(14).c().b() != ModBase.Lamp) || var3.energyLeft()) {
                // empty if block
            }
        }
    }
}
