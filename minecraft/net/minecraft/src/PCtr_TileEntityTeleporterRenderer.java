package net.minecraft.src;


/**
 * Renderer for teleporter - the "label"
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
public class PCtr_TileEntityTeleporterRenderer extends TileEntitySpecialRenderer {


	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f0) {

		PCtr_TileEntityTeleporter tet = (PCtr_TileEntityTeleporter) tileEntity;

		if (!tet.hideLabel) {

			String foo = tet.isSender() ? "→" + tet.targetName : tet.identifierName;

			PC_Renderer.renderEntityLabelAt(foo, new PC_CoordF(tet.xCoord, tet.yCoord, tet.zCoord), 10, 1.3F, x, y, z);
		}
	}
}
