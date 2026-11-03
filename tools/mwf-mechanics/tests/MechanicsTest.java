package ru.metrocraft.mwf;

import java.util.*;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import com.modularwarfare.common.guns.ItemGun;
import com.modularwarfare.common.handler.ServerTickHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;

/** Isolated hook/transaction tests with explicit compile-time Minecraft facades, NOT a game test. */
public final class MechanicsTest {
    static int checks;
    static void check(boolean value,String label){checks++;if(!value)throw new AssertionError(label);}
    static void equal(int actual,int expected,String label){check(actual==expected,label+": "+actual+" != "+expected);}
    public static final class Type {public Integer internalAmmoStorage=2;public String internalName;public int roundsPerMin=150,numBullets=1;Type(String id){internalName=id;}}
    public static final class World {public boolean isRemote;public long tick=100;public long getTotalWorldTime(){return tick;}}
    public static final class Inventory {
        public int currentItem;public ItemStack[] slots=new ItemStack[41];public ItemStack cursor=ItemStack.EMPTY;
        public Inventory(){Arrays.fill(slots,ItemStack.EMPTY);}public ItemStack getStackInSlot(int i){return slots[i];}
        public void setInventorySlotContents(int i,ItemStack s){slots[i]=s;}public ItemStack getItemStack(){return cursor;}
        public void setItemStack(ItemStack s){cursor=s;}public void markDirty(){}
    }
    public static final class Container {public List<Object> inventorySlots=new ArrayList<Object>();public Container(){for(int i=0;i<46;i++)inventorySlots.add(new Object());}}
    public static final class Player extends net.minecraft.entity.player.EntityPlayerMP {
        public World world=new World();public Inventory inventory=new Inventory();public Container inventoryContainer=new Container();
        public int ping=20,dimension;public double posX,posY,posZ;public boolean alive=true,spectator;public int syncs;public String message;
        public UUID uuid=UUID.randomUUID();public UUID getUniqueID(){return uuid;}public ItemStack getHeldItemMainhand(){return inventory.slots[inventory.currentItem];}
        public boolean isEntityAlive(){return alive;}public boolean isSpectator(){return spectator;}public void sendSlotContents(Container c,int slot,ItemStack s){syncs++;}
        public void sendStatusMessage(Object text,boolean actionbar){message=text.toString();}public Object getPosition(){return null;}
    }
    public static final class Packet {public String internalname;public int entityId;Packet(String id,int target){internalname=id;entityId=target;}}
    static ItemStack gun(String id,int energy) {
        ItemStack s=new ItemStack(new ItemGun(id,new Type(id)));s.tag=new NBTTagCompound();s.tag.setInteger("ammocount",4);s.tag.setInteger(MechanicsHooks.ENERGY,energy);return s;
    }
    static Player player(String id,int energy) {Player p=new Player();p.inventory.slots[0]=gun(id,energy);return p;}
    static ItemStack module(String id){return new ItemStack(new Item("modularwarfare:"+id,64));}
    static void attach(ItemStack s,String id){s.tag.setTag("attachment_barrel",module(id).writeToNBT(new NBTTagCompound()));}
    static void pumpTests() throws Exception {
        Player p=player("metro_tihar",0);ItemStack g=p.getHeldItemMainhand();
        check(!MechanicsHooks.beforeShot(p,new Packet("metro_tihar",0)),"empty pressure rejects shot");
        check(!MechanicsHooks.allowLegacy(p,g),"legacy/basic packet cannot bypass pressure authorization");
        check(MechanicsHooks.pump(p,0),"first pump accepted");equal(MechanicsHooks.energy(g),20,"pump adds 20");
        check(!MechanicsHooks.pump(p,0),"same-tick spam rejected");check(!MechanicsHooks.beforeShot(p,new Packet("metro_tihar",0)),"cannot fire during pumping");
        p.world.tick+=10;check(MechanicsHooks.beforeShot(p,new Packet("metro_tihar",0)),"charged shot accepted");
        equal(MechanicsHooks.energy(g),12,"one logical shot spends eight");
        check(!MechanicsHooks.beforeShot(p,new Packet("metro_tihar",0)),"same-tick fire spam rejected");
        check(MechanicsHooks.beforeHit(p,new Packet("metro_tihar",42)),"authorized target hit accepted");
        check(!MechanicsHooks.beforeHit(p,new Packet("metro_tihar",42)),"single projectile cannot damage same target twice");
        equal(MechanicsHooks.energy(g),12,"hit does not spend energy again");
        check(MechanicsHooks.scaleDamage(10,p)>0&&MechanicsHooks.scaleDamage(10,p)<10,"low pressure reduces damage");
        p.world.tick+=6;check(!MechanicsHooks.beforeHit(p,new Packet("metro_tihar",43)),"expired hit rejected");
        check(!MechanicsHooks.pump(p,1),"stale hotbar slot rejected");
        ServerTickHandler.playerReloadCooldown.put(p.uuid,1);p.world.tick+=20;
        check(!MechanicsHooks.pump(p,0),"reload blocks pump");check(!MechanicsHooks.beforeShot(p,new Packet("metro_tihar",0)),"reload blocks fire");
        ServerTickHandler.playerReloadCooldown.clear();p.alive=false;check(!MechanicsHooks.pump(p,0),"dead player cannot pump");p.alive=true;p.spectator=true;
        check(!MechanicsHooks.pump(p,0),"spectator cannot pump");p.spectator=false;p.world.isRemote=true;check(!MechanicsHooks.pump(p,0),"server handler rejects remote world");
        Player zero=player("metro_tihar",100);check(!MechanicsHooks.beforeHit(zero,new Packet("metro_tihar",1)),"hit packet cannot authorize itself");
        check(!MechanicsHooks.beforeShot(zero,new Packet("metro_helsing",1)),"weapon-ID spoof refused");
        check(!MechanicsHooks.beforeHit(zero,new Packet("metro_helsing",1)),"hit weapon-ID spoof refused");
        Player normal=player("metro_ak",0);check(MechanicsHooks.beforeShot(normal,new Packet("metro_ak",1)),"unrelated gun untouched");
        check(MechanicsHooks.beforeHit(normal,new Packet("metro_ak",1)),"unrelated hit untouched");
        check(MechanicsHooks.allowLegacy(normal,normal.getHeldItemMainhand()),"unrelated legacy path untouched");
        Player negative=player("metro_helsing",-99);equal(MechanicsHooks.energy(negative.getHeldItemMainhand()),0,"negative pressure clamped");
        Player huge=player("metro_tihar",Integer.MAX_VALUE);equal(MechanicsHooks.energy(huge.getHeldItemMainhand()),120,"NBT pressure cannot exceed maximum");
        Player noammo=player("metro_tihar",100);noammo.getHeldItemMainhand().tag.setInteger("ammocount",0);
        check(!MechanicsHooks.beforeShot(noammo,new Packet("metro_tihar",1)),"no-ammo shot rejected");equal(MechanicsHooks.energy(noammo.getHeldItemMainhand()),100,"rejected shot spends no pressure");
        Player swapped=player("metro_tihar",100);check(MechanicsHooks.beforeShot(swapped,new Packet("metro_tihar",1)),"shot before weapon switch");
        swapped.inventory.slots[0]=gun("metro_tihar",100);check(!MechanicsHooks.beforeHit(swapped,new Packet("metro_tihar",1)),"different stack cannot reuse authorization");
    }
    static void capacityTests() throws Exception {
        ItemStack base=gun("metro_duplet",0),quad=gun("metro_duplet",0);attach(quad,"metro_duplet_extra_barrels");
        Object type=((ItemGun)base.item).type;
        equal(MechanicsHooks.capacity(type,base),2,"base capacity");equal(MechanicsHooks.capacity(type,quad),4,"quad capacity");
        equal(((Type)type).internalAmmoStorage,2,"shared GunType unchanged");
        equal(MechanicsHooks.capacity(type,base),2,"second player remains two-round");
        quad.tag.removeTag("attachment_barrel");equal(MechanicsHooks.capacity(type,quad),2,"removal restores two rounds");
        attach(quad,"metro_duplet_suppressor");equal(MechanicsHooks.capacity(type,quad),2,"suppressor not mistaken for quad");
        Object rifleType=((ItemGun)gun("metro_ak",0).item).type;((Type)rifleType).internalAmmoStorage=30;
        equal(MechanicsHooks.capacity(rifleType,quad),30,"non-Duplet baseline preserved");
    }
    static void transactions() throws Exception {
        Player p=player("metro_duplet",0);p.getHeldItemMainhand().tag.setInteger("ammocount",0);
        p.getHeldItemMainhand().tag.setString("customName","preserved");p.inventory.slots[1]=module("metro_duplet_extra_barrels");
        AttachmentTransactions.transact(p,p.getHeldItemMainhand(),p.inventory.slots[1],null,false);
        check(p.inventory.slots[1].isEmpty(),"quad item consumed exactly once");
        check(MechanicsHooks.attachment(p.getHeldItemMainhand(),"metro_duplet_extra_barrels"),"quad installed");
        check("preserved".equals(p.getHeldItemMainhand().tag.getString("customName")),"unknown gun NBT preserved");
        check("metro_duplet".equals(MechanicsHooks.id(p.getHeldItemMainhand())),"no duplicate weapon ID");
        AttachmentTransactions.transact(p,p.getHeldItemMainhand(),null,"barrel",false);
        check(!MechanicsHooks.attachment(p.getHeldItemMainhand(),"metro_duplet_extra_barrels"),"quad removed");
        check("metro_duplet_extra_barrels".equals(MechanicsHooks.id(p.inventory.slots[1])),"module returned");
        p.getHeldItemMainhand().tag.setInteger("ammocount",4);
        boolean refused=false;try{AttachmentTransactions.transact(p,p.getHeldItemMainhand(),p.inventory.slots[1],null,false);}catch(AttachmentTransactions.Refusal e){refused=true;}
        check(refused,"loaded weapon modification refused without losing shells");
        equal(p.getHeldItemMainhand().tag.getInteger("ammocount"),4,"loaded shells unchanged");
        p.getHeldItemMainhand().tag.setInteger("ammocount",0);
        for(int i=1;i<36;i++)p.inventory.slots[i]=new ItemStack(new Item("test:stone",64),64);
        attach(p.getHeldItemMainhand(),"metro_duplet_extra_barrels");NBTTagCompound before=p.getHeldItemMainhand().writeToNBT(new NBTTagCompound());
        refused=false;try{AttachmentTransactions.transact(p,p.getHeldItemMainhand(),null,"barrel",false);}catch(AttachmentTransactions.Refusal e){refused=true;}
        check(refused,"full inventory refuses module loss");check(before.equals(p.getHeldItemMainhand().writeToNBT(new NBTTagCompound())),"refused transaction leaves all NBT intact");
        p.inventory.slots[1]=ItemStack.EMPTY;p.inventory.cursor=module("metro_duplet_suppressor");
        AttachmentTransactions.transact(p,p.getHeldItemMainhand(),p.inventory.cursor,null,false);
        check(p.inventory.cursor.isEmpty(),"cursor module consumed once");check(MechanicsHooks.attachment(p.getHeldItemMainhand(),"metro_duplet_suppressor"),"cursor replacement installed");
        check("metro_duplet_extra_barrels".equals(MechanicsHooks.id(p.inventory.slots[1])),"replaced quad returned atomically");
    }
    static void leakAndRail() throws Exception {
        equal(MechanicsRules.leak(120,40),118,"overpressure leaks once per second");equal(MechanicsRules.leak(120,10000),100,"leak stops at normal pressure");
        equal(MechanicsRules.leak(60,10000),60,"normal pressure retained");equal(MechanicsRules.leak(120,-100),120,"clock rollback cannot increase loss");
        Player p=player("metro_tihar",120);p.getHeldItemMainhand().tag.setLong(MechanicsHooks.LEAK,60);
        net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent ev=new net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent();ev.player=p;
        new MechanicsHooks().tick(ev);equal(MechanicsHooks.energy(p.getHeldItemMainhand()),118,"inventory energy tick leaks excess");
        Player rail=player("metro_railgun",0);attach(rail.getHeldItemMainhand(),"metro_mod_railgun_akk");equal(MechanicsHooks.maximum(rail.getHeldItemMainhand()),150,"battery expands energy by 50 percent");
        attach(rail.getHeldItemMainhand(),"metro_mod_railgun_kon");equal(MechanicsHooks.maximum(rail.getHeldItemMainhand()),100,"capacitor not a capacity upgrade");
        check(MechanicsRules.damage("metro_railgun",100,9,true)>MechanicsRules.damage("metro_railgun",100,9,false),"capacitor accumulates full shot sooner");
        check(Math.abs(MechanicsRules.damage("metro_railgun",100,9,true)-1f)<.0001,"upgraded full charge at nine ticks");
        Player twoRails=player("metro_railgun",100);ItemStack first=twoRails.getHeldItemMainhand();
        check(MechanicsHooks.beforeShot(twoRails,new Packet("metro_railgun",1)),"first rail shot");
        twoRails.world.tick+=8;twoRails.inventory.slots[0]=gun("metro_railgun",100);
        check(MechanicsHooks.beforeShot(twoRails,new Packet("metro_railgun",1)),"second rail shot after player cooldown");
        check(Math.abs(MechanicsHooks.scaleDamage(10,twoRails)-10f)<.0001,"independent rail stack has independent accumulator");
        Player saved=player("metro_railgun",0);saved.inventory.slots[0]=first.copy();saved.world.tick=103;
        check(MechanicsHooks.beforeShot(saved,new Packet("metro_railgun",1)),"saved rail state can fire");
        check(MechanicsHooks.scaleDamage(10,saved)<3,"saved NBT remembers incomplete accumulator charge");
        Player client=player("metro_tihar",100);FMLCommonHandler.side=FMLCommonHandler.Side.CLIENT;
        MechanicsHooks.afterConsume(client.getHeldItemMainhand());equal(MechanicsHooks.energy(client.getHeldItemMainhand()),92,"client predicts one charge consumption");
        FMLCommonHandler.side=FMLCommonHandler.Side.SERVER;MechanicsHooks.afterConsume(client.getHeldItemMainhand());
        equal(MechanicsHooks.energy(client.getHeldItemMainhand()),92,"server native ammo consumption cannot double-spend pressure");
    }
    public static void main(String[] args) throws Exception {
        pumpTests();capacityTests();transactions();leakAndRail();
        System.out.println("MECHANICS_TESTS_OK "+checks+" isolated assertions; NOT a Minecraft runtime test");
    }
}
