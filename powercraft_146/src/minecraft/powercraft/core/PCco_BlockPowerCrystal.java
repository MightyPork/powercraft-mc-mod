package powercraft.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.management.PC_BeamTracer;
import powercraft.management.PC_BeamTracer.BeamSettings;
import powercraft.management.PC_BeamTracer.result;
import powercraft.management.PC_Block;
import powercraft.management.PC_Color;
import powercraft.management.PC_MathHelper;
import powercraft.management.PC_Property;
import powercraft.management.PC_Renderer;
import powercraft.management.PC_Utils;
import powercraft.management.PC_Utils.GameInfo;
import powercraft.management.PC_Utils.ValueWriting;
import powercraft.management.PC_VecI;

public class PCco_BlockPowerCrystal extends PC_Block
{
    public static boolean makeSound;
    public static int genCrystalsInChunk;
    public static int genCrystalsDepositMaxCount;
    public static int genCrystalsMaxY;
    public static int genCrystalsMinY;

    public PCco_BlockPowerCrystal(int id)
    {
        super(id, 4, Material.glass);
        setHardness(0.5F);
        setResistance(0.5F);
        setStepSound(Block.soundGlassFootstep);
        setLightValue(1.0F);
        setCreativeTab(CreativeTabs.tabMaterials);
    }

    @Override
    public void onBlockAdded(World world, int i, int j, int k)
    {
        super.onBlockAdded(world, i, j, k);
    }

    @Override
    public int getBlockColor()
    {
        return PC_Color.crystal_colors[2];
    }

    @Override
    public int getRenderColor(int i)
    {
        return PC_Color.crystal_colors[PC_MathHelper.clamp_int(i, 0, 7)];
    }

    @Override
    public int colorMultiplier(IBlockAccess iblockaccess, int i, int j, int k)
    {
        return PC_Color.crystal_colors[PC_MathHelper.clamp_int(iblockaccess.getBlockMetadata(i, j, k), 0, 7)];
    }

    @Override
    public int getRenderBlockPass()
    {
        return 1;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
    {
        return true;
    }

    @Override
    public int quantityDropped(Random random)
    {
        return 1;
    }

    @Override
    public int damageDropped(int i)
    {
        return i;
    }

    @Override
    public int idDropped(int i, Random random, int j)
    {
        return blockID;
    }

    @Override
    public int getMobilityFlag()
    {
        return 0;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public void randomDisplayTick(World world, int i, int j, int k, Random random)
    {
        int id_under = world.getBlockId(i, j - 1, k);

        if (makeSound && GameInfo.isSoundEnabled())
        {
            EntityPlayer player = world.getClosestPlayer(i + 0.5D, j + 0.5D, k + 0.5D, 12);

            if (player != null)
            {
                if (id_under == Block.stone.blockID || id_under == 7 || id_under == blockID)
                {
                    int distance = (int) Math.round(player.getDistanceSq(i + 0.5D, j + 0.5D, k + 0.5D) / 10);

                    if (distance == 0)
                    {
                        distance = 1;
                    }

                    if (random.nextInt(distance) == 0)
                    {
                        ValueWriting.playSound(i + 0.5D, j + 0.5D, k + 0.5D, "random.orb", 0.15F,
                                0.5F * ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.8F));
                    }
                }
            }
        }

        int meta = world.getBlockMetadata(i, j, k);

        if (meta == 8)
        {
            world.setBlockMetadataWithNotify(i, j, k, 0);
            meta = 0;
        }

        double r = PC_Color.red(getRenderColor(meta));
        double g = PC_Color.green(getRenderColor(meta));
        double b = PC_Color.blue(getRenderColor(meta));
        r = (r > 0D ? r : 0.001D);
        g = (g > 0D ? g : 0.001D);
        b = (b > 0D ? b : 0.001D);
        float y = j + random.nextFloat();
        float x = i + random.nextFloat();
        float z = k + random.nextFloat();
        world.spawnParticle("reddust", x, y, z, r, g, b);
    }

    public void renderInventoryBlock(Block block, int metadata, int modelID, Object renderer){
        PC_Renderer.swapTerrain(block);
        Random posRand = new Random(metadata);

        for (int q = 3 + posRand.nextInt(3); q > 0; q--)
        {
            float x, y, z, a, b, c;
            x = 0.0F + posRand.nextFloat() * 0.6F;
            y = 0.0F + posRand.nextFloat() * 0.6F;
            z = 0.0F + posRand.nextFloat() * 0.6F;
            a = 0.2F + Math.max(posRand.nextFloat() * (0.7F - x), 0.3F);
            b = 0.2F + Math.max(posRand.nextFloat() * (0.7F - y), 0.3F);
            c = 0.2F + Math.max(posRand.nextFloat() * (0.7F - z), 0.3F);
            ValueWriting.setBlockBounds(block, x, y, z, x + a, y + b, z + c);
            PC_Renderer.renderInvBox(renderer, block, metadata);
        }

        ValueWriting.setBlockBounds(block, 0, 0, 0, 1, 1, 1);
        PC_Renderer.resetTerrain(true);
    }

    public void renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, Object renderer) {
        PC_Renderer.tessellatorDraw();
        PC_Renderer.swapTerrain(block);
        PC_Renderer.tessellatorStartDrawingQuads();
        Random posRand = new Random(x + x * y * z + z + world.getBlockMetadata(x, y, z));

        for (int q = 3 + posRand.nextInt(2); q > 0; q--)
        {
            float i, j, k, a, b, c;
            i = posRand.nextFloat() * 0.6F;
            j = (q == 2 ? 0.001F : posRand.nextFloat() * 0.6F);
            k = posRand.nextFloat() * 0.6F;
            a = i + 0.3F + posRand.nextFloat() * (0.7F - i);
            b = j + 0.3F + posRand.nextFloat() * (0.7F - j);
            c = k + 0.3F + posRand.nextFloat() * (0.7F - k);
            ValueWriting.setBlockBounds(block, i, j, k, a, b, c);
            PC_Renderer.renderStandardBlock(renderer, block, x, y, z);
        }

        ValueWriting.setBlockBounds(block, 0, 0, 0, 1, 1, 1);
        PC_Renderer.tessellatorDraw();
        PC_Renderer.resetTerrain(true);
        PC_Renderer.tessellatorStartDrawingQuads();
    }

