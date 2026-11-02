package net.minecraft.item;import net.minecraft.nbt.NBTTagCompound;
public class ItemStack {private Item item;private int count,damage;private NBTTagCompound tag,forge;
 public ItemStack(String id,int n){item=new Item(id);count=n;}
 public ItemStack(NBTTagCompound n){this(n.getString("id"),n.getInteger("Count"));if(!Item.fourRegistered&&n.getString("id").equals("modularwarfare:metro_duplet_4"))count=0;damage=n.getInteger("Damage");if(n.hasKey("tag"))tag=n.getCompoundTag("tag").copy();if(n.hasKey("ForgeCaps"))forge=n.getCompoundTag("ForgeCaps").copy();}
 public boolean isEmpty(){return count<=0||item.getRegistryName().isEmpty();}public int getCount(){return count;}public void setCount(int n){count=n;}public int getMaxStackSize(){return item.max();}public Item getItem(){return item;}public int getItemDamage(){return damage;}public void setItemDamage(int n){damage=n;}
 public NBTTagCompound getTagCompound(){return tag;}public void setTagCompound(NBTTagCompound n){tag=n;}
 public NBTTagCompound writeToNBT(NBTTagCompound n){n.setString("id",item.getRegistryName());n.setInteger("Count",count);n.setInteger("Damage",damage);if(tag!=null)n.setTag("tag",tag.copy());if(forge!=null)n.setTag("ForgeCaps",forge.copy());return n;}
 public ItemStack copy(){return new ItemStack(writeToNBT(new NBTTagCompound()));}public String toString(){return writeToNBT(new NBTTagCompound()).toString();}}
