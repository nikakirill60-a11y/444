package ru.metrocraft.port.inventory;

import net.minecraft.item.ItemStack;

/** Stable names, independent of vanilla's four-tier enchantment rarity. */
public enum ItemRarity {
    COMMON(0x999999,"7"), UNCOMMON(0x65ba58,"a"), RARE(0x63b8ed,"b"),
    EPIC(0xb378e5,"5"), LEGENDARY(0xf0ce50,"e"), MYTHIC(0xe45b55,"c"), EXCLUSIVE(0xf27fbb,"d");
    public final int color;public final String format;
    ItemRarity(int color,String code){this.color=color;this.format="\u00a7"+code;}
    public static ItemRarity parse(String value){
        try{return valueOf(value.toUpperCase(java.util.Locale.ROOT));}catch(IllegalArgumentException e){return COMMON;}
    }
    public static ItemRarity of(ItemStack stack){
        if(stack.isEmpty())return COMMON;
        if(stack.hasTagCompound() && stack.getTagCompound().hasKey("MetroRarity",8))return parse(stack.getTagCompound().getString("MetroRarity"));
        return parse(ItemMetrics.profile(stack).rarity);
    }
    public String key(){return "metroport.rarity."+name().toLowerCase(java.util.Locale.ROOT);}
}
