package powercraft.deco;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.item.PC_IItemInfo;

@PC_BlockInfo(name="Redstone Storage")
public class PCde_BlockRedstoneStorage extends PC_Block implements PC_IItemInfo {
	private boolean wiresProvidePower = true;
	private Set blocksNeedingUpdate = new HashSet();
	
	public PCde_BlockRedstoneStorage(int id) {
		super(id, Material.rock, "redstonestorage");
		setHardness(1.5F);
		setResistance(50.0F);
		setStepSound(Block.soundMetalFootstep);
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this));
		return arrayList;
	}
	
	@Override
	public boolean showInCraftingTool(){
		return false;
	}
	
}
