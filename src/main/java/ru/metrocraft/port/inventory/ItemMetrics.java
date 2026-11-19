package ru.metrocraft.port.inventory;

import com.google.gson.Gson;
import net.minecraft.item.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import ru.metrocraft.port.blackrazid.BlackrazidBackpackItem;

/** Integer grams avoid rounding exploits in partial-stack pickup. Defaults are explicit balance values. */
public final class ItemMetrics {
    public static final class Profile {
        public int grams=250,capacityGrams=0,width=1,height=1;public String rarity="common";
    }
    private static final class Table {Map<String,Profile> categories=new HashMap<>(),items=new HashMap<>();}
    private static final Table TABLE=load();
    private static Table load(){
        try(Reader r=new InputStreamReader(Objects.requireNonNull(ItemMetrics.class.getResourceAsStream("/assets/metroport/inventory_balance.json")),StandardCharsets.UTF_8)){
            return new Gson().fromJson(r,Table.class);
        }catch(IOException e){throw new IllegalStateException("Missing inventory balance",e);}
    }
    public static String id(ItemStack s){return s.isEmpty()||s.getItem().getRegistryName()==null?"":s.getItem().getRegistryName().toString();}
    public static boolean currency(ItemStack stack){return id(stack).equals("metroport:mcmod_money5") || id(stack).equals("metroport:fivearmyrounds");}
    public static Profile profile(ItemStack stack){
        Profile exact=TABLE.items.get(id(stack));if(exact!=null)return exact;
        String category="item";
        if(!stack.isEmpty()){
            Item item=stack.getItem();String name=item.getClass().getSimpleName().toLowerCase(Locale.ROOT),id=id(stack);
            if(item instanceof ru.metrocraft.port.patches.FactionPatchItem)category="patch";
            else if(item instanceof BackpackItem)category="backpack";
            else if(LoadoutRules.firearm(stack))category="firearm";
            else if(LoadoutRules.melee(stack))category="melee";
            else if(NvgEquipment.accepts(stack))category="nvg";
            else if(item instanceof ItemArmor)category=((ItemArmor)item).armorType==EntityEquipmentSlot.CHEST?"armor":"helmet";
            else if(WatchEquipment.accepts(stack))category="watch";
            else if(name.contains("ammotype")||name.contains("ammoitem")||name.equals("itemammo")||name.contains("magazine"))category="magazine";
            else if(name.contains("bullet")||id.contains("ammo")||id.contains("round"))category="ammunition";
            else if(name.contains("attachment"))category="attachment";
            else if(item instanceof ItemFood)category="food";
            else if(item instanceof ItemBlock)category="block";
        }
        return TABLE.categories.get(category);
    }
    public static int width(ItemStack stack){return Math.max(1,Math.min(PackingGrid.SIDE,profile(stack).width));}
    public static int height(ItemStack stack){return Math.max(1,Math.min(PackingGrid.SIDE,profile(stack).height));}
    public static int unitGrams(ItemStack stack){return stack.isEmpty()?0:Math.max(1,profile(stack).grams);}
    public static long grams(ItemStack stack){return grams(stack,0);}
    private static long grams(ItemStack stack,int depth){
        if(stack.isEmpty())return 0;
        long total=(long)unitGrams(stack)*stack.getCount();
        // Legacy contents are still physical cargo, including while holding the bag on the cursor.
        if(depth<8 && stack.getItem() instanceof BackpackItem && stack.hasTagCompound()){
            NBTTagList list=stack.getTagCompound().getTagList("ItemInventory",10);
            for(int i=0;i<list.tagCount();i++)total+=grams(new ItemStack(list.getCompoundTagAt(i)),depth+1);
        }
        if(depth<8)total+=grams(ru.metrocraft.port.patches.FactionPatches.get(stack),depth+1);
        return total;
    }
    public static int capacityBonus(ItemStack stack){
        if(stack.isEmpty())return 0;
        if(stack.getItem() instanceof BackpackItem){
            int tier=((BackpackItem)stack.getItem()).rows;
            if(stack.getItem() instanceof BlackrazidBackpackItem)tier=Math.max(1,(((BlackrazidBackpackItem)stack.getItem()).entry.storageSlots+4)/5);
            return Math.max(1,Math.min(3,tier))*10000;
        }
        return Math.max(0,profile(stack).capacityGrams);
    }
    public static String kg(long grams){return String.format(Locale.ROOT,"%.2f",grams/1000.0);}
    private ItemMetrics(){}
}
