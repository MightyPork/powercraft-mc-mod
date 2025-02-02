package powercraft.management;

import net.minecraft.src.Container;

public interface PC_IGresClient {

	public void initGui(PC_IGresGui gui);
	
	public void onGuiClosed(PC_IGresGui gui);
	
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui);
	
	public void onEscapePressed(PC_IGresGui gui);
	
	public void onReturnPressed(PC_IGresGui gui);
	
	public void updateTick(PC_IGresGui gui);
	
	public void updateScreen(PC_IGresGui gui);
	
	public boolean drawBackground(PC_IGresGui gui, int par1, int par2, float par3);
	
}
