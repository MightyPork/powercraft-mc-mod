package powercraft.logic;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.management.PC_GresButton;
import powercraft.management.PC_GresCheckBox;
import powercraft.management.PC_GresLabel;
import powercraft.management.PC_GresLayoutH;
import powercraft.management.PC_GresLayoutV;
import powercraft.management.PC_GresTextEdit;
import powercraft.management.PC_GresTextEdit.PC_GresInputType;
import powercraft.management.PC_GresWidget;
import powercraft.management.PC_GresWidget.PC_GresAlign;
import powercraft.management.PC_GresWindow;
import powercraft.management.PC_IGresClient;
import powercraft.management.PC_IGresGui;
import powercraft.management.PC_PacketHandler;
import powercraft.management.PC_Utils.Converter;
import powercraft.management.PC_Utils.Lang;

public class PClo_GuiPulsar implements PC_IGresClient {

	private PClo_TileEntityPulsar pulsar;
	
	private PC_GresWidget buttonOK, buttonCancel;
	private PC_GresWidget editDelay;
	private PC_GresWidget editHold;
	private PC_GresWidget txError;
	
	private PC_GresCheckBox checkSilent, checkPaused;
	
	private boolean error = false;
	
	public PClo_GuiPulsar(EntityPlayer player, Object[] o){
		pulsar = (PClo_TileEntityPulsar)player.worldObj.getBlockTileEntity((Integer)o[0], (Integer)o[1], (Integer)o[2]);
	}
	
	@Override
	public void initGui(PC_IGresGui gui) {
		PC_GresWindow w = new PC_GresWindow(Lang.tr("tile.PClo_BlockPulsar.name"));
		
		w.setAlignH(PC_GresAlign.STRETCH);
		PC_GresWidget hg, vg;

		// layout with the inputs
		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.CENTER);

		vg = new PC_GresLayoutV().setAlignH(PC_GresAlign.LEFT);
		vg.add(new PC_GresLabel(Lang.tr("pc.gui.pulsar.delay")));
		vg.add(editDelay = new PC_GresTextEdit(Converter.doubleToString(Converter.ticksToSecs(pulsar.getDelay())), 8, PC_GresInputType.UNSIGNED_FLOAT));
		hg.add(vg);

		vg = new PC_GresLayoutV().setAlignH(PC_GresAlign.LEFT);
		vg.add(new PC_GresLabel(Lang.tr("pc.gui.pulsar.hold")));
		vg.add(editHold = new PC_GresTextEdit(Converter.doubleToString(Converter.ticksToSecs(pulsar.getHold())), 8, PC_GresInputType.UNSIGNED_FLOAT));
		hg.add(vg);

		w.add(hg);


		w.add(txError = new PC_GresLabel("").setColor(PC_GresWidget.textColorEnabled, 0x990000).setColor(PC_GresWidget.textColorHover, 0x990000));

		/*
		 * w.add(new PC_GresGap(0,3));
		 * w.add(new PC_GresImage(mod_PClogic.getImgDir()+"pulsar_hint.png", 0, 0, 131, 20));
		 * w.add(new PC_GresGap(0,3));
		 */

		// buttons
		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.CENTER);
		hg.setAlignH(PC_GresAlign.CENTER);
		hg.add(checkSilent = new PC_GresCheckBox(Lang.tr("pc.gui.pulsar.silent")).check(pulsar.isSilent()));
		hg.add(checkPaused = new PC_GresCheckBox(Lang.tr("pc.gui.pulsar.paused")).check(pulsar.isPaused()));
		w.add(hg);
		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.CENTER);
		hg.setAlignH(PC_GresAlign.JUSTIFIED);
		hg.add(buttonCancel = new PC_GresButton(Lang.tr("pc.gui.cancel")).setId(1));
		hg.add(buttonOK = new PC_GresButton(Lang.tr("pc.gui.ok")).setId(0));
		w.add(hg);
		
		gui.add(w);
		
	}

	@Override
	public void onGuiClosed(PC_IGresGui gui) {
		
	}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
		
		if(widget==editDelay || widget==editHold){
			String delay = editDelay.getText();
			String hold = editHold.getText();
			txError.setText("");
			if(!(delay.equals("")||hold.equals(""))){
				int idelay = Converter.secsToTicks(Double.parseDouble(delay));
				int ihold = Converter.secsToTicks(Double.parseDouble(hold));
				if(idelay<2){
					txError.setText(Lang.tr("pc.gui.pulsar.errDelay"));
				}else if(ihold>=idelay||ihold<=0){
					txError.setText(Lang.tr("pc.gui.pulsar.errHold"));
				}
			}
		}
		
		if(widget==buttonCancel){
			gui.close();
		}
		
		if(widget==buttonOK){
			String delay = editDelay.getText();
			String hold = editHold.getText();
			txError.setText("");
			if(!(delay.equals("")||hold.equals(""))){
				int idelay = Converter.secsToTicks(Double.parseDouble(delay));
				int ihold = Converter.secsToTicks(Double.parseDouble(hold));
				if(idelay<2){
					txError.setText(Lang.tr("pc.gui.pulsar.errDelay"));
				}else if(ihold>=idelay||ihold<=0){
					txError.setText(Lang.tr("pc.gui.pulsar.errHold"));
				}else{
					PC_PacketHandler.setTileEntity(pulsar, "silent", checkSilent.isChecked(), "paused", checkPaused.isChecked(), "delay", idelay, "hold", ihold);
					gui.close();
				}
			}
		}
		
	}

	@Override
	public void onEscapePressed(PC_IGresGui gui) {
		gui.close();
	}

	@Override
	public void onReturnPressed(PC_IGresGui gui) {}

	@Override
	public void updateTick(PC_IGresGui gui) {}

	@Override
	public void updateScreen(PC_IGresGui gui) {}

	@Override
	public boolean drawBackground(PC_IGresGui gui, int par1, int par2,
			float par3) {
		return false;
	}

}
