package powercraft.light;

import net.minecraft.src.NBTTagCompound;
import powercraft.management.PC_PacketHandler;
import powercraft.management.PC_TileEntity;
import powercraft.management.PC_Utils.ValueWriting;

public class PCli_TileEntityLaserSensor extends PC_TileEntity {

	private boolean active=false;
	private int coolDown = 0;
	
	public void hitByBeam() {
		coolDown = 2;
		if(!active){
			active = true;
			PC_PacketHandler.setTileEntity(this, "active", active);
			ValueWriting.hugeUpdate(worldObj, xCoord, yCoord, zCoord);
		}
	}

	public boolean isActive() {
		return active;
	}

	@Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
    	if(coolDown>0){
    		if(--coolDown==0){
    			active = false;
    			PC_PacketHandler.setTileEntity(this, "active", active);
    			ValueWriting.hugeUpdate(worldObj, xCoord, yCoord, zCoord);
    		}
    	}
    }
	
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        active = nbttagcompound.getBoolean("active");
        coolDown = nbttagcompound.getInteger("coolDown");
        
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("active", active);
        nbttagcompound.setInteger("coolDown", coolDown);
    }
    
    @Override
	public void setData(Object[] o) {
		int p = 0;

        while (p < o.length)
        {
            String var = (String)o[p++];
            if(var.equals("active")){
            	active = (Boolean)o[p++];
            }else if(var.equals("coolDown")){
            	coolDown = (Integer)o[p++];
            }
   
        }
        ValueWriting.hugeUpdate(worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public Object[] getData() {
		return new Object[]{
			"active", active,
			"coolDown", coolDown
		};
	}
    
}
