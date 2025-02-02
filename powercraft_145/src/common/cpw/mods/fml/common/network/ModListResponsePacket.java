package cpw.mods.fml.common.network;

import static cpw.mods.fml.common.network.FMLPacket.Type.MOD_IDENTIFIERS;
import static cpw.mods.fml.common.network.FMLPacket.Type.MOD_LIST_RESPONSE;
import static cpw.mods.fml.common.network.FMLPacket.Type.MOD_MISSING;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class ModListResponsePacket extends FMLPacket
{
    private Map<String,String> modVersions;
    private List<String> missingMods;

    public ModListResponsePacket()
    {
        super(MOD_LIST_RESPONSE);
    }

    @Override
    public byte[] generatePacket(Object... data)
    {
        Map<String,String> modVersions = (Map<String, String>) data[0];
        List<String> missingMods = (List<String>) data[1];
        ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        dat.writeInt(modVersions.size());
        for (Entry<String, String> version : modVersions.entrySet())
        {
            dat.writeUTF(version.getKey());
            dat.writeUTF(version.getValue());
        }
        dat.writeInt(missingMods.size());
        for (String missing : missingMods)
        {
            dat.writeUTF(missing);
        }
        return dat.toByteArray();
    }

    @Override
    public FMLPacket consumePacket(byte[] data)
    {
        ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        int versionListSize = dat.readInt();
        modVersions = Maps.newHashMapWithExpectedSize(versionListSize);
        for (int i = 0; i < versionListSize; i++)
        {
            String modName = dat.readUTF();
            String modVersion = dat.readUTF();
            modVersions.put(modName, modVersion);
        }

        int missingModSize = dat.readInt();
        missingMods = Lists.newArrayListWithExpectedSize(missingModSize);

        for (int i = 0; i < missingModSize; i++)
        {
            missingMods.add(dat.readUTF());
        }
        return this;
    }

    @Override
    public void execute(INetworkManager network, FMLNetworkHandler handler, NetHandler netHandler, String userName)
    {
        Map<String, ModContainer> indexedModList = Maps.newHashMap(Loader.instance().getIndexedModList());
        List<String> missingClientMods = Lists.newArrayList();
        List<String> versionIncorrectMods = Lists.newArrayList();

        for (String m : missingMods)
        {
            ModContainer mc = indexedModList.get(m);
            NetworkModHandler networkMod = handler.findNetworkModHandler(mc);
            if (networkMod.requiresClientSide())
            {
                missingClientMods.add(m);
            }
        }

        for (Entry<String,String> modVersion : modVersions.entrySet())
        {
            ModContainer mc = indexedModList.get(modVersion.getKey());
            NetworkModHandler networkMod = handler.findNetworkModHandler(mc);
            if (!networkMod.acceptVersion(modVersion.getValue()))
            {
                versionIncorrectMods.add(modVersion.getKey());
            }
        }

        Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.channel = "FML";
        if (missingClientMods.size()>0 || versionIncorrectMods.size() > 0)
        {
            pkt.data = FMLPacket.makePacket(MOD_MISSING, missingClientMods, versionIncorrectMods);
            Logger.getLogger("Minecraft").info(String.format("User %s connection failed: missing %s, bad versions %s", userName, missingClientMods, versionIncorrectMods));
            FMLLog.info("User %s connection failed: missing %s, bad versions %s", userName, missingClientMods, versionIncorrectMods);
            // Mark this as bad
            FMLNetworkHandler.setHandlerState((NetLoginHandler) netHandler, FMLNetworkHandler.MISSING_MODS_OR_VERSIONS);
        }
        else
        {
            pkt.data = FMLPacket.makePacket(MOD_IDENTIFIERS, netHandler);
            Logger.getLogger("Minecraft").info(String.format("User %s connecting with mods %s", userName, modVersions.keySet()));
            FMLLog.info("User %s connecting with mods %s", userName, modVersions.keySet());
        }

        pkt.length = pkt.data.length;
        network.addToSendQueue(pkt);
        // reset the continuation flag - we have completed extra negotiation and the login should complete now
        NetLoginHandler.func_72531_a((NetLoginHandler) netHandler, true);
    }

}
