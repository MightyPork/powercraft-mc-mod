package cpw.mods.fml.common.network;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet131MapData;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.Packet3Chat;
import net.minecraft.src.World;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.network.FMLPacket.Type;

public class NetworkRegistry
{
    private static final NetworkRegistry INSTANCE = new NetworkRegistry();

    private Multimap<Player, String> activeChannels = ArrayListMultimap.create();

    private Multimap<String, IPacketHandler> universalPacketHandlers = ArrayListMultimap.create();
    private Multimap<String, IPacketHandler> clientPacketHandlers = ArrayListMultimap.create();
    private Multimap<String, IPacketHandler> serverPacketHandlers = ArrayListMultimap.create();

    private Set<IConnectionHandler> connectionHandlers = Sets.newLinkedHashSet();
    private Map<ModContainer, IGuiHandler> serverGuiHandlers = Maps.newHashMap();
    private Map<ModContainer, IGuiHandler> clientGuiHandlers = Maps.newHashMap();
    private List<IChatListener> chatListeners = Lists.newArrayList();

    public static NetworkRegistry instance()
    {
        return INSTANCE;
    }

    byte[] getPacketRegistry(Side side)
    {
        return Joiner.on('\0').join(Iterables.concat(Arrays.asList("FML"), universalPacketHandlers.keySet(), side.isClient() ? clientPacketHandlers.keySet() : serverPacketHandlers.keySet())).getBytes(Charsets.UTF_8);
    }

    public boolean isChannelActive(String channel, Player player)
    {
        return activeChannels.containsEntry(player, channel);
    }

    public void registerChannel(IPacketHandler handler, String channelName)
    {
        if (Strings.isNullOrEmpty(channelName) || (channelName != null && channelName.length() > 16))
        {
            FMLLog.severe("Invalid channel name '%s' : %s", channelName, Strings.isNullOrEmpty(channelName) ? "Channel name is empty" : "Channel name is too long (16 chars is maximum)");
            throw new RuntimeException("Channel name is invalid");
        }

        universalPacketHandlers.put(channelName, handler);
    }

    public void registerChannel(IPacketHandler handler, String channelName, Side side)
    {
        if (side == null)
        {
            registerChannel(handler, channelName);
            return;
        }

        if (Strings.isNullOrEmpty(channelName) || (channelName != null && channelName.length() > 16))
        {
            FMLLog.severe("Invalid channel name '%s' : %s", channelName, Strings.isNullOrEmpty(channelName) ? "Channel name is empty" : "Channel name is too long (16 chars is maximum)");
            throw new RuntimeException("Channel name is invalid");
        }

        if (side.isClient())
        {
            clientPacketHandlers.put(channelName, handler);
        }
        else
        {
            serverPacketHandlers.put(channelName, handler);
        }
    }

    void activateChannel(Player player, String channel)
    {
        activeChannels.put(player, channel);
    }

    void deactivateChannel(Player player, String channel)
    {
        activeChannels.remove(player, channel);
    }

    public void registerConnectionHandler(IConnectionHandler handler)
    {
        connectionHandlers.add(handler);
    }

    public void registerChatListener(IChatListener listener)
    {
        chatListeners.add(listener);
    }

    void playerLoggedIn(EntityPlayerMP player, NetServerHandler netHandler, INetworkManager manager)
    {
        generateChannelRegistration(player, netHandler, manager);

        for (IConnectionHandler handler : connectionHandlers)
        {
            handler.playerLoggedIn((Player)player, netHandler, manager);
        }
    }

    String connectionReceived(NetLoginHandler netHandler, INetworkManager manager)
    {
        for (IConnectionHandler handler : connectionHandlers)
        {
            String kick = handler.connectionReceived(netHandler, manager);

            if (!Strings.isNullOrEmpty(kick))
            {
                return kick;
            }
        }

        return null;
    }

