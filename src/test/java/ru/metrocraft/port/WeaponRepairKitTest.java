package ru.metrocraft.port;

import net.minecraft.entity.player.*;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** MWF API-name fixture only: never pretends to test proprietary MWF internals. */
public class WeaponRepairKitTest {
    @BeforeClass public static void boot(){Bootstrap.register();}
    private static class DamageableGun extends com.modularwarfare.common.guns.ItemGun {
        DamageableGun(){setMaxDamage(100);setMaxStackSize(1);}
    }
    @Test public void standardDamageRepairKeepsAmmoAttachmentAndNoDropNbt(){
        Definition d=new Definition();d.id="remkomplekt";d.name="repair";d.repairGun=true;d.uses=1;
        ToolItem item=new ToolItem(d);ItemStack kit=new ItemStack(item),gun=new ItemStack(new DamageableGun());gun.setItemDamage(70);
        NBTTagCompound tag=new NBTTagCompound();tag.setInteger("ammocount",6);tag.setBoolean("NoDrop",true);tag.setString("attachment_scope","kept");gun.setTagCompound(tag.copy());
        EntityPlayer p=mock(EntityPlayer.class);p.capabilities=new PlayerCapabilities();
        when(p.isEntityAlive()).thenReturn(true);when(p.getHeldItem(EnumHand.MAIN_HAND)).thenReturn(kit);
        when(p.getHeldItemOffhand()).thenReturn(gun);when(p.getCooldownTracker()).thenReturn(new CooldownTracker());
        assertEquals(EnumActionResult.SUCCESS,item.onItemRightClick(mock(World.class),p,EnumHand.MAIN_HAND).getType());
        assertEquals(0,gun.getItemDamage());assertEquals(tag,gun.getTagCompound());assertTrue(kit.isEmpty());
    }
    @Test public void healthyOrNonDamageableGunDoesNotConsumeKit(){
        Definition d=new Definition();d.id="remkomplekt";d.name="repair";d.repairGun=true;
        ToolItem item=new ToolItem(d);ItemStack kit=new ItemStack(item);
        EntityPlayer p=mock(EntityPlayer.class);p.capabilities=new PlayerCapabilities();
        when(p.isEntityAlive()).thenReturn(true);when(p.getHeldItem(EnumHand.MAIN_HAND)).thenReturn(kit);
        when(p.getCooldownTracker()).thenReturn(new CooldownTracker());
        for(Item gun:new Item[]{new DamageableGun(),new com.modularwarfare.common.guns.ItemGun()}){
            when(p.getHeldItemOffhand()).thenReturn(new ItemStack(gun));
            assertEquals(EnumActionResult.FAIL,item.onItemRightClick(mock(World.class),p,EnumHand.MAIN_HAND).getType());
            assertEquals(1,kit.getCount());
        }
    }
}
