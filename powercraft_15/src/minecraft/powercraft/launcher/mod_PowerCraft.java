package powercraft.launcher;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;

/**
 * 
 * PowerCraft Main Class with will be constructed form forge
 * 
 * @author XOR
 * 
 */
@Mod(modid = "PowerCraft", name = "PowerCraft", version = "3.5.3", dependencies = "after:*")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, clientPacketHandlerSpec = @SidedPacketHandler(channels = { "PowerCraft" }, packetHandler = PC_PacketHandler.class), serverPacketHandlerSpec = @SidedPacketHandler(channels = { "PowerCraft" }, packetHandler = PC_PacketHandler.class))
public class mod_PowerCraft {

	/**
	 * The LauncherUtils
	 */
	@SidedProxy(clientSide = "powercraft.launcher.PC_LauncherClientUtils", serverSide = "powercraft.launcher.PC_LauncherUtils")
	public static PC_LauncherUtils proxy;

	/**
	 * Mod instance
	 */
	private static mod_PowerCraft instance;

	/**
	 * 
	 * get the Mod instance
	 * 
	 * @return the Mod instance
	 */
	public static mod_PowerCraft getInstance() {
		return instance;
	}

	public mod_PowerCraft() {
		instance = this;
	}

	/**
	 * 
	 * pre init function, will be called from forge
	 * 
	 * @param event
	 */
	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		hackInfo();
		PC_Launcher.preInit();
	}

	/**
	 * 
	 * init function, will be called from forge
	 * 
	 * @param event
	 */
	@Init
	public void init(FMLInitializationEvent event) {
		PC_Launcher.init();
	}

	/**
	 * 
	 * post init function, will be called from forge
	 * 
	 * @param event
	 */
	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
		PC_Launcher.postInit();
	}

	/**
	 * 
	 * get PowerCraft mod container from forge
	 * 
	 * @return the mod container
	 */
	public ModContainer getModContainer() {
		List<ModContainer> modContainers = Loader.instance().getModList();

		for (ModContainer modContainer : modContainers) {
			if (modContainer.matches(this)) {
				return modContainer;
			}
		}

		return null;
	}

	/**
	 * 
	 * get PowerCraft mod metadata from forge
	 * 
	 * @return the mod metadata
	 */
	public ModMetadata getModMetadata() {
		return getModContainer().getMetadata();
	}

	/**
	 * 
	 * get the powercraft version
	 * 
	 * @return powercraft version
	 */
	public String getVersion() {
		return getModMetadata().version;
	}

	/**
	 * 
	 * get the powercraft name
	 * 
	 * @return powercraft name
	 */
	public String getName() {
		return getModMetadata().name;
	}

	/**
	 * 
	 * hack forge powercraft info
	 * 
	 */
	private void hackInfo() {
		ModMetadata mm = getModMetadata();
		mm.autogenerated = false;
		mm.authorList = new ArrayList<String>();
		mm.authorList.add("XOR");
		mm.authorList.add("Rapus");
		mm.authorList.add("wolfhowl42");
		mm.credits = "MightyPork, RxD, LOLerul2";
		mm.description = "";
		mm.logoFile = "/textures/PowerCraft.png";
		mm.url = "http://powercrafting.net/";
	}

}
