package ru.metrocraft.port.blackrazid.weapons;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.*;
import ru.metrocraft.port.*;
import java.util.*;

/** Own basic firearms, not aliases for MCMOD. Resource stores are serialized stacks, never a shared Item timer.
 * Native meshes/animations remain pending; modules use actual native source branches. No legacy general player/faction/air implementation is imported. */
public final class BlackrazidGunItem extends GunItem {
    private static final String MAG="BlackrazidMagazine",LOOSE="BlackrazidLooseRounds",AIR="BlackrazidPressure",
            TICKS="BlackrazidActionTicks",ACTION="BlackrazidAction",COOLDOWN="BlackrazidShotTicks",OWNER="BlackrazidActionOwner",SOUND="BlackrazidFlameSoundTicks";
    public final BlackrazidWeapons.Entry entry;
    public BlackrazidGunItem(BlackrazidWeapons.Entry entry){super(definition(entry));this.entry=entry;setMaxDamage(0);}
    private static Definition definition(BlackrazidWeapons.Entry e){
        Definition d=new Definition();d.id=e.id;d.kind="gun";d.name=e.nameRu;d.automatic=true;d.damage=e.damage;d.delay=e.delay;
        d.reloadTicks=e.loadingTicks;d.recoil=e.recoil;d.shootSound=e.shootSound;d.reloadSound=e.reloadSound;d.zoom=1.25F;return d;
    }
    private static void sound(World world,EntityPlayer player,String id,float volume){
        SoundEvent event=MetroPort.SOUNDS.get(id);if(event!=null)world.playSound(null,player.posX,player.posY,player.posZ,event,SoundCategory.PLAYERS,volume,1);
    }
    private boolean live(ItemStack gun,EntityPlayer p){return !p.world.isRemote && p.isEntityAlive() && !p.isSpectator() && gun.getCount()==1 && gun.getItem()==this
            && p.getHeldItemMainhand()==gun && p.openContainer==p.inventoryContainer;}
    public boolean accepts(ItemStack magazine){
        return !magazine.isEmpty() && magazine.getCount()==1 && magazine.getItem() instanceof MagazineItem
                && Arrays.asList(entry.magazines).contains(((MagazineItem)magazine.getItem()).entry.id);
    }
    /** Detached visual preview only: never reserve/consume ammunition, and never inspect another player's inventory. */
    public ItemStack visualMagazine(ItemStack gun,EntityPlayer viewer){
        if(!validGun(gun))return ItemStack.EMPTY;
        ItemStack loaded=magazine(gun);if(!loaded.isEmpty())return loaded.copy();
        if(viewer==null || viewer.getHeldItemMainhand()!=gun || visualAction(gun)!=1)return ItemStack.EMPTY;
        int candidate=slot(viewer);return candidate<0?ItemStack.EMPTY:viewer.inventory.getStackInSlot(candidate).copy();
    }
    private boolean validGun(ItemStack gun){return !gun.isEmpty() && gun.getItem()==this && gun.getCount()==1;}
    public ItemStack magazine(ItemStack gun){
        if(!validGun(gun) || !gun.hasTagCompound())return ItemStack.EMPTY;
        ItemStack mag=new ItemStack(gun.getTagCompound().getCompoundTag(MAG));return accepts(mag)?mag:ItemStack.EMPTY;
    }
    public ItemStack loose(ItemStack gun){
        if(!validGun(gun) || !gun.hasTagCompound() || entry.looseRound==null)return ItemStack.EMPTY;
        ItemStack stack=new ItemStack(gun.getTagCompound().getCompoundTag(LOOSE));
        return stack.getItem()==BlackrazidWeapons.item(entry.looseRound) && stack.getCount()<=looseCapacity(gun)?stack:ItemStack.EMPTY;
    }
    private void store(ItemStack gun,String key,ItemStack stack){if(stack.isEmpty())tag(gun).removeTag(key);else tag(gun).setTag(key,stack.writeToNBT(new NBTTagCompound()));}
    private static int rounds(ItemStack magazine){return magazine.isEmpty()?0:((MagazineItem)magazine.getItem()).rounds(magazine);}
    @Override public int ammo(ItemStack gun){return rounds(magazine(gun))+loose(gun).getCount();}
    @Override public int capacity(ItemStack gun){ItemStack mag=magazine(gun);return mag.isEmpty()?looseCapacity(gun):((MagazineItem)mag.getItem()).entry.capacity+(entry.keepChamber?1:0);}
    public int pressure(ItemStack gun){return validGun(gun) && gun.hasTagCompound()?Math.max(0,Math.min(pressureCapacity(gun),gun.getTagCompound().getInteger(AIR))):0;}
    private void pressure(ItemStack gun,int amount){tag(gun).setInteger(AIR,Math.max(0,Math.min(pressureCapacity(gun),amount)));}
    public int looseCapacity(ItemStack gun){return entry.sourceId.equals("Duplet") && BlackrazidModules.has(gun,1,"QuadBarrel")?4:entry.looseCapacity;}
    public int pressureCapacity(ItemStack gun){return BlackrazidModules.has(gun,2,"RailgunAkk")?60:entry.airCapacity;}
    private int pressureStep(ItemStack gun){return BlackrazidModules.has(gun,2,"RailgunAkk")?10:entry.airStep;}
    public NativeModuleEffects effects(ItemStack gun){return new NativeModuleEffects(entry,gun,pressure(gun));}
    public int shotDelay(ItemStack gun){return effects(gun).delay;}
    public boolean modificationReady(ItemStack gun){return validGun(gun) && !reloading(gun) && (!gun.hasTagCompound() || gun.getTagCompound().getInteger(COOLDOWN)<=0);}
    public int visualAction(ItemStack gun){return validGun(gun) && reloading(gun)?gun.getTagCompound().getInteger(ACTION):0;}
    public int visualTicks(ItemStack gun){return validGun(gun) && reloading(gun)?Math.max(0,Math.min(10000,gun.getTagCompound().getInteger(TICKS))):0;}
    @Override public boolean reloading(ItemStack gun){return gun.hasTagCompound() && gun.getTagCompound().getInteger(TICKS)>0;}
    private int slot(EntityPlayer p){
        int end=MagazineItem.inventoryEnd(p);
        for(boolean filled:new boolean[]{true,false})for(String id:entry.magazines)for(int i=0;i<end;i++){
            ItemStack candidate=p.inventory.getStackInSlot(i);
            if(accepts(candidate) && candidate.getItem()==BlackrazidWeapons.item(id) && ((rounds(candidate)>0)==filled))return i;
        }
        return -1;
    }
    private int looseSlot(ItemStack gun,EntityPlayer p){
        if(entry.looseRound==null || entry.keepChamber || !magazine(gun).isEmpty() || (!entry.topUp && ammo(gun)>0) || ammo(gun)>=looseCapacity(gun))return -1;
        ItemStack held=loose(gun);Item item=BlackrazidWeapons.item(entry.looseRound);int end=MagazineItem.inventoryEnd(p);
        for(int i=0;i<=end;i++){
            int slot=i==end?40:i;ItemStack candidate=p.inventory.getStackInSlot(slot);
            if(candidate.isEmpty() || candidate.getItem()!=item || candidate.getCount()<Math.max(1,entry.looseMinimum))continue;
            if(!held.isEmpty() && !ItemStack.areItemStackTagsEqual(held,candidate))continue;
            return slot;
        }
        return -1;
    }
    private void feedChamber(ItemStack gun,ItemStack mag){
        if(entry.keepChamber && loose(gun).isEmpty() && rounds(mag)>0){
            ((MagazineItem)mag.getItem()).rounds(mag,rounds(mag)-1);
            store(gun,LOOSE,new ItemStack(BlackrazidWeapons.item(entry.looseRound)));
        }
    }
    public boolean insertMagazine(ItemStack gun,EntityPlayer p){
        if(!live(gun,p) || !magazine(gun).isEmpty() || (!entry.keepChamber && !loose(gun).isEmpty()))return false;
        int slot=slot(p);if(slot<0)return false;ItemStack source=p.inventory.getStackInSlot(slot).copy();
        feedChamber(gun,source);store(gun,MAG,source);p.inventory.setInventorySlotContents(slot,ItemStack.EMPTY);p.inventory.markDirty();return true;
    }
    public boolean loadLoose(ItemStack gun,EntityPlayer p){
        if(!live(gun,p))return false;int slot=looseSlot(gun,p);if(slot<0)return false;
        ItemStack source=p.inventory.getStackInSlot(slot),current=loose(gun);
        int amount=Math.min(source.getCount(),looseCapacity(gun)-current.getCount());
        if(amount<Math.max(1,entry.looseMinimum))return false;
        ItemStack loaded=source.copy();loaded.setCount(current.getCount()+amount);
        source.shrink(amount);store(gun,LOOSE,loaded);p.inventory.markDirty();return true;
    }
    /** No Toss callbacks. A full inventory keeps the magazine installed, including NoDrop/custom tags. */
    public boolean ejectMagazine(ItemStack gun,EntityPlayer p){
        if(!live(gun,p))return false;ItemStack mag=magazine(gun);if(mag.isEmpty())return false;
        int end=MagazineItem.inventoryEnd(p);
        for(int i=0;i<end;i++)if(p.inventory.getStackInSlot(i).isEmpty()){
            store(gun,MAG,ItemStack.EMPTY);p.inventory.setInventorySlotContents(i,mag);p.inventory.markDirty();return true;
        }
        return false;
    }
    private void begin(ItemStack gun,EntityPlayer p,int action,int duration,String sound){
        tag(gun).setInteger(TICKS,Math.max(1,duration));tag(gun).setInteger(ACTION,action);tag(gun).setUniqueId(OWNER,p.getUniqueID());sound(p.world,p,sound,1);
    }
    @Override public void startReload(ItemStack gun,EntityPlayer p){
        if(!live(gun,p) || reloading(gun) || p.isSprinting())return;
        if(!magazine(gun).isEmpty())begin(gun,p,2,entry.unloadingTicks,entry.unloadSound);
        else if((loose(gun).isEmpty() || entry.keepChamber) && slot(p)>=0)begin(gun,p,1,entry.loadingTicks,entry.reloadSound);
        else if(looseSlot(gun,p)>=0)begin(gun,p,3,entry.looseLoadingTicks,entry.looseReloadSound==null?entry.reloadSound:entry.looseReloadSound);
    }
    public boolean startPump(ItemStack gun,EntityPlayer p){
        if(!live(gun,p) || entry.airCapacity<=0 || pressure(gun)>=pressureCapacity(gun) || reloading(gun) || p.isSprinting())return false;
        begin(gun,p,4,entry.pumpTicks,entry.pumpSound);return true;
    }
    void cancel(ItemStack gun){if(!gun.hasTagCompound())return;tag(gun).removeTag(TICKS);tag(gun).removeTag(ACTION);tag(gun).removeTag(OWNER+"Most");tag(gun).removeTag(OWNER+"Least");}
    @Override public void onUpdate(ItemStack gun,World world,Entity entity,int slot,boolean selected){
        if(world.isRemote || !(entity instanceof EntityPlayer))return;EntityPlayer p=(EntityPlayer)entity;
        if(gun.hasTagCompound()){
            int cd=gun.getTagCompound().getInteger(COOLDOWN);
            if(cd>0){cd=Math.min(Math.max(7,entry.delay),cd-1);tag(gun).setInteger(COOLDOWN,cd);
                if(cd==20 && !entry.cycleAfterShot && entry.cycleSound!=null && ammo(gun)>0 && live(gun,p))sound(world,p,entry.cycleSound,1);}
            int snd=gun.getTagCompound().getInteger(SOUND);if(snd>0)tag(gun).setInteger(SOUND,Math.min(23,snd-1));
        }
        if(entry.rotor && live(gun,p) && BlackrazidModules.has(gun,2,"GatlingMotor"))pressure(gun,entry.airCapacity);
        if(!reloading(gun))return;
        if(!live(gun,p) || !selected || (tag(gun).getInteger(ACTION)==4 && p.isSprinting()) || !tag(gun).hasUniqueId(OWNER) || !p.getUniqueID().equals(tag(gun).getUniqueId(OWNER))){cancel(gun);return;}
        int max=Math.max(Math.max(entry.loadingTicks,entry.unloadingTicks),Math.max(entry.looseLoadingTicks,entry.pumpTicks));
        int ticks=Math.min(max,tag(gun).getInteger(TICKS))-1;tag(gun).setInteger(TICKS,ticks);
        if(ticks<=0){int action=tag(gun).getInteger(ACTION);cancel(gun);
            if(action==1)insertMagazine(gun,p);else if(action==2)ejectMagazine(gun,p);else if(action==3)loadLoose(gun,p);
            else if(action==4 && entry.airCapacity>0)pressure(gun,pressure(gun)+pressureStep(gun));}
    }
    public boolean fireNative(ItemStack gun,EntityPlayer p){
        if(!live(gun,p) || reloading(gun) || p.isSprinting() || (gun.hasTagCompound() && tag(gun).getInteger(COOLDOWN)>0))return false;
        NativeModuleEffects effects=effects(gun);
        boolean motor=entry.rotor && BlackrazidModules.has(gun,2,"GatlingMotor");
        if(ammo(gun)<effects.ammoCost || (entry.airCapacity>0 && !motor && pressure(gun)<entry.airCost)){
            tag(gun).setInteger(COOLDOWN,7);sound(p.world,p,entry.emptySound==null?"blackrazid_empty":entry.emptySound,1);return false;
        }
        // One shell, one shot UUID: pellets aggregate through the existing safe damage queue.
        UUID shot=UUID.randomUUID();boolean fired=false;int delay=effects.delay;
        for(int i=0;i<Math.max(1,Math.min(16,effects.pellets));i++){
            BlackrazidBullet bullet=new BlackrazidBullet(p.world,p,effects.damage,effects.speed,effects.accuracy,shot,entry.flame);
            if(p.world.spawnEntity(bullet))fired=true;
        }
        if(!fired)return false;
        ItemStack mag=magazine(gun),loose=loose(gun);
        if(!loose.isEmpty()){loose.shrink(effects.ammoCost);store(gun,LOOSE,loose);feedChamber(gun,mag);}
        else ((MagazineItem)mag.getItem()).rounds(mag,rounds(mag)-effects.ammoCost);
        store(gun,MAG,mag);if(entry.airCapacity>0)pressure(gun,motor?entry.airCapacity:pressure(gun)-entry.airCost);
        tag(gun).setInteger(COOLDOWN,delay);tag(gun).setLong(NativeWeaponVisuals.SHOT_TIME,p.world.getTotalWorldTime());p.inventory.markDirty();
        if(!entry.flame || tag(gun).getInteger(SOUND)==0){sound(p.world,p,effects.sound,effects.volume);if(entry.flame)tag(gun).setInteger(SOUND,23);}
        if(entry.cycleAfterShot && ammo(gun)>0 && !mag.isEmpty())sound(p.world,p,entry.cycleSound,1);
        if(entry.rotor)sound(p.world,p,"blackrazid_gatling_rot",1);
        if(p instanceof EntityPlayerMP)MetroPort.NETWORK.sendTo(new ShotFeedbackPacket(entry.id,effects.recoil*2,0),(EntityPlayerMP)p);
        return true;
    }
    @Override public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer p,EnumHand hand){
        ItemStack stack=p.getHeldItem(hand);if(hand!=EnumHand.MAIN_HAND || p.isSpectator() || !p.isEntityAlive())return new ActionResult<>(EnumActionResult.FAIL,stack);
        if(p.isSneaking()){if(!world.isRemote)startReload(stack,p);}else{p.setActiveHand(hand);if(!world.isRemote)fireNative(stack,p);}
        return new ActionResult<>(EnumActionResult.SUCCESS,stack);
    }
    @Override public void onUsingTick(ItemStack stack,EntityLivingBase entity,int count){if(entity instanceof EntityPlayer && !entity.world.isRemote)fireNative(stack,(EntityPlayer)entity);}
    @Override public boolean onEntitySwing(EntityLivingBase entity,ItemStack stack){return true;}
    @Override public boolean onBlockStartBreak(ItemStack stack,net.minecraft.util.math.BlockPos pos,EntityPlayer player){return true;}
    @Override @SideOnly(Side.CLIENT) public void addInformation(ItemStack stack,World world,List<String> lines,ITooltipFlag flag){
        lines.add(I18n.format("metroport.blackrazid.gun.basic"));
        if(BlackrazidModules.hasSockets(stack))lines.add(I18n.format("metroport.blackrazid.modules.help"));
        lines.add(capacity(stack)==0?I18n.format("metroport.blackrazid.gun.empty"):I18n.format("metroport.blackrazid.rounds",ammo(stack),capacity(stack)));
        lines.add(I18n.format(entry.magazines.length==0?"metroport.blackrazid.gun.loose":"metroport.blackrazid.gun.reload"));
        if(entry.airCapacity>0)lines.add(I18n.format("metroport.blackrazid.gun.pressure",pressure(stack),pressureCapacity(stack)));
    }
}
