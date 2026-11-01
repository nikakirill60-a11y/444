package ru.metrocraft.port.blackrazid.mutants;
import net.minecraft.world.World;

/** Own BlackRazid source profile; not a reskin/replacement of a pre-existing mob. */
public final class DarkEntity extends BlackrazidMutant {
    public DarkEntity(World world){super(world);setSize(1,2.5F);}
    @Override public String species(){return "dark";}
    @Override public String soundFamily(){return "dark";}
    @Override public double followRange(){return 16;}
    @Override public float watchRange(){return 8;}
    @Override public boolean canBreatheUnderwater(){return true;}
    @Override protected void applyEntityAttributes(){super.applyEntityAttributes();profile(200,12,.47);}
}
