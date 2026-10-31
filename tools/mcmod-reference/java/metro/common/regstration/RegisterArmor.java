/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.common.registry.LanguageRegistry
 *  net.minecraft.item.Item
 */
package metro.common.regstration;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import metro.common.ItemCustomArmor;
import metro.common.Main;
import net.minecraft.item.Item;

public class RegisterArmor {
    public static Item gasmask = new ItemCustomArmor(300, 0, 1, 0).func_77655_b("gasmask").func_111206_d("metro:gasmask").func_77637_a(Main.armorTab);
    public static Item hanza = new ItemCustomArmor(1350, 1, 2, 55).func_77655_b("hanza").func_111206_d("metro:hanzaz").func_77637_a(Main.armorTab);
    public static Item himza = new ItemCustomArmor(1200, 1, 3, 10).func_77655_b("himza").func_111206_d("metro:himza").func_77637_a(Main.armorTab);
    public static Item officer_ozk = new ItemCustomArmor(1500, 1, 4, 22).func_77655_b("officer_ozk").func_111206_d("metro:officer_ozk").func_77637_a(Main.armorTab);
    public static Item ozk = new ItemCustomArmor(1150, 1, 5, 10).func_77655_b("ozk").func_111206_d("metro:ozk").func_77637_a(Main.armorTab);
    public static Item povstanec = new ItemCustomArmor(3500, 1, 6, 50).func_77655_b("povstanec").func_111206_d("metro:povstanec").func_77637_a(Main.armorTab);
    public static Item psh1b = new ItemCustomArmor(2500, 1, 7, 45).func_77655_b("psh1b").func_111206_d("metro:psh1b").func_77637_a(Main.armorTab);
    public static Item Scout_armor = new ItemCustomArmor(2500, 1, 8, 48).func_77655_b("Scout_armor").func_111206_d("metro:Scout_armor").func_77637_a(Main.armorTab);
    public static Item sverchok_mp6 = new ItemCustomArmor(4000, 1, 9, 55).func_77655_b("sverchok_mp6").func_111206_d("metro:sverchok_mp6").func_77637_a(Main.armorTab);
    public static Item bandit_armor;
    public static Item banditkurtka;
    public static Item bkz;
    public static Item BTBKZ;
    public static Item ghillie;
    public static Item gorka;
    public static Item juggernaut;
    public static Item kitel;
    public static Item kurtka;
    public static Item olkhon;
    public static Item plash;
    public static Item police;
    public static Item sniper;
    public static Item sniper_camo;
    public static Item standart_k;
    public static Item sverchok;
    public static Item taiga;
    public static Item vatnik;
    public static Item voen_tactical;
    public static Item zapal;
    public static Item prorok;
    public static Item cherv;
    public static Item reih;
    public static Item mgu;

