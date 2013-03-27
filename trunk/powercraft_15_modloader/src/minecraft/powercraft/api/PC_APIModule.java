package powercraft.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.src.BaseMod;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.EntityList;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntitySkeleton;
import net.minecraft.src.EntitySmokeFX;
import net.minecraft.src.EntityZombie;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.Packet23VehicleSpawn;
import net.minecraft.src.Render;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.World;
import powercraft.api.PC_Utils.GameInfo;
import powercraft.api.annotation.PC_FieldObject;
import powercraft.api.block.PC_Block;
import powercraft.api.block.PC_WorldOreGenerator;
import powercraft.api.entity.PC_EntityFanFX;
import powercraft.api.entity.PC_EntityLaserFX;
import powercraft.api.entity.PC_EntityLaserParticleFX;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresClient;
import powercraft.api.hacks.PC_ClientHacks;
import powercraft.api.hacks.PC_MainMenuHacks;
import powercraft.api.hacks.PC_ModInfo;
import powercraft.api.hacks.PC_RenderPlayerHack;
import powercraft.api.hacks.PC_RenderSkeletonHack;
import powercraft.api.hacks.PC_RenderZombieHack;
import powercraft.api.item.PC_Item;
import powercraft.api.item.PC_ItemArmor;
import powercraft.api.recipes.PC_IRecipe;
import powercraft.api.reflect.PC_FieldWithAnnotation;
import powercraft.api.reflect.PC_IFieldAnnotationIterator;
import powercraft.api.reflect.PC_ReflectHelper;
import powercraft.api.registry.PC_BlockRegistry;
import powercraft.api.registry.PC_DataHandlerRegistry;
import powercraft.api.registry.PC_EntityRegistry;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.registry.PC_ItemRegistry;
import powercraft.api.registry.PC_KeyRegistry;
import powercraft.api.registry.PC_LangRegistry;
import powercraft.api.registry.PC_LangRegistry.LangEntry;
import powercraft.api.registry.PC_MSGRegistry;
import powercraft.api.registry.PC_ModuleRegistry;
import powercraft.api.registry.PC_RecipeRegistry;
import powercraft.api.registry.PC_RegistryClient;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.registry.PC_TickRegistry;
import powercraft.api.renderer.PC_ClientRenderer;
import powercraft.api.thread.PC_ThreadManager;
import powercraft.api.tick.PC_ITickHandler;
import powercraft.api.tick.PC_TickHandler;
import powercraft.launcher.PC_LauncherUtils;
import powercraft.launcher.PC_Logger;
import powercraft.launcher.PC_Property;
import powercraft.launcher.loader.PC_ModLoader;
import powercraft.launcher.loader.PC_Module;
import powercraft.launcher.loader.PC_Module.PC_Init;
import powercraft.launcher.loader.PC_Module.PC_InitProperties;
import powercraft.launcher.loader.PC_Module.PC_Instance;
import powercraft.launcher.loader.PC_Module.PC_PostInit;
import powercraft.launcher.loader.PC_Module.PC_PreInit;
import powercraft.launcher.loader.PC_ModuleObject;

@PC_Module(name = "Api", version = "3.5.0", modLoader = PC_ModLoader.RISUGAMIS_MODLOADER)
public class PC_APIModule {

	private PC_WorldOreGenerator worldGenerator;
	
	private PC_FuelHandler fuelHandler;
	
	private PC_ClientRenderer cr1, cr2;
	
	private PC_MainMenuHacks mainMenuHacks = new PC_MainMenuHacks();
	
	private PC_ClientPacketHandler packetHandler;
	
	private PC_TickHandler tickHandler = new PC_TickHandler();
	
	private static List<KeyBinding> watchKeysForUp = new ArrayList<KeyBinding>();
	
	@PC_Instance
	private static PC_ModuleObject instance;

	static {
		powercraft.launcher.PC_PacketHandler.handler = new PC_ClientPacketHandler();
	}

