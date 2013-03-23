package powercraft.weasel;

import net.minecraft.block.Block;
import net.minecraft.util.Icon;
import powercraft.api.PC_Utils.ValueWriting;
import powercraft.api.renderer.PC_Renderer;

public class PCws_WeaselPluginInfoDiskDrive extends PCws_WeaselPluginInfo {
	
	private static PCws_WeaselModelDiskDrive model = new PCws_WeaselModelDiskDrive();
	
	public PCws_WeaselPluginInfoDiskDrive() {
		super(PCws_WeaselPluginDiskDrive.class, "Weasel Disk Drive", "weasel_diskdrive_down", "weasel_diskdrive_top", "weasel_diskdrive_side");
	}

	@Override
	public void renderInventoryBlock(Block block, Object renderer) {

		float px = 0.0625F;
		block.setBlockBounds(0, 0, 0, 16 * px, 13 * px, 16 * px);
		PC_Renderer.renderInvBoxWithTextures(renderer, block, new Icon[] { icons[4], icons[5], icons[6], icons[6], icons[6], icons[6] });
		block.setBlockBounds(0, 0, 0, 1, 1, 1);
	}

	@Override
	public float[] getBounds() {
		return new float[] { 0, 0, 0, 1, 1 - 2 * 0.0625F, 1 };
	}
	
	@Override
	public PCws_WeaselModelBase getModel() {
		return model;
	}
	
	@Override
	public int inventorySize(){
		return 8;
	}
	
	@Override
	public int inventoryStackLimit(){
		return 1;
	}
	
	@Override
	public Icon getTexture(int side) {
		if(side==1){
			return icons[5];
		}else if(side==0){
			return icons[4];
		}else{
			return icons[6];
		}
	}
	
}

