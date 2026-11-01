package ru.metrocraft.port.mcmod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import ru.metrocraft.port.MetroPort;

/** Server-only atomic inventory plan: no partial charge, repeated refund, lost rounds, or client prices. */
public final class GunUpgrades {
    public static final String[] KEYS={"","modStvol","modScope","modMag","modRessiver"};
    private GunUpgrades(){}
    public static boolean supported(String id,int page,int value){
        if(value<0||value>2||page<1||page>4)return false;
        String gun=id.startsWith("mcmod_")?id.substring(6):"";
        if(gun.isEmpty())return false;
        switch(page){
            case 1:return value<=1&&!java.util.Arrays.asList("preved","tihar","railgun","helsing").contains(gun);
            case 2:
                if(gun.equals("duplet"))return false;
                if(java.util.Arrays.asList("ubo","ashot","rev").contains(gun))return value==0||value==2;
                return !gun.equals("railgun")||value<=1;
            case 3:return value<=1&&java.util.Arrays.asList("ak74","ventil","preved").contains(gun);
            case 4:return value<=1&&!gun.equals("ashot");
            default:return false;
        }
    }
    public static int[] cost(int page,int value){
        if(value==0)return new int[]{0,0};
        if(page==1)return new int[]{25,5};
        if(page==2)return value==1?new int[]{50,30}:new int[]{35,25};
        if(page==5)return new int[]{120,50};
        return new int[]{15,10};
    }
    private static boolean consume(ItemStack[] inventory,Item item,int count){
        for(ItemStack stack:inventory)if(stack.getItem()==item){int used=Math.min(count,stack.getCount());stack.shrink(used);count-=used;if(count==0)return true;}
        return count==0;
    }
    private static boolean insert(ItemStack[] inventory,ItemStack incoming){
        if(incoming.isEmpty())return true;
        for(ItemStack slot:inventory)if(ItemStack.areItemsEqual(slot,incoming)&&ItemStack.areItemStackTagsEqual(slot,incoming)){
            int n=Math.min(incoming.getCount(),Math.max(0,slot.getMaxStackSize()-slot.getCount()));slot.grow(n);incoming.shrink(n);if(incoming.isEmpty())return true;
        }
        for(int i=0;i<inventory.length;i++)if(inventory[i].isEmpty()){
            int n=Math.min(incoming.getCount(),incoming.getMaxStackSize());inventory[i]=incoming.copy();inventory[i].setCount(n);incoming.shrink(n);if(incoming.isEmpty())return true;
        }
        return false;
    }
    public static int count(EntityPlayer player,String id){
        Item item=MetroPort.ITEMS.get(id);int count=0;
        for(ItemStack stack:player.inventory.mainInventory)if(!stack.isEmpty()&&stack.getItem()==item)count+=stack.getCount();
        return count;
    }
    public static boolean apply(EntityPlayer player,ItemStack gun,int page,int value){
        if(player.world.isRemote||player.isSpectator()||!(gun.getItem() instanceof McmodAkItem)||player.getHeldItemMainhand()!=gun)return false;
        McmodAkItem item=(McmodAkItem)gun.getItem();if(item.reloading(gun)||player.isHandActive())return false;
        boolean repair=page==5&&value==1;
        if(repair){if(gun.getItemDamage()==0)return false;}
        else {
            if(!supported(item.getDefinition().id,page,value))return false;
            int current=McmodAkItem.module(gun,KEYS[page]);
            // A replacement must first be removed, so existing module cost/refund is never lost.
            if(value==0?current==0:current!=0)return false;
        }
        Item metal=MetroPort.ITEMS.get("mcmod_res_metall"),chem=MetroPort.ITEMS.get("mcmod_res_xim");
        if(metal==null||chem==null)return false;
        ItemStack[] plan=new ItemStack[36];for(int i=0;i<36;i++)plan[i]=player.inventory.mainInventory.get(i).copy();
        int[] price=cost(page,value);
        if(!consume(plan,metal,price[0])||!consume(plan,chem,price[1]))return false;
        if(value==0&&(!insert(plan,new ItemStack(metal,8))||!insert(plan,new ItemStack(chem,4))))return false;
        int remainingAmmo=item.ammo(gun);
        if(page==3&&value==0){
            remainingAmmo=Math.min(remainingAmmo,item.getDefinition().capacity);
            int surplus=item.ammo(gun)-remainingAmmo;
            Item bullet=MetroPort.ITEMS.get(item.getDefinition().ammo);
            if(surplus>0&&(bullet==null||!insert(plan,new ItemStack(bullet,surplus))))return false;
        }
        for(int i=0;i<36;i++)if(!ItemStack.areItemStacksEqual(plan[i],player.inventory.mainInventory.get(i)))player.inventory.mainInventory.set(i,plan[i]);
        if(repair)gun.setItemDamage(0);
        else {if(!gun.hasTagCompound())gun.setTagCompound(new net.minecraft.nbt.NBTTagCompound());gun.getTagCompound().setInteger(KEYS[page],value);gun.getTagCompound().setInteger("MetroAmmo",remainingAmmo);}
        player.inventory.markDirty();return true;
    }
}
