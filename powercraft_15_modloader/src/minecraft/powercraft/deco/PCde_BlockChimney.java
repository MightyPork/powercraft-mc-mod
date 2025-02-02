package powercraft.deco;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityEnderEye;
import net.minecraft.src.EntityFireworkRocket;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityXPOrb;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Icon;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;

@PC_BlockInfo(name="Chimney", itemBlock=PCde_ItemBlockChimney.class, tileEntity=PCde_TileEntityChimney.class)
public class PCde_BlockChimney extends PC_Block {

	public PCde_BlockChimney(int id) {
		super(id, Material.rock);
		setHardness(1.5F);
		setResistance(50.0F);
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
	public Icon getIcon(PC_Direction par1, int par2) {
		if (par2 == 0) return Block.cobblestone.getBlockTextureFromSide(par1.getMCDir());
		if (par2 == 1) return Block.brick.getBlockTextureFromSide(par1.getMCDir());
		if (par2 == 2) return Block.stoneBrick.getBlockTextureFromSide(par1.getMCDir());
		
		if (par2 == 3) return Block.stoneBrick.getIcon(par1.getMCDir(), 2);
		if (par2 == 4) return Block.stoneBrick.getIcon(par1.getMCDir(), 3);
		
		if (par2 == 5) return Block.sandStone.getIcon(par1.getMCDir(), 0);
		if (par2 == 6) return Block.sandStone.getIcon(par1.getMCDir(), 1);
		if (par2 == 7) return Block.sandStone.getIcon(par1.getMCDir(), 2);

		if (par2 == 8) return Block.netherBrick.getIcon(par1.getMCDir(), 1);

		if (par2 == 9) return Block.blockNetherQuartz.getIcon(par1.getMCDir(), 0);
		if (par2 == 10) return Block.blockNetherQuartz.getIcon(par1.getMCDir(), 1);
		if (par2 == 11) return Block.blockNetherQuartz.getIcon(par1.getMCDir(), 2);

		if (par2 == 12) return Block.cobblestoneMossy.getBlockTextureFromSide(par1.getMCDir());
		if (par2 == 13) return Block.stoneBrick.getIcon(par1.getMCDir(), 1);
		
		if (par2 == 14) return Block.blockClay.getBlockTextureFromSide(par1.getMCDir());
		if (par2 == 15) return Block.blockIron.getBlockTextureFromSide(par1.getMCDir());
		
		return null;
	}

	@Override
	public boolean renderInventoryBlock(int metadata, Object renderer) {
		final float px = 0.0625F;
		float w = px * 3;

		setBlockBounds(0, 0, 0, 1, 1, w);
		PC_Renderer.renderInvBox(renderer, this, metadata);
		setBlockBounds(1 - w, 0, w, 1, 1, 1 - w);
		PC_Renderer.renderInvBox(renderer, this, metadata);
		setBlockBounds(0, 0, 1 - w, 1, 1, 1);
		PC_Renderer.renderInvBox(renderer, this, metadata);
		setBlockBounds(0, 0, w, w, 1, 1 - w);
		PC_Renderer.renderInvBox(renderer, this, metadata);
		setBlockBounds(0, 0, 0, 1, 1, 1);
		
		return true;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Object renderer) {
		
		final float px = 0.0625F;
		float w = px * 3;

		setBlockBounds(0, 0, 0, 1, 1, w);
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		setBlockBounds(1 - w, 0, w, 1, 1, 1 - w);
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		setBlockBounds(0, 0, 1 - w, 1, 1, 1);
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		setBlockBounds(0, 0, w, w, 1, 1 - w);
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		setBlockBounds(0, 0, 0, 1, 1, 1);
		
		return true;
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisAlignedBB, List list, Entity entity) {
		final float px = 0.0625F*3;
		setBlockBounds(0, 0, 0, px, 1, 1);
		AxisAlignedBB axisalignedbb1 = super.getCollisionBoundingBoxFromPool(world, x, y, z);
        if (axisalignedbb1 != null && axisAlignedBB.intersectsWith(axisalignedbb1)){
        	list.add(axisalignedbb1);
        }
        setBlockBounds(0, 0, 0, 1, 1, px);
		axisalignedbb1 = super.getCollisionBoundingBoxFromPool(world, x, y, z);
        if (axisalignedbb1 != null && axisAlignedBB.intersectsWith(axisalignedbb1)){
        	list.add(axisalignedbb1);
        }
        setBlockBounds(1-px, 0, 0, 1, 1, 1);
		axisalignedbb1 = super.getCollisionBoundingBoxFromPool(world, x, y, z);
        if (axisalignedbb1 != null && axisAlignedBB.intersectsWith(axisalignedbb1)){
        	list.add(axisalignedbb1);
        }
        setBlockBounds(0, 0, 1-px, 1, 1, 1);
        axisalignedbb1 = super.getCollisionBoundingBoxFromPool(world, x, y, z);
        if (axisalignedbb1 != null && axisAlignedBB.intersectsWith(axisalignedbb1)){
        	list.add(axisalignedbb1);
        }
        setBlockBounds(0, 0, 0, 1, 1, 1);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		return null;
	}
	
}
