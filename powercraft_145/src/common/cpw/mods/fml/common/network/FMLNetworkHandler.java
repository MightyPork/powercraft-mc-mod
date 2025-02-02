package cpw.mods.fml.common.network;

import static cpw.mods.fml.common.network.FMLPacket.Type.MOD_LIST_REQUEST;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumGameType;
import net.minecraft.src.Item;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet131MapData;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.Packet3Chat;
import net.minecraft.src.ServerConfigurationManager;
import net.minecraft.src.World;
import net.minecraft.src.WorldType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.network.FMLPacket.Type;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;

public class FMLNetworkHandler
{
    private static final int FML_HASH = Hashing.murmur3_32().hashString("FML").asInt();
    private static final int PROTOCOL_VERSION = 0x1;
    private static final FMLNetworkHandler INSTANCE = new FMLNetworkHandler();

    // List of states for connections from clients to server
    static final int LOGIN_RECEIVED = 1;
    static final int CONNECTION_VALID = 2;
    static final int FML_OUT_OF_DATE = -1;
    static final int MISSING_MODS_OR_VERSIONS = -2;

    private Map<NetLoginHandler, Integer> loginStates = Maps.newHashMap();
    private Map<ModContainer, NetworkModHandler> networkModHandlers = Maps.newHashMap();

    private Map<Integer, NetworkModHandler> networkIdLookup = Maps.newHashMap();

    public static void handlePacket250Packet(Packet250CustomPayload packet, INetworkManager network, NetHandler handler)
    {
        String target = packet.channel;

        if (target.startsWith("MC|"))
        {
            handler.handleVanilla250Packet(packet);
        }
        if (target.equals("FML"))
        {
            instance().handleFMLPacket(packet, network, handler);
        }
        else
        {
            NetworkRegistry.instance().handleCustomPacket(packet, network, handler);
        }
    }

    public static void onConnectionEstablishedToServer(NetHandler clientHandler, INetworkManager manager, Packet1Login login)
    {
        NetworkRegistry.instance().clientLoggedIn(clientHandler, manager, login);
    }

    private void handleFMLPacket(Packet250CustomPayload packet, INetworkManager network, NetHandler netHandler)
    {
        FMLPacket pkt = FMLPacket.readPacket(packet.data);
        String userName = "";
        if (netHandler instanceof NetLoginHandler)
        {
            userName = ((NetLoginHandler) netHandler).clientUsername;
        }
        else
        {
            EntityPlayer pl = netHandler.getPlayer();
            if (pl != null)
            {
                userName = pl.getCommandSenderName();
            }
        }

        pkt.execute(network, this, netHandler, userName);
    }

    public static void onConnectionReceivedFromClient(NetLoginHandler netLoginHandler, MinecraftServer server, SocketAddress address, String userName)
    {
        instance().handleClientConnection(netLoginHandler, server, address, userName);
    }

    private void handleClientConnection(NetLoginHandler netLoginHandler, MinecraftServer server, SocketAddress address, String userName)
    {
        if (!loginStates.containsKey(netLoginHandler))
        {
            if (handleVanillaLoginKick(netLoginHandler, server, address, userName))
            {
                // No FML on the client
                FMLLog.fine("Connection from %s rejected - no FML packet received from client", userName);
                netLoginHandler.completeConnection("You don't have FML installed, you cannot connect to this server");
                return;
            }
            else
            {
                // Vanilla kicked us for some reason - bye now!
                FMLLog.fine("Connection from %s was closed by vanilla minecraft", userName);
                return;
            }

        }
        switch (loginStates.get(netLoginHandler))
        {
        case LOGIN_RECEIVED:
            // mods can try and kick undesireables here
            String modKick = NetworkRegistry.instance().connectionReceived(netLoginHandler, netLoginHandler.myTCPConnection);
            if (modKick != null)
            {
                netLoginHandler.completeConnection(modKick);
                loginStates.remove(netLoginHandler);
                return;
            }
            // The vanilla side wanted to kick
            if (!handleVanillaLoginKick(netLoginHandler, server, address, userName))
            {
                loginStates.remove(netLoginHandler);
                return;
            }
            // Reset the "connection completed" flag so processing can continue
            NetLoginHandler.func_72531_a(netLoginHandler, false);
            // Send the mod list request packet to the client from the server
            netLoginHandler.myTCPConnection.addToSendQueue(getModListRequestPacket());
            loginStates.put(netLoginHandler, CONNECTION_VALID);
            break;
        case CONNECTION_VALID:
            netLoginHandler.completeConnection(null);
            loginStates.remove(netLoginHandler);
            break;
        case MISSING_MODS_OR_VERSIONS:
            netLoginHandler.completeConnection("The server requires mods that are absent or out of date on your client");
            loginStates.remove(netLoginHandler);
            break;
        case FML_OUT_OF_DATE:
            netLoginHandler.completeConnection("Your client is not running a new enough version of FML to connect to this server");
            loginStates.remove(netLoginHandler);
            break;
        default:
            netLoginHandler.completeConnection("There was a problem during FML negotiation");
            loginStates.remove(netLoginHandler);
            break;
        }
    }

