package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ArgsWrapper;
import cpw.mods.fml.relauncher.FMLRelauncher;
import net.minecraft.src.AnvilSaveConverter;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.BehaviorArrowDispense;
import net.minecraft.src.BehaviorBucketEmptyDispense;
import net.minecraft.src.BehaviorBucketFullDispense;
import net.minecraft.src.BehaviorDispenseBoat;
import net.minecraft.src.BehaviorDispenseFireball;
import net.minecraft.src.BehaviorDispenseMinecart;
import net.minecraft.src.BehaviorEggDispense;
import net.minecraft.src.BehaviorExpBottleDispense;
import net.minecraft.src.BehaviorMobEggDispense;
import net.minecraft.src.BehaviorPotionDispense;
import net.minecraft.src.BehaviorSnowballDispense;
import net.minecraft.src.BlockDispenser;
import net.minecraft.src.CallableIsServerModded;
import net.minecraft.src.CallableServerMemoryStats;
import net.minecraft.src.CallableServerProfiler;
import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.CommandBase;
import net.minecraft.src.ConvertingProgressUpdate;
import net.minecraft.src.CrashReport;
import net.minecraft.src.DedicatedServer;
import net.minecraft.src.DemoWorldServer;
import net.minecraft.src.EnumGameType;
import net.minecraft.src.ICommandManager;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.IPlayerUsage;
import net.minecraft.src.IProgressUpdate;
import net.minecraft.src.ISaveFormat;
import net.minecraft.src.ISaveHandler;
import net.minecraft.src.IUpdatePlayerListBox;
import net.minecraft.src.Item;
import net.minecraft.src.MathHelper;
import net.minecraft.src.MinecraftException;
import net.minecraft.src.NetworkListenThread;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet4UpdateTime;
import net.minecraft.src.PlayerUsageSnooper;
import net.minecraft.src.Profiler;
import net.minecraft.src.RConConsoleSource;
import net.minecraft.src.ReportedException;
import net.minecraft.src.ServerCommandManager;
import net.minecraft.src.ServerConfigurationManager;
import net.minecraft.src.StatList;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.StringUtils;
import net.minecraft.src.ThreadDedicatedServer;
import net.minecraft.src.ThreadMinecraftServer;
import net.minecraft.src.WorldInfo;
import net.minecraft.src.WorldManager;
import net.minecraft.src.WorldServer;
import net.minecraft.src.WorldServerMulti;
import net.minecraft.src.WorldSettings;
import net.minecraft.src.WorldType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

public abstract class MinecraftServer implements ICommandSender, Runnable, IPlayerUsage
{
    public static Logger logger = Logger.getLogger("Minecraft");

    private static MinecraftServer mcServer = null;
    private final ISaveFormat anvilConverterForAnvilFile;

    private final PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("server", this);
    private final File anvilFile;

    private final List tickables = new ArrayList();
    private final ICommandManager commandManager;
    public final Profiler theProfiler = new Profiler();

    private String hostname;

    private int serverPort = -1;

    public WorldServer[] worldServers;

    private ServerConfigurationManager serverConfigManager;

    private boolean serverRunning = true;

    private boolean serverStopped = false;

    private int tickCounter = 0;

    public String currentTask;

    public int percentDone;

    private boolean onlineMode;

    private boolean canSpawnAnimals;
    private boolean canSpawnNPCs;

    private boolean pvpEnabled;

    private boolean allowFlight;

    private String motd;

    private int buildLimit;
    private long lastSentPacketID;
    private long lastSentPacketSize;
    private long lastReceivedID;
    private long lastReceivedSize;
    public final long[] sentPacketCountArray = new long[100];
    public final long[] sentPacketSizeArray = new long[100];
    public final long[] receivedPacketCountArray = new long[100];
    public final long[] receivedPacketSizeArray = new long[100];
    public final long[] tickTimeArray = new long[100];

    public Hashtable<Integer, long[]> worldTickTimes = new Hashtable<Integer, long[]>();
    private KeyPair serverKeyPair;

    private String serverOwner;
    private String folderName;
    @SideOnly(Side.CLIENT)
    private String worldName;
    private boolean isDemo;
    private boolean enableBonusChest;

    private boolean worldIsBeingDeleted;
    private String texturePack = "";
    private boolean serverIsRunning = false;

    private long timeOfLastWarning;
    private String userMessage;
    private boolean startProfiling;