    public static void load(FMLPreInitializationEvent event) {
        GameRegistry.registerItem((Item)gasmask, (String)"gasmask");
        LanguageRegistry.addName((Object)gasmask, (String)"\u041f\u0440\u043e\u0442\u0438\u0432\u043e\u0433\u0430\u0437");
        GameRegistry.registerItem((Item)hanza, (String)"hanza");
        LanguageRegistry.addName((Object)hanza, (String)"\u0411\u0440\u043e\u043d\u044f \u0413\u0430\u043d\u0437\u044b");
        GameRegistry.registerItem((Item)himza, (String)"himza");
        LanguageRegistry.addName((Object)himza, (String)"\u0425\u0438\u043c\u0437\u0430");
        GameRegistry.registerItem((Item)officer_ozk, (String)"officer_ozk");
        LanguageRegistry.addName((Object)officer_ozk, (String)"\u041e\u0444\u0438\u0446\u0435\u0440\u0441\u043a\u0438\u0439 \u041e\u0417\u041a");
        GameRegistry.registerItem((Item)ozk, (String)"ozk");
        LanguageRegistry.addName((Object)ozk, (String)"\u041e\u0417\u041a");
        GameRegistry.registerItem((Item)povstanec, (String)"povstanec");
        LanguageRegistry.addName((Object)povstanec, (String)"\u0411\u0440\u043e\u043d\u044f \u041f\u043e\u0432\u0441\u0442\u0430\u043d\u0446\u0430");
        GameRegistry.registerItem((Item)psh1b, (String)"psh1b");
        LanguageRegistry.addName((Object)psh1b, (String)"\u041f\u0428-1\u0411");
        GameRegistry.registerItem((Item)Scout_armor, (String)"Scout_armor");
        LanguageRegistry.addName((Object)Scout_armor, (String)"\u0411\u0440\u043e\u043d\u044f \u0440\u0430\u0437\u0432\u0435\u0434\u0447\u0438\u043a\u0430");
        GameRegistry.registerItem((Item)sverchok_mp6, (String)"sverchok_mp6");
        LanguageRegistry.addName((Object)sverchok_mp6, (String)"\u0421\u0432\u0435\u0440\u0447\u043e\u043a \u041c\u041f-6");
        RegisterArmor.regArmor(900, bandit_armor, "bandit_armor", "\u0411\u0430\u043d\u0434\u0438\u0442\u0441\u043a\u0430\u044f \u0431\u0440\u043e\u043d\u044f", 10, 55);
        RegisterArmor.regArmor(500, banditkurtka, "banditkurtka", "\u0411\u0430\u043d\u0434\u0438\u0442\u0441\u043a\u0430\u044f \u043a\u0443\u0440\u0442\u043a\u0430", 11, 35);
        RegisterArmor.regArmor(3000, bkz, "bkz", "\u0411\u0440\u043e\u043d\u044f \u0411\u041a\u0417", 12, 60);
        RegisterArmor.regArmor(3000, BTBKZ, "BTBKZ", "\u0411\u0440\u043e\u043d\u044f \u0411\u0422\u0411\u041a\u0417", 13, 68);
        RegisterArmor.regArmor(800, ghillie, "ghillie", "\u041a\u0430\u043c\u0443\u0444\u043b\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u0430\u044f \u0431\u0440\u043e\u043d\u044f", 14, 27);
        RegisterArmor.regArmor(800, gorka, "gorka", "\u0413\u043e\u0440\u043a\u0430", 15, 25);
        RegisterArmor.regArmor(600, kitel, "kitel", "\u041a\u0438\u0442\u0435\u043b\u044c", 17, 16);
        RegisterArmor.regArmor(450, kurtka, "kurtka", "\u041a\u0443\u0440\u0442\u043a\u0430", 18, 19);
        RegisterArmor.regArmor(4000, olkhon, "olkhon", "\u041e\u043b\u044c\u0445\u043e\u043d", 19, 62);
        RegisterArmor.regArmor(800, plash, "plash", "\u041f\u043b\u0430\u0449", 20, 5);
        RegisterArmor.regArmor(600, police, "police", "\u041a\u0441\u0442\u044e\u043c \u043f\u043e\u043b\u0438\u0446\u0435\u0439\u0441\u043a\u043e\u0433\u043e", 21, 45);
        RegisterArmor.regArmor(1200, sniper, "sniper", "\u0421\u043d\u0430\u0439\u043f\u0435\u0440\u0441\u043a\u0438\u0439 \u043a\u043e\u0441\u0442\u044e\u043c", 22, 53);
        RegisterArmor.regArmor(800, sniper_camo, "sniper_camo", "\u0421\u043d\u0430\u0439\u043f\u0435\u0440\u0441\u043a\u0438\u0439 \u043a\u0430\u043c\u0443\u0444\u043b\u044f\u0436", 23, 55);
        RegisterArmor.regArmor(5500, standart_k, "standart_k", "\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442 \u041a", 24, 50);
        RegisterArmor.regArmor(4000, sverchok, "sverchok", "\u0421\u0432\u0435\u0440\u0447\u043e\u043a", 25, 53);
        RegisterArmor.regArmor(3500, taiga, "taiga", "\u041a\u043e\u0441\u0442\u044e\u043c \u0422\u0430\u0439\u0433\u0430", 26, 50);
        RegisterArmor.regArmor(300, vatnik, "vatnik", "\u0412\u0430\u0442\u043d\u0438\u043a", 27, 12);
        RegisterArmor.regArmor(4500, voen_tactical, "voen_tactical", "\u0412\u043e\u0435\u043d\u043d\u0430\u044f \u0442\u0430\u043a\u0442\u0438\u0447\u0435\u0441\u043a\u0430\u044f \u0431\u0440\u043e\u043d\u044f", 28, 60);
        GameRegistry.registerItem((Item)zapal, (String)"zapal");
        LanguageRegistry.addName((Object)zapal, (String)"\u041a\u0441\u0442\u044e\u043c \u0417\u0430\u043f\u0430\u043b");
        GameRegistry.registerItem((Item)juggernaut, (String)"juggernaut");
        LanguageRegistry.addName((Object)juggernaut, (String)"\u0414\u0436\u0435\u0433\u0435\u0440\u043d\u0430\u0443\u0442");
        GameRegistry.registerItem((Item)prorok, (String)"prorok");
        LanguageRegistry.addName((Object)prorok, (String)"\u041a\u043e\u043c\u0431\u0438\u043d\u0435\u0437\u043e\u043d \u043f\u0440\u043e\u0440\u043e\u043a");
        GameRegistry.registerItem((Item)cherv, (String)"cherv");
        LanguageRegistry.addName((Object)cherv, (String)"\u0411\u0440\u043e\u043d\u044f \u0447\u0435\u0440\u0432\u0435\u043f\u043e\u043a\u043b\u043e\u043d\u0438\u043a\u043e\u0432");
        GameRegistry.registerItem((Item)reih, (String)"reih");
        LanguageRegistry.addName((Object)reih, (String)"\u0411\u0440\u043e\u043d\u044f 4\u043e\u0433\u043e \u0440\u0435\u0439\u0445\u0430");
        GameRegistry.registerItem((Item)mgu, (String)"mgu");
        LanguageRegistry.addName((Object)mgu, (String)"\u0411\u0440\u043e\u043d\u044f \u041c\u0413\u0423");
    }

