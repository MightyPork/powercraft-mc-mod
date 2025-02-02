package net.minecraft.network.rcon;

public interface IServer
{
    /**
     * Gets an integer property. If it does not exist, set it to the specified value.
     */
    int getIntProperty(String var1, int var2);

    /**
     * Gets a string property. If it does not exist, set it to the specified value.
     */
    String getStringProperty(String var1, String var2);

    /**
     * Saves an Object with the given property name.
     */
    void setProperty(String var1, Object var2);

    /**
     * Saves all of the server properties to the properties file.
     */
    void saveProperties();

    String getSettingsFilePath();

    /**
     * Returns the server's hostname.
     */
    String getHostname();

    /**
     * Never used, but "getServerPort" is already taken.
     */
    int getPort();

    /**
     * minecraftServer.getMOTD is used in 2 places instead (it is a non-virtual function which returns the same thing)
     */
    String getServerMOTD();

    /**
     * Returns the server's Minecraft version as string.
     */
    String getMinecraftVersion();

    /**
     * Returns the number of players currently on the server.
     */
    int getCurrentPlayerCount();

    /**
     * Returns the maximum number of players allowed on the server.
     */
    int getMaxPlayers();

    /**
     * Returns an array of the usernames of all the connected players.
     */
    String[] getAllUsernames();

    String getFolderName();

    /**
     * Used by RCon's Query in the form of "MajorServerMod 1.2.3: MyPlugin 1.3; AnotherPlugin 2.1; AndSoForth 1.0".
     */
    String getPlugins();

    String executeCommand(String var1);

    /**
     * Returns true if debugging is enabled, false otherwise.
     */
    boolean isDebuggingEnabled();

    /**
     * Logs the message with a level of INFO.
     */
    void logInfo(String var1);

    /**
     * Logs the message with a level of WARN.
     */
    void logWarning(String var1);

    /**
     * Logs the error message with a level of SEVERE.
     */
    void logSevere(String var1);

    /**
     * If isDebuggingEnabled(), logs the message with a level of INFO.
     */
    void logDebug(String var1);
}
