package powercraft.light;

import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import powercraft.core.PC_BeamTracer;
import powercraft.core.PC_BeamTracer.result;
import powercraft.core.PC_Block;
import powercraft.core.PC_Color;
import powercraft.core.PC_CoordI;
import powercraft.core.PC_IBeamSpecialHandling;
import powercraft.core.PC_IBlockRenderer;
import powercraft.core.PC_ICraftingToolDisplayer;
import powercraft.core.PC_Renderer;
import powercraft.core.PC_Utils;

public class PCli_BlockPrism extends PC_Block implements
		PC_IBeamSpecialHandling, PC_IBlockRenderer, PC_ICraftingToolDisplayer {

	public PCli_BlockPrism(int id) {
		super(id, Material.glass);
		float f = 0.4F;
		float f1 = 1.0F;
		setBlockBounds(0.5F - f, 0.1F, 0.5F - f, 0.5F + f, f1 - 0.1F, 0.5F + f);
		setHardness(1.0F);
		setResistance(4.0F);
		setStepSound(Block.soundStoneFootstep);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public String getDefaultName() {
		return "Prism";
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new PCli_TileEntityPrism();
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int i) {
		return true;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int par6, float par7, float par8, float par9) {
		ItemStack ihold = player.getCurrentEquippedItem();
		if (ihold != null) {
			if (ihold.getItem() instanceof ItemBlock && ihold.itemID != blockID) {
				Block bhold = Block.blocksList[ihold.getItem().shiftedIndex];
				if(bhold.blockID != Block.thinGlass.blockID)
					return false;
			}
		}
		
		if(world.isRemote)
			return true;
		
		int angle = MathHelper.floor_double((((player.rotationYaw + 180F) * 16F) / 360F) + 0.5D) & 0xf;
		angle &= 0xE;
		angle = angle >> 1;
		angle += 2;
		if (angle > 7) {
			angle = angle - 8;
		}

		angle += 2;

		// if close enough
		if (MathHelper.abs((float) player.posX - (i + 0.5F)) < 1.3F && MathHelper.abs((float) player.posZ - (k + 0.5F)) < 1.3F) {
			double d = (player.posY + 1.8200000000000001D) - player.yOffset;

			if (d - j > 2D) {
				angle = 1;
			}

			if (j - d > 0.0D) {
				angle = 0;
			}
		}

		boolean drop = true;
		if (ihold != null) {
			if (ihold.getItem().shiftedIndex == Block.thinGlass.blockID) {

				if (isGlassPanelOnSide(world, i, j, k, angle) == false) {

					PCli_TileEntityPrism teo = PC_Utils.getTE(world, i, j, k, blockID);
					if (teo != null) {
						teo.setPrismSide(angle, true);
					}
					if (!PC_Utils.isCreative(player)) {
						ihold.stackSize--;
					}
					drop = false;

				}

			}
		}
		if (drop) {

			if (isGlassPanelOnSide(world, i, j, k, angle)) {

				PCli_TileEntityPrism teo = PC_Utils.getTE(world, i, j, k, blockID);
				if (teo != null) {
					teo.setPrismSide(angle, false);
				}
				if (!PC_Utils.isCreative(player)) {
					PC_Utils.dropItemStack(world, new ItemStack(Block.thinGlass, 1), new PC_CoordI(i, j, k));
				}

			}

		}

		return true;
	}
	
	/**
	 * Check if prism's side is active (with glass pane)
	 * 
	 * @param iblockaccess
	 * @param x
	 * @param y
	 * @param z
	 * @param side
	 * @return has glass panel
	 */
	public static boolean isGlassPanelOnSide(IBlockAccess iblockaccess, int x, int y, int z, int side) {

		PCli_TileEntityPrism teo = PC_Utils.getTE(iblockaccess, x, y, z);

		if (teo == null) {
			return false;
		}

		return teo.getPrismSide(side);
	}
	
	@Override
	public int getRenderColor(int i) {
		return 0xffffcc;
	}

	@Override
	public int colorMultiplier(IBlockAccess iblockaccess, int i, int j, int k) {
		return 0xffffcc;
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, Object renderer) {
		Block ice = Block.ice;
		float px = 0.0625F;
		PC_Renderer.bindTexture("/terrain.png");
		ice.setBlockBounds(3 * px, 3 * px, 3 * px, 12 * px, 12 * px, 12 * px);
		PC_Renderer.renderInvBox(renderer, ice, 0);
		ice.setBlockBounds(4 * px, 4 * px, 2 * px, 11 * px, 11 * px, 13 * px);
		PC_Renderer.renderInvBox(renderer, ice, 0);
		ice.setBlockBounds(2 * px, 4 * px, 4 * px, 13 * px, 11 * px, 11 * px);
		PC_Renderer.renderInvBox(renderer, ice, 0);
		ice.setBlockBounds(4 * px, 2 * px, 4 * px, 11 * px, 13 * px, 11 * px);
		PC_Renderer.renderInvBox(renderer, ice, 0);
		ice.setBlockBounds(0, 0, 0, 1, 1, 1);
	}

	@Override
	public void renderWorldBlock(IBlockAccess world, int x, int y, int z,Block block, int modelId, Object renderer) {
		
	}

	/** prism redirection vector for side */
	private static final PC_CoordI[] prismMove = { new PC_CoordI(0, -1, 0), new PC_CoordI(0, 1, 0), new PC_CoordI(1, 0, 0), new PC_CoordI(1, 0, 1),
			new PC_CoordI(0, 0, 1), new PC_CoordI(-1, 0, 1), new PC_CoordI(-1, 0, 0), new PC_CoordI(-1, 0, -1), new PC_CoordI(0, 0, -1),
			new PC_CoordI(1, 0, -1) };
	
	/**
	 * Get index of prism side which faces the beam
	 * 
	 * @param move beam movement vector
	 * @return side number
	 */
	private int getPrismSideFacingMove(PC_CoordI move) {
		for (int i = 0; i < 10; i++) {
			if (prismMove[i].equals(move.getInverted())) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * get movement vector from prism's side
	 * 
	 * @param side the side number
	 * @return vector (coord)
	 */
	private PC_CoordI getPrismOutputMove(int side) {
		return prismMove[side];
	}
	
	@Override
	public result onHitByBeamTracer(PC_BeamTracer beamTracer, PC_CoordI cnt, PC_CoordI move, PC_Color color, float strength, int distanceToMove) {

		PCli_TileEntityPrism prism = PC_Utils.getTE(beamTracer.getWorld(), cnt.x, cnt.y, cnt.z);

		int sideCount = 0;
		int[] side = new int[10];

		int thisPrismSide = getPrismSideFacingMove(move);

		for (int h = 0; h < 10; h++) {
			// include only non-this & not false sides.
			if (prism.getPrismSide(h) && h != thisPrismSide) {
				side[sideCount] = h;
				sideCount++;
			}
		}

		if (sideCount >= 1) {

			for (int h = 0; h < sideCount; h++) {
				PC_CoordI newMove = getPrismOutputMove(side[h]).copy();

				beamTracer.forkBeam(cnt, newMove, color, strength, distanceToMove / Math.round((sideCount * 0.75F)));
			}

		}

		if (sideCount > 0) {
			return result.STOP;
		}

		return result.CONTINUE;
	}

	@Override
	public String getCraftingToolModule() {
		return mod_PowerCraftLight.getInstance().getNameWithoutPowerCraft();
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
		list.add(PC_Utils.PASSIVE);
		return list;
	}

	@Override
	public List<String> getItemFlags(ItemStack stack, List<String> list) {
		list.add(PC_Utils.NO_BUILD);
		return list;
	}
	
}
