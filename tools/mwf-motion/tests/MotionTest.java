package ru.metrocraft.mwf;

import java.util.*;
import java.lang.reflect.Field;
import org.joml.Matrix4f;
import mchhui.hegltf.DataNode;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import com.modularwarfare.common.guns.ItemGun;
import com.modularwarfare.common.handler.ServerTickHandler;

/** Rules/hooks and native ammo on explicit facades; NOT Minecraft/Forge/OpenGL testing. */
public final class MotionTest {
    static int checks;
    static void check(boolean b,String s){checks++;if(!b)throw new AssertionError(s);}
    static void close(double a,double b,String s){check(Math.abs(a-b)<1e-5,s+": "+a+" != "+b);}
    public static final class World {public boolean isRemote;public long time=101;public long getTotalWorldTime(){return time;}}
    public static final class Inventory {public int currentItem;public ItemStack[] slots=new ItemStack[41];Inventory(){Arrays.fill(slots,ItemStack.EMPTY);}}
    public static final class Container {public List<Object> inventorySlots=new ArrayList<Object>();Container(){for(int i=0;i<46;i++)inventorySlots.add(new Object());}}
    public static final class Player extends net.minecraft.entity.player.EntityPlayerMP {
        public World world=new World();public Inventory inventory=new Inventory();public Container inventoryContainer=new Container();public UUID uuid=UUID.randomUUID();
        public boolean alive=true,sprint,spectator;public int syncs;
        public UUID getUniqueID(){return uuid;}public ItemStack getHeldItemMainhand(){return inventory.slots[inventory.currentItem];}
        public boolean isEntityAlive(){return alive;}public boolean isSprinting(){return sprint;}public boolean isSpectator(){return spectator;}
        public int getEntityId(){return 7;}public void sendSlotContents(Container c,int slot,ItemStack s){syncs++;}
    }
    public static final class Type {public String internalName="metro_gatling";public int roundsPerMin=600,numBullets=1;public Integer internalAmmoStorage;}
    public static final class Packet {public String internalname;public int entityId;Packet(String s,int e){internalname=s;entityId=e;}}
    public static final class Network {public int packets;public void sendToAllTracking(Object packet,Object player){packets++;}}
    public static final class ReloadTask {public ItemStack reloadGun;ReloadTask(ItemStack s){reloadGun=s;}}
    static Player player(){Player p=new Player();p.inventory.slots[0]=new ItemStack(new ItemGun("metro_gatling",new Type()));p.inventory.slots[0].tag=new NBTTagCompound();p.inventory.slots[0].tag.setInteger("ammocount",100);return p;}
    static Packet shot(){return new Packet("metro_gatling",1);}
    static void attach(ItemStack s,String slot,String id){s.tag.setTag("attachment_"+slot,new ItemStack(new Item("modularwarfare:"+id,1)).writeToNBT(new NBTTagCompound()));}
    static void tick(Player p,int n){MotionHooks h=new MotionHooks();for(int i=0;i<n;i++){p.world.time++;TickEvent.PlayerTickEvent e=new TickEvent.PlayerTickEvent();e.player=p;h.tick(e);}}
    static void rules(){
        RotorRules.Rotor r=new RotorRules.Rotor();check(!r.ready(),"cold barrel cannot fire");
        for(int i=0;i<4;i++)r.tick(true,false);check(!r.ready(),"not ready before five ticks");r.tick(true,false);check(r.ready(),"server minimum warmup");
        int low=r.rpm(false);for(int i=5;i<20;i++)r.tick(true,false);close(r.speed,1,"full normal speed in 20 ticks");
        check(low<r.rpm(false),"RPM increases with rotor speed");check(r.rpm(false)==400&&r.rpm(true)==600,"donor full-speed cadence tiers");
        check(r.interval(false)==3&&r.interval(true)==2,"server fixed-tick max intervals");
        double angle=r.angle;r.tick(false,false);check(r.speed>0&&r.speed<1&&r.angle!=angle,"coasting not an abrupt stop");
        for(int i=0;i<40;i++)r.tick(false,false);close(r.speed,0,"stops after release");angle=r.angle;r.tick(false,false);close(r.angle,angle,"stopped rotor remains stopped");
        RotorRules.Rotor m=new RotorRules.Rotor();for(int i=0;i<10;i++)m.tick(true,true);close(m.speed,1,"motor halves spinup time");
        for(int i=0;i<1000;i++){m.tick(i%7<4,i%3==0);check(m.speed>=0&&m.speed<=1&&m.angle>=0&&m.angle<360,"bounded state");}
    }
    static void server()throws Exception{
        Network network=new Network();com.modularwarfare.ModularWarfare.NETWORK=network;
        Player p=player();ItemStack gun=p.getHeldItemMainhand();
        check(!MotionHooks.beforeHit(p,shot()),"hit cannot authorize itself");check(!MotionHooks.beforeShot(p,shot()),"cold server rejects");
        MotionHooks.trigger(p,9,true);check(!MotionHooks.state(p).pressed,"invalid slot ignored");MotionHooks.trigger(p,1,true);check(!MotionHooks.state(p).pressed,"stale slot ignored");
        MotionHooks.trigger(p,0,true);tick(p,5);check(MotionHooks.beforeShot(p,shot()),"warm server permits shot");
        check(gun.tag.getInteger("ammocount")==100,"gate itself does not spend ammo");ItemGun.consumeShot(gun);check(ItemGun.getAmmoCount(gun)==99,"native consume spends exactly one");
        check(!MotionHooks.beforeShot(p,shot()),"same tick spam rejected");check(MotionHooks.beforeHit(p,shot()),"authorized hit accepted");check(!MotionHooks.beforeHit(p,shot()),"duplicate target denied");
        check(!MotionHooks.beforeShot(p,new Packet("metro_ak",1)),"weapon spoof denied");check(!MotionHooks.allowLegacy(p,gun),"basic packet cannot bypass spinup");
        MotionHooks.trigger(p,0,false);check(!MotionHooks.beforeShot(p,shot()),"release immediately blocks new shots");
        tick(p,11);check(!MotionHooks.state(p).pressed,"lost heartbeat stops trigger");
        MotionHooks.trigger(p,0,true);tick(p,11);check(!MotionHooks.state(p).pressed,"stale positive trigger expires");
        MotionHooks.trigger(p,0,true);p.sprint=true;tick(p,1);check(!MotionHooks.state(p).pressed,"sprint cancels trigger");p.sprint=false;
        MotionHooks.trigger(p,0,true);ServerTickHandler.playerReloadCooldown.put(p.uuid,1);tick(p,1);check(!MotionHooks.beforeShot(p,shot()),"reload blocks firing");ServerTickHandler.playerReloadCooldown.clear();
        MotionHooks.trigger(p,0,true);p.alive=false;tick(p,1);check(!MotionHooks.state(p).pressed,"death cancels trigger");p.alive=true;
        MotionHooks.State prior=MotionHooks.state(p);p.inventory.slots[0]=gun.copy();check(MotionHooks.state(p)!=prior&&MotionHooks.state(p).rotor.speed==0,"same-slot weapon swap resets spinup");
        prior=MotionHooks.state(p);p.world=new World();check(MotionHooks.state(p)!=prior,"world change resets state");
        Object task=new ReloadTask(p.getHeldItemMainhand());ServerTickHandler.reloadEnhancedTask.put(p.uuid,task);check(MotionHooks.reloadTask(p)==task,"Gatling completed reload captured");MechanicsHooks.finishReload(p,task);check(!ServerTickHandler.reloadEnhancedTask.containsKey(p.uuid),"native reload lifecycle released after completion");
        attach(p.getHeldItemMainhand(),"grip","metro_mod_gatling_motor");check(MotionHooks.has(p.getHeldItemMainhand(),"grip","metro_mod_gatling_motor"),"motor resolved from grip slot, not barrel");
        check(!MotionHooks.has(p.getHeldItemMainhand(),"stock","metro_mod_gatling_motor"),"wrong attachment slot never matches");
        check(network.packets>0,"authoritative remote visual samples emitted");
        Player other=player();check(MotionHooks.state(other).rotor.speed==0,"players have independent rotor state");
        gun.tag.setInteger("ammocount",0);p.inventory.slots[0]=gun;MotionHooks.trigger(p,0,true);check(!MotionHooks.state(p).pressed,"no-ammo trigger refused");
        Player normal=player();normal.inventory.slots[0]=MechanicsTest.gun("metro_ak",0);check(MotionHooks.beforeShot(normal,new Packet("metro_ak",1)),"unrelated shot delegated untouched");
    }
    static void poses()throws Exception{
        // Invoke the real local-matrix hook, with classic/slim sharing the same wrapper.
        Player p=player();ItemStack gun=p.getHeldItemMainhand();((Type)((ItemGun)gun.item).type).internalName="metro_revolver";
        // Registry name, rather than mutable type, is intentionally authoritative.
        gun=new ItemStack(new Item("modularwarfare:metro_revolver",1));gun.tag=new NBTTagCompound();
        MotionClient.Profile profile=new MotionClient.Profile();profile.gun="metro_revolver";profile.attachment="metro_mod_butt";
        profile.matrix=new Matrix4f().translation(.1f,.05f,-.02f).rotateZ(.2f).get(new float[16]);
        Map<String,MotionClient.Profile> map=new HashMap<String,MotionClient.Profile>();map.put("metroMotionRevolverLeft",profile);
        Field f=MotionClient.class.getDeclaredField("profiles");f.setAccessible(true);f.set(null,map);
        DataNode n=new DataNode();n.name="metroMotionRevolverLeft";MotionClient.enter(gun,p);
        Matrix4f a=new Matrix4f();MotionClient.node(n,a);close(a.m30(),0,"no-stock baseline preserved");
        attach(gun,"stock","metro_mod_butt");a=new Matrix4f();MotionClient.node(n,a);close(a.m30(),.1,"stock applies authored support-hand delta");
        Matrix4f b=new Matrix4f();MotionClient.node(n,b);close(a.m30(),b.m30(),"fresh matrices do not accumulate offsets frame to frame");
        gun.tag.removeTag("attachment_stock");a=new Matrix4f();MotionClient.node(n,a);close(a.m30(),0,"removing stock restores exact original");
        MotionClient.leave();attach(gun,"stock","metro_mod_butt");a=new Matrix4f();MotionClient.node(n,a);close(a.m30(),0,"no render context means no leaking pose");
    }
    static void codec()throws Exception{
        for(int slot:new int[]{0,8,255})for(boolean down:new boolean[]{false,true}){
            PacketMetroRotor sent=new PacketMetroRotor(slot,down),read=new PacketMetroRotor();io.netty.buffer.ByteBuf buf=new io.netty.buffer.ByteBuf();sent.encodeInto(null,buf);read.decodeInto(null,buf);
            check(read.slot==slot&&read.command==(down?1:0),"trigger slot/command round trip");
        }
        PacketMetroRotor sent=new PacketMetroRotor(123456,1000,359999),read=new PacketMetroRotor();io.netty.buffer.ByteBuf buf=new io.netty.buffer.ByteBuf();sent.encodeInto(null,buf);read.decodeInto(null,buf);
        check(read.entity==123456&&read.speed==1000&&read.angle==359999&&read.command==2,"authoritative visual packet round trip");
        Player p=player();read.handleServerSide(p);check(!MotionHooks.state(p).pressed,"client cannot inject server visual state as trigger");
        read=new PacketMetroRotor(255,true);read.handleServerSide(p);check(!MotionHooks.state(p).pressed,"invalid unsigned slot ignored by packet handler");
        read=new PacketMetroRotor(0,true);read.handleServerSide(p);check(MotionHooks.state(p).pressed,"valid trigger accepted");
        read=new PacketMetroRotor(0,false);read.handleServerSide(p);check(!MotionHooks.state(p).pressed,"stop trigger accepted");
    }
    public static void main(String[] args)throws Exception{rules();server();poses();codec();System.out.println("MOTION_TESTS_OK "+checks+" isolated assertions; NOT a live Minecraft test");}
}
