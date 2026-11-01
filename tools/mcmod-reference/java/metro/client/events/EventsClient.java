/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  metro.client.gui.GuiUtils
 *  metro.client.gui.RenderingHelper
 *  metro.common.player.ExtendedPlayer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.audio.ISound
 *  net.minecraft.client.audio.PositionedSoundRecord
 *  net.minecraft.client.entity.EntityClientPlayerMP
 *  net.minecraft.client.gui.GuiMainMenu
 *  net.minecraft.client.gui.GuiOptions
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  net.minecraftforge.client.event.DrawBlockHighlightEvent
 *  net.minecraftforge.client.event.GuiOpenEvent
 *  net.minecraftforge.client.event.RenderGameOverlayEvent
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$ElementType
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.GL11
 *  org.newdawn.slick.Color
 *  org.newdawn.slick.UnicodeFont
 */
package metro.client.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import metro.client.events.LeftClickingEvent;
import metro.client.gui.GuiMods;
import metro.client.gui.GuiUtils;
import metro.client.gui.RenderingHelper;
import metro.common.gun.ItemGun;
import metro.common.player.ExtendedPlayer;
import metro.common.regstration.RegisterArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;

public class EventsClient {
    Minecraft mc = Minecraft.func_71410_x();
    public static final ResourceLocation rad1 = new ResourceLocation("metro", "gui/rad/rad1.png");
    public static final ResourceLocation rad2 = new ResourceLocation("metro", "gui/rad/rad2.png");
    public static final ResourceLocation rad3 = new ResourceLocation("metro", "gui/rad/rad3.png");
    public static final ResourceLocation gasmask = new ResourceLocation("metro", "gui/rad/gasmask.png");
    public static final ResourceLocation jager = new ResourceLocation("metro", "gui/jager.png");
    public static final ResourceLocation hearth = new ResourceLocation("metro", "gui/hearth.png");
    public static final ResourceLocation food = new ResourceLocation("metro", "gui/food.png");
    public static final ResourceLocation hit = new ResourceLocation("metro", "gui/hitmark.png");
    ResourceLocation color = new ResourceLocation("metro", "gui/color.png");
    float c;
    boolean z;
    public static int hitmark;
    int ticks;
    int KeyTick;
    float fov;
    int h_timer;
    int aim_tick1;
    int aim_tick0;
    public static boolean guiUpgrade;
    public static Entity whoKill;
    public static String nameKiller;
    public static int itemcont;
    public static int itemcontsave;
    private boolean s = false;

    @SubscribeEvent
    public void onOpenGui(DrawBlockHighlightEvent event) {
        if (!Minecraft.func_71410_x().field_71439_g.field_71075_bZ.field_75098_d) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onOpenGui(GuiOpenEvent event) {
        if (event.gui instanceof GuiMainMenu) {
            if (!this.s) {
                Minecraft.func_71410_x().func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_147674_a((ResourceLocation)new ResourceLocation("metro:music"), (float)1.0f));
            }
            this.s = true;
            this.fov = this.mc.field_71474_y.field_74334_X;
        }
        if (event.gui instanceof GuiOptions) {
            this.fov = this.mc.field_71474_y.field_74334_X;
        }
        guiUpgrade = event.gui instanceof GuiMods;
    }

