package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;

public class Vec3Pool
{
    private final int truncateArrayResetThreshold;
    private final int minimumSize;

    /** items at and above nextFreeSpace are assumed to be available */
    private final List vec3Cache = new ArrayList();
    private int nextFreeSpace = 0;
    private int maximumSizeSinceLastTruncation = 0;
    private int resetCount = 0;

    public Vec3Pool(int par1, int par2)
    {
        this.truncateArrayResetThreshold = par1;
        this.minimumSize = par2;
    }

    /**
     * extends the pool if all vecs are currently "out"
     */
    public Vec3 getVecFromPool(double par1, double par3, double par5)
    {
        if (this.func_82589_e())
        {
            return new Vec3(this, par1, par3, par5);
        }
        else
        {
            Vec3 vec3;

            if (this.nextFreeSpace >= this.vec3Cache.size())
            {
                vec3 = new Vec3(this, par1, par3, par5);
                this.vec3Cache.add(vec3);
            }
            else
            {
                vec3 = (Vec3)this.vec3Cache.get(this.nextFreeSpace);
                vec3.setComponents(par1, par3, par5);
            }

            ++this.nextFreeSpace;
            return vec3;
        }
    }

    /**
     * Will truncate the array everyN clears to the maximum size observed since the last truncation.
     */
    public void clear()
    {
        if (!this.func_82589_e())
        {
            if (this.nextFreeSpace > this.maximumSizeSinceLastTruncation)
            {
                this.maximumSizeSinceLastTruncation = this.nextFreeSpace;
            }

            if (this.resetCount++ == this.truncateArrayResetThreshold)
            {
                int i = Math.max(this.maximumSizeSinceLastTruncation, this.vec3Cache.size() - this.minimumSize);

                while (this.vec3Cache.size() > i)
                {
                    this.vec3Cache.remove(i);
                }

                this.maximumSizeSinceLastTruncation = 0;
                this.resetCount = 0;
            }

            this.nextFreeSpace = 0;
        }
    }

    @SideOnly(Side.CLIENT)
    public void clearAndFreeCache()
    {
        if (!this.func_82589_e())
        {
            this.nextFreeSpace = 0;
            this.vec3Cache.clear();
        }
    }

    public int getPoolSize()
    {
        return this.vec3Cache.size();
    }

    public int func_82590_d()
    {
        return this.nextFreeSpace;
    }

    private boolean func_82589_e()
    {
        return this.minimumSize < 0 || this.truncateArrayResetThreshold < 0;
    }
}
