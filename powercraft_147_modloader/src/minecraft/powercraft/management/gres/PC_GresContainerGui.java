package powercraft.management.gres;

import java.util.List;

import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.OpenGlHelper;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.RenderItem;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.Slot;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import powercraft.management.PC_ClientUtils;
import powercraft.management.PC_RectI;
import powercraft.management.PC_VecI;
import powercraft.management.inventory.PC_Slot;
import powercraft.management.tileentity.PC_ITileEntityWatcher;
import powercraft.management.tileentity.PC_TileEntity;

/**
 * GuiScreen class
 * 
 * @authors XOR19, Rapus95, MightyPork
 * @copy (c) 2012
 */
public class PC_GresContainerGui extends GuiContainer implements PC_IGresGui, PC_ITileEntityWatcher {

	/** The wrapped GUI */
	private PC_IGresClient gui;
	private PC_GresLayoutV child;
	private PC_GresWidget lastFocus;
	private boolean isContainer;
	private boolean pauseGame = false;
	private boolean shiftTransfer = false;
	private PC_TileEntity tileEntity;
	
	/**
	 * Constructor for creating a gui
	 * 
	 * @param gui the gui
	 */
	public PC_GresContainerGui(PC_TileEntity te, PC_GresBaseWithInventory gui) {
		super(gui);
		this.gui = (PC_IGresClient)gui;
		tileEntity = te;
		if(tileEntity!=null)
			tileEntity.addTileEntityWatcher(this);
	}

	@Override
	public PC_GresWidget add(PC_GresWidget widget) {
		PC_GresWidget c = child.add(widget);
		xSize = child.getSize().x;
		ySize = child.getSize().y;
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
		child.setPosition(guiLeft, guiTop);
		return c;
	}

	@Override
	public void setPausesGame(boolean b) {
		pauseGame = b;
	}

	@Override
	public void close() {
		mc.displayGuiScreen(null);
		mc.setIngameFocus();
		mc.thePlayer.closeScreen();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		child.tick();
		if (lastFocus != null) {
			lastFocus.updateCursorCounter();
		}
		gui.updateScreen(this);
	}

	@Override
	public void setFocus(PC_GresWidget widget) {
		if (widget != lastFocus) {
			if (lastFocus != null) {
				lastFocus.setFocus(false);
			}
			if (widget != null) {
				widget.setFocus(true);
			}
			lastFocus = widget;
		}
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		mc.thePlayer.openContainer = inventorySlots;
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
		child = new PC_GresLayoutV();
		child.setFontRenderer(fontRenderer);
		child.setGui(this);
		child.setSize(0, 0);
		gui.initGui(this);
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		gui.onGuiClosed(this);
		if(tileEntity!=null)
			tileEntity.removeTileEntityWatcher(this);
		super.onGuiClosed();
	}

	@Override
	protected void keyTyped(char c, int i) {

		if (i == Keyboard.KEY_F11) {}

		if (lastFocus != null && lastFocus.visible) {
			if (lastFocus.keyTyped(c, i)) {
				registerAction(lastFocus);
				return;
			}
		}

		if (i == Keyboard.KEY_ESCAPE || i == Keyboard.KEY_E) {
			gui.onEscapePressed(this);
		} else if (i == Keyboard.KEY_RETURN) {
			gui.onReturnPressed(this);
		}
	}

	private void putSlotUnderMouse(int x, int y){
		for(int i=0; i<inventorySlots.inventorySlots.size(); i++){
			Slot s = (Slot)inventorySlots.inventorySlots.get(i);
			s.xDisplayPosition = x - 999;
			s.yDisplayPosition = y - 999;
		}
		Slot s = getSlotAtPosition(x, y);
		if(s!=null){
			s.xDisplayPosition = x-guiLeft-8;
			s.yDisplayPosition = y-guiTop-8;
		}
	}
	
	protected void func_85041_a(int x, int y, int par3, long par4){
		putSlotUnderMouse(x, y);
		super.func_85041_a(x, y, par3, par4);
	}
	
