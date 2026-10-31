package ru.metrocraft.port.exodus;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;
import ru.metrocraft.port.entity.LegacyHostileMob;

/** The RP has static meshes, not AI or stats. Explicit standard melee defaults, no invented abilities. */
public abstract class ExodusMutant extends LegacyHostileMob {
    protected ExodusMutant(World world) { super(world); setSize(1.1F, 1.2F); }
    @Override protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(.25);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16);
    }
    public abstract String modelId();
    public static final class Shrimp extends ExodusMutant {
        public Shrimp(World world) { super(world); }
        @Override public String modelId() { return "exodus_shrimp"; }
    }
    public static final class Guard extends ExodusMutant {
        public Guard(World world) { super(world); }
        @Override public String modelId() { return "exodus_guard"; }
    }
}
