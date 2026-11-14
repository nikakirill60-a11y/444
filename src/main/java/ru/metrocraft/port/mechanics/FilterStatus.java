package ru.metrocraft.port.mechanics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import ru.metrocraft.port.MetroArmor;
import ru.metrocraft.port.MetroPort;

/** Owner-only telemetry. Never inserts/removes a watch or changes filter/radiation state. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class FilterStatus {
    private FilterStatus() {}
    public static int remaining(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof MetroArmor)
                || !((MetroArmor) stack.getItem()).isGasMask()) return -1;
        return Math.max(0, FilterRules.sanitize(stack.hasTagCompound()
                ? stack.getTagCompound().getInteger("MetroFilterTicks") : 0));
    }
    public static int remaining(EntityPlayer player) {
        // Existing protection is HEAD OR CHEST: report the longest available protection.
        return Math.max(remaining(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD)),
                remaining(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST)));
    }
    @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (event.phase != TickEvent.Phase.END || player.world.isRemote || !(player instanceof EntityPlayerMP)) return;
        if (player.ticksExisted != 1 && player.ticksExisted % 20 != 0) return;
        MetroPort.NETWORK.sendTo(new FilterStatusPacket(player.dimension, player.getEntityId(),
                player.isEntityAlive() ? remaining(player) : -1), (EntityPlayerMP) player);
    }
}
