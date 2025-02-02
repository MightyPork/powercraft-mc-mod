package net.minecraft.src;


/**
 * @author MightyPork
 * @copy (c) 2012
 */
public class PC_GresInventoryPlayer extends PC_GresLayoutV {

	private boolean showLabel = true;

	/** align of the label on top */
	protected PC_GresAlign labelAlign = PC_GresAlign.LEFT;

	/** the upper inv block */
	protected PC_GresInventory inv1;

	/** the lower inv block */
	protected PC_GresInventory inv2;

	/**
	 * @param labelVisible show inventory label.
	 */
	public PC_GresInventoryPlayer(boolean labelVisible) {
		showLabel = labelVisible;
		setAlignH(labelAlign);
		setAlignV(PC_GresAlign.TOP);
	}
	
	public void hideSlots() {
		if (containerManager == null) return;
		
		Slot[][] slots = getContainerManager().inventoryPlayerUpper;
	
		for (int x = 0; x < slots.length; x++) {
			for (int y = 0; y < slots[0].length; y++) {
				if (slots[x][y] != null) {
					slots[x][y].xDisplayPosition = -3000;
				}
			}
		}
		
		slots = getContainerManager().inventoryPlayerLower;
	
		for (int x = 0; x < slots.length; x++) {
			for (int y = 0; y < slots[0].length; y++) {
				if (slots[x][y] != null) {
					slots[x][y].xDisplayPosition = -3000;
				}
			}
		}
	}

	@Override
	public void addedToWidget() {
		if (containerManager == null) {
			return;
		}


		canAddWidget = true;
		PC_GresWidget label = new PC_GresLabel(PC_Lang.tr("container.inventory")).setWidgetMargin(2).setColor(textColorEnabled, 0x404040);
		if (showLabel) {
			add(label);
		}

		inv1 = new PC_GresInventory(9, 3);
		inv1.slots = getContainerManager().inventoryPlayerUpper;
		add(inv1.setWidgetMargin(4));

		inv2 = new PC_GresInventory(9, 1);
		inv2.slots = getContainerManager().inventoryPlayerLower;
		add(inv2.setWidgetMargin(4));
		canAddWidget = false;
		super.addedToWidget();
	}

	@Override
	public boolean mouseOver(PC_CoordI mousePos) {
		return true;
	}

	@Override
	public boolean mouseClick(PC_CoordI mousePos, int key) {
		return inv1.mouseClick(mousePos, key) || inv2.mouseClick(mousePos, key);
	}

}
