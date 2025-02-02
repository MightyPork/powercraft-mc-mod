package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet11PlayerPosition extends Packet10Flying
{
    public Packet11PlayerPosition()
    {
        this.moving = true;
    }

    @SideOnly(Side.CLIENT)
    public Packet11PlayerPosition(double par1, double par3, double par5, double par7, boolean par9)
    {
        this.xPosition = par1;
        this.yPosition = par3;
        this.stance = par5;
        this.zPosition = par7;
        this.onGround = par9;
        this.moving = true;
    }

    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.xPosition = par1DataInputStream.readDouble();
        this.yPosition = par1DataInputStream.readDouble();
        this.stance = par1DataInputStream.readDouble();
        this.zPosition = par1DataInputStream.readDouble();
        super.readPacketData(par1DataInputStream);
    }

    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeDouble(this.xPosition);
        par1DataOutputStream.writeDouble(this.yPosition);
        par1DataOutputStream.writeDouble(this.stance);
        par1DataOutputStream.writeDouble(this.zPosition);
        super.writePacketData(par1DataOutputStream);
    }

    public int getPacketSize()
    {
        return 33;
    }
}
