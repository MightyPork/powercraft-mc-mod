package powercraft.machines;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import powercraft.launcher.PC_Logger;
import powercraft.api.PC_Utils.GameInfo;
import powercraft.api.PC_Utils.Inventory;

/**
 * Custom Crops manager
 * 
 * @author MightyPork
 */
public class PCma_CropHarvestingManager {

	private static class CropBlockIDMetaInfo{
		public int id;
		public int meta;
		public int meta_replant;
	}
	
	/*
	 * These are used internally for crop loading.
	 */
	private List<CropBlockIDMetaInfo> new_crop;
	private PC_CropEntry new_entry;
	private boolean new_started = false;

	/**
	 * Folder with the crops xml files
	 */
	private static final File folder = new File(GameInfo.getPowerCraftFile(), "/crops");

	private static boolean cropsLoaded = false;

	/** hash table of crops - ID -> entry */
	private static List<PC_CropEntry> crops = new ArrayList<PC_CropEntry>();

	/**
	 * Start new crop entry (block)
	 * 
	 * @param id block id
	 * @param meta_mature meta when the crop is mature<br>
	 *            -1 = harvest with any meta
	 * @param meta_replant meta to replant this crop with.<br>
	 *            -1 = disable replanting.
	 */
	public void startCrop(List<CropBlockIDMetaInfo> crop) {

		if (new_started) {
			PC_Logger.severe("Crop manager - startCrop - Crop entry already started!");
		}

		new_crop = crop;
		new_started = true;
		new_entry = new PC_CropEntry();

		new_entry.setBlockInfo(new_crop);
	}


	/**
	 * Add dropped item to the currently constructed crop entry.
	 * 
	 * @param id item ID
	 * @param metaFrom lowest possible metadata
	 * @param metaTo highest possible metadata;<br>
	 *            For constant metadata set to the same value as metaFrom.
	 * @param countFrom lowest possible item count
	 * @param countTo highest possible item count;<br>
	 *            For constant item count, set to the same as countFrom.
	 * @param rarityNumenator rarity fraction numerator;<br>
	 *            For 1/200, set to 1<br>
	 * @param rarityDenominator rarity fraction denominator;<br>
	 *            For 1/200, set to 200<br>
	 *            If you want this item to drop each time, set to 1 (the same as
	 *            numenator)
	 * @param itemPriority priority pass<br>
	 *            When crop is harvested, items with priority 1 are checked
	 *            first. If nothing is dropped, items with priority 2 are
	 *            checked, then 3, 4 etc., until something is dropped. Highest
	 *            priority used can have rarity 1/1, and it will work as
	 *            fallback drop.
	 */
	public void addDropItem(int id, int metaFrom, int metaTo, int countFrom, int countTo, int rarityNumenator, int rarityDenominator, int itemPriority) {
		if (!new_started) {
			PC_Logger.severe("Crop manager - addDropItem - Crop entry not started!");
			return;
		}
		new_entry.addDropItem(id, metaFrom, metaTo, countFrom, countTo, rarityNumenator, rarityDenominator, itemPriority);
	}


	/**
	 * End and apply newly created crop entry.<br>
	 * Store the crop into the static Crops List.
	 */
	public void saveCrop() {
		if (!new_started) {
			PC_Logger.severe("Crop manager - endCrop - Crop entry not started!");
		}
		crops.add(new_entry);
		new_started = false;
		new_entry = null;
	}


	/**
	 * Cancel the currently created crop entry.
	 */
	public void cancelCrop() {
		if (!new_started) {
			PC_Logger.warning("Crop manager - cancelCrop - Crop entry not started!");
		}
		new_entry = null;
		new_started = false;
	}

	/**
	 * Check if this block is registered as crop.
	 * 
	 * @param block_id block id
	 * @return is a crop
	 */
	public static boolean isBlockRegisteredCrop(int block_id) {
		
		for(PC_CropEntry entry : crops){
			if(entry.isBlockRegisteredCrop(block_id))
				return true;
		}
		
		return false;
	}

	/**
	 * Check if this block can be harvested (is crop & is mature)
	 * 
	 * @param block_id block id
	 * @param block_meta block meta
	 * @return can harvest
	 */
	public static PC_CropEntry getHarvestBlockCrop(int block_id, int block_meta) {

		for(PC_CropEntry entry:crops){
			if(entry.canHarvestBlock(block_id, block_meta)){
				return entry;
			}
		}
		
		return null;
	}

