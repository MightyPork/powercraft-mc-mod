/*
 * The FML Forge Mod Loader suite. Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package cpw.mods.fml.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CrashReport;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet131MapData;
import net.minecraft.src.Render;
import net.minecraft.src.RenderManager;
import net.minecraft.src.World;
import net.minecraft.src.WorldClient;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import cpw.mods.fml.client.modloader.ModLoaderClientHelper;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.DuplicateModsFoundException;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.IFMLSidedHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.MissingModsException;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.WrongMinecraftVersionException;
import cpw.mods.fml.common.network.EntitySpawnAdjustmentPacket;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.network.ModMissingPacket;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.common.registry.LanguageRegistry;


/**
 * Handles primary communication from hooked code into the system
 *
 * The FML entry point is {@link #beginMinecraftLoading(Minecraft)} called from
 * {@link Minecraft}
 *
 * Obfuscated code should focus on this class and other members of the "server"
 * (or "client") code
 *
 * The actual mod loading is handled at arms length by {@link Loader}
 *
 * It is expected that a similar class will exist for each target environment:
 * Bukkit and Client side.
 *
 * It should not be directly modified.
 *
 * @author cpw
 *
 */
public class FMLClientHandler implements IFMLSidedHandler
{
    /**
     * The singleton
     */
    private static final FMLClientHandler INSTANCE = new FMLClientHandler();

    /**
     * A reference to the server itself
     */
    private Minecraft client;

    private DummyModContainer optifineContainer;

    private boolean guiLoaded;

    private boolean serverIsRunning;

    private MissingModsException modsMissing;

    private boolean loading;

    private WrongMinecraftVersionException wrongMC;

    private CustomModLoadingErrorDisplayException customError;

	private DuplicateModsFoundException dupesFound;

    /**
     * Called to start the whole game off
     *
     * @param minecraft The minecraft instance being launched
     */
    public void beginMinecraftLoading(Minecraft minecraft)
    {
        if (minecraft.isDemo())
        {
            FMLLog.severe("DEMO MODE DETECTED, FML will not work. Finishing now.");
            haltGame("FML will not run in demo mode", new RuntimeException());
            return;
        }

        loading = true;
        client = minecraft;
        ObfuscationReflectionHelper.detectObfuscation(World.class);
        TextureFXManager.instance().setClient(client);
        FMLCommonHandler.instance().beginLoading(this);
        new ModLoaderClientHelper(client);
        try
        {
            Class<?> optifineConfig = Class.forName("Config", false, Loader.instance().getModClassLoader());
            String optifineVersion = (String) optifineConfig.getField("VERSION").get(null);
            Map<String,Object> dummyOptifineMeta = ImmutableMap.<String,Object>builder().put("name", "Optifine").put("version", optifineVersion).build();
            ModMetadata optifineMetadata = MetadataCollection.from(getClass().getResourceAsStream("optifinemod.info"),"optifine").getMetadataForId("optifine", dummyOptifineMeta);
            optifineContainer = new DummyModContainer(optifineMetadata);
            FMLLog.info("Forge Mod Loader has detected optifine %s, enabling compatibility features",optifineContainer.getVersion());
        }
        catch (Exception e)
        {
            optifineContainer = null;
        }
        try
        {
            Loader.instance().loadMods();
        }
        catch (WrongMinecraftVersionException wrong)
        {
            wrongMC = wrong;
        }
        catch (DuplicateModsFoundException dupes)
        {
        	dupesFound = dupes;
        }
        catch (MissingModsException missing)
        {
            modsMissing = missing;
        }
        catch (CustomModLoadingErrorDisplayException custom)
        {
            FMLLog.log(Level.SEVERE, custom, "A custom exception was thrown by a mod, the game will now halt");
            customError = custom;
        }
        catch (LoaderException le)
        {
            haltGame("There was a severe problem during mod loading that has caused the game to fail", le);
            return;
        }
    }

    @Override
    public void haltGame(String message, Throwable t)
    {
        client.displayCrashReport(new CrashReport(message, t));
        throw Throwables.propagate(t);
    }
    /**
     * Called a bit later on during initialization to finish loading mods
     * Also initializes key bindings
     *
     */
    @SuppressWarnings("deprecation")
    public void finishMinecraftLoading()
    {
        if (modsMissing != null || wrongMC != null || customError!=null || dupesFound!=null)
        {
            return;
        }
        try
        {
            Loader.instance().initializeMods();
        }
        catch (CustomModLoadingErrorDisplayException custom)
        {
            FMLLog.log(Level.SEVERE, custom, "A custom exception was thrown by a mod, the game will now halt");
            customError = custom;
            return;
        }
        catch (LoaderException le)
        {
            haltGame("There was a severe problem during mod loading that has caused the game to fail", le);
            return;
        }
        LanguageRegistry.reloadLanguageTable();
        RenderingRegistry.instance().loadEntityRenderers((Map<Class<? extends Entity>, Render>)RenderManager.instance.entityRenderMap);

        loading = false;
        KeyBindingRegistry.instance().uploadKeyBindingsToGame(client.gameSettings);
    }

    public void onInitializationComplete()
    {
        if (wrongMC != null)
        {
            client.displayGuiScreen(new GuiWrongMinecraft(wrongMC));
        }
        else if (modsMissing != null)
        {
            client.displayGuiScreen(new GuiModsMissing(modsMissing));
        }
        else if (dupesFound != null)
        {
        	client.displayGuiScreen(new GuiDupesFound(dupesFound));
        }
        else if (customError != null)
        {
            client.displayGuiScreen(new GuiCustomModLoadingErrorScreen(customError));
        }
        else
        {
            TextureFXManager.instance().loadTextures(client.texturePackList.getSelectedTexturePack());
        }
    }
    /**
     * Get the server instance
     */
    public Minecraft getClient()
    {
        return client;
    }

