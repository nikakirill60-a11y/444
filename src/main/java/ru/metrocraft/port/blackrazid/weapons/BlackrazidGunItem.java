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
 * Native models/modules remain pending. No legacy general player/faction/air implementation is imported. */
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
    private boolean validGun(ItemStack gun){return !gun.isEmpty() && gun.getItem()==this && gun.getCount()==1;}
    public ItemStack magazine(ItemStack gun){
        if(!validGun(gun) || !gun.hasTagCompound())return ItemStack.EMPTY;
        ItemStack mag=new ItemStack(gun.getTagCompound().getCompoundTag(MAG));return accepts(mag)?mag:ItemStack.EMPTY;
    }
    public ItemStack loose(ItemStack gun){
        if(!validGun(gun) || !gun.hasTagCompound() || entry.looseRound==null)return ItemStack.EMPTY;
        ItemStack stack=new ItemStack(gun.getTagCompound().getCompoundTag(LOOSE));
        return stack.getItem()==BlackrazidWeapons.item(entry.looseRound) && stack.getCount()<=entry.looseCapacity?stack:ItemStack.EMPTY;
    }
    private void store(ItemStack gun,String key,ItemStack stack){if(stack.isEmpty())tag(gun).removeTag(key);else tag(gun).setTag(key,stack.writeToNBT(new NBTTagCompound()));}
    private static int rounds(ItemStack magazine){return magazine.isEmpty()?0:((MagazineItem)magazine.getItem()).rounds(magazine);}
    @Override public int ammo(ItemStack gun){return rounds(magazine(gun))+loose(gun).getCount();}
    @Override public int capacity(ItemStack gun){ItemStack mag=magazine(gun);return mag.isEmpty()?entry.looseCapacity:((MagazineItem)mag.getItem()).entry.capacity+(entry.keepChamber?1:0);}
    public int pressure(ItemStack gun){return validGun(gun) && gun.hasTagCompound()?Math.max(0,Math.min(entry.airCapacity,gun.getTagCompound().getInteger(AIR))):0;}
    private void pressure(ItemStack gun,int amount){tag(gun).setInteger(AIR,Math.max(0,Math.min(entry.airCapacity,amount)));}
    public int shotDelay(ItemStack gun){int air=pressure(gun);return entry.rotor?(air>266?3:air>133?4:5):entry.delay;}
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
        if(entry.looseRound==null || entry.keepChamber || !magazine(gun).isEmpty() || (!entry.topUp && ammo(gun)>0) || ammo(gun)>=entry.looseCapacity)return -1;
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
        int amount=Math.min(source.getCount(),entry.looseCapacity-current.getCount());
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
        if(!live(gun,p) || entry.airCapacity<=0 || pressure(gun)>=entry.airCapacity || reloading(gun) || p.isSprinting())return false;
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
        if(!reloading(gun))return;
        if(!live(gun,p) || !selected || (tag(gun).getInteger(ACTION)==4 && p.isSprinting()) || !tag(gun).hasUniqueId(OWNER) || !p.getUniqueID().equals(tag(gun).getUniqueId(OWNER))){cancel(gun);return;}
        int max=Math.max(Math.max(entry.loadingTicks,entry.unloadingTicks),Math.max(entry.looseLoadingTicks,entry.pumpTicks));
        int ticks=Math.min(max,tag(gun).getInteger(TICKS))-1;tag(gun).setInteger(TICKS,ticks);
        if(ticks<=0){int action=tag(gun).getInteger(ACTION);cancel(gun);
            if(action==1)insertMagazine(gun,p);else if(action==2)ejectMagazine(gun,p);else if(action==3)loadLoose(gun,p);
            else if(action==4 && entry.airCapacity>0)pressure(gun,pressure(gun)+entry.airStep);}
    }
    public boolean fireNative(ItemStack gun,EntityPlayer p){
        if(!live(gun,p) || reloading(gun) || p.isSprinting() || (gun.hasTagCompound() && tag(gun).getInteger(COOLDOWN)>0))return false;
        if(ammo(gun)==0 || (entry.airCapacity>0 && pressure(gun)<entry.airCost)){
            tag(gun).setInteger(COOLDOWN,7);sound(p.world,p,entry.emptySound==null?"blackrazid_empty":entry.emptySound,1);return false;
        }
        // One shell, one shot UUID: pellets aggregate through the existing safe damage queue.
        UUID shot=UUID.randomUUID();boolean fired=false;int delay=shotDelay(gun);
        for(int i=0;i<Math.max(1,Math.min(16,entry.pellets));i++){
            BlackrazidBullet bullet=new BlackrazidBullet(p.world,p,entry.damage,entry.speed,entry.accuracy,shot,entry.flame);
            if(p.world.spawnEntity(bullet))fired=true;
        }
        if(!fired)return false;
        ItemStack mag=magazine(gun),loose=loose(gun);
        if(!loose.isEmpty()){loose.shrink(1);store(gun,LOOSE,loose);feedChamber(gun,mag);}
        else ((MagazineItem)mag.getItem()).rounds(mag,rounds(mag)-1);
        store(gun,MAG,mag);if(entry.airCapacity>0)pressure(gun,pressure(gun)-entry.airCost);
        tag(gun).setInteger(COOLDOWN,delay);p.inventory.markDirty();
        if(!entry.flame || tag(gun).getInteger(SOUND)==0){sound(p.world,p,entry.shootSound,1);if(entry.flame)tag(gun).setInteger(SOUND,23);}
        if(entry.cycleAfterShot && ammo(gun)>0 && !mag.isEmpty())sound(p.world,p,entry.cycleSound,1);
        if(entry.rotor)sound(p.world,p,"blackrazid_gatling_rot",1);
        if(p instanceof EntityPlayerMP)MetroPort.NETWORK.sendTo(new ShotFeedbackPacket(entry.id,entry.recoil*2,0),(EntityPlayerMP)p);
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
        lines.add(capacity(stack)==0?I18n.format("metroport.blackrazid.gun.empty"):I18n.format("metroport.blackrazid.rounds",ammo(stack),capacity(stack)));
        lines.add(I18n.format(entry.magazines.length==0?"metroport.blackrazid.gun.loose":"metroport.blackrazid.gun.reload"));
        if(entry.airCapacity>0)lines.add(I18n.format("metroport.blackrazid.gun.pressure",pressure(stack),entry.airCapacity));
    }
}
