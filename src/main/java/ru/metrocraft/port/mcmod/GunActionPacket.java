package ru.metrocraft.port.mcmod;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.inventory.InventoryCapacity;
public final class GunActionPacket implements IMessage {
    private int action,window,page,value;
    public GunActionPacket(){}
    public GunActionPacket(int action,int window,int page,int value){this.action=action;this.window=window;this.page=page;this.value=value;}
    @Override public void toBytes(ByteBuf out){out.writeByte(action).writeByte(window).writeByte(page).writeByte(value);}
    @Override public void fromBytes(ByteBuf in){action=in.readUnsignedByte();window=in.readUnsignedByte();page=in.readUnsignedByte();value=in.readUnsignedByte();}
    public static final class Handler implements IMessageHandler<GunActionPacket,IMessage>{
        @Override public IMessage onMessage(GunActionPacket msg,MessageContext context){
            EntityPlayerMP player=context.getServerHandler().player;
            player.getServerWorld().addScheduledTask(()->{
                ItemStack gun=player.getHeldItemMainhand();
                if(!player.isEntityAlive()||player.isSpectator()||!(gun.getItem() instanceof McmodAkItem))return;
                long now=player.world.getTotalWorldTime();McmodAkItem item=(McmodAkItem)gun.getItem();
                if(msg.action==0){
                    if(player.openContainer!=player.inventoryContainer||!InventoryCapacity.emptyForOpen(player)||item.reloading(gun))return;
                    if(player.getEntityData().hasKey("MetroLastModsOpen")&&now-player.getEntityData().getLong("MetroLastModsOpen")<5)return;
                    player.getEntityData().setLong("MetroLastModsOpen",now);player.stopActiveHand();
                    player.openGui(MetroPort.INSTANCE,1,player.world,0,0,0);
                }else if(msg.action==1){
                    if(!(player.openContainer instanceof GunWorkbenchContainer)||player.openContainer.windowId!=msg.window||!player.openContainer.canInteractWith(player))return;
                    if(player.getEntityData().hasKey("MetroLastGunUpgrade")&&now-player.getEntityData().getLong("MetroLastGunUpgrade")<3)return;
                    player.getEntityData().setLong("MetroLastGunUpgrade",now);
                    boolean ok=GunUpgrades.apply(player,gun,msg.page,msg.value);
                    player.sendStatusMessage(new net.minecraft.util.text.TextComponentTranslation(ok?"metroport.mcmod.mods.success":"metroport.mcmod.mods.failed"),true);
                    if(ok){
                        net.minecraft.util.SoundEvent sound=MetroPort.SOUNDS.get("mcmod_craft");
                        if(sound!=null)player.world.playSound(null,player.posX,player.posY,player.posZ,sound,net.minecraft.util.SoundCategory.PLAYERS,.7F,1);
                        player.sendContainerToPlayer(player.inventoryContainer);
                    }
                }else if(msg.action==2&&player.openContainer==player.inventoryContainer)item.pump(gun,player);
            });return null;
        }
    }
}
