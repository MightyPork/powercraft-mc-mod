package powercraft.machines;

import java.util.Random;

import net.minecraft.src.EntityPlayer;
import powercraft.core.PC_ClientUtils;
import powercraft.core.PC_GresButton;
import powercraft.core.PC_GresLabel;
import powercraft.core.PC_GresLayoutH;
import powercraft.core.PC_GresSeparatorH;
import powercraft.core.PC_GresWidget;
import powercraft.core.PC_GresWidget.PC_GresAlign;
import powercraft.core.PC_GresWindow;
import powercraft.core.PC_IGresClient;
import powercraft.core.PC_IGresGui;
import powercraft.core.PC_Utils;

public class PCma_GuiXPBank implements PC_IGresClient {

	private PCma_TileEntityXPBank xpbank;
	private PC_GresWidget buttonClose;
	private PC_GresWidget txStoragePoints;
	private PC_GresWidget txPlayerLevels;
	private EntityPlayer player;
	
	public PCma_GuiXPBank(EntityPlayer player, Object[] o){
		xpbank = PC_Utils.getTE(player.worldObj, (Integer)o[0], (Integer)o[1], (Integer)o[2]);
		this.player = player;
	}
	
	@Override
	public void initGui(PC_IGresGui gui) {
		PC_GresWindow w = new PC_GresWindow(PC_Utils.tr(mod_PowerCraftMachines.xpBank.getBlockName()+".name"));
		w.setAlignH(PC_GresAlign.CENTER);

		if (xpbank.getXP() < 0) xpbank.setXP(0);

		PC_GresWidget hg;

		int labelWidth = 0;
		labelWidth = Math.max(labelWidth, w.getStringWidth(PC_Utils.tr("pc.gui.xpbank.storagePoints")));
		labelWidth = Math.max(labelWidth, w.getStringWidth(PC_Utils.tr("pc.gui.xpbank.currentPlayerLevel")));
		labelWidth = Math.max(labelWidth, w.getStringWidth(PC_Utils.tr("pc.gui.xpbank.withdraw")));
		labelWidth = Math.max(labelWidth, w.getStringWidth(PC_Utils.tr("pc.gui.xpbank.deposit")));
		labelWidth = Math.max(labelWidth, 80);

		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.LEFT).setMinWidth(200);
		hg.add(new PC_GresLabel(PC_Utils.tr("pc.gui.xpbank.storagePoints")).setMinWidth(labelWidth).setAlignH(PC_GresAlign.RIGHT));
		hg.add(txStoragePoints = new PC_GresLabel(xpbank.getXP() + "").setColor(PC_GresWidget.textColorEnabled, 0x009900));
		hg.add(new PC_GresLabel(PC_Utils.tr("pc.gui.xpbank.xpUnit")));
		w.add(hg);

		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.LEFT).setMinWidth(200);
		hg.add(new PC_GresLabel(PC_Utils.tr("pc.gui.xpbank.withdraw")).setMinWidth(labelWidth).setAlignH(PC_GresAlign.RIGHT));

		hg.add(new PC_GresButton(PC_Utils.tr("pc.gui.xpbank.oneLevel")).setId(10).setMinWidth(50).setWidgetMargin(2));
		hg.add(new PC_GresButton(PC_Utils.tr("pc.gui.xpbank.all")).setId(11).setMinWidth(50).setWidgetMargin(2));
		w.add(hg);

		w.add(new PC_GresSeparatorH(0, 5).setLineColor(0x999999));

		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.LEFT).setMinWidth(200);
		hg.add(new PC_GresLabel(PC_Utils.tr("pc.gui.xpbank.currentPlayerLevel")).setMinWidth(labelWidth).setAlignH(PC_GresAlign.RIGHT));
		hg.add(txPlayerLevels = new PC_GresLabel(xpbank.getXP() + "").setColor(PC_GresWidget.textColorEnabled, 0x990099));
		hg.add(new PC_GresLabel(PC_Utils.tr("pc.gui.xpbank.xpLevels")));
		w.add(hg);

		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.LEFT).setMinWidth(200);
		hg.add(new PC_GresLabel(PC_Utils.tr("pc.gui.xpbank.deposit")).setMinWidth(labelWidth).setAlignH(PC_GresAlign.RIGHT));

		hg.add(new PC_GresButton(PC_Utils.tr("pc.gui.xpbank.oneLevel")).setId(20).setMinWidth(50).setWidgetMargin(2));
		hg.add(new PC_GresButton(PC_Utils.tr("pc.gui.xpbank.all")).setId(21).setMinWidth(50).setWidgetMargin(2));
		w.add(hg);

		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.CENTER);
		hg.add(buttonClose = new PC_GresButton(PC_Utils.tr("pc.gui.ok")).setId(0));
		w.add(hg);

		updateCounters();

		gui.add(w);
	}

	private void updateCounters() {
		txStoragePoints.setText(xpbank.getXP() + "").setMinWidth(0);
		txPlayerLevels.setText(player.experienceLevel + "").setMinWidth(0);
	}
	
	@Override
	public void onGuiClosed(PC_IGresGui gui) {
		xpbank.worldObj.markBlockAsNeedsUpdate(xpbank.xCoord, xpbank.yCoord, xpbank.zCoord);
	}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
		Random rand = new Random();

		switch (widget.getId()) {
			case 0:
				gui.close();
				break;

			case 10: //withdraw one level

				xpbank.givePlayerXP(player, 1);
				
				//withdrawOneLevel();
				
				PC_ClientUtils.mc().theWorld.playSoundAtEntity(player, "random.orb", 0.3F, 0.5F * ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.8F));
				break;

			case 11: // withdraw all				
				
				//withdrawOneLevel();
				xpbank.givePlayerXP(player, xpbank.getXP());
				
				PC_ClientUtils.mc().theWorld.playSoundAtEntity(player, "random.orb", 0.3F, 0.5F * ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.8F));
				
				break;

			case 20: //deposit one level
				
				//depositOneLevel();
				xpbank.givePlayerXP(player, -1);
				
				PC_ClientUtils.mc().theWorld.playSoundAtEntity(player, "random.orb", 0.3F, 0.5F * ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.8F));
				break;

			case 21: //deposit all
				
				//depositOneLevel();
				xpbank.givePlayerXP(player, -player.experienceTotal);

				PC_ClientUtils.mc().theWorld.playSoundAtEntity(player, "random.orb", 0.3F, 0.5F * ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.8F));
				break;


		}

		updateCounters();
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
	public void updateTick(PC_IGresGui gui) {
		updateCounters();
	}

	@Override
	public void updateScreen(PC_IGresGui gui) {}

	@Override
	public boolean drawBackground(PC_IGresGui gui, int par1, int par2,
			float par3) {
		return false;
	}

}
