package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ChestGenHooks;
import static net.minecraftforge.common.ChestGenHooks.*;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

public class WorldServer extends World
{
    private final MinecraftServer mcServer;
    private final EntityTracker theEntityTracker;
    private final PlayerManager thePlayerManager;
    private Set field_73064_N;

    private TreeSet pendingTickListEntries;
    public ChunkProviderServer theChunkProviderServer;

    public boolean canNotSave;

    private boolean allPlayersSleeping;
    private int updateEntityTick = 0;
    private final Teleporter field_85177_Q;

    private ServerBlockEventList[] blockEventCache = new ServerBlockEventList[] {new ServerBlockEventList((ServerBlockEvent)null), new ServerBlockEventList((ServerBlockEvent)null)};

    private int blockEventCacheIndex = 0;
    public static final WeightedRandomChestContent[] bonusChestContent = new WeightedRandomChestContent[] {new WeightedRandomChestContent(Item.stick.shiftedIndex, 0, 1, 3, 10), new WeightedRandomChestContent(Block.planks.blockID, 0, 1, 3, 10), new WeightedRandomChestContent(Block.wood.blockID, 0, 1, 3, 10), new WeightedRandomChestContent(Item.axeStone.shiftedIndex, 0, 1, 1, 3), new WeightedRandomChestContent(Item.axeWood.shiftedIndex, 0, 1, 1, 5), new WeightedRandomChestContent(Item.pickaxeStone.shiftedIndex, 0, 1, 1, 3), new WeightedRandomChestContent(Item.pickaxeWood.shiftedIndex, 0, 1, 1, 5), new WeightedRandomChestContent(Item.appleRed.shiftedIndex, 0, 2, 3, 5), new WeightedRandomChestContent(Item.bread.shiftedIndex, 0, 2, 3, 3)};

    private IntHashMap entityIdMap;

    protected Set<ChunkCoordIntPair> doneChunks = new HashSet<ChunkCoordIntPair>();
    public List<Teleporter> customTeleporters = new ArrayList<Teleporter>();

    public WorldServer(MinecraftServer par1MinecraftServer, ISaveHandler par2ISaveHandler, String par3Str, int par4, WorldSettings par5WorldSettings, Profiler par6Profiler)
    {
        super(par2ISaveHandler, par3Str, par5WorldSettings, WorldProvider.getProviderForDimension(par4), par6Profiler);
        this.mcServer = par1MinecraftServer;
        this.theEntityTracker = new EntityTracker(this);
        this.thePlayerManager = new PlayerManager(this, par1MinecraftServer.getConfigurationManager().getViewDistance());

        if (this.entityIdMap == null)
        {
            this.entityIdMap = new IntHashMap();
        }

        if (this.field_73064_N == null)
        {
            this.field_73064_N = new HashSet();
        }

        if (this.pendingTickListEntries == null)
        {
            this.pendingTickListEntries = new TreeSet();
        }

        this.field_85177_Q = new Teleporter(this);
        DimensionManager.setWorld(par4, this);
    }

    public void tick()
    {
        super.tick();

        if (this.getWorldInfo().isHardcoreModeEnabled() && this.difficultySetting < 3)
        {
            this.difficultySetting = 3;
        }

        this.provider.worldChunkMgr.cleanupCache();

        if (this.areAllPlayersAsleep())
        {
            boolean var1 = false;

            if (this.spawnHostileMobs && this.difficultySetting >= 1)
            {
                ;
            }

            if (!var1)
            {
                long var2 = this.worldInfo.getWorldTime() + 24000L;
                this.worldInfo.setWorldTime(var2 - var2 % 24000L);
                this.wakeAllPlayers();
            }
        }

        this.theProfiler.startSection("mobSpawner");

        if (this.getGameRules().getGameRuleBooleanValue("doMobSpawning"))
        {
            SpawnerAnimals.findChunksForSpawning(this, this.spawnHostileMobs, this.spawnPeacefulMobs, this.worldInfo.getWorldTotalTime() % 400L == 0L);
        }

        this.theProfiler.endStartSection("chunkSource");
        this.chunkProvider.unload100OldestChunks();
        int var4 = this.calculateSkylightSubtracted(1.0F);

        if (var4 != this.skylightSubtracted)
        {
            this.skylightSubtracted = var4;
        }

        this.sendAndApplyBlockEvents();
        this.worldInfo.func_82572_b(this.worldInfo.getWorldTotalTime() + 1L);
        this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
        this.theProfiler.endStartSection("tickPending");
        this.tickUpdates(false);
        this.theProfiler.endStartSection("tickTiles");
        this.tickBlocksAndAmbiance();
        this.theProfiler.endStartSection("chunkMap");
        this.thePlayerManager.updatePlayerInstances();
        this.theProfiler.endStartSection("village");
        this.villageCollectionObj.tick();
        this.villageSiegeObj.tick();
        this.theProfiler.endStartSection("portalForcer");
        this.field_85177_Q.func_85189_a(this.getTotalWorldTime());

        for (Teleporter tele : customTeleporters)
        {
            tele.func_85189_a(getTotalWorldTime());
        }

        this.theProfiler.endSection();
        this.sendAndApplyBlockEvents();
    }

