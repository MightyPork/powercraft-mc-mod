package powercraft.management;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import net.minecraft.src.CompressedStreamTools;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public abstract class PC_TileEntity extends TileEntity
{

    @Override
    public Packet getDescriptionPacket()
    {
        Object[] o = getData();
        
        if (o == null)
        {
            return null;
        }

        ByteArrayOutputStream data = new ByteArrayOutputStream();
        ObjectOutputStream sendData;

        try
        {
            sendData = new ObjectOutputStream(data);
            sendData.writeInt(PC_PacketHandler.PACKETTILEENTITY);
            sendData.writeInt(xCoord);
            sendData.writeInt(yCoord);
            sendData.writeInt(zCoord);
           	sendData.writeObject(o);
            sendData.writeInt(PC_PacketHandler.PACKETTILEENTITY);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return new Packet250CustomPayload("PowerCraft", data.toByteArray());
    }

    public PC_VecI getCoord()
    {
        return new PC_VecI(xCoord, yCoord, zCoord);
    }
    
    public void setData(Object[] o)
    {
    }

    public Object[] getData()
    {
        return null;
    }

    public void create(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
    }

}
