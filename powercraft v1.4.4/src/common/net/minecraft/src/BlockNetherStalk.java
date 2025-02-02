package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import java.util.ArrayList;
import java.util.Random;

public class BlockNetherStalk extends BlockFlower
{
    protected BlockNetherStalk(int par1)
    {
        super(par1, 226);
        this.setTickRandomly(true);
        float var2 = 0.5F;
        this.setBlockBounds(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, 0.25F, 0.5F + var2);
        this.setCreativeTab((CreativeTabs)null);
    }

    protected boolean canThisPlantGrowOnThisBlockID(int par1)
    {
        return par1 == Block.slowSand.blockID;
    }

    public boolean canBlockStay(World par1World, int par2, int par3, int par4)
    {
        return this.canThisPlantGrowOnThisBlockID(par1World.getBlockId(par2, par3 - 1, par4));
    }

    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        int var6 = par1World.getBlockMetadata(par2, par3, par4);

        if (var6 < 3 && par5Random.nextInt(10) == 0)
        {
            ++var6;
            par1World.setBlockMetadataWithNotify(par2, par3, par4, var6);
        }

        super.updateTick(par1World, par2, par3, par4, par5Random);
    }

    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        return par2 >= 3 ? this.blockIndexInTexture + 2 : (par2 > 0 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture);
    }

    public int getRenderType()
    {
        return 6;
    }

    public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7)
    {
        super.dropBlockAsItemWithChance(par1World, par2, par3, par4, par5, par6, par7);
    }

    public int idDropped(int par1, Random par2Random, int par3)
    {
        return 0;
    }

    public int quantityDropped(Random par1Random)
    {
        return 0;
    }

    @SideOnly(Side.CLIENT)

    public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return Item.netherStalkSeeds.shiftedIndex;
    }

    @Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        int count = 1;

        if (metadata >= 3)
        {
            count = 2 + world.rand.nextInt(3) + (fortune > 0 ? world.rand.nextInt(fortune + 1) : 0);
        }

        for (int i = 0; i < count; i++)
        {
            ret.add(new ItemStack(Item.netherStalkSeeds));
        }

        return ret;
    }
}