    public SpawnListEntry spawnRandomCreature(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4)
    {
        List var5 = this.getChunkProvider().getPossibleCreatures(par1EnumCreatureType, par2, par3, par4);
        return var5 != null && !var5.isEmpty() ? (SpawnListEntry)WeightedRandom.getRandomItem(this.rand, var5) : null;
    }

    public void updateAllPlayersSleepingFlag()
    {
        this.allPlayersSleeping = !this.playerEntities.isEmpty();
        Iterator var1 = this.playerEntities.iterator();

        while (var1.hasNext())
        {
            EntityPlayer var2 = (EntityPlayer)var1.next();

            if (!var2.isPlayerSleeping())
            {
                this.allPlayersSleeping = false;
                break;
            }
        }
    }

    protected void wakeAllPlayers()
    {
        this.allPlayersSleeping = false;
        Iterator var1 = this.playerEntities.iterator();

        while (var1.hasNext())
        {
            EntityPlayer var2 = (EntityPlayer)var1.next();

            if (var2.isPlayerSleeping())
            {
                var2.wakeUpPlayer(false, false, true);
            }
        }

        this.resetRainAndThunder();
    }

    private void resetRainAndThunder()
    {
        provider.resetRainAndThunder();
    }

    public boolean areAllPlayersAsleep()
    {
        if (this.allPlayersSleeping && !this.isRemote)
        {
            Iterator var1 = this.playerEntities.iterator();
            EntityPlayer var2;

            do
            {
                if (!var1.hasNext())
                {
                    return true;
                }

                var2 = (EntityPlayer)var1.next();
            }
            while (var2.isPlayerFullyAsleep());

            return false;
        }
        else
        {
            return false;
        }
    }

    @SideOnly(Side.CLIENT)

    public void setSpawnLocation()
    {
        if (this.worldInfo.getSpawnY() <= 0)
        {
            this.worldInfo.setSpawnY(64);
        }

        int var1 = this.worldInfo.getSpawnX();
        int var2 = this.worldInfo.getSpawnZ();
        int var3 = 0;

        while (this.getFirstUncoveredBlock(var1, var2) == 0)
        {
            var1 += this.rand.nextInt(8) - this.rand.nextInt(8);
            var2 += this.rand.nextInt(8) - this.rand.nextInt(8);
            ++var3;

            if (var3 == 10000)
            {
                break;
            }
        }

        this.worldInfo.setSpawnX(var1);
        this.worldInfo.setSpawnZ(var2);
    }

