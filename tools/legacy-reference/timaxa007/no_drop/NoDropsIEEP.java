/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.world.World
 *  net.minecraftforge.common.IExtendedEntityProperties
 */
package timaxa007.no_drop;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class NoDropsIEEP
implements IExtendedEntityProperties {
    public static final String TAG = "NoDrops";
    public final ArrayList<ItemStack> save_drops = new ArrayList();

    public void saveNBTData(NBTTagCompound nbt) {
        if (this.save_drops.isEmpty()) {
            return;
        }
        NBTTagList list = new NBTTagList();
        for (ItemStack item : this.save_drops) {
            NBTTagCompound compound_item = new NBTTagCompound();
            item.func_77955_b(compound_item);
            list.func_74742_a((NBTBase)compound_item);
        }
        nbt.func_74782_a(TAG, (NBTBase)list);
    }

    public void loadNBTData(NBTTagCompound nbt) {
        if (!nbt.func_150297_b(TAG, 9)) {
            return;
        }
        NBTTagList list = nbt.func_150295_c(TAG, 10);
        this.save_drops.clear();
        for (int i = 0; i < list.func_74745_c(); ++i) {
            this.save_drops.add(ItemStack.func_77949_a((NBTTagCompound)list.func_150305_b(i)));
        }
    }

    public void init(Entity entity, World world) {
    }

    public static void reg(EntityPlayer player) {
        player.registerExtendedProperties(TAG, (IExtendedEntityProperties)new NoDropsIEEP());
    }

    public static NoDropsIEEP get(EntityPlayer player) {
        return player != null ? (NoDropsIEEP)player.getExtendedProperties(TAG) : null;
    }
}

