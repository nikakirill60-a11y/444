package ru.metrocraft.port.blackrazid.mutants;
import net.minecraft.world.World;

/** Own BlackRazid source profile; not a reskin/replacement of a pre-existing mob. */
public final class NosachEntity extends BlackrazidMutant {
    public NosachEntity(World world){super(world);}
    @Override public String species(){return "nosach";}
    @Override public String soundFamily(){return "nosach";}
    @Override public double followRange(){return 32;}
    @Override public float watchRange(){return 8;}
    @Override public boolean canBreatheUnderwater(){return true;}
    @Override protected void applyEntityAttributes(){super.applyEntityAttributes();profile(120,6,.46);}
}
