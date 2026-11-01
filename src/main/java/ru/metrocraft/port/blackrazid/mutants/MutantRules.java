package ru.metrocraft.port.blackrazid.mutants;

import net.minecraft.util.math.Vec3d;

/** Source 1.5.2 gaze predicate, without general ExtendetPlayer data or shared mob state. */
public final class MutantRules {
    private MutantRules(){}
    public static boolean looking(Vec3d look,Vec3d offset,boolean visible){
        if(!visible || !finite(look) || !finite(offset))return false;
        double distance=offset.length();
        if(distance<1e-8 || distance>46 || look.lengthSquared()<1e-12)return false;
        return look.normalize().dotProduct(offset.scale(1/distance))>1-.025/distance;
    }
    private static boolean finite(Vec3d v){return v!=null && Double.isFinite(v.x) && Double.isFinite(v.y) && Double.isFinite(v.z);}
    public static boolean librarianSlows(float health,boolean looking){return Float.isFinite(health) && health>125 && looking;}
    public static boolean spiderFears(float health,float energy,boolean lamp,boolean looking,int panic){
        return Float.isFinite(health) && health>0 && Float.isFinite(energy) && energy>10 && lamp && looking && panic<=0;
    }
    public static int panic(int value){return Math.max(0,Math.min(60,value));}
}
