package ru.metrocraft.port.blackrazid.weapons;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.*;
import ru.metrocraft.port.inventory.InventoryCapacity;
import java.util.List;

/** Source Ammo NBT; fuel is encoded as Ammo+200 and is deliberately not refillable. */
public final class MagazineItem extends Item {
    public final BlackrazidWeapons.Entry entry;
    public MagazineItem(BlackrazidWeapons.Entry entry){this.entry=entry;setMaxStackSize(1);}
    public int rounds(ItemStack stack){
        if(stack.isEmpty() || stack.getItem()!=this || stack.getCount()!=1)return 0;
        int raw=stack.hasTagCompound()?stack.getTagCompound().getInteger("Ammo"):0;
        // Clamp before adding the fuel offset: malicious/old NBT must not overflow.
        return entry.refillable?Math.max(0,Math.min(entry.capacity,raw)):Math.max(-entry.capacity,Math.min(0,raw))+entry.defaultRounds;
    }
    public void rounds(ItemStack stack,int amount){
        if(stack.isEmpty() || stack.getItem()!=this || stack.getCount()!=1)throw new IllegalArgumentException("Requires one matching magazine");
        if(!stack.hasTagCompound())stack.setTagCompound(new NBTTagCompound());
        int bounded=Math.max(0,Math.min(entry.capacity,amount));
        stack.getTagCompound().setInteger("Ammo",entry.refillable?bounded:bounded-entry.defaultRounds);
    }
    static int inventoryEnd(EntityPlayer player){return 9+9*InventoryCapacity.rows(player);}
    /** Returns only a live accessible slot, never an armor/watch/backpack-contained stack. */
    private int roundSlot(EntityPlayer player){
        Item round=BlackrazidWeapons.item(entry.roundItem);
        for(int i=0;i<inventoryEnd(player);i++)if(!player.inventory.getStackInSlot(i).isEmpty() && player.inventory.getStackInSlot(i).getItem()==round)return i;
        ItemStack off=player.inventory.getStackInSlot(40);return !off.isEmpty() && off.getItem()==round?40:-1;
    }
    public boolean fillOne(ItemStack stack,EntityPlayer player){
        if(player.world.isRemote || !player.isEntityAlive() || player.isSpectator() || !entry.refillable || stack.getCount()!=1
                || (player.getHeldItemMainhand()!=stack && player.getHeldItemOffhand()!=stack) || player.openContainer!=player.inventoryContainer
                || rounds(stack)>=entry.capacity || player.getCooldownTracker().hasCooldown(this))return false;
        int slot=roundSlot(player);if(slot<0)return false;
        player.inventory.getStackInSlot(slot).shrink(1);rounds(stack,rounds(stack)+1);
        player.inventory.markDirty();player.getCooldownTracker().setCooldown(this,4);return true;
    }
    @Override public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand){
        ItemStack stack=player.getHeldItem(hand);boolean success=world.isRemote || fillOne(stack,player);
        return new ActionResult<>(success?EnumActionResult.SUCCESS:EnumActionResult.FAIL,stack);
    }
    @Override public boolean onEntitySwing(net.minecraft.entity.EntityLivingBase entity,ItemStack stack){return true;}
    @Override @SideOnly(Side.CLIENT) public void addInformation(ItemStack stack,World world,List<String> lines,ITooltipFlag flag){
        lines.add(I18n.format("metroport.blackrazid.rounds",rounds(stack),entry.capacity));
        lines.add(I18n.format(entry.refillable?"metroport.blackrazid.magazine.fill":"metroport.blackrazid.magazine.fuel"));
    }
}
