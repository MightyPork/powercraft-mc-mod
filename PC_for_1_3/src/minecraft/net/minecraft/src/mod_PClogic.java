package net.minecraft.src;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;


/**
 * PowerCraft Logic module
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
public class mod_PClogic extends PC_Module {
	@Override
	public String getVersion() {
		return mod_PCcore.VERSION;
	}

	/**
	 * Get images directory (ending with slash)
	 * 
	 * @return the directory
	 */
	public static String getImgDir() {
		return "/PowerCraft/logic/";
	}

	/**
	 * Get terrain file path (png)
	 * 
	 * @return the file path
	 */
	public static String getTerrainFile() {
		return getImgDir() + "tiles.png";
	}

	@Override
	public String getModuleName() {
		return "LOGIC";
	}


	/**
	 * World-wide radio manager
	 */
	public static PClo_RadioBus RADIO = new PClo_RadioBus();


	// *** PROPERTIES ***

	/** Channel used for newly built radio */
	public static String default_radio_channel = "default";
	/** range of newly placed sensor */
	public static int default_sensor_range = 3;

	private static final String pk_idGateOff = "id.block.gate_off";
	private static final String pk_idGateOn = "id.block.gate_on";
	private static final String pk_idPulsar = "id.block.pulsar";
	private static final String pk_idLightOff = "id.block.light_off";
	private static final String pk_idLightOn = "id.block.light_on";
	private static final String pk_brightLight = "brightness.light_on";
	private static final String pk_brightGate = "brightness.gate_on";
	private static final String pk_idRadio = "id.block.radio";
	private static final String pk_idSensor = "id.block.motion_sensor";
	private static final String pk_idRemote = "id.item.radio_remote";
	private static final String pk_optRadioDefChannel = "default.radio.channel";
	private static final String pk_optSensorDefRange = "default.sensor.range";



	// *** BLOCKS & ITEMS ***

	/** Radio transmitter block */
	public static Block radio;

	/** Entity sensor block (metadata subtypes) */
	public static PClo_BlockSensor sensor;

	/** Portable transmitter */
	public static Item portableTx;

	/** flat device, off state */
	public static Block gateOff;

	/** flat device, on state */
	public static Block gateOn;

	/** pulsar block */
	public static Block pulsar;

	/** light, off state */
	public static Block lightOff;

	/** light, on state */
	public static Block lightOn;



	// *** MODULE INIT ***

	@Override
	public void preInit() {}

	@Override
	public void initProperties(PC_PropertyManager conf) {

		conf.putBlock(pk_idGateOff, 223);
		conf.putBlock(pk_idGateOn, 224);
		conf.putBlock(pk_idPulsar, 225);
		conf.putBlock(pk_idLightOff, 226);
		conf.putBlock(pk_idLightOn, 227);
		conf.putInteger(pk_brightLight, 12, "Light block brightness, scale 0-15.");
		conf.putInteger(pk_brightGate, 8, "Active gate block brightness, scale 0-15.");
		conf.putBlock(pk_idRadio, 236);
		conf.putBlock(pk_idSensor, 229);
		conf.putItem(pk_idRemote, 19000);
		conf.putString(pk_optRadioDefChannel, "default", "the default channel for radios");
		conf.putInteger(pk_optSensorDefRange, 3, "the range of newly placed sensor");

		conf.apply();

		default_radio_channel = conf.getString(pk_optRadioDefChannel);
		default_sensor_range = conf.getInteger(pk_optSensorDefRange);
	}

	@Override
	public void registerEntities(List<PC_Struct3<Class<? extends Entity>, String, Integer>> list) {}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void registerTileEntities(List<PC_Struct3<Class<? extends TileEntity>, String, TileEntitySpecialRenderer>> list) {
		list.add(new PC_Struct3(PClo_TileEntityGate.class, "FCLogicGate", null));
		list.add(new PC_Struct3(PClo_TileEntityPulsar.class, "FCRedstonePulsar", null));
		list.add(new PC_Struct3(PClo_TileEntityLight.class, "FCRedstoneIndicator", new PClo_TileEntityLightRenderer()));
		list.add(new PC_Struct3(PClo_TileEntitySensor.class, "FCSensorRanged", new PClo_TileEntitySensorRenderer()));
		list.add(new PC_Struct3(PClo_TileEntityRadio.class, "PCRadioDevice", new PClo_TileEntityRadioRenderer()));
	}

	@Override
	public void registerBlockRenderers() {
		PClo_Renderer.radioRenderer = ModLoader.getUniqueBlockModelID(this, true);
		PClo_Renderer.sensorRenderer = ModLoader.getUniqueBlockModelID(this, true);
		PClo_Renderer.lightRenderer = ModLoader.getUniqueBlockModelID(this, true);
	}

	@Override
	public void registerBlocks(List<Block> list) {
		//@formatter:off
		
		radio = new PClo_BlockRadio(cfg().getInteger(pk_idRadio))
				.setBlockName("PCloRadio")
				.setHardness(0.35F)
				.setResistance(30.0F);

		sensor = (PClo_BlockSensor) (new PClo_BlockSensor(cfg().getInteger(pk_idSensor))
				.setBlockName("PCloSensorRanged")
				.setHardness(0.35F)
				.setResistance(30.0F));


		gateOff = new PClo_BlockGate(cfg().getInteger(pk_idGateOff), false)
				.setBlockName("PCloLogicGate")
				.setHardness(0.35F).setLightValue(0)
				.setStepSound(Block.soundWoodFootstep)
				.disableStats().setRequiresSelfNotify()
				.setResistance(30.0F);

		gateOn = new PClo_BlockGate(cfg().getInteger(pk_idGateOn), true)
				.setBlockName("PCloLogicGate")
				.setHardness(0.35F).setLightValue(cfg().getInteger(pk_brightGate) * 0.0625F)
				.setStepSound(Block.soundWoodFootstep)
				.disableStats().setRequiresSelfNotify()
				.setResistance(30.0F);

		pulsar = new PClo_BlockPulsar(cfg().getInteger(pk_idPulsar))
				.setHardness(0.8F)
				.setResistance(30.0F)
				.setBlockName("PCloRedstonePulsar")
				.setRequiresSelfNotify()
				.setStepSound(Block.soundWoodFootstep);

		lightOff = new PClo_BlockLight(cfg().getInteger(pk_idLightOff), false)
				.setHardness(0.3F)
				.setResistance(20F)
				.setBlockName("PCloLight")
				.setStepSound(Block.soundStoneFootstep)
				.setRequiresSelfNotify();

		lightOn = new PClo_BlockLight(cfg().getInteger(pk_idLightOn), true)
				.setHardness(0.3F)
				.setResistance(20F)
				.setLightValue(cfg().getInteger(pk_brightLight) * 0.0625F)
				.setBlockName("PCloLight")
				.setStepSound(Block.soundStoneFootstep)
				.setRequiresSelfNotify();
		
		list.add(radio);
		list.add(sensor);
		list.add(gateOff);
		list.add(gateOn);
		list.add(pulsar);
		list.add(lightOff);
		list.add(lightOn);
		
		//@formatter:on

	}

	@Override
	public void registerItems() {
		// @formatter:off
		
		portableTx = (new PClo_ItemRadioRemote(cfg().getInteger(pk_idRemote)))
				.setMaxStackSize(1)
				.setItemName("PCloRadioPortableTx");
		
		// @formatter:on

		removeBlockItem(gateOn.blockID);
		setBlockItem(gateOn.blockID, new PClo_ItemBlockGate(gateOn.blockID - 256));
		removeBlockItem(lightOn.blockID);
		setBlockItem(lightOn.blockID, new PClo_ItemBlockLight(lightOn.blockID - 256));
		removeBlockItem(sensor.blockID);
		setBlockItem(sensor.blockID, new PClo_ItemBlockSensor(sensor.blockID - 256));
		removeBlockItem(radio.blockID);
		setBlockItem(radio.blockID, new PClo_ItemBlockRadio(radio.blockID - 256));

	}

	@Override
	public void preloadTextures(List<String> list) {
		list.add(getTerrainFile());
		list.add(getImgDir() + "block_radio.png");
		list.add(getImgDir() + "block_sensor.png");
		list.add(getImgDir() + "block_light.png");
	}

	@Override
	public void setTextures() {
		portableTx.setIconIndex(ModLoader.addOverride("/gui/items.png", getImgDir() + "portable.png"));
	}

	@Override
	public void setNames(Map<Object, String> map) {
		map.put(pulsar, "Redstone Pulsar");
		map.put(portableTx, "Radio Remote");

		map.put("tile.PCloRadio.name", "Redstone Radio Device");
		map.put("tile.PCloRadio.tx.name", "Redstone Radio Transmitter");
		map.put("tile.PCloRadio.rx.name", "Redstone Radio Receiver");

		map.put("tile.PCloSensorRanged.name", "Proximity Detector");
		map.put("tile.PCloSensorRanged.item.name", "Item Proximity Detector");
		map.put("tile.PCloSensorRanged.living.name", "Mob Proximity Detector");
		map.put("tile.PCloSensorRanged.player.name", "Player Proximity Detector");

		map.put("tile.PCloLight.isHuge", "is Huge");
		map.put("tile.PCloLight.isStable", "is Stable");
		map.put("tile.PCloLight.name", "Light");

		map.put("tile.PCloLogicGate.name", "Redstone Logic Gate");
		map.put("tile.PCloLogicGate.not.name", "Redstone Inverter");
		map.put("tile.PCloLogicGate.and.name", "Redstone AND gate");
		map.put("tile.PCloLogicGate.nand.name", "Redstone NAND gate");
		map.put("tile.PCloLogicGate.or.name", "Redstone OR gate");
		map.put("tile.PCloLogicGate.nor.name", "Redstone NOR gate");
		map.put("tile.PCloLogicGate.xor.name", "Redstone XOR gate");
		map.put("tile.PCloLogicGate.xnor.name", "Redstone XNOR gate");
		map.put("tile.PCloLogicGate.xnor3.name", "Redstone 3-input XNOR gate");
		map.put("tile.PCloLogicGate.and3.name", "Redstone 3-input AND gate");
		map.put("tile.PCloLogicGate.nand3.name", "Redstone 3-input NAND gate");
		map.put("tile.PCloLogicGate.or3.name", "Redstone 3-input OR gate");
		map.put("tile.PCloLogicGate.nor3.name", "Redstone 3-input NOR gate");
		map.put("tile.PCloLogicGate.xor3.name", "Redstone 3-input XOR gate");

		map.put("tile.PCloLogicGate.d.name", "Redstone D flip-flop");
		map.put("tile.PCloLogicGate.rs.name", "Redstone RS flip-flop");
		map.put("tile.PCloLogicGate.t.name", "Redstone T flip-flop");

		map.put("tile.PCloLogicGate.day.name", "Day Sensor");
		map.put("tile.PCloLogicGate.rain.name", "Rain Sensor");
		map.put("tile.PCloLogicGate.chestEmpty.name", "Empty Chest Detector");
		map.put("tile.PCloLogicGate.chestFull.name", "Full Chest Detector");
		map.put("tile.PCloLogicGate.special.name", "Redstone Special Controller");
		map.put("tile.PCloLogicGate.buffer.name", "Buffered Delayer");
		map.put("tile.PCloLogicGate.slowRepeater.name", "Slow Repeater");
		map.put("tile.PCloLogicGate.crossing.name", "Redstone Crossing");
		map.put("tile.PCloLogicGate.random.name", "Redstone Random Gate");
		map.put("tile.PCloLogicGate.repeaterStraight.name", "Quick Repeater");
		map.put("tile.PCloLogicGate.repeaterCorner.name", "Angled Repeater");
		map.put("tile.PCloLogicGate.repeaterStraightInstant.name", "Instant Repeater");
		map.put("tile.PCloLogicGate.repeaterCornerInstant.name", "Instant Angled Repeater");
		map.put("tile.PCloLogicGate.night.name", "Night Sensor");
		map.put("tile.PCloLogicGate.splitter.name", "Redstone Splitter");


		// descriptions.
		map.put("pc.gate.not.desc", "negates input");
		map.put("pc.gate.and.desc", "both inputs on");
		map.put("pc.gate.nand.desc", "some inputs off");
		map.put("pc.gate.or.desc", "at least one input on");
		map.put("pc.gate.nor.desc", "all inputs off");
		map.put("pc.gate.xor.desc", "inputs different");
		map.put("pc.gate.xnor.desc", "inputs equal");
		map.put("pc.gate.xnor3.desc", "all inputs equal");
		map.put("pc.gate.and3.desc", "all inputs on");
		map.put("pc.gate.nand3.desc", "some inputs off");
		map.put("pc.gate.or3.desc", "at least one input on");
		map.put("pc.gate.nor3.desc", "all inputs off");
		map.put("pc.gate.xor3.desc", "inputs different");

		map.put("pc.gate.d.desc", "latch memory");
		map.put("pc.gate.rs.desc", "set/reset memory");
		map.put("pc.gate.t.desc", "divides signal by 2");

		map.put("pc.gate.day.desc", "on during day");
		map.put("pc.gate.rain.desc", "on during rain");
		map.put("pc.gate.chestEmpty.desc", "on if nearby container is empty");
		map.put("pc.gate.chestFull.desc", "on if nearby container is full");
		map.put("pc.gate.special.desc", "spawner & pulsar control");
		map.put("pc.gate.buffer.desc", "slows down signal");
		map.put("pc.gate.slowRepeater.desc", "makes pulses longer");
		map.put("pc.gate.crossing.desc", "lets two wires intersect");
		map.put("pc.gate.random.desc", "changes state randomly on pulse");
		map.put("pc.gate.repeaterStraight.desc", "simple 1-tick repeater");
		map.put("pc.gate.repeaterCorner.desc", "simple 1-tick corner repeater");
		map.put("pc.gate.repeaterStraightInstant.desc", "instant repeater");
		map.put("pc.gate.repeaterCornerInstant.desc", "instant corner repeater");
		map.put("pc.gate.night.desc", "on during night");
		map.put("pc.gate.splitter.desc", "splits signal");


		map.put("pc.radio.activatorGetChannel", "Channel \"%s\" assigned to activation crystal.");
		map.put("pc.radio.activatorSetChannel", "Radio connected to channel \"%s\".");
		
		map.put("pc.radioRemote.connected", "Portable transmitter connected to channel \"%s\".");
		map.put("pc.radioRemote.desc", "Channel: %s");
		
		
		map.put("pc.gui.gate.delay", "Delay (sec)");
		map.put("pc.gui.gate.delayer.errRange", "Delay time out of range.");
		map.put("pc.gui.gate.delayer.errNumFormat", "Invalid number format.");
		map.put("pc.gui.gate.delayer.ticks", "ticks");
		map.put("pc.gui.pulsar.silent", "Silent");
		map.put("pc.gui.pulsar.delay", "Delay (sec)");
		map.put("pc.gui.pulsar.hold", "Hold time (sec)");
		map.put("pc.gui.pulsar.ticks", "ticks");
		map.put("pc.gui.pulsar.errDelay", "Bad delay time!");
		map.put("pc.gui.pulsar.errHold", "Bad hold time!");
		map.put("pc.gui.radio.channel", "Channel:");
		map.put("pc.gui.radio.showLabel", "Show label");
		map.put("pc.gui.radio.errChannel", "Invalid channel name.");
		map.put("pc.gui.radio.renderSmall", "Tiny");
		map.put("pc.pulsar.clickMsg", "Period %s ticks (%s s)");
		map.put("pc.pulsar.clickMsgTime", "Period %s ticks (%s s), remains %s");


		map.put("pc.gui.sensor.range", "Detection distance:");

		map.put("pc.sensor.range.1", "Range: %s block");
		map.put("pc.sensor.range.2-4", "Range: %s blocks");
		map.put("pc.sensor.range.5+", "Range: %s blocks");


		map.put("pc.gui.chestFull.requireAllSlotsFull", "All slots must be fully used");

	}

	@Override
	public void addRecipes() {
		//@formatter:off
		
		// *** pulsar ***
		
		ModLoader.addRecipe(
				new ItemStack(pulsar, 1, 0),
				new Object[] { " r ", "ror", " r ",
					'r', Item.redstone, 'o', Block.obsidian });

		
		// *** basic gates ***
		
		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.NOT),
				new Object[] { "RST",
					'R', Item.redstone, 'S', Block.stone, 'T', Block.torchRedstoneActive });

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.AND),
				new Object[] { " R ", "SSS", "R R",
					'R', Item.redstone, 'S', Block.stone });

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.OR),
				new Object[] { " R ", "RSR",
					'R', Item.redstone, 'S', Block.stone });

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.XOR),
				new Object[] { "R", "X",
					'X', new ItemStack(gateOn, 1, PClo_GateType.OR), 'R', Item.redstone });

		
		// *** negated ***
		
		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.NAND),
				new Object[] { "I", "G",
					'I', new ItemStack(gateOn, 1, PClo_GateType.NOT), 'G', new ItemStack(gateOn, 1, PClo_GateType.AND) });

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.NOR),
				new Object[] { "I", "G",
					'I', new ItemStack(gateOn, 1, PClo_GateType.NOT), 'G', new ItemStack(gateOn, 1, PClo_GateType.OR) });

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.XNOR),
				new Object[] { "I", "G",
					'I', new ItemStack(gateOn, 1, PClo_GateType.NOT), 'G', new ItemStack(gateOn, 1, PClo_GateType.XOR) });

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.NOR3),
				new Object[] { "I", "G",
					'I', new ItemStack(gateOn, 1, PClo_GateType.NOT), 'G', new ItemStack(gateOn, 1, PClo_GateType.OR3) });

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.NAND3),
				new Object[] { "I", "G",
					'I', new ItemStack(gateOn, 1, PClo_GateType.NOT), 'G', new ItemStack(gateOn, 1, PClo_GateType.AND3) });

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.XNOR3),
				new Object[] { "I", "G",
					'I', new ItemStack(gateOn, 1, PClo_GateType.NOT), 'G', new ItemStack(gateOn, 1, PClo_GateType.XOR3) });

		
		// *** basic 3-input ***
		
		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.AND3),
				new Object[] { " R ", "SSS", "RRR",
					'R', Item.redstone, 'S', Block.stone });

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.OR3),
				new Object[] { " R ", "RSR", " R ",
					'R', Item.redstone, 'S', Block.stone });

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.XOR3),
				new Object[] { "R", "X",
					'X', new ItemStack(gateOn, 1, PClo_GateType.OR3), 'R', Item.redstone });

		// *** flip flops ***
		
		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.D),
				new Object[] { " S ", "RSR", " S ",
					'S', Block.stone, 'R', Item.redstone });

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.RS),
				new Object[] { " R ", "SLS", "R R",
					'R', Item.redstone, 'S', Block.stone, 'L', Block.lever });

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.T),
				new Object[] { "RSR",
					'R', Item.redstone, 'S', Block.stone });
		

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.RANDOM),
				new Object[] { "R","T",
					'R', Item.redstone, 'T', new ItemStack(gateOn, 1, PClo_GateType.T) });

		// *** special sensors ***
		
		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.DAY),
				new Object[] { "G", "P",
					'G', Item.lightStoneDust, 'P', Block.pressurePlatePlanks });

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.DAY),
				new Object[] { "G", "P",
					'G', Item.lightStoneDust, 'P', Block.pressurePlateStone });
		
		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.NIGHT),
				new Object[] { "N", "G",
					'N', new ItemStack(gateOn, 1, PClo_GateType.NOT), 'G', new ItemStack(gateOn, 1, PClo_GateType.DAY) });

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.RAIN),
				new Object[] { "L", "P",
					'L', new ItemStack(Item.dyePowder, 1, 4), 'P', Block.pressurePlatePlanks });
		
		ModLoader.addRecipe(
				new ItemStack(gateOn, 1,
						PClo_GateType.RAIN),
				new Object[] { "L", "P",
					'L', new ItemStack(Item.dyePowder, 1, 4), 'P', Block.pressurePlateStone });

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.CHEST_EMPTY),
				new Object[] { "C", "P",
					'C', Block.chest, 'P', Block.pressurePlatePlanks });
		
		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.CHEST_EMPTY),
				new Object[] { "C", "P",
					'C', Block.chest, 'P', Block.pressurePlateStone });

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.CHEST_FULL),
				new Object[] { "I", "G",
					'I', new ItemStack(gateOn, 1, PClo_GateType.NOT), 'G', new ItemStack(gateOn, 1, PClo_GateType.CHEST_EMPTY) });

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.SPECIAL),
				new Object[] { " I", "RS",
					'R', Item.redstone, 'S', Block.stone, 'I', Item.ingotIron });

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.FIFO_DELAYER),
				new Object[] { "DDD", "SSS",
					'D', Item.redstoneRepeater, 'S', Block.stone });

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.HOLD_DELAYER),
				new Object[] { "DD", "SS",
					'D', Item.redstoneRepeater, 'S', Block.stone });
		

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.REPEATER_STRAIGHT),
				new Object[] { "R", "R", "R",
					'R', Item.redstone});
		
		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.REPEATER_CORNER),
				new Object[] { "RR", " R",
					'R', Item.redstone});
		
		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.REPEATER_STRAIGHT_I),
				new Object[] { "R", "S",
					'R', Item.redstone, 'S', new ItemStack(gateOn,1,PClo_GateType.REPEATER_STRAIGHT)});
		
		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.REPEATER_CORNER_I),
				new Object[] { "R", "S",
					'R', Item.redstone, 'S', new ItemStack(gateOn,1,PClo_GateType.REPEATER_CORNER)});

		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.CROSSING),
				new Object[] { " + ", "+++", " + ",
					'+', Item.redstone });
		
		ModLoader.addRecipe(
				new ItemStack(gateOn, 1, PClo_GateType.SPLITTER_I),
				new Object[] { "S+S", "+++", "S+S",
					'+', Item.redstone,'S', Block.stone});
		
		
		// *** lights ***
		
		// SMALL
		
		for(int i=0; i<16; i++) {
			ModLoader.addShapelessRecipe(
					new ItemStack(lightOn, 1, i),
					new Object[] { Item.redstone, Item.lightStoneDust, new ItemStack(Item.dyePowder, 1, i) });
		}

		
		// STABLE

		for(int i=16; i<32; i++) {
			ModLoader.addShapelessRecipe(
					new ItemStack(lightOn, 1, i),
					new Object[] { Item.lightStoneDust, new ItemStack(Item.dyePowder, 1, i%16) });
		}
		
		// SEGMENTS
		
		for(int i=32; i<48; i++) {
			ModLoader.addShapelessRecipe(
					new ItemStack(lightOn, 1, i),
					new Object[] { Item.redstone, Block.glowStone, new ItemStack(Item.dyePowder, 1, i%16) });
		}
		
		
		
		// *** RADIOS ***
		
		//transmiter (gold) = 0
		ModLoader.addRecipe(
				new ItemStack(radio,1,0),
				new Object[] { " I ", "RIR", "SSS",
					'I', Item.ingotGold, 'R', Item.redstone, 'S', Block.stone });

		//receiver (iron) = 1
		ModLoader.addRecipe(
				new ItemStack(radio,1,1),
				new Object[] { " I ", "RIR", "SSS",
					'I', Item.ingotIron, 'R', Item.redstone, 'S', Block.stone });

		ModLoader.addRecipe(
				new ItemStack(portableTx),
				new Object[] { "T", "B",
					'B', Block.button, 'T', radio });

		// *** SENSORS ***
		
		ModLoader.addRecipe(
				new ItemStack(sensor, 1, 1),
				new Object[] { "R", "I", "S",
					'I', Item.ingotIron, 'R', Item.redstone, 'S', Block.stone });


		ModLoader.addRecipe(
				new ItemStack(sensor, 1, 0),
				new Object[] { "R", "I", "W",
					'I', Item.ingotIron, 'R', Item.redstone, 'W', Block.planks });

		ModLoader.addRecipe(
				new ItemStack(sensor, 1, 2),
				new Object[] { "R", "I", "O",
					'I', Item.ingotIron, 'R', Item.redstone, 'O', Block.obsidian });
		

		//@formatter:on
	}

	@Override
	public void postInit() {
		PC_InveditManager.setDamageRange(gateOn.blockID, 0, PClo_GateType.TOTAL_GATE_COUNT - 1);
		PC_InveditManager.setDamageRange(sensor.blockID, 0, 2);
		PC_InveditManager.setDamageRange(lightOn.blockID, 0, 47);
		PC_InveditManager.hideItem(gateOff.blockID);
		PC_InveditManager.hideItem(lightOff.blockID);

		PC_InveditManager.setItemCategory(lightOn.blockID, "Control lights");

		PC_InveditManager.setItemCategory(pulsar.blockID, "Logic gates");
		PC_InveditManager.setItemCategory(gateOn.blockID, "Logic gates");
		PC_InveditManager.setItemCategory(sensor.blockID, "Wireless");
		PC_InveditManager.setItemCategory(radio.blockID, "Wireless");
		PC_InveditManager.setItemCategory(portableTx.shiftedIndex, "Wireless");


		//@formatter:off
		addStackRangeToCraftingTool(PC_ItemGroup.LOGIC, gateOn.blockID, 0, PClo_GateType.TOTAL_GATE_COUNT - 1, 1);
		addStacksToCraftingTool(PC_ItemGroup.LOGIC, 
				new ItemStack(pulsar));
		
		addStacksToCraftingTool(PC_ItemGroup.LIGHTS,new ItemStack(lightOn, 1, 0));
		
		addStacksToCraftingTool(PC_ItemGroup.WIRELESS,
				
				new ItemStack(radio, 1, 0),
				new ItemStack(radio, 1, 1), 
				new ItemStack(portableTx),
				
				new ItemStack(sensor, 1, 0),
				new ItemStack(sensor, 1, 1),
				new ItemStack(sensor, 1, 2));
		
		//@formatter:on
	}



	@Override
	public boolean renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iblockaccess, int i, int j, int k, Block block, int renderType) {
		return PClo_Renderer.renderBlockByType(renderblocks, iblockaccess, i, j, k, block, renderType);
	}

	@Override
	public void renderInvBlock(RenderBlocks renderblocks, Block block, int i, int rtype) {
		PClo_Renderer.renderInvBlockByType(renderblocks, block, i, rtype);
	}

	@Override
	protected Hashtable<String, PC_PacketHandler> addPacketHandler() {
		return null;
	}

	@Override
	protected Hashtable<String, PC_INBTWD> addNetManager() {
		Hashtable<String, PC_INBTWD> nh = new Hashtable<String, PC_INBTWD>();
		nh.put("RadioHandler", RADIO);
		return nh;
	}
}