    public MinecraftServer(File par1File)
    {
        mcServer = this;
        this.anvilFile = par1File;
        this.commandManager = new ServerCommandManager();
        this.anvilConverterForAnvilFile = new AnvilSaveConverter(par1File);
        this.registerDispenseBehaviors();
    }

    private void registerDispenseBehaviors()
    {
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.arrow, new BehaviorArrowDispense(this));
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.egg, new BehaviorEggDispense(this));
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.snowball, new BehaviorSnowballDispense(this));
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.expBottle, new BehaviorExpBottleDispense(this));
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.potion, new BehaviorPotionDispense(this));
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.monsterPlacer, new BehaviorMobEggDispense(this));
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.fireballCharge, new BehaviorDispenseFireball(this));
        BehaviorDispenseMinecart var1 = new BehaviorDispenseMinecart(this);
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.minecartEmpty, var1);
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.minecartCrate, var1);
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.minecartPowered, var1);
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.boat, new BehaviorDispenseBoat(this));
        BehaviorBucketFullDispense var2 = new BehaviorBucketFullDispense(this);
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.bucketLava, var2);
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.bucketWater, var2);
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.bucketEmpty, new BehaviorBucketEmptyDispense(this));
    }

    protected abstract boolean startServer() throws IOException;

    protected void convertMapIfNeeded(String par1Str)
    {
        if (this.getActiveAnvilConverter().isOldMapFormat(par1Str))
        {
            logger.info("Converting map!");
            this.setUserMessage("menu.convertingLevel");
            this.getActiveAnvilConverter().convertMapFormat(par1Str, new ConvertingProgressUpdate(this));
        }
    }

    protected synchronized void setUserMessage(String par1Str)
    {
        this.userMessage = par1Str;
    }

    @SideOnly(Side.CLIENT)

    public synchronized String getUserMessage()
    {
        return this.userMessage;
    }

    protected void loadAllWorlds(String par1Str, String par2Str, long par3, WorldType par5WorldType, String par6Str)
    {
        this.convertMapIfNeeded(par1Str);
        this.setUserMessage("menu.loadingLevel");
        ISaveHandler var7 = this.anvilConverterForAnvilFile.getSaveLoader(par1Str, true);
        WorldInfo var9 = var7.loadWorldInfo();
        WorldSettings var8;

        if (var9 == null)
        {
            var8 = new WorldSettings(par3, this.getGameType(), this.canStructuresSpawn(), this.isHardcore(), par5WorldType);
            var8.func_82750_a(par6Str);
        }
        else
        {
            var8 = new WorldSettings(var9);
        }

        if (this.enableBonusChest)
        {
            var8.enableBonusChest();
        }

        WorldServer overWorld = (isDemo() ? new DemoWorldServer(this, var7, par2Str, 0, theProfiler) : new WorldServer(this, var7, par2Str, 0, var8, theProfiler));

        for (int dim : DimensionManager.getStaticDimensionIDs())
        {
            WorldServer world = (dim == 0 ? overWorld : new WorldServerMulti(this, var7, par2Str, dim, var8, overWorld, theProfiler));
            world.addWorldAccess(new WorldManager(this, world));

            if (!this.isSinglePlayer())
            {
                world.getWorldInfo().setGameType(this.getGameType());
            }

            this.serverConfigManager.setPlayerManager(this.worldServers);
            MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(world));
        }

        this.serverConfigManager.setPlayerManager(new WorldServer[] { overWorld });
        this.setDifficultyForAllWorlds(this.getDifficulty());
        this.initialWorldChunkLoad();
    }

    protected void initialWorldChunkLoad()
    {
        int var5 = 0;
        this.setUserMessage("menu.generatingTerrain");
        byte var6 = 0;
        logger.info("Preparing start region for level " + var6);
        WorldServer var7 = this.worldServers[var6];
        ChunkCoordinates var8 = var7.getSpawnPoint();
        long var9 = System.currentTimeMillis();

        for (int var11 = -192; var11 <= 192 && this.isServerRunning(); var11 += 16)
        {
            for (int var12 = -192; var12 <= 192 && this.isServerRunning(); var12 += 16)
            {
                long var13 = System.currentTimeMillis();

                if (var13 - var9 > 1000L)
                {
                    this.outputPercentRemaining("Preparing spawn area", var5 * 100 / 625);
                    var9 = var13;
                }

                ++var5;
                var7.theChunkProviderServer.loadChunk(var8.posX + var11 >> 4, var8.posZ + var12 >> 4);
            }
        }

        this.clearCurrentTask();
    }

    public abstract boolean canStructuresSpawn();

    public abstract EnumGameType getGameType();

    public abstract int getDifficulty();

    public abstract boolean isHardcore();

    protected void outputPercentRemaining(String par1Str, int par2)
    {
        this.currentTask = par1Str;
        this.percentDone = par2;
        logger.info(par1Str + ": " + par2 + "%");
    }

    protected void clearCurrentTask()
    {
        this.currentTask = null;
        this.percentDone = 0;
    }

    protected void saveAllWorlds(boolean par1)
    {
        if (!this.worldIsBeingDeleted)
        {
            WorldServer[] var2 = this.worldServers;
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4)
            {
                WorldServer var5 = var2[var4];

                if (var5 != null)
                {
                    if (!par1)
                    {
                        logger.info("Saving chunks for level \'" + var5.getWorldInfo().getWorldName() + "\'/" + var5.provider.getDimensionName());
                    }

                    try
                    {
                        var5.saveAllChunks(true, (IProgressUpdate)null);
                    }
                    catch (MinecraftException var7)
                    {
                        logger.warning(var7.getMessage());
                    }
                }
            }
        }
    }

    public void stopServer()
    {
        if (!this.worldIsBeingDeleted)
        {
            logger.info("Stopping server");

            if (this.getNetworkThread() != null)
            {
                this.getNetworkThread().stopListening();
            }

            if (this.serverConfigManager != null)
            {
                logger.info("Saving players");
                this.serverConfigManager.saveAllPlayerData();
                this.serverConfigManager.removeAllPlayers();
            }

            logger.info("Saving worlds");
            this.saveAllWorlds(false);

            for (int var1 = 0; var1 < this.worldServers.length; ++var1)
            {
                WorldServer var2 = this.worldServers[var1];
                MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(var2));
                var2.flush();
                DimensionManager.setWorld(var2.provider.dimensionId, null);
            }

            if (this.usageSnooper != null && this.usageSnooper.isSnooperRunning())
            {
                this.usageSnooper.stopSnooper();
            }
        }
    }

    public String getServerHostname()
    {
        return this.hostname;
    }

    public void setHostname(String par1Str)
    {
        this.hostname = par1Str;
    }

    public boolean isServerRunning()
    {
        return this.serverRunning;
    }

    public void initiateShutdown()
    {
        this.serverRunning = false;
    }

    public void run()
    {
        try
        {
            if (this.startServer())
            {
                FMLCommonHandler.instance().handleServerStarted();
                long var1 = System.currentTimeMillis();
                FMLCommonHandler.instance().onWorldLoadTick(worldServers);

                for (long var50 = 0L; this.serverRunning; this.serverIsRunning = true)
                {
                    long var5 = System.currentTimeMillis();
                    long var7 = var5 - var1;

                    if (var7 > 2000L && var1 - this.timeOfLastWarning >= 15000L)
                    {
                        logger.warning("Can\'t keep up! Did the system time change, or is the server overloaded?");
                        var7 = 2000L;
                        this.timeOfLastWarning = var1;
                    }

                    if (var7 < 0L)
                    {
                        logger.warning("Time ran backwards! Did the system time change?");
                        var7 = 0L;
                    }

                    var50 += var7;
                    var1 = var5;

                    if (this.worldServers[0].areAllPlayersAsleep())
                    {
                        this.tick();
                        var50 = 0L;
                    }
                    else
                    {
                        while (var50 > 50L)
                        {
                            var50 -= 50L;
                            this.tick();
                        }
                    }

                    Thread.sleep(1L);
                }

                FMLCommonHandler.instance().handleServerStopping();
            }
            else
            {
                this.finalTick((CrashReport)null);
            }
        }
        catch (Throwable var48)
        {
            var48.printStackTrace();
            logger.log(Level.SEVERE, "Encountered an unexpected exception " + var48.getClass().getSimpleName(), var48);
            CrashReport var2 = null;

            if (var48 instanceof ReportedException)
            {
                var2 = this.addServerInfoToCrashReport(((ReportedException)var48).getTheReportedExceptionCrashReport());
            }
            else
            {
                var2 = this.addServerInfoToCrashReport(new CrashReport("Exception in server tick loop", var48));
            }

            File var3 = new File(new File(this.getDataDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");

            if (var2.saveToFile(var3))
            {
                logger.severe("This crash report has been saved to: " + var3.getAbsolutePath());
            }
            else
            {
                logger.severe("We were unable to save this crash report to disk.");
            }

            this.finalTick(var2);
        }
        finally
        {
            try
            {
                this.stopServer();
                this.serverStopped = true;
            }
            catch (Throwable var46)
            {
                var46.printStackTrace();
            }
            finally
            {
                this.systemExitNow();
            }
        }
    }

    protected File getDataDirectory()
    {
        return new File(".");
    }

    protected void finalTick(CrashReport par1CrashReport) {}

    protected void systemExitNow() {}

    public void tick()
    {
        FMLCommonHandler.instance().rescheduleTicks(Side.SERVER);
        long var1 = System.nanoTime();
        AxisAlignedBB.getAABBPool().cleanPool();
        FMLCommonHandler.instance().onPreServerTick();
        ++this.tickCounter;

        if (this.startProfiling)
        {
            this.startProfiling = false;
            this.theProfiler.profilingEnabled = true;
            this.theProfiler.clearProfiling();
        }

        this.theProfiler.startSection("root");
        this.updateTimeLightAndEntities();

        if (this.tickCounter % 900 == 0)
        {
            this.theProfiler.startSection("save");
            this.serverConfigManager.saveAllPlayerData();
            this.saveAllWorlds(true);
            this.theProfiler.endSection();
        }

        this.theProfiler.startSection("tallying");
        this.tickTimeArray[this.tickCounter % 100] = System.nanoTime() - var1;
        this.sentPacketCountArray[this.tickCounter % 100] = Packet.sentID - this.lastSentPacketID;
        this.lastSentPacketID = Packet.sentID;
        this.sentPacketSizeArray[this.tickCounter % 100] = Packet.sentSize - this.lastSentPacketSize;
        this.lastSentPacketSize = Packet.sentSize;
        this.receivedPacketCountArray[this.tickCounter % 100] = Packet.receivedID - this.lastReceivedID;
        this.lastReceivedID = Packet.receivedID;
        this.receivedPacketSizeArray[this.tickCounter % 100] = Packet.receivedSize - this.lastReceivedSize;
        this.lastReceivedSize = Packet.receivedSize;
        this.theProfiler.endSection();
        this.theProfiler.startSection("snooper");

        if (!this.usageSnooper.isSnooperRunning() && this.tickCounter > 100)
        {
            this.usageSnooper.startSnooper();
        }

        if (this.tickCounter % 6000 == 0)
        {
            this.usageSnooper.addMemoryStatsToSnooper();
        }

        this.theProfiler.endSection();
        this.theProfiler.endSection();
        FMLCommonHandler.instance().onPostServerTick();
    }

    public void updateTimeLightAndEntities()
    {
        this.theProfiler.startSection("levels");
        int var1;
        Integer[] ids = DimensionManager.getIDs();

        for (int x = 0; x < ids.length; x++)
        {
            int id = ids[x];
            long var2 = System.nanoTime();

            if (id == 0 || this.getAllowNether())
            {
                WorldServer var4 = DimensionManager.getWorld(id);
                this.theProfiler.startSection(var4.getWorldInfo().getWorldName());
                this.theProfiler.startSection("pools");
                var4.getWorldVec3Pool().clear();
                this.theProfiler.endSection();

                if (this.tickCounter % 20 == 0)
                {
                    this.theProfiler.startSection("timeSync");
                    this.serverConfigManager.sendPacketToAllPlayersInDimension(new Packet4UpdateTime(var4.getTotalWorldTime(), var4.getWorldTime()), var4.provider.dimensionId);
                    this.theProfiler.endSection();
                }

                this.theProfiler.startSection("tick");
                FMLCommonHandler.instance().onPreWorldTick(var4);
                CrashReport var6;

                try
                {
                    var4.tick();
                }
                catch (Throwable var8)
                {
                    var6 = CrashReport.func_85055_a(var8, "Exception ticking world");
                    var4.addWorldInfoToCrashReport(var6);
                    throw new ReportedException(var6);
                }

                try
                {
                    var4.updateEntities();
                }
                catch (Throwable var7)
                {
                    var6 = CrashReport.func_85055_a(var7, "Exception ticking world entities");
                    var4.addWorldInfoToCrashReport(var6);
                    throw new ReportedException(var6);
                }

                FMLCommonHandler.instance().onPostWorldTick(var4);
                this.theProfiler.endSection();
                this.theProfiler.startSection("tracker");
                var4.getEntityTracker().updateTrackedEntities();
                this.theProfiler.endSection();
                this.theProfiler.endSection();
            }

            worldTickTimes.get(id)[this.tickCounter % 100] = System.nanoTime() - var2;
        }

        this.theProfiler.endStartSection("dim_unloading");
        DimensionManager.unloadWorlds(worldTickTimes);
        this.theProfiler.endStartSection("connection");
        this.getNetworkThread().networkTick();
        this.theProfiler.endStartSection("players");
        this.serverConfigManager.sendPlayerInfoToAllPlayers();
        this.theProfiler.endStartSection("tickables");

        for (var1 = 0; var1 < this.tickables.size(); ++var1)
        {
            ((IUpdatePlayerListBox)this.tickables.get(var1)).update();
        }

        this.theProfiler.endSection();
    }

    public boolean getAllowNether()
    {
        return true;
    }

    public void startServerThread()
    {
        (new ThreadMinecraftServer(this, "Server thread")).start();
    }

    public File getFile(String par1Str)
    {
        return new File(this.getDataDirectory(), par1Str);
    }

    public void logInfo(String par1Str)
    {
        logger.info(par1Str);
    }

    public void logWarning(String par1Str)
    {
        logger.warning(par1Str);
    }

    public WorldServer worldServerForDimension(int par1)
    {
        WorldServer ret = DimensionManager.getWorld(par1);

        if (ret == null)
        {
            DimensionManager.initDimension(par1);
            ret = DimensionManager.getWorld(par1);
        }

        return ret;
    }

    @SideOnly(Side.SERVER)
    public void func_82010_a(IUpdatePlayerListBox par1IUpdatePlayerListBox)
    {
        this.tickables.add(par1IUpdatePlayerListBox);
    }

    public String getHostname()
    {
        return this.hostname;
    }

    public int getPort()
    {
        return this.serverPort;
    }

    public String getServerMOTD()
    {
        return this.motd;
    }

    public String getMinecraftVersion()
    {
        return "1.4.4";
    }

    public int getCurrentPlayerCount()
    {
        return this.serverConfigManager.getCurrentPlayerCount();
    }

    public int getMaxPlayers()
    {
        return this.serverConfigManager.getMaxPlayers();
    }

    public String[] getAllUsernames()
    {
        return this.serverConfigManager.getAllUsernames();
    }

    public String getPlugins()
    {
        return "";
    }

    public String executeCommand(String par1Str)
    {
        RConConsoleSource.consoleBuffer.resetLog();
        this.commandManager.executeCommand(RConConsoleSource.consoleBuffer, par1Str);
        return RConConsoleSource.consoleBuffer.getChatBuffer();
    }

    public boolean isDebuggingEnabled()
    {
        return false;
    }

    public void logSevere(String par1Str)
    {
        logger.log(Level.SEVERE, par1Str);
    }

    public void logDebug(String par1Str)
    {
        if (this.isDebuggingEnabled())
        {
            logger.log(Level.INFO, par1Str);
        }
    }

    public String getServerModName()
    {
        return "forge,fml";
    }

    public CrashReport addServerInfoToCrashReport(CrashReport par1CrashReport)
    {
        par1CrashReport.func_85056_g().addCrashSectionCallable("Profiler Position", new CallableIsServerModded(this));

        if (this.worldServers != null && this.worldServers.length > 0 && this.worldServers[0] != null)
        {
            par1CrashReport.func_85056_g().addCrashSectionCallable("Vec3 Pool Size", new CallableServerProfiler(this));
        }

        if (this.serverConfigManager != null)
        {
            par1CrashReport.func_85056_g().addCrashSectionCallable("Player Count", new CallableServerMemoryStats(this));
        }

        return par1CrashReport;
    }

    public List getPossibleCompletions(ICommandSender par1ICommandSender, String par2Str)
    {
        ArrayList var3 = new ArrayList();

        if (par2Str.startsWith("/"))
        {
            par2Str = par2Str.substring(1);
            boolean var10 = !par2Str.contains(" ");
            List var11 = this.commandManager.getPossibleCommands(par1ICommandSender, par2Str);

            if (var11 != null)
            {
                Iterator var12 = var11.iterator();

                while (var12.hasNext())
                {
                    String var13 = (String)var12.next();

                    if (var10)
                    {
                        var3.add("/" + var13);
                    }
                    else
                    {
                        var3.add(var13);
                    }
                }
            }

            return var3;
        }
        else
        {
            String[] var4 = par2Str.split(" ", -1);
            String var5 = var4[var4.length - 1];
            String[] var6 = this.serverConfigManager.getAllUsernames();
            int var7 = var6.length;

            for (int var8 = 0; var8 < var7; ++var8)
            {
                String var9 = var6[var8];

                if (CommandBase.doesStringStartWith(var5, var9))
                {
                    var3.add(var9);
                }
            }

            return var3;
        }
    }

    public static MinecraftServer getServer()
    {
        return mcServer;
    }

    public String getCommandSenderName()
    {
        return "Server";
    }

    public void sendChatToPlayer(String par1Str)
    {
        logger.info(StringUtils.stripControlCodes(par1Str));
    }

    public boolean canCommandSenderUseCommand(int par1, String par2Str)
    {
        return true;
    }

    public String translateString(String par1Str, Object ... par2ArrayOfObj)
    {
        return StringTranslate.getInstance().translateKeyFormat(par1Str, par2ArrayOfObj);
    }

    public ICommandManager getCommandManager()
    {
        return this.commandManager;
    }

    public KeyPair getKeyPair()
    {
        return this.serverKeyPair;
    }

    public int getServerPort()
    {
        return this.serverPort;
    }

    public void setServerPort(int par1)
    {
        this.serverPort = par1;
    }

    public String getServerOwner()
    {
        return this.serverOwner;
    }

    public void setServerOwner(String par1Str)
    {
        this.serverOwner = par1Str;
    }

    public boolean isSinglePlayer()
    {
        return this.serverOwner != null;
    }

    public String getFolderName()
    {
        return this.folderName;
    }

    public void setFolderName(String par1Str)
    {
        this.folderName = par1Str;
    }

    @SideOnly(Side.CLIENT)
    public void setWorldName(String par1Str)
    {
        this.worldName = par1Str;
    }

    @SideOnly(Side.CLIENT)
    public String getWorldName()
    {
        return this.worldName;
    }

    public void setKeyPair(KeyPair par1KeyPair)
    {
        this.serverKeyPair = par1KeyPair;
    }

    public void setDifficultyForAllWorlds(int par1)
    {
        for (int var2 = 0; var2 < this.worldServers.length; ++var2)
        {
            WorldServer var3 = this.worldServers[var2];

            if (var3 != null)
            {
                if (var3.getWorldInfo().isHardcoreModeEnabled())
                {
                    var3.difficultySetting = 3;
                    var3.setAllowedSpawnTypes(true, true);
                }
                else if (this.isSinglePlayer())
                {
                    var3.difficultySetting = par1;
                    var3.setAllowedSpawnTypes(var3.difficultySetting > 0, true);
                }
                else
                {
                    var3.difficultySetting = par1;
                    var3.setAllowedSpawnTypes(this.allowSpawnMonsters(), this.canSpawnAnimals);
                }
            }
        }
    }

    protected boolean allowSpawnMonsters()
    {
        return true;
    }

    public boolean isDemo()
    {
        return this.isDemo;
    }

    public void setDemo(boolean par1)
    {
        this.isDemo = par1;
    }

    public void canCreateBonusChest(boolean par1)
    {
        this.enableBonusChest = par1;
    }

    public ISaveFormat getActiveAnvilConverter()
    {
        return this.anvilConverterForAnvilFile;
    }

    public void deleteWorldAndStopServer()
    {
        this.worldIsBeingDeleted = true;
        this.getActiveAnvilConverter().flushCache();

        for (int var1 = 0; var1 < this.worldServers.length; ++var1)
        {
            WorldServer var2 = this.worldServers[var1];

            if (var2 != null)
            {
                MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(var2));
                var2.flush();
            }
        }

        this.getActiveAnvilConverter().deleteWorldDirectory(this.worldServers[0].getSaveHandler().getSaveDirectoryName());
        this.initiateShutdown();
    }

    public String getTexturePack()
    {
        return this.texturePack;
    }

    public void setTexturePack(String par1Str)
    {
        this.texturePack = par1Str;
    }

    public void addServerStatsToSnooper(PlayerUsageSnooper par1PlayerUsageSnooper)
    {
        par1PlayerUsageSnooper.addData("whitelist_enabled", Boolean.valueOf(false));
        par1PlayerUsageSnooper.addData("whitelist_count", Integer.valueOf(0));
        par1PlayerUsageSnooper.addData("players_current", Integer.valueOf(this.getCurrentPlayerCount()));
        par1PlayerUsageSnooper.addData("players_max", Integer.valueOf(this.getMaxPlayers()));
        par1PlayerUsageSnooper.addData("players_seen", Integer.valueOf(this.serverConfigManager.getAvailablePlayerDat().length));
        par1PlayerUsageSnooper.addData("uses_auth", Boolean.valueOf(this.onlineMode));
        par1PlayerUsageSnooper.addData("gui_state", this.getGuiEnabled() ? "enabled" : "disabled");
        par1PlayerUsageSnooper.addData("avg_tick_ms", Integer.valueOf((int)(MathHelper.average(this.tickTimeArray) * 1.0E-6D)));
        par1PlayerUsageSnooper.addData("avg_sent_packet_count", Integer.valueOf((int)MathHelper.average(this.sentPacketCountArray)));
        par1PlayerUsageSnooper.addData("avg_sent_packet_size", Integer.valueOf((int)MathHelper.average(this.sentPacketSizeArray)));
        par1PlayerUsageSnooper.addData("avg_rec_packet_count", Integer.valueOf((int)MathHelper.average(this.receivedPacketCountArray)));
        par1PlayerUsageSnooper.addData("avg_rec_packet_size", Integer.valueOf((int)MathHelper.average(this.receivedPacketSizeArray)));
        int var2 = 0;

        for (int var3 = 0; var3 < this.worldServers.length; ++var3)
        {
            if (this.worldServers[var3] != null)
            {
                WorldServer var4 = this.worldServers[var3];
                WorldInfo var5 = var4.getWorldInfo();
                par1PlayerUsageSnooper.addData("world[" + var2 + "][dimension]", Integer.valueOf(var4.provider.dimensionId));
                par1PlayerUsageSnooper.addData("world[" + var2 + "][mode]", var5.getGameType());
                par1PlayerUsageSnooper.addData("world[" + var2 + "][difficulty]", Integer.valueOf(var4.difficultySetting));
                par1PlayerUsageSnooper.addData("world[" + var2 + "][hardcore]", Boolean.valueOf(var5.isHardcoreModeEnabled()));
                par1PlayerUsageSnooper.addData("world[" + var2 + "][generator_name]", var5.getTerrainType().getWorldTypeName());
                par1PlayerUsageSnooper.addData("world[" + var2 + "][generator_version]", Integer.valueOf(var5.getTerrainType().getGeneratorVersion()));
                par1PlayerUsageSnooper.addData("world[" + var2 + "][height]", Integer.valueOf(this.buildLimit));
                par1PlayerUsageSnooper.addData("world[" + var2 + "][chunks_loaded]", Integer.valueOf(var4.getChunkProvider().getLoadedChunkCount()));
                ++var2;
            }
        }

        par1PlayerUsageSnooper.addData("worlds", Integer.valueOf(var2));
    }

    public void addServerTypeToSnooper(PlayerUsageSnooper par1PlayerUsageSnooper)
    {
        par1PlayerUsageSnooper.addData("singleplayer", Boolean.valueOf(this.isSinglePlayer()));
        par1PlayerUsageSnooper.addData("server_brand", this.getServerModName());
        par1PlayerUsageSnooper.addData("gui_supported", GraphicsEnvironment.isHeadless() ? "headless" : "supported");
        par1PlayerUsageSnooper.addData("dedicated", Boolean.valueOf(this.isDedicatedServer()));
    }

    public boolean isSnooperEnabled()
    {
        return true;
    }

    public int textureSize()
    {
        return 16;
    }

    public abstract boolean isDedicatedServer();

    public boolean isServerInOnlineMode()
    {
        return this.onlineMode;
    }

    public void setOnlineMode(boolean par1)
    {
        this.onlineMode = par1;
    }

    public boolean getCanSpawnAnimals()
    {
        return this.canSpawnAnimals;
    }

    public void setCanSpawnAnimals(boolean par1)
    {
        this.canSpawnAnimals = par1;
    }

    public boolean getCanSpawnNPCs()
    {
        return this.canSpawnNPCs;
    }

    public void setCanSpawnNPCs(boolean par1)
    {
        this.canSpawnNPCs = par1;
    }

    public boolean isPVPEnabled()
    {
        return this.pvpEnabled;
    }

    public void setAllowPvp(boolean par1)
    {
        this.pvpEnabled = par1;
    }

    public boolean isFlightAllowed()
    {
        return this.allowFlight;
    }

    public void setAllowFlight(boolean par1)
    {
        this.allowFlight = par1;
    }

    public abstract boolean isCommandBlockEnabled();

    public String getMOTD()
    {
        return this.motd;
    }

    public void setMOTD(String par1Str)
    {
        this.motd = par1Str;
    }

    public int getBuildLimit()
    {
        return this.buildLimit;
    }

    public void setBuildLimit(int par1)
    {
        this.buildLimit = par1;
    }

    public boolean isServerStopped()
    {
        return this.serverStopped;
    }

    public ServerConfigurationManager getConfigurationManager()
    {
        return this.serverConfigManager;
    }

    public void setConfigurationManager(ServerConfigurationManager par1ServerConfigurationManager)
    {
        this.serverConfigManager = par1ServerConfigurationManager;
    }

    public void setGameType(EnumGameType par1EnumGameType)
    {
        for (int var2 = 0; var2 < this.worldServers.length; ++var2)
        {
            getServer().worldServers[var2].getWorldInfo().setGameType(par1EnumGameType);
        }
    }

    public abstract NetworkListenThread getNetworkThread();

    @SideOnly(Side.CLIENT)
    public boolean serverIsInRunLoop()
    {
        return this.serverIsRunning;
    }

    public boolean getGuiEnabled()
    {
        return false;
    }

    public abstract String shareToLAN(EnumGameType var1, boolean var2);

    public int getTickCounter()
    {
        return this.tickCounter;
    }

    public void enableProfiling()
    {
        this.startProfiling = true;
    }

    @SideOnly(Side.CLIENT)
    public PlayerUsageSnooper getPlayerUsageSnooper()
    {
        return this.usageSnooper;
    }

    public ChunkCoordinates getPlayerCoordinates()
    {
        return new ChunkCoordinates(0, 0, 0);
    }

    public int getSpawnProtectionSize()
    {
        return 16;
    }

    public static ServerConfigurationManager getServerConfigurationManager(MinecraftServer par0MinecraftServer)
    {
        return par0MinecraftServer.serverConfigManager;
    }

    @SideOnly(Side.SERVER)
    public static void main(String[] par0ArrayOfStr)
    {
        FMLRelauncher.handleServerRelaunch(new ArgsWrapper(par0ArrayOfStr));
    }
    @SideOnly(Side.SERVER)
    public static void fmlReentry(ArgsWrapper wrap)
    {
        String[] par0ArrayOfStr = wrap.args;
        StatList.func_75919_a();

        try
        {
            boolean var1 = !GraphicsEnvironment.isHeadless();
            String var2 = null;
            String var3 = ".";
            String var4 = null;
            boolean var5 = false;
            boolean var6 = false;
            int var7 = -1;

            for (int var8 = 0; var8 < par0ArrayOfStr.length; ++var8)
            {
                String var9 = par0ArrayOfStr[var8];
                String var10 = var8 == par0ArrayOfStr.length - 1 ? null : par0ArrayOfStr[var8 + 1];
                boolean var11 = false;

                if (!var9.equals("nogui") && !var9.equals("--nogui"))
                {
                    if (var9.equals("--port") && var10 != null)
                    {
                        var11 = true;

                        try
                        {
                            var7 = Integer.parseInt(var10);
                        }
                        catch (NumberFormatException var13)
                        {
                            ;
                        }
                    }
                    else if (var9.equals("--singleplayer") && var10 != null)
                    {
                        var11 = true;
                        var2 = var10;
                    }
                    else if (var9.equals("--universe") && var10 != null)
                    {
                        var11 = true;
                        var3 = var10;
                    }
                    else if (var9.equals("--world") && var10 != null)
                    {
                        var11 = true;
                        var4 = var10;
                    }
                    else if (var9.equals("--demo"))
                    {
                        var5 = true;
                    }
                    else if (var9.equals("--bonusChest"))
                    {
                        var6 = true;
                    }
                }
                else
                {
                    var1 = false;
                }

                if (var11)
                {
                    ++var8;
                }
            }

            DedicatedServer var15 = new DedicatedServer(new File(var3));

            if (var2 != null)
            {
                var15.setServerOwner(var2);
            }

            if (var4 != null)
            {
                var15.setFolderName(var4);
            }

            if (var7 >= 0)
            {
                var15.setServerPort(var7);
            }

            if (var5)
            {
                var15.setDemo(true);
            }

            if (var6)
            {
                var15.canCreateBonusChest(true);
            }

            if (var1)
            {
                var15.func_82011_an();
            }

            var15.startServerThread();
            Runtime.getRuntime().addShutdownHook(new ThreadDedicatedServer(var15));
        }
        catch (Exception var14)
        {
            logger.log(Level.SEVERE, "Failed to start the minecraft server", var14);
        }
    }
}