    protected void tickBlocksAndAmbiance()
    {
        super.tickBlocksAndAmbiance();
        int var1 = 0;
        int var2 = 0;
        Iterator var3 = this.activeChunkSet.iterator();
        doneChunks.retainAll(activeChunkSet);

        if (doneChunks.size() == activeChunkSet.size())
        {
            doneChunks.clear();
        }

        final long time = -System.currentTimeMillis();

        while (var3.hasNext())
        {
            ChunkCoordIntPair var4 = (ChunkCoordIntPair)var3.next();
            int var5 = var4.chunkXPos * 16;
            int var6 = var4.chunkZPos * 16;
            this.theProfiler.startSection("getChunk");
            Chunk var7 = this.getChunkFromChunkCoords(var4.chunkXPos, var4.chunkZPos);
            this.moodSoundAndLightCheck(var5, var6, var7);
            this.theProfiler.endStartSection("tickChunk");

            if (System.currentTimeMillis() + time <= 4 && doneChunks.add(var4))
            {
                var7.updateSkylight();
            }

            this.theProfiler.endStartSection("thunder");
            int var8;
            int var9;
            int var10;
            int var11;

            if (provider.canDoLightning(var7) && this.rand.nextInt(100000) == 0 && this.isRaining() && this.isThundering())
            {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                var8 = this.updateLCG >> 2;
                var9 = var5 + (var8 & 15);
                var10 = var6 + (var8 >> 8 & 15);
                var11 = this.getPrecipitationHeight(var9, var10);

                if (this.canLightningStrikeAt(var9, var11, var10))
                {
                    this.addWeatherEffect(new EntityLightningBolt(this, (double)var9, (double)var11, (double)var10));
                    this.lastLightningBolt = 2;
                }
            }

            this.theProfiler.endStartSection("iceandsnow");
            int var13;

            if (provider.canDoRainSnowIce(var7) && this.rand.nextInt(16) == 0)
            {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                var8 = this.updateLCG >> 2;
                var9 = var8 & 15;
                var10 = var8 >> 8 & 15;
                var11 = this.getPrecipitationHeight(var9 + var5, var10 + var6);

                if (this.isBlockFreezableNaturally(var9 + var5, var11 - 1, var10 + var6))
                {
                    this.setBlockWithNotify(var9 + var5, var11 - 1, var10 + var6, Block.ice.blockID);
                }

                if (this.isRaining() && this.canSnowAt(var9 + var5, var11, var10 + var6))
                {
                    this.setBlockWithNotify(var9 + var5, var11, var10 + var6, Block.snow.blockID);
                }

                if (this.isRaining())
                {
                    BiomeGenBase var12 = this.getBiomeGenForCoords(var9 + var5, var10 + var6);

                    if (var12.canSpawnLightningBolt())
                    {
                        var13 = this.getBlockId(var9 + var5, var11 - 1, var10 + var6);

                        if (var13 != 0)
                        {
                            Block.blocksList[var13].fillWithRain(this, var9 + var5, var11 - 1, var10 + var6);
                        }
                    }
                }
            }

            this.theProfiler.endStartSection("tickTiles");
            ExtendedBlockStorage[] var19 = var7.getBlockStorageArray();
            var9 = var19.length;

            for (var10 = 0; var10 < var9; ++var10)
            {
                ExtendedBlockStorage var21 = var19[var10];

                if (var21 != null && var21.getNeedsRandomTick())
                {
                    for (int var20 = 0; var20 < 3; ++var20)
                    {
                        this.updateLCG = this.updateLCG * 3 + 1013904223;
                        var13 = this.updateLCG >> 2;
                        int var14 = var13 & 15;
                        int var15 = var13 >> 8 & 15;
                        int var16 = var13 >> 16 & 15;
                        int var17 = var21.getExtBlockID(var14, var16, var15);
                        ++var2;
                        Block var18 = Block.blocksList[var17];

                        if (var18 != null && var18.getTickRandomly())
                        {
                            ++var1;
                            var18.updateTick(this, var14 + var5, var16 + var21.getYLocation(), var15 + var6, this.rand);
                        }
                    }
                }
            }

            this.theProfiler.endSection();
        }
    }

    public void scheduleBlockUpdate(int par1, int par2, int par3, int par4, int par5)
    {
        this.func_82740_a(par1, par2, par3, par4, par5, 0);
    }

