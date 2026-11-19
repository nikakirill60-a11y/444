package ru.metrocraft.port;

import net.minecraft.init.*;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import com.google.common.collect.ImmutableList;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NamespaceMigrationTest {
    @BeforeClass public static void boot(){Bootstrap.register();}
    @Test public void onlyOldOwnNamespacesChangeNotMwfOrMinecraft(){
        assertEquals("minetro",MetroPort.ID);
        for(String old:new String[]{"metroport","metrocraftport"})assertEquals(new ResourceLocation("minetro","mcmod_watch"),NamespaceMigration.current(new ResourceLocation(old,"mcmod_watch")));
        assertEquals(new ResourceLocation("modularwarfare","metro_ak"),NamespaceMigration.current(new ResourceLocation("modularwarfare","metro_ak")));
        assertEquals(new ResourceLocation("metroport","kanistra"),NamespaceMigration.balanceId(new ResourceLocation("minetro","kanistra")));
    }
    @Test @SuppressWarnings("unchecked") public void actualMissingMappingRemapsToSameItemObject(){
        IForgeRegistry<Item> registry=mock(IForgeRegistry.class);ResourceLocation old=new ResourceLocation("metroport","card_item"),target=new ResourceLocation("minetro","card_item");
        Item item=new Item();when(registry.containsKey(target)).thenReturn(true);when(registry.getValue(target)).thenReturn(item);when(registry.getKey(item)).thenReturn(target);
        RegistryEvent.MissingMappings.Mapping<Item> mapping=new RegistryEvent.MissingMappings.Mapping<>(registry,registry,old,42);
        RegistryEvent.MissingMappings<Item> event=mock(RegistryEvent.MissingMappings.class);when(event.getAllMappings()).thenReturn(ImmutableList.of(mapping));
        NamespaceMigration.items(event);assertEquals(RegistryEvent.MissingMappings.Action.REMAP,mapping.getAction());assertSame(item,mapping.getTarget());
    }
}
