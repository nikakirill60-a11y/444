package ru.metrocraft.port;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.List;

public class MetroArmor extends ItemArmor implements net.minecraftforge.common.ISpecialArmor {
    private final Definition definition;

    private static ArmorMaterial material(Definition d) {
        int protection = Math.max(0, Math.min(16, Math.round(d.defence * 20)));
        int[] reduction = new int[]{0, 0, d.slot.equals("chest") ? protection : 0, d.slot.equals("head") ? protection : 0};
        return EnumHelper.addArmorMaterial("METROPORT_" + d.id.toUpperCase(java.util.Locale.ROOT),
                "minecraft:iron", 1, reduction, 0, net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0);
    }

    public MetroArmor(Definition d) {
        super(material(d), 0, d.slot.equals("head") ? EntityEquipmentSlot.HEAD : EntityEquipmentSlot.CHEST);
        definition = d;
        setMaxDamage(Math.max(1, d.durability));
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "metroport:textures/legacy/" + definition.id + ".png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public net.minecraft.client.model.ModelBiped getArmorModel(net.minecraft.entity.EntityLivingBase entity,
            ItemStack stack, EntityEquipmentSlot slot, net.minecraft.client.model.ModelBiped original) {
        return ru.metrocraft.port.client.LegacyArmorModel.get(definition.id);
    }

    @Override public boolean isValidArmor(ItemStack stack,EntityEquipmentSlot slot,net.minecraft.entity.Entity entity) {
        if(slot!=armorType || definition.nightVision)return false;
        return !(entity instanceof EntityPlayer) || ru.metrocraft.port.inventory.EquipmentRules.allowed((EntityPlayer)entity,slot,stack);
    }
    @Override public net.minecraft.util.ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,net.minecraft.util.EnumHand hand) {
        ItemStack stack=player.getHeldItem(hand);
        if(!ru.metrocraft.port.inventory.EquipmentRules.allowed(player,armorType,stack))
            return new net.minecraft.util.ActionResult<>(net.minecraft.util.EnumActionResult.FAIL,stack);
        return super.onItemRightClick(world,player,hand);
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack ingredient) {
        return false; // Repair kits, not an unintended iron-ingot repair recipe.
    }

    @Override public net.minecraftforge.common.ISpecialArmor.ArmorProperties getProperties(
            net.minecraft.entity.EntityLivingBase entity,ItemStack stack,net.minecraft.util.DamageSource source,double damage,int slot) {
        return new net.minecraftforge.common.ISpecialArmor.ArmorProperties(1,definition.defence,Integer.MAX_VALUE);
    }
    @Override public int getArmorDisplay(EntityPlayer player,ItemStack stack,int slot){return (int)(definition.defence*20);}
    @Override public void damageArmor(net.minecraft.entity.EntityLivingBase entity,ItemStack stack,
                                      net.minecraft.util.DamageSource source,int damage,int slot){
        stack.damageItem(1,entity);
        if(stack.hasTagCompound())stack.getTagCompound().setBoolean("isRepaired",false);
    }
    public Definition getDefinition(){return definition;}
    @Override public com.google.common.collect.Multimap<String,net.minecraft.entity.ai.attributes.AttributeModifier>
            getAttributeModifiers(EntityEquipmentSlot slot,ItemStack stack){
        com.google.common.collect.Multimap<String,net.minecraft.entity.ai.attributes.AttributeModifier> map=
                com.google.common.collect.HashMultimap.create();
        if(slot!=armorType)return map;
        java.util.UUID id=java.util.UUID.nameUUIDFromBytes(("metroport.armor."+slot.getName()).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        map.put(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED.getName(),
                new net.minecraft.entity.ai.attributes.AttributeModifier(id,"Metro movement",definition.moveSpeedModifier-1,2));
        map.put(net.minecraft.entity.SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(),
                new net.minecraft.entity.ai.attributes.AttributeModifier(id,"Metro knockback",definition.knockbackModifier,0));
        return map;
    }

    public boolean isGasMask() { return definition.gasMask || ru.metrocraft.port.inventory.EquipmentRules.sealedId(definition.id); }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        if (!world.isRemote && isGasMask() && stack.hasTagCompound()) {
            int ticks = stack.getTagCompound().getInteger("MetroFilterTicks");
            if (ticks > 0) stack.getTagCompound().setInteger("MetroFilterTicks", ticks - 1);
        }
        if (!world.isRemote && definition.nightVision) {
            PotionEffect current = player.getActivePotionEffect(MobEffects.NIGHT_VISION);
            if (current == null || current.getDuration() < 220) {
                player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> lines, ITooltipFlag flag) {
        if(ru.metrocraft.port.inventory.EquipmentRules.sealed(stack))lines.add(I18n.format("metroport.armor.builtin_head"));
        if(definition.nightVision)lines.add(I18n.format("metroport.nvg.help"));
        if (isGasMask()) {
            int ticks = stack.hasTagCompound() ? stack.getTagCompound().getInteger("MetroFilterTicks") : 0;
            lines.add(I18n.format("metroport.armor.filter", (ticks + 19) / 20));
        }
    }
}
