package net.minecraft.src;


/**
 * Proximity sensor tile entity
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
public class PClo_TileEntitySensor extends PC_TileEntity {


	/** Flag that the sensor is active - giving power */
	public boolean active = false;
	/** Current range in blocks. */
	public int range = mod_PClogic.default_sensor_range;
	
	public int type;

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		active = nbttagcompound.getBoolean("on");
		range = nbttagcompound.getInteger("range");
		if (range < 1) {
			range = mod_PClogic.default_sensor_range;
		}
		type = nbttagcompound.getInteger("type");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setBoolean("on", active);
		nbttagcompound.setInteger("range", range);
		nbttagcompound.setInteger("type", type);
	}


	/**
	 * show range using
	 */
	public void printRange() {
		String msg = "";

		if (range == 1) {
			msg = PC_Lang.tr("pc.sensor.range.1", new String[] { range + "" });
		}
		if (range > 1 && range < 5) {
			msg = PC_Lang.tr("pc.sensor.range.2-4", new String[] { range + "" });
		}
		if (range >= 5) {
			msg = PC_Lang.tr("pc.sensor.range.5+", new String[] { range + "" });
		}

		PC_Utils.chatMsg(msg, true);
	}

	/**
	 * Forge method - can update?
	 * 
	 * @return can update
	 */
	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		int count = 0;
		if (getGroup() == 0) {
			
			count += worldObj.getEntitiesWithinAABB(
					net.minecraft.src.EntityItem.class,
					AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).expand(getRange(), getRange(),
							getRange())).size();
			
			count += worldObj.getEntitiesWithinAABB(
					net.minecraft.src.EntityXPOrb.class,
					AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).expand(getRange(), getRange(),
							getRange())).size();
			
			count += worldObj.getEntitiesWithinAABB(
					net.minecraft.src.EntityArrow.class,
					AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).expand(getRange(), getRange(),
							getRange())).size();
			
		} else if (getGroup() == 1) {
			count += worldObj.getEntitiesWithinAABB(
					net.minecraft.src.EntityAnimal.class,
					AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).expand(getRange(), getRange(),
							getRange())).size();
			count += worldObj.getEntitiesWithinAABB(
					net.minecraft.src.EntityCreature.class,
					AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).expand(getRange(), getRange(),
							getRange())).size();
			count += worldObj.getEntitiesWithinAABB(
					net.minecraft.src.EntitySlime.class,
					AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).expand(getRange(), getRange(),
							getRange())).size();
		} else if (getGroup() == 2) {
			count += worldObj.getEntitiesWithinAABB(
					net.minecraft.src.EntityPlayer.class,
					AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).expand(getRange(), getRange(),
							getRange())).size();
		}
		if (count > 0) {
			if (!active) {
				active = true;
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType().blockID);
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord - 1, zCoord, getBlockType().blockID);
				worldObj.markBlocksDirty(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
				worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);

			}
		} else {
			if (active) {
				active = false;
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType().blockID);
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord - 1, zCoord, getBlockType().blockID);
				worldObj.markBlocksDirty(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
				worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);

			}
		}
	}

	/**
	 * Get detected entity group (item, mob, player)
	 * 
	 * @return group number
	 */
	public int getGroup() {
		return type;
	}

	/**
	 * Get surrent range distance.
	 * 
	 * @return range
	 */
	public int getRange() {
		return range;
	}

	@Override
	public void set(Object[] o) {
		int p = 0;
		while(p<o.length){
			String var = (String)o[p++];
			if(var.equals("active"))
				active = (Boolean)o[p++];
			else if(var.equals("range"))
				range = (Integer)o[p++];
			else if(var.equals("type"))
				type = (Integer)o[p++];
		}
		
	}

	@Override
	public Object[] get() {
		return new Object[] {
				"active", active,
				"range", range,
				"type", type
		};
	}
}
