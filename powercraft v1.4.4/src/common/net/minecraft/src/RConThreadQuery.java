package net.minecraft.src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class RConThreadQuery extends RConThreadBase
{
    private long lastAuthCheckTime;

    private int queryPort;

    private int serverPort;

    private int maxPlayers;

    private String serverMotd;

    private String worldName;

    private DatagramSocket querySocket = null;

    private byte[] buffer = new byte[1460];

    private DatagramPacket incomingPacket = null;
    private Map field_72644_p;

    private String queryHostname;

    private String serverHostname;

    private Map queryClients;

    private long time;

    private RConOutputStream output;

    private long lastQueryResponseTime;

    public RConThreadQuery(IServer par1IServer)
    {
        super(par1IServer);
        this.queryPort = par1IServer.getIntProperty("query.port", 0);
        this.serverHostname = par1IServer.getHostname();
        this.serverPort = par1IServer.getPort();
        this.serverMotd = par1IServer.getServerMOTD();
        this.maxPlayers = par1IServer.getMaxPlayers();
        this.worldName = par1IServer.getFolderName();
        this.lastQueryResponseTime = 0L;
        this.queryHostname = "0.0.0.0";

        if (0 != this.serverHostname.length() && !this.queryHostname.equals(this.serverHostname))
        {
            this.queryHostname = this.serverHostname;
        }
        else
        {
            this.serverHostname = "0.0.0.0";

            try
            {
                InetAddress var2 = InetAddress.getLocalHost();
                this.queryHostname = var2.getHostAddress();
            }
            catch (UnknownHostException var3)
            {
                this.logWarning("Unable to determine local host IP, please set server-ip in \'" + par1IServer.getSettingsFilePath() + "\' : " + var3.getMessage());
            }
        }

        if (0 == this.queryPort)
        {
            this.queryPort = this.serverPort;
            this.logInfo("Setting default query port to " + this.queryPort);
            par1IServer.setProperty("query.port", Integer.valueOf(this.queryPort));
            par1IServer.setProperty("debug", Boolean.valueOf(false));
            par1IServer.saveProperties();
        }

        this.field_72644_p = new HashMap();
        this.output = new RConOutputStream(1460);
        this.queryClients = new HashMap();
        this.time = (new Date()).getTime();
    }

    private void sendResponsePacket(byte[] par1ArrayOfByte, DatagramPacket par2DatagramPacket) throws IOException
    {
        this.querySocket.send(new DatagramPacket(par1ArrayOfByte, par1ArrayOfByte.length, par2DatagramPacket.getSocketAddress()));
    }

    private boolean parseIncomingPacket(DatagramPacket par1DatagramPacket) throws IOException
    {
        byte[] var2 = par1DatagramPacket.getData();
        int var3 = par1DatagramPacket.getLength();
        SocketAddress var4 = par1DatagramPacket.getSocketAddress();
        this.logDebug("Packet len " + var3 + " [" + var4 + "]");

        if (3 <= var3 && -2 == var2[0] && -3 == var2[1])
        {
            this.logDebug("Packet \'" + RConUtils.getByteAsHexString(var2[2]) + "\' [" + var4 + "]");

            switch (var2[2])
            {
                case 0:
                    if (!this.verifyClientAuth(par1DatagramPacket).booleanValue())
                    {
                        this.logDebug("Invalid challenge [" + var4 + "]");
                        return false;
                    }
                    else if (15 == var3)
                    {
                        this.sendResponsePacket(this.createQueryResponse(par1DatagramPacket), par1DatagramPacket);
                        this.logDebug("Rules [" + var4 + "]");
                    }
                    else
                    {
                        RConOutputStream var5 = new RConOutputStream(1460);
                        var5.writeInt(0);
                        var5.writeByteArray(this.getRequestId(par1DatagramPacket.getSocketAddress()));
                        var5.writeString(this.serverMotd);
                        var5.writeString("SMP");
                        var5.writeString(this.worldName);
                        var5.writeString(Integer.toString(this.getNumberOfPlayers()));
                        var5.writeString(Integer.toString(this.maxPlayers));
                        var5.writeShort((short)this.serverPort);
                        var5.writeString(this.queryHostname);
                        this.sendResponsePacket(var5.toByteArray(), par1DatagramPacket);
                        this.logDebug("Status [" + var4 + "]");
                    }

                case 9:
                    this.sendAuthChallenge(par1DatagramPacket);
                    this.logDebug("Challenge [" + var4 + "]");
                    return true;

                default:
                    return true;
            }
        }
        else
        {
            this.logDebug("Invalid packet [" + var4 + "]");
            return false;
        }
    }

    private byte[] createQueryResponse(DatagramPacket par1DatagramPacket) throws IOException
    {
        long var2 = System.currentTimeMillis();

        if (var2 < this.lastQueryResponseTime + 5000L)
        {
            byte[] var7 = this.output.toByteArray();
            byte[] var8 = this.getRequestId(par1DatagramPacket.getSocketAddress());
            var7[1] = var8[0];
            var7[2] = var8[1];
            var7[3] = var8[2];
            var7[4] = var8[3];
            return var7;
        }
        else
        {
            this.lastQueryResponseTime = var2;
            this.output.reset();
            this.output.writeInt(0);
            this.output.writeByteArray(this.getRequestId(par1DatagramPacket.getSocketAddress()));
            this.output.writeString("splitnum");
            this.output.writeInt(128);
            this.output.writeInt(0);
            this.output.writeString("hostname");
            this.output.writeString(this.serverMotd);
            this.output.writeString("gametype");
            this.output.writeString("SMP");
            this.output.writeString("game_id");
            this.output.writeString("MINECRAFT");
            this.output.writeString("version");
            this.output.writeString(this.server.getMinecraftVersion());
            this.output.writeString("plugins");
            this.output.writeString(this.server.getPlugins());
            this.output.writeString("map");
            this.output.writeString(this.worldName);
            this.output.writeString("numplayers");
            this.output.writeString("" + this.getNumberOfPlayers());
            this.output.writeString("maxplayers");
            this.output.writeString("" + this.maxPlayers);
            this.output.writeString("hostport");
            this.output.writeString("" + this.serverPort);
            this.output.writeString("hostip");
            this.output.writeString(this.queryHostname);
            this.output.writeInt(0);
            this.output.writeInt(1);
            this.output.writeString("player_");
            this.output.writeInt(0);
            String[] var4 = this.server.getAllUsernames();
            byte var5 = (byte)var4.length;

            for (byte var6 = (byte)(var5 - 1); var6 >= 0; --var6)
            {
                this.output.writeString(var4[var6]);
            }

            this.output.writeInt(0);
            return this.output.toByteArray();
        }
    }

    private byte[] getRequestId(SocketAddress par1SocketAddress)
    {
        return ((RConThreadQueryAuth)this.queryClients.get(par1SocketAddress)).getRequestId();
    }

    private Boolean verifyClientAuth(DatagramPacket par1DatagramPacket)
    {
        SocketAddress var2 = par1DatagramPacket.getSocketAddress();

        if (!this.queryClients.containsKey(var2))
        {
            return Boolean.valueOf(false);
        }
        else
        {
            byte[] var3 = par1DatagramPacket.getData();
            return ((RConThreadQueryAuth)this.queryClients.get(var2)).getRandomChallenge() != RConUtils.getBytesAsBEint(var3, 7, par1DatagramPacket.getLength()) ? Boolean.valueOf(false) : Boolean.valueOf(true);
        }
    }

    private void sendAuthChallenge(DatagramPacket par1DatagramPacket) throws IOException
    {
        RConThreadQueryAuth var2 = new RConThreadQueryAuth(this, par1DatagramPacket);
        this.queryClients.put(par1DatagramPacket.getSocketAddress(), var2);
        this.sendResponsePacket(var2.getChallengeValue(), par1DatagramPacket);
    }

    private void cleanQueryClientsMap()
    {
        if (this.running)
        {
            long var1 = System.currentTimeMillis();

            if (var1 >= this.lastAuthCheckTime + 30000L)
            {
                this.lastAuthCheckTime = var1;
                Iterator var3 = this.queryClients.entrySet().iterator();

                while (var3.hasNext())
                {
                    Entry var4 = (Entry)var3.next();

                    if (((RConThreadQueryAuth)var4.getValue()).hasExpired(var1).booleanValue())
                    {
                        var3.remove();
                    }
                }
            }
        }
    }

    public void run()
    {
        this.logInfo("Query running on " + this.serverHostname + ":" + this.queryPort);
        this.lastAuthCheckTime = System.currentTimeMillis();
        this.incomingPacket = new DatagramPacket(this.buffer, this.buffer.length);

        try
        {
            while (this.running)
            {
                try
                {
                    this.querySocket.receive(this.incomingPacket);
                    this.cleanQueryClientsMap();
                    this.parseIncomingPacket(this.incomingPacket);
                }
                catch (SocketTimeoutException var7)
                {
                    this.cleanQueryClientsMap();
                }
                catch (PortUnreachableException var8)
                {
                    ;
                }
                catch (IOException var9)
                {
                    this.stopWithException(var9);
                }
            }
        }
        finally
        {
            this.closeAllSockets();
        }
    }

    public void startThread()
    {
        if (!this.running)
        {
            if (0 < this.queryPort && 65535 >= this.queryPort)
            {
                if (this.initQuerySystem())
                {
                    super.startThread();
                }
            }
            else
            {
                this.logWarning("Invalid query port " + this.queryPort + " found in \'" + this.server.getSettingsFilePath() + "\' (queries disabled)");
            }
        }
    }

    private void stopWithException(Exception par1Exception)
    {
        if (this.running)
        {
            this.logWarning("Unexpected exception, buggy JRE? (" + par1Exception.toString() + ")");

            if (!this.initQuerySystem())
            {
                this.logSevere("Failed to recover from buggy JRE, shutting down!");
                this.running = false;
            }
        }
    }

    private boolean initQuerySystem()
    {
        try
        {
            this.querySocket = new DatagramSocket(this.queryPort, InetAddress.getByName(this.serverHostname));
            this.registerSocket(this.querySocket);
            this.querySocket.setSoTimeout(500);
            return true;
        }
        catch (SocketException var2)
        {
            this.logWarning("Unable to initialise query system on " + this.serverHostname + ":" + this.queryPort + " (Socket): " + var2.getMessage());
        }
        catch (UnknownHostException var3)
        {
            this.logWarning("Unable to initialise query system on " + this.serverHostname + ":" + this.queryPort + " (Unknown Host): " + var3.getMessage());
        }
        catch (Exception var4)
        {
            this.logWarning("Unable to initialise query system on " + this.serverHostname + ":" + this.queryPort + " (E): " + var4.getMessage());
        }

        return false;
    }
}
