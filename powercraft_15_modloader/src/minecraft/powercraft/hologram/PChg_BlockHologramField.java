package powercraft.hologram;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.item.PC_IItemInfo;
import powercraft.api.registry.PC_ItemRegistry;
import powercraft.api.registry.PC_KeyRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;

@PC_BlockInfo(name="Hologram Field", tileEntity=PChg_TileEntityHologramField.class)
public class PChg_BlockHologramField extends PC_Block implements PC_IItemInfo {

	public PChg_BlockHologramField(int id) {
		super(id, Material.ground, "hologram");
		setHardness(0.5F);
        setResistance(0.5F);
        setStepSound(Block.soundGlassFootstep);
        setCreativeTab(CreativeTabs.tabDecorations);
	}
	
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
        return null;
    }

	@Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float par7, float par8, float par9)
    {
        ItemStack ihold = entityplayer.getCurrentEquippedItem();

        PChg_TileEntityHologramField tileentity = PC_Utils.getTE(world, i, j, k);
        
        if (ihold != null)
        {
            if (ihold.getItem() == PC_ItemRegistry.getPCItemByName("PCco_ItemActivator"))
            {
                
                if (PC_KeyRegistry.isPlacingReversed(entityplayer))
                {
                    switch(l){
                    case 0:l=1;break;
                    case 1:l=0;break;
                    case 2:l=3;break;
                    case 3:l=2;break;
                    case 4:l=5;break;
                    case 5:l=4;break;
                    }
                }

                if (tileentity != null)
                {
                	PC_VecI coordOffset = tileentity.getOffset();
                	if(coordOffset==null)
                		coordOffset = new PC_VecI();
                	if(!world.isRemote){
	                    switch (l)
	                    {
	                        case 0:
	                            coordOffset.y+=2;
	                            break;
	
	                        case 1:
	                            coordOffset.y-=2;
	                            break;
	                            
	                        case 2:
	                            coordOffset.z-=2;
	                            break;
	
	                        case 3:
	                            coordOffset.z+=2;
	                            break;
	
	                        case 4:
	                            coordOffset.x-=2;
	                            break;
	
	                        case 5:
	                            coordOffset.x+=2;
	                            break;
	                    }
	                    
	                    coordOffset.x = MathHelper.clamp_int(coordOffset.x, -100, 100);
	                    coordOffset.y = MathHelper.clamp_int(coordOffset.y+tileentity.yCoord, 0, world.getActualHeight())-tileentity.yCoord;
	                    coordOffset.z = MathHelper.clamp_int(coordOffset.z, -100, 100);
	                    tileentity.setOffset(coordOffset);
                	}
                }

                return true;
            }
        }
        
        return false;
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
	public boolean renderInventoryBlock(int metadata, Object renderer){
		float s = 1.0f/16.0f;
		PC_Utils.setBlockBounds(Block.blockIron, 0, 0, 0, s*15, s*5, s);
    	PC_Renderer.renderInvBox(renderer, Block.blockIron, 0);
    	PC_Utils.setBlockBounds(Block.blockIron, s*15, 0, 0, s*16, s*5, s*15);
    	PC_Renderer.renderInvBox(renderer, Block.blockIron, 0);
    	PC_Utils.setBlockBounds(Block.blockIron, s, 0, s*15, s*16, s*5, s*16);
    	PC_Renderer.renderInvBox(renderer, Block.blockIron, 0);
    	PC_Utils.setBlockBounds(Block.blockIron, 0, 0, s, s, s*5, s*16);
    	PC_Renderer.renderInvBox(renderer, Block.blockIron, 0);
    	PC_Utils.setBlockBounds(Block.blockIron, s, 0, s, s*15, s, s*15);
    	PC_Renderer.renderInvBox(renderer, Block.blockIron, 0);
    	PC_Utils.setBlockBounds(Block.blockIron, 0, 0, 0, 1, 1, 1);	
    	setBlockBounds(s*3, s*6, s*3, s*13, s*16, s*13);
    	PC_Renderer.renderInvBox(renderer, this, 0);
    	setBlockBounds(0, 0, 0, 1, 1, 1);
    	return true;
    }
	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Object renderer) {
		float s = 1.0f/16.0f;
		PC_Renderer.tessellatorDraw();
        PC_Renderer.tessellatorStartDrawingQuads();
        PC_Utils.setBlockBounds(Block.blockIron, 0, 0, 0, s*15, s*5, s);
		PC_Renderer.renderStandardBlock(renderer, Block.blockIron, x, y, z);
		PC_Utils.setBlockBounds(Block.blockIron, s*15, 0, 0, s*16, s*5, s*15);
    	PC_Renderer.renderStandardBlock(renderer, Block.blockIron, x, y, z);
    	PC_Utils.setBlockBounds(Block.blockIron, s, 0, s*15, s*16, s*5, s*16);
    	PC_Renderer.renderStandardBlock(renderer, Block.blockIron, x, y, z);
    	PC_Utils.setBlockBounds(Block.blockIron, 0, 0, s, s, s*5, s*16);
    	PC_Renderer.renderStandardBlock(renderer, Block.blockIron, x, y, z);
    	PC_Utils.setBlockBounds(Block.blockIron, s, 0, s, s*15, s, s*15);
    	PC_Renderer.renderStandardBlock(renderer, Block.blockIron, x, y, z);
    	PC_Utils.setBlockBounds(Block.blockIron, 0, 0, 0, 1, 1, 1);
    	PC_Renderer.tessellatorDraw();
        PC_Renderer.tessellatorStartDrawingQuads();
        setBlockBounds(s*3, s*6, s*3, s*13, s*16, s*13);
        PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
    	setBlockBounds(0, 0, 0, 1, 1, 1);
    	PC_Renderer.tessellatorDraw();
        PC_Renderer.tessellatorStartDrawingQuads();
        return true;
    }
	
	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this));
		return arrayList;
	}

}
