package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.StringTranslate;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiScreenServerList extends GuiScreen
{
    /**
     * Remembers the last hostname or IP address entered into text field between invocations of the GUI.
     */
    private static String lastServerName = "";

    /** Needed a change as a local variable was conflicting on construct */
    private final GuiScreen guiScreen;

    /** Instance of ServerData. */
    private final ServerData theServerData;
    private GuiTextField serverTextField;

    public GuiScreenServerList(GuiScreen par1GuiScreen, ServerData par2ServerData)
    {
        this.guiScreen = par1GuiScreen;
        this.theServerData = par2ServerData;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.serverTextField.updateCursorCounter();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        StringTranslate var1 = StringTranslate.getInstance();
        Keyboard.enableRepeatEvents(true);
        this.controlList.clear();
        this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, var1.translateKey("selectServer.select")));
        this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, var1.translateKey("gui.cancel")));
        this.serverTextField = new GuiTextField(this.fontRenderer, this.width / 2 - 100, 116, 200, 20);
        this.serverTextField.setMaxStringLength(128);
        this.serverTextField.setFocused(true);
        this.serverTextField.setText(lastServerName);
        ((GuiButton)this.controlList.get(0)).enabled = this.serverTextField.getText().length() > 0 && this.serverTextField.getText().split(":").length > 0;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        lastServerName = this.serverTextField.getText();
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == 1)
            {
                this.guiScreen.confirmClicked(false, 0);
            }
            else if (par1GuiButton.id == 0)
            {
                this.theServerData.serverIP = this.serverTextField.getText();
                this.guiScreen.confirmClicked(true, 0);
            }
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (this.serverTextField.textboxKeyTyped(par1, par2))
        {
            ((GuiButton)this.controlList.get(0)).enabled = this.serverTextField.getText().length() > 0 && this.serverTextField.getText().split(":").length > 0;
        }
        else if (par2 == 28)
        {
            this.actionPerformed((GuiButton)this.controlList.get(0));
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.serverTextField.mouseClicked(par1, par2, par3);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        StringTranslate var4 = StringTranslate.getInstance();
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, var4.translateKey("selectServer.direct"), this.width / 2, this.height / 4 - 60 + 20, 16777215);
        this.drawString(this.fontRenderer, var4.translateKey("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
        this.serverTextField.drawTextBox();
        super.drawScreen(par1, par2, par3);
    }
}
