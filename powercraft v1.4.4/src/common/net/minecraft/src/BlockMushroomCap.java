package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.Random;

public class BlockMushroomCap extends Block
{
    private int mushroomType;

    public BlockMushroomCap(int par1, Material par2Material, int par3, int par4)
    {
        super(par1, par3, par2Material);
        this.mushroomType = par4;
    }

    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        return par2 == 10 && par1 > 1 ? this.blockIndexInTexture - 1 : (par2 >= 1 && par2 <= 9 && par1 == 1 ? this.blockIndexInTexture - 16 - this.mushroomType : (par2 >= 1 && par2 <= 3 && par1 == 2 ? this.blockIndexInTexture - 16 - this.mushroomType : (par2 >= 7 && par2 <= 9 && par1 == 3 ? this.blockIndexInTexture - 16 - this.mushroomType : ((par2 == 1 || par2 == 4 || par2 == 7) && par1 == 4 ? this.blockIndexInTexture - 16 - this.mushroomType : ((par2 == 3 || par2 == 6 || par2 == 9) && par1 == 5 ? this.blockIndexInTexture - 16 - this.mushroomType : (par2 == 14 ? this.blockIndexInTexture - 16 - this.mushroomType : (par2 == 15 ? this.blockIndexInTexture - 1 : this.blockIndexInTexture)))))));
    }

    public int quantityDropped(Random par1Random)
    {
        int var2 = par1Random.nextInt(10) - 7;

        if (var2 < 0)
        {
            var2 = 0;
        }

        return var2;
    }

    public int idDropped(int par1, Random par2Random, int par3)
    {
        return Block.mushroomBrown.blockID + this.mushroomType;
    }

    @SideOnly(Side.CLIENT)

    public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return Block.mushroomBrown.blockID + this.mushroomType;
    }
}
