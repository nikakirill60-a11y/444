package ru.metrocraft.port.mcmod;

import com.google.gson.Gson;
import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.metrocraft.port.MetroPort;
import java.io.*;
import java.nio.charset.StandardCharsets;

/** Three separate server-authoritative melee items, not skins or firearms. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class MeleeWeapons {
    public static final class Entry {
        public String id,nameRu,nameEn,rarity;
        public int grams,width,height,durability,triangles;
        public float damage,attackSpeed,gripOffset;
    }
    public static final Entry[] ENTRIES=load();
    private static Entry[] load() {
        try(Reader r=new InputStreamReader(MeleeWeapons.class.getResourceAsStream("/assets/metroport/melee_weapons.json"),StandardCharsets.UTF_8)) {
            return new Gson().fromJson(r,Entry[].class);
        } catch(Exception e) { throw new IllegalStateException("Cannot load melee weapons",e); }
    }
    public static Entry find(String id) {
        for(Entry e:ENTRIES)if(e.id.equals(id))return e;
        return null;
    }
    @SubscribeEvent public static void register(RegistryEvent.Register<Item> event) {
        for(Entry e:ENTRIES) {
            Item item=new Weapon(e).setRegistryName(MetroPort.ID,e.id).setTranslationKey(MetroPort.ID+"."+e.id).setCreativeTab(MetroPort.WEAPONS);
            event.getRegistry().register(item);
            if(MetroPort.ITEMS.put(e.id,item)!=null)throw new IllegalStateException("Duplicate melee id: "+e.id);
        }
    }
    public static final class Weapon extends ItemSword {
        private final Entry entry;
        public Weapon(Entry entry) { super(ToolMaterial.IRON);this.entry=entry;setMaxStackSize(1);setMaxDamage(entry.durability); }
        @Override public Multimap<String,AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
            Multimap<String,AttributeModifier> result=super.getItemAttributeModifiers(slot);
            if(slot==EntityEquipmentSlot.MAINHAND) {
                result.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
                result.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
                result.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),new AttributeModifier(ATTACK_DAMAGE_MODIFIER,"Weapon modifier",entry.damage-1,0));
                result.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),new AttributeModifier(ATTACK_SPEED_MODIFIER,"Weapon modifier",entry.attackSpeed,0));
            }
            return result;
        }
    }
}
