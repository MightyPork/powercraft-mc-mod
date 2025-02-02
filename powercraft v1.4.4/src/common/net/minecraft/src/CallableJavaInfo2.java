package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableJavaInfo2 implements Callable
{
    final CrashReport theCrashReport;

    CallableJavaInfo2(CrashReport par1CrashReport)
    {
        this.theCrashReport = par1CrashReport;
    }

    public String getJavaVMInfoAsString()
    {
        return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
    }

    public Object call()
    {
        return this.getJavaVMInfoAsString();
    }
}
