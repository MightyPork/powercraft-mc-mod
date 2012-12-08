package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public abstract class GuiContainer extends GuiScreen
{
    /** Stacks renderer. Icons, stack size, health, etc... */
    protected static RenderItem itemRenderer = new RenderItem();

    /** The X size of the inventory window in pixels. */
    protected int xSize = 176;

    /** The Y size of the inventory window in pixels. */
    protected int ySize = 166;

    /** A list of the players inventory slots. */
    public Container inventorySlots;

    /**
     * Starting X position for the Gui. Inconsistent use for Gui backgrounds.
     */
    protected int guiLeft;

    /**
     * Starting Y position for the Gui. Inconsistent use for Gui backgrounds.
     */
    protected int guiTop;
    private Slot theSlot;
    private Slot field_85051_p = null;
    private boolean field_90018_r = false;
    private ItemStack field_85050_q = null;
    private int field_85049_r = 0;
    private int field_85048_s = 0;
    private Slot field_85047_t = null;
    private long field_85046_u = 0L;
    private ItemStack field_85045_v = null;

    public GuiContainer(Container par1Container)
    {
        this.inventorySlots = par1Container;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        super.initGui();
        this.mc.thePlayer.openContainer = this.inventorySlots;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        int var4 = this.guiLeft;
        int var5 = this.guiTop;
        this.drawGuiContainerBackgroundLayer(par3, par1, par2);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        super.drawScreen(par1, par2, par3);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)var4, (float)var5, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        this.theSlot = null;
        short var6 = 240;
        short var7 = 240;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var6 / 1.0F, (float)var7 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int var8;
        int var9;

        for (int var13 = 0; var13 < this.inventorySlots.inventorySlots.size(); ++var13)
        {
            Slot var14 = (Slot)this.inventorySlots.inventorySlots.get(var13);
            this.drawSlotInventory(var14);

            if (this.isMouseOverSlot(var14, par1, par2))
            {
                this.theSlot = var14;
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                var8 = var14.xDisplayPosition;
                var9 = var14.yDisplayPosition;
                this.drawGradientRect(var8, var9, var8 + 16, var9 + 16, -2130706433, -2130706433);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
        }

        this.drawGuiContainerForegroundLayer(par1, par2);
        InventoryPlayer var15 = this.mc.thePlayer.inventory;
        ItemStack var16 = this.field_85050_q == null ? var15.getItemStack() : this.field_85050_q;

        if (var16 != null)
        {
            var8 = this.field_85050_q == null ? 8 : 0;

            if (this.field_85050_q != null && this.field_90018_r)
            {
                var16 = var16.copy();
                var16.stackSize = MathHelper.ceiling_float_int((float)var16.stackSize / 2.0F);
            }

            this.func_85044_b(var16, par1 - var4 - var8, par2 - var5 - var8);
        }

        if (this.field_85045_v != null)
        {
            float var17 = (float)(Minecraft.getSystemTime() - this.field_85046_u) / 100.0F;

            if (var17 >= 1.0F)
            {
                var17 = 1.0F;
                this.field_85045_v = null;
            }

            var9 = this.field_85047_t.xDisplayPosition - this.field_85049_r;
            int var10 = this.field_85047_t.yDisplayPosition - this.field_85048_s;
            int var11 = this.field_85049_r + (int)((float)var9 * var17);
            int var12 = this.field_85048_s + (int)((float)var10 * var17);
            this.func_85044_b(this.field_85045_v, var11, var12);
        }

        if (var15.getItemStack() == null && this.theSlot != null && this.theSlot.getHasStack())
        {
            ItemStack var18 = this.theSlot.getStack();
            this.func_74184_a(var18, par1 - var4 + 8, par2 - var5 + 8);
        }

        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableStandardItemLighting();
    }

    private void func_85044_b(ItemStack par1ItemStack, int par2, int par3)
    {
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        itemRenderer.zLevel = 200.0F;
        itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, par1ItemStack, par2, par3);
        itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, par1ItemStack, par2, par3);
        this.zLevel = 0.0F;
        itemRenderer.zLevel = 0.0F;
    }

    protected void func_74184_a(ItemStack par1ItemStack, int par2, int par3)
    {
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        List var4 = par1ItemStack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

        if (!var4.isEmpty())
        {
            int var5 = 0;
            int var6;
            int var7;

            for (var6 = 0; var6 < var4.size(); ++var6)
            {
                var7 = this.fontRenderer.getStringWidth((String)var4.get(var6));

                if (var7 > var5)
                {
                    var5 = var7;
                }
            }

            var6 = par2 + 12;
            var7 = par3 - 12;
            int var9 = 8;

            if (var4.size() > 1)
            {
                var9 += 2 + (var4.size() - 1) * 10;
            }

            this.zLevel = 300.0F;
            itemRenderer.zLevel = 300.0F;
            int var10 = -267386864;
            this.drawGradientRect(var6 - 3, var7 - 4, var6 + var5 + 3, var7 - 3, var10, var10);
            this.drawGradientRect(var6 - 3, var7 + var9 + 3, var6 + var5 + 3, var7 + var9 + 4, var10, var10);
            this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 + var9 + 3, var10, var10);
            this.drawGradientRect(var6 - 4, var7 - 3, var6 - 3, var7 + var9 + 3, var10, var10);
            this.drawGradientRect(var6 + var5 + 3, var7 - 3, var6 + var5 + 4, var7 + var9 + 3, var10, var10);
            int var11 = 1347420415;
            int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
            this.drawGradientRect(var6 - 3, var7 - 3 + 1, var6 - 3 + 1, var7 + var9 + 3 - 1, var11, var12);
            this.drawGradientRect(var6 + var5 + 2, var7 - 3 + 1, var6 + var5 + 3, var7 + var9 + 3 - 1, var11, var12);
            this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 - 3 + 1, var11, var11);
            this.drawGradientRect(var6 - 3, var7 + var9 + 2, var6 + var5 + 3, var7 + var9 + 3, var12, var12);

            for (int var13 = 0; var13 < var4.size(); ++var13)
            {
                String var14 = (String)var4.get(var13);

                if (var13 == 0)
                {
                    var14 = "\u00a7" + Integer.toHexString(par1ItemStack.getRarity().rarityColor) + var14;
                }
                else
                {
                    var14 = "\u00a77" + var14;
                }

                this.fontRenderer.drawStringWithShadow(var14, var6, var7, -1);

                if (var13 == 0)
                {
                    var7 += 2;
                }

                var7 += 10;
            }

            this.zLevel = 0.0F;
            itemRenderer.zLevel = 0.0F;
        }
    }

    /**
     * Draws the text when mouse is over creative inventory tab. Params: current creative tab to be checked, current
     * mouse x position, current mouse y position.
     */
    protected void drawCreativeTabHoveringText(String par1Str, int par2, int par3)
    {
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        int var4 = this.fontRenderer.getStringWidth(par1Str);
        int var5 = par2 + 12;
        int var6 = par3 - 12;
        byte var8 = 8;
        this.zLevel = 300.0F;
        itemRenderer.zLevel = 300.0F;
        int var9 = -267386864;
        this.drawGradientRect(var5 - 3, var6 - 4, var5 + var4 + 3, var6 - 3, var9, var9);
        this.drawGradientRect(var5 - 3, var6 + var8 + 3, var5 + var4 + 3, var6 + var8 + 4, var9, var9);
        this.drawGradientRect(var5 - 3, var6 - 3, var5 + var4 + 3, var6 + var8 + 3, var9, var9);
        this.drawGradientRect(var5 - 4, var6 - 3, var5 - 3, var6 + var8 + 3, var9, var9);
        this.drawGradientRect(var5 + var4 + 3, var6 - 3, var5 + var4 + 4, var6 + var8 + 3, var9, var9);
        int var10 = 1347420415;
        int var11 = (var10 & 16711422) >> 1 | var10 & -16777216;
        this.drawGradientRect(var5 - 3, var6 - 3 + 1, var5 - 3 + 1, var6 + var8 + 3 - 1, var10, var11);
        this.drawGradientRect(var5 + var4 + 2, var6 - 3 + 1, var5 + var4 + 3, var6 + var8 + 3 - 1, var10, var11);
        this.drawGradientRect(var5 - 3, var6 - 3, var5 + var4 + 3, var6 - 3 + 1, var10, var10);
        this.drawGradientRect(var5 - 3, var6 + var8 + 2, var5 + var4 + 3, var6 + var8 + 3, var11, var11);
        this.fontRenderer.drawStringWithShadow(par1Str, var5, var6, -1);
        this.zLevel = 0.0F;
        itemRenderer.zLevel = 0.0F;
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {}

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected abstract void drawGuiContainerBackgroundLayer(float var1, int var2, int var3);

    /**
     * Draws an inventory slot
     */
    private void drawSlotInventory(Slot par1Slot)
    {
        int var2 = par1Slot.xDisplayPosition;
        int var3 = par1Slot.yDisplayPosition;
        ItemStack var4 = par1Slot.getStack();
        boolean var5 = par1Slot == this.field_85051_p && this.field_85050_q != null && !this.field_90018_r;

        if (par1Slot == this.field_85051_p && this.field_85050_q != null && this.field_90018_r && var4 != null)
        {
            var4 = var4.copy();
            var4.stackSize /= 2;
        }

        this.zLevel = 100.0F;
        itemRenderer.zLevel = 100.0F;

        if (var4 == null)
        {
            int var6 = par1Slot.getBackgroundIconIndex();

            if (var6 >= 0)
            {
                GL11.glDisable(GL11.GL_LIGHTING);
                this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture(par1Slot.getBackgroundIconTexture()));
                this.drawTexturedModalRect(var2, var3, var6 % 16 * 16, var6 / 16 * 16, 16, 16);
                GL11.glEnable(GL11.GL_LIGHTING);
                var5 = true;
            }
        }

        if (!var5)
        {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, var4, var2, var3);
            itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var4, var2, var3);
        }

        itemRenderer.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    /**
     * Returns the slot at the given coordinates or null if there is none.
     */
    private Slot getSlotAtPosition(int par1, int par2)
    {
        for (int var3 = 0; var3 < this.inventorySlots.inventorySlots.size(); ++var3)
        {
            Slot var4 = (Slot)this.inventorySlots.inventorySlots.get(var3);

            if (this.isMouseOverSlot(var4, par1, par2))
            {
                return var4;
            }
        }

        return null;
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        boolean var4 = par3 == this.mc.gameSettings.keyBindPickBlock.keyCode + 100;

        if (par3 == 0 || par3 == 1 || var4)
        {
            Slot var5 = this.getSlotAtPosition(par1, par2);
            int var6 = this.guiLeft;
            int var7 = this.guiTop;
            boolean var8 = par1 < var6 || par2 < var7 || par1 >= var6 + this.xSize || par2 >= var7 + this.ySize;
            int var9 = -1;

            if (var5 != null)
            {
                var9 = var5.slotNumber;
            }

            if (var8)
            {
                var9 = -999;
            }

            if (this.mc.gameSettings.field_85185_A && var8 && this.mc.thePlayer.inventory.getItemStack() == null)
            {
                this.mc.displayGuiScreen((GuiScreen)null);
                return;
            }

            if (var9 != -1)
            {
                if (this.mc.gameSettings.field_85185_A)
                {
                    if (var5 != null && var5.getHasStack())
                    {
                        this.field_85051_p = var5;
                        this.field_85050_q = null;
                        this.field_90018_r = par3 == 1;
                    }
                    else
                    {
                        this.field_85051_p = null;
                    }
                }
                else if (var4)
                {
                    this.handleMouseClick(var5, var9, par3, 3);
                }
                else
                {
                    boolean var10 = var9 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                    this.handleMouseClick(var5, var9, par3, var10 ? 1 : 0);
                }
            }
        }
    }

    protected void func_85041_a(int par1, int par2, int par3, long par4)
    {
        if (this.field_85051_p != null && this.mc.gameSettings.field_85185_A && this.field_85050_q == null)
        {
            if (par3 == 0 || par3 == 1)
            {
                Slot var6 = this.getSlotAtPosition(par1, par2);

                if (var6 != this.field_85051_p)
                {
                    this.field_85050_q = this.field_85051_p.getStack();
                }
            }
        }
    }

    /**
     * Called when the mouse is moved or a mouse button is released.  Signature: (mouseX, mouseY, which) which==-1 is
     * mouseMove, which==0 or which==1 is mouseUp
     */
    protected void mouseMovedOrUp(int par1, int par2, int par3)
    {
        if (this.field_85051_p != null && this.mc.gameSettings.field_85185_A)
        {
            if (par3 == 0 || par3 == 1)
            {
                Slot var4 = this.getSlotAtPosition(par1, par2);
                int var5 = this.guiLeft;
                int var6 = this.guiTop;
                boolean var7 = par1 < var5 || par2 < var6 || par1 >= var5 + this.xSize || par2 >= var6 + this.ySize;
                int var8 = -1;

                if (var4 != null)
                {
                    var8 = var4.slotNumber;
                }

                if (var7)
                {
                    var8 = -999;
                }

                if (this.field_85050_q == null && var4 != this.field_85051_p)
                {
                    this.field_85050_q = this.field_85051_p.getStack();
                }

                boolean var9 = var4 == null || !var4.getHasStack();

                if (var4 != null && var4.getHasStack() && this.field_85050_q != null && ItemStack.areItemStackTagsEqual(var4.getStack(), this.field_85050_q))
                {
                    var9 |= var4.getStack().stackSize + this.field_85050_q.stackSize <= this.field_85050_q.getMaxStackSize();
                }

                if (var8 != -1 && this.field_85050_q != null && var9)
                {
                    this.handleMouseClick(this.field_85051_p, this.field_85051_p.slotNumber, par3, 0);
                    this.handleMouseClick(var4, var8, 0, 0);

                    if (this.mc.thePlayer.inventory.getItemStack() != null)
                    {
                        this.handleMouseClick(this.field_85051_p, this.field_85051_p.slotNumber, par3, 0);
                        this.field_85049_r = par1 - var5;
                        this.field_85048_s = par2 - var6;
                        this.field_85047_t = this.field_85051_p;
                        this.field_85045_v = this.field_85050_q;
                        this.field_85046_u = Minecraft.getSystemTime();
                    }
                    else
                    {
                        this.field_85045_v = null;
                    }
                }
                else if (this.field_85050_q != null)
                {
                    this.field_85049_r = par1 - var5;
                    this.field_85048_s = par2 - var6;
                    this.field_85047_t = this.field_85051_p;
                    this.field_85045_v = this.field_85050_q;
                    this.field_85046_u = Minecraft.getSystemTime();
                }

                this.field_85050_q = null;
                this.field_85051_p = null;
            }
        }
    }

    /**
     * Returns if the passed mouse position is over the specified slot.
     */
    private boolean isMouseOverSlot(Slot par1Slot, int par2, int par3)
    {
        return this.func_74188_c(par1Slot.xDisplayPosition, par1Slot.yDisplayPosition, 16, 16, par2, par3);
    }

    protected boolean func_74188_c(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        int var7 = this.guiLeft;
        int var8 = this.guiTop;
        par5 -= var7;
        par6 -= var8;
        return par5 >= par1 - 1 && par5 < par1 + par3 + 1 && par6 >= par2 - 1 && par6 < par2 + par4 + 1;
    }

    protected void handleMouseClick(Slot par1Slot, int par2, int par3, int par4)
    {
        if (par1Slot != null)
        {
            par2 = par1Slot.slotNumber;
        }

        this.mc.playerController.windowClick(this.inventorySlots.windowId, par2, par3, par4, this.mc.thePlayer);
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.keyCode)
        {
            this.mc.thePlayer.closeScreen();
        }

        this.func_82319_a(par2);

        if (par2 == this.mc.gameSettings.keyBindPickBlock.keyCode && this.theSlot != null && this.theSlot.getHasStack())
        {
            this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, this.ySize, 3);
        }
    }

    protected boolean func_82319_a(int par1)
    {
        if (this.mc.thePlayer.inventory.getItemStack() == null && this.theSlot != null)
        {
            for (int var2 = 0; var2 < 9; ++var2)
            {
                if (par1 == 2 + var2)
                {
                    this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, var2, 2);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        if (this.mc.thePlayer != null)
        {
            this.inventorySlots.onCraftGuiClosed(this.mc.thePlayer);
        }
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();

        if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead)
        {
            this.mc.thePlayer.closeScreen();
        }
    }
}