	/**
	 * Call this method to explicitly init static fields -> list of crops from
	 * XML files
	 */
	public static void loadCrops() {
		if (cropsLoaded) {
			return;
		}

		PC_Logger.finer("Loading XML configuration for crops.");

		if (!folder.exists()) {
			folder.mkdirs();
		}

		if (!(new File(folder + "/default.xml")).exists()) {

			try {
				PC_Logger.finest("Generating default crops config in " + folder + "/default.xml");

				FileWriter out = new FileWriter(new File(folder + "/default.xml"));

				//@formatter:off
				// write the default crops
				out.write("<?xml version='1.1' encoding='UTF-8' ?>\n" + "<!-- \n" + "  BLOCK HARVESTER CONFIG FILE\n"
						+ "  You can add your own crops into this file.\n" + "  Any other xml files in this folder will be parsed too.\n\n"
						+ "  If you make a setup file for some mod, please post it on forums.\n\n" + "  Special values:\n"
						+ "    metaMature  = -1  ...  any metadata\n" + "    metaReplant = -1  ...  do not replant\n\n"
						+ "    Item meta   <  0  ...  get item with meta = blockMeta & abs(THIS_NUMBER) - useful for leaves\n\n"
						+ "  Item meta can be ranged - use 4-7 for random meta in range 4 to 7 (inclusive).\n"
						+ "  You can also use range for item count (eg. 0-5). \n\n"
						+ "  Higher rarity number means more rare. Use 1 for regular drops. \n"
						+ "-->\n\n"
						+ "<crops>\n"
						+ "\n"
						+ "\t<crop name='Wheat'>\n" 
						+ "\t\t<block id='59' metaMature='7' metaReplant='0' />\n"
						+ "\t\t<item id='296' meta='0' count='1' rarity='1' priority='1' /><!-- wheat -->\n"
						+ "\t\t<item id='295' meta='0' count='0-2' rarity='1' priority='1' /><!-- seeds -->\n"
						+ "\t</crop>\n"
						+ "\n"
						+ "\t<crop name='Carrot'>\n" 
						+ "\t\t<block id='141' metaMature='7' metaReplant='0' />\n"
						+ "\t\t<item id='391' meta='0' count='2' rarity='1' priority='1' /><!-- carrot -->\n"
						+ "\t</crop>\n"
						+ "\n"
						+ "\t<crop name='Potato'>\n" 
						+ "\t\t<block id='142' metaMature='7' metaReplant='0' />\n"
						+ "\t\t<item id='392' meta='0' count='2' rarity='1' priority='1' /><!-- potato -->\n"
						+ "\t</crop>\n"
						+ "\n"
						+ "\t<crop name='Cocoa'>\n" 
						+ "\t\t<block id='127' metaMature='8' metaReplant='0' />\n"
						+ "\t\t<block id='127' metaMature='9' metaReplant='1' />\n"
						+ "\t\t<block id='127' metaMature='10' metaReplant='2' />\n"
						+ "\t\t<block id='127' metaMature='11' metaReplant='3' />\n"
						+ "\t\t<block id='127' metaMature='12' metaReplant='0' />\n"
						+ "\t\t<block id='127' metaMature='13' metaReplant='1' />\n"
						+ "\t\t<block id='127' metaMature='14' metaReplant='2' />\n"
						+ "\t\t<block id='127' metaMature='15' metaReplant='3' />\n"
						+ "\t\t<item id='351' meta='3' count='2' rarity='1' priority='1' /><!-- cocoa -->\n"
						+ "\t</crop>\n"
						+ "\n"
						+ "\t<crop name='Nether Wart'>\n"
						+ "\t\t<block id='115' metaMature='3' metaReplant='0' />\n"
						+ "\t\t<item id='372' meta='0' count='2-5' rarity='1' priority='1' /><!-- wart seeds -->\n"
						+ "\t</crop>\n"
						+ "\n"
						+ "\t<crop name='Leaves'>\n"
						+ "\t\t<block id='18' metaMature='-1' metaReplant='-1' />\n"
						+ "\t\t<item id='260' meta='0'  count='1' rarity='1/200' priority='1' /><!-- apple -->\n"
						+ "\t\t<item id='6'   meta='-3' count='1' rarity='1/20'  priority='2' /><!-- sapling -->\n"
						+ "\t\t<item id='18'  meta='-3' count='1' rarity='1'   priority='3' /><!-- leaf -->\n"
						+ "\t</crop>\n"
						+ "\n"
						+ "</crops>");
				//@formatter:on

				out.close();

			} catch (IOException e) {
				PC_Logger.severe("Generating default crops config file failed due to an IOException.");
				e.printStackTrace();
			}

		}

		String[] files = folder.list(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.matches("[^.]+[.]xml");
			}
		});

		for (String filename : files) {

			PC_Logger.finest("* loading file " + filename + "...");
			File file = new File(folder + "/" + filename);
			parseFile(file);

		}

		PC_Logger.finer("Crops configuration loaded.");

		cropsLoaded = true;

	}

	/*
	 * <?xml version="1.0" encoding="UTF-8" ?>
	 * <crops>
	 * <crop name="My Crop">
	 * <block id="79" metaReplant="0" metaMature="7">
	 * <item id="318" meta="0" count="1-2" rarity="1">
	 * <item id="319" meta="1-5" count="1-2" rarity="4">
	 * </crop>
	 * </crops>
	 */

	/**
	 * Load and parse XML file with crops specs
	 * 
	 * @param file the file to load
	 */
	private static void parseFile(File file) {

		try {

			PCma_CropHarvestingManager manager = new PCma_CropHarvestingManager();

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);

			doc.getDocumentElement().normalize();

			NodeList cropsList = doc.getElementsByTagName("crop");

			croploop:
			for (int i = 0; i < cropsList.getLength(); i++) {

				Node cropNode = cropsList.item(i);
				if (cropNode.getNodeType() == Node.ELEMENT_NODE) {

					// process one crop entry

					Element crop = (Element) cropNode;

					// <block>
					NodeList blocks = crop.getElementsByTagName("block");

					// <item>
					NodeList items = crop.getElementsByTagName("item");
					if (blocks.getLength() < 1) {
						PC_Logger.warning("Crop manager - parseFile - Error while parsing " + file + " - no <item>s in <crop>");
						continue croploop;
					}

					int itemCount = items.getLength();
					
					List<CropBlockIDMetaInfo> cropList = new ArrayList<CropBlockIDMetaInfo>();
					
					for(int j=0; j<blocks.getLength(); j++){
					
						CropBlockIDMetaInfo c = new CropBlockIDMetaInfo();
						
						Element block = (Element) blocks.item(j);
	
						// <block attrs>
						String block_id_s = block.getAttribute("id");
	
						if (block_id_s.equals("") || !block_id_s.matches("[0-9]+")) {
							PC_Logger.warning("Crop manager - parseFile - Error while parsing " + file + " - bad block ID");
							continue croploop;
						}
	
						c.id = Integer.parseInt(block_id_s);
	
						String block_meta_replant_s = block.getAttribute("metaReplant");
	
						if (block_meta_replant_s.equals("") || !block_meta_replant_s.matches("[-]?[0-9]+")) {
							PC_Logger.warning("Crop manager - parseFile - Error while parsing " + file + " - bad replant meta");
							continue croploop;
						}
	
						c.meta_replant = Integer.parseInt(block_meta_replant_s);
	
						String block_meta_mature_s = block.getAttribute("metaMature");
	
						if (block_meta_mature_s.equals("") || !block_meta_mature_s.matches("[-]?[0-9]+")) {
							PC_Logger.warning("Crop manager - parseFile - Error while parsing " + file + " - bad mature meta");
							continue croploop;
						}
	
						c.meta = Integer.parseInt(block_meta_mature_s);
						
						cropList.add(c);
						
					}
					
					// store block
					manager.startCrop(cropList);

					itemloop:
					for (int j = 0; j < itemCount; j++) {

						try {
							int itemMetaA, itemMetaB, itemCountA, itemCountB, itemRarityA, itemRarityB, itemPriority, itemId;

							Element item = (Element) items.item(j);

							// id
							String item_id_s = item.getAttribute("id");

							if (item_id_s.equals("") || !item_id_s.matches("[0-9]+")) {
								PC_Logger.warning("Crop manager - parseFile - Error while parsing " + file + " - bad item ID");
								continue croploop;
							}

							itemId = Integer.parseInt(item_id_s);

							// priority
							String item_priority_s = item.getAttribute("priority");

							if (item_id_s.equals("")) {

								item_priority_s = "1";

							} else if (!item_id_s.matches("[0-9]+")) {
								PC_Logger.warning("Crop manager - parseFile - Error while parsing " + file + " - bad item ID");
								continue croploop;
							}

							itemPriority = Integer.parseInt(item_priority_s);

							// rarity 1/200
							String item_rarity_s = item.getAttribute("rarity");

							if (item_rarity_s.equals("")) {

								item_rarity_s = "1";

							}
							if (!item_rarity_s.matches("[0-9]+([/][0-9]+)?")) {
								PC_Logger.warning("Crop manager - parseFile - Error while parsing " + file + " - bad item rarity");
								continue croploop;
							}

							String[] item_rarity_parts = item_rarity_s.split("/");

							if (item_rarity_parts.length == 1) {
								itemRarityA = 1;
								itemRarityB = Integer.parseInt(item_rarity_parts[0]);
							} else {
								itemRarityA = Integer.parseInt(item_rarity_parts[0]);
								itemRarityB = Integer.parseInt(item_rarity_parts[1]);

								if (itemRarityA > itemRarityB) {
									itemRarityA = itemRarityB = 1;
								}
							}

							// meta start-stop
							String item_meta_s = item.getAttribute("meta");

							if (item_meta_s.equals("")) {
								item_meta_s = "0";
							} else if (!item_meta_s.matches("[-]?[0-9]+") && !item_meta_s.matches("[0-9]+[-][0-9]+")) {
								PC_Logger.warning("Crop manager - parseFile - Error while parsing " + file + " - bad item meta");
								continue croploop;
							}

							String[] item_meta_parts;

							if (item_meta_s.matches("[-]?[0-9]+")) {
								item_meta_parts = new String[1];
								item_meta_parts[0] = item_meta_s;

							} else {
								item_meta_parts = item_meta_s.split("-");
							}

							if (item_meta_parts.length == 1) {
								itemMetaA = itemMetaB = Integer.parseInt(item_meta_parts[0]);
							} else {
								itemMetaA = Integer.parseInt(item_meta_parts[0]);
								itemMetaB = Integer.parseInt(item_meta_parts[1]);

								if (itemMetaB < itemMetaA) {
									itemMetaB = itemMetaA;
								}
							}

							// cout start-stop
							String item_count_s = item.getAttribute("count");

							if (item_count_s.equals("")) {

								item_count_s = "1";

							} else if (!item_count_s.matches("[0-9]+(-[0-9]+)?")) {
								PC_Logger.warning("Crop manager - parseFile - Error while parsing " + file + " - bad item count");
								continue croploop;
							}

							String[] item_count_parts = item_count_s.split("-");

							if (item_count_parts.length == 1) {
								itemCountA = itemCountB = Integer.parseInt(item_count_parts[0]);
							} else {
								itemCountA = Integer.parseInt(item_count_parts[0]);
								itemCountB = Integer.parseInt(item_count_parts[1]);

								if (itemCountB < itemCountA) {
									itemCountB = itemCountA;
								}
							}

							manager.addDropItem(itemId, itemMetaA, itemMetaB, itemCountA, itemCountB, itemRarityA, itemRarityB, itemPriority);

						} catch (NumberFormatException e) {
							continue itemloop;
						}

					}

					PC_Logger.finest("   - Loaded crop \"" + crop.getAttribute("name") + "\".");

					manager.saveCrop();

				}

			}

		} catch (SAXParseException err) {
			PC_Logger.severe("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
			PC_Logger.severe(" " + err.getMessage());
		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}


	/**
	 * Crop list entry
	 * 
	 * @author MightyPork
	 */
	public class PC_CropEntry {

		private List<CropBlockIDMetaInfo> crop;

		// each added item creates one entry in all of these lists.
		private ArrayList<Integer> itemId = new ArrayList<Integer>();
		private ArrayList<Integer> metaStart = new ArrayList<Integer>();
		private ArrayList<Integer> metaEnd = new ArrayList<Integer>();
		private ArrayList<Integer> countStart = new ArrayList<Integer>();
		private ArrayList<Integer> countEnd = new ArrayList<Integer>();
		private ArrayList<Integer> rarityNum = new ArrayList<Integer>();
		private ArrayList<Integer> rarityDenom = new ArrayList<Integer>();
		private ArrayList<Integer> priority = new ArrayList<Integer>();

		private int itemsCount = 0;
		private Random rand = new Random();

		/**
		 * set block to harvest
		 * 
		 * @param id block id
		 * @param metadataMature metadata of block ready to be harvested
		 * @param metadataReplant metadata of replanted block (the seeds block)
		 */
		public void setBlockInfo(List<CropBlockIDMetaInfo> crop) {
			this.crop = crop;
		}

		public boolean isBlockRegisteredCrop(int block_id) {
			for(CropBlockIDMetaInfo c:crop){
				if(c.id == block_id){
					return true;
				}
			}
			return false;
		}

		/**
		 * Add dropped item
		 * 
		 * @param id item ID
		 * @param metaFrom lowest possible metadata
		 * @param metaTo highest possible metadata
		 * @param countFrom lowest possible item count
		 * @param countTo highest possible item count
		 * @param rarityNumenator rarity fraction numerator; A in A/B
		 * @param rarityDenominator rarity fraction denominator; B in A/B
		 * @param itemPriority priority pass
		 */
		public void addDropItem(int id, int metaFrom, int metaTo, int countFrom, int countTo, int rarityNumenator, int rarityDenominator,
				int itemPriority) {

			itemId.add(id);
			metaStart.add(metaFrom);
			metaEnd.add(metaTo);
			countStart.add(countFrom);
			countEnd.add(countTo);
			rarityNum.add(rarityNumenator);
			rarityDenom.add(rarityDenominator);
			priority.add(itemPriority);

			itemsCount++;

		}

		/**
		 * Checks if this entry can harvest given block with given metadata
		 * 
		 * @param id block ID
		 * @param meta block METADATA
		 * @return true if can harvest
		 */
		public boolean canHarvestBlock(int id, int meta) {
			for(CropBlockIDMetaInfo c:crop){
				if(c.id == id && (c.meta == meta || c.meta < 0)){
					return true;
				}
			}
			return false;
		}

		/**
		 * Get list of stacks obtained by harvesting certain block.
		 * 
		 * @param id block ID
		 * @param meta block META
		 * @return ItemStack[] the stacks harvested, or null.
		 */
		public ItemStack[] getHarvestedStacks(int id, int meta) {
			ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();

			for (int priorityTurn = 1; priorityTurn < 20; priorityTurn++) {

				int itemsOfPriority = 0;
				int itemsDropped = 0;

				for (int i = 0; i < itemsCount; i++) {
					if (priority.get(i) == priorityTurn) {

						itemsOfPriority++;

						if (rarityDenom.get(i) > 0 && rand.nextInt(rarityDenom.get(i)) < rarityNum.get(i)) {

							int stackMeta;

							if (metaStart.get(i) < 0) {
								stackMeta = meta & (-metaStart.get(i));
							} else {
								stackMeta = metaStart.get(i) + rand.nextInt(metaEnd.get(i) - metaStart.get(i) + 1);
							}

							int stackCount = countStart.get(i) + rand.nextInt(countEnd.get(i) - countStart.get(i) + 1);

							if (stackMeta >= 0 && stackMeta < 32000 && stackCount > 0 && Item.itemsList[itemId.get(i)] != null) {

								stacks.add(new ItemStack(itemId.get(i), stackCount, stackMeta));
								itemsDropped++;

							}
						}

					}

				}

				if (itemsOfPriority == 0) {
					break;
				}

				if (itemsOfPriority > 0 && itemsDropped > 0) {
					break;
				}

			}

			if (stacks.size() == 0) {
				return null;
			}

			return Inventory.groupStacks(stacks.toArray(new ItemStack[stacks.size()]));

		}

		/**
		 * Metadata for the replanted block.
		 * 
		 * @return int (metadata), or -1 if the block should be removed
		 */
		public int getReplantMetadata(int id, int meta) {
			for(CropBlockIDMetaInfo c:crop){
				if(c.id == id && (c.meta == meta || c.meta < 0)){
					return c.meta_replant;
				}
			}
			return -1;
		}

	}

}

