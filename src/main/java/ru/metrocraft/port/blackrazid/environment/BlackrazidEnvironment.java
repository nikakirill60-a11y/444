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
    public static final TunnelKitItem KIT=new TunnelKitItem();
    public static final TransientLightBlock[] LIGHTS={new TransientLightBlock(.7F),new TransientLightBlock(.6F),new TransientLightBlock(.5F),new TransientLightBlock(.3F)};
    public static void initialize(){GameRegistry.registerTileEntity(TunnelTile.class,new ResourceLocation(MetroPort.ID,"blackrazid_tunnel"));GameRegistry.registerTileEntity(TransientLightTile.class,new ResourceLocation(MetroPort.ID,"blackrazid_transient_light"));}
    @SubscribeEvent public static void blocks(RegistryEvent.Register<Block> e){
        TUNNEL.setRegistryName(MetroPort.ID,"blackrazid_tunnel");PROXY.setRegistryName(MetroPort.ID,"blackrazid_tunnel_proxy");e.getRegistry().registerAll(TUNNEL,PROXY);
        for(int i=0;i<LIGHTS.length;i++){LIGHTS[i].setRegistryName(MetroPort.ID,"blackrazid_transient_light_"+i);e.getRegistry().register(LIGHTS[i]);}
    }
    @SubscribeEvent public static void items(RegistryEvent.Register<Item> e){KIT.setRegistryName(MetroPort.ID,"blackrazid_tunnels").setTranslationKey("metroport.blackrazid_tunnels").setCreativeTab(MetroPort.DECOR);e.getRegistry().register(KIT);if(MetroPort.ITEMS.put("blackrazid_tunnels",KIT)!=null)throw new IllegalStateException("Duplicate tunnel kit");}
}
