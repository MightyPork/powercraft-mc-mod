/**
 * 
 */
package powercraft.api.blocks;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import powercraft.api.inventory.PC_Inventory;
import powercraft.api.upgrade.PC_ItemUpgrade;
import powercraft.api.upgrade.PC_UpgradeFamily;


/**
 * @author Aaron
 *
 */
public abstract class PC_TileEntityUpgradable extends PC_TileEntityWithInventory{
	
	protected int upgradeInv;
	
	
	/**
	 * Used to create a TileEntity for any Upgradeable Block.
	 * @param vfamilies an int that contains all allowed upgrade families (
	 * @param upgradeSlots the count of slots that should be used for upgrades
	 * @param name that's the name of the TileEntity used to store the kind
	 * @param inventories WARNING: DON'T INPUT UPGRADE INVENTORY gets automatically added
	 * @param slotsForID
	 */
	public PC_TileEntityUpgradable(int vfamilies, int upgradeSlots, String name, PC_Inventory[] inventories, int[] outputs, int[]... slotsForID){				
		super(name, addUpgradeInventory(inventories, upgradeSlots, vfamilies), outputs, slotsForID);
		upgradeInv = inventories.length;
	}
	
	private static PC_Inventory[] addUpgradeInventory(PC_Inventory[] inv, int slots, int validFamilies){
		PC_Inventory upgrades = new UpgradeInventory(slots, validFamilies);
		PC_Inventory[] newInv = new PC_Inventory[inv.length+1];
		System.arraycopy(inv, 0, newInv, 0, inv.length);
		newInv[inv.length] = upgrades;
		return newInv;
	}
	
	public PC_Inventory getUpgradeInventory(){
		return getSubInventoryByID(upgradeInv);
	}
	
	/*
	 * Not needed any more, use instead onInventoryChanged()
	 *  the GUI needs to pass an array of ID's of all upgrades currently set any time something is added or removed from the list 
	 */
//	@Override	
//	public boolean onUpgradesChanged(PC_ItemUpgrade[] upgrades)
//	{ 
//		return upgrades.length <= upgradeList.length;
//	}
	
	/**
	 * Checks to see if any of the upgrades currently installed are Security Upgrades 
	 * @return true if Security upgrade is installed
	 */
	public boolean hasSecurityUpgrade()
	{
		for (ItemStack itemStack : getUpgradeInventory())
		{
			PC_ItemUpgrade upg = (PC_ItemUpgrade) itemStack.getItem();
			
			if (upg.getUpgradeFamily() == PC_UpgradeFamily.Security)
			{
				return true;
			}
		}
		return false;
	}	
	
	private static class UpgradeInventory extends PC_Inventory{

		private int validFamilies;
		
		public UpgradeInventory(int size, int validFamilies) {
			super("upgrades", size, 1, 0);
			this.validFamilies = validFamilies;
		}

		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			Item item = itemstack.getItem();
			if(item instanceof PC_ItemUpgrade){
				PC_ItemUpgrade upg = (PC_ItemUpgrade)item;
				if((upg.getUpgradeFamily().getFamilyID()&validFamilies)!=0)
					return super.isItemValidForSlot(i, itemstack);
			}
			return false;
		}
		
	}
	
}
