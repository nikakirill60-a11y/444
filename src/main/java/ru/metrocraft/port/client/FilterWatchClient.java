package ru.metrocraft.port.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.inventory.WatchEquipment;
import ru.metrocraft.port.mcmod.WatchItem;
import ru.metrocraft.port.mechanics.FilterAlarm;
import ru.metrocraft.port.mechanics.FilterRules;

/** Filter telemetry only: no watch-slot transactions and no equipment NBT writes. */
@Mod.EventBusSubscriber(modid=MetroPort.ID,value=Side.CLIENT)
public final class FilterWatchClient {
    private static World world;
    private static EntityPlayer owner;
    private static FilterAlarm alarm = new FilterAlarm();
    private static int ticks = -1, age;
    private static boolean ready;
    private FilterWatchClient() {}
    private static void reset(World next, EntityPlayer player) {
        world = next; owner = player; ready = false; ticks = -1; age = 0; alarm = new FilterAlarm();
    }
    public static void receive(int dimension, int entityId, int remaining) {
        Minecraft mc = Minecraft.getMinecraft();
        World expectedWorld = mc.world;
        EntityPlayer expectedPlayer = mc.player;
        mc.addScheduledTask(() -> {
            if (expectedWorld == null || expectedPlayer == null || mc.world != expectedWorld || mc.player != expectedPlayer
                    || mc.player.dimension != dimension || mc.player.getEntityId() != entityId) return;
            if (world != expectedWorld || owner != expectedPlayer) reset(expectedWorld, expectedPlayer);
            ticks = FilterRules.sanitize(remaining); age = 0; ready = true;
        });
    }
    public static ItemStack displayedWatch(EntityPlayer player) {
        ItemStack watch = WatchEquipment.get(player);
        if (watch.isEmpty() && WatchEquipment.accepts(player.getHeldItemMainhand())) watch = player.getHeldItemMainhand();
        if (watch.isEmpty() && WatchEquipment.accepts(player.getHeldItemOffhand())) watch = player.getHeldItemOffhand();
        return watch;
    }
    public static boolean detector(ItemStack watch) { return !watch.isEmpty() && watch.getItem() instanceof WatchItem; }
    public static boolean ready() {
        Minecraft mc = Minecraft.getMinecraft();
        return ready && age <= 60 && world == mc.world && owner == mc.player;
    }
    public static int remaining() { return ticks; }
    @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (world != mc.world || owner != mc.player) reset(mc.world, mc.player);
        if (mc.player == null || mc.world == null || mc.isGamePaused()) return;
        if (age < 61) age++;
        if (!ready() || !mc.player.isEntityAlive() || mc.player.isSpectator()) return;
        if (alarm.update(ticks, detector(displayedWatch(mc.player)))) {
            // Temporary vanilla alert, not falsely advertised as decoded donor audio.
            mc.player.playSound(SoundEvents.BLOCK_NOTE_PLING, .35F, 1F);
        }
    }
}
