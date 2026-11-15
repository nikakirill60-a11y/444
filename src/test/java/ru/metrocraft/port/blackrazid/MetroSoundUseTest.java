package ru.metrocraft.port.blackrazid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.*;
import org.junit.*;
import ru.metrocraft.port.*;
import ru.metrocraft.port.blackrazid.survival.*;
import ru.metrocraft.port.mcmod.McmodMedkitItem;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MetroSoundUseTest {
    private Map<String,SoundEvent> saved;
    @BeforeClass public static void boot(){NativeWeaponsTest.boot();}
    @Before public void save(){saved=new LinkedHashMap<>(MetroPort.SOUNDS);}
    @After public void restore(){MetroPort.SOUNDS.clear();MetroPort.SOUNDS.putAll(saved);}
    private SoundEvent sound(String id){SoundEvent s=new SoundEvent(new ResourceLocation("metroport",id));MetroPort.SOUNDS.put(id,s);return s;}
    private void played(EntityPlayer p,SoundEvent sound){verify(p.world).playSound(isNull(),eq(0D),eq(0D),eq(0D),same(sound),eq(SoundCategory.PLAYERS),eq(1F),eq(1F));}
    @Test public void flaskOriginalSoundOccursOnlyOnSuccessfulServerCompletion()throws Exception{
        EntityPlayer p=NativeWeaponsTest.player(false);SurvivalState s=new SurvivalState();s.water=10;SurvivalSystem.save(p,s);
        SurvivalItem item=new SurvivalItem(SurvivalItem.Type.FLASK);ItemStack stack=new ItemStack(item);SoundEvent audio=sound("metromod_flask");
        item.onPlayerStoppedUsing(stack,p.world,p,20);verify(p.world,never()).playSound(any(),anyDouble(),anyDouble(),anyDouble(),any(),any(),anyFloat(),anyFloat());
        item.onItemUseFinish(stack,p.world,p);played(p,audio);assertEquals(9,SurvivalItem.sips(stack));
    }
    @Test public void medkitOriginalSoundRetains40TicksFiveUsesAndExistingHealing()throws Exception{
        EntityPlayer p=NativeWeaponsTest.player(false);when(p.getHealth()).thenReturn(5F);when(p.getMaxHealth()).thenReturn(20F);
        McmodMedkitItem item=new McmodMedkitItem();ItemStack stack=new ItemStack(item);SoundEvent audio=sound("metromod_medkit");
        assertEquals(40,item.getMaxItemUseDuration(stack));item.onItemUseFinish(stack,p.world,p);
        played(p,audio);verify(p).setHealth(20F);assertEquals(4,McmodMedkitItem.remaining(stack));
    }
    @Test public void filterOriginalSoundIsCompletionOnlyAndRefillsOnce()throws Exception{
        EntityPlayer p=NativeWeaponsTest.player(false);p.inventoryContainer=mock(Container.class);
        Definition d=new Definition();d.id="audio_test_mask";d.slot="head";d.gasMask=true;d.durability=100;ItemStack mask=new ItemStack(new MetroArmor(d));
        when(p.getItemStackFromSlot(EntityEquipmentSlot.HEAD)).thenReturn(mask);when(p.getItemStackFromSlot(EntityEquipmentSlot.CHEST)).thenReturn(ItemStack.EMPTY);
        FilterItem item=new FilterItem();ItemStack stack=new ItemStack(item,2);SoundEvent audio=sound("metromod_filter");
        item.onPlayerStoppedUsing(stack,p.world,p,20);item.onItemUseFinish(stack,p.world,p);played(p,audio);
        assertEquals(1,stack.getCount());item.onItemUseFinish(stack,p.world,p);assertEquals(1,stack.getCount());played(p,audio);
    }
    @Test public void fullFlaskAndDeadMedkitUserProduceNoSound()throws Exception{
        EntityPlayer p=NativeWeaponsTest.player(false);sound("metromod_flask");sound("metromod_medkit");
        SurvivalItem flask=new SurvivalItem(SurvivalItem.Type.FLASK);flask.onItemUseFinish(new ItemStack(flask),p.world,p);
        when(p.getHealth()).thenReturn(0F);McmodMedkitItem medkit=new McmodMedkitItem();medkit.onItemUseFinish(new ItemStack(medkit),p.world,p);
        verify(p.world,never()).playSound(any(),anyDouble(),anyDouble(),anyDouble(),any(),any(),anyFloat(),anyFloat());
    }
}
