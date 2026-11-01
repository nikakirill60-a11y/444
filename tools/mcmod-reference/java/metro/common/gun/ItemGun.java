/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 */
package metro.common.gun;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemGun
extends Item {
    public static boolean fire;
    public double damage;
    public int shoottimedown;
    public float accuracity;
    public float weight;
    public int max_ammo;
    public float recoil;
    public float recoilgor;
    public String name;
    public String soundshoot;
    public String soundreload;
    public int tickReloadMax;
    public double distance;
    public float efdistance;
    public int curammo;
    public float cooldown;
    public int timereload;
    public boolean isReload = false;
    public boolean isManualReload;
    public int finalammo;
    public int idammo;
    public int firemod;
    public int modScope;
    public int modStvol;
    public int maxcondition;
    public boolean isAir;

    public ItemGun(boolean isAir, double damage, int firemod, float shoottimedown, String soundshoot, String soundreload, float accuracity, float weight, int max_ammo, int idammo, float recoil, float recoilgor, String name, int tickReloadMax, double distance, float efdistance, int maxcondition) {
        this.isAir = isAir;
        this.damage = damage;
        this.firemod = firemod;
        this.shoottimedown = (int)shoottimedown;
        this.soundshoot = soundshoot;
        this.soundreload = soundreload;
        this.accuracity = accuracity;
        this.weight = weight;
        this.max_ammo = max_ammo;
        this.idammo = idammo;
        this.recoil = recoil;
        this.recoilgor = recoilgor;
        this.name = name;
        this.tickReloadMax = tickReloadMax;
        this.distance = distance;
        this.efdistance = efdistance;
        this.maxcondition = maxcondition;
    }

    public void func_77622_d(ItemStack item, World w, EntityPlayer p) {
        item.field_77990_d = new NBTTagCompound();
        item.field_77990_d.func_74776_a("shoottimedown", 0.0f);
        item.field_77990_d.func_74776_a("accuracity", this.accuracity);
        item.field_77990_d.func_74768_a("curammo", 0);
        item.field_77990_d.func_74768_a("maxammo", this.max_ammo);
        item.field_77990_d.func_74757_a("isFire", false);
        item.field_77990_d.func_74757_a("isReload", false);
        item.field_77990_d.func_74768_a("maxshoottimedown", this.shoottimedown);
        item.field_77990_d.func_74768_a("reloadtimer", 0);
        item.field_77990_d.func_74768_a("firemod", this.firemod);
        item.field_77990_d.func_74768_a("condition", this.maxcondition);
        item.field_77990_d.func_74768_a("modRessiver", 0);
        item.field_77990_d.func_74768_a("modStvol", 0);
        item.field_77990_d.func_74768_a("modScope", 0);
        item.field_77990_d.func_74768_a("modPriklad", 0);
        item.field_77990_d.func_74768_a("modMag", 0);
        item.field_77990_d.func_74757_a("isAir", this.isAir);
        item.field_77990_d.func_74768_a("davl", 0);
    }

    public int func_77626_a(ItemStack p_77626_1_) {
        return 72000;
    }

    public EnumAction func_77661_b(ItemStack p_77661_1_) {
        return EnumAction.none;
    }

    public boolean func_150894_a(ItemStack p_150894_1_, World p_150894_2_, Block p_150894_3_, int p_150894_4_, int p_150894_5_, int p_150894_6_, EntityLivingBase p_150894_7_) {
        return true;
    }

    public boolean func_77648_a(ItemStack p_77648_1_, EntityPlayer player, World world, int x, int y, int z, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
        return false;
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

    public void func_77663_a(ItemStack item, World w, Entity e, int p_77663_4_, boolean p_77663_5_) {
        if (item.field_77990_d == null) {
            item.field_77990_d = new NBTTagCompound();
            item.field_77990_d.func_74776_a("shoottimedown", (float)this.shoottimedown);
            item.field_77990_d.func_74776_a("accuracity", this.accuracity);
            item.field_77990_d.func_74768_a("curammo", 0);
            item.field_77990_d.func_74768_a("maxammo", this.max_ammo);
            item.field_77990_d.func_74757_a("isReload", false);
            item.field_77990_d.func_74757_a("isFire", false);
            item.field_77990_d.func_74768_a("maxshoottimedown", this.shoottimedown);
            item.field_77990_d.func_74768_a("idammo", this.idammo);
            item.field_77990_d.func_74768_a("reloadtimer", 0);
            item.field_77990_d.func_74768_a("firemod", this.firemod);
            item.field_77990_d.func_74768_a("condition", this.maxcondition);
            item.field_77990_d.func_74768_a("modRessiver", 0);
            item.field_77990_d.func_74768_a("modStvol", 0);
            item.field_77990_d.func_74768_a("modScope", 0);
            item.field_77990_d.func_74768_a("modPriklad", 0);
            item.field_77990_d.func_74768_a("modMag", 0);
            item.field_77990_d.func_74757_a("isAir", this.isAir);
            item.field_77990_d.func_74768_a("davl", 0);
        } else {
            ItemGun gun = (ItemGun)item.func_77973_b();
            if (item.field_77990_d.func_74762_e("firemod") != gun.firemod) {
                item.field_77990_d.func_74768_a("firemod", gun.firemod);
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void func_77624_a(ItemStack item, EntityPlayer p, List list, boolean p_77624_4_) {
        ItemGun i = (ItemGun)item.func_77973_b();
        list.add(1, "\u00a7d\u041d\u0435\u0432\u044b\u043f\u0430\u0434\u0430\u0435\u043c\u044b\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442");
        if (item.field_77990_d != null) {
            double dam = 0.0;
            if (item.field_77990_d.func_74762_e("condition") >= item.field_77990_d.func_74762_e("maxcondition") / 2) {
                dam = i.damage;
            } else {
                if (item.field_77990_d.func_74762_e("condition") < item.field_77990_d.func_74762_e("maxcondition") / 2) {
                    dam = i.damage - i.damage / 4.0;
                }
                if (item.field_77990_d.func_74762_e("condition") < item.field_77990_d.func_74762_e("maxcondition") / 4) {
                    dam = i.damage / 2.0;
                }
            }
            if (item.field_77990_d.func_74762_e("modStvol") == 1) {
                list.add(2, "\u0423\u0440\u043e\u043d:" + (dam -= 2.0));
            }
            if (item.field_77990_d.func_74762_e("modStvol") == 0) {
                list.add(2, "\u0423\u0440\u043e\u043d:" + dam);
            }
            if (item.field_77990_d.func_74762_e("firemod") == 1) {
                list.add(3, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u0440\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c: " + 20 / i.shoottimedown * 30 + " \u0432\u044b\u0441\u0442\u0440./\u043c\u0438\u043d.");
            } else {
                list.add(3, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u0440\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c: -");
            }
            list.add(4, "\u041f\u0430\u0442\u0440\u043e\u043d\u043e\u0432 \u0437\u0430\u0440\u044f\u0436\u0435\u043d\u043e: " + item.field_77990_d.func_74762_e("curammo") + "/" + item.field_77990_d.func_74762_e("maxammo"));
            list.add(5, "\u041c\u043e\u0434\u0438\u0444\u0438\u043a\u0430\u0446\u0438\u0438:");
            if (item.field_77990_d.func_74762_e("modStvol") == 0) {
                list.add(6, "\u0421\u0442\u0432\u043e\u043b: - \u041e\u0431\u044b\u0447\u043d\u044b\u0439");
            }
            if (item.field_77990_d.func_74762_e("modStvol") == 1) {
                list.add(6, "\u0421\u0442\u0432\u043e\u043b: - \u0413\u043b\u0443\u0448\u0438\u0442\u0435\u043b\u044c");
            }
            if (item.field_77990_d.func_74762_e("modScope") == 0) {
                list.add(7, "\u041f\u0440\u0438\u0446\u0435\u043b: - \u041d\u0435\u0442");
            }
            if (item.field_77990_d.func_74762_e("modScope") == 1) {
                list.add(7, "\u041f\u0440\u0438\u0446\u0435\u043b: - \u041e\u043f\u0442\u0438\u043a\u0430");
            }
            if (item.field_77990_d.func_74762_e("modScope") == 2) {
                list.add(7, "\u041f\u0440\u0438\u0446\u0435\u043b: - \u041a\u043e\u043b\u0438\u043c\u0430\u0442\u043e\u0440\u043d\u044b\u0439 \u043f\u0440\u0438\u0446\u0435\u043b");
            }
            if (item.field_77990_d.func_74762_e("modMag") == 0) {
                list.add(8, "\u041c\u0430\u0433\u0430\u0437\u0438\u043d: - \u041e\u0431\u044b\u0447\u043d\u044b\u0439 " + item.field_77990_d.func_74762_e("maxammo") + " \u043f\u0430\u0442\u0440.");
            }
            if (item.field_77990_d.func_74762_e("modMag") == 1) {
                list.add(8, "\u041c\u0430\u0433\u0430\u0437\u0438\u043d: - \u0420\u0430\u0441\u0448\u0438\u0440\u0435\u043d\u043d\u044b\u0439 " + item.field_77990_d.func_74762_e("maxammo") + " \u043f\u0430\u0442\u0440.");
            }
            if (item.field_77990_d.func_74762_e("modRessiver") == 0) {
                list.add(9, "\u041b\u0426\u0423: - \u041d\u0435\u0442");
            }
            if (item.field_77990_d.func_74762_e("modRessiver") == 1) {
                list.add(9, "\u041b\u0426\u0423: - \u0414\u0430");
            }
            list.add(10, "\u0421\u043e\u0441\u0442\u043e\u044f\u043d\u0438\u0435: " + item.field_77990_d.func_74762_e("condition") + "/" + i.maxcondition);
        }
    }
}
