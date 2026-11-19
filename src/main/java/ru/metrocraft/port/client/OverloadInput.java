package ru.metrocraft.port.client;

import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.inventory.CarryWeight;

/** Client prediction agrees with the server's zero-speed attribute at +10 kg. */
@Mod.EventBusSubscriber(modid=MetroPort.ID,value=Side.CLIENT)
public final class OverloadInput {
    @SubscribeEvent public static void input(InputUpdateEvent event){
        if(!CarryWeight.immobilized(event.getEntityPlayer()))return;
        event.getMovementInput().moveForward=0;event.getMovementInput().moveStrafe=0;event.getMovementInput().jump=false;
        event.getEntityPlayer().setSprinting(false);
    }
}
