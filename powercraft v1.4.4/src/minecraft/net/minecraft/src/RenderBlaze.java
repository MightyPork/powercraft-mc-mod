package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlaze extends RenderLiving
{
    private int field_77068_a;

    public RenderBlaze()
    {
        super(new ModelBlaze(), 0.5F);
        this.field_77068_a = ((ModelBlaze)this.mainModel).func_78104_a();
    }

    public void renderBlaze(EntityBlaze par1EntityBlaze, double par2, double par4, double par6, float par8, float par9)
    {
        int var10 = ((ModelBlaze)this.mainModel).func_78104_a();

        if (var10 != this.field_77068_a)
        {
            this.field_77068_a = var10;
            this.mainModel = new ModelBlaze();
        }

        super.doRenderLiving(par1EntityBlaze, par2, par4, par6, par8, par9);
    }

    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderBlaze((EntityBlaze)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderBlaze((EntityBlaze)par1Entity, par2, par4, par6, par8, par9);
    }
}
