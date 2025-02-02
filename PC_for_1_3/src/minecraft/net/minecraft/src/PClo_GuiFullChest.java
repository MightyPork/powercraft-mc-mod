package net.minecraft.src;


import java.util.List;

import net.minecraft.src.PC_GresWidget.PC_GresAlign;


/**
 * Gui for full chest sensor
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
public class PClo_GuiFullChest extends PC_GresBase {

	private PClo_TileEntityGate gate;

	private PC_GresWidget buttonOK, buttonCancel;

	private PC_GresCheckBox check;
	
	/**
	 * @param tes Sensor tile entity
	 */
	public PClo_GuiFullChest(EntityPlayer player, TileEntity tes) {
		gate = (PClo_TileEntityGate)tes;
		this.player = player;
	}

	@Override
	public void initGui(PC_IGresGui gui) {

		String title = PC_Lang.tr("tile.PCloLogicGate.chestFull.name");


		// window
		PC_GresWindow w = new PC_GresWindow(title);
		w.setAlignH(PC_GresAlign.CENTER);

		w.add(new PC_GresGap(0, 8));
		w.add(check = new PC_GresCheckBox(PC_Lang.tr("pc.gui.chestFull.requireAllSlotsFull")));
		check.check(gate.fullChestNeedsAllSlotsFull);
		w.add(new PC_GresGap(0, 8));
		// buttons
		PC_GresWidget hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.CENTER);
		hg.add(new PC_GresButton(PC_Lang.tr("pc.gui.cancel")).setId(1));
		hg.add(new PC_GresButton(PC_Lang.tr("pc.gui.ok")).setId(0));
		w.add(hg);

		gui.add(w);


		gui.setPausesGame(false);

	}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {

		if (widget.getId() == 0) {

			gate.fullChestNeedsAllSlotsFull = check.isChecked();

			gui.close();

		} else if (widget.getId() == 1) {
			gui.close();
		}

	}

	@Override
	public void onEscapePressed(PC_IGresGui gui) {
		gui.close();
	}

	@Override
	public void onReturnPressed(PC_IGresGui gui) {
		actionPerformed(buttonOK, gui);
	}

}
