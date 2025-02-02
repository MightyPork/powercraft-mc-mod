package powercraft.core;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.management.PC_Item;
import powercraft.management.PC_Struct3;
import powercraft.management.PC_Utils;
import powercraft.management.PC_Utils.Gres;
import powercraft.management.PC_Utils.Lang;

public class PCco_ItemOreSniffer extends PC_Item
{
    public PCco_ItemOreSniffer(int id)
    {
    	super(id);
        setMaxStackSize(1);
        setMaxDamage(500);
        setIconIndex(1);
        setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float par8, float par9, float par10)
    {
        if (world.isRemote)
        {
            Gres.openGres("OreSnifferResultScreen", entityplayer, i, j, k, l);
        }

        itemstack.damageItem(1, entityplayer);
        return false;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean b)
    {
        list.add(Lang.tr("pc.sniffer.desc"));
    }

	@Override
	public Object msg(int msg, Object... obj) {
		switch(msg){
		case PC_Utils.MSG_DEFAULT_NAME:
			List<PC_Struct3<String, String, String[]>> names = (List<PC_Struct3<String, String, String[]>>)obj[0];
			names.add(new PC_Struct3<String, String, String[]>(getItemName(), "Ore Sniffer", null));
            return names;
		}
		return null;
	}
}
