package net.minecraft.crash;

import java.util.concurrent.Callable;
import net.minecraft.util.AxisAlignedBB;

class CallableCrashMemoryReport implements Callable
{
    final CrashReport theCrashReport;

    CallableCrashMemoryReport(CrashReport par1CrashReport)
    {
        this.theCrashReport = par1CrashReport;
    }

    public String func_83003_a()
    {
        int var1 = AxisAlignedBB.getAABBPool().getlistAABBsize();
        int var2 = 56 * var1;
        int var3 = var2 / 1024 / 1024;
        int var4 = AxisAlignedBB.getAABBPool().getnextPoolIndex();
        int var5 = 56 * var4;
        int var6 = var5 / 1024 / 1024;
        return var1 + " (" + var2 + " bytes; " + var3 + " MB) allocated, " + var4 + " (" + var5 + " bytes; " + var6 + " MB) used";
    }

    public Object call()
    {
        return this.func_83003_a();
    }
}
