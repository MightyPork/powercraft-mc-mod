package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.Map;

@SideOnly(Side.CLIENT)
class ThreadStatSyncherSend extends Thread
{
    final Map field_77483_a;

    final StatsSyncher syncher;

    ThreadStatSyncherSend(StatsSyncher par1StatsSyncher, Map par2Map)
    {
        this.syncher = par1StatsSyncher;
        this.field_77483_a = par2Map;
    }

    public void run()
    {
        try
        {
            StatsSyncher.func_77414_a(this.syncher, this.field_77483_a, StatsSyncher.getUnsentDataFile(this.syncher), StatsSyncher.getUnsentTempFile(this.syncher), StatsSyncher.getUnsentOldFile(this.syncher));
        }
        catch (Exception var5)
        {
            var5.printStackTrace();
        }
        finally
        {
            StatsSyncher.setBusy(this.syncher, false);
        }
    }
}
