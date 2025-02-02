package powercraft.api.inventory;

import net.minecraft.item.ItemStack;

public interface PC_IStateReportingInventory {

	public boolean isContainerFull();

	public boolean hasContainerNoFreeSlots();

	public boolean isContainerEmpty();

	public boolean hasInventoryPlaceFor(ItemStack itemStack);

	public boolean isContainerEmptyOf(ItemStack itemStack);

}