	@PC_PreInit
	public void preInit() {
		PC_ClientUtils.create();
		PC_Logger.enterSection("PreInit");
		PC_GlobalVariables.loadConfig();
		PC_Logger.enterSection("Register Hacks");
		PC_ClientHacks.hackClient();
		PC_Logger.exitSection();
		PC_Logger.enterSection("Module PreInit");
		List<PC_ModuleObject> modules = PC_ModuleRegistry.getModuleList();
		for (PC_ModuleObject module : modules) {
			module.preInit();
		}
		PC_Logger.exitSection();
		PC_Logger.enterSection("Module Property Init");
		for (PC_ModuleObject module : modules) {
			module.initProperties(module.getConfig());
		}
		PC_KeyRegistry.setReverseKey(PC_GlobalVariables.config);
		PC_Logger.exitSection();
		if (GameInfo.isClient()) {
			PC_Logger.enterSection("Module Language Init");
			for (PC_ModuleObject module : modules) {
				List<LangEntry> l = module
						.initLanguage(new ArrayList<LangEntry>());
				if (l != null) {
					PC_LangRegistry.registerLanguage(module,
							l.toArray(new LangEntry[0]));
				}
			}
			ModLoader.addLocalization("pc.gui.mods", "en_US", "Mods");
			PC_Logger.exitSection();
			PC_Logger.enterSection("Module Texture Init");
			for (PC_ModuleObject module : modules) {
				List<String> l = module
						.loadTextureFiles(new ArrayList<String>());
				if (l != null) {
					for (String file : l) {
						PC_TextureRegistry.registerTexture(PC_TextureRegistry.getPowerCraftImageDir() + PC_TextureRegistry.getTextureName(module, file));
					}
				}
			}
			PC_TextureRegistry.registerTexture(PC_TextureRegistry
					.getGresImgDir() + "button.png");
			PC_TextureRegistry.registerTexture(PC_TextureRegistry
					.getGresImgDir() + "dialog.png");
			PC_TextureRegistry.registerTexture(PC_TextureRegistry
					.getGresImgDir() + "frame.png");
			PC_TextureRegistry.registerTexture(PC_TextureRegistry
					.getGresImgDir() + "scrollbar_handle.png");
			PC_TextureRegistry.registerTexture(PC_TextureRegistry
					.getGresImgDir() + "widgets.png");
			PC_Logger.exitSection();
		}
		PC_Logger.exitSection();
	}