    public void func_82740_a(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        NextTickListEntry var7 = new NextTickListEntry(par1, par2, par3, par4);
        boolean isForced = getPersistentChunks().containsKey(new ChunkCoordIntPair(var7.xCoord >> 4, var7.zCoord >> 4));
        byte var8 = isForced ? (byte)0 : 8;

        if (this.scheduledUpdatesAreImmediate && par4 > 0)
        {
            if (Block.blocksList[par4].func_82506_l())
            {
                if (this.checkChunksExist(var7.xCoord - var8, var7.yCoord - var8, var7.zCoord - var8, var7.xCoord + var8, var7.yCoord + var8, var7.zCoord + var8))
                {
                    int var9 = this.getBlockId(var7.xCoord, var7.yCoord, var7.zCoord);

                    if (var9 == var7.blockID && var9 > 0)
                    {
                        Block.blocksList[var9].updateTick(this, var7.xCoord, var7.yCoord, var7.zCoord, this.rand);
                    }
                }

                return;
            }

            par5 = 1;
        }

        if (this.checkChunksExist(par1 - var8, par2 - var8, par3 - var8, par1 + var8, par2 + var8, par3 + var8))
        {
            if (par4 > 0)
            {
                var7.setScheduledTime((long)par5 + this.worldInfo.getWorldTotalTime());
                var7.func_82753_a(par6);
            }

            if (!this.field_73064_N.contains(var7))
            {
                this.field_73064_N.add(var7);
                this.pendingTickListEntries.add(var7);
            }
        }
    }

    public void scheduleBlockUpdateFromLoad(int par1, int par2, int par3, int par4, int par5)
    {
        NextTickListEntry var6 = new NextTickListEntry(par1, par2, par3, par4);

        if (par4 > 0)
        {
            var6.setScheduledTime((long)par5 + this.worldInfo.getWorldTotalTime());
        }

        if (!this.field_73064_N.contains(var6))
        {
            this.field_73064_N.add(var6);
            this.pendingTickListEntries.add(var6);
        }
    }

    public void updateEntities()
    {
        if (this.playerEntities.isEmpty() && getPersistentChunks().isEmpty())
        {
            if (this.updateEntityTick++ >= 1200)
            {
                return;
            }
        }
        else
        {
            this.func_82742_i();
        }

        super.updateEntities();
    }

    public void func_82742_i()
    {
        this.updateEntityTick = 0;
    }

    public boolean tickUpdates(boolean par1)
    {
        int var2 = this.pendingTickListEntries.size();

        if (var2 != this.field_73064_N.size())
        {
            throw new IllegalStateException("TickNextTick list out of synch");
        }
        else
        {
            if (var2 > 1000)
            {
                var2 = 1000;
            }

            for (int var3 = 0; var3 < var2; ++var3)
            {
                NextTickListEntry var4 = (NextTickListEntry)this.pendingTickListEntries.first();

                if (!par1 && var4.scheduledTime > this.worldInfo.getWorldTotalTime())
                {
                    break;
                }

                this.pendingTickListEntries.remove(var4);
                this.field_73064_N.remove(var4);
                boolean isForced = getPersistentChunks().containsKey(new ChunkCoordIntPair(var4.xCoord >> 4, var4.zCoord >> 4));
                byte var5 = isForced ? (byte)0 : 8;

                if (this.checkChunksExist(var4.xCoord - var5, var4.yCoord - var5, var4.zCoord - var5, var4.xCoord + var5, var4.yCoord + var5, var4.zCoord + var5))
                {
                    int var6 = this.getBlockId(var4.xCoord, var4.yCoord, var4.zCoord);

                    if (var6 == var4.blockID && var6 > 0)
                    {
                        try
                        {
                            Block.blocksList[var6].updateTick(this, var4.xCoord, var4.yCoord, var4.zCoord, this.rand);
                        }
                        catch (Throwable var13)
                        {
                            CrashReport var8 = CrashReport.func_85055_a(var13, "Exception while ticking a block");
                            CrashReportCategory var9 = var8.func_85058_a("Block being ticked");
                            int var10;

                            try
                            {
                                var10 = this.getBlockMetadata(var4.xCoord, var4.yCoord, var4.zCoord);
                            }
                            catch (Throwable var12)
                            {
                                var10 = -1;
                            }

                            CrashReportCategory.func_85068_a(var9, var4.xCoord, var4.yCoord, var4.zCoord, var6, var10);
                            throw new ReportedException(var8);
                        }
                    }
                }
            }

            return !this.pendingTickListEntries.isEmpty();
        }
    }

