/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ava
 *  bbz
 *  bcd
 *  bgz
 *  bje
 *  cpw.mods.fml.client.registry.ClientRegistry
 *  cpw.mods.fml.client.registry.KeyBindingRegistry
 *  cpw.mods.fml.client.registry.KeyBindingRegistry$KeyHandler
 *  cpw.mods.fml.client.registry.RenderingRegistry
 *  cpw.mods.fml.common.ITickHandler
 *  cpw.mods.fml.common.registry.TickRegistry
 *  cpw.mods.fml.relauncher.Side
 *  mods.provoda.CommonProxy
 *  mods.provoda.entity.EntityPassiveFire
 *  mods.provoda.entity.EntityPropulsionFX
 *  mods.provoda.entity.EntitySpawnerKikimora
 *  mods.provoda.entity.EntityTestDeathFull
 *  mods.provoda.entity.render.RenderBullet
 *  mods.provoda.entity.render.RenderDeath
 *  mods.provoda.entity.render.RenderDrezina
 *  mods.provoda.entity.render.RenderRegina
 *  mods.provoda.guns.ammo.renderers.RenderRpkAmmo
 *  mods.provoda.guns.ammo.renderers.RenderRpkAmmoBig
 *  mods.provoda.guns.ammo.renderers.RendererAK2012Ammo
 *  mods.provoda.guns.ammo.renderers.RendererAKAmmo
 *  mods.provoda.guns.ammo.renderers.RendererAKKAmmo
 *  mods.provoda.guns.ammo.renderers.RendererAbzacAmmo
 *  mods.provoda.guns.ammo.renderers.RendererAbzacAmmoBig
 *  mods.provoda.guns.ammo.renderers.RendererBullet12_7x108
 *  mods.provoda.guns.ammo.renderers.RendererBullet12_7x55
 *  mods.provoda.guns.ammo.renderers.RendererBullet12x7
 *  mods.provoda.guns.ammo.renderers.RendererBullet12x70
 *  mods.provoda.guns.ammo.renderers.RendererBullet15mm
 *  mods.provoda.guns.ammo.renderers.RendererBullet5x45
 *  mods.provoda.guns.ammo.renderers.RendererBullet7_62x54
 *  mods.provoda.guns.ammo.renderers.RendererBulletBolt
 *  mods.provoda.guns.ammo.renderers.RendererBulletMagnum
 *  mods.provoda.guns.ammo.renderers.RendererGatlingAmmo
 *  mods.provoda.guns.ammo.renderers.RendererHelsingAmmo
 *  mods.provoda.guns.ammo.renderers.RendererPodonokAmmo
 *  mods.provoda.guns.ammo.renderers.RendererPodonokAmmoBig
 *  mods.provoda.guns.ammo.renderers.RendererPrevedAmmo
 *  mods.provoda.guns.ammo.renderers.RendererSaigaAmmo
 *  mods.provoda.guns.ammo.renderers.RendererSaigaAmmoBig
 *  mods.provoda.guns.ammo.renderers.RendererTiharAmmo
 *  mods.provoda.guns.ammo.renderers.RendererUbludokAmmo
 *  mods.provoda.guns.ammo.renderers.RendererVentilAmmo
 *  mods.provoda.guns.ammo.renderers.RendererVentilAmmoBig
 *  mods.provoda.guns.ammo.renderers.RendererVsvAmmo
 *  mods.provoda.guns.ammo.renderers.RendererVyhlopAmmo
 *  mods.provoda.guns.grenades.renderers.RenderGrenade
 *  mods.provoda.guns.grenades.renderers.RenderGrenadeBig
 *  mods.provoda.guns.grenades.renderers.RendererGrenade
 *  mods.provoda.guns.grenades.renderers.RendererGrenadeBig
 *  mods.provoda.guns.renderers.Render_AK
 *  mods.provoda.guns.renderers.Render_AK2012
 *  mods.provoda.guns.renderers.Render_AKK
 *  mods.provoda.guns.renderers.Render_AKSU
 *  mods.provoda.guns.renderers.Render_Abzac
 *  mods.provoda.guns.renderers.Render_Ashot
 *  mods.provoda.guns.renderers.Render_Duplet
 *  mods.provoda.guns.renderers.Render_Gatling
 *  mods.provoda.guns.renderers.Render_Helsing
 *  mods.provoda.guns.renderers.Render_Machete
 *  mods.provoda.guns.renderers.Render_Ognemet
 *  mods.provoda.guns.renderers.Render_Podonok
 *  mods.provoda.guns.renderers.Render_Preved
 *  mods.provoda.guns.renderers.Render_Railgun
 *  mods.provoda.guns.renderers.Render_Revolver
 *  mods.provoda.guns.renderers.Render_Rpk
 *  mods.provoda.guns.renderers.Render_Saiga
 *  mods.provoda.guns.renderers.Render_Tihar
 *  mods.provoda.guns.renderers.Render_Ubludok
 *  mods.provoda.guns.renderers.Render_Uboinik
 *  mods.provoda.guns.renderers.Render_Velopal
 *  mods.provoda.guns.renderers.Render_Ventil
 *  mods.provoda.guns.renderers.Render_Vsv
 *  mods.provoda.guns.renderers.Render_Vyhlop
 *  mods.provoda.items.armor.gasmask.renderers.RenderFilter
 *  mods.provoda.items.armor.gasmask.renderers.RenderGasMask
 *  mods.provoda.items.armor.hats.RenderHeavy1Hat
 *  mods.provoda.items.armor.hats.RenderHeavyHat
 *  mods.provoda.items.armor.hats.RenderNaziHat
 *  mods.provoda.items.att.renderers.RenderBarrel
 *  mods.provoda.items.att.renderers.RenderButt
 *  mods.provoda.items.att.renderers.RenderKolimator
 *  mods.provoda.items.att.renderers.RenderLaser
 *  mods.provoda.items.att.renderers.RenderOptica
 *  mods.provoda.items.att.renderers.RenderOpticaSmall
 *  mods.provoda.items.att.renderers.RenderUbludokButt
 *  mods.provoda.items.renderers.RenderFlyashka
 *  mods.provoda.items.renderers.RenderLamp
 *  mods.provoda.items.renderers.RenderMed
 *  mods.provoda.items.renderers.RenderPower
 *  mods.provoda.mobs.models.aqua_female.ModelAquaf
 *  mods.provoda.mobs.models.aqua_male.ModelAquam
 *  mods.provoda.mobs.models.bear.ModelBear
 *  mods.provoda.mobs.models.bibliotecar.ModelBiblio
 *  mods.provoda.mobs.models.dark.ModelDark
 *  mods.provoda.mobs.models.demon.ModelDemon
 *  mods.provoda.mobs.models.kikimora.ModelKikimora
 *  mods.provoda.mobs.models.mobus.ModelMobus
 *  mods.provoda.mobs.models.nosach.ModelNosach
 *  mods.provoda.mobs.models.rat.ModelRat
 *  mods.provoda.mobs.models.spider.ModelSpider
 *  mods.provoda.mobs.models.strazh.ModelStrazh
 *  mods.provoda.mobs.renderers.RendererAquaf
 *  mods.provoda.mobs.renderers.RendererAquam
 *  mods.provoda.mobs.renderers.RendererBear
 *  mods.provoda.mobs.renderers.RendererBiblio
 *  mods.provoda.mobs.renderers.RendererDark
 *  mods.provoda.mobs.renderers.RendererDemon
 *  mods.provoda.mobs.renderers.RendererKikimora
 *  mods.provoda.mobs.renderers.RendererMobus
 *  mods.provoda.mobs.renderers.RendererNosach
 *  mods.provoda.mobs.renderers.RendererRat
 *  mods.provoda.mobs.renderers.RendererSpider
 *  mods.provoda.mobs.renderers.RendererStrazh
 *  mods.provoda.renderers.RenderGermo2
 *  mods.provoda.renderers.RenderPalatka
 *  mods.provoda.renderers.RendererBarStol
 *  mods.provoda.renderers.RendererBed
 *  mods.provoda.renderers.RendererBox1
 *  mods.provoda.renderers.RendererBoxp
 *  mods.provoda.renderers.RendererBoxpl
 *  mods.provoda.renderers.RendererChair1
 *  mods.provoda.renderers.RendererChair2
 *  mods.provoda.renderers.RendererDivan
 *  mods.provoda.renderers.RendererDivan1
 *  mods.provoda.renderers.RendererDivan2
 *  mods.provoda.renderers.RendererDivan3
 *  mods.provoda.renderers.RendererDivan4
 *  mods.provoda.renderers.RendererElBox1
 *  mods.provoda.renderers.RendererElBox2
 *  mods.provoda.renderers.RendererElBox3
 *  mods.provoda.renderers.RendererElBox4
 *  mods.provoda.renderers.RendererFp1
 *  mods.provoda.renderers.RendererFp2
 *  mods.provoda.renderers.RendererFreez1
 *  mods.provoda.renderers.RendererFreez2
 *  mods.provoda.renderers.RendererGermo
 *  mods.provoda.renderers.RendererGermo1
 *  mods.provoda.renderers.RendererGunb1
 *  mods.provoda.renderers.RendererGunb2
 *  mods.provoda.renderers.RendererKab
 *  mods.provoda.renderers.RendererKan
 *  mods.provoda.renderers.RendererKreslo
 *  mods.provoda.renderers.RendererLav2
 *  mods.provoda.renderers.RendererLavka1
 *  mods.provoda.renderers.RendererPhone
 *  mods.provoda.renderers.RendererPlita
 *  mods.provoda.renderers.RendererPolka1
 *  mods.provoda.renderers.RendererPolka2
 *  mods.provoda.renderers.RendererPolka3
 *  mods.provoda.renderers.RendererPolka4
 *  mods.provoda.renderers.RendererRak
 *  mods.provoda.renderers.RendererShkaf
 *  mods.provoda.renderers.RendererShkaf0
 *  mods.provoda.renderers.RendererShkaf1
 *  mods.provoda.renderers.RendererShkaf2
 *  mods.provoda.renderers.RendererShkafm
 *  mods.provoda.renderers.RendererStand
 *  mods.provoda.renderers.RendererStelazh1
 *  mods.provoda.renderers.RendererStelazh2
 *  mods.provoda.renderers.RendererStelazh3
 *  mods.provoda.renderers.RendererStol
 *  mods.provoda.renderers.RendererStolik
 *  mods.provoda.renderers.RendererStul
 *  mods.provoda.renderers.RendererStul1
 *  mods.provoda.renderers.RendererStul2
 *  mods.provoda.renderers.RendererTabl1
 *  mods.provoda.renderers.RendererTul1
 *  mods.provoda.renderers.RendererTul2
 *  mods.provoda.renderers.RendererTumba1
 *  mods.provoda.renderers.RendererTumba2
 *  mods.provoda.renderers.RendererTumba3
 *  mods.provoda.renderers.RendererTunnel
 *  mods.provoda.renderers.RendererTv1
 *  mods.provoda.renderers.RendererUmi
 *  mods.provoda.renderers.RendererVanna
 *  mods.provoda.renderers.RendererWash
 *  mods.provoda.renderers.death.RenderDeath1
 *  mods.provoda.renderers.tunnels.RenderTunnel90
 *  mods.provoda.renderers.tunnels.RenderTunnelBeton1
 *  mods.provoda.renderers.tunnels.RenderTunnelBetonDerevo
 *  mods.provoda.renderers.tunnels.RenderTunnelBetonDerevo1
 *  mods.provoda.renderers.tunnels.RenderTunnelBetonDouble1
 *  mods.provoda.renderers.tunnels.RenderTunnelBetonExit
 *  mods.provoda.renderers.tunnels.RenderTunnelBetonProvoda
 *  mods.provoda.renderers.tunnels.RenderTunnelBetonTrub
 *  mods.provoda.renderers.tunnels.RenderTunnelBetonX
 *  mods.provoda.renderers.tunnels.RenderTunnelNew
 *  mods.provoda.renderers.tunnels.RenderTunnelZelezo1
 *  mods.provoda.renderers.tunnels.RenderTunnelZelezo90
 *  mods.provoda.renderers.tunnels.RenderTunnelZelezoDerevo
 *  mods.provoda.renderers.tunnels.RenderTunnelZelezoDerevo1
 *  mods.provoda.renderers.tunnels.RenderTunnelZelezoDouble
 *  mods.provoda.renderers.tunnels.RenderTunnelZelezoExit
 *  mods.provoda.renderers.tunnels.RenderTunnelZelezoProvoda
 *  mods.provoda.renderers.tunnels.RenderTunnelZelezoTrub
 *  mods.provoda.renderers.tunnels.TileEntityWideRailRenderer
 *  mods.provoda.tileentitys.TileEntityBarStol
 *  mods.provoda.tileentitys.TileEntityBed
 *  mods.provoda.tileentitys.TileEntityBox1
 *  mods.provoda.tileentitys.TileEntityBoxp
 *  mods.provoda.tileentitys.TileEntityBoxpl
 *  mods.provoda.tileentitys.TileEntityChair1
 *  mods.provoda.tileentitys.TileEntityChair2
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
 *  mods.provoda.until.ClientTickHandler
 *  mods.provoda.until.ClockKeyBind
 *  mods.provoda.until.DrezinaKeyHandler
 *  mods.provoda.until.ReloadKeyBind
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.MinecraftForgeClient
 */
