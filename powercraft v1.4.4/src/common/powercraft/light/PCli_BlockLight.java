package powercraft.light;

import java.util.List;
import java.util.Random;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Item;
import net.minecraft.src.ItemDye;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.Configuration;
import powercraft.core.PC_Block;
import powercraft.core.PC_Color;
import powercraft.core.PC_IBlockRenderer;
import powercraft.core.PC_IConfigLoader;
import powercraft.core.PC_ICraftingToolDisplayer;
import powercraft.core.PC_MathHelper;
import powercraft.core.PC_Renderer;
import powercraft.core.PC_Shining;
import powercraft.core.PC_Shining.OFF;
import powercraft.core.PC_Shining.ON;
import powercraft.core.PC_Utils;

@PC_Shining
public class PCli_BlockLight extends PC_Block implements PC_ICraftingToolDisplayer, PC_IConfigLoader, PC_IBlockRenderer
{
    @ON
    public static PCli_BlockLight on;
    @OFF
    public static PCli_BlockLight off;

    public PCli_BlockLight(int id, boolean on)
    {
        super(id, 66, Material.glass);
        setHardness(0.3F);
        setResistance(20F);
        setStepSound(Block.soundStoneFootstep);
        setRequiresSelfNotify();

        if (on)
        {
            setCreativeTab(CreativeTabs.tabDecorations);
        }
    }

    @Override
    public String getDefaultName()
    {
        return "Light";
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new PCli_TileEntityLight();
    }

    @Override
    public int getBlockTexture(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5)
    {
        PCli_TileEntityLight te = PC_Utils.getTE(par1iBlockAccess, par2, par3, par4);

        if (te == null) return
                    super.getBlockTexture(par1iBlockAccess, par2, par3, par4, par5);

        if (!te.isHuge()) return
                    66;

        return 117;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, int i, int j, int k, int l)
    {
        return true;
    }
    
    @Override
	public int func_85104_a(World world, int x, int y, int z, int l, float par6, float par7, float par8, int par9) {
    	int metadata = 0;
		if (l == 2) {
			metadata = 1;
		} else if (l == 3) {
			metadata = 2;
		} else if (l == 4) {
			metadata = 3;
		} else if (l == 5) {
			metadata = 4;
		}else if (l == 0) {
			metadata = 5;
		}
		return metadata;
    }
    