    public List getPendingBlockUpdates(Chunk par1Chunk, boolean par2)
    {
        ArrayList var3 = null;
        ChunkCoordIntPair var4 = par1Chunk.getChunkCoordIntPair();
        int var5 = var4.chunkXPos << 4;
        int var6 = var5 + 16;
        int var7 = var4.chunkZPos << 4;
        int var8 = var7 + 16;
        Iterator var9 = this.pendingTickListEntries.iterator();

        while (var9.hasNext())
        {
            NextTickListEntry var10 = (NextTickListEntry)var9.next();

            if (var10.xCoord >= var5 && var10.xCoord < var6 && var10.zCoord >= var7 && var10.zCoord < var8)
            {
                if (par2)
                {
                    this.field_73064_N.remove(var10);
                    var9.remove();
                }

                if (var3 == null)
                {
                    var3 = new ArrayList();
                }

                var3.add(var10);
            }
        }

        return var3;
    }

    public void updateEntityWithOptionalForce(Entity par1Entity, boolean par2)
    {
        if (!this.mcServer.getCanSpawnAnimals() && (par1Entity instanceof EntityAnimal || par1Entity instanceof EntityWaterMob))
        {
            par1Entity.setDead();
        }

        if (!this.mcServer.getCanSpawnNPCs() && par1Entity instanceof INpc)
        {
            par1Entity.setDead();
        }

        if (!(par1Entity.riddenByEntity instanceof EntityPlayer))
        {
            super.updateEntityWithOptionalForce(par1Entity, par2);
        }
    }

    public void uncheckedUpdateEntity(Entity par1Entity, boolean par2)
    {
        super.updateEntityWithOptionalForce(par1Entity, par2);
    }

    protected IChunkProvider createChunkProvider()
    {
        IChunkLoader var1 = this.saveHandler.getChunkLoader(this.provider);
        this.theChunkProviderServer = new ChunkProviderServer(this, var1, this.provider.getChunkProvider());
        return this.theChunkProviderServer;
    }

    public List getAllTileEntityInBox(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        ArrayList var7 = new ArrayList();

        for (int x = (par1 >> 4); x <= (par4 >> 4); x++)
        {
            for (int z = (par3 >> 4); z <= (par6 >> 4); z++)
            {
                Chunk chunk = getChunkFromChunkCoords(x, z);

                if (chunk != null)
                {
                    for (Object obj : chunk.chunkTileEntityMap.values())
                    {
                        TileEntity entity = (TileEntity)obj;

                        if (!entity.isInvalid())
                        {
                            if (entity.xCoord >= par1 && entity.yCoord >= par2 && entity.zCoord >= par3 &&
                                    entity.xCoord <= par4 && entity.yCoord <= par5 && entity.zCoord <= par6)
                            {
                                var7.add(entity);
                            }
                        }
                    }
                }
            }
        }

        return var7;
    }

    public boolean canMineBlock(EntityPlayer par1EntityPlayer, int par2, int par3, int par4)
    {
        return super.canMineBlock(par1EntityPlayer, par2, par3, par4);
    }

    public boolean canMineBlockBody(EntityPlayer par1EntityPlayer, int par2, int par3, int par4)
    {
        int var5 = MathHelper.abs_int(par2 - this.worldInfo.getSpawnX());
        int var6 = MathHelper.abs_int(par4 - this.worldInfo.getSpawnZ());

        if (var5 > var6)
        {
            var6 = var5;
        }

        return var6 > mcServer.getSpawnProtectionSize() || this.mcServer.getConfigurationManager().areCommandsAllowed(par1EntityPlayer.username) || this.mcServer.isSinglePlayer();
    }

    protected void initialize(WorldSettings par1WorldSettings)
    {
        if (this.entityIdMap == null)
        {
            this.entityIdMap = new IntHashMap();
        }

        if (this.field_73064_N == null)
        {
            this.field_73064_N = new HashSet();
        }

        if (this.pendingTickListEntries == null)
        {
            this.pendingTickListEntries = new TreeSet();
        }

        this.createSpawnPosition(par1WorldSettings);
        super.initialize(par1WorldSettings);
    }

