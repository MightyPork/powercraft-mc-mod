package powercraft.machines;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.interfaces.PC_IBeamHandlerExt;
import powercraft.api.inventory.PC_ISpecialInventoryTextures;
import powercraft.api.item.PC_IItemInfo;
import powercraft.api.network.PC_IPacketHandler;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.registry.PC_KeyRegistry;
import powercraft.api.registry.PC_MSGRegistry;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Color;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.apiOld.PC_BeamTracer;

@PC_BlockInfo(name="Block Builder", tileEntity=PCma_TileEntityBlockBuilder.class, canPlacedRotated=true)
public class PCma_BlockBlockBuilder extends PC_Block implements PC_IItemInfo, PC_IBeamHandlerExt, PC_IPacketHandler{
	private static final int TXSIDE = 0, TXFRONT = 1;
	
	public static final int ENDBLOCK = 98;
	
	public PCma_BlockBlockBuilder(int id) {
		super(id, Material.ground, "side", "side", "side", "builder_front", "side", "side");
		setHardness(0.7F);
		setResistance(10.0F);
		setStepSound(Block.soundStoneFootstep);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public int tickRate(World world) {
		return 1;
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9) {

		if (world.isRemote) {
			return true;
		}

		PC_GresRegistry.openGres("BlockBuilder", entityplayer, PC_Utils.<PC_TileEntity>getTE(world, i, j, k));

		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		if (l > 0 && Block.blocksList[l].canProvidePower()) {
			boolean flag = isIndirectlyPowered(world, i, j, k);
			if (flag) {
				world.scheduleBlockUpdate(i, j, k, blockID, tickRate(world));
			}
		}
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {
		if (isIndirectlyPowered(world, i, j, k)) {
			buildBlocks(world, i, j, k, world.getBlockMetadata(i, j, k));
		}
	}

	/**
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param deviceMeta
	 */
	private void buildBlocks(World world, int x, int y, int z, int deviceMeta) {

		if(!world.isRemote)
			PC_PacketHandler.sendToPacketHandler(true, world, "PCma_BlockBlockBuilder", x, y, z, deviceMeta);
		
		deviceMeta &= 0x7;

		PC_VecI cnt = new PC_VecI(x, y, z);
		PC_BeamTracer beamTracer = new PC_BeamTracer(world, this);

		beamTracer.setStartCoord(cnt);
		beamTracer.setStartMove(getRotation(deviceMeta).getOffset());
		beamTracer.setCanChangeColor(false);
		beamTracer.setDetectEntities(true);
		beamTracer.setTotalLengthLimit(8000);
		beamTracer.setMaxLengthAfterCrystal(2000);
		beamTracer.setStartLength(30);
		beamTracer.setData("crystalAdd", 100);
		beamTracer.setColor(new PC_Color(0.001f, 0.001f, 1.0f));

		if (world.getBlockId(x, y - 1, z) == ENDBLOCK) {
			beamTracer.setStartLength(1);
			beamTracer.setMaxLengthAfterCrystal(1);
		}
		
		beamTracer.flash();

	}

	private boolean isIndirectlyPowered(World world, int i, int j, int k) {
		if (PC_Utils.getBlockRedstonePowereValue(world, i, j, k)>0) {
			return true;
		}

		if (world.isBlockIndirectlyGettingPowered(i, j, k)) {
			return true;
		}

		if (PC_Utils.getBlockRedstonePowereValue(world, i, j-1, k)>0) {
			return true;
		}

		if (world.isBlockIndirectlyGettingPowered(i, j - 1, k)) {
			return true;
		}
		return false;
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this));
		return arrayList;
	}

	@Override
	public boolean onBlockHit(PC_BeamTracer beamTracer, Block block, PC_VecI coord) {
		return false;
	}

	@Override
	public boolean onEntityHit(PC_BeamTracer beamTracer, Entity entity, PC_VecI coord) {
		return false;
	}

	@Override
	public boolean onEmptyBlockHit(PC_BeamTracer beamTracer, PC_VecI coord) {
		World world = beamTracer.getWorld();
		PCma_TileEntityBlockBuilder tebb = PC_Utils.getTE(world, beamTracer.getStartCoord());
		return tebb.useItem(coord);
	}

	@Override
	public boolean handleIncomingPacket(EntityPlayer player, Object[] o) {
		if(player.worldObj.isRemote)
			buildBlocks(player.worldObj, (Integer)o[0], (Integer)o[1], (Integer)o[2], (Integer)o[3]);
		return false;
	}
	
}
