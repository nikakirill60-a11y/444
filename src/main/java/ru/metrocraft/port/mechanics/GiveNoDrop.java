package ru.metrocraft.port.mechanics;

import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import java.util.*;

public final class GiveNoDrop extends CommandBase {
    @Override public String getName(){return "givenodrop";}
    @Override public int getRequiredPermissionLevel(){return 2;}
    @Override public String getUsage(ICommandSender sender){return "/givenodrop <player> <item> [count] [metadata] [NBT]";}
    @Override public void execute(MinecraftServer server,ICommandSender sender,String[] args) throws CommandException {
        if(args.length<2)throw new WrongUsageException(getUsage(sender));
        EntityPlayerMP player=getPlayer(server,sender,args[0]); Item item=getItemByText(sender,args[1]);
        int count=args.length>2?parseInt(args[2],1,64):1, meta=args.length>3?parseInt(args[3],0):0;
        ItemStack stack=new ItemStack(item,count,meta); NBTTagCompound tag=new NBTTagCompound();
        if(args.length>4)try{tag=JsonToNBT.getTagFromJson(buildString(args,4));}
        catch(NBTException e){throw new CommandException("commands.give.tagError",e.getMessage());}
        tag.setBoolean("NoDrop",true);stack.setTagCompound(tag);
        player.inventory.addItemStackToInventory(stack); NoDrop.retain(player,stack);
        player.inventoryContainer.detectAndSendChanges();
        notifyCommandListener(sender,this,"commands.give.success",new ItemStack(item).getDisplayName(),count,player.getName());
    }
    @Override public boolean isUsernameIndex(String[] args,int index){return index==0;}
    @Override public List<String> getTabCompletions(MinecraftServer server,ICommandSender sender,String[] args,BlockPos pos){
        if(args.length==1)return getListOfStringsMatchingLastWord(args,server.getOnlinePlayerNames());
        if(args.length==2)return getListOfStringsMatchingLastWord(args,Item.REGISTRY.getKeys());
        return Collections.emptyList();
    }
}
