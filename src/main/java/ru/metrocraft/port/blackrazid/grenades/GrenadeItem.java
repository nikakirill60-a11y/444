package ru.metrocraft.port.blackrazid.grenades;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.World;

public final class GrenadeItem extends Item {
    public final boolean big;
    public GrenadeItem(boolean big){this.big=big;setMaxStackSize(5);}
    @Override public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer p,EnumHand hand){p.setActiveHand(hand);return new ActionResult<>(EnumActionResult.SUCCESS,p.getHeldItem(hand));}
    @Override public EnumAction getItemUseAction(ItemStack stack){return EnumAction.BOW;}
    @Override public int getMaxItemUseDuration(ItemStack stack){return 72000;}
    @Override public void onPlayerStoppedUsing(ItemStack stack,World world,EntityLivingBase entity,int timeLeft){
        if(world.isRemote || !(entity instanceof EntityPlayer) || stack.isEmpty())return;
        EntityPlayer p=(EntityPlayer)entity;if(!p.isEntityAlive() || p.isSpectator() || p.getCooldownTracker().hasCooldown(this))return;
        GrenadeEntity grenade=new GrenadeEntity(world,p,big);grenade.shoot(p,p.rotationPitch,p.rotationYaw,0,1.35F,1);
        if(world.spawnEntity(grenade)){if(!p.capabilities.isCreativeMode)stack.shrink(1);p.getCooldownTracker().setCooldown(this,5);}
    }
}
