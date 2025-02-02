package cpw.mods.fml.common.modloader;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class ModLoaderPacketHandler implements IPacketHandler
{
    private BaseModProxy mod;

    public ModLoaderPacketHandler(BaseModProxy mod)
    {
        this.mod = mod;
    }

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        if (player instanceof EntityPlayerMP)
        {
            mod.serverCustomPayload(((EntityPlayerMP)player).playerNetServerHandler, packet);
        }
        else
        {
            ModLoaderHelper.sidedHelper.sendClientPacket(mod, packet);
        }
    }

}
