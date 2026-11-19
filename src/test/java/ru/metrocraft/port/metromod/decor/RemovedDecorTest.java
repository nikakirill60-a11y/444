package ru.metrocraft.port.metromod.decor;

import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.init.Bootstrap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.junit.*;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RemovedDecorTest {
    @BeforeClass public static void boot(){Bootstrap.register();}
    @Test public void migrationTargetsOnlyRemovedMetroDecorNotLeversOrPortableLamp(){
        for(OriginalGateProfile p:OriginalGateProfile.all())
            assertEquals(p.id,!p.is("Lever"),RemovedDecorMappings.retired(new ResourceLocation("metroport",p.id)));
        for(String id:new String[]{"germo","germo_open","germo_right","germo_left","germo_left45","germo_half_open","gate_part"})
            assertTrue(RemovedDecorMappings.retired(new ResourceLocation("metroport",id)));
        assertFalse(RemovedDecorMappings.retired(new ResourceLocation("minecraft","germo")));
        assertFalse(RemovedDecorMappings.retired(new ResourceLocation("metroport","blackrazid_lamp")));
        assertFalse(RemovedDecorMappings.retired(new ResourceLocation("metroport","metromod_gate_part")));
    }
    @Test @SuppressWarnings("unchecked") public void actualRegistryHookRegistersSixLeversAndTheirProxyOnly(){
        RegistryEvent.Register<Block> event=mock(RegistryEvent.Register.class);
        IForgeRegistry<Block> registry=mock(IForgeRegistry.class);when(event.getRegistry()).thenReturn(registry);
        Map<String,OriginalGateBlock> previous=new LinkedHashMap<>(OriginalGates.BLOCKS);
        try{
            OriginalGates.BLOCKS.clear();OriginalGates.blocks(event);
            ArgumentCaptor<Block> blocks=ArgumentCaptor.forClass(Block.class);
            verify(registry,times(7)).register(blocks.capture());
            assertEquals(6,OriginalGates.BLOCKS.size());
            for(Block block:blocks.getAllValues())if(block instanceof OriginalGateBlock)
                assertTrue(((OriginalGateBlock)block).profile.is("Lever"));
            else assertSame(OriginalGates.PART,block);
        }finally{OriginalGates.BLOCKS.clear();OriginalGates.BLOCKS.putAll(previous);}
    }
}
