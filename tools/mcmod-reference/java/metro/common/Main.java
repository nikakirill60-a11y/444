/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.Mod
 *  cpw.mods.fml.common.Mod$EventHandler
 *  cpw.mods.fml.common.Mod$Instance
 *  cpw.mods.fml.common.SidedProxy
 *  cpw.mods.fml.common.event.FMLInitializationEvent
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.network.IGuiHandler
 *  cpw.mods.fml.common.network.NetworkRegistry
 *  cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  metro.client.events.ArmRotation
 *  metro.client.events.EventsClient
 *  metro.common.CommonProxy
 *  metro.common.network.Network
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraftforge.common.MinecraftForge
 */
package metro.common;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import metro.client.events.ArmRotation;
import metro.client.events.EventsClient;
import metro.client.gui.GuiHandler;
import metro.common.CommonProxy;
import metro.common.TickHundlerServer;
import metro.common.TutEventHandler;
import metro.common.network.Network;
import metro.common.regstration.RegisterArmor;
import metro.common.regstration.RegisterBlocks;
import metro.common.regstration.RegisterGuns;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid="metro", name="metro mod", version="0.0.1")
public class Main {
    @Mod.Instance(value="metro")
    public static Main instance;
    @SidedProxy(clientSide="metro.common.ClientProxy", serverSide="metro.common.CommonProxy")
    public static CommonProxy proxy;
    public static int h_rad;
    public static final SimpleNetworkWrapper INSTANCE;
    public static CreativeTabs dekorTab;
    public static CreativeTabs gunTab;
    public static CreativeTabs armorTab;
    public static Item gasMask;
    public static boolean isServer;

    @Mod.EventHandler
    public void preLoad(FMLPreInitializationEvent event) {
        Network.load((FMLPreInitializationEvent)event);
        RegisterBlocks.load(event);
        RegisterGuns.load(event);
        RegisterArmor.load(event);
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler((Object)instance, (IGuiHandler)new GuiHandler());
        MinecraftForge.EVENT_BUS.register((Object)new TutEventHandler());
        FMLCommonHandler.instance().bus().register((Object)new TickHundlerServer());
        if (event.getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register((Object)new ArmRotation());
            MinecraftForge.EVENT_BUS.register((Object)new EventsClient());
        }
        proxy.init();
    }

    static {
        h_rad = 94;
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("metro");
        dekorTab = new CreativeTabs("Decor blocks for Building"){

            @SideOnly(value=Side.CLIENT)
            public Item func_78016_d() {
                return Items.field_151113_aN;
            }
        };
        gunTab = new CreativeTabs("Guns"){

            @SideOnly(value=Side.CLIENT)
            public Item func_78016_d() {
                return Items.field_151031_f;
            }
        };
        armorTab = new CreativeTabs("Armor"){

            @SideOnly(value=Side.CLIENT)
            public Item func_78016_d() {
                return Items.field_151161_ac;
            }
        };
        gasMask = RegisterArmor.gasmask;
        isServer = false;
    }
}
