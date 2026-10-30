/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.registry.ClientRegistry
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 */
package sub_mod.techne_block;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import sub_mod.techne_block.ProxyCommon;
import sub_mod.techne_block.RenderTileEntityTechne;
import sub_mod.techne_block.TileEntityTechne;
import sub_mod.techne_block.renders.RenderGermoHalfOpen;
import sub_mod.techne_block.renders.RenderGermoLeft;
import sub_mod.techne_block.renders.RenderGermoLeft45;
import sub_mod.techne_block.renders.RenderGermoOpen;
import sub_mod.techne_block.renders.RenderGermoRight;
import sub_mod.techne_block.tiles.TileGermoHalfOpen;
import sub_mod.techne_block.tiles.TileGermoLeft;
import sub_mod.techne_block.tiles.TileGermoLeft45;
import sub_mod.techne_block.tiles.TileGermoOpen;
import sub_mod.techne_block.tiles.TileGermoRight;

public class ProxyClient
extends ProxyCommon {
    @Override
    public void preInit() {
        super.preInit();
    }

    @Override
    public void init() {
        super.init();
        ClientRegistry.bindTileEntitySpecialRenderer(TileGermoRight.class, (TileEntitySpecialRenderer)new RenderGermoRight());
        ClientRegistry.bindTileEntitySpecialRenderer(TileGermoLeft45.class, (TileEntitySpecialRenderer)new RenderGermoLeft45());
        ClientRegistry.bindTileEntitySpecialRenderer(TileGermoLeft.class, (TileEntitySpecialRenderer)new RenderGermoLeft());
        ClientRegistry.bindTileEntitySpecialRenderer(TileGermoHalfOpen.class, (TileEntitySpecialRenderer)new RenderGermoHalfOpen());
        ClientRegistry.bindTileEntitySpecialRenderer(TileGermoOpen.class, (TileEntitySpecialRenderer)new RenderGermoOpen());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTechne.class, (TileEntitySpecialRenderer)new RenderTileEntityTechne());
    }
}

