package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.net.URI;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiScreenDemo extends GuiScreen
{
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.controlList.clear();
        byte var1 = -16;
        this.controlList.add(new GuiButton(1, this.width / 2 - 116, this.height / 2 + 62 + var1, 114, 20, StatCollector.translateToLocal("demo.help.buy")));
        this.controlList.add(new GuiButton(2, this.width / 2 + 2, this.height / 2 + 62 + var1, 114, 20, StatCollector.translateToLocal("demo.help.later")));
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        switch (par1GuiButton.id)
        {
            case 1:
                par1GuiButton.enabled = false;

                try
                {
                    Class var2 = Class.forName("java.awt.Desktop");
                    Object var3 = var2.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
                    var2.getMethod("browse", new Class[] {URI.class}).invoke(var3, new Object[] {new URI("http://www.minecraft.net/store?source=demo")});
                }
                catch (Throwable var4)
                {
                    var4.printStackTrace();
                }

                break;
            case 2:
                this.mc.displayGuiScreen((GuiScreen)null);
                this.mc.setIngameFocus();
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
    }

    /**
     * Draws either a gradient over the background screen (when it exists) or a flat gradient over background.png
     */
    public void drawDefaultBackground()
    {
        super.drawDefaultBackground();
        int var1 = this.mc.renderEngine.getTexture("/gui/demo_bg.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(var1);
        int var2 = (this.width - 248) / 2;
        int var3 = (this.height - 166) / 2;
        this.drawTexturedModalRect(var2, var3, 0, 0, 248, 166);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        int var4 = (this.width - 248) / 2 + 10;
        int var5 = (this.height - 166) / 2 + 8;
        this.fontRenderer.drawString(StatCollector.translateToLocal("demo.help.title"), var4, var5, 2039583);
        var5 += 12;
        GameSettings var7 = this.mc.gameSettings;
        String var6 = StatCollector.translateToLocal("demo.help.movementShort");
        var6 = String.format(var6, new Object[] {Keyboard.getKeyName(var7.keyBindForward.keyCode), Keyboard.getKeyName(var7.keyBindLeft.keyCode), Keyboard.getKeyName(var7.keyBindBack.keyCode), Keyboard.getKeyName(var7.keyBindRight.keyCode)});
        this.fontRenderer.drawString(var6, var4, var5, 5197647);
        var6 = StatCollector.translateToLocal("demo.help.movementMouse");
        this.fontRenderer.drawString(var6, var4, var5 + 12, 5197647);
        var6 = StatCollector.translateToLocal("demo.help.jump");
        var6 = String.format(var6, new Object[] {Keyboard.getKeyName(var7.keyBindJump.keyCode)});
        this.fontRenderer.drawString(var6, var4, var5 + 24, 5197647);
        var6 = StatCollector.translateToLocal("demo.help.inventory");
        var6 = String.format(var6, new Object[] {Keyboard.getKeyName(var7.keyBindInventory.keyCode)});
        this.fontRenderer.drawString(var6, var4, var5 + 36, 5197647);
        this.fontRenderer.drawSplitString(StatCollector.translateToLocal("demo.help.fullWrapped"), var4, var5 + 68, 218, 2039583);
        super.drawScreen(par1, par2, par3);
    }
}
