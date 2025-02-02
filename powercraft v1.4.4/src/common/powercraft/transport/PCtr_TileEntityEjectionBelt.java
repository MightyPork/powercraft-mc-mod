package powercraft.transport;

import java.util.Random;

import net.minecraft.src.NBTTagCompound;
import powercraft.core.PC_TileEntity;

public class PCtr_TileEntityEjectionBelt extends PC_TileEntity
{
    protected Random rand = new Random();

    public int actionType = 0;

    public int numStacksEjected = 1;

    public int numItemsEjected = 1;

    public int itemSelectMode = 0;

    public PCtr_TileEntityEjectionBelt() {}

    @Override
    public final boolean canUpdate()
    {
        return false;
    }

    @Override
    public final void updateEntity() {}

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setInteger("actionType", actionType);
        tag.setInteger("numStacks", numStacksEjected);
        tag.setInteger("numItems", numItemsEjected);
        tag.setInteger("selectMode", itemSelectMode);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        actionType = tag.getInteger("actionType");
        numStacksEjected = tag.getInteger("numStacks");
        numItemsEjected = tag.getInteger("numItems");
        itemSelectMode = tag.getInteger("selectMode");
    }

    @Override
    public void setData(Object[] o)
    {
        int p = 0;

        while (p < o.length)
        {
            String var = (String)o[p++];

            if (var.equals("actionType"))
            {
                actionType = (int)(Integer)o[p++];
            }
            else if (var.equals("itemSelectMode"))
            {
                itemSelectMode = (int)(Integer)o[p++];
            }
            else if (var.equals("numStacksEjected"))
            {
                numStacksEjected = (int)(Integer)o[p++];
            }
            else if (var.equals("numItemsEjected"))
            {
                numItemsEjected = (int)(Integer)o[p++];
            }
        }
    }

    @Override
    public Object[] getData()
    {
        Object[] o = new Object[8];
        o[0] = "actionType";
        o[1] = actionType;
        o[2] = "itemSelectMode";
        o[3] = itemSelectMode;
        o[4] = "numStacksEjected";
        o[5] = numStacksEjected;
        o[6] = "numItemsEjected";
        o[7] = numItemsEjected;
        return o;
    }
}
