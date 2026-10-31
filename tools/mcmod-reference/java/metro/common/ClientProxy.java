/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.registry.ClientRegistry
 *  metro.client.blocks.render.RenderRad
 *  metro.client.events.ArmorEvent
 *  metro.client.gui.RenderingHelper
 *  metro.common.CommonProxy
 *  metro.common.blocks.tileentity.TileEntityAir
 *  metro.common.blocks.tileentity.TileEntityHudrant
 *  metro.common.blocks.tileentity.TileEntityRad
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraftforge.client.model.IModelCustom
 *  net.minecraftforge.common.MinecraftForge
 *  org.newdawn.slick.SlickException
 */
package metro.common;

import cpw.mods.fml.client.registry.ClientRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import metro.client.blocks.render.RenderRad;
import metro.client.blocks.render.RenderTileEntityHudrant;
import metro.client.events.ArmorEvent;
import metro.client.gui.RenderingHelper;
import metro.common.CommonProxy;
import metro.common.blocks.HudrantBlock;
import metro.common.blocks.tileentity.TileEntityAir;
import metro.common.blocks.tileentity.TileEntityHudrant;
import metro.common.blocks.tileentity.TileEntityRad;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.MinecraftForge;
import org.newdawn.slick.SlickException;

public class ClientProxy
extends CommonProxy {
    public static HashMap<String, IModelCustom> blockModels = new HashMap();
    public static HashMap<String, Integer> blockModelslist = new HashMap();
    public static List<HudrantBlock> blocksdekor = new ArrayList<HudrantBlock>();

    public void preInit() {
        super.preInit();
    }

    public void init() {
        super.init();
        for (HudrantBlock block : blocksdekor) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHudrant.class, (TileEntitySpecialRenderer)new RenderTileEntityHudrant(block));
        }
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRad.class, (TileEntitySpecialRenderer)new RenderRad());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAir.class, (TileEntitySpecialRenderer)new RenderRad());
        MinecraftForge.EVENT_BUS.register((Object)new ArmorEvent());
        try {
            RenderingHelper.init();
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
