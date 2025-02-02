package powercraft.weasel;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.management.PC_GresButton;
import powercraft.management.PC_GresColorMap;
import powercraft.management.PC_GresLayoutV;
import powercraft.management.PC_GresWidget;
import powercraft.management.PC_GresWidget.PC_GresAlign;
import powercraft.management.PC_IGresClient;
import powercraft.management.PC_IGresGui;
import powercraft.management.PC_PacketHandler;
import powercraft.management.PC_Struct4;
import powercraft.management.PC_Utils.GameInfo;
import powercraft.management.PC_Utils.Lang;
import powercraft.management.PC_VecI;


public class PCws_GuiWeaselTouchscreen implements PC_IGresClient {

	private PCws_TileEntityWeasel te;
	private PC_GresColorMap colorMap;
	private PC_GresButton close;
	private int screen[][] = new int[PCws_WeaselPluginTouchscreen.WIDTH][PCws_WeaselPluginTouchscreen.HEIGHT];
	private int timer = -1;
	
	public PCws_GuiWeaselTouchscreen(EntityPlayer player, Object[] o){
		te = GameInfo.getTE(player.worldObj, (Integer)o[0], (Integer)o[1], (Integer)o[2]);
	}
	
	@Override
	public void initGui(PC_IGresGui gui) {
		updateScreen(gui);
		PC_GresWidget mvg = new PC_GresLayoutV();
		mvg.setAlignH(PC_GresAlign.CENTER);

		colorMap = new PC_GresColorMap(screen).setScale(4);
		mvg.add(colorMap);
		mvg.add(close = new PC_GresButton(Lang.tr("pc.gui.close")).setButtonPadding(4, 4));

		gui.add(mvg);
		gui.setBackground(0x90101010, 0x90101010);

		gui.setFocus(colorMap);
	}

	@Override
	public void onGuiClosed(PC_IGresGui gui) {}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
		if (widget == colorMap) {
			if(timer == -1) {
				timer = 0;
				return;
			}
			String event = colorMap.getLastEvent();
			if(event.length()==0) return;
			PC_VecI mouse = colorMap.getLastMousePos();
			int mouseKey = colorMap.getLastMouseKey();
			char key = colorMap.getLastKey();

			PC_PacketHandler.setTileEntity(te, "msg", "event", new PC_Struct4<String, PC_VecI, Integer, Integer>(event, mouse, mouseKey, (int)key));
			
			colorMap.setLastEvent("");
		}else if(widget == close){
			gui.close();
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
	public void updateScreen(PC_IGresGui gui) {
		for(int j=0; j<PCws_WeaselPluginTouchscreen.HEIGHT; j++){
			for(int i=0; i<PCws_WeaselPluginTouchscreen.WIDTH; i++){
				screen[i][j] = (Integer)te.getData("pic["+i+"]["+j+"]");
			}
		}
	}

	@Override
	public boolean drawBackground(PC_IGresGui gui, int par1, int par2,
			float par3) {
		return false;
	}

	

}
