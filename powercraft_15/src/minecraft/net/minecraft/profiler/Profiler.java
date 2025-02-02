package net.minecraft.profiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Profiler
{
    /** List of parent sections */
    private final List sectionList = new ArrayList();

    /** List of timestamps (System.nanoTime) */
    private final List timestampList = new ArrayList();

    /** Flag profiling enabled */
    public boolean profilingEnabled = false;

    /** Current profiling section */
    private String profilingSection = "";

    /** Profiling map */
    private final Map profilingMap = new HashMap();

    /**
     * Clear profiling.
     */
    public void clearProfiling()
    {
        this.profilingMap.clear();
        this.profilingSection = "";
        this.sectionList.clear();
    }

    /**
     * Start section
     */
    public void startSection(String par1Str)
    {
        if (this.profilingEnabled)
        {
            if (this.profilingSection.length() > 0)
            {
                this.profilingSection = this.profilingSection + ".";
            }

            this.profilingSection = this.profilingSection + par1Str;
            this.sectionList.add(this.profilingSection);
            this.timestampList.add(Long.valueOf(System.nanoTime()));
        }
    }

    /**
     * End section
     */
    public void endSection()
    {
        if (this.profilingEnabled)
        {
            long i = System.nanoTime();
            long j = ((Long)this.timestampList.remove(this.timestampList.size() - 1)).longValue();
            this.sectionList.remove(this.sectionList.size() - 1);
            long k = i - j;

            if (this.profilingMap.containsKey(this.profilingSection))
            {
                this.profilingMap.put(this.profilingSection, Long.valueOf(((Long)this.profilingMap.get(this.profilingSection)).longValue() + k));
            }
            else
            {
                this.profilingMap.put(this.profilingSection, Long.valueOf(k));
            }

            if (k > 100000000L)
            {
                System.out.println("Something\'s taking too long! \'" + this.profilingSection + "\' took aprox " + (double)k / 1000000.0D + " ms");
            }

            this.profilingSection = !this.sectionList.isEmpty() ? (String)this.sectionList.get(this.sectionList.size() - 1) : "";
        }
    }

    /**
     * Get profiling data
     */
    public List getProfilingData(String par1Str)
    {
        if (!this.profilingEnabled)
        {
            return null;
        }
        else
        {
            long i = this.profilingMap.containsKey("root") ? ((Long)this.profilingMap.get("root")).longValue() : 0L;
            long j = this.profilingMap.containsKey(par1Str) ? ((Long)this.profilingMap.get(par1Str)).longValue() : -1L;
            ArrayList arraylist = new ArrayList();

            if (par1Str.length() > 0)
            {
                par1Str = par1Str + ".";
            }

            long k = 0L;
            Iterator iterator = this.profilingMap.keySet().iterator();

            while (iterator.hasNext())
            {
                String s1 = (String)iterator.next();

                if (s1.length() > par1Str.length() && s1.startsWith(par1Str) && s1.indexOf(".", par1Str.length() + 1) < 0)
                {
                    k += ((Long)this.profilingMap.get(s1)).longValue();
                }
            }

            float f = (float)k;

            if (k < j)
            {
                k = j;
            }

            if (i < k)
            {
                i = k;
            }

            Iterator iterator1 = this.profilingMap.keySet().iterator();
            String s2;

            while (iterator1.hasNext())
            {
                s2 = (String)iterator1.next();

                if (s2.length() > par1Str.length() && s2.startsWith(par1Str) && s2.indexOf(".", par1Str.length() + 1) < 0)
                {
                    long l = ((Long)this.profilingMap.get(s2)).longValue();
                    double d0 = (double)l * 100.0D / (double)k;
                    double d1 = (double)l * 100.0D / (double)i;
                    String s3 = s2.substring(par1Str.length());
                    arraylist.add(new ProfilerResult(s3, d0, d1));
                }
            }

            iterator1 = this.profilingMap.keySet().iterator();

            while (iterator1.hasNext())
            {
                s2 = (String)iterator1.next();
                this.profilingMap.put(s2, Long.valueOf(((Long)this.profilingMap.get(s2)).longValue() * 999L / 1000L));
            }

            if ((float)k > f)
            {
                arraylist.add(new ProfilerResult("unspecified", (double)((float)k - f) * 100.0D / (double)k, (double)((float)k - f) * 100.0D / (double)i));
            }

            Collections.sort(arraylist);
            arraylist.add(0, new ProfilerResult(par1Str, 100.0D, (double)k * 100.0D / (double)i));
            return arraylist;
        }
    }

    /**
     * End current section and start a new section
     */
    public void endStartSection(String par1Str)
    {
        this.endSection();
        this.startSection(par1Str);
    }

    public String getNameOfLastSection()
    {
        return this.sectionList.size() == 0 ? "[UNKNOWN]" : (String)this.sectionList.get(this.sectionList.size() - 1);
    }
}
