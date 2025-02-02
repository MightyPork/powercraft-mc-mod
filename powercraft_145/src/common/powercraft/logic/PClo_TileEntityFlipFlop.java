package powercraft.logic;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import powercraft.management.PC_PacketHandler;
import powercraft.management.PC_TileEntity;
import powercraft.management.PC_Utils.ValueWriting;

public class PClo_TileEntityFlipFlop extends PC_TileEntity
{
    private int type = 0;
    private boolean clock = false;

    public void create(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        type = stack.getItemDamage();
    }

    public int getType()
    {
        return type;
    }

    public boolean getClock()
    {
        return clock;
    }

    public void setClock(boolean state)
    {
        clock = state;
        PC_PacketHandler.setTileEntity(this, "clock", clock);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readFromNBT(nbtTagCompound);
        type = nbtTagCompound.getInteger("type");
        clock = nbtTagCompound.getBoolean("clock");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setInteger("type", type);
        nbtTagCompound.setBoolean("clock", clock);
    }

    @Override
    public void setData(Object[] o)
    {
        int p = 0;

        while (p < o.length)
        {
            String var = (String)o[p++];

            if (var.equals("type"))
            {
                type = (Integer)o[p++];
            }
            else if (var.equals("clock"))
            {
                clock = (Boolean)o[p++];
            }
        }

        ValueWriting.hugeUpdate(worldObj, xCoord, yCoord, zCoord);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public Object[] getData()
    {
        return new Object[]
                {
                    "type", type,
                    "clock", clock
                };
    }
}
