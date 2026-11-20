package ru.metrocraft.port.mcmod;

import net.minecraft.init.Bootstrap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.junit.*;
import java.io.InputStream;
import java.util.*;
import ru.metrocraft.port.client.ToolActionPose;
import ru.metrocraft.port.inventory.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MeleeWeaponsTest {
    @BeforeClass public static void boot(){Bootstrap.register();}
    private ItemStack stack(MeleeWeapons.Entry e){return new ItemStack(new MeleeWeapons.Weapon(e).setRegistryName("minetro",e.id));}
    @Test public void threeDistinctItemsHaveRequestedTiersWeightsAndMeleeSlot() {
        assertEquals(3,MeleeWeapons.ENTRIES.length);
        String[] tiers={"rare","epic","legendary"};int i=0;
        for(MeleeWeapons.Entry e:MeleeWeapons.ENTRIES) {
            ItemStack stack=stack(e);
            assertEquals(1,stack.getMaxStackSize());assertEquals(e.durability,stack.getMaxDamage());
            assertEquals(tiers[i++],ItemRarity.of(stack).name().toLowerCase(Locale.ROOT));
            assertEquals(e.grams,ItemMetrics.unitGrams(stack));assertEquals(e.width,ItemMetrics.width(stack));assertEquals(e.height,ItemMetrics.height(stack));
            assertTrue(LoadoutRules.melee(stack));assertTrue(LoadoutRules.accepts(2,stack));
            assertFalse(LoadoutRules.firearm(stack));assertFalse(LoadoutRules.accepts(0,stack));assertFalse(LoadoutRules.accepts(1,stack));
            assertTrue(ToolActionPose.animated(e.id));
        }
        assertNull(MeleeWeapons.find("mcmod_machete"));assertNull(MeleeWeapons.find(null));
    }
    @Test public void damageAndCooldownModifiersAreMainHandOnly() {
        for(MeleeWeapons.Entry e:MeleeWeapons.ENTRIES) {
            MeleeWeapons.Weapon weapon=(MeleeWeapons.Weapon)stack(e).getItem();
            Collection<AttributeModifier> damage=weapon.getItemAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
            Collection<AttributeModifier> speed=weapon.getItemAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_SPEED.getName());
            assertEquals(1,damage.size());assertEquals(e.damage-1,damage.iterator().next().getAmount(),1e-6);
            assertEquals(1,speed.size());assertEquals(e.attackSpeed,speed.iterator().next().getAmount(),1e-6);
            assertTrue(weapon.getItemAttributeModifiers(EntityEquipmentSlot.OFFHAND).isEmpty());
        }
    }
    @Test public void actualHitConsumesVanillaDurabilityWithoutAnyAmmoLifecycle() {
        for(MeleeWeapons.Entry e:MeleeWeapons.ENTRIES) {
            ItemStack stack=stack(e);EntityLivingBase target=mock(EntityLivingBase.class),attacker=mock(EntityLivingBase.class);
            when(attacker.getRNG()).thenReturn(new Random(7));
            assertTrue(stack.getItem().hitEntity(stack,target,attacker));assertEquals(1,stack.getItemDamage());
            assertFalse(stack.hasTagCompound());
        }
    }
    @Test public void originalObjAllGroupsAndTrianglesAreReadableByRuntime()throws Exception {
        for(MeleeWeapons.Entry e:MeleeWeapons.ENTRIES) {
            try(InputStream in=getClass().getResourceAsStream("/assets/metroport/models/legacy/mcmod_tools/"+e.id+".obj")) {
                assertNotNull(in);McmodObjMesh mesh=McmodObjMesh.read(in);
                assertEquals(e.triangles*3,mesh.groups.values().stream().mapToInt(List::size).sum());
                for(List<float[]> group:mesh.groups.values())for(float[] v:group)for(float value:v)assertTrue(Float.isFinite(value));
            }
        }
    }
}
