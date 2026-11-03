"""Compile/test-only API facades. NEVER package net.*, io.* or com.* stubs in the patched JAR.
They make isolated rule/hook tests reproducible without claiming Minecraft/Forge runtime coverage.
"""
from pathlib import Path

STUBS = {
'net/minecraft/item/Item.java': '''package net.minecraft.item;
public class Item { public final String id; public final int limit;
 public static java.util.Map<String,Item> registry=new java.util.HashMap<String,Item>();
 public Item(){this("test:unknown",64);} public Item(String id,int limit){this.id=id;this.limit=limit;registry.put(id,this);}
 public net.minecraft.util.ResourceLocation getRegistryName(){return new net.minecraft.util.ResourceLocation(id);} }
''',
'net/minecraft/util/ResourceLocation.java': '''package net.minecraft.util; public class ResourceLocation {
 private final String value;public ResourceLocation(String s){value=s;} public String toString(){return value;} }
''',
'net/minecraft/nbt/NBTBase.java': 'package net.minecraft.nbt; public class NBTBase {}',
'net/minecraft/nbt/NBTTagCompound.java': '''package net.minecraft.nbt;
public class NBTTagCompound extends NBTBase {
 public java.util.Map<String,Object> values=new java.util.HashMap<String,Object>();
 public void setInteger(String k,int v){values.put(k,v);}public int getInteger(String k){Object v=values.get(k);return v instanceof Number?((Number)v).intValue():0;}
 public void setLong(String k,long v){values.put(k,v);}public long getLong(String k){Object v=values.get(k);return v instanceof Number?((Number)v).longValue():0;}
 public void setByte(String k,byte v){values.put(k,v);}public byte getByte(String k){return(byte)getInteger(k);}
 public void setString(String k,String v){values.put(k,v);}public String getString(String k){Object v=values.get(k);return v instanceof String?(String)v:"";}
 public void setTag(String k,NBTBase v){values.put(k,v);}public NBTTagCompound getCompoundTag(String k){Object v=values.get(k);return v instanceof NBTTagCompound?(NBTTagCompound)v:new NBTTagCompound();}
 public boolean hasKey(String k){return values.containsKey(k);}public void removeTag(String k){values.remove(k);}
 public java.util.Set<String> getKeySet(){return values.keySet();}
 public NBTTagCompound copy(){NBTTagCompound n=new NBTTagCompound();for(String k:values.keySet()){Object v=values.get(k);n.values.put(k,v instanceof NBTTagCompound?((NBTTagCompound)v).copy():v);}return n;}
 public boolean equals(Object n){return n instanceof NBTTagCompound&&values.equals(((NBTTagCompound)n).values);}public int hashCode(){return values.hashCode();}
}''',
'net/minecraft/item/ItemStack.java': '''package net.minecraft.item; import net.minecraft.nbt.NBTTagCompound;
public class ItemStack {
 public static final ItemStack EMPTY=new ItemStack((Item)null,0);public Item item;public int count;public NBTTagCompound tag;
 public ItemStack(){this((Item)null,0);}public ItemStack(Item i){this(i,1);}public ItemStack(Item i,int c){item=i;count=c;}
 public ItemStack(NBTTagCompound n){String id=n.getString("id");item=Item.registry.get(id);if(item==null&&!id.isEmpty())item=new Item(id,64);count=n.getByte("Count");tag=n.hasKey("tag")?n.getCompoundTag("tag").copy():null;}
 public boolean isEmpty(){return item==null||count<=0;}public Item getItem(){return item;}public int getCount(){return count;}public void setCount(int n){count=n;}
 public int getMaxStackSize(){return item==null?64:item.limit;}public NBTTagCompound getTagCompound(){return tag;}public void setTagCompound(NBTTagCompound n){tag=n;}
 public ItemStack copy(){ItemStack s=new ItemStack(item,count);s.tag=tag==null?null:tag.copy();return s;}
 public NBTTagCompound writeToNBT(NBTTagCompound n){n.setString("id",item==null?"":item.id);n.setByte("Count",(byte)count);if(tag!=null)n.setTag("tag",tag.copy());return n;}
}''',
'net/minecraft/entity/player/EntityPlayer.java': 'package net.minecraft.entity.player; public class EntityPlayer {}',
'net/minecraft/entity/player/EntityPlayerMP.java': 'package net.minecraft.entity.player; public class EntityPlayerMP extends EntityPlayer {}',
'net/minecraft/util/text/TextComponentString.java': '''package net.minecraft.util.text;public class TextComponentString {
 public final String text;public TextComponentString(String s){text=s;}public String toString(){return text;}}''',
'net/minecraftforge/fml/common/eventhandler/Event.java': '''package net.minecraftforge.fml.common.eventhandler;
public class Event {private boolean canceled;public void setCanceled(boolean b){canceled=b;}public boolean isCanceled(){return canceled;}}''',
'net/minecraftforge/fml/common/eventhandler/EventPriority.java': 'package net.minecraftforge.fml.common.eventhandler; public enum EventPriority {HIGHEST,HIGH,NORMAL,LOW,LOWEST}',
'net/minecraftforge/fml/common/eventhandler/SubscribeEvent.java': '''package net.minecraftforge.fml.common.eventhandler;
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME) @java.lang.annotation.Target(java.lang.annotation.ElementType.METHOD)
public @interface SubscribeEvent {EventPriority priority() default EventPriority.NORMAL;boolean receiveCanceled() default false;}''',
'net/minecraftforge/fml/common/eventhandler/Cancelable.java': '''package net.minecraftforge.fml.common.eventhandler;
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME) public @interface Cancelable {}''',
'net/minecraftforge/fml/common/gameevent/TickEvent.java': '''package net.minecraftforge.fml.common.gameevent;
public class TickEvent extends net.minecraftforge.fml.common.eventhandler.Event {
 public enum Phase {START,END}public Phase phase=Phase.END;
 public static class PlayerTickEvent extends TickEvent {public net.minecraft.entity.player.EntityPlayer player;}
 public static class ClientTickEvent extends TickEvent {}
}''',
'net/minecraftforge/fml/common/gameevent/PlayerEvent.java': '''package net.minecraftforge.fml.common.gameevent;
public class PlayerEvent extends net.minecraftforge.fml.common.eventhandler.Event {
 public net.minecraft.entity.player.EntityPlayer player;public static class PlayerLoggedOutEvent extends PlayerEvent {}}
''',
'net/minecraftforge/client/event/RenderGameOverlayEvent.java': '''package net.minecraftforge.client.event;
public class RenderGameOverlayEvent extends net.minecraftforge.fml.common.eventhandler.Event {
 public enum ElementType {ALL}public ElementType getType(){return ElementType.ALL;}public static class Post extends RenderGameOverlayEvent {}}
''',
'io/netty/buffer/ByteBuf.java': '''package io.netty.buffer; public class ByteBuf {private int value;public ByteBuf writeByte(int n){value=n&255;return this;}public int readUnsignedByte(){return value;}}''',
'io/netty/channel/ChannelHandlerContext.java': 'package io.netty.channel; public interface ChannelHandlerContext {}',
}

