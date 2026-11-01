package ru.metrocraft.port.blackrazid.weapons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Five original stack-owned sockets. No static mutable installed inventory, and no legacy player data. */
public final class BlackrazidModules {
    private static final String KEY="BlackrazidModules";
    public static final class Entry { public String id,sourceId,icon,nameRu,nameEn; }
    public static final class ModuleItem extends Item {
        public final Entry entry;
        ModuleItem(Entry entry){this.entry=entry;setMaxStackSize(1);}
    }
    private static final Entry[] ENTRIES=read("blackrazid_modules.json",Entry[].class);
    private static final Map<String,String[][]> FITTINGS=read("blackrazid_module_fittings.json",new TypeToken<Map<String,String[][]>>(){}.getType());
    private BlackrazidModules(){}
    private static <T>T read(String file,java.lang.reflect.Type type){
        try(Reader reader=new InputStreamReader(Objects.requireNonNull(BlackrazidModules.class.getResourceAsStream("/assets/metroport/"+file)),StandardCharsets.UTF_8)){
            return new Gson().fromJson(reader,type);
        }catch(IOException ex){throw new IllegalStateException("Cannot load native modules",ex);}
    }
    public static List<Entry> entries(){return Collections.unmodifiableList(Arrays.asList(ENTRIES));}
    public static Entry entry(int index){return index>=0 && index<ENTRIES.length?ENTRIES[index]:null;}
    public static boolean accepts(ItemStack gun,int slot,ItemStack module){
        if(gun.isEmpty() || gun.getCount()!=1 || !(gun.getItem() instanceof BlackrazidGunItem) || slot<0 || slot>=5
                || module.isEmpty() || module.getCount()!=1 || !(module.getItem() instanceof ModuleItem))return false;
        String[][] slots=FITTINGS.get(((BlackrazidGunItem)gun.getItem()).entry.sourceId);
        return slots!=null && Arrays.asList(slots[slot]).contains(((ModuleItem)module.getItem()).entry.sourceId);
    }
    public static boolean hasSockets(ItemStack gun){
        if(!(gun.getItem() instanceof BlackrazidGunItem))return false;
        String[][] slots=FITTINGS.get(((BlackrazidGunItem)gun.getItem()).entry.sourceId);
        if(slots!=null)for(String[] options:slots)if(options.length>0)return true;
        return false;
    }
    /** Return detached stacks. Merely inspecting a gun never adds NBT. Wrong counts/types/slots have no effect. */
    public static ItemStack get(ItemStack gun,int slot){
        if(slot<0 || slot>=5 || !gun.hasTagCompound())return ItemStack.EMPTY;
        ItemStack stack=new ItemStack(gun.getTagCompound().getCompoundTag(KEY).getCompoundTag(Integer.toString(slot)));
        return accepts(gun,slot,stack)?stack.copy():ItemStack.EMPTY;
    }
    public static boolean has(ItemStack gun,int slot,String sourceId){
        ItemStack module=get(gun,slot);return !module.isEmpty() && sourceId.equals(((ModuleItem)module.getItem()).entry.sourceId);
    }
    private static void store(ItemStack gun,int slot,ItemStack module){
        NBTTagCompound root=gun.hasTagCompound()?gun.getTagCompound():new NBTTagCompound();
        NBTTagCompound modules=root.getCompoundTag(KEY);
        if(module.isEmpty())modules.removeTag(Integer.toString(slot));else modules.setTag(Integer.toString(slot),module.writeToNBT(new NBTTagCompound()));
        if(modules.isEmpty())root.removeTag(KEY);else root.setTag(KEY,modules);
        gun.setTagCompound(root);
    }
    public static int count(EntityPlayer p,Item item){
        int n=0;for(int i=0;i<MagazineItem.inventoryEnd(p);i++){ItemStack s=p.inventory.getStackInSlot(i);if(s.getItem()==item)n+=s.getCount();}return n;
    }
    /** Called only for the exact server container/window. No swap, toss, cursor, creative duplication or free refunds. */
    public static boolean change(EntityPlayer p,ItemStack gun,int slot,int choice){
        if(p.world.isRemote || !(p.openContainer instanceof NativeModuleContainer) || !p.openContainer.canInteractWith(p)
                || p.getHeldItemMainhand()!=gun || slot<0 || slot>=5 || choice<0 || choice>ENTRIES.length || !p.inventory.getItemStack().isEmpty())return false;
        BlackrazidGunItem item=(BlackrazidGunItem)gun.getItem();ItemStack installed=get(gun,slot);
        if(choice==0){
            if(installed.isEmpty())return false;
            // Removing quad barrels must NEVER discard two loaded shells as the old source did.
            if(has(gun,slot,"QuadBarrel") && item.ammo(gun)>2)return false;
            for(int i=0;i<MagazineItem.inventoryEnd(p);i++)if(p.inventory.getStackInSlot(i).isEmpty()){
                store(gun,slot,ItemStack.EMPTY);p.inventory.setInventorySlotContents(i,installed);p.inventory.markDirty();return true;
            }
            return false;
        }
        if(!installed.isEmpty())return false;Entry entry=ENTRIES[choice-1];
        for(int i=0;i<MagazineItem.inventoryEnd(p);i++){
            ItemStack source=p.inventory.getStackInSlot(i);
            if(source.getItem()!=BlackrazidWeapons.item(entry.id) || source.getCount()!=1 || !accepts(gun,slot,source))continue;
            store(gun,slot,source.copy());p.inventory.setInventorySlotContents(i,ItemStack.EMPTY);p.inventory.markDirty();return true;
        }
        return false;
    }
    /** Explicit client optical adaptation, not a claim that the old multipart scopes are rendered. */
    public static float zoom(ItemStack gun){
        for(int slot=0;slot<5;slot++){
            ItemStack module=get(gun,slot);if(module.isEmpty())continue;String id=((ModuleItem)module.getItem()).entry.sourceId;
            if(id.equals("Kolimator"))return 1.5F;if(id.equals("OpticaSmall"))return 2;if(id.equals("Optica") || id.equals("TiharOptica"))return 4;
        }
        return 1.2F;
    }
    public static boolean laser(ItemStack gun){return has(gun,0,"Laser") || has(gun,1,"Laser");}
}