    /**
     * @param netLoginHandler
     * @param server
     * @param address
     * @param userName
     * @return if the user can carry on
     */
    private boolean handleVanillaLoginKick(NetLoginHandler netLoginHandler, MinecraftServer server, SocketAddress address, String userName)
    {
        // Vanilla reasons first
        ServerConfigurationManager playerList = server.getConfigurationManager();
        String kickReason = playerList.allowUserToConnect(address, userName);

        if (kickReason != null)
        {
            netLoginHandler.completeConnection(kickReason);
        }
        return kickReason == null;
    }

    public static void handleLoginPacketOnServer(NetLoginHandler handler, Packet1Login login)
    {
        if (login.clientEntityId == FML_HASH)
        {
            if (login.dimension == PROTOCOL_VERSION)
            {
                FMLLog.finest("Received valid FML login packet from %s", handler.myTCPConnection.getSocketAddress());
                instance().loginStates.put(handler, LOGIN_RECEIVED);
            }
            else if (login.dimension != PROTOCOL_VERSION)
            {
                FMLLog.finest("Received incorrect FML (%x) login packet from %s", login.dimension, handler.myTCPConnection.getSocketAddress());
                instance().loginStates.put(handler, FML_OUT_OF_DATE);
            }
        }
        else
        {
            FMLLog.fine("Received invalid login packet (%x, %x) from %s", login.clientEntityId, login.dimension,
                    handler.myTCPConnection.getSocketAddress());
        }
    }

    static void setHandlerState(NetLoginHandler handler, int state)
    {
        instance().loginStates.put(handler, state);
    }

    public static FMLNetworkHandler instance()
    {
        return INSTANCE;
    }

    public static Packet1Login getFMLFakeLoginPacket()
    {
        // Always reset compat to zero before sending our fake packet
        FMLCommonHandler.instance().getSidedDelegate().setClientCompatibilityLevel((byte) 0);
        Packet1Login fake = new Packet1Login();
        // Hash FML using a simple function
        fake.clientEntityId = FML_HASH;
        // The FML protocol version
        fake.dimension = PROTOCOL_VERSION;
        fake.gameType = EnumGameType.NOT_SET;
        fake.terrainType = WorldType.worldTypes[0];
        return fake;
    }

    public Packet250CustomPayload getModListRequestPacket()
    {
        return PacketDispatcher.getPacket("FML", FMLPacket.makePacket(MOD_LIST_REQUEST));
    }

    public void registerNetworkMod(NetworkModHandler handler)
    {
        networkModHandlers.put(handler.getContainer(), handler);
        networkIdLookup.put(handler.getNetworkId(), handler);
    }
    public boolean registerNetworkMod(ModContainer container, Class<?> networkModClass, ASMDataTable asmData)
    {
        NetworkModHandler handler = new NetworkModHandler(container, networkModClass, asmData);
        if (handler.isNetworkMod())
        {
            registerNetworkMod(handler);
        }

        return handler.isNetworkMod();
    }

    public NetworkModHandler findNetworkModHandler(Object mc)
    {
        if (mc instanceof ModContainer)
        {
            return networkModHandlers.get(mc);
        }
        else if (mc instanceof Integer)
        {
            return networkIdLookup.get(mc);
        }
        else
        {
            return networkModHandlers.get(FMLCommonHandler.instance().findContainerFor(mc));
        }
    }

    public Set<ModContainer> getNetworkModList()
    {
        return networkModHandlers.keySet();
    }

    public static void handlePlayerLogin(EntityPlayerMP player, NetServerHandler netHandler, INetworkManager manager)
    {
        NetworkRegistry.instance().playerLoggedIn(player, netHandler, manager);
        GameRegistry.onPlayerLogin(player);
    }

