package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet130UpdateSign extends Packet
{
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public String[] signLines;

    public Packet130UpdateSign()
    {
        this.isChunkDataPacket = true;
    }

    public Packet130UpdateSign(int par1, int par2, int par3, String[] par4ArrayOfStr)
    {
        this.isChunkDataPacket = true;
        this.xPosition = par1;
        this.yPosition = par2;
        this.zPosition = par3;
        this.signLines = new String[] {par4ArrayOfStr[0], par4ArrayOfStr[1], par4ArrayOfStr[2], par4ArrayOfStr[3]};
    }

    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.xPosition = par1DataInputStream.readInt();
        this.yPosition = par1DataInputStream.readShort();
        this.zPosition = par1DataInputStream.readInt();
        this.signLines = new String[4];

        for (int var2 = 0; var2 < 4; ++var2)
        {
            this.signLines[var2] = readString(par1DataInputStream, 15);
        }
    }

    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.xPosition);
        par1DataOutputStream.writeShort(this.yPosition);
        par1DataOutputStream.writeInt(this.zPosition);

        for (int var2 = 0; var2 < 4; ++var2)
        {
            writeString(this.signLines[var2], par1DataOutputStream);
        }
    }

    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleUpdateSign(this);
    }

    public int getPacketSize()
    {
        int var1 = 0;

        for (int var2 = 0; var2 < 4; ++var2)
        {
            var1 += this.signLines[var2].length();
        }

        return var1;
    }
}
