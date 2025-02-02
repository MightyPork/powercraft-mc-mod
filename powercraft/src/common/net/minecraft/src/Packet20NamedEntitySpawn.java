package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class Packet20NamedEntitySpawn extends Packet
{
    public int entityId;

    public String name;

    public int xPosition;

    public int yPosition;

    public int zPosition;

    public byte rotation;

    public byte pitch;

    public int currentItem;
    private DataWatcher metadata;
    private List field_73517_j;

    public Packet20NamedEntitySpawn() {}

    public Packet20NamedEntitySpawn(EntityPlayer par1EntityPlayer)
    {
        this.entityId = par1EntityPlayer.entityId;
        this.name = par1EntityPlayer.username;
        this.xPosition = MathHelper.floor_double(par1EntityPlayer.posX * 32.0D);
        this.yPosition = MathHelper.floor_double(par1EntityPlayer.posY * 32.0D);
        this.zPosition = MathHelper.floor_double(par1EntityPlayer.posZ * 32.0D);
        this.rotation = (byte)((int)(par1EntityPlayer.rotationYaw * 256.0F / 360.0F));
        this.pitch = (byte)((int)(par1EntityPlayer.rotationPitch * 256.0F / 360.0F));
        ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();
        this.currentItem = var2 == null ? 0 : var2.itemID;
        this.metadata = par1EntityPlayer.getDataWatcher();
    }

    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.entityId = par1DataInputStream.readInt();
        this.name = readString(par1DataInputStream, 16);
        this.xPosition = par1DataInputStream.readInt();
        this.yPosition = par1DataInputStream.readInt();
        this.zPosition = par1DataInputStream.readInt();
        this.rotation = par1DataInputStream.readByte();
        this.pitch = par1DataInputStream.readByte();
        this.currentItem = par1DataInputStream.readShort();
        this.field_73517_j = DataWatcher.readWatchableObjects(par1DataInputStream);
    }

    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.entityId);
        writeString(this.name, par1DataOutputStream);
        par1DataOutputStream.writeInt(this.xPosition);
        par1DataOutputStream.writeInt(this.yPosition);
        par1DataOutputStream.writeInt(this.zPosition);
        par1DataOutputStream.writeByte(this.rotation);
        par1DataOutputStream.writeByte(this.pitch);
        par1DataOutputStream.writeShort(this.currentItem);
        this.metadata.writeWatchableObjects(par1DataOutputStream);
    }

    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleNamedEntitySpawn(this);
    }

    public int getPacketSize()
    {
        return 28;
    }

    @SideOnly(Side.CLIENT)
    public List func_73509_c()
    {
        if (this.field_73517_j == null)
        {
            this.field_73517_j = this.metadata.func_75685_c();
        }

        return this.field_73517_j;
    }
}
