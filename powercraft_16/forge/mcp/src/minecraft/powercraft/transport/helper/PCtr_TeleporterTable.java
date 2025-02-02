package powercraft.transport.helper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import powercraft.api.security.PC_Permissions;

public class PCtr_TeleporterTable 
{

	//Name
	//SubRoom
	
	
	private static class TeleporterRoom
	{
		public PC_Permissions permissions;
		public String name;
		public TeleporterRoom parent;
		public List<TeleporterRoom> teleporterSubRooms = new ArrayList<TeleporterRoom>();
		
		private TeleporterRoom(String parname, TeleporterRoom parparent)
		{
			name = parname;
			parent = parparent;
		}
		
		public static TeleporterRoom newRoom(String parname, TeleporterRoom parParent)
		{
			return new TeleporterRoom(parname, parParent);
		}
		
	}
	
	private static class TeleporterInfo extends TeleporterRoom
	{
		public TileEntity loadedTileEntity;
		public int dimension;
		public int x;
		public int y;
		public int z;
		
		public TeleporterInfo()
		{
			super("", null);
		}
	}
	
}
