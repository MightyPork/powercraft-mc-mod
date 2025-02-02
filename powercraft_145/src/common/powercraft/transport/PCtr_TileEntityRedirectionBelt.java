package powercraft.transport;

import net.minecraft.src.Entity;
import powercraft.management.PC_Utils.GameInfo;
import powercraft.management.PC_VecI;

public class PCtr_TileEntityRedirectionBelt extends PCtr_TileEntityRedirectionBeltBase
{
    public PCtr_TileEntityRedirectionBelt() {}

    @Override
    protected int calculateItemDirection(Entity entity)
    {
        PCtr_BlockBeltRedirector block = ((PCtr_BlockBeltRedirector) PCtr_App.redirectionBelt);
        PC_VecI pos = getCoord();
        int meta = PCtr_BeltHelper.getRotation(GameInfo.getMD(worldObj, pos));
        int redir = 0;

        if (block.isPowered(worldObj, pos))
        {
            switch (meta)
            {
                case 0:
                    if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(1, 0, 0)))
                    {
                        redir = -1;
                    }
                    else if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(-1, 0, 0)))
                    {
                        redir = 1;
                    }

                    break;

                case 1:
                    if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, 1)))
                    {
                        redir = -1;
                    }
                    else if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, -1)))
                    {
                        redir = 1;
                    }

                    break;

                case 2:
                    if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(-1, 0, 0)))
                    {
                        redir = -1;
                    }
                    else if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(1, 0, 0)))
                    {
                        redir = 1;
                    }

                    break;

                case 3:
                    if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, -1)))
                    {
                        redir = -1;
                    }
                    else if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, 1)))
                    {
                        redir = 1;
                    }

                    break;
            }
        }

        if (redir == 0)
        {
            switch (meta)
            {
                case 0:
                    if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(1, 0, 0)) && PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(-1, 0, 0))
                            && !PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, -1)))
                    {
                        redir = 1;
                    }

                    break;

                case 1:
                    if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, 1)) && PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, -1))
                            && !PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(1, 0, 0)))
                    {
                        redir = 1;
                    }

                    break;

                case 2:
                    if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(-1, 0, 0)) && PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(1, 0, 0))
                            && !PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, 1)))
                    {
                        redir = 1;
                    }

                    break;

                case 3:
                    if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, -1)) && PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, 1))
                            && !PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(-1, 0, 0)))
                    {
                        redir = 1;
                    }

                    break;
            }
        }

        redir = -redir;
        setItemDirection(entity, Integer.valueOf(redir));
        return redir;
    }
}
