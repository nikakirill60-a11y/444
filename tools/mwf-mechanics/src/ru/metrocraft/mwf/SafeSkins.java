package ru.metrocraft.mwf;

import com.modularwarfare.common.guns.SkinType;

/** Pure render fallback: stale skinId values must not crash GUI, held gun or inventory rendering.
 * Does not rewrite NBT on the client or change valid skin selections.
 */
public final class SafeSkins {
    private static final SkinType MISSING=new SkinType();
    static {MISSING.internalName="missing";MISSING.skinAsset="missing";}
    private SafeSkins() {}
    public static SkinType select(SkinType[] skins,int index) {
        if(skins!=null) {
            if(index>=0&&index<skins.length&&skins[index]!=null)return skins[index];
            for(SkinType skin:skins)if(skin!=null)return skin;
        }
        // Bad/empty content definitions show Minecraft's missing texture instead of crashing.
        return MISSING;
    }
}
