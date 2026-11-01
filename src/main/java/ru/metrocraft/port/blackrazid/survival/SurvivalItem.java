package ru.metrocraft.port.blackrazid.survival;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.translation.I18n;
import java.util.List;

public final class SurvivalItem extends Item {
    public enum Type { FLASK,LAMP,POWER }
    public final Type type;
    public SurvivalItem(Type type){this.type=type;setMaxStackSize(1);}
    public static int sips(ItemStack stack){return stack.hasTagCompound() && stack.getTagCompound().hasKey("BlackrazidSips",99)?Math.max(0,Math.min(10,stack.getTagCompound().getInteger("BlackrazidSips"))):10;}
    public static void sips(ItemStack stack,int value){if(!stack.hasTagCompound())stack.setTagCompound(new NBTTagCompound());stack.getTagCompound().setInteger("BlackrazidSips",Math.max(0,Math.min(10,value)));}
    @Override public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer p,EnumHand hand){
        ItemStack stack=p.getHeldItem(hand);
        if(!p.isEntityAlive() || p.isSpectator())return new ActionResult<>(EnumActionResult.FAIL,stack);
        if(type==Type.LAMP){
            if(!world.isRemote){
                if(!ru.metrocraft.port.inventory.ExtraEquipment.get(p,ru.metrocraft.port.inventory.ExtraEquipment.Kind.LAMP).isEmpty())return new ActionResult<>(EnumActionResult.FAIL,stack);
                ru.metrocraft.port.inventory.ExtraEquipment.equipAfterUse(p,ru.metrocraft.port.inventory.ExtraEquipment.Kind.LAMP,hand,stack);
            }return new ActionResult<>(EnumActionResult.SUCCESS,stack);
        }
        if(type==Type.POWER && !SurvivalSystem.hasLamp(p))return new ActionResult<>(EnumActionResult.FAIL,stack);
        SurvivalState s=SurvivalSystem.get(p);
        if(type==Type.FLASK && (sips(stack)<=0 || (!world.isRemote && s.water>=100)) || type==Type.POWER && !world.isRemote && s.energy>=100)return new ActionResult<>(EnumActionResult.FAIL,stack);
        p.setActiveHand(hand);return new ActionResult<>(EnumActionResult.SUCCESS,stack);
    }
    @Override public EnumAction getItemUseAction(ItemStack stack){return type==Type.FLASK?EnumAction.DRINK:EnumAction.BOW;}
    @Override public int getMaxItemUseDuration(ItemStack stack){return type==Type.FLASK?40:72000;}
    @Override public ItemStack onItemUseFinish(ItemStack stack,World world,EntityLivingBase entity){
        if(type!=Type.FLASK || world.isRemote || !(entity instanceof EntityPlayer) || sips(stack)<=0)return stack;
        EntityPlayer p=(EntityPlayer)entity;if(!p.isEntityAlive() || p.isSpectator())return stack;
        SurvivalState s=SurvivalSystem.get(p);if(s.drink()){
            if(!p.capabilities.isCreativeMode)sips(stack,sips(stack)-1);SurvivalSystem.save(p,s);SurvivalSystem.sync(p);
            world.playSound(null,p.posX,p.posY,p.posZ,ru.metrocraft.port.MetroPort.SOUNDS.get("blackrazid_flyashka"),SoundCategory.PLAYERS,1,1);
        }return stack;
    }
    @Override public void onUsingTick(ItemStack stack,EntityLivingBase entity,int count){
        if(type!=Type.POWER || entity.world.isRemote || !(entity instanceof EntityPlayer))return;
        EntityPlayer p=(EntityPlayer)entity;if(!p.isEntityAlive() || p.isSpectator() || !SurvivalSystem.hasLamp(p)){p.stopActiveHand();return;}
        int elapsed=getMaxItemUseDuration(stack)-count;if(elapsed<=0 || elapsed%15!=0)return;
        SurvivalState s=SurvivalSystem.get(p);if(s.charge()){
            SurvivalSystem.save(p,s);SurvivalSystem.sync(p);
            p.world.playSound(null,p.posX,p.posY,p.posZ,ru.metrocraft.port.MetroPort.SOUNDS.get("blackrazid_railgun_air"),SoundCategory.PLAYERS,1,1);
        }if(s.energy>=100)p.stopActiveHand();
    }
    @Override public void addInformation(ItemStack stack,World world,List<String> lines,ITooltipFlag flag){
        lines.add(I18n.translateToLocal("metroport.blackrazid."+type.name().toLowerCase(java.util.Locale.ROOT)+".help"));
        if(type==Type.FLASK)lines.add(I18n.translateToLocalFormatted("metroport.blackrazid.sips",sips(stack)));
    }
}
