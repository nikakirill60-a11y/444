package ru.metrocraft.port.mechanics;

import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.junit.*;
import ru.metrocraft.port.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Real ItemStacks/armor/NBT and mocked world/player; not a full vanilla use lifecycle. */
public class FilterItemTest {
    private FilterItem item;
    private EntityPlayer player;
    private ItemStack filter,mask;
    @BeforeClass public static void boot(){Bootstrap.register();}
    static EntityPlayer player(boolean remote)throws Exception{
        EntityPlayer p=mock(EntityPlayer.class);p.world=mock(World.class);
        java.lang.reflect.Field side=World.class.getField("isRemote");side.setAccessible(true);side.setBoolean(p.world,remote);
        java.lang.reflect.Field caps=EntityPlayer.class.getField("capabilities");caps.setAccessible(true);caps.set(p,new PlayerCapabilities());
        p.inventory=new InventoryPlayer(p);p.inventoryContainer=mock(Container.class);
        when(p.isEntityAlive()).thenReturn(true);
        when(p.getItemStackFromSlot(any(EntityEquipmentSlot.class))).thenReturn(ItemStack.EMPTY);
        return p;
    }
    static ItemStack mask(String id,String slot,boolean gas){
        Definition d=new Definition();d.id=id;d.slot=slot;d.gasMask=gas;d.durability=100;
        return new ItemStack(new MetroArmor(d));
    }
    @Before public void setup()throws Exception{
        item=new FilterItem();filter=new ItemStack(item,3);mask=mask("filter_test_mask","head",true);player=player(false);
        when(player.getHeldItem(EnumHand.MAIN_HAND)).thenReturn(filter);
        when(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD)).thenReturn(mask);
    }
    static int ticks(ItemStack s){return s.hasTagCompound()?s.getTagCompound().getInteger("MetroFilterTicks"):0;}
    @Test public void beginAnd59ProgressTicksDoNotWriteNBTOrConsume(){
        assertEquals(EnumActionResult.SUCCESS,item.onItemRightClick(player.world,player,EnumHand.MAIN_HAND).getType());
        verify(player).setActiveHand(EnumHand.MAIN_HAND);
        for(int left=60;left>1;left--)item.onUsingTick(filter,player,left);
        assertFalse(mask.hasTagCompound());assertEquals(3,filter.getCount());
        item.onPlayerStoppedUsing(filter,player.world,player,1);
        assertFalse(mask.hasTagCompound());assertEquals(3,filter.getCount());
        assertEquals(60,item.getMaxItemUseDuration(filter));assertEquals(EnumAction.NONE,item.getItemUseAction(filter));
    }
    @Test public void finishRefillsAndConsumesExactlyOnePreservingOtherNBT(){
        mask.setTagCompound(new NBTTagCompound());mask.getTagCompound().setBoolean("NoDrop",true);mask.setItemDamage(7);
        assertSame(filter,item.onItemUseFinish(filter,player.world,player));
        assertEquals(6000,ticks(mask));assertEquals(2,filter.getCount());assertEquals(7,mask.getItemDamage());
        assertTrue(mask.getTagCompound().getBoolean("NoDrop"));verify(player.inventoryContainer).detectAndSendChanges();
        item.onItemUseFinish(filter,player.world,player);assertEquals(2,filter.getCount());
    }
    @Test public void clientCanOnlyPredictUseAndCannotRefillOrConsume()throws Exception{
        EntityPlayer client=player(true);when(client.getItemStackFromSlot(EntityEquipmentSlot.HEAD)).thenReturn(mask);
        item.onItemUseFinish(filter,client.world,client);assertFalse(mask.hasTagCompound());assertEquals(3,filter.getCount());
    }
    @Test public void removedMaskCancelsAndCannotRefill(){
        when(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD)).thenReturn(ItemStack.EMPTY);
        item.onUsingTick(filter,player,1);verify(player).stopActiveHand();
        item.onItemUseFinish(filter,player.world,player);assertEquals(3,filter.getCount());assertFalse(mask.hasTagCompound());
    }
    @Test public void deadAndSpectatorCannotStartOrComplete(){
        when(player.isEntityAlive()).thenReturn(false);
        assertEquals(EnumActionResult.FAIL,item.onItemRightClick(player.world,player,EnumHand.MAIN_HAND).getType());
        item.onItemUseFinish(filter,player.world,player);assertEquals(3,filter.getCount());
        when(player.isEntityAlive()).thenReturn(true);when(player.isSpectator()).thenReturn(true);
        assertEquals(EnumActionResult.FAIL,item.onItemRightClick(player.world,player,EnumHand.MAIN_HAND).getType());
        item.onItemUseFinish(filter,player.world,player);assertEquals(3,filter.getCount());assertFalse(mask.hasTagCompound());
    }
    @Test public void creativeRefillsWithoutSpending(){
        player.capabilities.isCreativeMode=true;item.onItemUseFinish(filter,player.world,player);
        assertEquals(6000,ticks(mask));assertEquals(3,filter.getCount());
    }
    @Test public void normalHelmetAndEmptyOrForeignFilterCannotRefill(){
        when(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD)).thenReturn(new ItemStack(Items.IRON_HELMET));
        item.onItemUseFinish(filter,player.world,player);assertEquals(3,filter.getCount());
        when(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD)).thenReturn(mask);
        item.onItemUseFinish(ItemStack.EMPTY,player.world,player);
        item.onItemUseFinish(new ItemStack(Items.STICK),player.world,player);assertFalse(mask.hasTagCompound());
    }
    @Test public void fullMaskRejectsStartAndSealedChestSuitAlsoWorks(){
        mask.setTagCompound(new NBTTagCompound());mask.getTagCompound().setInteger("MetroFilterTicks",6000);
        assertEquals(EnumActionResult.FAIL,item.onItemRightClick(player.world,player,EnumHand.MAIN_HAND).getType());
        ItemStack suit=mask("himza","chest",false);when(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST)).thenReturn(suit);
        item.onItemUseFinish(filter,player.world,player);assertEquals(6000,ticks(suit));assertEquals(2,filter.getCount());
    }
}
