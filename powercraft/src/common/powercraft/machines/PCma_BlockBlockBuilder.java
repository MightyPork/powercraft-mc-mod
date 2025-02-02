package powercraft.machines;

import java.util.List;
import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import powercraft.core.PC_Block;
import powercraft.core.PC_CoordI;
import powercraft.core.PC_ICraftingToolDisplayer;
import powercraft.core.PC_ISpecialInventoryTextures;
import powercraft.core.PC_InvUtils;
import powercraft.core.PC_Utils;

public class PCma_BlockBlockBuilder extends PC_Block implements PC_ISpecialInventoryTextures, PC_ICraftingToolDisplayer {
	private static final int TXDOWN = 109, TXTOP = 156, TXSIDE = 140, TXFRONT = 108, TXBACK = 124;
	
	public PCma_BlockBlockBuilder(int id) {
		super(id, TXDOWN, Material.ground);
		setHardness(0.7F);
		setResistance(10.0F);
		setStepSound(Block.soundStoneFootstep);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public String getDefaultName() {
		return "Block Builder";
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public int getBlockTextureFromSideAndMetadata(int s, int m) {
		if (s == 1) {
			return TXTOP;
		}
		if (s == 0) {
			return TXDOWN;
		} else {
			if (m == s) {
				return TXFRONT;
			}
			if ((m == 2 && s == 3) || (m == 3 && s == 2) || (m == 4 && s == 5) || (m == 5 && s == 4)) {
				return TXBACK;
			}
			return TXSIDE;
		}
	}

	@Override
	public int getInvTexture(int i, int m) {
		if (i == 1) {
			return TXTOP;
		}
		if (i == 0) {
			return TXDOWN;
		}
		if (i == 3) {
			return TXFRONT;
		} else if (i == 4) {
			return TXBACK;
		} else {
			return TXSIDE;
		}
	}
	
	@Override
	public int tickRate() {
		return 1;
	}
	
	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		setDispenserDefaultDirection(world, i, j, k);
	}

	private void setDispenserDefaultDirection(World world, int i, int j, int k) {
		if (!world.isRemote) {
			int l = world.getBlockId(i, j, k - 1);
			int i1 = world.getBlockId(i, j, k + 1);
			int j1 = world.getBlockId(i - 1, j, k);
			int k1 = world.getBlockId(i + 1, j, k);
			byte byte0 = 3;
			if (Block.opaqueCubeLookup[l] && !Block.opaqueCubeLookup[i1]) {
				byte0 = 3;
			}
			if (Block.opaqueCubeLookup[i1] && !Block.opaqueCubeLookup[l]) {
				byte0 = 2;
			}
			if (Block.opaqueCubeLookup[j1] && !Block.opaqueCubeLookup[k1]) {
				byte0 = 5;
			}
			if (Block.opaqueCubeLookup[k1] && !Block.opaqueCubeLookup[j1]) {
				byte0 = 4;
			}
			world.setBlockMetadataWithNotify(i, j, k, byte0);
		}
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9) {
		ItemStack ihold = entityplayer.getCurrentEquippedItem();
		if (ihold != null) {
			if (ihold.getItem() instanceof ItemBlock && ihold.getItem().shiftedIndex != blockID) {

				Block bhold = Block.blocksList[ihold.getItem().shiftedIndex];
				return false;

			}
		}

		if (world.isRemote) {
			return true;
		}

		PC_Utils.openGres("BlockBuilder",  entityplayer, i, j, k);

		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		if (l > 0 && Block.blocksList[l].canProvidePower()) {
			boolean flag = isIndirectlyPowered(world, i, j, k);
			if (flag) {
				world.scheduleBlockUpdate(i, j, k, blockID, tickRate());
			}
		}
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {
		if (!world.isRemote && isIndirectlyPowered(world, i, j, k)) {
			PCma_TileEntityBlockBuilder tileentity = (PCma_TileEntityBlockBuilder) world.getBlockTileEntity(i, j, k);
			if (tileentity != null) {
				tileentity.useItem();
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new PCma_TileEntityBlockBuilder();
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving) {
		int l = MathHelper.floor_double(((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;

		if (entityliving instanceof EntityPlayer && PC_Utils.isPlacingReversed((EntityPlayer)entityliving)) {
			l = PC_Utils.reverseSide(l);
		}

		if (l == 0) {
			l = 2;
		} else if (l == 1) {
			l = 5;
		} else if (l == 2) {
			l = 3;
		} else if (l == 3) {
			l = 4;
		}

		world.setBlockMetadataWithNotify(i, j, k, l);
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, int par5, int par6) {
		PCma_TileEntityBlockBuilder teb = (PCma_TileEntityBlockBuilder) world.getBlockTileEntity(i, j, k);


		if (teb != null) {
			PC_InvUtils.dropInventoryContents(teb, world, teb.getCoord());
		}
		super.breakBlock(world, i, j, k, par5, par6);
	}

	private boolean isIndirectlyPowered(World world, int i, int j, int k) {
		if (world.isBlockGettingPowered(i, j, k)) {
			return true;
		}

		if (world.isBlockIndirectlyGettingPowered(i, j, k)) {
			return true;
		}

		if (world.isBlockGettingPowered(i, j - 1, k)) {
			return true;
		}

		if (world.isBlockIndirectlyGettingPowered(i, j - 1, k)) {
			return true;
		}
		return false;
	}

	@Override
	public String getCraftingToolModule() {
		return mod_PowerCraftMachines.getInstance().getNameWithoutPowerCraft();
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this));
		return arrayList;
	}
	
	@Override
   	public List<String> getBlockFlags(World world, PC_CoordI pos, List<String> list) {

   		list.add(PC_Utils.NO_HARVEST);
   		list.add(PC_Utils.NO_PICKUP);
   		list.add(PC_Utils.HARVEST_STOP);
   		return list;
   	}

   	@Override
   	public List<String> getItemFlags(ItemStack stack, List<String> list) {
   		list.add(PC_Utils.NO_BUILD);
   		return list;
   	}
	
}
