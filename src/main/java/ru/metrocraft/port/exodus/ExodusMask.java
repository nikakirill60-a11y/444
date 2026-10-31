package ru.metrocraft.port.exodus;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.metrocraft.port.Definition;
import ru.metrocraft.port.MetroArmor;

/** Uses the existing mask/filter/radiation mechanics, but a baked resource-pack head model. */
public final class ExodusMask extends MetroArmor {
    public ExodusMask(String id) { super(definition(id)); }
    private static Definition definition(String id) {
        Definition d = new Definition();
        d.id=id; d.kind="armor"; d.slot="head"; d.gasMask=true;
        d.durability=165; d.defence=0; d.knockbackModifier=0;
        // A resource pack supplies no combat stats. Do not invent superior armour protection.
        return d;
    }
    @Override public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "minecraft:textures/atlas/blocks.png";
    }
    @SideOnly(Side.CLIENT)
    @Override public net.minecraft.client.model.ModelBiped getArmorModel(EntityLivingBase entity,
            ItemStack stack, EntityEquipmentSlot slot, net.minecraft.client.model.ModelBiped original) {
        return ru.metrocraft.port.client.ExodusMaskModel.get(getDefinition().id);
    }
}
