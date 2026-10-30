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

public final class GunItem extends ContentItem {
    private static final String AMMO = "MetroAmmo", RELOAD = "MetroReloadTicks";
    private static final double RANGE = 100;

    public GunItem(Definition d) {
        super(d);
        setMaxStackSize(1);
        setMaxDamage(Math.max(1, d.durability));
    }

    private static NBTTagCompound tag(ItemStack stack) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        return stack.getTagCompound();
    }

    private int ammo(ItemStack stack) {
        return stack.hasTagCompound() ? Math.max(0, Math.min(definition.capacity, stack.getTagCompound().getInteger(AMMO))) : 0;
    }

    private boolean reloading(ItemStack stack) {
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
        if (player.world.isRemote || player.isSpectator() || reloading(stack) || ammo(stack) >= definition.capacity) return;
        if (!player.capabilities.isCreativeMode && available(player, false, 1) == 0) return;
        tag(stack).setInteger(RELOAD, Math.max(1, Math.round(definition.reloadTicks * AttachmentItem.multiplier(stack, "reload"))));
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
        if (ticks == 0) {
            int needed = definition.capacity - ammo(stack);
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
            if (definition.automatic) player.setActiveHand(hand);
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

    private void fire(ItemStack stack, EntityPlayer player) {
        if (player.isSpectator() || reloading(stack) || player.getCooldownTracker().hasCooldown(this)) return;
        if (!player.capabilities.isCreativeMode && ammo(stack) == 0) {
            startReload(stack, player);
            return;
        }
        if (!player.capabilities.isCreativeMode) tag(stack).setInteger(AMMO, ammo(stack) - 1);
        player.getCooldownTracker().setCooldown(this, Math.max(1, definition.delay));
        sound(player.world, player, definition.shootSound, 1F);
        // Aggregate pellets so vanilla hurt resistance does not discard all but the first pellet.
        Map<Entity, Float> hits = new LinkedHashMap<>();
        Vec3d start = player.getPositionEyes(1F);
        for (int i = 0; i < Math.max(1, definition.pellets); i++) {
            Vec3d look = player.getLookVec();
            double deviation = definition.spread * AttachmentItem.multiplier(stack, "spread") * 0.0025;
            Vec3d direction = look.add(player.getRNG().nextGaussian() * deviation,
                    player.getRNG().nextGaussian() * deviation, player.getRNG().nextGaussian() * deviation).normalize();
            Vec3d end = start.add(direction.scale(RANGE));
            RayTraceResult block = player.world.rayTraceBlocks(start, end, false, true, false);
            if (block != null) end = block.hitVec;
            Entity victim = null;
            double closest = start.squareDistanceTo(end);
            AxisAlignedBB area = player.getEntityBoundingBox().expand(direction.x * RANGE, direction.y * RANGE,
                    direction.z * RANGE).grow(1);
            for (Entity target : player.world.getEntitiesWithinAABBExcludingEntity(player, area)) {
                if (!target.canBeCollidedWith() || (target instanceof EntityPlayer && ((EntityPlayer) target).isSpectator()) || target == player.getRidingEntity()) continue;
                AxisAlignedBB bounds = target.getEntityBoundingBox().grow(target.getCollisionBorderSize());
                RayTraceResult intercept = bounds.calculateIntercept(start, end);
                double distance = bounds.contains(start) ? 0 : intercept == null ? Double.POSITIVE_INFINITY : start.squareDistanceTo(intercept.hitVec);
                if (distance < closest) {
                    closest = distance;
                    victim = target;
                }
            }
            if (victim != null) {
                // Use the same team/PvP permission rules as vanilla ranged weapons.
                if (!(victim instanceof EntityPlayer) || player.canAttackPlayer((EntityPlayer) victim)) {
                    hits.put(victim, hits.getOrDefault(victim, 0F) + definition.damage * AttachmentItem.multiplier(stack, "damage"));
                }
            }
            Vec3d impact = start.add(direction.scale(Math.sqrt(closest)));
            ((WorldServer) player.world).spawnParticle(EnumParticleTypes.CRIT, impact.x, impact.y, impact.z, 2, 0.03, 0.03, 0.03, 0);
        }
        for (Map.Entry<Entity, Float> hit : hits.entrySet()) {
            // Respect existing vanilla invulnerability frames and Forge damage event hooks.
            hit.getKey().attackEntityFrom(DamageSource.causePlayerDamage(player).setProjectile(), hit.getValue());
        }
        stack.damageItem(1, player);
    }

    private static void sound(World world, EntityPlayer player, String id, float volume) {
        SoundEvent sound = MetroPort.SOUNDS.get(id);
        if (sound != null) world.playSound(null, player.posX, player.posY, player.posZ, sound, SoundCategory.PLAYERS, volume, 1F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> lines, ITooltipFlag flag) {
        lines.add(I18n.format("metroport.gun.ammo", ammo(stack), definition.capacity));
        Item bullet = MetroPort.ITEMS.get(definition.ammo);
        if (bullet != null) lines.add(new ItemStack(bullet).getDisplayName());
        lines.add(I18n.format("metroport.gun.help"));
        if (reloading(stack)) lines.add(I18n.format("metroport.gun.loading"));
    }
}
