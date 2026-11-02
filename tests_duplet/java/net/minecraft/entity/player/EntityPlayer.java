package net.minecraft.entity.player;import java.util.UUID;
public class EntityPlayer {public final InventoryPlayer inventory=new InventoryPlayer();public final World world=new World();public String message="";private final UUID uuid=UUID.randomUUID();
 public static class World {public boolean isRemote;}public UUID getUniqueID(){return uuid;}public void sendStatusMessage(Object text,boolean action){message=String.valueOf(text);}}
