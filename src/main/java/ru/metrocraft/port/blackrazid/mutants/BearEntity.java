package ru.metrocraft.port.blackrazid.mutants;
import net.minecraft.world.World;

/** Own BlackRazid source profile; not a reskin/replacement of a pre-existing mob. */
public final class BearEntity extends BlackrazidMutant {
    public BearEntity(World world){super(world);}
    @Override public String species(){return "bear";}
    @Override public String soundFamily(){return "bear";}
    @Override public double followRange(){return 64;}
    @Override public float watchRange(){return 8;}
    @Override public boolean canBreatheUnderwater(){return true;}
    @Override protected void applyEntityAttributes(){super.applyEntityAttributes();profile(140,8,.45);}
}
