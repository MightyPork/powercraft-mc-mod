package net.minecraft.src;

public class EntityMoveHelper
{
    private EntityLiving entity;
    private double posX;
    private double posY;
    private double posZ;

    private float speed;
    private boolean field_75643_f = false;

    public EntityMoveHelper(EntityLiving par1EntityLiving)
    {
        this.entity = par1EntityLiving;
        this.posX = par1EntityLiving.posX;
        this.posY = par1EntityLiving.posY;
        this.posZ = par1EntityLiving.posZ;
    }

    public boolean func_75640_a()
    {
        return this.field_75643_f;
    }

    public float getSpeed()
    {
        return this.speed;
    }

    public void setMoveTo(double par1, double par3, double par5, float par7)
    {
        this.posX = par1;
        this.posY = par3;
        this.posZ = par5;
        this.speed = par7;
        this.field_75643_f = true;
    }

    public void onUpdateMoveHelper()
    {
        this.entity.setMoveForward(0.0F);

        if (this.field_75643_f)
        {
            this.field_75643_f = false;
            int var1 = MathHelper.floor_double(this.entity.boundingBox.minY + 0.5D);
            double var2 = this.posX - this.entity.posX;
            double var4 = this.posZ - this.entity.posZ;
            double var6 = this.posY - (double)var1;
            double var8 = var2 * var2 + var6 * var6 + var4 * var4;

            if (var8 >= 2.500000277905201E-7D)
            {
                float var10 = (float)(Math.atan2(var4, var2) * 180.0D / Math.PI) - 90.0F;
                this.entity.rotationYaw = this.func_75639_a(this.entity.rotationYaw, var10, 30.0F);
                this.entity.setAIMoveSpeed(this.speed * this.entity.getSpeedModifier());

                if (var6 > 0.0D && var2 * var2 + var4 * var4 < 1.0D)
                {
                    this.entity.getJumpHelper().setJumping();
                }
            }
        }
    }

    private float func_75639_a(float par1, float par2, float par3)
    {
        float var4 = MathHelper.wrapAngleTo180_float(par2 - par1);

        if (var4 > par3)
        {
            var4 = par3;
        }

        if (var4 < -par3)
        {
            var4 = -par3;
        }

        return par1 + var4;
    }
}