    public Map<Integer, NetworkModHandler> getNetworkIdMap()
    {
        return networkIdLookup;
    }

    public void bindNetworkId(String key, Integer value)
    {
        Map<String, ModContainer> mods = Loader.instance().getIndexedModList();
        NetworkModHandler handler = findNetworkModHandler(mods.get(key));
        if (handler != null)
        {
            handler.setNetworkId(value);
            networkIdLookup.put(value, handler);
        }
    }

    public static void onClientConnectionToRemoteServer(NetHandler netClientHandler, String server, int port, INetworkManager networkManager)
    {
        NetworkRegistry.instance().connectionOpened(netClientHandler, server, port, networkManager);
    }

    public static void onClientConnectionToIntegratedServer(NetHandler netClientHandler, MinecraftServer server, INetworkManager networkManager)
    {
        NetworkRegistry.instance().connectionOpened(netClientHandler, server, networkManager);
    }

    public static void onConnectionClosed(INetworkManager manager, EntityPlayer player)
    {
        NetworkRegistry.instance().connectionClosed(manager, player);
    }


    public static void openGui(EntityPlayer player, Object mod, int modGuiId, World world, int x, int y, int z)
    {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        if (mc == null)
        {
            NetworkModHandler nmh = instance().findNetworkModHandler(mod);
            if (nmh != null)
            {
                mc = nmh.getContainer();
            }
            else
            {
                FMLLog.warning("A mod tried to open a gui on the server without being a NetworkMod");
                return;
            }
        }
        if (player instanceof EntityPlayerMP)
        {
            NetworkRegistry.instance().openRemoteGui(mc, (EntityPlayerMP) player, modGuiId, world, x, y, z);
        }
        else
        {
            NetworkRegistry.instance().openLocalGui(mc, player, modGuiId, world, x, y, z);
        }
    }

    public static Packet getEntitySpawningPacket(Entity entity)
    {
        EntityRegistration er = EntityRegistry.instance().lookupModSpawn(entity.getClass(), false);
        if (er == null)
        {
            return null;
        }
        if (er.usesVanillaSpawning())
        {
            return null;
        }
        return PacketDispatcher.getPacket("FML", FMLPacket.makePacket(Type.ENTITYSPAWN, er, entity, instance().findNetworkModHandler(er.getContainer())));
    }

    public static void makeEntitySpawnAdjustment(int entityId, EntityPlayerMP player, int serverX, int serverY, int serverZ)
    {
        Packet250CustomPayload pkt = PacketDispatcher.getPacket("FML", FMLPacket.makePacket(Type.ENTITYSPAWNADJUSTMENT, entityId, serverX, serverY, serverZ));
        player.playerNetServerHandler.sendPacketToPlayer(pkt);
    }

    public static InetAddress computeLocalHost() throws IOException
    {
        InetAddress add = null;
        List<InetAddress> addresses = Lists.newArrayList();
        InetAddress localHost = InetAddress.getLocalHost();
        for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces()))
        {
            if (!ni.isLoopback() && ni.isUp())
            {
                addresses.addAll(Collections.list(ni.getInetAddresses()));
                if (addresses.contains(localHost))
                {
                    add = localHost;
                    break;
                }
            }
        }
        if (add == null && !addresses.isEmpty())
        {
            for (InetAddress addr: addresses)
            {
                if (addr.getAddress().length == 4)
                {
                    add = addr;
                    break;
                }
            }
        }
        if (add == null)
        {
            add = localHost;
        }
        return add;
    }

    public static Packet3Chat handleChatMessage(NetHandler handler, Packet3Chat chat)
    {
        return NetworkRegistry.instance().handleChat(handler, chat);
    }

    public static void handlePacket131Packet(NetHandler handler, Packet131MapData mapData)
    {
        if (handler instanceof NetServerHandler || mapData.itemID != Item.map.shiftedIndex)
        {
            // Server side and not "map" packets are always handled by us
            NetworkRegistry.instance().handleTinyPacket(handler, mapData);
        }
        else
        {
            // Fallback to the net client handler implementation
            FMLCommonHandler.instance().handleTinyPacket(handler, mapData);
        }
    }

    public static int getCompatibilityLevel()
    {
        return PROTOCOL_VERSION;
    }

    public static boolean vanillaLoginPacketCompatibility()
    {
        return FMLCommonHandler.instance().getSidedDelegate().getClientCompatibilityLevel() == 0;
    }
}