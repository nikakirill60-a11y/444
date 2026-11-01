package ru.metrocraft.port.blackrazid.mutants;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.world.World;

/** Native rat is timid: panic/wander/watch, no melee or target tasks despite its source damage value. */
public final class RatEntity extends BlackrazidMutant {
    public RatEntity(World world){super(world);setSize(.5F,.2F);setPathPriority(PathNodeType.WATER,8);}
    @Override public String species(){return "rat";}
    @Override public boolean timid(){return true;}
    @Override protected void applyEntityAttributes(){super.applyEntityAttributes();profile(15,4,.45);}
    @Override public boolean canBreatheUnderwater(){return true;}
}
