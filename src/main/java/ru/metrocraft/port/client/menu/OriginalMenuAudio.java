package ru.metrocraft.port.client.menu;

import java.util.*;
import net.minecraft.client.audio.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

/** Owns only this screen's sound handles; never stops game/weapon sounds. */
final class OriginalMenuAudio implements AutoCloseable {
    private final SoundHandler handler;
    private final Map<String, ISound> owned = new LinkedHashMap<>();
    private final Random random = new Random();
    private int knockTicks;
    OriginalMenuAudio(SoundHandler handler) { this.handler = handler; }
    private void play(String name, float volume, SoundCategory category, boolean restart) {
        ISound previous = owned.get(name);
        if (previous != null && handler.isSoundPlaying(previous)) {
            if (!restart) return;
            handler.stopSound(previous);
        }
        ISound sound = new PositionedSoundRecord(new ResourceLocation("metroport", "metromod.menu."+name),
            category, volume, 1, false, 0, ISound.AttenuationType.NONE, 0, 0, 0);
        owned.put(name,sound); handler.playSound(sound);
    }
    void tick(float speed) {
        play("menu_intro_loop",1,SoundCategory.AMBIENT,false);
        if(speed>.01f)play("regina_engine_idle",.7f,SoundCategory.AMBIENT,false);
        if(speed>.14f) {
            play("menu_carnoise",.4f,SoundCategory.AMBIENT,false);
            play("menu_railnoise",.3f,SoundCategory.AMBIENT,false);
        }
        // Donor interval is 240..480 units at its nominal 40-unit clock.
        if(speed>.1f && --knockTicks<=0) {
            play("menu_wheel_knock_noise",.5f,SoundCategory.AMBIENT,true);
            knockTicks=120+random.nextInt(121);
        }
    }
    void selected() { play("switch2_light",.25f,SoundCategory.MASTER,true); }
    @Override public void close() {
        for(ISound sound:owned.values())handler.stopSound(sound);
        owned.clear(); knockTicks=0;
    }
}
