package ru.metrocraft.port.mechanics;

import net.minecraft.init.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.*;
import net.minecraftforge.oredict.OreDictionary;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class MedkitRecipeTest {
    @BeforeClass public static void vanilla(){Bootstrap.register();OreDictionary.registerOre("fiberglass",new ItemStack(Items.STRING));}
    private InventoryCrafting grid(){return new InventoryCrafting(new Container(){
        @Override public boolean canInteractWith(EntityPlayer player){return true;}
    },3,3);}
    private ItemStack potion(PotionType type){return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM),type);}
    @Test public void waterCannotReplaceStrongHealing(){
        InventoryCrafting grid=grid();
        for(int i=0;i<9;i++)grid.setInventorySlotContents(i,i>=3&&i<6?potion(PotionTypes.WATER):new ItemStack(Items.STRING));
        MedkitRecipes.Recipe recipe=new MedkitRecipes.Recipe(false,Items.PAPER);
        assertFalse(recipe.matches(grid,null));
        for(int i=3;i<6;i++)grid.setInventorySlotContents(i,potion(PotionTypes.STRONG_HEALING));
        assertTrue(recipe.matches(grid,null));
        assertEquals(Items.PAPER,recipe.getCraftingResult(grid).getItem());
    }
    @Test public void rechargingPreservesNoDropAndOnlyResetsUses(){
        InventoryCrafting grid=grid();ItemStack kit=new ItemStack(Items.IRON_SWORD,1,2);
        NBTTagCompound tag=new NBTTagCompound();tag.setBoolean("NoDrop",true);kit.setTagCompound(tag);
        grid.setInventorySlotContents(0,kit);
        for(int i=1;i<=3;i++)grid.setInventorySlotContents(i,potion(PotionTypes.STRONG_HEALING));
        MedkitRecipes.Recipe recipe=new MedkitRecipes.Recipe(true,Items.IRON_SWORD);
        assertTrue(recipe.matches(grid,null));ItemStack result=recipe.getCraftingResult(grid);
        assertEquals(0,result.getItemDamage());assertTrue(NoDrop.marked(result));assertEquals(2,kit.getItemDamage());
        assertEquals(Items.GLASS_BOTTLE,recipe.getRemainingItems(grid).get(1).getItem());
    }
    @Test public void extraIngredientsCannotBeConsumedAccidentally(){
        InventoryCrafting grid=grid();grid.setInventorySlotContents(0,new ItemStack(Items.IRON_SWORD,1,2));
        for(int i=1;i<=3;i++)grid.setInventorySlotContents(i,potion(PotionTypes.STRONG_HEALING));
        grid.setInventorySlotContents(4,new ItemStack(Items.DIAMOND));
        assertFalse(new MedkitRecipes.Recipe(true,Items.IRON_SWORD).matches(grid,null));
    }
    @Test public void noDropRequiresAnExplicitTrueMarker(){
        ItemStack stack=new ItemStack(Items.PAPER);assertFalse(NoDrop.marked(stack));
        NBTTagCompound tag=new NBTTagCompound();tag.setBoolean("NoDrop",false);stack.setTagCompound(tag);
        assertFalse(NoDrop.marked(stack));tag.setBoolean("NoDrop",true);assertTrue(NoDrop.marked(stack));
        assertFalse(NoDrop.marked(ItemStack.EMPTY));
    }
}
