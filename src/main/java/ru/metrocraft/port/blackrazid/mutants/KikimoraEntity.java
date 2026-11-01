package ru.metrocraft.port.blackrazid.mutants;
import net.minecraft.world.World;

/** Own BlackRazid source profile; not a reskin/replacement of a pre-existing mob. */
public final class KikimoraEntity extends BlackrazidMutant {
    public KikimoraEntity(World world){super(world);setSize(.5F,.5F);}
    @Override public String species(){return "kikimora";}
    @Override public String soundFamily(){return "kikimora";}
    @Override public double followRange(){return 32;}
    @Override public float watchRange(){return 8;}
    @Override public boolean canBreatheUnderwater(){return true;}
    @Override protected void applyEntityAttributes(){super.applyEntityAttributes();profile(40,4,.45);}
}
