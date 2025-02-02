package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SideOnly(Side.SERVER)
final class ServerWindowAdapter extends WindowAdapter
{
    final DedicatedServer mc;

    ServerWindowAdapter(DedicatedServer par1DedicatedServer)
    {
        this.mc = par1DedicatedServer;
    }

    public void windowClosing(WindowEvent par1WindowEvent)
    {
        this.mc.initiateShutdown();

        while (!this.mc.isServerStopped())
        {
            try
            {
                Thread.sleep(100L);
            }
            catch (InterruptedException var3)
            {
                var3.printStackTrace();
            }
        }

        System.exit(0);
    }
}
