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
    public static final class Type {public Integer internalAmmoStorage=2;public String internalName;public String[] acceptedBullets={"metro_12g_buckshot"};public int roundsPerMin=150,numBullets=1;Type(String id){internalName=id;}}
    public static final class World {public boolean isRemote;public long tick=100;public long getTotalWorldTime(){return tick;}}
    public static final class Inventory {
        public int currentItem;public ItemStack[] slots=new ItemStack[41];public ItemStack cursor=ItemStack.EMPTY;
        public Inventory(){Arrays.fill(slots,ItemStack.EMPTY);}public ItemStack getStackInSlot(int i){return slots[i];}
        public int writes,failOnWrite=-1;
        public void setInventorySlotContents(int i,ItemStack s){if(++writes==failOnWrite)throw new IllegalStateException("simulated inventory API failure");slots[i]=s;}public ItemStack getItemStack(){return cursor;}
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
        p.getHeldItemMainhand().tag.setInteger("ammocount",2);
        AttachmentTransactions.transact(p,p.getHeldItemMainhand(),p.inventory.slots[1],null,false);
        equal(p.getHeldItemMainhand().tag.getInteger("ammocount"),2,"loaded base installs quad without unloading");
        equal(MechanicsHooks.capacity(((ItemGun)p.getHeldItemMainhand().item).type,p.getHeldItemMainhand()),4,"loaded upgrade expands capacity");
        boolean refused=false;
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
    static ItemStack bullet(int amount) {
        ItemStack s=new ItemStack(new com.modularwarfare.common.guns.ItemBullet("modularwarfare:metro_12g_buckshot"),amount);
        s.tag=new NBTTagCompound();s.tag.setString("batch","preserve-custom-bullet-nbt");return s;
    }
    static void loaded(ItemStack gun,int rounds) {
        gun.tag.setInteger("ammocount",rounds);gun.tag.setTag("bullet",bullet(1).writeToNBT(new NBTTagCompound()));
    }
    static int inventoryCount(Player p,String id) throws Exception {
        int result=0;for(int i=0;i<36;i++)if(id.equals(MechanicsHooks.id(p.inventory.slots[i])))result+=p.inventory.slots[i].count;
        return result;
    }
    static List<NBTTagCompound> snapshot(Player p) {
        List<NBTTagCompound> result=new ArrayList<NBTTagCompound>();
        for(int i=0;i<36;i++)result.add(p.inventory.slots[i].writeToNBT(new NBTTagCompound()));
        result.add(p.inventory.cursor.writeToNBT(new NBTTagCompound()));return result;
    }
    static void loadedTransactions() throws Exception {
        for(int rounds=0;rounds<=4;rounds++) {
            Player p=player("metro_duplet",0);loaded(p.getHeldItemMainhand(),rounds);attach(p.getHeldItemMainhand(),"metro_duplet_extra_barrels");
            AttachmentTransactions.transact(p,p.getHeldItemMainhand(),null,"barrel",false);
            equal(p.getHeldItemMainhand().tag.getInteger("ammocount"),Math.min(2,rounds),"removing quad retains loaded rounds "+rounds);
            equal(inventoryCount(p,"metro_12g_buckshot"),Math.max(0,rounds-2),"exact excess returned "+rounds);
            equal(inventoryCount(p,"metro_duplet_extra_barrels"),1,"removed quad returned once "+rounds);
            for(ItemStack item:p.inventory.slots)if("metro_12g_buckshot".equals(MechanicsHooks.id(item)))
                check("preserve-custom-bullet-nbt".equals(item.tag.getString("batch")),"returned bullet metadata preserved");
        }
        for(String incoming:new String[]{"metro_duplet_suppressor","metro_duplet_long_barrel"}) {
            Player p=player("metro_duplet",0);loaded(p.getHeldItemMainhand(),4);attach(p.getHeldItemMainhand(),"metro_duplet_extra_barrels");
            p.inventory.slots[1]=module(incoming);
            AttachmentTransactions.transact(p,p.getHeldItemMainhand(),p.inventory.slots[1],null,false);
            equal(p.getHeldItemMainhand().tag.getInteger("ammocount"),2,"loaded barrel replacement retains two");
            equal(inventoryCount(p,"metro_12g_buckshot"),2,"loaded barrel replacement refunds two");
            check(MechanicsHooks.attachment(p.getHeldItemMainhand(),incoming),"replacement installed");
            equal(inventoryCount(p,incoming),0,"incoming consumed exactly once");
        }
        Player full=player("metro_duplet",0);loaded(full.getHeldItemMainhand(),4);attach(full.getHeldItemMainhand(),"metro_duplet_extra_barrels");
        for(int i=1;i<36;i++)full.inventory.slots[i]=new ItemStack(new Item("test:stone",64),64);
        full.inventory.slots[1]=ItemStack.EMPTY; // room for module, but not for two shells
        List<NBTTagCompound> before=snapshot(full);
        boolean refused=false;try{AttachmentTransactions.transact(full,full.getHeldItemMainhand(),null,"barrel",false);}catch(AttachmentTransactions.Refusal e){refused=true;}
        check(refused,"no shell space refuses entire operation");check(before.equals(snapshot(full)),"no item/NBT loss when only module fits");
        full.inventory.slots[2]=bullet(62);
        AttachmentTransactions.transact(full,full.getHeldItemMainhand(),null,"barrel",false);
        equal(full.inventory.slots[2].count,64,"excess shells merge into compatible existing stack");
        equal(full.getHeldItemMainhand().tag.getInteger("ammocount"),2,"successful merged refund leaves two");
        Player rollback=player("metro_duplet",0);loaded(rollback.getHeldItemMainhand(),4);attach(rollback.getHeldItemMainhand(),"metro_duplet_extra_barrels");
        rollback.inventory.cursor=module("metro_duplet_suppressor");before=snapshot(rollback);rollback.inventory.failOnWrite=2;
        refused=false;try{AttachmentTransactions.transact(rollback,rollback.getHeldItemMainhand(),rollback.inventory.cursor,null,false);}catch(Exception e){refused=true;}
        check(refused,"simulated backend error propagates");check(before.equals(snapshot(rollback)),"rollback restores gun, shells, attachments and cursor");
        for(boolean all:new boolean[]{false,true}) {
            Player p=player("metro_duplet",0);loaded(p.getHeldItemMainhand(),4);attach(p.getHeldItemMainhand(),"metro_duplet_extra_barrels");
            p.getHeldItemMainhand().tag.setTag("attachment_laser",module("metro_mod_laser").writeToNBT(new NBTTagCompound()));
            AttachmentTransactions.transact(p,p.getHeldItemMainhand(),null,all?null:"laser",all);
            equal(p.getHeldItemMainhand().tag.getInteger("ammocount"),all?2:4,"unload all versus unrelated slot preserves capacity");
            equal(inventoryCount(p,"metro_12g_buckshot"),all?2:0,"unrelated slot must not eject shells");
        }
        for(int mode=0;mode<4;mode++) {
            Player p=player("metro_duplet",0);loaded(p.getHeldItemMainhand(),4);attach(p.getHeldItemMainhand(),"metro_duplet_extra_barrels");
            if(mode==0)p.getHeldItemMainhand().tag.removeTag("bullet");
            if(mode==1)p.getHeldItemMainhand().tag.setTag("bullet",module("metro_duplet_stock").writeToNBT(new NBTTagCompound()));
            if(mode==2)ServerTickHandler.reloadEnhancedTask.put(p.uuid,new Object());
            if(mode==3)p.getHeldItemMainhand().tag.setInteger("ammocount",Integer.MAX_VALUE);
            before=snapshot(p);refused=false;
            try{AttachmentTransactions.transact(p,p.getHeldItemMainhand(),null,"barrel",false);}catch(AttachmentTransactions.Refusal e){refused=true;}
            check(refused,"unsafe loaded transaction refused "+mode);check(before.equals(snapshot(p)),"refusal is atomic "+mode);
            ServerTickHandler.reloadEnhancedTask.clear();
        }
    }
    static void skinTests() {
        com.modularwarfare.common.guns.SkinType first=new com.modularwarfare.common.guns.SkinType();first.skinAsset="original";
        com.modularwarfare.common.guns.SkinType second=new com.modularwarfare.common.guns.SkinType();second.skinAsset="alternate";
        com.modularwarfare.common.guns.SkinType[] one={first},two={first,second};
        check(SafeSkins.select(one,1)==first,"reported GuiGunModify skinId=1 crash falls back to zero");
        check(SafeSkins.select(one,-1)==first,"negative skin id safe");
        check(SafeSkins.select(one,Integer.MAX_VALUE)==first,"large skin id safe");
        check(SafeSkins.select(two,1)==second,"valid alternative skin unchanged");
        check(SafeSkins.select(new com.modularwarfare.common.guns.SkinType[]{null,second},0)==second,"null skin entry falls back");
        check(SafeSkins.select(null,1).getSkin()!=null,"null skin array has nonnull texture fallback");
        check(SafeSkins.select(new com.modularwarfare.common.guns.SkinType[0],1).getSkin()!=null,"empty skin array safe");
    }
    static void leakAndRail() throws Exception {
        equal(MechanicsRules.leak(120,40),118,"overpressure leaks once per second");equal(MechanicsRules.leak(120,10000),100,"leak stops at normal pressure");
        equal(MechanicsRules.leak(60,10000),60,"normal pressure retained");equal(MechanicsRules.leak(120,-100),120,"clock rollback cannot increase loss");
        Player p=player("metro_tihar",120);p.getHeldItemMainhand().tag.setLong(MechanicsHooks.LEAK,60);
        net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent ev=new net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent();ev.player=p;
        new MechanicsHooks().tick(ev);equal(MechanicsHooks.energy(p.getHeldItemMainhand()),118,"inventory energy tick leaks excess");
        Player rail=player("metro_railgun",0);attach(rail.getHeldItemMainhand(),"metro_mod_railgun_akk");equal(MechanicsHooks.maximum(rail.getHeldItemMainhand()),150,"battery expands energy by 50 percent");
        attach(rail.getHeldItemMainhand(),"metro_mod_railgun_kon");equal(MechanicsHooks.maximum(rail.getHeldItemMainhand()),100,"capacitor not a capacity upgrade");
        check(MechanicsRules.damage("metro_railgun",100,20,true)>MechanicsRules.damage("metro_railgun",100,20,false),"capacitor accumulates full shot sooner");
        check(Math.abs(MechanicsRules.damage("metro_railgun",100,30,true)-1f)<.0001,"upgraded full charge at thirty ticks");
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
        pumpTests();capacityTests();transactions();loadedTransactions();skinTests();leakAndRail();
        System.out.println("MECHANICS_TESTS_OK "+checks+" isolated assertions; NOT a Minecraft runtime test");
    }
}
