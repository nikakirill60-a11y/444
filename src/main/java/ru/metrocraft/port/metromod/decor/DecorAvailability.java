package ru.metrocraft.port.metromod.decor;

import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.metrocraft.port.MetroPort;

/** Old block/tile IDs remain readable in saved worlds; only the six switches are offered now. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class DecorAvailability {
    public static boolean published(OriginalGateProfile profile){return profile.is("Lever");}
    public static boolean blocked(ItemStack stack){
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemBlock))return false;
        Block block=Block.getBlockFromItem(stack.getItem());
        return block instanceof ru.metrocraft.port.gates.GateBlock || block instanceof OriginalGateBlock && !published(((OriginalGateBlock)block).profile);
    }
    @SubscribeEvent public static void place(PlayerInteractEvent.RightClickBlock event){
        if(blocked(event.getItemStack())){event.setCanceled(true);event.setCancellationResult(EnumActionResult.FAIL);}
    }
    public static boolean flipped(OriginalGateProfile profile){
        return profile.is("Lever") && profile.source!=null && !profile.source.endsWith("_01");
    }
    private DecorAvailability(){}
}