    void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager networkManager)
    {
        for (IConnectionHandler handler : connectionHandlers)
        {
            handler.connectionOpened(netClientHandler, server, port, networkManager);
        }
    }

    void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager networkManager)
    {
        for (IConnectionHandler handler : connectionHandlers)
        {
            handler.connectionOpened(netClientHandler, server, networkManager);
        }
    }

    void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login)
    {
        generateChannelRegistration(clientHandler.getPlayer(), clientHandler, manager);

        for (IConnectionHandler handler : connectionHandlers)
        {
            handler.clientLoggedIn(clientHandler, manager, login);
        }
    }

    void connectionClosed(INetworkManager manager, EntityPlayer player)
    {
        for (IConnectionHandler handler : connectionHandlers)
        {
            handler.connectionClosed(manager);
        }

        activeChannels.removeAll(player);
    }

    void generateChannelRegistration(EntityPlayer player, NetHandler netHandler, INetworkManager manager)
    {
        Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.channel = "REGISTER";
        pkt.data = getPacketRegistry(player instanceof EntityPlayerMP ? Side.SERVER : Side.CLIENT);
        pkt.length = pkt.data.length;
        manager.addToSendQueue(pkt);
    }

    void handleCustomPacket(Packet250CustomPayload packet, INetworkManager network, NetHandler handler)
    {
        if ("REGISTER".equals(packet.channel))
        {
            handleRegistrationPacket(packet, (Player)handler.getPlayer());
        }
        else if ("UNREGISTER".equals(packet.channel))
        {
            handleUnregistrationPacket(packet, (Player)handler.getPlayer());
        }
        else
        {
            handlePacket(packet, network, (Player)handler.getPlayer());
        }
    }

    private void handlePacket(Packet250CustomPayload packet, INetworkManager network, Player player)
    {
        String channel = packet.channel;

        for (IPacketHandler handler : Iterables.concat(universalPacketHandlers.get(channel), player instanceof EntityPlayerMP ? serverPacketHandlers.get(channel) : clientPacketHandlers.get(channel)))
        {
            handler.onPacketData(network, packet, player);
        }
    }

    private void handleRegistrationPacket(Packet250CustomPayload packet, Player player)
    {
        List<String> channels = extractChannelList(packet);

        for (String channel : channels)
        {
            activateChannel(player, channel);
        }
    }
    private void handleUnregistrationPacket(Packet250CustomPayload packet, Player player)
    {
        List<String> channels = extractChannelList(packet);

        for (String channel : channels)
        {
            deactivateChannel(player, channel);
        }
    }

    private List<String> extractChannelList(Packet250CustomPayload packet)
    {
        String request = new String(packet.data, Charsets.UTF_8);
        List<String> channels = Lists.newArrayList(Splitter.on('\0').split(request));
        return channels;
    }

    public void registerGuiHandler(Object mod, IGuiHandler handler)
    {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);

        if (mc == null)
        {
            mc = Loader.instance().activeModContainer();
            FMLLog.log(Level.WARNING, "Mod %s attempted to register a gui network handler during a construction phase", mc.getModId());
        }

        NetworkModHandler nmh = FMLNetworkHandler.instance().findNetworkModHandler(mc);

        if (nmh == null)
        {
            FMLLog.log(Level.FINE, "The mod %s needs to be a @NetworkMod to register a Networked Gui Handler", mc.getModId());
        }
        else
        {
            serverGuiHandlers.put(mc, handler);
        }

        clientGuiHandlers.put(mc, handler);
    }
    void openRemoteGui(ModContainer mc, EntityPlayerMP player, int modGuiId, World world, int x, int y, int z)
    {
        IGuiHandler handler = serverGuiHandlers.get(mc);
        NetworkModHandler nmh = FMLNetworkHandler.instance().findNetworkModHandler(mc);

        if (handler != null && nmh != null)
        {
            Container container = (Container)handler.getServerGuiElement(modGuiId, player, world, x, y, z);

            if (container != null)
            {
                player.incrementWindowID();
                player.closeInventory();
                int windowId = player.currentWindowId;
                Packet250CustomPayload pkt = new Packet250CustomPayload();
                pkt.channel = "FML";
                pkt.data = FMLPacket.makePacket(Type.GUIOPEN, windowId, nmh.getNetworkId(), modGuiId, x, y, z);
                pkt.length = pkt.data.length;
                player.playerNetServerHandler.sendPacketToPlayer(pkt);
                player.craftingInventory = container;
                player.craftingInventory.windowId = windowId;
                player.craftingInventory.addCraftingToCrafters(player);
            }
        }
    }
    void openLocalGui(ModContainer mc, EntityPlayer player, int modGuiId, World world, int x, int y, int z)
    {
        IGuiHandler handler = clientGuiHandlers.get(mc);
        FMLCommonHandler.instance().showGuiScreen(handler.getClientGuiElement(modGuiId, player, world, x, y, z));
    }
    public Packet3Chat handleChat(NetHandler handler, Packet3Chat chat)
    {
        Side s = Side.CLIENT;

        if (handler instanceof NetServerHandler)
        {
            s = Side.SERVER;
        }

        for (IChatListener listener : chatListeners)
        {
            chat = s.isClient() ? listener.clientChat(handler, chat) : listener.serverChat(handler, chat);
        }

        return chat;
    }
    public void handleTinyPacket(NetHandler handler, Packet131MapData mapData)
    {
        NetworkModHandler nmh = FMLNetworkHandler.instance().findNetworkModHandler((int)mapData.itemID);

        if (nmh == null)
        {
            FMLLog.info("Received a tiny packet for network id %d that is not recognised here", mapData.itemID);
            return;
        }

        if (nmh.hasTinyPacketHandler())
        {
            nmh.getTinyPacketHandler().handle(handler, mapData);
        }
        else
        {
            FMLLog.info("Received a tiny packet for a network mod that does not accept tiny packets %s", nmh.getContainer().getModId());
        }
    }
}
