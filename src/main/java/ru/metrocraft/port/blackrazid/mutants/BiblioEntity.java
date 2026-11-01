package ru.metrocraft.port.blackrazid.mutants;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/** al() in the original is setInWeb(), NOT pacification, teleport or a new attack. */
public final class BiblioEntity extends BlackrazidMutant {
    public BiblioEntity(World world){super(world);setSize(1,2.5F);experienceValue=0;}
    @Override public String species(){return "biblio";}
    @Override protected void applyEntityAttributes(){super.applyEntityAttributes();profile(200,10,.46);}
    public boolean applyGaze(EntityPlayer player){
        if(world.isRemote || !isEntityAlive() || !MutantRules.librarianSlows(getHealth(),looking(player,1/1.5)))return false;
        setInWeb();return true;
    }
    @Override public void onEntityUpdate(){super.onEntityUpdate();if(!world.isRemote && isEntityAlive())applyGaze(world.getClosestPlayerToEntity(this,46));}
}
