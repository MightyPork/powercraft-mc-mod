package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class EntityItemFrame extends EntityHanging
{
    private float itemDropChance = 1.0F;

    public EntityItemFrame(World par1World)
    {
        super(par1World);
    }

    public EntityItemFrame(World par1World, int par2, int par3, int par4, int par5)
    {
        super(par1World, par2, par3, par4, par5);
        this.func_82328_a(par5);
    }

    protected void entityInit()
    {
        this.getDataWatcher().addObjectByDataType(2, 5);
        this.getDataWatcher().addObject(3, Byte.valueOf((byte)0));
    }

    public int func_82329_d()
    {
        return 9;
    }

    public int func_82330_g()
    {
        return 9;
    }

    @SideOnly(Side.CLIENT)

    public boolean isInRangeToRenderDist(double par1)
    {
        double var3 = 16.0D;
        var3 *= 64.0D * this.renderDistanceWeight;
        return par1 < var3 * var3;
    }

    public void dropItemStack()
    {
        this.entityDropItem(new ItemStack(Item.itemFrame), 0.0F);
        ItemStack var1 = this.func_82335_i();

        if (var1 != null && this.rand.nextFloat() < this.itemDropChance)
        {
            var1 = var1.copy();
            var1.setItemFrame((EntityItemFrame)null);
            this.entityDropItem(var1, 0.0F);
        }
    }

    public ItemStack func_82335_i()
    {
        return this.getDataWatcher().getWatchableObjectItemStack(2);
    }

    public void func_82334_a(ItemStack par1ItemStack)
    {
        par1ItemStack = par1ItemStack.copy();
        par1ItemStack.stackSize = 1;
        par1ItemStack.setItemFrame(this);
        this.getDataWatcher().updateObject(2, par1ItemStack);
        this.getDataWatcher().func_82708_h(2);
    }

    public int getRotation()
    {
        return this.getDataWatcher().getWatchableObjectByte(3);
    }

    public void func_82336_g(int par1)
    {
        this.getDataWatcher().updateObject(3, Byte.valueOf((byte)(par1 % 4)));
    }

    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        if (this.func_82335_i() != null)
        {
            par1NBTTagCompound.setCompoundTag("Item", this.func_82335_i().writeToNBT(new NBTTagCompound()));
            par1NBTTagCompound.setByte("ItemRotation", (byte)this.getRotation());
            par1NBTTagCompound.setFloat("ItemDropChance", this.itemDropChance);
        }

        super.writeEntityToNBT(par1NBTTagCompound);
    }

    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        NBTTagCompound var2 = par1NBTTagCompound.getCompoundTag("Item");

        if (var2 != null && !var2.hasNoTags())
        {
            this.func_82334_a(ItemStack.loadItemStackFromNBT(var2));
            this.func_82336_g(par1NBTTagCompound.getByte("ItemRotation"));

            if (par1NBTTagCompound.hasKey("ItemDropChance"))
            {
                this.itemDropChance = par1NBTTagCompound.getFloat("ItemDropChance");
            }
        }

        super.readEntityFromNBT(par1NBTTagCompound);
    }

    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        if (this.func_82335_i() == null)
        {
            ItemStack var2 = par1EntityPlayer.getHeldItem();

            if (var2 != null && !this.worldObj.isRemote)
            {
                this.func_82334_a(var2);

                if (!par1EntityPlayer.capabilities.isCreativeMode && --var2.stackSize <= 0)
                {
                    par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
                }
            }
        }
        else if (!this.worldObj.isRemote)
        {
            this.func_82336_g(this.getRotation() + 1);
        }

        return true;
    }
}
