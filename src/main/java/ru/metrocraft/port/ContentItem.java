package ru.metrocraft.port;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.List;

public class ContentItem extends Item {
    protected final Definition definition;

    public ContentItem(Definition definition) {
        this.definition = definition;
        setMaxStackSize(Math.max(1, Math.min(64, definition.stackSize)));
    }

    public Definition getDefinition() { return definition; }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> lines, ITooltipFlag flag) {
        if (definition.description != null && !definition.description.isEmpty()) {
            for (String line : definition.description.split("\n")) lines.add(line);
        }
        if (definition.kind.equals("attachment")) lines.add(I18n.format("metroport.attachment.info"));
    }
}
