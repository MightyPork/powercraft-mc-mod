package cpw.mods.fml.common.versioning;

import java.util.List;
import java.util.logging.Level;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.LoaderException;

/**
 * Parses version strings according to the specification here:
 * http://docs.codehaus.org/display/MAVEN/Versioning
 * and allows for comparison of versions based on that document.
 * Bounded version specifications are defined as
 * http://maven.apache.org/plugins/maven-enforcer-plugin/rules/versionRanges.html
 *
 * Borrows heavily from maven version range management code
 *
 * @author cpw
 *
 */
public class VersionParser
{
    private static final Splitter SEPARATOR = Splitter.on('@').omitEmptyStrings().trimResults();
    public static ArtifactVersion parseVersionReference(String labelledRef)
    {
        if (Strings.isNullOrEmpty(labelledRef))
        {
            throw new RuntimeException(String.format("Empty reference %s", labelledRef));
        }
        List<String> parts = Lists.newArrayList(SEPARATOR.split(labelledRef));
        if (parts.size()>2)
        {
            throw new RuntimeException(String.format("Invalid versioned reference %s", labelledRef));
        }
        if (parts.size()==1)
        {
            return new DefaultArtifactVersion(parts.get(0), true);
        }
        return new DefaultArtifactVersion(parts.get(0),parseRange(parts.get(1)));
    }

    public static boolean satisfies(ArtifactVersion target, ArtifactVersion source)
    {
        return target.containsVersion(source);
    }

    public static VersionRange parseRange(String range)
    {
        try
        {
            return VersionRange.createFromVersionSpec(range);
        }
        catch (InvalidVersionSpecificationException e)
        {
            FMLLog.log(Level.SEVERE, e, "Unable to parse a version range specification successfully %s", range);
            throw new LoaderException(e);
        }
    }
}
