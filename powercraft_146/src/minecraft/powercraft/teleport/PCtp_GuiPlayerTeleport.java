package powercraft.teleport;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.management.PC_GresButton;
import powercraft.management.PC_GresLabel;
import powercraft.management.PC_GresLayoutV;
import powercraft.management.PC_GresRadioButton;
import powercraft.management.PC_GresScrollArea;
import powercraft.management.PC_GresTextEdit;
import powercraft.management.PC_GresWidget;
import powercraft.management.PC_GresWidget.PC_GresAlign;
import powercraft.management.PC_IGresClient;
import powercraft.management.PC_IGresGui;
import powercraft.management.PC_PacketHandler;
import powercraft.management.PC_Utils.Lang;

public class PCtp_GuiPlayerTeleport implements PC_IGresClient {

	private EntityPlayer player;
	private List<String> names;
	private PC_GresButton cancel;
	private PC_GresTextEdit search;
	private List<PC_GresButton> targets;
	private PC_GresWidget targetsBox;
	private PC_GresWidget targetsBoxScroll;
	
	public PCtp_GuiPlayerTeleport(EntityPlayer player, Object[]o) {
		this.player = player;
		names = (List<String>)o[0];
	}
	
	@Override
	public void initGui(PC_IGresGui gui) {
		PC_GresWidget vg = new PC_GresLayoutV();
		vg.add(new PC_GresLabel(Lang.tr("pc.gui.teleportTo.title")).setColor(PC_GresWidget.textColorDisabled, 0xFFFFFF).enable(false));
		vg.add(search = new PC_GresTextEdit("", 10));
		targetsBox = new PC_GresLayoutV();
		targetsBox.setAlignH(PC_GresAlign.LEFT);
		targets = new ArrayList<PC_GresButton>();
		for(String name:names){
			PC_GresButton but = new PC_GresButton(name);
			targetsBox.add(but);
			targets.add(but);
		}
		vg.add(targetsBoxScroll = new PC_GresScrollArea(0, 120, targetsBox, PC_GresScrollArea.VSCROLL));
		vg.add(cancel = new PC_GresButton(Lang.tr("pc.gui.cancel")));
		gui.add(vg);
	}

	@Override
	public void onGuiClosed(PC_IGresGui gui) {}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
		if(widget==cancel){
			gui.close();
		}else if(widget==search){
			String searchFor = widget.getText();
			for(PC_GresButton but:targets){
				but.setVisible(but.getText().toLowerCase().contains(searchFor.toLowerCase()));
			}
			targetsBox.setSize(targetsBox.getSize().x, 10);
			targetsBox.calcSize();
			targetsBoxScroll.calcChildPositions();
		}else if(widget instanceof PC_GresButton){
			PC_PacketHandler.sendToPacketHandler(player.worldObj, "Teleporter", "makeTelport", widget.getText());
			gui.close();
		}
	}

	@Override
	public void onEscapePressed(PC_IGresGui gui) {
		gui.close();
	}

	@Override
	public void onReturnPressed(PC_IGresGui gui) {
		gui.close();
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

}
