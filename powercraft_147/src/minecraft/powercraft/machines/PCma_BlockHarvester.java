package powercraft.machines;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.machines.PCma_CropHarvestingManager.PC_CropEntry;
import powercraft.api.PC_BeamTracer;
import powercraft.api.PC_Color;
import powercraft.api.PC_IBeamHandler;
import powercraft.api.PC_IPacketHandler;
import powercraft.api.PC_PacketHandler;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Utils.GameInfo;
import powercraft.api.PC_Utils.Inventory;
import powercraft.api.PC_Utils.ValueWriting;
import powercraft.api.PC_VecI;
import powercraft.api.block.PC_Block;
import powercraft.api.inventory.PC_ISpecialInventoryTextures;
import powercraft.api.item.PC_IItemInfo;
import powercraft.api.registry.PC_KeyRegistry;
import powercraft.api.registry.PC_MSGRegistry;
import powercraft.api.registry.PC_SoundRegistry;

public class PCma_BlockHarvester extends PC_Block implements
		PC_ISpecialInventoryTextures, PC_IBeamHandler, PC_IItemInfo, PC_IPacketHandler {

	private static final int TXDOWN = 109, TXTOP = 155, TXSIDE = 139, TXFRONT = 107, TXBACK = 123;
	/**
	 * Block which ends the harvesting. 98 = stone brick. Obsidian + bedrock
	 * stop too.
	 */
	public static final int ENDBLOCK = 98;
	
	/** Stacks harvested during this flash (including stacks from animals etc.) */
	private ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
	
	public PCma_BlockHarvester(int id) {
		super(id, TXSIDE, Material.ground);
		setHardness(0.7F);
		setResistance(10.0F);
		setStepSound(Block.soundStoneFootstep);
		setCreativeTab(CreativeTabs.tabDecorations);
		PC_PacketHandler.registerPackethandler("PCma_BlockHarvester", this);
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
	public boolean isBlockSolid(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return true;
	}

	@Override
	public int tickRate() {
		return 1;
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving) {
		int l = MathHelper.floor_double(((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;

		if (entityliving instanceof EntityPlayer && PC_KeyRegistry.isPlacingReversed((EntityPlayer)entityliving)) {
			l = ValueWriting.reverseSide(l);
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

		if (isIndirectlyPowered(world, i, j, k)) {
			world.scheduleBlockUpdate(i, j, k, blockID, tickRate());
		}

		world.setBlockMetadataWithNotify(i, j, k, l);
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		int l = world.getBlockMetadata(i, j, k);
		if (isIndirectlyPowered(world, i, j, k)) {
			harvestBlocks(world, i, j, k, l);

			l |= 8;
		}
		world.setBlockMetadataWithNotify(i, j, k, l);
	}

	private boolean isIndirectlyPowered(World world, int i, int j, int k) {
		// if (world.isBlockGettingPowered(i, j, k)) { return true; }

		if (world.isBlockIndirectlyGettingPowered(i, j, k)) {
			return true;
		}

		// if (world.isBlockGettingPowered(i, j - 1, k)) { return true; }

		if (world.isBlockIndirectlyGettingPowered(i, j - 1, k)) {
			return true;
		}
		return false;
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
		if (isIndirectlyPowered(world, i, j, k)) {
			harvestBlocks(world, i, j, k, world.getBlockMetadata(i, j, k));
		}
	}


	/**
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param deviceMeta
	 */
	private void harvestBlocks(World world, int x, int y, int z, int deviceMeta) {

		if(!world.isRemote)
			PC_PacketHandler.sendToPacketHandler(true, world, "PCma_BlockHarvester", x, y, z, deviceMeta);
		
		deviceMeta &= 0x7;

		int incZ = Facing.offsetsZForSide[deviceMeta];
		int incX = Facing.offsetsXForSide[deviceMeta];

		PC_VecI move = new PC_VecI(incX, 0, incZ);

		PC_VecI cnt = new PC_VecI(x, y, z);
		PC_BeamTracer beamTracer = new PC_BeamTracer(world, this);

		beamTracer.setStartCoord(cnt);
		beamTracer.setStartMove(move);
		beamTracer.setCanChangeColor(false);
		beamTracer.setDetectEntities(true);
		beamTracer.setTotalLengthLimit(8000);
		beamTracer.setMaxLengthAfterCrystal(2000);
		beamTracer.setStartLength(30);
		beamTracer.setData("crystalAdd", 100);
		beamTracer.setColor(new PC_Color(0.001f, 1.0f, 0.001f));

		if (world.getBlockId(x, y - 1, z) == ENDBLOCK) {
			beamTracer.setStartLength(1);
			beamTracer.setMaxLengthAfterCrystal(1);
		}

		if(!world.isRemote)
			drops.clear();
		
		beamTracer.flash();

		if (drops != null && !world.isRemote) {
			Inventory.groupStacks(drops);


			for (ItemStack stack : drops) {
				dispenseItem(world, cnt, stack);
			}
		}

	}
	
	
	@Override
	public boolean onBlockHit(PC_BeamTracer beamTracer, Block block, PC_VecI coord) {
		World world = beamTracer.getWorld();
		int id = GameInfo.getBID(world, coord);
		int meta = GameInfo.getMD(world, coord);

		if (id == 49 || id == 7 || id == ENDBLOCK) {
			return true;
		}


		// sapling on grass
		if (PCma_TreeHarvestingManager.isBlockTreeSapling(id, meta)) {
			int underId = GameInfo.getBID(world, coord.offset(0, -1, 0));
			if (underId == Block.dirt.blockID || underId == Block.grass.blockID || underId == Block.mycelium.blockID) {
				return false;
			}
		}
		
		if(PC_MSGRegistry.hasFlag(world, coord, PC_Utils.HARVEST_STOP)){
			return true;
		}

		// SKIP non-breaking
		if (id == 0 || id == Block.glass.blockID || id == Block.thinGlass.blockID || id == Block.redstoneLampActive.blockID
				|| id == Block.redstoneLampIdle.blockID || Block.blocksList[id] == null || id == 8 || id == 9 || id == 10 || id == 11
				|| id == Block.sapling.blockID || id == Block.pumpkinStem.blockID || id == Block.melonStem.blockID || id == Block.cake.blockID
				|| id == Block.fire.blockID || Block.blocksList[id] instanceof BlockTorch || id == Block.redstoneWire.blockID || id == Block.mobSpawner.blockID
				|| id == Block.lever.blockID || id == Block.woodenButton.blockID || id == Block.stoneButton.blockID || Block.blocksList[id] instanceof BlockRedstoneRepeater
				|| id == Block.pistonStickyBase.blockID || id == Block.pistonBase.blockID || id == Block.pistonExtension.blockID
				|| id == Block.pistonMoving.blockID || Block.blocksList[id] instanceof BlockRail || PC_MSGRegistry.hasFlag(world, coord, PC_Utils.NO_HARVEST)) {

			return false;
		}



		// tree - replace with sapling
		if (PCma_TreeHarvestingManager.isBlockTreeWood(id, meta)) {
			if(world.isRemote)
				return true;
			ItemStack[] output = PCma_TreeHarvestingManager.harvestTreeAt(world, coord);

			if (output != null) {
				if(!world.isRemote)
					for (ItemStack stack : output) {
						addToDispenseList(world, stack);
					}
			}

			return true;
			// return false;
		}

		// block registered using XML
		if (PCma_CropHarvestingManager.isBlockRegisteredCrop(id)) {

			PC_CropEntry cropEntry;
			
			if ((cropEntry = PCma_CropHarvestingManager.getHarvestBlockCrop(id, meta))!=null) {

				if(world.isRemote)
					return true;
				
				ItemStack[] harvested = cropEntry.getHarvestedStacks(id, meta);

				if (harvested != null) {

					for (ItemStack stack : harvested) {

						// play breaking sound and animation
						if (PC_SoundRegistry.isSoundEnabled()) {
							world.playAuxSFX(2001, coord.x, coord.y, coord.z, id + (meta << 12));
						}

						addToDispenseList(world, stack);
					}

				}

				int newMeta = cropEntry.getReplantMetadata(id, meta);

				if (newMeta == -1) {
					world.setBlockWithNotify(coord.x, coord.y, coord.z, 0);
				} else {
					world.setBlockMetadataWithNotify(coord.x, coord.y, coord.z, newMeta);
				}

				return true;

			}

			return false;

		}

		// ignore inventory blocks
		if (world.getBlockTileEntity(coord.x, coord.y, coord.z) != null && world.getBlockTileEntity(coord.x, coord.y, coord.z) instanceof IInventory) {
			return false;
		}

		if(world.isRemote)
			return true;
		
		// now regular block breaking
		int dropId;
		int dropMeta;
		int dropQuant;

		dropId = Block.blocksList[id].idDropped(id, world.rand, meta);
		dropMeta = Block.blocksList[id].damageDropped(meta);
		dropQuant = Block.blocksList[id].quantityDropped(world.rand);

		// play breaking sound and animation
		if (PC_SoundRegistry.isSoundEnabled()) {
			world.playAuxSFX(2001, coord.x, coord.y, coord.z, id + (meta << 12));
		}

		// set air, or water in case of ice
		world.setBlockWithNotify(coord.x, coord.y, coord.z, id == Block.ice.blockID ? Block.waterMoving.blockID : 0);

		if (id == Block.tallGrass.blockID) {
			dropId = Item.seeds.itemID;
			if (world.rand.nextInt(5) != 0) {
				return true;
			} // dddd
		}

		if (dropId <= 0) {
			dropId = id;
		}

		if (dropQuant <= 0) {
			dropQuant = 1;
		}

		addToDispenseList(world, new ItemStack(dropId, dropQuant, dropMeta));

		return true;
		// return false;
	}


	@Override
	public boolean onEntityHit(PC_BeamTracer beamTracer, Entity entity, PC_VecI coord) {

		World world = beamTracer.getWorld();
		
		if (entity == null) return false;

		if (entity instanceof EntityMinecart) {

			if(world.isRemote)
				return true;
			
			EntityMinecart cart = (EntityMinecart) entity;

			if (cart.isDead) {
				return false;
			}

			int l = GameInfo.getMD(world, coord.x, coord.y, coord.z) & 7;

			int iPLUS1 = -Facing.offsetsXForSide[l];
			int kPLUS1 = -Facing.offsetsZForSide[l];

			PC_VecI startCoord = beamTracer.getStartCoord();
			
			cart.posX = startCoord.x + iPLUS1 * 1.5D;
			cart.posY = startCoord.y;
			cart.posZ = startCoord.z + kPLUS1 * 1.5D;
			for(int i=0; i<cart.getSizeInventory(); i++){
				addToDispenseList(world, cart.getStackInSlot(i));
				cart.setInventorySlotContents(i, null);
			}
			addToDispenseList(world, new ItemStack(Item.minecartEmpty));
			if(cart.minecartType==1){
				addToDispenseList(world, new ItemStack(Block.chest));
			}else if(cart.minecartType==2){
				addToDispenseList(world, new ItemStack(Block.stoneOvenIdle));
			}
			cart.setDead();
			
		} else if (entity instanceof EntitySheep) {

			if(world.isRemote)
				return true;
			
			EntitySheep sheep = (EntitySheep) entity;

			if (sheep.isDead) {
				return false;
			}

			if (!sheep.getSheared()) {
				sheep.setSheared(true);
				addToDispenseList(world, new ItemStack(Block.cloth.blockID, 1 + world.rand.nextInt(3), sheep.getFleeceColor()));
			}

		} else if (entity instanceof EntityMooshroom) {

			if(world.isRemote)
				return true;
			
			EntityMooshroom mooshroom = (EntityMooshroom) entity;

			if (mooshroom.isDead) {
				return false;
			}

			if (mooshroom.getGrowingAge() >= 0) {

				if(!world.isRemote){
					EntityCow entitycow = new EntityCow(world);
					entitycow.setLocationAndAngles(mooshroom.posX, mooshroom.posY, mooshroom.posZ, mooshroom.rotationYaw, mooshroom.rotationPitch);
					entitycow.setEntityHealth(mooshroom.getHealth());
					entitycow.renderYawOffset = mooshroom.renderYawOffset;
					world.spawnEntityInWorld(entitycow);
					addToDispenseList(world, new ItemStack(Block.mushroomRed.blockID, 1 + world.rand.nextInt(5), 0));
				}
				mooshroom.setDead();
				mooshroom.deathTime = 0;
				
				if(world.isRemote){
					world.spawnParticle("largeexplode", mooshroom.posX, mooshroom.posY + (mooshroom.height / 2.0F), mooshroom.posZ, 0.0D, 0.0D, 0.0D);
				}

			}

		} else {
			return false;
		}

		return true;
	}

	private void addToDispenseList(World world, ItemStack stack) {
		if(!world.isRemote && stack!=null && stack.getItem()!=null && stack.stackSize!=0)
			drops.add(stack);
	}


	private void dispenseItem(World world, PC_VecI devPos, ItemStack itemstack) {

		if (itemstack == null || itemstack.stackSize <= 0) {
			return;
		}

		int l = world.getBlockMetadata(devPos.x, devPos.y, devPos.z) & 7;

		int dispIncX = -Facing.offsetsXForSide[l];
		int dispIncZ = -Facing.offsetsZForSide[l];

		double dx = devPos.x + dispIncX * 1.0D + 0.5D;
		double dy = devPos.y + 0.5D;
		double dz = devPos.z + dispIncZ * 1.0D + 0.5D;

		EntityItem entityitem = new EntityItem(world, dx, dy - 0.29999999999999999D, dz, itemstack);
		double throwSpeed = world.rand.nextDouble() * 0.10000000000000001D + 0.20000000000000001D;
		
		Block b = GameInfo.getBlock(world, devPos.offset(dispIncX, 0, dispIncZ));
		String module = null;
		if(b instanceof PC_Block){
			module = ((PC_Block) b).getModule().getModuleName();
		}
		
		if (module!=null && module.equalsIgnoreCase("Transport")) {
			entityitem.motionX = 0;
			entityitem.motionY = 0;
			entityitem.motionZ = 0;
		} else {
			entityitem.motionX = dispIncX * throwSpeed;
			entityitem.motionY = 0.20000000298023224D;
			entityitem.motionZ = dispIncZ * throwSpeed;
		}

		entityitem.delayBeforeCanPickup = 5;
		world.spawnEntityInWorld(entityitem);
		if (PC_SoundRegistry.isSoundEnabled()) {
			world.playAuxSFX(1000, devPos.x, devPos.y, devPos.z, 0);
		}

		world.playAuxSFX(2000, devPos.x, devPos.y, devPos.z, dispIncX + 1 + (dispIncZ + 1) * 3);

	}

	@Override
	public int quantityDropped(Random random) {
		return 1;
	}

	@Override
	public int getMobilityFlag() {
		return 0;
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this));
		return arrayList;
	}
	
	@Override
	public Object msg(IBlockAccess world, PC_VecI pos, int msg, Object... obj) {
		switch (msg){
		case PC_MSGRegistry.MSG_DEFAULT_NAME:
			return "Harvester";
		case PC_MSGRegistry.MSG_BLOCK_FLAGS:{
			List<String> list = (List<String>)obj[0];
	   		list.add(PC_Utils.NO_HARVEST);
	   		list.add(PC_Utils.NO_PICKUP);
	   		list.add(PC_Utils.HARVEST_STOP);
	   		return list;
		}case PC_MSGRegistry.MSG_ITEM_FLAGS:{
			List<String> list = (List<String>)obj[1];
			list.add(PC_Utils.NO_BUILD);
			return list;
		}
		}
		return null;
	}

	@Override
	public boolean handleIncomingPacket(EntityPlayer player, Object[] o) {
		if(player.worldObj.isRemote)
			harvestBlocks(player.worldObj, (Integer)o[0], (Integer)o[1], (Integer)o[2], (Integer)o[3]);
		return false;
	}
   	
}
