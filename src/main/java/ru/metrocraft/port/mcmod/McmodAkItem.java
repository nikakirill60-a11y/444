package ru.metrocraft.port.mcmod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import ru.metrocraft.port.*;
/** Original condition-zero behavior: unusable until repaired, never delete the whole gun. */
public final class McmodAkItem extends GunItem {
    public McmodAkItem(Definition d){super(d);}
    @Override public boolean onLeftClickEntity(ItemStack stack,net.minecraft.entity.player.EntityPlayer player,net.minecraft.entity.Entity target){return true;}
    @Override public boolean onEntitySwing(net.minecraft.entity.EntityLivingBase entity,ItemStack stack){return true;}
    @Override protected boolean canFire(ItemStack stack){return stack.getItemDamage()<getMaxDamage() && (!definition.mcmodAir || pressure(stack)>0);}
    public static int pressure(ItemStack stack){return stack.hasTagCompound()?Math.max(0,Math.min(30,stack.getTagCompound().getInteger("davl"))):0;}
    public static int gauge(int pressure){return pressure>26?8:pressure>22?7:pressure>18?6:pressure>14?5:pressure>11?4:pressure>8?3:pressure>6?2:pressure>3?1:0;}
    public static int module(ItemStack stack,String key){return stack.hasTagCompound()?stack.getTagCompound().getInteger(key):0;}
    @Override public int capacity(ItemStack stack){
        if(module(stack,"modMag")==1){
            if(definition.id.equals("mcmod_ak74"))return 45;
            if(definition.id.equals("mcmod_ventil"))return 10;
            if(definition.id.equals("mcmod_preved"))return 5;
        }
        return definition.capacity;
    }
    @Override protected boolean originalMcmodFlight(){return true;}
    @Override protected float shotVolume(ItemStack stack){return module(stack,"modStvol")==1?.3F:1F;}
    @Override protected float shotDamage(ItemStack stack){
        float damage=definition.damage;
        if(definition.mcmodAir){int p=pressure(stack);return damage*(p>=20?1:p>=15?2F/3:p>=8?.5F:1F/3);}
        int condition=getMaxDamage()-stack.getItemDamage();
        if(condition<getMaxDamage()/4)damage*=.5F;
        else if(condition<getMaxDamage()/2)damage*=.75F;
        return Math.max(0,damage-(module(stack,"modStvol")==1?2:0));
    }
    @Override protected double deviation(ItemStack stack,net.minecraft.entity.player.EntityPlayer player,int pellet){
        if(definition.mcmodFireMode==3){float[] min={3,2,5,4,4,1},range={5,9,4,6,6,14};return (min[pellet%6]+player.getRNG().nextFloat()*range[pellet%6])*.0075;}
        return definition.spread*.0075;
    }
    @Override protected void afterShot(ItemStack stack,net.minecraft.entity.player.EntityPlayer player){
        if(definition.mcmodAir)tag(stack).setInteger("davl",Math.max(0,pressure(stack)-1));
    }
    public void pump(ItemStack stack,net.minecraft.entity.player.EntityPlayer player){
        if(!definition.mcmodAir || player.world.isRemote || reloading(stack) || player.isHandActive() || pressure(stack)>=30)return;
        long now=player.world.getTotalWorldTime();
        if(tag(stack).hasKey("MetroLastPump") && now-tag(stack).getLong("MetroLastPump")<8)return;
        net.minecraft.util.SoundEvent sound=MetroPort.SOUNDS.get("mcmod_tikhar_pump_1");
        if(sound!=null)player.world.playSound(null,player.posX,player.posY,player.posZ,sound,net.minecraft.util.SoundCategory.PLAYERS,.5F,1);
        tag(stack).setInteger("davl",pressure(stack)+1);tag(stack).setLong("MetroLastPump",now);player.inventory.markDirty();
    }
    @Override @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    public void addInformation(ItemStack stack,net.minecraft.world.World world,java.util.List<String> lines,net.minecraft.client.util.ITooltipFlag flag){
        super.addInformation(stack,world,lines,flag);
        lines.add(net.minecraft.client.resources.I18n.format("metroport.mcmod.gun.condition",getMaxDamage()-stack.getItemDamage(),getMaxDamage()));
        lines.add(net.minecraft.client.resources.I18n.format("metroport.mcmod.gun.mods"));
        if(definition.mcmodAir)lines.add(net.minecraft.client.resources.I18n.format("metroport.mcmod.gun.pressure",pressure(stack)));
    }
    @Override public void setDamage(ItemStack stack,int damage){
        super.setDamage(stack,Math.max(0,Math.min(getMaxDamage(),damage)));
        if(!stack.hasTagCompound())stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger("condition",Math.max(0,getMaxDamage()-stack.getItemDamage()));
    }
}
