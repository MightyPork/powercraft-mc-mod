package net.minecraft.dispenser;

import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;

public class BehaviorMobEggDispense extends BehaviorDefaultDispenseItem
{
    /** Reference to the MinecraftServer object. */
    final MinecraftServer mcServer;

    public BehaviorMobEggDispense(MinecraftServer par1)
    {
        this.mcServer = par1;
    }

    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    public ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack)
    {
        EnumFacing var3 = EnumFacing.func_82600_a(par1IBlockSource.func_82620_h());
        double var4 = par1IBlockSource.getX() + (double)var3.func_82601_c();
        double var6 = (double)((float)par1IBlockSource.getYInt() + 0.2F);
        double var8 = par1IBlockSource.getZ() + (double)var3.func_82599_e();
        ItemMonsterPlacer.spawnCreature(par1IBlockSource.getWorld(), par2ItemStack.getItemDamage(), var4, var6, var8);
        par2ItemStack.splitStack(1);
        return par2ItemStack;
    }
}
