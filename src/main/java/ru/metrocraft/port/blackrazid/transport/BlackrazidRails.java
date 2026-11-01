package ru.metrocraft.port.blackrazid.transport;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ru.metrocraft.port.MetroPort;

// Archived feature: intentionally not registered in the reduced build.
public final class BlackrazidRails {
    private BlackrazidRails(){}
    public static final WideRailBlock RAIL=new WideRailBlock(true),PROXY=new WideRailBlock(false);
    public static final RailKitItem KIT=new RailKitItem();
    public static void initialize(){GameRegistry.registerTileEntity(RailTile.class,new ResourceLocation(MetroPort.ID,"blackrazid_rail"));}
    @SubscribeEvent public static void blocks(RegistryEvent.Register<Block> event){
        RAIL.setRegistryName(MetroPort.ID,"blackrazid_wide_rail").setTranslationKey("metroport.blackrazid_wide_rail");
        PROXY.setRegistryName(MetroPort.ID,"blackrazid_rail_proxy").setTranslationKey("metroport.blackrazid_rail_proxy");event.getRegistry().registerAll(RAIL,PROXY);
    }
    @SubscribeEvent public static void items(RegistryEvent.Register<Item> event){
        KIT.setRegistryName(MetroPort.ID,"blackrazid_wide_rails").setTranslationKey("metroport.blackrazid_wide_rails").setCreativeTab(MetroPort.DECOR);
        event.getRegistry().register(KIT);if(MetroPort.ITEMS.put("blackrazid_wide_rails",KIT)!=null)throw new IllegalStateException("Duplicate native rail kit");
    }
}
