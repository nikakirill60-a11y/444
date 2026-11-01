/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.simpleimpl.IMessage
 *  metro.client.gui.GuiUtils
 *  metro.common.network.Network
 *  metro.common.network.packets.ModMessage
 *  metro.common.network.packets.RepairMessage
 *  metro.common.player.ExtendedPlayer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.audio.ISound
 *  net.minecraft.client.audio.PositionedSoundRecord
 *  net.minecraft.client.entity.EntityClientPlayerMP
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.GL11
 */
package metro.client.gui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import metro.client.gui.GuiUtils;
import metro.common.gun.ItemGun;
import metro.common.network.Network;
import metro.common.network.packets.ModMessage;
import metro.common.network.packets.RepairMessage;
import metro.common.player.ExtendedPlayer;
import metro.common.regstration.RegisterGuns;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiMods
extends GuiScreen {
    ResourceLocation head = new ResourceLocation("metro", "gui/mods/ak/bar.png");
    ResourceLocation body = new ResourceLocation("metro", "gui/mods/ak/bar2.png");
    ResourceLocation body3 = new ResourceLocation("metro", "gui/mods/ak/bar3.png");
    ResourceLocation color = new ResourceLocation("ffg", "gui/skills/color.png");
    ResourceLocation mag = new ResourceLocation("metro", "gui/mods/ak/mag.png");
    ResourceLocation mag_gl = new ResourceLocation("metro", "gui/mods/ak/mag_gl.png");
    ResourceLocation part = new ResourceLocation("metro", "gui/mods/ak/part.png");
    ResourceLocation pric = new ResourceLocation("metro", "gui/mods/ak/pric.png");
    ResourceLocation pric_gl = new ResourceLocation("metro", "gui/mods/ak/pric_gl.png");
    ResourceLocation stv = new ResourceLocation("metro", "gui/mods/ak/stv.png");
    ResourceLocation stv0 = new ResourceLocation("metro", "gui/mods/ak/stv0.png");
    ResourceLocation pric2 = new ResourceLocation("metro", "gui/mods/ak/pric2.png");
    ResourceLocation pric2_gl = new ResourceLocation("metro", "gui/mods/ak/pric2_gl.png");
    ResourceLocation pric3 = new ResourceLocation("metro", "gui/mods/ak/pric3.png");
    ResourceLocation laser = new ResourceLocation("metro", "gui/mods/ak/laser.png");
    ResourceLocation pric0 = new ResourceLocation("metro", "gui/mods/ak/pric0.png");
    ResourceLocation ok = new ResourceLocation("metro", "gui/mods/ak/ok.png");
    static ResourceLocation card = new ResourceLocation("ffg", "gui/skills/card/card.png");
    static ResourceLocation card_icon = new ResourceLocation("ffg", "gui/skills/card/card_icon.png");
    boolean onButton;
    public static int id_button;
    float i = 0.8f;
    boolean click = false;
    boolean doe = false;
    int page = 0;
    float y = 0.0f;
    int timer;

    public void func_73866_w_() {
        this.field_146292_n.clear();
    }

    public void func_73863_a(int guiLeft, int guiTop, float par3) {
        super.func_73863_a(guiLeft, guiTop, par3);
        if (this.timer < 10) {
            ++this.timer;
        }
        if (this.timer == 1) {
            Minecraft.func_71410_x().func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_147674_a((ResourceLocation)new ResourceLocation("metro:open"), (float)1.0f));
        }
        EntityClientPlayerMP player = this.field_146297_k.field_71439_g;
        ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer)player);
        if (Mouse.isButtonDown((int)0)) {
            this.click = true;
        } else {
            this.click = false;
            this.doe = false;
        }
        float scaleD = Minecraft.func_71410_x().field_71443_c / Minecraft.func_71410_x().field_71440_d;
        ScaledResolution resolution = new ScaledResolution(Minecraft.func_71410_x(), Minecraft.func_71410_x().field_71443_c, Minecraft.func_71410_x().field_71443_c);
        float scaleDD = scaleD < 2.0f ? scaleD : (scaleD >= 3.0f ? scaleD / (scaleD * 1.0f) : scaleD / (scaleD * 1.0f));
        this.y += (float)(Mouse.getDWheel() / 6);
        if (this.y <= -420.0f) {
            this.y = -420.0f;
        }
        if (this.y > 0.0f) {
            this.y = 0.0f;
        }
        this.i = 0.6f;
        ItemStack heldItem = player.func_70694_bm();
        if (heldItem != null && heldItem.func_77973_b() instanceof ItemGun && heldItem.field_77990_d != null) {
            int im = 0;
            int ix = 0;
            if (player.field_71071_by.func_146028_b(RegisterGuns.res_metall) && player.field_71071_by.func_146028_b(RegisterGuns.res_xim)) {
                for (int i3 = 0; i3 < player.field_71069_bz.func_75138_a().size(); ++i3) {
                    int iq;
                    Slot slot = (Slot)player.field_71069_bz.field_75151_b.get(i3);
                    if (player.field_71069_bz.func_75138_a().get(i3) == null) continue;
                    if (slot.func_75211_c().func_77973_b() == RegisterGuns.res_metall) {
                        for (iq = 0; iq < slot.func_75211_c().field_77994_a; ++iq) {
                            ++im;
                        }
                    }
                    if (slot.func_75211_c().func_77973_b() != RegisterGuns.res_xim) continue;
                    for (iq = 0; iq < slot.func_75211_c().field_77994_a; ++iq) {
                        ++ix;
                    }
                }
            }
            GuiUtils.tesselatorDrawCenter((float)500.0f, (float)60.0f, (float)scaleDD, (int)(this.field_146294_l - this.field_146294_l + 310), (int)(this.field_146295_m + this.field_146295_m - 100), (ResourceLocation)this.body);
            GuiUtils.tesselatorDrawTop((float)700.0f, (float)60.0f, (int)0, (float)scaleDD, (int)this.field_146294_l, (ResourceLocation)this.head);
            this.drawButtonCustom(this.stv0, 1, -this.field_146294_l / 2 + 30, -78.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
            this.drawButtonCustom(this.pric_gl, 2, -this.field_146294_l / 2 + 30, -25.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
            this.drawButtonCustom(this.mag_gl, 3, -this.field_146294_l / 2 + 30, 25.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
            this.drawButtonCustom(this.part, 4, -this.field_146294_l / 2 + 30, 75.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
            if (this.page == 1 && heldItem.func_77973_b() != RegisterGuns.preved && heldItem.func_77973_b() != RegisterGuns.tihar && heldItem.func_77973_b() != RegisterGuns.railgun && heldItem.func_77973_b() != RegisterGuns.helsing) {
                this.drawButtonCustom(this.pric0, 11, -this.field_146294_l / 2 + 81, -78.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
                this.drawButtonCustom(this.stv, 12, -this.field_146294_l / 2 + 130, -78.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
            }
            if (this.page == 2) {
                if (heldItem.func_77973_b() == RegisterGuns.helsing || heldItem.func_77973_b() == RegisterGuns.rpk || heldItem.func_77973_b() == RegisterGuns.ak74u || heldItem.func_77973_b() == RegisterGuns.ak2012 || heldItem.func_77973_b() == RegisterGuns.tihar || heldItem.func_77973_b() == RegisterGuns.ak74 || heldItem.func_77973_b() == RegisterGuns.vsv || heldItem.func_77973_b() == RegisterGuns.ubludok || heldItem.func_77973_b() == RegisterGuns.ventil || heldItem.func_77973_b() == RegisterGuns.preved) {
                    this.drawButtonCustom(this.pric0, 20, -this.field_146294_l / 2 + 81, -25.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
                    this.drawButtonCustom(this.pric2, 21, -this.field_146294_l / 2 + 131, -25.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
                    this.drawButtonCustom(this.pric, 22, -this.field_146294_l / 2 + 181, -25.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
                }
                if (heldItem.func_77973_b() == RegisterGuns.ubo || heldItem.func_77973_b() == RegisterGuns.ashot) {
                    this.drawButtonCustom(this.pric0, 20, -this.field_146294_l / 2 + 81, -25.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
                    this.drawButtonCustom(this.pric, 22, -this.field_146294_l / 2 + 131, -25.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
                }
                if (heldItem.func_77973_b() == RegisterGuns.rev) {
                    this.drawButtonCustom(this.pric0, 20, -this.field_146294_l / 2 + 81, -25.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
                    this.drawButtonCustom(this.pric, 22, -this.field_146294_l / 2 + 131, -25.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
                }
                if (heldItem.func_77973_b() == RegisterGuns.railgun) {
                    this.drawButtonCustom(this.pric0, 20, -this.field_146294_l / 2 + 81, -25.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
                    this.drawButtonCustom(this.pric2, 21, -this.field_146294_l / 2 + 131, -25.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
                }
            }
            if (this.page == 3 && (heldItem.func_77973_b() == RegisterGuns.ak74 || heldItem.func_77973_b() == RegisterGuns.ventil || heldItem.func_77973_b() == RegisterGuns.preved)) {
                this.drawButtonCustom(this.pric0, 30, -this.field_146294_l / 2 + 81, 25.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
                this.drawButtonCustom(this.mag, 31, -this.field_146294_l / 2 + 131, 25.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
            }
            if (this.page == 4) {
                this.drawButtonCustom(this.pric0, 40, -this.field_146294_l / 2 + 81, 75.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
                this.drawButtonCustom(this.laser, 41, -this.field_146294_l / 2 + 131, 75.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
            }
            if (this.page == 5) {
                GuiUtils.tesselatorDrawCenter((float)360.0f, (float)90.0f, (float)scaleDD, (int)this.field_146294_l, (int)(this.field_146295_m - 60), (ResourceLocation)this.body3);
                GuiUtils.drawText((String)"\u041e\u0442\u0440\u0435\u043c\u043e\u043d\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u043e\u0440\u0443\u0436\u0438\u0435 \u0437\u0430: 120 \u043c\u0435\u0442\u0430\u043b\u043b\u043e\u043b\u043e\u043c\u0430 \u0438 40 \u0445\u0438\u043c\u0438\u0438?", (float)0.0f, (float)0.0f, (float)1.5f, (int)(this.field_146294_l - 260), (int)this.field_146295_m, (float)scaleDD);
                this.drawButtonCustom(this.ok, 50, -27.0f, -20.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
                this.drawButtonCustom(this.pric0, 0, 27.0f, -20.0f, 50, 50, scaleD, this.field_146294_l, this.field_146295_m, guiLeft, guiTop, true);
            } else {
                GuiUtils.drawText((String)"[R] \u0420\u0435\u043c\u043e\u043d\u0442 \u043e\u0440\u0443\u0436\u0438\u044f", (float)0.0f, (float)0.0f, (float)1.5f, (int)(this.field_146294_l - 130), (int)(this.field_146295_m + this.field_146295_m + 30), (float)scaleDD);
            }
            ItemGun i = (ItemGun)heldItem.func_77973_b();
            if (Keyboard.isKeyDown((int)19)) {
                this.page = 5;
            }
            if (heldItem.field_77990_d != null) {
                GuiUtils.drawText((String)("\u041c\u0435\u0442\u0430\u043b\u043b\u043e\u043b\u043e\u043c: " + im + "       \u0421\u043e\u0441\u0442\u043e\u044f\u043d\u0438\u0435 \u043e\u0440\u0443\u0436\u0438\u044f: " + heldItem.field_77990_d.func_74762_e("condition") + "/" + i.maxcondition), (float)0.0f, (float)0.0f, (float)1.0f, (int)(this.field_146294_l - this.field_146294_l + 60), (int)(this.field_146295_m + this.field_146295_m - 30), (float)scaleDD);
                GuiUtils.drawText((String)("\u0425\u0438\u043c\u0438\u044f:            " + ix), (float)0.0f, (float)0.0f, (float)1.0f, (int)(this.field_146294_l - this.field_146294_l + 60), (int)(this.field_146295_m + this.field_146295_m), (float)scaleDD);
            }
        }
    }

    protected void actionButton(float scaleD) {
        switch (id_button) {
            case 0: {
                this.page = 0;
                break;
            }
            case 1: {
                this.page = 1;
                break;
            }
            case 2: {
                this.page = 2;
                break;
            }
            case 3: {
                this.page = 3;
                break;
            }
            case 4: {
                this.page = 4;
                break;
            }
            case 11: {
                Network.INSTANCE.sendToServer((IMessage)new ModMessage(0, 1));
                break;
            }
            case 12: {
                Network.INSTANCE.sendToServer((IMessage)new ModMessage(1, 1));
                break;
            }
            case 20: {
                Network.INSTANCE.sendToServer((IMessage)new ModMessage(0, 2));
                break;
            }
            case 21: {
                Network.INSTANCE.sendToServer((IMessage)new ModMessage(1, 2));
                break;
            }
            case 22: {
                Network.INSTANCE.sendToServer((IMessage)new ModMessage(2, 2));
                break;
            }
            case 23: {
                Network.INSTANCE.sendToServer((IMessage)new ModMessage(3, 2));
                break;
            }
            case 30: {
                Network.INSTANCE.sendToServer((IMessage)new ModMessage(0, 3));
                break;
            }
            case 31: {
                Network.INSTANCE.sendToServer((IMessage)new ModMessage(1, 3));
                break;
            }
            case 40: {
                Network.INSTANCE.sendToServer((IMessage)new ModMessage(0, 4));
                break;
            }
            case 41: {
                Network.INSTANCE.sendToServer((IMessage)new ModMessage(1, 4));
                break;
            }
            case 50: {
                Network.INSTANCE.sendToServer((IMessage)new RepairMessage(0, 0));
                break;
            }
        }
    }

    public boolean func_73868_f() {
        return false;
    }

    public void drawButtonCustom(ResourceLocation button_n, int id, float X, float Y, int w, int h, float scaleD, int width, int height, int guiLeft, int guiTop, boolean active) {
        int with_button = (int)((float)w * scaleD);
        int height_button = (int)((float)h * scaleD);
        int start = (int)((float)(width / 2 - with_button / 2) + X * scaleD);
        int end = (int)((float)(width / 2 + with_button / 2) + X * scaleD);
        int startH = (int)((float)(height / 2 - height_button / 2) + Y * scaleD);
        int endH = (int)((float)(height / 2 + height_button / 2) + Y * scaleD);
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glColor4f((float)this.i, (float)this.i, (float)this.i, (float)1.0f);
        GL11.glBlendFunc((int)770, (int)771);
        GuiUtils.drawButton((float)X, (float)Y, (int)start, (int)end, (int)startH, (int)endH, (float)scaleD, (int)width, (int)height, (ResourceLocation)button_n);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
        if (guiLeft >= start && guiLeft <= end && guiTop >= startH && guiTop <= endH) {
            if (this.click && !this.doe) {
                id_button = id;
                Minecraft.func_71410_x().func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_147674_a((ResourceLocation)new ResourceLocation("metro:upgrade"), (float)1.0f));
                this.actionButton(scaleD);
                this.doe = true;
            } else {
                this.onButton = true;
                GL11.glPushMatrix();
                GL11.glEnable((int)3042);
                GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                GL11.glBlendFunc((int)770, (int)771);
                GuiUtils.drawButton((float)X, (float)Y, (int)start, (int)end, (int)startH, (int)endH, (float)scaleD, (int)width, (int)height, (ResourceLocation)button_n);
                GL11.glDisable((int)3042);
                GL11.glPopMatrix();
            }
        } else {
            this.onButton = false;
        }
    }
}
