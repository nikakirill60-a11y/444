package ru.metrocraft.port.blackrazid.mutants;
import net.minecraft.world.World;

/** Own BlackRazid source profile; not a reskin/replacement of a pre-existing mob. */
public final class MobusEntity extends BlackrazidMutant {
    public MobusEntity(World world){super(world);}
    @Override public String species(){return "mobus";}
    @Override public String soundFamily(){return "bear";}
    @Override public double followRange(){return 64;}
    @Override public float watchRange(){return 8;}
    @Override public boolean canBreatheUnderwater(){return true;}
    @Override protected void applyEntityAttributes(){super.applyEntityAttributes();profile(140,8,.45);}
}
