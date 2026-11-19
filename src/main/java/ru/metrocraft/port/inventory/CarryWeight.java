package ru.metrocraft.port.inventory;

import java.util.UUID;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import ru.metrocraft.port.MetroPort;

/** Capacity is server-owned. Overload never deletes existing items or traps the player in a GUI. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class CarryWeight {
    public static final int BASE_GRAMS=10000;
    private static final UUID OVERLOAD=UUID.fromString("8178c502-e5d5-467a-8245-71fe7fa3e418");
    public static long carried(EntityPlayer p){
        long grams=0;
        for(int i=0;i<p.inventory.getSizeInventory();i++)grams+=ItemMetrics.grams(p.inventory.getStackInSlot(i));
        grams+=ItemMetrics.grams(p.inventory.getItemStack());
        grams+=ItemMetrics.grams(Backpacks.get(p));grams+=ItemMetrics.grams(NvgEquipment.get(p));grams+=ItemMetrics.grams(WatchEquipment.get(p));
        for(ExtraEquipment.Kind kind:ExtraEquipment.Kind.values())grams+=ItemMetrics.grams(ExtraEquipment.get(p,kind));
        return grams;
    }
    public static long capacity(EntityPlayer p){
        return BASE_GRAMS+Math.max(ItemMetrics.capacityBonus(Backpacks.get(p)),ItemMetrics.capacityBonus(ExtraEquipment.get(p,ExtraEquipment.Kind.BLACKRAZID_BACKPACK)))
                +ItemMetrics.capacityBonus(p.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
    }
    public static int fitting(long current,long capacity,long unit,int count){
        if(unit<=0 || count<=0 || current>=capacity)return 0;
        return (int)Math.min(count,(capacity-current)/unit);
    }
    public static int fitting(EntityPlayer p,ItemStack stack){
        if(p.capabilities.isCreativeMode)return stack.getCount();
        ItemStack unit=stack.copy();unit.setCount(1);
        return fitting(carried(p),capacity(p),ItemMetrics.grams(unit),stack.getCount());
    }
    @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent e){
        EntityPlayer p=e.player;if(e.phase!=TickEvent.Phase.END || p.world.isRemote || p.ticksExisted%10!=0)return;
        IAttributeInstance speed=p.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        boolean overloaded=!p.capabilities.isCreativeMode && !p.isSpectator() && carried(p)>capacity(p);
        if(overloaded){
            if(speed.getModifier(OVERLOAD)==null)speed.applyModifier(new AttributeModifier(OVERLOAD,"MinETRO overload",-0.5,2).setSaved(false));
            p.setSprinting(false);
        }else if(speed.getModifier(OVERLOAD)!=null)speed.removeModifier(OVERLOAD);
    }
    private CarryWeight(){}
}