package mods.provoda;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import mods.provoda.CommonProxy;
import mods.provoda.ModBase;
import mods.provoda.entity.EntityDrezina;
import mods.provoda.entity.EntityFlameBall;
import mods.provoda.entity.EntityGrenade1;
import mods.provoda.entity.EntityGrenadeBig;
import mods.provoda.entity.EntityPassiveFire;
import mods.provoda.entity.EntityPropulsionFX;
import mods.provoda.entity.EntitySpawnerKikimora;
import mods.provoda.entity.EntityTestDeathFull;
import mods.provoda.entity.Entityregina;
import mods.provoda.entity.render.RenderBullet;
import mods.provoda.entity.render.RenderDeath;
import mods.provoda.entity.render.RenderDrezina;
import mods.provoda.entity.render.RenderRegina;
import mods.provoda.guns.ammo.renderers.RenderRpkAmmo;
import mods.provoda.guns.ammo.renderers.RenderRpkAmmoBig;
import mods.provoda.guns.ammo.renderers.RendererAK2012Ammo;
import mods.provoda.guns.ammo.renderers.RendererAKAmmo;
import mods.provoda.guns.ammo.renderers.RendererAKKAmmo;
import mods.provoda.guns.ammo.renderers.RendererAbzacAmmo;
import mods.provoda.guns.ammo.renderers.RendererAbzacAmmoBig;
import mods.provoda.guns.ammo.renderers.RendererBullet12_7x108;
import mods.provoda.guns.ammo.renderers.RendererBullet12_7x55;
import mods.provoda.guns.ammo.renderers.RendererBullet12x7;
import mods.provoda.guns.ammo.renderers.RendererBullet12x70;
import mods.provoda.guns.ammo.renderers.RendererBullet15mm;
import mods.provoda.guns.ammo.renderers.RendererBullet5x45;
import mods.provoda.guns.ammo.renderers.RendererBullet7_62x54;
import mods.provoda.guns.ammo.renderers.RendererBulletBolt;
import mods.provoda.guns.ammo.renderers.RendererBulletMagnum;
import mods.provoda.guns.ammo.renderers.RendererGatlingAmmo;
import mods.provoda.guns.ammo.renderers.RendererHelsingAmmo;
import mods.provoda.guns.ammo.renderers.RendererPodonokAmmo;
import mods.provoda.guns.ammo.renderers.RendererPodonokAmmoBig;
import mods.provoda.guns.ammo.renderers.RendererPrevedAmmo;
import mods.provoda.guns.ammo.renderers.RendererSaigaAmmo;
import mods.provoda.guns.ammo.renderers.RendererSaigaAmmoBig;
import mods.provoda.guns.ammo.renderers.RendererTiharAmmo;
import mods.provoda.guns.ammo.renderers.RendererUbludokAmmo;
import mods.provoda.guns.ammo.renderers.RendererVentilAmmo;
import mods.provoda.guns.ammo.renderers.RendererVentilAmmoBig;
import mods.provoda.guns.ammo.renderers.RendererVsvAmmo;
import mods.provoda.guns.ammo.renderers.RendererVyhlopAmmo;
import mods.provoda.guns.grenades.renderers.RenderGrenade;
import mods.provoda.guns.grenades.renderers.RenderGrenadeBig;
import mods.provoda.guns.grenades.renderers.RendererGrenade;
import mods.provoda.guns.grenades.renderers.RendererGrenadeBig;
import mods.provoda.guns.renderers.Render_AK;
import mods.provoda.guns.renderers.Render_AK2012;
import mods.provoda.guns.renderers.Render_AKK;
import mods.provoda.guns.renderers.Render_AKSU;
import mods.provoda.guns.renderers.Render_Abzac;
import mods.provoda.guns.renderers.Render_Ashot;
import mods.provoda.guns.renderers.Render_Duplet;
import mods.provoda.guns.renderers.Render_Gatling;
import mods.provoda.guns.renderers.Render_Helsing;
import mods.provoda.guns.renderers.Render_Machete;
import mods.provoda.guns.renderers.Render_Ognemet;
import mods.provoda.guns.renderers.Render_Podonok;
import mods.provoda.guns.renderers.Render_Preved;
import mods.provoda.guns.renderers.Render_Railgun;
import mods.provoda.guns.renderers.Render_Revolver;
import mods.provoda.guns.renderers.Render_Rpk;
import mods.provoda.guns.renderers.Render_Saiga;
import mods.provoda.guns.renderers.Render_Tihar;
import mods.provoda.guns.renderers.Render_Ubludok;
import mods.provoda.guns.renderers.Render_Uboinik;
import mods.provoda.guns.renderers.Render_Velopal;
import mods.provoda.guns.renderers.Render_Ventil;
import mods.provoda.guns.renderers.Render_Vsv;
import mods.provoda.guns.renderers.Render_Vyhlop;
import mods.provoda.items.armor.backpacklarge.ModelBackPackLarge;
import mods.provoda.items.armor.backpackmedium.ModelBackPackMedium;
import mods.provoda.items.armor.backpacksmall.ModelSmallBackPack;
import mods.provoda.items.armor.cclean.models.CCleanModel;
import mods.provoda.items.armor.clean.models.Clean;
import mods.provoda.items.armor.easy.ModelEasy;
import mods.provoda.items.armor.gasmask.models.Protivogas;
import mods.provoda.items.armor.gasmask.renderers.RenderFilter;
import mods.provoda.items.armor.gasmask.renderers.RenderGasMask;
import mods.provoda.items.armor.hats.ModelHeavy1Hat;
import mods.provoda.items.armor.hats.ModelHeavyHat;
import mods.provoda.items.armor.hats.ModelNaziHat;
import mods.provoda.items.armor.hats.RenderHeavy1Hat;
import mods.provoda.items.armor.hats.RenderHeavyHat;
import mods.provoda.items.armor.hats.RenderNaziHat;
import mods.provoda.items.armor.heavy.models.HeavyChest;
import mods.provoda.items.armor.heavy1.ModelHeavy1;
import mods.provoda.items.armor.him.models.HimCheest;
import mods.provoda.items.armor.kozh.ModelKozh;
import mods.provoda.items.armor.nazi.ModelNazi;
import mods.provoda.items.armor.normal.models.ModelNormal;
import mods.provoda.items.armor.red.ModelRed;
import mods.provoda.items.armor.sparta.ModelSparta;
import mods.provoda.items.att.renderers.RenderBarrel;
import mods.provoda.items.att.renderers.RenderButt;
import mods.provoda.items.att.renderers.RenderKolimator;
import mods.provoda.items.att.renderers.RenderLaser;
import mods.provoda.items.att.renderers.RenderOptica;
import mods.provoda.items.att.renderers.RenderOpticaSmall;
import mods.provoda.items.att.renderers.RenderUbludokButt;
import mods.provoda.items.renderers.RenderFlyashka;
import mods.provoda.items.renderers.RenderLamp;
import mods.provoda.items.renderers.RenderMed;
import mods.provoda.items.renderers.RenderPower;
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
import mods.provoda.mobs.models.aqua_female.ModelAquaf;
import mods.provoda.mobs.models.aqua_male.ModelAquam;
import mods.provoda.mobs.models.bear.ModelBear;
import mods.provoda.mobs.models.bibliotecar.ModelBiblio;
import mods.provoda.mobs.models.dark.ModelDark;
import mods.provoda.mobs.models.demon.ModelDemon;
import mods.provoda.mobs.models.kikimora.ModelKikimora;
import mods.provoda.mobs.models.mobus.ModelMobus;
import mods.provoda.mobs.models.nosach.ModelNosach;
import mods.provoda.mobs.models.rat.ModelRat;
import mods.provoda.mobs.models.spider.ModelSpider;
import mods.provoda.mobs.models.strazh.ModelStrazh;
import mods.provoda.mobs.renderers.RendererAquaf;
import mods.provoda.mobs.renderers.RendererAquam;
import mods.provoda.mobs.renderers.RendererBear;
import mods.provoda.mobs.renderers.RendererBiblio;
import mods.provoda.mobs.renderers.RendererDark;
import mods.provoda.mobs.renderers.RendererDemon;
import mods.provoda.mobs.renderers.RendererKikimora;
import mods.provoda.mobs.renderers.RendererMobus;
import mods.provoda.mobs.renderers.RendererNosach;
import mods.provoda.mobs.renderers.RendererRat;
import mods.provoda.mobs.renderers.RendererSpider;
import mods.provoda.mobs.renderers.RendererStrazh;
import mods.provoda.renderers.RenderGermo2;
import mods.provoda.renderers.RenderPalatka;
import mods.provoda.renderers.RendererBarStol;
import mods.provoda.renderers.RendererBed;
import mods.provoda.renderers.RendererBox1;
import mods.provoda.renderers.RendererBoxp;
import mods.provoda.renderers.RendererBoxpl;
import mods.provoda.renderers.RendererChair1;
import mods.provoda.renderers.RendererChair2;
import mods.provoda.renderers.RendererDivan;
import mods.provoda.renderers.RendererDivan1;
import mods.provoda.renderers.RendererDivan2;
import mods.provoda.renderers.RendererDivan3;
import mods.provoda.renderers.RendererDivan4;
import mods.provoda.renderers.RendererElBox1;
import mods.provoda.renderers.RendererElBox2;
import mods.provoda.renderers.RendererElBox3;
import mods.provoda.renderers.RendererElBox4;
import mods.provoda.renderers.RendererFp1;
import mods.provoda.renderers.RendererFp2;
import mods.provoda.renderers.RendererFreez1;
import mods.provoda.renderers.RendererFreez2;
import mods.provoda.renderers.RendererGermo;
import mods.provoda.renderers.RendererGermo1;
import mods.provoda.renderers.RendererGunb1;
import mods.provoda.renderers.RendererGunb2;
import mods.provoda.renderers.RendererKab;
import mods.provoda.renderers.RendererKan;
import mods.provoda.renderers.RendererKreslo;
import mods.provoda.renderers.RendererLav2;
import mods.provoda.renderers.RendererLavka1;
import mods.provoda.renderers.RendererPhone;
import mods.provoda.renderers.RendererPlita;
import mods.provoda.renderers.RendererPolka1;
import mods.provoda.renderers.RendererPolka2;
import mods.provoda.renderers.RendererPolka3;
import mods.provoda.renderers.RendererPolka4;
import mods.provoda.renderers.RendererRak;
import mods.provoda.renderers.RendererShkaf;
import mods.provoda.renderers.RendererShkaf0;
import mods.provoda.renderers.RendererShkaf1;
import mods.provoda.renderers.RendererShkaf2;
import mods.provoda.renderers.RendererShkafm;
import mods.provoda.renderers.RendererStand;
import mods.provoda.renderers.RendererStelazh1;
import mods.provoda.renderers.RendererStelazh2;
import mods.provoda.renderers.RendererStelazh3;
import mods.provoda.renderers.RendererStol;
import mods.provoda.renderers.RendererStolik;
import mods.provoda.renderers.RendererStul;
import mods.provoda.renderers.RendererStul1;
import mods.provoda.renderers.RendererStul2;
import mods.provoda.renderers.RendererTabl1;
import mods.provoda.renderers.RendererTul1;
import mods.provoda.renderers.RendererTul2;
import mods.provoda.renderers.RendererTumba1;
import mods.provoda.renderers.RendererTumba2;
import mods.provoda.renderers.RendererTumba3;
import mods.provoda.renderers.RendererTunnel;
import mods.provoda.renderers.RendererTv1;
import mods.provoda.renderers.RendererUmi;
import mods.provoda.renderers.RendererVanna;
import mods.provoda.renderers.RendererWash;
import mods.provoda.renderers.death.RenderDeath1;
import mods.provoda.renderers.tunnels.RenderTunnel90;
import mods.provoda.renderers.tunnels.RenderTunnelBeton1;
import mods.provoda.renderers.tunnels.RenderTunnelBetonDerevo;
import mods.provoda.renderers.tunnels.RenderTunnelBetonDerevo1;
import mods.provoda.renderers.tunnels.RenderTunnelBetonDouble1;
import mods.provoda.renderers.tunnels.RenderTunnelBetonExit;
import mods.provoda.renderers.tunnels.RenderTunnelBetonProvoda;
import mods.provoda.renderers.tunnels.RenderTunnelBetonTrub;
import mods.provoda.renderers.tunnels.RenderTunnelBetonX;
import mods.provoda.renderers.tunnels.RenderTunnelNew;
import mods.provoda.renderers.tunnels.RenderTunnelZelezo1;
import mods.provoda.renderers.tunnels.RenderTunnelZelezo90;
import mods.provoda.renderers.tunnels.RenderTunnelZelezoDerevo;
import mods.provoda.renderers.tunnels.RenderTunnelZelezoDerevo1;
import mods.provoda.renderers.tunnels.RenderTunnelZelezoDouble;
import mods.provoda.renderers.tunnels.RenderTunnelZelezoExit;
import mods.provoda.renderers.tunnels.RenderTunnelZelezoProvoda;
import mods.provoda.renderers.tunnels.RenderTunnelZelezoTrub;
import mods.provoda.renderers.tunnels.TileEntityWideRailRenderer;
import mods.provoda.tileentitys.TileEntityBarStol;
import mods.provoda.tileentitys.TileEntityBed;
import mods.provoda.tileentitys.TileEntityBox1;
import mods.provoda.tileentitys.TileEntityBoxp;
import mods.provoda.tileentitys.TileEntityBoxpl;
import mods.provoda.tileentitys.TileEntityChair1;
import mods.provoda.tileentitys.TileEntityChair2;
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
import mods.provoda.until.ClientTickHandler;
import mods.provoda.until.ClockKeyBind;
import mods.provoda.until.DrezinaKeyHandler;
import mods.provoda.until.LampKeyBind;
import mods.provoda.until.ModifiKeyBind;
import mods.provoda.until.ReloadKeyBind;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy
extends CommonProxy {
    public final Protivogas dragonChestplate = new Protivogas(1.0f);
    public final HimCheest himCheest = new HimCheest(1.0f);
    public final HeavyChest HeavyCheest = new HeavyChest(1.0f);
    public final ModelHeavy1Hat Heavy1Hat = new ModelHeavy1Hat(1.0f);
    public final Clean Clean = new Clean(1.0f);
    public final CCleanModel CClean = new CCleanModel(1.0f);
    public final ModelNormal Normal = new ModelNormal(1.0f);
    public final ModelSparta Sparta = new ModelSparta(1.0f);
    public final ModelHeavy1 Heavy1 = new ModelHeavy1(1.0f);
    public final ModelKozh Kozh = new ModelKozh(1.0f);
    public final ModelEasy Easy = new ModelEasy(1.0f);
    public final ModelHeavyHat HeavyHat = new ModelHeavyHat(1.0f);
    public final ModelNaziHat NaziHat = new ModelNaziHat(1.0f);
    public final ModelRed Red = new ModelRed(1.0f);
    public final ModelNazi Nazi = new ModelNazi(1.0f);
    public final ModelSmallBackPack BackPackSmall = new ModelSmallBackPack(1.0f);
    public final ModelBackPackMedium BackPackMedium = new ModelBackPackMedium(1.0f);
    public final ModelBackPackLarge BackPackLarge = new ModelBackPackLarge(1.0f);

    public void registerRenderThings() {
        this.Key_Binding();
        RenderingRegistry.registerEntityRenderingHandler(Entityregina.class, (bgz)new RenderRegina());
        RenderingRegistry.registerEntityRenderingHandler(EntityDrezina.class, (bgz)new RenderDrezina());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWideRail.class, (bje)new TileEntityWideRailRenderer());
        KeyBindingRegistry.registerKeyBinding((KeyBindingRegistry.KeyHandler)new DrezinaKeyHandler());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.UbludokAmmo.cp, (IItemRenderer)new RendererUbludokAmmo());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.AK2012Ammo.cp, (IItemRenderer)new RendererAK2012Ammo());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.AKAmmo.cp, (IItemRenderer)new RendererAKAmmo());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.GatlingAmmo.cp, (IItemRenderer)new RendererGatlingAmmo());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.HelsingAmmo.cp, (IItemRenderer)new RendererHelsingAmmo());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.TiharAmmo.cp, (IItemRenderer)new RendererTiharAmmo());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.PodonokAmmo.cp, (IItemRenderer)new RendererPodonokAmmo());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.RpkAmmo.cp, (IItemRenderer)new RenderRpkAmmo());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.RpkAmmoBig.cp, (IItemRenderer)new RenderRpkAmmoBig());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.SaigaAmmo.cp, (IItemRenderer)new RendererSaigaAmmo());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.SaigaAmmoBig.cp, (IItemRenderer)new RendererSaigaAmmoBig());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.AbzacAmmo.cp, (IItemRenderer)new RendererAbzacAmmo());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.AbzacAmmoBig.cp, (IItemRenderer)new RendererAbzacAmmoBig());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.VentilAmmo.cp, (IItemRenderer)new RendererVentilAmmo());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.VentilAmmoBig.cp, (IItemRenderer)new RendererVentilAmmoBig());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.VsvAmmo.cp, (IItemRenderer)new RendererVsvAmmo());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.VyhlopAmmo.cp, (IItemRenderer)new RendererVyhlopAmmo());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.PodonokAmmoBig.cp, (IItemRenderer)new RendererPodonokAmmoBig());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.PrevedAmmo.cp, (IItemRenderer)new RendererPrevedAmmo());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Bullet5x45.cp, (IItemRenderer)new RendererBullet5x45());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Bullet12x70.cp, (IItemRenderer)new RendererBullet12x70());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Bullet12x7.cp, (IItemRenderer)new RendererBullet12x7());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.BulletBolt.cp, (IItemRenderer)new RendererBulletBolt());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Bullet15mm.cp, (IItemRenderer)new RendererBullet15mm());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.BulletMagnum.cp, (IItemRenderer)new RendererBulletMagnum());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Bullet12_7x108.cp, (IItemRenderer)new RendererBullet12_7x108());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Bullet7_62x54.cp, (IItemRenderer)new RendererBullet7_62x54());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Bullet12_7x55.cp, (IItemRenderer)new RendererBullet12_7x55());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Ubludok.cp, (IItemRenderer)new Render_Ubludok());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Helsing.cp, (IItemRenderer)new Render_Helsing());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.AK2012.cp, (IItemRenderer)new Render_AK2012());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.AK.cp, (IItemRenderer)new Render_AK());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.gas_mask.cp, (IItemRenderer)new RenderGasMask());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.HeavyHat.cp, (IItemRenderer)new RenderHeavyHat());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Heavy1Hat.cp, (IItemRenderer)new RenderHeavy1Hat());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.NaziHat.cp, (IItemRenderer)new RenderNaziHat());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.filter.cp, (IItemRenderer)new RenderFilter());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.AKSU.cp, (IItemRenderer)new Render_AKSU());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Ashot.cp, (IItemRenderer)new Render_Ashot());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Duplet.cp, (IItemRenderer)new Render_Duplet());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Grenade.cp, (IItemRenderer)new RenderGrenade());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.GrenadeBig.cp, (IItemRenderer)new RenderGrenadeBig());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Gatling.cp, (IItemRenderer)new Render_Gatling());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Machete.cp, (IItemRenderer)new Render_Machete());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Tihar.cp, (IItemRenderer)new Render_Tihar());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Podonok.cp, (IItemRenderer)new Render_Podonok());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Preved.cp, (IItemRenderer)new Render_Preved());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Railgun.cp, (IItemRenderer)new Render_Railgun());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Revolver.cp, (IItemRenderer)new Render_Revolver());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Rpk.cp, (IItemRenderer)new Render_Rpk());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Saiga.cp, (IItemRenderer)new Render_Saiga());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Abzac.cp, (IItemRenderer)new Render_Abzac());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Uboinik.cp, (IItemRenderer)new Render_Uboinik());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Ventil.cp, (IItemRenderer)new Render_Ventil());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Vsv.cp, (IItemRenderer)new Render_Vsv());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Velopal.cp, (IItemRenderer)new Render_Velopal());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Vyhlop.cp, (IItemRenderer)new Render_Vyhlop());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Ognemet.cp, (IItemRenderer)new Render_Ognemet());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Optica.cp, (IItemRenderer)new RenderOptica());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Butt.cp, (IItemRenderer)new RenderButt());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Barrel.cp, (IItemRenderer)new RenderBarrel());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Laser.cp, (IItemRenderer)new RenderLaser());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.OpticaSmall.cp, (IItemRenderer)new RenderOpticaSmall());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Kolimator.cp, (IItemRenderer)new RenderKolimator());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.UbludokButt.cp, (IItemRenderer)new RenderUbludokButt());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Power.cp, (IItemRenderer)new RenderPower());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Flyashka.cp, (IItemRenderer)new RenderFlyashka());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Med.cp, (IItemRenderer)new RenderMed());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Lamp.cp, (IItemRenderer)new RenderLamp());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.Patron.cp, (IItemRenderer)new RendererBullet5x45());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.GoldMag.cp, (IItemRenderer)new RendererUbludokAmmo());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.AKK.cp, (IItemRenderer)new Render_AKK());
        MinecraftForgeClient.registerItemRenderer((int)ModBase.AKKAmmo.cp, (IItemRenderer)new RendererAKKAmmo());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGermo1.class, (bje)new RendererGermo1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGermo.class, (bje)new RendererGermo());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityShkaf1.class, (bje)new RendererShkaf1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityShkaf.class, (bje)new RendererShkaf());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityShkaf2.class, (bje)new RendererShkaf2());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChair1.class, (bje)new RendererChair1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChair2.class, (bje)new RendererChair2());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLavka1.class, (bje)new RendererLavka1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPolka1.class, (bje)new RendererPolka1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPolka2.class, (bje)new RendererPolka2());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTabl1.class, (bje)new RendererTabl1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVanna.class, (bje)new RendererVanna());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWash.class, (bje)new RendererWash());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTumba1.class, (bje)new RendererTumba1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTumba2.class, (bje)new RendererTumba2());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTumba3.class, (bje)new RendererTumba3());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBarStol.class, (bje)new RendererBarStol());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBed.class, (bje)new RendererBed());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBox1.class, (bje)new RendererBox1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBoxp.class, (bje)new RendererBoxp());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBoxpl.class, (bje)new RendererBoxpl());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDivan1.class, (bje)new RendererDivan1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDivan2.class, (bje)new RendererDivan2());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDivan3.class, (bje)new RendererDivan3());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDivan4.class, (bje)new RendererDivan4());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElBox1.class, (bje)new RendererElBox1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElBox2.class, (bje)new RendererElBox2());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElBox3.class, (bje)new RendererElBox3());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElBox4.class, (bje)new RendererElBox4());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFreez1.class, (bje)new RendererFreez1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFreez2.class, (bje)new RendererFreez2());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGunb1.class, (bje)new RendererGunb1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGunb2.class, (bje)new RendererGunb2());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKab.class, (bje)new RendererKab());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKan.class, (bje)new RendererKan());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLav2.class, (bje)new RendererLav2());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPhone.class, (bje)new RendererPhone());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPlita.class, (bje)new RendererPlita());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRak.class, (bje)new RendererRak());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityShkaf0.class, (bje)new RendererShkaf0());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityShkafm.class, (bje)new RendererShkafm());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStand.class, (bje)new RendererStand());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStelazh1.class, (bje)new RendererStelazh1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStelazh2.class, (bje)new RendererStelazh2());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStelazh3.class, (bje)new RendererStelazh3());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStul1.class, (bje)new RendererStul1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStul2.class, (bje)new RendererStul2());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTul1.class, (bje)new RendererTul1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTul2.class, (bje)new RendererTul2());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityUmi.class, (bje)new RendererUmi());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFp1.class, (bje)new RendererFp1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFp2.class, (bje)new RendererFp2());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTv1.class, (bje)new RendererTv1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKreslo.class, (bje)new RendererKreslo());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDivan.class, (bje)new RendererDivan());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPolka3.class, (bje)new RendererPolka3());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPolka4.class, (bje)new RendererPolka4());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStul.class, (bje)new RendererStul());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStol.class, (bje)new RendererStol());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStolik.class, (bje)new RendererStolik());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPalatka.class, (bje)new RenderPalatka());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTunnel.class, (bje)new RendererTunnel());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTunnel90.class, (bje)new RenderTunnel90());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTunnelBeton1.class, (bje)new RenderTunnelBeton1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTunnelZelezo1.class, (bje)new RenderTunnelZelezo1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTunnelBetonDouble1.class, (bje)new RenderTunnelBetonDouble1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGermo2.class, (bje)new RenderGermo2());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTunnelBetonX.class, (bje)new RenderTunnelBetonX());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTunnelZelezo90.class, (bje)new RenderTunnelZelezo90());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTunnelZelezoDouble.class, (bje)new RenderTunnelZelezoDouble());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTunnelZelezoExit.class, (bje)new RenderTunnelZelezoExit());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTunnelBetonExit.class, (bje)new RenderTunnelBetonExit());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTunnelBetonProvoda.class, (bje)new RenderTunnelBetonProvoda());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTunnelZelezoProvoda.class, (bje)new RenderTunnelZelezoProvoda());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTunnelBetonDerevo.class, (bje)new RenderTunnelBetonDerevo());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTunnelBetonDerevo1.class, (bje)new RenderTunnelBetonDerevo1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTunnelZelezoDerevo.class, (bje)new RenderTunnelZelezoDerevo());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTunnelZelezoDerevo1.class, (bje)new RenderTunnelZelezoDerevo1());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTunnelBetonTrub.class, (bje)new RenderTunnelBetonTrub());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTunnelZelezoTrub.class, (bje)new RenderTunnelZelezoTrub());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTunnelNew.class, (bje)new RenderTunnelNew());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDeath1.class, (bje)new RenderDeath1());
        TickRegistry.registerTickHandler((ITickHandler)new ClientTickHandler(), (Side)Side.CLIENT);
        RenderingRegistry.registerEntityRenderingHandler(Spider.class, (bgz)new RendererSpider((bcd)new ModelSpider(), 0.0f));
        RenderingRegistry.registerEntityRenderingHandler(Nosach.class, (bgz)new RendererNosach((bcd)new ModelNosach(), 0.0f));
        RenderingRegistry.registerEntityRenderingHandler(Kikimora.class, (bgz)new RendererKikimora((bcd)new ModelKikimora(), 0.0f));
        RenderingRegistry.registerEntityRenderingHandler(Dark.class, (bgz)new RendererDark((bcd)new ModelDark(), 0.0f));
        RenderingRegistry.registerEntityRenderingHandler(Biblio.class, (bgz)new RendererBiblio((bcd)new ModelBiblio(), 0.0f));
        RenderingRegistry.registerEntityRenderingHandler(Strazh.class, (bgz)new RendererStrazh((bcd)new ModelStrazh(), 0.0f));
        RenderingRegistry.registerEntityRenderingHandler(Demon.class, (bgz)new RendererDemon((bcd)new ModelDemon(), 0.0f));
        RenderingRegistry.registerEntityRenderingHandler(Rat.class, (bgz)new RendererRat((bcd)new ModelRat(), 0.0f));
        RenderingRegistry.registerEntityRenderingHandler(Bear.class, (bgz)new RendererBear((bcd)new ModelBear(), 0.0f));
        RenderingRegistry.registerEntityRenderingHandler(Aqua_female.class, (bgz)new RendererAquaf((bcd)new ModelAquaf(), 0.0f));
        RenderingRegistry.registerEntityRenderingHandler(Aqua_male.class, (bgz)new RendererAquam((bcd)new ModelAquam(), 0.0f));
        RenderingRegistry.registerEntityRenderingHandler(EntitySpawnerKikimora.class, (bgz)new RendererRat((bcd)new ModelRat(), 0.0f));
        RenderingRegistry.registerEntityRenderingHandler(EntityGrenade1.class, (bgz)new RendererGrenade(1));
        RenderingRegistry.registerEntityRenderingHandler(EntityGrenadeBig.class, (bgz)new RendererGrenadeBig(1));
        RenderingRegistry.registerEntityRenderingHandler(EntityTestDeathFull.class, (bgz)new RenderDeath("texture"));
        RenderingRegistry.registerEntityRenderingHandler(EntityPropulsionFX.class, (bgz)new RenderBullet("fire"));
        RenderingRegistry.registerEntityRenderingHandler(EntityPassiveFire.class, (bgz)new RenderBullet("fire"));
        RenderingRegistry.registerEntityRenderingHandler(EntityFlameBall.class, (bgz)new RenderBullet("flame"));
        RenderingRegistry.registerEntityRenderingHandler(Mobus.class, (bgz)new RendererMobus((bcd)new ModelMobus(), 0.0f));
    }

    private void Key_Binding() {
        ava[] var1 = new ava[]{new ava("\u041f\u043e\u0441\u043c\u043e\u0442\u0440\u0435\u0442\u044c \u043d\u0430 \u0447\u0430\u0441\u044b", 46)};
        boolean[] var2 = new boolean[]{false};
        KeyBindingRegistry.registerKeyBinding((KeyBindingRegistry.KeyHandler)new ClockKeyBind(var1, var2));
        ava[] var3 = new ava[]{new ava("\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430", 19)};
        KeyBindingRegistry.registerKeyBinding((KeyBindingRegistry.KeyHandler)new ReloadKeyBind(var3, var2));
        ava[] var4 = new ava[]{new ava("\u041c\u043e\u0434\u0438\u0444\u0438\u043a\u0430\u0446\u0438\u0438 \u043e\u0440\u0443\u0436\u0438\u044f", 45)};
        KeyBindingRegistry.registerKeyBinding((KeyBindingRegistry.KeyHandler)new ModifiKeyBind(var4, var2));
        ava[] var5 = new ava[]{new ava("\u0424\u043e\u043d\u0430\u0440\u0438\u043a", 33)};
        KeyBindingRegistry.registerKeyBinding((KeyBindingRegistry.KeyHandler)new LampKeyBind(var5, var2));
    }

    public bbz getArmorModel(int var1) {
        switch (var1) {
            case 0: {
                return this.dragonChestplate;
            }
        }
        return this.dragonChestplate;
    }

    public bbz getHimModel(int var1) {
        switch (var1) {
            case 1: {
                return this.himCheest;
            }
        }
        return this.himCheest;
    }

    public bbz getHeavyModel(int var1) {
        switch (var1) {
            case 0: {
                return this.HeavyCheest;
            }
            case 1: {
                return this.HeavyCheest;
            }
            case 2: {
                return this.HeavyCheest;
            }
            case 3: {
                return this.HeavyCheest;
            }
        }
        return this.HeavyCheest;
    }

    public bbz getCleanModel(int var1) {
        switch (var1) {
            case 0: {
                return this.Clean;
            }
            case 1: {
                return this.Clean;
            }
            case 2: {
                return this.Clean;
            }
            case 3: {
                return this.Clean;
            }
        }
        return this.Clean;
    }

    public bbz getNormalModel(int var1) {
        switch (var1) {
            case 0: {
                return this.Normal;
            }
            case 1: {
                return this.Normal;
            }
            case 2: {
                return this.Normal;
            }
            case 3: {
                return this.Normal;
            }
        }
        return this.Normal;
    }

    public bbz getCCleanModel(int var1) {
        switch (var1) {
            case 0: {
                return this.CClean;
            }
            case 1: {
                return this.CClean;
            }
            case 2: {
                return this.CClean;
            }
            case 3: {
                return this.CClean;
            }
        }
        return this.CClean;
    }

    public bbz getSpartaModel(int var1) {
        switch (var1) {
            case 0: {
                return this.Sparta;
            }
            case 1: {
                return this.Sparta;
            }
            case 2: {
                return this.Sparta;
            }
            case 3: {
                return this.Sparta;
            }
        }
        return this.Sparta;
    }

    public bbz getHeavy1Model(int var1) {
        switch (var1) {
            case 0: {
                return this.Heavy1;
            }
            case 1: {
                return this.Heavy1;
            }
            case 2: {
                return this.Heavy1;
            }
            case 3: {
                return this.Heavy1;
            }
        }
        return this.Heavy1;
    }

    public bbz getEasyModel(int var1) {
        switch (var1) {
            case 0: {
                return this.Easy;
            }
            case 1: {
                return this.Easy;
            }
            case 2: {
                return this.Easy;
            }
            case 3: {
                return this.Easy;
            }
        }
        return this.Easy;
    }

    public bbz getKozhModel(int var1) {
        switch (var1) {
            case 0: {
                return this.Kozh;
            }
            case 1: {
                return this.Kozh;
            }
            case 2: {
                return this.Kozh;
            }
            case 3: {
                return this.Kozh;
            }
        }
        return this.Kozh;
    }

    public bbz getSmallBackModel(int var1) {
        switch (var1) {
            case 0: {
                return this.BackPackSmall;
            }
            case 1: {
                return this.BackPackSmall;
            }
            case 2: {
                return this.BackPackSmall;
            }
            case 3: {
                return this.BackPackSmall;
            }
        }
        return this.BackPackSmall;
    }

    public bbz getMediumBackModel(int var1) {
        switch (var1) {
            case 0: {
                return this.BackPackMedium;
            }
            case 1: {
                return this.BackPackMedium;
            }
            case 2: {
                return this.BackPackMedium;
            }
            case 3: {
                return this.BackPackMedium;
            }
        }
        return this.BackPackMedium;
    }

    public bbz getLargeBackModel(int var1) {
        switch (var1) {
            case 0: {
                return this.BackPackLarge;
            }
            case 1: {
                return this.BackPackLarge;
            }
            case 2: {
                return this.BackPackLarge;
            }
            case 3: {
                return this.BackPackLarge;
            }
        }
        return this.BackPackLarge;
    }

    public bbz getHeavyHat(int var1) {
        switch (var1) {
            case 0: {
                return this.HeavyHat;
            }
            case 1: {
                return this.HeavyHat;
            }
            case 2: {
                return this.HeavyHat;
            }
            case 3: {
                return this.HeavyHat;
            }
        }
        return this.HeavyHat;
    }

    public bbz getHeavy1Hat(int var1) {
        switch (var1) {
            case 0: {
                return this.Heavy1Hat;
            }
            case 1: {
                return this.Heavy1Hat;
            }
            case 2: {
                return this.Heavy1Hat;
            }
            case 3: {
                return this.Heavy1Hat;
            }
        }
        return this.Heavy1Hat;
    }

    public bbz getNaziHat(int var1) {
        switch (var1) {
            case 0: {
                return this.NaziHat;
            }
            case 1: {
                return this.NaziHat;
            }
            case 2: {
                return this.NaziHat;
            }
            case 3: {
                return this.NaziHat;
            }
        }
        return this.NaziHat;
    }

    public bbz getNazi(int var1) {
        switch (var1) {
            case 0: {
                return this.Nazi;
            }
            case 1: {
                return this.Nazi;
            }
            case 2: {
                return this.Nazi;
            }
            case 3: {
                return this.Nazi;
            }
        }
        return this.Nazi;
    }

    public bbz getRed(int var1) {
        switch (var1) {
            case 0: {
                return this.Red;
            }
            case 1: {
                return this.Red;
            }
            case 2: {
                return this.Red;
            }
            case 3: {
                return this.Red;
            }
        }
        return this.Red;
    }
}
