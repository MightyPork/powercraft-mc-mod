package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

public class CrashReportCategory
{
    private final CrashReport field_85078_a;
    private final String field_85076_b;
    private final List field_85077_c = new ArrayList();
    private StackTraceElement[] field_85075_d = new StackTraceElement[0];

    public CrashReportCategory(CrashReport par1CrashReport, String par2Str)
    {
        this.field_85078_a = par1CrashReport;
        this.field_85076_b = par2Str;
    }

    @SideOnly(Side.CLIENT)
    public static String func_85074_a(double par0, double par2, double par4)
    {
        return String.format("%.2f,%.2f,%.2f - %s", new Object[] {Double.valueOf(par0), Double.valueOf(par2), Double.valueOf(par4), func_85071_a(MathHelper.floor_double(par0), MathHelper.floor_double(par2), MathHelper.floor_double(par4))});
    }

    public static String func_85071_a(int par0, int par1, int par2)
    {
        StringBuilder var3 = new StringBuilder();

        try
        {
            var3.append(String.format("World: (%d,%d,%d)", new Object[] {Integer.valueOf(par0), Integer.valueOf(par1), Integer.valueOf(par2)}));
        }
        catch (Throwable var16)
        {
            var3.append("(Error finding world loc)");
        }

        var3.append(", ");
        int var4;
        int var5;
        int var6;
        int var7;
        int var8;
        int var9;
        int var10;
        int var11;
        int var12;

        try
        {
            var4 = par0 >> 4;
            var5 = par2 >> 4;
            var6 = par0 & 15;
            var7 = par1 >> 4;
            var8 = par2 & 15;
            var9 = var4 << 4;
            var10 = var5 << 4;
            var11 = (var4 + 1 << 4) - 1;
            var12 = (var5 + 1 << 4) - 1;
            var3.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", new Object[] {Integer.valueOf(var6), Integer.valueOf(var7), Integer.valueOf(var8), Integer.valueOf(var4), Integer.valueOf(var5), Integer.valueOf(var9), Integer.valueOf(var10), Integer.valueOf(var11), Integer.valueOf(var12)}));
        }
        catch (Throwable var15)
        {
            var3.append("(Error finding chunk loc)");
        }

        var3.append(", ");

        try
        {
            var4 = par0 >> 9;
            var5 = par2 >> 9;
            var6 = var4 << 5;
            var7 = var5 << 5;
            var8 = (var4 + 1 << 5) - 1;
            var9 = (var5 + 1 << 5) - 1;
            var10 = var4 << 9;
            var11 = var5 << 9;
            var12 = (var4 + 1 << 9) - 1;
            int var13 = (var5 + 1 << 9) - 1;
            var3.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", new Object[] {Integer.valueOf(var4), Integer.valueOf(var5), Integer.valueOf(var6), Integer.valueOf(var7), Integer.valueOf(var8), Integer.valueOf(var9), Integer.valueOf(var10), Integer.valueOf(var11), Integer.valueOf(var12), Integer.valueOf(var13)}));
        }
        catch (Throwable var14)
        {
            var3.append("(Error finding world loc)");
        }

        return var3.toString();
    }

    public void addCrashSectionCallable(String par1Str, Callable par2Callable)
    {
        try
        {
            this.addCrashSection(par1Str, par2Callable.call());
        }
        catch (Throwable var4)
        {
            this.addCrashSectionThrowable(par1Str, var4);
        }
    }

    public void addCrashSection(String par1Str, Object par2Obj)
    {
        this.field_85077_c.add(new CrashReportCategoryEntry(par1Str, par2Obj));
    }

    public void addCrashSectionThrowable(String par1Str, Throwable par2Throwable)
    {
        this.addCrashSection(par1Str, par2Throwable);
    }

    public int func_85073_a(int par1)
    {
        StackTraceElement[] var2 = Thread.currentThread().getStackTrace();
        this.field_85075_d = new StackTraceElement[var2.length - 3 - par1];
        System.arraycopy(var2, 3 + par1, this.field_85075_d, 0, this.field_85075_d.length);
        return this.field_85075_d.length;
    }

    public boolean func_85069_a(StackTraceElement par1StackTraceElement, StackTraceElement par2StackTraceElement)
    {
        if (this.field_85075_d.length != 0 && par1StackTraceElement != null)
        {
            StackTraceElement var3 = this.field_85075_d[0];

            if (var3.isNativeMethod() == par1StackTraceElement.isNativeMethod() && var3.getClassName().equals(par1StackTraceElement.getClassName()) && var3.getFileName().equals(par1StackTraceElement.getFileName()) && var3.getMethodName().equals(par1StackTraceElement.getMethodName()))
            {
                if (par2StackTraceElement != null != this.field_85075_d.length > 1)
                {
                    return false;
                }
                else if (par2StackTraceElement != null && !this.field_85075_d[1].equals(par2StackTraceElement))
                {
                    return false;
                }
                else
                {
                    this.field_85075_d[0] = par1StackTraceElement;
                    return true;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public void func_85070_b(int par1)
    {
        StackTraceElement[] var2 = new StackTraceElement[this.field_85075_d.length - par1];
        System.arraycopy(this.field_85075_d, 0, var2, 0, var2.length);
        this.field_85075_d = var2;
    }

    public void func_85072_a(StringBuilder par1StringBuilder)
    {
        par1StringBuilder.append("-- ").append(this.field_85076_b).append(" --\n");
        par1StringBuilder.append("Details:");
        Iterator var2 = this.field_85077_c.iterator();

        while (var2.hasNext())
        {
            CrashReportCategoryEntry var3 = (CrashReportCategoryEntry)var2.next();
            par1StringBuilder.append("\n\t");
            par1StringBuilder.append(var3.func_85089_a());
            par1StringBuilder.append(": ");
            par1StringBuilder.append(var3.func_85090_b());
        }

        if (this.field_85075_d != null && this.field_85075_d.length > 0)
        {
            par1StringBuilder.append("\nStacktrace:");
            StackTraceElement[] var6 = this.field_85075_d;
            int var7 = var6.length;

            for (int var4 = 0; var4 < var7; ++var4)
            {
                StackTraceElement var5 = var6[var4];
                par1StringBuilder.append("\n\tat ");
                par1StringBuilder.append(var5.toString());
            }
        }
    }

    public static void func_85068_a(CrashReportCategory par0CrashReportCategory, int par1, int par2, int par3, int par4, int par5)
    {
        par0CrashReportCategory.addCrashSectionCallable("Block type", new CallableBlockType(par4));
        par0CrashReportCategory.addCrashSectionCallable("Block data value", new CallableBlockDataValue(par5));
        par0CrashReportCategory.addCrashSectionCallable("Block location", new CallableBlockLocation(par1, par2, par3));
    }
}
