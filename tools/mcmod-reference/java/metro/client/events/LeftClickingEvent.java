/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.gameevent.TickEvent$ClientTickEvent
 *  cpw.mods.fml.common.network.simpleimpl.IMessage
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  metro.common.network.Network
 *  metro.common.network.packets.AirPnevmMessage
 *  metro.common.network.packets.GasMaskMessage
 *  metro.common.network.packets.RadMessage
 *  metro.common.network.packets.ReloadBMessage
 *  metro.common.network.packets.ShootMessage
 *  metro.common.player.ExtendedPlayer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.audio.ISound
 *  net.minecraft.client.audio.PositionedSoundRecord
 *  net.minecraft.client.entity.EntityClientPlayerMP
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.client.settings.GameSettings
 *  net.minecraft.client.settings.KeyBinding
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  org.lwjgl.input.Keyboard
 */
package metro.client.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import metro.client.events.EventsClient;
import metro.common.Main;
import metro.common.gun.ItemGun;
import metro.common.network.Network;
import metro.common.network.packets.AirPnevmMessage;
import metro.common.network.packets.GasMaskMessage;
import metro.common.network.packets.RadMessage;
import metro.common.network.packets.ReloadBMessage;
import metro.common.network.packets.ShootMessage;
import metro.common.player.ExtendedPlayer;
import metro.common.regstration.RegisterArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public class LeftClickingEvent {
    public static boolean isRad;
    int ticks = 0;
    float rotate_pre;
    int shoot;
    static boolean RightRotate;
    static boolean LeftRotate;
    boolean jump;
    int jump_timer;
    int key_i;
    int key_i2;

    @SideOnly(value=Side.CLIENT)
    @SubscribeEvent
    public void leftClickTicks(TickEvent.ClientTickEvent event) {
        EntityClientPlayerMP player = Minecraft.func_71410_x().field_71439_g;
        WorldClient world = Minecraft.func_71410_x().field_71441_e;
        this.ticks = this.ticks <= 2000 ? ++this.ticks : 0;
        if (world != null) {
            ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer)player);
            int h_world = Main.h_rad;
            Item item = RegisterArmor.gasmask;
            float health = player.func_110143_aJ();
            float damage_rad = 2.0f;
            Item itemg2 = null;
            Item item22 = null;
            if (player.field_70163_u > (double)h_world || isRad) {
                if (player.field_71071_by.func_70440_f(3) != null) {
                    itemg2 = player.field_71071_by.func_70440_f(3).func_77973_b();
                }
                if (player.field_71071_by.func_70440_f(2) != null) {
                    item22 = player.field_71071_by.func_70440_f(2).func_77973_b();
                }
                if (!player.field_71075_bZ.field_75098_d) {
                    if (itemg2 == RegisterArmor.gasmask || item22 == RegisterArmor.hanza || item22 == RegisterArmor.himza || item22 == RegisterArmor.officer_ozk || item22 == RegisterArmor.ozk || item22 == RegisterArmor.povstanec || item22 == RegisterArmor.psh1b || item22 == RegisterArmor.Scout_armor || item22 == RegisterArmor.sverchok_mp6 || item22 == RegisterArmor.zapal || item22 == RegisterArmor.prorok || item22 == RegisterArmor.reih || item22 == RegisterArmor.mgu) {
                        if (props.getGasTick() > 0) {
                            if (this.ticks % 100 == 0) {
                                Minecraft.func_71410_x().func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_147674_a((ResourceLocation)new ResourceLocation("metro:breath"), (float)1.0f));
                            }
                            if (this.ticks % 40 == 0 && props.getGasTick() == 60) {
                                Minecraft.func_71410_x().func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_147674_a((ResourceLocation)new ResourceLocation("metro:clock"), (float)1.0f));
                            }
                        } else {
                            if (this.ticks % 40 == 0) {
                                Network.INSTANCE.sendToServer((IMessage)new RadMessage(1));
                            }
                            if (this.ticks % 80 == 0) {
                                Minecraft.func_71410_x().func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_147674_a((ResourceLocation)new ResourceLocation("metro:nogas"), (float)1.0f));
                            }
                            if (this.ticks % 30 == 0) {
                                Minecraft.func_71410_x().func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_147674_a((ResourceLocation)new ResourceLocation("metro:geiger_long"), (float)1.0f));
                            }
                        }
                    } else {
                        if (this.ticks % 40 == 0) {
                            Network.INSTANCE.sendToServer((IMessage)new RadMessage(1));
                        }
                        if (this.ticks % 80 == 0) {
                            Minecraft.func_71410_x().func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_147674_a((ResourceLocation)new ResourceLocation("metro:nogas"), (float)1.0f));
                        }
                        if (this.ticks % 30 == 0) {
                            Minecraft.func_71410_x().func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_147674_a((ResourceLocation)new ResourceLocation("metro:geiger_long"), (float)1.0f));
                        }
                    }
                }
            }
            if (Minecraft.func_71410_x().field_71439_g.func_70090_H()) {
                if (player.field_71071_by.func_70440_f(2) != null) {
                    item22 = player.field_71071_by.func_70440_f(2).func_77973_b();
                }
                if (!player.field_71075_bZ.field_75098_d && item22 != RegisterArmor.himza && item22 != RegisterArmor.ozk) {
                    if (this.ticks % 40 == 0) {
                        Network.INSTANCE.sendToServer((IMessage)new RadMessage(3));
                    }
                    if (this.ticks % 20 == 0) {
                        Minecraft.func_71410_x().func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_147674_a((ResourceLocation)new ResourceLocation("metro:geiger_long"), (float)1.0f));
                    }
                }
            }
        }
        if (Minecraft.func_71410_x().field_71462_r == null) {
            Minecraft.func_71410_x().field_71474_y.field_151441_H = true;
            Minecraft.func_71410_x().field_71474_y.field_92117_D = false;
            ItemStack heldItem = player.func_70694_bm();
            if (heldItem != null && heldItem.func_77973_b() instanceof ItemGun && heldItem.field_77990_d != null && GameSettings.func_100015_a((KeyBinding)Minecraft.func_71410_x().field_71474_y.field_74312_F) && !player.func_70051_ag()) {
                if (heldItem.field_77990_d.func_74762_e("firemod") == 1) {
                    Network.INSTANCE.sendToServer((IMessage)new ShootMessage(0));
                }
                if (heldItem.field_77990_d.func_74762_e("firemod") == 2 || heldItem.field_77990_d.func_74762_e("firemod") == 3) {
                    ++this.shoot;
                }
                if (this.shoot == 1) {
                    Network.INSTANCE.sendToServer((IMessage)new ShootMessage());
                }
            } else {
                this.shoot = 0;
            }
            if (heldItem != null && heldItem.func_77973_b() instanceof ItemGun && heldItem.field_77990_d != null && Keyboard.isKeyDown((int)19) && !EventsClient.guiUpgrade) {
                Network.INSTANCE.sendToServer((IMessage)new ReloadBMessage());
            }
            if (heldItem != null && heldItem.func_77973_b() instanceof ItemGun && heldItem.field_77990_d != null && Keyboard.isKeyDown((int)33) && !EventsClient.guiUpgrade && heldItem.field_77990_d.func_74767_n("isAir") && this.ticks % 60 == 0) {
                Network.INSTANCE.sendToServer((IMessage)new AirPnevmMessage(0));
            }
        }
        if (Minecraft.func_71410_x().field_71462_r == null) {
            if (Keyboard.isKeyDown((int)24) && !Minecraft.func_71410_x().field_71439_g.func_70051_ag()) {
                player.openGui((Object)Main.instance, 1, (World)world, (int)player.field_70165_t, (int)player.field_70163_u, (int)player.field_70161_v);
            }
            Item itemg = null;
            Item item2 = null;
            if (player.field_71071_by.func_70440_f(3) != null) {
                itemg = player.field_71071_by.func_70440_f(3).func_77973_b();
            }
            if (player.field_71071_by.func_70440_f(2) != null) {
                item2 = player.field_71071_by.func_70440_f(2).func_77973_b();
            }
            if (itemg == RegisterArmor.gasmask || item2 == RegisterArmor.hanza || item2 == RegisterArmor.himza || item2 == RegisterArmor.officer_ozk || item2 == RegisterArmor.ozk || item2 == RegisterArmor.povstanec || item2 == RegisterArmor.psh1b || item2 == RegisterArmor.Scout_armor || item2 == RegisterArmor.sverchok_mp6 || item2 == RegisterArmor.zapal || item2 == RegisterArmor.prorok || item2 == RegisterArmor.reih || item2 == RegisterArmor.mgu) {
                if (Keyboard.isKeyDown((int)21)) {
                    ++this.key_i2;
                    if (this.key_i2 == 1) {
                        Network.INSTANCE.sendToServer((IMessage)new GasMaskMessage(false, true));
                    }
                } else {
                    this.key_i2 = 0;
                }
            }
            if (Keyboard.isKeyDown((int)34)) {
                ++this.key_i;
                if (this.key_i == 1) {
                    Network.INSTANCE.sendToServer((IMessage)new GasMaskMessage(true, false));
                }
            } else {
                this.key_i = 0;
            }
        }
    }
}
