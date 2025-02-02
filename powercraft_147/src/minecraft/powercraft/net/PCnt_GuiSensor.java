package powercraft.net;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresButton;
import powercraft.api.gres.PC_GresLabel;
import powercraft.api.gres.PC_GresLayoutH;
import powercraft.api.gres.PC_GresLayoutV;
import powercraft.api.gres.PC_GresProgressBar;
import powercraft.api.gres.PC_GresWidget;
import powercraft.api.gres.PC_GresWidget.PC_GresAlign;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresClient;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.tileentity.PC_TileEntity;

public class PCnt_GuiSensor implements PC_IGresClient {

	private PCnt_TileEntitySensor sensor;

	private PC_GresWidget buttonOK, buttonCancel;
	private PC_GresProgressBar slider;
	
	/**
	 * @param tes Sensor tile entity
	 */
	public PCnt_GuiSensor(EntityPlayer player, PC_TileEntity te, Object[] o) {
		sensor = (PCnt_TileEntitySensor)te; 
	}
	
	@Override
	public void initGui(PC_IGresGui gui) {
		String title = "";
		if (sensor.getGroup() == 0) {
			title = "tile.PCnt_BlockSensor.item.name";
		}
		if (sensor.getGroup() == 1) {
			title = "tile.PCnt_BlockSensor.living.name";
		}
		if (sensor.getGroup() == 2) {
			title = "tile.PCnt_BlockSensor.player.name";
		}

		// window
		PC_GresWindow w = new PC_GresWindow(title);
		w.setAlignH(PC_GresAlign.CENTER);
		PC_GresWidget hg;
		PC_GresWidget vg;

		// layout with the input
		vg = new PC_GresLayoutV().setAlignH(PC_GresAlign.LEFT);
		vg.add(new PC_GresLabel("pc.gui.sensor.range"));
		vg.add(slider = new PC_GresProgressBar(0x00ff00, 200));
		slider.configureLabel("", "32", 32);
		slider.setEditable(true);
		slider.setFraction(sensor.getRange() / 32F);
		w.add(vg);

		// buttons
		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.CENTER);
		hg.add(buttonCancel = new PC_GresButton("pc.gui.cancel").setId(1));
		hg.add(buttonOK = new PC_GresButton("pc.gui.ok").setId(0));
		w.add(hg);

		gui.add(w);


		gui.setPausesGame(false);
	}

	@Override
	public void onGuiClosed(PC_IGresGui gui) {}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
		if (widget.getId() == 0) {

			sensor.setRange(Math.round(slider.getFraction() * 32));
			
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

	@Override
	public void updateTick(PC_IGresGui gui) {}

	@Override
	public void updateScreen(PC_IGresGui gui) {}

	@Override
	public boolean drawBackground(PC_IGresGui gui, int par1, int par2,
			float par3) {
		return false;
	}

	@Override
	public void keyChange(String key, Object value) {
		if(key.equals("range")){
			slider.setFraction(((Integer)value)/32F);
		}
	}

}
