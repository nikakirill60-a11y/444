package ru.metrocraft.port.metromod;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.metrocraft.port.MetroPort;

/** Independent non-weapon events. Old source events remain intact for compatibility. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class MetroAudio {
    public static final String[] IDS={"metromod_flask","metromod_medkit","metromod_filter",
            "metromod_watch_alarm_1","metromod_watch_alarm_2","metromod_watch_alarm_3",
            "metromod_gate_open","metromod_gate_close"};
    private MetroAudio() {}
    @SubscribeEvent public static void register(RegistryEvent.Register<SoundEvent> event){
        for(String id:IDS){ResourceLocation name=new ResourceLocation(MetroPort.ID,id);
            SoundEvent sound=new SoundEvent(name).setRegistryName(name);event.getRegistry().register(sound);MetroPort.SOUNDS.put(id,sound);}
    }
    public static SoundEvent get(String primary,String fallback){
        SoundEvent sound=MetroPort.SOUNDS.get(primary);return sound!=null?sound:MetroPort.SOUNDS.get(fallback);
    }
    public static String gate(boolean opening){return opening?"metromod_gate_open":"metromod_gate_close";}
    public static String alarm(int index){return "metromod_watch_alarm_"+(Math.floorMod(index,3)+1);}
}
