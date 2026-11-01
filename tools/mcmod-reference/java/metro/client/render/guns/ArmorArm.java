/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  metro.common.player.ExtendedPlayer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityClientPlayerMP
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.AdvancedModelLoader
 *  net.minecraftforge.client.model.IModelCustom
 *  org.lwjgl.opengl.GL11
 */
package metro.client.render.guns;

import java.time.LocalTime;
import metro.client.events.LeftClickingEvent;
import metro.common.Main;
import metro.common.player.ExtendedPlayer;
import metro.common.regstration.RegisterArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public class ArmorArm {
    private static IModelCustom objmodel = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/guns/ak/model.obj"));
    public static final ResourceLocation arm = new ResourceLocation("metro", "models/guns/mods/clock_r.png");
    public static final ResourceLocation perch = new ResourceLocation("metro", "models/guns/mods/perch.png");
    public static final ResourceLocation clock = new ResourceLocation("metro", "models/guns/mods/clock-cR.png");
    public static final ResourceLocation num1 = new ResourceLocation("metro", "models/clock/1.png");
    public static final ResourceLocation num2 = new ResourceLocation("metro", "models/clock/2.png");
    public static final ResourceLocation num3 = new ResourceLocation("metro", "models/clock/3.png");
    public static final ResourceLocation num4 = new ResourceLocation("metro", "models/clock/4.png");
    public static final ResourceLocation num5 = new ResourceLocation("metro", "models/clock/5.png");
    public static final ResourceLocation num6 = new ResourceLocation("metro", "models/clock/6.png");
    public static final ResourceLocation num7 = new ResourceLocation("metro", "models/clock/7.png");
    public static final ResourceLocation num8 = new ResourceLocation("metro", "models/clock/8.png");
    public static final ResourceLocation num9 = new ResourceLocation("metro", "models/clock/9.png");
    public static final ResourceLocation num0 = new ResourceLocation("metro", "models/clock/0.png");

    public static void renderA() {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        Minecraft.func_71410_x().field_71446_o.func_110577_a(arm);
        objmodel.renderOnly(new String[]{"arm_left"});
        Minecraft.func_71410_x().field_71446_o.func_110577_a(perch);
        objmodel.renderOnly(new String[]{"perch_left"});
        Minecraft.func_71410_x().field_71446_o.func_110577_a(clock);
        objmodel.renderOnly(new String[]{"clock"});
        EntityClientPlayerMP player = Minecraft.func_71410_x().field_71439_g;
        ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer)player);
        World world = player.field_70170_p;
        LocalTime localTime = LocalTime.now();
        int hour = localTime.getHour();
        int minute = localTime.getMinute();
        int min_g = props.getGasTick() / 60;
        int sec_g = props.getGasTick() - min_g * 60;
        int sec_g2 = props.getGasTick() % 10;
        Item itemg = null;
        Item item2 = null;
        if (player.field_71071_by.func_70440_f(3) != null) {
            itemg = player.field_71071_by.func_70440_f(3).func_77973_b();
        }
        if (player.field_71071_by.func_70440_f(2) != null) {
            item2 = player.field_71071_by.func_70440_f(2).func_77973_b();
        }
        if (player.field_70163_u >= (double)Main.h_rad || LeftClickingEvent.isRad) {
            if (itemg == RegisterArmor.gasmask || item2 == RegisterArmor.hanza || item2 == RegisterArmor.himza || item2 == RegisterArmor.officer_ozk || item2 == RegisterArmor.ozk || item2 == RegisterArmor.povstanec || item2 == RegisterArmor.psh1b || item2 == RegisterArmor.Scout_armor || item2 == RegisterArmor.sverchok_mp6 || item2 == RegisterArmor.zapal || item2 == RegisterArmor.mgu) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                objmodel.renderOnly(new String[]{"num1"});
                OpenGlHelper.func_77475_a((int)OpenGlHelper.field_77476_b, (float)240.0f, (float)240.0f);
                if (min_g == 5) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
                }
                if (min_g == 4) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
                }
                if (min_g == 3) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
                }
                if (min_g == 2) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
                }
                if (min_g == 1) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
                }
                if (min_g == 0) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                }
                objmodel.renderOnly(new String[]{"num2"});
                OpenGlHelper.func_77475_a((int)OpenGlHelper.field_77476_b, (float)240.0f, (float)240.0f);
                if (sec_g == 0) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                }
                if (sec_g == 1) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                }
                if (sec_g == 2) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                }
                if (sec_g == 3) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                }
                if (sec_g == 4) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                }
                if (sec_g == 5) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                }
                if (sec_g == 6) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                }
                if (sec_g == 7) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                }
                if (sec_g == 8) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                }
                if (sec_g == 9) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                }
                if (sec_g == 10) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
                }
                if (sec_g == 11) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
                }
                if (sec_g == 12) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
                }
                if (sec_g == 13) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
                }
                if (sec_g == 14) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
                }
                if (sec_g == 15) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
                }
                if (sec_g == 16) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
                }
                if (sec_g == 17) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
                }
                if (sec_g == 18) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
                }
                if (sec_g == 19) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
                }
                if (sec_g == 20) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
                }
                if (sec_g == 21) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
                }
                if (sec_g == 22) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
                }
                if (sec_g == 23) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
                }
                if (sec_g == 24) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
                }
                if (sec_g == 25) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
                }
                if (sec_g == 26) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
                }
                if (sec_g == 27) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
                }
                if (sec_g == 28) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
                }
                if (sec_g == 29) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
                }
                if (sec_g == 30) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
                }
                if (sec_g == 31) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
                }
                if (sec_g == 32) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
                }
                if (sec_g == 33) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
                }
                if (sec_g == 34) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
                }
                if (sec_g == 35) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
                }
                if (sec_g == 36) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
                }
                if (sec_g == 37) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
                }
                if (sec_g == 38) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
                }
                if (sec_g == 39) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
                }
                if (sec_g == 40) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
                }
                if (sec_g == 41) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
                }
                if (sec_g == 42) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
                }
                if (sec_g == 43) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
                }
                if (sec_g == 44) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
                }
                if (sec_g == 45) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
                }
                if (sec_g == 46) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
                }
                if (sec_g == 47) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
                }
                if (sec_g == 48) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
                }
                if (sec_g == 49) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
                }
                if (sec_g == 50) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
                }
                if (sec_g == 51) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
                }
                if (sec_g == 52) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
                }
                if (sec_g == 53) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
                }
                if (sec_g == 54) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
                }
                if (sec_g == 55) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
                }
                if (sec_g == 56) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
                }
                if (sec_g == 57) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
                }
                if (sec_g == 58) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
                }
                if (sec_g == 59) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
                }
                if (sec_g == 60) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                }
                objmodel.renderOnly(new String[]{"num3"});
                OpenGlHelper.func_77475_a((int)OpenGlHelper.field_77476_b, (float)240.0f, (float)240.0f);
                if (sec_g2 == 0) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                }
                if (sec_g2 == 1) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
                }
                if (sec_g2 == 2) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
                }
                if (sec_g2 == 3) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
                }
                if (sec_g2 == 4) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
                }
                if (sec_g2 == 5) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
                }
                if (sec_g2 == 6) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num6);
                }
                if (sec_g2 == 7) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num7);
                }
                if (sec_g2 == 8) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num8);
                }
                if (sec_g2 == 9) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num9);
                }
                if (sec_g2 == 10) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                }
                if (sec_g2 == 11) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
                }
                if (sec_g2 == 12) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
                }
                if (sec_g2 == 13) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
                }
                if (sec_g2 == 14) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
                }
                if (sec_g2 == 15) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
                }
                if (sec_g2 == 16) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num6);
                }
                if (sec_g2 == 17) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num7);
                }
                if (sec_g2 == 18) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num8);
                }
                if (sec_g2 == 19) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num9);
                }
                if (sec_g2 == 20) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                }
                if (sec_g2 == 21) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
                }
                if (sec_g2 == 22) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
                }
                if (sec_g2 == 23) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
                }
                if (sec_g2 == 24) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
                }
                if (sec_g2 == 25) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
                }
                if (sec_g2 == 26) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num6);
                }
                if (sec_g2 == 27) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num7);
                }
                if (sec_g2 == 28) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num8);
                }
                if (sec_g2 == 29) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num9);
                }
                if (sec_g2 == 30) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                }
                if (sec_g2 == 31) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
                }
                if (sec_g2 == 32) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
                }
                if (sec_g2 == 33) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
                }
                if (sec_g2 == 34) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
                }
                if (sec_g2 == 35) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
                }
                if (sec_g2 == 36) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num6);
                }
                if (sec_g2 == 37) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num7);
                }
                if (sec_g2 == 38) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num8);
                }
                if (sec_g2 == 39) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num9);
                }
                if (sec_g2 == 40) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                }
                if (sec_g2 == 41) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
                }
                if (sec_g2 == 42) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
                }
                if (sec_g2 == 43) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
                }
                if (sec_g2 == 44) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
                }
                if (sec_g2 == 45) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
                }
                if (sec_g2 == 46) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num6);
                }
                if (sec_g2 == 47) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num7);
                }
                if (sec_g2 == 48) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num8);
                }
                if (sec_g2 == 49) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num9);
                }
                if (sec_g2 == 50) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                }
                if (sec_g2 == 51) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
                }
                if (sec_g2 == 52) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
                }
                if (sec_g2 == 53) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
                }
                if (sec_g2 == 54) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
                }
                if (sec_g2 == 55) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
                }
                if (sec_g2 == 56) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num6);
                }
                if (sec_g2 == 57) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num7);
                }
                if (sec_g2 == 58) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num8);
                }
                if (sec_g2 == 59) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num9);
                }
                if (sec_g2 == 60) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
                }
                objmodel.renderOnly(new String[]{"num4"});
            }
        } else {
            OpenGlHelper.func_77475_a((int)OpenGlHelper.field_77476_b, (float)240.0f, (float)240.0f);
            if (hour == 0) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (hour == 1) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (hour == 2) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (hour == 3) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (hour == 4) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (hour == 5) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (hour == 6) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (hour == 7) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (hour == 8) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (hour == 9) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (hour == 10) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (hour == 11) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (hour == 12) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (hour == 13) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (hour == 14) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (hour == 15) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (hour == 16) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (hour == 17) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (hour == 18) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (hour == 19) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (hour == 20) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (hour == 21) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (hour == 22) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (hour == 23) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            objmodel.renderOnly(new String[]{"num1"});
            OpenGlHelper.func_77475_a((int)OpenGlHelper.field_77476_b, (float)240.0f, (float)240.0f);
            if (hour == 0) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (hour == 1) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (hour == 2) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (hour == 3) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
            }
            if (hour == 4) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
            }
            if (hour == 5) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
            }
            if (hour == 6) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num6);
            }
            if (hour == 7) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num7);
            }
            if (hour == 8) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num8);
            }
            if (hour == 9) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num9);
            }
            if (hour == 10) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (hour == 11) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (hour == 12) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (hour == 13) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
            }
            if (hour == 14) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
            }
            if (hour == 15) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
            }
            if (hour == 16) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num6);
            }
            if (hour == 17) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num7);
            }
            if (hour == 18) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num8);
            }
            if (hour == 19) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num9);
            }
            if (hour == 20) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (hour == 21) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (hour == 22) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (hour == 23) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
            }
            objmodel.renderOnly(new String[]{"num2"});
            OpenGlHelper.func_77475_a((int)OpenGlHelper.field_77476_b, (float)240.0f, (float)240.0f);
            if (minute == 0) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (minute == 1) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (minute == 2) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (minute == 3) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (minute == 4) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (minute == 5) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (minute == 6) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (minute == 7) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (minute == 8) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (minute == 9) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (minute == 10) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (minute == 11) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (minute == 12) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (minute == 13) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (minute == 14) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (minute == 15) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (minute == 16) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (minute == 17) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (minute == 18) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (minute == 19) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (minute == 20) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (minute == 21) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (minute == 22) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (minute == 23) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (minute == 24) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (minute == 25) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (minute == 26) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (minute == 27) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (minute == 28) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (minute == 29) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (minute == 30) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
            }
            if (minute == 31) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
            }
            if (minute == 32) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
            }
            if (minute == 33) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
            }
            if (minute == 34) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
            }
            if (minute == 35) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
            }
            if (minute == 36) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
            }
            if (minute == 37) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
            }
            if (minute == 38) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
            }
            if (minute == 39) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
            }
            if (minute == 40) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
            }
            if (minute == 41) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
            }
            if (minute == 42) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
            }
            if (minute == 43) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
            }
            if (minute == 44) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
            }
            if (minute == 45) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
            }
            if (minute == 46) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
            }
            if (minute == 47) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
            }
            if (minute == 48) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
            }
            if (minute == 49) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
            }
            if (minute == 50) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
            }
            if (minute == 51) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
            }
            if (minute == 52) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
            }
            if (minute == 53) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
            }
            if (minute == 54) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
            }
            if (minute == 55) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
            }
            if (minute == 56) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
            }
            if (minute == 57) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
            }
            if (minute == 58) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
            }
            if (minute == 59) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
            }
            if (minute == 60) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            objmodel.renderOnly(new String[]{"num3"});
            OpenGlHelper.func_77475_a((int)OpenGlHelper.field_77476_b, (float)240.0f, (float)240.0f);
            if (minute == 0) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (minute == 1) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (minute == 2) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (minute == 3) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
            }
            if (minute == 4) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
            }
            if (minute == 5) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
            }
            if (minute == 6) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num6);
            }
            if (minute == 7) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num7);
            }
            if (minute == 8) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num8);
            }
            if (minute == 9) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num9);
            }
            if (minute == 10) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (minute == 11) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (minute == 12) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (minute == 13) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
            }
            if (minute == 14) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
            }
            if (minute == 15) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
            }
            if (minute == 16) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num6);
            }
            if (minute == 17) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num7);
            }
            if (minute == 18) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num8);
            }
            if (minute == 19) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num9);
            }
            if (minute == 20) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (minute == 21) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (minute == 22) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (minute == 23) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
            }
            if (minute == 24) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
            }
            if (minute == 25) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
            }
            if (minute == 26) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num6);
            }
            if (minute == 27) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num7);
            }
            if (minute == 28) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num8);
            }
            if (minute == 29) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num9);
            }
            if (minute == 30) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (minute == 31) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (minute == 32) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (minute == 33) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
            }
            if (minute == 34) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
            }
            if (minute == 35) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
            }
            if (minute == 36) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num6);
            }
            if (minute == 37) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num7);
            }
            if (minute == 38) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num8);
            }
            if (minute == 39) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num9);
            }
            if (minute == 40) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (minute == 41) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (minute == 42) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (minute == 43) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
            }
            if (minute == 44) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
            }
            if (minute == 45) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
            }
            if (minute == 46) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num6);
            }
            if (minute == 47) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num7);
            }
            if (minute == 48) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num8);
            }
            if (minute == 49) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num9);
            }
            if (minute == 50) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            if (minute == 51) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num1);
            }
            if (minute == 52) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num2);
            }
            if (minute == 53) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num3);
            }
            if (minute == 54) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num4);
            }
            if (minute == 55) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num5);
            }
            if (minute == 56) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num6);
            }
            if (minute == 57) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num7);
            }
            if (minute == 58) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num8);
            }
            if (minute == 59) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num9);
            }
            if (minute == 60) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(num0);
            }
            objmodel.renderOnly(new String[]{"num4"});
        }
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void renderB() {
        GL11.glPushMatrix();
        Minecraft.func_71410_x().field_71446_o.func_110577_a(arm);
        objmodel.renderPart("arm_right");
        Minecraft.func_71410_x().field_71446_o.func_110577_a(perch);
        objmodel.renderOnly(new String[]{"perch_right"});
        GL11.glPopMatrix();
    }
}
