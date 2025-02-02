package net.minecraft.src;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class WorldType
{
    public static final BiomeGenBase[] base11Biomes = new BiomeGenBase[] {BiomeGenBase.desert, BiomeGenBase.forest, BiomeGenBase.extremeHills, BiomeGenBase.swampland, BiomeGenBase.plains, BiomeGenBase.taiga};
    public static final BiomeGenBase[] base12Biomes = ObjectArrays.concat(base11Biomes, BiomeGenBase.jungle);

    public static final WorldType[] worldTypes = new WorldType[16];

    public static final WorldType DEFAULT = (new WorldType(0, "default", 1)).setVersioned();

    public static final WorldType FLAT = new WorldType(1, "flat");

    public static final WorldType LARGE_BIOMES = new WorldType(2, "largeBiomes");

    public static final WorldType DEFAULT_1_1 = (new WorldType(8, "default_1_1", 0)).setCanBeCreated(false);

    private final int worldTypeId;

    private final String worldType;

    private final int generatorVersion;

    private boolean canBeCreated;

    private boolean isWorldTypeVersioned;

    protected BiomeGenBase[] biomesForWorldType;

    public WorldType(int par1, String par2Str)
    {
        this(par1, par2Str, 0);
    }

    public WorldType(int par1, String par2Str, int par3)
    {
        this.worldType = par2Str;
        this.generatorVersion = par3;
        this.canBeCreated = true;
        this.worldTypeId = par1;
        worldTypes[par1] = this;

        switch (par1)
        {
            case 8:
                biomesForWorldType = base11Biomes;
                break;

            default:
                biomesForWorldType = base12Biomes;
        }
    }

    public String getWorldTypeName()
    {
        return this.worldType;
    }

    @SideOnly(Side.CLIENT)

    public String getTranslateName()
    {
        return "generator." + this.worldType;
    }

    public int getGeneratorVersion()
    {
        return this.generatorVersion;
    }

    public WorldType getWorldTypeForGeneratorVersion(int par1)
    {
        return this == DEFAULT && par1 == 0 ? DEFAULT_1_1 : this;
    }

    private WorldType setCanBeCreated(boolean par1)
    {
        this.canBeCreated = par1;
        return this;
    }

    @SideOnly(Side.CLIENT)

    public boolean getCanBeCreated()
    {
        return this.canBeCreated;
    }

    private WorldType setVersioned()
    {
        this.isWorldTypeVersioned = true;
        return this;
    }

    public boolean isVersioned()
    {
        return this.isWorldTypeVersioned;
    }

    public static WorldType parseWorldType(String par0Str)
    {
        for (int var1 = 0; var1 < worldTypes.length; ++var1)
        {
            if (worldTypes[var1] != null && worldTypes[var1].worldType.equalsIgnoreCase(par0Str))
            {
                return worldTypes[var1];
            }
        }

        return null;
    }

    public int func_82747_f()
    {
        return this.worldTypeId;
    }

    public WorldChunkManager getChunkManager(World world)
    {
        if (this == FLAT)
        {
            FlatGeneratorInfo var1 = FlatGeneratorInfo.func_82651_a(world.getWorldInfo().func_82571_y());
            return new WorldChunkManagerHell(BiomeGenBase.biomeList[var1.getBiome()], 0.5F, 0.5F);
        }
        else
        {
            return new WorldChunkManager(world);
        }
    }

    public IChunkProvider getChunkGenerator(World world, String generatorOptions)
    {
        return (this == FLAT ? new ChunkProviderFlat(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), generatorOptions) : new ChunkProviderGenerate(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled()));
    }

    public int getMinimumSpawnHeight(World world)
    {
        return this == FLAT ? 4 : 64;
    }

    public double getHorizon(World world)
    {
        return this == FLAT ? 0.0D : 63.0D;
    }

    public boolean hasVoidParticles(boolean var1)
    {
        return this != FLAT && !var1;
    }

    public double voidFadeMagnitude()
    {
        return this == FLAT ? 1.0D : 0.03125D;
    }

    public BiomeGenBase[] getBiomesForWorldType()
    {
        return biomesForWorldType;
    }

    public void addNewBiome(BiomeGenBase biome)
    {
        Set<BiomeGenBase> newBiomesForWorld = Sets.newLinkedHashSet(Arrays.asList(biomesForWorldType));
        newBiomesForWorld.add(biome);
        biomesForWorldType = newBiomesForWorld.toArray(new BiomeGenBase[0]);
    }

    public void removeBiome(BiomeGenBase biome)
    {
        Set<BiomeGenBase> newBiomesForWorld = Sets.newLinkedHashSet(Arrays.asList(biomesForWorldType));
        newBiomesForWorld.remove(biome);
        biomesForWorldType = newBiomesForWorld.toArray(new BiomeGenBase[0]);
    }

    public boolean handleSlimeSpawnReduction(Random random, World world)
    {
        return this == FLAT ? random.nextInt(4) != 1 : false;
    }

    public void onGUICreateWorldPress() { }

    public int getSpawnFuzz()
    {
        return 20;
    }
}
