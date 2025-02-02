package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.PublicKey;

public class Packet253ServerAuthData extends Packet
{
    private String serverId;
    private PublicKey publicKey;
    private byte[] verifyToken = new byte[0];

    public Packet253ServerAuthData() {}

    public Packet253ServerAuthData(String par1Str, PublicKey par2PublicKey, byte[] par3ArrayOfByte)
    {
        this.serverId = par1Str;
        this.publicKey = par2PublicKey;
        this.verifyToken = par3ArrayOfByte;
    }

    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.serverId = readString(par1DataInputStream, 20);
        this.publicKey = CryptManager.func_75896_a(readBytesFromStream(par1DataInputStream));
        this.verifyToken = readBytesFromStream(par1DataInputStream);
    }

    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        writeString(this.serverId, par1DataOutputStream);
        writeByteArray(par1DataOutputStream, this.publicKey.getEncoded());
        writeByteArray(par1DataOutputStream, this.verifyToken);
    }

    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleServerAuthData(this);
    }

    public int getPacketSize()
    {
        return 2 + this.serverId.length() * 2 + 2 + this.publicKey.getEncoded().length + 2 + this.verifyToken.length;
    }

    @SideOnly(Side.CLIENT)
    public String getServerId()
    {
        return this.serverId;
    }

    @SideOnly(Side.CLIENT)
    public PublicKey getPublicKey()
    {
        return this.publicKey;
    }

    @SideOnly(Side.CLIENT)
    public byte[] getVerifyToken()
    {
        return this.verifyToken;
    }
}
