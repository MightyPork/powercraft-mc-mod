package net.minecraft.src;

public class TileEntitySkull extends TileEntity
{
    /** Entity type for this skull. */
    private int skullType;

    /** The skull's rotation. */
    private int skullRotation;

    /** Extra data for this skull, used as player username by player heads */
    private String extraType = "";

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setByte("SkullType", (byte)(this.skullType & 255));
        par1NBTTagCompound.setByte("Rot", (byte)(this.skullRotation & 255));
        par1NBTTagCompound.setString("ExtraType", this.extraType);
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.skullType = par1NBTTagCompound.getByte("SkullType");
        this.skullRotation = par1NBTTagCompound.getByte("Rot");

        if (par1NBTTagCompound.hasKey("ExtraType"))
        {
            this.extraType = par1NBTTagCompound.getString("ExtraType");
        }
    }

    /**
     * Overriden in a sign to provide the text.
     */
    public Packet getDescriptionPacket()
    {
        NBTTagCompound var1 = new NBTTagCompound();
        this.writeToNBT(var1);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 4, var1);
    }

    public void func_82118_a(int par1, String par2Str)
    {
        this.skullType = par1;
        this.extraType = par2Str;
    }

    public int func_82117_a()
    {
        return this.skullType;
    }

    public void func_82116_a(int par1)
    {
        this.skullRotation = par1;
    }

    public String func_82120_c()
    {
        return this.extraType;
    }
}
