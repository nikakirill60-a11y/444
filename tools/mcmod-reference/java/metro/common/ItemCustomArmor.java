/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 */
package metro.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import metro.armor_utill.ArmorCustomModel;
import metro.armor_utill.ArmorCustomModelTest;
import metro.armor_utill.ItemCustomArmorDummy;
import metro.common.Main;
import metro.common.regstration.RegisterArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemCustomArmor
extends ItemCustomArmorDummy {
    public ItemCustomArmor(int maxcondition, int armorType, int armor, int protect) {
        super(armorType, armor, protect, maxcondition);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public ArmorCustomModel getModel(EntityLivingBase entityPlayer, ItemStack stack, int slot) {
        if (!Main.isServer) {
            switch (slot) {
                case 3: {
                    return new ArmorCustomModelTest(0, this.armor);
                }
                case 2: {
                    return new ArmorCustomModelTest(0, this.armor);
                }
                case 1: {
                    return new ArmorCustomModelTest(0, this.armor);
                }
                case 0: {
                    return new ArmorCustomModelTest(0, this.armor);
                }
            }
            return null;
        }
        return null;
    }

    public void func_77622_d(ItemStack item, World w, EntityPlayer p) {
        item.field_77990_d = new NBTTagCompound();
        item.field_77990_d.func_74768_a("condition", this.maxcondition);
    }

    public void func_77663_a(ItemStack item, World w, Entity e, int p_77663_4_, boolean p_77663_5_) {
        if (item.field_77990_d == null) {
            item.field_77990_d = new NBTTagCompound();
            item.field_77990_d.func_74768_a("condition", this.maxcondition);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void func_77624_a(ItemStack item, EntityPlayer p, List list, boolean p_77624_4_) {
        ItemCustomArmor i = (ItemCustomArmor)item.func_77973_b();
        list.add(1, "\u00a7d\u041d\u0435\u0432\u044b\u043f\u0430\u0434\u0430\u0435\u043c\u044b\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442");
        list.add(2, "\u0417\u0430\u0449\u0438\u0442\u0430: " + i.protect + "/100");
        if (item.field_77990_d != null) {
            if (item.func_77973_b() == RegisterArmor.gasmask || item.func_77973_b() == RegisterArmor.hanza || item.func_77973_b() == RegisterArmor.himza || item.func_77973_b() == RegisterArmor.officer_ozk || item.func_77973_b() == RegisterArmor.ozk || item.func_77973_b() == RegisterArmor.povstanec || item.func_77973_b() == RegisterArmor.psh1b || item.func_77973_b() == RegisterArmor.Scout_armor || item.func_77973_b() == RegisterArmor.sverchok_mp6 || item.func_77973_b() == RegisterArmor.zapal || item.func_77973_b() == RegisterArmor.prorok) {
                list.add(3, "\u0417\u0430\u0449\u0438\u0442\u0430 \u043e\u0442 \u0440\u0430\u0434\u0438\u0430\u0446\u0438\u0438: \u0414\u0430");
            } else {
                list.add(3, "\u0417\u0430\u0449\u0438\u0442\u0430 \u043e\u0442 \u0440\u0430\u0434\u0438\u0430\u0446\u0438\u0438: \u041d\u0435\u0442");
            }
            list.add(4, "\u0421\u043e\u0441\u0442\u043e\u044f\u043d\u0438\u0435: " + item.field_77990_d.func_74762_e("condition") + "/" + i.maxcondition);
        }
    }
}
