/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ModLoader
 *  aav
 *  aif
 *  apa
 *  cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler
 *  cpw.mods.fml.client.registry.RenderingRegistry
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.IPlayerTracker
 *  cpw.mods.fml.common.ITickHandler
 *  cpw.mods.fml.common.Mod
 *  cpw.mods.fml.common.Mod$Init
 *  cpw.mods.fml.common.Mod$Instance
 *  cpw.mods.fml.common.Mod$PostInit
 *  cpw.mods.fml.common.Mod$PreInit
 *  cpw.mods.fml.common.Mod$ServerStarting
 *  cpw.mods.fml.common.SidedProxy
 *  cpw.mods.fml.common.event.FMLInitializationEvent
 *  cpw.mods.fml.common.event.FMLPostInitializationEvent
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.event.FMLServerStartingEvent
 *  cpw.mods.fml.common.network.NetworkMod
 *  cpw.mods.fml.common.registry.EntityRegistry
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.common.registry.LanguageRegistry
 *  cpw.mods.fml.common.registry.TickRegistry
 *  cpw.mods.fml.relauncher.Side
 *  mods.provoda.CommonProxy
 *  mods.provoda.ItemGeneral
 *  mods.provoda.MetroTab
 *  mods.provoda.MetroTabArmor
 *  mods.provoda.MetroTabAtt
 *  mods.provoda.MetroTabDecor
 *  mods.provoda.MetroTabGuns
 *  mods.provoda.MetroTabTunnels
 *  mods.provoda.blocks.Block1
 *  mods.provoda.blocks.Block10
 *  mods.provoda.blocks.Block11
 *  mods.provoda.blocks.Block12
 *  mods.provoda.blocks.Block13
 *  mods.provoda.blocks.Block14
 *  mods.provoda.blocks.Block15
 *  mods.provoda.blocks.Block16
 *  mods.provoda.blocks.Block17
 *  mods.provoda.blocks.Block18
 *  mods.provoda.blocks.Block19
 *  mods.provoda.blocks.Block2
 *  mods.provoda.blocks.Block20
 *  mods.provoda.blocks.Block21
 *  mods.provoda.blocks.Block22
 *  mods.provoda.blocks.Block23
 *  mods.provoda.blocks.Block24
 *  mods.provoda.blocks.Block25
 *  mods.provoda.blocks.Block26
 *  mods.provoda.blocks.Block27
 *  mods.provoda.blocks.Block28
 *  mods.provoda.blocks.Block29
 *  mods.provoda.blocks.Block3
 *  mods.provoda.blocks.Block30
 *  mods.provoda.blocks.Block31
 *  mods.provoda.blocks.Block32
 *  mods.provoda.blocks.Block33
 *  mods.provoda.blocks.Block34
 *  mods.provoda.blocks.Block35
 *  mods.provoda.blocks.Block36
 *  mods.provoda.blocks.Block37
 *  mods.provoda.blocks.Block38
 *  mods.provoda.blocks.Block39
 *  mods.provoda.blocks.Block4
 *  mods.provoda.blocks.Block40
 *  mods.provoda.blocks.Block41
 *  mods.provoda.blocks.Block42
 *  mods.provoda.blocks.Block43
 *  mods.provoda.blocks.Block44
 *  mods.provoda.blocks.Block45
 *  mods.provoda.blocks.Block46
 *  mods.provoda.blocks.Block47
 *  mods.provoda.blocks.Block48
 *  mods.provoda.blocks.Block49
 *  mods.provoda.blocks.Block5
 *  mods.provoda.blocks.Block50
 *  mods.provoda.blocks.Block6
 *  mods.provoda.blocks.Block7
 *  mods.provoda.blocks.Block8
 *  mods.provoda.blocks.Block9
 *  mods.provoda.blocks.BlockGag
 *  mods.provoda.blocks.BlockRailGag
 *  mods.provoda.blocks.DoubleGrassButtom
 *  mods.provoda.blocks.DoubleGrassTop
 *  mods.provoda.blocks.Empty
 *  mods.provoda.blocks.Empty25
 *  mods.provoda.blocks.Empty50
 *  mods.provoda.blocks.EmptyPhone
 *  mods.provoda.blocks.EmptyShkaf1
 *  mods.provoda.blocks.EmptyShkaf2
 *  mods.provoda.blocks.EmptyStand
 *  mods.provoda.blocks.EmptyStand2
 *  mods.provoda.blocks.Flour_Tall
 *  mods.provoda.blocks.Grass
 *  mods.provoda.blocks.GrassTall
 *  mods.provoda.blocks.Sapling_Tall
 *  mods.provoda.entity.EntityMountableBlock
 *  mods.provoda.entity.EntitySpawnerAquaf
 *  mods.provoda.entity.EntitySpawnerAquam
 *  mods.provoda.entity.EntitySpawnerBear
 *  mods.provoda.entity.EntitySpawnerBiblio
 *  mods.provoda.entity.EntitySpawnerDark
 *  mods.provoda.entity.EntitySpawnerDemon
 *  mods.provoda.entity.EntitySpawnerKikimora
 *  mods.provoda.entity.EntitySpawnerMobus
 *  mods.provoda.entity.EntitySpawnerNosach
 *  mods.provoda.entity.EntitySpawnerSpider
 *  mods.provoda.entity.EntitySpawnerStrazh
 *  mods.provoda.entity.EntityTestDeathFull
 *  mods.provoda.guns.AKK
 *  mods.provoda.guns.ammo.AKKAmmo
 *  mods.provoda.handlers.BlockHandlerBarStol
 *  mods.provoda.handlers.BlockHandlerBed
 *  mods.provoda.handlers.BlockHandlerBox1
 *  mods.provoda.handlers.BlockHandlerBoxp
 *  mods.provoda.handlers.BlockHandlerBoxpl
 *  mods.provoda.handlers.BlockHandlerChair1
 *  mods.provoda.handlers.BlockHandlerChair2
 *  mods.provoda.handlers.BlockHandlerDivan
 *  mods.provoda.handlers.BlockHandlerDivan1
 *  mods.provoda.handlers.BlockHandlerDivan2
 *  mods.provoda.handlers.BlockHandlerDivan3
 *  mods.provoda.handlers.BlockHandlerDivan4
 *  mods.provoda.handlers.BlockHandlerElBox1
 *  mods.provoda.handlers.BlockHandlerElBox2
 *  mods.provoda.handlers.BlockHandlerElBox3
 *  mods.provoda.handlers.BlockHandlerElBox4
 *  mods.provoda.handlers.BlockHandlerFp1
 *  mods.provoda.handlers.BlockHandlerFp2
 *  mods.provoda.handlers.BlockHandlerFreez1
 *  mods.provoda.handlers.BlockHandlerFreez2
 *  mods.provoda.handlers.BlockHandlerGermo
 *  mods.provoda.handlers.BlockHandlerGermo1
 *  mods.provoda.handlers.BlockHandlerGermo2
 *  mods.provoda.handlers.BlockHandlerGunb1
 *  mods.provoda.handlers.BlockHandlerGunb2
 *  mods.provoda.handlers.BlockHandlerKab
 *  mods.provoda.handlers.BlockHandlerKan
 *  mods.provoda.handlers.BlockHandlerKreslo
 *  mods.provoda.handlers.BlockHandlerLav2
 *  mods.provoda.handlers.BlockHandlerLavka1
 *  mods.provoda.handlers.BlockHandlerPalatka
 *  mods.provoda.handlers.BlockHandlerPhone
 *  mods.provoda.handlers.BlockHandlerPlita
 *  mods.provoda.handlers.BlockHandlerPolka1
 *  mods.provoda.handlers.BlockHandlerPolka2
 *  mods.provoda.handlers.BlockHandlerPolka3
 *  mods.provoda.handlers.BlockHandlerPolka4
 *  mods.provoda.handlers.BlockHandlerRak
 *  mods.provoda.handlers.BlockHandlerShkaf
 *  mods.provoda.handlers.BlockHandlerShkaf0
 *  mods.provoda.handlers.BlockHandlerShkaf1
 *  mods.provoda.handlers.BlockHandlerShkaf2
 *  mods.provoda.handlers.BlockHandlerShkafm
 *  mods.provoda.handlers.BlockHandlerStand
 *  mods.provoda.handlers.BlockHandlerStelazh1
 *  mods.provoda.handlers.BlockHandlerStelazh2
 *  mods.provoda.handlers.BlockHandlerStelazh3
 *  mods.provoda.handlers.BlockHandlerStol
 *  mods.provoda.handlers.BlockHandlerStolik
 *  mods.provoda.handlers.BlockHandlerStul
 *  mods.provoda.handlers.BlockHandlerStul1
 *  mods.provoda.handlers.BlockHandlerStul2
 *  mods.provoda.handlers.BlockHandlerTabl1
 *  mods.provoda.handlers.BlockHandlerTul1
 *  mods.provoda.handlers.BlockHandlerTul2
 *  mods.provoda.handlers.BlockHandlerTumba1
 *  mods.provoda.handlers.BlockHandlerTumba2
 *  mods.provoda.handlers.BlockHandlerTumba3
 *  mods.provoda.handlers.BlockHandlerTunnel
 *  mods.provoda.handlers.BlockHandlerTv1
 *  mods.provoda.handlers.BlockHandlerUmi
 *  mods.provoda.handlers.BlockHandlerVanna
 *  mods.provoda.handlers.BlockHandlerWash
 *  mods.provoda.handlers.death.BlockHandlerDeath1
 *  mods.provoda.handlers.tunnels.BlockHandlerTunnel90
 *  mods.provoda.handlers.tunnels.BlockHandlerTunnelBeton1
 *  mods.provoda.handlers.tunnels.BlockHandlerTunnelBetonDerevo
 *  mods.provoda.handlers.tunnels.BlockHandlerTunnelBetonDerevo1
 *  mods.provoda.handlers.tunnels.BlockHandlerTunnelBetonDouble1
 *  mods.provoda.handlers.tunnels.BlockHandlerTunnelBetonExit
 *  mods.provoda.handlers.tunnels.BlockHandlerTunnelBetonProvoda
 *  mods.provoda.handlers.tunnels.BlockHandlerTunnelBetonTrub
 *  mods.provoda.handlers.tunnels.BlockHandlerTunnelBetonX
 *  mods.provoda.handlers.tunnels.BlockHandlerTunnelNew
 *  mods.provoda.handlers.tunnels.BlockHandlerTunnelZelezo1
 *  mods.provoda.handlers.tunnels.BlockHandlerTunnelZelezo90
 *  mods.provoda.handlers.tunnels.BlockHandlerTunnelZelezoDerevo
 *  mods.provoda.handlers.tunnels.BlockHandlerTunnelZelezoDerevo1
 *  mods.provoda.handlers.tunnels.BlockHandlerTunnelZelezoDouble
 *  mods.provoda.handlers.tunnels.BlockHandlerTunnelZelezoExit
 *  mods.provoda.handlers.tunnels.BlockHandlerTunnelZelezoProvoda
 *  mods.provoda.handlers.tunnels.BlockHandlerTunnelZelezoTrub
 *  mods.provoda.items.Cev
 *  mods.provoda.items.GazMeh
 *  mods.provoda.items.GoldMag
 *  mods.provoda.items.Item1
 *  mods.provoda.items.Item10
 *  mods.provoda.items.Item2
 *  mods.provoda.items.Item3
 *  mods.provoda.items.Item4
 *  mods.provoda.items.Item5
 *  mods.provoda.items.Item6
 *  mods.provoda.items.Item7
 *  mods.provoda.items.Item8
 *  mods.provoda.items.Item9
 *  mods.provoda.items.Patron
 *  mods.provoda.items.Priklad
 *  mods.provoda.items.Stvol
 *  mods.provoda.items.WeaponParts
 *  mods.provoda.items.Zheton
 *  mods.provoda.items.armor.gasmask.Filter
 *  mods.provoda.items.att.AbzacGlushak
 *  mods.provoda.items.att.AshotButt
 *  mods.provoda.items.att.AutoFire
 *  mods.provoda.items.att.Barrel
 *  mods.provoda.items.att.Butt
 *  mods.provoda.items.att.DupletBarrel
 *  mods.provoda.items.att.DupletButt
 *  mods.provoda.items.att.DupletGlushak
 *  mods.provoda.items.att.DupletPriklad
 *  mods.provoda.items.att.GatlingBarrel
 *  mods.provoda.items.att.GatlingButt
 *  mods.provoda.items.att.GatlingGiro
 *  mods.provoda.items.att.GatlingMotor
 *  mods.provoda.items.att.Glushak
 *  mods.provoda.items.att.Kolimator
 *  mods.provoda.items.att.Laser
 *  mods.provoda.items.att.Optica
 *  mods.provoda.items.att.OpticaSmall
 *  mods.provoda.items.att.PodonokButt
 *  mods.provoda.items.att.PodonokGas
 *  mods.provoda.items.att.QuadBarrel
 *  mods.provoda.items.att.RailgunAkk
 *  mods.provoda.items.att.RailgunKon
 *  mods.provoda.items.att.TiharOptica
 *  mods.provoda.items.att.UbludokButt
 *  mods.provoda.items.att.UboinikBarrel
 *  mods.provoda.items.att.VentilGlushak
 *  mods.provoda.items.att.VsvGlushak
 *  mods.provoda.model_blocks.BlockDivan
 *  mods.provoda.model_blocks.BlockGermo
 *  mods.provoda.model_blocks.BlockGermo1
 *  mods.provoda.model_blocks.BlockGermo2
 *  mods.provoda.model_blocks.BlockKreslo
 *  mods.provoda.model_blocks.BlockPalatka
 *  mods.provoda.model_blocks.BlockPolka3
 *  mods.provoda.model_blocks.BlockPolka4
 *  mods.provoda.model_blocks.BlockShkaf
 *  mods.provoda.model_blocks.BlockStol
 *  mods.provoda.model_blocks.BlockStolik
 *  mods.provoda.model_blocks.BlockStul
 *  mods.provoda.model_blocks.BlockTunnel
 *  mods.provoda.objblocks.BlockBarStol
 *  mods.provoda.objblocks.BlockBed
 *  mods.provoda.objblocks.BlockBox1
 *  mods.provoda.objblocks.BlockBoxp
 *  mods.provoda.objblocks.BlockBoxpl
 *  mods.provoda.objblocks.BlockChair1
 *  mods.provoda.objblocks.BlockChair2
 *  mods.provoda.objblocks.BlockDivan1
 *  mods.provoda.objblocks.BlockDivan2
 *  mods.provoda.objblocks.BlockDivan3
 *  mods.provoda.objblocks.BlockDivan4
 *  mods.provoda.objblocks.BlockElBox1
 *  mods.provoda.objblocks.BlockElBox2
 *  mods.provoda.objblocks.BlockElBox3
 *  mods.provoda.objblocks.BlockElBox4
 *  mods.provoda.objblocks.BlockFp1
 *  mods.provoda.objblocks.BlockFp2
 *  mods.provoda.objblocks.BlockFreez1
 *  mods.provoda.objblocks.BlockFreez2
 *  mods.provoda.objblocks.BlockGunb1
 *  mods.provoda.objblocks.BlockGunb2
 *  mods.provoda.objblocks.BlockKab
 *  mods.provoda.objblocks.BlockKan
 *  mods.provoda.objblocks.BlockLav2
 *  mods.provoda.objblocks.BlockLavka1
 *  mods.provoda.objblocks.BlockPhone
 *  mods.provoda.objblocks.BlockPlita
 *  mods.provoda.objblocks.BlockPolka1
 *  mods.provoda.objblocks.BlockPolka2
 *  mods.provoda.objblocks.BlockRak
 *  mods.provoda.objblocks.BlockShkaf0
 *  mods.provoda.objblocks.BlockShkaf1
 *  mods.provoda.objblocks.BlockShkaf2
 *  mods.provoda.objblocks.BlockShkafm
 *  mods.provoda.objblocks.BlockStand
 *  mods.provoda.objblocks.BlockStelazh1
 *  mods.provoda.objblocks.BlockStelazh2
 *  mods.provoda.objblocks.BlockStelazh3
 *  mods.provoda.objblocks.BlockStul1
 *  mods.provoda.objblocks.BlockStul2
 *  mods.provoda.objblocks.BlockTabl1
 *  mods.provoda.objblocks.BlockTul1
 *  mods.provoda.objblocks.BlockTul2
 *  mods.provoda.objblocks.BlockTumba1
 *  mods.provoda.objblocks.BlockTumba2
 *  mods.provoda.objblocks.BlockTumba3
 *  mods.provoda.objblocks.BlockTv1
 *  mods.provoda.objblocks.BlockUmi
 *  mods.provoda.objblocks.BlockVanna
 *  mods.provoda.objblocks.BlockWash
 *  mods.provoda.objblocks.tunnels.BlockTunnel90
 *  mods.provoda.objblocks.tunnels.BlockTunnelBeton1
 *  mods.provoda.objblocks.tunnels.BlockTunnelBetonDerevo
 *  mods.provoda.objblocks.tunnels.BlockTunnelBetonDerevo1
 *  mods.provoda.objblocks.tunnels.BlockTunnelBetonDouble1
 *  mods.provoda.objblocks.tunnels.BlockTunnelBetonExit
 *  mods.provoda.objblocks.tunnels.BlockTunnelBetonProvoda
 *  mods.provoda.objblocks.tunnels.BlockTunnelBetonTrub
 *  mods.provoda.objblocks.tunnels.BlockTunnelBetonX
 *  mods.provoda.objblocks.tunnels.BlockTunnelNew
 *  mods.provoda.objblocks.tunnels.BlockTunnelZelezo1
 *  mods.provoda.objblocks.tunnels.BlockTunnelZelezo90
 *  mods.provoda.objblocks.tunnels.BlockTunnelZelezoDerevo
 *  mods.provoda.objblocks.tunnels.BlockTunnelZelezoDerevo1
 *  mods.provoda.objblocks.tunnels.BlockTunnelZelezoDouble
 *  mods.provoda.objblocks.tunnels.BlockTunnelZelezoExit
 *  mods.provoda.objblocks.tunnels.BlockTunnelZelezoProvoda
 *  mods.provoda.objblocks.tunnels.BlockTunnelZelezoTrub
 *  mods.provoda.tileentitys.TileEntityBarStol
 *  mods.provoda.tileentitys.TileEntityBed
 *  mods.provoda.tileentitys.TileEntityBox1
 *  mods.provoda.tileentitys.TileEntityBoxp
 *  mods.provoda.tileentitys.TileEntityBoxpl
 *  mods.provoda.tileentitys.TileEntityChair1
 *  mods.provoda.tileentitys.TileEntityDivan
 *  mods.provoda.tileentitys.TileEntityDivan1
 *  mods.provoda.tileentitys.TileEntityDivan2
 *  mods.provoda.tileentitys.TileEntityDivan3
 *  mods.provoda.tileentitys.TileEntityDivan4
 *  mods.provoda.tileentitys.TileEntityElBox1
 *  mods.provoda.tileentitys.TileEntityElBox2
 *  mods.provoda.tileentitys.TileEntityElBox3
 *  mods.provoda.tileentitys.TileEntityElBox4
 *  mods.provoda.tileentitys.TileEntityFp1
 *  mods.provoda.tileentitys.TileEntityFp2
 *  mods.provoda.tileentitys.TileEntityFreez1
 *  mods.provoda.tileentitys.TileEntityFreez2
 *  mods.provoda.tileentitys.TileEntityGermo
 *  mods.provoda.tileentitys.TileEntityGermo1
 *  mods.provoda.tileentitys.TileEntityGermo2
 *  mods.provoda.tileentitys.TileEntityGunb1
 *  mods.provoda.tileentitys.TileEntityGunb2
 *  mods.provoda.tileentitys.TileEntityKab
 *  mods.provoda.tileentitys.TileEntityKan
 *  mods.provoda.tileentitys.TileEntityKreslo
 *  mods.provoda.tileentitys.TileEntityLav2
 *  mods.provoda.tileentitys.TileEntityLavka1
 *  mods.provoda.tileentitys.TileEntityPalatka
 *  mods.provoda.tileentitys.TileEntityPhone
 *  mods.provoda.tileentitys.TileEntityPlita
 *  mods.provoda.tileentitys.TileEntityPolka1
 *  mods.provoda.tileentitys.TileEntityPolka2
 *  mods.provoda.tileentitys.TileEntityPolka3
 *  mods.provoda.tileentitys.TileEntityPolka4
 *  mods.provoda.tileentitys.TileEntityRak
 *  mods.provoda.tileentitys.TileEntityShkaf
 *  mods.provoda.tileentitys.TileEntityShkaf0
 *  mods.provoda.tileentitys.TileEntityShkaf1
 *  mods.provoda.tileentitys.TileEntityShkaf2
 *  mods.provoda.tileentitys.TileEntityShkafm
 *  mods.provoda.tileentitys.TileEntityStand
 *  mods.provoda.tileentitys.TileEntityStelazh1
 *  mods.provoda.tileentitys.TileEntityStelazh2
 *  mods.provoda.tileentitys.TileEntityStelazh3
 *  mods.provoda.tileentitys.TileEntityStol
 *  mods.provoda.tileentitys.TileEntityStolik
 *  mods.provoda.tileentitys.TileEntityStul
 *  mods.provoda.tileentitys.TileEntityStul1
 *  mods.provoda.tileentitys.TileEntityStul2
 *  mods.provoda.tileentitys.TileEntityTabl1
 *  mods.provoda.tileentitys.TileEntityTul1
 *  mods.provoda.tileentitys.TileEntityTul2
 *  mods.provoda.tileentitys.TileEntityTumba1
 *  mods.provoda.tileentitys.TileEntityTumba2
 *  mods.provoda.tileentitys.TileEntityTumba3
 *  mods.provoda.tileentitys.TileEntityTunnel
 *  mods.provoda.tileentitys.TileEntityTv1
 *  mods.provoda.tileentitys.TileEntityUmi
 *  mods.provoda.tileentitys.TileEntityVanna
 *  mods.provoda.tileentitys.TileEntityWash
 *  mods.provoda.tileentitys.death.TileEntityDeath1
 *  mods.provoda.tileentitys.tunnels.TileEntityGag
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnel90
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelBeton1
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelBetonDerevo
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelBetonDerevo1
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelBetonDouble1
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelBetonExit
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelBetonProvoda
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelBetonTrub
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelBetonX
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelNew
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelZelezo1
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelZelezo90
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelZelezoDerevo
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelZelezoDerevo1
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelZelezoDouble
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelZelezoExit
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelZelezoProvoda
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelZelezoTrub
 *  mods.provoda.tileentitys.tunnels.TileEntityWideRail
 *  mods.provoda.until.AirSetup
 *  mods.provoda.until.BlockLightTile
 *  mods.provoda.until.CommandDefence
 *  mods.provoda.until.CustomEPP
 *  mods.provoda.until.EventHandler
 *  mods.provoda.until.GuiHandler
 *  mods.provoda.until.PlayerTracker
 *  mods.provoda.until.ServerTickHandler
 *  mods.provoda.until.Sounds
 *  mv
 *  mw
 *  net.minecraftforge.common.Configuration
 *  net.minecraftforge.common.EnumHelper
 *  net.minecraftforge.common.MinecraftForge
 *  nn
 *  qm
 *  qr
 *  rt
 *  ru
 *  rv
 *  sf
 *  sg
 *  sh
 *  sj
 *  uq
 *  ve
 *  wk
 *  wm
 *  z
 */
