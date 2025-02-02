package powercraft.api.nei;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import powercraft.api.gres.PC_GresContainerGui;
import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;

public class PC_NEIGuiHandler implements INEIGuiHandler {

	@Override
	public VisiblityData modifyVisiblity(GuiContainer gui, VisiblityData currentVisibility) {
		if(gui instanceof PC_GresContainerGui){
			currentVisibility.showNEI = false;
		}
		return currentVisibility;
	}

	@Override
	public int getItemSpawnSlot(GuiContainer gui, ItemStack item) {
		return -1;
	}

	@Override
	public List<TaggedInventoryArea> getInventoryAreas(GuiContainer gui) {
		return null;
	}

}
