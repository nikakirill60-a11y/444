package ru.metrocraft.port.metromod;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.*;
import ru.metrocraft.port.blackrazid.survival.*;
import java.util.List;

/** Native active-hand use: server-owned, completion-only; icons are explicitly provisional vanilla assets. */
public final class MetroFoodItem extends ItemFood {
    public static final int USE_TICKS=32;
    public final MetroConsumables.Profile profile;
    public MetroFoodItem(MetroConsumables.Profile profile){
        super(profile.hunger,profile.saturation,false);this.profile=profile;setMaxStackSize(8);
    }
    private boolean valid(ItemStack stack,EntityPlayer player){
        return !stack.isEmpty() && stack.getItem()==this && player.isEntityAlive() && !player.isSpectator();
    }
    private boolean needed(EntityPlayer player){
        // On the client water is owner telemetry, not a locally authoritative SurvivalState.
        return profile.water ? (player.world.isRemote || SurvivalSystem.get(player).water<100) : player.canEat(false);
    }
    @Override public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand){
        ItemStack stack=player.getHeldItem(hand);
        if(!valid(stack,player) || !needed(player))return new ActionResult<>(EnumActionResult.FAIL,stack);
        player.setActiveHand(hand);return new ActionResult<>(EnumActionResult.SUCCESS,stack);
    }
    @Override public int getMaxItemUseDuration(ItemStack stack){return USE_TICKS;}
    @Override public EnumAction getItemUseAction(ItemStack stack){
        // gribtea really uses the donor food class; do not invent thirst restoration for its name.
        return profile.water?EnumAction.DRINK:EnumAction.EAT;
    }
    @Override public ItemStack onItemUseFinish(ItemStack stack,World world,EntityLivingBase user){
        if(world.isRemote || !(user instanceof EntityPlayer))return stack;
        EntityPlayer player=(EntityPlayer)user;
        if(!valid(stack,player) || !needed(player))return stack;
        if(profile.water){
            SurvivalState state=SurvivalSystem.get(player);
            if(!state.drink())return stack; // +30, capped at 100; shared with the existing flask.
            SurvivalSystem.save(player,state);SurvivalSystem.sync(player);
        }else{
            player.getFoodStats().addStats(this,stack);
            world.playSound(null,player.posX,player.posY,player.posZ,SoundEvents.ENTITY_PLAYER_BURP,SoundCategory.PLAYERS,.5F,1);
        }
        // No empty container/re-filling recipe: those were not confirmed in the donor.
        if(!player.capabilities.isCreativeMode)stack.shrink(1);
        player.inventory.markDirty();return stack;
    }
    @Override @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack,World world,List<String> lines,ITooltipFlag flag){
        lines.add(I18n.translateToLocal("metroport.metromod."+(profile.water?"water":"food")));
        lines.add(I18n.translateToLocal("metroport.metromod.provisional_icon"));
    }
}
