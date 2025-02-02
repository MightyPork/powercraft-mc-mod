package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.concurrent.Callable;

@SideOnly(Side.CLIENT)
class CallableScreenSize implements Callable
{
    final ScaledResolution field_90029_a;

    final EntityRenderer field_90028_b;

    CallableScreenSize(EntityRenderer par1EntityRenderer, ScaledResolution par2ScaledResolution)
    {
        this.field_90028_b = par1EntityRenderer;
        this.field_90029_a = par2ScaledResolution;
    }

    public String func_90027_a()
    {
        return String.format("Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d", new Object[] {Integer.valueOf(this.field_90029_a.getScaledWidth()), Integer.valueOf(this.field_90029_a.getScaledHeight()), Integer.valueOf(EntityRenderer.func_90030_a(this.field_90028_b).displayWidth), Integer.valueOf(EntityRenderer.func_90030_a(this.field_90028_b).displayHeight), Integer.valueOf(this.field_90029_a.getScaleFactor())});
    }

    public Object call()
    {
        return this.func_90027_a();
    }
}
