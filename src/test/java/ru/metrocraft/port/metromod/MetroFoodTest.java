package ru.metrocraft.port.metromod;

import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.junit.*;
import ru.metrocraft.port.blackrazid.survival.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Actual ItemFood/FoodStats and shared survival state, not an imitation of consumption. */
public class MetroFoodTest {
    @BeforeClass public static void boot(){Bootstrap.register();}
    private EntityPlayer player(boolean remote)throws Exception{
        EntityPlayer p=mock(EntityPlayer.class);p.world=mock(World.class);
        java.lang.reflect.Field side=World.class.getField("isRemote");side.setAccessible(true);side.setBoolean(p.world,remote);
        java.lang.reflect.Field caps=EntityPlayer.class.getField("capabilities");caps.setAccessible(true);caps.set(p,new PlayerCapabilities());
        p.inventory=new InventoryPlayer(p);when(p.isEntityAlive()).thenReturn(true);when(p.getEntityData()).thenReturn(new NBTTagCompound());
        net.minecraft.util.FoodStats food=new net.minecraft.util.FoodStats();food.setFoodLevel(10);when(p.getFoodStats()).thenReturn(food);
        when(p.canEat(false)).thenAnswer(i->p.capabilities.isCreativeMode || food.needFood());
        SurvivalState state=new SurvivalState();state.water=10;state.energy=47;SurvivalSystem.save(p,state);return p;
    }
    private MetroFoodItem item(MetroConsumables.Profile profile){return new MetroFoodItem(profile);}
    @Test public void confirmedProfilesKeepFoodParametersAndStackLimit(){
        int[] hunger={3,2,3,0};float[] sat={.3F,.2F,.35F,0};int n=0;
        for(MetroConsumables.Profile profile:MetroConsumables.Profile.values()){
            MetroFoodItem item=item(profile);ItemStack stack=new ItemStack(item);
            assertEquals(hunger[n],item.getHealAmount(stack));assertEquals(sat[n++],item.getSaturationModifier(stack),0);
            assertEquals(8,stack.getMaxStackSize());assertEquals(32,item.getMaxItemUseDuration(stack));
        }
    }
    @Test public void foodCompletionUsesActualVanillaFoodStatsAndOneItem()throws Exception{
        for(MetroConsumables.Profile profile:new MetroConsumables.Profile[]{MetroConsumables.Profile.KONSERVI,MetroConsumables.Profile.GALETI,MetroConsumables.Profile.GRIBTEA}){
            EntityPlayer p=player(false);MetroFoodItem item=item(profile);ItemStack stack=new ItemStack(item,3);
            item.onItemUseFinish(stack,p.world,p);assertEquals(2,stack.getCount());assertEquals(10+profile.hunger,p.getFoodStats().getFoodLevel());
            assertEquals(5+profile.hunger*profile.saturation*2,p.getFoodStats().getSaturationLevel(),.00001F);
            assertEquals(10,SurvivalSystem.get(p).water,0);assertEquals(47,SurvivalSystem.get(p).energy,0);
        }
    }
    @Test public void waterCompletionReusesFlaskThirstWithoutFeedingOrHealing()throws Exception{
        EntityPlayer p=player(false);MetroFoodItem item=item(MetroConsumables.Profile.WATERBOTTLE);ItemStack stack=new ItemStack(item,2);
        item.onItemUseFinish(stack,p.world,p);assertEquals(1,stack.getCount());assertEquals(40,SurvivalSystem.get(p).water,0);
        assertEquals(10,p.getFoodStats().getFoodLevel());assertEquals(47,SurvivalSystem.get(p).energy,0);verify(p,never()).heal(anyFloat());
    }
    @Test public void fullWaterRefusesUseAndDoesNotConsume()throws Exception{
        EntityPlayer p=player(false);SurvivalState s=SurvivalSystem.get(p);s.water=100;SurvivalSystem.save(p,s);
        MetroFoodItem item=item(MetroConsumables.Profile.WATERBOTTLE);ItemStack stack=new ItemStack(item,2);when(p.getHeldItem(EnumHand.MAIN_HAND)).thenReturn(stack);
        assertEquals(EnumActionResult.FAIL,item.onItemRightClick(p.world,p,EnumHand.MAIN_HAND).getType());item.onItemUseFinish(stack,p.world,p);assertEquals(2,stack.getCount());
    }
    @Test public void nearlyFullWaterCapsAt100WithoutTouchingEnergy()throws Exception{
        EntityPlayer p=player(false);SurvivalState s=SurvivalSystem.get(p);s.water=95;SurvivalSystem.save(p,s);
        MetroFoodItem item=item(MetroConsumables.Profile.WATERBOTTLE);ItemStack stack=new ItemStack(item,2);item.onItemUseFinish(stack,p.world,p);
        assertEquals(100,SurvivalSystem.get(p).water,0);assertEquals(47,SurvivalSystem.get(p).energy,0);assertEquals(1,stack.getCount());
    }
    @Test public void startingAndInterruptingUseNeverChangesStatsOrStack()throws Exception{
        for(MetroConsumables.Profile profile:MetroConsumables.Profile.values()){
            EntityPlayer p=player(false);MetroFoodItem item=item(profile);ItemStack stack=new ItemStack(item,2);when(p.getHeldItem(EnumHand.OFF_HAND)).thenReturn(stack);
            assertEquals(EnumActionResult.SUCCESS,item.onItemRightClick(p.world,p,EnumHand.OFF_HAND).getType());verify(p).setActiveHand(EnumHand.OFF_HAND);
            item.onPlayerStoppedUsing(stack,p.world,p,16);assertEquals(2,stack.getCount());assertEquals(10,p.getFoodStats().getFoodLevel());assertEquals(10,SurvivalSystem.get(p).water,0);
        }
    }
    @Test public void clientCompletionNeverChangesItemHungerOrWater()throws Exception{
        for(MetroConsumables.Profile profile:MetroConsumables.Profile.values()){
            EntityPlayer p=player(true);MetroFoodItem item=item(profile);ItemStack stack=new ItemStack(item,2);item.onItemUseFinish(stack,p.world,p);
            assertEquals(2,stack.getCount());assertEquals(10,p.getFoodStats().getFoodLevel());assertEquals(10,SurvivalSystem.get(p).water,0);
        }
    }
    @Test public void deadAndSpectatorCannotCompleteOrStart()throws Exception{
        for(MetroConsumables.Profile profile:MetroConsumables.Profile.values())for(boolean dead:new boolean[]{true,false}){
            EntityPlayer p=player(false);when(p.isEntityAlive()).thenReturn(!dead);when(p.isSpectator()).thenReturn(!dead);
            MetroFoodItem item=item(profile);ItemStack stack=new ItemStack(item,2);when(p.getHeldItem(EnumHand.MAIN_HAND)).thenReturn(stack);
            assertEquals(EnumActionResult.FAIL,item.onItemRightClick(p.world,p,EnumHand.MAIN_HAND).getType());item.onItemUseFinish(stack,p.world,p);
            assertEquals(2,stack.getCount());assertEquals(10,p.getFoodStats().getFoodLevel());assertEquals(10,SurvivalSystem.get(p).water,0);
        }
    }
    @Test public void creativeReceivesEffectButNeverLosesItem()throws Exception{
        for(MetroConsumables.Profile profile:MetroConsumables.Profile.values()){
            EntityPlayer p=player(false);p.capabilities.isCreativeMode=true;MetroFoodItem item=item(profile);ItemStack stack=new ItemStack(item);
            item.onItemUseFinish(stack,p.world,p);assertEquals(1,stack.getCount());
            assertEquals(profile.water?40:10,SurvivalSystem.get(p).water,0);assertEquals(10+profile.hunger,p.getFoodStats().getFoodLevel());
        }
    }
    @Test public void foreignAndEmptyStacksAreRejected()throws Exception{
        for(MetroConsumables.Profile profile:MetroConsumables.Profile.values()){
            EntityPlayer p=player(false);MetroFoodItem item=item(profile);ItemStack foreign=new ItemStack(Items.APPLE,3);
            item.onItemUseFinish(ItemStack.EMPTY,p.world,p);item.onItemUseFinish(foreign,p.world,p);
            assertEquals(3,foreign.getCount());assertEquals(10,p.getFoodStats().getFoodLevel());assertEquals(10,SurvivalSystem.get(p).water,0);
        }
    }
    @Test public void fullHungerDoesNotConsumeFoodEvenIfHungerFillsDuringUse()throws Exception{
        EntityPlayer p=player(false);MetroFoodItem item=item(MetroConsumables.Profile.KONSERVI);ItemStack stack=new ItemStack(item,2);
        when(p.getHeldItem(EnumHand.MAIN_HAND)).thenReturn(stack);item.onItemRightClick(p.world,p,EnumHand.MAIN_HAND);
        p.getFoodStats().setFoodLevel(20);item.onItemUseFinish(stack,p.world,p);assertEquals(2,stack.getCount());assertEquals(20,p.getFoodStats().getFoodLevel());
    }
    @Test public void teaRemainsFoodAndOnlyBottledWaterHasDrinkingAction(){
        for(MetroConsumables.Profile profile:MetroConsumables.Profile.values()){
            MetroFoodItem item=item(profile);assertEquals(profile.water?EnumAction.DRINK:EnumAction.EAT,item.getItemUseAction(new ItemStack(item)));
        }
    }
}
