package ru.metrocraft.port.metromod;

import net.minecraft.item.ItemStack;
import ru.metrocraft.port.MetroArmor;

/** Common-side selectors only: no graphics context, equipment mutations or new mask durability rules. */
public final class OverlayRules {
    private OverlayRules() {}
    public static boolean visible(boolean world,boolean firstPerson,boolean alive,boolean spectator,boolean hidden,boolean screen){
        return world && firstPerson && alive && !spectator && !hidden && !screen;
    }
    public static boolean gasMask(ItemStack stack){
        return !stack.isEmpty() && stack.getItem() instanceof MetroArmor && ((MetroArmor)stack.getItem()).isGasMask();
    }
    public static boolean mask(ItemStack head,ItemStack chest){return gasMask(head)||gasMask(chest);}
    public static float noiseU(int ticks){return Math.floorMod(ticks,128)/128F;}
    public static float noiseV(int ticks){return Math.floorMod(ticks,127)/127F;}
}
