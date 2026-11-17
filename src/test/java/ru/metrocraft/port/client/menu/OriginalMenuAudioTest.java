package ru.metrocraft.port.client.menu;

import net.minecraft.client.audio.*;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.mockito.ArgumentCaptor;
import java.util.*;

public class OriginalMenuAudioTest {
    @Test public void introDoesNotDuplicateWhilePlaying() {
        SoundHandler handler=mock(SoundHandler.class);
        when(handler.isSoundPlaying(any(ISound.class))).thenReturn(true);
        OriginalMenuAudio audio=new OriginalMenuAudio(handler);
        audio.tick(0);audio.tick(0);audio.tick(0);
        verify(handler,times(1)).playSound(any(ISound.class));
        audio.close();verify(handler,times(1)).stopSound(any(ISound.class));
    }
    @Test public void movingSceneAndSelectionUseExactlyTheSixOriginalEvents() {
        SoundHandler handler=mock(SoundHandler.class);
        when(handler.isSoundPlaying(any(ISound.class))).thenReturn(true);
        OriginalMenuAudio audio=new OriginalMenuAudio(handler);audio.tick(.15f);audio.selected();
        ArgumentCaptor<ISound> sounds=ArgumentCaptor.forClass(ISound.class);
        verify(handler,times(6)).playSound(sounds.capture());
        Set<String> names=new HashSet<>();
        for(ISound sound:sounds.getAllValues()) {
            names.add(sound.getSoundLocation().toString());
            assertEquals(ISound.AttenuationType.NONE,sound.getAttenuationType());
            assertFalse(sound.canRepeat());
        }
        assertEquals(new HashSet<>(Arrays.asList("metroport:metromod.menu.menu_intro_loop","metroport:metromod.menu.regina_engine_idle",
            "metroport:metromod.menu.menu_carnoise","metroport:metromod.menu.menu_railnoise",
            "metroport:metromod.menu.menu_wheel_knock_noise","metroport:metromod.menu.switch2_light")),names);
        audio.close();for(ISound sound:sounds.getAllValues())verify(handler).stopSound(sound);
        verify(handler,never()).stopSounds();
    }
    @Test public void closeIsIdempotentAndNextOpenCanPlayAgain() {
        SoundHandler handler=mock(SoundHandler.class);
        OriginalMenuAudio audio=new OriginalMenuAudio(handler);audio.tick(0);audio.close();audio.close();
        verify(handler,times(1)).stopSound(any(ISound.class));
        audio.tick(0);verify(handler,times(2)).playSound(any(ISound.class));
    }
}
