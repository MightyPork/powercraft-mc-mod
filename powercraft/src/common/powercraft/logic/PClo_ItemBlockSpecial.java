package powercraft.logic;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import powercraft.core.PC_ItemBlock;
import powercraft.core.PC_MathHelper;
import powercraft.core.PC_Utils;

public class PClo_ItemBlockSpecial extends PC_ItemBlock
{
    public PClo_ItemBlockSpecial(int id)
    {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public String[] getDefaultNames()
    {
        List<String> s =  new ArrayList<String>();

        for (int i = 0; i < PClo_SpecialType.TOTAL_SPECIAL_COUNT - 1; i++)
        {
            s.add(getItemName() + ".special" + i);
            s.add("sensor " + PClo_SpecialType.names[i]);
        };

        int i = PClo_SpecialType.TOTAL_SPECIAL_COUNT - 1;

        s.add(getItemName() + ".special" + i);

        s.add(PClo_SpecialType.names[i] + " controller");

        s.add(getItemName());

        s.add("special");

        return s.toArray(new String[0]);
    }

    @Override
    public List<ItemStack> getItemStacks(List<ItemStack> arrayList)
    {
        for (int i = 0; i < PClo_SpecialType.TOTAL_SPECIAL_COUNT; i++)
        {
            arrayList.add(new ItemStack(this, 1, i));
        }

        return arrayList;
    }

    @Override
    public int getIconFromDamage(int i)
    {
        return mod_PowerCraftLogic.special.getBlockTextureFromSideAndMetadata(1, 0);
    }

    @Override
    public String getItemNameIS(ItemStack itemstack)
    {
        return getItemName() + ".special" + itemstack.getItemDamage();
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
        list.add(getDescriptionForSpecial(itemStack.getItemDamage()));
    }

    public static String getDescriptionForSpecial(int dmg)
    {
        return PC_Utils.tr("pc.special." + PClo_SpecialType.names[PC_MathHelper.clamp_int(dmg, 0, PClo_SpecialType.TOTAL_SPECIAL_COUNT - 1)] + ".desc");
    }
}
