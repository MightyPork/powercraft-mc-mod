package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class WorldGenLiquids extends WorldGenerator
{
    /** The ID of the liquid block used in this liquid generator. */
    private int liquidBlockId;

    public WorldGenLiquids(int par1)
    {
        this.liquidBlockId = par1;
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        if (par1World.getBlockId(par3, par4 + 1, par5) != Block.stone.blockID)
        {
            return false;
        }
        else if (par1World.getBlockId(par3, par4 - 1, par5) != Block.stone.blockID)
        {
            return false;
        }
        else if (par1World.getBlockId(par3, par4, par5) != 0 && par1World.getBlockId(par3, par4, par5) != Block.stone.blockID)
        {
            return false;
        }
        else
        {
            int var6 = 0;

            if (par1World.getBlockId(par3 - 1, par4, par5) == Block.stone.blockID)
            {
                ++var6;
            }

            if (par1World.getBlockId(par3 + 1, par4, par5) == Block.stone.blockID)
            {
                ++var6;
            }

            if (par1World.getBlockId(par3, par4, par5 - 1) == Block.stone.blockID)
            {
                ++var6;
            }

            if (par1World.getBlockId(par3, par4, par5 + 1) == Block.stone.blockID)
            {
                ++var6;
            }

            int var7 = 0;

            if (par1World.isAirBlock(par3 - 1, par4, par5))
            {
                ++var7;
            }

            if (par1World.isAirBlock(par3 + 1, par4, par5))
            {
                ++var7;
            }

            if (par1World.isAirBlock(par3, par4, par5 - 1))
            {
                ++var7;
            }

            if (par1World.isAirBlock(par3, par4, par5 + 1))
            {
                ++var7;
            }

            if (var6 == 3 && var7 == 1)
            {
                par1World.setBlockWithNotify(par3, par4, par5, this.liquidBlockId);
                par1World.scheduledUpdatesAreImmediate = true;
                Block.blocksList[this.liquidBlockId].updateTick(par1World, par3, par4, par5, par2Random);
                par1World.scheduledUpdatesAreImmediate = false;
            }

            return true;
        }
    }
}
