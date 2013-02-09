package powercraft.logic;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.management.PC_TileEntity;
import powercraft.management.PC_Utils.Lang;
import powercraft.management.gres.PC_GresInventory;
import powercraft.management.gres.PC_GresInventoryPlayer;
import powercraft.management.gres.PC_GresLabel;
import powercraft.management.gres.PC_GresLayoutH;
import powercraft.management.gres.PC_GresWidget;
import powercraft.management.gres.PC_GresWindow;
import powercraft.management.gres.PC_IGresClient;
import powercraft.management.gres.PC_IGresGui;

public class PClo_GuiSpecial extends PClo_ContainerSpecial implements PC_IGresClient {
	
	private String addString;
	
	public PClo_GuiSpecial(EntityPlayer player, PC_TileEntity te, Object[] o) {
		super(player, te, o);
		addString = PClo_SpecialType.names[tileEntity.getType()];
	}

	@Override
	public void initGui(PC_IGresGui gui) {
		PC_GresWindow w = new PC_GresWindow(Lang.tr("pc.gui.special."+addString+".name"));
		
		PC_GresWidget lh = new PC_GresLayoutH();
		
		lh.add(new PC_GresLabel(Lang.tr("pc.gui.special."+addString+".inv")));
		PC_GresInventory inv = new PC_GresInventory(1, 1);
		inv.setSlot(0, 0, invSlots[0]);
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

	@Override
	public void keyChange(String key, Object value) {}

}