package ru.metrocraft.port.blackrazid.environment;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ru.metrocraft.port.MetroPort;
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class BlackrazidEnvironment {
    private BlackrazidEnvironment(){}
    public static final TunnelBlock TUNNEL=new TunnelBlock(true),PROXY=new TunnelBlock(false);
    public static final FactionTeleportBlock TELEPORT=new FactionTeleportBlock();
    public static final TunnelKitItem KIT=new TunnelKitItem();
    public static final TransientLightBlock[] LIGHTS={new TransientLightBlock(.7F),new TransientLightBlock(.6F),new TransientLightBlock(.5F),new TransientLightBlock(.3F)};
    public static void initialize(){GameRegistry.registerTileEntity(TransientLightTile.class,new ResourceLocation(MetroPort.ID,"blackrazid_transient_light"));}
    @SubscribeEvent public static void blocks(RegistryEvent.Register<Block> e){
        for(int i=0;i<LIGHTS.length;i++){LIGHTS[i].setRegistryName(MetroPort.ID,"blackrazid_transient_light_"+i);e.getRegistry().register(LIGHTS[i]);}
    }
    // No obtainable tunnel, furniture, rail or teleport item in this build.
}
