package powercraft.machines;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.management.PC_Block;
import powercraft.management.PC_IItemInfo;
import powercraft.management.PC_PacketHandler;
import powercraft.management.PC_Utils;
import powercraft.management.PC_Utils.GameInfo;
import powercraft.management.PC_Utils.Gres;
import powercraft.management.PC_VecI;

public class PCma_BlockRoaster extends PC_Block implements PC_IItemInfo
{
    private static final int TXDOWN = 62, TXTOP = 61, TXSIDE = 46;

    public PCma_BlockRoaster(int id)
    {
        super(id, TXDOWN, Material.ground);
        setLightOpacity(0);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return true;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k)
    {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public int getBlockTextureFromSideAndMetadata(int s, int m)
    {
        if (s == 1)
        {
            return TXTOP;
        }

        if (s == 0)
        {
            return TXDOWN;
        }
        else
        {
            return TXSIDE;
        }
    }

    @Override
    public int tickRate()
    {
        return 4;
    }

    @Override
    public int idDropped(int i, Random random, int j)
    {
        return blockID;
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        ItemStack ihold = entityplayer.getCurrentEquippedItem();

        if (ihold != null)
        {
            if (ihold.getItem() instanceof ItemBlock && ihold.getItem().shiftedIndex != blockID)
            {
                return false;
            }
        }

        if (world.isRemote)
        {
            return true;
        }

        Gres.openGres("Roaster", entityplayer, i, j, k);
        return true;
    }

    @Override
    public TileEntity newTileEntity(World world, int metadata) {
        return new PCma_TileEntityRoaster();
    }

    public static boolean isIndirectlyPowered(World world, int x, int y, int z)
    {
    	PCma_TileEntityRoaster te = GameInfo.getTE(world, x, y, z);
    	if(world.isRemote){
    		if(te==null)
    			return false;
    		return te.isActive;
    	}
    	
    	boolean on=false;
    	
        if (world.isBlockGettingPowered(x, y, z))
        {
        	on= true;
        }

        if (world.isBlockIndirectlyGettingPowered(x, y, z))
        {
        	on= true;
        }

        if (world.isBlockGettingPowered(x, y - 1, z))
        {
        	on= true;
        }

        if (world.isBlockIndirectlyGettingPowered(x, y - 1, z))
        {
        	on= true;
        }

        if(on != te.isActive){
        	te.isActive = on;
        	PC_PacketHandler.setTileEntity(te, "isActive", on);
        }
        return on;
    }

    private static boolean hasFuel(World world, int x, int y, int z)
    {
        try
        {
            return ((PCma_TileEntityRoaster) world.getBlockTileEntity(x, y, z)).burnTime > 0;
        }
        catch (RuntimeException re)
        {
            return false;
        }
    }

    private boolean isNethering(World world, int x, int y, int z)
    {
        try
        {
            return ((PCma_TileEntityRoaster) world.getBlockTileEntity(x, y, z)).netherTime > 0 && isIndirectlyPowered(world, x, y, z);
        }
        catch (RuntimeException re)
        {
            return false;
        }
    }

    public static boolean isBurning(World world, int x, int y, int z)
    {
        return isIndirectlyPowered(world, x, y, z) && hasFuel(world, x, y, z);
    }

    @Override
    public void randomDisplayTick(World world, int i, int j, int k, Random random)
    {
        if (isBurning(world, i, j, k))
        {
            if (random.nextInt(24) == 0)
            {
                world.playSoundEffect(i + 0.5F, j + 0.5F, k + 0.5F, "fire.fire", 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F);
            }

            for (int c = 0; c < 5; c++)
            {
                float y = j + 0.74F + (random.nextFloat() * 0.3F);
                float x = i + 0.2F + (random.nextFloat() * 0.6F);
                float z = k + 0.2F + (random.nextFloat() * 0.6F);
                world.spawnParticle("smoke", x, y, z, 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", x, y, z, 0.0D, 0.0D, 0.0D);
            }

            for (int c = 0; c < 5; c++)
            {
                float y = j + 1.3F;
                float x = i + 0.2F + (random.nextFloat() * 0.6F);
                float z = k + 0.2F + (random.nextFloat() * 0.6F);
                world.spawnParticle("smoke", x, y, z, 0.0D, 0.0D, 0.0D);
            }
        }

        if (isNethering(world, i, j, k))
        {
            for (int c = 0; c < 8; c++)
            {
                float y = j + 0.74F + (random.nextFloat() * 0.3F);
                float x = i + 0.2F + (random.nextFloat() * 0.6F);
                float z = k + 0.2F + (random.nextFloat() * 0.6F);
                world.spawnParticle("reddust", x, y, z, 0.0D, 0.0D, 0.0D);
            }

            for (int c = 0; c < 20; c++)
            {
                float y = (float) j + -2 + (random.nextFloat() * 4F);
                float x = (float) i + -6 + (random.nextFloat() * 12F);
                float z = (float) k + -6 + (random.nextFloat() * 12F);
                world.spawnParticle("reddust", x, y, z, 0.6D, 0.001D, 0.001D);
            }
        }
    }

    @Override
    public List<ItemStack> getItemStacks(List<ItemStack> arrayList)
    {
        arrayList.add(new ItemStack(this));
        return arrayList;
    }

	@Override
	public Object msg(IBlockAccess world, PC_VecI pos, int msg, Object... obj) {
		switch (msg){
		case PC_Utils.MSG_DEFAULT_NAME:
			return "Roaster";
		case PC_Utils.MSG_ITEM_FLAGS:{
			List<String> list = (List<String>)obj[1];
			list.add(PC_Utils.NO_BUILD);
			return list;
		}case PC_Utils.MSG_BLOCK_FLAGS:{
			List<String> list = (List<String>)obj[0];
	   		list.add(PC_Utils.NO_HARVEST);
	   		list.add(PC_Utils.NO_PICKUP);
	   		list.add(PC_Utils.HARVEST_STOP);
	   		return list;
		}case PC_Utils.MSG_STR_MSG:{
			if("isBurning".equalsIgnoreCase((String) obj[0]))
				return isBurning((World)world, pos.x, pos.y, pos.z);
		}
		}
		return null;
	}
    
}
