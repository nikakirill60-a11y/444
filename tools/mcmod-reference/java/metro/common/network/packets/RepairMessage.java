/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.simpleimpl.IMessage
 *  cpw.mods.fml.common.network.simpleimpl.IMessageHandler
 *  cpw.mods.fml.common.network.simpleimpl.MessageContext
 *  io.netty.buffer.ByteBuf
 *  metro.common.player.ExtendedPlayer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 */
package metro.common.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import metro.common.gun.ItemGun;
import metro.common.player.ExtendedPlayer;
import metro.common.regstration.RegisterGuns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RepairMessage
implements IMessage,
IMessageHandler<RepairMessage, RepairMessage> {
    private int value;
    private int page;

    public RepairMessage() {
        this.value = 0;
        this.page = 0;
    }

    public RepairMessage(int number, int page) {
        this.value = number;
        this.page = page;
    }

    public void fromBytes(ByteBuf buf) {
        this.value = buf.readInt();
        this.page = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.value);
        buf.writeInt(this.page);
    }

    public RepairMessage onMessage(RepairMessage message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().field_147369_b;
        EntityPlayerMP playermp = ctx.getServerHandler().field_147369_b;
        World world = player.field_70170_p;
        ItemStack heldItem = player.func_70694_bm();
        ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer)player);
        NBTTagCompound compound = new NBTTagCompound();
        int im = 0;
        int ix = 0;
        if (player.field_71071_by.func_146028_b(RegisterGuns.res_metall) && player.field_71071_by.func_146028_b(RegisterGuns.res_xim)) {
            for (int i3 = 0; i3 < player.field_71069_bz.func_75138_a().size(); ++i3) {
                int iq;
                Slot slot = (Slot)player.field_71069_bz.field_75151_b.get(i3);
                if (player.field_71069_bz.func_75138_a().get(i3) == null) continue;
                if (slot.func_75211_c().func_77973_b() == RegisterGuns.res_metall) {
                    for (iq = 0; iq < slot.func_75211_c().field_77994_a; ++iq) {
                        ++im;
                    }
                }
                if (slot.func_75211_c().func_77973_b() != RegisterGuns.res_xim) continue;
                for (iq = 0; iq < slot.func_75211_c().field_77994_a; ++iq) {
                    ++ix;
                }
            }
        }
        ItemGun it = (ItemGun)heldItem.func_77973_b();
        if (im >= 120 && ix >= 50 && heldItem.field_77990_d.func_74762_e("condition") < it.maxcondition) {
            int i;
            for (i = 0; i <= 120; ++i) {
                player.field_71071_by.func_146026_a(RegisterGuns.res_metall);
            }
            for (i = 0; i <= 50; ++i) {
                player.field_71071_by.func_146026_a(RegisterGuns.res_xim);
            }
            world.func_72956_a((Entity)player, "metro:craft", 1.0f, 1.0f);
            heldItem.field_77990_d.func_74768_a("condition", it.maxcondition);
            im = 0;
            ix = 0;
        }
        return null;
    }
}
