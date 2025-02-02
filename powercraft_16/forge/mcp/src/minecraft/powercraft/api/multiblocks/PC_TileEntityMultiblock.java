package powercraft.api.multiblocks;


import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Direction;
import powercraft.api.PC_FieldDescription;
import powercraft.api.PC_Reflection;
import powercraft.api.PC_Utils;
import powercraft.api.blocks.PC_ITileEntitySpecialRenderer;
import powercraft.api.blocks.PC_TileEntity;
import powercraft.api.multiblocks.conduits.PC_ConduitTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class PC_TileEntityMultiblock extends PC_TileEntity implements PC_ITileEntitySpecialRenderer {

	@PC_FieldDescription(sync=true)
	private PC_MultiblockTileEntity[] tileEntities = new PC_MultiblockTileEntity[27];


	@Override
	public boolean canUpdate() {

		return true;
	}


	@Override
	public void updateEntity() {

		for (int i = 0; i < tileEntities.length; i++) {
			if (tileEntities[i] != null) {
				tileEntities[i].update();
			}
		}
		super.updateEntity();
	}


	@Override
	public void invalidate() {

		super.invalidate();
		for (int i = 0; i < tileEntities.length; i++) {
			if (tileEntities[i] != null) {
				tileEntities[i].invalidate();
			}
		}
	}


	@Override
	public void validate() {

		super.validate();
		for (int i = 0; i < tileEntities.length; i++) {
			if (tileEntities[i] != null) {
				tileEntities[i].validate();
			}
		}
	}


	@Override
	public void onBlockBreak() {

		for (int i = 0; i < tileEntities.length; i++) {
			if (tileEntities[i] != null) {
				tileEntities[i].onBreak();
			}
		}
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(Random random) {

		for (int i = 0; i < tileEntities.length; i++) {
			if (tileEntities[i] != null) {
				tileEntities[i].randomDisplayTick(random);
			}
		}
	}


	@Override
	public void onNeighborBlockChange(int neighborID) {

		for (int i = 0; i < tileEntities.length; i++) {
			if (tileEntities[i] != null) {
				tileEntities[i].onNeighborBlockChange(neighborID);
			}
		}
	}


	@Override
	public int getRedstonePowerValue(PC_Direction side) {

		int maxRedstonePowerValue = 0;
		for (int i = 0; i < tileEntities.length; i++) {
			if (tileEntities[i] != null) {
				int redstonePowerValue = tileEntities[i].getRedstonePowerValue(side);
				if (redstonePowerValue > maxRedstonePowerValue) {
					maxRedstonePowerValue = redstonePowerValue;
				}
			}
		}
		return maxRedstonePowerValue;
	}


	@Override
	public int getLightValue() {

		int maxLightValue = 0;
		for (int i = 0; i < tileEntities.length; i++) {
			if (tileEntities[i] != null) {
				int lightValue = tileEntities[i].getLightValue();
				if (lightValue > maxLightValue) {
					maxLightValue = lightValue;
				}
			}
		}
		return maxLightValue;
	}


	@Override
	public boolean canConnectRedstone(PC_Direction side) {

		boolean canConnectRedstone = false;
		for (int i = 0; i < tileEntities.length; i++) {
			if (tileEntities[i] != null) {
				canConnectRedstone |= tileEntities[i].canConnectRedstone(side);
			}
		}
		return canConnectRedstone;
	}


	@Override
	public int getLightOpacity() {

		int maxLightOpacity = 0;
		for (int i = 0; i < tileEntities.length; i++) {
			if (tileEntities[i] != null) {
				int lightOpacity = tileEntities[i].getLightOpacity();
				if (lightOpacity > maxLightOpacity) {
					maxLightOpacity = lightOpacity;
				}
			}
		}
		return maxLightOpacity;
	}


	@Override
	public void onChunkUnload() {

		for (int i = 0; i < tileEntities.length; i++) {
			if (tileEntities[i] != null) {
				tileEntities[i].onChunkUnload();
			}
		}
	}


	public boolean setMultiblockTileEntity(PC_MultiblockIndex index, PC_MultiblockTileEntity tileEntity) {

		return setMultiblockTileEntity(index.ordinal(), tileEntity);
	}


	public boolean setMultiblockTileEntity(int index, PC_MultiblockTileEntity tileEntity) {

		if (tileEntities[index] == null) {
			tileEntities[index] = tileEntity;
			tileEntity.setIndexAndMultiblock(PC_MultiblockIndex.values()[index], this);
			if (!tileEntity.onAdded()) {
				return false;
			}
		} else {
			if (tileEntities[index].canMixWith(tileEntity)) {
				tileEntities[index] = tileEntities[index].mixWith(tileEntity);
				tileEntities[index].setIndexAndMultiblock(PC_MultiblockIndex.values()[index], this);
			} else {
				return false;
			}
		}
		notifyNeighbors();
		sendToClient();
		return true;
	}


	public List<ItemStack> removeMultiblockTileEntity(PC_MultiblockIndex index) {

		return removeMultiblockTileEntity(index.ordinal());
	}


	public List<ItemStack> removeMultiblockTileEntity(int index) {

		if (tileEntities[index] == null) {
			return null;
		}
		List<ItemStack> drop = tileEntities[index].getDrop();
		PC_MultiblockTileEntity te = tileEntities[index];
		tileEntities[index] = null;
		te.onBreak();
		notifyNeighbors();
		sendToClient();
		return drop;
	}


	@Override
	public void notifyNeighbors() {
		if(worldObj!=null){
			onNeighborBlockChange(getBlockType().blockID);
			super.notifyNeighbors();
		}
	}


	public PC_MultiblockTileEntity getMultiblockTileEntity(PC_MultiblockIndex index) {

		return getMultiblockTileEntity(index.ordinal());
	}


	public PC_MultiblockTileEntity getMultiblockTileEntity(int index) {

		return tileEntities[index];
	}


	@SuppressWarnings("unchecked")
	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderWorldBlock(RenderBlocks renderer) {
		if(renderer.hasOverrideBlockTexture()){
			RenderGlobal renderGlobal = PC_ClientUtils.mc().renderGlobal;
			Icon[] destroyBlockIcons = PC_Reflection.getValue(RenderGlobal.class, renderGlobal, 29, Icon[].class);
			for(Entry<Integer, DestroyBlockProgress> e:(Set<Entry<Integer, DestroyBlockProgress>>)renderGlobal.damagedBlocks.entrySet()){
				DestroyBlockProgress destroyblockprogress = e.getValue();
				int x = destroyblockprogress.getPartialBlockX();
				int y = destroyblockprogress.getPartialBlockY();
				int z = destroyblockprogress.getPartialBlockZ();
				if(x == xCoord && y == yCoord && z == zCoord){
					PC_MultiblockIndex index = PC_BlockMultiblock.playerSelection.get(e.getKey());
					if(index!=null && tileEntities[index.ordinal()] != null){
						renderer.setOverrideBlockTexture(destroyBlockIcons[destroyblockprogress.getPartialBlockDamage()]);
						tileEntities[index.ordinal()].renderWorldBlock(renderer);
					}else{
						PC_BlockMultiblock.playerSelection.remove(e.getKey());
					}
				}
			}
		}else{
			for (int i = 0; i < tileEntities.length; i++) {
				if (tileEntities[i] != null) {
					tileEntities[i].renderWorldBlock(renderer);
				}
			}
		}
		return true;
	}


	public List<AxisAlignedBB> getCollisionBoxes(PC_MultiblockIndex index) {

		PC_MultiblockTileEntity multiblock = tileEntities[index.ordinal()];
		if (multiblock != null) {
			return multiblock.getCollisionBoxes();
		}
		return null;
	}


	public AxisAlignedBB getSelectionBox(PC_MultiblockIndex index) {

		PC_MultiblockTileEntity multiblock = tileEntities[index.ordinal()];
		if (multiblock != null) {
			return multiblock.getSelectionBox();
		}
		return null;
	}


	@SuppressWarnings("unused")
	public void addCollisionBoxesToList(List<AxisAlignedBB> list, Entity entity) {

		for (int i = 0; i < tileEntities.length; i++) {
			if (tileEntities[i] != null) {
				List<AxisAlignedBB> l = tileEntities[i].getCollisionBoxes();
				if (l != null) {
					for (AxisAlignedBB e : l) {
						list.add(e);
					}
				}
			}
		}
	}


	public boolean noBlocks() {

		for (int i = 0; i < tileEntities.length; i++) {
			if (tileEntities[i] != null) {
				return false;
			}
		}
		return true;
	}


	public void drop(List<ItemStack> drops) {

		PC_Utils.spawnItems(worldObj, xCoord, yCoord, zCoord, drops);
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void renderTileEntityAt(float timeStamp) {
		for (int i = 0; i < tileEntities.length; i++) {
			if (tileEntities[i] != null) {
				tileEntities[i].renderTileEntityAt(timeStamp);
			}
		}
	}


	@Override
	public void onLoadedFromNBT() {
		super.onLoadedFromNBT();
		for(int i=0; i < tileEntities.length; i++) {
			if (tileEntities[i] != null) {
				tileEntities[i].setIndexAndMultiblock(PC_MultiblockIndex.values()[i], this);
			}
		}
		notifyNeighbors();
		renderUpdate();
	}


	@Override
	public boolean isLadder(EntityLivingBase entity) {
		return tileEntities[0] == null ? false : tileEntities[0] instanceof PC_ConduitTileEntity;
	}


	@Override
	public boolean isBlockSolid(PC_Direction side) {
		PC_MultiblockIndex index = PC_MultiblockIndex.FACEINDEXFORDIR[side.ordinal()];
		if (tileEntities[index.ordinal()] != null) {
			return tileEntities[index.ordinal()].isSolid();
		}
		return false;
	}


	@Override
	public ItemStack getPickBlock(MovingObjectPosition target) {
		int subHit = target.subHit;
		if(subHit>=0 && subHit<tileEntities.length){
			if(tileEntities[subHit]!=null){
				return tileEntities[subHit].getPickBlock();
			}
		}
		return null;
	}
	
	public float getTileHardness(PC_MultiblockIndex index, EntityPlayer player){
		if (tileEntities[index.ordinal()] != null) {
			return tileEntities[index.ordinal()].getHardness(player);
		}
		return 0;
	}
	
}
