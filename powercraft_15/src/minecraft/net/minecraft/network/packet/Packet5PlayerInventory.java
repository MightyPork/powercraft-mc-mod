package net.minecraft.network.packet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.item.ItemStack;

public class Packet5PlayerInventory extends Packet
{
    /** Entity ID of the object. */
    public int entityID;

    /** Equipment slot: 0=held, 1-4=armor slot */
    public int slot;

    /** The item in the slot format (an ItemStack) */
    private ItemStack itemSlot;

    public Packet5PlayerInventory() {}

    public Packet5PlayerInventory(int par1, int par2, ItemStack par3ItemStack)
    {
        this.entityID = par1;
        this.slot = par2;
        this.itemSlot = par3ItemStack == null ? null : par3ItemStack.copy();
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.entityID = par1DataInputStream.readInt();
        this.slot = par1DataInputStream.readShort();
        this.itemSlot = readItemStack(par1DataInputStream);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.entityID);
        par1DataOutputStream.writeShort(this.slot);
        writeItemStack(this.itemSlot, par1DataOutputStream);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handlePlayerInventory(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 8;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Gets the item in the slot format (an ItemStack)
     */
    public ItemStack getItemSlot()
    {
        return this.itemSlot;
    }

    /**
     * only false for the abstract Packet class, all real packets return true
     */
    public boolean isRealPacket()
    {
        return true;
    }

    /**
     * eg return packet30entity.entityId == entityId; WARNING : will throw if you compare a packet to a different packet
     * class
     */
    public boolean containsSameEntityIDAs(Packet par1Packet)
    {
        Packet5PlayerInventory packet5playerinventory = (Packet5PlayerInventory)par1Packet;
        return packet5playerinventory.entityID == this.entityID && packet5playerinventory.slot == this.slot;
    }
}
