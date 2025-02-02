package powercraft.light;

import net.minecraft.src.NBTTagCompound;
import powercraft.management.PC_Color;
import powercraft.management.PC_ITileEntityRenderer;
import powercraft.management.PC_Renderer;
import powercraft.management.PC_TileEntity;
import powercraft.management.PC_Utils.ModuleInfo;

public class PCli_TileEntityMirrow extends PC_TileEntity implements PC_ITileEntityRenderer {

	private int mirrorColor = -1;
	private static PCli_ModelMirror modelMirror = new PCli_ModelMirror();
	
	public void setMirrorColor(int mirrorColor) {
		this.mirrorColor = mirrorColor;
	}

	public int getMirrorColor() {
		return mirrorColor;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("optMirrorColor", 1 + mirrorColor);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		mirrorColor = tag.getInteger("optMirrorColor") - 1;
	}
	
	@Override
	public void renderTileEntityAt(double x, double y, double z, float rot) {

		modelMirror.bottomSticks.showModel = false;
		modelMirror.ceilingSticks.showModel = false;
		modelMirror.stickXplus.showModel = false;
		modelMirror.stickXminus.showModel = false;
		modelMirror.stickZplus.showModel = false;
		modelMirror.stickZminus.showModel = false;
		modelMirror.stickZminus.showModel = false;

		modelMirror.signBoard.showModel = true;

		int i, j, k;

		i = xCoord;
		j = yCoord;
		k = zCoord;

		if (worldObj.getBlockMaterial(i, j - 1, k).isSolid()) {
			modelMirror.bottomSticks.showModel = true;
		} else if (worldObj.getBlockMaterial(i, j + 1, k).isSolid()) {
			modelMirror.ceilingSticks.showModel = true;
		} else if (worldObj.getBlockMaterial(i + 1, j, k).isSolid()) {
			modelMirror.stickXplus.showModel = true;
		} else if (worldObj.getBlockMaterial(i - 1, j, k).isSolid()) {
			modelMirror.stickXminus.showModel = true;
		} else if (worldObj.getBlockMaterial(i, j, k + 1).isSolid()) {
			modelMirror.stickZplus.showModel = true;
		} else if (worldObj.getBlockMaterial(i, j, k - 1).isSolid()) {
			modelMirror.stickZminus.showModel = true;
		}

		PC_Renderer.glPushMatrix();
		float f = 0.6666667F;

		PC_Renderer.glTranslatef((float) x + 0.5F, (float) y + 0.5F /* *f0 */, (float) z + 0.5F);
		float f1 = (getBlockMetadata() * 360) / 16F;

		PC_Renderer.bindTexture(ModuleInfo.getTextureDirectory(ModuleInfo.getModule("Light")) + "mirror.png");

		PC_Renderer.glPushMatrix();
		PC_Renderer.glRotatef(-f1, 0.0F, 1.0F, 0.0F);
		PC_Renderer.glScalef(f, -f, -f);

		int color = getMirrorColor();

		if (color != -1) {

			float red = (float)PC_Color.red(PC_Color.crystal_colors[color]);
			float green = (float)PC_Color.green(PC_Color.crystal_colors[color]);
			float blue = (float)PC_Color.blue(PC_Color.crystal_colors[color]);

			PC_Renderer.glColor4f(red, green, blue, 0.5f);

		}

		modelMirror.renderMirrorNoSideSticks();
		PC_Renderer.glPopMatrix();

		PC_Renderer.glPushMatrix();
		PC_Renderer.glScalef(f, -f, -f);
		modelMirror.renderMirrorSideSticks();
		PC_Renderer.glPopMatrix();

		PC_Renderer.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		PC_Renderer.glPopMatrix();
	}
	
	@Override
	public void setData(Object[] o) {
		int p = 0;
		while(p<o.length){
			String var = (String)o[p++];
			if(var.equals("mirrorColor"))
				mirrorColor = (Integer)o[p++];
		}
	}

	@Override
	public Object[] getData() {
		return new Object[]{
				"mirrorColor",
				mirrorColor
		};
	}
	
}
