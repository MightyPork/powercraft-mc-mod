package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NBTTagList extends NBTBase
{
    private List tagList = new ArrayList();

    private byte tagType;

    public NBTTagList()
    {
        super("");
    }

    public NBTTagList(String par1Str)
    {
        super(par1Str);
    }

    void write(DataOutput par1DataOutput) throws IOException
    {
        if (!this.tagList.isEmpty())
        {
            this.tagType = ((NBTBase)this.tagList.get(0)).getId();
        }
        else
        {
            this.tagType = 1;
        }

        par1DataOutput.writeByte(this.tagType);
        par1DataOutput.writeInt(this.tagList.size());

        for (int var2 = 0; var2 < this.tagList.size(); ++var2)
        {
            ((NBTBase)this.tagList.get(var2)).write(par1DataOutput);
        }
    }

    void load(DataInput par1DataInput) throws IOException
    {
        this.tagType = par1DataInput.readByte();
        int var2 = par1DataInput.readInt();
        this.tagList = new ArrayList();

        for (int var3 = 0; var3 < var2; ++var3)
        {
            NBTBase var4 = NBTBase.newTag(this.tagType, (String)null);
            var4.load(par1DataInput);
            this.tagList.add(var4);
        }
    }

    public byte getId()
    {
        return (byte)9;
    }

    public String toString()
    {
        return "" + this.tagList.size() + " entries of type " + NBTBase.getTagName(this.tagType);
    }

    public void appendTag(NBTBase par1NBTBase)
    {
        this.tagType = par1NBTBase.getId();
        this.tagList.add(par1NBTBase);
    }

    @SideOnly(Side.CLIENT)

    public NBTBase removeTag(int par1)
    {
        return (NBTBase)this.tagList.remove(par1);
    }

    public NBTBase tagAt(int par1)
    {
        return (NBTBase)this.tagList.get(par1);
    }

    public int tagCount()
    {
        return this.tagList.size();
    }

    public NBTBase copy()
    {
        NBTTagList var1 = new NBTTagList(this.getName());
        var1.tagType = this.tagType;
        Iterator var2 = this.tagList.iterator();

        while (var2.hasNext())
        {
            NBTBase var3 = (NBTBase)var2.next();
            NBTBase var4 = var3.copy();
            var1.tagList.add(var4);
        }

        return var1;
    }

    public boolean equals(Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            NBTTagList var2 = (NBTTagList)par1Obj;

            if (this.tagType == var2.tagType)
            {
                return this.tagList.equals(var2.tagList);
            }
        }

        return false;
    }

    public int hashCode()
    {
        return super.hashCode() ^ this.tagList.hashCode();
    }
}
