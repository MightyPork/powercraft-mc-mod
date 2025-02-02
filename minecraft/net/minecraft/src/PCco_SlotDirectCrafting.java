package net.minecraft.src;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.lwjgl.input.Keyboard;


/**
 * Direct Crafting slot, used in Crafting Tool.<br>
 * "No matter HOW it works. Just use it."
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
public class PCco_SlotDirectCrafting extends Slot implements PC_ISlotWithBackground {

	/** Enable recursion when crafting */
	public static boolean recursiveCrafting = true;

	/** Enable cheating in survival */
	public static boolean survivalCheating = false;

	private EntityPlayer thePlayer;
	/** Stack craftable from this slot. Read only, please. */
	public ItemStack product;
	private static final int RECURSION_LIMIT = 50;

	private boolean available = false;

	/**
	 * @param entityplayer the Player
	 * @param product product stack (Must be in the same size as crafted from
	 *            the recipe!)
	 * @param index slot index
	 * @param x slot position X
	 * @param y slot position Y
	 */
	public PCco_SlotDirectCrafting(EntityPlayer entityplayer, ItemStack product, int index, int x, int y) {
		super(null, index, x, y);
		thePlayer = entityplayer;
		this.product = product;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return false;
	}

	@Override
	public void onPickupFromSlot(ItemStack itemstack) {
		// itemstack.onCrafting(thePlayer.worldObj, thePlayer);
		super.onPickupFromSlot(itemstack);

		doCrafting();
		updateAvailability();
	}

	@Override
	public ItemStack decrStackSize(int i) {
		if (available) {
			ItemStack output = product.copy();
			if ((PC_Utils.isCreative() || survivalCheating)
					&& (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))) {
				output.stackSize = output.getMaxStackSize();
			}
			return output;
		}
		return null;
	}

	@Override
	public ItemStack getStack() {
		if (available && product != null) {
			ItemStack output = product.copy();
			if ((PC_Utils.isCreative() || survivalCheating)
					&& (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))) {
				output.stackSize = output.getMaxStackSize();
			}
			return output;
		}
		return null;
	}

	@Override
	public void putStack(ItemStack itemstack) {}

	/**
	 * Set slot's product (rendered as "ghost" item if not available)
	 * 
	 * @param itemstack
	 */
	public void setProduct(ItemStack itemstack) {
		product = itemstack;
	}

	@Override
	public void onSlotChanged() {}

	@Override
	public int getSlotStackLimit() {
		if (product == null) {
			return 1;
		}
		return product.getMaxStackSize();
	}

	/**
	 * Check if the PRODUCT is available for crafting
	 * 
	 * @return true if the item can be crafted
	 */
	private boolean isAvailable() {
		if (product == null) {
			return false;
		}

		if (ModLoader.getMinecraftInstance().playerController.isInCreativeMode() || survivalCheating) {
			return true;
		}

		lastRecipe = -1;
		// local recipe counter
		int recipe_number = -1;
		while (true) {
			lastRecipe = recipe_number;
			IRecipe irecipe = getNextRecipeForProduct(product);
			recipe_number = lastRecipe;

			if (irecipe == null) {
				return false;
			}
			recursionCount = 0;
			allocatedStacks.clear();
			toConsume.clear();
			toGiveBack.clear();
			stacksSeekedInThisRecursion.clear();
			recipeBannedStacks.clear();
			recipeBannedStacks.add(getStackDescriptor(product));
			recipeBannedStacks.add(getStackDescriptorAnyMeta(product));
			if (tryToFindMaterialsForRecipe(irecipe)) {
				if (toConsume.size() == 1 && toConsume.get(0).isItemEqual(product)
						|| (toConsume.get(0).itemID == product.itemID && toConsume.get(0).getItemDamage() == -1)) {
					continue; // next recipe
				}
				return true;
			}
		}
	}

	/**
	 * Craft the product and consume needed items from inventory
	 */
	private void doCrafting() {
		if (product == null) {
			return;
		}

		if (ModLoader.getMinecraftInstance().playerController.isInCreativeMode() || survivalCheating) {
			return;
		}

		lastRecipe = -1;
		// local recipe counter
		int recipe_number = -1;

		while (true) {

			lastRecipe = recipe_number;
			IRecipe irecipe = getNextRecipeForProduct(product);
			recipe_number = lastRecipe;

			if (irecipe == null) {
				return;
			}

			recursionCount = 0;
			allocatedStacks.clear();
			toConsume.clear();
			toGiveBack.clear();
			stacksSeekedInThisRecursion.clear();
			recipeBannedStacks.clear();
			recipeBannedStacks.add(getStackDescriptor(product));
			recipeBannedStacks.add(getStackDescriptorAnyMeta(product));
			if (tryToFindMaterialsForRecipe(irecipe)) {
				if (toConsume.size() == 1 && toConsume.get(0).isItemEqual(product)
						|| (toConsume.get(0).itemID == product.itemID && toConsume.get(0).getItemDamage() == -1)) {
					continue; // next recipe
				}

				for (ItemStack stack : toConsume) {
					consumePlayerItems(stack, stack.stackSize);
				}

				if (irecipe.getRecipeOutput().stackSize > product.stackSize) {
					toGiveBack.add(new ItemStack(irecipe.getRecipeOutput().itemID, irecipe.getRecipeOutput().stackSize - product.stackSize, product
							.getItemDamage()));
				}

				for (ItemStack stack : toGiveBack) {
					thePlayer.inventory.addItemStackToInventory(stack);
					if (stack.stackSize > 0) {
						thePlayer.dropPlayerItem(stack);
					}
				}

				return;
			}
		}
	}

	/**
	 * Get stack descriptor (item name@damage)
	 * 
	 * @param stack the stack
	 * @return descriptor
	 */
	private String getStackDescriptor(ItemStack stack) {
		return Item.itemsList[stack.itemID].getItemName() + "@" + stack.getItemDamage();
	}

	/**
	 * Get any-meta stack descriptor (item name@-1)
	 * 
	 * @param stack the stack
	 * @return descriptor with meta ignored
	 */
	private String getStackDescriptorAnyMeta(ItemStack stack) {
		return Item.itemsList[stack.itemID].getItemName() + "@-1";
	}

	/** List of stacks allocated during the recipe sequence finding process */
	private Hashtable<String, Integer> allocatedStacks = new Hashtable<String, Integer>();

	/**
	 * Try to count and allocated given amount of player's items
	 * 
	 * @param stack1 stack to allocate
	 * @param needed stack size needed
	 * @return could allocate all
	 */
	private boolean allocatePlayerItems(ItemStack stack1, int needed) {
		if (stack1 == null) {
			return true;
		}

		if (recipeBannedStacks.contains(getStackDescriptor(stack1))) {
			return false;
		}

		Integer alloc = allocatedStacks.get(getStackDescriptor(stack1));
		if (alloc == null) {
			alloc = 0;
		}

		if (ModLoader.getMinecraftInstance().playerController.isInCreativeMode() || survivalCheating) {
			return true;
		}

		int counter = 0;
		for (int i = 0; i < thePlayer.inventory.getSizeInventory(); i++) {
			ItemStack curStack = thePlayer.inventory.getStackInSlot(i);
			if (curStack != null && (curStack.isItemEqual(stack1) || (curStack.itemID == stack1.itemID && stack1.getItemDamage() == -1))) {
				counter += curStack.stackSize;


				if (counter - alloc >= needed) {
					allocatedStacks.put(getStackDescriptor(stack1), alloc + needed);
					return true;
				}
			}
		}

		if (counter - alloc >= needed) {
			allocatedStacks.put(getStackDescriptor(stack1), alloc + needed);
			return true;
		}
		return false;
	}

	/**
	 * Count items in inventory of type
	 * 
	 * @param wanted wanted stack
	 * @return number found
	 */
	private int countPlayerItems(ItemStack wanted) {
		if (wanted == null) {
			return 0;
		}

		if (recipeBannedStacks.contains(getStackDescriptor(wanted))) {
			return 0;
		}

		Integer alloc = allocatedStacks.get(getStackDescriptor(wanted));
		if (alloc == null) {
			alloc = 0;
		}

		if (ModLoader.getMinecraftInstance().playerController.isInCreativeMode() || survivalCheating) {
			return -1;
		}

		int counter = 0;
		for (int i = 0; i < thePlayer.inventory.getSizeInventory(); i++) {
			ItemStack curStack = thePlayer.inventory.getStackInSlot(i);
			if (curStack != null && (curStack.isItemEqual(wanted) || (curStack.itemID == wanted.itemID && wanted.getItemDamage() == -1))) {
				counter += curStack.stackSize;
			}
		}

		return counter - alloc;
	}



	/**
	 * Do consume items from inventory
	 * 
	 * @param stack1 stack to consume
	 * @param count number of items to consume
	 * @return success
	 */
	private boolean consumePlayerItems(ItemStack stack1, int count) {

		if (ModLoader.getMinecraftInstance().playerController.isInCreativeMode() || survivalCheating) {
			return true;
		}

		if (stack1 == null) {
			return true;
		}

		for (int i = 0; i < thePlayer.inventory.getSizeInventory(); i++) {
			ItemStack curStack = thePlayer.inventory.getStackInSlot(i);
			if (curStack != null && (curStack.isItemEqual(stack1) || (curStack.itemID == stack1.itemID && stack1.getItemDamage() == -1))) {
				if (curStack.stackSize > count) {
					curStack.stackSize -= count;
					return true;
				} else if (curStack.stackSize <= count) {
					count -= curStack.stackSize;
					thePlayer.inventory.setInventorySlotContents(i, null);
				}
			}
		}
		if (count > 0) {
			return false;
		}
		return true;
	}

	private int lastRecipe = -1;

	/**
	 * Get next recipe in the recipes list. Uses "lastRecipe" field as counter,
	 * increments it on success.
	 * 
	 * @param prod required recipe output
	 * @return the recipe, or null
	 */
	private IRecipe getNextRecipeForProduct(ItemStack prod) {
		List<IRecipe> recipes = new ArrayList<IRecipe>(CraftingManager.getInstance().getRecipeList());

		if (lastRecipe == recipes.size() - 1) {
			return null;
		}

		int k;
		for (k = lastRecipe + 1; k < recipes.size(); k++) {
			IRecipe irecipe = recipes.get(k);
			try {
				if (irecipe.getRecipeOutput().isItemEqual(prod) || (irecipe.getRecipeOutput().itemID == prod.itemID && prod.getItemDamage() == -1)) {
					lastRecipe = k;
					return irecipe;
				}
			} catch (NullPointerException npe) {
				continue;
			}
		}

		lastRecipe = k;
		return null;
	}

	private int recursionCount = 0;

	/** Stacks already seeked. Used to prevent infinite recursion. */
	private ArrayList<String> stacksSeekedInThisRecursion = new ArrayList<String>();
	/**
	 * Stack descriptors of stacks that are excluded from recipe finding. Used
	 * to prevent finding of recipes like ironcube > ingots > ironcube.
	 */
	private ArrayList<String> recipeBannedStacks = new ArrayList<String>();

	/** List of stacks to consume, output of the recipe sequence finding method */
	private ArrayList<ItemStack> toConsume = new ArrayList<ItemStack>();
	/**
	 * List of stacks to give back to player, output of the recipe sequence
	 * finding method
	 */
	private ArrayList<ItemStack> toGiveBack = new ArrayList<ItemStack>();

	/**
	 * Try to find items needed for given recipe, do recursion if needed.<br>
	 * Some lists must be cleared before calling this in root method.
	 * 
	 * @param irecipe recipe to craft (the topmost)
	 * @return true if valid recipe sequence was found.
	 */
	private boolean tryToFindMaterialsForRecipe(IRecipe irecipe) {

		if (ModLoader.getMinecraftInstance().playerController.isInCreativeMode() || survivalCheating) {
			return true;
		}

		recursionCount++;
		if (recursionCount > RECURSION_LIMIT) {
			return false;
		}
		try {

			ItemStack[] tmps;
			if (irecipe instanceof ShapedRecipes) {
				tmps = (ItemStack[]) ModLoader.getPrivateValue(net.minecraft.src.ShapedRecipes.class, irecipe, 2);
			} else if (irecipe instanceof ShapelessRecipes) {
				List<ItemStack> foo = ((List<ItemStack>) ModLoader.getPrivateValue(net.minecraft.src.ShapelessRecipes.class, irecipe, 1));
				tmps = foo.toArray(new ItemStack[foo.size()]);
			} else {
				return false;
			}

			// get copy of stacks in recipe
			ItemStack[] recipeStacks = new ItemStack[tmps.length];

			for (int i = 0; i < tmps.length; i++) {
				if (tmps[i] != null) {
					recipeStacks[i] = tmps[i].copy();
				}
			}

			// group stacks in recipe
			for (int i = 0; i < recipeStacks.length; i++) {
				if (recipeStacks[i] != null) {
					recipeStacks[i].stackSize = 1;
					for (int j = i + 1; j < recipeStacks.length; j++) {
						if (recipeStacks[j] != null && recipeStacks[j].isItemEqual(recipeStacks[i])) {
							recipeStacks[i].stackSize++;
							recipeStacks[j] = null;
						}
					}
				}
			}

			recloop:
			for (int i = 0; i < recipeStacks.length; i++) {

				// skip empty recipe fields
				if (recipeStacks[i] == null) {
					continue recloop;
				}

				// try to allocate all items in this stack directly
				if (!allocatePlayerItems(recipeStacks[i], recipeStacks[i].stackSize)) {

					// if can't craft recursively, stop.
					if (!recursiveCrafting) {
						return false;
					}

					// if already tried to find this, stop. (prevent infinite recursion and stack overflow)
					if (stacksSeekedInThisRecursion.contains(getStackDescriptor(recipeStacks[i]))) {
						return false;
					}

					// if stack in recipe is banned, stop.
					if (recipeBannedStacks.contains(getStackDescriptor(recipeStacks[i]))) {
						return false;
					}

					// check recursion depth
					if (recursionCount > RECURSION_LIMIT) {
						return false;
					}

					// take copy of the needed stack
					ItemStack needed = recipeStacks[i].copy();


					// count how many items are directly available from inventory, allocate them and reduce needed count.
					int availableDirectly = countPlayerItems(needed);

					if (availableDirectly > 0) {
						needed.stackSize -= availableDirectly;
						Integer alloc = allocatedStacks.get(getStackDescriptor(needed));
						if (alloc == null) {
							alloc = 0;
						}
						allocatedStacks.put(getStackDescriptor(needed), alloc + availableDirectly);
						ItemStack consumed = new ItemStack(needed.itemID, availableDirectly, needed.getItemDamage());
						toConsume.add(consumed);

						if (needed.stackSize <= 0) {
							continue recloop;
						}
					}


					// fix for stacks with damage -1 (= ignore damage)
					boolean anymeta = false;
					int tmpmeta = 0;
					if (needed.getItemDamage() < 0) {
						needed.setItemDamage(tmpmeta);
						anymeta = true;
					}

					// last recipe tried index
					int recipe_number = -1;

					while (true) {

						// get next recipe with "needed" as output
						lastRecipe = recipe_number;
						IRecipe irecipe2 = getNextRecipeForProduct(needed);
						recipe_number = lastRecipe;

						// if no recipe found, stop. If recipe is any-meta, try next meta.
						if (irecipe2 == null) {
							if (anymeta && tmpmeta <= 15) {
								needed.setItemDamage(++tmpmeta);
								recipe_number = -1;
								continue;
							} else {
								return false;
							}
						}

						// consume items in recipe as many times as needed
						innerloop:
						while (true) {
							stacksSeekedInThisRecursion.add(getStackDescriptor(recipeStacks[i]));
							// ###### RECURSION ######
							if (tryToFindMaterialsForRecipe(irecipe2)) {

								stacksSeekedInThisRecursion.remove(getStackDescriptor(recipeStacks[i]));

								needed.stackSize -= irecipe2.getRecipeOutput().stackSize;

								if (needed.stackSize > 0) {

									// something yet to consume
									continue innerloop;

								} else if (needed.stackSize < 0) {

									// consumed more than wanted, give back
									ItemStack got = needed.copy();
									got.stackSize = Math.abs(got.stackSize);
									if (got.getItemDamage() < 0) {
										got.setItemDamage(0);
									}
									toGiveBack.add(got);

								}

								continue recloop;

							} else {
								break;
							}
						}
					}
				} else {
					// whole stack consumed directly
					toConsume.add(recipeStacks[i]);
				}
			}

			// recipe found, ok.
			return true;

		} catch (IllegalArgumentException et) {
			et.printStackTrace();
		} catch (SecurityException es) {
			es.printStackTrace();
		} catch (NoSuchFieldException en) {
			en.printStackTrace();
		}
		// recipe not found. (rarely goes this far)
		return false;
	}

	/**
	 * Update availability status
	 */
	public void updateAvailability() {
		available = isAvailable();
	}

	@Override
	public ItemStack getBackgroundStack() {
		return product;
	}

	@Override
	public Slot setBackgroundStack(ItemStack stack) {
		product = stack.copy();
		return this;
	}

	@Override
	public boolean renderTooltipWhenEmpty() {
		return true;
	}

	@Override
	public boolean renderGrayWhenEmpty() {
		return true;
	}

}
