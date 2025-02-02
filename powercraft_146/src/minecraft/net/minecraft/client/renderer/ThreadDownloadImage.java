package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;

@SideOnly(Side.CLIENT)
class ThreadDownloadImage extends Thread
{
    /** The URL of the image to download. */
    final String location;

    /** The image buffer to use. */
    final IImageBuffer buffer;

    /** The image data. */
    final ThreadDownloadImageData imageData;

    ThreadDownloadImage(ThreadDownloadImageData par1, String par2Str, IImageBuffer par3IImageBuffer)
    {
        this.imageData = par1;
        this.location = par2Str;
        this.buffer = par3IImageBuffer;
    }

    public void run()
    {
        HttpURLConnection var1 = null;

        try
        {
            URL var2 = new URL(this.location);
            var1 = (HttpURLConnection)var2.openConnection();
            var1.setDoInput(true);
            var1.setDoOutput(false);
            var1.connect();

            if (var1.getResponseCode() / 100 == 4)
            {
                return;
            }

            if (this.buffer == null)
            {
                this.imageData.image = ImageIO.read(var1.getInputStream());
            }
            else
            {
                this.imageData.image = this.buffer.parseUserSkin(ImageIO.read(var1.getInputStream()));
            }
        }
        catch (Exception var6)
        {
            var6.printStackTrace();
        }
        finally
        {
            var1.disconnect();
        }
    }
}
