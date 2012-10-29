package powercraft.logic;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import powercraft.core.PC_TileEntity;
import powercraft.core.PC_Utils;

public class PClo_TileEntityGate extends PC_TileEntity {

	private int type=0;
	
	public void create(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		type = stack.getItemDamage();
	}
	
	public int getType() {
		return type;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		type = nbtTagCompound.getInteger("type");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		nbtTagCompound.setInteger("type", type);
	}

	@Override
	public void setData(Object[] o) {
		int p = 0;
		while(p<o.length){
			String var = (String)o[p++];
			if(var.equals("type")){
				type = (Integer)o[p++];
			}
		}
	}

	@Override
	public Object[] getData() {
		return new Object[]{
				"type", type
		};
	}	
	
}
