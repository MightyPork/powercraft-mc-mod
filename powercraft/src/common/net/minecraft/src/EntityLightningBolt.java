package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.List;

public class EntityLightningBolt extends EntityWeatherEffect
{
    private int lightningState;

    public long boltVertex = 0L;

    private int boltLivingTime;

    public EntityLightningBolt(World par1World, double par2, double par4, double par6)
    {
        super(par1World);
        this.setLocationAndAngles(par2, par4, par6, 0.0F, 0.0F);
        this.lightningState = 2;
        this.boltVertex = this.rand.nextLong();
        this.boltLivingTime = this.rand.nextInt(3) + 1;

        if (!par1World.isRemote && par1World.difficultySetting >= 2 && par1World.doChunksNearChunkExist(MathHelper.floor_double(par2), MathHelper.floor_double(par4), MathHelper.floor_double(par6), 10))
        {
            int var8 = MathHelper.floor_double(par2);
            int var9 = MathHelper.floor_double(par4);
            int var10 = MathHelper.floor_double(par6);

            if (par1World.getBlockId(var8, var9, var10) == 0 && Block.fire.canPlaceBlockAt(par1World, var8, var9, var10))
            {
                par1World.setBlockWithNotify(var8, var9, var10, Block.fire.blockID);
            }

            for (var8 = 0; var8 < 4; ++var8)
            {
                var9 = MathHelper.floor_double(par2) + this.rand.nextInt(3) - 1;
                var10 = MathHelper.floor_double(par4) + this.rand.nextInt(3) - 1;
                int var11 = MathHelper.floor_double(par6) + this.rand.nextInt(3) - 1;

                if (par1World.getBlockId(var9, var10, var11) == 0 && Block.fire.canPlaceBlockAt(par1World, var9, var10, var11))
                {
                    par1World.setBlockWithNotify(var9, var10, var11, Block.fire.blockID);
                }
            }
        }
    }

    public void onUpdate()
    {
        super.onUpdate();

        if (this.lightningState == 2)
        {
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "ambient.weather.thunder", 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F);
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "random.explode", 2.0F, 0.5F + this.rand.nextFloat() * 0.2F);
        }

        --this.lightningState;

        if (this.lightningState < 0)
        {
            if (this.boltLivingTime == 0)
            {
                this.setDead();
            }
            else if (this.lightningState < -this.rand.nextInt(10))
            {
                --this.boltLivingTime;
                this.lightningState = 1;
                this.boltVertex = this.rand.nextLong();

                if (!this.worldObj.isRemote && this.worldObj.doChunksNearChunkExist(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 10))
                {
                    int var1 = MathHelper.floor_double(this.posX);
                    int var2 = MathHelper.floor_double(this.posY);
                    int var3 = MathHelper.floor_double(this.posZ);

                    if (this.worldObj.getBlockId(var1, var2, var3) == 0 && Block.fire.canPlaceBlockAt(this.worldObj, var1, var2, var3))
                    {
                        this.worldObj.setBlockWithNotify(var1, var2, var3, Block.fire.blockID);
                    }
                }
            }
        }

        if (!this.worldObj.isRemote && this.lightningState >= 0)
        {
            double var6 = 3.0D;
            List var7 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(this.posX - var6, this.posY - var6, this.posZ - var6, this.posX + var6, this.posY + 6.0D + var6, this.posZ + var6));

            for (int var4 = 0; var4 < var7.size(); ++var4)
            {
                Entity var5 = (Entity)var7.get(var4);
                var5.onStruckByLightning(this);
            }

            this.worldObj.lightningFlash = 2;
        }
    }

    protected void entityInit() {}

    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {}

    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {}

    @SideOnly(Side.CLIENT)

    public boolean isInRangeToRenderVec3D(Vec3 par1Vec3)
    {
        return this.lightningState >= 0;
    }
}
