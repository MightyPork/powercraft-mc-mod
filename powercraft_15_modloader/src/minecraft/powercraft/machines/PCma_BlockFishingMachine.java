package powercraft.machines;

import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Icon;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.recipes.PC_3DRecipe;
import powercraft.api.recipes.PC_I3DRecipeHandler;
import powercraft.api.registry.PC_SoundRegistry;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Struct2;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;

@PC_BlockInfo(name="Fishing Machine", tileEntity=PCma_TileEntityFishingMachine.class)
public class PCma_BlockFishingMachine extends PC_Block implements PC_I3DRecipeHandler {

	private static PC_3DRecipe struct;
	
	public PCma_BlockFishingMachine(int id) {
		super(id, Material.iron);
		struct = new PC_3DRecipe(null, 
				new String[]{
				"www",
				"www",
				"www"},
				new String[]{
				"www",
				"www",
				"www"},
				new String[]{
				"www",
				"www",
				"www"},
				new String[]{
				"www",
				"www",
				"www"}, 
				new String[]{
				"www",
				"www",
				"www"},
				new String[]{
				"fpf",
				"pmp",
				"fpf"},
				new String[]{
				" !c ",
				"!cc!c",
				" !c "},
				'w', Block.waterMoving, Block.waterStill, 'f', Block.fence, 'p', Block.planks, 'm', this, 'c', Block.chest);
		
	}
	
	@Override
	public boolean showInCraftingTool() {
		return false;
	}
	
	@Override
	public int idDropped(int par1, Random par2Random, int par3) {
		return Block.blockIron.blockID;
	}

	@Override
	public int damageDropped(int par1)
    {
        return par1;
    }
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public int idPicked(World par1World, int par2, int par3, int par4){
		return Block.blockIron.blockID;
	}
	
	public static boolean isStructOK(World world, PC_VecI pos){
		return struct.getStructRotation(world, pos.offset(-1, -5, -1), 0);
	}
	
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if(PC_Utils.<PCma_TileEntityFishingMachine>getTE(world, x, y, z).isRunning()){
			// bubbles
			for (int i = 0; i < 2; i++) {
				double buX = x + 0.2D + rand.nextFloat() * 0.6F;
				double buY = y - 4.8D + rand.nextFloat() * 0.6F;
				double buZ = z + 0.2D + rand.nextFloat() * 0.6F;
				world.spawnParticle("bubble", buX, buY, buZ, 0, 0.01F, 0);
			}
	
			// splash sound
			if (rand.nextInt(20) == 0 && PC_SoundRegistry.isSoundEnabled()) {
				PC_SoundRegistry.playSound(x, y, z, "random.splash", 0.08F, 0.5F + rand.nextFloat() * 0.3F);
			}
	
			// smoke from chimney
			if (rand.nextInt(2) == 0) {
				double chimX = x + 0.5;
				double chimY = y + 2.4F;
				double chimZ = z + 0.5;
				int rota = PC_Utils.getMD(world, x, y+1, z);
				if (rota == 2){
					chimX += 0.6;
		        }else if (rota == 3){
		        	chimX -= 0.6;
		        }else if (rota == 4){
		        	chimZ -= 0.6;
		        }else if (rota == 5){
		        	chimZ += 0.6;
		        }
				world.spawnParticle("largesmoke", chimX, chimY, chimZ, 0, 0, 0);
			}
		}
	}

	@Override
	public Icon getIcon(PC_Direction par1, int par2) {
		return Block.blockIron.getIcon(par1.getMCDir(), par2);
	}

	@Override
	public boolean foundStructAt(EntityPlayer entityplayer, World world, PC_Struct2<PC_VecI, Integer> structStart) {
		PC_VecI pos = structStart.a.offset(1, 5, 1);
		int fishingMachine = PCma_App.fishingMachine.blockID;
		for(int z=-2; z<=2; z++){
			for(int x=-2; x<=2; x++){
				int blockID = PC_Utils.getBID(world, pos.x + x, pos.y, pos.z - z);
				if(blockID==fishingMachine){
					return false;
				}
			}
		}
		int meta = PC_Utils.getMD(world, pos);
		PC_Utils.setBID(world, pos, blockID, meta);
		return true;
	}

	@Override
	public boolean canBeCrafted() {
		return blockID!=-1;
	}

}
