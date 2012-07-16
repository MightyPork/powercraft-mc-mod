package net.minecraft.src;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.src.forge.ITextureProvider;


/**
 * Decorative block;<br>
 * Subtypes: iron frame, redstone storage, lightning conductor, obsidian chest
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
public class PCde_BlockDeco extends BlockContainer implements PC_IBlockType, PC_ISwapTerrain, ITextureProvider {

	@Override
	public String getTextureFile() {
		return getTerrainFile();
	}

	@Override
	public TileEntity getBlockEntity() {
		return new PCde_TileEntityDeco();
	}

	@Override
	public String getTerrainFile() {
		return mod_PCdeco.getTerrainFile();
	}


	@Override
	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer) {

		PCde_TileEntityDeco tileentity = (PCde_TileEntityDeco) world.getBlockTileEntity(i, j, k);

		ItemStack ihold = entityplayer.getCurrentEquippedItem();
		if (ihold != null) {
			if (ihold.getItem() instanceof ItemBlock) {
				if (ihold.getItem().shiftedIndex != blockID) {
					if (Block.blocksList[ihold.getItem().shiftedIndex] instanceof PC_IBlockType) {
						return false;
					}
					if (ihold.getItem().shiftedIndex == Block.blockSteel.blockID) return false;
				} else if (ihold.getItemDamage() != tileentity.type) {
					return false;
				}
			}
		}

		if (tileentity != null) {
			if (tileentity.type == 3 && tileentity.getInventory() != null) {
				PC_Utils.openGres(entityplayer, new PCde_GuiTransmutator(entityplayer, (PCde_InventoryTransmutationContainer) tileentity.getInventory()));
				return true;
			}
		}

		return false;
	}


	/**
	 * Decorative block;
	 * 
	 * @param i id
	 * @param j texture index
	 * @param material block material
	 */
	public PCde_BlockDeco(int i, int j, Material material) {
		super(i, j, material);
	}

	/** flag used by renderer to alter individual textures */
	public int renderFlag = 0;

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		// item + particle

		// iron frame
		if (meta == 0) {
			return 22;
		}

		// redstone block
		if (meta == 1) {
			return 129;
		}

		// the lightning conductor
		if (meta == 2) {
			return 22;
		}

		// the obsidian storage
		if (meta == 3) {
			return 37;
		}

		return 0;
	}

	@Override
	public int colorMultiplier(IBlockAccess iblockaccess, int x, int y, int z) {
		// colors particles
		PCde_TileEntityDeco ted = getTE(iblockaccess, x, y, z);
		if (ted == null) return 0xffffff;
		if (ted.type == 0) {
			return 0xffffff;
		}
		if (ted.type == 1) {
			return 0xcc0000;
		}
		if (ted.type == 2) {
			return 0xffffff;
		}
		if (ted.type == 3) {
			return 0xffffff;
		}
		return 0xffffff;

	}

	@Override
	public int getRenderColor(int i) {
		if (i == 0) {
			return 0xcccccc;
		}
		if (i == 1) {
			return 0xcc0000;
		}
		if (i == 2) {
			return 0xcccccc;
		}
		if (i == 3) {
			return 0xcccccc;
		}
		return 0xffffff;
	}


	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k) {
		PCde_TileEntityDeco ted = getTE(iblockaccess, i, j, k);
		if (ted == null) return;

		// frames
		if (ted.type == 0) {
			setBlockBounds(0, 0, 0, 1, 1, 1);
			return;
		}

		// storage block RS
		if (ted.type == 1) {
			setBlockBounds(0, 0, 0, 1, 1, 1);
			return;
		}

		// point
		if (ted.type == 2) {
			setBlockBounds(0, 0, 0, 1, 2.5F, 1);
			return;
		}

		// obsidian storage
		if (ted.type == 3) {
			setBlockBounds(0, 0, 0, 1, 1, 1);
			return;
		}

		setBlockBounds(0, 0, 0, 1, 1, 1);


	}

	@Override
	public void getCollidingBoundingBoxes(World world, int x, int y, int z, AxisAlignedBB axisalignedbb, ArrayList arraylist) {

		PCde_TileEntityDeco ted = getTE(world, x, y, z);
		if (ted == null) return;

		if (ted.type == 0 || ted.type == 1 || ted.type == 3) {
			setBlockBounds(0, 0, 0, 1, 1, 1);
			super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
			return;
		}
		if (ted.type == 2) {
			setBlockBounds(0, 0, 0, 1, 2.5F, 1);
			super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
			return;
		}

		setBlockBounds(0, 0, 0, 1, 1, 1);
		super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
	}

	/**
	 * Get tile entity at position
	 * 
	 * @param iblockaccess block access
	 * @param x
	 * @param y
	 * @param z
	 * @return the tile entity or null
	 */
	public PCde_TileEntityDeco getTE(IBlockAccess iblockaccess, int x, int y, int z) {
		TileEntity te = iblockaccess.getBlockTileEntity(x, y, z);
		if (te == null) {
			return null;
		}
		return (PCde_TileEntityDeco) te;
	}

	@Override
	public int getBlockTexture(IBlockAccess iblockaccess, int x, int y, int z, int side) {
		// in world - block
		PCde_TileEntityDeco ted = getTE(iblockaccess, x, y, z);
		if (ted == null) return 0;
		if (ted.type == 0) {
			return 22;
		}
		if (ted.type == 1) {
			return 129; // only this used
		}
		if (ted.type == 2) {
			return 22;
		}
		if (ted.type == 3) {
			if (renderFlag == 1) return 22;
			return 37;
		}
		return 0;

	}

	@Override
	protected int damageDropped(int i) {
		return 0;
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return -1;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		if (iblockaccess.getBlockMetadata(i, j, k) == 0) {
			return true;
		}
		return super.shouldSideBeRendered(iblockaccess, i, j, k, l);
	}

	@Override
	public void onBlockRemoval(World world, int x, int y, int z) {

		PCde_TileEntityDeco teg = getTE(world, x, y, z);

		if (teg != null) {

			if (!PC_Utils.isCreative()) {
				dropBlockAsItem_do(world, x, y, z, new ItemStack(mod_PCdeco.deco, 1, teg.type));
			}
		}

		super.onBlockRemoval(world, x, y, z);
	}

	@Override
	public int getRenderBlockPass() {
		return 0;// solid
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}

	@Override
	public int getRenderType() {
		return PCde_Renderer.decorativeBlockRenderer;
	}



	@Override
	public Set<String> getBlockFlags(World world, PC_CoordI pos) {

		PCde_TileEntityDeco ted = getTE(world, pos.x, pos.y, pos.z);
		if (ted == null) return new HashSet<String>(0);

		Set<String> set = new HashSet<String>();

		set.add("NO_HARVEST");
		set.add("NO_PICKUP");
		set.add("DECORATIVE");
		set.add("PASSIVE");

		if (ted.type == 0) set.add("IRON_FRAME");
		if (ted.type == 1) set.add("REDSTONE_BLOCK");

		if (ted.type == 1) {
			set.add("HARVEST_STOP");
		}
		if (ted.type == 0) {
			set.add("TRANSLUCENT");
		}

		return set;
	}

	@Override
	public Set<String> getItemFlags(ItemStack stack) {
		Set<String> set = new HashSet<String>();
		set.add("NO_BUILD");
		if (stack.getItemDamage() == 0) set.add("IRON_FRAME");
		if (stack.getItemDamage() == 1) set.add("REDSTONE_BLOCK");
		return set;
	}

}
