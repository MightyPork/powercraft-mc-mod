package powercraft.weasel;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.management.PC_GresButton;
import powercraft.management.PC_GresLabel;
import powercraft.management.PC_GresLayoutH;
import powercraft.management.PC_GresLayoutV;
import powercraft.management.PC_GresTab;
import powercraft.management.PC_GresTextEdit;
import powercraft.management.PC_GresTextEditMultiline;
import powercraft.management.PC_GresWidget;
import powercraft.management.PC_IGresGui;
import powercraft.management.PC_PacketHandler;
import powercraft.management.PC_Utils.Lang;

public class PCws_GuiWeaselTerminal extends PCws_GuiWeasel {

	private PC_GresTextEditMultiline cons;
	private PC_GresTextEdit inp;
	private PC_GresButton send;
	
	public PCws_GuiWeaselTerminal(EntityPlayer player, Object[] o) {
		super(player, o);
	}

	@Override
	public void onReturnPressed(PC_IGresGui gui) {
		gui.close();
	}

	private void makeTerminalTab(PC_GresTab tab){
		PC_GresLayoutV lv = new PC_GresLayoutV();
		lv.add(cons = new PC_GresTextEditMultiline(((String)te.getData("text")).trim(), 300, 120, 0x00EE00, 0x000000));
		cons.enable(false);
		cons.scrollToBottom();
		PC_GresLayoutH lh = new PC_GresLayoutH();
		lh.add(inp = new PC_GresTextEdit("", 30));
		inp.setColor(PC_GresWidget.textColorEnabled, 0x009900);
		inp.setColor(PC_GresWidget.textColorDisabled, 0x009900);
		inp.setColor(PC_GresWidget.textColorClicked, 0x00ff00);
		inp.setColor(PC_GresWidget.textColorHover, 0x00ff00);
		lh.add(send = new PC_GresButton(Lang.tr("pc.gui.ok")));
		lv.add(lh);
		tab.addTab(lv, new PC_GresLabel(Lang.tr("pc.gui.weasel.terminal.terminal")));
	}
	
	@Override
	protected void addTabs(PC_GresTab tab) {
		makeTerminalTab(tab);
	}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
		if(widget==send){
			String txt = inp.getText().trim();
			inp.setText("");
			PC_PacketHandler.setTileEntity(te, "msg", "input", txt);
		}else{
			super.actionPerformed(widget, gui);
		}
	}

	@Override
	public void updateScreen(PC_IGresGui gui) {
		if(!cons.getText().equals(((String)te.getData("text")).trim())){
			cons.setText(((String)te.getData("text")).trim());
			cons.scrollToBottom();
		}
	}
	
}
