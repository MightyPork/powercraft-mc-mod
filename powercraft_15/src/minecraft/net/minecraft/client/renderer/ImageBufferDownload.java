package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;

@SideOnly(Side.CLIENT)
public class ImageBufferDownload implements IImageBuffer
{
    private int[] imageData;
    private int imageWidth;
    private int imageHeight;

    public BufferedImage parseUserSkin(BufferedImage par1BufferedImage)
    {
        if (par1BufferedImage == null)
        {
            return null;
        }
        else
        {
            this.imageWidth = 64;
            this.imageHeight = 32;
            BufferedImage bufferedimage1 = new BufferedImage(this.imageWidth, this.imageHeight, 2);
            Graphics graphics = bufferedimage1.getGraphics();
            graphics.drawImage(par1BufferedImage, 0, 0, (ImageObserver)null);
            graphics.dispose();
            this.imageData = ((DataBufferInt)bufferedimage1.getRaster().getDataBuffer()).getData();
            this.setAreaOpaque(0, 0, 32, 16);
            this.setAreaTransparent(32, 0, 64, 32);
            this.setAreaOpaque(0, 16, 64, 32);
            boolean flag = false;
            int i;
            int j;
            int k;

            for (i = 32; i < 64; ++i)
            {
                for (j = 0; j < 16; ++j)
                {
                    k = this.imageData[i + j * 64];

                    if ((k >> 24 & 255) < 128)
                    {
                        flag = true;
                    }
                }
            }

            if (!flag)
            {
                for (i = 32; i < 64; ++i)
                {
                    for (j = 0; j < 16; ++j)
                    {
                        k = this.imageData[i + j * 64];

                        if ((k >> 24 & 255) < 128)
                        {
                            flag = true;
                        }
                    }
                }
            }

            return bufferedimage1;
        }
    }

    /**
     * Makes the given area of the image transparent if it was previously completely opaque (used to remove the outer
     * layer of a skin around the head if it was saved all opaque; this would be redundant so it's assumed that the skin
     * maker is just using an image editor without an alpha channel)
     */
    private void setAreaTransparent(int par1, int par2, int par3, int par4)
    {
        if (!this.hasTransparency(par1, par2, par3, par4))
        {
            for (int i1 = par1; i1 < par3; ++i1)
            {
                for (int j1 = par2; j1 < par4; ++j1)
                {
                    this.imageData[i1 + j1 * this.imageWidth] &= 16777215;
                }
            }
        }
    }

    /**
     * Makes the given area of the image opaque
     */
    private void setAreaOpaque(int par1, int par2, int par3, int par4)
    {
        for (int i1 = par1; i1 < par3; ++i1)
        {
            for (int j1 = par2; j1 < par4; ++j1)
            {
                this.imageData[i1 + j1 * this.imageWidth] |= -16777216;
            }
        }
    }

    /**
     * Returns true if the given area of the image contains transparent pixels
     */
    private boolean hasTransparency(int par1, int par2, int par3, int par4)
    {
        for (int i1 = par1; i1 < par3; ++i1)
        {
            for (int j1 = par2; j1 < par4; ++j1)
            {
                int k1 = this.imageData[i1 + j1 * this.imageWidth];

                if ((k1 >> 24 & 255) < 128)
                {
                    return true;
                }
            }
        }

        return false;
    }
}
