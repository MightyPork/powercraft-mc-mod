package net.minecraft.src;


/**
 * laser device model
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
public class PCma_ModelLaser extends ModelBase {

	/** Parts of the model */
	public ModelRenderer laserParts[];

	/**
	 * laser model
	 */
	public PCma_ModelLaser() {

		textureWidth = 64;
		textureHeight = 64;

		laserParts = new ModelRenderer[11];

		// legs
		laserParts[0] = new ModelRenderer(this, 0, 0);
		laserParts[0].addBox(-8F, 8F, -8F, 4, 4, 4, 0.0F);

		laserParts[1] = new ModelRenderer(this, 0, 0);
		laserParts[1].addBox(4F, 8F, -8F, 4, 4, 4, 0.0F);

		laserParts[2] = new ModelRenderer(this, 0, 0);
		laserParts[2].addBox(-8F, 8F, 4F, 4, 4, 4, 0.0F);

		laserParts[3] = new ModelRenderer(this, 0, 0);
		laserParts[3].addBox(4F, 8F, 4F, 4, 4, 4, 0.0F);

		// body
		laserParts[4] = new ModelRenderer(this, 0, 0);
		laserParts[4].addBox(-8F, 5F, -8F, 16, 3, 16, 0.0F);

		// neck
		laserParts[5] = new ModelRenderer(this, 40, 20);
		laserParts[5].addBox(-3F, 4F, -3F, 6, 2, 6, 0.0F);

		// head
		laserParts[6] = new ModelRenderer(this, 0, 20);
		laserParts[6].addBox(-6F, -4F, -4F, 12, 8, 8, 0.0F);

		// lens (coloured)
		laserParts[7] = new ModelRenderer(this, 0, 42);
		laserParts[7].addBox(6F, -2F, -2F, 1, 4, 4, 0.0F);
		laserParts[8] = new ModelRenderer(this, 10, 42);
		laserParts[8].addBox(6F, -2F, -2F, 1, 4, 4, 0.0F);
		laserParts[9] = new ModelRenderer(this, 20, 42);
		laserParts[9].addBox(6F, -2F, -2F, 1, 4, 4, 0.0F);
		laserParts[10] = new ModelRenderer(this, 30, 42);
		laserParts[10].addBox(6F, -2F, -2F, 1, 4, 4, 0.0F);

	}

	/**
	 * render the model
	 */
	public void renderLaser() {
		for (ModelRenderer laserPart : laserParts) {
			if (laserPart == null) {
				break;
			}

			laserPart.render(0.0625F);

		}
	}
}