	@PC_Init
	public void init() {
		PC_Logger.enterSection("Init");
		worldGenerator = new PC_WorldOreGenerator();
		fuelHandler = new PC_FuelHandler();
		mainMenuHacks = new PC_MainMenuHacks();
		packetHandler = new PC_ClientPacketHandler();
		ModLoader.registerPacketChannel(PC_LauncherUtils.getMod(), "PowerCraft");
		ModLoader.setInGUIHook(PC_LauncherUtils.getMod(), true, false);
		ModLoader.setInGameHook(PC_LauncherUtils.getMod(), true, false);
		PC_ClientUtils.registerEnitiyFX(PC_EntityLaserParticleFX.class);
		PC_ClientUtils.registerEnitiyFX(PC_EntityLaserFX.class);
		PC_ClientUtils.registerEnitiyFX(PC_EntityFanFX.class);
		PC_ClientUtils.registerEnitiyFX("EntitySmokeFX", EntitySmokeFX.class);
		PC_ThreadManager.init();
		List<PC_ModuleObject> modules = PC_ModuleRegistry.getModuleList();
		cr1 = new PC_ClientRenderer(true);
		cr2 = new PC_ClientRenderer(false);
		PC_Logger.enterSection("Module Init");
		for (PC_ModuleObject module : modules) {
			module.init();
		}
		PC_Logger.exitSection();
		PC_Logger.enterSection("Module Field Init");
		for (PC_ModuleObject module : modules) {
			final PC_ModuleObject m = module;
			PC_ReflectHelper.getAllFieldsWithAnnotation(
					module.getModuleClass(), module, PC_FieldObject.class,
					new PC_IFieldAnnotationIterator<PC_FieldObject>() {

						@Override
						public boolean onFieldWithAnnotation(
								PC_FieldWithAnnotation<PC_FieldObject> fieldWithAnnotation) {
							Class<?> clazz = fieldWithAnnotation
									.getAnnotation().clazz();
							Object o;
							if (PC_Block.class.isAssignableFrom(clazz)) {
								o = PC_BlockRegistry.register(m,
										(Class<? extends PC_Block>) clazz);
							} else if (PC_Item.class.isAssignableFrom(clazz)
									|| PC_ItemArmor.class
											.isAssignableFrom(clazz)) {
								o = PC_ItemRegistry.register(m,
										(Class<? extends Item>) clazz);
							} else {
								o = PC_ReflectHelper.create(clazz);
								if (o instanceof PC_IMSG) {
									PC_MSGRegistry
											.registerMSGObject((PC_IMSG) o);
								}
								if (o instanceof PC_ITickHandler) {
									PC_TickRegistry
											.register((PC_ITickHandler) o);
								}
							}
							fieldWithAnnotation.setValue(o);
							return false;
						}

					});
		}
		PC_Logger.exitSection();
		PC_Logger.enterSection("Module Entity Init");
		for (PC_ModuleObject module : modules) {
			List<PC_Struct2<Class<? extends Entity>, Integer>> l = module
					.initEntities(new ArrayList<PC_Struct2<Class<? extends Entity>, Integer>>());
			if (l != null) {
				for (PC_Struct2<Class<? extends Entity>, Integer> entity : l) {
					PC_EntityRegistry.register(module, entity.a, entity.b);
				}
			}
		}
		PC_Logger.exitSection();
		PC_Logger.enterSection("Module Container Init");
		for (PC_ModuleObject module : modules) {
			List<PC_Struct2<String, Class<PC_GresBaseWithInventory>>> l = module.registerContainers(new ArrayList<PC_Struct2<String, Class<PC_GresBaseWithInventory>>>());
			if (l != null) {
				for (PC_Struct2<String, Class<PC_GresBaseWithInventory>> g : l) {
					PC_GresRegistry.registerGresContainer(g.a, g.b);
				}
			}
		}
		PC_Logger.exitSection();
		if (GameInfo.isClient()) {
			PC_Logger.enterSection("Module Gui Init");
			for (PC_ModuleObject module : modules) {
				List<PC_Struct2<String, Class<PC_IGresClient>>> l = module.registerGuis(new ArrayList<PC_Struct2<String, Class<PC_IGresClient>>>());
				if (l != null) {
					for (PC_Struct2<String, Class<PC_IGresClient>> g : l) {
						PC_GresRegistry.registerGresGui(g.a, g.b);
					}
				}
			}
			PC_Logger.exitSection();
			PC_Logger.enterSection("Module Splashes Init");
			for (PC_ModuleObject module : modules) {
				List<String> l = module.addSplashes(new ArrayList<String>());
				if (l != null) {
					PC_GlobalVariables.splashes.addAll(l);
				}
			}

			PC_GlobalVariables.splashes.add("GRES");

			for (int i = 0; i < 10; i++) {
				PC_GlobalVariables.splashes.add("Modded by MightyPork!");
			}

			for (int i = 0; i < 6; i++) {
				PC_GlobalVariables.splashes.add("Modded by XOR!");
			}

			for (int i = 0; i < 5; i++) {
				PC_GlobalVariables.splashes.add("Modded by Rapus95!");
			}

			for (int i = 0; i < 4; i++) {
				PC_GlobalVariables.splashes.add("Reviewed by RxD");
			}

			PC_GlobalVariables.splashes.add("Modded by masters!");

			for (int i = 0; i < 3; i++) {
				PC_GlobalVariables.splashes.add("PowerCraft "
						+ PC_LauncherUtils.getPowerCraftVersion());
			}

			PC_GlobalVariables.splashes.add("Null Pointers included!");
			PC_GlobalVariables.splashes.add("ArrayIndexOutOfBoundsException");
			PC_GlobalVariables.splashes.add("Null Pointer loves you!");
			PC_GlobalVariables.splashes.add("Unstable!");
			PC_GlobalVariables.splashes.add("Buggy code!");
			PC_GlobalVariables.splashes.add("Break it down!");
			PC_GlobalVariables.splashes.add("Addictive!");
			PC_GlobalVariables.splashes.add("Earth is flat!");
			PC_GlobalVariables.splashes.add("Faster than Atari!");
			PC_GlobalVariables.splashes.add("DAFUQ??");
			PC_GlobalVariables.splashes.add("LWJGL");
			PC_GlobalVariables.splashes.add("Don't press the button!");
			PC_GlobalVariables.splashes.add("Press the button!");
			PC_GlobalVariables.splashes.add("Ssssssssssssssss!");
			PC_GlobalVariables.splashes.add("C'mon!");
			PC_GlobalVariables.splashes.add("Redstone Wizzard!");
			PC_GlobalVariables.splashes.add("Keep your mods up-to-date!");
			PC_GlobalVariables.splashes.add("Read the changelog!");
			PC_GlobalVariables.splashes.add("Read the log files!");
			PC_GlobalVariables.splashes.add("Discoworld!");
			PC_GlobalVariables.splashes.add("Also try ICE AGE mod!");
			PC_GlobalVariables.splashes.add("Also try Backpack mod!");

			PC_Logger.exitSection();
		}
		PC_Logger.exitSection();
	}

