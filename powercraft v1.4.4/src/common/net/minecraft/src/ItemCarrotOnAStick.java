package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class ItemCarrotOnAStick extends Item
{
    public ItemCarrotOnAStick(int par1)
    {
        super(par1);
        this.setCreativeTab(CreativeTabs.tabTransport);
        this.setMaxStackSize(1);
        this.setMaxDamage(25);
    }

    @SideOnly(Side.CLIENT)

    public boolean isFull3D()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)

    public boolean shouldRotateAroundWhenRendering()
    {
        return true;
    }

    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        if (par3EntityPlayer.isRiding() && par3EntityPlayer.ridingEntity instanceof EntityPig)
        {
            EntityPig var4 = (EntityPig)par3EntityPlayer.ridingEntity;

            if (var4.getAIControlledByPlayer().isControlledByPlayer() && par1ItemStack.getMaxDamage() - par1ItemStack.getItemDamage() >= 7)
            {
                var4.getAIControlledByPlayer().boostSpeed();
                par1ItemStack.damageItem(7, par3EntityPlayer);

                if (par1ItemStack.stackSize == 0)
                {
                    return new ItemStack(Item.fishingRod);
                }
            }
        }

        return par1ItemStack;
    }
}
