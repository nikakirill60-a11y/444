package ru.metrocraft.port.blackrazid.survival;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.*;

/** A toggle request contains no player ID, inventory slot, light position or energy. */
public final class LampPacket implements IMessage {
    @Override public void toBytes(ByteBuf b){} @Override public void fromBytes(ByteBuf b){}
    public static final class Handler implements IMessageHandler<LampPacket,IMessage>{
        @Override public IMessage onMessage(LampPacket packet,MessageContext ctx){
            EntityPlayerMP p=ctx.getServerHandler().player;
            p.getServerWorld().addScheduledTask(()->{
                if(!p.isEntityAlive() || p.isSpectator())return;
                long now=p.getServerWorld().getTotalWorldTime();
                net.minecraft.nbt.NBTTagCompound data=p.getEntityData();
                if(data.hasKey("MetroBlackrazidLampToggle",99) && now>=data.getLong("MetroBlackrazidLampToggle") && now-data.getLong("MetroBlackrazidLampToggle")<5)return;
                data.setLong("MetroBlackrazidLampToggle",now);
                SurvivalState s=SurvivalSystem.get(p);s.toggle(SurvivalSystem.hasLamp(p));SurvivalSystem.save(p,s);SurvivalSystem.sync(p);
            });return null;
        }
    }
}
