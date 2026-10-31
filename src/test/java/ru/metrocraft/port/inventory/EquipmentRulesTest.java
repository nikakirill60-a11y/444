package ru.metrocraft.port.inventory;

import net.minecraft.init.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import org.junit.*;
import ru.metrocraft.port.*;
import ru.metrocraft.port.mcmod.*;
import static org.junit.Assert.*;

public class EquipmentRulesTest {
    @BeforeClass public static void setup() {Bootstrap.register();McmodContent.load();}
    private MetroArmor armor(String id,boolean nvg) {
        Definition d=new Definition();d.id=id;d.slot=nvg?"head":"chest";d.nightVision=nvg;d.durability=100;
        return new MetroArmor(d);
    }
    @Test public void onlyTheThreeRequestedWholeSuitsBlockHelmets() {
        assertTrue(EquipmentRules.sealedId("himza"));assertTrue(EquipmentRules.sealedId("satanarmor"));assertTrue(EquipmentRules.sealedId("mcmod_officer_ozk"));
        for(String id:new String[]{"mcmod_ozk_vest","mcmod_himza_vest","mcmod_banditkurtka","bandit","pnv"})assertFalse(EquipmentRules.sealedId(id));
        for(String id:new String[]{"himza","satanarmor"}) {
            ItemStack suit=new ItemStack(armor(id,false));
            assertTrue(EquipmentRules.sealed(suit));assertTrue(((MetroArmor)suit.getItem()).isGasMask());
            assertFalse(EquipmentRules.allowed(suit,ItemStack.EMPTY,EntityEquipmentSlot.HEAD,new ItemStack(Items.IRON_HELMET)));
            assertFalse(EquipmentRules.allowed(ItemStack.EMPTY,new ItemStack(Items.IRON_HELMET),EntityEquipmentSlot.CHEST,suit));
            assertTrue(EquipmentRules.allowed(ItemStack.EMPTY,ItemStack.EMPTY,EntityEquipmentSlot.CHEST,suit));
        }
    }
    @Test public void officerWholeOzkBlocksHeadButSeparateMcmPartsRemainSeparate() {
        for(McmodArmorEntry e:McmodContent.armor) {
            ItemStack stack=new ItemStack(new McmodArmorItem(e));
            if(e.id.equals("mcmod_officer_ozk"))assertTrue(EquipmentRules.sealed(stack));
            if(e.id.startsWith("mcmod_ozk_") || e.id.startsWith("mcmod_himza_"))assertFalse(EquipmentRules.sealed(stack));
        }
    }
    @Test public void nvgIsValidOnlyForItsOwnSlotAndNotVanillaHeadArmor() {
        ItemStack nvg=new ItemStack(armor("pnv",true));
        assertTrue(NvgEquipment.accepts(nvg));assertFalse(NvgEquipment.accepts(new ItemStack(Items.IRON_HELMET)));
        assertFalse(NvgEquipment.accepts(ItemStack.EMPTY));assertFalse(nvg.getItem().isValidArmor(nvg,EntityEquipmentSlot.HEAD,null));
        assertFalse(EquipmentRules.allowed(ItemStack.EMPTY,ItemStack.EMPTY,EntityEquipmentSlot.HEAD,nvg));
    }
    @Test public void nvgCleanupDoesNotClaimLongerStrongerOrVisiblePotionEffects() {
        assertTrue(NvgEquipment.ownEffect(new PotionEffect(MobEffects.NIGHT_VISION,220,0,false,false)));
        assertFalse(NvgEquipment.ownEffect(new PotionEffect(MobEffects.NIGHT_VISION,6000,0,false,false)));
        assertFalse(NvgEquipment.ownEffect(new PotionEffect(MobEffects.NIGHT_VISION,220,1,false,false)));
        assertFalse(NvgEquipment.ownEffect(new PotionEffect(MobEffects.NIGHT_VISION,220,0,false,true)));
        assertFalse(NvgEquipment.ownEffect(null));
    }
    @Test public void shortExternalVisionIsNeverClaimedJustBecauseItNeedsRefreshing() {
        net.minecraft.potion.PotionEffect shortExternal=new net.minecraft.potion.PotionEffect(net.minecraft.init.MobEffects.NIGHT_VISION,80,0,false,false);
        assertFalse(NvgEquipment.refreshVision(false,shortExternal));
        assertTrue(NvgEquipment.refreshVision(true,shortExternal));
        assertTrue(NvgEquipment.refreshVision(false,null));
        assertFalse(NvgEquipment.refreshVision(true,new net.minecraft.potion.PotionEffect(net.minecraft.init.MobEffects.NIGHT_VISION,80,1,false,false)));
        assertFalse(NvgEquipment.refreshVision(true,new net.minecraft.potion.PotionEffect(net.minecraft.init.MobEffects.NIGHT_VISION,80,0,false,true)));
        assertFalse(NvgEquipment.refreshVision(true,new net.minecraft.potion.PotionEffect(net.minecraft.init.MobEffects.NIGHT_VISION,220,0,false,false)));
    }

}
