package ru.metrocraft.port.blackrazid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import org.junit.*;
import ru.metrocraft.port.blackrazid.survival.*;
import ru.metrocraft.port.inventory.ExtraEquipment;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SurvivalSafetyTest {
    @BeforeClass public static void boot(){SurvivalRulesTest.boot();}
    private EntityPlayer thirsty()throws Exception{
        EntityPlayer p=SurvivalRulesTest.player(false);SurvivalState s=new SurvivalState();s.water=10;SurvivalSystem.save(p,s);return p;
    }
    @Test public void deadAndSpectatorCannotCompleteDrinking()throws Exception{
        SurvivalItem item=new SurvivalItem(SurvivalItem.Type.FLASK);
        for(boolean dead:new boolean[]{true,false}){
            EntityPlayer p=thirsty();when(p.isEntityAlive()).thenReturn(!dead);when(p.isSpectator()).thenReturn(!dead);
            ItemStack stack=new ItemStack(item);item.onItemUseFinish(stack,p.world,p);
            assertEquals(10,SurvivalSystem.get(p).water,0);assertEquals(10,SurvivalItem.sips(stack));
        }
    }
    @Test public void foreignAndEmptyStacksCannotCompleteDrinking()throws Exception{
        EntityPlayer p=thirsty();SurvivalItem item=new SurvivalItem(SurvivalItem.Type.FLASK);
        item.onItemUseFinish(ItemStack.EMPTY,p.world,p);item.onItemUseFinish(new ItemStack(Items.STICK),p.world,p);
        assertEquals(10,SurvivalSystem.get(p).water,0);
    }
    @Test public void creativeDrinkRestoresWaterWithoutConsumingSip()throws Exception{
        EntityPlayer p=thirsty();p.capabilities.isCreativeMode=true;SurvivalItem item=new SurvivalItem(SurvivalItem.Type.FLASK);ItemStack stack=new ItemStack(item);
        item.onItemUseFinish(stack,p.world,p);assertEquals(40,SurvivalSystem.get(p).water,0);assertEquals(10,SurvivalItem.sips(stack));
    }
    @Test public void invalidGeneratorCountsAndForeignStacksNeverCharge()throws Exception{
        EntityPlayer p=thirsty();SurvivalItem item=new SurvivalItem(SurvivalItem.Type.POWER);ItemStack stack=new ItemStack(item);
        ExtraEquipment.inventory(p,ExtraEquipment.Kind.LAMP).setInventorySlotContents(0,new ItemStack(new SurvivalItem(SurvivalItem.Type.LAMP)));
        for(int count:new int[]{Integer.MIN_VALUE,-15,72000,72015,Integer.MAX_VALUE})item.onUsingTick(stack,p,count);
        item.onUsingTick(ItemStack.EMPTY,p,71985);item.onUsingTick(new ItemStack(Items.STICK),p,71985);
        assertEquals(0,SurvivalSystem.get(p).energy,0);
    }
    @Test public void removingLampStopsGeneratorWithoutFreeEnergy()throws Exception{
        EntityPlayer p=thirsty();SurvivalItem item=new SurvivalItem(SurvivalItem.Type.POWER);item.onUsingTick(new ItemStack(item),p,71985);
        assertEquals(0,SurvivalSystem.get(p).energy,0);verify(p).stopActiveHand();
    }
}
