package net.minecraft.src;

import java.util.Iterator;
import net.minecraft.server.MinecraftServer;

public class WorldManager implements IWorldAccess
{
    /** Reference to the MinecraftServer object. */
    private MinecraftServer mcServer;

    /** The WorldServer object. */
    private WorldServer theWorldServer;

    public WorldManager(MinecraftServer par1MinecraftServer, WorldServer par2WorldServer)
    {
        this.mcServer = par1MinecraftServer;
        this.theWorldServer = par2WorldServer;
    }

    /**
     * Spawns a particle. Arg: particleType, x, y, z, velX, velY, velZ
     */
    public void spawnParticle(String par1Str, double par2, double par4, double par6, double par8, double par10, double par12) {}

    /**
     * Start the skin for this entity downloading, if necessary, and increment its reference counter
     */
    public void obtainEntitySkin(Entity par1Entity)
    {
        this.theWorldServer.getEntityTracker().addEntityToTracker(par1Entity);
    }

    /**
     * Decrement the reference counter for this entity's skin image data
     */
    public void releaseEntitySkin(Entity par1Entity)
    {
        this.theWorldServer.getEntityTracker().removeEntityFromAllTrackingPlayers(par1Entity);
    }

    /**
     * Plays the specified sound. Arg: soundName, x, y, z, volume, pitch
     */
    public void playSound(String par1Str, double par2, double par4, double par6, float par8, float par9)
    {
        this.mcServer.getConfigurationManager().sendToAllNear(par2, par4, par6, par8 > 1.0F ? (double)(16.0F * par8) : 16.0D, this.theWorldServer.provider.dimensionId, new Packet62LevelSound(par1Str, par2, par4, par6, par8, par9));
    }

    public void func_85102_a(EntityPlayer par1EntityPlayer, String par2Str, double par3, double par5, double par7, float par9, float par10)
    {
        this.mcServer.getConfigurationManager().sendToAllNearExcept(par1EntityPlayer, par3, par5, par7, par9 > 1.0F ? (double)(16.0F * par9) : 16.0D, this.theWorldServer.provider.dimensionId, new Packet62LevelSound(par2Str, par3, par5, par7, par9, par10));
    }

    /**
     * On the client, re-renders all blocks in this range, inclusive. On the server, does nothing. Args: min x, min y,
     * min z, max x, max y, max z
     */
    public void markBlockRangeForRenderUpdate(int par1, int par2, int par3, int par4, int par5, int par6) {}

    /**
     * On the client, re-renders the block. On the server, sends the block to the client (which will re-render it),
     * including the tile entity description packet if applicable. Args: x, y, z
     */
    public void markBlockForUpdate(int par1, int par2, int par3)
    {
        this.theWorldServer.getPlayerManager().flagChunkForUpdate(par1, par2, par3);
    }

    /**
     * On the client, re-renders this block. On the server, does nothing. Used for lighting updates.
     */
    public void markBlockForRenderUpdate(int par1, int par2, int par3) {}

    /**
     * Plays the specified record. Arg: recordName, x, y, z
     */
    public void playRecord(String par1Str, int par2, int par3, int par4) {}

    /**
     * Plays a pre-canned sound effect along with potentially auxiliary data-driven one-shot behaviour (particles, etc).
     */
    public void playAuxSFX(EntityPlayer par1EntityPlayer, int par2, int par3, int par4, int par5, int par6)
    {
        this.mcServer.getConfigurationManager().sendToAllNearExcept(par1EntityPlayer, (double)par3, (double)par4, (double)par5, 64.0D, this.theWorldServer.provider.dimensionId, new Packet61DoorChange(par2, par3, par4, par5, par6, false));
    }

    public void func_82746_a(int par1, int par2, int par3, int par4, int par5)
    {
        this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new Packet61DoorChange(par1, par2, par3, par4, par5, true));
    }

    /**
     * Starts (or continues) destroying a block with given ID at the given coordinates for the given partially destroyed
     * value
     */
    public void destroyBlockPartially(int par1, int par2, int par3, int par4, int par5)
    {
        Iterator var6 = this.mcServer.getConfigurationManager().playerEntityList.iterator();

        while (var6.hasNext())
        {
            EntityPlayerMP var7 = (EntityPlayerMP)var6.next();

            if (var7 != null && var7.worldObj == this.theWorldServer && var7.entityId != par1)
            {
                double var8 = (double)par2 - var7.posX;
                double var10 = (double)par3 - var7.posY;
                double var12 = (double)par4 - var7.posZ;

                if (var8 * var8 + var10 * var10 + var12 * var12 < 1024.0D)
                {
                    var7.playerNetServerHandler.sendPacketToPlayer(new Packet55BlockDestroy(par1, par2, par3, par4, par5));
                }
            }
        }
    }
}