package net.minecraft.src;


/**
 * Special slot which shows an item, but contains nothing.
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
public class PC_SlotNoPickup extends Slot implements PC_ISlotWithBackground {

	/** shown stack. */
	public ItemStack shownStack;

	/**
	 * no-pickup slot, in ore sniffer
	 */
	public PC_SlotNoPickup() {
		super(new IInventory() {
			@Override
			public void setInventorySlotContents(int i, ItemStack itemstack) {}

			@Override
			public void openChest() {}

			@Override
			public void onInventoryChanged() {}

			@Override
			public boolean isUseableByPlayer(EntityPlayer entityplayer) {
				return true;
			}

			@Override
			public ItemStack getStackInSlotOnClosing(int i) {
				return null;
			}

			@Override
			public ItemStack getStackInSlot(int i) {
				return null;
			}

			@Override
			public int getSizeInventory() {
				return 1;
			}

			@Override
			public int getInventoryStackLimit() {
				return 0;
			}

			@Override
			public String getInvName() {
				return "FAKE";
			}

			@Override
			public ItemStack decrStackSize(int i, int j) {
				return null;
			}

			@Override
			public void closeChest() {}
		}, 0, 0, 0);
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return false;
	}

	@Override
	public ItemStack getBackgroundStack() {
		return shownStack;
	}

	@Override
	public Slot setBackgroundStack(ItemStack stack) {
		shownStack = stack;
		return this;
	}

	@Override
	public boolean renderTooltipWhenEmpty() {
		return true;
	}

	@Override
	public boolean renderGrayWhenEmpty() {
		return false;
	}

}
