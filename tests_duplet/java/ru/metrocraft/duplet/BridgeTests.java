package ru.metrocraft.duplet;
import com.modularwarfare.api.WeaponAttachmentEvent;
import com.modularwarfare.common.guns.AttachmentPresetEnum;
import com.modularwarfare.common.handler.ServerTickHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import java.util.*;

/** Test doubles are compile/test inputs only. These classes NEVER enter the addon JAR. */
public final class BridgeTests {
 static int tests;
 static void check(boolean value,String msg){if(!value)throw new AssertionError(msg);}
 static void test(String name,Runnable r){ServerTickHandler.playerReloadCooldown.clear();ServerTickHandler.reloadEnhancedTask.clear();Item.fourRegistered=true;r.run();tests++;System.out.println("PASS "+name);}
 static ItemStack item(String id,int n){return new ItemStack(id,n);}
 static EntityPlayer player(boolean four){EntityPlayer p=new EntityPlayer();ItemStack gun=item(four?DupletBridge.FOUR:DupletBridge.BASE,1);NBTTagCompound tag=new NBTTagCompound();tag.setInteger("ammocount",0);tag.setInteger("skinId",7);tag.setString("custom","preserve me");gun.setTagCompound(tag);p.inventory.slots[0]=gun;if(four)attach(gun,"barrel",DupletBridge.MODULE);return p;}
 static ItemStack gun(EntityPlayer p){return p.inventory.slots[0];}
 static String id(EntityPlayer p){return gun(p).getItem().getRegistryName();}
 static void attach(ItemStack g,String slot,String id){g.getTagCompound().setTag("attachment_"+slot,item(id,1).writeToNBT(new NBTTagCompound()));}
 static void load(EntityPlayer p,int slot){WeaponAttachmentEvent.Load e=new WeaponAttachmentEvent.Load(p,gun(p),p.inventory.slots[slot]);DupletBridge.load(e);check(e.isCanceled(),"native operation must be cancelled");}
 static void unload(EntityPlayer p,boolean all){WeaponAttachmentEvent.Unload e=new WeaponAttachmentEvent.Unload(p,gun(p),AttachmentPresetEnum.Barrel,all);DupletBridge.unload(e);check(e.isCanceled(),"native unload must be cancelled");}
 static int total(EntityPlayer p,String id){int n=0;for(ItemStack s:p.inventory.slots)if(!s.isEmpty()&&s.getItem().getRegistryName().equals(id))n+=s.getCount();return n;}
 static String state(EntityPlayer p){return Arrays.toString(p.inventory.slots)+p.inventory.getItemStack();}
 static void fill(EntityPlayer p){for(int i=1;i<36;i++)p.inventory.slots[i]=item("minecraft:stone",64);}
 public static class SrgOnly {public int func_74762_e(String k){return 42;}}
 public static void main(String[] args){
 test("2->4 consumes exactly one module",()->{EntityPlayer p=player(false);p.inventory.slots[1]=item(DupletBridge.MODULE,1);load(p,1);check(id(p).equals(DupletBridge.FOUR),"4 type");check(total(p,DupletBridge.MODULE)==0,"consumed");check(gun(p).getTagCompound().hasKey("attachment_barrel"),"attached");});
 test("4->2 returns exactly one module",()->{EntityPlayer p=player(true);unload(p,false);check(id(p).equals(DupletBridge.BASE),"2 type");check(total(p,DupletBridge.MODULE)==1,"return");check(!gun(p).getTagCompound().hasKey("attachment_barrel"),"removed");});
 test("loaded install refused with exact state conservation",()->{EntityPlayer p=player(false);gun(p).getTagCompound().setInteger("ammocount",2);p.inventory.slots[1]=item(DupletBridge.MODULE,1);String s=state(p);load(p,1);check(s.equals(state(p)),"no mutation");check(p.message.contains("разряди"),"explanation");});
 test("loaded removal refused, including four rounds",()->{EntityPlayer p=player(true);gun(p).getTagCompound().setInteger("ammocount",4);String s=state(p);unload(p,false);check(s.equals(state(p)),"no ammo loss/dup");});
 test("server reload cooldown blocks swap",()->{EntityPlayer p=player(true);ServerTickHandler.playerReloadCooldown.put(p.getUniqueID(),40);String s=state(p);unload(p,false);check(s.equals(state(p)),"unchanged");});
 test("enhanced pending reload task blocks swap",()->{EntityPlayer p=player(true);ServerTickHandler.reloadEnhancedTask.put(p.getUniqueID(),new Object());String s=state(p);unload(p,false);check(s.equals(state(p)),"unchanged");});
 test("other player's reload does not block this player",()->{EntityPlayer p=player(true);ServerTickHandler.reloadEnhancedTask.put(UUID.randomUUID(),new Object());unload(p,false);check(id(p).equals(DupletBridge.BASE),"independent");});
 test("full inventory removal is all-or-nothing",()->{EntityPlayer p=player(true);fill(p);String s=state(p);unload(p,false);check(s.equals(state(p)),"not discarded");check(p.message.contains("место"),"space explanation");});
 test("replacement returns previous module and consumes suppressor",()->{EntityPlayer p=player(true);p.inventory.slots[1]=item(DupletBridge.BASE+"_suppressor",1);load(p,1);check(id(p).equals(DupletBridge.BASE),"2 after replacement");check(total(p,DupletBridge.MODULE)==1,"old returned");check(total(p,DupletBridge.BASE+"_suppressor")==0,"new consumed");});
 test("full inventory replacement can use consumed attachment slot",()->{EntityPlayer p=player(true);fill(p);p.inventory.slots[1]=item(DupletBridge.BASE+"_long_barrel",1);load(p,1);check(id(p).equals(DupletBridge.BASE),"changed");check(total(p,DupletBridge.MODULE)==1,"returned in freed slot");});
 test("cursor install consumes cursor item",()->{EntityPlayer p=player(false);p.inventory.setItemStack(item(DupletBridge.MODULE,1));WeaponAttachmentEvent.Load e=new WeaponAttachmentEvent.Load(p,gun(p),p.inventory.getItemStack());DupletBridge.load(e);check(id(p).equals(DupletBridge.FOUR),"4 type");check(p.inventory.getItemStack().isEmpty(),"cursor consumed");});
 test("cursor replacement with full inventory refuses without consuming",()->{EntityPlayer p=player(true);fill(p);p.inventory.setItemStack(item(DupletBridge.BASE+"_suppressor",1));String s=state(p);DupletBridge.load(new WeaponAttachmentEvent.Load(p,gun(p),p.inventory.getItemStack()));check(s.equals(state(p)),"cursor and gun unchanged");});
 test("unload all returns barrel/grip/stock once",()->{EntityPlayer p=player(true);attach(gun(p),"grip",DupletBridge.BASE+"_grip");attach(gun(p),"stock",DupletBridge.BASE+"_stock");unload(p,true);check(id(p).equals(DupletBridge.BASE),"base");for(String a:new String[]{DupletBridge.MODULE,DupletBridge.BASE+"_grip",DupletBridge.BASE+"_stock"})check(total(p,a)==1,"returned "+a);check(!gun(p).getTagCompound().hasKey("attachment_grip"),"grip removed");});
 test("unload-all full inventory has no partial removals",()->{EntityPlayer p=player(true);attach(gun(p),"stock",DupletBridge.BASE+"_stock");fill(p);p.inventory.slots[1]=item("",0);String s=state(p);unload(p,true);check(s.equals(state(p)),"two returns need two slots");});
 test("unknown attachment refused",()->{EntityPlayer p=player(false);p.inventory.slots[1]=item("modularwarfare:other",1);String s=state(p);load(p,1);check(s.equals(state(p)),"unknown unchanged");});
 test("unowned attachment object refused",()->{EntityPlayer p=player(false);String s=state(p);DupletBridge.load(new WeaponAttachmentEvent.Load(p,gun(p),item(DupletBridge.MODULE,1)));check(s.equals(state(p)),"no free attachment");});
 test("other guns not intercepted",()->{EntityPlayer p=player(false);p.inventory.slots[0]=item("modularwarfare:metro_uboinik",1);WeaponAttachmentEvent.Load e=new WeaponAttachmentEvent.Load(p,gun(p),item(DupletBridge.MODULE,1));DupletBridge.load(e);check(!e.isCanceled(),"other gun native");});
 test("NBT and metadata survive item type switch",()->{EntityPlayer p=player(false);gun(p).setItemDamage(9);NBTTagCompound wanted=gun(p).getTagCompound().copy();p.inventory.slots[1]=item(DupletBridge.MODULE,1);load(p,1);check(gun(p).getItemDamage()==9,"damage");NBTTagCompound result=gun(p).getTagCompound().copy();result.removeTag("attachment_barrel");check(result.equals(wanted),"full tag equality");});
 test("missing 4-round registered item refuses without consuming",()->{EntityPlayer p=player(false);p.inventory.slots[1]=item(DupletBridge.MODULE,1);String s=state(p);Item.fourRegistered=false;load(p,1);check(s.equals(state(p)),"no loss");});
 test("client-side event cannot mutate inventory",()->{EntityPlayer p=player(true);p.world.isRemote=true;String s=state(p);unload(p,false);check(s.equals(state(p)),"server authority");});
 test("switching one player's gun does not change another",()->{EntityPlayer p=player(false),q=player(false);p.inventory.slots[1]=item(DupletBridge.MODULE,1);load(p,1);check(id(q).equals(DupletBridge.BASE),"no global capacity mutation");});
 test("MCP-to-SRG method fallback",()->{try{check(DupletBridge.call(new SrgOnly(),"getInteger|func_74762_e","x").equals(42),"SRG fallback");}catch(Exception e){throw new RuntimeException(e);}});
 test("repeated attach/detach conserves module",()->{EntityPlayer p=player(false);p.inventory.slots[1]=item(DupletBridge.MODULE,1);for(int n=0;n<50;n++){int at=-1;for(int i=1;i<36;i++)if(!p.inventory.slots[i].isEmpty()&&p.inventory.slots[i].getItem().getRegistryName().equals(DupletBridge.MODULE))at=i;load(p,at);unload(p,false);}check(total(p,DupletBridge.MODULE)==1,"exactly one module after 50 cycles");});
 test("mid-commit inventory exception rolls every slot back",()->{EntityPlayer p=player(false);p.inventory.slots[1]=item(DupletBridge.MODULE,1);String before=state(p);p.inventory.failOnceAt=1;load(p,1);check(before.equals(state(p)),"rollback");});
 test("malformed stored attachment count is refused",()->{EntityPlayer p=player(true);gun(p).getTagCompound().setTag("attachment_barrel",item(DupletBridge.MODULE,2).writeToNBT(new NBTTagCompound()));String before=state(p);unload(p,false);check(before.equals(state(p)),"no invalid stack insertion");});
 test("serialized ForgeCaps survive conversion",()->{EntityPlayer p=player(false);NBTTagCompound serialized=gun(p).writeToNBT(new NBTTagCompound()),caps=new NBTTagCompound();caps.setString("test:capability","opaque state");serialized.setTag("ForgeCaps",caps);p.inventory.slots[0]=new ItemStack(serialized);p.inventory.slots[1]=item(DupletBridge.MODULE,1);load(p,1);check(gun(p).writeToNBT(new NBTTagCompound()).getCompoundTag("ForgeCaps").equals(caps),"caps preserved");});
 System.out.println("Bridge tests: "+tests+" passed against selected JAR WeaponAttachmentEvent classes (mock game/Forge ABI). Not a Minecraft runtime test.");
 }
}
