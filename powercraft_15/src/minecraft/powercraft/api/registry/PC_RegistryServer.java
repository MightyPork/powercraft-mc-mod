package powercraft.api.registry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import powercraft.api.block.PC_Block;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.network.PC_IPacketHandler;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.reflect.PC_ReflectHelper;
import powercraft.api.registry.PC_LangRegistry.LangEntry;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.launcher.loader.PC_ModuleObject;

public class PC_RegistryServer implements PC_IPacketHandler {
	
	protected static final int KEYEVENT = 0;
	
	protected static PC_RegistryServer instance;
	
	protected PC_RegistryServer() {
		PC_PacketHandler.registerPackethandler("RegistryPacket", this);
	}
	
	public static boolean create() {
		if (instance == null) {
			instance = new PC_RegistryServer();
			return true;
		}
		return false;
	}
	
	public static PC_RegistryServer getInstance() {
		return instance;
	}
	
	protected void registerLanguage(PC_ModuleObject module, String lang, LangEntry[] translations) {
	}
	
	protected void loadLanguage(PC_ModuleObject module) {
	}
	
	protected void saveLanguage(PC_ModuleObject module) {
	}
	
	protected void tileEntitySpecialRenderer(Class<? extends TileEntity> tileEntityClass) {
		GameRegistry.registerTileEntity(tileEntityClass, tileEntityClass.getName());
	}
	
	protected void openGres(String name, EntityPlayer player, PC_TileEntity te, Object[] o) {
		if (!(player instanceof EntityPlayerMP)) {
			return;
		}
		
		int guiID = 0;
		
		try {
			Field var6 = EntityPlayerMP.class.getDeclaredFields()[16];
			var6.setAccessible(true);
			guiID = var6.getInt(player);
			guiID = guiID % 100 + 1;
			var6.setInt(player, guiID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		ObjectOutputStream sendData;
		
		try {
			sendData = new ObjectOutputStream(data);
			sendData.writeInt(PC_PacketHandler.PACKETGUI);
			sendData.writeObject(name);
			sendData.writeInt(guiID);
			if (te == null) {
				sendData.writeObject(null);
			} else {
				sendData.writeObject(te.getCoord());
			}
			sendData.writeObject(o);
			sendData.writeInt(PC_PacketHandler.PACKETGUI);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		PC_PacketHandler.sendToPlayer(player, data);
		
		Class<? extends PC_GresBaseWithInventory> c = PC_GresRegistry.getContainer(name);
		
		if (c != null) {
			
			if (PC_GresBaseWithInventory.class.isAssignableFrom(c)) {
				try {
					PC_GresBaseWithInventory bwi = PC_ReflectHelper.create(c, player, te, o);
					player.openContainer = bwi;
					player.openContainer.windowId = guiID;
					player.openContainer.addCraftingToCrafters((EntityPlayerMP) player);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected void registerTexture(String texture) {
	}
	
	protected void playSound(double x, double y, double z, String sound, float soundVolume, float pitch) {
	}
	
	protected void watchForKey(String name, int key) {
	}
	
	protected void onIconLoading(PC_Block block, Object iconRegister) {
		
	}
	
	protected Icon registerIcon(String texture) {
		return null;
	}
	
	@Override
	public boolean handleIncomingPacket(EntityPlayer player, Object[] o) {
		switch ((Integer) o[0]) {
			case KEYEVENT:
				PC_KeyRegistry.onKeyEvent(player, (Boolean) o[1], (String) o[2]);
				break;
		}
		return false;
	}

	public String getUsedLang() {
		return null;
	}
	
}
