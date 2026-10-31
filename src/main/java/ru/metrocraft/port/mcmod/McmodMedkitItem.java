package ru.metrocraft.port.mcmod;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.*;
import java.util.List;

/** Source five syringes and 100-tick per-stack timer; never alters the player's maximum health. */
public final class McmodMedkitItem extends Item {
    public McmodMedkitItem() { setMaxStackSize(1); setMaxDamage(5); setNoRepair(); }
    public static int remaining(ItemStack stack) {
        int uses=stack.hasTagCompound() && stack.getTagCompound().hasKey("uses",3)
                ?stack.getTagCompound().getInteger("uses"):5-stack.getItemDamage();
        return Math.max(0,Math.min(5,uses));
    }
    public static int timer(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("timer",3)
                ?Math.max(0,Math.min(100,stack.getTagCompound().getInteger("timer"))):100;
    }
    public static boolean canUse(ItemStack stack,float health,float maximum,boolean spectator) {
        return !spectator && health>0 && health<maximum && remaining(stack)>0 && timer(stack)>=100;
    }
    static void writeState(ItemStack stack,int uses,int ticks) {
        if(!stack.hasTagCompound())stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger("uses",Math.max(0,Math.min(5,uses)));
        stack.getTagCompound().setInteger("timer",Math.max(0,Math.min(100,ticks)));
        stack.setItemDamage(5-remaining(stack));
    }
    static void spend(ItemStack stack,boolean creative) { writeState(stack,remaining(stack)-(creative?0:1),0); }
    static void tick(ItemStack stack) { writeState(stack,remaining(stack),timer(stack)+1); }
    @Override public void onUpdate(ItemStack stack,World world,Entity entity,int slot,boolean selected) {
        if(!world.isRemote)tick(stack);
    }
    @Override public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand) {
        ItemStack stack=player.getHeldItem(hand);
        if(!canUse(stack,player.getHealth(),player.getMaxHealth(),player.isSpectator()))
            return new ActionResult<>(EnumActionResult.FAIL,stack);
        if(!world.isRemote) {
            player.setHealth(player.getMaxHealth());
            spend(stack,player.capabilities.isCreativeMode);
            player.inventory.markDirty();
        }
        return new ActionResult<>(EnumActionResult.SUCCESS,stack);
    }
    @Override public boolean onLeftClickEntity(ItemStack stack,EntityPlayer player,Entity entity) { return true; }
    @Override public boolean onEntitySwing(EntityLivingBase entity,ItemStack stack) { return true; }
    @Override public boolean isRepairable() { return false; }
    @Override public boolean getIsRepairable(ItemStack stack,ItemStack ingredient) { return false; }
    @Override @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack,World world,List<String> lines,ITooltipFlag flag) {
        lines.add(I18n.format("metroport.mcmod.medkit.uses",remaining(stack)));
        lines.add(I18n.format(remaining(stack)==0?"metroport.mcmod.medkit.empty":"metroport.mcmod.medkit.help"));
    }
}
