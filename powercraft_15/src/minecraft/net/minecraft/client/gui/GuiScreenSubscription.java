package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.mco.McoServer;
import net.minecraft.client.mco.ValueObjectSubscription;
import net.minecraft.util.StringTranslate;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiScreenSubscription extends GuiScreen
{
    private final GuiScreen field_98067_a;
    private final McoServer field_98065_b;
    private final int field_98066_c = 0;
    private final int field_98064_d = 1;
    private int field_98068_n;
    private String field_98069_o;

    public GuiScreenSubscription(GuiScreen par1GuiScreen, McoServer par2McoServer)
    {
        this.field_98067_a = par1GuiScreen;
        this.field_98065_b = par2McoServer;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {}

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.func_98063_a(this.field_98065_b.field_96408_a);
        StringTranslate stringtranslate = StringTranslate.getInstance();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, stringtranslate.translateKey("gui.cancel")));
    }

    private void func_98063_a(long par1)
    {
        McoClient mcoclient = new McoClient(this.mc.session);

        try
        {
            ValueObjectSubscription valueobjectsubscription = mcoclient.func_98177_f(par1);
            this.field_98068_n = valueobjectsubscription.field_98170_b;
            this.field_98069_o = this.func_98062_b(valueobjectsubscription.field_98171_a);
        }
        catch (ExceptionMcoService exceptionmcoservice)
        {
            ;
        }
        catch (IOException ioexception)
        {
            ;
        }
    }

    private String func_98062_b(long par1)
    {
        GregorianCalendar gregoriancalendar = new GregorianCalendar(TimeZone.getDefault());
        gregoriancalendar.setTimeInMillis(par1);
        return SimpleDateFormat.getDateTimeInstance().format(gregoriancalendar.getTime());
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
            if (par1GuiButton.id == 0)
            {
                this.mc.displayGuiScreen(this.field_98067_a);
            }
            else if (par1GuiButton.id == 1)
            {
                ;
            }
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2) {}

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, stringtranslate.translateKey("mco.configure.world.subscription.title"), this.width / 2, 17, 16777215);
        this.drawString(this.fontRenderer, stringtranslate.translateKey("mco.configure.world.subscription.start"), this.width / 2 - 100, 53, 10526880);
        this.drawString(this.fontRenderer, this.field_98069_o, this.width / 2 - 100, 66, 16777215);
        this.drawString(this.fontRenderer, stringtranslate.translateKey("mco.configure.world.subscription.daysleft"), this.width / 2 - 100, 85, 10526880);
        this.drawString(this.fontRenderer, String.valueOf(this.field_98068_n), this.width / 2 - 100, 98, 16777215);
        super.drawScreen(par1, par2, par3);
    }
}
