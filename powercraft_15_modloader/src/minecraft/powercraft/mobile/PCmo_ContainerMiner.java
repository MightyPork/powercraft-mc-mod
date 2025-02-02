package powercraft.mobile;

import net.minecraft.src.EntityPlayer;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.inventory.PC_Slot;
import powercraft.api.tileentity.PC_TileEntity;

public class PCmo_ContainerMiner extends PC_GresBaseWithInventory<PC_TileEntity> {

	protected PCmo_EntityMiner miner;
	
	public PCmo_ContainerMiner(EntityPlayer player, PC_TileEntity te, Object[] o) {
		super(player, te, o);
	}
	
	@Override
	protected void init(Object[] o) {
		miner = (PCmo_EntityMiner)thePlayer.worldObj.getEntityByID((Integer)o[0]);
	}

	@Override
	protected PC_Slot[] getAllSlots() {
		
		invSlots = new PC_Slot[miner.xtals.getSizeInventory()+miner.cargo.getSizeInventory()];
		
		int n=0;
		for(int i=0; i<miner.xtals.getSizeInventory(); i++){
			invSlots[n] = new PC_Slot(miner.xtals, i);
			n++;
		}
		for(int i=0; i<miner.cargo.getSizeInventory(); i++){
			invSlots[n] = new PC_Slot(miner.cargo, i);
			n++;
		}
		
		return invSlots;
	}

	@Override
	public void onCraftGuiClosed(EntityPlayer par1EntityPlayer) {
		super.onCraftGuiClosed(par1EntityPlayer);
		miner.xtals.closeChest();
		miner.cargo.closeChest();
	}
	
	@Override
	protected boolean canShiftTransfer() {
		return true;
	}
	
}
