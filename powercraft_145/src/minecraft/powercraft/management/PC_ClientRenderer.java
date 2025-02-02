package powercraft.management;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.Tessellator;
import net.minecraft.src.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class PC_ClientRenderer extends PC_Renderer implements ISimpleBlockRenderingHandler {
	
	public PC_ClientRenderer(boolean render3d){
		super(render3d);
		if(render3d)
			render3dId = RenderingRegistry.instance().getNextAvailableRenderId();
		else
			render2dId = RenderingRegistry.instance().getNextAvailableRenderId();
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		boolean b = false;
		if(block instanceof PC_IMSG){
			Object o = ((PC_IMSG)block).msg(PC_Utils.MSG_RENDER_INVENTORY_BLOCK, block, metadata, modelID, renderer);
			if(o instanceof Boolean)
				b = (Boolean)o;
		}
		if(!b){
			iRenderInvBlockRotatedBox(block, metadata, modelID, renderer);
		}/*else{
			boolean swapped = swapTerrain(block);
			iRenderInvBox(renderer, block, metadata);
			resetTerrain(swapped);
		}*/
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		boolean b = false;
		if(block instanceof PC_IMSG){
			Object o = ((PC_IMSG) block).msg(PC_Utils.MSG_RENDER_WORLD_BLOCK, world, new PC_VecI(x, y, z), block, modelId, renderer);
			if(o instanceof Boolean)
				b = (Boolean)o;
		}
		if(!b){
			iRenderBlockRotatedBox(world, x, y, z, block, modelId, renderer);
		}/*else
			iRenderBlock(world, x, y, z, block, modelId, renderer);*/
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return render3d;
	}

	@Override
	public int getRenderId() {
		if(render3d)
			return render3dId;
		return render2dId;
	}
	
	@Override
	protected void iTessellatorDraw(){
		Tessellator.instance.draw();
	};
	
	@Override
	protected void iTessellatorStartDrawingQuads(){
		Tessellator.instance.startDrawingQuads();
	};
	
	private RenderEngine getRenderEngine(){
		return PC_ClientUtils.mc().renderEngine;
	}
	
	@Override
	protected void iBindTexture(String texture){
		RenderEngine re = getRenderEngine();
		re.bindTexture(re.getTexture(texture));
	};
	
	@Override
	protected void iRenderStandardBlock(Object renderer, Block block, int x, int y, int z){
		((RenderBlocks)renderer).func_83018_a(block);
		((RenderBlocks)renderer).renderStandardBlock(block, x, y, z);
		((RenderBlocks)renderer).func_83017_b();
	};

	@Override
	protected void iRenderInvBox(Object renderer, Block block, int metadata){
		Tessellator tessellator = Tessellator.instance;
		RenderBlocks renderblocks = (RenderBlocks)renderer;
		
		int[] textures = new int[6];
		if (block instanceof PC_ISpecialInventoryTextures) {
			for (int a = 0; a < 6; a++) {
				textures[a] = ((PC_ISpecialInventoryTextures) block).getInvTexture(a, metadata);
			}
		} else {
			for (int a = 0; a < 6; a++) {
				textures[a] = block.getBlockTextureFromSideAndMetadata(a, metadata);
			}
		}
		
		block.setBlockBoundsForItemRender();
		((RenderBlocks)renderer).func_83018_a(block);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		renderblocks.renderBottomFace(block, 0.0D, 0.0D, 0.0D, textures[0]);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderblocks.renderTopFace(block, 0.0D, 0.0D, 0.0D, textures[1]);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		renderblocks.renderEastFace(block, 0.0D, 0.0D, 0.0D, textures[2]);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderblocks.renderWestFace(block, 0.0D, 0.0D, 0.0D, textures[3]);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		renderblocks.renderNorthFace(block, 0.0D, 0.0D, 0.0D, textures[4]);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderblocks.renderSouthFace(block, 0.0D, 0.0D, 0.0D, textures[5]);
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		((RenderBlocks)renderer).func_83017_b();
	}
	
	@Override
	protected void iRenderBlockRotatedBox(IBlockAccess world, int x, int y, int z, Block block, int modelId, Object renderer){
		Tessellator tessellator = Tessellator.instance;
		int metaAt = world.getBlockMetadata(x, y, z);

		if (block instanceof PC_IMSG) {

			tessellator.draw();
			tessellator.startDrawingQuads();

			block.setBlockBoundsBasedOnState(world, x, y, z);
			((RenderBlocks)renderer).func_83018_a(block);
			Object o=((PC_IMSG) block).msg(PC_Utils.MSG_ROTATION, metaAt);
			if(o instanceof Integer){
				boolean swapped = swapTerrain(block);
				int l = (Integer)o;
				((RenderBlocks)renderer).renderStandardBlock(block, x, y, z);
	
				tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
				tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
				tessellator.setNormal(0.0F, 1F, 0.0F);
				int k1 = block.getBlockTexture(world, x, y, z, 1);
				int l1 = (k1 & 0xf) << 4;
				int i2 = k1 & 0xf0;
				double d5 = l1 / 256F;
				double d6 = (l1 + 15.99F) / 256F;
				double d7 = i2 / 256F;
				double d8 = (i2 + 15.99F) / 256F;
				double d9 = (block.getBlockBoundsMaxY());
				double d10 = x + block.getBlockBoundsMaxX();
				double d11 = x + block.getBlockBoundsMaxX();
				double d12 = x + block.getBlockBoundsMinY();
				double d13 = x + block.getBlockBoundsMinY();
				double d14 = z + block.getBlockBoundsMinZ();
				double d15 = z + block.getBlockBoundsMaxZ();
				double d16 = z + block.getBlockBoundsMaxZ();
				double d17 = z + block.getBlockBoundsMinZ();
				double d18 = y + d9;
				if (l == 2) {
					d10 = d11 = x + block.getBlockBoundsMinY();
					d12 = d13 = x + block.getBlockBoundsMaxX();
					d14 = d17 = z + block.getBlockBoundsMaxZ();
					d15 = d16 = z + block.getBlockBoundsMinZ();
				} else if (l == 3) {
					d10 = d13 = x + block.getBlockBoundsMinY();
					d11 = d12 = x + block.getBlockBoundsMaxX();
					d14 = d15 = z + block.getBlockBoundsMinZ();
					d16 = d17 = z + block.getBlockBoundsMaxZ();
				} else if (l == 1) {
					d10 = d13 = x + block.getBlockBoundsMaxX();
					d11 = d12 = x + block.getBlockBoundsMinY();
					d14 = d15 = z + block.getBlockBoundsMaxZ();
					d16 = d17 = z + block.getBlockBoundsMinZ();
				}
	
				tessellator.addVertexWithUV(d13, d18, d17, d5, d7);
				tessellator.addVertexWithUV(d12, d18, d16, d5, d8);
				tessellator.addVertexWithUV(d11, d18, d15, d6, d8);
				tessellator.addVertexWithUV(d10, d18, d14, d6, d7);
	
				tessellator.draw();
				tessellator.startDrawingQuads();
				((RenderBlocks)renderer).func_83017_b();
				resetTerrain(swapped);
			}else{
				iRenderBlock(world, x, y, z, block, modelId, (RenderBlocks)renderer);
			}
		}

	}
	
	@Override
	protected void iRenderInvBlockRotatedBox(Block block, int metadata, int modelID, Object renderer){
		RenderBlocks renderblocks = (RenderBlocks)renderer;
		Tessellator tessellator = Tessellator.instance;
		
		if (block instanceof PC_IMSG) {

			boolean swapped = swapTerrain(block);

			Object o=((PC_IMSG) block).msg(PC_Utils.MSG_RENDER_ITEM_HORIZONTAL);
			if(o instanceof Boolean){
				boolean renderOnSide = (Boolean)o;
	
				if (renderOnSide) {
					block.setBlockBoundsForItemRender();
				} else {
					block.setBlockBounds(-0.1F, -0.1F, 0.4F, 1.1F, 1.1F, 0.6F);
				}
				
				((RenderBlocks)renderer).func_83018_a(block);
				GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
				tessellator.startDrawingQuads();
				tessellator.setNormal(0.0F, -1F, 0.0F);
				renderblocks.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(renderOnSide ? 0 : 0, metadata));
				tessellator.draw();
				tessellator.startDrawingQuads();
				tessellator.setNormal(0.0F, 1.0F, 0.0F);
				renderblocks.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(renderOnSide ? 1 : 0, metadata));
				tessellator.draw();
				tessellator.startDrawingQuads();
				tessellator.setNormal(0.0F, 0.0F, -1F);
				renderblocks.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(renderOnSide ? 2 : 0, metadata));
				tessellator.draw();
				tessellator.startDrawingQuads();
				tessellator.setNormal(0.0F, 0.0F, 1.0F);
				renderblocks.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(renderOnSide ? 3 : 1, metadata));
				tessellator.draw();
				tessellator.startDrawingQuads();
				tessellator.setNormal(-1F, 0.0F, 0.0F);
				renderblocks.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(renderOnSide ? 4 : 0, metadata));
				tessellator.draw();
				tessellator.startDrawingQuads();
				tessellator.setNormal(1.0F, 0.0F, 0.0F);
				renderblocks.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(renderOnSide ? 5 : 0, metadata));
				tessellator.draw();
				GL11.glTranslatef(0.5F, 0.5F, 0.5F);
				((RenderBlocks)renderer).func_83017_b();
			}else{
				iRenderInvBox(renderer, block, metadata);
			}
			resetTerrain(swapped);
			return;
		}
	}
	
	protected void iRenderBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.draw();
		boolean swapped = swapTerrain(block);
		tessellator.startDrawingQuads();
		block.setBlockBoundsBasedOnState(world, x, y, z);
		iRenderStandardBlock(renderer, block, x, y, z);
		tessellator.draw();
		tessellator.startDrawingQuads();
		resetTerrain(swapped);
	}
	
	protected void iRenderInvBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		RenderBlocks renderblocks = (RenderBlocks)renderer;
		Tessellator tessellator = Tessellator.instance;

		boolean swapped = swapTerrain(block);
		
		block.setBlockBoundsForItemRender();
		((RenderBlocks)renderer).func_83018_a(block);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		renderblocks.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(0, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderblocks.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(1, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		renderblocks.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(2, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderblocks.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(3, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		renderblocks.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(4, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderblocks.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(5, metadata));
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		((RenderBlocks)renderer).func_83017_b();
		resetTerrain(swapped);

	}
	
	@Override
	protected void iRenderInvBoxWithTexture(Object renderer, Block block, int tectureID){
		RenderBlocks renderblocks = (RenderBlocks)renderer;
		Tessellator tessellator = Tessellator.instance;
		((RenderBlocks)renderer).func_83018_a(block);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		renderblocks.renderBottomFace(block, 0.0D, 0.0D, 0.0D, tectureID);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderblocks.renderTopFace(block, 0.0D, 0.0D, 0.0D, tectureID);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		renderblocks.renderEastFace(block, 0.0D, 0.0D, 0.0D, tectureID);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderblocks.renderWestFace(block, 0.0D, 0.0D, 0.0D, tectureID);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		renderblocks.renderNorthFace(block, 0.0D, 0.0D, 0.0D, tectureID);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderblocks.renderSouthFace(block, 0.0D, 0.0D, 0.0D, tectureID);
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		((RenderBlocks)renderer).func_83017_b();
	}
	
	/**
	 * Use texture file as terrain.png
	 * 
	 * @param filename name of the used texture file (png)
	 */
	@Override
	protected void iSwapTerrain(String filename) {
		RenderEngine renderengine = PC_ClientUtils.mc().renderEngine;
		renderengine.bindTexture(renderengine.getTexture(filename));
	}

	/**
	 * If block implements ISwapTerrain, set used terrain texture to the one
	 * from this block
	 * 
	 * @param block the block to render
	 * @return true if terrain was swapped -> call resetTerrain() to re-enable
	 *         original terrain.png
	 */
	@Override
	protected boolean iSwapTerrain(Block block) {
		if (block instanceof PC_Block && !block.getTextureFile().equalsIgnoreCase("/terrain.png")) {
			swapTerrain(block.getTextureFile());
			return true;
		}
		return false;
	}

	/**
	 * Reset swapped terrain - set the original terrain file back.
	 * 
	 * @param do_it false = do nothing
	 */
	@Override
	protected void iResetTerrain(boolean do_it) {
		if(do_it){
			RenderEngine renderengine = PC_ClientUtils.mc().renderEngine;
			renderengine.bindTexture(renderengine.getTexture("/terrain.png"));
		}
	}
	
	@Override
	protected void iglColor4f(float r, float g, float b, float a) {
		GL11.glColor4f(r, g, b, a);
	}
	
	@Override
	protected void iglPushMatrix() {
		GL11.glPushMatrix();
	}

	@Override
	protected void iglPopMatrix() {
		GL11.glPopMatrix();
	}

	@Override
	protected void iglTranslatef(float x, float y, float z) {
		GL11.glTranslatef(x, y, z);
	}
	
	@Override
	protected void iglRotatef(float angel, float x, float y, float z) {
		GL11.glRotatef(angel, x, y, z);
	}

	@Override
	protected void iglScalef(float x, float y, float z) {
		GL11.glScalef(x, y, z);
	}

	@Override
	protected void iglEnable(int i) {
		GL11.glEnable(i);
	}

	@Override
	protected void iglDisable(int i) {
		GL11.glDisable(i);
	}
		
	@Override
	protected void iglBlendFunc(int i, int j) {
		GL11.glBlendFunc(i, j);
	}
	
}