# Test-only replacements for reflective backend calls; production compiles against the real MWF JAR.
TEST_BACKEND = {
'com/modularwarfare/common/guns/ItemGun.java': '''package com.modularwarfare.common.guns;
public class ItemGun extends net.minecraft.item.Item {public Object type;public ItemGun(String name,Object type){super("modularwarfare:"+name,1);this.type=type;}
 public static boolean hasNextShot(net.minecraft.item.ItemStack stack){return stack.tag!=null&&stack.tag.getInteger("ammocount")>0;}}
''',
'com/modularwarfare/common/handler/ServerTickHandler.java': '''package com.modularwarfare.common.handler;public class ServerTickHandler {
 public static java.util.Map<Object,Object> playerReloadCooldown=new java.util.HashMap<Object,Object>();
 public static java.util.Map<Object,Object> reloadEnhancedTask=new java.util.HashMap<Object,Object>();}
''',
'net/minecraftforge/fml/common/FMLCommonHandler.java': '''package net.minecraftforge.fml.common;public class FMLCommonHandler {
 public enum Side{CLIENT,SERVER}public static Side side=Side.SERVER;public static FMLCommonHandler instance(){return new FMLCommonHandler();}
 public Side getEffectiveSide(){return side;}public Side getSide(){return side;}}
''',
'com/modularwarfare/ModularWarfare.java': '''package com.modularwarfare; public class ModularWarfare {public static Object NETWORK=null,PROXY=null;}''',
'com/modularwarfare/common/network/PacketPlaySound.java': '''package com.modularwarfare.common.network;public class PacketPlaySound {public PacketPlaySound(Object pos,String name,float volume,float pitch){}}''',
}


def materialize(root, test_backend=False):
    root = Path(root)
    for name, text in (TEST_BACKEND if test_backend else STUBS).items():
        path = root / name
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(text, encoding='utf-8')
    return sorted(str(p) for p in root.rglob('*.java'))
