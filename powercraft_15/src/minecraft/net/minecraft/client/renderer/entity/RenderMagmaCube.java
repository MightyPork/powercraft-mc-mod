package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMagmaCube;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderMagmaCube extends RenderLiving
{
    private int field_77120_a;

    public RenderMagmaCube()
    {
        super(new ModelMagmaCube(), 0.25F);
        this.field_77120_a = ((ModelMagmaCube)this.mainModel).func_78107_a();
    }

    public void renderMagmaCube(EntityMagmaCube par1EntityMagmaCube, double par2, double par4, double par6, float par8, float par9)
    {
        int i = ((ModelMagmaCube)this.mainModel).func_78107_a();

        if (i != this.field_77120_a)
        {
            this.field_77120_a = i;
            this.mainModel = new ModelMagmaCube();
            Minecraft.getMinecraft().getLogAgent().logInfo("Loaded new lava slime model");
        }

        super.doRenderLiving(par1EntityMagmaCube, par2, par4, par6, par8, par9);
    }

    protected void scaleMagmaCube(EntityMagmaCube par1EntityMagmaCube, float par2)
    {
        int i = par1EntityMagmaCube.getSlimeSize();
        float f1 = (par1EntityMagmaCube.field_70812_c + (par1EntityMagmaCube.field_70811_b - par1EntityMagmaCube.field_70812_c) * par2) / ((float)i * 0.5F + 1.0F);
        float f2 = 1.0F / (f1 + 1.0F);
        float f3 = (float)i;
        GL11.glScalef(f2 * f3, 1.0F / f2 * f3, f2 * f3);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityLiving par1EntityLiving, float par2)
    {
        this.scaleMagmaCube((EntityMagmaCube)par1EntityLiving, par2);
    }

    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderMagmaCube((EntityMagmaCube)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderMagmaCube((EntityMagmaCube)par1Entity, par2, par4, par6, par8, par9);
    }
}
