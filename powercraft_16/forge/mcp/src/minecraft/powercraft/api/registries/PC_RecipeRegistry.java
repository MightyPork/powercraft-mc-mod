package powercraft.api.registries;

import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import powercraft.api.PC_Logger;
import powercraft.api.PC_Module;
import powercraft.api.PC_Utils;
import powercraft.api.blocks.PC_Block;
import powercraft.api.items.PC_Item;

public class PC_RecipeRegistry {

	public static enum PC_RecipeTypes {
		SHAPED, SHAPELESS, RECIPE3D, SMELTING, IRECIPE;
	}
	
	public static void registerRecipes(PC_Module module, Object object) {

		PC_ModuleRegistry.setActiveModule(module);
		if (object instanceof PC_Block) {
			((PC_Block) object).registerRecipes();
		} else if (object instanceof PC_Item) {
			((PC_Item) object).registerRecipes();
		}
		PC_ModuleRegistry.releaseActiveModule();
	}


	public static void addRecipe(PC_RecipeTypes recipeType, Object... obj) {

		switch (recipeType) {
			case RECIPE3D:
				break;
			case SHAPED:
				addShapedRecipe(obj);
				break;
			case SHAPELESS:
				addShapelessRecipe(obj);
				break;
			case SMELTING:
				addSmelting(obj);
				break;
			case IRECIPE:
				addIRecipe(obj);
				break;
			default:
				PC_Logger.severe("Unknown recipe type %s", recipeType);
				break;
		}
	}


	@SuppressWarnings("unchecked")
	private static void addIRecipe(Object[] obj) {
		
		CraftingManager.getInstance().getRecipeList().add(obj[0]);
		
	}


	private static void addShapedRecipe(Object... obj) {

		ItemStack output = PC_Utils.getItemStack(obj[0]);
		CraftingManager.getInstance().addRecipe(output, Arrays.copyOfRange(obj, 1, obj.length));
	}


	private static void addShapelessRecipe(Object... obj) {

		ItemStack output = PC_Utils.getItemStack(obj[0]);
		CraftingManager.getInstance().addShapelessRecipe(output, Arrays.copyOfRange(obj, 1, obj.length));
	}


	private static void addSmelting(Object... obj) {

		ItemStack output = PC_Utils.getItemStack(obj[0]);
		int i = 1;
		while (i < obj.length) {
			ItemStack input = PC_Utils.getItemStack(obj[i]);
			float experience = 0;
			i++;
			if (i < obj.length && obj[i] instanceof Float) {
				experience = (Float) obj[i];
				i++;
			}
			FurnaceRecipes.smelting().addSmelting(input.itemID, input.getItemDamage(), output, experience);
		}
	}
	
}
