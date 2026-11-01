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
import metro.common.player.ExtendedPlayer;
import metro.common.regstration.RegisterGuns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ModMessage
implements IMessage,
IMessageHandler<ModMessage, ModMessage> {
    private int value;
    private int page;

    public ModMessage() {
        this.value = 0;
        this.page = 0;
    }

    public ModMessage(int number, int page) {
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

    public ModMessage onMessage(ModMessage message, MessageContext ctx) {
        int i;
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
        if (message.page == 1) {
            if (message.value == 1 && heldItem.field_77990_d.func_74762_e("modStvol") != message.value && im >= 25 && ix >= 5) {
                for (i = 0; i <= 25; ++i) {
                    player.field_71071_by.func_146026_a(RegisterGuns.res_metall);
                }
                for (i = 0; i <= 5; ++i) {
                    player.field_71071_by.func_146026_a(RegisterGuns.res_xim);
                }
                world.func_72956_a((Entity)player, "metro:craft", 1.0f, 1.0f);
                heldItem.field_77990_d.func_74768_a("modStvol", message.value);
                im = 0;
                ix = 0;
            }
            if (message.value == 0 && heldItem.field_77990_d.func_74762_e("modStvol") >= 1) {
                world.func_72956_a((Entity)player, "metro:loot_metal", 1.0f, 1.0f);
                for (i = 0; i <= 7; ++i) {
                    player.field_71071_by.func_70441_a(new ItemStack(RegisterGuns.res_metall));
                }
                for (i = 0; i <= 3; ++i) {
                    player.field_71071_by.func_70441_a(new ItemStack(RegisterGuns.res_xim));
                }
                heldItem.field_77990_d.func_74768_a("modStvol", message.value);
            }
        }
        if (message.page == 2) {
            if (message.value == 1 && heldItem.field_77990_d.func_74762_e("modScope") != message.value && im >= 50 && ix >= 30) {
                for (i = 0; i <= 50; ++i) {
                    player.field_71071_by.func_146026_a(RegisterGuns.res_metall);
                }
                for (i = 0; i <= 30; ++i) {
                    player.field_71071_by.func_146026_a(RegisterGuns.res_xim);
                }
                world.func_72956_a((Entity)player, "metro:craft", 1.0f, 1.0f);
                heldItem.field_77990_d.func_74768_a("modScope", message.value);
                im = 0;
                ix = 0;
            }
            if (message.value == 2 && heldItem.field_77990_d.func_74762_e("modScope") != message.value && im >= 35 && ix >= 25) {
                for (i = 0; i <= 35; ++i) {
                    player.field_71071_by.func_146026_a(RegisterGuns.res_metall);
                }
                for (i = 0; i <= 25; ++i) {
                    player.field_71071_by.func_146026_a(RegisterGuns.res_xim);
                }
                world.func_72956_a((Entity)player, "metro:craft", 1.0f, 1.0f);
                heldItem.field_77990_d.func_74768_a("modScope", message.value);
                im = 0;
                ix = 0;
            }
            if (message.value == 0 && heldItem.field_77990_d.func_74762_e("modScope") >= 1) {
                world.func_72956_a((Entity)player, "metro:loot_metal", 1.0f, 1.0f);
                for (i = 0; i <= 7; ++i) {
                    player.field_71071_by.func_70441_a(new ItemStack(RegisterGuns.res_metall));
                }
                for (i = 0; i <= 3; ++i) {
                    player.field_71071_by.func_70441_a(new ItemStack(RegisterGuns.res_xim));
                }
                heldItem.field_77990_d.func_74768_a("modScope", message.value);
            }
        }
        if (message.page == 3) {
            if (message.value == 1 && heldItem.field_77990_d.func_74762_e("modMag") != message.value && im >= 15 && ix >= 10) {
                for (i = 0; i <= 15; ++i) {
                    player.field_71071_by.func_146026_a(RegisterGuns.res_metall);
                }
                for (i = 0; i <= 10; ++i) {
                    player.field_71071_by.func_146026_a(RegisterGuns.res_xim);
                }
                world.func_72956_a((Entity)player, "metro:craft", 1.0f, 1.0f);
                heldItem.field_77990_d.func_74768_a("modMag", message.value);
                if (heldItem.func_77973_b() == RegisterGuns.ak74) {
                    heldItem.field_77990_d.func_74768_a("maxammo", 45);
                }
                if (heldItem.func_77973_b() == RegisterGuns.preved) {
                    heldItem.field_77990_d.func_74768_a("maxammo", 5);
                }
                if (heldItem.func_77973_b() == RegisterGuns.ventil) {
                    heldItem.field_77990_d.func_74768_a("maxammo", 10);
                }
                im = 0;
                ix = 0;
            }
            if (message.value == 0 && heldItem.field_77990_d.func_74762_e("modMag") >= 1) {
                world.func_72956_a((Entity)player, "metro:loot_metal", 1.0f, 1.0f);
                for (i = 0; i <= 7; ++i) {
                    player.field_71071_by.func_70441_a(new ItemStack(RegisterGuns.res_metall));
                }
                for (i = 0; i <= 3; ++i) {
                    player.field_71071_by.func_70441_a(new ItemStack(RegisterGuns.res_xim));
                }
                heldItem.field_77990_d.func_74768_a("modMag", message.value);
                if (heldItem.func_77973_b() == RegisterGuns.ak74) {
                    heldItem.field_77990_d.func_74768_a("maxammo", 30);
                }
                if (heldItem.func_77973_b() == RegisterGuns.preved) {
                    heldItem.field_77990_d.func_74768_a("maxammo", 1);
                }
                if (heldItem.func_77973_b() == RegisterGuns.ventil) {
                    heldItem.field_77990_d.func_74768_a("maxammo", 5);
                }
            }
        }
        if (message.page == 4) {
            if (message.value == 1 && heldItem.field_77990_d.func_74762_e("modRessiver") != message.value && im >= 15 && ix >= 10) {
                for (i = 0; i <= 15; ++i) {
                    player.field_71071_by.func_146026_a(RegisterGuns.res_metall);
                }
                for (i = 0; i <= 10; ++i) {
                    player.field_71071_by.func_146026_a(RegisterGuns.res_xim);
                }
                world.func_72956_a((Entity)player, "metro:craft", 1.0f, 1.0f);
                heldItem.field_77990_d.func_74768_a("modRessiver", message.value);
                im = 0;
                ix = 0;
            }
            if (message.value == 0 && heldItem.field_77990_d.func_74762_e("modRessiver") >= 1) {
                world.func_72956_a((Entity)player, "metro:loot_metal", 1.0f, 1.0f);
                for (i = 0; i <= 7; ++i) {
                    player.field_71071_by.func_70441_a(new ItemStack(RegisterGuns.res_metall));
                }
                for (i = 0; i <= 3; ++i) {
                    player.field_71071_by.func_70441_a(new ItemStack(RegisterGuns.res_xim));
                }
                heldItem.field_77990_d.func_74768_a("modRessiver", message.value);
            }
        }
        return null;
    }
}
