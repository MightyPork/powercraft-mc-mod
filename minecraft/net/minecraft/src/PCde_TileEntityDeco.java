package net.minecraft.src;


import java.util.List;
import java.util.Random;


/**
 * Decorative block tile entity - because of the renderer.
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
public class PCde_TileEntityDeco extends PC_TileEntity implements PC_IInventoryWrapper {

	/** block type. */
	public int type = 0;

	/** Flag for migrating from 3.4pre3 to newer. */
	private boolean flag34pre4 = true;

	private PCde_InventoryTransmutationContainer transmutabox = new PCde_InventoryTransmutationContainer(this);


	/** Charge level in the lightning conductor */
	public long lightningCharge = 0;
	/** Charge needed to make lightning */
	public long lightningChargeRequired = FLASH_CHARGE_MIN + 500 + rand.nextInt(FLASH_CHARGE_MAX - FLASH_CHARGE_MIN);

	private int flashStructureCheckTimeout = 1;
	private boolean flashStructureComplete = false;

	/**
	 * if this is the chamber, here may be instance of conductor.
	 */
	public PCde_TileEntityDeco conductor = null;
	/**
	 * if this is the conductor, here may be instance of chamber
	 */
	public PCde_TileEntityDeco chamber = null;


	@Override
	public void updateEntity() {
		if (type == 0 || type == 1) return;


		// conversion of old iron ledges to their own block (walkable)
		if (!flag34pre4 && type == 2) {

			// if block was made in 3.4pre3, then type=2 should now be BlockNonSolid, type 0

			Block block = mod_PCdeco.walkable;
			if (worldObj.setBlock(xCoord, yCoord, zCoord, block.blockID)) {
				// set tile entity
				PCde_TileEntityWalkable ted = (PCde_TileEntityWalkable) worldObj.getBlockTileEntity(xCoord, yCoord, zCoord);
				if (ted == null) {
					ted = (PCde_TileEntityWalkable) ((BlockContainer) block).getBlockEntity();
				}
				ted.type = 0;
				worldObj.setBlockTileEntity(xCoord, yCoord, zCoord, ted);
				block.onBlockPlaced(worldObj, xCoord, yCoord, zCoord, 0);

				worldObj.markBlocksDirty(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
				worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);

				PC_Logger.finest("Old Iron Ledge block updated to use now format at [" + xCoord + ";" + yCoord + ";" + zCoord + "]");
				return;
			}
		}

		// conductor
		if (type == 2) {

			if (flashStructureCheckTimeout-- <= 0) {
				flashStructureCheckTimeout = 40;
				flashStructureComplete = isTransmuterStructureComplete();
			}

			if (flashStructureComplete) {
				updateFlashCharge();

				//the particle field

				for (int i = 0; i < 2; i++) {
					ModLoader.getMinecraftInstance().effectRenderer.addEffect(new PC_EntityLaserParticleFX(worldObj, new PC_CoordD(getCoord())
							.offset(-0.1F + rand.nextFloat() * 1.2F, rand.nextFloat() * 0.8F, -0.1F + rand.nextFloat() * 1.2F), new PC_Color(0.6,
							0.6, 1), new PC_CoordI(), 0));
				}

				for (int i = 0; i < 2; i++) {
					ModLoader.getMinecraftInstance().effectRenderer.addEffect(new PC_EntityLaserParticleFX(worldObj, new PC_CoordD(getCoord())
							.offset(0.1F + rand.nextFloat() * 0.8F, 0.8F + rand.nextFloat() * 0.8F, 0.1F + rand.nextFloat() * 0.8F), new PC_Color(
							0.6, 0.6, 1), new PC_CoordI(), 0));
				}

				for (int i = 0; i < 2; i++) {
					ModLoader.getMinecraftInstance().effectRenderer.addEffect(new PC_EntityLaserParticleFX(worldObj, new PC_CoordD(getCoord())
							.offset(0.2F + rand.nextFloat() * 0.6F, 1.6F + rand.nextFloat() * 0.9F, 0.2F + rand.nextFloat() * 0.6F), new PC_Color(
							0.6, 0.6, 1), new PC_CoordI(), 0));
				}

				if (isLightningReadyToStrike()) {
					makeLightning(2);
					lightningCharge = 0;
					lightningChargeRequired = FLASH_CHARGE_MIN + 100 + rand.nextInt(FLASH_CHARGE_MAX - FLASH_CHARGE_MIN - 100);
				}

			} else {
				if (lightningCharge > FLASH_CHARGE_MIN) lightningCharge--;
			}

		}

		//chimney
		if (type >= 4 && type <= 6) {
			if (rand.nextInt(6) == 0) {
				tryToSmoke();
			}
		}

		flag34pre4 = true;
	}

	/**
	 * forge method - receives update ticks
	 * 
	 * @return false
	 */
	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		type = tag.getInteger("type");
		flag34pre4 = tag.getBoolean("flag34pre4");

		lightningCharge = tag.getLong("LightningCharge");
		lightningChargeRequired = tag.getLong("LightningChargeRequired");

		if (type == 3) {
			PC_InvUtils.loadInventoryFromNBT(tag, "TransmutaBox", transmutabox);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("type", type);
		tag.setBoolean("flag34pre4", flag34pre4);

		tag.setLong("LightningCharge", lightningCharge);
		tag.setLong("LightningChargeRequired", lightningChargeRequired);

		if (type == 3) {
			PC_InvUtils.saveInventoryToNBT(tag, "TransmutaBox", transmutabox);
		}
	}

	@Override
	public IInventory getInventory() {
		if (type == 3) return transmutabox;
		return null;
	}


	/** lowest allowed charge level for flash */
	public static final int FLASH_CHARGE_MIN = 8000;
	/** highest allowed charge level needed for flash */
	public static final int FLASH_CHARGE_MAX = 17000;
	private static final int FLASH_MIN_HEIGHT = 79;


	private void updateFlashCharge() {

		int increment = rand.nextInt(4);

		if (worldObj.isThundering()) {
			increment = 2 + rand.nextInt(15);
		} else if (worldObj.isRaining()) {
			increment = 1 + rand.nextInt(10);
		} else if (worldObj.isBlockHighHumidity(xCoord, yCoord, zCoord)) {
			increment = rand.nextInt(2);
		}

		lightningCharge += increment;

	}

	private boolean isTransmuterStructureComplete() {

		boolean ok = true;

		int steel = Block.blockSteel.blockID;

		//check integrity of the underlying iron pillar
		for (int i = -1; i >= -6; i--) {
			if (getCoord().offset(0, i, 0).getId(worldObj) != steel) ok = false;
		}

		ok &= worldObj.canBlockSeeTheSky(xCoord, yCoord, zCoord);
		ok &= yCoord >= FLASH_MIN_HEIGHT;

		reconnectWithChamber();

		return ok;

	}

	private boolean isLightningReadyToStrike() {
		return lightningCharge >= lightningChargeRequired;
	}

	private void makeLightning(int count) {
		EntityLightningBolt bolt = null;
		for (int i = 0; i < count; i++) {
			bolt = new EntityLightningBolt(worldObj, xCoord, yCoord + 1D, zCoord);
			worldObj.addWeatherEffect(bolt);
		}

		if (chamber != null) {

			worldObj.createExplosion(null, chamber.getCoord().x + 0.5F, chamber.getCoord().y + 0.5F, chamber.getCoord().z + 0.5F, 1F);

			worldObj.playSoundEffect(chamber.getCoord().x + 0.5F, chamber.getCoord().y + 0.5F, chamber.getCoord().z + 0.5F, "random.explode", 1.5F,
					(1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

			chamber.transmutabox.onHitByLightning();

			// hit entites near the chamber by lightning.
			double d = 3D;
			List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(
					bolt,
					AxisAlignedBB.getBoundingBoxFromPool(chamber.getCoord().x - d, chamber.getCoord().y - d, chamber.getCoord().z - d,
							chamber.getCoord().x + d, chamber.getCoord().y + 6D + d, chamber.getCoord().z + d));

			for (int l = 0; l < list.size(); l++) {
				Entity entity = list.get(l);
				if (!(entity instanceof EntityItem)) entity.dealFireDamage(5);
			}
		}

	}



	private boolean reconnectWithChamber() {

		int steel = Block.blockSteel.blockID;

		if (chamber != null) chamber.conductor = null;
		chamber = null;

		//check integrity of the underlying iron pillar
		for (int j = -1; j >= -255; j--) {
			PC_CoordI pos = getCoord().offset(0, j, 0);
			int id = pos.getId(worldObj);

			if (id != steel) {

				if (id == mod_PCdeco.deco.blockID) {

					PCde_TileEntityDeco ted = (PCde_TileEntityDeco) pos.getTileEntity(worldObj);
					if (ted != null) {
						if (ted.type == 3) {
							ted.conductor = this;
							chamber = ted;
							return true;
						}
					}

				}
				return false;
			}
		}

		return false;
	}



	// chimney

	private void doSmoke() {
		for (int l = 0; l < 12; l++) {
			float smI = xCoord + rand.nextFloat() * 0.4F + 0.2F;
			float smJ = yCoord + 0.4F + rand.nextFloat() * 0.6F;
			float smK = zCoord + rand.nextFloat() * 0.4F + 0.2F;

			ModLoader.getMinecraftInstance().effectRenderer.addEffect(new EntitySmokeFX(worldObj, smI, smJ, smK, 0, 0, 0, 2.0F));
		}
	}

	private boolean doesBlockSmoke(PC_CoordI pos) {
		int id = pos.getId(worldObj);
		if (id == Block.stoneOvenActive.blockID) return true;
		if (id == Block.fire.blockID) return true;

		if (PC_BlockUtils.hasFlag(worldObj, pos, "SMOKE")) return true;
		return false;
	}

	private boolean doesBlockSmokeOpenly(PC_CoordI pos) {
		int id = pos.getId(worldObj);
		if (id == Block.fire.blockID) return true;

		if (PC_BlockUtils.hasFlag(worldObj, pos, "SMOKE")) return true;
		return false;
	}

	private boolean isBlockLitFurnace(PC_CoordI pos) {
		return pos.getId(worldObj) == Block.stoneOvenActive.blockID;
	}

	private boolean isBlockChimney(PC_CoordI pos) {
		if (pos.getId(worldObj) == mod_PCdeco.deco.blockID) {
			PCde_TileEntityDeco ted = (PCde_TileEntityDeco) pos.getTileEntity(worldObj);
			if (ted.type >= 4 && ted.type <= 6) return true;
		}
		return false;
	}

	private void tryToSmoke() {
		if (worldObj.isAirBlock(xCoord, yCoord + 1, zCoord)) { //test if air is above chimney	    

			PC_CoordI cursor = getCoord().copy();

			boolean smoke = false;

			cursor.y++;
			while (cursor.y > 0) {
				cursor.y--;

				if (isBlockChimney(cursor)) {
					smoke |= isBlockLitFurnace(cursor.offset(-1, 0, 0));
					smoke |= isBlockLitFurnace(cursor.offset(1, 0, 0));
					smoke |= isBlockLitFurnace(cursor.offset(0, 0, -1));
					smoke |= isBlockLitFurnace(cursor.offset(0, 0, 1));
					if (smoke) break;
					continue;
				} else {
					// no more chimney. check what is underneath.

					// smoke source directly here
					if (doesBlockSmoke(cursor)) {
						smoke = true;
						break;
					}

					//a block under
					if (doesBlockSmoke(cursor.offset(0, -1, 0))) {
						smoke = true;
						break;
					}

					// smoke sources by side
					smoke |= doesBlockSmokeOpenly(cursor.offset(-1, 0, 0));
					smoke |= doesBlockSmokeOpenly(cursor.offset(1, 0, 0));
					smoke |= doesBlockSmokeOpenly(cursor.offset(0, 0, -1));
					smoke |= doesBlockSmokeOpenly(cursor.offset(0, 0, 1));
					if (smoke) break;

					// smoke sources by corner
					smoke |= doesBlockSmokeOpenly(cursor.offset(1, 0, -1));
					smoke |= doesBlockSmokeOpenly(cursor.offset(-1, 0, 1));
					smoke |= doesBlockSmokeOpenly(cursor.offset(-1, 0, -1));
					smoke |= doesBlockSmokeOpenly(cursor.offset(1, 0, 1));
					if (smoke) break;

					// under by side
					smoke |= doesBlockSmokeOpenly(cursor.offset(-1, -1, 0));
					smoke |= doesBlockSmokeOpenly(cursor.offset(1, -1, 0));
					smoke |= doesBlockSmokeOpenly(cursor.offset(0, -1, -1));
					smoke |= doesBlockSmokeOpenly(cursor.offset(0, -1, 1));
					if (smoke) break;

					//under by corner
					smoke |= doesBlockSmokeOpenly(cursor.offset(1, -1, -1));
					smoke |= doesBlockSmokeOpenly(cursor.offset(-1, -1, 1));
					smoke |= doesBlockSmokeOpenly(cursor.offset(-1, -1, -1));
					smoke |= doesBlockSmokeOpenly(cursor.offset(1, -1, 1));
					if (smoke) break;
				}
			}

			if (smoke) {
				doSmoke();
			}

		}
	}

	private static Random rand = new Random();
}