    @SubscribeEvent
    public void drawGui(RenderGameOverlayEvent event) {
        if (event.type == RenderGameOverlayEvent.ElementType.HEALTH) {
            event.setCanceled(true);
        }
        if (event.type == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            event.setCanceled(true);
        }
        if (event.type == RenderGameOverlayEvent.ElementType.FOOD) {
            event.setCanceled(true);
        }
        if (event.type == RenderGameOverlayEvent.ElementType.ARMOR) {
            event.setCanceled(true);
        }
        if ((guiUpgrade || Mouse.isButtonDown((int)1) || !Minecraft.func_71410_x().field_71439_g.field_71075_bZ.field_75098_d) && event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            event.setCanceled(true);
        }
        EntityClientPlayerMP player = Minecraft.func_71410_x().field_71439_g;
        ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer)player);
        World world = player.field_70170_p;
        if (!player.field_71075_bZ.field_75098_d && this.mc.field_71439_g.field_71071_by.field_70461_c > 3) {
            this.mc.field_71439_g.field_71071_by.field_70461_c = 0;
        }
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        Item itemg = null;
        Item item2 = null;
        if (player.field_71071_by.func_70440_f(3) != null) {
            itemg = player.field_71071_by.func_70440_f(3).func_77973_b();
        }
        if (player.field_71071_by.func_70440_f(2) != null) {
            item2 = player.field_71071_by.func_70440_f(2).func_77973_b();
        }
        if (Minecraft.func_71410_x().field_71474_y.field_74320_O == 0) {
            GL11.glPushMatrix();
            GL11.glEnable((int)3042);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)0.35f);
            if (item2 == RegisterArmor.juggernaut) {
                GuiUtils.tesselatorDrawFull((int)event.resolution.func_78326_a(), (int)event.resolution.func_78328_b(), (ResourceLocation)jager);
            }
            GL11.glDisable((int)3042);
            GL11.glPopMatrix();
        }
        if (itemg == RegisterArmor.gasmask || item2 == RegisterArmor.hanza || item2 == RegisterArmor.himza || item2 == RegisterArmor.officer_ozk || item2 == RegisterArmor.ozk || item2 == RegisterArmor.povstanec || item2 == RegisterArmor.psh1b || item2 == RegisterArmor.Scout_armor || item2 == RegisterArmor.sverchok_mp6 || item2 == RegisterArmor.zapal || item2 == RegisterArmor.reih || item2 == RegisterArmor.mgu) {
            if (Minecraft.func_71410_x().field_71474_y.field_74320_O == 0) {
                GuiUtils.tesselatorDrawFull((int)event.resolution.func_78326_a(), (int)event.resolution.func_78328_b(), (ResourceLocation)gasmask);
            }
        } else if (LeftClickingEvent.isRad) {
            if (this.ticks == 100) {
                GuiUtils.tesselatorDrawFull((int)event.resolution.func_78326_a(), (int)event.resolution.func_78328_b(), (ResourceLocation)rad1);
            }
            if (this.ticks == 200) {
                GuiUtils.tesselatorDrawFull((int)event.resolution.func_78326_a(), (int)event.resolution.func_78328_b(), (ResourceLocation)rad2);
            }
            if (this.ticks == 300) {
                GuiUtils.tesselatorDrawFull((int)event.resolution.func_78326_a(), (int)event.resolution.func_78328_b(), (ResourceLocation)rad3);
            }
        }
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
        this.ticks = this.ticks <= 300 ? ++this.ticks : 0;
        int fovc = (int)Minecraft.func_71410_x().field_71474_y.field_74334_X;
        ItemStack heldItem = player.func_70694_bm();
        if (heldItem != null && heldItem.func_77973_b() instanceof ItemGun && heldItem.field_77990_d != null) {
            boolean pric;
            if (!guiUpgrade && Mouse.isButtonDown((int)1) && !Minecraft.func_71410_x().field_71439_g.func_70051_ag() && !heldItem.field_77990_d.func_74767_n("isReload")) {
                pric = true;
                this.aim_tick1 = 0;
                if (this.aim_tick0 < 20) {
                    ++this.aim_tick0;
                }
                if (this.aim_tick0 == 1) {
                    Minecraft.func_71410_x().func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_147674_a((ResourceLocation)new ResourceLocation("metro:aim_in_00"), (float)1.0f));
                }
            } else {
                this.aim_tick0 = 0;
                if (this.aim_tick1 < 20) {
                    ++this.aim_tick1;
                }
                if (this.aim_tick1 == 1) {
                    Minecraft.func_71410_x().func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_147674_a((ResourceLocation)new ResourceLocation("metro:aim_out_00"), (float)1.0f));
                }
                pric = false;
            }
            if (Minecraft.func_71410_x().field_71462_r == null) {
                if (heldItem.field_77990_d.func_74762_e("modScope") == 0) {
                    if (pric) {
                        if (Minecraft.func_71410_x().field_71474_y.field_74334_X >= this.fov - 20.0f) {
                            Minecraft.func_71410_x().field_71474_y.field_74334_X -= 1.0f;
                        }
                    } else if (Minecraft.func_71410_x().field_71474_y.field_74334_X <= this.fov) {
                        Minecraft.func_71410_x().field_71474_y.field_74334_X += 1.0f;
                    }
                }
                if (heldItem.field_77990_d.func_74762_e("modScope") == 1) {
                    if (pric) {
                        if (Minecraft.func_71410_x().field_71474_y.field_74334_X >= this.fov / 4.0f) {
                            Minecraft.func_71410_x().field_71474_y.field_74334_X -= 1.0f;
                        }
                    } else if (Minecraft.func_71410_x().field_71474_y.field_74334_X <= this.fov) {
                        Minecraft.func_71410_x().field_71474_y.field_74334_X += 1.0f;
                    }
                }
                if (heldItem.field_77990_d.func_74762_e("modScope") == 2) {
                    // empty if block
                }
                if (heldItem.field_77990_d.func_74762_e("modScope") == 3) {
                    // empty if block
                }
            }
        }
        int w = event.resolution.func_78326_a();
        int h = event.resolution.func_78328_b();
        float scale = w / h;
        GL11.glPushMatrix();
        int he = (int)props.getHealth() / 5;
        RenderingHelper.renderString((String)("  " + he * 5), (float)25.0f, (float)7.0f, (UnicodeFont)RenderingHelper.FONT_48, (Color)new Color(255, 255, 255, 255));
        Minecraft.func_71410_x().field_71446_o.func_110577_a(hearth);
        GL11.glEnable((int)3042);
        GuiUtils.tesselatorDrawPolosLeft((float)25.0f, (float)0.0f, (int)0, (int)0, (float)scale, (int)w, (int)25);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        int hg = Minecraft.func_71410_x().field_71439_g.func_71024_bL().func_75116_a();
        RenderingHelper.renderString((String)("  " + hg * 5), (float)25.0f, (float)30.0f, (UnicodeFont)RenderingHelper.FONT_48, (Color)new Color(255, 255, 255, 255));
        Minecraft.func_71410_x().field_71446_o.func_110577_a(food);
        GL11.glTranslatef((float)0.0f, (float)25.0f, (float)0.0f);
        GL11.glEnable((int)3042);
        GuiUtils.tesselatorDrawPolosLeft((float)25.0f, (float)0.0f, (int)0, (int)0, (float)scale, (int)w, (int)25);
        GL11.glDisable((int)3042);
        if (heldItem != null && heldItem.func_77973_b() instanceof ItemGun && heldItem.field_77990_d != null) {
            RenderingHelper.renderString((String)(heldItem.field_77990_d.func_74762_e("curammo") + " / " + heldItem.field_77990_d.func_74762_e("maxammo")), (float)5.0f, (float)30.0f, (UnicodeFont)RenderingHelper.FONT_48, (Color)new Color(255, 255, 255, 255));
        }
        GL11.glPopMatrix();
    }

    @SubscribeEvent
    public void drawGui1(RenderGameOverlayEvent event) {
        int w = event.resolution.func_78326_a();
        int h = event.resolution.func_78328_b();
        float scaleDD = Minecraft.func_71410_x().field_71443_c / Minecraft.func_71410_x().field_71440_d;
        EntityClientPlayerMP player = this.mc.field_71439_g;
        ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer)player);
        double sinAnimate = Double.valueOf(this.mc.field_71426_K.split(" ")[0]) / 100.0 * 2.0;
        float scaleD = scaleDD < 2.0f ? scaleDD : (scaleDD >= 3.0f ? scaleDD / (scaleDD * 0.5f) : scaleDD / (scaleDD * 0.7f));
        if (hitmark <= 80) {
            GL11.glPushMatrix();
            GL11.glEnable((int)3042);
            GuiUtils.tesselatorDrawCenter((float)25.0f, (float)25.0f, (float)scaleD, (int)w, (int)h, (ResourceLocation)hit);
            GL11.glDisable((int)3042);
            GL11.glPopMatrix();
        }
        if (hitmark <= 90) {
            ++hitmark;
        }
    }
}
