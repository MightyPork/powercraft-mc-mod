package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet34EntityTeleport extends Packet
{
    public int entityId;

    public int xPosition;

    public int yPosition;

    public int zPosition;

    public byte yaw;

    public byte pitch;

    public Packet34EntityTeleport() {}

    public Packet34EntityTeleport(Entity par1Entity)
    {
        this.entityId = par1Entity.entityId;
        this.xPosition = MathHelper.floor_double(par1Entity.posX * 32.0D);
        this.yPosition = MathHelper.floor_double(par1Entity.posY * 32.0D);
        this.zPosition = MathHelper.floor_double(par1Entity.posZ * 32.0D);
        this.yaw = (byte)((int)(par1Entity.rotationYaw * 256.0F / 360.0F));
        this.pitch = (byte)((int)(par1Entity.rotationPitch * 256.0F / 360.0F));
    }

    public Packet34EntityTeleport(int par1, int par2, int par3, int par4, byte par5, byte par6)
    {
        this.entityId = par1;
        this.xPosition = par2;
        this.yPosition = par3;
        this.zPosition = par4;
        this.yaw = par5;
        this.pitch = par6;
    }

    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.entityId = par1DataInputStream.readInt();
        this.xPosition = par1DataInputStream.readInt();
        this.yPosition = par1DataInputStream.readInt();
        this.zPosition = par1DataInputStream.readInt();
        this.yaw = (byte)par1DataInputStream.read();
        this.pitch = (byte)par1DataInputStream.read();
    }

    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.entityId);
        par1DataOutputStream.writeInt(this.xPosition);
        par1DataOutputStream.writeInt(this.yPosition);
        par1DataOutputStream.writeInt(this.zPosition);
        par1DataOutputStream.write(this.yaw);
        par1DataOutputStream.write(this.pitch);
    }

    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleEntityTeleport(this);
    }

    public int getPacketSize()
    {
        return 34;
    }

    public boolean isRealPacket()
    {
        return true;
    }

    public boolean containsSameEntityIDAs(Packet par1Packet)
    {
        Packet34EntityTeleport var2 = (Packet34EntityTeleport)par1Packet;
        return var2.entityId == this.entityId;
    }
}