	@Override
	public Object msg(IBlockAccess world, PC_VecI pos, int msg, Object... obj) {
		switch(msg){
		case PC_Utils.MSG_LOAD_FROM_CONFIG:
			setLightValue(((PC_Property)obj[0]).getInt("brightness", 16) * 0.0625F);
			makeSound = ((PC_Property)obj[0]).getBoolean("makeSound", true);
			genCrystalsInChunk = ((PC_Property)obj[0]).getInt("spawn.in_chunk", 3, "Number of deposits in each 16x16 chunk.");
    		genCrystalsDepositMaxCount = ((PC_Property)obj[0]).getInt("spawn.deposit_max_size", 4, "Highest crystal count in one deposit");
    		genCrystalsMaxY = ((PC_Property)obj[0]).getInt("spawn.min_y", 5, "Min Y coordinate of crystal deposits.");
    		genCrystalsMinY = ((PC_Property)obj[0]).getInt("spawn.max_y", 15, "Max Y coordinate of crystal deposits.");
			break;
		case PC_Utils.MSG_RENDER_INVENTORY_BLOCK:
			renderInventoryBlock((Block)obj[0], (Integer)obj[1], (Integer)obj[2], obj[3]);
			break;
		case PC_Utils.MSG_RENDER_WORLD_BLOCK:
			renderWorldBlock(world, pos.x, pos.y, pos.z, (Block)obj[0], (Integer)obj[1], obj[2]);
			break;
		case PC_Utils.MSG_SPAWNS_IN_CHUNK:
			return genCrystalsInChunk;
		case PC_Utils.MSG_BLOCKS_ON_SPAWN_POINT:
			return ((Random)obj[0]).nextInt(PC_MathHelper.clamp_int(genCrystalsDepositMaxCount - 1, 1, 10)) + 2;
		case PC_Utils.MSG_SPAWN_POINT:
			return new PC_VecI(((Random)obj[0]).nextInt(16),
					((Random)obj[0]).nextInt(PC_MathHelper.clamp_int(genCrystalsMaxY - genCrystalsMinY, 1, 255)) + genCrystalsMinY,
					((Random)obj[0]).nextInt(16));
		case PC_Utils.MSG_SPAWN_POINT_METADATA:
			return ((Random)obj[0]).nextInt(8);
		case PC_Utils.MSG_ON_HIT_BY_BEAM_TRACER:
			BeamSettings bs = (BeamSettings)obj[0];
			pos = bs.getPos();
			bs.setColor(PC_Color.fromHex(colorMultiplier(world, pos.x, pos.y, pos.z)));
			Object crystalAdd = bs.getData("crystalAdd");
			if(crystalAdd instanceof Integer)
				bs.setLength(bs.getLength()+(Integer)crystalAdd);
			return result.CONTINUE;
		case PC_Utils.MSG_RATING:{
			List<Integer> l = new ArrayList<Integer>();
			l.add(10000);
			l.add(10000);
			l.add(10000);
			l.add(10000);
			l.add(10000);
			l.add(10000);
			l.add(10000);
			l.add(10000);
			return l;
		}
		default:
			return null;
		}
		return true;
	}
}
