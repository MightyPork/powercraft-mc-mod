package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.Iterator;
import java.util.List;

public class TileEntityBeacon extends TileEntity implements IInventory
{
    /** List of effects that Beacon can apply */
    public static final Potion[][] effectsList = new Potion[][] {{Potion.moveSpeed, Potion.digSpeed}, {Potion.resistance, Potion.jump}, {Potion.damageBoost}, {Potion.regeneration}};
    @SideOnly(Side.CLIENT)
    private long field_82137_b;
    @SideOnly(Side.CLIENT)
    private float field_82138_c;
    private boolean field_82135_d;

    /** Level of this beacon's pyramid. */
    private int levels = -1;

    /** Primary potion effect given by this beacon. */
    private int primaryEffect;

    /** Secondary potion effect given by this beacon. */
    private int secondaryEffect;

    /** Item given to this beacon as payment. */
    private ItemStack payment;

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    public void updateEntity()
    {
        if (this.worldObj.getTotalWorldTime() % 80L == 0L)
        {
            this.func_82131_u();
            this.func_82124_t();
        }
    }

    private void func_82124_t()
    {
        if (this.field_82135_d && this.levels > 0 && !this.worldObj.isRemote && this.primaryEffect > 0)
        {
            double var1 = (double)(this.levels * 8 + 8);
            byte var3 = 0;

            if (this.levels >= 4 && this.primaryEffect == this.secondaryEffect)
            {
                var3 = 1;
            }

            AxisAlignedBB var4 = AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)(this.xCoord + 1), (double)(this.yCoord + 1), (double)(this.zCoord + 1)).expand(var1, var1, var1);
            List var5 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, var4);
            Iterator var6 = var5.iterator();
            EntityPlayer var7;

            while (var6.hasNext())
            {
                var7 = (EntityPlayer)var6.next();
                var7.addPotionEffect(new PotionEffect(this.primaryEffect, 180, var3, true));
            }

            if (this.levels >= 4 && this.primaryEffect != this.secondaryEffect && this.secondaryEffect > 0)
            {
                var6 = var5.iterator();

                while (var6.hasNext())
                {
                    var7 = (EntityPlayer)var6.next();
                    var7.addPotionEffect(new PotionEffect(this.secondaryEffect, 180, 0, true));
                }
            }
        }
    }

    private void func_82131_u()
    {
        if (!this.worldObj.canBlockSeeTheSky(this.xCoord, this.yCoord + 1, this.zCoord))
        {
            this.field_82135_d = false;
            this.levels = 0;
        }
        else
        {
            this.field_82135_d = true;
            this.levels = 0;

            for (int var1 = 1; var1 <= 4; this.levels = var1++)
            {
                int var2 = this.yCoord - var1;

                if (var2 < 1)
                {
                    break;
                }

                boolean var3 = true;

                for (int var4 = this.xCoord - var1; var4 <= this.xCoord + var1 && var3; ++var4)
                {
                    for (int var5 = this.zCoord - var1; var5 <= this.zCoord + var1; ++var5)
                    {
                        int var6 = this.worldObj.getBlockId(var4, var2, var5);

                        if (var6 != Block.blockEmerald.blockID && var6 != Block.blockGold.blockID && var6 != Block.blockDiamond.blockID && var6 != Block.blockSteel.blockID)
                        {
                            var3 = false;
                            break;
                        }
                    }
                }

                if (!var3)
                {
                    break;
                }
            }

            if (this.levels == 0)
            {
                this.field_82135_d = false;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public float func_82125_v_()
    {
        if (!this.field_82135_d)
        {
            return 0.0F;
        }
        else
        {
            int var1 = (int)(this.worldObj.getTotalWorldTime() - this.field_82137_b);
            this.field_82137_b = this.worldObj.getTotalWorldTime();

            if (var1 > 1)
            {
                this.field_82138_c -= (float)var1 / 40.0F;

                if (this.field_82138_c < 0.0F)
                {
                    this.field_82138_c = 0.0F;
                }
            }

            this.field_82138_c += 0.025F;

            if (this.field_82138_c > 1.0F)
            {
                this.field_82138_c = 1.0F;
            }

            return this.field_82138_c;
        }
    }

    /**
     * Return the primary potion effect given by this beacon.
     */
    public int getPrimaryEffect()
    {
        return this.primaryEffect;
    }

    /**
     * Return the secondary potion effect given by this beacon.
     */
    public int getSecondaryEffect()
    {
        return this.secondaryEffect;
    }

    /**
     * Return the levels of this beacon's pyramid.
     */
    public int getLevels()
    {
        return this.levels;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Set the levels of this beacon's pyramid.
     */
    public void setLevels(int par1)
    {
        this.levels = par1;
    }

    public void func_82128_d(int par1)
    {
        this.primaryEffect = 0;

        for (int var2 = 0; var2 < this.levels && var2 < 3; ++var2)
        {
            Potion[] var3 = effectsList[var2];
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; ++var5)
            {
                Potion var6 = var3[var5];

                if (var6.id == par1)
                {
                    this.primaryEffect = par1;
                    return;
                }
            }
        }
    }

    public void func_82127_e(int par1)
    {
        this.secondaryEffect = 0;

        if (this.levels >= 4)
        {
            for (int var2 = 0; var2 < 4; ++var2)
            {
                Potion[] var3 = effectsList[var2];
                int var4 = var3.length;

                for (int var5 = 0; var5 < var4; ++var5)
                {
                    Potion var6 = var3[var5];

                    if (var6.id == par1)
                    {
                        this.secondaryEffect = par1;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Overriden in a sign to provide the text.
     */
    public Packet getDescriptionPacket()
    {
        NBTTagCompound var1 = new NBTTagCompound();
        this.writeToNBT(var1);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 3, var1);
    }

    @SideOnly(Side.CLIENT)
    public double func_82115_m()
    {
        return 65536.0D;
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.primaryEffect = par1NBTTagCompound.getInteger("Primary");
        this.secondaryEffect = par1NBTTagCompound.getInteger("Secondary");
        this.levels = par1NBTTagCompound.getInteger("Levels");
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("Primary", this.primaryEffect);
        par1NBTTagCompound.setInteger("Secondary", this.secondaryEffect);
        par1NBTTagCompound.setInteger("Levels", this.levels);
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return 1;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int par1)
    {
        return par1 == 0 ? this.payment : null;
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (par1 == 0 && this.payment != null)
        {
            if (par2 >= this.payment.stackSize)
            {
                ItemStack var3 = this.payment;
                this.payment = null;
                return var3;
            }
            else
            {
                this.payment.stackSize -= par2;
                return new ItemStack(this.payment.itemID, par2, this.payment.getItemDamage());
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (par1 == 0 && this.payment != null)
        {
            ItemStack var2 = this.payment;
            this.payment = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        if (par1 == 0)
        {
            this.payment = par2ItemStack;
        }
    }

    /**
     * Returns the name of the inventory.
     */
    public String getInvName()
    {
        return "container.beacon";
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getInventoryStackLimit()
    {
        return 1;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
    }

    public void openChest() {}

    public void closeChest() {}
}
