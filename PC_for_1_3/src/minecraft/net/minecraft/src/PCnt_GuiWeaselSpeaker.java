package net.minecraft.src;


import java.util.List;

import net.minecraft.src.PC_GresTextEdit.PC_GresInputType;
import net.minecraft.src.PC_GresWidget.PC_GresAlign;



/**
 * Gui for weasel speaker
 * 
 * @author MightyPork
 */
public class PCnt_GuiWeaselSpeaker extends PC_GresBase {

	private PCnt_WeaselPluginSpeaker_UNUSED speaker;
	private PC_GresWindow w;
	private PC_GresWidget txError;
	private PC_GresWidget edName;
	private PC_GresWidget btnOk;
	private PC_GresButton btnCancel;


	/**
	 * GUI for port.
	 * 
	 * @param speaker plugin instance
	 */
	public PCnt_GuiWeaselSpeaker(PCnt_WeaselPluginSpeaker_UNUSED speaker) {
		this.speaker = speaker;
	}

	@Override
	public EntityPlayer getPlayer() {
		return PC_Utils.mc().thePlayer;
	}

	@Override
	public void initGui(PC_IGresGui gui) {
		w = new PC_GresWindow(PC_Lang.tr("pc.gui.weasel.sound.title"));
		w.setAlignH(PC_GresAlign.CENTER);
		w.setMinWidth(260);

		PC_GresWidget hg;

		int colorLabel = 0x000000;
		int colorValue = 0x000099;

		hg = new PC_GresLayoutH();
		hg.add(new PC_GresLabel(PC_Lang.tr("pc.gui.weasel.connectedToNetwork")).setColor(PC_GresWidget.textColorEnabled, colorLabel));
		hg.add(new PC_GresColor(speaker.getNetworkColor()));
		hg.add(new PC_GresLabel(speaker.getNetworkName()).setColor(PC_GresWidget.textColorEnabled, colorValue));
		w.add(hg);


		hg = new PC_GresLayoutH();
		hg.add(new PC_GresLabel(PC_Lang.tr("pc.gui.weasel.sound.speakerName")));
		hg.add(edName = new PC_GresTextEdit(speaker.getName(), 14, PC_GresInputType.IDENTIFIER).setWidgetMargin(2));
		w.add(hg);

		w.add(txError = new PC_GresLabel("").setWidgetMargin(2).setColor(PC_GresWidget.textColorEnabled, 0x000000));

		hg = new PC_GresLayoutH();
		hg.add(btnCancel = new PC_GresButton(PC_Lang.tr("pc.gui.cancel")));
		hg.add(btnOk = new PC_GresButton(PC_Lang.tr("pc.gui.weasel.rename")));
		w.add(hg);
		w.add(new PC_GresGap(0, 0));

		gui.add(w);

		btnOk.enable(false);

	}

	@Override
	public void onGuiClosed(PC_IGresGui gui) {}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {

		if (widget == edName) {
			String name = edName.text.trim();
			if (name.length() == 0) {
				txError.text = PC_Lang.tr("pc.gui.weasel.errDeviceNameTooShort");
				btnOk.enabled = false;
			} else if (speaker.getNetwork() != null && speaker.getNetwork().getMembers().get(name) != null) {
				txError.text = PC_Lang.tr("pc.gui.weasel.errDeviceNameAlreadyUsed", name);
				btnOk.enabled = false;
			} else {
				txError.text = "";
				btnOk.enabled = true;
			}
			w.calcSize();
			return;
		}

		if (widget == btnOk) {
			String name = edName.text.trim();
			speaker.setMemberName(name);
			//txError.text = PC_Lang.tr("pc.gui.weasel.deviceRenamed", name);
			//w.calcSize();
			gui.close();
			return;
		}

		if (widget == btnCancel) {
			gui.close();
			return;
		}


	}

	@Override
	public void onEscapePressed(PC_IGresGui gui) {
		gui.close();
	}

	@Override
	public void onReturnPressed(PC_IGresGui gui) {}

	@Override
	public void onCraftMatrixChanged(IInventory iinventory) {}

	@Override
	public void updateTick(PC_IGresGui gui) {}

	@Override
	public List<Slot> getAllSlots(Container c) {
		return null;
	}

	@Override
	public boolean canShiftTransfer() {
		return false;
	}

}
