package net.minecraft.src;


/**
 * GRES GUI interface.<br>
 * This is used by the GUI wrapper class {@link PC_GresGui}, and passed to
 * widgets.
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
public interface PC_IGresGui {

	/**
	 * Add a widget
	 * 
	 * @param widget widget to add
	 * @return the child layout
	 */
	public abstract PC_GresWidget add(PC_GresWidget widget);

	/**
	 * @param b does pause game?
	 */
	public abstract void setPausesGame(boolean b);

	/**
	 * Is shift transport of stacks allowed to this inventory?<br>
	 * If returns false, only transfer inventory -> player is allowed.
	 * 
	 * @param b can sift transfer to inv
	 */
	public abstract void setCanShiftTransfer(boolean b);

	/**
	 * @return can shift transfer to this inventory
	 */
	public boolean canShiftTransfer();

	/**
	 * Close the gui and set in-game focus.
	 */
	public abstract void close();

	/**
	 * On "craft matrix changed" event is generated
	 * 
	 * @param iinventory
	 */
	public abstract void onCraftMatrixChanged(IInventory iinventory);

	/**
	 * Get the container manager
	 * 
	 * @return container
	 */
	public abstract Container getContainer();

	/**
	 * Set focused element.
	 * 
	 * @param widget
	 */
	public void setFocus(PC_GresWidget widget);

	/**
	 * Set background gradient colors - 0xAARRGGBB.
	 * 
	 * @param top
	 * @param bottom
	 */
	public void setBackground(int top, int bottom);

}
