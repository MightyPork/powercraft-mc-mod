package powercraft.core;

import java.util.List;

import net.minecraft.src.IRecipe;
import powercraft.management.PC_Configuration;
import powercraft.management.PC_IModule;
import powercraft.management.PC_LangEntry;
import powercraft.management.PC_Utils;

public class PowerCraftCore implements PC_IModule {

	@Override
	public String getName() {
		return "Core";
	}

	@Override
	public void preInit() {
		System.out.println("Loading POWERCRAFT");
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void postInit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initProperties(PC_Configuration config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initBlocks() {
		PC_Utils.register(this, PCco_BlockPowerCrystal.class, PCco_ItemBlockPowerCrystal.class);
	}

	@Override
	public void initItems() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<IRecipe> initRecipes(List<IRecipe> recipes) {
		// TODO Auto-generated method stub
		return null;
	}

}