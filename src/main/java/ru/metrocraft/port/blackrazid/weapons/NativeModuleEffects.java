package ru.metrocraft.port.blackrazid.weapons;

import net.minecraft.item.ItemStack;
import static ru.metrocraft.port.blackrazid.weapons.BlackrazidModules.has;

/** Values follow actual branches in the recovered 22 gun sources, not MCMOD upgrades or invented global bonuses. */
public final class NativeModuleEffects {
    public float damage,speed,recoil,volume=1;
    public int accuracy,pellets,delay,ammoCost=1;
    public String sound;
    public NativeModuleEffects(BlackrazidWeapons.Entry e,ItemStack gun,int pressure){
        damage=e.damage;speed=e.speed;recoil=e.recoil;accuracy=e.accuracy;pellets=Math.max(1,e.pellets);delay=e.delay;sound=e.shootSound;
        switch(e.sourceId){
            case "AK":case "AK2012":case "Ubludok":case "Revolver":case "Podonok":
                if(has(gun,2,"Glushak"))sound="blackrazid_vsv_shootclose";
                break;
            case "Vsv":if(has(gun,2,"VsvGlushak"))sound="blackrazid_vsv_shootclose";break;
            case "Saiga":case "Ashot":case "Uboinik":
                if(has(gun,2,"Glushak")){sound="blackrazid_saiga_closeshoot";volume=e.sourceId.equals("Saiga")?.1F:e.sourceId.equals("Uboinik")?.3F:1;}
                if(has(gun,2,"UboinikBarrel"))accuracy=70;
                break;
            case "Duplet":
                if(has(gun,2,"DupletButt") || has(gun,3,"DupletPriklad"))recoil=1;
                if(!BlackrazidModules.get(gun,1).isEmpty())speed=4;
                if(has(gun,1,"DupletGlushak")){sound="blackrazid_saiga_closeshoot";volume=.5F;}
                if(has(gun,1,"DupletBarrel"))accuracy=70;
                if(has(gun,1,"QuadBarrel")){pellets=16;ammoCost=2;}
                break;
            case "Abzac":
                if(has(gun,0,"AutoFire")){delay=4;sound="blackrazid_abzac_fastshoot";}
                if(has(gun,2,"AbzacGlushak"))recoil=2;
                break;
            case "Railgun":if(has(gun,2,"RailgunKon"))delay=8;break;
            case "Gatling":
                delay=(pressure>266?3:pressure>133?4:5)-(has(gun,0,"GatlingBarrel")?1:0);
                recoil=has(gun,1,"GatlingButt")?(has(gun,2,"GatlingGiro")?.3F:.5F):(has(gun,2,"GatlingGiro")?.4F:.6F);
                break;
            case "Preved":if(has(gun,2,"VentilGlushak"))recoil=.8F;break;
            case "Ventil":if(has(gun,2,"VentilGlushak"))recoil=.4F;break;
            default:break;
        }
        if(e.sourceId.equals("Ubludok") && has(gun,3,"UbludokButt"))recoil=.4F;
        if(e.sourceId.equals("Revolver") && has(gun,3,"Butt"))recoil=.4F;
        if(e.sourceId.equals("Podonok")){
            if(has(gun,3,"PodonokButt"))recoil=.4F;
            if(has(gun,4,"PodonokGas"))delay=3;
        }
        if(has(gun,2,"Barrel")){
            damage+=1;if(e.sourceId.equals("Preved"))speed=10;
        }
    }
}
