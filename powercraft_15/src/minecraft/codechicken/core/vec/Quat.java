package codechicken.core.vec;

import java.util.Formatter;
import java.util.Locale;


public class Quat
{
    public double x;
    public double y;
    public double z;
    public double s;
    public static final double SQRT2 = Math.sqrt(2D);

    public Quat()
    {
        s = 1.0D;
        x = 0.0D;
        y = 0.0D;
        z = 0.0D;
    }

    public Quat(Quat quat)
    {
        x = quat.x;
        y = quat.y;
        z = quat.z;
        s = quat.s;
    }

    public Quat(double d, double d1, double d2, double d3)
    {
        x = d1;
        y = d2;
        z = d3;
        s = d;
    }

    public Quat set(Quat quat)
    {
        x = quat.x;
        y = quat.y;
        z = quat.z;
        s = quat.s;
        
        return this;
    }

    public Quat set(double d, double d1, double d2, double d3)
    {
        x = d1;
        y = d2;
        z = d3;
        s = d;
        
        return this;
    }
    
    public static Quat aroundAxis(double ax, double ay, double az, double angle)
    {
        return new Quat().setAroundAxis(ax, ay, az, angle);
    }

    public static Quat aroundAxis(Vector3 axis, double angle)
    {
        return aroundAxis(axis.x, axis.y, axis.z, angle);
    }
    
    public Quat setAroundAxis(double ax, double ay, double az, double angle)
    {
        angle *= 0.5D;
        double d4 = Math.sin(angle);
        return set(Math.cos(angle), ax * d4, ay * d4, az * d4);
    }
    
    public Quat setAroundAxis(Vector3 axis, double angle)
    {
        return setAroundAxis(axis.x, axis.y, axis.z, angle);
    }

    public Quat multiply(Quat quat)
    {
        double d = s * quat.s - x * quat.x - y * quat.y - z * quat.z;
        double d1 = ((s * quat.x + x * quat.s) - y * quat.z) + z * quat.y;
        double d2 = (s * quat.y + x * quat.z + y * quat.s) - z * quat.x;
        double d3 = (s * quat.z - x * quat.y) + y * quat.x + z * quat.s;
        s = d;
        x = d1;
        y = d2;
        z = d3;
        
        return this;
    }

    public Quat rightMultiply(Quat quat)
    {
        double d = s * quat.s - x * quat.x - y * quat.y - z * quat.z;
        double d1 = (s * quat.x + x * quat.s + y * quat.z) - z * quat.y;
        double d2 = (s * quat.y - x * quat.z) + y * quat.s + z * quat.x;
        double d3 = ((s * quat.z + x * quat.y) - y * quat.x) + z * quat.s;
        s = d;
        x = d1;
        y = d2;
        z = d3;
        
        return this;
    }

    public double mag()
    {
        return Math.sqrt(x * x + y * y + z * z + s * s);
    }

    public Quat normalize()
    {
        double d = mag();
        if(d != 0.0D)
        {
            d = 1.0D / d;
            x *= d;
            y *= d;
            z *= d;
            s *= d;
        }
        
        return this;
    }

    public Quat copy()
    {
        return new Quat(this);
    }

    public void rotate(Vector3 vec)
    {
        double d = -x * vec.x - y * vec.y - z * vec.z;
        double d1 = (s * vec.x + y * vec.z) - z * vec.y;
        double d2 = (s * vec.y - x * vec.z) + z * vec.x;
        double d3 = (s * vec.z + x * vec.y) - y * vec.x;
        vec.x = (d1 * s - d * x - d2 * z) + d3 * y;
        vec.y = ((d2 * s - d * y) + d1 * z) - d3 * x;
        vec.z = (d3 * s - d * z - d1 * y) + d2 * x;
    }

    public String toString()
    {
        StringBuilder stringbuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringbuilder, Locale.US);
        formatter.format("Quaternion:\n", new Object[0]);
        formatter.format("  < %f %f %f %f >\n", new Object[] {
            Double.valueOf(s), Double.valueOf(x), Double.valueOf(y), Double.valueOf(z)
        });
        formatter.close();
        return stringbuilder.toString();
    }

}
