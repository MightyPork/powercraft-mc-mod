package net.minecraft.client.renderer.texture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class TextureCompass extends TextureStitched
{
    public static TextureCompass compassTexture;

    /** Current compass heading in radians */
    public double currentAngle;

    /** Speed and direction of compass rotation */
    public double angleDelta;

    public TextureCompass()
    {
        super("compass");
        compassTexture = this;
    }

    public void updateAnimation()
    {
        Minecraft minecraft = Minecraft.getMinecraft();

        if (minecraft.theWorld != null && minecraft.thePlayer != null)
        {
            this.updateCompass(minecraft.theWorld, minecraft.thePlayer.posX, minecraft.thePlayer.posZ, (double)minecraft.thePlayer.rotationYaw, false, false);
        }
        else
        {
            this.updateCompass((World)null, 0.0D, 0.0D, 0.0D, true, false);
        }
    }

    /**
     * Updates the compass based on the given x,z coords and camera direction
     */
    public void updateCompass(World par1World, double par2, double par4, double par6, boolean par8, boolean par9)
    {
        double d3 = 0.0D;

        if (par1World != null && !par8)
        {
            ChunkCoordinates chunkcoordinates = par1World.getSpawnPoint();
            double d4 = (double)chunkcoordinates.posX - par2;
            double d5 = (double)chunkcoordinates.posZ - par4;
            par6 %= 360.0D;
            d3 = -((par6 - 90.0D) * Math.PI / 180.0D - Math.atan2(d5, d4));

            if (!par1World.provider.isSurfaceWorld())
            {
                d3 = Math.random() * Math.PI * 2.0D;
            }
        }

        if (par9)
        {
            this.currentAngle = d3;
        }
        else
        {
            double d6;

            for (d6 = d3 - this.currentAngle; d6 < -Math.PI; d6 += (Math.PI * 2D))
            {
                ;
            }

            while (d6 >= Math.PI)
            {
                d6 -= (Math.PI * 2D);
            }

            if (d6 < -1.0D)
            {
                d6 = -1.0D;
            }

            if (d6 > 1.0D)
            {
                d6 = 1.0D;
            }

            this.angleDelta += d6 * 0.1D;
            this.angleDelta *= 0.8D;
            this.currentAngle += this.angleDelta;
        }

        int i;

        for (i = (int)((this.currentAngle / (Math.PI * 2D) + 1.0D) * (double)this.textureList.size()) % this.textureList.size(); i < 0; i = (i + this.textureList.size()) % this.textureList.size())
        {
            ;
        }

        if (i != this.frameCounter)
        {
            this.frameCounter = i;
            this.textureSheet.copyFrom(this.originX, this.originY, (Texture)this.textureList.get(this.frameCounter), false); //FML: We rotate the textures in init.
        }
    }
}
