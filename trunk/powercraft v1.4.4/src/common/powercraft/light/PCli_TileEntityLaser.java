package powercraft.light;

import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import powercraft.core.PC_BeamTracer;
import powercraft.core.PC_Color;
import powercraft.core.PC_CoordI;
import powercraft.core.PC_IBeamHandler;
import powercraft.core.PC_TileEntity;
import powercraft.core.PC_Utils;

public class PCli_TileEntityLaser extends PC_TileEntity implements PC_IBeamHandler
{
    private boolean active = false;
    private int ilcType;
    private PC_BeamTracer laser;

	public boolean isActive()
    {
        return active;
    }

    public void setType(int type)
    {
        ilcType = type;
    }

    public int getType()
    {
        return ilcType;
    }

    @Override
    public boolean canUpdate()
    {
        return true;
    }

    @Override
    public void updateEntity()
    {
    	if(laser==null){
	    	laser = new PC_BeamTracer(worldObj, this);
	    	laser.setStartCoord(getCoord());
	    	int metadata = PC_Utils.getMD(worldObj, xCoord, yCoord, zCoord);
	    	laser.setStartMove(metadata == 4?1:metadata == 5?-1:0, 0, metadata == 2?1:metadata == 3?-1:0);
	    	laser.setColor(new PC_Color(1.0, 1.0, 0.0));
	    	laser.setDetectEntities(true);
    	}
    	laser.flash();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        active = nbttagcompound.getBoolean("on");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("on", active);
    }

	@Override
	public boolean onBlockHit(PC_BeamTracer beamTracer, Block block, PC_CoordI coord) {
		if(!block.isOpaqueCube())
			return false;
		return true;
	}

	@Override
	public boolean onEntityHit(PC_BeamTracer beamTracer, Entity entity, PC_CoordI coord) {
		return true;
	}
}
