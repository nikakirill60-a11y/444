/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.Mod
 *  cpw.mods.fml.common.Mod$EventHandler
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 */
package ru.metrocraft.stilian;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import ru.metrocraft.stilian.BlockTut;
import ru.metrocraft.stilian.TestPickAxe;

@Mod(modid="stilmetro", name="MetroCraft Stilian", version="1.1.0")
public class Main {
    public static final CreativeTabs tabMetroCraftBlo\u043f\u0457\u0405ks = new CreativeTabs("tabMetroCraftBlo\u043f\u0457\u0405ks"){

        public Item func_78016_d() {
            return Item.func_150898_a((Block)Blocks.field_150349_c);
        }
    };
    public static final String MODID = "stilmetro";
    public static final String MODNAME = "MetroCraft Stilian";
    public static final String VERSION = "1.1.0";
    public static Block blockWood;
    public static Item testpickaxe;

    @Mod.EventHandler
    public void preLoad(FMLPreInitializationEvent event) {
        blockWood = new BlockTut(Material.field_151575_d, "Block0", "0").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block1", "1").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block2", "2").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block3", "3").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block4", "4").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block5", "5").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block6", "6").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block7", "7").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block8", "8").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block9", "9").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block10", "10").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block11", "11").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block12", "12").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block13", "13").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block14", "14").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block15", "15").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block16", "16").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block17", "17").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block18", "18").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block19", "19").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block20", "20").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block21", "21").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block22", "22").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block23", "23").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block24", "24").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block25", "25").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block26", "26").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block27", "27").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block28", "28").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block29", "29").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block30", "30").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block31", "31").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block32", "32").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block33", "33").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block34", "34").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block35", "35").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block36", "36").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block37", "37").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block38", "38").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block39", "39").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block40", "40").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block41", "41").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block42", "42").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block43", "43").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block44", "44").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block45", "45").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block46", "46").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block47", "47").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block48", "48").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block49", "49").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block50", "50").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block51", "51").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block52", "52").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block53", "53").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block54", "54").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block55", "55").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block56", "56").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        blockWood = new BlockTut(Material.field_151575_d, "Block57", "57").func_149647_a(tabMetroCraftBlo\u043f\u0457\u0405ks).func_149711_c(2.0f);
        testpickaxe = new TestPickAxe().func_77655_b("testpickaxe");
        GameRegistry.registerItem((Item)testpickaxe, (String)"testpickaxe");
    }
}

