package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class Packet23VehicleSpawn extends Packet
{
    /** Entity ID of the object. */
    public int entityId;

    /** The X position of the object. */
    public int xPosition;

    /** The Y position of the object. */
    public int yPosition;

    /** The Z position of the object. */
    public int zPosition;

    /**
     * Not sent if the thrower entity ID is 0. The speed of this fireball along the X axis.
     */
    public int speedX;

    /**
     * Not sent if the thrower entity ID is 0. The speed of this fireball along the Y axis.
     */
    public int speedY;

    /**
     * Not sent if the thrower entity ID is 0. The speed of this fireball along the Z axis.
     */
    public int speedZ;

    /** The pitch in steps of 2p/256 */
    public int pitch;

    /** The yaw in steps of 2p/256 */
    public int yaw;

    /** The type of object. */
    public int type;

    /** 0 if not a fireball. Otherwise, this is the Entity ID of the thrower. */
    public int throwerEntityId;

    public Packet23VehicleSpawn() {}

    public Packet23VehicleSpawn(Entity par1Entity, int par2)
    {
        this(par1Entity, par2, 0);
    }

    public Packet23VehicleSpawn(Entity par1Entity, int par2, int par3)
    {
        this.entityId = par1Entity.entityId;
        this.xPosition = MathHelper.floor_double(par1Entity.posX * 32.0D);
        this.yPosition = MathHelper.floor_double(par1Entity.posY * 32.0D);
        this.zPosition = MathHelper.floor_double(par1Entity.posZ * 32.0D);
        this.pitch = MathHelper.floor_float(par1Entity.rotationPitch * 256.0F / 360.0F);
        this.yaw = MathHelper.floor_float(par1Entity.rotationYaw * 256.0F / 360.0F);
        this.type = par2;
        this.throwerEntityId = par3;

        if (par3 > 0)
        {
            double d0 = par1Entity.motionX;
            double d1 = par1Entity.motionY;
            double d2 = par1Entity.motionZ;
            double d3 = 3.9D;

            if (d0 < -d3)
            {
                d0 = -d3;
            }

            if (d1 < -d3)
            {
                d1 = -d3;
            }

            if (d2 < -d3)
            {
                d2 = -d3;
            }

            if (d0 > d3)
            {
                d0 = d3;
            }

            if (d1 > d3)
            {
                d1 = d3;
            }

            if (d2 > d3)
            {
                d2 = d3;
            }

            this.speedX = (int)(d0 * 8000.0D);
            this.speedY = (int)(d1 * 8000.0D);
            this.speedZ = (int)(d2 * 8000.0D);
        }
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.entityId = par1DataInputStream.readInt();
        this.type = par1DataInputStream.readByte();
        this.xPosition = par1DataInputStream.readInt();
        this.yPosition = par1DataInputStream.readInt();
        this.zPosition = par1DataInputStream.readInt();
        this.pitch = par1DataInputStream.readByte();
        this.yaw = par1DataInputStream.readByte();
        this.throwerEntityId = par1DataInputStream.readInt();

        if (this.throwerEntityId > 0)
        {
            this.speedX = par1DataInputStream.readShort();
            this.speedY = par1DataInputStream.readShort();
            this.speedZ = par1DataInputStream.readShort();
        }
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.entityId);
        par1DataOutputStream.writeByte(this.type);
        par1DataOutputStream.writeInt(this.xPosition);
        par1DataOutputStream.writeInt(this.yPosition);
        par1DataOutputStream.writeInt(this.zPosition);
        par1DataOutputStream.writeByte(this.pitch);
        par1DataOutputStream.writeByte(this.yaw);
        par1DataOutputStream.writeInt(this.throwerEntityId);

        if (this.throwerEntityId > 0)
        {
            par1DataOutputStream.writeShort(this.speedX);
            par1DataOutputStream.writeShort(this.speedY);
            par1DataOutputStream.writeShort(this.speedZ);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleVehicleSpawn(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 21 + this.throwerEntityId > 0 ? 6 : 0;
    }
}
