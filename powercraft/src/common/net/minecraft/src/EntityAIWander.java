package net.minecraft.src;

public class EntityAIWander extends EntityAIBase
{
    private EntityCreature entity;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private float speed;

    public EntityAIWander(EntityCreature par1EntityCreature, float par2)
    {
        this.entity = par1EntityCreature;
        this.speed = par2;
        this.setMutexBits(1);
    }

    public boolean shouldExecute()
    {
        if (this.entity.getAge() >= 100)
        {
            return false;
        }
        else if (this.entity.getRNG().nextInt(120) != 0)
        {
            return false;
        }
        else
        {
            Vec3 var1 = RandomPositionGenerator.findRandomTarget(this.entity, 10, 7);

            if (var1 == null)
            {
                return false;
            }
            else
            {
                this.xPosition = var1.xCoord;
                this.yPosition = var1.yCoord;
                this.zPosition = var1.zCoord;
                return true;
            }
        }
    }

    public boolean continueExecuting()
    {
        return !this.entity.getNavigator().noPath();
    }

    public void startExecuting()
    {
        this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }
}