	@Override
    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving player)
    {
        PCli_TileEntityLight tileentity = PC_Utils.getTE(world, i, j, k);

        if (tileentity != null && tileentity.isStable())
        {
            return;
        }

        onPoweredBlockChange(world, i, j, k, world.isBlockIndirectlyGettingPowered(i, j, k));
    }

    @Override
    public boolean canPlaceBlockAt(World world, int i, int j, int k)
    {
        return true;
    }

    private int[] meta2side = { 1, 2, 3, 4, 5, 0 };

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        ItemStack ihold = entityplayer.getCurrentEquippedItem();

        if (ihold != null)
        {
            if (ihold.getItem().shiftedIndex == Item.dyePowder.shiftedIndex)
            {
                PC_Utils.<PCli_TileEntityLight>getTE(world, i, j, k, blockID).setColor(new PC_Color(PC_Color.getHexColorForName(ItemDye.dyeColorNames[ihold.getItemDamage()])));
                return true;
            }
        }

        PC_Utils.openGres("Light", entityplayer, i, j, k);
        return true;
    }

    @Override
    public void onNeighborBlockChange(World world, int i, int j, int k, int l)
    {
        PCli_TileEntityLight tileentity = PC_Utils.getTE(world, i, j, k, blockID);

        if (tileentity == null || tileentity.isStable())
        {
            return;
        }

        boolean powered = world.isBlockIndirectlyGettingPowered(i, j, k) ;

        if (tileentity.isActive() != powered)
        {
            world.scheduleBlockUpdate(i, j, k, blockID, 1);
        }
    }

    @Override
    public void updateTick(World world, int i, int j, int k, Random random)
    {
        PCli_TileEntityLight tileentity = PC_Utils.getTE(world, i, j, k);

        if (tileentity == null || tileentity.isStable())
        {
            return;
        }

        boolean powered = world.isBlockGettingPowered(i, j, k) || world.isBlockIndirectlyGettingPowered(i, j, k)
                ;

        if (tileentity.isActive() != powered)
        {
            onPoweredBlockChange(world, i, j, k, powered);
        }
    }

    private boolean isAttachmentBlockPowered(World world, int x, int y, int z, int side)
    {
        if (side == 0)
        {
            return world.isBlockGettingPowered(x, y - 1, z) && world.getBlockId(x, y - 1, x) != 0;
        }

        if (side == 1)
        {
            return world.isBlockGettingPowered(x, y, z + 1) && world.getBlockId(x, y, x + 1) != 0;
        }

        if (side == 2)
        {
            return world.isBlockGettingPowered(x, y, z - 1) && world.getBlockId(x, y, x - 1) != 0;
        }

        if (side == 3)
        {
            return world.isBlockGettingPowered(x + 1, y, z) && world.getBlockId(x + 1, y, x) != 0;
        }

        if (side == 4)
        {
            return world.isBlockGettingPowered(x - 1, y, z) && world.getBlockId(x - 1, y, x) != 0;
        }

        if (side == 5)
        {
            return world.isBlockGettingPowered(x, y + 1, z) && world.getBlockId(x, y + 1, x) != 0;
        }

        return false;
    }

    public static void onPoweredBlockChange(World world, int x, int y, int z, boolean rs_state)
    {
        PCli_TileEntityLight tileentity = PC_Utils.getTE(world, x, y, z);

        if ((tileentity == null || tileentity.isStable()) && rs_state == false)
        {
            return;
        }

        PC_Utils.setBlockState(world, x, y, z, rs_state);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k)
    {
        int i1 = iblockaccess.getBlockMetadata(i, j, k);
        PCli_TileEntityLight te = PC_Utils.getTE(iblockaccess, i, j, k);

        if (te == null)
        {
            return;
        }

        float sidehalf = te.isHuge() ? 0.5F : 0.1875F;
        float height = te.isHuge() ? 0.0625F * 3 : 0.0625F * 2;

        if (i1 == 0)
        {
            setBlockBounds((float) 0.5 - sidehalf, 0, (float) 0.5 - sidehalf, (float) 0.5 + sidehalf, height, (float) 0.5 + sidehalf);
        }
        else if (i1 == 1)
        {
            setBlockBounds((float) 0.5 - sidehalf, (float) 0.5 - sidehalf, 1 - height, (float) 0.5 + sidehalf, (float) 0.5 + sidehalf, 1);
        }
        else if (i1 == 2)
        {
            setBlockBounds((float) 0.5 - sidehalf, (float) 0.5 - sidehalf, 0, (float) 0.5 + sidehalf, (float) 0.5 + sidehalf, height);
        }
        else if (i1 == 3)
        {
            setBlockBounds(1 - height, (float) 0.5 - sidehalf, (float) 0.5 - sidehalf, 1, (float) 0.5 + sidehalf, (float) 0.5 + sidehalf);
        }
        else if (i1 == 4)
        {
            setBlockBounds(0, (float) 0.5 - sidehalf, (float) 0.5 - sidehalf, height, (float) 0.5 + sidehalf, (float) 0.5 + sidehalf);
        }

        if (i1 == 5)
        {
            setBlockBounds((float) 0.5 - sidehalf, 1 - height, (float) 0.5 - sidehalf, (float) 0.5 + sidehalf, 1, (float) 0.5 + sidehalf);
        }
    }

    @Override
    public void setBlockBoundsForItemRender()
    {
    }

    private PC_Color getColor(IBlockAccess w, int i, int j, int k)
    {
        PCli_TileEntityLight tei = PC_Utils.getTE(w, i, j, k);

        if (tei == null)
        {
            return null;
        }

        return tei.getColor();
    }

    @Override
    public int quantityDropped(Random random)
    {
        return 0;
    }

    @Override
    public int idDropped(int i, Random random, int j)
    {
        return -1;
    }

    @Override
    public void randomDisplayTick(World world, int i, int j, int k, Random random)
    {
        PCli_TileEntityLight tei = PC_Utils.getTE(world, i, j, k);

        if (!tei.isActive())
        {
            return;
        }

        try
        {
            if (tei.isHuge())
            {
                return;
            }

            if (tei.isStable() && world.rand.nextInt(3) != 0)
            {
                return;
            }
        }
        catch (NullPointerException e) {}

        int l = world.getBlockMetadata(i, j, k);
        PC_Color color = getColor(world, i, j, k);

        if (color == null)
        {
            return;
        }

        double ii = i + 0.5D;
        double jj = j + 0.5D;
        double kk = k + 0.5D;
        double h = 0.22D;
        double r = color.r, g = color.g, b = color.b;
        r = (r == 0) ? 0.001D : r;
        g = (g == 0) ? 0.001D : g;
        b = (b == 0) ? 0.001D : b;

        if (l == 0)
        {
            world.spawnParticle("reddust", ii, j + h, kk, r, g, b);
        }
        else if (l == 1)
        {
            world.spawnParticle("reddust", ii, jj, k + 1 - h, r, g, b);
        }
        else if (l == 2)
        {
            world.spawnParticle("reddust", ii, jj, k + h, r, g, b);
        }
        else if (l == 3)
        {
            world.spawnParticle("reddust", i + 1 - h, jj, kk, r, g, b);
        }
        else if (l == 4)
        {
            world.spawnParticle("reddust", i + h, jj, kk, r, g, b);
        }

        if (l == 5)
        {
            world.spawnParticle("reddust", i, jj + 1 - h, kk, r, g, b);
        }
    }

    @Override
    public String getCraftingToolModule()
    {
        return mod_PowerCraftLight.getInstance().getNameWithoutPowerCraft();
    }

    @Override
    public List<ItemStack> getItemStacks(List<ItemStack> arrayList)
    {
        arrayList.add(new ItemStack(this));
        return arrayList;
    }

    @Override
    public void loadFromConfig(Configuration config)
    {
        on.setLightValue(PC_Utils.getConfigInt(config, Configuration.CATEGORY_GENERAL, "LampLightValueOn", 12) / 16.0f);
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, Object renderer)
    {
        PC_Renderer.swapTerrain(block);
        float sidehalf = 0.1875F;
        float height = 0.15F;
        PC_Renderer.glColor3f(1.0f, 1.0f, 1.0f);
        block.setBlockBounds(0.5F - sidehalf, 0.5F - sidehalf, 0.5F - height / 2F, 0.5F + sidehalf, 0.5F + sidehalf, 0.5F + height / 2F);
        PC_Renderer.renderInvBoxWithTexture(renderer, block, 66);
        PC_Renderer.resetTerrain(true);
    }

    @Override
    public void renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, Object renderer) {}
}
