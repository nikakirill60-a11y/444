package ru.metrocraft.port.blackrazid.mutants;
import net.minecraft.world.World;

/** Own BlackRazid source profile; not a reskin/replacement of a pre-existing mob. */
public final class AquaMaleEntity extends BlackrazidMutant {
    public AquaMaleEntity(World world){super(world);}
    @Override public String species(){return "aqua_male";}
    @Override public String soundFamily(){return "aquam";}
    @Override public double followRange(){return 32;}
    @Override public float watchRange(){return 8;}
    @Override public boolean canBreatheUnderwater(){return false;}
    @Override protected void applyEntityAttributes(){super.applyEntityAttributes();profile(250,10,.45);}
}
