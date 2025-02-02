package powercraft.api;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import powercraft.api.block.PC_IBlock;
import powercraft.api.block.PC_TileEntity;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

@SuppressWarnings("unused") 
public class PC_PacketHandler implements IPacketHandler {

	public static final int BLOCKDATA = 1, BLOCKMESSAGE = 2, GUIOPEN = 3;


	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {

		DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		EntityPlayer entityPlayer = ((EntityPlayer) player);
		World world = entityPlayer.worldObj;
		try {
			int packetType = dataInputStream.readInt();
			switch (packetType) {
				case BLOCKDATA:
					packetBlockData(world, entityPlayer, dataInputStream);
					break;
				case BLOCKMESSAGE:
					packetBlockMessage(world, entityPlayer, dataInputStream);
					break;
				case GUIOPEN:
					packetGuiOpen(world, entityPlayer, dataInputStream);
					break;
				default:
					PC_Logger.severe("Unknown packet type %s", packetType);
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
			PC_Logger.severe("Error while reading packet");
		}
	}

	protected void packetGuiOpen(World world, EntityPlayer player, DataInputStream dataInputStream) throws IOException {

		PC_Logger.severe("Client %s try to open gui", player.username);
	}

	protected void packetBlockData(World world, EntityPlayer player, DataInputStream dataInputStream) throws IOException {

		PC_Logger.severe("Client %s try to write tileentity data", player.username);
	}


	protected void packetBlockMessage(World world, EntityPlayer player, DataInputStream dataInputStream) throws IOException {

		int x = dataInputStream.readInt();
		int y = dataInputStream.readInt();
		int z = dataInputStream.readInt();
		Block block = PC_Utils.getBlock(world, x, y, z);
		((PC_IBlock) block).onBlockMessage(world, x, y, z, player, readNBTTagCompound(dataInputStream));
	}


	public static Packet250CustomPayload getPowerCraftPacket(byte[] byteArray) {

		return new Packet250CustomPayload("PowerCraft", byteArray);
	}


	public static Packet250CustomPayload getBlockDataPacket(World world, int x, int y, int z) {

		Block block = PC_Utils.getBlock(world, x, y, z);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		try {
			dataOutputStream.writeInt(BLOCKDATA);
			dataOutputStream.writeInt(x);
			dataOutputStream.writeInt(y);
			dataOutputStream.writeInt(z);
			NBTTagCompound nbtTagCompound = new NBTTagCompound("save");
			((PC_IBlock) block).saveToNBT(world, x, y, z, nbtTagCompound);
			writeNBTTagCompound(dataOutputStream, nbtTagCompound);
			return getPowerCraftPacket(byteArrayOutputStream.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			PC_Logger.severe("Error while generating packet");
		}
		return null;
	}


	public static Packet250CustomPayload getGuiPacket(PC_TileEntity tileEntity, int windowId) {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		try {
			dataOutputStream.writeInt(GUIOPEN);
			dataOutputStream.writeInt(tileEntity.xCoord);
			dataOutputStream.writeInt(tileEntity.yCoord);
			dataOutputStream.writeInt(tileEntity.zCoord);
			dataOutputStream.writeInt(windowId);
			return getPowerCraftPacket(byteArrayOutputStream.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			PC_Logger.severe("Error while generating packet");
		}
		return null;
	}


	public static Packet250CustomPayload getGuiPacket(String guiOpenHandlerName, int windowId) {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		try {
			dataOutputStream.writeInt(GUIOPEN);
			dataOutputStream.writeInt(0);
			dataOutputStream.writeInt(-1);
			dataOutputStream.writeUTF(guiOpenHandlerName);
			dataOutputStream.writeInt(windowId);
			return getPowerCraftPacket(byteArrayOutputStream.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			PC_Logger.severe("Error while generating packet");
		}
		return null;
	}


	public static Packet250CustomPayload getGuiPacket(int itemID, int windowId) {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		try {
			dataOutputStream.writeInt(GUIOPEN);
			dataOutputStream.writeInt(0);
			dataOutputStream.writeInt(-2);
			dataOutputStream.writeInt(itemID);
			dataOutputStream.writeInt(windowId);
			return getPowerCraftPacket(byteArrayOutputStream.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			PC_Logger.severe("Error while generating packet");
		}
		return null;
	}


	public static void sendPacketToAllInDimension(Packet250CustomPayload blockDataPacket, int dimension) {

		PacketDispatcher.sendPacketToAllInDimension(blockDataPacket, dimension);
	}


	public static void sendPacketToPlayer(Packet250CustomPayload guiPacket, EntityPlayer player) {

		PacketDispatcher.sendPacketToPlayer(guiPacket, (Player) player);
	}


	public static NBTTagCompound readNBTTagCompound(DataInputStream dataInputStream) throws IOException {

		int size = dataInputStream.readInt();
		byte[] buffer = new byte[size];
		int pos = 0;
		while (dataInputStream.read(buffer, pos, size - pos) < size - pos) {}
		return CompressedStreamTools.decompress(buffer);
	}


	public static void writeNBTTagCompound(DataOutputStream dataOutputStream, NBTTagCompound nbtTagCompound) throws IOException {

		byte[] buffer = CompressedStreamTools.compress(nbtTagCompound);
		dataOutputStream.writeInt(buffer.length);
		dataOutputStream.write(buffer);
	}

}
