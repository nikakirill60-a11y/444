/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.ICommand
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.command.WrongUsageException
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.JsonToNBT
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTException
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.server.MinecraftServer
 */
package timaxa007.no_drop;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class NoDropCommand
extends CommandBase {
    public String func_71517_b() {
        return "givenodrop";
    }

    public int func_82362_a() {
        return 2;
    }

    public String func_71518_a(ICommandSender ics) {
        return "commands.givenodrop.usage";
    }

    public void func_71515_b(ICommandSender ics, String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException(this.func_71518_a(ics), new Object[0]);
        }
        EntityPlayerMP player = NoDropCommand.func_82359_c((ICommandSender)ics, (String)args[0]);
        Item item = NoDropCommand.func_147179_f((ICommandSender)ics, (String)args[1]);
        int count = 1;
        int metadata = 0;
        if (args.length >= 3) {
            count = NoDropCommand.func_71532_a((ICommandSender)ics, (String)args[2], (int)1, (int)64);
        }
        if (args.length >= 4) {
            metadata = NoDropCommand.func_71526_a((ICommandSender)ics, (String)args[3]);
        }
        ItemStack itemstack = new ItemStack(item, count, metadata);
        if (args.length >= 5) {
            String s = NoDropCommand.func_147178_a((ICommandSender)ics, (String[])args, (int)4).func_150260_c();
            try {
                NBTBase nbtbase = JsonToNBT.func_150315_a((String)s);
                if (!(nbtbase instanceof NBTTagCompound)) {
                    NoDropCommand.func_152373_a((ICommandSender)ics, (ICommand)this, (String)"commands.give.tagError", (Object[])new Object[]{"Not a valid tag"});
                    return;
                }
                itemstack.func_77982_d((NBTTagCompound)nbtbase);
            }
            catch (NBTException nbtexception) {
                NoDropCommand.func_152373_a((ICommandSender)ics, (ICommand)this, (String)"commands.give.tagError", (Object[])new Object[]{nbtexception.getMessage()});
                return;
            }
        }
        if (!itemstack.func_77942_o()) {
            itemstack.func_77982_d(new NBTTagCompound());
        }
        itemstack.func_77978_p().func_74757_a("NoDrop", true);
        if (!player.field_71071_by.func_70441_a(itemstack)) {
            EntityItem entityitem = player.func_71019_a(itemstack, false);
            entityitem.field_145804_b = 0;
            entityitem.func_145797_a(player.func_70005_c_());
        }
        NoDropCommand.func_152373_a((ICommandSender)ics, (ICommand)this, (String)"commands.give.success", (Object[])new Object[]{itemstack.func_151000_E(), count, player.func_70005_c_()});
    }

    public List func_71516_a(ICommandSender ics, String[] args) {
        return args.length == 1 ? NoDropCommand.func_71530_a((String[])args, (String[])MinecraftServer.func_71276_C().func_71213_z()) : (args.length == 2 ? NoDropCommand.func_71531_a((String[])args, (Iterable)Item.field_150901_e.func_148742_b()) : null);
    }

    public boolean func_82358_a(String[] args, int id) {
        return id == 0;
    }
}

