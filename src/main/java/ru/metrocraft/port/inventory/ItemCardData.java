package ru.metrocraft.port.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import ru.metrocraft.port.MetroArmor;
import java.lang.reflect.Field;
import java.util.*;

/** Read-only card snapshot. No invented ammunition, no dependency on MWF classes at link time. */
public final class ItemCardData {
    public final String name,id,weight,primary,secondary;
    public final int count;
    private ItemCardData(ItemStack stack,String primary,String secondary){
        name=stack.getDisplayName();id=stack.getItem().getRegistryName()==null?"":stack.getItem().getRegistryName().toString();
        weight=ItemMetrics.kg(ItemMetrics.grams(stack));count=stack.getCount();this.primary=primary;this.secondary=secondary;
    }
    public static ItemCardData of(ItemStack stack,EntityPlayer viewer){
        String first="",second="";
        if(LoadoutRules.firearm(stack)){
            Object type=field(stack.getItem(),"type");Integer capacity=number(field(type,"internalAmmoStorage"));
            ItemStack magazine=nested(stack,"ammo");
            if(capacity==null&&!magazine.isEmpty())capacity=number(field(field(magazine.getItem(),"type"),"ammoCapacity"));
            // The installed compatibility hook accounts for the Duplet's optional extra barrels.
            if(type!=null)try{
                Object value=Class.forName("ru.metrocraft.mwf.MechanicsHooks").getMethod("capacity",Object.class,Object.class).invoke(null,type,stack);
                if(value instanceof Integer)capacity=(Integer)value;
            }catch(ReflectiveOperationException|LinkageError ignored){}
            int loaded=ammoCount(stack.hasTagCompound()&&stack.getTagCompound().hasKey("ammocount",3)?stack:magazine);
            first=loaded+"/"+(capacity==null?"—":Math.max(0,capacity))+"/"+reserve(viewer,type);
            ItemStack bullet=nested(stack,"bullet");if(bullet.isEmpty())bullet=nested(magazine,"bullet");
            if(!bullet.isEmpty())second=bullet.getDisplayName();
            else{
                Object accepted=field(type,"acceptedBullets");
                if(accepted instanceof String[]&&((String[])accepted).length==1){
                    Item item=Item.REGISTRY.getObject(new ResourceLocation("modularwarfare",((String[])accepted)[0]));
                    if(item!=null&&item!=net.minecraft.init.Items.AIR)second=new ItemStack(item).getDisplayName();
                }
            }
        }else{
            if(stack.isItemStackDamageable())first=Math.max(0,stack.getMaxDamage()-stack.getItemDamage())+"/"+stack.getMaxDamage();
            int bonus=ItemMetrics.capacityBonus(stack);if(bonus>0)second="+"+ItemMetrics.kg(bonus)+" kg";
            else if(stack.getItem() instanceof MetroArmor)second=String.format(Locale.ROOT,"%.1f%%",((MetroArmor)stack.getItem()).getDefinition().defence*100);
            else second=ItemMetrics.width(stack)+"×"+ItemMetrics.height(stack);
        }
        return new ItemCardData(stack,first,second);
    }
    public static int ammoCount(ItemStack stack){
        if(stack.isEmpty()||!stack.hasTagCompound())return 0;
        NBTTagCompound tag=stack.getTagCompound();long total=0;
        for(String key:tag.getKeySet())if(key.equals("ammocount")||key.matches("ammocount[0-9]+"))total+=Math.max(0,tag.getInteger(key));
        return (int)Math.min(Integer.MAX_VALUE,total);
    }
    private static int reserve(EntityPlayer viewer,Object type){
        if(viewer==null)return 0;long total=0;
        Set<String> bullets=strings(field(type,"acceptedBullets")),magazines=strings(field(type,"acceptedAmmo"));
        for(ItemStack stack:viewer.inventory.mainInventory){
            if(stack.isEmpty()||stack.getItem().getRegistryName()==null)continue;
            ResourceLocation id=stack.getItem().getRegistryName();if(!id.getNamespace().equals("modularwarfare"))continue;
            if(bullets.contains(id.getPath()))total+=stack.getCount();
            else if(magazines.contains(id.getPath()))total+=(long)ammoCount(stack)*stack.getCount();
        }
        return (int)Math.min(Integer.MAX_VALUE,total);
    }
    private static Set<String> strings(Object value){return value instanceof String[]?new HashSet<>(Arrays.asList((String[])value)):Collections.emptySet();}
    private static Integer number(Object value){return value instanceof Number?Math.max(0,((Number)value).intValue()):null;}
    private static ItemStack nested(ItemStack stack,String key){return !stack.isEmpty()&&stack.hasTagCompound()&&stack.getTagCompound().hasKey(key,10)?new ItemStack(stack.getTagCompound().getCompoundTag(key).copy()):ItemStack.EMPTY;}
    private static final Map<Class<?>,Map<String,Field>> FIELDS=new WeakHashMap<>();
    private static Object field(Object value,String name){
        if(value==null)return null;
        try{
            Map<String,Field> fields=FIELDS.computeIfAbsent(value.getClass(),key->new HashMap<>());
            if(!fields.containsKey(name)){Field field=null;try{field=value.getClass().getField(name);}catch(NoSuchFieldException ignored){}fields.put(name,field);}
            Field field=fields.get(name);return field==null?null:field.get(value);
        }catch(IllegalAccessException|SecurityException ignored){return null;}
    }
}
