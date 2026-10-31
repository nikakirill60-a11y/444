/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.common.registry.LanguageRegistry
 *  metro.common.blocks.AirBlock
 *  metro.common.blocks.CollisionBlock
 *  metro.common.blocks.tileentity.TileEntityAir
 *  metro.common.blocks.tileentity.TileEntityHudrant
 *  metro.common.blocks.tileentity.TileEntityRad
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 */
package metro.common.regstration;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import metro.common.ClientProxy;
import metro.common.blocks.AirBlock;
import metro.common.blocks.CollisionBlock;
import metro.common.blocks.HudrantBlock;
import metro.common.blocks.RadBlock;
import metro.common.blocks.RegBlock;
import metro.common.blocks.tileentity.TileEntityAir;
import metro.common.blocks.tileentity.TileEntityHudrant;
import metro.common.blocks.tileentity.TileEntityRad;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class RegisterBlocks {
    public static Block afishapost;
    public static Block bag_line;
    public static Block bag_line_short;
    public static Block barrel_01;
    public static Block barrel_01_close;
    public static Block barrel_03_garbage;
    public static Block barrel_04_broken;
    public static Block bench_2;
    public static Block bench_6;
    public static Block cafe_chair;
    public static Block car_b;
    public static Block car_f;
    public static Block car_t;
    public static Block car_w;
    public static Block chair;
    public static Block chair_01;
    public static Block chair_cabinet;
    public static Block covers_cart_010;
    public static Block covers_lump_010;
    public static Block covers_lump_012;
    public static Block covers_strongbox_010;
    public static Block covers_woodcase_010;
    public static Block divan;
    public static Block drizina_pricep_test;
    public static Block engine_turbo_01;
    public static Block fireplace_01;
    public static Block g_1;
    public static Block germogates;
    public static Block kachelka_2;
    public static Block kitchen_tv;
    public static Block krest;
    public static Block l04_lampion04;
    public static Block lenin;
    public static Block metro_sign;
    public static Block monitor_80s_00;
    public static Block musor_bidon_01;
    public static Block patron_box_full;
    public static Block plastic_table_01;
    public static Block sartir_kabinka;
    public static Block table;
    public static Block table_benches;
    public static Block table_cafe_tkan;
    public static Block table_cafe01;
    public static Block table_nastil_1;
    public static Block table_selfmade;
    public static Block televizor_br;
    public static Block temp_regina;
    public static Block tree_kashtan;
    public static Block trees_03_;
    public static Block tunnels_props_burn01;
    public static Block tunnels_props_r1_01;
    public static Block tunnels_props_wood_tupik_01;
    public static Block umivalnik1;
    public static Block vanna;
    public static Block veh_bus;
    public static Block veh_trolleybus;
    public static Block veh_uaz_452;
    public static Block washing_mashine_01;
    public static Block wc_urinal_010;
    public static Block collision;
    public static Block rad_block;
    public static Block air_block;
    public static Block reg_block;

    public static void load(FMLPreInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityHudrant.class, (String)"TileEntityHudrant");
        afishapost = new HudrantBlock(Material.field_151576_e, "afishapost", "afishapost", 1.0f).func_149663_c("afishapost");
        GameRegistry.registerBlock((Block)afishapost, (String)"afishapost");
        LanguageRegistry.addName((Object)afishapost, (String)"\u0410\u0444\u0438\u0448\u0430");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)afishapost);
        }
        bag_line = new HudrantBlock(Material.field_151576_e, "bag_line", "bag_line", 1.0f).func_149663_c("bag_line");
        GameRegistry.registerBlock((Block)bag_line, (String)"bag_line");
        LanguageRegistry.addName((Object)bag_line, (String)"\u041c\u0435\u0448\u043a\u0438 \u0441 \u043f\u0435\u0441\u043a\u043e\u043c (\u0434\u043b\u0438\u043d\u043d\u044b\u0439 \u0440\u044f\u0434)");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)bag_line);
        }
        bag_line_short = new HudrantBlock(Material.field_151576_e, "bag_line_short", "bag_line_short", 1.0f).func_149663_c("bag_line");
        GameRegistry.registerBlock((Block)bag_line_short, (String)"bag_line_short");
        LanguageRegistry.addName((Object)bag_line_short, (String)"\u041c\u0435\u0448\u043a\u0438 \u0441 \u043f\u0435\u0441\u043a\u043e\u043c (\u043a\u043e\u0440\u043e\u0442\u043a\u0438\u0439 \u0440\u044f\u0434)");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)bag_line_short);
        }
        barrel_01 = new HudrantBlock(Material.field_151576_e, "barrel_01", "barrel_01", 1.0f).func_149663_c("barrel_01");
        GameRegistry.registerBlock((Block)barrel_01, (String)"barrel_01");
        LanguageRegistry.addName((Object)barrel_01, (String)"\u0411\u043e\u0447\u043a\u0430 \u0441 \u043a\u043e\u0441\u0442\u0440\u043e\u043c");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)barrel_01);
        }
        barrel_01_close = new HudrantBlock(Material.field_151576_e, "barrel_01_close", "barrel_01_close", 1.0f).func_149663_c("barrel_01_close");
        GameRegistry.registerBlock((Block)barrel_01_close, (String)"barrel_01_close");
        LanguageRegistry.addName((Object)barrel_01_close, (String)"\u0411\u043e\u0447\u043a\u0430 \u0441 \u043a\u043e\u0441\u0442\u0440\u043e\u043c (\u0437\u0430\u043a\u0440\u044b\u0442\u0430\u044f)");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)barrel_01_close);
        }
        barrel_03_garbage = new HudrantBlock(Material.field_151576_e, "barrel_03_garbage", "barrel_03_garbage", 1.0f).func_149663_c("barrel_03_garbage");
        GameRegistry.registerBlock((Block)barrel_03_garbage, (String)"barrel_03_garbage");
        LanguageRegistry.addName((Object)barrel_03_garbage, (String)"\u0411\u043e\u0447\u043a\u0430 \u0441 \u043c\u0443\u0441\u043e\u0440\u043e\u043c");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)barrel_03_garbage);
        }
        barrel_04_broken = new HudrantBlock(Material.field_151576_e, "barrel_04_broken", "barrel_04_broken", 1.0f).func_149663_c("barrel_04_broken");
        GameRegistry.registerBlock((Block)barrel_04_broken, (String)"barrel_04_broken");
        LanguageRegistry.addName((Object)barrel_04_broken, (String)"\u041c\u044f\u0442\u0430\u044f \u0431\u043e\u0447\u043a\u0430");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)barrel_04_broken);
        }
        bench_2 = new HudrantBlock(Material.field_151576_e, "bench_2", "bench_2", 1.0f).func_149663_c("bench_2");
        GameRegistry.registerBlock((Block)bench_2, (String)"bench_2");
        LanguageRegistry.addName((Object)bench_2, (String)"\u0421\u043a\u0430\u043c\u0435\u0439\u043a\u0430 \u0446\u0435\u043b\u0430\u044f");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)bench_2);
        }
        bench_6 = new HudrantBlock(Material.field_151576_e, "bench_6", "bench_6", 1.0f).func_149663_c("bench_6");
        GameRegistry.registerBlock((Block)bench_6, (String)"bench_6");
        LanguageRegistry.addName((Object)bench_6, (String)"\u0421\u043a\u0430\u043c\u0435\u0439\u043a\u0430 \u0441\u043b\u043e\u043c\u0430\u043d\u043d\u0430\u044f");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)bench_6);
        }
        cafe_chair = new HudrantBlock(Material.field_151576_e, "cafe_chair", "cafe_chair", 1.0f).func_149663_c("cafe_chair");
        GameRegistry.registerBlock((Block)cafe_chair, (String)"cafe_chair");
        LanguageRegistry.addName((Object)cafe_chair, (String)"\u0412\u044b\u0441\u043e\u043a\u0438\u0439 \u0442\u0430\u0431\u0443\u0440\u0435\u0442");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)cafe_chair);
        }
        car_b = new HudrantBlock(Material.field_151576_e, "car_b", "car_b", 1.0f).func_149663_c("car_b");
        GameRegistry.registerBlock((Block)car_b, (String)"car_b");
        LanguageRegistry.addName((Object)car_b, (String)"\u0411\u041c\u0412 \u04255");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)car_b);
        }
        car_f = new HudrantBlock(Material.field_151576_e, "car_f", "car_f", 1.0f).func_149663_c("car_f");
        GameRegistry.registerBlock((Block)car_f, (String)"car_f");
        LanguageRegistry.addName((Object)car_f, (String)"\u0412\u0410\u0417-2110");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)car_f);
        }
        car_t = new HudrantBlock(Material.field_151576_e, "car_t", "car_t", 1.0f).func_149663_c("car_t");
        GameRegistry.registerBlock((Block)car_t, (String)"car_t");
        LanguageRegistry.addName((Object)car_t, (String)"\u041c\u0410\u0417");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)car_t);
        }
        car_w = new HudrantBlock(Material.field_151576_e, "car_w", "car_w", 1.0f).func_149663_c("car_w");
        GameRegistry.registerBlock((Block)car_w, (String)"car_w");
        LanguageRegistry.addName((Object)car_w, (String)"\u0418\u0416 \u041e\u0434\u0430");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)car_w);
        }
        chair = new HudrantBlock(Material.field_151576_e, "chair", "chair", 1.0f).func_149663_c("chair");
        GameRegistry.registerBlock((Block)chair, (String)"chair");
        LanguageRegistry.addName((Object)chair, (String)"\u0428\u043a\u043e\u043b\u044c\u043d\u044b\u0439 \u0441\u0442\u0443\u043b");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)chair);
        }
        chair_01 = new HudrantBlock(Material.field_151576_e, "chair_01", "chair_01", 1.0f).func_149663_c("chair_01");
        GameRegistry.registerBlock((Block)chair_01, (String)"chair_01");
        LanguageRegistry.addName((Object)chair_01, (String)"\u0421\u0442\u0443\u043b");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)chair_01);
        }
        chair_cabinet = new HudrantBlock(Material.field_151576_e, "chair_cabinet", "chair_cabinet", 1.0f).func_149663_c("chair_cabinet");
        GameRegistry.registerBlock((Block)chair_cabinet, (String)"chair_cabinet");
        LanguageRegistry.addName((Object)chair_cabinet, (String)"\u041e\u0444\u0438\u0441\u043d\u043e\u0435 \u043a\u0440\u0435\u0441\u043b\u043e");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)chair_cabinet);
        }
        covers_cart_010 = new HudrantBlock(Material.field_151576_e, "covers_cart_010", "covers_cart_010", 1.0f).func_149663_c("covers_cart_010");
        GameRegistry.registerBlock((Block)covers_cart_010, (String)"covers_cart_010");
        LanguageRegistry.addName((Object)covers_cart_010, (String)"\u0417\u0435\u043b\u0435\u043d\u044b\u0439 \u0436\u0435\u043b\u0435\u0437\u043d\u044b\u0439 \u0448\u043a\u0430\u0444");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)covers_cart_010);
        }
        covers_lump_010 = new HudrantBlock(Material.field_151576_e, "covers_lump_010", "covers_lump_010", 1.0f).func_149663_c("covers_lump_010");
        GameRegistry.registerBlock((Block)covers_lump_010, (String)"covers_lump_010");
        LanguageRegistry.addName((Object)covers_lump_010, (String)"\u0414\u0435\u0440\u0435\u0432\u044f\u043d\u043d\u044b\u0435 \u044f\u0449\u0438\u043a\u0438 1");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)covers_lump_010);
        }
        covers_lump_012 = new HudrantBlock(Material.field_151576_e, "covers_lump_012", "covers_lump_012", 1.0f).func_149663_c("covers_lump_012");
        GameRegistry.registerBlock((Block)covers_lump_012, (String)"covers_lump_012");
        LanguageRegistry.addName((Object)covers_lump_012, (String)"\u0414\u0435\u0440\u0435\u0432\u044f\u043d\u043d\u044b\u0435 \u044f\u0449\u0438\u043a\u0438 2");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)covers_lump_012);
        }
        covers_strongbox_010 = new HudrantBlock(Material.field_151576_e, "covers_strongbox_010", "covers_strongbox_010", 1.0f).func_149663_c("covers_strongbox_010");
        GameRegistry.registerBlock((Block)covers_strongbox_010, (String)"covers_strongbox_010");
        LanguageRegistry.addName((Object)covers_strongbox_010, (String)"\u041a\u043e\u0440\u043e\u0431 \u043c\u0435\u0442\u0430\u043b\u043b");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)covers_strongbox_010);
        }
        covers_woodcase_010 = new HudrantBlock(Material.field_151576_e, "covers_woodcase_010", "covers_woodcase_010", 1.0f).func_149663_c("covers_woodcase_010");
        GameRegistry.registerBlock((Block)covers_woodcase_010, (String)"covers_woodcase_010");
        LanguageRegistry.addName((Object)covers_woodcase_010, (String)"\u041a\u043e\u0440\u043e\u0431 \u0434\u0435\u0440\u0435\u0432\u044f\u043d\u043d\u044b\u0439");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)covers_woodcase_010);
        }
        divan = new HudrantBlock(Material.field_151576_e, "divan", "divan", 1.0f).func_149663_c("divan");
        GameRegistry.registerBlock((Block)divan, (String)"divan");
        LanguageRegistry.addName((Object)divan, (String)"\u0414\u0438\u0432\u0430\u043d");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)divan);
        }
        drizina_pricep_test = new HudrantBlock(Material.field_151576_e, "drizina_pricep_test", "drizina_pricep_test", 1.0f).func_149663_c("drizina_pricep_test");
        GameRegistry.registerBlock((Block)drizina_pricep_test, (String)"drizina_pricep_test");
        LanguageRegistry.addName((Object)drizina_pricep_test, (String)"\u0414\u0440\u0435\u0437\u0438\u043d\u0430 \u043f\u0440\u0438\u0446\u0435\u043f");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)drizina_pricep_test);
        }
        engine_turbo_01 = new HudrantBlock(Material.field_151576_e, "engine_turbo_01", "engine_turbo_01", 1.0f).func_149663_c("engine_turbo_01");
        GameRegistry.registerBlock((Block)engine_turbo_01, (String)"engine_turbo_01");
        LanguageRegistry.addName((Object)engine_turbo_01, (String)"\u0414\u0438\u0437\u0435\u043b\u044c \u0433\u0435\u043d\u0435\u0440\u0430\u0442\u043e\u0440");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)engine_turbo_01);
        }
        fireplace_01 = new HudrantBlock(Material.field_151576_e, "fireplace_01", "fireplace_01", 1.0f).func_149663_c("fireplace_01");
        GameRegistry.registerBlock((Block)fireplace_01, (String)"fireplace_01");
        LanguageRegistry.addName((Object)fireplace_01, (String)"\u041a\u043e\u0441\u0442\u0435\u0440");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)fireplace_01);
        }
        g_1 = new HudrantBlock(Material.field_151576_e, "g_1", "g_1", 1.0f).func_149663_c("g_1");
        GameRegistry.registerBlock((Block)g_1, (String)"g_1");
        LanguageRegistry.addName((Object)g_1, (String)"\u0413\u0410\u0417-3110");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)g_1);
        }
        germogates = new HudrantBlock(Material.field_151576_e, "germogates", "germogates", 1.0f).func_149663_c("germogates");
        GameRegistry.registerBlock((Block)germogates, (String)"germogates");
        LanguageRegistry.addName((Object)germogates, (String)"\u0413\u0435\u0440\u043c\u043e\u0432\u043e\u0440\u043e\u0442\u0430 (\u0441\u0442\u0430\u0442\u0438\u0447\u043d\u044b\u0435)");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)germogates);
        }
        kachelka_2 = new HudrantBlock(Material.field_151576_e, "kachelka_2", "kachelka_2", 1.0f).func_149663_c("kachelka_2");
        GameRegistry.registerBlock((Block)kachelka_2, (String)"kachelka_2");
        LanguageRegistry.addName((Object)kachelka_2, (String)"\u041a\u0430\u0447\u0435\u043b\u0438 '\u0432\u0435\u0441\u044b'");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)kachelka_2);
        }
        kitchen_tv = new HudrantBlock(Material.field_151576_e, "kitchen_tv", "kitchen_tv", 1.0f).func_149663_c("kitchen_tv");
        GameRegistry.registerBlock((Block)kitchen_tv, (String)"kitchen_tv");
        LanguageRegistry.addName((Object)kitchen_tv, (String)"\u041a\u0443\u0445\u043e\u043d\u043d\u044b\u0439 \u0442\u0435\u043b\u0435\u0432\u0438\u0437\u043e\u0440");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)kitchen_tv);
        }
        krest = new HudrantBlock(Material.field_151576_e, "krest", "krest", 1.0f).func_149663_c("krest");
        GameRegistry.registerBlock((Block)krest, (String)"krest");
        LanguageRegistry.addName((Object)krest, (String)"\u041c\u0435\u0442\u0430\u043b\u043b\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u043a\u0440\u0435\u0441\u0442");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)krest);
        }
        l04_lampion04 = new HudrantBlock(Material.field_151576_e, "l04_lampion04", "l04_lampion04", 1.0f).func_149663_c("l04_lampion04");
        GameRegistry.registerBlock((Block)l04_lampion04, (String)"l04_lampion04");
        LanguageRegistry.addName((Object)l04_lampion04, (String)"\u0421\u0432\u0435\u0447\u043a\u0430");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)l04_lampion04);
        }
        lenin = new HudrantBlock(Material.field_151576_e, "lenin", "lenin", 1.0f).func_149663_c("lenin");
        GameRegistry.registerBlock((Block)lenin, (String)"lenin");
        LanguageRegistry.addName((Object)lenin, (String)"\u041b\u0435\u043d\u0438\u043d");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)lenin);
        }
        metro_sign = new HudrantBlock(Material.field_151576_e, "metro_sign", "metro_sign", 1.0f).func_149663_c("metro_sign");
        GameRegistry.registerBlock((Block)metro_sign, (String)"metro_sign");
        LanguageRegistry.addName((Object)metro_sign, (String)"\u0417\u043d\u0430\u043a \u043c\u0435\u0442\u0440\u043e");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)metro_sign);
        }
        monitor_80s_00 = new HudrantBlock(Material.field_151576_e, "monitor_80s_00", "monitor_80s_00", 1.0f).func_149663_c("monitor_80s_00");
        GameRegistry.registerBlock((Block)monitor_80s_00, (String)"monitor_80s_00");
        LanguageRegistry.addName((Object)monitor_80s_00, (String)"\u041c\u043e\u043d\u0438\u0442\u043e\u0440 \u0441\u0442\u0430\u0440\u043e\u0433\u043e \u043e\u0431\u0440\u0430\u0437\u0446\u0430");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)monitor_80s_00);
        }
        musor_bidon_01 = new HudrantBlock(Material.field_151576_e, "musor_bidon_01", "musor_bidon_01", 1.0f).func_149663_c("musor_bidon_01");
        GameRegistry.registerBlock((Block)musor_bidon_01, (String)"musor_bidon_01");
        LanguageRegistry.addName((Object)musor_bidon_01, (String)"\u041c\u0443\u0441\u043e\u0440\u043d\u044b\u0439 \u0431\u0438\u0434\u043e\u043d");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)musor_bidon_01);
        }
        patron_box_full = new HudrantBlock(Material.field_151576_e, "patron_box_full", "patron_box_full", 1.0f).func_149663_c("patron_box_full");
        GameRegistry.registerBlock((Block)patron_box_full, (String)"patron_box_full");
        LanguageRegistry.addName((Object)patron_box_full, (String)"\u041a\u043e\u0440\u043e\u0431\u043e\u0447\u043a\u0430 \u0441 \u043f\u0430\u0442\u0440\u043e\u043d\u0430\u043c\u0438");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)patron_box_full);
        }
        plastic_table_01 = new HudrantBlock(Material.field_151576_e, "plastic_table_01", "plastic_table_01", 1.0f).func_149663_c("plastic_table_01");
        GameRegistry.registerBlock((Block)plastic_table_01, (String)"plastic_table_01");
        LanguageRegistry.addName((Object)plastic_table_01, (String)"\u041f\u043b\u0430\u0441\u0442\u0438\u043a\u043e\u0432\u044b\u0439 \u0441\u0442\u043e\u043b");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)plastic_table_01);
        }
        sartir_kabinka = new HudrantBlock(Material.field_151576_e, "sartir_kabinka", "sartir_kabinka", 1.0f).func_149663_c("sartir_kabinka");
        GameRegistry.registerBlock((Block)sartir_kabinka, (String)"sartir_kabinka");
        LanguageRegistry.addName((Object)sartir_kabinka, (String)"\u0421\u0430\u0440\u0442\u0438\u0440");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)sartir_kabinka);
        }
        table = new HudrantBlock(Material.field_151576_e, "table", "table", 1.0f).func_149663_c("table");
        GameRegistry.registerBlock((Block)table, (String)"table");
        LanguageRegistry.addName((Object)table, (String)"\u0421\u0442\u043e\u043b");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)table);
        }
        table_benches = new HudrantBlock(Material.field_151576_e, "table_benches", "table_benches", 1.0f).func_149663_c("table_benches");
        GameRegistry.registerBlock((Block)table_benches, (String)"table_benches");
        LanguageRegistry.addName((Object)table_benches, (String)"\u0421\u0442\u043e\u043b \u0438\u0437 \u0434\u043e\u0441\u043e\u043a");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)table_benches);
        }
        table_cafe_tkan = new HudrantBlock(Material.field_151576_e, "table_cafe_tkan", "table_cafe_tkan", 1.0f).func_149663_c("table_cafe_tkan");
        GameRegistry.registerBlock((Block)table_cafe_tkan, (String)"table_cafe_tkan");
        LanguageRegistry.addName((Object)table_cafe_tkan, (String)"\u0421\u0442\u043e\u043b \u043d\u0430\u043a\u0440\u044b\u0442\u044b\u0439 \u043a\u0440\u0430\u0441\u043d\u043e\u0439 \u0441\u043a\u0430\u0442\u0435\u0440\u0442\u044c\u044e");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)table_cafe_tkan);
        }
        table_cafe01 = new HudrantBlock(Material.field_151576_e, "table_cafe01", "table_cafe01", 1.0f).func_149663_c("table_cafe01");
        GameRegistry.registerBlock((Block)table_cafe01, (String)"table_cafe01");
        LanguageRegistry.addName((Object)table_cafe01, (String)"\u0421\u0442\u043e\u043b \u043a\u0430\u0444\u0435");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)table_cafe01);
        }
        table_nastil_1 = new HudrantBlock(Material.field_151576_e, "table_nastil_1", "table_nastil_1", 1.0f).func_149663_c("table_nastil_1");
        GameRegistry.registerBlock((Block)table_nastil_1, (String)"table_nastil_1");
        LanguageRegistry.addName((Object)table_nastil_1, (String)"\u0421\u0442\u043e\u043b \u0438\u0437 \u0434\u0435\u0440\u0435\u0432\u044f\u043d\u043d\u043e\u0433\u043e \u043d\u0430\u0441\u0442\u0438\u043b\u0430");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)table_nastil_1);
        }
        table_selfmade = new HudrantBlock(Material.field_151576_e, "table_selfmade", "table_selfmade", 1.0f).func_149663_c("table_selfmade");
        GameRegistry.registerBlock((Block)table_selfmade, (String)"table_selfmade");
        LanguageRegistry.addName((Object)table_selfmade, (String)"\u0421\u0430\u043c\u043e\u0434\u0435\u043b\u044c\u043d\u044b\u0439 \u0441\u0442\u043e\u043b");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)table_selfmade);
        }
        televizor_br = new HudrantBlock(Material.field_151576_e, "televizor_br", "televizor_br", 1.0f).func_149663_c("televizor_br");
        GameRegistry.registerBlock((Block)televizor_br, (String)"televizor_br");
        LanguageRegistry.addName((Object)televizor_br, (String)"\u0422\u0435\u043b\u0435\u0432\u0438\u0437\u043e\u0440");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)televizor_br);
        }
        temp_regina = new HudrantBlock(Material.field_151576_e, "temp_regina", "temp_regina", 1.0f).func_149663_c("temp_regina");
        GameRegistry.registerBlock((Block)temp_regina, (String)"temp_regina");
        LanguageRegistry.addName((Object)temp_regina, (String)"\u041e\u0441\u0442\u043e\u0432 \u0420\u0435\u0433\u0438\u043d\u044b");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)temp_regina);
        }
        tree_kashtan = new HudrantBlock(Material.field_151576_e, "tree_kashtan", "tree_kashtan", 0.35f).func_149663_c("tree_kashtan");
        GameRegistry.registerBlock((Block)tree_kashtan, (String)"tree_kashtan");
        LanguageRegistry.addName((Object)tree_kashtan, (String)"\u0414\u0435\u0440\u0435\u0432\u043e \u043a\u0430\u0448\u0442\u0430\u043d");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)tree_kashtan);
        }
        trees_03_ = new HudrantBlock(Material.field_151576_e, "trees_03_", "trees_03_", 1.0f).func_149663_c("trees_03_");
        GameRegistry.registerBlock((Block)trees_03_, (String)"trees_03_");
        LanguageRegistry.addName((Object)trees_03_, (String)"\u0414\u0435\u0440\u0435\u0432\u043e");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)trees_03_);
        }
        tunnels_props_burn01 = new HudrantBlock(Material.field_151576_e, "tunnels_props_burn01", "tunnels_props_burn01", 1.0f).func_149663_c("tunnels_props_burn01");
        GameRegistry.registerBlock((Block)tunnels_props_burn01, (String)"tunnels_props_burn01");
        LanguageRegistry.addName((Object)tunnels_props_burn01, (String)"\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u0438\u0437 \u0431\u0440\u0435\u0432\u0435\u043d");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)tunnels_props_burn01);
        }
        tunnels_props_r1_01 = new HudrantBlock(Material.field_151576_e, "tunnels_props_r1_01", "tunnels_props_r1_01", 1.0f).func_149663_c("tunnels_props_r1_01");
        GameRegistry.registerBlock((Block)tunnels_props_r1_01, (String)"tunnels_props_r1_01");
        LanguageRegistry.addName((Object)tunnels_props_r1_01, (String)"\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u0438\u0437 \u0434\u043e\u0441\u043e\u043a");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)tunnels_props_r1_01);
        }
        tunnels_props_wood_tupik_01 = new HudrantBlock(Material.field_151576_e, "tunnels_props_wood_tupik_01", "tunnels_props_wood_tupik_01", 1.0f).func_149663_c("tunnels_props_wood_tupik_01");
        GameRegistry.registerBlock((Block)tunnels_props_wood_tupik_01, (String)"tunnels_props_wood_tupik_01");
        LanguageRegistry.addName((Object)tunnels_props_wood_tupik_01, (String)"\u041a\u043e\u043d\u0435\u0446 \u043f\u0443\u0442\u0438 \u0438\u0437 \u0431\u0440\u0435\u0432\u0435\u043d (\u0442\u0443\u043f\u0438\u043a)");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)tunnels_props_wood_tupik_01);
        }
        umivalnik1 = new HudrantBlock(Material.field_151576_e, "umivalnik1", "umivalnik1", 1.0f).func_149663_c("umivalnik1");
        GameRegistry.registerBlock((Block)umivalnik1, (String)"umivalnik1");
        LanguageRegistry.addName((Object)umivalnik1, (String)"\u0423\u043c\u044b\u0432\u0430\u043b\u044c\u043d\u0438\u043a");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)umivalnik1);
        }
        vanna = new HudrantBlock(Material.field_151576_e, "vanna", "vanna", 1.0f).func_149663_c("vanna");
        GameRegistry.registerBlock((Block)vanna, (String)"vanna");
        LanguageRegistry.addName((Object)vanna, (String)"\u0412\u0430\u043d\u043d\u0430");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)vanna);
        }
        veh_bus = new HudrantBlock(Material.field_151576_e, "veh_bus", "veh_bus", 1.0f).func_149663_c("veh_bus");
        GameRegistry.registerBlock((Block)veh_bus, (String)"veh_bus");
        LanguageRegistry.addName((Object)veh_bus, (String)"\u0410\u0432\u0442\u043e\u0431\u0443\u0441");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)veh_bus);
        }
        veh_trolleybus = new HudrantBlock(Material.field_151576_e, "veh_trolleybus", "veh_trolleybus", 1.0f).func_149663_c("veh_trolleybus");
        GameRegistry.registerBlock((Block)veh_trolleybus, (String)"veh_trolleybus");
        LanguageRegistry.addName((Object)veh_trolleybus, (String)"\u0422\u0440\u043e\u043b\u0435\u0439\u0431\u0443\u0441");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)veh_trolleybus);
        }
        veh_uaz_452 = new HudrantBlock(Material.field_151576_e, "veh_uaz_452", "veh_uaz_452", 1.0f).func_149663_c("veh_uaz_452");
        GameRegistry.registerBlock((Block)veh_uaz_452, (String)"veh_uaz_452");
        LanguageRegistry.addName((Object)veh_uaz_452, (String)"\u0423\u0410\u0417 \u0411\u0443\u0445\u0430\u043d\u043a\u0430");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)veh_uaz_452);
        }
        washing_mashine_01 = new HudrantBlock(Material.field_151576_e, "washing_mashine_01", "washing_mashine_01", 1.0f).func_149663_c("washing_mashine_01");
        GameRegistry.registerBlock((Block)washing_mashine_01, (String)"washing_mashine_01");
        LanguageRegistry.addName((Object)washing_mashine_01, (String)"\u0421\u0442\u0438\u0440\u0430\u043b\u044c\u043d\u0430\u044f \u043c\u0430\u0448\u0438\u043d\u0430");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)washing_mashine_01);
        }
        wc_urinal_010 = new HudrantBlock(Material.field_151576_e, "wc_urinal_010", "wc_urinal_010", 1.0f).func_149663_c("wc_urinal_010");
        GameRegistry.registerBlock((Block)wc_urinal_010, (String)"wc_urinal_010");
        LanguageRegistry.addName((Object)wc_urinal_010, (String)"\u041f\u0438\u0441\u0443\u0430\u0440");
        if (event.getSide().isClient()) {
            ClientProxy.blocksdekor.add((HudrantBlock)wc_urinal_010);
        }
        collision = new CollisionBlock(Material.field_151576_e).func_149663_c("wc_urinal_010");
        GameRegistry.registerBlock((Block)collision, (String)"collision");
        LanguageRegistry.addName((Object)collision, (String)"\u041a\u043e\u043b\u043b\u0438\u0437\u0438\u044f");
        GameRegistry.registerTileEntity(TileEntityRad.class, (String)"TileEntityRad");
        rad_block = new RadBlock(Material.field_151576_e, "wc_urinal_010", "wc_urinal_010", 1.0f).func_149663_c("rad_block");
        GameRegistry.registerBlock((Block)rad_block, (String)"rad_block");
        LanguageRegistry.addName((Object)rad_block, (String)"\u0411\u043b\u043e\u043a \u0440\u0430\u0434\u0438\u0430\u0446\u0438\u0438");
        GameRegistry.registerTileEntity(TileEntityAir.class, (String)"TileEntityAir");
        air_block = new AirBlock(Material.field_151576_e, "wc_urinal_010", "wc_urinal_010", 1.0f).func_149663_c("air_block");
        GameRegistry.registerBlock((Block)air_block, (String)"air_block");
        LanguageRegistry.addName((Object)air_block, (String)"\u0411\u043b\u043e\u043a \u0432\u043e\u0437\u0434\u0443\u0445\u0430");
        reg_block = new RegBlock(Material.field_151576_e).func_149663_c("reg_block");
        GameRegistry.registerBlock((Block)reg_block, (String)"reg_block");
        LanguageRegistry.addName((Object)reg_block, (String)"\u0411\u043b\u043e\u043a \u0440\u0435\u0433\u0438\u0441\u0442\u0440\u0430\u0446\u0438\u0438");
    }
}
