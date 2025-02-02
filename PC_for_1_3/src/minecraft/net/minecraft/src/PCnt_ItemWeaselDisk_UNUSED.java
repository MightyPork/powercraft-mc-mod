package net.minecraft.src;


import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


import weasel.Calc;
import weasel.exception.WeaselRuntimeException;
import weasel.lang.Instruction;
import weasel.obj.WeaselInteger;
import weasel.obj.WeaselObject;
import weasel.obj.WeaselVariableMap;


/**
 * Disk for weasel
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
public class PCnt_ItemWeaselDisk_UNUSED extends Item {

	@SuppressWarnings("javadoc")
	public static final int EMPTY = 0, TEXT = 1, IMAGE = 2, NUMBERLIST = 3, STRINGLIST = 4, VARMAP = 5, LIBRARY = 6;

	/** bg texture */
	public int textureBg;
	/** fg (label) texture */
	public int textureFg;

	/**
	 * @param i ID
	 */
	public PCnt_ItemWeaselDisk_UNUSED(int i) {
		super(i);
		setMaxDamage(0);
		setMaxStackSize(1);
		setHasSubtypes(true);
	}

	@Override
	public void onCreated(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		checkTag(itemstack);
	}

	/**
	 * Get type label for display, localized.
	 * 
	 * @param itemstack disk
	 * @return label
	 */
	public static String getTypeVerbose(ItemStack itemstack) {
		checkTag(itemstack);
		switch (itemstack.getTagCompound().getInteger("Type")) {
			case EMPTY:
				return PC_Lang.tr("pc.weasel.disk.empty");
			case TEXT:
				return PC_Lang.tr("pc.weasel.disk.text");
			case IMAGE:
				return PC_Lang.tr("pc.weasel.disk.image");
			case NUMBERLIST:
				return PC_Lang.tr("pc.weasel.disk.numberList");
			case STRINGLIST:
				return PC_Lang.tr("pc.weasel.disk.stringList");
			case VARMAP:
				return PC_Lang.tr("pc.weasel.disk.variableMap");
			case LIBRARY:
				return PC_Lang.tr("pc.weasel.disk.programLibrary");
		}
		return "FAILED DISK";
	}


	@Override
	public void addInformation(ItemStack itemstack, List list) {
		checkTag(itemstack);
		list.add(itemstack.getTagCompound().getString("Label"));
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		return getTypeVerbose(itemstack);
	}

	/**
	 * Erase all disk data, only preserve label, set type to EMPTY
	 * 
	 * @param itemstack
	 */
	public static void eraseDisk(ItemStack itemstack) {
		checkTag(itemstack);
		String label = getLabel(itemstack);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("Label", label);
		tag.setInteger("Type", EMPTY);
		itemstack.setTagCompound(tag);
	}

	/**
	 * Format disk to given type.
	 * 
	 * @param itemstack
	 * @param type type, constants from PClo_ItemWeaselDisk
	 */
	public static void formatDisk(ItemStack itemstack, int type) {
		checkTag(itemstack);
		eraseDisk(itemstack);
		NBTTagCompound tag = itemstack.getTagCompound();
		setType(itemstack, type);
		switch (type) {
			case EMPTY:
				return;

			case TEXT:
				tag.setString("Text", "");
				return;

			case IMAGE:
				int[][] img = new int[8][8];
				for (int i = 0; i < 8; i++)
					Arrays.fill(img[i], -1);

				setImageData(itemstack, img);
				return;

			case LIBRARY:
				tag.setInteger("Size", 0);
				NBTTagCompound vals = new NBTTagCompound();
				tag.setCompoundTag("Data", vals);
				return;

			case STRINGLIST:
			case NUMBERLIST:
				tag.setString("ListData", "");
				tag.setString("ListDelimiter", ",");
				return;

			case VARMAP:
				PC_Utils.saveToNBT(tag, "Map", new WeaselVariableMap());
				return;
		}
	}

	/**
	 * Get library disk number of isntrs
	 * 
	 * @param itemstack image disk
	 * @return size
	 */
	public static int getLibrarySize(ItemStack itemstack) {
		checkTag(itemstack);
		if (getType(itemstack) != LIBRARY) throw new WeaselRuntimeException("Library function called on " + getTypeVerbose(itemstack) + " disk.");
		return itemstack.getTagCompound().getInteger("Size");
	}

	/**
	 * Get library instructions
	 * 
	 * @param itemstack image disk
	 * @return size
	 */
	public static List<Instruction> getLibraryInstructions(ItemStack itemstack) {
		checkTag(itemstack);
		if (getType(itemstack) != LIBRARY) throw new WeaselRuntimeException("Library function called on " + getTypeVerbose(itemstack) + " disk.");
		int length = itemstack.getTagCompound().getInteger("Size");
		if (length == 0) return new ArrayList<Instruction>(0);

		NBTTagCompound tag = itemstack.getTagCompound();

		List<Instruction> list = new ArrayList<Instruction>();

		NBTTagCompound dataTag = tag.getCompoundTag("Data");
		for (int i = 0; i < length; i++) {
			list.add(Instruction.loadInstructionFromNBT(dataTag.getCompoundTag("i" + i)));
		}

		return list;
	}

	/**
	 * Set library instructions
	 * 
	 * @param itemstack image disk
	 * @param list list of instructions obtained by compiling source code.
	 */
	public static void setLibraryInstructions(ItemStack itemstack, List<Instruction> list) {
		checkTag(itemstack);
		if (getType(itemstack) != LIBRARY) throw new WeaselRuntimeException("Library function called on " + getTypeVerbose(itemstack) + " disk.");
		itemstack.getTagCompound().setInteger("Size", list.size());


		NBTTagCompound tag = itemstack.getTagCompound();

		NBTTagCompound dataTag = new NBTTagCompound();
		for (int i = 0; i < list.size(); i++) {
			dataTag.setCompoundTag("i" + i, Instruction.saveInstructionToNBT(list.get(i), new NBTTagCompound()));
		}
		tag.setCompoundTag("Data", dataTag);
	}

	/**
	 * Set library source
	 * 
	 * @param itemstack image disk
	 * @param source source code
	 */
	public static void setLibrarySource(ItemStack itemstack, String source) {
		checkTag(itemstack);
		if (getType(itemstack) != LIBRARY) throw new WeaselRuntimeException("Library function called on " + getTypeVerbose(itemstack) + " disk.");
		itemstack.getTagCompound().setString("Source", source);
	}

	/**
	 * get library source
	 * 
	 * @param itemstack image disk
	 * @return source
	 */
	public static String getLibrarySource(ItemStack itemstack) {
		checkTag(itemstack);
		if (getType(itemstack) != LIBRARY) throw new WeaselRuntimeException("Library function called on " + getTypeVerbose(itemstack) + " disk.");
		return itemstack.getTagCompound().getString("Source");
	}



	/**
	 * Get image disk size
	 * 
	 * @param itemstack image disk
	 * @return size
	 */
	public static PC_CoordI getImageSize(ItemStack itemstack) {
		checkTag(itemstack);
		if (getType(itemstack) != IMAGE) throw new WeaselRuntimeException("Image function called on " + getTypeVerbose(itemstack) + " disk.");
		return new PC_CoordI().readFromNBT(itemstack.getTagCompound().getCompoundTag("Size"));
	}

	/**
	 * Set image disk size
	 * 
	 * @param itemstack image disk
	 * @param size size to set
	 */
	public static void setImageSize(ItemStack itemstack, PC_CoordI size) {
		checkTag(itemstack);
		if (getType(itemstack) != IMAGE) throw new WeaselRuntimeException("Image function called on " + getTypeVerbose(itemstack) + " disk.");

		itemstack.getTagCompound().setCompoundTag("Size", new PC_CoordI(size.x, size.y).writeToNBT(new NBTTagCompound()));
	}

	/**
	 * Get image color at coord
	 * 
	 * @param itemstack image disk
	 * @param pos coord
	 * @return color hex rgb
	 */
	public static int getImageColorAt(ItemStack itemstack, PC_CoordI pos) {
		checkTag(itemstack);
		if (getType(itemstack) != IMAGE) throw new WeaselRuntimeException("Image function called on " + getTypeVerbose(itemstack) + " disk.");

		int[][] data = getImageData(itemstack);
		return data[pos.x][pos.y];
	}

	/**
	 * Set color of pixel at coord
	 * 
	 * @param itemstack image disk
	 * @param pos coord
	 * @param color color to set
	 */
	public static void setImageColorAt(ItemStack itemstack, PC_CoordI pos, int color) {
		checkTag(itemstack);
		if (getType(itemstack) != IMAGE) throw new WeaselRuntimeException("Image function called on " + getTypeVerbose(itemstack) + " disk.");

		PC_CoordI size = getImageSize(itemstack);
		if (pos.x < 0 || pos.y < 0 || pos.x > size.x || pos.y > size.y) throw new WeaselRuntimeException("Image Disk: coordinate out of range");

		int[][] data = getImageData(itemstack);
		data[pos.x][pos.y] = color;
		setImageData(itemstack, data);
	}

	/**
	 * Set image array and resize if needed
	 * 
	 * @param itemstack image disk
	 * @param data data to set - array of rgb ints or -1
	 */
	public static void setImageData(ItemStack itemstack, int[][] data) {
		checkTag(itemstack);
		if (getType(itemstack) != IMAGE) throw new WeaselRuntimeException("Image function called on " + getTypeVerbose(itemstack) + " disk.");

		NBTTagCompound tag = itemstack.getTagCompound();

		tag.setCompoundTag("Size", new PC_CoordI(data.length, data[0].length).writeToNBT(new NBTTagCompound()));

		NBTTagCompound dataTag = new NBTTagCompound();

		for (int x = 0; x < data.length; x++) {
			String name = "r"+Integer.toHexString(x);
			dataTag.setByteArray(name, int2byte(data[x]));
		}
		tag.setCompoundTag("Data", dataTag);
	}

	private static byte[] int2byte(int[] src) {
		int srcLength = src.length;
		byte[] dst = new byte[srcLength << 2];

		for (int i = 0; i < srcLength; i++) {
			int x = src[i];
			int j = i << 2;
			dst[j++] = (byte) ((x >>> 0) & 0xff);
			dst[j++] = (byte) ((x >>> 8) & 0xff);
			dst[j++] = (byte) ((x >>> 16) & 0xff);
			dst[j++] = (byte) ((x >>> 24) & 0xff);
		}
		return dst;
	}

	private static int[] byte2int(byte[] src) {
		int dstLength = src.length >>> 2;
		int[] dst = new int[dstLength];

		for (int i = 0; i < dstLength; i++) {
			int j = i << 2;
			int x = 0;
			x += (src[j++] & 0xff) << 0;
			x += (src[j++] & 0xff) << 8;
			x += (src[j++] & 0xff) << 16;
			x += (src[j++] & 0xff) << 24;
			dst[i] = x;
		}
		return dst;
	}

	/**
	 * Get image array
	 * 
	 * @param itemstack image disk
	 * @return array of rgb ints or -1
	 */
	public static int[][] getImageData(ItemStack itemstack) {
		if (itemstack == null) return null;
		checkTag(itemstack);
		if (getType(itemstack) != IMAGE) throw new WeaselRuntimeException("Image function called on " + getTypeVerbose(itemstack) + " disk.");

		NBTTagCompound tag = itemstack.getTagCompound();

		PC_CoordI size = getImageSize(itemstack);

		int[][] data = new int[size.x][size.y];

		NBTTagCompound dataTag = tag.getCompoundTag("Data");

		for (int x = 0; x < data.length; x++) {
			data[x] = byte2int(dataTag.getByteArray("r"+Integer.toHexString(x)));
		}
			
		return data;
	}



	/**
	 * Get text
	 * 
	 * @param itemstack
	 * @return text
	 */
	public static String getText(ItemStack itemstack) {
		checkTag(itemstack);
		if (itemstack.getTagCompound().getInteger("Type") != TEXT)
			throw new WeaselRuntimeException("Text function called on " + getTypeVerbose(itemstack) + " disk.");
		return itemstack.getTagCompound().getString("Text");
	}

	/**
	 * Set text
	 * 
	 * @param itemstack
	 * @param text text
	 */
	public static void setText(ItemStack itemstack, String text) {
		checkTag(itemstack);
		if (getType(itemstack) != TEXT) throw new WeaselRuntimeException("Text function called on " + getTypeVerbose(itemstack) + " disk.");
		itemstack.getTagCompound().setString("Text", text);
	}

	/**
	 * Set varmap value
	 * 
	 * @param itemstack
	 * @param name var name
	 * @param value var value
	 */
	public static void setMapVariable(ItemStack itemstack, String name, WeaselObject value) {
		checkTag(itemstack);
		if (getType(itemstack) != VARMAP) throw new WeaselRuntimeException("VarMap function called on " + getTypeVerbose(itemstack) + " disk.");

		WeaselVariableMap map = (WeaselVariableMap) PC_Utils.loadFromNBT(itemstack.getTagCompound(), "Map", new WeaselVariableMap());
		map.setVariableForce(name, value);
		PC_Utils.saveToNBT(itemstack.getTagCompound(), "Map", map);
	}

	/**
	 * Get varmap value
	 * 
	 * @param itemstack
	 * @param name var name
	 * @return value found
	 */
	public static WeaselObject getMapVariable(ItemStack itemstack, String name) {
		checkTag(itemstack);
		if (getType(itemstack) != VARMAP) throw new WeaselRuntimeException("VarMap function called on " + getTypeVerbose(itemstack) + " disk.");

		WeaselVariableMap map = (WeaselVariableMap) PC_Utils.loadFromNBT(itemstack.getTagCompound(), "Map", new WeaselVariableMap());

		return map.getVariable(name);
	}

	/**
	 * has varmap value
	 * 
	 * @param itemstack
	 * @param name var name
	 * @return has this key
	 */
	public static boolean hasMapVariable(ItemStack itemstack, String name) {
		checkTag(itemstack);
		if (getType(itemstack) != VARMAP) throw new WeaselRuntimeException("VarMap function called on " + getTypeVerbose(itemstack) + " disk.");

		WeaselVariableMap map = (WeaselVariableMap) PC_Utils.loadFromNBT(itemstack.getTagCompound(), "Map", new WeaselVariableMap());
		
		return (map.getVariable(name) != null);
	}

	/**
	 * Remove varmap value
	 * 
	 * @param itemstack
	 * @param name var name
	 */
	public static void removeMapVariable(ItemStack itemstack, String name) {
		checkTag(itemstack);
		if (getType(itemstack) != VARMAP) throw new WeaselRuntimeException("VarMap function called on " + getTypeVerbose(itemstack) + " disk.");

		WeaselVariableMap map = (WeaselVariableMap) PC_Utils.loadFromNBT(itemstack.getTagCompound(), "Map", new WeaselVariableMap());
		map.unsetVariable(name);
		PC_Utils.saveToNBT(itemstack.getTagCompound(), "Map", map);
		
	}



	/**
	 * erase varmap
	 * 
	 * @param itemstack image disk
	 */
	public static void eraseVarMap(ItemStack itemstack) {
		checkTag(itemstack);
		if (getType(itemstack) != VARMAP) throw new WeaselRuntimeException("VarMap function called on " + getTypeVerbose(itemstack) + " disk.");

		WeaselVariableMap map = (WeaselVariableMap) PC_Utils.loadFromNBT(itemstack.getTagCompound(), "Map", new WeaselVariableMap());
		map.clear();
		PC_Utils.saveToNBT(itemstack.getTagCompound(), "Map", map);
		
	}

	/**
	 * get varmap
	 * 
	 * @param itemstack image disk
	 * @return the tag
	 */
	public static WeaselVariableMap getVarMapMap(ItemStack itemstack) {
		checkTag(itemstack);
		if (getType(itemstack) != VARMAP) throw new WeaselRuntimeException("VarMap function called on " + getTypeVerbose(itemstack) + " disk.");

		return (WeaselVariableMap) PC_Utils.loadFromNBT(itemstack.getTagCompound(), "Map", new WeaselVariableMap());
	}


	// list manipulation

	/**
	 * Get list - array of strings. You must trim thyem and try to convert to
	 * correct data type
	 * 
	 * @param itemstack stack
	 * @return strings
	 */
	public static String[] getListEntries(ItemStack itemstack) {
		checkTag(itemstack);
		int type = getType(itemstack);
		if (type != NUMBERLIST && type != STRINGLIST)
			throw new WeaselRuntimeException("List function called on " + getTypeVerbose(itemstack) + " disk.");
		return itemstack.getTagCompound().getString("ListData")
				.split(Pattern.quote(itemstack.getTagCompound().getString("ListDelimiter").replace("\\n", "\n")));
	}

	/**
	 * Get length of a list on disk
	 * 
	 * @param itemstack list disk
	 * @return length
	 */
	public static int getListLength(ItemStack itemstack) {
		checkTag(itemstack);
		int type = getType(itemstack);
		if (type != NUMBERLIST && type != STRINGLIST)
			throw new WeaselRuntimeException("List function called on " + getTypeVerbose(itemstack) + " disk.");
		return getListEntries(itemstack).length;
	}

	/**
	 * Get list entry at index 0 based
	 * 
	 * @param itemstack list disk
	 * @param entry entry index
	 * @return entry value converted to string or integer
	 */
	public static WeaselObject getListEntry(ItemStack itemstack, int entry) {
		checkTag(itemstack);
		int type = getType(itemstack);
		if (type != NUMBERLIST && type != STRINGLIST)
			throw new WeaselRuntimeException("List function called on " + getTypeVerbose(itemstack) + " disk.");

		int size = getListLength(itemstack);
		if (entry < 0 || entry >= size) throw new WeaselRuntimeException("List " + getLabel(itemstack) + ": index " + entry + " out of bounds.");

		String str = getListEntries(itemstack)[entry];
		if (getType(itemstack) == NUMBERLIST) {
			return new WeaselInteger(Calc.toInteger(str));
		}
		if (getType(itemstack) == STRINGLIST) {
			return new WeaselInteger(Calc.toString(str));
		}

		return null;
	}

	/**
	 * Get list raw string with delimiter-separated entries
	 * 
	 * @param itemstack stack
	 * @return raw list
	 */
	public static String getListText(ItemStack itemstack) {
		checkTag(itemstack);
		int type = getType(itemstack);
		if (type != NUMBERLIST && type != STRINGLIST)
			throw new WeaselRuntimeException("List function called on " + getTypeVerbose(itemstack) + " disk.");
		return itemstack.getTagCompound().getString("ListData");
	}

	/**
	 * Get list delimiter, usually ","
	 * 
	 * @param itemstack stack
	 * @return delimiter
	 */
	public static String getListDelimiter(ItemStack itemstack) {
		checkTag(itemstack);
		int type = getType(itemstack);
		if (type != NUMBERLIST && type != STRINGLIST)
			throw new WeaselRuntimeException("List function called on " + getTypeVerbose(itemstack) + " disk.");
		return itemstack.getTagCompound().getString("ListDelimiter");
	}

	/**
	 * Set list - string with delimiter-separated entries
	 * 
	 * @param itemstack
	 * @param listtext "entry,entry,entry"
	 * @param delimiter ","
	 */
	public static void setListText(ItemStack itemstack, String listtext, String delimiter) {
		checkTag(itemstack);
		int type = getType(itemstack);
		if (type != NUMBERLIST && type != STRINGLIST)
			throw new WeaselRuntimeException("List function called on " + getTypeVerbose(itemstack) + " disk.");
		itemstack.getTagCompound().setString("ListData", listtext);
		itemstack.getTagCompound().setString("ListDelimiter", delimiter);
	}

	/**
	 * Get disk type
	 * 
	 * @param itemstack
	 * @return type index
	 */
	public static int getType(ItemStack itemstack) {
		checkTag(itemstack);
		return itemstack.getTagCompound().getInteger("Type");
	}

	/**
	 * Set disk type (does not format)
	 * 
	 * @param itemstack disk
	 * @param type type index
	 */
	public static void setType(ItemStack itemstack, int type) {
		checkTag(itemstack);
		itemstack.getTagCompound().setInteger("Type", type);
	}

	/**
	 * Check tag, create if missing
	 * 
	 * @param stack disk stack
	 */
	public static void checkTag(ItemStack stack) {
		if (stack == null) return;
		if (stack.hasTagCompound()) {
			if (!stack.getTagCompound().hasKey("Type")) {
				stack.getTagCompound().setInteger("Type", EMPTY);
			}
			if (!stack.getTagCompound().hasKey("Label")) {
				stack.getTagCompound().setString("Label", PC_Lang.tr("pc.weasel.disk.new_label"));
			}
			return;
		} else {
			stack.setTagCompound(new NBTTagCompound());
			checkTag(stack);
		}
	}



	// set drive label

	/**
	 * Get drive label
	 * 
	 * @param itemstack
	 * @return the label
	 */
	public static String getLabel(ItemStack itemstack) {
		checkTag(itemstack);
		return itemstack.getTagCompound().getString("Label");
	}

	/**
	 * Set drive label
	 * 
	 * @param itemstack
	 * @param text the label
	 */
	public static void setLabel(ItemStack itemstack, String text) {
		checkTag(itemstack);
		itemstack.getTagCompound().setString("Label", text);
	}



	// manipulate the itemstack color

	/**
	 * Get color - internally stored as 0xFFF = 0xF0F0F0
	 * 
	 * @param itemstack
	 * @return color hex
	 */
	public static int getColor(ItemStack itemstack) {
		return makeHexFromDamage(itemstack.getItemDamage());
	}

	/**
	 * Set color
	 * 
	 * @param itemstack
	 * @param hexColor color hex
	 */
	public static void setColor(ItemStack itemstack, int hexColor) {
		itemstack.setItemDamage(makeDamageFromHex(hexColor));
	}



	/**
	 * Get texture from damage and pass
	 * 
	 * @param dmg damage
	 * @param pass pass 0-1
	 */
	@Override
	public int getIconFromDamageForRenderPass(int dmg, int pass) {
		return pass == 0 ? textureBg : textureFg;
	}

	/**
	 * render double pass
	 */
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	/**
	 * make true hex color from damage
	 * 
	 * @param dmg damage
	 * @return hex
	 */
	public static int makeHexFromDamage(int dmg) {
		int color = dmg & 0xFFF;
		int r = color >> 8 & 0xF;
		int g = color >> 4 & 0xF;
		int b = color & 0xF;
		return r << 20 | g << 12 | b << 4 * 1;
	}

	/**
	 * Convert full hex to damage (0xF0F0F0 -> 0xFFF)
	 * 
	 * @param hex hex color
	 * @return damage
	 */
	public static int makeDamageFromHex(int hex) {
		int color = hex & 0xF0F0F0;
		int r = color >> 20 & 0xF;
		int g = color >> 12 & 0xF;
		int b = color >> 4 & 0xF;
		return r << 8 | g << 4 | b;
	}


	@Override
	public int getColorFromDamage(int dmg, int pass) {
		if (pass == 0) return 0xffffff;
		return makeHexFromDamage(dmg);
	}
}
