package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet103SetSlot extends Packet
{
    public int windowId;

    public int itemSlot;

    public ItemStack myItemStack;

    public Packet103SetSlot() {}

    public Packet103SetSlot(int par1, int par2, ItemStack par3ItemStack)
    {
        this.windowId = par1;
        this.itemSlot = par2;
        this.myItemStack = par3ItemStack == null ? par3ItemStack : par3ItemStack.copy();
    }

    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleSetSlot(this);
    }

    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.windowId = par1DataInputStream.readByte();
        this.itemSlot = par1DataInputStream.readShort();
        this.myItemStack = readItemStack(par1DataInputStream);
    }

    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeByte(this.windowId);
        par1DataOutputStream.writeShort(this.itemSlot);
        writeItemStack(this.myItemStack, par1DataOutputStream);
    }

    public int getPacketSize()
    {
        return 8;
    }
}
