package ru.metrocraft.port.metromod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.junit.*;
import ru.metrocraft.port.*;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MetroSelectionTest {
    @BeforeClass public static void boot(){Bootstrap.register();}
    @Test public void eightSoundIdsAreUniqueAndNonWeapon(){
        assertEquals(8,MetroAudio.IDS.length);assertEquals(8,new HashSet<>(Arrays.asList(MetroAudio.IDS)).size());
        for(String id:MetroAudio.IDS){assertTrue(id.startsWith("metromod_"));assertFalse(id.contains("weapon"));}
    }
    @Test public void gateDirectionSelectsDistinctSounds(){assertEquals("metromod_gate_open",MetroAudio.gate(true));assertEquals("metromod_gate_close",MetroAudio.gate(false));}
    @Test public void alarmVariantsCycleSafelyIncludingIntegerOverflow(){
        assertEquals("metromod_watch_alarm_1",MetroAudio.alarm(0));assertEquals("metromod_watch_alarm_3",MetroAudio.alarm(2));assertEquals(MetroAudio.alarm(0),MetroAudio.alarm(3));
        for(int i:new int[]{-1,Integer.MIN_VALUE,Integer.MAX_VALUE})assertTrue(Arrays.asList(MetroAudio.IDS).contains(MetroAudio.alarm(i)));
    }
    @Test public void originalAudioIsPreferredWithLegacyFallback(){
        Map<String,SoundEvent> saved=new LinkedHashMap<>(MetroPort.SOUNDS);
        try{
            SoundEvent original=new SoundEvent(new ResourceLocation("metroport","test_original")),legacy=new SoundEvent(new ResourceLocation("metroport","test_legacy"));
            MetroPort.SOUNDS.put("test_legacy",legacy);assertSame(legacy,MetroAudio.get("test_original","test_legacy"));
            MetroPort.SOUNDS.put("test_original",original);assertSame(original,MetroAudio.get("test_original","test_legacy"));
        }finally{MetroPort.SOUNDS.clear();MetroPort.SOUNDS.putAll(saved);}
    }
    @Test public void medicalMaterialsRetainDonorStackLimitsAndCannotBeEaten(){
        for(int limit:new int[]{8,24,8}){
            MedicalMaterialItem item=new MedicalMaterialItem(limit);ItemStack stack=new ItemStack(item);
            assertEquals(limit,stack.getMaxStackSize());assertEquals(0,item.getMaxItemUseDuration(stack));assertFalse((Item)item instanceof ItemFood);
        }
    }
    @Test public void medicalMaterialRightClickNeverHealsConsumesOrStartsUse(){
        EntityPlayer p=mock(EntityPlayer.class);World world=mock(World.class);
        MedicalMaterialItem item=new MedicalMaterialItem(8);ItemStack stack=new ItemStack(item,3);when(p.getHeldItem(EnumHand.MAIN_HAND)).thenReturn(stack);
        assertEquals(EnumActionResult.PASS,item.onItemRightClick(world,p,EnumHand.MAIN_HAND).getType());assertEquals(3,stack.getCount());
        verify(p,never()).heal(anyFloat());verify(p,never()).setActiveHand(any(EnumHand.class));verify(p,never()).setHealth(anyFloat());
    }
    @Test public void overlaysRequireFirstPersonAliveUnobstructedGameplay(){
        assertTrue(OverlayRules.visible(true,true,true,false,false,false));
        assertFalse(OverlayRules.visible(false,true,true,false,false,false));assertFalse(OverlayRules.visible(true,false,true,false,false,false));
        assertFalse(OverlayRules.visible(true,true,false,false,false,false));assertFalse(OverlayRules.visible(true,true,true,true,false,false));
        assertFalse(OverlayRules.visible(true,true,true,false,true,false));assertFalse(OverlayRules.visible(true,true,true,false,false,true));
    }
    private ItemStack armor(String id,String slot,boolean mask){
        Definition d=new Definition();d.id=id;d.slot=slot;d.gasMask=mask;d.durability=100;return new ItemStack(new MetroArmor(d));
    }
    @Test public void masksSelectOnlyActualHeadOrChestProtection(){
        ItemStack mask=armor("overlay_test_mask","head",true),plain=armor("overlay_test_plain","chest",false);
        assertTrue(OverlayRules.mask(mask,ItemStack.EMPTY));assertTrue(OverlayRules.mask(ItemStack.EMPTY,mask));
        assertFalse(OverlayRules.mask(plain,ItemStack.EMPTY));assertFalse(OverlayRules.mask(ItemStack.EMPTY,ItemStack.EMPTY));
        assertFalse(OverlayRules.gasMask(new ItemStack(new MedicalMaterialItem(8))));
    }
    @Test public void sealedWholeSuitAlsoSelectsCleanMaskWithoutInventingDamageStages(){
        assertTrue(OverlayRules.mask(ItemStack.EMPTY,armor("mcmod_officer_ozk","chest",false)));
    }
    @Test public void animatedNoiseOffsetsStayFiniteAndBounded(){
        for(int tick:new int[]{0,1,-1,127,128,123456,Integer.MIN_VALUE,Integer.MAX_VALUE}){
            assertTrue(OverlayRules.noiseU(tick)>=0 && OverlayRules.noiseU(tick)<1);
            assertTrue(OverlayRules.noiseV(tick)>=0 && OverlayRules.noiseV(tick)<1);
        }
    }
}
