package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntitySquid;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderSquid extends RenderLiving
{
    public RenderSquid(ModelBase par1ModelBase, float par2)
    {
        super(par1ModelBase, par2);
    }

    /**
     * Renders the Living Squid.
     */
    public void renderLivingSquid(EntitySquid par1EntitySquid, double par2, double par4, double par6, float par8, float par9)
    {
        super.doRenderLiving(par1EntitySquid, par2, par4, par6, par8, par9);
    }

    /**
     * Rotates the Squid's corpse.
     */
    protected void rotateSquidsCorpse(EntitySquid par1EntitySquid, float par2, float par3, float par4)
    {
        float var5 = par1EntitySquid.field_70862_e + (par1EntitySquid.field_70861_d - par1EntitySquid.field_70862_e) * par4;
        float var6 = par1EntitySquid.field_70860_g + (par1EntitySquid.field_70859_f - par1EntitySquid.field_70860_g) * par4;
        GL11.glTranslatef(0.0F, 0.5F, 0.0F);
        GL11.glRotatef(180.0F - par3, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(var5, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(var6, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, -1.2F, 0.0F);
    }

    protected float handleRotationFloat(EntitySquid par1EntitySquid, float par2)
    {
        return par1EntitySquid.lastTentacleAngle + (par1EntitySquid.tentacleAngle - par1EntitySquid.lastTentacleAngle) * par2;
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     */
    protected float handleRotationFloat(EntityLiving par1EntityLiving, float par2)
    {
        return this.handleRotationFloat((EntitySquid)par1EntityLiving, par2);
    }

    protected void rotateCorpse(EntityLiving par1EntityLiving, float par2, float par3, float par4)
    {
        this.rotateSquidsCorpse((EntitySquid)par1EntityLiving, par2, par3, par4);
    }

    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderLivingSquid((EntitySquid)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderLivingSquid((EntitySquid)par1Entity, par2, par4, par6, par8, par9);
    }
}
