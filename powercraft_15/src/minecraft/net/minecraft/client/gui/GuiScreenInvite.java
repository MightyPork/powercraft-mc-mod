package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.mco.McoServer;
import net.minecraft.util.StringTranslate;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiScreenInvite extends GuiScreen
{
    private GuiTextField field_96227_a;
    private McoServer field_96223_b;
    private final GuiScreen field_96224_c;
    private final GuiScreenConfigureWorld field_96222_d;
    private final int field_96228_n = 0;
    private final int field_96229_o = 1;
    private String field_101016_p = "Could not invite the provided name";
    private String field_96226_p;
    private boolean field_96225_q = false;

    public GuiScreenInvite(GuiScreen par1GuiScreen, GuiScreenConfigureWorld par2GuiScreenConfigureWorld, McoServer par3McoServer)
    {
        this.field_96224_c = par1GuiScreen;
        this.field_96222_d = par2GuiScreenConfigureWorld;
        this.field_96223_b = par3McoServer;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.field_96227_a.updateCursorCounter();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, stringtranslate.translateKey("mco.configure.world.buttons.invite")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, stringtranslate.translateKey("gui.cancel")));
        this.field_96227_a = new GuiTextField(this.fontRenderer, this.width / 2 - 100, 66, 200, 20);
        this.field_96227_a.setFocused(true);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
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
                this.mc.displayGuiScreen(this.field_96222_d);
            }
            else if (par1GuiButton.id == 0)
            {
                McoClient mcoclient = new McoClient(this.mc.session);

                try
                {
                    McoServer mcoserver = mcoclient.func_96387_b(this.field_96223_b.field_96408_a, this.field_96227_a.getText());

                    if (mcoserver != null)
                    {
                        this.field_96223_b.field_96402_f = mcoserver.field_96402_f;
                        this.mc.displayGuiScreen(new GuiScreenConfigureWorld(this.field_96224_c, this.field_96223_b));
                    }
                    else
                    {
                        this.func_101015_a(this.field_101016_p);
                    }
                }
                catch (ExceptionMcoService exceptionmcoservice)
                {
                    this.func_101015_a(exceptionmcoservice.field_96391_b);
                }
                catch (IOException ioexception)
                {
                    this.func_101015_a(this.field_101016_p);
                }
            }
        }
    }

    private void func_101015_a(String par1Str)
    {
        this.field_96225_q = true;
        this.field_96226_p = par1Str;
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        this.field_96227_a.textboxKeyTyped(par1, par2);

        if (par1 == 9)
        {
            if (this.field_96227_a.isFocused())
            {
                this.field_96227_a.setFocused(false);
            }
            else
            {
                this.field_96227_a.setFocused(true);
            }
        }

        if (par1 == 13)
        {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.field_96227_a.mouseClicked(par1, par2, par3);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, stringtranslate.translateKey(""), this.width / 2, 17, 16777215);
        this.drawString(this.fontRenderer, stringtranslate.translateKey("mco.configure.world.invite.profile.name"), this.width / 2 - 100, 53, 10526880);

        if (this.field_96225_q)
        {
            this.drawCenteredString(this.fontRenderer, this.field_96226_p, this.width / 2, 100, 16711680);
        }

        this.field_96227_a.drawTextBox();
        super.drawScreen(par1, par2, par3);
    }
}
