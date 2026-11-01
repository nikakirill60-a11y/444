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

/** Native basic automatics. The nested magazine is the ONLY authoritative ammunition store.
 * No loose-round/MetroAmmo path, shared item timers, MCMOD upgrades or arbitrary attachment multipliers.
 * Modules and native meshes are explicitly still pending, not silently replaced by MCMOD counterparts. */
public final class BlackrazidGunItem extends GunItem {
    private static final String MAG="BlackrazidMagazine",TICKS="BlackrazidActionTicks",ACTION="BlackrazidAction",COOLDOWN="BlackrazidShotTicks",OWNER="BlackrazidActionOwner";
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
    public ItemStack magazine(ItemStack gun){
        if(gun.isEmpty() || gun.getItem()!=this || gun.getCount()!=1 || !gun.hasTagCompound())return ItemStack.EMPTY;
        ItemStack mag=new ItemStack(gun.getTagCompound().getCompoundTag(MAG));return accepts(mag)?mag:ItemStack.EMPTY;
    }
    private void store(ItemStack gun,ItemStack magazine){
        if(magazine.isEmpty())tag(gun).removeTag(MAG);else tag(gun).setTag(MAG,magazine.writeToNBT(new NBTTagCompound()));
    }
    @Override public int ammo(ItemStack gun){ItemStack mag=magazine(gun);return mag.isEmpty()?0:((MagazineItem)mag.getItem()).rounds(mag);}
    @Override public int capacity(ItemStack gun){ItemStack mag=magazine(gun);return mag.isEmpty()?0:((MagazineItem)mag.getItem()).entry.capacity;}
    @Override public boolean reloading(ItemStack gun){return gun.hasTagCompound() && gun.getTagCompound().getInteger(TICKS)>0;}
    private int slot(EntityPlayer p){
        // Native order: filled magazines before empty, preferred type (e.g. RPK drum) before smaller type.
        for(boolean filled:new boolean[]{true,false})for(String id:entry.magazines)for(int i=0;i<MagazineItem.inventoryEnd(p);i++){
            ItemStack candidate=p.inventory.getStackInSlot(i);
            if(accepts(candidate) && candidate.getItem()==BlackrazidWeapons.item(id)
                    && ((((MagazineItem)candidate.getItem()).rounds(candidate)>0)==filled))return i;
        }
        return -1;
    }
    /** Re-evaluates inventory at completion. Nothing is reserved/consumed when the timer starts. */
    public boolean insertMagazine(ItemStack gun,EntityPlayer p){
        if(!live(gun,p) || !magazine(gun).isEmpty())return false;
        int slot=slot(p);if(slot<0)return false;ItemStack source=p.inventory.getStackInSlot(slot);
        store(gun,source.copy());p.inventory.setInventorySlotContents(slot,ItemStack.EMPTY);p.inventory.markDirty();return true;
    }
    /** No toss callbacks: a full inventory keeps the exact magazine inside the gun. */
    public boolean ejectMagazine(ItemStack gun,EntityPlayer p){
        if(!live(gun,p))return false;ItemStack mag=magazine(gun);if(mag.isEmpty())return false;
        for(int i=0;i<MagazineItem.inventoryEnd(p);i++)if(p.inventory.getStackInSlot(i).isEmpty()){
            store(gun,ItemStack.EMPTY);p.inventory.setInventorySlotContents(i,mag);p.inventory.markDirty();return true;
        }
        // ItemTossEvent listeners may already retain a canceled drop (e.g. NoDrop).
        // Restoring after such a callback duplicates ammunition; never publish it at all.
        return false;
    }
    @Override public void startReload(ItemStack gun,EntityPlayer p){
        if(!live(gun,p) || reloading(gun))return;
        boolean unload=!magazine(gun).isEmpty();if(!unload && slot(p)<0)return;
        int duration=unload?entry.unloadingTicks:entry.loadingTicks;
        tag(gun).setInteger(TICKS,duration);tag(gun).setInteger(ACTION,unload?2:1);tag(gun).setUniqueId(OWNER,p.getUniqueID());
        sound(p.world,p,unload?entry.unloadSound:entry.reloadSound,1);
    }
    void cancel(ItemStack gun){if(!gun.hasTagCompound())return;tag(gun).removeTag(TICKS);tag(gun).removeTag(ACTION);tag(gun).removeTag(OWNER+"Most");tag(gun).removeTag(OWNER+"Least");}
    @Override public void onUpdate(ItemStack gun,World world,Entity entity,int slot,boolean selected){
        if(world.isRemote || !(entity instanceof EntityPlayer))return;EntityPlayer p=(EntityPlayer)entity;
        if(gun.hasTagCompound()){int cd=gun.getTagCompound().getInteger(COOLDOWN);if(cd>0)tag(gun).setInteger(COOLDOWN,Math.min(7,cd-1));}
        if(!reloading(gun))return;
        if(!live(gun,p) || !selected || !tag(gun).hasUniqueId(OWNER) || !p.getUniqueID().equals(tag(gun).getUniqueId(OWNER))){cancel(gun);return;}
        int ticks=Math.min(Math.max(entry.loadingTicks,entry.unloadingTicks),tag(gun).getInteger(TICKS))-1;
        tag(gun).setInteger(TICKS,ticks);
        if(ticks<=0){int action=tag(gun).getInteger(ACTION);cancel(gun);if(action==1)insertMagazine(gun,p);else if(action==2)ejectMagazine(gun,p);}
    }
    public boolean fireNative(ItemStack gun,EntityPlayer p){
        if(!live(gun,p) || reloading(gun) || p.isSprinting() || (gun.hasTagCompound() && tag(gun).getInteger(COOLDOWN)>0))return false;
        ItemStack mag=magazine(gun);
        if(mag.isEmpty() || ammo(gun)==0){tag(gun).setInteger(COOLDOWN,7);sound(p.world,p,"blackrazid_empty",1);return false;}
        BlackrazidBullet bullet=new BlackrazidBullet(p.world,p,entry.damage,entry.speed,entry.accuracy);
        if(!p.world.spawnEntity(bullet))return false;
        MagazineItem item=(MagazineItem)mag.getItem();item.rounds(mag,item.rounds(mag)-1);store(gun,mag);
        tag(gun).setInteger(COOLDOWN,entry.delay);p.inventory.markDirty();sound(p.world,p,entry.shootSound,1);
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
        lines.add(magazine(stack).isEmpty()?I18n.format("metroport.blackrazid.gun.empty"):I18n.format("metroport.blackrazid.rounds",ammo(stack),capacity(stack)));
        lines.add(I18n.format("metroport.blackrazid.gun.reload"));
    }
}
