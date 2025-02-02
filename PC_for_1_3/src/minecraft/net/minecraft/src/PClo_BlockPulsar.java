package net.minecraft.src;


import java.util.HashSet;
import java.util.Random;
import java.util.Set;


/**
 * redstone Pulsar block
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
public class PClo_BlockPulsar extends BlockContainer implements PC_IBlockType {

	/**
	 * pulsar block
	 * 
	 * @param i ID
	 */
	protected PClo_BlockPulsar(int i) {
		super(i, 74, Material.wood);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new PClo_TileEntityPulsar();
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
	}

	@Override
	public boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return isActive(iblockaccess, i, j, k);
	}

	@Override
	public boolean isIndirectlyPoweringTo(World world, int i, int j, int k, int l) {
		return isActive(world, i, j, k);
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {
		super.updateTick(world, i, j, k, random);
		world.markBlockAsNeedsUpdate(i, j, k);
		world.notifyBlockChange(i, j, k, blockID);
		world.notifyBlockChange(i + 1, j, k, blockID);
		world.notifyBlockChange(i - 1, j, k, blockID);
		world.notifyBlockChange(i, j, k + 1, blockID);
		world.notifyBlockChange(i, j, k - 1, blockID);
		world.notifyBlockChange(i, j + 1, k + 1, blockID);
		world.notifyBlockChange(i, j - 1, k - 1, blockID);
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int par6, float par7, float par8, float par9) {
		ItemStack ihold = player.getCurrentEquippedItem();
		if (ihold != null) {
			if (ihold.getItem() instanceof ItemBlock && ihold.getItem().shiftedIndex != blockID) {

				Block bhold = Block.blocksList[ihold.getItem().shiftedIndex];
				if (bhold instanceof PC_IBlockType) {

					return false;

				}
			}

			if (ihold.getItem().shiftedIndex == Item.stick.shiftedIndex) {
				changeDelay(world, player, i, j, k, player.isSneaking() ? -1 : 1);
				return true;
			}
		}
		PC_Utils.openGres(player, PClo_GuiPulsar.class, i, j, k); 

		return true;
	}

	@Override
	public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer) {
		printDelay(world, i, j, k);
	}

	/**
	 * Add some increment to the delay length.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param delay to add, like +1 or -1.
	 */
	public static void changeDelay(World world, EntityPlayer player, int x, int y, int z, int delay) {
		PClo_TileEntityPulsar ent = (PClo_TileEntityPulsar) world.getBlockTileEntity(x, y, z);
		PC_Utils.setTileEntity(player, ent, "changeDelay", delay);
		if(!world.isRemote)
			ent.printDelay();
	}

	/**
	 * Show current delay on player's screen (chat msg);
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public static void printDelay(World world, int x, int y, int z) {
		PClo_TileEntityPulsar ent = (PClo_TileEntityPulsar) world.getBlockTileEntity(x, y, z);
		if(!world.isRemote)
			ent.printDelayTime();
	}

	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
		if (isActive(world, i, j, k)&&world.isRemote) {
			world.spawnParticle("reddust", i + 0.5D, j + 1.0D, k + 0.5D, 0D, 0D, 0D);
		}
	}

	/**
	 * Is the pulsar in "on" state?
	 * 
	 * @param iblockaccess
	 * @param x
	 * @param y
	 * @param z
	 * @return is active
	 */
	public boolean isActive(IBlockAccess iblockaccess, int x, int y, int z) {
		TileEntity te = iblockaccess.getBlockTileEntity(x, y, z);
		if (te == null) {
			return false;
		}
		PClo_TileEntityPulsar tep = (PClo_TileEntityPulsar) te;

		return tep.active;
	}

	@Override
	public int getRenderColor(int i) {
		return 0xff3333;
	}

	@Override
	public int colorMultiplier(IBlockAccess iblockaccess, int i, int j, int k) {
		if (isActive(iblockaccess, i, j, k)) {
			return 0xff3333;
		} else {
			return 0x771111;
		}
	}

	@Override
	public Set<String> getBlockFlags(World world, PC_CoordI pos) {

		Set<String> set = new HashSet<String>();

		set.add("NO_HARVEST");
		set.add("HARVEST_STOP");
		set.add("REDSTONE");
		set.add("LOGIC");
		set.add("PULSAR");

		return set;
	}

	@Override
	public Set<String> getItemFlags(ItemStack stack) {
		Set<String> set = new HashSet<String>();
		set.add("NO_BUILD");
		set.add("PULSAR");
		return set;
	}

}