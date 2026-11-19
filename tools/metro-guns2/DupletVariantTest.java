package ru.metrocraft.mwf;
import net.minecraft.item.*;
import com.modularwarfare.common.guns.ItemGun;
import net.minecraft.nbt.NBTTagCompound;
/** Execute patched native ammo + existing anti-dupe regressions on isolated API facades. */
public final class DupletVariantTest {
 public static void main(String[] args)throws Exception {
  MechanicsTest.main(args);
  for(String id:new String[]{"metro_duplet","metro_duplet_2"}) {
   MechanicsTest.check(MechanicsRules.duplet(id),"both independent Duplet IDs are scoped");
   for(int rounds=0;rounds<=4;rounds++){
    MechanicsTest.Player p=MechanicsTest.player(id,0);ItemStack s=p.getHeldItemMainhand();
    MechanicsTest.loaded(s,rounds);s.tag.setString("custom","variant-NBT");MechanicsTest.attach(s,"metro_duplet_extra_barrels");
    MechanicsTest.equal(ItemGun.getAmmoCount(s),rounds,"native count uses loose rounds");
    MechanicsTest.equal(MechanicsHooks.capacity(((ItemGun)s.item).type,s),4,"quad capacity on both IDs");
    MechanicsTest.equal(MechanicsHooks.initialAmmoCount(((ItemGun)s.item).type,s),0,"no free initial rounds");
    AttachmentTransactions.transact(p,s,null,"barrel",false);s=p.getHeldItemMainhand();
    MechanicsTest.check(id.equals(MechanicsHooks.id(s)),"attachment transaction never changes registry ID");
    MechanicsTest.equal(ItemGun.getAmmoCount(s),Math.min(2,rounds),"native cap on detached quad");
    MechanicsTest.equal(MechanicsTest.inventoryCount(p,"metro_12g_buckshot"),Math.max(0,rounds-2),"exact excess refund");
    MechanicsTest.check("variant-NBT".equals(s.tag.getString("custom")),"custom NBT survives");
   }
   MechanicsTest.Player p=MechanicsTest.player(id,0);ItemStack s=p.getHeldItemMainhand();MechanicsTest.loaded(s,2);
   ItemGun.consumeShot(s);MechanicsTest.equal(ItemGun.getAmmoCount(s),1,"native consumption exactly once");
   MechanicsTest.check(!MechanicsHooks.beforeShot(p,new MechanicsTest.Packet(id.equals("metro_duplet")?"metro_duplet_2":"metro_duplet",1)),"cross-variant spoof refused");
  }
  System.out.println("GUNS2_TESTS_OK "+MechanicsTest.checks+" isolated assertions; NOT Minecraft runtime testing");
 }
}
