package powercraft.logic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.management.annotation.PC_ClientServerSync;
import powercraft.management.tileentity.PC_TileEntity;

public class PClo_TileEntityFlipFlop extends PC_TileEntity
{
	@PC_ClientServerSync
	private int type = 0;
	@PC_ClientServerSync
    private boolean clock = false;
	
    public void create(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
    	type = stack.getItemDamage();
    }

    public int getType()
    {
        return type;
    }

    public boolean getClock()
    {
        return clock;
    }

    public void setClock(boolean state)
    {
    	if(clock!=state){
    		clock=state;
    		notifyChanges("clock");
    	}
    }

}
