package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class InventoryEffectRenderer extends GuiContainer
{
    private boolean field_74222_o;

    public InventoryEffectRenderer(Container par1Container)
    {
        super(par1Container);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        super.initGui();

        if (!this.mc.thePlayer.getActivePotionEffects().isEmpty())
        {
            this.guiLeft = 160 + (this.width - this.xSize - 200) / 2;
            this.field_74222_o = true;
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);

        if (this.field_74222_o)
        {
            this.displayDebuffEffects();
        }
    }

    /**
     * Displays debuff/potion effects that are currently being applied to the player
     */
    private void displayDebuffEffects()
    {
        int i = this.guiLeft - 124;
        int j = this.guiTop;
        Collection collection = this.mc.thePlayer.getActivePotionEffects();

        if (!collection.isEmpty())
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_LIGHTING);
            int k = 33;

            if (collection.size() > 5)
            {
                k = 132 / (collection.size() - 1);
            }

            for (Iterator iterator = this.mc.thePlayer.getActivePotionEffects().iterator(); iterator.hasNext(); j += k)
            {
                PotionEffect potioneffect = (PotionEffect)iterator.next();
                Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.renderEngine.bindTexture("/gui/inventory.png");
                this.drawTexturedModalRect(i, j, 0, 166, 140, 32);

                if (potion.hasStatusIcon())
                {
                    int l = potion.getStatusIconIndex();
                    this.drawTexturedModalRect(i + 6, j + 7, 0 + l % 8 * 18, 198 + l / 8 * 18, 18, 18);
                }

                String s = StatCollector.translateToLocal(potion.getName());

                if (potioneffect.getAmplifier() == 1)
                {
                    s = s + " II";
                }
                else if (potioneffect.getAmplifier() == 2)
                {
                    s = s + " III";
                }
                else if (potioneffect.getAmplifier() == 3)
                {
                    s = s + " IV";
                }

                this.fontRenderer.drawStringWithShadow(s, i + 10 + 18, j + 6, 16777215);
                String s1 = Potion.getDurationString(potioneffect);
                this.fontRenderer.drawStringWithShadow(s1, i + 10 + 18, j + 6 + 10, 8355711);
            }
        }
    }
}
