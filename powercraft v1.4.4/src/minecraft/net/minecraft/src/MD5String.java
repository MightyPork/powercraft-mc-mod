package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SideOnly(Side.CLIENT)
public class MD5String
{
    /** The salt prepended to the string to be hashed */
    private String salt;

    public MD5String(String par1Str)
    {
        this.salt = par1Str;
    }

    /**
     * Gets the MD5 string
     */
    public String getMD5String(String par1Str)
    {
        try
        {
            String var2 = this.salt + par1Str;
            MessageDigest var3 = MessageDigest.getInstance("MD5");
            var3.update(var2.getBytes(), 0, var2.length());
            return (new BigInteger(1, var3.digest())).toString(16);
        }
        catch (NoSuchAlgorithmException var4)
        {
            throw new RuntimeException(var4);
        }
    }
}
