package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

final class DispenserBehaviorMobEgg extends BehaviorDefaultDispenseItem
{
    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    public ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack)
    {
        EnumFacing enumfacing = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
        double d0 = par1IBlockSource.getX() + (double)enumfacing.getFrontOffsetX();
        double d1 = (double)((float)par1IBlockSource.getYInt() + 0.2F);
        double d2 = par1IBlockSource.getZ() + (double)enumfacing.getFrontOffsetZ();
        Entity entity = ItemMonsterPlacer.spawnCreature(par1IBlockSource.getWorld(), par2ItemStack.getItemDamage(), d0, d1, d2);

        if (entity instanceof EntityLiving && par2ItemStack.hasDisplayName())
        {
            ((EntityLiving)entity).func_94058_c(par2ItemStack.getDisplayName());
        }

        par2ItemStack.splitStack(1);
        return par2ItemStack;
    }
}