package mods.provoda;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import mods.provoda.CommonProxy;
import mods.provoda.ItemGeneral;
import mods.provoda.MetroTab;
import mods.provoda.MetroTabArmor;
import mods.provoda.MetroTabAtt;
import mods.provoda.MetroTabDecor;
import mods.provoda.MetroTabGuns;
import mods.provoda.MetroTabTunnels;
import mods.provoda.blocks.Block1;
import mods.provoda.blocks.Block10;
import mods.provoda.blocks.Block11;
import mods.provoda.blocks.Block12;
import mods.provoda.blocks.Block13;
import mods.provoda.blocks.Block14;
import mods.provoda.blocks.Block15;
import mods.provoda.blocks.Block16;
import mods.provoda.blocks.Block17;
import mods.provoda.blocks.Block18;
import mods.provoda.blocks.Block19;
import mods.provoda.blocks.Block2;
import mods.provoda.blocks.Block20;
import mods.provoda.blocks.Block21;
import mods.provoda.blocks.Block22;
import mods.provoda.blocks.Block23;
import mods.provoda.blocks.Block24;
import mods.provoda.blocks.Block25;
import mods.provoda.blocks.Block26;
import mods.provoda.blocks.Block27;
import mods.provoda.blocks.Block28;
import mods.provoda.blocks.Block29;
import mods.provoda.blocks.Block3;
import mods.provoda.blocks.Block30;
import mods.provoda.blocks.Block31;
import mods.provoda.blocks.Block32;
import mods.provoda.blocks.Block33;
import mods.provoda.blocks.Block34;
import mods.provoda.blocks.Block35;
import mods.provoda.blocks.Block36;
import mods.provoda.blocks.Block37;
import mods.provoda.blocks.Block38;
import mods.provoda.blocks.Block39;
import mods.provoda.blocks.Block4;
import mods.provoda.blocks.Block40;
import mods.provoda.blocks.Block41;
import mods.provoda.blocks.Block42;
import mods.provoda.blocks.Block43;
import mods.provoda.blocks.Block44;
import mods.provoda.blocks.Block45;
import mods.provoda.blocks.Block46;
import mods.provoda.blocks.Block47;
import mods.provoda.blocks.Block48;
import mods.provoda.blocks.Block49;
import mods.provoda.blocks.Block5;
import mods.provoda.blocks.Block50;
import mods.provoda.blocks.Block6;
import mods.provoda.blocks.Block7;
import mods.provoda.blocks.Block8;
import mods.provoda.blocks.Block9;
import mods.provoda.blocks.BlockGag;
import mods.provoda.blocks.BlockLight;
import mods.provoda.blocks.BlockLight1;
import mods.provoda.blocks.BlockLight2;
import mods.provoda.blocks.BlockLight3;
import mods.provoda.blocks.BlockRailGag;
import mods.provoda.blocks.BlockWideRail;
import mods.provoda.blocks.DoubleGrassButtom;
import mods.provoda.blocks.DoubleGrassTop;
import mods.provoda.blocks.Empty;
import mods.provoda.blocks.Empty25;
import mods.provoda.blocks.Empty50;
import mods.provoda.blocks.EmptyPhone;
import mods.provoda.blocks.EmptyShkaf1;
import mods.provoda.blocks.EmptyShkaf2;
import mods.provoda.blocks.EmptyStand;
import mods.provoda.blocks.EmptyStand2;
import mods.provoda.blocks.Flour_Tall;
import mods.provoda.blocks.Grass;
import mods.provoda.blocks.GrassTall;
import mods.provoda.blocks.Radiation;
import mods.provoda.blocks.Sapling_Tall;
import mods.provoda.blocks.TpBlock;
import mods.provoda.entity.EntityDrezina;
import mods.provoda.entity.EntityFlameBall;
import mods.provoda.entity.EntityGrenade1;
import mods.provoda.entity.EntityGrenadeBig;
import mods.provoda.entity.EntityMountableBlock;
import mods.provoda.entity.EntityPassiveFire;
import mods.provoda.entity.EntityPropulsionFX;
import mods.provoda.entity.EntitySpawnerAquaf;
import mods.provoda.entity.EntitySpawnerAquam;
import mods.provoda.entity.EntitySpawnerBear;
import mods.provoda.entity.EntitySpawnerBiblio;
import mods.provoda.entity.EntitySpawnerDark;
import mods.provoda.entity.EntitySpawnerDemon;
import mods.provoda.entity.EntitySpawnerKikimora;
import mods.provoda.entity.EntitySpawnerMobus;
import mods.provoda.entity.EntitySpawnerNosach;
import mods.provoda.entity.EntitySpawnerSpider;
import mods.provoda.entity.EntitySpawnerStrazh;
import mods.provoda.entity.EntityTestDeathFull;
import mods.provoda.entity.Entityregina;
import mods.provoda.guns.AK;
import mods.provoda.guns.AK2012;
import mods.provoda.guns.AKK;
import mods.provoda.guns.AKSU;
import mods.provoda.guns.Abzac;
import mods.provoda.guns.Ashot;
import mods.provoda.guns.Duplet;
import mods.provoda.guns.Gatling;
import mods.provoda.guns.Helsing;
import mods.provoda.guns.Machete;
import mods.provoda.guns.Ognemet;
import mods.provoda.guns.Podonok;
import mods.provoda.guns.Preved;
import mods.provoda.guns.Railgun;
import mods.provoda.guns.Revolver;
import mods.provoda.guns.Rpk;
import mods.provoda.guns.Saiga;
import mods.provoda.guns.Tihar;
import mods.provoda.guns.Ubludok;
import mods.provoda.guns.Uboinik;
import mods.provoda.guns.Velopal;
import mods.provoda.guns.Ventil;
import mods.provoda.guns.Vsv;
import mods.provoda.guns.Vyhlop;
import mods.provoda.guns.ammo.AK2012Ammo;
import mods.provoda.guns.ammo.AKAmmo;
import mods.provoda.guns.ammo.AKKAmmo;
import mods.provoda.guns.ammo.AbzacAmmo;
import mods.provoda.guns.ammo.AbzacAmmoBig;
import mods.provoda.guns.ammo.Bullet12_7x108;
import mods.provoda.guns.ammo.Bullet12_7x55;
import mods.provoda.guns.ammo.Bullet12x7;
import mods.provoda.guns.ammo.Bullet12x70;
import mods.provoda.guns.ammo.Bullet15mm;
import mods.provoda.guns.ammo.Bullet5x45;
import mods.provoda.guns.ammo.Bullet7_62x54;
import mods.provoda.guns.ammo.BulletBolt;
import mods.provoda.guns.ammo.BulletMagnum;
import mods.provoda.guns.ammo.GatlingAmmo;
import mods.provoda.guns.ammo.HelsingAmmo;
import mods.provoda.guns.ammo.OgnemetAmmo;
import mods.provoda.guns.ammo.PodonokAmmo;
import mods.provoda.guns.ammo.PodonokAmmoBig;
import mods.provoda.guns.ammo.PrevedAmmo;
import mods.provoda.guns.ammo.RpkAmmo;
import mods.provoda.guns.ammo.RpkAmmoBig;
import mods.provoda.guns.ammo.SaigaAmmo;
import mods.provoda.guns.ammo.SaigaAmmoBig;
import mods.provoda.guns.ammo.TiharAmmo;
import mods.provoda.guns.ammo.UbludokAmmo;
import mods.provoda.guns.ammo.VentilAmmo;
import mods.provoda.guns.ammo.VentilAmmoBig;
import mods.provoda.guns.ammo.VsvAmmo;
import mods.provoda.guns.ammo.VyhlopAmmo;
import mods.provoda.guns.grenades.Grenade;
import mods.provoda.guns.grenades.GrenadeBig;
import mods.provoda.handlers.BlockHandlerBarStol;
import mods.provoda.handlers.BlockHandlerBed;
import mods.provoda.handlers.BlockHandlerBox1;
import mods.provoda.handlers.BlockHandlerBoxp;
import mods.provoda.handlers.BlockHandlerBoxpl;
import mods.provoda.handlers.BlockHandlerChair1;
import mods.provoda.handlers.BlockHandlerChair2;
import mods.provoda.handlers.BlockHandlerDivan;
import mods.provoda.handlers.BlockHandlerDivan1;
import mods.provoda.handlers.BlockHandlerDivan2;
import mods.provoda.handlers.BlockHandlerDivan3;
import mods.provoda.handlers.BlockHandlerDivan4;
import mods.provoda.handlers.BlockHandlerElBox1;
import mods.provoda.handlers.BlockHandlerElBox2;
import mods.provoda.handlers.BlockHandlerElBox3;
import mods.provoda.handlers.BlockHandlerElBox4;
import mods.provoda.handlers.BlockHandlerFp1;
import mods.provoda.handlers.BlockHandlerFp2;
import mods.provoda.handlers.BlockHandlerFreez1;
import mods.provoda.handlers.BlockHandlerFreez2;
import mods.provoda.handlers.BlockHandlerGermo;
import mods.provoda.handlers.BlockHandlerGermo1;
import mods.provoda.handlers.BlockHandlerGermo2;
import mods.provoda.handlers.BlockHandlerGunb1;
import mods.provoda.handlers.BlockHandlerGunb2;
import mods.provoda.handlers.BlockHandlerKab;
import mods.provoda.handlers.BlockHandlerKan;
import mods.provoda.handlers.BlockHandlerKreslo;
import mods.provoda.handlers.BlockHandlerLav2;
import mods.provoda.handlers.BlockHandlerLavka1;
import mods.provoda.handlers.BlockHandlerPalatka;
import mods.provoda.handlers.BlockHandlerPhone;
import mods.provoda.handlers.BlockHandlerPlita;
import mods.provoda.handlers.BlockHandlerPolka1;
import mods.provoda.handlers.BlockHandlerPolka2;
import mods.provoda.handlers.BlockHandlerPolka3;
import mods.provoda.handlers.BlockHandlerPolka4;
import mods.provoda.handlers.BlockHandlerRak;
import mods.provoda.handlers.BlockHandlerShkaf;
import mods.provoda.handlers.BlockHandlerShkaf0;
import mods.provoda.handlers.BlockHandlerShkaf1;
import mods.provoda.handlers.BlockHandlerShkaf2;
import mods.provoda.handlers.BlockHandlerShkafm;
import mods.provoda.handlers.BlockHandlerStand;
import mods.provoda.handlers.BlockHandlerStelazh1;
import mods.provoda.handlers.BlockHandlerStelazh2;
import mods.provoda.handlers.BlockHandlerStelazh3;
import mods.provoda.handlers.BlockHandlerStol;
import mods.provoda.handlers.BlockHandlerStolik;
import mods.provoda.handlers.BlockHandlerStul;
import mods.provoda.handlers.BlockHandlerStul1;
import mods.provoda.handlers.BlockHandlerStul2;
import mods.provoda.handlers.BlockHandlerTabl1;
import mods.provoda.handlers.BlockHandlerTul1;
import mods.provoda.handlers.BlockHandlerTul2;
import mods.provoda.handlers.BlockHandlerTumba1;
import mods.provoda.handlers.BlockHandlerTumba2;
import mods.provoda.handlers.BlockHandlerTumba3;
import mods.provoda.handlers.BlockHandlerTunnel;
import mods.provoda.handlers.BlockHandlerTv1;
import mods.provoda.handlers.BlockHandlerUmi;
import mods.provoda.handlers.BlockHandlerVanna;
import mods.provoda.handlers.BlockHandlerWash;
import mods.provoda.handlers.death.BlockHandlerDeath1;
import mods.provoda.handlers.tunnels.BlockHandlerTunnel90;
import mods.provoda.handlers.tunnels.BlockHandlerTunnelBeton1;
import mods.provoda.handlers.tunnels.BlockHandlerTunnelBetonDerevo;
import mods.provoda.handlers.tunnels.BlockHandlerTunnelBetonDerevo1;
import mods.provoda.handlers.tunnels.BlockHandlerTunnelBetonDouble1;
import mods.provoda.handlers.tunnels.BlockHandlerTunnelBetonExit;
import mods.provoda.handlers.tunnels.BlockHandlerTunnelBetonProvoda;
import mods.provoda.handlers.tunnels.BlockHandlerTunnelBetonTrub;
import mods.provoda.handlers.tunnels.BlockHandlerTunnelBetonX;
import mods.provoda.handlers.tunnels.BlockHandlerTunnelNew;
import mods.provoda.handlers.tunnels.BlockHandlerTunnelZelezo1;
import mods.provoda.handlers.tunnels.BlockHandlerTunnelZelezo90;
import mods.provoda.handlers.tunnels.BlockHandlerTunnelZelezoDerevo;
import mods.provoda.handlers.tunnels.BlockHandlerTunnelZelezoDerevo1;
import mods.provoda.handlers.tunnels.BlockHandlerTunnelZelezoDouble;
import mods.provoda.handlers.tunnels.BlockHandlerTunnelZelezoExit;
import mods.provoda.handlers.tunnels.BlockHandlerTunnelZelezoProvoda;
import mods.provoda.handlers.tunnels.BlockHandlerTunnelZelezoTrub;
import mods.provoda.items.BackPackLarge;
import mods.provoda.items.BackPackMedium;
import mods.provoda.items.BackPackSmall;
import mods.provoda.items.Cev;
import mods.provoda.items.Flyashka;
import mods.provoda.items.GazMeh;
import mods.provoda.items.GoldMag;
import mods.provoda.items.Item1;
import mods.provoda.items.Item10;
import mods.provoda.items.Item2;
import mods.provoda.items.Item3;
import mods.provoda.items.Item4;
import mods.provoda.items.Item5;
import mods.provoda.items.Item6;
import mods.provoda.items.Item7;
import mods.provoda.items.Item8;
import mods.provoda.items.Item9;
import mods.provoda.items.ItemDrezina;
import mods.provoda.items.ItemRegina;
import mods.provoda.items.ItemWideRails;
import mods.provoda.items.Lamp;
import mods.provoda.items.Med;
import mods.provoda.items.Patron;
import mods.provoda.items.Power;
import mods.provoda.items.Priklad;
import mods.provoda.items.Stvol;
import mods.provoda.items.WeaponParts;
import mods.provoda.items.Zheton;
import mods.provoda.items.armor.cclean.CClean;
import mods.provoda.items.armor.clean.Clean;
import mods.provoda.items.armor.easy.Easy;
import mods.provoda.items.armor.gasmask.Filter;
import mods.provoda.items.armor.gasmask.GasMask;
import mods.provoda.items.armor.hats.Heavy1Hat;
import mods.provoda.items.armor.hats.HeavyHat;
import mods.provoda.items.armor.hats.NaziHat;
import mods.provoda.items.armor.heavy.HeavyArmor;
import mods.provoda.items.armor.heavy1.Heavy1;
import mods.provoda.items.armor.him.HimArmor;
import mods.provoda.items.armor.kozh.Kozh;
import mods.provoda.items.armor.nazi.ArmorNazi;
import mods.provoda.items.armor.normal.Normal;
import mods.provoda.items.armor.red.ArmorRed;
import mods.provoda.items.armor.sparta.Sparta;
import mods.provoda.items.att.AbzacGlushak;
import mods.provoda.items.att.AshotButt;
import mods.provoda.items.att.AutoFire;
import mods.provoda.items.att.Barrel;
import mods.provoda.items.att.Butt;
import mods.provoda.items.att.DupletBarrel;
import mods.provoda.items.att.DupletButt;
import mods.provoda.items.att.DupletGlushak;
import mods.provoda.items.att.DupletPriklad;
import mods.provoda.items.att.GatlingBarrel;
import mods.provoda.items.att.GatlingButt;
import mods.provoda.items.att.GatlingGiro;
import mods.provoda.items.att.GatlingMotor;
import mods.provoda.items.att.Glushak;
import mods.provoda.items.att.Kolimator;
import mods.provoda.items.att.Laser;
import mods.provoda.items.att.Optica;
import mods.provoda.items.att.OpticaSmall;
import mods.provoda.items.att.PodonokButt;
import mods.provoda.items.att.PodonokGas;
import mods.provoda.items.att.QuadBarrel;
import mods.provoda.items.att.RailgunAkk;
import mods.provoda.items.att.RailgunKon;
import mods.provoda.items.att.TiharOptica;
import mods.provoda.items.att.UbludokButt;
import mods.provoda.items.att.UboinikBarrel;
import mods.provoda.items.att.VentilGlushak;
import mods.provoda.items.att.VsvGlushak;
import mods.provoda.mobs.mobs.Aqua_female;
import mods.provoda.mobs.mobs.Aqua_male;
import mods.provoda.mobs.mobs.Bear;
import mods.provoda.mobs.mobs.Biblio;
import mods.provoda.mobs.mobs.Dark;
import mods.provoda.mobs.mobs.Demon;
import mods.provoda.mobs.mobs.Kikimora;
import mods.provoda.mobs.mobs.Mobus;
import mods.provoda.mobs.mobs.Nosach;
import mods.provoda.mobs.mobs.Rat;
import mods.provoda.mobs.mobs.Spider;
import mods.provoda.mobs.mobs.Strazh;
import mods.provoda.model_blocks.BlockDivan;
import mods.provoda.model_blocks.BlockGermo;
import mods.provoda.model_blocks.BlockGermo1;
import mods.provoda.model_blocks.BlockGermo2;
import mods.provoda.model_blocks.BlockKreslo;
import mods.provoda.model_blocks.BlockPalatka;
import mods.provoda.model_blocks.BlockPolka3;
import mods.provoda.model_blocks.BlockPolka4;
import mods.provoda.model_blocks.BlockShkaf;
import mods.provoda.model_blocks.BlockStol;
import mods.provoda.model_blocks.BlockStolik;
import mods.provoda.model_blocks.BlockStul;
import mods.provoda.model_blocks.BlockTunnel;
import mods.provoda.objblocks.BlockBarStol;
import mods.provoda.objblocks.BlockBed;
import mods.provoda.objblocks.BlockBox1;
import mods.provoda.objblocks.BlockBoxp;
import mods.provoda.objblocks.BlockBoxpl;
import mods.provoda.objblocks.BlockChair1;
import mods.provoda.objblocks.BlockChair2;
import mods.provoda.objblocks.BlockDivan1;
import mods.provoda.objblocks.BlockDivan2;
import mods.provoda.objblocks.BlockDivan3;
import mods.provoda.objblocks.BlockDivan4;
import mods.provoda.objblocks.BlockElBox1;
import mods.provoda.objblocks.BlockElBox2;
import mods.provoda.objblocks.BlockElBox3;
import mods.provoda.objblocks.BlockElBox4;
import mods.provoda.objblocks.BlockFp1;
import mods.provoda.objblocks.BlockFp2;
import mods.provoda.objblocks.BlockFreez1;
import mods.provoda.objblocks.BlockFreez2;
import mods.provoda.objblocks.BlockGunb1;
import mods.provoda.objblocks.BlockGunb2;
import mods.provoda.objblocks.BlockKab;
import mods.provoda.objblocks.BlockKan;
import mods.provoda.objblocks.BlockLav2;
import mods.provoda.objblocks.BlockLavka1;
import mods.provoda.objblocks.BlockPhone;
import mods.provoda.objblocks.BlockPlita;
import mods.provoda.objblocks.BlockPolka1;
import mods.provoda.objblocks.BlockPolka2;
import mods.provoda.objblocks.BlockRak;
import mods.provoda.objblocks.BlockShkaf0;
import mods.provoda.objblocks.BlockShkaf1;
import mods.provoda.objblocks.BlockShkaf2;
import mods.provoda.objblocks.BlockShkafm;
import mods.provoda.objblocks.BlockStand;
import mods.provoda.objblocks.BlockStelazh1;
import mods.provoda.objblocks.BlockStelazh2;
import mods.provoda.objblocks.BlockStelazh3;
import mods.provoda.objblocks.BlockStul1;
import mods.provoda.objblocks.BlockStul2;
import mods.provoda.objblocks.BlockTabl1;
import mods.provoda.objblocks.BlockTul1;
import mods.provoda.objblocks.BlockTul2;
import mods.provoda.objblocks.BlockTumba1;
import mods.provoda.objblocks.BlockTumba2;
import mods.provoda.objblocks.BlockTumba3;
import mods.provoda.objblocks.BlockTv1;
import mods.provoda.objblocks.BlockUmi;
import mods.provoda.objblocks.BlockVanna;
import mods.provoda.objblocks.BlockWash;
import mods.provoda.objblocks.tunnels.BlockTunnel90;
import mods.provoda.objblocks.tunnels.BlockTunnelBeton1;
import mods.provoda.objblocks.tunnels.BlockTunnelBetonDerevo;
import mods.provoda.objblocks.tunnels.BlockTunnelBetonDerevo1;
import mods.provoda.objblocks.tunnels.BlockTunnelBetonDouble1;
import mods.provoda.objblocks.tunnels.BlockTunnelBetonExit;
import mods.provoda.objblocks.tunnels.BlockTunnelBetonProvoda;
import mods.provoda.objblocks.tunnels.BlockTunnelBetonTrub;
import mods.provoda.objblocks.tunnels.BlockTunnelBetonX;
import mods.provoda.objblocks.tunnels.BlockTunnelNew;
import mods.provoda.objblocks.tunnels.BlockTunnelZelezo1;
import mods.provoda.objblocks.tunnels.BlockTunnelZelezo90;
import mods.provoda.objblocks.tunnels.BlockTunnelZelezoDerevo;
import mods.provoda.objblocks.tunnels.BlockTunnelZelezoDerevo1;
import mods.provoda.objblocks.tunnels.BlockTunnelZelezoDouble;
import mods.provoda.objblocks.tunnels.BlockTunnelZelezoExit;
import mods.provoda.objblocks.tunnels.BlockTunnelZelezoProvoda;
import mods.provoda.objblocks.tunnels.BlockTunnelZelezoTrub;
import mods.provoda.tileentitys.TileEntityBarStol;
import mods.provoda.tileentitys.TileEntityBed;
import mods.provoda.tileentitys.TileEntityBox1;
import mods.provoda.tileentitys.TileEntityBoxp;
import mods.provoda.tileentitys.TileEntityBoxpl;
import mods.provoda.tileentitys.TileEntityChair1;
import mods.provoda.tileentitys.TileEntityDivan;
import mods.provoda.tileentitys.TileEntityDivan1;
import mods.provoda.tileentitys.TileEntityDivan2;
import mods.provoda.tileentitys.TileEntityDivan3;
import mods.provoda.tileentitys.TileEntityDivan4;
import mods.provoda.tileentitys.TileEntityElBox1;
import mods.provoda.tileentitys.TileEntityElBox2;
import mods.provoda.tileentitys.TileEntityElBox3;
import mods.provoda.tileentitys.TileEntityElBox4;
import mods.provoda.tileentitys.TileEntityFp1;
import mods.provoda.tileentitys.TileEntityFp2;
import mods.provoda.tileentitys.TileEntityFreez1;
import mods.provoda.tileentitys.TileEntityFreez2;
import mods.provoda.tileentitys.TileEntityGermo;
import mods.provoda.tileentitys.TileEntityGermo1;
import mods.provoda.tileentitys.TileEntityGermo2;
import mods.provoda.tileentitys.TileEntityGunb1;
import mods.provoda.tileentitys.TileEntityGunb2;
import mods.provoda.tileentitys.TileEntityKab;
import mods.provoda.tileentitys.TileEntityKan;
import mods.provoda.tileentitys.TileEntityKreslo;
import mods.provoda.tileentitys.TileEntityLav2;
import mods.provoda.tileentitys.TileEntityLavka1;
import mods.provoda.tileentitys.TileEntityPalatka;
import mods.provoda.tileentitys.TileEntityPhone;
import mods.provoda.tileentitys.TileEntityPlita;
import mods.provoda.tileentitys.TileEntityPolka1;
import mods.provoda.tileentitys.TileEntityPolka2;
import mods.provoda.tileentitys.TileEntityPolka3;
import mods.provoda.tileentitys.TileEntityPolka4;
import mods.provoda.tileentitys.TileEntityRak;
import mods.provoda.tileentitys.TileEntityShkaf;
import mods.provoda.tileentitys.TileEntityShkaf0;
import mods.provoda.tileentitys.TileEntityShkaf1;
import mods.provoda.tileentitys.TileEntityShkaf2;
import mods.provoda.tileentitys.TileEntityShkafm;
import mods.provoda.tileentitys.TileEntityStand;
import mods.provoda.tileentitys.TileEntityStelazh1;
import mods.provoda.tileentitys.TileEntityStelazh2;
import mods.provoda.tileentitys.TileEntityStelazh3;
import mods.provoda.tileentitys.TileEntityStol;
import mods.provoda.tileentitys.TileEntityStolik;
import mods.provoda.tileentitys.TileEntityStul;
import mods.provoda.tileentitys.TileEntityStul1;
import mods.provoda.tileentitys.TileEntityStul2;
import mods.provoda.tileentitys.TileEntityTabl1;
import mods.provoda.tileentitys.TileEntityTul1;
import mods.provoda.tileentitys.TileEntityTul2;
import mods.provoda.tileentitys.TileEntityTumba1;
import mods.provoda.tileentitys.TileEntityTumba2;
import mods.provoda.tileentitys.TileEntityTumba3;
import mods.provoda.tileentitys.TileEntityTunnel;
import mods.provoda.tileentitys.TileEntityTv1;
import mods.provoda.tileentitys.TileEntityUmi;
import mods.provoda.tileentitys.TileEntityVanna;
import mods.provoda.tileentitys.TileEntityWash;
import mods.provoda.tileentitys.death.TileEntityDeath1;
import mods.provoda.tileentitys.tunnels.TileEntityGag;
import mods.provoda.tileentitys.tunnels.TileEntityTunnel90;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelBeton1;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelBetonDerevo;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelBetonDerevo1;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelBetonDouble1;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelBetonExit;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelBetonProvoda;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelBetonTrub;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelBetonX;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelNew;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelZelezo1;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelZelezo90;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelZelezoDerevo;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelZelezoDerevo1;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelZelezoDouble;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelZelezoExit;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelZelezoProvoda;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelZelezoTrub;
import mods.provoda.tileentitys.tunnels.TileEntityWideRail;
import mods.provoda.until.AirSetup;
import mods.provoda.until.BlockLightTile;
import mods.provoda.until.CommandDefence;
import mods.provoda.until.CustomEPP;
import mods.provoda.until.EventHandler;
import mods.provoda.until.GuiHandler;
import mods.provoda.until.PacketHandler;
import mods.provoda.until.PlayerTracker;
import mods.provoda.until.ServerTickHandler;
import mods.provoda.until.Sounds;
import mods.provoda.until.Thirst;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid="Metro 2033 Server Mod", name="Metro 2033 Server Mod", version="PreBeta 0.0")
@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels={"tutchannel"}, packetHandler=PacketHandler.class)
public class ModBase {
    public static final String modid = "provoda";
    public static int clientType;
    public static String VDNHx;
    public static String VDNHy;
    public static String VDNHz;
    public static String Polisx;
    public static String Polisy;
    public static String Polisz;
    public static String Krx;
    public static String Kry;
    public static String Krz;
    public static String Ganzax;
    public static String Ganzay;
    public static String Ganzaz;
    public static String Prestx;
    public static String Presty;
    public static String Prestz;
    public static String ArbatKonfederationx;
    public static String ArbatKonfederationy;
    public static String ArbatKonfederationz;
    public static String Konfederation1905yearx;
    public static String Konfederation1905yeary;
    public static String Konfederation1905yearz;
    public static String Nazix;
    public static String Naziy;
    public static String Naziz;
    public static ve MetroTab;
    public static ve MetroTabDecor;
    public static ve MetroTabGuns;
    public static ve MetroTabArmor;
    public static ve MetroTabAtt;
    public static ve MetroTabTunnels;
    static int startEntityId;
    public static boolean DrawBlockHighlight;
    public static apa blocklight;
    public static apa blocklight1;
    public static apa blocklight2;
    public static apa blocklight3;
    public static apa Block1;
    public static apa Block46;
    public static apa Block47;
    public static apa EmptyShkaf1;
    public static apa EmptyShkaf2;
    public static apa Empty;
    public static apa Empty25;
    public static apa Empty50;
    public static apa EmptyPhone;
    public static apa EmptyStand;
    public static apa EmptyStand2;
    public static apa Block2;
    public static apa Block3;
    public static apa Block4;
    public static apa Block5;
    public static apa Block7;
    public static apa Block8;
    public static apa Block9;
    public static apa Block10;
    public static apa Block11;
    public static apa Block12;
    public static apa Block13;
    public static apa Block14;
    public static apa Block15;
    public static apa Block16;
    public static apa Block17;
    public static apa Block18;
    public static apa Block19;
    public static apa Block20;
    public static apa Block21;
    public static apa Block22;
    public static apa Block23;
    public static apa Block24;
    public static apa Block25;
    public static apa Block26;
    public static apa Block27;
    public static apa Block28;
    public static apa Block29;
    public static apa Block30;
    public static apa Block31;
    public static apa Block32;
    public static apa Block33;
    public static apa Block34;
    public static apa Block35;
    public static apa Block36;
    public static apa Block37;
    public static apa Block38;
    public static apa Block39;
    public static apa Block40;
    public static apa Block41;
    public static apa Block42;
    public static apa Block43;
    public static apa Block44;
    public static apa Block45;
    public static apa Block6;
    public static apa Block48;
    public static apa Block49;
    public static apa Block50;
    public static apa Block_Radiation;
    public static apa Block_Grass;
    public static apa Block_Grass_Tall;
    public static apa Double_Grass_Buttom;
    public static apa Double_Grass_Top;
    public static apa Flour_Tall;
    public static apa Sapling_Tall;
    public static final apa rails;
    public static final apa gag;
    public static final apa railGag;
    public static final wk itemRails;
    public static final wk itemRegina;
    public static final wk itemDrezina;
    public static uq BackPackMaterial;
    public static uq GasMaskMaterial;
    public static uq HeavyArmorMaterial;
    public static uq SpartaArmorMaterial;
    public static uq HeavyPlusArmorMaterial;
    public static uq HeavyPlus1ArmorMaterial;
    public static uq NormalArmorMaterial;
    public static uq LowArmorMaterial;
    public static uq VeryLowArmorMaterial;
    public static uq HimzaArmorMaterial;
    public static uq CcleanArmorMaterial;
    public static uq HeavyHatArmorMaterial;
    public static uq HeavyNaziHatArmorMaterial;
    public static uq Heavy1HatArmorMaterial;
    public static wk Machete;
    public static wk Himza;
    public static wk Heavy;
    public static wk Clean;
    public static wk CClean;
    public static wk Normal;
    public static wk Kozh;
    public static wk Easy;
    public static wk Heavy1;
    public static wk Sparta;
    public static wk HeavyHat;
    public static wk NaziHat;
    public static wk Heavy1Hat;
    public static wk Red;
    public static wk Nazi;
    public static wk filter;
    public static wk gas_mask;
    public static wk Patron;
    public static wk GoldMag;
    public static wk Zheton;
    public static wk Item1;
    public static wk Item2;
    public static wk Item3;
    public static wk Item4;
    public static wk Item5;
    public static wk Item6;
    public static wk Item7;
    public static wk Item8;
    public static wk Item9;
    public static wk Item10;
    public static wk Priklad;
    public static wk WeaponParts;
    public static wk Stvol;
    public static wk GazMeh;
    public static wk Cev;
    public static wk Optica;
    public static wk OpticaSmall;
    public static wk Kolimator;
    public static wk Butt;
    public static wk UboinikBarrel;
    public static wk Barrel;
    public static wk Laser;
    public static wk Glushak;
    public static wk AutoFire;
    public static wk AbzacGlushak;
    public static wk VentilGlushak;
    public static wk VsvGlushak;
    public static wk UbludokButt;
    public static wk AshotButt;
    public static wk QuadBarrel;
    public static wk DupletGlushak;
    public static wk DupletBarrel;
    public static wk DupletPriklad;
    public static wk DupletButt;
    public static wk GatlingButt;
    public static wk GatlingBarrel;
    public static wk GatlingMotor;
    public static wk GatlingGiro;
    public static wk TiharOptica;
    public static wk PodonokButt;
    public static wk PodonokGas;
    public static wk RailgunAkk;
    public static wk RailgunKon;
    public static wk BackPackSmall;
    public static wk BackPackMedium;
    public static wk BackPackLarge;
    public static wk Med;
    public static wk Lamp;
    public static wk Power;
    public static wk Flyashka;
    public static wk Bullet5x45;
    public static wk Bullet12x70;
    public static wk Bullet12x7;
    public static wk BulletBolt;
    public static wk Bullet15mm;
    public static wk BulletMagnum;
    public static wk Bullet12_7x108;
    public static wk Bullet7_62x54;
    public static wk Bullet12_7x55;
    public static wk Grenade;
    public static wk GrenadeBig;
    public static wk Ubludok;
    public static wk AK2012;
    public static wk AK;
    public static wk AKSU;
    public static wk Ashot;
    public static wk Duplet;
    public static wk Gatling;
    public static wk Helsing;
    public static wk Tihar;
    public static wk Podonok;
    public static wk Preved;
    public static wk Railgun;
    public static wk Revolver;
    public static wk Rpk;
    public static wk Saiga;
    public static wk Abzac;
    public static wk Uboinik;
    public static wk Ventil;
    public static wk Vsv;
    public static wk Vyhlop;
    public static wk Velopal;
    public static wk Ognemet;
    public static wk PodonokAmmo;
    public static wk TiharAmmo;
    public static wk HelsingAmmo;
    public static wk GatlingAmmo;
    public static wk UbludokAmmo;
    public static wk AK2012Ammo;
    public static wk AKAmmo;
    public static wk RpkAmmo;
    public static wk RpkAmmoBig;
    public static wk SaigaAmmo;
    public static wk SaigaAmmoBig;
    public static wk AbzacAmmo;
    public static wk AbzacAmmoBig;
    public static wk VentilAmmo;
    public static wk VentilAmmoBig;
    public static wk VsvAmmo;
    public static wk VyhlopAmmo;
    public static wk OgnemetAmmo;
    public static wk PodonokAmmoBig;
    public static wk PrevedAmmo;
    public static wk AKK;
    public static wk AKKAmmo;
    @SidedProxy(clientSide="mods.provoda.ClientProxy", serverSide="mods.provoda.CommonProxy")
    public static CommonProxy proxy;
    public static int renderModelGermo2;
    public static int renderModelGermo1;
    public static int renderModelGermo;
    public static int renderModelShkaf1;
    public static int renderModelShkaf2;
    public static int renderModelShkaf;
    public static int renderModelChair1;
    public static int renderModelChair2;
    public static int renderModelLavka1;
    public static int renderModelPolka1;
    public static int renderModelPolka2;
    public static int renderModelTabl1;
    public static int renderModelVanna;
    public static int renderModelWash;
    public static int renderModelTumba1;
    public static int renderModelTumba2;
    public static int renderModelTumba3;
    public static int renderModelBarStol;
    public static int renderModelBed;
    public static int renderModelBox1;
    public static int renderModelBoxp;
    public static int renderModelBoxpl;
    public static int renderModelDivan1;
    public static int renderModelDivan2;
    public static int renderModelDivan3;
    public static int renderModelDivan4;
    public static int renderModelElBox1;
    public static int renderModelElBox2;
    public static int renderModelElBox3;
    public static int renderModelElBox4;
    public static int renderModelFreez1;
    public static int renderModelFreez2;
    public static int renderModelGunb1;
    public static int renderModelGunb2;
    public static int renderModelKab;
    public static int renderModelKan;
    public static int renderModelLav2;
    public static int renderModelPhone;
    public static int renderModelPlita;
    public static int renderModelRak;
    public static int renderModelShkaf0;
    public static int renderModelShkafm;
    public static int renderModelStand;
    public static int renderModelStelazh1;
    public static int renderModelStelazh2;
    public static int renderModelStelazh3;
    public static int renderModelStul1;
    public static int renderModelStul2;
    public static int renderModelTul1;
    public static int renderModelTul2;
    public static int renderModelUmi;
    public static int renderModelFp1;
    public static int renderModelFp2;
    public static int renderModelTv1;
    public static int renderModelStul;
    public static int renderModelStol;
    public static int renderModelDivan;
    public static int renderModelKreslo;
    public static int renderModelStolik;
    public static int renderModelPolka3;
    public static int renderModelPolka4;
    public static int renderModelPalatka;
    public static int renderModelTunnel;
    public static int renderModelTunnel90;
    public static int renderModelTunnelBeton1;
    public static int renderModelTunnelZelezo1;
    public static int renderModelTunnelBetonDouble1;
    public static int renderModelTunnelBetonX;
    public static int renderModelTunnelZelezo90;
    public static int renderModelTunnelZelezoDouble;
    public static int renderModelTunnelZelezoExit;
    public static int renderModelTunnelBetonExit;
    public static int renderModelTunnelZelezoProvoda;
    public static int renderModelTunnelBetonProvoda;
    public static int renderModelTunnelBetonDerevo;
    public static int renderModelTunnelBetonDerevo1;
    public static int renderModelTunnelZelezoDerevo;
    public static int renderModelTunnelZelezoDerevo1;
    public static int renderModelTunnelBetonTrub;
    public static int renderModelTunnelZelezoTrub;
    public static int renderModelTunnelNew;
    public static int renderModelDeath1;
    public static apa ModelGermo2;
    public static apa ModelGermo1;
    public static apa ModelGermo;
    public static apa ModelShkaf;
    public static apa ModelShkaf1;
    public static apa ModelChair1;
    public static apa ModelShkaf2;
    public static apa ModelChair2;
    public static apa ModelLavka1;
    public static apa ModelPolka1;
    public static apa ModelPolka2;
    public static apa ModelTabl1;
    public static apa ModelVanna;
    public static apa ModelWash;
    public static apa ModelTumba1;
    public static apa ModelTumba2;
    public static apa ModelTumba3;
    public static apa ModelBarStol;
    public static apa ModelBed;
    public static apa ModelBox1;
    public static apa ModelBoxp;
    public static apa ModelBoxpl;
    public static apa ModelDivan1;
    public static apa ModelDivan2;
    public static apa ModelDivan3;
    public static apa ModelDivan4;
    public static apa ModelElBox1;
    public static apa ModelElBox2;
    public static apa ModelElBox3;
    public static apa ModelElBox4;
    public static apa ModelFreez1;
    public static apa ModelFreez2;
    public static apa ModelGunb1;
    public static apa ModelGunb2;
    public static apa ModelKab;
    public static apa ModelKan;
    public static apa ModelLav2;
    public static apa ModelPhone;
    public static apa ModelPlita;
    public static apa ModelRak;
    public static apa ModelShkaf0;
    public static apa ModelShkafm;
    public static apa ModelStand;
    public static apa ModelStelazh1;
    public static apa ModelStelazh2;
    public static apa ModelStelazh3;
    public static apa ModelStul1;
    public static apa ModelStul2;
    public static apa ModelTul1;
    public static apa ModelTul2;
    public static apa ModelUmi;
    public static apa ModelTv1;
    public static apa ModelFp1;
    public static apa ModelFp2;
    public static apa ModelPalatka;
    public static apa ModelTunnel;
    public static apa ModelTunnel90;
    public static apa ModelTunnelBeton1;
    public static apa ModelTunnelZelezo1;
    public static apa ModelTunnelBetonDouble1;
    public static apa ModelTunnelBetonX;
    public static apa ModelTunnelZelezo90;
    public static apa ModelTunnelZelezoDouble;
    public static apa ModelTunnelZelezoExit;
    public static apa ModelTunnelBetonExit;
    public static apa ModelTunnelBetonProvoda;
    public static apa ModelTunnelZelezoProvoda;
    public static apa ModelTunnelBetonDerevo;
    public static apa ModelTunnelBetonDerevo1;
    public static apa ModelTunnelZelezoDerevo;
    public static apa ModelTunnelZelezoDerevo1;
    public static apa ModelTunnelZelezoTrub;
    public static apa ModelTunnelBetonTrub;
    public static apa ModelTunnelNew;
    public static aav[] allBiomes;
    public static apa ModelStol;
    public static apa ModelStul;
    public static apa ModelPolka3;
    public static apa ModelPolka4;
    public static apa ModelDivan;
    public static apa ModelKreslo;
    public static apa ModelStolik;
    public static apa BlockTp;
    @Mod.Instance(value="Metro 2033 Server Mod")
    public static ModBase instance;

