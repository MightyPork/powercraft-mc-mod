package powercraft.logic;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.launcher.PC_Property;
import powercraft.api.PC_MathHelper;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Utils.GameInfo;
import powercraft.api.PC_Utils.ValueWriting;
import powercraft.api.PC_VecI;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.annotation.PC_Shining;
import powercraft.api.annotation.PC_Shining.OFF;
import powercraft.api.annotation.PC_Shining.ON;
import powercraft.api.block.PC_Block;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.registry.PC_KeyRegistry;
import powercraft.api.registry.PC_MSGRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.tileentity.PC_TileEntity;

@PC_Shining
@PC_BlockInfo(itemBlock=PClo_ItemBlockSpecial.class, tileEntity=PClo_TileEntitySpecial.class)
public class PClo_BlockSpecial extends PC_Block
{
    @ON
    public static PClo_BlockSpecial on;
    @OFF
    public static PClo_BlockSpecial off;

    public PClo_BlockSpecial(int id, boolean on)
    {
        super(id, 6, Material.ground);
        setHardness(0.35F);
        setStepSound(Block.soundWoodFootstep);
        disableStats();
        setRequiresSelfNotify();
        setResistance(30.0F);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.1875F, 1.0F);

        if (on)
        {
            setCreativeTab(CreativeTabs.tabRedstone);
        }
    }

    @Override
    public TileEntity newTileEntity(World world, int metadata) {
        return new PClo_TileEntitySpecial();
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random)
    {
        PClo_TileEntitySpecial te = getTE(world, x, y, z);
        boolean shouldState = false;
        boolean state = isActive(world, x, y, z);
        int rot = getRotation_static(GameInfo.getMD(world, x, y, z));
        int xAdd = 0, zAdd = 0;

        if (rot == 0)
        {
            zAdd = 1;
        }
        else if (rot == 1)
        {
            xAdd = -1;
        }
        else if (rot == 2)
        {
            zAdd = -1;
        }
        else if (rot == 3)
        {
            xAdd = 1;
        }

        switch (te.getType())
        {
            case PClo_SpecialType.DAY:
                shouldState = world.isDaytime();
                break;

            case PClo_SpecialType.NIGHT:
                shouldState = !world.isDaytime();
                break;

            case PClo_SpecialType.RAIN:
                shouldState = world.isRaining();
                break;

            case PClo_SpecialType.CHEST_EMPTY:
                shouldState = GameInfo.isChestEmpty(world, x + xAdd, y, z + zAdd, te.getStackInSlot(0));
                break;

            case PClo_SpecialType.CHEST_FULL:
                shouldState = GameInfo.isChestFull(world, x + xAdd, y, z + zAdd, te.getStackInSlot(0));
                break;

            case PClo_SpecialType.SPECIAL:
                shouldState = GameInfo.poweredFromInput(world, x, y, z, PC_Utils.BACK, rot);
                TileEntity tes = GameInfo.getTE(world, x - xAdd, y, z - zAdd);

                if (tes instanceof PClo_TileEntityPulsar)
                {
                    ((PClo_TileEntityPulsar) tes).setPaused(shouldState);
                }

                if (shouldState == true && shouldState != state)
                {
                    spawnMobsFromSpawners(world, x, y, z);
                }

                break;
        }

        if (state != shouldState)
        {
            ValueWriting.setBlockState(world, x, y, z, shouldState);
        }
    }

    private void spawnMobsFromSpawners(World world, int x, int y, int z)
    {
        ValueWriting.spawnMobFromSpawner(world, x + 1, y, z);
        ValueWriting.spawnMobFromSpawner(world, x - 1, y, z);
        ValueWriting.spawnMobFromSpawner(world, x, y + 1, z);
        ValueWriting.spawnMobFromSpawner(world, x, y, z + 1);
        ValueWriting.spawnMobFromSpawner(world, x, y, z - 1);
    }

    @Override
    public int tickRate()
    {
        return 1;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int side)
    {
        world.scheduleBlockUpdate(x, y, z, blockID, tickRate());
    }

    private boolean isOutputActive(World world, int x, int y, int z)
    {
        int rot = getRotation_static(GameInfo.getMD(world, x, y, z));
        return false;
    }

    @Override
   	public boolean isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int s) {
    	int meta = GameInfo.getMD(world, x, y, z);
        int rotation = getRotation_static(meta);

        if (!isActive(world, x, y, z))
        {
            return false;
        }

        switch (getType(world, x, y, z))
        {
            case PClo_SpecialType.DAY:
            case PClo_SpecialType.NIGHT:
            case PClo_SpecialType.RAIN:
                return true;

            case PClo_SpecialType.CHEST_EMPTY:
            case PClo_SpecialType.CHEST_FULL:
                if ((rotation == 0 && s == 3) || (rotation == 1 && s == 4) || (rotation == 2 && s == 2) || (rotation == 3 && s == 5))
                {
                    return true;
                }

                break;
        }

        return false;
   	}

   	@Override
   	public boolean isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int s) {
   		return isProvidingWeakPower(world, x, y, z, s);
   	}

    @Override
    public boolean canProvidePower()
    {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return PC_Renderer.getRendererID(true);
    }

    public static PClo_TileEntitySpecial getTE(IBlockAccess world, int x, int y, int z)
    {
        TileEntity te = GameInfo.getTE(world, x, y, z);

        if (te instanceof PClo_TileEntitySpecial)
        {
            return (PClo_TileEntitySpecial)te;
        }

        return null;
    }

    public static int getType(IBlockAccess world, int x, int y, int z)
    {
        PClo_TileEntitySpecial te = getTE(world, x, y, z);

        if (te != null)
        {
            return te.getType();
        }

        return 0;
    }

    public static boolean isActive(IBlockAccess world, int x, int y, int z)
    {
        return GameInfo.getBID(world, x, y, z) == on.blockID;
    }

    @Override
    public int getBlockTexture(IBlockAccess iblockaccess, int x, int y, int z, int side)
    {
        if (side == 1)
        {
            return getTopFaceFromEnum(getType(iblockaccess, x, y, z)) + (isActive(iblockaccess, x, y, z) ? 16 : 0);
        }

        if (side == 0)
        {
            return 6;
        }

        return 5;
    }

    @Override
    public int getBlockTextureFromSideAndMetadata(int side, int meta)
    {
        if (side == 0)
        {
            return 6;
        }

        if (side == 1)
        {
            return getTopFaceFromEnum(meta) + 16;
        }
        else
        {
            return 5;
        }
    }

    private int getTopFaceFromEnum(int meta)
    {
        if (meta >= 0 && meta < PClo_SpecialType.TOTAL_SPECIAL_COUNT)
        {
            return PClo_SpecialType.index[meta];
        }

        return 6;
    }

    public static int getRotation_static(int meta)
    {
        return meta & 0x3;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int x, int y, int z)
    {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.1875F, 1.0F);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entityliving)
    {
        int type = getType(world, x, y, z);
        int l = ((PC_MathHelper.floor_double(((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3) + 2) % 4;

        if (entityliving instanceof EntityPlayer && PC_KeyRegistry.isPlacingReversed(((EntityPlayer)entityliving)))
        {
            l = ValueWriting.reverseSide(l);
        }

        world.setBlockMetadataWithNotify(x, y, z, l);
        ValueWriting.hugeUpdate(world, x, y, z);
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random)
    {
        if (!isActive(world, x, y, z))
        {
            return;
        }

        if (random.nextInt(3) != 0)
        {
            return;
        }

        double d = (x + 0.5F) + (random.nextFloat() - 0.5F) * 0.20000000000000001D;
        double d1 = (y + 0.2F) + (random.nextFloat() - 0.5F) * 0.20000000000000001D;
        double d2 = (z + 0.5F) + (random.nextFloat() - 0.5F) * 0.20000000000000001D;
        world.spawnParticle("reddust", d, d1, d2, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public int idDropped(int i, Random random, int j)
    {
        return -1;
    }

    @Override
    public int quantityDropped(Random random)
    {
        return 0;
    }

    @Override
    public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z)
    {
        int type = getType(world, x, y, z);
        boolean remove = super.removeBlockByPlayer(world, player, x, y, z);

        if (remove && !GameInfo.isCreative(player))
        {
            dropBlockAsItem_do(world, x, y, z, new ItemStack(PClo_App.special, 1, type));
        }

        return remove;
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        int type = getType(world, i, j, k);

        if (type != PClo_SpecialType.CHEST_EMPTY && type != PClo_SpecialType.CHEST_FULL)
        {
            return false;
        }

        ItemStack ihold = entityplayer.getCurrentEquippedItem();

        if (ihold != null)
        {
            if (ihold.getItem() instanceof ItemBlock)
            {
                if (ihold.itemID == blockID)
                {
                    return false;
                }
            }
        }

        PC_GresRegistry.openGres("Special", entityplayer, GameInfo.<PC_TileEntity>getTE(world, i, j, k));
        return true;
    }
    
	@Override
	public Object msg(IBlockAccess world, PC_VecI pos, int msg, Object... obj) {
		switch(msg){
		case PC_MSGRegistry.MSG_LOAD_FROM_CONFIG:
			on.setLightValue(((PC_Property)obj[0]).getInt("brightness", 15) * 0.0625F);
			break;
		case PC_MSGRegistry.MSG_BLOCK_FLAGS:{
			List<String> list = (List<String>)obj[0];
			list.add(PC_Utils.NO_HARVEST);
			list.add(PC_Utils.NO_PICKUP);
	   		return list;
		}case PC_MSGRegistry.MSG_ITEM_FLAGS:{
			List<String> list = (List<String>)obj[1];
			list.add(PC_Utils.NO_BUILD);
			return list;
		}case PC_MSGRegistry.MSG_RENDER_ITEM_HORIZONTAL:
			return false;
		case PC_MSGRegistry.MSG_ROTATION:
			return getRotation_static((Integer)obj[0]);
		default:
			return null;
		}
		return true;
	}
}
