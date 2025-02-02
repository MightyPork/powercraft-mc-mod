package net.minecraft.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class Slot
{
    /** The index of the slot in the inventory. */
    private final int slotIndex;

    /** The inventory we want to extract a slot from. */
    public final IInventory inventory;

    /** the id of the slot(also the index in the inventory arraylist) */
    public int slotNumber;

    /** display position of the inventory slot on the screen x axis */
    public int xDisplayPosition;

    /** display position of the inventory slot on the screen y axis */
    public int yDisplayPosition;

    /** Position within background texture file, normally -1 which causes no background to be drawn. */
    protected int backgroundIconIndex = -1;

    /** Background texture file assigned to this slot, if any. Vanilla "/gui/items.png" is used if this is null. */
    protected String texture = "/gui/items.png";

    public Slot(IInventory par1IInventory, int par2, int par3, int par4)
    {
        this.inventory = par1IInventory;
        this.slotIndex = par2;
        this.xDisplayPosition = par3;
        this.yDisplayPosition = par4;
    }

    /**
     * if par2 has more items than par1, onCrafting(item,countIncrease) is called
     */
    public void onSlotChange(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        if (par1ItemStack != null && par2ItemStack != null)
        {
            if (par1ItemStack.itemID == par2ItemStack.itemID)
            {
                int var3 = par2ItemStack.stackSize - par1ItemStack.stackSize;

                if (var3 > 0)
                {
                    this.onCrafting(par1ItemStack, var3);
                }
            }
        }
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    protected void onCrafting(ItemStack par1ItemStack, int par2) {}

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    protected void onCrafting(ItemStack par1ItemStack) {}

    public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
    {
        this.onSlotChanged();
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return true;
    }

    /**
     * Helper fnct to get the stack in the slot.
     */
    public ItemStack getStack()
    {
        return this.inventory.getStackInSlot(this.slotIndex);
    }

    /**
     * Returns if this slot contains a stack.
     */
    public boolean getHasStack()
    {
        return this.getStack() != null;
    }

    /**
     * Helper method to put a stack in the slot.
     */
    public void putStack(ItemStack par1ItemStack)
    {
        this.inventory.setInventorySlotContents(this.slotIndex, par1ItemStack);
        this.onSlotChanged();
    }

    /**
     * Called when the stack in a Slot changes
     */
    public void onSlotChanged()
    {
        this.inventory.onInventoryChanged();
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    public int getSlotStackLimit()
    {
        return this.inventory.getInventoryStackLimit();
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    public ItemStack decrStackSize(int par1)
    {
        return this.inventory.decrStackSize(this.slotIndex, par1);
    }

    /**
     * returns true if this slot is in par2 of par1
     */
    public boolean isSlotInInventory(IInventory par1IInventory, int par2)
    {
        return par1IInventory == this.inventory && par2 == this.slotIndex;
    }

    /**
     * Return whether this slot's stack can be taken from this slot.
     */
    public boolean canTakeStack(EntityPlayer par1EntityPlayer)
    {
        return true;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the icon index on items.png that is used as background image of the slot.
     */
    public int getBackgroundIconIndex()
    {
        return backgroundIconIndex;
    }

    /**
     * Gets the path of the texture file to use for the background image of this slot when drawing the GUI.
     * @return String: The texture file that will be used in GuiContainer.drawSlotInventory for the slot background.  
     */
    public String getBackgroundIconTexture()
    {
        return (texture == null ? "/gui/items.png" : texture);
    }

    /**
     * Sets which icon index to use as the background image of the slot when it's empty.
     * @param iconIndex int: The index into the texture file, 0-255, or -1 for no background.  
     */
    public void setBackgroundIconIndex(int iconIndex)
    {
        backgroundIconIndex = iconIndex;
    }

    /**
     * Sets the texture file to use for the background image of the slot when it's empty.
     * @param textureFilename String: Path of texture file to use, or null to use "/gui/items.png"
     */
    public void setBackgroundIconTexture(String textureFilename)
    {
        texture = textureFilename;
    }

    /**
     * Retrieves the index in the inventory for this slot, this value should typically not 
     * be used, but can be useful for some occasions.
     * 
     * @return Index in associated inventory for this slot.
     */
    public int getSlotIndex()
    {
        return slotIndex;
    }
}
