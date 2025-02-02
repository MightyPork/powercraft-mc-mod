package powercraft.checkpoints;

import net.minecraft.item.ItemStack;
import powercraft.api.annotation.PC_ClientServerSync;
import powercraft.api.tileentity.PC_TileEntityWithInventory;

public class PCcp_TileEntityCheckpoint extends PC_TileEntityWithInventory {

	@PC_ClientServerSync
	private boolean collideTriggerd=false;
	
	public PCcp_TileEntityCheckpoint() {
		super("Checkpoint", 36);
		isWhiteList=true;
	}
	
	public void setCollideTriggerd(boolean b){
		if(collideTriggerd!=b){
			collideTriggerd = b;
			notifyChanges("collideTriggerd");
		}
	}
	
	public boolean isCollideTriggerd(){
		return collideTriggerd;
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		return false;
	}
	
}
