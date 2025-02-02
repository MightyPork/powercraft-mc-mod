package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;

public class EntityMoveHelper
{
    /** The EntityLiving that is being moved */
    private EntityLiving entity;
    private double posX;
    private double posY;
    private double posZ;

    /** The speed at which the entity should move */
    private float speed;
    private boolean update = false;

    public EntityMoveHelper(EntityLiving par1EntityLiving)
    {
        this.entity = par1EntityLiving;
        this.posX = par1EntityLiving.posX;
        this.posY = par1EntityLiving.posY;
        this.posZ = par1EntityLiving.posZ;
    }

    public boolean isUpdating()
    {
        return this.update;
    }

    public float getSpeed()
    {
        return this.speed;
    }

    /**
     * Sets the speed and location to move to
     */
    public void setMoveTo(double par1, double par3, double par5, float par7)
    {
        this.posX = par1;
        this.posY = par3;
        this.posZ = par5;
        this.speed = par7;
        this.update = true;
    }

    public void onUpdateMoveHelper()
    {
        this.entity.setMoveForward(0.0F);

        if (this.update)
        {
            this.update = false;
            int i = MathHelper.floor_double(this.entity.boundingBox.minY + 0.5D);
            double d0 = this.posX - this.entity.posX;
            double d1 = this.posZ - this.entity.posZ;
            double d2 = this.posY - (double)i;
            double d3 = d0 * d0 + d2 * d2 + d1 * d1;

            if (d3 >= 2.500000277905201E-7D)
            {
                float f = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
                this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, f, 30.0F);
                this.entity.setAIMoveSpeed(this.speed * this.entity.getSpeedModifier());

                if (d2 > 0.0D && d0 * d0 + d1 * d1 < 1.0D)
                {
                    this.entity.getJumpHelper().setJumping();
                }
            }
        }
    }

    /**
     * Limits the given angle to a upper and lower limit.
     */
    private float limitAngle(float par1, float par2, float par3)
    {
        float f3 = MathHelper.wrapAngleTo180_float(par2 - par1);

        if (f3 > par3)
        {
            f3 = par3;
        }

        if (f3 < -par3)
        {
            f3 = -par3;
        }

        return par1 + f3;
    }
}
