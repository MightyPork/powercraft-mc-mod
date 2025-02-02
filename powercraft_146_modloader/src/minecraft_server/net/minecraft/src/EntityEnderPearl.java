package net.minecraft.src;

public class EntityEnderPearl extends EntityThrowable
{
    public EntityEnderPearl(World par1World)
    {
        super(par1World);
    }

    public EntityEnderPearl(World par1World, EntityLiving par2EntityLiving)
    {
        super(par1World, par2EntityLiving);
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(MovingObjectPosition par1MovingObjectPosition)
    {
        if (par1MovingObjectPosition.entityHit != null)
        {
            par1MovingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.func_85052_h()), 0);
        }

        for (int var2 = 0; var2 < 32; ++var2)
        {
            this.worldObj.spawnParticle("portal", this.posX, this.posY + this.rand.nextDouble() * 2.0D, this.posZ, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian());
        }

        if (!this.worldObj.isRemote)
        {
            if (this.func_85052_h() != null && this.func_85052_h() instanceof EntityPlayerMP)
            {
                EntityPlayerMP var3 = (EntityPlayerMP)this.func_85052_h();

                if (!var3.playerNetServerHandler.connectionClosed && var3.worldObj == this.worldObj)
                {
                    this.func_85052_h().setPositionAndUpdate(this.posX, this.posY, this.posZ);
                    this.func_85052_h().fallDistance = 0.0F;
                    this.func_85052_h().attackEntityFrom(DamageSource.fall, 5);
                }
            }

            this.setDead();
        }
    }
}
