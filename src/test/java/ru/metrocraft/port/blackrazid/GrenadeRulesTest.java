package ru.metrocraft.port.blackrazid;
import net.minecraft.init.Bootstrap;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.*;
import static org.junit.Assert.*;
import ru.metrocraft.port.blackrazid.grenades.*;

public class GrenadeRulesTest {
    @BeforeClass public static void boot(){Bootstrap.register();}
    @Test public void nativeMaxDamageDiffersForOrdinaryAndBig(){assertEquals(10,GrenadeRules.damage(false,0,1));assertEquals(15,GrenadeRules.damage(true,0,1));}
    @Test public void coverAndDistanceAttenuateDamage(){assertEquals(5,GrenadeRules.damage(false,5.5,1));assertEquals(2,GrenadeRules.damage(false,5.5,.5));assertEquals(0,GrenadeRules.damage(true,0,0));assertEquals(0,GrenadeRules.damage(true,11,1));}
    @Test public void invalidDamageInputsCannotCreateExcessOrNegativeDamage(){assertEquals(0,GrenadeRules.damage(false,Double.NaN,1));assertEquals(0,GrenadeRules.damage(true,0,Double.POSITIVE_INFINITY));assertEquals(0,GrenadeRules.damage(true,-1,1));assertEquals(15,GrenadeRules.damage(true,0,200));assertEquals(0,GrenadeRules.damage(true,0,-2));}
    @Test public void nativeFuseAndStackLimit(){assertEquals(101,GrenadeRules.FUSE);assertEquals(5,new GrenadeItem(false).getItemStackLimit());assertEquals(5,new GrenadeItem(true).getItemStackLimit());assertEquals(0,GrenadeRules.age(-5));assertEquals(101,GrenadeRules.age(999));}
    @Test public void savedFuseAndVariantAreNotResetOnReload(){
        GrenadeEntity entity=new GrenadeEntity(null);NBTTagCompound data=new NBTTagCompound();data.setInteger("BlackrazidAge",73);data.setBoolean("BlackrazidBig",true);entity.readEntityFromNBT(data);
        NBTTagCompound saved=new NBTTagCompound();entity.writeEntityToNBT(saved);assertTrue(entity.isBig());assertTrue(saved.getBoolean("BlackrazidBig"));assertEquals(73,saved.getInteger("BlackrazidAge"));
    }
}
