package ru.metrocraft.port.blackrazid.mutants;
import net.minecraft.world.World;

/** Own BlackRazid source profile; not a reskin/replacement of a pre-existing mob. */
public final class StrazhEntity extends BlackrazidMutant {
    public StrazhEntity(World world){super(world);}
    @Override public String species(){return "strazh";}
    @Override public String soundFamily(){return "strazh";}
    @Override public double followRange(){return 32;}
    @Override public float watchRange(){return 16;}
    @Override public boolean canBreatheUnderwater(){return true;}
    @Override protected void applyEntityAttributes(){super.applyEntityAttributes();profile(100,8,.48);}
}
