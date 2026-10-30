/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 *  net.minecraftforge.common.IExtendedEntityProperties
 */
package metrocraft.entities.clientplayer;

import metrocraft.entities.clientplayer.InventoryCustomPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPlayer
implements IExtendedEntityProperties {
    public static final String EXT_PROP_NAME = "ExtendedPlayer";
    private EntityPlayer player;
    public final InventoryCustomPlayer inventory;
    public int playersKilled;
    public int zombiesKilled;

    public void init(Entity entity, World world) {
    }

    public ExtendedPlayer(EntityPlayer player) {
        this.inventory = new InventoryCustomPlayer(this.player);
        this.playersKilled = 0;
        this.zombiesKilled = 0;
        this.player = player;
    }

    public static final void register(EntityPlayer player) {
        player.registerExtendedProperties(EXT_PROP_NAME, (IExtendedEntityProperties)new ExtendedPlayer(player));
    }

    public static final ExtendedPlayer get(EntityPlayer player) {
        return (ExtendedPlayer)player.getExtendedProperties(EXT_PROP_NAME);
    }

    public void copy(ExtendedPlayer props) {
        this.inventory.copy(props.inventory);
        this.playersKilled = props.playersKilled;
        this.zombiesKilled = props.zombiesKilled;
    }

    public final void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound properties = new NBTTagCompound();
        this.inventory.writeToNBT(properties);
        properties.func_74768_a("playersKilled", this.playersKilled);
        properties.func_74768_a("zombiesKilled", this.zombiesKilled);
        compound.func_74782_a(EXT_PROP_NAME, (NBTBase)properties);
    }

    public final void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound properties = (NBTTagCompound)compound.func_74781_a(EXT_PROP_NAME);
        this.inventory.readFromNBT(properties);
        this.playersKilled = properties.func_74762_e("playersKilled");
        this.zombiesKilled = properties.func_74762_e("zombiesKilled");
    }
}

