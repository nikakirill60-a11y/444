package ru.metrocraft.port.blackrazid.weapons;

import com.google.gson.Gson;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.metrocraft.port.MetroPort;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Own native items; never aliases for similarly named MCMOD guns or ammunition. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class BlackrazidWeapons {
    public static final class Entry {
        public String id,sourceId,kind,icon,nameRu,nameEn,roundItem,shootSound,reloadSound,unloadSound,runtimeStatus,looseRound,looseReloadSound,cycleSound,pumpSound,emptySound;
        public String[] magazines;
        public int capacity,defaultRounds,sourceCapacityArgument,loadingTicks,unloadingTicks,delay,accuracy,pellets,looseCapacity,looseLoadingTicks,looseMinimum,airCapacity,airStep,pumpTicks,airCost;
        public float damage,speed,recoil;
        public boolean refillable,topUp,keepChamber,rotor,flame,cycleAfterShot;
    }
    private static final Map<String,Item> ITEMS=new LinkedHashMap<>();
    private static Entry[] entries;
    private BlackrazidWeapons(){}
    public static synchronized void load(){
        if(entries!=null)return;
        try(Reader reader=new InputStreamReader(Objects.requireNonNull(BlackrazidWeapons.class.getResourceAsStream("/assets/metroport/blackrazid_weapons.json")),StandardCharsets.UTF_8)){
            entries=new Gson().fromJson(reader,Entry[].class);
            for(Entry e:entries){
                Item item;
                switch(e.kind){case "round":item=new Item();break;case "magazine":item=new MagazineItem(e);break;case "gun":item=new BlackrazidGunItem(e);break;default:throw new IllegalStateException(e.kind);}
                item.setRegistryName(MetroPort.ID,e.id).setTranslationKey("metroport."+e.id).setCreativeTab(MetroPort.WEAPONS);
                if(ITEMS.put(e.id,item)!=null)throw new IllegalStateException("Duplicate BlackRazid item "+e.id);
            }
            for(BlackrazidModules.Entry e:BlackrazidModules.entries()){
                Item item=new BlackrazidModules.ModuleItem(e).setRegistryName(MetroPort.ID,e.id).setTranslationKey("metroport."+e.id).setCreativeTab(MetroPort.WEAPONS);
                if(ITEMS.put(e.id,item)!=null)throw new IllegalStateException("Duplicate module "+e.id);
            }
        }catch(IOException ex){throw new IllegalStateException("Cannot load BlackRazid firearms",ex);}
    }
    public static Item item(String id){load();return ITEMS.get(id);}
    public static Map<String,Item> items(){load();return Collections.unmodifiableMap(ITEMS);}
    @SubscribeEvent public static void registerItems(RegistryEvent.Register<Item> event){
        load();for(Map.Entry<String,Item> e:ITEMS.entrySet()){
            if(MetroPort.ITEMS.put(e.getKey(),e.getValue())!=null)throw new IllegalStateException("Duplicate item "+e.getKey());
            event.getRegistry().register(e.getValue());
        }
    }
    private static void cancelActions(net.minecraft.entity.player.EntityPlayer player){
        if(player.world.isRemote)return;
        for(int i=0;i<player.inventory.getSizeInventory();i++){
            net.minecraft.item.ItemStack stack=player.inventory.getStackInSlot(i);
            if(stack.getItem() instanceof BlackrazidGunItem)((BlackrazidGunItem)stack.getItem()).cancel(stack);
        }
        player.inventory.markDirty();
    }
    @SubscribeEvent public static void death(net.minecraftforge.event.entity.living.LivingDeathEvent event){
        if(event.getEntityLiving() instanceof net.minecraft.entity.player.EntityPlayer)cancelActions((net.minecraft.entity.player.EntityPlayer)event.getEntityLiving());
    }
    @SubscribeEvent public static void logout(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent event){cancelActions(event.player);}
    @SubscribeEvent public static void dimension(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event){cancelActions(event.player);}
    @SubscribeEvent public static void registerSounds(RegistryEvent.Register<SoundEvent> event){
        load();Set<String> ids=new LinkedHashSet<>();ids.add("blackrazid_empty");ids.add("blackrazid_vsv_shootclose");ids.add("blackrazid_gatling_rot");ids.add("blackrazid_saiga_closeshoot");ids.add("blackrazid_abzac_fastshoot");
        for(Entry e:entries)if("gun".equals(e.kind)){for(String id:new String[]{e.shootSound,e.reloadSound,e.unloadSound,e.looseReloadSound,e.cycleSound,e.pumpSound,e.emptySound})if(id!=null && !id.equals("blackrazid_railgun_air"))ids.add(id);}
        for(String id:ids){ResourceLocation key=new ResourceLocation(MetroPort.ID,id);SoundEvent sound=new SoundEvent(key).setRegistryName(key);
            if(MetroPort.SOUNDS.put(id,sound)!=null)throw new IllegalStateException("Duplicate sound "+id);event.getRegistry().register(sound);}
    }
}
