package ru.metrocraft.port;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GunItem extends ContentItem {
    private static final String AMMO = "MetroAmmo", RELOAD = "MetroReloadTicks";


    public GunItem(Definition d) {
        super(d);
        setMaxStackSize(1);
        setMaxDamage(Math.max(1, d.durability));
    }

    protected static NBTTagCompound tag(ItemStack stack) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        return stack.getTagCompound();
    }

    public int ammo(ItemStack stack) {
        return stack.hasTagCompound() ? Math.max(0, Math.min(capacity(stack), stack.getTagCompound().getInteger(AMMO))) : 0;
    }

    public boolean reloading(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().getInteger(RELOAD) > 0;
    }

    private int available(EntityPlayer player, boolean consume, int wanted) {
        Item bullet = MetroPort.ITEMS.get(definition.ammo);
        if (bullet == null) return 0;
        int found = 0;
        for (int slot = 0; slot < player.inventory.getSizeInventory() && found < wanted; slot++) {
            ItemStack stack = player.inventory.getStackInSlot(slot);
            if (stack.getItem() != bullet) continue;
            int count = Math.min(stack.getCount(), wanted - found);
            found += count;
            if (consume) stack.shrink(count);
        }
        if (consume) player.inventory.markDirty();
        return found;
    }

    public void startReload(ItemStack stack, EntityPlayer player) {
        if (player.world.isRemote || player.isSpectator() || reloading(stack) || ammo(stack) >= capacity(stack)) return;
        if (!player.capabilities.isCreativeMode && available(player, false, 1) == 0) return;
        int duration=Math.max(1,Math.round(definition.reloadTicks*AttachmentItem.multiplier(stack,"reload")));
        tag(stack).setInteger(RELOAD,duration);
        tag(stack).setInteger("MetroReloadDuration",duration);
        tag(stack).setLong("MetroReloadStart",player.world.getTotalWorldTime());
        sound(player.world, player, definition.reloadSound, 0.7F);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isRemote || !(entity instanceof EntityPlayer) || !reloading(stack)) return;
        EntityPlayer player = (EntityPlayer) entity;
        if (player.getHeldItemMainhand() != stack) {
            tag(stack).setInteger(RELOAD, 0); // Switching slots cancels; no ammunition was removed yet.
            return;
        }
        int ticks = tag(stack).getInteger(RELOAD) - 1;
        tag(stack).setInteger(RELOAD, ticks);
        tag(stack).setLong("MetroReloadStart",world.getTotalWorldTime()-(tag(stack).getInteger("MetroReloadDuration")-ticks));
        if (ticks == 0) {
            int needed = capacity(stack) - ammo(stack);
            int loaded = player.capabilities.isCreativeMode ? needed : available(player, true, needed);
            tag(stack).setInteger(AMMO, ammo(stack) + loaded);
            player.inventory.markDirty();
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (hand != EnumHand.MAIN_HAND || player.isSpectator()) return new ActionResult<>(EnumActionResult.FAIL, stack);
        if (player.isSneaking()) {
            if (!world.isRemote) startReload(stack, player);
        } else {
            player.setActiveHand(hand);
            if (!world.isRemote) fire(stack, player);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) { return 72000; }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) { return EnumAction.NONE; }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
        if (definition.automatic && entity instanceof EntityPlayer && !entity.world.isRemote) {
            fire(stack, (EntityPlayer) entity);
        }
    }

    public int capacity(ItemStack stack){return definition.capacity;}
    protected float shotDamage(ItemStack stack){return definition.damage*AttachmentItem.multiplier(stack,"damage");}
    protected double deviation(ItemStack stack,EntityPlayer player,int pellet){return definition.spread*AttachmentItem.multiplier(stack,"spread")*.0025;}
    protected float shotVolume(ItemStack stack){return AttachmentItem.multiplier(stack,"sound");}
    protected boolean originalMcmodFlight(){return false;}
    protected void afterShot(ItemStack stack,EntityPlayer player){}
    protected boolean canFire(ItemStack stack){return true;}

    private void fire(ItemStack stack, EntityPlayer player) {
        if (player.isSpectator() || reloading(stack) || !canFire(stack) || player.getCooldownTracker().hasCooldown(this)) return;
        if (!player.capabilities.isCreativeMode && ammo(stack) == 0) {
            startReload(stack, player);
            return;
        }
        if (!player.capabilities.isCreativeMode) tag(stack).setInteger(AMMO, ammo(stack) - 1);
        player.getCooldownTracker().setCooldown(this, Math.max(1, definition.delay));
        sound(player.world, player, definition.shootSound, shotVolume(stack));
        Definition ammunition=((ContentItem)MetroPort.ITEMS.get(definition.ammo)).getDefinition();
        java.util.UUID shot=java.util.UUID.randomUUID();
        for(int i=0;i<Math.max(1,definition.pellets);i++){
            Vec3d look=player.getLookVec();
            double deviation=deviation(stack,player,i);
            float speed=definition.bulletSpeed*AttachmentItem.multiplier(stack,"speed");
            Vec3d velocity=look.scale(speed).add(player.getRNG().nextGaussian()*deviation*speed,
                    player.getRNG().nextGaussian()*deviation*speed,player.getRNG().nextGaussian()*deviation*speed);
            ru.metrocraft.port.entity.MetroBullet.ItemStackData data=new ru.metrocraft.port.entity.MetroBullet.ItemStackData();
            data.damage=shotDamage(stack);
            data.mcmod=originalMcmodFlight();
            data.gravity=originalMcmodFlight()?.05F:ammunition.fallSpeed;data.power=ammunition.penetrates?1:.25F;
            player.world.spawnEntity(new ru.metrocraft.port.entity.MetroBullet(player.world,player,data,velocity,shot));
        }
        MetroPort.NETWORK.sendTo(new ShotFeedbackPacket(definition.id,
                (definition.recoil+player.getRNG().nextFloat()*definition.randomRecoil)*AttachmentItem.multiplier(stack,"recoil"),
                (player.getRNG().nextFloat()-.5F)*definition.randomYaw*AttachmentItem.multiplier(stack,"recoil")),
                (net.minecraft.entity.player.EntityPlayerMP)player);
        tag(stack).setLong("MetroLastShot",player.world.getTotalWorldTime());
        tag(stack).setInteger("MetroShotSequence",tag(stack).getInteger("MetroShotSequence")+1);
        afterShot(stack,player);
        stack.damageItem(1, player);
    }

    private static void sound(World world, EntityPlayer player, String id, float volume) {
        SoundEvent sound = MetroPort.SOUNDS.get(id);
        if (sound != null) world.playSound(null, player.posX, player.posY, player.posZ, sound, SoundCategory.PLAYERS, volume, 1F);
    }

    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack,ItemStack newStack,boolean slotChanged){
        return slotChanged || oldStack.getItem()!=newStack.getItem();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> lines, ITooltipFlag flag) {
        lines.add(I18n.format("metroport.gun.ammo", ammo(stack), capacity(stack)));
        Item bullet = MetroPort.ITEMS.get(definition.ammo);
        if (bullet != null) lines.add(new ItemStack(bullet).getDisplayName());
        lines.add(I18n.format("metroport.gun.help"));
        if (reloading(stack)) lines.add(I18n.format("metroport.gun.loading"));
    }
}
