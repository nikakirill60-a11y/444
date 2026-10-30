/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.Mod
 *  cpw.mods.fml.common.Mod$EventHandler
 *  cpw.mods.fml.common.Mod$Instance
 *  cpw.mods.fml.common.SidedProxy
 *  cpw.mods.fml.common.event.FMLInitializationEvent
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.block.Block
 */
package sub_mod.techne_block;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import sub_mod.techne_block.BlockTechne;
import sub_mod.techne_block.Empty;
import sub_mod.techne_block.GermoHalfOpen;
import sub_mod.techne_block.GermoLeft;
import sub_mod.techne_block.GermoLeft45;
import sub_mod.techne_block.GermoOpen;
import sub_mod.techne_block.GermoRight;
import sub_mod.techne_block.ProxyCommon;
import sub_mod.techne_block.TileEntityTechne;
import sub_mod.techne_block.polublocks.PuluEmpty;
import sub_mod.techne_block.polublocks.PuluEmpty1;
import sub_mod.techne_block.polublocks.PuluEmpty2;
import sub_mod.techne_block.polublocks.PuluEmpty3;
import sub_mod.techne_block.tiles.TileGermoHalfOpen;
import sub_mod.techne_block.tiles.TileGermoLeft;
import sub_mod.techne_block.tiles.TileGermoLeft45;
import sub_mod.techne_block.tiles.TileGermoOpen;
import sub_mod.techne_block.tiles.TileGermoRight;

@Mod(modid="sub_mod_techne_block", name="SM_TB", version="0.1a")
public class ModBlockTechne {
    public static final String MODID = "sub_mod_techne_block";
    public static final String MODNAME = "SM_TB";
    public static final String VERSION = "0.1a";
    @Mod.Instance(value="sub_mod_techne_block")
    public static ModBlockTechne instance;
    @SidedProxy(modId="sub_mod_techne_block", clientSide="sub_mod.techne_block.ProxyClient", serverSide="sub_mod.techne_block.ProxyCommon")
    public static ProxyCommon proxy;
    public static Block Empty;
    public static Block PoluEmpty;
    public static Block block_techne;
    public static Block germo_open;
    public static Block germo_half_open;
    public static Block germo_left;
    public static Block germo_left_45;
    public static Block germo_right;
    public static Block PoluEmpty1;
    public static Block PoluEmpty2;
    public static Block PoluEmpty3;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerBlock((Block)PoluEmpty, (String)"poluEmpty");
        GameRegistry.registerBlock((Block)PoluEmpty1, (String)"poluEmpty1");
        GameRegistry.registerBlock((Block)PoluEmpty2, (String)"poluEmpty2");
        GameRegistry.registerBlock((Block)PoluEmpty3, (String)"poluEmpty3");
        GameRegistry.registerBlock((Block)germo_right, (String)"6");
        GameRegistry.registerBlock((Block)germo_left, (String)"4");
        GameRegistry.registerBlock((Block)germo_left_45, (String)"5");
        GameRegistry.registerBlock((Block)germo_half_open, (String)"3");
        GameRegistry.registerBlock((Block)germo_open, (String)"2");
        GameRegistry.registerBlock((Block)Empty, (String)"empty");
        GameRegistry.registerBlock((Block)block_techne, (String)"1");
        GameRegistry.registerTileEntity(TileGermoRight.class, (String)"TileGermoRight");
        GameRegistry.registerTileEntity(TileGermoLeft45.class, (String)"TileGermoLeft45");
        GameRegistry.registerTileEntity(TileGermoLeft.class, (String)"TileGermoLeft");
        GameRegistry.registerTileEntity(TileGermoOpen.class, (String)"TileGermoOpen");
        GameRegistry.registerTileEntity(TileGermoHalfOpen.class, (String)"TileGermoHalfOpen");
        GameRegistry.registerTileEntity(TileEntityTechne.class, (String)"TileEntityTechne");
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    static {
        Empty = new Empty();
        PoluEmpty = new PuluEmpty();
        block_techne = new BlockTechne();
        germo_open = new GermoOpen();
        germo_half_open = new GermoHalfOpen();
        germo_left = new GermoLeft();
        germo_left_45 = new GermoLeft45();
        germo_right = new GermoRight();
        PoluEmpty1 = new PuluEmpty1();
        PoluEmpty2 = new PuluEmpty2();
        PoluEmpty3 = new PuluEmpty3();
    }
}

