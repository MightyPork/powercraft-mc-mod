package cpw.mods.fml.common.versioning;

public class DefaultArtifactVersion implements ArtifactVersion
{
    private ComparableVersion comparableVersion;
    private String label;
    private boolean unbounded;
    private VersionRange range;

    public DefaultArtifactVersion(String versionNumber)
    {
        comparableVersion = new ComparableVersion(versionNumber);
        range = VersionRange.createFromVersion(versionNumber, this);
    }

    public DefaultArtifactVersion(String label, VersionRange range)
    {
        this.label = label;
        this.range = range;
    }
    public DefaultArtifactVersion(String label, String version)
    {
        this(version);
        this.label = label;
    }

    public DefaultArtifactVersion(String string, boolean unbounded)
    {
        this.label = string;
        this.unbounded = true;
    }

    @Override
    public boolean equals(Object obj)
    {
        return ((DefaultArtifactVersion)obj).containsVersion(this);
    }

    @Override
    public int compareTo(ArtifactVersion o)
    {
        return unbounded ? 0 : this.comparableVersion.compareTo(((DefaultArtifactVersion)o).comparableVersion);
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    @Override
    public boolean containsVersion(ArtifactVersion source)
    {
        if (!source.getLabel().equals(getLabel()))
        {
            return false;
        }

        if (unbounded)
        {
            return true;
        }

        if (range != null)
        {
            return range.containsVersion(source);
        }
        else
        {
            return false;
        }
    }

    @Override
    public String getVersionString()
    {
        return comparableVersion == null ? "unknown" : comparableVersion.toString();
    }

    @Override
    public String getRangeString()
    {
        return range == null ? "any" : range.toString();
    }
    @Override
    public String toString()
    {
        return label == null ? comparableVersion.toString() : label + (unbounded ? "" : "@" + range);
    }
}
