package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet53BlockChange extends Packet
{
    public int xPosition;

    public int yPosition;

    public int zPosition;

    public int type;

    public int metadata;

    public Packet53BlockChange()
    {
        this.isChunkDataPacket = true;
    }

    public Packet53BlockChange(int par1, int par2, int par3, World par4World)
    {
        this.isChunkDataPacket = true;
        this.xPosition = par1;
        this.yPosition = par2;
        this.zPosition = par3;
        this.type = par4World.getBlockId(par1, par2, par3);
        this.metadata = par4World.getBlockMetadata(par1, par2, par3);
    }

    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.xPosition = par1DataInputStream.readInt();
        this.yPosition = par1DataInputStream.read();
        this.zPosition = par1DataInputStream.readInt();
        this.type = par1DataInputStream.readShort();
        this.metadata = par1DataInputStream.read();
    }

    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.xPosition);
        par1DataOutputStream.write(this.yPosition);
        par1DataOutputStream.writeInt(this.zPosition);
        par1DataOutputStream.writeShort(this.type);
        par1DataOutputStream.write(this.metadata);
    }

    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleBlockChange(this);
    }

    public int getPacketSize()
    {
        return 11;
    }
}
