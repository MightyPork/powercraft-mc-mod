package powercraft.core;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.src.EntityList;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntityMobSpawner;
import powercraft.core.PC_GresRadioButton.PC_GresRadioGroup;
import powercraft.core.PC_GresWidget.PC_GresAlign;

public class PCco_GuiSpawnerEditor implements PC_IGresClient {

	private TileEntityMobSpawner tems;
	private List<String> entityIds = new ArrayList<String>();
	private EntityPlayer thePlayer;
	
	public PCco_GuiSpawnerEditor(EntityPlayer player, Object[] o){
		tems = (TileEntityMobSpawner)player.worldObj.getBlockTileEntity((Integer)o[0], (Integer)o[1], (Integer)o[2]);
		thePlayer = player;
	}
	
	@Override
	public void initGui(PC_IGresGui gui) {
		PC_GresWidget w = new PC_GresWindow(230, 100, PC_Utils.tr("tile.mobSpawner.name")).setAlignH(PC_GresAlign.STRETCH);
		
		PC_GresScrollArea sa = new PC_GresScrollArea(PC_GresScrollArea.VSCROLL);
		sa.setSize(0, 100);
		
		PC_GresLayoutV lv = new PC_GresLayoutV();
		lv.setAlignH(PC_GresAlign.LEFT);
		
		PC_GresRadioGroup rg = new PC_GresRadioGroup();
		
		Map<Class, String> c2s = (Map<Class, String>)EntityList.classToStringMapping;
		Entry<Class, String>[] se = c2s.entrySet().toArray(new Entry[0]);
		Arrays.sort(se,  new Comparator<Entry<Class, String>>(){

			@Override
			public int compare(Entry<Class, String> arg0, Entry<Class, String> arg1) {
				return PC_Utils.tr(arg0.getValue()).compareToIgnoreCase(PC_Utils.tr(arg1.getValue()));
			}
			
		});
		
		for(Entry<Class, String> e: se){
			Class mob = e.getKey();
			if((mob.getModifiers() & Modifier.ABSTRACT)==0 && EntityLiving.class.isAssignableFrom(mob) && !EntityPlayer.class.isAssignableFrom(mob)){
				PC_GresRadioButton rb = new PC_GresRadioButton(PC_Utils.tr(e.getValue()), rg);
				entityIds.add(e.getValue());
				rb.setId(entityIds.size());
				if(e.getValue().equalsIgnoreCase(tems.getMobID()))
					rb.check(true);
				lv.add(rb);
			}
		}
		
		sa.setChild(lv);
		
		w.add(sa);
		
		gui.add(w);
	}

	@Override
	public void onGuiClosed(PC_IGresGui gui) {}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
		
		int id = widget.getId()-1;
		if(id>=0){
			PC_PacketHandler.sendToPacketHandler(thePlayer.worldObj, "MobSpawner", tems.xCoord, tems.yCoord, tems.zCoord, entityIds.get(id));
		}
		
	}

	@Override
	public void onEscapePressed(PC_IGresGui gui) {
		gui.close();
	}

	@Override
	public void onReturnPressed(PC_IGresGui gui) {}

	@Override
	public void updateTick(PC_IGresGui gui) {}

	@Override
	public void updateScreen(PC_IGresGui gui) {}

	@Override
	public boolean drawBackground(PC_IGresGui gui, int par1, int par2, float par3) {
		return false;
	}

}
