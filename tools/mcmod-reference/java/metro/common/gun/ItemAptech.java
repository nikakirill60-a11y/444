/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  metro.common.player.ExtendedPlayer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 */
package metro.common.gun;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import metro.common.player.ExtendedPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemAptech
extends Item {
    public int timer;
    public int uses;

    public ItemAptech(int itemDamage) {
        this.func_77656_e(5);
    }

    public void func_77622_d(ItemStack item, World w, EntityPlayer p) {
        item.field_77990_d = new NBTTagCompound();
        item.field_77990_d.func_74768_a("timer", 100);
        item.field_77990_d.func_74768_a("uses", 5);
    }

    public void func_77663_a(ItemStack item, World w, Entity e, int p_77663_4_, boolean p_77663_5_) {
        if (item.field_77990_d == null) {
            item.field_77990_d = new NBTTagCompound();
            item.field_77990_d.func_74768_a("timer", 100);
            item.field_77990_d.func_74768_a("uses", 5);
        }
        if (item.field_77990_d != null && item.field_77990_d.func_74762_e("timer") <= 100) {
            item.field_77990_d.func_74768_a("timer", item.field_77990_d.func_74762_e("timer") + 1);
        }
    }

    public ItemStack func_77659_a(ItemStack i, World w, EntityPlayer p) {
        if (i.field_77990_d != null) {
            ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer)p);
            if (i.field_77990_d.func_74762_e("uses") > 0 && i.field_77990_d.func_74762_e("timer") > 99) {
                i.field_77990_d.func_74768_a("uses", i.field_77990_d.func_74762_e("uses") - 1);
                i.field_77990_d.func_74768_a("timer", 0);
                w.func_72956_a((Entity)p, "metro:medkit_use_heavy", 10.0f, 1.0f);
                props.setHealth(100.0f);
                p.func_70606_j(1000.0f);
            }
        }
        return i;
    }

    public boolean func_82788_x() {
        return false;
    }

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        return true;
    }

    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public void func_77624_a(ItemStack item, EntityPlayer p, List list, boolean p_77624_4_) {
        if (item.field_77990_d != null) {
            list.add(1, "\u00a7d\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0448\u043f\u0440\u0438\u0446\u043e\u0432: " + item.field_77990_d.func_74762_e("uses"));
        }
    }
}
