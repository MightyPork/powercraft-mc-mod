package net.minecraft.src;

public class ItemReed extends Item
{
    private int spawnID;

    public ItemReed(int par1, Block par2Block)
    {
        super(par1);
        this.spawnID = par2Block.blockID;
    }

    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        int var11 = par3World.getBlockId(par4, par5, par6);

        if (var11 == Block.snow.blockID)
        {
            par7 = 1;
        }
        else if (var11 != Block.vine.blockID && var11 != Block.tallGrass.blockID && var11 != Block.deadBush.blockID)
        {
            if (par7 == 0)
            {
                --par5;
            }

            if (par7 == 1)
            {
                ++par5;
            }

            if (par7 == 2)
            {
                --par6;
            }

            if (par7 == 3)
            {
                ++par6;
            }

            if (par7 == 4)
            {
                --par4;
            }

            if (par7 == 5)
            {
                ++par4;
            }
        }

        if (!par2EntityPlayer.func_82247_a(par4, par5, par6, par7, par1ItemStack))
        {
            return false;
        }
        else if (par1ItemStack.stackSize == 0)
        {
            return false;
        }
        else
        {
            if (par3World.canPlaceEntityOnSide(this.spawnID, par4, par5, par6, false, par7, (Entity)null))
            {
                Block var12 = Block.blocksList[this.spawnID];
                int var13 = var12.func_85104_a(par3World, par4, par5, par6, par7, par8, par9, par10, 0);

                if (par3World.setBlockAndMetadataWithNotify(par4, par5, par6, this.spawnID, var13))
                {
                    if (par3World.getBlockId(par4, par5, par6) == this.spawnID)
                    {
                        Block.blocksList[this.spawnID].onBlockPlacedBy(par3World, par4, par5, par6, par2EntityPlayer);
                        Block.blocksList[this.spawnID].func_85105_g(par3World, par4, par5, par6, var13);
                    }

                    par3World.playSoundEffect((double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), (double)((float)par6 + 0.5F), var12.stepSound.getPlaceSound(), (var12.stepSound.getVolume() + 1.0F) / 2.0F, var12.stepSound.getPitch() * 0.8F);
                    --par1ItemStack.stackSize;
                }
            }

            return true;
        }
    }
}