    protected void createSpawnPosition(WorldSettings par1WorldSettings)
    {
        if (!this.provider.canRespawnHere())
        {
            this.worldInfo.setSpawnPosition(0, this.provider.getAverageGroundLevel(), 0);
        }
        else
        {
            this.findingSpawnPoint = true;
            WorldChunkManager var2 = this.provider.worldChunkMgr;
            List var3 = var2.getBiomesToSpawnIn();
            Random var4 = new Random(this.getSeed());
            ChunkPosition var5 = var2.findBiomePosition(0, 0, 256, var3, var4);
            int var6 = 0;
            int var7 = this.provider.getAverageGroundLevel();
            int var8 = 0;

            if (var5 != null)
            {
                var6 = var5.x;
                var8 = var5.z;
            }
            else
            {
                System.out.println("Unable to find spawn biome");
            }

            int var9 = 0;

            while (!this.provider.canCoordinateBeSpawn(var6, var8))
            {
                var6 += var4.nextInt(64) - var4.nextInt(64);
                var8 += var4.nextInt(64) - var4.nextInt(64);
                ++var9;

                if (var9 == 1000)
                {
                    break;
                }
            }

            this.worldInfo.setSpawnPosition(var6, var7, var8);
            this.findingSpawnPoint = false;

            if (par1WorldSettings.isBonusChestEnabled())
            {
                this.createBonusChest();
            }
        }
    }

    protected void createBonusChest()
    {
        WorldGeneratorBonusChest var1 = new WorldGeneratorBonusChest(ChestGenHooks.getItems(BONUS_CHEST), ChestGenHooks.getCount(BONUS_CHEST, rand));

        for (int var2 = 0; var2 < 10; ++var2)
        {
            int var3 = this.worldInfo.getSpawnX() + this.rand.nextInt(6) - this.rand.nextInt(6);
            int var4 = this.worldInfo.getSpawnZ() + this.rand.nextInt(6) - this.rand.nextInt(6);
            int var5 = this.getTopSolidOrLiquidBlock(var3, var4) + 1;

            if (var1.generate(this, this.rand, var3, var5, var4))
            {
                break;
            }
        }
    }

    public ChunkCoordinates getEntrancePortalLocation()
    {
        return this.provider.getEntrancePortalLocation();
    }

    public void saveAllChunks(boolean par1, IProgressUpdate par2IProgressUpdate) throws MinecraftException
    {
        if (this.chunkProvider.canSave())
        {
            if (par2IProgressUpdate != null)
            {
                par2IProgressUpdate.displayProgressMessage("Saving level");
            }

            this.saveLevel();

            if (par2IProgressUpdate != null)
            {
                par2IProgressUpdate.resetProgresAndWorkingMessage("Saving chunks");
            }

            this.chunkProvider.saveChunks(par1, par2IProgressUpdate);
            MinecraftForge.EVENT_BUS.post(new WorldEvent.Save(this));
        }
    }

    protected void saveLevel() throws MinecraftException
    {
        this.checkSessionLock();
        this.saveHandler.saveWorldInfoWithPlayer(this.worldInfo, this.mcServer.getConfigurationManager().getTagsFromLastWrite());
        this.mapStorage.saveAllData();
        this.perWorldStorage.saveAllData();
    }

    protected void obtainEntitySkin(Entity par1Entity)
    {
        super.obtainEntitySkin(par1Entity);
        this.entityIdMap.addKey(par1Entity.entityId, par1Entity);
        Entity[] var2 = par1Entity.getParts();

        if (var2 != null)
        {
            for (int var3 = 0; var3 < var2.length; ++var3)
            {
                this.entityIdMap.addKey(var2[var3].entityId, var2[var3]);
            }
        }
    }

    protected void releaseEntitySkin(Entity par1Entity)
    {
        super.releaseEntitySkin(par1Entity);
        this.entityIdMap.removeObject(par1Entity.entityId);
        Entity[] var2 = par1Entity.getParts();

        if (var2 != null)
        {
            for (int var3 = 0; var3 < var2.length; ++var3)
            {
                this.entityIdMap.removeObject(var2[var3].entityId);
            }
        }
    }

    public Entity getEntityByID(int par1)
    {
        return (Entity)this.entityIdMap.lookup(par1);
    }

    public boolean addWeatherEffect(Entity par1Entity)
    {
        if (super.addWeatherEffect(par1Entity))
        {
            this.mcServer.getConfigurationManager().sendToAllNear(par1Entity.posX, par1Entity.posY, par1Entity.posZ, 512.0D, this.provider.dimensionId, new Packet71Weather(par1Entity));
            return true;
        }
        else
        {
            return false;
        }
    }

