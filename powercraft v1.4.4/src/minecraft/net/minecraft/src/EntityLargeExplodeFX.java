package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class EntityLargeExplodeFX extends EntityFX
{
    private int field_70581_a = 0;
    private int field_70584_aq = 0;

    /** The Rendering Engine. */
    private RenderEngine theRenderEngine;
    private float field_70582_as;

    public EntityLargeExplodeFX(RenderEngine par1RenderEngine, World par2World, double par3, double par5, double par7, double par9, double par11, double par13)
    {
        super(par2World, par3, par5, par7, 0.0D, 0.0D, 0.0D);
        this.theRenderEngine = par1RenderEngine;
        this.field_70584_aq = 6 + this.rand.nextInt(4);
        this.particleRed = this.particleGreen = this.particleBlue = this.rand.nextFloat() * 0.6F + 0.4F;
        this.field_70582_as = 1.0F - (float)par9 * 0.5F;
    }

    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        int var8 = (int)(((float)this.field_70581_a + par2) * 15.0F / (float)this.field_70584_aq);

        if (var8 <= 15)
        {
            this.theRenderEngine.bindTexture(this.theRenderEngine.getTexture("/misc/explosion.png"));
            float var9 = (float)(var8 % 4) / 4.0F;
            float var10 = var9 + 0.24975F;
            float var11 = (float)(var8 / 4) / 4.0F;
            float var12 = var11 + 0.24975F;
            float var13 = 2.0F * this.field_70582_as;
            float var14 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)par2 - interpPosX);
            float var15 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)par2 - interpPosY);
            float var16 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)par2 - interpPosZ);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_LIGHTING);
            RenderHelper.disableStandardItemLighting();
            par1Tessellator.startDrawingQuads();
            par1Tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, 1.0F);
            par1Tessellator.setNormal(0.0F, 1.0F, 0.0F);
            par1Tessellator.setBrightness(240);
            par1Tessellator.addVertexWithUV((double)(var14 - par3 * var13 - par6 * var13), (double)(var15 - par4 * var13), (double)(var16 - par5 * var13 - par7 * var13), (double)var10, (double)var12);
            par1Tessellator.addVertexWithUV((double)(var14 - par3 * var13 + par6 * var13), (double)(var15 + par4 * var13), (double)(var16 - par5 * var13 + par7 * var13), (double)var10, (double)var11);
            par1Tessellator.addVertexWithUV((double)(var14 + par3 * var13 + par6 * var13), (double)(var15 + par4 * var13), (double)(var16 + par5 * var13 + par7 * var13), (double)var9, (double)var11);
            par1Tessellator.addVertexWithUV((double)(var14 + par3 * var13 - par6 * var13), (double)(var15 - par4 * var13), (double)(var16 + par5 * var13 - par7 * var13), (double)var9, (double)var12);
            par1Tessellator.draw();
            GL11.glPolygonOffset(0.0F, 0.0F);
            GL11.glEnable(GL11.GL_LIGHTING);
        }
    }

    public int getBrightnessForRender(float par1)
    {
        return 61680;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        ++this.field_70581_a;

        if (this.field_70581_a == this.field_70584_aq)
        {
            this.setDead();
        }
    }

    public int getFXLayer()
    {
        return 3;
    }
}
