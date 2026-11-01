package ru.metrocraft.port.blackrazid.environment;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public final class TunnelKitItem extends Item {
    public TunnelKitItem(){setHasSubtypes(true);setMaxDamage(0);setMaxStackSize(64);}
    @Override public String getTranslationKey(ItemStack stack){return "item.metroport.blackrazid_tunnels_"+(TunnelRules.valid(stack.getMetadata())?stack.getMetadata():0);}
    @Override public void getSubItems(CreativeTabs tab,NonNullList<ItemStack> items){if(isInCreativeTab(tab))for(int i=0;i<TunnelRules.count();i++)items.add(new ItemStack(this,1,i));}
    @Override public EnumActionResult onItemUse(EntityPlayer player,World w,BlockPos clicked,EnumHand hand,EnumFacing side,float x,float y,float z){
        ItemStack stack=player.getHeldItem(hand);if(!TunnelRules.valid(stack.getMetadata())||player.isSpectator())return EnumActionResult.FAIL;
        if(w.isRemote)return EnumActionResult.SUCCESS;
        if(!w.isBlockLoaded(clicked))return EnumActionResult.FAIL;
        int dir=TunnelRules.placementDirection(player.rotationYaw);
        return TunnelPlacement.place(w,w.getBlockState(clicked).getBlock().isReplaceable(w,clicked)?clicked:clicked.offset(side),stack.getMetadata(),dir,player,stack,side)?EnumActionResult.SUCCESS:EnumActionResult.FAIL;
    }
}
