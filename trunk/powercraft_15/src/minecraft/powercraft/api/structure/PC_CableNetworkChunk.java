package powercraft.api.structure;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import powercraft.api.interfaces.PC_INBT;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;

public class PC_CableNetworkChunk implements PC_INBT<PC_CableNetworkChunk> {

	private int dimension;
	private List<PC_VecI> io = new ArrayList<PC_VecI>();
	private int ioToAddCount=0;
	private List<PC_VecI> ioToAdd;
	private int powerValue;
	private int cable;
	private int count;
	private int repeat;
	
	public PC_CableNetworkChunk(){
		
	}
	
	public PC_CableNetworkChunk(World world, int cable){
		dimension = world.getWorldInfo().getDimension();
		this.cable = cable;
	}
	
	public void addIO(PC_VecI pos){
		if(!io.contains(pos)){
			if(ioToAdd!=null)
				ioToAdd.add(pos);
			else
				io.add(pos);
			World world = getWorld();
			int pv = PC_Utils.getBlockRedstonePowereValue(world, pos);
			if(pv>powerValue){
				powerValue = pv;
				updateOutputs();
			}
		}
	}
	
	public void removeIO(PC_VecI pos){
		io.remove(pos);
	}
	
	public void setPowerValue(int value){
		if(value==powerValue)
			return;
		if(ioToAdd!=null){
			repeat = repeat<value?repeat:value;
			return;
		}
		if(value>powerValue){
			powerValue = value;
			updateOutputs();
		}else{
			do{
				repeat = 15;
				updatePowerValue();
			}while(repeat<powerValue);
		}
		repeat = 0;
	}
	
	public int getPowerValue(){
		return powerValue==0?0:powerValue-1;
	}
	
	public void addRef(){
		count++;
	}
	
	public boolean release(){
		count--;
		return count<=0;
	}
	
	public void updatePowerValue(){
		System.out.println("updatePowerValue");
		powerValue = 0;
		World world = getWorld();
		for(PC_VecI pos:io){
			int pv = powercraft.api.structure.PC_BlockStructure.structure.getRedstonePowereValueEx(world, pos.x, pos.y, pos.z);
			if(pv>powerValue)
				powerValue = pv;
		}
		updateOutputs();
	}
	
	public void updateOutputs(){
		World world = getWorld();
		if(ioToAdd!=null){
			ioToAddCount++;
		}else{
			ioToAdd = new ArrayList<PC_VecI>();
		}
		for(PC_VecI pos:io){
			PC_Utils.hugeUpdate(world, pos);
		}
		if(ioToAddCount>0){
			ioToAddCount--;
		}else{
			io.addAll(ioToAdd);
			ioToAdd = null;
		}
	}
	
	public World getWorld(){
		return PC_Utils.mcs().worldServerForDimension(dimension);
	}

	@Override
	public PC_CableNetworkChunk readFromNBT(NBTTagCompound nbttag) {
		dimension = nbttag.getInteger("dimension");
		powerValue = nbttag.getInteger("powerValue");
		cable = nbttag.getInteger("cable");
		count = nbttag.getInteger("count");
		io = (List<PC_VecI>)PC_Utils.loadFromNBT(nbttag, "io");
		return this;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttag) {
		nbttag.setInteger("dimension", dimension);
		nbttag.setInteger("powerValue", powerValue);
		nbttag.setInteger("cable", cable);
		nbttag.setInteger("count", count);
		PC_Utils.saveToNBT(nbttag, "io", io);
		return nbttag;
	}
	
}
