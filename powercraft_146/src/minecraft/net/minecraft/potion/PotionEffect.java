package net.minecraft.potion;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PotionEffect
{
    /** ID value of the potion this effect matches. */
    private int potionID;

    /** The duration of the potion effect */
    private int duration;

    /** The amplifier of the potion effect */
    private int amplifier;
    private boolean field_82723_d;
    private boolean field_82724_e;

    /** List of ItemStack that can cure the potion effect **/
    private List<ItemStack> curativeItems;

    public PotionEffect(int par1, int par2)
    {
        this(par1, par2, 0);
    }

    public PotionEffect(int par1, int par2, int par3)
    {
        this(par1, par2, par3, false);
    }

    public PotionEffect(int par1, int par2, int par3, boolean par4)
    {
        this.potionID = par1;
        this.duration = par2;
        this.amplifier = par3;
        this.field_82724_e = par4;
        this.curativeItems = new ArrayList<ItemStack>();
        this.curativeItems.add(new ItemStack(Item.bucketMilk));
    }

    public PotionEffect(PotionEffect par1PotionEffect)
    {
        this.potionID = par1PotionEffect.potionID;
        this.duration = par1PotionEffect.duration;
        this.amplifier = par1PotionEffect.amplifier;
        this.curativeItems = par1PotionEffect.getCurativeItems();
    }

    /**
     * merges the input PotionEffect into this one if this.amplifier <= tomerge.amplifier. The duration in the supplied
     * potion effect is assumed to be greater.
     */
    public void combine(PotionEffect par1PotionEffect)
    {
        if (this.potionID != par1PotionEffect.potionID)
        {
            System.err.println("This method should only be called for matching effects!");
        }

        if (par1PotionEffect.amplifier > this.amplifier)
        {
            this.amplifier = par1PotionEffect.amplifier;
            this.duration = par1PotionEffect.duration;
        }
        else if (par1PotionEffect.amplifier == this.amplifier && this.duration < par1PotionEffect.duration)
        {
            this.duration = par1PotionEffect.duration;
        }
        else if (!par1PotionEffect.field_82724_e && this.field_82724_e)
        {
            this.field_82724_e = par1PotionEffect.field_82724_e;
        }
    }

    /**
     * Retrieve the ID of the potion this effect matches.
     */
    public int getPotionID()
    {
        return this.potionID;
    }

    public int getDuration()
    {
        return this.duration;
    }

    public int getAmplifier()
    {
        return this.amplifier;
    }
    
    /***
     * Returns a list of curative items for the potion effect
     * @return The list (ItemStack) of curative items for the potion effect 
     */
    public List<ItemStack> getCurativeItems()
    {
        return this.curativeItems;
    }
    
    /***
     * Checks the given ItemStack to see if it is in the list of curative items for the potion effect
     * @param stack The ItemStack being checked against the list of curative items for the potion effect
     * @return true if the given ItemStack is in the list of curative items for the potion effect, false otherwise
     */
    public boolean isCurativeItem(ItemStack stack)
    {
        boolean found = false;
        for (ItemStack curativeItem : this.curativeItems)
        {
            if (curativeItem.isItemEqual(stack))
            {
                found = true;
            }
        }

        return found;
    }
    
    /***
     * Sets the array of curative items for the potion effect 
     * @param curativeItems The list of ItemStacks being set to the potion effect
     */
    public void setCurativeItems(List<ItemStack> curativeItems)
    {
        this.curativeItems = curativeItems;
    }
    
    /***
     * Adds the given stack to list of curative items for the potion effect
     * @param stack The ItemStack being added to the curative item list
     */
    public void addCurativeItem(ItemStack stack)
    {
        boolean found = false;
        for (ItemStack curativeItem : this.curativeItems)
        {
            if (curativeItem.isItemEqual(stack))
            {
                found = true;
            }
        }
        if (!found)
        {
            this.curativeItems.add(stack);
        }
    }

    /**
     * Set whether this potion is a splash potion.
     */
    public void setSplashPotion(boolean par1)
    {
        this.field_82723_d = par1;
    }

    public boolean func_82720_e()
    {
        return this.field_82724_e;
    }

    public boolean onUpdate(EntityLiving par1EntityLiving)
    {
        if (this.duration > 0)
        {
            if (Potion.potionTypes[this.potionID].isReady(this.duration, this.amplifier))
            {
                this.performEffect(par1EntityLiving);
            }

            this.deincrementDuration();
        }

        return this.duration > 0;
    }

    private int deincrementDuration()
    {
        return --this.duration;
    }

    public void performEffect(EntityLiving par1EntityLiving)
    {
        if (this.duration > 0)
        {
            Potion.potionTypes[this.potionID].performEffect(par1EntityLiving, this.amplifier);
        }
    }

    public String getEffectName()
    {
        return Potion.potionTypes[this.potionID].getName();
    }

    public int hashCode()
    {
        return this.potionID;
    }

    public String toString()
    {
        String var1 = "";

        if (this.getAmplifier() > 0)
        {
            var1 = this.getEffectName() + " x " + (this.getAmplifier() + 1) + ", Duration: " + this.getDuration();
        }
        else
        {
            var1 = this.getEffectName() + ", Duration: " + this.getDuration();
        }

        if (this.field_82723_d)
        {
            var1 = var1 + ", Splash: true";
        }

        return Potion.potionTypes[this.potionID].isUsable() ? "(" + var1 + ")" : var1;
    }

    public boolean equals(Object par1Obj)
    {
        if (!(par1Obj instanceof PotionEffect))
        {
            return false;
        }
        else
        {
            PotionEffect var2 = (PotionEffect)par1Obj;
            return this.potionID == var2.potionID && this.amplifier == var2.amplifier && this.duration == var2.duration && this.field_82723_d == var2.field_82723_d && this.field_82724_e == var2.field_82724_e;
        }
    }

    /**
     * Write a custom potion effect to a potion item's NBT data.
     */
    public NBTTagCompound writeCustomPotionEffectToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setByte("Id", (byte)this.getPotionID());
        par1NBTTagCompound.setByte("Amplifier", (byte)this.getAmplifier());
        par1NBTTagCompound.setInteger("Duration", this.getDuration());
        par1NBTTagCompound.setBoolean("Ambient", this.func_82720_e());
        return par1NBTTagCompound;
    }

    /**
     * Read a custom potion effect from a potion item's NBT data.
     */
    public static PotionEffect readCustomPotionEffectFromNBT(NBTTagCompound par0NBTTagCompound)
    {
        byte var1 = par0NBTTagCompound.getByte("Id");
        byte var2 = par0NBTTagCompound.getByte("Amplifier");
        int var3 = par0NBTTagCompound.getInteger("Duration");
        boolean var4 = par0NBTTagCompound.getBoolean("Ambient");
        return new PotionEffect(var1, var3, var2, var4);
    }
}