	@PC_PostInit
	public void postInit() {
		PC_Logger.enterSection("PostInit");
		PC_TickRegistry.register(PC_ChunkUpdateForcer.getInstance());
		loadMods();
		PC_Logger.enterSection("Module Recipes Init");
		List<PC_ModuleObject> modules = PC_ModuleRegistry.getModuleList();
		for (PC_ModuleObject module : modules) {
			List<PC_IRecipe> l = module
					.initRecipes(new ArrayList<PC_IRecipe>());
			if (l != null) {
				for (PC_IRecipe recipe : l) {
					PC_RecipeRegistry.register(recipe);
				}
			}
		}
		PC_Logger.exitSection();
		PC_Logger.enterSection("Module Data Handlers Init");
		PC_DataHandlerRegistry.regsterDataHandler("chunckUpdateForcer",
				PC_ChunkUpdateForcer.getInstance());
		for (PC_ModuleObject module : modules) {
			List<PC_Struct2<String, PC_IDataHandler>> l = module
					.initDataHandlers(new ArrayList<PC_Struct2<String, PC_IDataHandler>>());
			if (l != null) {
				for (PC_Struct2<String, PC_IDataHandler> dataHandler : l) {
					PC_DataHandlerRegistry.regsterDataHandler(dataHandler.a,
							dataHandler.b);
				}
			}
		}
		PC_Logger.exitSection();
		PC_Logger.enterSection("Module Packet Handlers Init");
		for (PC_ModuleObject module : modules) {
			List<PC_Struct2<String, PC_IPacketHandler>> l = module
					.initPacketHandlers(new ArrayList<PC_Struct2<String, PC_IPacketHandler>>());
			if (l != null) {
				for (PC_Struct2<String, PC_IPacketHandler> packetHandler : l) {
					PC_PacketHandler.registerPackethandler(packetHandler.a,
							packetHandler.b);
				}
			}
		}
		PC_Logger.exitSection();
		PC_Logger.enterSection("Module PostInit");
		for (PC_ModuleObject module : modules) {
			module.postInit();
		}
		PC_Logger.exitSection();
		if (PC_Utils.GameInfo.isClient()) {
			PC_Logger.enterSection("Module Language Saving");
			for (PC_ModuleObject module : modules) {
				PC_LangRegistry.saveLanguage(module);
			}
			PC_Logger.exitSection();
		}
		PC_Logger.enterSection("Module Config Saving");
		for (PC_ModuleObject module : modules) {
			
			module.saveConfig();
		}
		PC_GlobalVariables.saveConfig();
		PC_Logger.exitSection();
		PC_Logger.exitSection();
	}

	private void loadMods() {
		PC_ModInfo modLoader = new PC_ModInfo();
		modLoader.name = "ModLoader";
		modLoader.version = ModLoader.VERSION;
		if(modLoader.version.startsWith(modLoader.name)){
			modLoader.version = modLoader.version.substring(modLoader.name.length()).trim();
		}
		modLoader.description = "This mod is needed for most other mods and will load them into Minecraft";
		List<BaseMod> list = ModLoader.getLoadedMods();
		for(BaseMod bm:list){
			new PC_ModInfo(bm);
		}
	}

	@PC_InitProperties
	public void initProperties(PC_Property config) {

	}

	public static PC_APIModule getInstance() {
		return (PC_APIModule)instance.getModule();
	}
	
	
	public void generateNether(World world, Random random, int chunkX, int chunkZ) {
		worldGenerator.generate(random, chunkX, chunkZ, world, null, null);
	}
	
