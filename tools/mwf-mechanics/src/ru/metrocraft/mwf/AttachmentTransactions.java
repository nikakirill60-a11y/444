package ru.metrocraft.mwf;

import com.modularwarfare.api.WeaponAttachmentEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Logger;

/** Server-side, fail-closed attachment transactions. No shared GunType mutation or bytecode patches. */
public final class AttachmentTransactions {
    static final String BASE="modularwarfare:metro_duplet", MODULE=BASE+"_extra_barrels";
    static final Map<String,String> ALLOWED=new HashMap<String,String>();
    static final Logger LOG=Logger.getLogger("MetroDupletBridge");
    static { ALLOWED.put(MODULE,"barrel"); ALLOWED.put(BASE+"_suppressor","barrel"); ALLOWED.put(BASE+"_long_barrel","barrel"); ALLOWED.put(BASE+"_grip","grip"); ALLOWED.put(BASE+"_stock","stock"); ALLOWED.put("modularwarfare:metro_mod_laser","laser"); }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public void load(WeaponAttachmentEvent.Load e) {
        if(!ours(e.gun))return;
        e.setCanceled(true); // Never let native replacement discard an old attachment on a full inventory.
        try { transact(e.player,e.gun,e.attach,null,false); }
        catch(Refusal x){tell(e.player,x.getMessage());}
        catch(Exception x){LOG.warning("Attachment operation refused: "+x);tell(e.player,"Дуплет: смена обвеса отменена. Смотри лог сервера.");}
    }
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public void unload(WeaponAttachmentEvent.Unload e) {
        if(!ours(e.gun))return;
        e.setCanceled(true);
        try { transact(e.player,e.gun,null,e.type==null?null:e.type.typeName,e.unloadAll); }
        catch(Refusal x){tell(e.player,x.getMessage());}
        catch(Exception x){LOG.warning("Attachment operation refused: "+x);tell(e.player,"Дуплет: снятие обвеса отменено. Смотри лог сервера.");}
    }
    static boolean ours(Object stack){try{String id=id(stack);return BASE.equals(id);}catch(Exception x){return false;}}
    static final class Refusal extends Exception {private static final long serialVersionUID=1L;Refusal(String message){super(message);}}

