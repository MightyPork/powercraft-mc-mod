package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class CallableType2 implements Callable
{
    final Minecraft mc;

    public CallableType2(Minecraft par1Minecraft)
    {
        this.mc = par1Minecraft;
    }

    public String func_82886_a()
    {
        return "Client (map_client.txt)";
    }

    public Object call()
    {
        return this.func_82886_a();
    }
}