    public void setEntityState(Entity par1Entity, byte par2)
    {
        Packet38EntityStatus var3 = new Packet38EntityStatus(par1Entity.entityId, par2);
        this.getEntityTracker().sendPacketToAllAssociatedPlayers(par1Entity, var3);
    }

    public Explosion newExplosion(Entity par1Entity, double par2, double par4, double par6, float par8, boolean par9, boolean par10)
    {
        Explosion var11 = new Explosion(this, par1Entity, par2, par4, par6, par8);
        var11.isFlaming = par9;
        var11.isSmoking = par10;
        var11.doExplosionA();
        var11.doExplosionB(false);

        if (!par10)
        {
            var11.affectedBlockPositions.clear();
        }

        Iterator var12 = this.playerEntities.iterator();

        while (var12.hasNext())
        {
            EntityPlayer var13 = (EntityPlayer)var12.next();

            if (var13.getDistanceSq(par2, par4, par6) < 4096.0D)
            {
                ((EntityPlayerMP)var13).playerNetServerHandler.sendPacketToPlayer(new Packet60Explosion(par2, par4, par6, par8, var11.affectedBlockPositions, (Vec3)var11.func_77277_b().get(var13)));
            }
        }

        return var11;
    }

    public void addBlockEvent(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        BlockEventData var7 = new BlockEventData(par1, par2, par3, par4, par5, par6);
        Iterator var8 = this.blockEventCache[this.blockEventCacheIndex].iterator();
        BlockEventData var9;

        do
        {
            if (!var8.hasNext())
            {
                this.blockEventCache[this.blockEventCacheIndex].add(var7);
                return;
            }

            var9 = (BlockEventData)var8.next();
        }
        while (!var9.equals(var7));
    }

    private void sendAndApplyBlockEvents()
    {
        while (!this.blockEventCache[this.blockEventCacheIndex].isEmpty())
        {
            int var1 = this.blockEventCacheIndex;
            this.blockEventCacheIndex ^= 1;
            Iterator var2 = this.blockEventCache[var1].iterator();

            while (var2.hasNext())
            {
                BlockEventData var3 = (BlockEventData)var2.next();

                if (this.onBlockEventReceived(var3))
                {
                    this.mcServer.getConfigurationManager().sendToAllNear((double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), 64.0D, this.provider.dimensionId, new Packet54PlayNoteBlock(var3.getX(), var3.getY(), var3.getZ(), var3.getBlockID(), var3.getEventID(), var3.getEventParameter()));
                }
            }

            this.blockEventCache[var1].clear();
        }
    }

    private boolean onBlockEventReceived(BlockEventData par1BlockEventData)
    {
        int var2 = this.getBlockId(par1BlockEventData.getX(), par1BlockEventData.getY(), par1BlockEventData.getZ());

        if (var2 == par1BlockEventData.getBlockID())
        {
            Block.blocksList[var2].onBlockEventReceived(this, par1BlockEventData.getX(), par1BlockEventData.getY(), par1BlockEventData.getZ(), par1BlockEventData.getEventID(), par1BlockEventData.getEventParameter());
            return true;
        }
        else
        {
            return false;
        }
    }

    public void flush()
    {
        this.saveHandler.flush();
    }

    protected void updateWeather()
    {
        boolean var1 = this.isRaining();
        super.updateWeather();

        if (var1 != this.isRaining())
        {
            if (var1)
            {
                this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new Packet70GameEvent(2, 0));
            }
            else
            {
                this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new Packet70GameEvent(1, 0));
            }
        }
    }

    public MinecraftServer getMinecraftServer()
    {
        return this.mcServer;
    }

    public EntityTracker getEntityTracker()
    {
        return this.theEntityTracker;
    }

    public PlayerManager getPlayerManager()
    {
        return this.thePlayerManager;
    }

    public Teleporter func_85176_s()
    {
        return this.field_85177_Q;
    }

    public File getChunkSaveLocation()
    {
        return ((AnvilChunkLoader)theChunkProviderServer.currentChunkLoader).chunkSaveLocation;
    }
}
