/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.model.ModelBiped
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemArmor$ArmorMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumChatFormatting
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.World
 *  net.minecraftforge.common.ISpecialArmor
 *  net.minecraftforge.common.ISpecialArmor$ArmorProperties
 *  org.lwjgl.input.Keyboard
 */
package com.flansmod.common.teams;

import com.flansmod.common.FlansMod;
import com.flansmod.common.teams.ArmourType;
import com.flansmod.common.types.IFlanItem;
import com.flansmod.common.types.InfoType;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import org.lwjgl.input.Keyboard;

public class ItemTeamArmour
extends ItemArmor
implements ISpecialArmor,
IFlanItem {
    int time = 0;
    public ArmourType type;
    protected static final UUID[] uuid = new UUID[]{UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()};

    public ItemTeamArmour(ArmourType t) {
        super(ItemArmor.ArmorMaterial.DIAMOND, 0, t.type);
        this.func_77656_e(t.durability);
        this.type = t;
        this.type.item = this;
        this.func_77637_a(FlansMod.tabFlanTeams);
        GameRegistry.registerItem((Item)this, (String)this.type.shortName, (String)"flansmod");
    }

    public ItemTeamArmour(ItemArmor.ArmorMaterial armorMaterial, int renderIndex, int armourType) {
        super(armorMaterial, renderIndex, armourType);
    }

    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        return new ISpecialArmor.ArmorProperties(1, this.type.defence, Integer.MAX_VALUE);
    }

    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return (int)(this.type.defence * 20.0);
    }

    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        stack.func_77972_a(1, entity);
        stack.field_77990_d.func_74757_a("isRepaired", false);
    }

    public String getArmorTexture(ItemStack itemstack, Entity entity, int slot, String s) {
        return "flansmod:armor/" + this.type.armourTextureName + "_" + (this.type.type == 2 ? "2" : "1") + ".png";
    }

    public void func_77663_a(ItemStack var1, World var2, Entity var3, int var4, boolean var5) {
        if (var1.func_77978_p() == null) {
            var1.func_77982_d(new NBTTagCompound());
        }
    }

    public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
        if (var1.func_77978_p() == null) {
            var1.func_77982_d(new NBTTagCompound());
        }
        if (this.type.isGasMask) {
            var1.func_77978_p().func_74757_a("isGasMask", true);
            var1.func_77978_p().func_74768_a("Timer", 0);
            int var4 = EntityLiving.func_82159_b((ItemStack)var1) - 1;
            ItemStack var5 = var3.func_82169_q(var4);
            if (var5 == null) {
                var3.func_70062_b(var4 + 1, var1.func_77946_l());
                var1.field_77994_a = 0;
            }
        }
        return var1;
    }

    public void func_77624_a(ItemStack stack, EntityPlayer player, List lines, boolean b) {
        lines.add(StatCollector.func_74838_a((String)(EnumChatFormatting.YELLOW + "\u0417\u0430\u0449\u0438\u0442\u0430: " + this.type.defence * 100.0 + "%")));
        if (this.type.description != null) {
            Collections.addAll(lines, this.type.description.split("_"));
        }
        if (Math.abs(this.type.jumpModifier - 1.0f) > 0.01f) {
            lines.add("\u00a73+" + (int)((this.type.jumpModifier - 1.0f) * 100.0f) + "% Jump Height");
        }
        if (this.type.smokeProtection) {
            lines.add("\u00a72+Smoke Protection");
        }
        if (this.type.nightVision) {
            lines.add("\u00a72+Night Vision");
        }
        if (this.type.negateFallDamage) {
            lines.add("\u00a72+Negates Fall Damage");
        }
        if (stack.func_77978_p() == null) {
            stack.func_77982_d(new NBTTagCompound());
        }
        if (this.type.isGasMask) {
            if (!Keyboard.isKeyDown((int)42)) {
                lines.add("<\u041d\u0430\u0436\u043c\u0438\u0442\u0435 " + EnumChatFormatting.RED + "Shift" + EnumChatFormatting.GRAY + ">");
            } else {
                lines.add("\u0424\u0438\u043b\u044c\u0442\u0440\u0430 \u0445\u0432\u0430\u0442\u0438\u0442 \u043d\u0430 " + stack.func_77978_p().func_74762_e("Time_Left") + " \u0441\u0435\u043a\u0443\u043d\u0434");
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public int func_82790_a(ItemStack par1ItemStack, int par2) {
        return this.type.colour;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean func_77623_v() {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public void func_94581_a(IIconRegister icon) {
        this.field_77791_bV = icon.func_94245_a("FlansMod:" + this.type.iconPath);
    }

    public Multimap getAttributeModifiers(ItemStack stack) {
        Multimap map = super.getAttributeModifiers(stack);
        map.put((Object)SharedMonsterAttributes.field_111266_c.func_111108_a(), (Object)new AttributeModifier(uuid[this.type.type], "KnockbackResist", (double)this.type.knockbackModifier, 0));
        map.put((Object)SharedMonsterAttributes.field_111263_d.func_111108_a(), (Object)new AttributeModifier(uuid[this.type.type], "MovementSpeed", (double)(this.type.moveSpeedModifier - 1.0f), 2));
        return map;
    }

    @SideOnly(value=Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
        return this.type.model;
    }

    @Override
    public InfoType getInfoType() {
        return this.type;
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        if (itemStack.func_77978_p() == null) {
            itemStack.func_77982_d(new NBTTagCompound());
        }
        itemStack.func_77978_p().func_74768_a("Time", itemStack.func_77978_p().func_74762_e("Time") + 1);
        if (itemStack.func_77978_p().func_74762_e("Time_Left") > 0 && itemStack.func_77978_p().func_74762_e("Time") % 20 == 0) {
            itemStack.func_77978_p().func_74768_a("Time_Left", itemStack.func_77978_p().func_74762_e("Time_Left") - 1);
        }
        if (this.type.nightVision && FlansMod.ticker % 25 == 0) {
            player.func_70690_d(new PotionEffect(Potion.field_76439_r.field_76415_H, 250));
        }
        if (this.type.jumpModifier > 1.01f && FlansMod.ticker % 25 == 0) {
            player.func_70690_d(new PotionEffect(Potion.field_76430_j.field_76415_H, 250, (int)((this.type.jumpModifier - 1.0f) * 2.0f), true));
        }
        if (this.type.negateFallDamage) {
            player.field_70143_R = 0.0f;
        }
        if (this.type.isGasMask && FlansMod.ticker % 41 == 0) {
            player.field_70170_p.func_72956_a((Entity)player, "sub_mod_techne_block:gas", 0.1f, 0.1f);
        }
    }
}

