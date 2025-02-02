package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IInventory
{
    /**
     * Returns the number of slots in the inventory.
     */
    int getSizeInventory();

    /**
     * Returns the stack in slot i
     */
    ItemStack getStackInSlot(int var1);

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    ItemStack decrStackSize(int var1, int var2);

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    ItemStack getStackInSlotOnClosing(int var1);

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    void setInventorySlotContents(int var1, ItemStack var2);

    /**
     * Returns the name of the inventory.
     */
    String getInvName();

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    int getInventoryStackLimit();

    /**
     * Called when an the contents of an Inventory change, usually
     */
    void onInventoryChanged();

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    boolean isUseableByPlayer(EntityPlayer var1);

    void openChest();

    void closeChest();
}