    /**
     * Get a handle to the client's logger instance
     * The client actually doesn't have one- so we return null
     */
    public Logger getMinecraftLogger()
    {
        return null;
    }

    /**
     * @return the instance
     */
    public static FMLClientHandler instance()
    {
        return INSTANCE;
    }

    /**
     * @param player
     * @param gui
     */
    public void displayGuiScreen(EntityPlayer player, GuiScreen gui)
    {
        if (client.thePlayer==player && gui != null) {
            client.displayGuiScreen(gui);
        }
    }

    /**
     * @param mods
     */
    public void addSpecialModEntries(ArrayList<ModContainer> mods)
    {
        if (optifineContainer!=null) {
            mods.add(optifineContainer);
        }
    }

    @Override
    public List<String> getAdditionalBrandingInformation()
    {
        if (optifineContainer!=null)
        {
            return Arrays.asList(String.format("Optifine %s",optifineContainer.getVersion()));
        } else {
            return ImmutableList.<String>of();
        }
    }

    @Override
    public Side getSide()
    {
        return Side.CLIENT;
    }

    public boolean hasOptifine()
    {
        return optifineContainer!=null;
    }

    @Override
    public void showGuiScreen(Object clientGuiElement)
    {
        GuiScreen gui = (GuiScreen) clientGuiElement;
        client.displayGuiScreen(gui);
    }

    @Override
    public Entity spawnEntityIntoClientWorld(EntityRegistration er, EntitySpawnPacket packet)
    {
        WorldClient wc = client.theWorld;

        Class<? extends Entity> cls = er.getEntityClass();

        try
        {
            Entity entity;
            if (er.hasCustomSpawning())
            {
                entity = er.doCustomSpawning(packet);
            }
            else
            {
                entity = (Entity)(cls.getConstructor(World.class).newInstance(wc));
                entity.entityId = packet.entityId;
                entity.setLocationAndAngles(packet.scaledX, packet.scaledY, packet.scaledZ, packet.scaledYaw, packet.scaledPitch);
                if (entity instanceof EntityLiving)
                {
                    ((EntityLiving)entity).rotationYawHead = packet.scaledHeadYaw;
                }

            }

            entity.serverPosX = packet.rawX;
            entity.serverPosY = packet.rawY;
            entity.serverPosZ = packet.rawZ;

            if (entity instanceof IThrowableEntity)
            {
                Entity thrower = client.thePlayer.entityId == packet.throwerId ? client.thePlayer : wc.getEntityByID(packet.throwerId);
                ((IThrowableEntity)entity).setThrower(thrower);
            }


            Entity parts[] = entity.getParts();
            if (parts != null)
            {
                int i = packet.entityId - entity.entityId;
                for (int j = 0; j < parts.length; j++)
                {
                    parts[j].entityId += i;
                }
            }


            if (packet.metadata != null)
            {
                entity.getDataWatcher().updateWatchedObjectsFromList((List)packet.metadata);
            }

            if (packet.throwerId > 0)
            {
                entity.setVelocity(packet.speedScaledX, packet.speedScaledY, packet.speedScaledZ);
            }

            if (entity instanceof IEntityAdditionalSpawnData)
            {
                ((IEntityAdditionalSpawnData)entity).readSpawnData(packet.dataStream);
            }

            wc.addEntityToWorld(packet.entityId, entity);
            return entity;
        }
        catch (Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "A severe problem occurred during the spawning of an entity");
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void adjustEntityLocationOnClient(EntitySpawnAdjustmentPacket packet)
    {
        Entity ent = client.theWorld.getEntityByID(packet.entityId);
        if (ent != null)
        {
            ent.serverPosX = packet.serverX;
            ent.serverPosY = packet.serverY;
            ent.serverPosZ = packet.serverZ;
        }
        else
        {
            FMLLog.fine("Attempted to adjust the position of entity %d which is not present on the client", packet.entityId);
        }
    }

    @Override
    public void beginServerLoading(MinecraftServer server)
    {
        // NOOP
    }

    @Override
    public void finishServerLoading()
    {
        // NOOP
    }

    @Override
    public MinecraftServer getServer()
    {
        return client.getIntegratedServer();
    }

    @Override
    public void sendPacket(Packet packet)
    {
        if(client.thePlayer != null)
        {
            client.thePlayer.sendQueue.addToSendQueue(packet);
        }
    }

    @Override
    public void displayMissingMods(ModMissingPacket modMissingPacket)
    {
        client.displayGuiScreen(new GuiModsMissingForServer(modMissingPacket));
    }

    /**
     * If the client is in the midst of loading, we disable saving so that custom settings aren't wiped out
     */
    public boolean isLoading()
    {
        return loading;
    }

    @Override
    public void handleTinyPacket(NetHandler handler, Packet131MapData mapData)
    {
        ((NetClientHandler)handler).fmlPacket131Callback(mapData);
    }

    @Override
    public void setClientCompatibilityLevel(byte compatibilityLevel)
    {
        NetClientHandler.setConnectionCompatibilityLevel(compatibilityLevel);
    }

    @Override
    public byte getClientCompatibilityLevel()
    {
        return NetClientHandler.getConnectionCompatibilityLevel();
    }
}
