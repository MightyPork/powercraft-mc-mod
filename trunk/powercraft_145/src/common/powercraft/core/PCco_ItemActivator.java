package powercraft.core;

import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Direction;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import powercraft.management.PC_IMSG;
import powercraft.management.PC_Item;
import powercraft.management.PC_MathHelper;
import powercraft.management.PC_Struct3;
import powercraft.management.PC_Utils;
import powercraft.management.PC_VecI;

public class PCco_ItemActivator extends PC_Item{
    
	public PCco_ItemActivator(){
        setMaxDamage(100);
        setMaxStackSize(1);
        setIconIndex(2);
        setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int l, float par8, float par9, float par10)
    {
    	
    	int dir = ((PC_MathHelper.floor_double(((entityplayer.rotationYaw * 4F) / 360F) + 0.5D) & 3) + 2) % 4;

//		if (PC_Utils.isPlacingReversed()) {
//			dir = PC_Utils.reverseSide(dir);
//		}

		for (int i = 0; i < 3; i++) {

			PC_VecI pos = new PC_VecI(x-Direction.offsetX[dir], y+i, z-Direction.offsetZ[dir]);
			if (i == 2) {
				//try direct up.
				pos = new PC_VecI(x, y+1, z);
			}

			if (PC_Utils.getBID(world, pos) == Block.chest.blockID && PC_Utils.getBID(world, pos.copy().add(0, -1, 0)) == Block.blockSteel.blockID) {
				break;
			}

			ItemStack stackchest = PC_Utils.extractAndRemoveChest(world, pos);
			if (stackchest != null) {
				PC_Utils.dropItemStack(world, stackchest, pos);
				return true;
			}
		}
        
		List<Object> objs = PC_Utils.getRgisterdObjects();

        for (Object obj : objs){
        	if(obj instanceof PC_IMSG){
        		Object o = ((PC_IMSG)obj).msg(PC_Utils.MSG_ON_ACTIVATOR_USED_ON_BLOCK, itemstack, entityplayer, world, new PC_VecI(x, y, z));
        		if(o instanceof Boolean && (Boolean)o){
        			return true;
        		}
        	}
        }

        return false;
    }

	@Override
	public Object msg(int msg, Object... obj) {
		switch(msg){
		case PC_Utils.MSG_DEFAULT_NAME:
			List<PC_Struct3<String, String, String[]>> names = (List<PC_Struct3<String, String, String[]>>)obj[0];
			names.add(new PC_Struct3<String, String, String[]>(getItemName(), "Activation Crystal", null));
            return names;
		}
		return null;
	}
}