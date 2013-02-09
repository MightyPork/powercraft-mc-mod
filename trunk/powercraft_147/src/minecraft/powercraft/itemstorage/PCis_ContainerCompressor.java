package powercraft.itemstorage;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import powercraft.management.PC_TileEntity;
import powercraft.management.gres.PC_GresBaseWithInventory;
import powercraft.management.inventory.PC_Slot;
import powercraft.management.inventory.PC_SlotNoPickup;

public class PCis_ContainerCompressor extends PC_GresBaseWithInventory<PC_TileEntity> {

	protected List<PC_Slot> lSlot;
	protected PCis_CompressorInventory inv;
	
	public PCis_ContainerCompressor(EntityPlayer player, PC_TileEntity te, Object[] o) {
		super(player, te, o);
	}

	@Override
	protected void init(Object[] o) {
		int index = thePlayer.inventory.currentItem;
		inventoryPlayerLower[index][0] = new PC_SlotNoPickup(thePlayer.inventory, index);
		inventorySlots.set(index, inventoryPlayerLower[index][0]);
		inventoryPlayerLower[index][0].slotNumber = index;
	}

	@Override
	protected PC_Slot[] getAllSlots() {
		inv = PCis_ItemCompressor.getInventoryFor(thePlayer);
		invSlots = new PC_Slot[inv.getSizeInventory()];
		for(int i=0; i<invSlots.length; i++){
			invSlots[i] = new PC_Slot(inv, i);
		}
		return invSlots;
	}

	@Override
	public void onCraftGuiClosed(EntityPlayer par1EntityPlayer) {
		inv.closeChest();
		super.onCraftGuiClosed(par1EntityPlayer);
	}
	
}