    @Mod.Init
    public void load(FMLInitializationEvent event) {
        allBiomes = new aav[]{aav.r, aav.d, aav.s, aav.e, aav.v, aav.f, aav.t, aav.l, aav.m, aav.j, aav.o, aav.n, aav.w, aav.x, aav.p, aav.q, aav.b, aav.c, aav.i, aav.k, aav.h, aav.g, aav.u};
        Block_Radiation = new Radiation(2001);
        Block_Grass = new Grass(2002).a(apa.i);
        Block_Grass_Tall = new GrassTall(2003).a(apa.i);
        Double_Grass_Buttom = new DoubleGrassButtom(2005).a(apa.i);
        Double_Grass_Top = new DoubleGrassTop(2006).a(apa.i);
        Sapling_Tall = new Sapling_Tall(2007).a(apa.i);
        Flour_Tall = new Flour_Tall(2008).a(apa.i);
        ModLoader.removeSpawn(qr.class, (nn)nn.d, (aav[])allBiomes);
        ModLoader.removeSpawn(ru.class, (nn)nn.a, (aav[])allBiomes);
        ModLoader.removeSpawn(sh.class, (nn)nn.a, (aav[])allBiomes);
        ModLoader.removeSpawn(sf.class, (nn)nn.a, (aav[])allBiomes);
        ModLoader.removeSpawn(rv.class, (nn)nn.a, (aav[])allBiomes);
        ModLoader.removeSpawn(sg.class, (nn)nn.a, (aav[])allBiomes);
        ModLoader.removeSpawn(sj.class, (nn)nn.a, (aav[])allBiomes);
        ModLoader.removeSpawn(rt.class, (nn)nn.a, (aav[])allBiomes);
        ModLoader.removeSpawn(qm.class, (nn)nn.a, (aav[])allBiomes);
        GameRegistry.registerBlock((apa)ModelGermo2);
        GameRegistry.registerBlock((apa)ModelGermo1);
        GameRegistry.registerBlock((apa)ModelGermo);
        GameRegistry.registerBlock((apa)ModelShkaf1);
        GameRegistry.registerBlock((apa)ModelShkaf);
        GameRegistry.registerBlock((apa)ModelShkaf2);
        GameRegistry.registerBlock((apa)ModelChair1);
        GameRegistry.registerBlock((apa)ModelChair2);
        GameRegistry.registerBlock((apa)ModelLavka1);
        GameRegistry.registerBlock((apa)ModelPolka1);
        GameRegistry.registerBlock((apa)ModelPolka2);
        GameRegistry.registerBlock((apa)ModelTabl1);
        GameRegistry.registerBlock((apa)ModelVanna);
        GameRegistry.registerBlock((apa)ModelWash);
        GameRegistry.registerBlock((apa)ModelTumba1);
        GameRegistry.registerBlock((apa)ModelTumba2);
        GameRegistry.registerBlock((apa)ModelTumba3);
        GameRegistry.registerBlock((apa)ModelBarStol);
        GameRegistry.registerBlock((apa)ModelBed);
        GameRegistry.registerBlock((apa)ModelBox1);
        GameRegistry.registerBlock((apa)ModelBoxp);
        GameRegistry.registerBlock((apa)ModelBoxpl);
        GameRegistry.registerBlock((apa)ModelDivan1);
        GameRegistry.registerBlock((apa)ModelDivan2);
        GameRegistry.registerBlock((apa)ModelDivan3);
        GameRegistry.registerBlock((apa)ModelDivan4);
        GameRegistry.registerBlock((apa)ModelElBox1);
        GameRegistry.registerBlock((apa)ModelElBox2);
        GameRegistry.registerBlock((apa)ModelElBox3);
        GameRegistry.registerBlock((apa)ModelElBox4);
        GameRegistry.registerBlock((apa)ModelFreez1);
        GameRegistry.registerBlock((apa)ModelFreez2);
        GameRegistry.registerBlock((apa)ModelGunb1);
        GameRegistry.registerBlock((apa)ModelGunb2);
        GameRegistry.registerBlock((apa)ModelKab);
        GameRegistry.registerBlock((apa)ModelKan);
        GameRegistry.registerBlock((apa)ModelLav2);
        GameRegistry.registerBlock((apa)ModelPhone);
        GameRegistry.registerBlock((apa)ModelPlita);
        GameRegistry.registerBlock((apa)ModelRak);
        GameRegistry.registerBlock((apa)ModelShkaf0);
        GameRegistry.registerBlock((apa)ModelShkafm);
        GameRegistry.registerBlock((apa)ModelStand);
        GameRegistry.registerBlock((apa)ModelStelazh1);
        GameRegistry.registerBlock((apa)ModelStelazh2);
        GameRegistry.registerBlock((apa)ModelStelazh3);
        GameRegistry.registerBlock((apa)ModelStul1);
        GameRegistry.registerBlock((apa)ModelStul2);
        GameRegistry.registerBlock((apa)ModelTul1);
        GameRegistry.registerBlock((apa)ModelTul2);
        GameRegistry.registerBlock((apa)ModelUmi);
        GameRegistry.registerBlock((apa)ModelFp1);
        GameRegistry.registerBlock((apa)ModelFp2);
        GameRegistry.registerBlock((apa)ModelTv1);
        GameRegistry.registerBlock((apa)ModelPalatka);
        GameRegistry.registerBlock((apa)rails, (String)"blockWideRail");
        GameRegistry.registerBlock((apa)Block_Radiation);
        GameRegistry.registerBlock((apa)Block_Grass);
        GameRegistry.registerBlock((apa)Block_Grass_Tall);
        GameRegistry.registerBlock((apa)Double_Grass_Top);
        GameRegistry.registerBlock((apa)Double_Grass_Buttom);
        GameRegistry.registerBlock((apa)Sapling_Tall);
        GameRegistry.registerBlock((apa)Flour_Tall);
        GameRegistry.registerBlock((apa)ModelStol);
        GameRegistry.registerBlock((apa)ModelStul);
        GameRegistry.registerBlock((apa)ModelPolka3);
        GameRegistry.registerBlock((apa)ModelPolka4);
        GameRegistry.registerBlock((apa)ModelDivan);
        GameRegistry.registerBlock((apa)ModelKreslo);
        GameRegistry.registerBlock((apa)ModelStolik);
        GameRegistry.registerBlock((apa)ModelTunnel);
        GameRegistry.registerBlock((apa)ModelTunnel90);
        GameRegistry.registerBlock((apa)ModelTunnelBeton1);
        GameRegistry.registerBlock((apa)ModelTunnelZelezo1);
        GameRegistry.registerBlock((apa)ModelTunnelBetonDouble1);
        GameRegistry.registerBlock((apa)ModelTunnelBetonX);
        GameRegistry.registerBlock((apa)ModelTunnelZelezo90);
        GameRegistry.registerBlock((apa)ModelTunnelZelezoDouble);
        GameRegistry.registerBlock((apa)ModelTunnelBetonExit);
        GameRegistry.registerBlock((apa)ModelTunnelZelezoExit);
        GameRegistry.registerBlock((apa)ModelTunnelBetonProvoda);
        GameRegistry.registerBlock((apa)ModelTunnelBetonDerevo);
        GameRegistry.registerBlock((apa)ModelTunnelBetonDerevo1);
        GameRegistry.registerBlock((apa)ModelTunnelBetonTrub);
        GameRegistry.registerBlock((apa)ModelTunnelZelezoProvoda);
        GameRegistry.registerBlock((apa)ModelTunnelZelezoDerevo);
        GameRegistry.registerBlock((apa)ModelTunnelZelezoDerevo1);
        GameRegistry.registerBlock((apa)ModelTunnelZelezoTrub);
        GameRegistry.registerBlock((apa)ModelTunnelNew);
        GameRegistry.registerBlock((apa)BlockTp);
        GameRegistry.registerTileEntity(TileEntityGermo2.class, (String)"TileEntityGermo2");
        GameRegistry.registerTileEntity(TileEntityGermo1.class, (String)"TileEntityGermo1");
        GameRegistry.registerTileEntity(TileEntityGermo.class, (String)"TileEntityGermo");
        GameRegistry.registerTileEntity(TileEntityShkaf.class, (String)"TileEntityShkaf");
        GameRegistry.registerTileEntity(TileEntityShkaf1.class, (String)"TileEntityShkaf1");
        GameRegistry.registerTileEntity(TileEntityShkaf2.class, (String)"TileEntityShkaf2");
        GameRegistry.registerTileEntity(TileEntityChair1.class, (String)"TileEntityChair1");
        GameRegistry.registerTileEntity(TileEntityChair1.class, (String)"TileEntityChair2");
        GameRegistry.registerTileEntity(TileEntityLavka1.class, (String)"TileEntityLavka1");
        GameRegistry.registerTileEntity(TileEntityPolka1.class, (String)"TileEntityPolka1");
        GameRegistry.registerTileEntity(TileEntityPolka2.class, (String)"TileEntityPolka2");
        GameRegistry.registerTileEntity(TileEntityTabl1.class, (String)"TileEntityTabl1");
        GameRegistry.registerTileEntity(TileEntityVanna.class, (String)"TileEntityVanna");
        GameRegistry.registerTileEntity(TileEntityWash.class, (String)"TileEntityWash");
        GameRegistry.registerTileEntity(TileEntityTumba1.class, (String)"TileEntityTumba1");
        GameRegistry.registerTileEntity(TileEntityTumba2.class, (String)"TileEntityTumba2");
        GameRegistry.registerTileEntity(TileEntityTumba3.class, (String)"TileEntityTumba3");
        GameRegistry.registerTileEntity(TileEntityBarStol.class, (String)"TileEntityBarStol");
        GameRegistry.registerTileEntity(TileEntityBed.class, (String)"TileEntityBed");
        GameRegistry.registerTileEntity(TileEntityBox1.class, (String)"TileEntityBox1");
        GameRegistry.registerTileEntity(TileEntityBoxp.class, (String)"TileEntityBoxp");
        GameRegistry.registerTileEntity(TileEntityBoxpl.class, (String)"TileEntityBoxpl");
        GameRegistry.registerTileEntity(TileEntityDivan1.class, (String)"TileEntityDivan1");
        GameRegistry.registerTileEntity(TileEntityDivan2.class, (String)"TileEntityDivan2");
        GameRegistry.registerTileEntity(TileEntityDivan3.class, (String)"TileEntityDivan3");
        GameRegistry.registerTileEntity(TileEntityDivan4.class, (String)"TileEntityDivan4");
        GameRegistry.registerTileEntity(TileEntityElBox1.class, (String)"TileEntityElBox1");
        GameRegistry.registerTileEntity(TileEntityElBox2.class, (String)"TileEntityElBox2");
        GameRegistry.registerTileEntity(TileEntityElBox3.class, (String)"TileEntityElBox3");
        GameRegistry.registerTileEntity(TileEntityElBox4.class, (String)"TileEntityElBox4");
        GameRegistry.registerTileEntity(TileEntityFreez1.class, (String)"TileEntityFreez1");
        GameRegistry.registerTileEntity(TileEntityFreez2.class, (String)"TileEntityFreez2");
        GameRegistry.registerTileEntity(TileEntityGunb1.class, (String)"TileEntityGunb1");
        GameRegistry.registerTileEntity(TileEntityGunb2.class, (String)"TileEntityGunb2");
        GameRegistry.registerTileEntity(TileEntityKab.class, (String)"TileEntityKab");
        GameRegistry.registerTileEntity(TileEntityKan.class, (String)"TileEntityKan");
        GameRegistry.registerTileEntity(TileEntityLav2.class, (String)"TileEntityLav2");
        GameRegistry.registerTileEntity(TileEntityPhone.class, (String)"TileEntityPhone");
        GameRegistry.registerTileEntity(TileEntityPlita.class, (String)"TileEntityPlita");
        GameRegistry.registerTileEntity(TileEntityRak.class, (String)"TileEntityRak");
        GameRegistry.registerTileEntity(TileEntityShkaf0.class, (String)"TileEntityShkaf0");
        GameRegistry.registerTileEntity(TileEntityShkafm.class, (String)"TileEntityShkafm");
        GameRegistry.registerTileEntity(TileEntityStand.class, (String)"TileEntityStand");
        GameRegistry.registerTileEntity(TileEntityStelazh1.class, (String)"TileEntityStelazh1");
        GameRegistry.registerTileEntity(TileEntityStelazh2.class, (String)"TileEntityStelazh2");
        GameRegistry.registerTileEntity(TileEntityStelazh3.class, (String)"TileEntityStelazh3");
        GameRegistry.registerTileEntity(TileEntityStul1.class, (String)"TileEntityStul1");
        GameRegistry.registerTileEntity(TileEntityStul2.class, (String)"TileEntityStul2");
        GameRegistry.registerTileEntity(TileEntityTul1.class, (String)"TileEntityTul1");
        GameRegistry.registerTileEntity(TileEntityTul2.class, (String)"TileEntityTul2");
        GameRegistry.registerTileEntity(TileEntityUmi.class, (String)"TileEntityUmi");
        GameRegistry.registerTileEntity(TileEntityFp1.class, (String)"TileEntityFp1");
        GameRegistry.registerTileEntity(TileEntityFp2.class, (String)"TileEntityFp2");
        GameRegistry.registerTileEntity(TileEntityTv1.class, (String)"TileEntityTv1");
        GameRegistry.registerTileEntity(TileEntityPalatka.class, (String)"TileEntityPalatka");
        GameRegistry.registerTileEntity(TileEntityWideRail.class, (String)"tileEntityWideRail");
        GameRegistry.registerTileEntity(TileEntityGag.class, (String)"tileEntityGag");
        itemRails.a(MetroTabTunnels);
        itemRegina.a(MetroTabTunnels);
        itemDrezina.a(MetroTabTunnels);
        for (int i = 0; i < ItemWideRails.Names.length; ++i) {
            wm ira = new wm(itemRails, 1, i);
            LanguageRegistry.addName((Object)ira, (String)ItemWideRails.Names[i]);
        }
        LanguageRegistry.addName((Object)rails, (String)"You shall not get this!");
        LanguageRegistry.addName((Object)gag, (String)"You shall not get this!");
        LanguageRegistry.addName((Object)railGag, (String)"You shall not get this!");
        LanguageRegistry.addName((Object)itemRegina, (String)"\u0420\u0435\u0433\u0438\u043d\u0430");
        LanguageRegistry.addName((Object)itemDrezina, (String)"4-\u0451\u0445 \u043c\u0435\u0441\u0442\u043d\u0430\u044f \u0434\u0440\u0435\u0437\u0438\u043d\u0430");
        GameRegistry.registerTileEntity(BlockLightTile.class, (String)"BlockLightTile");
        GameRegistry.registerTileEntity(TileEntityStol.class, (String)"TileEntityStol");
        GameRegistry.registerTileEntity(TileEntityStul.class, (String)"TileEntityStul");
        GameRegistry.registerTileEntity(TileEntityPolka3.class, (String)"TileEntityPolka3");
        GameRegistry.registerTileEntity(TileEntityPolka4.class, (String)"TileEntityPolka4");
        GameRegistry.registerTileEntity(TileEntityDivan.class, (String)"TileEntityDivan");
        GameRegistry.registerTileEntity(TileEntityKreslo.class, (String)"TileEntityKreslo");
        GameRegistry.registerTileEntity(TileEntityStolik.class, (String)"TileEntityStolik");
        GameRegistry.registerTileEntity(TileEntityTunnel.class, (String)"TileEntityTunnel");
        GameRegistry.registerTileEntity(TileEntityTunnel90.class, (String)"TileEntityTunnel90");
        GameRegistry.registerTileEntity(TileEntityTunnelBeton1.class, (String)"TileEntityTunnelBeton1");
        GameRegistry.registerTileEntity(TileEntityTunnelZelezo1.class, (String)"TileEntityTunnelZelezo1");
        GameRegistry.registerTileEntity(TileEntityTunnelBetonDouble1.class, (String)"TileEntityTunnelBetonDouble1");
        GameRegistry.registerTileEntity(TileEntityTunnelBetonX.class, (String)"TileEntityTunnelBetonX");
        GameRegistry.registerTileEntity(TileEntityTunnelZelezo90.class, (String)"TileEntityTunnelZelezo90");
        GameRegistry.registerTileEntity(TileEntityTunnelZelezoDouble.class, (String)"TileEntityTunnelZelezoDouble");
        GameRegistry.registerTileEntity(TileEntityTunnelZelezoExit.class, (String)"TileEntityTunnelZelezoExit");
        GameRegistry.registerTileEntity(TileEntityTunnelBetonExit.class, (String)"TileEntityTunnelBetonExit");
        GameRegistry.registerTileEntity(TileEntityTunnelNew.class, (String)"TileEntityTunnelNew");
        GameRegistry.registerTileEntity(TileEntityTunnelBetonProvoda.class, (String)"TileEntityTunnelBetonProvoda");
        GameRegistry.registerTileEntity(TileEntityTunnelBetonDerevo.class, (String)"TileEntityTunnelBetonDerevo");
        GameRegistry.registerTileEntity(TileEntityTunnelBetonDerevo1.class, (String)"TileEntityTunnelBetonDerevo1");
        GameRegistry.registerTileEntity(TileEntityTunnelZelezoDerevo.class, (String)"TileEntityTunnelZelezoDerevo");
        GameRegistry.registerTileEntity(TileEntityTunnelZelezoDerevo1.class, (String)"TileEntityTunnelZelezoDerevo1");
        GameRegistry.registerTileEntity(TileEntityTunnelZelezoProvoda.class, (String)"TileEntityTunnelZelezoProvoda");
        GameRegistry.registerTileEntity(TileEntityTunnelBetonTrub.class, (String)"TileEntityTunnelBetonTrub");
        GameRegistry.registerTileEntity(TileEntityTunnelZelezoTrub.class, (String)"TileEntityTunnelZelezoTrub");
        GameRegistry.registerTileEntity(TileEntityDeath1.class, (String)"TileEntityDeath1");
        LanguageRegistry.addName((Object)ModelGermo2, (String)"Germo_Left");
        LanguageRegistry.addName((Object)ModelGermo1, (String)"Germo");
        LanguageRegistry.addName((Object)ModelGermo, (String)"Germo1");
        LanguageRegistry.addName((Object)ModelShkaf1, (String)"Shkaf1");
        LanguageRegistry.addName((Object)ModelShkaf, (String)"Shkaf");
        LanguageRegistry.addName((Object)ModelShkaf2, (String)"Shkaf2");
        LanguageRegistry.addName((Object)ModelChair1, (String)"Chair1");
        LanguageRegistry.addName((Object)ModelChair2, (String)"Chair2");
        LanguageRegistry.addName((Object)ModelLavka1, (String)"Lavka1");
        LanguageRegistry.addName((Object)ModelPolka1, (String)"Polka1");
        LanguageRegistry.addName((Object)ModelPolka2, (String)"Polka2");
        LanguageRegistry.addName((Object)ModelTabl1, (String)"Tabl1");
        LanguageRegistry.addName((Object)ModelVanna, (String)"Vanna");
        LanguageRegistry.addName((Object)ModelWash, (String)"Wash");
        LanguageRegistry.addName((Object)ModelTumba1, (String)"Tumba1");
        LanguageRegistry.addName((Object)ModelTumba2, (String)"Tumba2");
        LanguageRegistry.addName((Object)ModelTumba3, (String)"Tumba3");
        LanguageRegistry.addName((Object)ModelBarStol, (String)"BarStol");
        LanguageRegistry.addName((Object)ModelBed, (String)"Bed");
        LanguageRegistry.addName((Object)ModelBox1, (String)"Box1");
        LanguageRegistry.addName((Object)ModelBoxp, (String)"Boxp");
        LanguageRegistry.addName((Object)ModelBoxpl, (String)"Boxpl");
        LanguageRegistry.addName((Object)ModelDivan1, (String)"Divan1");
        LanguageRegistry.addName((Object)ModelDivan2, (String)"Divan2");
        LanguageRegistry.addName((Object)ModelDivan3, (String)"Divan3");
        LanguageRegistry.addName((Object)ModelDivan4, (String)"Divan4");
        LanguageRegistry.addName((Object)ModelElBox1, (String)"ElBox1");
        LanguageRegistry.addName((Object)ModelElBox2, (String)"ElBox2");
        LanguageRegistry.addName((Object)ModelElBox3, (String)"ElBox3");
        LanguageRegistry.addName((Object)ModelElBox4, (String)"ElBox4");
        LanguageRegistry.addName((Object)ModelFreez1, (String)"Freez1");
        LanguageRegistry.addName((Object)ModelFreez2, (String)"Freez2");
        LanguageRegistry.addName((Object)ModelGunb1, (String)"Gunb1");
        LanguageRegistry.addName((Object)ModelGunb2, (String)"Gunb2");
        LanguageRegistry.addName((Object)ModelKab, (String)"Kab");
        LanguageRegistry.addName((Object)ModelKan, (String)"Kan");
        LanguageRegistry.addName((Object)ModelLav2, (String)"Lav2");
        LanguageRegistry.addName((Object)ModelPhone, (String)"Phone");
        LanguageRegistry.addName((Object)ModelPlita, (String)"Plita");
        LanguageRegistry.addName((Object)ModelRak, (String)"Rak");
        LanguageRegistry.addName((Object)ModelShkaf0, (String)"Shkaf0");
        LanguageRegistry.addName((Object)ModelShkafm, (String)"Shkafm");
        LanguageRegistry.addName((Object)ModelStand, (String)"Stand");
        LanguageRegistry.addName((Object)ModelStelazh1, (String)"Stelazh1");
        LanguageRegistry.addName((Object)ModelStelazh2, (String)"Stelazh2");
        LanguageRegistry.addName((Object)ModelStelazh3, (String)"Stelazh3");
        LanguageRegistry.addName((Object)ModelStul1, (String)"Stul1");
        LanguageRegistry.addName((Object)ModelStul2, (String)"Stul2");
        LanguageRegistry.addName((Object)ModelTul1, (String)"Tul1");
        LanguageRegistry.addName((Object)ModelTul2, (String)"Tul2");
        LanguageRegistry.addName((Object)ModelUmi, (String)"Umi");
        LanguageRegistry.addName((Object)ModelFp1, (String)"Fp1");
        LanguageRegistry.addName((Object)ModelFp2, (String)"Fp2");
        LanguageRegistry.addName((Object)ModelTv1, (String)"Tv1");
        LanguageRegistry.addName((Object)ModelPalatka, (String)"Palatka");
        LanguageRegistry.addName((Object)ModelStol, (String)"Stol");
        LanguageRegistry.addName((Object)ModelStul, (String)"Stul");
        LanguageRegistry.addName((Object)ModelPolka3, (String)"Polka3");
        LanguageRegistry.addName((Object)ModelPolka4, (String)"Polka4");
        LanguageRegistry.addName((Object)ModelDivan, (String)"Divan");
        LanguageRegistry.addName((Object)ModelKreslo, (String)"Kreslo");
        LanguageRegistry.addName((Object)ModelStolik, (String)"Stolik");
        LanguageRegistry.addName((Object)ModelTunnel, (String)"Tunnel");
        LanguageRegistry.addName((Object)ModelTunnel90, (String)"TunnelBeton90");
        LanguageRegistry.addName((Object)ModelTunnelZelezo90, (String)"TunnelZelezo90");
        LanguageRegistry.addName((Object)ModelTunnelBeton1, (String)"TunnelBeton1");
        LanguageRegistry.addName((Object)ModelTunnelZelezo1, (String)"TunnelZelezo1");
        LanguageRegistry.addName((Object)ModelTunnelBetonX, (String)"ModelTunnelBetonX");
        LanguageRegistry.addName((Object)ModelTunnelZelezoDouble, (String)"ModelTunnelZelezoDouble");
        LanguageRegistry.addName((Object)ModelTunnelBetonDouble1, (String)"ModelTunnelBetonDouble");
        LanguageRegistry.addName((Object)ModelTunnelBetonExit, (String)"ModelTunnelBetonExit");
        LanguageRegistry.addName((Object)ModelTunnelZelezoExit, (String)"ModelTunnelZelezoExit");
        LanguageRegistry.addName((Object)ModelTunnelBetonProvoda, (String)"TunnelBetonProvoda");
        LanguageRegistry.addName((Object)ModelTunnelBetonDerevo, (String)"TunnelBetonDerevo");
        LanguageRegistry.addName((Object)ModelTunnelBetonDerevo1, (String)"TunnelBetonDerevo1");
        LanguageRegistry.addName((Object)ModelTunnelBetonTrub, (String)"TunnelBetonTrub");
        LanguageRegistry.addName((Object)ModelTunnelZelezoProvoda, (String)"TunnelZelezoProvoda");
        LanguageRegistry.addName((Object)ModelTunnelZelezoDerevo, (String)"TunnelZelezoDerevo");
        LanguageRegistry.addName((Object)ModelTunnelZelezoDerevo1, (String)"TunnelZelezoDerevo1");
        LanguageRegistry.addName((Object)ModelTunnelZelezoTrub, (String)"TunnelZelezoTrub");
        LanguageRegistry.addName((Object)ModelTunnelNew, (String)"TunnelNew");
        LanguageRegistry.addName((Object)Block_Radiation, (String)"\u0420\u0430\u0434\u0438\u0430\u0446\u0438\u044f");
        LanguageRegistry.addName((Object)Block_Grass, (String)"\u0411\u043b\u043e\u043a \u0442\u0440\u0430\u0432\u044b");
        LanguageRegistry.addName((Object)Block_Grass_Tall, (String)"\u0422\u0440\u0430\u0432\u0430");
        LanguageRegistry.addName((Object)Double_Grass_Top, (String)"\u0414\u0432\u043e\u0439\u043d\u0430\u044f \u0442\u0440\u0430\u0432\u0430 \u0432\u0435\u0440\u0445");
        LanguageRegistry.addName((Object)Double_Grass_Buttom, (String)"\u0414\u0432\u043e\u0439\u043d\u0430\u044f \u0442\u0440\u0430\u0432\u0430 \u043d\u0438\u0437");
        renderModelGermo2 = RenderingRegistry.getNextAvailableRenderId();
        renderModelGermo1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelGermo = RenderingRegistry.getNextAvailableRenderId();
        renderModelShkaf = RenderingRegistry.getNextAvailableRenderId();
        renderModelShkaf1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelShkaf2 = RenderingRegistry.getNextAvailableRenderId();
        renderModelChair1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelChair2 = RenderingRegistry.getNextAvailableRenderId();
        renderModelLavka1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelPolka1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelPolka2 = RenderingRegistry.getNextAvailableRenderId();
        renderModelTabl1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelVanna = RenderingRegistry.getNextAvailableRenderId();
        renderModelWash = RenderingRegistry.getNextAvailableRenderId();
        renderModelTumba1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelTumba2 = RenderingRegistry.getNextAvailableRenderId();
        renderModelTumba3 = RenderingRegistry.getNextAvailableRenderId();
        renderModelBarStol = RenderingRegistry.getNextAvailableRenderId();
        renderModelBed = RenderingRegistry.getNextAvailableRenderId();
        renderModelBox1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelBoxp = RenderingRegistry.getNextAvailableRenderId();
        renderModelBoxpl = RenderingRegistry.getNextAvailableRenderId();
        renderModelDivan1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelDivan2 = RenderingRegistry.getNextAvailableRenderId();
        renderModelDivan3 = RenderingRegistry.getNextAvailableRenderId();
        renderModelDivan4 = RenderingRegistry.getNextAvailableRenderId();
        renderModelElBox1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelElBox2 = RenderingRegistry.getNextAvailableRenderId();
        renderModelElBox3 = RenderingRegistry.getNextAvailableRenderId();
        renderModelElBox4 = RenderingRegistry.getNextAvailableRenderId();
        renderModelFreez1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelFreez2 = RenderingRegistry.getNextAvailableRenderId();
        renderModelGunb1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelGunb2 = RenderingRegistry.getNextAvailableRenderId();
        renderModelKab = RenderingRegistry.getNextAvailableRenderId();
        renderModelKan = RenderingRegistry.getNextAvailableRenderId();
        renderModelLav2 = RenderingRegistry.getNextAvailableRenderId();
        renderModelPhone = RenderingRegistry.getNextAvailableRenderId();
        renderModelPlita = RenderingRegistry.getNextAvailableRenderId();
        renderModelRak = RenderingRegistry.getNextAvailableRenderId();
        renderModelShkaf0 = RenderingRegistry.getNextAvailableRenderId();
        renderModelShkafm = RenderingRegistry.getNextAvailableRenderId();
        renderModelStand = RenderingRegistry.getNextAvailableRenderId();
        renderModelStelazh1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelStelazh2 = RenderingRegistry.getNextAvailableRenderId();
        renderModelStelazh3 = RenderingRegistry.getNextAvailableRenderId();
        renderModelStul1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelStul2 = RenderingRegistry.getNextAvailableRenderId();
        renderModelTul1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelTul2 = RenderingRegistry.getNextAvailableRenderId();
        renderModelUmi = RenderingRegistry.getNextAvailableRenderId();
        renderModelFp1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelFp2 = RenderingRegistry.getNextAvailableRenderId();
        renderModelTv1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelPalatka = RenderingRegistry.getNextAvailableRenderId();
        renderModelStol = RenderingRegistry.getNextAvailableRenderId();
        renderModelStul = RenderingRegistry.getNextAvailableRenderId();
        renderModelPolka3 = RenderingRegistry.getNextAvailableRenderId();
        renderModelPolka4 = RenderingRegistry.getNextAvailableRenderId();
        renderModelDivan = RenderingRegistry.getNextAvailableRenderId();
        renderModelKreslo = RenderingRegistry.getNextAvailableRenderId();
        renderModelStolik = RenderingRegistry.getNextAvailableRenderId();
        renderModelTunnel = RenderingRegistry.getNextAvailableRenderId();
        renderModelTunnel90 = RenderingRegistry.getNextAvailableRenderId();
        renderModelTunnelBeton1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelTunnelZelezo1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelTunnelBetonDouble1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelTunnelBetonX = RenderingRegistry.getNextAvailableRenderId();
        renderModelTunnelZelezo90 = RenderingRegistry.getNextAvailableRenderId();
        renderModelTunnelZelezoDouble = RenderingRegistry.getNextAvailableRenderId();
        renderModelTunnelZelezoExit = RenderingRegistry.getNextAvailableRenderId();
        renderModelTunnelBetonExit = RenderingRegistry.getNextAvailableRenderId();
        renderModelTunnelBetonProvoda = RenderingRegistry.getNextAvailableRenderId();
        renderModelTunnelZelezoProvoda = RenderingRegistry.getNextAvailableRenderId();
        renderModelTunnelBetonDerevo = RenderingRegistry.getNextAvailableRenderId();
        renderModelTunnelBetonDerevo1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelTunnelZelezoDerevo = RenderingRegistry.getNextAvailableRenderId();
        renderModelTunnelZelezoDerevo1 = RenderingRegistry.getNextAvailableRenderId();
        renderModelTunnelBetonTrub = RenderingRegistry.getNextAvailableRenderId();
        renderModelTunnelZelezoTrub = RenderingRegistry.getNextAvailableRenderId();
        renderModelTunnelNew = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler((int)renderModelGermo2, (ISimpleBlockRenderingHandler)new BlockHandlerGermo2());
        RenderingRegistry.registerBlockHandler((int)renderModelGermo1, (ISimpleBlockRenderingHandler)new BlockHandlerGermo1());
        RenderingRegistry.registerBlockHandler((int)renderModelGermo, (ISimpleBlockRenderingHandler)new BlockHandlerGermo());
        RenderingRegistry.registerBlockHandler((int)renderModelShkaf1, (ISimpleBlockRenderingHandler)new BlockHandlerShkaf1());
        RenderingRegistry.registerBlockHandler((int)renderModelShkaf2, (ISimpleBlockRenderingHandler)new BlockHandlerShkaf2());
        RenderingRegistry.registerBlockHandler((int)renderModelShkaf, (ISimpleBlockRenderingHandler)new BlockHandlerShkaf());
        RenderingRegistry.registerBlockHandler((int)renderModelChair1, (ISimpleBlockRenderingHandler)new BlockHandlerChair1());
        RenderingRegistry.registerBlockHandler((int)renderModelChair2, (ISimpleBlockRenderingHandler)new BlockHandlerChair2());
        RenderingRegistry.registerBlockHandler((int)renderModelLavka1, (ISimpleBlockRenderingHandler)new BlockHandlerLavka1());
        RenderingRegistry.registerBlockHandler((int)renderModelPolka1, (ISimpleBlockRenderingHandler)new BlockHandlerPolka1());
        RenderingRegistry.registerBlockHandler((int)renderModelPolka2, (ISimpleBlockRenderingHandler)new BlockHandlerPolka2());
        RenderingRegistry.registerBlockHandler((int)renderModelTabl1, (ISimpleBlockRenderingHandler)new BlockHandlerTabl1());
        RenderingRegistry.registerBlockHandler((int)renderModelVanna, (ISimpleBlockRenderingHandler)new BlockHandlerVanna());
        RenderingRegistry.registerBlockHandler((int)renderModelWash, (ISimpleBlockRenderingHandler)new BlockHandlerWash());
        RenderingRegistry.registerBlockHandler((int)renderModelTumba1, (ISimpleBlockRenderingHandler)new BlockHandlerTumba1());
        RenderingRegistry.registerBlockHandler((int)renderModelTumba2, (ISimpleBlockRenderingHandler)new BlockHandlerTumba2());
        RenderingRegistry.registerBlockHandler((int)renderModelTumba3, (ISimpleBlockRenderingHandler)new BlockHandlerTumba3());
        RenderingRegistry.registerBlockHandler((int)renderModelBarStol, (ISimpleBlockRenderingHandler)new BlockHandlerBarStol());
        RenderingRegistry.registerBlockHandler((int)renderModelBed, (ISimpleBlockRenderingHandler)new BlockHandlerBed());
        RenderingRegistry.registerBlockHandler((int)renderModelBox1, (ISimpleBlockRenderingHandler)new BlockHandlerBox1());
        RenderingRegistry.registerBlockHandler((int)renderModelBoxp, (ISimpleBlockRenderingHandler)new BlockHandlerBoxp());
        RenderingRegistry.registerBlockHandler((int)renderModelBoxpl, (ISimpleBlockRenderingHandler)new BlockHandlerBoxpl());
        RenderingRegistry.registerBlockHandler((int)renderModelDivan1, (ISimpleBlockRenderingHandler)new BlockHandlerDivan1());
        RenderingRegistry.registerBlockHandler((int)renderModelDivan2, (ISimpleBlockRenderingHandler)new BlockHandlerDivan2());
        RenderingRegistry.registerBlockHandler((int)renderModelDivan3, (ISimpleBlockRenderingHandler)new BlockHandlerDivan3());
        RenderingRegistry.registerBlockHandler((int)renderModelDivan4, (ISimpleBlockRenderingHandler)new BlockHandlerDivan4());
        RenderingRegistry.registerBlockHandler((int)renderModelElBox1, (ISimpleBlockRenderingHandler)new BlockHandlerElBox1());
        RenderingRegistry.registerBlockHandler((int)renderModelElBox2, (ISimpleBlockRenderingHandler)new BlockHandlerElBox2());
        RenderingRegistry.registerBlockHandler((int)renderModelElBox3, (ISimpleBlockRenderingHandler)new BlockHandlerElBox3());
        RenderingRegistry.registerBlockHandler((int)renderModelElBox4, (ISimpleBlockRenderingHandler)new BlockHandlerElBox4());
        RenderingRegistry.registerBlockHandler((int)renderModelFreez1, (ISimpleBlockRenderingHandler)new BlockHandlerFreez1());
        RenderingRegistry.registerBlockHandler((int)renderModelFreez2, (ISimpleBlockRenderingHandler)new BlockHandlerFreez2());
        RenderingRegistry.registerBlockHandler((int)renderModelGunb1, (ISimpleBlockRenderingHandler)new BlockHandlerGunb1());
        RenderingRegistry.registerBlockHandler((int)renderModelGunb2, (ISimpleBlockRenderingHandler)new BlockHandlerGunb2());
        RenderingRegistry.registerBlockHandler((int)renderModelKab, (ISimpleBlockRenderingHandler)new BlockHandlerKab());
        RenderingRegistry.registerBlockHandler((int)renderModelKan, (ISimpleBlockRenderingHandler)new BlockHandlerKan());
        RenderingRegistry.registerBlockHandler((int)renderModelLav2, (ISimpleBlockRenderingHandler)new BlockHandlerLav2());
        RenderingRegistry.registerBlockHandler((int)renderModelPhone, (ISimpleBlockRenderingHandler)new BlockHandlerPhone());
        RenderingRegistry.registerBlockHandler((int)renderModelPlita, (ISimpleBlockRenderingHandler)new BlockHandlerPlita());
        RenderingRegistry.registerBlockHandler((int)renderModelRak, (ISimpleBlockRenderingHandler)new BlockHandlerRak());
        RenderingRegistry.registerBlockHandler((int)renderModelShkaf0, (ISimpleBlockRenderingHandler)new BlockHandlerShkaf0());
        RenderingRegistry.registerBlockHandler((int)renderModelShkafm, (ISimpleBlockRenderingHandler)new BlockHandlerShkafm());
        RenderingRegistry.registerBlockHandler((int)renderModelStand, (ISimpleBlockRenderingHandler)new BlockHandlerStand());
        RenderingRegistry.registerBlockHandler((int)renderModelStelazh1, (ISimpleBlockRenderingHandler)new BlockHandlerStelazh1());
        RenderingRegistry.registerBlockHandler((int)renderModelStelazh2, (ISimpleBlockRenderingHandler)new BlockHandlerStelazh2());
        RenderingRegistry.registerBlockHandler((int)renderModelStelazh3, (ISimpleBlockRenderingHandler)new BlockHandlerStelazh3());
        RenderingRegistry.registerBlockHandler((int)renderModelStul1, (ISimpleBlockRenderingHandler)new BlockHandlerStul1());
        RenderingRegistry.registerBlockHandler((int)renderModelStul2, (ISimpleBlockRenderingHandler)new BlockHandlerStul2());
        RenderingRegistry.registerBlockHandler((int)renderModelTul1, (ISimpleBlockRenderingHandler)new BlockHandlerTul1());
        RenderingRegistry.registerBlockHandler((int)renderModelTul2, (ISimpleBlockRenderingHandler)new BlockHandlerTul2());
        RenderingRegistry.registerBlockHandler((int)renderModelUmi, (ISimpleBlockRenderingHandler)new BlockHandlerUmi());
        RenderingRegistry.registerBlockHandler((int)renderModelFp1, (ISimpleBlockRenderingHandler)new BlockHandlerFp1());
        RenderingRegistry.registerBlockHandler((int)renderModelFp2, (ISimpleBlockRenderingHandler)new BlockHandlerFp2());
        RenderingRegistry.registerBlockHandler((int)renderModelTv1, (ISimpleBlockRenderingHandler)new BlockHandlerTv1());
        RenderingRegistry.registerBlockHandler((int)renderModelPalatka, (ISimpleBlockRenderingHandler)new BlockHandlerPalatka());
        RenderingRegistry.registerBlockHandler((int)renderModelStol, (ISimpleBlockRenderingHandler)new BlockHandlerStol());
        RenderingRegistry.registerBlockHandler((int)renderModelStul, (ISimpleBlockRenderingHandler)new BlockHandlerStul());
        RenderingRegistry.registerBlockHandler((int)renderModelPolka3, (ISimpleBlockRenderingHandler)new BlockHandlerPolka3());
        RenderingRegistry.registerBlockHandler((int)renderModelPolka4, (ISimpleBlockRenderingHandler)new BlockHandlerPolka4());
        RenderingRegistry.registerBlockHandler((int)renderModelDivan, (ISimpleBlockRenderingHandler)new BlockHandlerDivan());
        RenderingRegistry.registerBlockHandler((int)renderModelKreslo, (ISimpleBlockRenderingHandler)new BlockHandlerKreslo());
        RenderingRegistry.registerBlockHandler((int)renderModelStolik, (ISimpleBlockRenderingHandler)new BlockHandlerStolik());
        RenderingRegistry.registerBlockHandler((int)renderModelTunnel, (ISimpleBlockRenderingHandler)new BlockHandlerTunnel());
        RenderingRegistry.registerBlockHandler((int)renderModelTunnel90, (ISimpleBlockRenderingHandler)new BlockHandlerTunnel90());
        RenderingRegistry.registerBlockHandler((int)renderModelTunnelZelezo90, (ISimpleBlockRenderingHandler)new BlockHandlerTunnelZelezo90());
        RenderingRegistry.registerBlockHandler((int)renderModelTunnelBeton1, (ISimpleBlockRenderingHandler)new BlockHandlerTunnelBeton1());
        RenderingRegistry.registerBlockHandler((int)renderModelTunnelZelezo1, (ISimpleBlockRenderingHandler)new BlockHandlerTunnelZelezo1());
        RenderingRegistry.registerBlockHandler((int)renderModelTunnelBetonDouble1, (ISimpleBlockRenderingHandler)new BlockHandlerTunnelBetonDouble1());
        RenderingRegistry.registerBlockHandler((int)renderModelTunnelBetonX, (ISimpleBlockRenderingHandler)new BlockHandlerTunnelBetonX());
        RenderingRegistry.registerBlockHandler((int)renderModelTunnelZelezoDouble, (ISimpleBlockRenderingHandler)new BlockHandlerTunnelZelezoDouble());
        RenderingRegistry.registerBlockHandler((int)renderModelTunnelZelezoExit, (ISimpleBlockRenderingHandler)new BlockHandlerTunnelZelezoExit());
        RenderingRegistry.registerBlockHandler((int)renderModelTunnelBetonExit, (ISimpleBlockRenderingHandler)new BlockHandlerTunnelBetonExit());
        RenderingRegistry.registerBlockHandler((int)renderModelTunnelBetonProvoda, (ISimpleBlockRenderingHandler)new BlockHandlerTunnelBetonProvoda());
        RenderingRegistry.registerBlockHandler((int)renderModelTunnelZelezoProvoda, (ISimpleBlockRenderingHandler)new BlockHandlerTunnelZelezoProvoda());
        RenderingRegistry.registerBlockHandler((int)renderModelTunnelBetonDerevo, (ISimpleBlockRenderingHandler)new BlockHandlerTunnelBetonDerevo());
        RenderingRegistry.registerBlockHandler((int)renderModelTunnelZelezoDerevo, (ISimpleBlockRenderingHandler)new BlockHandlerTunnelZelezoDerevo());
        RenderingRegistry.registerBlockHandler((int)renderModelTunnelBetonDerevo1, (ISimpleBlockRenderingHandler)new BlockHandlerTunnelBetonDerevo1());
        RenderingRegistry.registerBlockHandler((int)renderModelTunnelZelezoDerevo1, (ISimpleBlockRenderingHandler)new BlockHandlerTunnelZelezoDerevo1());
        RenderingRegistry.registerBlockHandler((int)renderModelTunnelBetonTrub, (ISimpleBlockRenderingHandler)new BlockHandlerTunnelBetonTrub());
        RenderingRegistry.registerBlockHandler((int)renderModelTunnelZelezoTrub, (ISimpleBlockRenderingHandler)new BlockHandlerTunnelZelezoTrub());
        RenderingRegistry.registerBlockHandler((int)renderModelTunnelNew, (ISimpleBlockRenderingHandler)new BlockHandlerTunnelNew());
        RenderingRegistry.registerBlockHandler((int)renderModelDeath1, (ISimpleBlockRenderingHandler)new BlockHandlerDeath1());
        Block48 = new Block48(2074, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block48");
        Block49 = new Block49(2075, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block49");
        Block50 = new Block50(2004, aif.d).a(apa.g).c(2.5f).a(MetroTab).c("Block50");
        EmptyStand2 = new EmptyStand2(1985, aif.e).a(apa.j).r().c("EmptyStand2");
        EmptyStand = new EmptyStand(1986, aif.e).a(apa.j).r().c("EmptyStand");
        EmptyPhone = new EmptyPhone(1994, aif.e).a(apa.j).r().c("EmptyPhone");
        Empty50 = new Empty50(1995, aif.e).a(apa.j).r().c("Empty50");
        Empty25 = new Empty25(1996, aif.e).a(apa.j).r().c("Empty25");
        Empty = new Empty(1997, aif.e).a(apa.j).r().c("Empty");
        EmptyShkaf2 = new EmptyShkaf2(1998, aif.e).a(apa.j).r().c("EmptyShkaf2");
        EmptyShkaf1 = new EmptyShkaf1(1999, aif.e).a(apa.j).r().c("EmptyShkaf1");
        Block47 = new Block47(2032, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block47");
        Block46 = new Block46(2031, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block46");
        Block1 = new Block1(2030, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block1");
        Block2 = new Block2(2033, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block2");
        Block3 = new Block3(2034, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block3");
        Block4 = new Block4(2035, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block4");
        Block5 = new Block5(2036, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block5");
        Block6 = new Block6(2037, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block6");
        Block7 = new Block7(2038, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block7");
        Block8 = new Block8(2039, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block8");
        Block9 = new Block9(2040, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block9");
        Block10 = new Block10(2041, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block10");
        Block11 = new Block11(2042, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block11");
        Block12 = new Block12(2043, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block12");
        Block13 = new Block13(2044, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block13");
        Block14 = new Block14(2045, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block14");
        Block15 = new Block15(2046, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block15");
        Block16 = new Block16(2047, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block16");
        Block17 = new Block17(2048, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block17");
        Block18 = new Block18(2049, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block18");
        Block19 = new Block19(2050, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block19");
        Block20 = new Block20(2051, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block20");
        Block21 = new Block21(2052, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block21");
        Block22 = new Block22(2053, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block22");
        Block23 = new Block23(2054, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block23");
        Block24 = new Block24(2055, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block24");
        Block25 = new Block25(2056, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block25");
        Block26 = new Block26(2057, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block26");
        Block27 = new Block27(2058, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block27");
        Block28 = new Block28(2059, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block28");
        Block29 = new Block29(2060, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block29");
        Block30 = new Block30(2061, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block30");
        Block31 = new Block31(2062, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block31");
        Block32 = new Block32(2063, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block32");
        Block33 = new Block33(2064, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block33");
        Block34 = new Block34(2065, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block34");
        Block35 = new Block35(2066, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block35");
        Block36 = new Block36(2067, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block36");
        Block37 = new Block37(2068, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block37");
        Block38 = new Block38(2069, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block38");
        Block39 = new Block39(2070, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block39");
        Block40 = new Block40(2071, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block40");
        Block41 = new Block41(2072, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block41");
        Block42 = new Block42(2073, aif.e).a(apa.j).c(2.5f).a(MetroTab).c("Block42");
        Block43 = new Block43(472, aif.e).a(apa.j).c("Block43").r();
        Block44 = new Block44(469, aif.e).a(apa.j).c("Block44").r();
        Block45 = new Block45(466, aif.e).a(apa.j).c("Block45").r();
        GameRegistry.registerBlock((apa)Block48);
        LanguageRegistry.addName((Object)Block48, (String)"\u0421\u043e\u0442\u044b");
        GameRegistry.registerBlock((apa)Block49);
        LanguageRegistry.addName((Object)Block49, (String)"\u0423\u043b\u0435\u0439");
        GameRegistry.registerBlock((apa)Block50);
        LanguageRegistry.addName((Object)Block50, (String)"\u0414\u0435\u0440\u0435\u0432\u043e");
        GameRegistry.registerBlock((apa)Block46);
        LanguageRegistry.addName((Object)Block46, (String)"\u0421\u0442\u0435\u043d\u0430 \u0438\u0437 \u043c\u0443\u0441\u043e\u0440\u0430 2");
        GameRegistry.registerBlock((apa)Block47);
        LanguageRegistry.addName((Object)Block47, (String)"\u041f\u043e\u043b \u043c\u0440\u0430\u043c\u043e\u0440 2");
        GameRegistry.registerBlock((apa)Block1);
        LanguageRegistry.addName((Object)Block1, (String)"\u0417\u0435\u043b\u0451\u043d\u0430\u044f \u0441\u0442\u0435\u043d\u0430");
        GameRegistry.registerBlock((apa)Block2);
        LanguageRegistry.addName((Object)Block2, (String)"\u041a\u0440\u0430\u0441\u043d\u0430\u044f \u0441\u0442\u0435\u043d\u0430");
        GameRegistry.registerBlock((apa)Block3);
        LanguageRegistry.addName((Object)Block3, (String)"\u041f\u043e\u0442\u043e\u043b\u043e\u043a 1");
        GameRegistry.registerBlock((apa)Block4);
        LanguageRegistry.addName((Object)Block4, (String)"\u0427\u0451\u0440\u043d\u0430\u044f \u0441\u0442\u0435\u043d\u0430");
        GameRegistry.registerBlock((apa)Block5);
        LanguageRegistry.addName((Object)Block5, (String)"\u0411\u0435\u0442\u043e\u043d\u043d\u0430\u044f \u0441\u0442\u0435\u043d\u0430");
        GameRegistry.registerBlock((apa)Block6);
        LanguageRegistry.addName((Object)Block6, (String)"\u0411\u0435\u0440\u044e\u0437\u043e\u0432\u0430\u044f \u0441\u0442\u0435\u043d\u0430");
        GameRegistry.registerBlock((apa)Block7);
        LanguageRegistry.addName((Object)Block7, (String)"\u041a\u0440\u0430\u0441\u043d\u0430\u044f(\u041e\u0431\u043b\u0435\u0441\u0448\u0430\u044f) \u0441\u0442\u0435\u043d\u0430");
        GameRegistry.registerBlock((apa)Block8);
        LanguageRegistry.addName((Object)Block8, (String)"\u0414\u0435\u0440\u0435\u0432\u043e");
        GameRegistry.registerBlock((apa)Block9);
        LanguageRegistry.addName((Object)Block9, (String)"\u0421\u0438\u043d\u044f\u044f \u043f\u043b\u0438\u0442\u043a\u0430");
        GameRegistry.registerBlock((apa)Block10);
        LanguageRegistry.addName((Object)Block10, (String)"\u041f\u043e\u043b(\u041c\u0440\u0430\u043c\u043e\u0440)");
        GameRegistry.registerBlock((apa)Block11);
        LanguageRegistry.addName((Object)Block11, (String)"\u0411\u0435\u0442\u043e\u043d\u043d\u044b\u0439 \u043f\u043e\u043b");
        GameRegistry.registerBlock((apa)Block12);
        LanguageRegistry.addName((Object)Block12, (String)"\u041f\u043e\u043b(\u041c\u0443\u0441\u043e\u0440)");
        GameRegistry.registerBlock((apa)Block13);
        LanguageRegistry.addName((Object)Block13, (String)"\u041f\u043e\u043b(\u041c\u0443\u0441\u043e\u0440)2");
        GameRegistry.registerBlock((apa)Block14);
        LanguageRegistry.addName((Object)Block14, (String)"\u0416\u0451\u043b\u0442\u0430\u044f \u0441\u0442\u0435\u043d\u0430");
        GameRegistry.registerBlock((apa)Block15);
        LanguageRegistry.addName((Object)Block15, (String)"\u041f\u043e\u043b(\u0413\u0440\u044f\u0437\u044c)3");
        GameRegistry.registerBlock((apa)Block16);
        LanguageRegistry.addName((Object)Block16, (String)"\u041f\u043b\u0438\u0442\u043a\u0430 \u0412\u0414\u041d\u0425(\u043f\u043e\u043b)");
        GameRegistry.registerBlock((apa)Block17);
        LanguageRegistry.addName((Object)Block17, (String)"\u041f\u043e\u0442\u043e\u043b\u043e\u043a 2");
        GameRegistry.registerBlock((apa)Block18);
        LanguageRegistry.addName((Object)Block18, (String)"\u041f\u043e\u0442\u043e\u043b\u043e\u043a 3");
        GameRegistry.registerBlock((apa)Block19);
        LanguageRegistry.addName((Object)Block19, (String)"\u041f\u043e\u043b(\u0413\u0440\u044f\u0437\u044c)4");
        GameRegistry.registerBlock((apa)Block20);
        LanguageRegistry.addName((Object)Block20, (String)"\u041f\u043e\u0442\u043e\u043b\u043e\u043a 4");
        GameRegistry.registerBlock((apa)Block21);
        LanguageRegistry.addName((Object)Block21, (String)"\u0427\u0451\u0440\u043d\u0430\u044f \u043f\u043b\u0438\u0442\u043a\u0430");
        GameRegistry.registerBlock((apa)Block22);
        LanguageRegistry.addName((Object)Block22, (String)"\u0411\u0435\u043b\u0430\u044f \u043f\u043b\u0438\u0442\u043a\u0430");
        GameRegistry.registerBlock((apa)Block23);
        LanguageRegistry.addName((Object)Block23, (String)"\u0411\u0435\u0442\u043e\u043d\u043d\u0430\u044f \u0441\u0442\u0435\u043d\u0430 2");
        GameRegistry.registerBlock((apa)Block24);
        LanguageRegistry.addName((Object)Block24, (String)"\u0413\u043e\u043b\u0443\u0431\u0430\u044f \u0441\u0442\u0435\u043d\u0430");
        GameRegistry.registerBlock((apa)Block25);
        LanguageRegistry.addName((Object)Block25, (String)"\u0411\u0435\u0442\u043e\u043d\u043d\u0430\u044f \u0441\u0442\u0435\u043d\u0430 3");
        GameRegistry.registerBlock((apa)Block26);
        LanguageRegistry.addName((Object)Block26, (String)"\u0413\u043e\u043b\u0443\u0431\u0430\u044f \u0441\u0442\u0435\u043d\u0430 2");
        GameRegistry.registerBlock((apa)Block27);
        LanguageRegistry.addName((Object)Block27, (String)"\u0417\u0435\u043b\u0451\u043d\u0430\u044f \u0441\u0442\u0435\u043d\u0430 2");
        GameRegistry.registerBlock((apa)Block28);
        LanguageRegistry.addName((Object)Block28, (String)"\u0416\u0451\u043b\u0442\u0430\u044f \u043f\u043b\u0438\u0442\u043a\u0430");
        GameRegistry.registerBlock((apa)Block29);
        LanguageRegistry.addName((Object)Block29, (String)"\u0427\u0451\u0440\u043d\u0430\u044f \u0431\u0435\u0442\u043e\u043d\u043d\u0430\u044f \u0441\u0442\u0435\u043d\u0430");
        GameRegistry.registerBlock((apa)Block30);
        LanguageRegistry.addName((Object)Block30, (String)"\u0411\u0435\u0442\u043e\u043d\u043d\u0430\u044f \u0441\u0442\u0435\u043d\u0430 4");
        GameRegistry.registerBlock((apa)Block31);
        LanguageRegistry.addName((Object)Block31, (String)"\u0411\u0435\u0442\u043e\u043d\u043d\u0430\u044f \u0441\u0442\u0435\u043d\u0430 5");
        GameRegistry.registerBlock((apa)Block32);
        LanguageRegistry.addName((Object)Block32, (String)"\u0411\u0435\u0442\u043e\u043d\u043d\u0430\u044f \u0441\u0442\u0435\u043d\u0430 6");
        GameRegistry.registerBlock((apa)Block33);
        LanguageRegistry.addName((Object)Block33, (String)"\u0416\u0451\u043b\u0442\u0430\u044f \u0441\u0442\u0435\u043d\u0430 2");
        GameRegistry.registerBlock((apa)Block34);
        LanguageRegistry.addName((Object)Block34, (String)"\u041a\u0438\u0440\u043f\u0438\u0447\u043d\u0430\u044f \u0441\u0442\u0435\u043d\u0430");
        GameRegistry.registerBlock((apa)Block35);
        LanguageRegistry.addName((Object)Block35, (String)"\u041a\u0438\u0440\u043f\u0438\u0447\u043d\u0430\u044f \u0441\u0442\u0435\u043d\u0430 2");
        GameRegistry.registerBlock((apa)Block36);
        LanguageRegistry.addName((Object)Block36, (String)"\u0416\u0451\u043b\u0442\u0430\u044f \u043a\u0438\u0440\u043f\u0438\u0447\u043d\u0430\u044f \u0441\u0442\u0435\u043d\u0430");
        GameRegistry.registerBlock((apa)Block37);
        LanguageRegistry.addName((Object)Block37, (String)"\u041a\u0438\u0440\u043f\u0438\u0447\u043d\u0430\u044f \u0441\u0442\u0435\u043d\u0430 3");
        GameRegistry.registerBlock((apa)Block38);
        LanguageRegistry.addName((Object)Block38, (String)"\u0421\u0442\u0435\u043d\u0430 \u0438\u0437 \u043c\u0443\u0441\u043e\u0440\u0430");
        GameRegistry.registerBlock((apa)Block39);
        LanguageRegistry.addName((Object)Block39, (String)"\u041a\u0438\u0440\u043f\u0438\u0447\u0438");
        GameRegistry.registerBlock((apa)Block40);
        LanguageRegistry.addName((Object)Block40, (String)"\u041a\u0438\u0440\u043f\u0438\u0447\u0438 2");
        GameRegistry.registerBlock((apa)Block41);
        LanguageRegistry.addName((Object)Block41, (String)"\u041a\u0438\u0440\u043f\u0438\u0447\u0438 3");
        GameRegistry.registerBlock((apa)Block42);
        LanguageRegistry.addName((Object)Block42, (String)"\u041a\u0438\u0440\u043f\u0438\u0447\u0438 4");
        GameRegistry.registerBlock((apa)Block43, ItemGeneral.class, (String)(modid + Block43.a().substring(5)));
        LanguageRegistry.addName((Object)new wm(Block43, 1, 0), (String)"Block43");
        LanguageRegistry.addName((Object)new wm(Block43, 1, 1), (String)"Block43:1");
        LanguageRegistry.addName((Object)new wm(Block43, 1, 2), (String)"Block43:2");
        LanguageRegistry.addName((Object)new wm(Block43, 1, 3), (String)"Block43:3");
        GameRegistry.registerBlock((apa)Block44, ItemGeneral.class, (String)(modid + Block44.a().substring(5)));
        LanguageRegistry.addName((Object)new wm(Block44, 1, 0), (String)"Block44");
        LanguageRegistry.addName((Object)new wm(Block44, 1, 1), (String)"Block44:1");
        LanguageRegistry.addName((Object)new wm(Block44, 1, 2), (String)"Block44:2");
        LanguageRegistry.addName((Object)new wm(Block44, 1, 3), (String)"Block44:3");
        LanguageRegistry.addName((Object)new wm(Block44, 1, 4), (String)"Block44:4");
        GameRegistry.registerBlock((apa)Block45, ItemGeneral.class, (String)(modid + Block45.a().substring(5)));
        LanguageRegistry.addName((Object)new wm(Block45, 1, 0), (String)"Block45");
        LanguageRegistry.addName((Object)new wm(Block45, 1, 1), (String)"Block45:1");
        GameRegistry.registerBlock((apa)Empty);
        GameRegistry.registerBlock((apa)Empty25);
        LanguageRegistry.addName((Object)Patron, (String)"\u0410\u0440\u043c\u0435\u0439\u0441\u043a\u0438\u0439 \u041f\u0430\u0442\u0440\u043e\u043d");
        LanguageRegistry.addName((Object)GoldMag, (String)"\u041c\u0430\u0433\u0430\u0437\u0438\u043d \u0441 30-\u0442\u044c\u044e \u0430\u0440\u043c\u0435\u0439\u0441\u043a\u0438\u043c\u0438 \u043f\u0430\u0442\u0440\u043e\u043d\u0430\u043c\u0438");
        LanguageRegistry.addName((Object)Zheton, (String)"\u0416\u0435\u0442\u043e\u043d");
        LanguageRegistry.addName((Object)Item1, (String)"\u0427\u0435\u043c\u043e\u0434\u0430\u043d");
        LanguageRegistry.addName((Object)Item2, (String)"\u0422\u0435\u043b\u0435\u0444\u043e\u043d");
        LanguageRegistry.addName((Object)Item3, (String)"\u0418\u043d\u0441\u0442\u0440\u0443\u043c\u0435\u043d\u0442\u044b");
        LanguageRegistry.addName((Object)Item4, (String)"\u041a\u0430\u043d\u0438\u0441\u0442\u0440\u0430");
        LanguageRegistry.addName((Object)Item5, (String)"\u0412\u0435\u043b\u043e\u0441\u0438\u043f\u0435\u0434");
        LanguageRegistry.addName((Object)Item6, (String)"\u041a\u043d\u0438\u0433\u0438");
        LanguageRegistry.addName((Object)Item7, (String)"\u0418\u0433\u0440\u0443\u0448\u043a\u0430");
        LanguageRegistry.addName((Object)Item8, (String)"\u041c\u043e\u043d\u0438\u0442\u043e\u0440");
        LanguageRegistry.addName((Object)Item9, (String)"\u0427\u0430\u0441\u044b");
        LanguageRegistry.addName((Object)Item10, (String)"\u041a\u043b\u0430\u0432\u0438\u0430\u0442\u0443\u0440\u0430");
        LanguageRegistry.addName((Object)Ubludok, (String)"\u0423\u0431\u043b\u044e\u0434\u043e\u043a");
        LanguageRegistry.addName((Object)Tihar, (String)"\u0422\u0438\u0445\u0430\u0440\u044c");
        LanguageRegistry.addName((Object)AK, (String)"\u041a\u0430\u043b\u0430\u0448");
        LanguageRegistry.addName((Object)AK2012, (String)"\u041a\u0430\u043b\u0430\u0448 2012");
        LanguageRegistry.addName((Object)Ashot, (String)"\u0410\u0448\u043e\u0442");
        LanguageRegistry.addName((Object)AKSU, (String)"\u0410\u041a\u0421-\u0423");
        LanguageRegistry.addName((Object)Gatling, (String)"\u0420\u0443\u0447\u043d\u043e\u0439 \u043f\u0443\u043b\u0435\u043c\u0451\u0442");
        LanguageRegistry.addName((Object)Duplet, (String)"\u0414\u0443\u043f\u043b\u0435\u0442");
        LanguageRegistry.addName((Object)Podonok, (String)"\u041f\u043e\u0434\u043e\u043d\u043e\u043a");
        LanguageRegistry.addName((Object)Preved, (String)"\u041f\u0440\u0435\u0432\u0435\u0434");
        LanguageRegistry.addName((Object)Revolver, (String)"\u0420\u0435\u0432\u043e\u043b\u044c\u0432\u0435\u0440");
        LanguageRegistry.addName((Object)Rpk, (String)"\u0420\u041f\u041a");
        LanguageRegistry.addName((Object)Saiga, (String)"\u0421\u0430\u0439\u0433\u0430");
        LanguageRegistry.addName((Object)Abzac, (String)"\u0410\u0431\u0437\u0430\u0446");
        LanguageRegistry.addName((Object)Uboinik, (String)"\u0423\u0431\u043e\u0439\u043d\u0438\u043a");
        LanguageRegistry.addName((Object)Ventil, (String)"\u0412\u0435\u043d\u0442\u0438\u043b\u044c");
        LanguageRegistry.addName((Object)Vsv, (String)"\u0412\u0421\u0412");
        LanguageRegistry.addName((Object)Vyhlop, (String)"\u0425\u043b\u043e\u043f\u0443\u0448\u043a\u0430");
        LanguageRegistry.addName((Object)Velopal, (String)"\u0412\u0435\u043b\u043e\u043f\u0430\u043b");
        LanguageRegistry.addName((Object)Ognemet, (String)"\u041e\u0433\u043d\u0435\u043c\u0451\u0442");
        LanguageRegistry.addName((Object)Helsing, (String)"\u0425\u0435\u043b\u044c\u0441\u0438\u043d\u0433");
        LanguageRegistry.addName((Object)Railgun, (String)"\u0420\u0435\u043b\u044c\u0441\u0430");
        LanguageRegistry.addName((Object)Machete, (String)"\u041c\u0430\u0447\u0435\u0442\u0430");
        LanguageRegistry.addName((Object)Optica, (String)"\u041e\u043f\u0442\u0438\u043a\u0430");
        LanguageRegistry.addName((Object)AshotButt, (String)"\u041f\u0440\u0438\u043a\u043b\u0430\u0434 \u0434\u043b\u044f \u0410\u0448\u043e\u0442\u0430");
        LanguageRegistry.addName((Object)Glushak, (String)"\u0413\u043b\u0443\u0448\u0438\u0442\u0435\u043b\u044c");
        LanguageRegistry.addName((Object)Butt, (String)"\u041f\u0440\u0438\u043a\u043b\u0430\u0434");
        LanguageRegistry.addName((Object)Barrel, (String)"\u0421\u0442\u0432\u043e\u043b");
        LanguageRegistry.addName((Object)UboinikBarrel, (String)"\u0421\u0442\u0432\u043e\u043b \u0434\u043b\u044f \u0443\u0431\u043e\u0439\u043d\u0438\u043a\u0430");
        LanguageRegistry.addName((Object)Laser, (String)"\u041b\u0430\u0437\u0435\u0440");
        LanguageRegistry.addName((Object)OpticaSmall, (String)"\u041e\u043f\u0442\u0438\u043a\u0430 IRNV");
        LanguageRegistry.addName((Object)Kolimator, (String)"\u041a\u043e\u043b\u043b\u0438\u043c\u0430\u0442\u043e\u0440\u043d\u044b\u0439 \u043f\u0440\u0438\u0446\u0435\u043b");
        LanguageRegistry.addName((Object)AutoFire, (String)"\u041c\u043e\u0434\u0443\u043b\u044c \u0434\u043b\u044f \u0430\u0431\u0437\u0430\u0446\u0430 \"Autofire\"");
        LanguageRegistry.addName((Object)AbzacGlushak, (String)"\u0413\u043b\u0443\u0448\u0438\u0442\u0435\u043b\u044c \u0434\u043b\u044f \u0430\u0431\u0437\u0430\u0446\u0430");
        LanguageRegistry.addName((Object)VentilGlushak, (String)"\u041f\u043b\u0430\u043c\u0435\u0433\u0430\u0441\u0438\u0442\u0435\u043b\u044c");
        LanguageRegistry.addName((Object)VsvGlushak, (String)"\u0413\u043b\u0443\u0448\u0438\u0442\u0435\u043b\u044c \u0434\u043b\u044f \u0412\u0421\u0412");
        LanguageRegistry.addName((Object)DupletBarrel, (String)"\u0421\u0442\u0432\u043e\u043b \u0434\u043b\u044f \u0414\u0443\u043f\u043b\u0435\u0442\u0430");
        LanguageRegistry.addName((Object)DupletButt, (String)"\u0420\u0443\u043a\u043e\u044f\u0442\u044c \u0434\u043b\u044f \u0414\u0443\u043f\u043b\u0435\u0442\u0430");
        LanguageRegistry.addName((Object)DupletPriklad, (String)"\u041f\u0440\u0438\u043a\u043b\u0430\u0434 \u0434\u043b\u044f \u0414\u0443\u043f\u043b\u0435\u0442\u0430");
        LanguageRegistry.addName((Object)QuadBarrel, (String)"2 \u0434\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0445 \u0441\u0442\u0432\u043e\u043b\u0430 \u0434\u043b\u044f \u0414\u0443\u043f\u043b\u0435\u0442\u0430");
        LanguageRegistry.addName((Object)DupletGlushak, (String)"\u0413\u043b\u0443\u0448\u0438\u0442\u0435\u043b\u044c \u0434\u043b\u044f \u0414\u0443\u043f\u043b\u0435\u0442\u0430");
        LanguageRegistry.addName((Object)GatlingBarrel, (String)"5 \u0441\u0442\u0432\u043e\u043b\u043e\u0432 \u0434\u043b\u044f \u0420\u0443\u0447\u043d\u043e\u0433\u043e \u043f\u0443\u043b\u0435\u043c\u0451\u0442\u0430");
        LanguageRegistry.addName((Object)GatlingMotor, (String)"\u041c\u043e\u0442\u043e\u0440 \u0434\u043b\u044f \u0420\u0443\u0447\u043d\u043e\u0433\u043e \u043f\u0443\u043b\u0435\u043c\u0451\u0442\u0430");
        LanguageRegistry.addName((Object)GatlingGiro, (String)"\u0421\u0442\u0430\u0431\u0438\u043b\u0438\u0437\u0430\u0442\u043e\u0440 \u0434\u043b\u044f \u0420\u0443\u0447\u043d\u043e\u0433\u043e \u043f\u0443\u043b\u0435\u043c\u0451\u0442\u0430");
        LanguageRegistry.addName((Object)GatlingButt, (String)"\u041f\u0440\u0438\u043a\u043b\u0430\u0434 \u0434\u043b\u044f \u0420\u0443\u0447\u043d\u043e\u0433\u043e \u043f\u0443\u043b\u0435\u043c\u0451\u0442\u0430");
        LanguageRegistry.addName((Object)TiharOptica, (String)"\u041e\u043f\u0442\u0438\u043a\u0430 \u0434\u043b\u044f \u0422\u0438\u0445\u0430\u0440\u044f");
        LanguageRegistry.addName((Object)PodonokButt, (String)"\u041f\u0440\u0438\u043a\u043b\u0430\u0434 \u0434\u043b\u044f \u041f\u043e\u0434\u043e\u043d\u043a\u0430");
        LanguageRegistry.addName((Object)PodonokGas, (String)"\u0413\u0430\u0437\u043e\u043e\u0442\u0432\u043e\u0434\u043d\u044b\u0439 \u043c\u0435\u0445\u0430\u043d\u0438\u0437\u043c \u0434\u043b\u044f \u041f\u043e\u0434\u043e\u043d\u043a\u0430");
        LanguageRegistry.addName((Object)RailgunKon, (String)"\u041a\u043e\u043d\u0434\u0435\u043d\u0441\u0430\u0442\u043e\u0440 \u043f\u043e\u0432\u044b\u0448\u0435\u043d\u043d\u043e\u0439 \u043c\u043e\u0449\u043d\u043e\u0441\u0442\u0438 \u0434\u043b\u044f \u0420\u0435\u043b\u044c\u0441\u044b");
        LanguageRegistry.addName((Object)RailgunAkk, (String)"\u0420\u0430\u0441\u0448\u0438\u0440\u0435\u043d\u043d\u044b\u0439 \u0430\u043a\u043a\u0430\u043c\u0443\u043b\u044f\u0442\u043e\u0440 \u0434\u043b\u044f \u0420\u0435\u043b\u044c\u0441\u044b");
        LanguageRegistry.addName((Object)UbludokButt, (String)"\u041f\u0440\u0438\u043a\u043b\u0430\u0434 \u0434\u043b\u044f \u0423\u0431\u043b\u044e\u0434\u043a\u0430");
        LanguageRegistry.addName((Object)Med, (String)"\u0410\u043f\u0442\u0435\u0447\u043a\u0430");
        LanguageRegistry.addName((Object)Power, (String)"\u0417\u0430\u0440\u044f\u0434\u043a\u0430 \u0434\u043b\u044f \u0444\u043e\u043d\u0430\u0440\u044f");
        LanguageRegistry.addName((Object)Lamp, (String)"\u0424\u043e\u043d\u0430\u0440\u044c");
        LanguageRegistry.addName((Object)Flyashka, (String)"\u0424\u043b\u044f\u0433\u0430");
        LanguageRegistry.addName((Object)BackPackSmall, (String)"\u041c\u0430\u043b\u0435\u043d\u044c\u043a\u0438\u0439 \u0440\u044e\u043a\u0437\u0430\u043a");
        LanguageRegistry.addName((Object)BackPackMedium, (String)"\u0421\u0440\u0435\u0434\u043d\u0438\u0439 \u0440\u044e\u043a\u0437\u0430\u043a");
        LanguageRegistry.addName((Object)BackPackLarge, (String)"\u0411\u043e\u043b\u044c\u0448\u043e\u0439 \u0440\u044e\u043a\u0437\u0430\u043a");
        LanguageRegistry.addName((Object)BulletBolt, (String)"\u0410\u0440\u0431\u0430\u043b\u0435\u0442\u043d\u044b\u0439 \u0431\u043e\u043b\u0442");
        LanguageRegistry.addName((Object)Bullet5x45, (String)"\u041f\u0430\u0442\u0440\u043e\u043d 5,45x39");
        LanguageRegistry.addName((Object)Bullet12x70, (String)"\u041f\u0430\u0442\u0440\u043e\u043d 12/70");
        LanguageRegistry.addName((Object)Bullet12x7, (String)"\u041f\u0430\u0442\u0440\u043e\u043d 12,7");
        LanguageRegistry.addName((Object)BulletMagnum, (String)"\u041f\u0430\u0442\u0440\u043e\u043d .44 Magnum");
        LanguageRegistry.addName((Object)Bullet12_7x108, (String)"\u041f\u0430\u0442\u0440\u043e\u043d 12,7x108");
        LanguageRegistry.addName((Object)Bullet7_62x54, (String)"\u041f\u0430\u0442\u0440\u043e\u043d 7,62x54");
        LanguageRegistry.addName((Object)Bullet12_7x55, (String)"\u041f\u0430\u0442\u0440\u043e\u043d 12,7x55");
        LanguageRegistry.addName((Object)Bullet15mm, (String)"\u0421\u0442\u0430\u043b\u044c\u043d\u044b\u0435 \u0448\u0430\u0440\u0438\u043a\u0438 15\u043c\u043c");
        LanguageRegistry.addName((Object)Grenade, (String)"\u0413\u0440\u0430\u043d\u0430\u0442\u0430");
        LanguageRegistry.addName((Object)GrenadeBig, (String)"\u0423\u0441\u0438\u043b\u0435\u043d\u043d\u0430\u044f \u0433\u0440\u0430\u043d\u0430\u0442\u0430");
        LanguageRegistry.addName((Object)Easy, (String)"\u041b\u0451\u0433\u043a\u0430\u044f \u0431\u0440\u043e\u043d\u044f");
        LanguageRegistry.addName((Object)Kozh, (String)"\u041a\u043e\u0436\u0430\u043d\u043d\u0430\u044f \u0431\u0440\u043e\u043d\u044f");
        LanguageRegistry.addName((Object)Normal, (String)"\u0422\u044f\u0436\u0451\u043b\u0430\u044f \u0431\u0440\u043e\u043d\u044f");
        LanguageRegistry.addName((Object)Heavy, (String)"\u0422\u044f\u0436\u0451\u043b\u0430\u044f \u0431\u0440\u043e\u043d\u044f");
        LanguageRegistry.addName((Object)Heavy1, (String)"\u0422\u044f\u0436\u0451\u043b\u0430\u044f \u0431\u0440\u043e\u043d\u044f");
        LanguageRegistry.addName((Object)Himza, (String)"\u0425\u0438\u043c. \u0437\u0430\u0449\u0438\u0442\u0430");
        LanguageRegistry.addName((Object)Sparta, (String)"\u0411\u0440\u043e\u043d\u044f \u0441\u043f\u0430\u0440\u0442\u044b");
        LanguageRegistry.addName((Object)Clean, (String)"\u0427\u0438\u0441\u0442\u044b\u0439 \u0432\u0435\u0442\u0435\u0440");
        LanguageRegistry.addName((Object)CClean, (String)"\u0427\u0438\u0441\u0442\u044b\u0439 \u0432\u0435\u0442\u0435\u0440 +");
        LanguageRegistry.addName((Object)Red, (String)"\u0411\u0440\u043e\u043d\u044f \u041a\u0440\u0430\u0441\u043d\u043e\u0439 \u043b\u0438\u043d\u0438\u0438");
        LanguageRegistry.addName((Object)Nazi, (String)"\u0411\u0440\u043e\u043d\u044f 4-\u043e\u0433\u043e \u0420\u0435\u0439\u0445\u0430");
        LanguageRegistry.addName((Object)gas_mask, (String)"\u041f\u041f\u041c-88");
        LanguageRegistry.addName((Object)HeavyHat, (String)"\u0417\u0430\u0449\u0438\u0442\u043d\u044b\u0439 \u043f\u0440\u043e\u0442\u0438\u0432\u043e\u0433\u0430\u0437");
        LanguageRegistry.addName((Object)NaziHat, (String)"\u0417\u0430\u0449\u0438\u0442\u043d\u044b\u0439 \u043f\u0440\u043e\u0442\u0438\u0432\u043e\u0433\u0430\u0437 4-\u043e\u0433\u043e \u0420\u0435\u0439\u0445\u0430");
        LanguageRegistry.addName((Object)Heavy1Hat, (String)"\u0417\u0430\u0449\u0438\u0442\u043d\u044b\u0439 \u0448\u043b\u0435\u043c");
        LanguageRegistry.addName((Object)filter, (String)"\u0424\u0438\u043b\u044c\u0442\u0440 \u0434\u043b \u041f\u041f\u041c-88");
        LanguageRegistry.addName((Object)HelsingAmmo, (String)"\u041c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043b\u044f \u0425\u0435\u043b\u044c\u0441\u0438\u043d\u0433\u0430");
        LanguageRegistry.addName((Object)TiharAmmo, (String)"\u041c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043b\u044f \u0422\u0438\u0445\u0430\u0440\u044f,\u0420\u0435\u043b\u044c\u0441\u044b");
        LanguageRegistry.addName((Object)GatlingAmmo, (String)"\u041c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043b\u044f \u0420\u0443\u0447\u043d\u043e\u0433\u043e \u043f\u0443\u043b\u0435\u043c\u0451\u0442\u0430");
        LanguageRegistry.addName((Object)AK2012Ammo, (String)"\u041c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043b\u044f \u041a\u0430\u043b\u0430\u0448\u0430 2012");
        LanguageRegistry.addName((Object)VsvAmmo, (String)"\u041c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043b\u044f \u0412\u0421\u0412");
        LanguageRegistry.addName((Object)AKAmmo, (String)"\u041c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043b\u044f \u041a\u0430\u043b\u0430\u0448\u0430,\u0410\u041a\u0421-\u0423");
        LanguageRegistry.addName((Object)UbludokAmmo, (String)"\u041c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043b\u044f \u0423\u0431\u043b\u044e\u0434\u043a\u0430");
        LanguageRegistry.addName((Object)PodonokAmmo, (String)"\u041c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043b\u044f \u041f\u043e\u0434\u043e\u043d\u043a\u0430");
        LanguageRegistry.addName((Object)RpkAmmo, (String)"\u041e\u0431\u043e\u0439\u043c\u0430 \u0434\u043b\u044f \u0420\u041f\u041a,\u041a\u0430\u043b\u0430\u0448\u0430,\u0410\u041a\u0421-\u0423");
        LanguageRegistry.addName((Object)VyhlopAmmo, (String)"\u041c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043b\u044f \u0445\u043b\u043e\u043f\u0443\u0448\u043a\u0438");
        LanguageRegistry.addName((Object)OgnemetAmmo, (String)"\u0413\u043e\u0440\u044e\u0447\u0435\u0435 \u0434\u043b\u044f \u043e\u0433\u043d\u0435\u043c\u0451\u0442\u0430");
        LanguageRegistry.addName((Object)RpkAmmoBig, (String)"\u041e\u0431\u043e\u0439\u043c\u0430 \u0434\u043b\u044f \u0420\u041f\u041a \u0440\u0430\u0441\u0448\u0438\u0440\u0435\u043d\u043d\u0430\u044f");
        LanguageRegistry.addName((Object)SaigaAmmo, (String)"\u041c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043b\u044f \u0441\u0430\u0439\u0433\u0438");
        LanguageRegistry.addName((Object)SaigaAmmoBig, (String)"\u0420\u0430\u0441\u0448\u0438\u0440\u0435\u043d\u043d\u044b\u0439 \u043c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043b\u044f \u0441\u0430\u0439\u0433\u0438");
        LanguageRegistry.addName((Object)AbzacAmmo, (String)"\u041c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043b\u044f \u0430\u0431\u0437\u0430\u0446\u0430");
        LanguageRegistry.addName((Object)AbzacAmmoBig, (String)"\u0420\u0430\u0441\u0448\u0438\u0440\u0435\u043d\u043d\u044b\u0439 \u043c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043b\u044f \u0430\u0431\u0437\u0430\u0446\u0430");
        LanguageRegistry.addName((Object)VentilAmmo, (String)"\u041c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043b\u044f \u0432\u0435\u043d\u0442\u0438\u043b\u044f");
        LanguageRegistry.addName((Object)VentilAmmoBig, (String)"\u0420\u0430\u0441\u0448\u0438\u0440\u0435\u043d\u043d\u044b\u0439 \u043c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043b\u044f \u0432\u0435\u043d\u0442\u0438\u043b\u044f");
        LanguageRegistry.addName((Object)PodonokAmmoBig, (String)"\u0420\u0430\u0441\u0448\u0438\u0440\u0435\u043d\u043d\u044b\u0439 \u043c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043b\u044f \u041f\u043e\u0434\u043e\u043d\u043a\u0430");
        LanguageRegistry.addName((Object)PrevedAmmo, (String)"\u041c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043b\u044f \u041f\u0440\u0435\u0432\u0435\u0434\u0430");
        LanguageRegistry.addName((Object)Priklad, (String)"\u041f\u0440\u0438\u043a\u043b\u0430\u0434");
        LanguageRegistry.addName((Object)GazMeh, (String)"\u0413\u0430\u0437\u043e\u0432\u044b\u0439 \u043c\u0435\u0445\u0430\u043d\u0438\u0437\u043c");
        LanguageRegistry.addName((Object)WeaponParts, (String)"\u0414\u0435\u0442\u0430\u043b\u0438 \u043e\u0440\u0443\u0436\u0438\u044f");
        LanguageRegistry.addName((Object)Stvol, (String)"\u0421\u0442\u0432\u043e\u043b");
        LanguageRegistry.addName((Object)Cev, (String)"\u0426\u0435\u0432\u044c\u0451");
        LanguageRegistry.addName((Object)AKK, (String)"\u041a\u0430\u043b\u0430\u0448\u043d\u0438\u043a\u043e\u0432");
        LanguageRegistry.addName((Object)AKKAmmo, (String)"\u041c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043b\u044f \u041a\u0430\u043b\u0430\u0448\u043d\u0438\u043a\u043e\u0432\u0430");
        EntityRegistry.registerGlobalEntityID(Spider.class, (String)"Spider", (int)108, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(Nosach.class, (String)"Nosach", (int)110, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(Biblio.class, (String)"Biblio", (int)111, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(Kikimora.class, (String)"Kikimora", (int)112, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(Strazh.class, (String)"Strazh", (int)113, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(Dark.class, (String)"Dark", (int)114, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(Demon.class, (String)"Demon", (int)115, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(Aqua_male.class, (String)"Aqua_male", (int)116, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(Aqua_female.class, (String)"Aqua_female", (int)117, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(Rat.class, (String)"Rat", (int)118, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(Bear.class, (String)"Bear", (int)119, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(EntitySpawnerKikimora.class, (String)"SpawnerKikimora", (int)120, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(EntitySpawnerStrazh.class, (String)"SpawnerStrazh", (int)121, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(EntitySpawnerNosach.class, (String)"SpawnerNosach", (int)122, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(EntitySpawnerDark.class, (String)"SpawnerDark", (int)123, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(EntitySpawnerAquam.class, (String)"SpawnerAquam", (int)124, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(EntitySpawnerAquaf.class, (String)"SpawnerAquaf", (int)125, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(EntitySpawnerDemon.class, (String)"SpawnerDemon", (int)126, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(EntitySpawnerBear.class, (String)"SpawnerBear", (int)127, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(EntitySpawnerMobus.class, (String)"SpawnerMobus", (int)117, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(Mobus.class, (String)"Mobus", (int)118, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(EntitySpawnerSpider.class, (String)"SpawnerSpider", (int)128, (int)16, (int)64);
        EntityRegistry.registerGlobalEntityID(EntitySpawnerBiblio.class, (String)"SpawnerBiblio", (int)129, (int)16, (int)64);
        EntityRegistry.registerModEntity(EntityGrenade1.class, (String)"Grenade", (int)109, (Object)this, (int)8, (int)1, (boolean)true);
        EntityRegistry.registerModEntity(EntityGrenadeBig.class, (String)"GrenadeBig", (int)137, (Object)this, (int)8, (int)1, (boolean)true);
        EntityRegistry.registerModEntity(EntityPropulsionFX.class, (String)"PropulsionFX", (int)130, (Object)this, (int)250, (int)9, (boolean)true);
        EntityRegistry.registerModEntity(EntityPassiveFire.class, (String)"PassiveFire", (int)131, (Object)this, (int)250, (int)9, (boolean)true);
        EntityRegistry.registerModEntity(EntityFlameBall.class, (String)"FlameBall", (int)132, (Object)this, (int)250, (int)1, (boolean)true);
        EntityRegistry.registerModEntity(EntityTestDeathFull.class, (String)"Death", (int)133, (Object)this, (int)250, (int)1, (boolean)true);
        EntityRegistry.registerModEntity(Entityregina.class, (String)"Regina", (int)134, (Object)this, (int)64, (int)15, (boolean)true);
        EntityRegistry.registerModEntity(EntityDrezina.class, (String)"Drezina", (int)135, (Object)this, (int)64, (int)15, (boolean)true);
        EntityRegistry.registerModEntity(EntityMountableBlock.class, (String)"EntityMountableBlock", (int)1, (Object)this, (int)136, (int)5, (boolean)false);
        EntityRegistry.addSpawn(Rat.class, (int)1, (int)1, (int)3, (nn)nn.a, (aav[])allBiomes);
        proxy.registerRenderThings();
        CommonProxy var10000 = proxy;
        CommonProxy.ArMload((FMLInitializationEvent)event);
        new GuiHandler();
        TickRegistry.registerTickHandler((ITickHandler)new ServerTickHandler(), (Side)Side.SERVER);
        MinecraftForge.EVENT_BUS.register((Object)new AirSetup());
        MinecraftForge.EVENT_BUS.register((Object)new Thirst());
        MinecraftForge.EVENT_BUS.register((Object)new CustomEPP());
        MinecraftForge.EVENT_BUS.register((Object)new mods.provoda.until.Lamp());
        MinecraftForge.EVENT_BUS.register((Object)new EventHandler());
        GameRegistry.registerPlayerTracker((IPlayerTracker)new PlayerTracker());
    }

    @Mod.PreInit
    public void preLoad(FMLPreInitializationEvent var1) {
        CommonProxy var10000 = proxy;
        CommonProxy.ArMpreload((FMLPreInitializationEvent)var1);
        if (FMLCommonHandler.instance().getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register((Object)new Sounds());
        }
        Configuration var2 = new Configuration(var1.getSuggestedConfigurationFile());
        var2.load();
        clientType = var2.get("Client_Type", "Client_Type", 0).getInt(0);
        DrawBlockHighlight = var2.get("DrawBlockHighlight", "DrawBlockHighlight", false).getBoolean(false);
        VDNHx = var2.get("VDNH", "VDNHx", 0).getString();
        VDNHy = var2.get("VDNH", "VDNHy", 1).getString();
        VDNHz = var2.get("VDNH", "VDNHz", 2).getString();
        Polisx = var2.get("Polis", "Polisx", 0).getString();
        Polisy = var2.get("Polis", "Polisy", 1).getString();
        Polisz = var2.get("Polis", "Polisz", 2).getString();
        Krx = var2.get("Red", "Krx", 0).getString();
        Kry = var2.get("Red", "Kry", 1).getString();
        Krz = var2.get("Red", "Krz", 2).getString();
        Ganzax = var2.get("Ganza", "Ganzax", 0).getString();
        Ganzay = var2.get("Ganza", "Ganzay", 1).getString();
        Ganzaz = var2.get("Ganza", "Ganzaz", 2).getString();
        Prestx = var2.get("Prest", "Prestx", 0).getString();
        Presty = var2.get("Prest", "Presty", 1).getString();
        Prestz = var2.get("Prest", "Prestz", 2).getString();
        ArbatKonfederationx = var2.get("ArbatKonfederation", "ArbatKonfederationx", 0).getString();
        ArbatKonfederationy = var2.get("ArbatKonfederation", "ArbatKonfederationy", 1).getString();
        ArbatKonfederationz = var2.get("ArbatKonfederation", "ArbatKonfederationz", 2).getString();
        Konfederation1905yearx = var2.get("Konfederation1905year", "Konfederation1905yearx", 0).getString();
        Konfederation1905yeary = var2.get("Konfederation1905year", "Konfederation1905yeary", 1).getString();
        Konfederation1905yearz = var2.get("Konfederation1905year", "Konfederation1905yearz", 2).getString();
        Nazix = var2.get("Nazi", "Nazix", 0).getString();
        Naziy = var2.get("Nazi", "Naziy", 1).getString();
        Naziz = var2.get("Nazi", "Naziz", 2).getString();
        var2.save();
    }

    @Mod.PostInit
    public static void postInit(FMLPostInitializationEvent var0) {
    }

    public static int getUniqueEntityId() {
        while (mv.b((int)(++startEntityId)) != null) {
        }
        return startEntityId;
    }

    @Mod.ServerStarting
    public void serverStarting(FMLServerStartingEvent e) {
        e.registerServerCommand((z)new CommandDefence());
    }

    public static void registerEntityEgg(Class entity, int primaryColor, int secondaryColor) {
        int id = ModBase.getUniqueEntityId();
        mv.d.put(id, entity);
        mv.a.put(id, new mw(id, 0xFFFFFF, 0));
    }

    public static int[] rotXZByDir(int x, int z2, int dir) {
        int[] nArray;
        if (dir == 0) {
            int[] nArray2 = new int[2];
            nArray2[0] = x;
            nArray = nArray2;
            nArray2[1] = z2;
        } else if (dir == 1) {
            int[] nArray3 = new int[2];
            nArray3[0] = -z2;
            nArray = nArray3;
            nArray3[1] = x;
        } else if (dir == 2) {
            int[] nArray4 = new int[2];
            nArray4[0] = -x;
            nArray = nArray4;
            nArray4[1] = -z2;
        } else {
            int[] nArray5 = new int[2];
            nArray5[0] = z2;
            nArray = nArray5;
            nArray5[1] = -x;
        }
        return nArray;
    }

    public static float[] rotXZByDir(float x, float z2, int dir) {
        float[] fArray;
        if (dir == 0) {
            float[] fArray2 = new float[2];
            fArray2[0] = x;
            fArray = fArray2;
            fArray2[1] = z2;
        } else if (dir == 1) {
            float[] fArray3 = new float[2];
            fArray3[0] = -z2;
            fArray = fArray3;
            fArray3[1] = x;
        } else if (dir == 2) {
            float[] fArray4 = new float[2];
            fArray4[0] = -x;
            fArray = fArray4;
            fArray4[1] = -z2;
        } else {
            float[] fArray5 = new float[2];
            fArray5[0] = z2;
            fArray = fArray5;
            fArray5[1] = -x;
        }
        return fArray;
    }

    static {
        MetroTab = new MetroTab(ve.getNextID(), "MetroTabBlocks");
        MetroTabDecor = new MetroTabDecor(ve.getNextID(), "MetroTabDecor");
        MetroTabGuns = new MetroTabGuns(ve.getNextID(), "MetroTabGuns");
        MetroTabArmor = new MetroTabArmor(ve.getNextID(), "MetroTabArmor");
        MetroTabAtt = new MetroTabAtt(ve.getNextID(), "MetroTabAtt");
        MetroTabTunnels = new MetroTabTunnels(ve.getNextID(), "MetroTabTunnels");
        startEntityId = 300;
        blocklight = new BlockLight(4016);
        blocklight1 = new BlockLight1(4017);
        blocklight2 = new BlockLight2(4018);
        blocklight3 = new BlockLight3(4019);
        rails = new BlockWideRail(500, aif.f).c(2.5f).b(5.0f);
        gag = new BlockGag(501, aif.f).c(2.5f).b(5.0f);
        railGag = new BlockRailGag(502, aif.f).c(2.5f).b(5.0f);
        itemRails = new ItemWideRails(600);
        itemRegina = new ItemRegina(700);
        itemDrezina = new ItemDrezina(701);
        BackPackMaterial = EnumHelper.addArmorMaterial((String)"BackPack", (int)1, (int[])new int[]{3, 2, 1, 2}, (int)5);
        GasMaskMaterial = EnumHelper.addArmorMaterial((String)"GasMaskMaterial", (int)1, (int[])new int[]{2, 1, 1, 1}, (int)5);
        HeavyArmorMaterial = EnumHelper.addArmorMaterial((String)"HeavyArmorMaterial", (int)1, (int[])new int[]{1, 20, 1, 1}, (int)5);
        SpartaArmorMaterial = EnumHelper.addArmorMaterial((String)"SpartaArmorMaterial", (int)1, (int[])new int[]{1, 22, 1, 1}, (int)5);
        HeavyPlusArmorMaterial = EnumHelper.addArmorMaterial((String)"HeavyPlusArmorMaterial", (int)1, (int[])new int[]{1, 25, 1, 1}, (int)5);
        HeavyPlus1ArmorMaterial = EnumHelper.addArmorMaterial((String)"HeavyPlus1ArmorMaterial", (int)1, (int[])new int[]{1, 30, 1, 1}, (int)5);
        NormalArmorMaterial = EnumHelper.addArmorMaterial((String)"NormalArmorMaterial", (int)1, (int[])new int[]{1, 15, 1, 1}, (int)5);
        LowArmorMaterial = EnumHelper.addArmorMaterial((String)"LowArmorMaterial", (int)1, (int[])new int[]{1, 10, 1, 1}, (int)5);
        VeryLowArmorMaterial = EnumHelper.addArmorMaterial((String)"VeryLowArmorMaterial", (int)1, (int[])new int[]{1, 7, 1, 1}, (int)5);
        HimzaArmorMaterial = EnumHelper.addArmorMaterial((String)"HimzaArmorMaterial", (int)1, (int[])new int[]{1, 12, 1, 1}, (int)5);
        CcleanArmorMaterial = EnumHelper.addArmorMaterial((String)"CcleanArmorMaterial", (int)1, (int[])new int[]{1, 14, 1, 1}, (int)5);
        HeavyHatArmorMaterial = EnumHelper.addArmorMaterial((String)"HeavyHatArmorMaterial", (int)1, (int[])new int[]{8, 1, 1, 1}, (int)5);
        HeavyNaziHatArmorMaterial = EnumHelper.addArmorMaterial((String)"HeavyNaziHatArmorMaterial", (int)1, (int[])new int[]{6, 1, 1, 1}, (int)5);
        Heavy1HatArmorMaterial = EnumHelper.addArmorMaterial((String)"Heavy1HatArmorMaterial", (int)1, (int[])new int[]{4, 1, 1, 1}, (int)5);
        Machete = new Machete(501).b("Machete").d(1);
        Himza = new HimArmor(5793, HimzaArmorMaterial, 1, 1).b("Himza").a(MetroTabArmor);
        Heavy = new HeavyArmor(5797, HeavyPlus1ArmorMaterial, 1, 1).b("Heavy").a(MetroTabArmor);
        Clean = new Clean(5801, CcleanArmorMaterial, 1, 1).b("Clean").a(MetroTabArmor);
        CClean = new CClean(5803, VeryLowArmorMaterial, 1, 1).b("CClean").a(MetroTabArmor);
        Normal = new Normal(5802, HeavyArmorMaterial, 1, 1).b("Normal").a(MetroTabArmor);
        Kozh = new Kozh(5804, LowArmorMaterial, 1, 1).b("Kozh").a(MetroTabArmor);
        Easy = new Easy(5805, NormalArmorMaterial, 1, 1).b("Easy").a(MetroTabArmor);
        Heavy1 = new Heavy1(5806, HeavyPlusArmorMaterial, 1, 1).b("Heavy1").a(MetroTabArmor);
        Sparta = new Sparta(5807, SpartaArmorMaterial, 1, 1).b("Sparta").a(MetroTabArmor);
        HeavyHat = new HeavyHat(5808, HeavyHatArmorMaterial, 1, 0).b("HeavyHat").a(MetroTabArmor);
        NaziHat = new NaziHat(5809, HeavyNaziHatArmorMaterial, 1, 0).b("NaziHat").a(MetroTabArmor);
        Heavy1Hat = new Heavy1Hat(5810, Heavy1HatArmorMaterial, 1, 0).b("Heavy1Hat").a(MetroTabArmor);
        Red = new ArmorRed(5811, CcleanArmorMaterial, 1, 1).b("Red").a(MetroTabArmor);
        Nazi = new ArmorNazi(5812, NormalArmorMaterial, 1, 1).b("Nazi").a(MetroTabArmor);
        filter = new Filter(5791).b("Filter");
        gas_mask = new GasMask(5792, GasMaskMaterial, 1, 0).b("Protivogas").a(MetroTabArmor);
        Patron = new Patron(2186).b("Patron").d(64);
        GoldMag = new GoldMag(2187).b("GoldMag").d(64);
        Zheton = new Zheton(2185).b("Zheton").d(64);
        Item1 = new Item1(2175).b("Item1").d(4);
        Item2 = new Item2(2176).b("Item2").d(16);
        Item3 = new Item3(2177).b("Item3").d(4);
        Item4 = new Item4(2178).b("Item4").d(5);
        Item5 = new Item5(2179).b("Item5").d(1);
        Item6 = new Item6(2180).b("Item6").d(16);
        Item7 = new Item7(2181).b("Item7").d(16);
        Item8 = new Item8(2182).b("Item8").d(1);
        Item9 = new Item9(2183).b("Item9").d(16);
        Item10 = new Item10(2184).b("Item10").d(4);
        Priklad = new Priklad(2188).b("Priklad").d(4);
        WeaponParts = new WeaponParts(2189).b("WeaponParts").d(4);
        Stvol = new Stvol(2190).b("Stvol").d(4);
        GazMeh = new GazMeh(2191).b("GazMeh").d(4);
        Cev = new Cev(2192).b("Cev").d(4);
        Optica = new Optica(2200).b("Optica").d(1);
        OpticaSmall = new OpticaSmall(2211).b("OpticaSmall").d(1);
        Kolimator = new Kolimator(2212).b("Kolimator").d(1);
        Butt = new Butt(2201).b("Butt").d(1);
        UboinikBarrel = new UboinikBarrel(2216).b("UboinikBarrel").d(1);
        Barrel = new Barrel(2202).b("Barrel").d(1);
        Laser = new Laser(2210).b("Laser").d(1);
        Glushak = new Glushak(2213).b("Glushak").d(1);
        AutoFire = new AutoFire(2214).b("AutoFire").d(1);
        AbzacGlushak = new AbzacGlushak(2215).b("AbzacGlushak").d(1);
        VentilGlushak = new VentilGlushak(2217).b("VentilGlushak").d(1);
        VsvGlushak = new VsvGlushak(2218).b("VsvGlushak").d(1);
        UbludokButt = new UbludokButt(2219).b("UbludokButt").d(1);
        AshotButt = new AshotButt(2220).b("AshotButt").d(1);
        QuadBarrel = new QuadBarrel(2221).b("QuadBarrel").d(1);
        DupletGlushak = new DupletGlushak(2222).b("DupletGlushak").d(1);
        DupletBarrel = new DupletBarrel(2223).b("DupletBarrel").d(1);
        DupletPriklad = new DupletPriklad(2224).b("DupletPriklad").d(1);
        DupletButt = new DupletButt(2225).b("DupletButt").d(1);
        GatlingButt = new GatlingButt(2226).b("GatlingButt").d(1);
        GatlingBarrel = new GatlingBarrel(2227).b("GtlingBarrel").d(1);
        GatlingMotor = new GatlingMotor(2228).b("GtlingMotor").d(1);
        GatlingGiro = new GatlingGiro(2229).b("GtlingGiro").d(1);
        TiharOptica = new TiharOptica(2230).b("TiharOptica").d(1);
        PodonokButt = new PodonokButt(2231).b("PodonokButt").d(1);
        PodonokGas = new PodonokGas(2232).b("PodonokGas").d(1);
        RailgunAkk = new RailgunAkk(2233).b("RailgunAkk").d(1);
        RailgunKon = new RailgunKon(2234).b("RailgunKon").d(1);
        BackPackSmall = new BackPackSmall(2203, BackPackMaterial, 1, 2).b("BackPackSmall").a(MetroTabArmor);
        BackPackMedium = new BackPackMedium(2204, BackPackMaterial, 1, 2).b("BackPackMedium").a(MetroTabArmor);
        BackPackLarge = new BackPackLarge(2205, BackPackMaterial, 1, 2).b("BackPackLarge").a(MetroTabArmor);
        Med = new Med(2206).b("Med").d(1);
        Lamp = new Lamp(2207).b("Lamp").d(1);
        Power = new Power(2208).b("Power").d(1);
        Flyashka = new Flyashka(2209).b("Flyashka").d(1);
        Bullet5x45 = new Bullet5x45(2700).b("Bullet5x45").d(64);
        Bullet12x70 = new Bullet12x70(2701).b("Bullet12x70").d(64);
        Bullet12x7 = new Bullet12x7(2702).b("Bullet12x7").d(64);
        BulletBolt = new BulletBolt(2703).b("BulletBolt").d(64);
        Bullet15mm = new Bullet15mm(2704).b("Bullet15mm").d(64);
        BulletMagnum = new BulletMagnum(2705).b("BulletMagnum").d(64);
        Bullet12_7x108 = new Bullet12_7x108(2706).b("Bullet12_7x108").d(64);
        Bullet7_62x54 = new Bullet7_62x54(2707).b("Bullet7_62x54").d(64);
        Bullet12_7x55 = new Bullet12_7x55(2708).b("Bullet12_7x55").d(64);
        Grenade = new Grenade(2900).b("Grenade").d(5);
        GrenadeBig = new GrenadeBig(2901).b("GrenadeBig").d(5);
        Ubludok = new Ubludok(2800, 30, 5).b("Ubludok");
        AK2012 = new AK2012(2802, 30, 5).b("AK2012");
        AK = new AK(2804, 30, 5).b("AK");
        AKSU = new AKSU(2805, 30, 5).b("AKSU");
        Ashot = new Ashot(2807, 1, 2).b("Ashot");
        Duplet = new Duplet(2808, 2, 3).b("Duplet");
        Gatling = new Gatling(2809, 150, 6).b("Gatling");
        Helsing = new Helsing(2811, 6, 6).b("Helsing");
        Tihar = new Tihar(2813, 15, 6).b("Tihar");
        Podonok = new Podonok(2815, 20, 4).b("Podonok");
        Preved = new Preved(2817, 1, 15).b("Preved");
        Railgun = new Railgun(2819, 15, 5).b("Railgun");
        Revolver = new Revolver(2820, 6, 4).b("Revolver");
        Rpk = new Rpk(2821, 100, 5).b("Rpk");
        Saiga = new Saiga(2824, 20, 3).b("Saiga");
        Abzac = new Abzac(2827, 40, 4).b("Abzac");
        Uboinik = new Uboinik(2830, 6, 3).b("Uboinik");
        Ventil = new Ventil(2831, 10, 10).b("Ventil");
        Vsv = new Vsv(2834, 20, 5).b("Vsv");
        Vyhlop = new Vyhlop(2836, 5, 12).b("Vyhlop");
        Velopal = new Velopal(2838, 6, 4).b("Velopal");
        Ognemet = new Ognemet(2839, 6, 4).b("Ognemet");
        PodonokAmmo = new PodonokAmmo(2816, ModBase.BulletMagnum.cp, 8, "\u0422\u0440\u0435\u0431\u0443\u044e\u0442\u0441\u044f \u043f\u0430\u0442\u0440\u043e\u043d\u044b .44 Magnum").b("PodonokAmmo");
        TiharAmmo = new TiharAmmo(2814, ModBase.Bullet15mm.cp, 15, "\u0422\u0440\u0435\u0431\u0443\u044e\u0442\u0441\u044f \u0441\u0442\u0430\u043b\u044c\u043d\u044b\u0435 \u0448\u0430\u0440\u0438\u043a\u0438 15\u043c\u043c").b("TiharAmmo");
        HelsingAmmo = new HelsingAmmo(2812, ModBase.BulletBolt.cp, 8, "\u0422\u0440\u0435\u0431\u0443\u044e\u0442\u0441\u044f \u0431\u043e\u043b\u0442\u044b").b("HelsingAmmo");
        GatlingAmmo = new GatlingAmmo(2810, ModBase.Bullet12x7.cp, 150, "\u0422\u0440\u0435\u0431\u0443\u044e\u0442\u0441\u044f \u043f\u0430\u0442\u0440\u043e\u043d\u044b 12,7").b("GatlingAmmo");
        UbludokAmmo = new UbludokAmmo(2801, ModBase.Bullet5x45.cp, 30, "\u041d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u044b \u043f\u0430\u0442\u0440\u043e\u043d\u044b 5,45x39").b("UbludokAmmo");
        AK2012Ammo = new AK2012Ammo(2803, ModBase.Bullet5x45.cp, 40, "\u041d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u044b \u043f\u0430\u0442\u0440\u043e\u043d\u044b 5,45x39").b("AK2012Ammo");
        AKAmmo = new AKAmmo(2806, ModBase.Bullet5x45.cp, 30, "\u041d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u044b \u043f\u0430\u0442\u0440\u043e\u043d\u044b 5,45x39").b("AKAmmo");
        RpkAmmo = new RpkAmmo(2822, ModBase.Bullet5x45.cp, 40, "\u041d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u044b \u043f\u0430\u0442\u0440\u043e\u043d\u044b 5,45x39").b("RpkAmmo");
        RpkAmmoBig = new RpkAmmoBig(2823, ModBase.Bullet5x45.cp, 100, "\u041d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u044b \u043f\u0430\u0442\u0440\u043e\u043d\u044b 5,45x39").b("RpkAmmoBig");
        SaigaAmmo = new SaigaAmmo(2825, ModBase.Bullet12x70.cp, 10, "\u0422\u0440\u0435\u0431\u0443\u044e\u0442\u0441\u044f \u043f\u0430\u0442\u0440\u043e\u043d\u044b 12/70").b("SaigaAmmo");
        SaigaAmmoBig = new SaigaAmmoBig(2826, ModBase.Bullet12x70.cp, 20, "\u0422\u0440\u0435\u0431\u0443\u044e\u0442\u0441\u044f \u043f\u0430\u0442\u0440\u043e\u043d\u044b 12/70").b("SaigaAmmoBig");
        AbzacAmmo = new AbzacAmmo(2828, ModBase.Bullet12x70.cp, 20, "\u0422\u0440\u0435\u0431\u0443\u044e\u0442\u0441\u044f \u043f\u0430\u0442\u0440\u043e\u043d\u044b 12/70").b("AbzacAmmo");
        AbzacAmmoBig = new AbzacAmmoBig(2829, ModBase.Bullet12x70.cp, 40, "\u0422\u0440\u0435\u0431\u0443\u044e\u0442\u0441\u044f \u043f\u0430\u0442\u0440\u043e\u043d\u044b 12/70").b("AbzacAmmoBig");
        VentilAmmo = new VentilAmmo(2832, ModBase.Bullet7_62x54.cp, 5, "\u0422\u0440\u0435\u0431\u0443\u044e\u0442\u0441\u044f \u043f\u0430\u0442\u0440\u043e\u043d\u044b 7,62x54").b("VentilAmmo");
        VentilAmmoBig = new VentilAmmoBig(2833, ModBase.Bullet7_62x54.cp, 10, "\u0422\u0440\u0435\u0431\u0443\u044e\u0442\u0441\u044f \u043f\u0430\u0442\u0440\u043e\u043d\u044b 7,62x54").b("VentilAmmoBig");
        VsvAmmo = new VsvAmmo(2835, ModBase.Bullet5x45.cp, 20, "\u0422\u0440\u0435\u0431\u0443\u044e\u0442\u0441\u044f \u043f\u0430\u0442\u0440\u043e\u043d\u044b 5,45x39").b("VsvAmmo");
        VyhlopAmmo = new VyhlopAmmo(2837, ModBase.Bullet12_7x55.cp, 5, "\u0422\u0440\u0435\u0431\u0443\u044e\u0442\u0441\u044f \u043f\u0430\u0442\u0440\u043e\u043d\u044b 5,45x39").b("VyhlopAmmo");
        OgnemetAmmo = new OgnemetAmmo(2840, ModBase.Bullet12_7x55.cp, 5, "").b("OgnemetAmmo");
        PodonokAmmoBig = new PodonokAmmoBig(2841, ModBase.BulletMagnum.cp, 20, "\u0422\u0440\u0435\u0431\u0443\u044e\u0442\u0441\u044f \u043f\u0430\u0442\u0440\u043e\u043d\u044b .44 Magnum").b("PodonokAmmoBig");
        PrevedAmmo = new PrevedAmmo(2842, ModBase.Bullet12_7x108.cp, 5, "\u0422\u0440\u0435\u0431\u0443\u044e\u0442\u0441\u044f \u043f\u0430\u0442\u0440\u043e\u043d\u044b 12,7x108").b("PrevedAmmo");
        AKK = new AKK(2281, 30, 5).b("AKK");
        AKKAmmo = new AKKAmmo(2282, ModBase.Bullet5x45.cp, 30, "\u041d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u044b \u043f\u0430\u0442\u0440\u043e\u043d\u044b 5,45x39").b("AKKAmmo");
        proxy = new CommonProxy();
        ModelGermo2 = new BlockGermo2(3000).a(MetroTab).r().c("Germo2");
        ModelGermo1 = new BlockGermo1(3002).a(MetroTab).r().c("Germo1");
        ModelGermo = new BlockGermo(3001).a(MetroTab).r().c("Germo");
        ModelShkaf = new BlockShkaf(3003).a(MetroTabDecor).r().c("Shkaf");
        ModelShkaf1 = new BlockShkaf1(3004).a(MetroTabDecor).r().c("Shkaf1");
        ModelChair1 = new BlockChair1(3009).a(MetroTabDecor).r().c("Chair1");
        ModelShkaf2 = new BlockShkaf2(3005).a(MetroTabDecor).r().c("Shkaf2");
        ModelChair2 = new BlockChair2(3006).a(MetroTabDecor).r().c("Chair2");
        ModelLavka1 = new BlockLavka1(3007).a(MetroTabDecor).r().c("Lavka1");
        ModelPolka1 = new BlockPolka1(3008).a(MetroTabDecor).r().c("Polka1");
        ModelPolka2 = new BlockPolka2(3010).a(MetroTabDecor).r().c("Polka2");
        ModelTabl1 = new BlockTabl1(3011).a(MetroTabDecor).r().c("Tabl1");
        ModelVanna = new BlockVanna(3012).a(MetroTabDecor).r().c("Vanna");
        ModelWash = new BlockWash(3013).a(MetroTabDecor).r().c("Wash");
        ModelTumba1 = new BlockTumba1(3014).a(MetroTabDecor).r().c("Tumba1");
        ModelTumba2 = new BlockTumba2(3015).a(MetroTabDecor).r().c("Tumba2");
        ModelTumba3 = new BlockTumba3(3016).a(MetroTabDecor).r().c("Tumba3");
        ModelBarStol = new BlockBarStol(3017).a(MetroTabDecor).r().c("BarStol");
        ModelBed = new BlockBed(3018).a(MetroTabDecor).r().c("Bed");
        ModelBox1 = new BlockBox1(3019).a(MetroTabDecor).r().c("Box1");
        ModelBoxp = new BlockBoxp(3020).a(MetroTabDecor).r().c("Boxp");
        ModelBoxpl = new BlockBoxpl(3021).a(MetroTabDecor).r().c("Boxpl");
        ModelDivan1 = new BlockDivan1(3022).a(MetroTabDecor).r().c("Divan1");
        ModelDivan2 = new BlockDivan2(3023).a(MetroTabDecor).r().c("Divan2");
        ModelDivan3 = new BlockDivan3(3024).a(MetroTabDecor).r().c("Divan3");
        ModelDivan4 = new BlockDivan4(3025).a(MetroTabDecor).r().c("Divan4");
        ModelElBox1 = new BlockElBox1(3026).a(MetroTabDecor).r().c("ElBox1");
        ModelElBox2 = new BlockElBox2(3027).a(MetroTabDecor).r().c("ElBox2");
        ModelElBox3 = new BlockElBox3(3028).a(MetroTabDecor).r().c("ElBox3");
        ModelElBox4 = new BlockElBox4(3029).a(MetroTabDecor).r().c("ElBox4");
        ModelFreez1 = new BlockFreez1(3030).a(MetroTabDecor).r().c("Freez1");
        ModelFreez2 = new BlockFreez2(3031).a(MetroTabDecor).r().c("Freez2");
        ModelGunb1 = new BlockGunb1(3032).a(MetroTabDecor).r().c("Gunb1");
        ModelGunb2 = new BlockGunb2(3033).a(MetroTabDecor).r().c("Gunb2");
        ModelKab = new BlockKab(3034).a(MetroTabDecor).r().c("Kab");
        ModelKan = new BlockKan(3035).a(MetroTabDecor).r().c("Kan");
        ModelLav2 = new BlockLav2(3036).a(MetroTabDecor).r().c("Lav2");
        ModelPhone = new BlockPhone(3037).a(MetroTabDecor).r().c("Phone");
        ModelPlita = new BlockPlita(3038).a(MetroTabDecor).r().c("Plita");
        ModelRak = new BlockRak(3039).a(MetroTabDecor).r().c("Rak");
        ModelShkaf0 = new BlockShkaf0(3040).a(MetroTabDecor).r().c("Shkaf0");
        ModelShkafm = new BlockShkafm(3041).a(MetroTabDecor).r().c("Shkafm");
        ModelStand = new BlockStand(3042).a(MetroTabDecor).r().c("Stand");
        ModelStelazh1 = new BlockStelazh1(3043).a(MetroTabDecor).r().c("Stelazh1");
        ModelStelazh2 = new BlockStelazh2(3044).a(MetroTabDecor).r().c("Stelazh2");
        ModelStelazh3 = new BlockStelazh3(3045).a(MetroTabDecor).r().c("Stelazh3");
        ModelStul1 = new BlockStul1(3046).a(MetroTabDecor).r().c("Stul1");
        ModelStul2 = new BlockStul2(3047).a(MetroTabDecor).r().c("Stul2");
        ModelTul1 = new BlockTul1(3048).a(MetroTabDecor).r().c("Tul1");
        ModelTul2 = new BlockTul2(3049).a(MetroTabDecor).r().c("Tul2");
        ModelUmi = new BlockUmi(3050).a(MetroTabDecor).r().c("Umi");
        ModelTv1 = new BlockTv1(3053).a(MetroTabDecor).r().c("Tv1");
        ModelFp1 = new BlockFp1(3051).a(MetroTabDecor).r().c("Fp1").a(1.0f);
        ModelFp2 = new BlockFp2(3052).a(MetroTabDecor).r().c("Fp2").a(1.0f);
        ModelPalatka = new BlockPalatka(4022).a(MetroTabDecor).r().c("Palatka").a(0.0f);
        ModelTunnel = new BlockTunnel(3054).a(MetroTabTunnels).r().c("Tunnel");
        ModelTunnel90 = new BlockTunnel90(3055).a(MetroTabTunnels).r().c("Tunnel90");
        ModelTunnelBeton1 = new BlockTunnelBeton1(4000).a(MetroTabTunnels).r().c("ModelTunnelBeton1");
        ModelTunnelZelezo1 = new BlockTunnelZelezo1(4001).a(MetroTabTunnels).r().c("ModelTunnelZelezo1");
        ModelTunnelBetonDouble1 = new BlockTunnelBetonDouble1(4002).a(MetroTabTunnels).r().c("ModelTunnelBetonDouble1");
        ModelTunnelBetonX = new BlockTunnelBetonX(4003).a(MetroTabTunnels).r().c("ModelTunnelBetonX");
        ModelTunnelZelezo90 = new BlockTunnelZelezo90(4004).a(MetroTabTunnels).r().c("ModelTunnelZelezo90");
        ModelTunnelZelezoDouble = new BlockTunnelZelezoDouble(4005).a(MetroTabTunnels).r().c("ModelTunnelZelezoDouble");
        ModelTunnelZelezoExit = new BlockTunnelZelezoExit(4006).a(MetroTabTunnels).r().c("ModelTunnelZelezoExit");
        ModelTunnelBetonExit = new BlockTunnelBetonExit(4007).a(MetroTabTunnels).r().c("ModelTunnelBetonExit");
        ModelTunnelBetonProvoda = new BlockTunnelBetonProvoda(4008).a(MetroTabTunnels).r().c("TunnelBetonProvoda");
        ModelTunnelZelezoProvoda = new BlockTunnelZelezoProvoda(4009).a(MetroTabTunnels).r().c("TunnelZelezoProvoda");
        ModelTunnelBetonDerevo = new BlockTunnelBetonDerevo(4010).a(MetroTabTunnels).r().c("TunnelBetonDerevo");
        ModelTunnelBetonDerevo1 = new BlockTunnelBetonDerevo1(4011).a(MetroTabTunnels).r().c("TunnelBetonDerevo1");
        ModelTunnelZelezoDerevo = new BlockTunnelZelezoDerevo(4012).a(MetroTabTunnels).r().c("TunnelZelezoDerevo");
        ModelTunnelZelezoDerevo1 = new BlockTunnelZelezoDerevo1(4013).a(MetroTabTunnels).r().c("TunnelZelezoDerevo1");
        ModelTunnelZelezoTrub = new BlockTunnelZelezoTrub(4014).a(MetroTabTunnels).r().c("TunnelZelezoTrub");
        ModelTunnelBetonTrub = new BlockTunnelBetonTrub(4015).a(MetroTabTunnels).r().c("TunnelBetonTrub");
        ModelTunnelNew = new BlockTunnelNew(4021).a(MetroTabTunnels).r().c("TunnelBetonNew");
        allBiomes = new aav[]{aav.r, aav.d, aav.s, aav.e, aav.v, aav.f, aav.t, aav.l, aav.m, aav.j, aav.o, aav.n, aav.w, aav.x, aav.p, aav.q, aav.b, aav.c, aav.i, aav.k, aav.h, aav.g, aav.u};
        ModelStol = new BlockStol(1993).a(MetroTabDecor).r().c("Stol");
        ModelStul = new BlockStul(1992).a(MetroTabDecor).r().c("Stul");
        ModelPolka3 = new BlockPolka3(1991).a(MetroTabDecor).r().c("Polka3");
        ModelPolka4 = new BlockPolka4(1990).a(MetroTabDecor).r().c("Polka4");
        ModelDivan = new BlockDivan(1989).a(MetroTabDecor).r().c("Divan");
        ModelKreslo = new BlockKreslo(1988).a(MetroTabDecor).r().c("Kreslo");
        ModelStolik = new BlockStolik(1987).a(MetroTabDecor).r().c("Stolik");
        BlockTp = new TpBlock(1984).a(MetroTab).r().c("BlockTp");
    }
}
