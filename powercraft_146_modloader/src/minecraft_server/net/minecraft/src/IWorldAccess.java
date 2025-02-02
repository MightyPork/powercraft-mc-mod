package net.minecraft.src;

public interface IWorldAccess
{
    /**
     * On the client, re-renders the block. On the server, sends the block to the client (which will re-render it),
     * including the tile entity description packet if applicable. Args: x, y, z
     */
    void markBlockForUpdate(int var1, int var2, int var3);

    /**
     * On the client, re-renders this block. On the server, does nothing. Used for lighting updates.
     */
    void markBlockForRenderUpdate(int var1, int var2, int var3);

    /**
     * On the client, re-renders all blocks in this range, inclusive. On the server, does nothing. Args: min x, min y,
     * min z, max x, max y, max z
     */
    void markBlockRangeForRenderUpdate(int var1, int var2, int var3, int var4, int var5, int var6);

    /**
     * Plays the specified sound. Arg: soundName, x, y, z, volume, pitch
     */
    void playSound(String var1, double var2, double var4, double var6, float var8, float var9);

    void func_85102_a(EntityPlayer var1, String var2, double var3, double var5, double var7, float var9, float var10);

    /**
     * Spawns a particle. Arg: particleType, x, y, z, velX, velY, velZ
     */
    void spawnParticle(String var1, double var2, double var4, double var6, double var8, double var10, double var12);

    /**
     * Start the skin for this entity downloading, if necessary, and increment its reference counter
     */
    void obtainEntitySkin(Entity var1);

    /**
     * Decrement the reference counter for this entity's skin image data
     */
    void releaseEntitySkin(Entity var1);

    /**
     * Plays the specified record. Arg: recordName, x, y, z
     */
    void playRecord(String var1, int var2, int var3, int var4);

    void broadcastSound(int var1, int var2, int var3, int var4, int var5);

    /**
     * Plays a pre-canned sound effect along with potentially auxiliary data-driven one-shot behaviour (particles, etc).
     */
    void playAuxSFX(EntityPlayer var1, int var2, int var3, int var4, int var5, int var6);

    /**
     * Starts (or continues) destroying a block with given ID at the given coordinates for the given partially destroyed
     * value
     */
    void destroyBlockPartially(int var1, int var2, int var3, int var4, int var5);
}
