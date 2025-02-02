package powercraft.logic;

import net.minecraft.src.EntityPlayer;
import powercraft.core.PC_GresInventory;
import powercraft.core.PC_GresInventoryPlayer;
import powercraft.core.PC_GresLabel;
import powercraft.core.PC_GresLayoutH;
import powercraft.core.PC_GresRadioButton;
import powercraft.core.PC_GresRadioButton.PC_GresRadioGroup;
import powercraft.core.PC_GresWidget;
import powercraft.core.PC_GresWindow;
import powercraft.core.PC_IGresClient;
import powercraft.core.PC_IGresGui;
import powercraft.core.PC_Utils;

public class PClo_GuiSpecial extends PClo_ContainerSpecial implements PC_IGresClient {
	
	private String addString;
	
	public PClo_GuiSpecial(EntityPlayer player, Object[] o) {
		super(player, o);
		addString = PClo_SpecialType.names[te.getType()];
	}

	@Override
	public void initGui(PC_IGresGui gui) {
		PC_GresWindow w = new PC_GresWindow(PC_Utils.tr("pc.gui.special."+addString+".name"));
		
		PC_GresWidget lh = new PC_GresLayoutH();
		
		lh.add(new PC_GresLabel(PC_Utils.tr("pc.gui.special."+addString+".inv")));
		PC_GresInventory inv = new PC_GresInventory(1, 1);
		inv.setSlot(lSlot.get(0), 0, 0);
		lh.add(inv);
		
		w.add(lh);
		
		w.add(new PC_GresInventoryPlayer(true));
		
		gui.add(w);
	}

	@Override
	public void onGuiClosed(PC_IGresGui gui) {}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {}

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
	public boolean drawBackground(PC_IGresGui gui, int par1, int par2, float par3) {
		return false;
	}

}
