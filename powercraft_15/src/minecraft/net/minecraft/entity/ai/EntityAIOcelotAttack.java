package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class EntityAIOcelotAttack extends EntityAIBase
{
    World theWorld;
    EntityLiving theEntity;
    EntityLiving theVictim;
    int attackCountdown = 0;

    public EntityAIOcelotAttack(EntityLiving par1EntityLiving)
    {
        this.theEntity = par1EntityLiving;
        this.theWorld = par1EntityLiving.worldObj;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLiving entityliving = this.theEntity.getAttackTarget();

        if (entityliving == null)
        {
            return false;
        }
        else
        {
            this.theVictim = entityliving;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.theVictim.isEntityAlive() ? false : (this.theEntity.getDistanceSqToEntity(this.theVictim) > 225.0D ? false : !this.theEntity.getNavigator().noPath() || this.shouldExecute());
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.theVictim = null;
        this.theEntity.getNavigator().clearPathEntity();
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.theEntity.getLookHelper().setLookPositionWithEntity(this.theVictim, 30.0F, 30.0F);
        double d0 = (double)(this.theEntity.width * 2.0F * this.theEntity.width * 2.0F);
        double d1 = this.theEntity.getDistanceSq(this.theVictim.posX, this.theVictim.boundingBox.minY, this.theVictim.posZ);
        float f = 0.23F;

        if (d1 > d0 && d1 < 16.0D)
        {
            f = 0.4F;
        }
        else if (d1 < 225.0D)
        {
            f = 0.18F;
        }

        this.theEntity.getNavigator().tryMoveToEntityLiving(this.theVictim, f);
        this.attackCountdown = Math.max(this.attackCountdown - 1, 0);

        if (d1 <= d0)
        {
            if (this.attackCountdown <= 0)
            {
                this.attackCountdown = 20;
                this.theEntity.attackEntityAsMob(this.theVictim);
            }
        }
    }
}
