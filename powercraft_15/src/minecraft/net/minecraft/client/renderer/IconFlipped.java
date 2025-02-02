package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.Icon;

@SideOnly(Side.CLIENT)
public class IconFlipped implements Icon
{
    private final Icon baseIcon;
    private final boolean flipU;
    private final boolean flipV;

    public IconFlipped(Icon par1Icon, boolean par2, boolean par3)
    {
        this.baseIcon = par1Icon;
        this.flipU = par2;
        this.flipV = par3;
    }

    /**
     * Returns the X position of this icon on its texture sheet, in pixels.
     */
    public int getOriginX()
    {
        return this.baseIcon.getOriginX();
    }

    /**
     * Returns the Y position of this icon on its texture sheet, in pixels.
     */
    public int getOriginY()
    {
        return this.baseIcon.getOriginY();
    }

    /**
     * Returns the minimum U coordinate to use when rendering with this icon.
     */
    public float getMinU()
    {
        return this.flipU ? this.baseIcon.getMaxU() : this.baseIcon.getMinU();
    }

    /**
     * Returns the maximum U coordinate to use when rendering with this icon.
     */
    public float getMaxU()
    {
        return this.flipU ? this.baseIcon.getMinU() : this.baseIcon.getMaxU();
    }

    /**
     * Gets a U coordinate on the icon. 0 returns uMin and 16 returns uMax. Other arguments return in-between values.
     */
    public float getInterpolatedU(double par1)
    {
        float f = this.getMaxU() - this.getMinU();
        return this.getMinU() + f * ((float)par1 / 16.0F);
    }

    /**
     * Returns the minimum V coordinate to use when rendering with this icon.
     */
    public float getMinV()
    {
        return this.flipV ? this.baseIcon.getMinV() : this.baseIcon.getMinV();
    }

    /**
     * Returns the maximum V coordinate to use when rendering with this icon.
     */
    public float getMaxV()
    {
        return this.flipV ? this.baseIcon.getMinV() : this.baseIcon.getMaxV();
    }

    /**
     * Gets a V coordinate on the icon. 0 returns vMin and 16 returns vMax. Other arguments return in-between values.
     */
    public float getInterpolatedV(double par1)
    {
        float f = this.getMaxV() - this.getMinV();
        return this.getMinV() + f * ((float)par1 / 16.0F);
    }

    public String getIconName()
    {
        return this.baseIcon.getIconName();
    }

    /**
     * Returns the width of the texture sheet this icon is on, in pixels.
     */
    public int getSheetWidth()
    {
        return this.baseIcon.getSheetWidth();
    }

    /**
     * Returns the height of the texture sheet this icon is on, in pixels.
     */
    public int getSheetHeight()
    {
        return this.baseIcon.getSheetHeight();
    }
}
