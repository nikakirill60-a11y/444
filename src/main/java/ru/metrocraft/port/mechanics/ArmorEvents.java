package ru.metrocraft.port.mechanics;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.metrocraft.port.*;

@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class ArmorEvents {
    @SubscribeEvent public static void jump(LivingEvent.LivingJumpEvent event){
        EntityLivingBase entity=event.getEntityLiving();
        for(ItemStack stack:entity.getArmorInventoryList())if(stack.getItem() instanceof MetroArmor)
            entity.motionY*=((MetroArmor)stack.getItem()).getDefinition().jumpModifier;
    }
    @SubscribeEvent public static void fall(LivingFallEvent event){
        for(ItemStack stack:event.getEntityLiving().getArmorInventoryList())if(stack.getItem() instanceof MetroArmor
                && ((MetroArmor)stack.getItem()).getDefinition().negateFallDamage){event.setCanceled(true);return;}
    }
    @SubscribeEvent public static void kills(LivingDeathEvent event){
        net.minecraft.entity.Entity killer=event.getSource().getTrueSource();
        if(killer instanceof net.minecraft.entity.player.EntityPlayer && !killer.world.isRemote){
            net.minecraft.nbt.NBTTagCompound data=killer.getEntityData();
            if(!data.hasKey("PlayerPersisted",10))data.setTag("PlayerPersisted",new net.minecraft.nbt.NBTTagCompound());
            net.minecraft.nbt.NBTTagCompound persisted=data.getCompoundTag("PlayerPersisted");
            String key=event.getEntityLiving() instanceof net.minecraft.entity.player.EntityPlayer?"MetroPlayerKills":"MetroMobKills";
            persisted.setInteger(key,persisted.getInteger(key)+1);
        }
    }
}
