package net.minecraft.src;

class TileEntityMobSpawnerSpawnData extends WeightedRandomItem
{
    public final NBTTagCompound field_92032_b;
    public final String field_92033_c;

    final TileEntityMobSpawner field_92031_d;

    public TileEntityMobSpawnerSpawnData(TileEntityMobSpawner par1TileEntityMobSpawner, NBTTagCompound par2NBTTagCompound)
    {
        super(par2NBTTagCompound.getInteger("Weight"));
        this.field_92031_d = par1TileEntityMobSpawner;
        this.field_92032_b = par2NBTTagCompound.getCompoundTag("Properties");
        this.field_92033_c = par2NBTTagCompound.getString("Type");
    }

    public TileEntityMobSpawnerSpawnData(TileEntityMobSpawner par1TileEntityMobSpawner, NBTTagCompound par2NBTTagCompound, String par3Str)
    {
        super(1);
        this.field_92031_d = par1TileEntityMobSpawner;
        this.field_92032_b = par2NBTTagCompound;
        this.field_92033_c = par3Str;
    }

    public NBTTagCompound func_92030_a()
    {
        NBTTagCompound var1 = new NBTTagCompound();
        var1.setCompoundTag("Properties", this.field_92032_b);
        var1.setString("Type", this.field_92033_c);
        var1.setInteger("Weight", this.itemWeight);
        return var1;
    }
}
