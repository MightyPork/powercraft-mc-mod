package powercraft.machines;

import net.minecraft.src.EntityPlayer;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.tileentity.PC_TileEntity;

public class PCma_ContainerReplacer extends PC_GresBaseWithInventory<PCma_TileEntityReplacer>
{
    public PCma_ContainerReplacer(EntityPlayer player, PC_TileEntity te, Object[] o)
    {
        super(player, (PCma_TileEntityReplacer)te, o);
    }
}
