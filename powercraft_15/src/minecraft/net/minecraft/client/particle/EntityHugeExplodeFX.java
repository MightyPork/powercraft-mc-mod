package net.minecraft.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntityHugeExplodeFX extends EntityFX
{
    private int timeSinceStart = 0;

    /** the maximum time for the explosion */
    private int maximumTime = 0;

    public EntityHugeExplodeFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        super(par1World, par2, par4, par6, 0.0D, 0.0D, 0.0D);
        this.maximumTime = 8;
    }

    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7) {}

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        for (int i = 0; i < 6; ++i)
        {
            double d0 = this.posX + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
            double d1 = this.posY + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
            double d2 = this.posZ + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
            this.worldObj.spawnParticle("largeexplode", d0, d1, d2, (double)((float)this.timeSinceStart / (float)this.maximumTime), 0.0D, 0.0D);
        }

        ++this.timeSinceStart;

        if (this.timeSinceStart == this.maximumTime)
        {
            this.setDead();
        }
    }

    public int getFXLayer()
    {
        return 1;
    }
}
