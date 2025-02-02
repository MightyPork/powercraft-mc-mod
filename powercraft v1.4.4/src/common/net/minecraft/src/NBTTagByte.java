package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByte extends NBTBase
{
    public byte data;

    public NBTTagByte(String par1Str)
    {
        super(par1Str);
    }

    public NBTTagByte(String par1Str, byte par2)
    {
        super(par1Str);
        this.data = par2;
    }

    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeByte(this.data);
    }

    void load(DataInput par1DataInput) throws IOException
    {
        this.data = par1DataInput.readByte();
    }

    public byte getId()
    {
        return (byte)1;
    }

    public String toString()
    {
        return "" + this.data;
    }

    public NBTBase copy()
    {
        return new NBTTagByte(this.getName(), this.data);
    }

    public boolean equals(Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            NBTTagByte var2 = (NBTTagByte)par1Obj;
            return this.data == var2.data;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return super.hashCode() ^ this.data;
    }
}