    static void transact(Object player,Object gun,Object incoming,String slot,boolean all) throws Exception {
        Object world=field(player,"world","field_70170_p");
        if(Boolean.TRUE.equals(field(world,"isRemote","field_72995_K")))throw new Refusal("Изменение обвеса выполняется сервером.");
        Object inv=field(player,"inventory","field_71071_by");
        int held=((Number)field(inv,"currentItem","field_70461_c")).intValue();
        if(held<0||held>8||call(inv,"getStackInSlot|func_70301_a",held)!=gun)throw new Refusal("Возьми Дуплет в основную руку.");
        if(count(gun)!=1)throw new Refusal("В слоте должен быть один Дуплет.");
        Object tag=call(gun,"getTagCompound|func_77978_p");
        if(tag==null)throw new Refusal("Подожди один тик после получения оружия.");
        int loaded=((Number)call(tag,"getInteger|func_74762_e","ammocount")).intValue();
        if(loaded<0||loaded>4)throw new Refusal("Некорректное количество патронов Дуплета; операция отменена.");
        Object uuid=call(player,"getUniqueID|func_110124_au");
        Class<?> handler=Class.forName("com.modularwarfare.common.handler.ServerTickHandler");
        for(String name:new String[]{"playerReloadCooldown","reloadEnhancedTask"}) {
            Object map=handler.getField(name).get(null);
            if(!(map instanceof Map))throw new Refusal("Несовместимая версия MWF.");
            if(((Map<?,?>)map).containsKey(uuid))throw new Refusal("Дождись окончания перезарядки или выгрузки.");
        }
        Object[] before=new Object[36],plan=new Object[36];
        for(int i=0;i<36;i++){before[i]=call(inv,"getStackInSlot|func_70301_a",i);plan[i]=copy(before[i]);}
        Object cursor=call(inv,"getItemStack|func_70445_o"),cursorAfter=copy(cursor);
        Object updated=copy(gun),nbt=call(updated,"getTagCompound|func_77978_p");
        List<Object> returned=new ArrayList<Object>();
        if(incoming!=null) {
            String type=ALLOWED.get(id(incoming));
            if(type==null||count(incoming)<1)throw new Refusal("Этот обвес не подходит Дуплету.");
            int source=-1;for(int i=0;i<36;i++)if(before[i]==incoming)source=i;
            if(source==held)throw new Refusal("Некорректный слот обвеса.");
            if(source>=0)call(plan[source],"setCount|func_190920_e",count(plan[source])-1);
            else if(cursor==incoming)call(cursorAfter,"setCount|func_190920_e",count(cursorAfter)-1);
            else throw new Refusal("Обвес должен находиться в инвентаре или на курсоре.");
            String key="attachment_"+type;Object old=attachment(gun,key);if(!empty(old))returned.add(old);
            Object one=copy(incoming);call(one,"setCount|func_190920_e",1);call(nbt,"setTag|func_74782_a",key,serialize(one));
        } else {
            if(!all&&!Arrays.asList("barrel","grip","stock","laser").contains(slot))throw new Refusal("У Дуплета нет такого слота.");
            Set<?> keys=new HashSet<Object>((Set<?>)call(nbt,"getKeySet|func_150296_c"));
            for(Object key:keys)if(key.toString().startsWith("attachment_")&&(all||key.equals("attachment_"+slot))) {
                Object old=attachment(gun,key.toString());if(!empty(old))returned.add(old);
                call(nbt,"removeTag|func_82580_o",key.toString());
            }
        }
        // Capacity is read dynamically from this stack; never swap item IDs or mutate GunType.
        if(!BASE.equals(id(updated))||count(updated)!=1)throw new Refusal("Некорректный Дуплет.");
        int capacity=MechanicsHooks.capacity(field(call(updated,"getItem|func_77973_b"),"type"),updated).intValue();
        int excess=Math.max(0,loaded-capacity);
        Object extraRounds=null;
        if(excess>0) {
            // Native loose-ammo MWF stores the actual loaded bullet ItemStack under "bullet".
            // Never invent default rounds or discard custom bullet NBT when reducing 4 -> 2.
            if(!Boolean.TRUE.equals(call(tag,"hasKey|func_74764_b","bullet")))
                throw new Refusal("Не найден тип заряженных патронов; операция отменена без потерь.");
            extraRounds=fromNbt(gun,call(tag,"getCompoundTag|func_74775_l","bullet"));
            if(empty(extraRounds)||!Class.forName("com.modularwarfare.common.guns.ItemBullet").isInstance(call(extraRounds,"getItem|func_77973_b")))
                throw new Refusal("Некорректный тип заряженных патронов.");
            Object gunType=field(call(gun,"getItem|func_77973_b"),"type");
            Object accepted=field(gunType,"acceptedBullets");
            boolean compatible=false;
            if(accepted instanceof String[])for(String allowed:(String[])accepted)
                if(allowed.equals(MechanicsHooks.id(extraRounds)))compatible=true;
            if(!compatible)throw new Refusal("Заряженные патроны не подходят Дуплету.");
            call(extraRounds,"setCount|func_190920_e",excess);
            call(nbt,"setInteger|func_74768_a","ammocount",loaded-excess);
        }
        plan[held]=updated;
        for(Object item:returned){if(count(item)!=1)throw new Refusal("Некорректное количество сохранённого обвеса.");if(!insert(plan,item))throw new Refusal("Освободи место в инвентаре для снятого обвеса.");}
        if(extraRounds!=null&&!insert(plan,extraRounds))
            throw new Refusal("Освободи место для лишних патронов; обвес и заряд не изменены.");
        // All validation, consumption and return-space checks happen on copies first.
        // Native inventory writes run together on the server thread, with rollback if an API call fails.
        try {
            for(int i=0;i<36;i++)if(!sameFull(before[i],plan[i]))call(inv,"setInventorySlotContents|func_70299_a",i,plan[i]);
            if(!sameFull(cursor,cursorAfter))call(inv,"setItemStack|func_70437_b",cursorAfter);
        } catch(Exception x) {
            for(int i=0;i<36;i++)call(inv,"setInventorySlotContents|func_70299_a",i,before[i]);
            call(inv,"setItemStack|func_70437_b",cursor);throw x;
        }
        call(inv,"markDirty|func_70296_d");
        MechanicsHooks.sync(player);
    }
    static boolean insert(Object[] slots,Object item) throws Exception {
        Object remainder=copy(item);
        for(int pass=0;pass<2;pass++)for(int i=0;i<slots.length;i++) {
            if(empty(remainder))return true;
            if(pass==0&&!empty(slots[i])&&same(slots[i],remainder)) {
                int max=((Number)call(slots[i],"getMaxStackSize|func_77976_d")).intValue();
                int n=Math.min(max-count(slots[i]),count(remainder));
                if(n>0){call(slots[i],"setCount|func_190920_e",count(slots[i])+n);call(remainder,"setCount|func_190920_e",count(remainder)-n);}
            } else if(pass==1&&empty(slots[i])) {slots[i]=copy(remainder);return true;}
        }
        return empty(remainder);
    }
    static Object attachment(Object gun,String key) throws Exception {
        Object nbt=call(gun,"getTagCompound|func_77978_p");if(nbt==null)return null;
        if(!Boolean.TRUE.equals(call(nbt,"hasKey|func_74764_b",key)))return null;
        return fromNbt(gun,call(nbt,"getCompoundTag|func_74775_l",key));
    }
    static Object serialize(Object stack) throws Exception {
        return call(stack,"writeToNBT|func_77955_b",new NBTTagCompound());
    }
    static Object fromNbt(Object sample,Object tag) throws Exception {
        for(Constructor<?> c:sample.getClass().getConstructors())if(c.getParameterTypes().length==1&&c.getParameterTypes()[0].isInstance(tag))return c.newInstance(tag);
        throw new NoSuchMethodException("ItemStack(NBTTagCompound)");
    }
    static String id(Object stack) throws Exception {if(empty(stack))return "";return String.valueOf(call(call(stack,"getItem|func_77973_b"),"getRegistryName"));}
    static boolean empty(Object stack) throws Exception {return stack==null||Boolean.TRUE.equals(call(stack,"isEmpty|func_190926_b"));}
    static int count(Object stack) throws Exception {return empty(stack)?0:((Number)call(stack,"getCount|func_190916_E")).intValue();}
    static Object copy(Object stack) throws Exception {return stack==null?null:call(stack,"copy|func_77946_l");}
    static boolean same(Object a,Object b) throws Exception {
        Object aa=serialize(a),bb=serialize(b);call(aa,"setInteger|func_74768_a","Count",1);call(bb,"setInteger|func_74768_a","Count",1);return aa.equals(bb);
    }
    static boolean sameFull(Object a,Object b) throws Exception {return empty(a)&&empty(b)||!empty(a)&&!empty(b)&&count(a)==count(b)&&same(a,b);}
    static void tell(Object player,String message){try{call(player,"sendStatusMessage|func_146105_b",new TextComponentString(message),false);}catch(Exception x){LOG.info(message);}}
    static Object field(Object target,String... names) throws Exception {
        for(Class<?> cls=target.getClass();cls!=null;cls=cls.getSuperclass())for(String name:names)try{Field f=cls.getDeclaredField(name);f.setAccessible(true);return f.get(target);}catch(NoSuchFieldException ignored){}
        throw new NoSuchFieldException(Arrays.toString(names));
    }
    static Object call(Object target,String names,Object... args) throws Exception {
        if(target==null)throw new NullPointerException(names);
        for(String name:names.split("\\|"))for(Method m:target.getClass().getMethods())if(m.getName().equals(name)&&m.getParameterTypes().length==args.length) {
            boolean match=true;Class<?>[] types=m.getParameterTypes();
            for(int i=0;i<args.length;i++){Class<?> t=types[i];if(t==int.class)t=Integer.class;if(t==boolean.class)t=Boolean.class;if(args[i]!=null&&!t.isInstance(args[i]))match=false;}
            if(match)try{return m.invoke(target,args);}catch(InvocationTargetException x){Throwable c=x.getCause();if(c instanceof Exception)throw (Exception)c;throw x;}
        }
        throw new NoSuchMethodException(target.getClass().getName()+"."+names);
    }
}
