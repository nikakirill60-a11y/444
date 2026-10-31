package ru.metrocraft.port.mechanics;

import net.minecraft.init.*;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import ru.metrocraft.port.MetroPort;

/** Original FFF/PPP/FFF and RechargeRecipe 3 potion.8229. No fabricated fiberglass substitute. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class MedkitRecipes {
    @SubscribeEvent public static void register(RegistryEvent.Register<IRecipe> event){
        event.getRegistry().register(new Recipe(false,MetroPort.ITEMS.get("medkit")).setRegistryName(MetroPort.ID,"medkit"));
        event.getRegistry().register(new Recipe(true,MetroPort.ITEMS.get("medkit")).setRegistryName(MetroPort.ID,"medkit_recharge"));
    }
    private static boolean potion(ItemStack stack){
        return stack.getItem()==Items.POTIONITEM && PotionUtils.getPotionFromItem(stack)==PotionTypes.STRONG_HEALING;
    }
    private static boolean fiberglass(ItemStack stack){
        if(stack.isEmpty())return false;
        for(ItemStack candidate:OreDictionary.getOres("fiberglass"))if(OreDictionary.itemMatches(candidate,stack,false))return true;
        return stack.getItem().getRegistryName()!=null && stack.getItem().getRegistryName().getPath().equals("fiberglass");
    }
    static final class Recipe extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
        private final boolean recharge;private final Item kit;
        Recipe(boolean recharge,Item kit){this.recharge=recharge;this.kit=kit;}
        @Override public boolean matches(InventoryCrafting inventory,World world){
            if(!recharge){
                if(inventory.getWidth()!=3||inventory.getHeight()!=3)return false;
                for(int y=0;y<3;y++)for(int x=0;x<3;x++)
                    if(y==1?!potion(inventory.getStackInRowAndColumn(x,y)):!fiberglass(inventory.getStackInRowAndColumn(x,y)))return false;
                return true;
            }
            int potions=0,kits=0;
            for(int i=0;i<inventory.getSizeInventory();i++){
                ItemStack stack=inventory.getStackInSlot(i);if(stack.isEmpty())continue;
                if(potion(stack))potions++;
                else if(stack.getItem()==kit&&stack.isItemDamaged())kits++;
                else return false;
            }
            return potions==3&&kits==1;
        }
        @Override public ItemStack getCraftingResult(InventoryCrafting inventory){
            ItemStack result=getRecipeOutput().copy();
            if(recharge)for(int i=0;i<inventory.getSizeInventory();i++){
                ItemStack kit=inventory.getStackInSlot(i);
                if(kit.getItem()==this.kit){result=kit.copy();result.setCount(1);result.setItemDamage(0);break;}
            }
            return result;
        }
        @Override public boolean canFit(int width,int height){return recharge?width*height>=4:width>=3&&height>=3;}
        @Override public ItemStack getRecipeOutput(){return kit==null?ItemStack.EMPTY:new ItemStack(kit);}
        @Override public boolean isDynamic(){return true;}
        @Override public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inventory){
            NonNullList<ItemStack> remaining=NonNullList.withSize(inventory.getSizeInventory(),ItemStack.EMPTY);
            for(int i=0;i<remaining.size();i++)if(potion(inventory.getStackInSlot(i)))remaining.set(i,new ItemStack(Items.GLASS_BOTTLE));
            return remaining;
        }
    }
}
