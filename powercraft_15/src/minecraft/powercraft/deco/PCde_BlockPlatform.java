package powercraft.deco;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.registry.PC_KeyRegistry;
import powercraft.api.registry.PC_MSGRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;

@PC_BlockInfo(name="Platform", tileEntity=PCde_TileEntityPlatform.class)
public class PCde_BlockPlatform extends PC_Block {

	public PCde_BlockPlatform(int id) {
		super(id, Material.rock, "ironplate", "fence");
		setHardness(1.5F);
		setResistance(30.0F);
		setStepSound(Block.soundMetalFootstep);
		setCreativeTab(CreativeTabs.tabDecorations);
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
	
	public void addACollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisalignedbb, List arraylist, Entity entity) {
		AxisAlignedBB axisalignedbb1 = super.getCollisionBoundingBoxFromPool(world, x, y, z);

        if (axisalignedbb1 != null && axisalignedbb.intersectsWith(axisalignedbb1))
        {
        	arraylist.add(axisalignedbb1);
        }
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisalignedbb, List arraylist, Entity entity) {

		boolean[] fences = getFencesShownLedge(world, new PC_VecI(x, y, z));

		if (fences[0]) {
			setBlockBounds(1 - 0.0625F, 0, 0, 1, 1.5F, 1);
			addACollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, entity);
		}
		if (fences[1]) {
			setBlockBounds(0, 0, 0, 0.0625F, 1.5F, 1);
			addACollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, entity);
		}
		if (fences[2]) {
			setBlockBounds(0, 0, 1 - 0.0625F, 1, 1.5F, 1);
			addACollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, entity);
		}
		if (fences[3]) {
			setBlockBounds(0, 0, 0, 1, 1.5F, 0.0625F);
			addACollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, entity);
		}
		if (fences[4]) {
			setBlockBounds(0, 0, 0, 1, 0.0625F, 1);
			addACollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, entity);
		}

		setBlockBounds(0, 0, 0, 1, 1, 1);
		
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		setBlockBounds(0, 0, 0, 1, 1, 1);
		return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
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
	public static boolean[] getFencesShownLedge(World world, PC_VecI pos) {
		boolean sides[] = { false, false, false, false, true };
		sides[0] = isFallBlock(world, pos.offset(1, 0, 0)) && isFallBlock(world, pos.offset(1, -1, 0));
		sides[1] = isFallBlock(world, pos.offset(-1, 0, 0)) && isFallBlock(world, pos.offset(-1, -1, 0));
		sides[2] = isFallBlock(world, pos.offset(0, 0, 1)) && isFallBlock(world, pos.offset(0, -1, 1));
		sides[3] = isFallBlock(world, pos.offset(0, 0, -1)) && isFallBlock(world, pos.offset(0, -1, -1));
		sides[4] = !isClimbBlock(world, pos.offset(0, -1, 0));
		return sides;
	}

	private static boolean isFallBlock(World world, PC_VecI pos) {
		int id = PC_Utils.getBID(world, pos);
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
		if (PC_MSGRegistry.hasFlag(world, pos, "BELT")) {
			return true;
		}
		return false;
	}

	private static boolean isClimbBlock(World world, PC_VecI pos) {
		int id = PC_Utils.getBID(world, pos);
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
	public boolean renderInventoryBlock(int metadata, Object renderer) {
		float p = 0.0625F;
		setBlockBounds(0, 0, 0, 1, p, 1);
		PC_Renderer.renderInvBoxWithTexture(renderer, this, sideIcons[0]);
		setBlockBounds(0, 0, 1 - p, 1, 1, 1);
		PC_Renderer.renderInvBoxWithTexture(renderer, this, sideIcons[1]);
		setBlockBounds(0, 0, 0, p, 1, 1);
		PC_Renderer.renderInvBoxWithTexture(renderer, this, sideIcons[1]);
		return true;
	}
	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Object renderer) {
		return true;
	}
	
	@Override
	public PC_VecI moveBlockTryToPlaceAt(World world, int x, int y, int z,
			PC_Direction dir, float xHit, float yHit, float zHit,
			ItemStack itemStack, EntityPlayer entityPlayer) {
		
		Item item = itemStack.getItem();
		if(item instanceof ItemBlock){
			Block block = Block.blocksList[((ItemBlock) item).getBlockID()];
			PC_Direction pRot = PC_Direction.getFromPlayerDir(MathHelper.floor_double(((entityPlayer.rotationYaw * 4F) / 360F) + 0.5D) & 3);
			PC_VecI offset = pRot.getOffset();
			if(block==PCde_App.stairs && PC_KeyRegistry.isPlacingReversed(entityPlayer)){
				offset.y--;
			}
			return offset;
		}
		return null;
	}
	
}
