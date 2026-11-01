package ru.metrocraft.port.blackrazid.transport;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public final class RailKitItem extends Item {
    public RailKitItem(){setHasSubtypes(true);setMaxDamage(0);setMaxStackSize(1);}
    @Override public String getTranslationKey(ItemStack stack){return "item.metroport.blackrazid_wide_rails_"+(RailRules.valid(stack.getMetadata())?stack.getMetadata():0);}
    @Override public void getSubItems(CreativeTabs tab,NonNullList<ItemStack> items){if(isInCreativeTab(tab))for(int i=0;i<12;i++)items.add(new ItemStack(this,1,i));}
    @Override public EnumActionResult onItemUse(EntityPlayer player,World w,BlockPos clicked,EnumHand hand,EnumFacing side,float x,float y,float z){
        ItemStack stack=player.getHeldItem(hand);if(!RailRules.valid(stack.getMetadata())||player.isSpectator())return EnumActionResult.FAIL;
        if(w.isRemote)return EnumActionResult.SUCCESS;
        int dir=MathHelper.floor(player.rotationYaw*4/360+.5)&3;
        return RailPlacement.place(w,clicked.up(),stack.getMetadata(),dir,player,stack)?EnumActionResult.SUCCESS:EnumActionResult.FAIL;
    }
}
