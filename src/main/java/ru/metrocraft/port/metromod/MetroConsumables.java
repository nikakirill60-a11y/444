package ru.metrocraft.port.metromod;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.metrocraft.port.MetroPort;

/** Confirmed donor constructor profiles only. No new medicine or duplicate flask/medkit. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class MetroConsumables {
    private MetroConsumables() {}
    public enum Profile {
        KONSERVI("konservi",3,.3F,false), GALETI("galeti",2,.2F,false),
        GRIBTEA("gribtea",3,.35F,false), WATERBOTTLE("waterbottle",0,0,true);
        public final String sourceId;
        public final int hunger;
        public final float saturation;
        public final boolean water;
        Profile(String id,int hunger,float saturation,boolean water){this.sourceId=id;this.hunger=hunger;this.saturation=saturation;this.water=water;}
        public String id(){return "metromod_"+sourceId;}
    }
    @SubscribeEvent public static void register(RegistryEvent.Register<Item> event){
        for(Profile profile:Profile.values()){
            Item item=new MetroFoodItem(profile).setRegistryName(MetroPort.ID,profile.id())
                    .setTranslationKey(MetroPort.ID+"."+profile.id()).setCreativeTab(MetroPort.THINGS);
            event.getRegistry().register(item);MetroPort.ITEMS.put(profile.id(),item);
        }
    }
}
