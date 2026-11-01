package ru.metrocraft.port;

/** User-approved reduced build. Firearms are supplied by ModularWarfare-Shining, not this mod. */
public final class ContentPolicy {
    private ContentPolicy(){}
    public static final boolean FIREARMS=false;
    public static boolean definition(Definition d){
        return !"gun".equals(d.kind) && !"ammo".equals(d.kind) && !"attachment".equals(d.kind) && !d.repairGun;
    }
    public static boolean exodus(String id,String kind){
        return !"decor".equals(kind) && !"exodus_gasmask_gold".equals(id) && !"exodus_gasmask_diamond".equals(id);
    }
}
