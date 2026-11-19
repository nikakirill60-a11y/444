package ru.metrocraft.port.inventory;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.Field;
import java.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.world.World;
import org.junit.*;
import ru.metrocraft.port.blackrazid.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WeightInventoryTest {
    @BeforeClass public static void boot(){Bootstrap.register();}
    private static class Player extends EntityPlayer {
        Player(World w){super(w,new GameProfile(UUID.randomUUID(),"weight-test"));}
        @Override public boolean isEntityAlive(){return true;}
        @Override public boolean isSpectator(){return false;}
        @Override public boolean isCreative(){return false;}
    }
    private Player player()throws Exception{
        Field f=sun.misc.Unsafe.class.getDeclaredField("theUnsafe");f.setAccessible(true);
        Player p=(Player)((sun.misc.Unsafe)f.get(null)).allocateInstance(Player.class);p.world=mock(World.class);
        p.inventory=new InventoryPlayer(p);Field c=EntityPlayer.class.getField("capabilities");c.setAccessible(true);c.set(p,new PlayerCapabilities());return p;
    }
    private BlackrazidBackpackItem bag(int slots){BlackrazidContent.Entry e=new BlackrazidContent.Entry();e.storageSlots=slots;return new BlackrazidBackpackItem(e);}
    @Test public void baseTenKgArmorTwoKgAndBackpacksTenTwentyThirtyKg()throws Exception{
        Player p=player();assertEquals(10000,CarryWeight.capacity(p));assertEquals(0,CarryWeight.carried(p));
        p.inventory.armorInventory.set(2,new ItemStack(Items.IRON_CHESTPLATE));assertEquals(12000,CarryWeight.capacity(p));
        for(int tier=1;tier<=3;tier++){
            ExtraEquipment.inventory(p,ExtraEquipment.Kind.BLACKRAZID_BACKPACK).setInventorySlotContents(0,new ItemStack(bag(tier*5)));
            assertEquals(12000+tier*10000,CarryWeight.capacity(p));assertEquals(3,InventoryCapacity.rows(p));
        }
    }
    @Test public void twoLegacyEquippedBagsDoNotStackBonusesOrLoseEitherBag()throws Exception{
        Player p=player();Backpacks.inventory(p).setInventorySlotContents(0,new ItemStack(new BackpackItem(2)));
        ExtraEquipment.inventory(p,ExtraEquipment.Kind.BLACKRAZID_BACKPACK).setInventorySlotContents(0,new ItemStack(bag(15)));
        ExtraEquipment.migrateLegacyBackpack(p);assertFalse(Backpacks.get(p).isEmpty());assertEquals(40000,CarryWeight.capacity(p));
        assertEquals(2000,CarryWeight.carried(p));
    }
    @Test public void cursorArmorAndLegacyBagContentsArePhysicalWeight()throws Exception{
        Player p=player();p.inventory.setItemStack(new ItemStack(Items.STICK,4));assertEquals(1000,CarryWeight.carried(p));
        p.inventory.mainInventory.set(9,p.inventory.getItemStack());p.inventory.setItemStack(ItemStack.EMPTY);assertEquals(1000,CarryWeight.carried(p));
        ItemStack pack=new ItemStack(bag(5));new BackpackStorage(pack,null).setInventorySlotContents(0,new ItemStack(Items.STICK,8));
        ExtraEquipment.inventory(p,ExtraEquipment.Kind.BLACKRAZID_BACKPACK).setInventorySlotContents(0,pack);
        assertEquals(4000,CarryWeight.carried(p));
    }
    @Test public void partialPickupUsesIntegerGramsAndNeverRoundsUp(){
        assertEquals(3,CarryWeight.fitting(9000,10000,300,64));assertEquals(0,CarryWeight.fitting(10001,10000,1,64));
        assertEquals(0,CarryWeight.fitting(10000,10000,1,64));assertEquals(0,CarryWeight.fitting(0,10000,0,64));
        assertEquals(2,CarryWeight.fitting(0,10000,300,2));assertEquals(64,CarryWeight.fitting(0,Long.MAX_VALUE,1,64));
    }
    @Test public void actualVanillaHotbarBacksTwoGunsAndOneMeleeSlot()throws Exception{
        Player p=player();MetroContainer c=new MetroContainer(p);p.openContainer=c;
        ItemStack gun=new ItemStack(new com.modularwarfare.common.guns.ItemGun()),knife=new ItemStack(Items.IRON_SWORD),food=new ItemStack(Items.APPLE);
        assertTrue(c.getSlot(33).isItemValid(gun));assertTrue(c.getSlot(34).isItemValid(gun));assertFalse(c.getSlot(35).isItemValid(gun));
        assertTrue(c.getSlot(35).isItemValid(knife));assertFalse(c.getSlot(33).isItemValid(knife));assertFalse(c.getSlot(33).isItemValid(food));
        assertFalse(c.getSlot(36).isItemValid(gun));assertTrue(c.getSlot(6).isItemValid(gun));
        p.inventory.setItemStack(gun);c.slotClick(33,0,ClickType.PICKUP,p);assertTrue(p.inventory.mainInventory.get(0).getItem() instanceof com.modularwarfare.common.guns.ItemGun);
        assertEquals(46,c.inventorySlots.size());assertEquals(0,c.getSlot(33).getSlotIndex());assertEquals(2,c.getSlot(35).getSlotIndex());
        p.inventory.mainInventory.set(9,food);c.slotClick(6,0,ClickType.SWAP,p);assertTrue(p.inventory.mainInventory.get(0).getItem() instanceof com.modularwarfare.common.guns.ItemGun);assertSame(food,p.inventory.mainInventory.get(9));
    }
    @Test public void allSixTiersHaveDistinctColorsAndNbtDoesNotReroll(){
        Set<Integer> colors=new HashSet<>();for(ItemRarity rarity:ItemRarity.values())colors.add(rarity.color);assertEquals(6,colors.size());
        ItemStack s=new ItemStack(Items.STICK);assertEquals(ItemRarity.COMMON,ItemRarity.of(s));
        s.setTagCompound(new net.minecraft.nbt.NBTTagCompound());s.getTagCompound().setString("MetroRarity","mythic");
        assertEquals(ItemRarity.MYTHIC,ItemRarity.of(s.copy()));s.getTagCompound().setString("MetroRarity","not-a-tier");assertEquals(ItemRarity.COMMON,ItemRarity.of(s));
    }
    @Test public void recoveryWindowCannotBeUsedAsNewStorage()throws Exception{
        Player p=player();ItemStack bag=new ItemStack(bag(5));p.inventory.mainInventory.set(3,bag);p.inventory.currentItem=3;
        assertFalse(BackpackContainer.valid(p,0));new BackpackStorage(bag,null).setInventorySlotContents(0,new ItemStack(Items.STICK,2));assertTrue(BackpackContainer.valid(p,0));
        BackpackContainer c=new BackpackContainer(p,0);p.openContainer=c;
        c.slotClick(0,0,ClickType.PICKUP,p);assertEquals(2,p.inventory.getItemStack().getCount());assertTrue(c.storage.isEmpty());
        c.slotClick(0,0,ClickType.PICKUP,p);assertTrue(c.storage.isEmpty());assertEquals(2,p.inventory.getItemStack().getCount());
        assertTrue(new BackpackStorage(bag,null).isEmpty());
    }
}
