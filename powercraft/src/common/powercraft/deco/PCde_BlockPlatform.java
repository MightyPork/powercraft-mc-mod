package powercraft.deco;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Entity;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import powercraft.core.PC_Block;
import powercraft.core.PC_CoordI;
import powercraft.core.PC_IBlockRenderer;
import powercraft.core.PC_ICraftingToolDisplayer;
import powercraft.core.PC_Renderer;
import powercraft.core.PC_Utils;

public class PCde_BlockPlatform extends PC_Block implements PC_IBlockRenderer {

	public PCde_BlockPlatform(int id) {
		super(id, 22, Material.rock);
		setHardness(1.5F);
		setResistance(30.0F);
		setStepSound(Block.soundMetalFootstep);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public String getDefaultName() {
		return null;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new PCde_TileEntityPlatform();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return true;
	}
	
	@Override
	public void addCollidingBlockToList(World world, int x, int y, int z, AxisAlignedBB axisalignedbb, List arraylist, Entity entity) {

		boolean[] fences = getFencesShownLedge(world, new PC_CoordI(x, y, z));

		if (fences[0]) {
			setBlockBounds(1 - 0.0625F, 0, 0, 1, 1.0F, 1);
			super.addCollidingBlockToList(world, x, y, z, axisalignedbb, arraylist, entity);
		}
		if (fences[1]) {
			setBlockBounds(0, 0, 0, 0.0625F, 1.0F, 1);
			super.addCollidingBlockToList(world, x, y, z, axisalignedbb, arraylist, entity);
		}
		if (fences[2]) {
			setBlockBounds(0, 0, 1 - 0.0625F, 1, 1.0F, 1);
			super.addCollidingBlockToList(world, x, y, z, axisalignedbb, arraylist, entity);
		}
		if (fences[3]) {
			setBlockBounds(0, 0, 0, 1, 1.0F, 0.0625F);
			super.addCollidingBlockToList(world, x, y, z, axisalignedbb, arraylist, entity);
		}
		if (fences[4]) {
			setBlockBounds(0, 0, 0, 1, 0.0625F, 1);
			super.addCollidingBlockToList(world, x, y, z, axisalignedbb, arraylist, entity);
		}

		setBlockBounds(0, 0, 0, 1, 1, 1);
		
	}

	@Override
	public int getRenderColor(int i) {
		return 0xcccccc;
	}

	/**
	 * Get fences that are shown.
	 * 
	 * @param world world
	 * @param pos block pos
	 * @return bool{X+, X-, Z+, Z-}
	 */
	public static boolean[] getFencesShownLedge(World world, PC_CoordI pos) {
		boolean sides[] = { false, false, false, false, true };
		sides[0] = isFallBlock(world, pos.offset(1, 0, 0)) && isFallBlock(world, pos.offset(1, -1, 0));
		sides[1] = isFallBlock(world, pos.offset(-1, 0, 0)) && isFallBlock(world, pos.offset(-1, -1, 0));
		sides[2] = isFallBlock(world, pos.offset(0, 0, 1)) && isFallBlock(world, pos.offset(0, -1, 1));
		sides[3] = isFallBlock(world, pos.offset(0, 0, -1)) && isFallBlock(world, pos.offset(0, -1, -1));
		sides[4] = !isClimbBlock(world, pos.offset(0, -1, 0));
		return sides;
	}

	private static boolean isFallBlock(World world, PC_CoordI pos) {
		int id = pos.getId(world);
		if (id == 0 || Block.blocksList[id] == null) {
			return true;
		}

		if (id == Block.ladder.blockID || id == Block.vine.blockID) {
			return false;
		}

		if (Block.blocksList[id].getCollisionBoundingBoxFromPool(world, pos.x, pos.y, pos.z) == null) {
			return true;
		}
		if (Block.blocksList[id].blockMaterial.isLiquid() || !Block.blocksList[id].blockMaterial.isSolid()) {
			return true;
		}
		if (PC_Utils.hasFlag(world, pos, "BELT")) {
			return true;
		}
		return false;
	}

	private static boolean isClimbBlock(World world, PC_CoordI pos) {
		int id = pos.getId(world);
		if (id == 0 || Block.blocksList[id] == null) {
			return false;
		}

		if (id == Block.ladder.blockID || id == Block.vine.blockID) {
			return true;
		}
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k) {
		setBlockBounds(0, 0, 0, 1, 0.0625F, 1);
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, Object renderer) {
		float p = 0.0625F;
		boolean swapped = PC_Renderer.swapTerrain(block);
		block.setBlockBounds(0, 0, 0, 1, p, 1);
		PC_Renderer.renderInvBoxWithTexture(renderer, block, 22);
		block.setBlockBounds(0, 0, 1 - p, 1, 1, 1);
		PC_Renderer.renderInvBoxWithTexture(renderer, block, 20);
		block.setBlockBounds(0, 0, 0, p, 1, 1);
		PC_Renderer.renderInvBoxWithTexture(renderer, block, 20);
		PC_Renderer.resetTerrain(swapped);
	}

	@Override
	public void renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, Object renderer) {
		
	}
	
	@Override
	public List<String> getBlockFlags(World world, PC_CoordI pos, List<String> list) {

		list.add(PC_Utils.NO_HARVEST);
		list.add(PC_Utils.NO_PICKUP);
		list.add(PC_Utils.PASSIVE);
		
		return list;
	}

	@Override
	public List<String> getItemFlags(ItemStack stack, List<String> list) {
		list.add(PC_Utils.NO_BUILD);
		return list;
	}
	
}
