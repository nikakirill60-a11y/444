package ru.metrocraft.port.blackrazid;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.init.Bootstrap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import ru.metrocraft.port.blackrazid.survival.*;

public class SurvivalRulesTest {
    @BeforeClass public static void boot(){Bootstrap.register();}
    static EntityPlayer player(boolean remote)throws Exception{
        EntityPlayer p=mock(EntityPlayer.class);p.world=mock(World.class);
        java.lang.reflect.Field side=World.class.getField("isRemote");side.setAccessible(true);side.setBoolean(p.world,remote);
        java.lang.reflect.Field caps=EntityPlayer.class.getField("capabilities");caps.setAccessible(true);caps.set(p,new PlayerCapabilities());
        when(p.getEntityData()).thenReturn(new NBTTagCompound());when(p.isEntityAlive()).thenReturn(true);
        when(p.getHeldItemMainhand()).thenReturn(ItemStack.EMPTY);when(p.getHeldItemOffhand()).thenReturn(ItemStack.EMPTY);return p;
    }
    @Test public void newPlayerDefaultsAreWater100Energy0(){SurvivalState s=SurvivalSystem.decode(new NBTTagCompound());assertEquals(100,s.water,0);assertEquals(0,s.energy,0);assertFalse(s.lamp);}
    @Test public void nativePerTickDecayClampedAtZero(){SurvivalState s=new SurvivalState();s.tick(false,false);assertEquals(99.996F,s.water,.0001F);s.water=.001F;s.tick(false,false);assertEquals(0,s.water,0);}
    @Test public void dehydrationPulsesOncePer65Ticks(){SurvivalState s=new SurvivalState();s.water=0;for(int i=0;i<64;i++)assertFalse(s.tick(false,false));assertTrue(s.tick(false,false));assertFalse(s.tick(false,false));}
    @Test public void pausedModesHaveNoDecayOrDamageCatchup(){SurvivalState s=new SurvivalState();s.water=0;s.starveTicks=63;s.energy=70;s.lamp=true;for(int i=0;i<100;i++)assertFalse(s.tick(true,true));assertEquals(70,s.energy,0);assertEquals(63,s.starveTicks);assertFalse(s.tick(false,true));assertTrue(s.tick(false,true));}
    @Test public void energyOnlyDrainsWhenEnabledWithLamp(){SurvivalState s=new SurvivalState();s.energy=20;s.tick(false,true);assertEquals(20,s.energy,0);s.toggle(true);s.tick(false,true);assertEquals(19.985F,s.energy,.0001F);s.tick(false,false);assertFalse(s.lamp);assertEquals(19.985F,s.energy,.0001F);}
    @Test public void emptyBatteryAndMissingLampCannotToggleOn(){SurvivalState s=new SurvivalState();assertFalse(s.toggle(true));s.energy=20;assertFalse(s.toggle(false));assertTrue(s.toggle(true));assertFalse(s.toggle(true));s.energy=.001F;s.lamp=true;s.tick(false,true);assertEquals(0,s.energy,0);assertFalse(s.lamp);}
    @Test public void drinkAndChargeRespectCaps(){SurvivalState s=new SurvivalState();s.water=90;assertTrue(s.drink());assertEquals(100,s.water,0);assertFalse(s.drink());for(int i=0;i<5;i++)assertTrue(s.charge());assertEquals(100,s.energy,0);assertFalse(s.charge());}
    @Test public void malformedNBTNeverCreatesNaNEnergyOrDamageTimers(){NBTTagCompound tag=new NBTTagCompound();tag.setFloat("Water",Float.NaN);tag.setFloat("Energy",Float.POSITIVE_INFINITY);tag.setInteger("StarveTicks",Integer.MAX_VALUE);SurvivalState s=SurvivalSystem.decode(tag);assertEquals(100,s.water,0);assertEquals(0,s.energy,0);assertEquals(64,s.starveTicks);tag.setFloat("Water",-500);tag.setFloat("Energy",700);s=SurvivalSystem.decode(tag);assertEquals(0,s.water,0);assertEquals(100,s.energy,0);}
    @Test public void ownNBTDataRoundtripDoesNotUseOldGeneralProperties(){SurvivalState s=new SurvivalState();s.water=23.5F;s.energy=45;s.lamp=true;s.starveTicks=3;NBTTagCompound tag=SurvivalSystem.encode(s);SurvivalState back=SurvivalSystem.decode(tag);assertEquals(s.water,back.water,0);assertEquals(s.energy,back.energy,0);assertTrue(back.lamp);assertFalse(tag.hasKey("Faction"));assertFalse(tag.hasKey("AIR"));}
    @Test public void twoPlayerNamespacesAndOldPropertiesStayIndependent()throws Exception{
        EntityPlayer a=player(false),b=player(false);a.getEntityData().setString("ExtendedPlayer","untouched");SurvivalState s=SurvivalSystem.get(a);s.water=5;SurvivalSystem.save(a,s);
        assertEquals(5,SurvivalSystem.get(a).water,0);assertEquals(100,SurvivalSystem.get(b).water,0);assertEquals("untouched",a.getEntityData().getString("ExtendedPlayer"));
    }
    @Test public void respawnRestoresWaterButNotFreeEnergyAndLampIsOff()throws Exception{
        EntityPlayer old=player(false),next=player(false);SurvivalState s=new SurvivalState();s.water=1;s.energy=27;s.lamp=true;SurvivalSystem.save(old,s);
        SurvivalSystem.clone(new net.minecraftforge.event.entity.player.PlayerEvent.Clone(next,old,true));
        s=SurvivalSystem.get(next);assertEquals(100,s.water,0);assertEquals(27,s.energy,0);assertFalse(s.lamp);
    }
    @Test public void aliveCloneKeepsWaterAndBattery()throws Exception{
        EntityPlayer old=player(false),next=player(false);SurvivalState s=new SurvivalState();s.water=19;s.energy=33;s.lamp=true;SurvivalSystem.save(old,s);
        SurvivalSystem.clone(new net.minecraftforge.event.entity.player.PlayerEvent.Clone(next,old,false));s=SurvivalSystem.get(next);assertEquals(19,s.water,0);assertEquals(33,s.energy,0);assertFalse(s.lamp);
    }
    @Test public void lampRequiresItsDedicatedSlotNotAnOccupiedHand()throws Exception{
        EntityPlayer p=player(false);assertFalse(SurvivalSystem.hasLamp(p));when(p.getHeldItemOffhand()).thenReturn(new ItemStack(new SurvivalItem(SurvivalItem.Type.LAMP)));assertFalse(SurvivalSystem.hasLamp(p));
        ru.metrocraft.port.inventory.ExtraEquipment.inventory(p,ru.metrocraft.port.inventory.ExtraEquipment.Kind.LAMP).setInventorySlotContents(0,p.getHeldItemOffhand());assertTrue(SurvivalSystem.hasLamp(p));
        ru.metrocraft.port.inventory.ExtraEquipment.inventory(p,ru.metrocraft.port.inventory.ExtraEquipment.Kind.LAMP).setInventorySlotContents(0,ItemStack.EMPTY);
        when(p.getHeldItemOffhand()).thenReturn(new ItemStack(new SurvivalItem(SurvivalItem.Type.POWER)));assertFalse(SurvivalSystem.hasLamp(p));
    }
    @Test public void flaskReadingDoesNotWriteNBTAndStacksStayIndependent(){
        SurvivalItem item=new SurvivalItem(SurvivalItem.Type.FLASK);ItemStack a=new ItemStack(item),b=new ItemStack(item);assertEquals(10,SurvivalItem.sips(a));assertFalse(a.hasTagCompound());SurvivalItem.sips(a,4);assertEquals(4,SurvivalItem.sips(a));assertEquals(10,SurvivalItem.sips(b));SurvivalItem.sips(a,-1);assertEquals(0,SurvivalItem.sips(a));
    }
    @Test public void completedDrinkConsumesOneSipAndOnlyServerCanApplyIt()throws Exception{
        SurvivalItem item=new SurvivalItem(SurvivalItem.Type.FLASK);ItemStack stack=new ItemStack(item);EntityPlayer p=player(false);SurvivalState s=new SurvivalState();s.water=20;SurvivalSystem.save(p,s);
        item.onItemUseFinish(stack,p.world,p);assertEquals(9,SurvivalItem.sips(stack));assertEquals(50,SurvivalSystem.get(p).water,0);
        EntityPlayer client=player(true);s.water=20;SurvivalSystem.save(client,s);item.onItemUseFinish(stack,client.world,client);assertEquals(9,SurvivalItem.sips(stack));assertEquals(20,SurvivalSystem.get(client).water,0);
    }
    @Test public void interruptedDrinkDoesNothingAndNoSipUsedAtFullWater()throws Exception{
        SurvivalItem item=new SurvivalItem(SurvivalItem.Type.FLASK);ItemStack stack=new ItemStack(item);EntityPlayer p=player(false);
        item.onPlayerStoppedUsing(stack,p.world,p,30);assertEquals(10,SurvivalItem.sips(stack));item.onItemUseFinish(stack,p.world,p);assertEquals(10,SurvivalItem.sips(stack));assertEquals(40,item.getMaxItemUseDuration(stack));
    }
    @Test public void emptyFlaskCannotReplenishWater()throws Exception{
        SurvivalItem item=new SurvivalItem(SurvivalItem.Type.FLASK);ItemStack stack=new ItemStack(item);SurvivalItem.sips(stack,0);EntityPlayer p=player(false);SurvivalState s=new SurvivalState();s.water=10;SurvivalSystem.save(p,s);item.onItemUseFinish(stack,p.world,p);assertEquals(10,SurvivalSystem.get(p).water,0);
    }
    @Test public void chargerRequiresFull15TickCycles()throws Exception{
        SurvivalItem item=new SurvivalItem(SurvivalItem.Type.POWER);ItemStack stack=new ItemStack(item);EntityPlayer p=player(false);
        ru.metrocraft.port.inventory.ExtraEquipment.inventory(p,ru.metrocraft.port.inventory.ExtraEquipment.Kind.LAMP).setInventorySlotContents(0,new ItemStack(new SurvivalItem(SurvivalItem.Type.LAMP)));
        item.onUsingTick(stack,p,72000);item.onUsingTick(stack,p,71986);assertEquals(0,SurvivalSystem.get(p).energy,0);
        item.onUsingTick(stack,p,71985);assertEquals(20,SurvivalSystem.get(p).energy,0);item.onUsingTick(stack,p,71970);assertEquals(40,SurvivalSystem.get(p).energy,0);
    }
    @Test public void clientChargerCannotChangeServerBattery()throws Exception{
        SurvivalItem item=new SurvivalItem(SurvivalItem.Type.POWER);EntityPlayer p=player(true);item.onUsingTick(new ItemStack(item),p,71985);assertEquals(0,SurvivalSystem.get(p).energy,0);
    }
    @Test public void lampLightHasFiniteBoundedForwardCone(){
        assertEquals(15,LampRules.brightness(0,1));assertEquals(12,LampRules.brightness(3,1));assertEquals(0,LampRules.brightness(4,-1));assertEquals(0,LampRules.brightness(9,1));assertEquals(0,LampRules.brightness(Double.NaN,1));assertEquals(0,LampRules.brightness(1,Double.NaN));
    }
}
