package powercraft.api.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import powercraft.api.block.PC_Block;
import powercraft.api.interfaces.PC_IMSG;
import powercraft.api.inventory.PC_ISpecialInventoryTextures;
import powercraft.api.registry.PC_MSGRegistry;
import powercraft.api.utils.PC_ClientUtils;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecF;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class PC_ClientRenderer extends PC_Renderer implements ISimpleBlockRenderingHandler {
	
	public PC_ClientRenderer(boolean render3d){
		super(render3d);
		if(render3d)
			render3dId = RenderingRegistry.getNextAvailableRenderId();
		else
			render2dId = RenderingRegistry.getNextAvailableRenderId();
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		if (block instanceof PC_Block) {
			PC_Block pcBlock = (PC_Block) block;
			if (!pcBlock.renderInventoryBlock(metadata, renderer)) {
				iRenderInvBlockRotatedBox(block, metadata, modelID, renderer);
			}
		}
	}
	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if (block instanceof PC_Block) {
			PC_Block pcBlock = (PC_Block) block;
			if (!pcBlock.renderWorldBlock(world, x, y, z, renderer)) {
				iRenderBlockRotatedBox(world, x, y, z, block, modelId, renderer);
			}
			return true;
		}
		return false;
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
	}
	
	@Override
	protected void iTessellatorStartDrawingQuads(){
		Tessellator.instance.startDrawingQuads();
	}
	
	@Override
	protected void iTessellatorStartDrawing(int i) {
		Tessellator.instance.startDrawing(i);
	}
    
	@Override
    protected void iTessellatorSetColorOpaque_I(int i) {
		Tessellator.instance.setColorOpaque_I(i);
	}
	
	@Override
	protected void iTessellatorSetColor(int r, int g, int b, int a) {
		Tessellator.instance.setColorRGBA(r, g, b, a);
	}
	
	@Override
	protected void iTessellatorAddVertex(double x, double y, double z) {
		Tessellator.instance.addVertex(x, y, z);
	}
	
	private RenderEngine getRenderEngine(){
		return PC_ClientUtils.mc().renderEngine;
	}
	
	@Override
	protected void iBindTexture(String texture){
		RenderEngine re = getRenderEngine();
		re.bindTexture(texture);
	}
	
	@Override
	protected void iRenderStandardBlock(Object renderer, Block block, int x, int y, int z){
		((RenderBlocks)renderer).setRenderBoundsFromBlock(block);
		((RenderBlocks)renderer).renderStandardBlock(block, x, y, z);
		((RenderBlocks)renderer).unlockBlockBounds();
	}
	
	@Override
	protected void iRenderBlockAllFaces(Object renderer, Block block, int x, int y, int z){
		((RenderBlocks)renderer).setRenderBoundsFromBlock(block);
		((RenderBlocks)renderer).renderAllFaces = true;
		((RenderBlocks)renderer).renderStandardBlock(block, x, y, z);
		((RenderBlocks)renderer).renderAllFaces = false;
		((RenderBlocks)renderer).unlockBlockBounds();
	}

	@Override
	protected void iRenderInvBox(Object renderer, Block block, int metadata){
		Tessellator tessellator = Tessellator.instance;
		RenderBlocks renderblocks = (RenderBlocks)renderer;
		
		Icon[] textures = new Icon[6];
		if (block instanceof PC_ISpecialInventoryTextures) {
			for (int a = 0; a < 6; a++) {
				textures[a] = ((PC_ISpecialInventoryTextures) block).getInvTexture(a, metadata);
			}
		} else {
			for (int a = 0; a < 6; a++) {
				textures[a] = block.getIcon(a, metadata);
			}
		}
		
		block.setBlockBoundsForItemRender();
		((RenderBlocks)renderer).setRenderBoundsFromBlock(block);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		if(textures[0]!=null){
			tessellator.setNormal(0.0F, -1F, 0.0F);
			renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, textures[0]);
		}
		if(textures[1]!=null){
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, textures[1]);
		}
		if(textures[2]!=null){
			tessellator.setNormal(0.0F, 0.0F, -1F);
			renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, textures[2]);
		}
		if(textures[3]!=null){
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, textures[3]);
		}
		if(textures[4]!=null){
			tessellator.setNormal(-1F, 0.0F, 0.0F);
			renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, textures[4]);
		}
		if(textures[5]!=null){
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, textures[5]);
		}
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		((RenderBlocks)renderer).unlockBlockBounds();
	}
	
	@Override
	protected void iRenderBlockRotatedBox(IBlockAccess world, int x, int y, int z, Block block, int modelId, Object renderer){
		RenderBlocks renderblocks = (RenderBlocks)renderer;
		if(block instanceof PC_Block){
			block.setBlockBoundsBasedOnState(world, x, y, z);
			renderblocks.setRenderBoundsFromBlock(block);
			renderblocks.uvRotateTop = ((PC_Block)block).getRotation2(PC_Utils.getMD(world, x, y, z)).getMCSide();
			renderblocks.renderStandardBlock(block, x, y, z);
			renderblocks.uvRotateTop = 0;
		}else{
			iRenderBlock(world, x, y, z, block, modelId, renderblocks);
		}

	}
	
	@Override
	protected void iRenderInvBlockRotatedBox(Block block, int metadata, int modelID, Object renderer){

		iRenderInvBox(renderer, block, metadata);

	}
	
	protected void iRenderBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.draw();
		tessellator.startDrawingQuads();
		block.setBlockBoundsBasedOnState(world, x, y, z);
		iRenderStandardBlock(renderer, block, x, y, z);
		tessellator.draw();
		tessellator.startDrawingQuads();
	}
	
	protected void iRenderInvBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		RenderBlocks renderblocks = (RenderBlocks)renderer;
		Tessellator tessellator = Tessellator.instance;
		
		block.setBlockBoundsForItemRender();
		((RenderBlocks)renderer).setRenderBoundsFromBlock(block);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, metadata));
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		((RenderBlocks)renderer).unlockBlockBounds();

	}
	
	@Override
	protected void iRenderInvBoxWithTexture(Object renderer, Block block, Icon icon){
		RenderBlocks renderblocks = (RenderBlocks)renderer;
		Tessellator tessellator = Tessellator.instance;
		((RenderBlocks)renderer).setRenderBoundsFromBlock(block);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		((RenderBlocks)renderer).unlockBlockBounds();
	}
	
	@Override
	protected void iRenderInvBoxWithTextures(Object renderer, Block block, Icon[] icon) {
		RenderBlocks renderblocks = (RenderBlocks)renderer;
		Tessellator tessellator = Tessellator.instance;
		((RenderBlocks)renderer).setRenderBoundsFromBlock(block);
		
		block.setBlockBoundsForItemRender();
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		if(icon[0]!=null){
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, -1F, 0.0F);
			renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, icon[0]);
			tessellator.draw();
		}
		if(icon[1]!=null){
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, icon[1]);
			tessellator.draw();
		}
		if(icon[2]!=null){
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, -1F);
			renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, icon[2]);
			tessellator.draw();
		}
		if(icon[3]!=null){
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, icon[3]);
			tessellator.draw();
		}
		if(icon[4]!=null){
			tessellator.startDrawingQuads();
			tessellator.setNormal(-1F, 0.0F, 0.0F);
			renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, icon[4]);
			tessellator.draw();
		}
		if(icon[5]!=null){
			tessellator.startDrawingQuads();
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, icon[5]);
			tessellator.draw();
		}
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		
		((RenderBlocks)renderer).unlockBlockBounds();
	}
	
	@Override
	protected void iRenderBlockByRenderType(Object renderer, Block block, int x, int y, int z) {
		RenderBlocks renderblocks = (RenderBlocks)renderer;
		renderblocks.renderBlockByRenderType(block, x, y, z);
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
	
	@Override
	protected void iglNormal3f(float x, float y, float z) {
		GL11.glNormal3f(x, y, z);
	}

	@Override
	protected void iglDepthMask(boolean state) {
		GL11.glDepthMask(state);
	}
	
	@Override
	protected void irenderEntityLabelAt(String label, PC_VecF realPos, int viewDistance, float yOffset, double x, double y, double z) {
		RenderManager renderManager = RenderManager.instance;
		label = label.trim();

		if(renderManager.livingPlayer==null)
			return;
		float f = (float) renderManager.livingPlayer.getDistance(realPos.x + 0.5D, realPos.y + 0.5D, realPos.z + 0.5D);

		if (f > viewDistance) {
			return;
		}

		FontRenderer fontrenderer = PC_ClientUtils.mc().fontRenderer;
		float f1 = 1.0F; // 1.6F;
		float f2 = 0.01666667F * f1;
		GL11.glPushMatrix();
		GL11.glTranslatef(0, yOffset - 0.5F, 0);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-f2, -f2, f2);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);
		// GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Tessellator tessellator = Tessellator.instance;
		byte byte0 = 0;

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		tessellator.startDrawingQuads();
		float i = (fontrenderer.getStringWidth(label) / 2) * 1.12F;
		tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.5F);
		tessellator.addVertex(-i - 1, -1 + byte0, 0.0D);
		tessellator.addVertex(-i - 1, 8 + byte0, 0.0D);
		tessellator.addVertex(i + 1, 8 + byte0, 0.0D);
		tessellator.addVertex(i + 1, -1 + byte0, 0.0D);
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		fontrenderer.drawString(label, -fontrenderer.getStringWidth(label) / 2, byte0, 0xffffffff);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}
	
	@Override
	protected FontRenderer igetFontRenderer() {
		return PC_ClientUtils.mc().fontRenderer;
	}
	
}
