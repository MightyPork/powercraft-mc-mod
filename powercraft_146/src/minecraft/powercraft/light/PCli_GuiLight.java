package powercraft.light;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.management.PC_Color;
import powercraft.management.PC_GresButton;
import powercraft.management.PC_GresCheckBox;
import powercraft.management.PC_GresColor;
import powercraft.management.PC_GresColorPicker;
import powercraft.management.PC_GresLayoutH;
import powercraft.management.PC_GresLayoutV;
import powercraft.management.PC_GresWidget;
import powercraft.management.PC_GresWidget.PC_GresAlign;
import powercraft.management.PC_GresWindow;
import powercraft.management.PC_IGresClient;
import powercraft.management.PC_IGresGui;
import powercraft.management.PC_PacketHandler;
import powercraft.management.PC_Utils.GameInfo;
import powercraft.management.PC_Utils.Lang;

public class PCli_GuiLight implements PC_IGresClient {

	private PCli_TileEntityLight light;
	private PC_Color color;
	private boolean isStable;
	private boolean isHuge;
	
	private PC_GresCheckBox checkHuge, checkStable;
	private PC_GresColor colorWidget;
	private PC_GresColorPicker colorPicker;
	private PC_GresButton accept, cancel;
	
	public PCli_GuiLight(EntityPlayer player, Object[] o){
		light = (PCli_TileEntityLight)GameInfo.getTE(player.worldObj, (Integer)o[0], (Integer)o[1], (Integer)o[2]);
	
		color = this.light.getColor();
		if(color==null) 
			color = new PC_Color(PC_Color.getHexColorForName("white"));
		else
			color = color.copy();
		isStable = this.light.isStable();
		isHuge = this.light.isHuge();
	}
	
	@Override
	public void initGui(PC_IGresGui gui) {
		PC_GresWindow w = (PC_GresWindow) new PC_GresWindow(Lang.tr("tile.PCli_BlockLight.name"));
		PC_GresLayoutV v = (PC_GresLayoutV)new PC_GresLayoutV().setAlignH(PC_GresAlign.STRETCH);
		
		PC_GresLayoutH h = (PC_GresLayoutH)new PC_GresLayoutH().setAlignH(PC_GresAlign.JUSTIFIED);
		h.add(checkHuge = (PC_GresCheckBox) new PC_GresCheckBox(Lang.tr("pc.gui.light.isHuge")).check(isHuge));
		h.add(checkStable = (PC_GresCheckBox) new PC_GresCheckBox(Lang.tr("pc.gui.light.isStable")).check(isStable));
		v.add(h);
		
		h = (PC_GresLayoutH)new PC_GresLayoutH().setAlignH(PC_GresAlign.STRETCH);
		h.add(colorWidget = new PC_GresColor(color));
		h.add(colorPicker = new PC_GresColorPicker(color.getHex(), 100, 20));
		v.add(h);
		
		h = (PC_GresLayoutH)new PC_GresLayoutH().setAlignH(PC_GresAlign.STRETCH);;
		h.add(accept = new PC_GresButton(Lang.tr("pc.gui.ok")));
		h.add(cancel = new PC_GresButton(Lang.tr("pc.gui.cancel")));
		v.add(h);
		w.add(v);
		gui.add(w);

	}


	@Override
	public void onGuiClosed(PC_IGresGui gui) {}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
		if(widget == colorPicker){
			colorWidget.setColor(((PC_GresColorPicker)widget).getColor());
			color.setTo(colorPicker.getColor());
		}else if(widget == checkHuge){
			isHuge = ((PC_GresCheckBox)widget).isChecked();
		}else if(widget == checkStable){
			isStable = ((PC_GresCheckBox)widget).isChecked();
		}else if(widget == accept){
			PC_PacketHandler.setTileEntity(light, "color", color, "isHuge", isHuge, "isStable", isStable);
			gui.close();
		}else if (widget == cancel) {
			gui.close();
		}

	}

	@Override
	public void onEscapePressed(PC_IGresGui gui) {
		gui.close();

	}

	@Override
	public void onReturnPressed(PC_IGresGui gui) {
		PC_PacketHandler.setTileEntity(light, "color", color, "isHuge", isHuge, "isStable", isStable);
		gui.close();
	}

	@Override
	public void updateTick(PC_IGresGui gui) {}

	@Override
	public void updateScreen(PC_IGresGui gui) {}

	@Override
	public boolean drawBackground(PC_IGresGui gui, int par1, int par2, float par3) {
		return false;
	}

}
