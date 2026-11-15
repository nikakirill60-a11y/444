package ru.metrocraft.port.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import java.util.*;

/** Forge loads the named factory on the physical client only. No common-side client class initialization. */
public final class MetroGuiFactory implements IModGuiFactory {
    @Override public void initialize(Minecraft mc){}
    @Override public boolean hasConfigGui(){return true;}
    @Override public GuiScreen createConfigGui(GuiScreen parent){return new VisualSettingsScreen(parent);}
    @Override public Set<RuntimeOptionCategoryElement> runtimeGuiCategories(){return Collections.emptySet();}
}