    public void generateSurface(World world, Random random, int chunkX, int chunkZ) {
    	worldGenerator.generate(random, chunkX, chunkZ, world, null, null);
    }
	
	public int addFuel(int var1, int var2)
    {
		return fuelHandler.getBurnTime(new ItemStack(var1, 1, var2));
    }
	
	public boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess blockAccess, int x, int y, int z, Block block, int modelId) {
		if(modelId == PC_ClientRenderer.getRendererID(true)){
			return cr1.renderWorldBlock(blockAccess, x, y, z, block, modelId, renderer);
		}else if(modelId == PC_ClientRenderer.getRendererID(false)){
			return cr2.renderWorldBlock(blockAccess, x, y, z, block, modelId, renderer);
		}
		return false;
	}

	public void renderInvBlock(RenderBlocks renderer, Block block, int metadata, int modelId) {
		if(modelId == PC_ClientRenderer.getRendererID(true)){
			cr1.renderInventoryBlock(block, metadata, modelId, renderer);
		}else if(modelId == PC_ClientRenderer.getRendererID(false)){
			cr2.renderInventoryBlock(block, metadata, modelId, renderer);
		}
	}
	
	public void keyboardEvent(KeyBinding kb) {
		if(kb.pressed && !watchKeysForUp.contains(kb)){
			PC_RegistryClient.keyEvent(kb.keyDescription, true);
			watchKeysForUp.add(kb);
		}
	}
	
	public boolean onTickInGUI(float var1, Minecraft var2, GuiScreen var3) {
		mainMenuHacks.tickStart();
		return true;
	}
	
	public boolean onTickInGame(float var1, Minecraft var2) {
		tickHandler.tick();
		Iterator<KeyBinding> i = watchKeysForUp.iterator();
		while(i.hasNext()){
			KeyBinding kb = i.next();
			if(!kb.pressed){
				i.remove();
				PC_RegistryClient.keyEvent(kb.keyDescription, false);
			}
		}
		return true;
	}
	
	public void clientConnect(NetClientHandler var1) {
		PC_PacketHandler.requestIDList();
	}
	
	public void addRenderer(Map map) {
		if(PC_Utils.GameInfo.isClient()){
			PC_Logger.enterSection("Register EntityRender");
			List<PC_ModuleObject> modules = PC_ModuleRegistry.getModuleList();
			for(PC_ModuleObject module:modules){
				List<PC_Struct2<Class<? extends Entity>, Render>> list = module.registerEntityRender(new ArrayList<PC_Struct2<Class<? extends Entity>, Render>>());
				if(list!=null){
					for(PC_Struct2<Class<? extends Entity>, Render> s:list){
						map.put(s.a, s.b);
					}
				}
			}
			map.put(EntityPlayer.class, new PC_RenderPlayerHack());
			map.put(EntitySkeleton.class, new PC_RenderSkeletonHack());
			map.put(EntityZombie.class, new PC_RenderZombieHack());
			PC_Logger.exitSection();
		}
	}
    
	public static void registerBlock(Block block, Class<? extends ItemBlock> itemBlock){
		if(itemBlock==null)
			ModLoader.registerBlock(block);
		else
			ModLoader.registerBlock(block, itemBlock);
	}

	public Packet23VehicleSpawn getSpawnPacket(Entity var1, int var2) {
		return new Packet23VehicleSpawn(var1, var2);
	}

	public Entity spawnEntity(int id, World world, double x, double y, double z) {
		Entity e = EntityList.createEntityByID(id, world);
		e.setPosition(x, y, z);
		return e;
	}
	
	public void onItemPickup(EntityPlayer var1, ItemStack var2) {}

	public void clientChat(String var1) {}

	public void serverChat(NetServerHandler var1, String var2) {}

	public void registerAnimation(Minecraft var1) {}

	public void clientDisconnect(NetClientHandler var1) {}

	public void takenFromCrafting(EntityPlayer var1, ItemStack var2, IInventory var3) {}

	public void takenFromFurnace(EntityPlayer var1, ItemStack var2) {}

	public GuiContainer getContainerGUI(EntityClientPlayerMP var1, int var2, int var3, int var4, int var5) {
		return null;
	}

}
