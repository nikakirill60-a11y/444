/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.gameevent.TickEvent$Phase
 *  cpw.mods.fml.common.gameevent.TickEvent$ServerTickEvent
 *  cpw.mods.fml.common.network.simpleimpl.IMessage
 *  metro.common.EntityBullet
 *  metro.common.gun.ItemGun
 *  metro.common.network.Network
 *  metro.common.network.packets.DeathMessage
 *  metro.common.network.packets.RecoilMessage
 *  metro.common.network.packets.VisualPacket
 *  metro.common.player.ExtendedPlayer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.ChatComponentText
 *  net.minecraft.util.IChatComponent
 *  net.minecraft.world.World
 */
package metro.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import java.util.Random;
import metro.common.EntityBullet;
import metro.common.gun.ItemGun;
import metro.common.network.Network;
import metro.common.network.packets.DeathMessage;
import metro.common.network.packets.RecoilMessage;
import metro.common.network.packets.VisualPacket;
import metro.common.player.ExtendedPlayer;
import metro.common.regstration.RegisterGuns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class TickHundlerServer {
    int ticks;
    float damage = 1.0f;

    TickHundlerServer() {
    }

    @SubscribeEvent
    public void tickServer(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            this.ticks = this.ticks <= 2000 ? ++this.ticks : 0;
            for (Object o : MinecraftServer.func_71276_C().func_130014_f_().field_73010_i) {
                EntityPlayer player = (EntityPlayer)o;
                if (player == null) continue;
                ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer)player);
                World world = player.field_70170_p;
                ItemStack heldItem = player.func_70694_bm();
                if (!props.isServerSide()) continue;
                if (this.ticks % 20 == 0 && props.getX() == 0.0f && props.getY() == 0.0f && props.getZ() == 0.0f) {
                    System.out.println("\u0418\u0433\u0440\u043e\u043a\u0443 - " + player.func_70005_c_() + " \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0430 \u0431\u0430\u0437\u043e\u0432\u0430\u044f \u0442\u043e\u0447\u043a\u0430 \u0440\u0435\u0441\u043f\u0430\u0432\u043d\u0430.");
                    props.setX(-475.5f);
                    props.setX(85.0f);
                    props.setX(646.5f);
                }
                if (this.ticks % 10 == 0) {
                    props.setHealth(player.func_110143_aJ() - 900.0f);
                }
                if (player.func_110143_aJ() < 900.0f) {
                    this.setDeath(player);
                }
                if (player.func_110143_aJ() < 800.0f) {
                    this.setHealth(player);
                }
                if (player.func_71024_bL().func_75116_a() > 10 && player.func_110143_aJ() < 1000.0f && this.ticks % 80 == 0) {
                    player.func_70606_j(player.func_110143_aJ() + 5.0f);
                }
                if (heldItem != null && heldItem.func_77973_b() instanceof ItemGun && heldItem.field_77990_d != null && heldItem.field_77990_d.func_74767_n("isReload")) {
                    int finalammo = 0;
                    Item ammo = null;
                    ItemGun gun = (ItemGun)heldItem.func_77973_b();
                    int idammo = heldItem.field_77990_d.func_74762_e("idammo");
                    if (idammo == 1) {
                        ammo = RegisterGuns.ammo5x45;
                    }
                    if (idammo == 2) {
                        ammo = RegisterGuns.ammo12x70;
                    }
                    if (idammo == 3) {
                        ammo = RegisterGuns.ammo44;
                    }
                    if (idammo == 4) {
                        ammo = RegisterGuns.ammo762;
                    }
                    if (idammo == 5) {
                        ammo = RegisterGuns.ammo145;
                    }
                    if (idammo == 6) {
                        ammo = RegisterGuns.ammoball;
                    }
                    if (idammo == 7) {
                        ammo = RegisterGuns.ammo_bolts;
                    }
                    if (player.field_71071_by.func_146028_b(ammo)) {
                        heldItem.field_77990_d.func_74757_a("isFire", false);
                        if (heldItem.field_77990_d.func_74762_e("curammo") == heldItem.field_77990_d.func_74762_e("maxammo")) {
                            heldItem.field_77990_d.func_74757_a("isReload", false);
                        } else if (heldItem.field_77990_d.func_74762_e("reloadtimer") >= gun.tickReloadMax) {
                            int i;
                            for (i = 0; i < heldItem.field_77990_d.func_74762_e("curammo"); ++i) {
                                player.field_71071_by.func_70441_a(new ItemStack(ammo));
                            }
                            heldItem.field_77990_d.func_74768_a("curammo", 0);
                            for (i = 0; i < heldItem.field_77990_d.func_74762_e("maxammo"); ++i) {
                                if (!player.field_71071_by.func_146028_b(ammo)) continue;
                                player.field_71071_by.func_146026_a(ammo);
                                ++finalammo;
                            }
                            heldItem.field_77990_d.func_74768_a("reloadtimer", 0);
                            heldItem.field_77990_d.func_74768_a("curammo", finalammo);
                            heldItem.field_77990_d.func_74757_a("isReload", false);
                            heldItem.field_77990_d.func_74757_a("isFire", false);
                        } else {
                            if (heldItem.field_77990_d.func_74762_e("reloadtimer") == 0) {
                                world.func_72956_a((Entity)player, "metro:" + gun.soundreload, 1.0f, 1.0f);
                            }
                            heldItem.field_77990_d.func_74768_a("reloadtimer", heldItem.field_77990_d.func_74762_e("reloadtimer") + 1);
                        }
                    } else {
                        player.func_145747_a((IChatComponent)new ChatComponentText("\u041d\u0435\u0442 \u043f\u0430\u0442\u0440\u043e\u043d\u043e\u0432!"));
                        heldItem.field_77990_d.func_74757_a("isReload", false);
                    }
                }
                if (heldItem == null || !(heldItem.func_77973_b() instanceof ItemGun) || heldItem.field_77990_d == null) continue;
                if (heldItem.field_77990_d.func_74767_n("isFire")) {
                    ItemGun gun = (ItemGun)heldItem.func_77973_b();
                    if (!heldItem.field_77990_d.func_74767_n("isAir")) {
                        if (heldItem.field_77990_d.func_74762_e("firemod") == 1 && !heldItem.field_77990_d.func_74767_n("isReload")) {
                            if (heldItem.field_77990_d.func_74762_e("shoottimedown") >= gun.shoottimedown) {
                                if (heldItem.field_77990_d.func_74762_e("curammo") > 0 && heldItem.field_77990_d.func_74762_e("condition") > 0) {
                                    Network.INSTANCE.sendTo((IMessage)new VisualPacket(true, false, false, 0), (EntityPlayerMP)player);
                                    double damage = gun.damage;
                                    if (heldItem.field_77990_d.func_74762_e("condition") < heldItem.field_77990_d.func_74762_e("maxcondition") / 2) {
                                        damage -= damage / 4.0;
                                    }
                                    if (heldItem.field_77990_d.func_74762_e("condition") < heldItem.field_77990_d.func_74762_e("maxcondition") / 4) {
                                        damage /= 2.0;
                                    }
                                    if (heldItem.field_77990_d.func_74762_e("modStvol") == 0) {
                                        world.func_72956_a((Entity)player, "metro:" + gun.soundshoot, 10.0f, 1.0f);
                                    }
                                    if (heldItem.field_77990_d.func_74762_e("modStvol") == 1) {
                                        damage -= 2.0;
                                        world.func_72956_a((Entity)player, "metro:slice", 2.0f, 1.0f);
                                    }
                                    heldItem.field_77990_d.func_74768_a("shoottimedown", 0);
                                    heldItem.field_77990_d.func_74768_a("condition", heldItem.field_77990_d.func_74762_e("condition") - 1);
                                    heldItem.field_77990_d.func_74768_a("curammo", heldItem.field_77990_d.func_74762_e("curammo") - 1);
                                    player.field_70170_p.func_72838_d((Entity)new EntityBullet(world, player, damage, heldItem.field_77990_d.func_74760_g("accuracity")));
                                    if (props.isServerSide()) {
                                        Network.INSTANCE.sendTo((IMessage)new RecoilMessage(gun.recoil, gun.recoilgor), (EntityPlayerMP)player);
                                    }
                                    heldItem.field_77990_d.func_74757_a("isFire", false);
                                }
                            } else {
                                heldItem.field_77990_d.func_74768_a("shoottimedown", heldItem.field_77990_d.func_74762_e("shoottimedown") + 1);
                            }
                        }
                        if (heldItem.field_77990_d.func_74762_e("firemod") == 2 && !heldItem.field_77990_d.func_74767_n("isReload") && heldItem.field_77990_d.func_74762_e("curammo") > 0) {
                            if (heldItem.field_77990_d.func_74762_e("shoottimedown") >= gun.shoottimedown) {
                                if (heldItem.field_77990_d.func_74762_e("condition") > 0) {
                                    float damage = (float)gun.damage;
                                    if (heldItem.field_77990_d.func_74762_e("condition") < heldItem.field_77990_d.func_74762_e("maxcondition") / 2) {
                                        damage -= damage / 4.0f;
                                    }
                                    if (heldItem.field_77990_d.func_74762_e("condition") < heldItem.field_77990_d.func_74762_e("maxcondition") / 4) {
                                        damage /= 2.0f;
                                    }
                                    if (heldItem.field_77990_d.func_74762_e("modStvol") == 0) {
                                        world.func_72956_a((Entity)player, "metro:" + gun.soundshoot, 10.0f, 1.0f);
                                    }
                                    if (heldItem.field_77990_d.func_74762_e("modStvol") == 1) {
                                        world.func_72956_a((Entity)player, "metro:slice", 2.0f, 1.0f);
                                    }
                                    heldItem.field_77990_d.func_74768_a("shoottimedown", 0);
                                    heldItem.field_77990_d.func_74768_a("condition", heldItem.field_77990_d.func_74762_e("condition") - 1);
                                    heldItem.field_77990_d.func_74768_a("curammo", heldItem.field_77990_d.func_74762_e("curammo") - 1);
                                    Network.INSTANCE.sendTo((IMessage)new VisualPacket(true, false, false, 0), (EntityPlayerMP)player);
                                    player.field_70170_p.func_72838_d((Entity)new EntityBullet(world, player, (double)damage, heldItem.field_77990_d.func_74760_g("accuracity")));
                                    heldItem.field_77990_d.func_74768_a("shoottimedown", 0);
                                    if (props.isServerSide()) {
                                        Network.INSTANCE.sendTo((IMessage)new RecoilMessage(gun.recoil, gun.recoilgor), (EntityPlayerMP)player);
                                    }
                                    heldItem.field_77990_d.func_74757_a("isFire", false);
                                }
                            } else {
                                heldItem.field_77990_d.func_74768_a("shoottimedown", heldItem.field_77990_d.func_74762_e("shoottimedown") + 1);
                            }
                        }
                        if (heldItem.field_77990_d.func_74762_e("firemod") == 3 && !heldItem.field_77990_d.func_74767_n("isReload") && heldItem.field_77990_d.func_74762_e("curammo") > 0) {
                            if (heldItem.field_77990_d.func_74762_e("shoottimedown") >= gun.shoottimedown) {
                                if (heldItem.field_77990_d.func_74762_e("condition") > 0) {
                                    float damage = (float)gun.damage;
                                    if (heldItem.field_77990_d.func_74762_e("condition") < heldItem.field_77990_d.func_74762_e("maxcondition") / 2) {
                                        damage -= damage / 4.0f;
                                    }
                                    if (heldItem.field_77990_d.func_74762_e("condition") < heldItem.field_77990_d.func_74762_e("maxcondition") / 4) {
                                        damage /= 2.0f;
                                    }
                                    if (heldItem.field_77990_d.func_74762_e("modStvol") == 0) {
                                        world.func_72956_a((Entity)player, "metro:" + gun.soundshoot, 10.0f, 1.0f);
                                    }
                                    if (heldItem.field_77990_d.func_74762_e("modStvol") == 1) {
                                        world.func_72956_a((Entity)player, "metro:slices", 2.0f, 1.0f);
                                    }
                                    heldItem.field_77990_d.func_74768_a("shoottimedown", 0);
                                    heldItem.field_77990_d.func_74768_a("condition", heldItem.field_77990_d.func_74762_e("condition") - 1);
                                    heldItem.field_77990_d.func_74768_a("curammo", heldItem.field_77990_d.func_74762_e("curammo") - 1);
                                    Network.INSTANCE.sendTo((IMessage)new VisualPacket(true, false, false, 0), (EntityPlayerMP)player);
                                    player.field_70170_p.func_72838_d((Entity)new EntityBullet(world, player, (double)damage, new Random().nextFloat() * 5.0f + 3.0f));
                                    player.field_70170_p.func_72838_d((Entity)new EntityBullet(world, player, (double)damage, new Random().nextFloat() * 9.0f + 2.0f));
                                    player.field_70170_p.func_72838_d((Entity)new EntityBullet(world, player, (double)damage, new Random().nextFloat() * 4.0f + 5.0f));
                                    player.field_70170_p.func_72838_d((Entity)new EntityBullet(world, player, (double)damage, new Random().nextFloat() * 6.0f + 4.0f));
                                    player.field_70170_p.func_72838_d((Entity)new EntityBullet(world, player, (double)damage, new Random().nextFloat() * 6.0f + 4.0f));
                                    player.field_70170_p.func_72838_d((Entity)new EntityBullet(world, player, (double)damage, new Random().nextFloat() * 14.0f + 1.0f));
                                    heldItem.field_77990_d.func_74768_a("shoottimedown", 0);
                                    if (props.isServerSide()) {
                                        Network.INSTANCE.sendTo((IMessage)new RecoilMessage(gun.recoil, gun.recoilgor), (EntityPlayerMP)player);
                                    }
                                    heldItem.field_77990_d.func_74757_a("isFire", false);
                                }
                            } else {
                                heldItem.field_77990_d.func_74768_a("shoottimedown", heldItem.field_77990_d.func_74762_e("shoottimedown") + 1);
                            }
                        }
                    }
                    if (!heldItem.field_77990_d.func_74767_n("isAir")) continue;
                    float dam = 0.0f;
                    dam = heldItem.field_77990_d.func_74762_e("davl") >= 20 ? (float)gun.damage : (heldItem.field_77990_d.func_74762_e("davl") >= 15 ? (float)((double)((float)gun.damage) - gun.damage / 3.0) : (heldItem.field_77990_d.func_74762_e("davl") >= 8 ? (float)gun.damage / 2.0f : (float)gun.damage / 3.0f));
                    if (heldItem.field_77990_d.func_74762_e("firemod") == 1) {
                        if (heldItem.field_77990_d.func_74762_e("davl") > 0) {
                            if (!heldItem.field_77990_d.func_74767_n("isReload")) {
                                if (heldItem.field_77990_d.func_74762_e("shoottimedown") >= gun.shoottimedown) {
                                    if (heldItem.field_77990_d.func_74762_e("curammo") > 0 && heldItem.field_77990_d.func_74762_e("condition") > 0) {
                                        Network.INSTANCE.sendTo((IMessage)new VisualPacket(true, false, false, 0), (EntityPlayerMP)player);
                                        world.func_72956_a((Entity)player, "metro:" + gun.soundshoot, 10.0f, 1.0f);
                                        heldItem.field_77990_d.func_74768_a("davl", heldItem.field_77990_d.func_74762_e("davl") - 1);
                                        heldItem.field_77990_d.func_74768_a("condition", heldItem.field_77990_d.func_74762_e("condition") - 1);
                                        heldItem.field_77990_d.func_74768_a("curammo", heldItem.field_77990_d.func_74762_e("curammo") - 1);
                                        player.field_70170_p.func_72838_d((Entity)new EntityBullet(world, player, (double)dam, heldItem.field_77990_d.func_74760_g("accuracity")));
                                        heldItem.field_77990_d.func_74768_a("shoottimedown", 0);
                                        if (props.isServerSide()) {
                                            Network.INSTANCE.sendTo((IMessage)new RecoilMessage(gun.recoil, gun.recoilgor), (EntityPlayerMP)player);
                                        }
                                        heldItem.field_77990_d.func_74757_a("isFire", false);
                                    }
                                } else {
                                    heldItem.field_77990_d.func_74768_a("shoottimedown", heldItem.field_77990_d.func_74762_e("shoottimedown") + 1);
                                }
                            }
                        } else {
                            heldItem.field_77990_d.func_74757_a("isFire", false);
                        }
                    }
                    if (heldItem.field_77990_d.func_74762_e("firemod") != 2) continue;
                    if (heldItem.field_77990_d.func_74762_e("davl") > 0) {
                        if (heldItem.field_77990_d.func_74767_n("isReload") || heldItem.field_77990_d.func_74762_e("curammo") <= 0) continue;
                        if (heldItem.field_77990_d.func_74762_e("shoottimedown") >= gun.shoottimedown) {
                            if (heldItem.field_77990_d.func_74762_e("condition") <= 0) continue;
                            world.func_72956_a((Entity)player, "metro:" + gun.soundshoot, 10.0f, 1.0f);
                            heldItem.field_77990_d.func_74768_a("davl", heldItem.field_77990_d.func_74762_e("davl") - 1);
                            heldItem.field_77990_d.func_74768_a("condition", heldItem.field_77990_d.func_74762_e("condition") - 1);
                            heldItem.field_77990_d.func_74768_a("curammo", heldItem.field_77990_d.func_74762_e("curammo") - 1);
                            Network.INSTANCE.sendTo((IMessage)new VisualPacket(true, false, false, 0), (EntityPlayerMP)player);
                            player.field_70170_p.func_72838_d((Entity)new EntityBullet(world, player, (double)dam, heldItem.field_77990_d.func_74760_g("accuracity")));
                            heldItem.field_77990_d.func_74768_a("shoottimedown", 0);
                            if (props.isServerSide()) {
                                Network.INSTANCE.sendTo((IMessage)new RecoilMessage(gun.recoil, gun.recoilgor), (EntityPlayerMP)player);
                            }
                            heldItem.field_77990_d.func_74757_a("isFire", false);
                            continue;
                        }
                        heldItem.field_77990_d.func_74768_a("shoottimedown", heldItem.field_77990_d.func_74762_e("shoottimedown") + 1);
                        continue;
                    }
                    heldItem.field_77990_d.func_74757_a("isFire", false);
                    continue;
                }
                ItemGun gun = (ItemGun)heldItem.func_77973_b();
                if (heldItem.field_77990_d.func_74762_e("shoottimedown") > heldItem.field_77990_d.func_74762_e("maxshoottimedown")) continue;
                if (heldItem.field_77990_d.func_74762_e("firemod") == 1) {
                    heldItem.field_77990_d.func_74768_a("shoottimedown", gun.shoottimedown);
                }
                if (heldItem.field_77990_d.func_74762_e("firemod") == 2) {
                    heldItem.field_77990_d.func_74768_a("shoottimedown", heldItem.field_77990_d.func_74762_e("shoottimedown") + 1);
                }
                if (heldItem.field_77990_d.func_74762_e("firemod") != 3) continue;
                heldItem.field_77990_d.func_74768_a("shoottimedown", heldItem.field_77990_d.func_74762_e("shoottimedown") + 1);
            }
        }
    }

    public void setHealth(EntityPlayer p) {
        p.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(1000.0);
        p.func_70606_j(1000.0f);
    }

    private void setDeath(EntityPlayer player) {
        ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer)player);
        int itemcont = 0;
        int itemcontsave = 0;
        if (props.isServerSide()) {
            Network.INSTANCE.sendTo((IMessage)new DeathMessage(1, "", itemcont, itemcontsave), (EntityPlayerMP)player);
        }
        itemcont = 0;
        itemcontsave = 0;
        player.func_71024_bL().func_75122_a(20, 20.0f);
        player.func_70606_j(1000.0f);
        props.setHealth(100.0f);
    }
}