	@Override
	protected void mouseClicked(int x, int y, int button) {

		PC_GresWidget newFocus = child.getWidgetUnderMouse(new PC_VecI(x, y));
		if (newFocus != null && !newFocus.visible) newFocus = null;

		if (newFocus != lastFocus) {
			if (lastFocus != null) {
				lastFocus.setFocus(false);
			}
			if (newFocus != null) {
				newFocus.setFocus(true);
			}
			lastFocus = newFocus;
		}

		boolean makeAction = false;
		if (newFocus != null) {
			PC_VecI fpos = newFocus.getPositionOnScreen();
			if (newFocus.mouseClick(new PC_VecI(x - fpos.x, y - fpos.y), button)) {
				makeAction = true;
			}
		}
		putSlotUnderMouse(x, y);
		super.mouseClicked(x, y, button);
		if(makeAction)
			registerAction(newFocus);
	}

	private void mouseMoved(int x, int y) {
		int wheel = Mouse.getDWheel();
		if (wheel < 0) {
			wheel = -1;
		}
		if (wheel > 0) {
			wheel = 1;
		}
		if (lastFocus != null) {
			PC_VecI fpos = lastFocus.getPositionOnScreen();
			lastFocus.mouseMove(new PC_VecI(x - fpos.x, y - fpos.y));
			lastFocus.mouseWheel(wheel);
		}
		child.getWidgetUnderMouse(new PC_VecI(x, y));
	}

	private void mouseUp(int x, int y, int state) {
		if (lastFocus != null) {
			PC_VecI fpos = lastFocus.getPositionOnScreen();
			if (lastFocus.mouseClick(new PC_VecI(x - fpos.x, y - fpos.y), -1)) {
				registerAction(lastFocus);
			}
		}
	}


