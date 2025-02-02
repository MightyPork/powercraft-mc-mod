package net.minecraft.src;


import java.util.List;

import net.minecraft.src.PC_GresTextEdit.PC_GresInputType;
import net.minecraft.src.PC_GresWidget.PC_GresAlign;


/**
 * GUI for changing radio device channel
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
public class PClo_GuiRadio extends PC_GresBase {

	private String errMsg = "";

	/** receiver index */
	public static final int RECEIVER = 1;
	/** transmitter index */
	public static final int TRANSMITTER = 0;

	private int type;
	private PC_CoordI pos;
	private String editedString;
	private int dim = 0;

	private PC_GresWidget buttonOK, buttonCancel;
	private PC_GresWidget edit;
	private PC_GresWidget txError;
	private PClo_TileEntityRadio ter;

	private PC_GresCheckBox checkLabel;

	private PC_GresCheckBox checkMicro;

	/**
	 * @param dimen Radio device dimension
	 * @param blockPos block position in world
	 * @param s device channel
	 * @param radiotype transmitter or receiver
	 */
	public PClo_GuiRadio(EntityPlayer player, TileEntity ter) {
		this.ter = (PClo_TileEntityRadio) ter;
		this.player = player;
		editedString = this.ter.getChannel();
		type = this.ter.isTransmitter() ? PClo_GuiRadio.TRANSMITTER : PClo_GuiRadio.RECEIVER;
		pos = this.ter.getCoord();
		dim = player.dimension;

	}

	@Override
	public void initGui(PC_IGresGui gui) {
		String title = "";
		if (type == 0) {
			title = PC_Lang.tr("tile.PCloRadio.tx.name");
		} else {
			title = PC_Lang.tr("tile.PCloRadio.rx.name");
		}

		// window
		PC_GresWindow w = new PC_GresWindow(title);
		w.setAlignH(PC_GresAlign.CENTER);
		PC_GresWidget hg;

		// layout with the input
		PC_GresWidget vg = new PC_GresLayoutV().setAlignH(PC_GresAlign.LEFT);
		vg.add(new PC_GresLabel(PC_Lang.tr("pc.gui.radio.channel")));
		vg.add(edit = new PC_GresTextEdit(editedString, 20, PC_GresInputType.TEXT).setMinWidth(140));
		w.add(vg);

		// eror
		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.CENTER);
		hg.add(txError = new PC_GresLabel("").setColor(PC_GresWidget.textColorEnabled, 0x990000));
		w.add(hg);

		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.CENTER);
		hg.add(checkLabel = new PC_GresCheckBox(PC_Lang.tr("pc.gui.radio.showLabel")));
		checkLabel.check(!ter.hideLabel);

		hg.add(checkMicro = new PC_GresCheckBox(PC_Lang.tr("pc.gui.radio.renderSmall")));
		checkMicro.check(ter.renderMicro);
		w.add(hg);

		// buttons
		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.CENTER);
		hg.add(buttonCancel = new PC_GresButton(PC_Lang.tr("pc.gui.cancel")).setId(1));
		hg.add(buttonOK = new PC_GresButton(PC_Lang.tr("pc.gui.ok")).setId(0));
		w.add(hg);

		gui.add(w);


		gui.setPausesGame(true);

		// refresh labels.
		actionPerformed(edit, gui);

	}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {

		if (widget.getId() == 0) {

			String newChannel = edit.getText().trim();

			PC_Utils.setTileEntity(getPlayer(), ter, "channel", newChannel,
					"renderMicro", checkMicro.isChecked(),
					"hideLabel", !checkLabel.isChecked(),
					"dim", dim);

			// player is in the right dimen, set it to make sure.
			ter.dim = dim;

			if (type == 1) {
				PC_Utils.setTileEntity(getPlayer(), ter, "active", mod_PClogic.RADIO.getChannelState(newChannel));
				if (ter.active) {
					PC_Utils.mc().theWorld.setBlockMetadataWithNotify(pos.x, pos.y, pos.z, 1);
				}
			}

			PC_Utils.mc().theWorld.scheduleBlockUpdate(pos.x, pos.y, pos.z, mod_PClogic.radio.blockID, 1);

			gui.close();

		} else if (widget.getId() == 1) {
			gui.close();
		}

		if (widget == edit) {

			if (edit.getText().trim().length() == 0) {
				errMsg = "pc.gui.radio.errChannel";
				txError.setText(PC_Lang.tr(errMsg));
			} else {
				txError.setText("");
			}

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
