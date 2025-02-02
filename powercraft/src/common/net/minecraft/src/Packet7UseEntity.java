package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet7UseEntity extends Packet
{
    public int playerEntityId;

    public int targetEntity;

    public int isLeftClick;

    public Packet7UseEntity() {}

    @SideOnly(Side.CLIENT)
    public Packet7UseEntity(int par1, int par2, int par3)
    {
        this.playerEntityId = par1;
        this.targetEntity = par2;
        this.isLeftClick = par3;
    }

    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.playerEntityId = par1DataInputStream.readInt();
        this.targetEntity = par1DataInputStream.readInt();
        this.isLeftClick = par1DataInputStream.readByte();
    }

    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.playerEntityId);
        par1DataOutputStream.writeInt(this.targetEntity);
        par1DataOutputStream.writeByte(this.isLeftClick);
    }

    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleUseEntity(this);
    }

    public int getPacketSize()
    {
        return 9;
    }
}
