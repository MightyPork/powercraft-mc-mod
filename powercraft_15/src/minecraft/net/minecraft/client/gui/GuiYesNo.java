package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.StringTranslate;

@SideOnly(Side.CLIENT)
public class GuiYesNo extends GuiScreen
{
    /**
     * A reference to the screen object that created this. Used for navigating between screens.
     */
    protected GuiScreen parentScreen;

    /** First line of text. */
    protected String message1;

    /** Second line of text. */
    private String message2;

    /** The text shown for the first button in GuiYesNo */
    protected String buttonText1;

    /** The text shown for the second button in GuiYesNo */
    protected String buttonText2;

    /** World number to be deleted. */
    protected int worldNumber;

    public GuiYesNo(GuiScreen par1GuiScreen, String par2Str, String par3Str, int par4)
    {
        this.parentScreen = par1GuiScreen;
        this.message1 = par2Str;
        this.message2 = par3Str;
        this.worldNumber = par4;
        StringTranslate stringtranslate = StringTranslate.getInstance();
        this.buttonText1 = stringtranslate.translateKey("gui.yes");
        this.buttonText2 = stringtranslate.translateKey("gui.no");
    }

    public GuiYesNo(GuiScreen par1GuiScreen, String par2Str, String par3Str, String par4Str, String par5Str, int par6)
    {
        this.parentScreen = par1GuiScreen;
        this.message1 = par2Str;
        this.message2 = par3Str;
        this.buttonText1 = par4Str;
        this.buttonText2 = par5Str;
        this.worldNumber = par6;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.add(new GuiSmallButton(0, this.width / 2 - 155, this.height / 6 + 96, this.buttonText1));
        this.buttonList.add(new GuiSmallButton(1, this.width / 2 - 155 + 160, this.height / 6 + 96, this.buttonText2));
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        this.parentScreen.confirmClicked(par1GuiButton.id == 0, this.worldNumber);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.message1, this.width / 2, 70, 16777215);
        this.drawCenteredString(this.fontRenderer, this.message2, this.width / 2, 90, 16777215);
        super.drawScreen(par1, par2, par3);
    }
}
