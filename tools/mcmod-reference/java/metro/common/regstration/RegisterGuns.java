/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.common.registry.LanguageRegistry
 *  metro.client.render.guns.Aptech_Render
 *  metro.client.render.guns.Ashot_Render
 *  metro.client.render.guns.Duplet_Render
 *  metro.client.render.guns.Helsing_Render
 *  metro.client.render.guns.Machete_Render
 *  metro.client.render.guns.Preved_Render
 *  metro.client.render.guns.Railgun_Render
 *  metro.client.render.guns.Rev_Render
 *  metro.client.render.guns.Tihar_Render
 *  metro.client.render.guns.Ubludok_Render
 *  metro.client.render.guns.Ubo_Render
 *  metro.client.render.guns.Ventil_Render
 *  metro.client.render.guns.ak2012_Render
 *  metro.client.render.guns.ak74_Render
 *  metro.client.render.guns.ak74u_Render
 *  metro.client.render.guns.rpk_Render
 *  metro.client.render.guns.vsv_Render
 *  metro.common.gun.ItemGun
 *  metro.common.gun.ammo.ItemAmmo
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$ToolMaterial
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.MinecraftForgeClient
 */
package metro.common.regstration;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import metro.client.render.guns.Aptech_Render;
import metro.client.render.guns.Ashot_Render;
import metro.client.render.guns.Duplet_Render;
import metro.client.render.guns.Helsing_Render;
import metro.client.render.guns.Machete_Render;
import metro.client.render.guns.Preved_Render;
import metro.client.render.guns.Railgun_Render;
import metro.client.render.guns.Rev_Render;
import metro.client.render.guns.Tihar_Render;
import metro.client.render.guns.Ubludok_Render;
import metro.client.render.guns.Ubo_Render;
import metro.client.render.guns.Ventil_Render;
import metro.client.render.guns.ak2012_Render;
import metro.client.render.guns.ak74_Render;
import metro.client.render.guns.ak74u_Render;
import metro.client.render.guns.rpk_Render;
import metro.client.render.guns.vsv_Render;
import metro.common.Main;
import metro.common.gun.ItemAptech;
import metro.common.gun.ItemAxeB;
import metro.common.gun.ItemGun;
import metro.common.gun.ammo.ItemAmmo;
import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

public class RegisterGuns {
    public static Item ammo5x45;
    public static Item ammo12x70;
    public static Item ammo44;
    public static Item ammo762;
    public static Item ammo145;
    public static Item ammo_bolts;
    public static Item money;
    public static Item money5;
    public static Item money30;
    public static Item moneyj;
    public static Item filtr;
    public static Item res_metall;
    public static Item res_xim;
    public static Item ak74;
    public static Item ashot;
    public static Item duplet;
    public static Item ubo;
    public static Item vsv;
    public static Item rev;
    public static Item ubludok;
    public static Item ventil;
    public static Item preved;
    public static Item machete;
    public static Item aptech;
    public static Item railgun;
    public static Item tihar;
    public static Item ak2012;
    public static Item g9a91;
    public static Item ammoball;
    public static Item ak74u;
    public static Item rpk;
    public static Item helsing;

