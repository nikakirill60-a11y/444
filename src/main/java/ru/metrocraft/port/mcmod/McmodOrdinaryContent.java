package ru.metrocraft.port.mcmod;

import com.google.gson.Gson;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.*;
import ru.metrocraft.port.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class McmodOrdinaryContent {
    public static final class Entry {
        public String id,sourceId,kind,nameRu,nameEn;
        public int stackSize;
    }
    public static Entry[] entries;
    public static void load() {
        try(Reader reader=new InputStreamReader(McmodOrdinaryContent.class.getResourceAsStream("/assets/metroport/mcmod_items.json"),StandardCharsets.UTF_8)) {
            entries=new Gson().fromJson(reader,Entry[].class);
        } catch(Exception e) { throw new IllegalStateException("Cannot load ordinary MCMOD items",e); }
    }
    public static Item create(Entry entry) {
        Item item;
        switch(entry.kind) {
            case "medkit":item=new McmodMedkitItem();break;
            case "machete":item=new Machete();break;
            case "filter":item=new MaskFilter();break;
            case "currency":case "material":item=new Basic(entry.kind);break;
            default:throw new IllegalArgumentException("Unknown MCMOD item kind: "+entry.kind);
        }
        item.setMaxStackSize(entry.stackSize);
        return item;
    }
    private static final class Basic extends Item {
        private final String kind;
        Basic(String kind) { this.kind=kind; }
        @Override @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack,World world,List<String> lines,ITooltipFlag flag) {
            lines.add(I18n.format("metroport.mcmod."+kind+".help"));
        }
    }
    private static final class MaskFilter extends FilterItem {
        @Override @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack,World world,List<String> lines,ITooltipFlag flag) {
            lines.add(I18n.format("metroport.mcmod.filter.help"));
        }
    }
    private static final class Machete extends ItemAxe {
        Machete() { super(ToolMaterial.DIAMOND); }
        // Original ItemAxeB explicitly did not damage the tool on an entity hit.
        @Override public boolean hitEntity(ItemStack stack,EntityLivingBase target,EntityLivingBase attacker) { return true; }
    }
}
