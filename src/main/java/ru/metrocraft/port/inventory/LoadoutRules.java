package ru.metrocraft.port.inventory;

import net.minecraft.item.*;

/** Slots 1/2/3 are actual vanilla hotbar slots: MWF keeps its original held-stack/NBT lifecycle. */
public final class LoadoutRules {
    private LoadoutRules(){}
    public static boolean firearm(ItemStack stack){
        if(stack.isEmpty())return false;
        for(Class<?> c=stack.getItem().getClass();c!=null;c=c.getSuperclass())
            if(c.getName().equals("com.modularwarfare.common.guns.ItemGun"))return true;
        String id=ItemMetrics.id(stack);
        return id.startsWith("modularwarfare:") && stack.getItem().getClass().getSimpleName().equals("ItemGun");
    }
    public static boolean melee(ItemStack stack){return !stack.isEmpty() && (stack.getItem() instanceof ItemSword || ItemMetrics.id(stack).contains("machete"));}
    public static boolean accepts(int index,ItemStack stack){
        if(stack.isEmpty())return true;
        return index<2?firearm(stack):index==2?melee(stack):index>=9||!firearm(stack)&&!melee(stack);
    }
    public static int limit(int index,ItemStack stack){return index<3?1:stack.getMaxStackSize();}
}
