package powercraft.transport;

import powercraft.core.PC_CoordI;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;

public class PCtr_BlockBeltSpeedy extends PCtr_BlockBeltBase {

	public PCtr_BlockBeltSpeedy(int id) {
		super(id);
		setBlockName("PCSpeedyBelt");
	}

	@Override
	public String getDefaultName() {
		// TODO Auto-generated method stub
		return "Speedy Belt";
	}
	
	@Override
	public int getBlockTextureFromSideAndMetadata(int i, int j) {
		if (i == 0) {
			return 1;
		}
		if (i == 1) {
			return 4;
		} else {
			return 2;
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int i, int j, int k,
			Entity entity) {
		PC_CoordI pos = new PC_CoordI(i, j, k);

		if (PCtr_BeltHelper.isEntityIgnored(entity)) {
			return;
		}

		if (entity instanceof EntityItem) {
			PCtr_BeltHelper.packItems(world, pos);
		}


		int direction = getRotation(pos.getMeta(world));

		PC_CoordI pos_leading_to = pos.copy();
		switch (direction) {
			case 0: // Z--
				pos_leading_to.z--;
				break;

			case 1: // X++
				pos_leading_to.x++;
				break;

			// 6,7
			case 2: // Z++
				pos_leading_to.z++;
				break;

			case 3: // X--
				pos_leading_to.x--;
				break;
		}

		boolean leadsToNowhere = PCtr_BeltHelper.isBlocked(world, pos_leading_to);
		leadsToNowhere = leadsToNowhere && PCtr_BeltHelper.isBeyondStorageBorder(world, direction, pos, entity, PCtr_BeltHelper.STORAGE_BORDER_LONG);

		// longlife!
		if (!leadsToNowhere) {
			PCtr_BeltHelper.entityPreventDespawning(world, pos, true, entity);
		}



		double speed_max = PCtr_BeltHelper.MAX_HORIZONTAL_SPEED*2;

		double boost = PCtr_BeltHelper.HORIZONTAL_BOOST*2;

		PCtr_BeltHelper.moveEntityOnBelt(world, pos, entity, true, !leadsToNowhere, direction, speed_max, boost);

	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k,
			EntityPlayer entityplayer, int par6, float par7, float par8,
			float par9) {
		return PCtr_BeltHelper.blockActivated(world, i, j, k, entityplayer);
	}
	
	

}