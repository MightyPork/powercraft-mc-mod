package net.minecraft.entity.boss;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityDragon extends EntityLiving implements IBossDisplayData, IEntityMultiPart
{
    public double targetX;
    public double targetY;
    public double targetZ;

    /**
     * Ring buffer array for the last 64 Y-positions and yaw rotations. Used to calculate offsets for the animations.
     */
    public double[][] ringBuffer = new double[64][3];

    /**
     * Index into the ring buffer. Incremented once per tick and restarts at 0 once it reaches the end of the buffer.
     */
    public int ringBufferIndex = -1;

    /** An array containing all body parts of this dragon */
    public EntityDragonPart[] dragonPartArray;

    /** The head bounding box of a dragon */
    public EntityDragonPart dragonPartHead;

    /** The body bounding box of a dragon */
    public EntityDragonPart dragonPartBody;
    public EntityDragonPart dragonPartTail1;
    public EntityDragonPart dragonPartTail2;
    public EntityDragonPart dragonPartTail3;
    public EntityDragonPart dragonPartWing1;
    public EntityDragonPart dragonPartWing2;

    /** Animation time at previous tick. */
    public float prevAnimTime = 0.0F;

    /**
     * Animation time, used to control the speed of the animation cycles (wings flapping, jaw opening, etc.)
     */
    public float animTime = 0.0F;

    /** Force selecting a new flight target at next tick if set to true. */
    public boolean forceNewTarget = false;

    /**
     * Activated if the dragon is flying though obsidian, white stone or bedrock. Slows movement and animation speed.
     */
    public boolean slowed = false;
    private Entity target;
    public int deathTicks = 0;

    /** The current endercrystal that is healing this dragon */
    public EntityEnderCrystal healingEnderCrystal = null;

    public EntityDragon(World par1World)
    {
        super(par1World);
        this.dragonPartArray = new EntityDragonPart[] {this.dragonPartHead = new EntityDragonPart(this, "head", 6.0F, 6.0F), this.dragonPartBody = new EntityDragonPart(this, "body", 8.0F, 8.0F), this.dragonPartTail1 = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.dragonPartTail2 = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.dragonPartTail3 = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.dragonPartWing1 = new EntityDragonPart(this, "wing", 4.0F, 4.0F), this.dragonPartWing2 = new EntityDragonPart(this, "wing", 4.0F, 4.0F)};
        this.setEntityHealth(this.getMaxHealth());
        this.texture = "/mob/enderdragon/ender.png";
        this.setSize(16.0F, 8.0F);
        this.noClip = true;
        this.isImmuneToFire = true;
        this.targetY = 100.0D;
        this.ignoreFrustumCheck = true;
    }

    public int getMaxHealth()
    {
        return 200;
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, new Integer(this.getMaxHealth()));
    }

    /**
     * Returns a double[3] array with movement offsets, used to calculate trailing tail/neck positions. [0] = yaw
     * offset, [1] = y offset, [2] = unused, always 0. Parameters: buffer index offset, partial ticks.
     */
    public double[] getMovementOffsets(int par1, float par2)
    {
        if (this.health <= 0)
        {
            par2 = 0.0F;
        }

        par2 = 1.0F - par2;
        int j = this.ringBufferIndex - par1 * 1 & 63;
        int k = this.ringBufferIndex - par1 * 1 - 1 & 63;
        double[] adouble = new double[3];
        double d0 = this.ringBuffer[j][0];
        double d1 = MathHelper.wrapAngleTo180_double(this.ringBuffer[k][0] - d0);
        adouble[0] = d0 + d1 * (double)par2;
        d0 = this.ringBuffer[j][1];
        d1 = this.ringBuffer[k][1] - d0;
        adouble[1] = d0 + d1 * (double)par2;
        adouble[2] = this.ringBuffer[j][2] + (this.ringBuffer[k][2] - this.ringBuffer[j][2]) * (double)par2;
        return adouble;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        float f;
        float f1;

        if (!this.worldObj.isRemote)
        {
            this.dataWatcher.updateObject(16, Integer.valueOf(this.health));
        }
        else
        {
            f = MathHelper.cos(this.animTime * (float)Math.PI * 2.0F);
            f1 = MathHelper.cos(this.prevAnimTime * (float)Math.PI * 2.0F);

            if (f1 <= -0.3F && f >= -0.3F)
            {
                this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.enderdragon.wings", 5.0F, 0.8F + this.rand.nextFloat() * 0.3F, false);
            }
        }

        this.prevAnimTime = this.animTime;
        float f2;

        if (this.health <= 0)
        {
            f = (this.rand.nextFloat() - 0.5F) * 8.0F;
            f1 = (this.rand.nextFloat() - 0.5F) * 4.0F;
            f2 = (this.rand.nextFloat() - 0.5F) * 8.0F;
            this.worldObj.spawnParticle("largeexplode", this.posX + (double)f, this.posY + 2.0D + (double)f1, this.posZ + (double)f2, 0.0D, 0.0D, 0.0D);
        }
        else
        {
            this.updateDragonEnderCrystal();
            f = 0.2F / (MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 10.0F + 1.0F);
            f *= (float)Math.pow(2.0D, this.motionY);

            if (this.slowed)
            {
                this.animTime += f * 0.5F;
            }
            else
            {
                this.animTime += f;
            }

            this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);

            if (this.ringBufferIndex < 0)
            {
                for (int i = 0; i < this.ringBuffer.length; ++i)
                {
                    this.ringBuffer[i][0] = (double)this.rotationYaw;
                    this.ringBuffer[i][1] = this.posY;
                }
            }

            if (++this.ringBufferIndex == this.ringBuffer.length)
            {
                this.ringBufferIndex = 0;
            }

            this.ringBuffer[this.ringBufferIndex][0] = (double)this.rotationYaw;
            this.ringBuffer[this.ringBufferIndex][1] = this.posY;
            double d0;
            double d1;
            double d2;
            double d3;
            float f3;

            if (this.worldObj.isRemote)
            {
                if (this.newPosRotationIncrements > 0)
                {
                    d3 = this.posX + (this.newPosX - this.posX) / (double)this.newPosRotationIncrements;
                    d0 = this.posY + (this.newPosY - this.posY) / (double)this.newPosRotationIncrements;
                    d1 = this.posZ + (this.newPosZ - this.posZ) / (double)this.newPosRotationIncrements;
                    d2 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double)this.rotationYaw);
                    this.rotationYaw = (float)((double)this.rotationYaw + d2 / (double)this.newPosRotationIncrements);
                    this.rotationPitch = (float)((double)this.rotationPitch + (this.newRotationPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
                    --this.newPosRotationIncrements;
                    this.setPosition(d3, d0, d1);
                    this.setRotation(this.rotationYaw, this.rotationPitch);
                }
            }
            else
            {
                d3 = this.targetX - this.posX;
                d0 = this.targetY - this.posY;
                d1 = this.targetZ - this.posZ;
                d2 = d3 * d3 + d0 * d0 + d1 * d1;

                if (this.target != null)
                {
                    this.targetX = this.target.posX;
                    this.targetZ = this.target.posZ;
                    double d4 = this.targetX - this.posX;
                    double d5 = this.targetZ - this.posZ;
                    double d6 = Math.sqrt(d4 * d4 + d5 * d5);
                    double d7 = 0.4000000059604645D + d6 / 80.0D - 1.0D;

                    if (d7 > 10.0D)
                    {
                        d7 = 10.0D;
                    }

                    this.targetY = this.target.boundingBox.minY + d7;
                }
                else
                {
                    this.targetX += this.rand.nextGaussian() * 2.0D;
                    this.targetZ += this.rand.nextGaussian() * 2.0D;
                }

                if (this.forceNewTarget || d2 < 100.0D || d2 > 22500.0D || this.isCollidedHorizontally || this.isCollidedVertically)
                {
                    this.setNewTarget();
                }

                d0 /= (double)MathHelper.sqrt_double(d3 * d3 + d1 * d1);
                f3 = 0.6F;

                if (d0 < (double)(-f3))
                {
                    d0 = (double)(-f3);
                }

                if (d0 > (double)f3)
                {
                    d0 = (double)f3;
                }

                this.motionY += d0 * 0.10000000149011612D;
                this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);
                double d8 = 180.0D - Math.atan2(d3, d1) * 180.0D / Math.PI;
                double d9 = MathHelper.wrapAngleTo180_double(d8 - (double)this.rotationYaw);

                if (d9 > 50.0D)
                {
                    d9 = 50.0D;
                }

                if (d9 < -50.0D)
                {
                    d9 = -50.0D;
                }

                Vec3 vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.targetX - this.posX, this.targetY - this.posY, this.targetZ - this.posZ).normalize();
                Vec3 vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool((double)MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F), this.motionY, (double)(-MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F))).normalize();
                float f4 = (float)(vec31.dotProduct(vec3) + 0.5D) / 1.5F;

                if (f4 < 0.0F)
                {
                    f4 = 0.0F;
                }

                this.randomYawVelocity *= 0.8F;
                float f5 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0F + 1.0F;
                double d10 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0D + 1.0D;

                if (d10 > 40.0D)
                {
                    d10 = 40.0D;
                }

                this.randomYawVelocity = (float)((double)this.randomYawVelocity + d9 * (0.699999988079071D / d10 / (double)f5));
                this.rotationYaw += this.randomYawVelocity * 0.1F;
                float f6 = (float)(2.0D / (d10 + 1.0D));
                float f7 = 0.06F;
                this.moveFlying(0.0F, -1.0F, f7 * (f4 * f6 + (1.0F - f6)));

                if (this.slowed)
                {
                    this.moveEntity(this.motionX * 0.800000011920929D, this.motionY * 0.800000011920929D, this.motionZ * 0.800000011920929D);
                }
                else
                {
                    this.moveEntity(this.motionX, this.motionY, this.motionZ);
                }

                Vec3 vec32 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.motionX, this.motionY, this.motionZ).normalize();
                float f8 = (float)(vec32.dotProduct(vec31) + 1.0D) / 2.0F;
                f8 = 0.8F + 0.15F * f8;
                this.motionX *= (double)f8;
                this.motionZ *= (double)f8;
                this.motionY *= 0.9100000262260437D;
            }

            this.renderYawOffset = this.rotationYaw;
            this.dragonPartHead.width = this.dragonPartHead.height = 3.0F;
            this.dragonPartTail1.width = this.dragonPartTail1.height = 2.0F;
            this.dragonPartTail2.width = this.dragonPartTail2.height = 2.0F;
            this.dragonPartTail3.width = this.dragonPartTail3.height = 2.0F;
            this.dragonPartBody.height = 3.0F;
            this.dragonPartBody.width = 5.0F;
            this.dragonPartWing1.height = 2.0F;
            this.dragonPartWing1.width = 4.0F;
            this.dragonPartWing2.height = 3.0F;
            this.dragonPartWing2.width = 4.0F;
            f1 = (float)(this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F / 180.0F * (float)Math.PI;
            f2 = MathHelper.cos(f1);
            float f9 = -MathHelper.sin(f1);
            float f10 = this.rotationYaw * (float)Math.PI / 180.0F;
            float f11 = MathHelper.sin(f10);
            float f12 = MathHelper.cos(f10);
            this.dragonPartBody.onUpdate();
            this.dragonPartBody.setLocationAndAngles(this.posX + (double)(f11 * 0.5F), this.posY, this.posZ - (double)(f12 * 0.5F), 0.0F, 0.0F);
            this.dragonPartWing1.onUpdate();
            this.dragonPartWing1.setLocationAndAngles(this.posX + (double)(f12 * 4.5F), this.posY + 2.0D, this.posZ + (double)(f11 * 4.5F), 0.0F, 0.0F);
            this.dragonPartWing2.onUpdate();
            this.dragonPartWing2.setLocationAndAngles(this.posX - (double)(f12 * 4.5F), this.posY + 2.0D, this.posZ - (double)(f11 * 4.5F), 0.0F, 0.0F);

            if (!this.worldObj.isRemote && this.hurtTime == 0)
            {
                this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing1.boundingBox.expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
                this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing2.boundingBox.expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
                this.attackEntitiesInList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartHead.boundingBox.expand(1.0D, 1.0D, 1.0D)));
            }

            double[] adouble = this.getMovementOffsets(5, 1.0F);
            double[] adouble1 = this.getMovementOffsets(0, 1.0F);
            f3 = MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
            float f13 = MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
            this.dragonPartHead.onUpdate();
            this.dragonPartHead.setLocationAndAngles(this.posX + (double)(f3 * 5.5F * f2), this.posY + (adouble1[1] - adouble[1]) * 1.0D + (double)(f9 * 5.5F), this.posZ - (double)(f13 * 5.5F * f2), 0.0F, 0.0F);

            for (int j = 0; j < 3; ++j)
            {
                EntityDragonPart entitydragonpart = null;

                if (j == 0)
                {
                    entitydragonpart = this.dragonPartTail1;
                }

                if (j == 1)
                {
                    entitydragonpart = this.dragonPartTail2;
                }

                if (j == 2)
                {
                    entitydragonpart = this.dragonPartTail3;
                }

                double[] adouble2 = this.getMovementOffsets(12 + j * 2, 1.0F);
                float f14 = this.rotationYaw * (float)Math.PI / 180.0F + this.simplifyAngle(adouble2[0] - adouble[0]) * (float)Math.PI / 180.0F * 1.0F;
                float f15 = MathHelper.sin(f14);
                float f16 = MathHelper.cos(f14);
                float f17 = 1.5F;
                float f18 = (float)(j + 1) * 2.0F;
                entitydragonpart.onUpdate();
                entitydragonpart.setLocationAndAngles(this.posX - (double)((f11 * f17 + f15 * f18) * f2), this.posY + (adouble2[1] - adouble[1]) * 1.0D - (double)((f18 + f17) * f9) + 1.5D, this.posZ + (double)((f12 * f17 + f16 * f18) * f2), 0.0F, 0.0F);
            }

            if (!this.worldObj.isRemote)
            {
                this.slowed = this.destroyBlocksInAABB(this.dragonPartHead.boundingBox) | this.destroyBlocksInAABB(this.dragonPartBody.boundingBox);
            }
        }
    }

    /**
     * Updates the state of the enderdragon's current endercrystal.
     */
    private void updateDragonEnderCrystal()
    {
        if (this.healingEnderCrystal != null)
        {
            if (this.healingEnderCrystal.isDead)
            {
                if (!this.worldObj.isRemote)
                {
                    this.attackEntityFromPart(this.dragonPartHead, DamageSource.setExplosionSource((Explosion)null), 10);
                }

                this.healingEnderCrystal = null;
            }
            else if (this.ticksExisted % 10 == 0 && this.getHealth() < this.getMaxHealth())
            {
                this.setEntityHealth(this.getHealth() + 1);
            }
        }

        if (this.rand.nextInt(10) == 0)
        {
            float f = 32.0F;
            List list = this.worldObj.getEntitiesWithinAABB(EntityEnderCrystal.class, this.boundingBox.expand((double)f, (double)f, (double)f));
            EntityEnderCrystal entityendercrystal = null;
            double d0 = Double.MAX_VALUE;
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                EntityEnderCrystal entityendercrystal1 = (EntityEnderCrystal)iterator.next();
                double d1 = entityendercrystal1.getDistanceSqToEntity(this);

                if (d1 < d0)
                {
                    d0 = d1;
                    entityendercrystal = entityendercrystal1;
                }
            }

            this.healingEnderCrystal = entityendercrystal;
        }
    }

    /**
     * Pushes all entities inside the list away from the enderdragon.
     */
    private void collideWithEntities(List par1List)
    {
        double d0 = (this.dragonPartBody.boundingBox.minX + this.dragonPartBody.boundingBox.maxX) / 2.0D;
        double d1 = (this.dragonPartBody.boundingBox.minZ + this.dragonPartBody.boundingBox.maxZ) / 2.0D;
        Iterator iterator = par1List.iterator();

        while (iterator.hasNext())
        {
            Entity entity = (Entity)iterator.next();

            if (entity instanceof EntityLiving)
            {
                double d2 = entity.posX - d0;
                double d3 = entity.posZ - d1;
                double d4 = d2 * d2 + d3 * d3;
                entity.addVelocity(d2 / d4 * 4.0D, 0.20000000298023224D, d3 / d4 * 4.0D);
            }
        }
    }

    /**
     * Attacks all entities inside this list, dealing 5 hearts of damage.
     */
    private void attackEntitiesInList(List par1List)
    {
        for (int i = 0; i < par1List.size(); ++i)
        {
            Entity entity = (Entity)par1List.get(i);

            if (entity instanceof EntityLiving)
            {
                entity.attackEntityFrom(DamageSource.causeMobDamage(this), 10);
            }
        }
    }

    /**
     * Sets a new target for the flight AI. It can be a random coordinate or a nearby player.
     */
    private void setNewTarget()
    {
        this.forceNewTarget = false;

        if (this.rand.nextInt(2) == 0 && !this.worldObj.playerEntities.isEmpty())
        {
            this.target = (Entity)this.worldObj.playerEntities.get(this.rand.nextInt(this.worldObj.playerEntities.size()));
        }
        else
        {
            boolean flag = false;

            do
            {
                this.targetX = 0.0D;
                this.targetY = (double)(70.0F + this.rand.nextFloat() * 50.0F);
                this.targetZ = 0.0D;
                this.targetX += (double)(this.rand.nextFloat() * 120.0F - 60.0F);
                this.targetZ += (double)(this.rand.nextFloat() * 120.0F - 60.0F);
                double d0 = this.posX - this.targetX;
                double d1 = this.posY - this.targetY;
                double d2 = this.posZ - this.targetZ;
                flag = d0 * d0 + d1 * d1 + d2 * d2 > 100.0D;
            }
            while (!flag);

            this.target = null;
        }
    }

    /**
     * Simplifies the value of a number by adding/subtracting 180 to the point that the number is between -180 and 180.
     */
    private float simplifyAngle(double par1)
    {
        return (float)MathHelper.wrapAngleTo180_double(par1);
    }

    /**
     * Destroys all blocks that aren't associated with 'The End' inside the given bounding box.
     */
    private boolean destroyBlocksInAABB(AxisAlignedBB par1AxisAlignedBB)
    {
        int i = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int j = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int k = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int l = MathHelper.floor_double(par1AxisAlignedBB.maxX);
        int i1 = MathHelper.floor_double(par1AxisAlignedBB.maxY);
        int j1 = MathHelper.floor_double(par1AxisAlignedBB.maxZ);
        boolean flag = false;
        boolean flag1 = false;

        for (int k1 = i; k1 <= l; ++k1)
        {
            for (int l1 = j; l1 <= i1; ++l1)
            {
                for (int i2 = k; i2 <= j1; ++i2)
                {
                    int j2 = this.worldObj.getBlockId(k1, l1, i2);
                    Block block = Block.blocksList[j2];

                    if (block != null)
                    {
                        if (block.canDragonDestroy(worldObj, k1, l1, i2) && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
                        {
                            flag1 = this.worldObj.setBlockToAir(k1, l1, i2) || flag1;
                        }
                        else
                        {
                            flag = true;
                        }
                    }
                }
            }
        }

        if (flag1)
        {
            double d0 = par1AxisAlignedBB.minX + (par1AxisAlignedBB.maxX - par1AxisAlignedBB.minX) * (double)this.rand.nextFloat();
            double d1 = par1AxisAlignedBB.minY + (par1AxisAlignedBB.maxY - par1AxisAlignedBB.minY) * (double)this.rand.nextFloat();
            double d2 = par1AxisAlignedBB.minZ + (par1AxisAlignedBB.maxZ - par1AxisAlignedBB.minZ) * (double)this.rand.nextFloat();
            this.worldObj.spawnParticle("largeexplode", d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }

        return flag;
    }

    public boolean attackEntityFromPart(EntityDragonPart par1EntityDragonPart, DamageSource par2DamageSource, int par3)
    {
        if (par1EntityDragonPart != this.dragonPartHead)
        {
            par3 = par3 / 4 + 1;
        }

        float f = this.rotationYaw * (float)Math.PI / 180.0F;
        float f1 = MathHelper.sin(f);
        float f2 = MathHelper.cos(f);
        this.targetX = this.posX + (double)(f1 * 5.0F) + (double)((this.rand.nextFloat() - 0.5F) * 2.0F);
        this.targetY = this.posY + (double)(this.rand.nextFloat() * 3.0F) + 1.0D;
        this.targetZ = this.posZ - (double)(f2 * 5.0F) + (double)((this.rand.nextFloat() - 0.5F) * 2.0F);
        this.target = null;

        if (par2DamageSource.getEntity() instanceof EntityPlayer || par2DamageSource.isExplosion())
        {
            this.func_82195_e(par2DamageSource, par3);
        }

        return true;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        return false;
    }

    protected boolean func_82195_e(DamageSource par1DamageSource, int par2)
    {
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    /**
     * handles entity death timer, experience orb and particle creation
     */
    protected void onDeathUpdate()
    {
        ++this.deathTicks;

        if (this.deathTicks >= 180 && this.deathTicks <= 200)
        {
            float f = (this.rand.nextFloat() - 0.5F) * 8.0F;
            float f1 = (this.rand.nextFloat() - 0.5F) * 4.0F;
            float f2 = (this.rand.nextFloat() - 0.5F) * 8.0F;
            this.worldObj.spawnParticle("hugeexplosion", this.posX + (double)f, this.posY + 2.0D + (double)f1, this.posZ + (double)f2, 0.0D, 0.0D, 0.0D);
        }

        int i;
        int j;

        if (!this.worldObj.isRemote)
        {
            if (this.deathTicks > 150 && this.deathTicks % 5 == 0)
            {
                i = 1000;

                while (i > 0)
                {
                    j = EntityXPOrb.getXPSplit(i);
                    i -= j;
                    this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
                }
            }

            if (this.deathTicks == 1)
            {
                this.worldObj.func_82739_e(1018, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
            }
        }

        this.moveEntity(0.0D, 0.10000000149011612D, 0.0D);
        this.renderYawOffset = this.rotationYaw += 20.0F;

        if (this.deathTicks == 200 && !this.worldObj.isRemote)
        {
            i = 2000;

            while (i > 0)
            {
                j = EntityXPOrb.getXPSplit(i);
                i -= j;
                this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
            }

            this.createEnderPortal(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));
            this.setDead();
        }
    }

    /**
     * Creates the ender portal leading back to the normal world after defeating the enderdragon.
     */
    private void createEnderPortal(int par1, int par2)
    {
        byte b0 = 64;
        BlockEndPortal.bossDefeated = true;
        byte b1 = 4;

        for (int k = b0 - 1; k <= b0 + 32; ++k)
        {
            for (int l = par1 - b1; l <= par1 + b1; ++l)
            {
                for (int i1 = par2 - b1; i1 <= par2 + b1; ++i1)
                {
                    double d0 = (double)(l - par1);
                    double d1 = (double)(i1 - par2);
                    double d2 = d0 * d0 + d1 * d1;

                    if (d2 <= ((double)b1 - 0.5D) * ((double)b1 - 0.5D))
                    {
                        if (k < b0)
                        {
                            if (d2 <= ((double)(b1 - 1) - 0.5D) * ((double)(b1 - 1) - 0.5D))
                            {
                                this.worldObj.setBlock(l, k, i1, Block.bedrock.blockID);
                            }
                        }
                        else if (k > b0)
                        {
                            this.worldObj.setBlock(l, k, i1, 0);
                        }
                        else if (d2 > ((double)(b1 - 1) - 0.5D) * ((double)(b1 - 1) - 0.5D))
                        {
                            this.worldObj.setBlock(l, k, i1, Block.bedrock.blockID);
                        }
                        else
                        {
                            this.worldObj.setBlock(l, k, i1, Block.endPortal.blockID);
                        }
                    }
                }
            }
        }

        this.worldObj.setBlock(par1, b0 + 0, par2, Block.bedrock.blockID);
        this.worldObj.setBlock(par1, b0 + 1, par2, Block.bedrock.blockID);
        this.worldObj.setBlock(par1, b0 + 2, par2, Block.bedrock.blockID);
        this.worldObj.setBlock(par1 - 1, b0 + 2, par2, Block.torchWood.blockID);
        this.worldObj.setBlock(par1 + 1, b0 + 2, par2, Block.torchWood.blockID);
        this.worldObj.setBlock(par1, b0 + 2, par2 - 1, Block.torchWood.blockID);
        this.worldObj.setBlock(par1, b0 + 2, par2 + 1, Block.torchWood.blockID);
        this.worldObj.setBlock(par1, b0 + 3, par2, Block.bedrock.blockID);
        this.worldObj.setBlock(par1, b0 + 4, par2, Block.dragonEgg.blockID);
        BlockEndPortal.bossDefeated = false;
    }

    /**
     * Makes the entity despawn if requirements are reached
     */
    protected void despawnEntity() {}

    /**
     * Return the Entity parts making up this Entity (currently only for dragons)
     */
    public Entity[] getParts()
    {
        return this.dragonPartArray;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the health points of the dragon.
     */
    public int getBossHealth()
    {
        return this.dataWatcher.getWatchableObjectInt(16);
    }

    public World func_82194_d()
    {
        return this.worldObj;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.enderdragon.growl";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.enderdragon.hit";
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return 5.0F;
    }
}