    static void regArmor(int condition, Item item, String name_id, String name, int armorID, int protect) {
        item = new ItemCustomArmor(condition, 1, armorID, protect).func_77655_b(name_id).func_111206_d("metro:" + name_id).func_77637_a(Main.armorTab);
        GameRegistry.registerItem((Item)item, (String)name_id);
        LanguageRegistry.addName((Object)item, (String)name);
    }

    static {
        juggernaut = new ItemCustomArmor(8000, 1, 16, 75).func_77655_b("juggernaut").func_111206_d("metro:juggernaut").func_77637_a(Main.armorTab);
        zapal = new ItemCustomArmor(4000, 1, 29, 70).func_77655_b("zapal").func_111206_d("metro:zapal").func_77637_a(Main.armorTab);
        prorok = new ItemCustomArmor(4000, 1, 30, 50).func_77655_b("prorok").func_111206_d("metro:prorok").func_77637_a(Main.armorTab);
        cherv = new ItemCustomArmor(4000, 1, 31, 50).func_77655_b("cherv").func_111206_d("metro:cherv").func_77637_a(Main.armorTab);
        reih = new ItemCustomArmor(4000, 1, 32, 50).func_77655_b("reih").func_111206_d("metro:reih").func_77637_a(Main.armorTab);
        mgu = new ItemCustomArmor(4000, 1, 33, 50).func_77655_b("mgu").func_111206_d("metro:mgu").func_77637_a(Main.armorTab);
    }
}
