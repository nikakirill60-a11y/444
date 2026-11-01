package ru.metrocraft.port.blackrazid;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.blackrazid.survival.*;
import ru.metrocraft.port.blackrazid.grenades.*;

/** Development expansion. See BLACKRAZID_EXPANSION.md: this is not a completed full-port release. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class BlackrazidExpansion {
    private BlackrazidExpansion(){}
    public static void initialize(){
        EntityRegistry.registerModEntity(new ResourceLocation(MetroPort.ID,"blackrazid_bullet"),ru.metrocraft.port.blackrazid.weapons.BlackrazidBullet.class,"blackrazid_bullet",71,MetroPort.INSTANCE,96,1,true);
        EntityRegistry.registerModEntity(new ResourceLocation(MetroPort.ID,"blackrazid_grenade"),GrenadeEntity.class,"blackrazid_grenade",70,MetroPort.INSTANCE,64,1,true);}
    private static void item(RegistryEvent.Register<Item> event,String id,Item item,boolean weapon){
        item.setRegistryName(MetroPort.ID,id).setTranslationKey("metroport."+id).setCreativeTab(weapon?MetroPort.WEAPONS:MetroPort.THINGS);
        if(MetroPort.ITEMS.put(id,item)!=null)throw new IllegalStateException("Duplicate BlackRazid item "+id);event.getRegistry().register(item);
    }
    @SubscribeEvent public static void items(RegistryEvent.Register<Item> event){
        item(event,"blackrazid_flyashka",new SurvivalItem(SurvivalItem.Type.FLASK),false);
        item(event,"blackrazid_lamp",new SurvivalItem(SurvivalItem.Type.LAMP),false);
        item(event,"blackrazid_power",new SurvivalItem(SurvivalItem.Type.POWER),false);
        item(event,"blackrazid_grenade",new GrenadeItem(false),true);
        item(event,"blackrazid_grenade_big",new GrenadeItem(true),true);
    }
    @SubscribeEvent public static void sounds(RegistryEvent.Register<SoundEvent> event){
        for(String id:new String[]{"blackrazid_flyashka","blackrazid_railgun_air"}){ResourceLocation key=new ResourceLocation(MetroPort.ID,id);SoundEvent s=new SoundEvent(key).setRegistryName(key);event.getRegistry().register(s);MetroPort.SOUNDS.put(id,s);}
    }
}
