package powercraft.projector;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Material;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;

@PC_BlockInfo(name="Projector", tileEntity=PCpj_TileEntityProjector.class, canPlacedRotated=true)
public class PCpj_BlockProjector extends PC_Block {

	public PCpj_BlockProjector(int id) {
		super(id, Material.rock, "projector");
		setCreativeTab(CreativeTabs.tabDecorations);
	}

}
