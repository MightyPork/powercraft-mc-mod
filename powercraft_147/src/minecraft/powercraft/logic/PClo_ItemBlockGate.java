package powercraft.logic;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import powercraft.api.PC_MathHelper;
import powercraft.api.block.PC_ItemBlock;
import powercraft.api.registry.PC_LangRegistry;
import powercraft.api.registry.PC_LangRegistry.LangEntry;
import powercraft.api.registry.PC_MSGRegistry;

public class PClo_ItemBlockGate extends PC_ItemBlock
{
    public PClo_ItemBlockGate(int id)
    {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public List<ItemStack> getItemStacks(List<ItemStack> arrayList)
    {
        for (int i = 0; i < PClo_GateType.TOTAL_GATE_COUNT; i++)
        {
            arrayList.add(new ItemStack(this, 1, i));
        }

        return arrayList;
    }

    @Override
    public int getIconFromDamage(int i)
    {
        return PClo_App.gate.getBlockTextureFromSideAndMetadata(1, 0);
    }

    @Override
    public String getItemNameIS(ItemStack itemstack)
    {
        return getItemName() + ".gate" + itemstack.getItemDamage();
    }

    @Override
    public boolean isFull3D()
    {
        return false;
    }

    @Override
    public boolean shouldRotateAroundWhenRendering()
    {
        return false;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean b)
    {
        list.add(getDescriptionForGate(itemStack.getItemDamage()));
    }

    public static String getDescriptionForGate(int dmg)
    {
        return PC_LangRegistry.tr("pc.gate." + PClo_GateType.names[PC_MathHelper.clamp_int(dmg, 0, PClo_GateType.TOTAL_GATE_COUNT - 1)] + ".desc");
    }

	@Override
	public Object msg(int msg, Object... obj) {
		switch(msg){
		case PC_MSGRegistry.MSG_DEFAULT_NAME:
			List<LangEntry> names = (List<LangEntry>)obj[0];
			for (int i = 0; i < PClo_GateType.TOTAL_GATE_COUNT; i++)
	        {
				names.add(new LangEntry(getItemName() + ".gate"+i, "gate "+PClo_GateType.names[i]));
	        };
            return names;
		}
		return null;
	}
}
