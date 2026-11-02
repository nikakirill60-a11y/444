package net.minecraft.nbt;
import java.util.*;
public class NBTTagCompound {final Map<String,Object> values=new HashMap<String,Object>();
 public void setInteger(String k,int n){values.put(k,n);} public int getInteger(String k){return values.containsKey(k)?((Number)values.get(k)).intValue():0;}
 public void setString(String k,String s){values.put(k,s);}public String getString(String k){return values.containsKey(k)?String.valueOf(values.get(k)):"";}
 public void setTag(String k,NBTTagCompound n){values.put(k,n);}public NBTTagCompound getCompoundTag(String k){Object v=values.get(k);return v instanceof NBTTagCompound?(NBTTagCompound)v:new NBTTagCompound();}
 public boolean hasKey(String k){return values.containsKey(k);}public Set<String> getKeySet(){return values.keySet();}public void removeTag(String k){values.remove(k);}
 public NBTTagCompound copy(){NBTTagCompound n=new NBTTagCompound();for(String k:values.keySet()){Object v=values.get(k);n.values.put(k,v instanceof NBTTagCompound?((NBTTagCompound)v).copy():v);}return n;}
 public boolean equals(Object o){return o instanceof NBTTagCompound&&values.equals(((NBTTagCompound)o).values);}public int hashCode(){return values.hashCode();}public String toString(){return new TreeMap<String,Object>(values).toString();}}
