package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

final class DispenserBehaviorFilledBucket extends BehaviorDefaultDispenseItem
{
    private final BehaviorDefaultDispenseItem field_96459_b = new BehaviorDefaultDispenseItem();

    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    public ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack)
    {
        ItemBucket itembucket = (ItemBucket)par2ItemStack.getItem();
        int i = par1IBlockSource.getXInt();
        int j = par1IBlockSource.getYInt();
        int k = par1IBlockSource.getZInt();
        EnumFacing enumfacing = BlockDispenser.func_100009_j_(par1IBlockSource.func_82620_h());

        if (itembucket.tryPlaceContainedLiquid(par1IBlockSource.getWorld(), (double)i, (double)j, (double)k, i + enumfacing.getFrontOffsetX(), j + enumfacing.func_96559_d(), k + enumfacing.getFrontOffsetZ()))
        {
            par2ItemStack.itemID = Item.bucketEmpty.itemID;
            par2ItemStack.stackSize = 1;
            return par2ItemStack;
        }
        else
        {
            return this.field_96459_b.dispense(par1IBlockSource, par2ItemStack);
        }
    }
}
