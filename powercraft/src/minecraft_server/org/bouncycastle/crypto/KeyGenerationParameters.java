package org.bouncycastle.crypto;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.security.SecureRandom;

@SideOnly(Side.CLIENT)
public class KeyGenerationParameters
{
    private SecureRandom random;
    private int strength;

    public KeyGenerationParameters(SecureRandom par1SecureRandom, int par2)
    {
        this.random = par1SecureRandom;
        this.strength = par2;
    }

    /**
     * Return the random source associated with this generator.
     */
    public SecureRandom getRandom()
    {
        return this.random;
    }

    /**
     * Return the bit strength for keys produced by this generator.
     */
    public int getStrength()
    {
        return this.strength;
    }
}
