package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.StringTranslate;

@SideOnly(Side.CLIENT)
public class GuiConfirmOpenLink extends GuiYesNo
{
    /** Text to warn players from opening unsafe links. */
    private String openLinkWarning;

    /** Label for the Copy to Clipboard button. */
    private String copyLinkButtonText;
    private String field_92028_p;
    private boolean field_92027_q = true;

    public GuiConfirmOpenLink(GuiScreen par1GuiScreen, String par2Str, int par3)
    {
        super(par1GuiScreen, StringTranslate.getInstance().translateKey("chat.link.confirm"), par2Str, par3);
        StringTranslate var4 = StringTranslate.getInstance();
        this.buttonText1 = var4.translateKey("gui.yes");
        this.buttonText2 = var4.translateKey("gui.no");
        this.copyLinkButtonText = var4.translateKey("chat.copy");
        this.openLinkWarning = var4.translateKey("chat.link.warning");
        this.field_92028_p = par2Str;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.controlList.add(new GuiButton(0, this.width / 3 - 83 + 0, this.height / 6 + 96, 100, 20, this.buttonText1));
        this.controlList.add(new GuiButton(2, this.width / 3 - 83 + 105, this.height / 6 + 96, 100, 20, this.copyLinkButtonText));
        this.controlList.add(new GuiButton(1, this.width / 3 - 83 + 210, this.height / 6 + 96, 100, 20, this.buttonText2));
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.id == 2)
        {
            this.copyLinkToClipboard();
        }

        this.parentScreen.confirmClicked(par1GuiButton.id == 0, this.worldNumber);
    }

    /**
     * Copies the link to the system clipboard.
     */
    public void copyLinkToClipboard()
    {
        setClipboardString(this.field_92028_p);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);

        if (this.field_92027_q)
        {
            this.drawCenteredString(this.fontRenderer, this.openLinkWarning, this.width / 2, 110, 16764108);
        }
    }

    public void func_92026_h()
    {
        this.field_92027_q = false;
    }
}
