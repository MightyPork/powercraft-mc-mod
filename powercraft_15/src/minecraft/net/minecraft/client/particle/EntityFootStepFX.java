package net.minecraft.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class EntityFootStepFX extends EntityFX
{
    private int field_70576_a = 0;
    private int field_70578_aq = 0;
    private RenderEngine currentFootSteps;

    public EntityFootStepFX(RenderEngine par1RenderEngine, World par2World, double par3, double par5, double par7)
    {
        super(par2World, par3, par5, par7, 0.0D, 0.0D, 0.0D);
        this.currentFootSteps = par1RenderEngine;
        this.motionX = this.motionY = this.motionZ = 0.0D;
        this.field_70578_aq = 200;
    }

    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        float f6 = ((float)this.field_70576_a + par2) / (float)this.field_70578_aq;
        f6 *= f6;
        float f7 = 2.0F - f6 * 2.0F;

        if (f7 > 1.0F)
        {
            f7 = 1.0F;
        }

        f7 *= 0.2F;
        GL11.glDisable(GL11.GL_LIGHTING);
        float f8 = 0.125F;
        float f9 = (float)(this.posX - interpPosX);
        float f10 = (float)(this.posY - interpPosY);
        float f11 = (float)(this.posZ - interpPosZ);
        float f12 = this.worldObj.getLightBrightness(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
        this.currentFootSteps.bindTexture("/misc/footprint.png");
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        par1Tessellator.startDrawingQuads();
        par1Tessellator.setColorRGBA_F(f12, f12, f12, f7);
        par1Tessellator.addVertexWithUV((double)(f9 - f8), (double)f10, (double)(f11 + f8), 0.0D, 1.0D);
        par1Tessellator.addVertexWithUV((double)(f9 + f8), (double)f10, (double)(f11 + f8), 1.0D, 1.0D);
        par1Tessellator.addVertexWithUV((double)(f9 + f8), (double)f10, (double)(f11 - f8), 1.0D, 0.0D);
        par1Tessellator.addVertexWithUV((double)(f9 - f8), (double)f10, (double)(f11 - f8), 0.0D, 0.0D);
        par1Tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        ++this.field_70576_a;

        if (this.field_70576_a == this.field_70578_aq)
        {
            this.setDead();
        }
    }

    public int getFXLayer()
    {
        return 3;
    }
}
