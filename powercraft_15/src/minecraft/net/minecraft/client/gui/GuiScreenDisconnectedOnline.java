package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.StringTranslate;

@SideOnly(Side.CLIENT)
public class GuiScreenDisconnectedOnline extends GuiScreen
{
    private String field_98113_a;
    private String field_98111_b;
    private Object[] field_98112_c;
    private List field_98110_d;
    private final GuiScreen field_98114_n;

    public GuiScreenDisconnectedOnline(GuiScreen par1GuiScreen, String par2Str, String par3Str, Object ... par4ArrayOfObj)
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        this.field_98114_n = par1GuiScreen;
        this.field_98113_a = stringtranslate.translateKey(par2Str);
        this.field_98111_b = par3Str;
        this.field_98112_c = par4ArrayOfObj;
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2) {}

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, stringtranslate.translateKey("gui.back")));

        if (this.field_98112_c != null)
        {
            this.field_98110_d = this.fontRenderer.listFormattedStringToWidth(stringtranslate.translateKeyFormat(this.field_98111_b, this.field_98112_c), this.width - 50);
        }
        else
        {
            this.field_98110_d = this.fontRenderer.listFormattedStringToWidth(stringtranslate.translateKey(this.field_98111_b), this.width - 50);
        }
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.id == 0)
        {
            this.mc.displayGuiScreen(this.field_98114_n);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.field_98113_a, this.width / 2, this.height / 2 - 50, 11184810);
        int k = this.height / 2 - 30;

        if (this.field_98110_d != null)
        {
            for (Iterator iterator = this.field_98110_d.iterator(); iterator.hasNext(); k += this.fontRenderer.FONT_HEIGHT)
            {
                String s = (String)iterator.next();
                this.drawCenteredString(this.fontRenderer, s, this.width / 2, k, 16777215);
            }
        }

        super.drawScreen(par1, par2, par3);
    }
}
