package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.WeightedRandomChestContent;

public class StructureMineshaftPieces
{
    /** List of contents that can generate in Mineshafts. */
    public static final WeightedRandomChestContent[] mineshaftChestContents = new WeightedRandomChestContent[] {new WeightedRandomChestContent(Item.ingotIron.shiftedIndex, 0, 1, 5, 10), new WeightedRandomChestContent(Item.ingotGold.shiftedIndex, 0, 1, 3, 5), new WeightedRandomChestContent(Item.redstone.shiftedIndex, 0, 4, 9, 5), new WeightedRandomChestContent(Item.dyePowder.shiftedIndex, 4, 4, 9, 5), new WeightedRandomChestContent(Item.diamond.shiftedIndex, 0, 1, 2, 3), new WeightedRandomChestContent(Item.coal.shiftedIndex, 0, 3, 8, 10), new WeightedRandomChestContent(Item.bread.shiftedIndex, 0, 1, 3, 15), new WeightedRandomChestContent(Item.pickaxeSteel.shiftedIndex, 0, 1, 1, 1), new WeightedRandomChestContent(Block.rail.blockID, 0, 4, 8, 1), new WeightedRandomChestContent(Item.melonSeeds.shiftedIndex, 0, 2, 4, 10), new WeightedRandomChestContent(Item.pumpkinSeeds.shiftedIndex, 0, 2, 4, 10)};

    private static StructureComponent getRandomComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
    {
        int var7 = par1Random.nextInt(100);
        StructureBoundingBox var8;

        if (var7 >= 80)
        {
            var8 = ComponentMineshaftCross.findValidPlacement(par0List, par1Random, par2, par3, par4, par5);

            if (var8 != null)
            {
                return new ComponentMineshaftCross(par6, par1Random, var8, par5);
            }
        }
        else if (var7 >= 70)
        {
            var8 = ComponentMineshaftStairs.findValidPlacement(par0List, par1Random, par2, par3, par4, par5);

            if (var8 != null)
            {
                return new ComponentMineshaftStairs(par6, par1Random, var8, par5);
            }
        }
        else
        {
            var8 = ComponentMineshaftCorridor.findValidPlacement(par0List, par1Random, par2, par3, par4, par5);

            if (var8 != null)
            {
                return new ComponentMineshaftCorridor(par6, par1Random, var8, par5);
            }
        }

        return null;
    }

    private static StructureComponent getNextMineShaftComponent(StructureComponent par0StructureComponent, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        if (par7 > 8)
        {
            return null;
        }
        else if (Math.abs(par3 - par0StructureComponent.getBoundingBox().minX) <= 80 && Math.abs(par5 - par0StructureComponent.getBoundingBox().minZ) <= 80)
        {
            StructureComponent var8 = getRandomComponent(par1List, par2Random, par3, par4, par5, par6, par7 + 1);

            if (var8 != null)
            {
                par1List.add(var8);
                var8.buildComponent(par0StructureComponent, par1List, par2Random);
            }

            return var8;
        }
        else
        {
            return null;
        }
    }

    static StructureComponent getNextComponent(StructureComponent par0StructureComponent, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        return getNextMineShaftComponent(par0StructureComponent, par1List, par2Random, par3, par4, par5, par6, par7);
    }

    static WeightedRandomChestContent[] func_78816_a()
    {
        return mineshaftChestContents;
    }
}