	/**
	 * state = -1 ... move, other ... up
	 */
	@Override
	protected void mouseMovedOrUp(int x, int y, int state) {
		putSlotUnderMouse(x, y);
		super.mouseMovedOrUp(x, y, state);
		
		if (state != -1) {
			mouseUp(x, y, state);
		} else {
			mouseMoved(x, y);
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return pauseGame;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		ScaledResolution sr = new ScaledResolution(PC_ClientUtils.mc().gameSettings, PC_ClientUtils.mc().displayWidth, PC_ClientUtils.mc().displayHeight);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		child.updateRenderer(new PC_VecI(0, 0), new PC_RectI(0, 0, sr.getScaledWidth(), sr.getScaledHeight()), sr.getScaleFactor());
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GL11.glPopMatrix();
	}

	private int bgColor1 = 0xa0101010;
	private int bgColor2 = 0x50101010;

	@Override
	public void setBackground(int top, int bottom) {
		bgColor1 = top;
		bgColor2 = bottom;
	}

	@Override
	public void drawWorldBackground(int par1) {
		if (mc.theWorld != null) {
			drawGradientRect(0, 0, width, height, bgColor1, bgColor2);
		} else {
			drawBackground(par1);
		}
	}


	/**
	 * Draws the screen and all the components in it. COPY FROM GuiContainer!<BR>
	 * NEEDED TO OVERRIDE render() AND FOR CUSTOM SLOT RENDERING.<br>
	 * <br>
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3) {

		gui.updateTick(this);

		if(!gui.drawBackground(this, par1, par2, par3))
			drawDefaultBackground();
		
		int i = guiLeft;
		int j = guiTop;
		drawGuiContainerBackgroundLayer(par3, par1, par2);
		
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glPushMatrix();
		GL11.glTranslatef(i, j, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		Slot slot = null;
		int k = 240;
		int i1 = 240;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k / 1.0F, i1 / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		drawGuiContainerForegroundLayer(par1, par2);

		InventoryPlayer inventoryplayer = mc.thePlayer.inventory;

		if (inventoryplayer.getItemStack() != null) {
			GL11.glTranslatef(0.0F, 0.0F, 32F);
			zLevel = 200F;
			itemRenderer.zLevel = 200F;
			itemRenderer.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, inventoryplayer.getItemStack(), par1 - i - 8, par2 - j - 8);
			itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, inventoryplayer.getItemStack(), par1 - i - 8, par2 - j - 8);
			zLevel = 0.0F;
			itemRenderer.zLevel = 0.0F;
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		if (inventoryplayer.getItemStack() == null) {
			
			List list = getTooltipAtPosition(par1, par2);
			
			if (list != null && list.size() > 0) {
				int l1 = 0;

				for (int i2 = 0; i2 < list.size(); i2++) {
					int k2 = fontRenderer.getStringWidth((String) list.get(i2));

					if (k2 > l1) {
						l1 = k2;
					}
				}

				int j2 = (par1 - i) + 12;
				int l2 = par2 - j - 12;
				int i3 = l1;
				int j3 = 8;

				if (list.size() > 1) {
					j3 += 2 + (list.size() - 1) * 10;
				}

				zLevel = 300F;
				itemRenderer.zLevel = 300F;
				int k3 = 0xf0100010;
				drawGradientRect(j2 - 3, l2 - 4, j2 + i3 + 3, l2 - 3, k3, k3);
				drawGradientRect(j2 - 3, l2 + j3 + 3, j2 + i3 + 3, l2 + j3 + 4, k3, k3);
				drawGradientRect(j2 - 3, l2 - 3, j2 + i3 + 3, l2 + j3 + 3, k3, k3);
				drawGradientRect(j2 - 4, l2 - 3, j2 - 3, l2 + j3 + 3, k3, k3);
				drawGradientRect(j2 + i3 + 3, l2 - 3, j2 + i3 + 4, l2 + j3 + 3, k3, k3);
				int l3 = 0x505000ff;
				int i4 = (l3 & 0xfefefe) >> 1 | l3 & 0xff000000;
				drawGradientRect(j2 - 3, (l2 - 3) + 1, (j2 - 3) + 1, (l2 + j3 + 3) - 1, l3, i4);
				drawGradientRect(j2 + i3 + 2, (l2 - 3) + 1, j2 + i3 + 3, (l2 + j3 + 3) - 1, l3, i4);
				drawGradientRect(j2 - 3, l2 - 3, j2 + i3 + 3, (l2 - 3) + 1, l3, l3);
				drawGradientRect(j2 - 3, l2 + j3 + 2, j2 + i3 + 3, l2 + j3 + 3, i4, i4);

				for (int j4 = 0; j4 < list.size(); j4++) {
					String s = (String) list.get(j4);

					fontRenderer.drawStringWithShadow(s, j2, l2, -1);

					if (j4 == 0) {
						l2 += 2;
					}

					l2 += 10;
				}

				zLevel = 0.0F;
				itemRenderer.zLevel = 0.0F;
			}
		}


		GL11.glPopMatrix();

		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	/**
	 * COPY FROM GuiContainer!<BR>
	 * NEEDED TO OVERRIDE render() AND FOR CUSTOM SLOT RENDERING.<br>
	 * <br>
	 * Returns if the passed mouse position is over the specified slot.
	 * 
	 * @param par1Slot the slot to check
	 * @param par2 x ?
	 * @param par3 y ?
	 * @return is over
	 */
	private boolean isMouseOverSlot(Slot par1Slot, int par2, int par3) {
		int i = guiLeft;
		int j = guiTop;
		par2 -= i;
		par3 -= j;
		return par2 >= par1Slot.xDisplayPosition - 1 && par2 < par1Slot.xDisplayPosition + 16 + 1 && par3 >= par1Slot.yDisplayPosition - 1
				&& par3 < par1Slot.yDisplayPosition + 16 + 1;
	}


	/**
	 * Draws an inventory slot.<br>
	 * Almost copy from GuiContainer, but also does PCco_SlotDirectCrafting's
	 * ghostly rendering.
	 * 
	 * @param slot the slot
	 */
	protected void drawSlotInventory(Slot slot) {
		int i = slot.xDisplayPosition;
		int j = slot.yDisplayPosition;
		ItemStack itemstack = slot.getStack();
		boolean isNull = false;
		int k = i;
		int l = j;
		zLevel = 100F;
		itemRenderer.zLevel = 100F;

		if(slot instanceof PC_Slot){
			if(((PC_Slot) slot).useAlwaysBackground())
				itemstack = null;
		}
		
		if (itemstack == null) {
			int i1 = slot.getBackgroundIconIndex();

			if (i1 >= 0) {
				GL11.glDisable(GL11.GL_LIGHTING);
				mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/gui/items.png"));
				drawTexturedModalRect(k, l, (i1 % 16) * 16, (i1 / 16) * 16, 16, 16);
				GL11.glEnable(GL11.GL_LIGHTING);
				isNull = true;
			}
		}

		if (isNull || itemstack == null) {
			
			if (slot instanceof PC_Slot) {
				PC_Slot dirslot = (PC_Slot) slot;
				if (dirslot.getBackgroundStack() != null) {
					itemRenderer.zLevel = 99F;
					zLevel = 99F;
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.2F);
					itemRenderer.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, dirslot.getBackgroundStack(), k, l);

					if(dirslot.renderGrayWhenEmpty()) {
						GL11.glDisable(GL11.GL_LIGHTING);
						GL11.glDisable(GL11.GL_DEPTH_TEST);
						int j1 = slot.xDisplayPosition;
						int k1 = slot.yDisplayPosition;
						drawGradientRect(j1, k1, j1 + 16, k1 + 16, 0xbb999999, 0xbb999999);
						GL11.glEnable(GL11.GL_LIGHTING);
						GL11.glEnable(GL11.GL_DEPTH_TEST);
					}
					
					zLevel = 100F;
					itemRenderer.zLevel = 100F;
				}

			}
			
		} else {
			itemRenderer.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, itemstack, k, l);
			itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, itemstack, k, l);
		}

		itemRenderer.zLevel = 0.0F;
		zLevel = 0.0F;
	}

	@Override
	public PC_GresBaseWithInventory getContainer() {
		return (PC_GresBaseWithInventory)inventorySlots;
	}

	@Override
	protected void handleMouseClick(Slot par1Slot, int par2, int par3, int par4)
    {
        if (par1Slot != null)
        {
            par2 = par1Slot.slotNumber;
        }

        this.mc.playerController.windowClick(this.inventorySlots.windowId, par2, par3, par4, this.mc.thePlayer);
    }

	@Override
	public PC_VecI getSize() {
		return new PC_VecI(width, height);
	}

	@Override
	public void registerAction(PC_GresWidget widget) {
		gui.actionPerformed(widget, this);
	}
	
	/**
     * Handles mouse input.
     */
    public void handleMouseInput()
    {
        int var1 = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int var2 = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        if (Mouse.getEventButtonState())
        {
            this.mouseClicked(var1, var2, Mouse.getEventButton());
        }else{
            this.mouseMovedOrUp(var1, var2, Mouse.getEventButton());
        }
    }
	
	public Slot getSlotAtPosition(int x, int y) {
		PC_GresWidget w = child.getWidgetUnderMouse(new PC_VecI(x, y));
		if(w==null)
			return null;
		PC_VecI fpos = w.getPositionOnScreen();
		return w.getSlotUnderMouse(new PC_VecI(x - fpos.x, y - fpos.y));
	}
	
	public List<String> getTooltipAtPosition(int x, int y) {
		PC_GresWidget w = child.getWidgetUnderMouse(new PC_VecI(x, y));
		if(w==null)
			return null;
		PC_VecI fpos = w.getPositionOnScreen();
		return w.getTooltip(new PC_VecI(x - fpos.x, y - fpos.y));
	}

	@Override
	public void keyChange(String key, Object value) {
		child.keyChange(key, value);
		gui.keyChange(key, value);
	}
	
	@Override
	public PC_TileEntity getTE() {
		return tileEntity;
	}
	
	public static RenderItem getItemRenderer(){
		return itemRenderer;
	}
	
}
