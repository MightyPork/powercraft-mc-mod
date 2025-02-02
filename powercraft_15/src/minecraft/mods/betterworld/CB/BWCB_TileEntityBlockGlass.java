package mods.betterworld.CB;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public class BWCB_TileEntityBlockGlass extends TileEntity {

	private int blockDamageID;
	private String userName;
	private boolean incomplete;
	private int lightValue;
	private boolean blockLocked;

	public BWCB_TileEntityBlockGlass() {
	}

	@Override
	public void readFromNBT(NBTTagCompound par1nbtTagCompound) {
		super.readFromNBT(par1nbtTagCompound);
		blockDamageID = par1nbtTagCompound.getInteger("type");
		userName = par1nbtTagCompound.getString("UName");
		lightValue = par1nbtTagCompound.getInteger("LightValue");
		blockLocked = par1nbtTagCompound.getBoolean("BlockLocked");
	}

	@Override
	public void writeToNBT(NBTTagCompound par1nbtTagCompound) {
		super.writeToNBT(par1nbtTagCompound);
		par1nbtTagCompound.setInteger("type", blockDamageID);
		par1nbtTagCompound.setString("UName", userName);
		par1nbtTagCompound.setInteger("LightValue", lightValue);
		par1nbtTagCompound.setBoolean("BlockLocked", blockLocked);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("type", blockDamageID);
		nbt.setString("UName", userName);
		nbt.setInteger("LightValue", lightValue);
		nbt.setBoolean("BlockLocked", blockLocked);

		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		blockDamageID = pkt.customParam1.getInteger("type");
		userName = pkt.customParam1.getString("UName");
		lightValue = pkt.customParam1.getInteger("LightValue");
		blockLocked = pkt.customParam1.getBoolean("BlockLocked");

	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getBlockDamageID() {
		System.out.println("Gesendet: " + blockDamageID);
		return blockDamageID;
	}

	public void setBlockDamageID(int i) {
		blockDamageID = i;
	}

	public int getLightValue() {
		return lightValue;
	}

	public void setLightValue(int lightValue) {
		this.lightValue = lightValue;
	}

	public boolean isBlockLocked() {
		return blockLocked;
	}

	public void setBlockLocked(boolean blockLocked) {
		this.blockLocked = blockLocked;
	}

}
