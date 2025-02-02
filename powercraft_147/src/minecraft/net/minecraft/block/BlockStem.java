package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.ForgeDirection;

public class BlockStem extends BlockFlower
{
    /** Defines if it is a Melon or a Pumpkin that the stem is producing. */
    private Block fruitType;

    protected BlockStem(int par1, Block par2Block)
    {
        super(par1, 111);
        this.fruitType = par2Block;
        this.setTickRandomly(true);
        float var3 = 0.125F;
        this.setBlockBounds(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, 0.25F, 0.5F + var3);
        this.setCreativeTab((CreativeTabs)null);
    }

    /**
     * Gets passed in the blockID of the block below and supposed to return true if its allowed to grow on the type of
     * blockID passed in. Args: blockID
     */
    protected boolean canThisPlantGrowOnThisBlockID(int par1)
    {
        return par1 == Block.tilledField.blockID;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        super.updateTick(par1World, par2, par3, par4, par5Random);

        if (par1World.getBlockLightValue(par2, par3 + 1, par4) >= 9)
        {
            float var6 = this.getGrowthModifier(par1World, par2, par3, par4);

            if (par5Random.nextInt((int)(25.0F / var6) + 1) == 0)
            {
                int var7 = par1World.getBlockMetadata(par2, par3, par4);

                if (var7 < 7)
                {
                    ++var7;
                    par1World.setBlockMetadataWithNotify(par2, par3, par4, var7);
                }
                else
                {
                    if (par1World.getBlockId(par2 - 1, par3, par4) == this.fruitType.blockID)
                    {
                        return;
                    }

                    if (par1World.getBlockId(par2 + 1, par3, par4) == this.fruitType.blockID)
                    {
                        return;
                    }

                    if (par1World.getBlockId(par2, par3, par4 - 1) == this.fruitType.blockID)
                    {
                        return;
                    }

                    if (par1World.getBlockId(par2, par3, par4 + 1) == this.fruitType.blockID)
                    {
                        return;
                    }

                    int var8 = par5Random.nextInt(4);
                    int var9 = par2;
                    int var10 = par4;

                    if (var8 == 0)
                    {
                        var9 = par2 - 1;
                    }

                    if (var8 == 1)
                    {
                        ++var9;
                    }

                    if (var8 == 2)
                    {
                        var10 = par4 - 1;
                    }

                    if (var8 == 3)
                    {
                        ++var10;
                    }

                    int var11 = par1World.getBlockId(var9, par3 - 1, var10);

                    boolean isSoil = (blocksList[var11] != null && blocksList[var11].canSustainPlant(par1World, var9, par3 - 1, var10, ForgeDirection.UP, this));
                    if (par1World.getBlockId(var9, par3, var10) == 0 && (isSoil || var11 == Block.dirt.blockID || var11 == Block.grass.blockID))
                    {
                        par1World.setBlockWithNotify(var9, par3, var10, this.fruitType.blockID);
                    }
                }
            }
        }
    }

    public void fertilizeStem(World par1World, int par2, int par3, int par4)
    {
        par1World.setBlockMetadataWithNotify(par2, par3, par4, 7);
    }

    private float getGrowthModifier(World par1World, int par2, int par3, int par4)
    {
        float var5 = 1.0F;
        int var6 = par1World.getBlockId(par2, par3, par4 - 1);
        int var7 = par1World.getBlockId(par2, par3, par4 + 1);
        int var8 = par1World.getBlockId(par2 - 1, par3, par4);
        int var9 = par1World.getBlockId(par2 + 1, par3, par4);
        int var10 = par1World.getBlockId(par2 - 1, par3, par4 - 1);
        int var11 = par1World.getBlockId(par2 + 1, par3, par4 - 1);
        int var12 = par1World.getBlockId(par2 + 1, par3, par4 + 1);
        int var13 = par1World.getBlockId(par2 - 1, par3, par4 + 1);
        boolean var14 = var8 == this.blockID || var9 == this.blockID;
        boolean var15 = var6 == this.blockID || var7 == this.blockID;
        boolean var16 = var10 == this.blockID || var11 == this.blockID || var12 == this.blockID || var13 == this.blockID;

        for (int var17 = par2 - 1; var17 <= par2 + 1; ++var17)
        {
            for (int var18 = par4 - 1; var18 <= par4 + 1; ++var18)
            {
                int var19 = par1World.getBlockId(var17, par3 - 1, var18);
                float var20 = 0.0F;

                if (blocksList[var19] != null && blocksList[var19].canSustainPlant(par1World, var17, par3 - 1, var18, ForgeDirection.UP, this))
                {
                    var20 = 1.0F;

                    if (blocksList[var19].isFertile(par1World, var17, par3 - 1, var18))
                    {
                        var20 = 3.0F;
                    }
                }

                if (var17 != par2 || var18 != par4)
                {
                    var20 /= 4.0F;
                }

                var5 += var20;
            }
        }

        if (var16 || var14 && var15)
        {
            var5 /= 2.0F;
        }

        return var5;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the color this block should be rendered. Used by leaves.
     */
    public int getRenderColor(int par1)
    {
        int var2 = par1 * 32;
        int var3 = 255 - par1 * 8;
        int var4 = par1 * 4;
        return var2 << 16 | var3 << 8 | var4;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return this.getRenderColor(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        return this.blockIndexInTexture;
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        float var1 = 0.125F;
        this.setBlockBounds(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 0.25F, 0.5F + var1);
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        this.maxY = (double)((float)(par1IBlockAccess.getBlockMetadata(par2, par3, par4) * 2 + 2) / 16.0F);
        float var5 = 0.125F;
        this.setBlockBounds(0.5F - var5, 0.0F, 0.5F - var5, 0.5F + var5, (float)this.maxY, 0.5F + var5);
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 19;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the current state of the stem. Returns -1 if the stem is not fully grown, or a value between 0 and 3
     * based on the direction the stem is facing.
     */
    public int getState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
        return var5 < 7 ? -1 : (par1IBlockAccess.getBlockId(par2 - 1, par3, par4) == this.fruitType.blockID ? 0 : (par1IBlockAccess.getBlockId(par2 + 1, par3, par4) == this.fruitType.blockID ? 1 : (par1IBlockAccess.getBlockId(par2, par3, par4 - 1) == this.fruitType.blockID ? 2 : (par1IBlockAccess.getBlockId(par2, par3, par4 + 1) == this.fruitType.blockID ? 3 : -1))));
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7)
    {
        super.dropBlockAsItemWithChance(par1World, par2, par3, par4, par5, par6, par7);
    }

    @Override 
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

        for (int i = 0; i < 3; i++)
        {
            if (world.rand.nextInt(15) <= metadata)
            {
                ret.add(new ItemStack(fruitType == pumpkin ? Item.pumpkinSeeds : Item.melonSeeds));
            }
        }

        return ret;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return -1;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return 1;
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return this.fruitType == Block.pumpkin ? Item.pumpkinSeeds.itemID : (this.fruitType == Block.melon ? Item.melonSeeds.itemID : 0);
    }
}