    public static void load(FMLPreInitializationEvent event) {
        res_metall = new ItemAmmo(0).func_77637_a(Main.gunTab).func_77655_b("res_metall").func_111206_d("metro:res_metall");
        GameRegistry.registerItem((Item)res_metall, (String)"res_metall");
        LanguageRegistry.addName((Object)res_metall, (String)"\u041c\u0435\u0442\u0430\u043b\u043b\u043e\u043b\u043e\u043c");
        res_xim = new ItemAmmo(0).func_77637_a(Main.gunTab).func_77655_b("res_xim").func_111206_d("metro:res_xim");
        GameRegistry.registerItem((Item)res_xim, (String)"res_xim");
        LanguageRegistry.addName((Object)res_xim, (String)"\u0425\u0438\u043c\u0438\u044f");
        filtr = new ItemAmmo(0).func_77637_a(Main.gunTab).func_77655_b("filtr").func_111206_d("metro:filtr");
        GameRegistry.registerItem((Item)filtr, (String)"filtr");
        LanguageRegistry.addName((Object)filtr, (String)"\u0424\u0438\u043b\u044c\u0442\u0440 \u0434\u043b\u044f \u043f\u0440\u043e\u0442\u0438\u0432\u043e\u0433\u0430\u0437\u0430");
        ammo5x45 = new ItemAmmo(0).func_77637_a(Main.gunTab).func_77655_b("ammo5x45").func_111206_d("metro:x545");
        GameRegistry.registerItem((Item)ammo5x45, (String)"ammo5x45");
        LanguageRegistry.addName((Object)ammo5x45, (String)"\u041f\u0430\u0442\u0440\u043e\u043d\u044b 5x45");
        ammo12x70 = new ItemAmmo(0).func_77637_a(Main.gunTab).func_77655_b("ammo12x70").func_111206_d("metro:x1270");
        GameRegistry.registerItem((Item)ammo12x70, (String)"ammo12x70");
        LanguageRegistry.addName((Object)ammo12x70, (String)"\u041f\u0430\u0442\u0440\u043e\u043d\u044b 12x70");
        ammo44 = new ItemAmmo(0).func_77637_a(Main.gunTab).func_77655_b("ammo44").func_111206_d("metro:x44");
        GameRegistry.registerItem((Item)ammo44, (String)"ammo44");
        LanguageRegistry.addName((Object)ammo44, (String)"\u041f\u0430\u0442\u0440\u043e\u043d\u044b 44 \u043a\u0430\u043b\u0438\u0431\u0440");
        ammo762 = new ItemAmmo(0).func_77637_a(Main.gunTab).func_77655_b("ammo762").func_111206_d("metro:x762");
        GameRegistry.registerItem((Item)ammo762, (String)"ammo762");
        LanguageRegistry.addName((Object)ammo762, (String)"\u041f\u0430\u0442\u0440\u043e\u043d\u044b 7.62");
        ammo145 = new ItemAmmo(0).func_77637_a(Main.gunTab).func_77655_b("ammo145").func_111206_d("metro:x145");
        GameRegistry.registerItem((Item)ammo145, (String)"ammo145");
        LanguageRegistry.addName((Object)ammo145, (String)"\u041f\u0430\u0442\u0440\u043e\u043d\u044b 14.5");
        ammoball = new ItemAmmo(0).func_77637_a(Main.gunTab).func_77655_b("ammoball").func_111206_d("metro:ball");
        GameRegistry.registerItem((Item)ammoball, (String)"ammoball");
        LanguageRegistry.addName((Object)ammoball, (String)"\u0416\u0435\u043b\u0435\u0437\u043d\u044b\u0435 \u0448\u0430\u0440\u0438\u043a\u0438 \u0434\u043b\u044f \u043f\u043d\u0435\u0432\u043c\u0430\u0442\u0438\u043a\u0438");
        ammo_bolts = new ItemAmmo(0).func_77637_a(Main.gunTab).func_77655_b("ammo_bolts").func_111206_d("metro:ammo_bolts");
        GameRegistry.registerItem((Item)ammo_bolts, (String)"ammo_bolts");
        LanguageRegistry.addName((Object)ammo_bolts, (String)"\u0411\u043e\u043b\u0442\u044b \u0434\u043b\u044f \u0445\u0435\u043b\u044c\u0441\u0438\u043d\u0433\u0430");
        money = new ItemAmmo(0).func_77637_a(Main.gunTab).func_77655_b("money").func_111206_d("metro:money");
        GameRegistry.registerItem((Item)money, (String)"money");
        LanguageRegistry.addName((Object)money, (String)"\u041f\u0430\u0442\u0440\u043e\u043d\u044b - \u0434\u0435\u043d\u044c\u0433\u0438");
        money5 = new ItemAmmo(0).func_77637_a(Main.gunTab).func_77655_b("money5").func_111206_d("metro:money5");
        GameRegistry.registerItem((Item)money5, (String)"money5");
        LanguageRegistry.addName((Object)money5, (String)"\u041f\u0430\u0442\u0440\u043e\u043d\u044b - \u0434\u0435\u043d\u044c\u0433\u0438 \u043e\u0431\u043e\u043c\u0439\u043c\u0430 (5 \u0448\u0442.)");
        money30 = new ItemAmmo(0).func_77637_a(Main.gunTab).func_77655_b("money30").func_111206_d("metro:money30");
        GameRegistry.registerItem((Item)money30, (String)"money30");
        LanguageRegistry.addName((Object)money30, (String)"\u041f\u0430\u0442\u0440\u043e\u043d\u044b - \u0434\u0435\u043d\u044c\u0433\u0438 \u043c\u0430\u0433\u0430\u0437\u0438\u043d (30 \u0448\u0442.)");
        moneyj = new ItemAmmo(0).func_77637_a(Main.gunTab).func_77655_b("moneyj").func_111206_d("metro:moneyj");
        GameRegistry.registerItem((Item)moneyj, (String)"moneyj");
        LanguageRegistry.addName((Object)moneyj, (String)"\u0416\u0435\u0442\u043e\u043d \u043c\u0435\u0442\u0440\u043e");
        ak74 = new ItemGun(false, 13.0, 1, 1.0f, "ak_boom", "ak74_reload", 1.0f, 1.5f, 30, 1, 1.0f, -1.0f, "ak74", 60, 25.0, 30.0f, 1600).func_77637_a(Main.gunTab).func_77625_d(1).func_77655_b("ak74").func_111206_d("metro:ak74");
        GameRegistry.registerItem((Item)ak74, (String)"ak74");
        LanguageRegistry.addName((Object)ak74, (String)"\u0410\u041a-74");
        if (event.getSide().isClient()) {
            MinecraftForgeClient.registerItemRenderer((Item)ak74, (IItemRenderer)new ak74_Render());
        }
        ashot = new ItemGun(false, 25.0, 3, 1.0f, "wpn_shootgun_shoot", "wpn_shootgun_reload", 3.0f, 1.5f, 1, 2, 2.0f, -6.0f, "ashot", 40, 25.0, 30.0f, 350).func_77637_a(Main.gunTab).func_77625_d(1).func_77655_b("ashot").func_111206_d("metro:ashot");
        GameRegistry.registerItem((Item)ashot, (String)"ashot");
        LanguageRegistry.addName((Object)ashot, (String)"\u0410\u0448\u043e\u0442");
        if (event.getSide().isClient()) {
            MinecraftForgeClient.registerItemRenderer((Item)ashot, (IItemRenderer)new Ashot_Render());
        }
        duplet = new ItemGun(false, 18.0, 3, 1.0f, "wpn_shootgun_shoot", "wpn_shootgun_reload", 3.0f, 1.5f, 2, 2, -2.0f, -6.0f, "ashot", 50, 25.0, 30.0f, 500).func_77637_a(Main.gunTab).func_77625_d(1).func_77655_b("duplet").func_111206_d("metro:duplet");
        GameRegistry.registerItem((Item)duplet, (String)"duplet");
        LanguageRegistry.addName((Object)duplet, (String)"\u0414\u0443\u043f\u043b\u0435\u0442");
        if (event.getSide().isClient()) {
            MinecraftForgeClient.registerItemRenderer((Item)duplet, (IItemRenderer)new Duplet_Render());
        }
        ubo = new ItemGun(false, 18.0, 3, 15.0f, "uboineg", "ubo_reload", 3.0f, 1.5f, 6, 2, 2.0f, -6.0f, "ubo", 80, 25.0, 30.0f, 500).func_77637_a(Main.gunTab).func_77625_d(1).func_77655_b("ubo").func_111206_d("metro:ubo");
        GameRegistry.registerItem((Item)ubo, (String)"ubo");
        LanguageRegistry.addName((Object)ubo, (String)"\u0423\u0431\u043e\u0439\u043d\u0438\u043a");
        if (event.getSide().isClient()) {
            MinecraftForgeClient.registerItemRenderer((Item)ubo, (IItemRenderer)new Ubo_Render());
        }
        vsv = new ItemGun(false, 19.0, 1, 2.0f, "ubludok_shoot", "ak74_reload", 0.0f, 1.5f, 20, 1, 0.0f, -1.0f, "vsv", 70, 25.0, 30.0f, 2800).func_77637_a(Main.gunTab).func_77625_d(1).func_77655_b("vsv").func_111206_d("metro:vsv");
        GameRegistry.registerItem((Item)vsv, (String)"vsv");
        LanguageRegistry.addName((Object)vsv, (String)"\u0412\u0421\u0412");
        if (event.getSide().isClient()) {
            MinecraftForgeClient.registerItemRenderer((Item)vsv, (IItemRenderer)new vsv_Render());
        }
        rev = new ItemGun(false, 11.0, 2, 10.0f, "revolver_shot_1", "duplet_reload", 1.0f, 1.5f, 6, 3, -1.0f, -3.0f, "rev", 60, 25.0, 30.0f, 600).func_77637_a(Main.gunTab).func_77625_d(1).func_77655_b("rev").func_111206_d("metro:rev");
        GameRegistry.registerItem((Item)rev, (String)"rev");
        LanguageRegistry.addName((Object)rev, (String)"\u0420\u0435\u0432\u043e\u043b\u044c\u0432\u0435\u0440");
        if (event.getSide().isClient()) {
            MinecraftForgeClient.registerItemRenderer((Item)rev, (IItemRenderer)new Rev_Render());
        }
        ubludok = new ItemGun(false, 11.0, 1, 1.0f, "ubludok_shoot", "ubl_oblom_0", 2.0f, 1.5f, 30, 1, 0.0f, -2.0f, "ubludok", 65, 25.0, 30.0f, 800).func_77637_a(Main.gunTab).func_77625_d(1).func_77655_b("ubludok").func_111206_d("metro:ubludok");
        GameRegistry.registerItem((Item)ubludok, (String)"ubludok");
        LanguageRegistry.addName((Object)ubludok, (String)"\u0423\u0431\u043b\u044e\u0434\u043e\u043a");
        if (event.getSide().isClient()) {
            MinecraftForgeClient.registerItemRenderer((Item)ubludok, (IItemRenderer)new Ubludok_Render());
        }
        ventil = new ItemGun(false, 40.0, 2, 20.0f, "ventil_shot", "ventil_reload", 0.0f, 1.5f, 5, 4, 1.0f, -6.0f, "ventil", 60, 25.0, 30.0f, 600).func_77637_a(Main.gunTab).func_77625_d(1).func_77655_b("ventil").func_111206_d("metro:ventil");
        GameRegistry.registerItem((Item)ventil, (String)"ventil");
        LanguageRegistry.addName((Object)ventil, (String)"\u0412\u0435\u043d\u0442\u0438\u043b\u044c");
        if (event.getSide().isClient()) {
            MinecraftForgeClient.registerItemRenderer((Item)ventil, (IItemRenderer)new Ventil_Render());
        }
        preved = new ItemGun(false, 80.0, 2, 35.0f, "preved_base_shot", "preved_reload", 0.0f, 1.5f, 1, 5, 4.0f, -6.0f, "preved", 75, 25.0, 30.0f, 800).func_77637_a(Main.gunTab).func_77625_d(1).func_77655_b("preved").func_111206_d("metro:preved");
        GameRegistry.registerItem((Item)preved, (String)"preved");
        LanguageRegistry.addName((Object)preved, (String)"\u041f\u0440\u0435\u0432\u0435\u0434");
        if (event.getSide().isClient()) {
            MinecraftForgeClient.registerItemRenderer((Item)preved, (IItemRenderer)new Preved_Render());
        }
        machete = new ItemAxeB(Item.ToolMaterial.EMERALD).func_77637_a(Main.gunTab).func_77625_d(1).func_77655_b("machete").func_111206_d("metro:machete");
        GameRegistry.registerItem((Item)machete, (String)"machete");
        LanguageRegistry.addName((Object)machete, (String)"\u041c\u0430\u0447\u0435\u0442\u0430");
        if (event.getSide().isClient()) {
            MinecraftForgeClient.registerItemRenderer((Item)machete, (IItemRenderer)new Machete_Render());
        }
        aptech = new ItemAptech(0).func_77637_a(Main.gunTab).func_77625_d(1).func_77655_b("aptech").func_111206_d("metro:aptech");
        GameRegistry.registerItem((Item)aptech, (String)"aptech");
        LanguageRegistry.addName((Object)aptech, (String)"\u0410\u043f\u0442\u0435\u0447\u043a\u0430");
        if (event.getSide().isClient()) {
            MinecraftForgeClient.registerItemRenderer((Item)aptech, (IItemRenderer)new Aptech_Render());
        }
        railgun = new ItemGun(true, 28.0, 2, 15.0f, "railgun_shoot", "railgun_reload", 0.0f, 1.5f, 15, 6, 1.0f, -1.0f, "preved", 75, 25.0, 30.0f, 5000).func_77637_a(Main.gunTab).func_77625_d(1).func_77655_b("railgun").func_111206_d("metro:railgun");
        GameRegistry.registerItem((Item)railgun, (String)"railgun");
        LanguageRegistry.addName((Object)railgun, (String)"\u0420\u0435\u043b\u044c\u0441\u0430");
        if (event.getSide().isClient()) {
            MinecraftForgeClient.registerItemRenderer((Item)railgun, (IItemRenderer)new Railgun_Render());
        }
        tihar = new ItemGun(true, 16.0, 1, 6.0f, "tihar_shoot", "railgun_reload", 0.0f, 1.5f, 15, 6, 1.0f, -1.0f, "preved", 75, 25.0, 30.0f, 3000).func_77637_a(Main.gunTab).func_77625_d(1).func_77655_b("tihar").func_111206_d("metro:tihar");
        GameRegistry.registerItem((Item)tihar, (String)"tihar");
        LanguageRegistry.addName((Object)tihar, (String)"\u0422\u0438\u0445\u0430\u0440\u044c");
        if (event.getSide().isClient()) {
            MinecraftForgeClient.registerItemRenderer((Item)tihar, (IItemRenderer)new Tihar_Render());
        }
        ak2012 = new ItemGun(false, 15.0, 1, 1.0f, "ak2012", "ak74_reload", 1.0f, 1.5f, 40, 1, 0.0f, -0.5f, "ak2012", 60, 25.0, 30.0f, 1200).func_77637_a(Main.gunTab).func_77625_d(1).func_77655_b("ak2012").func_111206_d("metro:ak2012");
        GameRegistry.registerItem((Item)ak2012, (String)"ak2012");
        LanguageRegistry.addName((Object)ak2012, (String)"\u0410\u041a-2012");
        if (event.getSide().isClient()) {
            MinecraftForgeClient.registerItemRenderer((Item)ak2012, (IItemRenderer)new ak2012_Render());
        }
        ak74u = new ItemGun(false, 12.0, 1, 1.0f, "ak_boom", "ak74_reload", 1.0f, 1.5f, 30, 1, 1.0f, -1.0f, "ak74u", 60, 25.0, 30.0f, 1200).func_77637_a(Main.gunTab).func_77625_d(1).func_77655_b("ak74u").func_111206_d("metro:ak74u");
        GameRegistry.registerItem((Item)ak74u, (String)"ak74u");
        LanguageRegistry.addName((Object)ak74u, (String)"\u0410\u041a-74\u0423 ");
        if (event.getSide().isClient()) {
            MinecraftForgeClient.registerItemRenderer((Item)ak74u, (IItemRenderer)new ak74u_Render());
        }
        rpk = new ItemGun(false, 17.0, 1, 2.0f, "ak2012", "ak74_reload", 1.0f, 1.5f, 45, 1, 1.0f, -1.0f, "rpk", 60, 25.0, 30.0f, 2200).func_77637_a(Main.gunTab).func_77625_d(1).func_77655_b("rpk").func_111206_d("metro:rpk");
        GameRegistry.registerItem((Item)rpk, (String)"rpk");
        LanguageRegistry.addName((Object)rpk, (String)"\u0420\u041f\u041a ");
        if (event.getSide().isClient()) {
            MinecraftForgeClient.registerItemRenderer((Item)rpk, (IItemRenderer)new rpk_Render());
        }
        helsing = new ItemGun(true, 26.0, 2, 15.0f, "helsing_shoot", "helsing_reload", 0.0f, 1.5f, 8, 7, 1.0f, -1.0f, "preved", 75, 25.0, 30.0f, 5000).func_77637_a(Main.gunTab).func_77625_d(1).func_77655_b("helsing").func_111206_d("metro:helsing");
        GameRegistry.registerItem((Item)helsing, (String)"helsing");
        LanguageRegistry.addName((Object)helsing, (String)"\u0425\u0435\u043b\u044c\u0441\u0438\u043d\u0433");
        if (event.getSide().isClient()) {
            MinecraftForgeClient.registerItemRenderer((Item)helsing, (IItemRenderer)new Helsing_Render());
        }
    }
}
